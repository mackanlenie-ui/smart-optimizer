package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;

public class MainActivity124 extends MainActivity123 {
  static final String SPASS="com.samsung.android.samsungpass";
  static final String SPASS_AF="com.samsung.android.samsungpassautofill";
  @Override public void onCreate(Bundle b){super.onCreate(b);}
  @Override void show(){super.show();try{((android.widget.TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.24");}catch(Throwable ignored){}}
  @Override void batteryHub122(){
    base("🔋 BATTERI & OPTIMERING","Kryptonian 1.3 • säkra tester • valfri Samsung Pass-debloat",true);note(shStatus());
    note("Analyserar först. Reversibla ändringar använder snapshot/återställning. Ingen force-idle, aggressiv Doze, GOS-avstängning eller permanent avinstallation.");
    btn("🧠 Kryptonian säkerhetsanalys 1.3",()->kryptonAnalyze124());btn("🛡️ Samsung Pass – status / valfri debloat",()->samsungPass124());
    btn("🔋 Smart batterianalys 1.1",()->smartBatteryPage());btn("🛡️ Säkra batteritweaks 1.0",()->batteryOptimizerPage());
    btn("📶 Test: Wi-Fi scan throttling = PÅ",()->confirmWifiThrottle123());btn("↩️ Återställ Wi-Fi scan throttling",()->restoreWifiThrottle123());
    btn("📶 Test: mobil data alltid aktiv = AV",()->confirmMobileDataAlwaysOn122());btn("↩️ Återställ mobil data alltid aktiv",()->restoreMobileDataAlwaysOn122());
  }
  String gv124(String key){try{return runShellText("settings get global "+key).trim();}catch(Exception e){return "?";}}
  boolean pkgExists124(String pkg){try{return runShellText("pm list packages "+pkg).contains(pkg);}catch(Exception e){return false;}}
  boolean pkgEnabled124(String pkg){try{return runShellText("pm list packages -e "+pkg).contains(pkg);}catch(Exception e){return false;}}
  void kryptonAnalyze124(){if(!shOk()){requestSh();return;}new Thread(()->{String out;try{
    String wifi=g122("wifi_scan_always_enabled"),ble=g122("ble_scan_always_enabled"),loc=g122("location_background_throttle_interval_ms"),mdao=g122("mobile_data_always_on"),saver=g122("low_power"),adaptive=g122("adaptive_battery_management_enabled"),throttle=g122("wifi_scan_throttle_enabled"),standby=g122("app_standby_enabled");
    String netNotify=gv124("wifi_networks_available_notification_on"),usage=gv124("usage_reporting_opt_in"),tcp=gv124("tcp_default_init_rwnd"),backup=g122("backup_manager_constants"),idle=gv124("device_idle_constants");
    boolean core="0".equals(wifi)&&"0".equals(ble)&&"600000".equals(loc)&&!"1".equals(mdao);
    String pass=pkgExists124(SPASS)?(pkgEnabled124(SPASS)?"🟡 Installerad och aktiv":"✅ Installerad men avaktiverad"):"⚪ Inte installerad för användaren";
    String passAf=pkgExists124(SPASS_AF)?(pkgEnabled124(SPASS_AF)?"🟡 Installerad och aktiv":"✅ Installerad men avaktiverad"):"⚪ Inte installerad för användaren";
    out="🔋 Kryptonian – säker analys 1.3\n\nWi-Fi bakgrundsskanning: "+wifi+"\nBluetooth bakgrundsskanning: "+ble+"\nBakgrundsplats: "+loc+" ms\nMobil data alltid aktiv: "+onoff122(mdao)+"\nEnergisparläge: "+onoff122(saver)+"\nAdaptive Battery: "+cleanFlag123(adaptive)+"\nWi-Fi scan throttling: "+cleanFlag123(throttle)+"\nApp standby: "+cleanFlag123(standby)+"\n\n"+(core?"✅ Grundtweaks redan optimala":"🟡 Någon grundtweak avviker")+"\n\n🔎 Fler Kryptonian-värden\nWi-Fi nätverksnotiser: "+cleanFlag123(netNotify)+"\nUsage reporting: "+cleanFlag123(usage)+"\nTCP init rwnd: "+cleanFlag123(tcp)+"\nBackup manager constants: "+cleanFlag123(backup)+"\nDevice idle constants: "+cleanFlag123(idle)+"\n\n🛡️ Samsung Pass\nSamsung Pass: "+pass+"\nAutofill: "+passAf+"\n\nℹ️ Wi-Fi-notiser och usage reporting visas för kontroll. TCP/backup/device-idle ändras inte automatiskt eftersom nyttan inte är tillräckligt säker.\n\n⛔ Force-idle, aggressiv Doze och GOS-avstängning används inte.\n\n✅ Endast analys.";
  }catch(Exception e){out="Analysen misslyckades: "+e.getClass().getSimpleName();}final String x=out;runOnUiThread(()->dialog118("Kryptonian – säker analys 1.3",x));}).start();}
  void samsungPass124(){if(!shOk()){requestSh();return;}new Thread(()->{boolean p1=pkgExists124(SPASS),e1=pkgEnabled124(SPASS),p2=pkgExists124(SPASS_AF),e2=pkgEnabled124(SPASS_AF);String status="Samsung Pass: "+(p1?(e1?"🟡 Aktiv":"✅ Avaktiverad"):"⚪ Ej installerad")+"\nSamsung Pass Autofill: "+(p2?(e2?"🟡 Aktiv":"✅ Avaktiverad"):"⚪ Ej installerad")+"\n\nAvaktivering använder pm disable-user --user 0, inte permanent uninstall. Kan återställas här.";runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Samsung Pass – valfri debloat").setMessage(status).setNegativeButton("Stäng",null).setNeutralButton("Återställ",(d,w)->restoreSamsungPass124()).setPositiveButton("Avaktivera",(d,w)->confirmDisableSamsungPass124()).show());}).start();}
  void confirmDisableSamsungPass124(){new AlertDialog.Builder(this).setTitle("Avaktivera Samsung Pass?").setMessage("Detta avaktiverar Samsung Pass och dess Autofill för användare 0. Inget paket avinstalleras permanent. Du kan återställa här senare.").setNegativeButton("Avbryt",null).setPositiveButton("Avaktivera",(d,w)->disableSamsungPass124()).show();}
  void disableSamsungPass124(){new Thread(()->{String out;try{p.edit().putBoolean("krypt_spass_was_enabled",pkgEnabled124(SPASS)).putBoolean("krypt_spassaf_was_enabled",pkgEnabled124(SPASS_AF)).putBoolean("krypt_spass_snapshot",true).apply();int a=runCode("pm disable-user --user 0 "+SPASS),b=runCode("pm disable-user --user 0 "+SPASS_AF);log("Kryptonian: Samsung Pass/Autofill avaktiverades reversibelt");out=(a==0||!pkgExists124(SPASS))&&(b==0||!pkgExists124(SPASS_AF))?"✅ Samsung Pass och Autofill är avaktiverade för användare 0.\n\nInget har avinstallerats permanent. Återställningsstatus är sparad.":"🟡 Något paket kunde inte avaktiveras. Kontrollera status igen.";}catch(Exception e){out="Kunde inte avaktivera: "+e.getClass().getSimpleName();}final String x=out;runOnUiThread(()->dialog118("Samsung Pass",x));}).start();}
  void restoreSamsungPass124(){if(!p.getBoolean("krypt_spass_snapshot",false)){toast("Ingen Samsung Pass-snapshot finns");return;}new Thread(()->{String out;try{if(p.getBoolean("krypt_spass_was_enabled",false))runCode("pm enable "+SPASS);if(p.getBoolean("krypt_spassaf_was_enabled",false))runCode("pm enable "+SPASS_AF);p.edit().remove("krypt_spass_snapshot").remove("krypt_spass_was_enabled").remove("krypt_spassaf_was_enabled").apply();log("Kryptonian: Samsung Pass/Autofill återställdes enligt snapshot");out="✅ Samsung Pass-status återställd enligt snapshoten från före debloat-testet.";}catch(Exception e){out="Återställningen misslyckades: "+e.getClass().getSimpleName();}final String x=out;runOnUiThread(()->dialog118("Samsung Pass – återställning",x));}).start();}
}
