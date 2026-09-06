use jni::errors::ThrowRuntimeExAndDefault;
use jni::objects::{Global, JObject, JString, JValue};
use jni::sys::{jboolean, jstring};
use jni::{Env, EnvUnowned, JavaVM};
use samloader_fus::{download_firmware, fetch_version_xml, DownloadProgress, FusClient};
use std::sync::atomic::{AtomicU64, Ordering};

fn jstr(env: &mut Env<'_>, s: String) -> jni::errors::Result<jstring> {
    Ok(JString::from_str(env, s)?.into_raw())
}

fn latest(client: &FusClient, model: &str, region: &str) -> Result<String, String> {
    client
        .fetch_history(model, region)
        .or_else(|_| fetch_version_xml(model, region))
        .map(|v| v.latest)
        .map_err(|e| format!("Kunde inte hämta firmware: {e}"))
}

struct JProgress {
    activity: Global<JObject<'static>>,
    total: AtomicU64,
    pos: AtomicU64,
    last_report: AtomicU64,
}

impl JProgress {
    fn new(activity: Global<JObject<'static>>) -> Self {
        Self {
            activity,
            total: AtomicU64::new(0),
            pos: AtomicU64::new(0),
            last_report: AtomicU64::new(0),
        }
    }

    fn report_length(&self, total: u64) {
        if let Ok(vm) = JavaVM::singleton() {
            let _ = vm.attach_current_thread(|env| -> jni::errors::Result<()> {
                env.call_method(
                    self.activity.as_obj(),
                    "onNativeLength",
                    "(J)V",
                    &[JValue::Long(total as i64)],
                )?;
                Ok(())
            });
        }
    }

    fn report_progress(&self, pos: u64, total: u64) {
        if let Ok(vm) = JavaVM::singleton() {
            let _ = vm.attach_current_thread(|env| -> jni::errors::Result<()> {
                env.call_method(
                    self.activity.as_obj(),
                    "onNativeProgress",
                    "(JJ)V",
                    &[JValue::Long(pos as i64), JValue::Long(total as i64)],
                )?;
                Ok(())
            });
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
        let total = self.total.load(Ordering::Relaxed);
        let last = self.last_report.load(Ordering::Relaxed);
        if pos.saturating_sub(last) >= 8 * 1024 * 1024 || pos >= total {
            if self
                .last_report
                .compare_exchange(last, pos, Ordering::Relaxed, Ordering::Relaxed)
                .is_ok()
            {
                self.report_progress(pos, total);
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
pub extern "system" fn Java_se_samsungfirmware_downloader_MainActivity_nativeInit<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JObject<'caller>,
    context: JObject<'caller>,
) -> jboolean {
    unowned_env
        .with_env(|env| -> jni::errors::Result<jboolean> {
            Ok(if rustls_platform_verifier::android::init_with_env(env, context).is_ok() {
                1
            } else {
                0
            })
        })
        .resolve::<ThrowRuntimeExAndDefault>()
}

#[no_mangle]
pub extern "system" fn Java_se_samsungfirmware_downloader_MainActivity_nativeCheck<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    _class: JObject<'caller>,
    model: JString<'caller>,
    region: JString<'caller>,
) -> jstring {
    unowned_env
        .with_env(|env| -> jni::errors::Result<jstring> {
            let model = model.try_to_string(env)?;
            let region = region.try_to_string(env)?;

            let result = (|| -> Result<String, String> {
                let mut client = FusClient::new()
                    .map_err(|e| format!("FUS-anslutning misslyckades: {e}"))?;
                let version = latest(&client, &model, &region)?;
                std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| {
                    client.fetch_binary_info(&model, &region, &version)
                }))
                .map_err(|_| "Samsung returnerade ogiltig filinformation".to_string())?;
                Ok(format!(
                    "OK|{}|{}|{}",
                    version, client.info.filename, client.info.size
                ))
            })();

            jstr(env, result.unwrap_or_else(|e| e))
        })
        .resolve::<ThrowRuntimeExAndDefault>()
}

#[no_mangle]
pub extern "system" fn Java_se_samsungfirmware_downloader_MainActivity_nativeDownload<'caller>(
    mut unowned_env: EnvUnowned<'caller>,
    activity: JObject<'caller>,
    model: JString<'caller>,
    region: JString<'caller>,
    out_dir: JString<'caller>,
) -> jstring {
    unowned_env
        .with_env(|env| -> jni::errors::Result<jstring> {
            let model = model.try_to_string(env)?;
            let region = region.try_to_string(env)?;
            let out_dir = out_dir.try_to_string(env)?;
            let global = env.new_global_ref(activity)?;

            let result = (|| -> Result<String, String> {
                let mut client = FusClient::new()
                    .map_err(|e| format!("FUS-anslutning misslyckades: {e}"))?;
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
                let progress = JProgress::new(global);
                download_firmware(&client, &out, 8, &progress)
                    .map_err(|e| format!("Nedladdningen misslyckades: {e}"))?;
                Ok(format!("OK|{}", out))
            })();

            jstr(env, result.unwrap_or_else(|e| e))
        })
        .resolve::<ThrowRuntimeExAndDefault>()
}
