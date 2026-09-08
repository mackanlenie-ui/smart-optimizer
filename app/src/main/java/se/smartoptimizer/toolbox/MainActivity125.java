package se.smartoptimizer.toolbox;

import android.os.Bundle;

public class MainActivity125 extends MainActivity124 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}

  @Override void show(){
    super.show();
    try{((android.widget.TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.25");}catch(Throwable ignored){}
  }

  @Override void batteryHub122(){
    base("🔋 BATTERI & OPTIMERING","Kryptonian 1.4 • ZIP-granskning • appdiagnostik",true);
    note(shStatus());
    note("10.25 granskar hela Kryptonian V4-logiken och visar vad som är relevant, tveksamt eller olämpligt på din S23 Ultra. Inga aggressiva Doze/force-idle/GOS-ändringar görs automatiskt.");
    btn("🧠 Kryptonian säkerhetsanalys 1.3",()->kryptonAnalyze124());
    btn("📦 Kryptonian ZIP – full granskning 1.0",()->kryptonFullReview125());
    btn("📊 App standby & Doze-diagnos",()->standbyDoze125());
    btn("🛡️ Samsung Pass – status / valfri debloat",()->samsungPass124());
    btn("🔋 Smart batterianalys 1.1",()->smartBatteryPage());
    btn("🛡️ Säkra batteritweaks 1.0",()->batteryOptimizerPage());
    btn("📶 Test: Wi-Fi scan throttling = PÅ",()->confirmWifiThrottle123());
    btn("↩️ Återställ Wi-Fi scan throttling",()->restoreWifiThrottle123());
    btn("📶 Test: mobil data alltid aktiv = AV",()->confirmMobileDataAlwaysOn122());
    btn("↩️ Återställ mobil data alltid aktiv",()->restoreMobileDataAlwaysOn122());
  }

  void kryptonFullReview125(){
    String s="📦 Kryptonian V4 – full granskning\n\n"+
      "✅ BRA / RIMLIGT\n"+
      "• wifi_scan_always_enabled = 0 – redan optimalt hos dig.\n"+
      "• ble_scan_always_enabled = 0 – redan optimalt hos dig.\n"+
      "• wifi_networks_available_notification_on = 0 – redan av hos dig.\n"+
      "• location_background_throttle_interval_ms = 600000 – redan satt hos dig.\n"+
      "• Valfri debloat av appar du inte använder kan ge liten vinst. Toolbox använder disable-user i stället för permanent user-uninstall.\n\n"+
      "🧪 TVEKSAM / LITEN ELLER OKLAR NYTTA\n"+
      "• wifi_scan_throttle_enabled = 1 – rimligt test, men din firmware exponerar inte värdet tydligt.\n"+
      "• usage_reporting_opt_in = 0 – kan minska viss rapportering men tydlig batterivinst är osäker.\n"+
      "• tcp_default_init_rwnd = 60 – gammal nätverkstweak; ingen bra grund för batterivinst på modern One UI.\n"+
      "• backup_manager_constants data_upload_rate_limit_kb=0 – 0 kan betyda ingen begränsning och är därför inte en självklar batteritweak.\n\n"+
      "⛔ AVRÅDS\n"+
      "• dumpsys deviceidle force-idle – test/diagnostik, inte en permanent optimering. Kan försena synk, nätverk, jobb och notiser.\n"+
      "• Aggressiva device_idle_constants – risk för försenade notiser och bakgrundsjobb.\n"+
      "• GOS-avstängning – kan öka effektförbrukning och temperatur under spel/tung last.\n\n"+
      "⚠️ FEL/BRISTER I ORIGINALSCRIPTET\n"+
      "• Paketnamnet com.faceboo.system är felstavat; korrekt Meta-systempaket brukar vara com.facebook.system.\n"+
      "• device_idle_constants skrivs två gånger. Den andra settings put kan ersätta den första strängen i stället för att lägga till den.\n"+
      "• Revert-scriptet återställer Wi-Fi/Bluetooth-skanning till 1 i stället för att återställa telefonens verkliga tidigare värden. Det är inte en riktig snapshot-återställning.\n"+
      "• Revert-scriptet använder install-existing för många paket oavsett om de var installerade/aktiva före tweakningen.\n\n"+
      "✅ Slutsats: de säkra delarna som faktiskt är relevanta är redan till stor del aktiva hos dig. Nästa verkliga vinst finns främst i apparnas bakgrundsbeteende och selektiv debloat, inte fler globala aggressiva tweaks.";
    dialog118("Kryptonian ZIP – full granskning",s);
  }

  void standbyDoze125(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String buckets=runShellText("am get-standby-bucket");
        String whitelist=runShellText("dumpsys deviceidle whitelist");
        String idle=runShellText("dumpsys deviceidle | grep -E 'mState=|mLightState=|mForceIdle=|mScreenOn=|mCharging=' | head -n 12");
        if(buckets==null||buckets.trim().isEmpty())buckets="Ej exponerat av denna firmware.";
        if(whitelist==null||whitelist.trim().isEmpty())whitelist="Inga poster eller ej exponerat.";
        if(idle==null||idle.trim().isEmpty())idle="Ej exponerat.";
        if(buckets.length()>7000)buckets=buckets.substring(0,7000)+"\n…(förkortat)";
        if(whitelist.length()>5000)whitelist=whitelist.substring(0,5000)+"\n…(förkortat)";
        out="📊 APP STANDBY / DOZE\n\nStandby buckets:\n"+buckets+"\n\nDoze-undantag / whitelist:\n"+whitelist+"\n\nAktuell DeviceIdle-status:\n"+idle+"\n\nℹ️ Active/Working Set/Frequent/Rare/Restricted styr hur mycket bakgrundsarbete appar får göra. Appar i Doze-whitelist får större frihet i bakgrunden. Den här sidan ändrar ingenting.";
      }catch(Exception e){out="Diagnosen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("App standby & Doze-diagnos",x));
    }).start();
  }
}
