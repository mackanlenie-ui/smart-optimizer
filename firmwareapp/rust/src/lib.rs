use jni::objects::{GlobalRef, JClass, JObject, JString, JValue};
use jni::sys::{jboolean, jstring};
use jni::{JNIEnv, JavaVM};
use samloader_fus::{download_firmware, fetch_version_xml, DownloadProgress, FusClient};
use std::sync::atomic::{AtomicU64, Ordering};

fn jstr(env: &mut JNIEnv, s: String) -> jstring {
    env.new_string(s).map(|v| v.into_raw()).unwrap_or(std::ptr::null_mut())
}

fn latest(client: &FusClient, model: &str, region: &str) -> Result<String, String> {
    client
        .fetch_history(model, region)
        .or_else(|_| fetch_version_xml(model, region))
        .map(|v| v.latest)
        .map_err(|e| format!("Kunde inte hämta firmware: {e}"))
}

struct JProgress {
    vm: JavaVM,
    activity: GlobalRef,
    total: AtomicU64,
    pos: AtomicU64,
    last_report: AtomicU64,
}

impl JProgress {
    fn new(vm: JavaVM, activity: GlobalRef) -> Self {
        Self {
            vm,
            activity,
            total: AtomicU64::new(0),
            pos: AtomicU64::new(0),
            last_report: AtomicU64::new(0),
        }
    }

    fn report_length(&self, total: u64) {
        if let Ok(mut env) = self.vm.attach_current_thread() {
            let _ = env.call_method(
                self.activity.as_obj(),
                "onNativeLength",
                "(J)V",
                &[JValue::Long(total as i64)],
            );
        }
    }

    fn report_progress(&self, pos: u64, total: u64) {
        if let Ok(mut env) = self.vm.attach_current_thread() {
            let _ = env.call_method(
                self.activity.as_obj(),
                "onNativeProgress",
                "(JJ)V",
                &[JValue::Long(pos as i64), JValue::Long(total as i64)],
            );
        }
    }
}

impl DownloadProgress for JProgress {
    fn set_length(&self, len: u64) {
        self.total.store(len, Ordering::Relaxed);
        self.report_length(len);
    }

    fn inc(&self, bytes: u64) {
        let pos = self.pos.fetch_add(bytes, Ordering::Relaxed) + bytes;
        let last = self.last_report.load(Ordering::Relaxed);
        if pos.saturating_sub(last) >= 8 * 1024 * 1024 || pos >= self.total.load(Ordering::Relaxed) {
            if self
                .last_report
                .compare_exchange(last, pos, Ordering::Relaxed, Ordering::Relaxed)
                .is_ok()
            {
                self.report_progress(pos, self.total.load(Ordering::Relaxed));
            }
        }
    }

    fn position(&self) -> u64 {
        self.pos.load(Ordering::Relaxed)
    }

    fn println(&self, _msg: &str) {}
    fn println_verbose(&self, _msg: &str) {}
}

#[no_mangle]
pub extern "system" fn Java_se_samsungfirmware_downloader_MainActivity_nativeInit(
    mut env: JNIEnv,
    _class: JObject,
    context: JObject,
) -> jboolean {
    match rustls_platform_verifier::android::init_with_env(&mut env, context) {
        Ok(_) => 1,
        Err(_) => 0,
    }
}

#[no_mangle]
pub extern "system" fn Java_se_samsungfirmware_downloader_MainActivity_nativeCheck(
    mut env: JNIEnv,
    _class: JObject,
    model: JString,
    region: JString,
) -> jstring {
    let model: String = match env.get_string(&model) {
        Ok(v) => v.into(),
        Err(e) => return jstr(&mut env, format!("Fel modell: {e}")),
    };
    let region: String = match env.get_string(&region) {
        Ok(v) => v.into(),
        Err(e) => return jstr(&mut env, format!("Fel region: {e}")),
    };

    let result = (|| -> Result<String, String> {
        let mut client = FusClient::new().map_err(|e| format!("FUS-anslutning misslyckades: {e}"))?;
        let version = latest(&client, &model, &region)?;
        std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| {
            client.fetch_binary_info(&model, &region, &version)
        }))
        .map_err(|_| "Samsung returnerade ogiltig filinformation".to_string())?;
        Ok(format!("OK|{}|{}|{}", version, client.info.filename, client.info.size))
    })();

    jstr(&mut env, result.unwrap_or_else(|e| e))
}

#[no_mangle]
pub extern "system" fn Java_se_samsungfirmware_downloader_MainActivity_nativeDownload(
    mut env: JNIEnv,
    activity: JObject,
    model: JString,
    region: JString,
    out_dir: JString,
) -> jstring {
    let model: String = match env.get_string(&model) {
        Ok(v) => v.into(),
        Err(e) => return jstr(&mut env, format!("Fel modell: {e}")),
    };
    let region: String = match env.get_string(&region) {
        Ok(v) => v.into(),
        Err(e) => return jstr(&mut env, format!("Fel region: {e}")),
    };
    let out_dir: String = match env.get_string(&out_dir) {
        Ok(v) => v.into(),
        Err(e) => return jstr(&mut env, format!("Fel mapp: {e}")),
    };

    let vm = match env.get_java_vm() {
        Ok(v) => v,
        Err(e) => return jstr(&mut env, format!("JNI-fel: {e}")),
    };
    let global = match env.new_global_ref(activity) {
        Ok(v) => v,
        Err(e) => return jstr(&mut env, format!("JNI-fel: {e}")),
    };

    let result = (|| -> Result<String, String> {
        let mut client = FusClient::new().map_err(|e| format!("FUS-anslutning misslyckades: {e}"))?;
        let version = latest(&client, &model, &region)?;
        std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| {
            client.fetch_binary_info(&model, &region, &version)
        }))
        .map_err(|_| "Samsung returnerade ogiltig filinformation".to_string())?;

        let filename = client
            .info
            .filename
            .strip_suffix(".enc4")
            .unwrap_or(client.info.filename.as_str())
            .to_string();
        let out = format!("{}/{}", out_dir.trim_end_matches('/'), filename);
        let progress = JProgress::new(vm, global);
        download_firmware(&client, &out, 8, &progress)
            .map_err(|e| format!("Nedladdningen misslyckades: {e}"))?;
        Ok(format!("OK|{}", out))
    })();

    jstr(&mut env, result.unwrap_or_else(|e| e))
}
