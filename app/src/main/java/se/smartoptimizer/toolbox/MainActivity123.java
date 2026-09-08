package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;

public class MainActivity123 extends MainActivity122 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}

  @Override void show(){
    super.show();
    try{((android.widget.TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.23");}catch(Throwable ignored){}
  }

  @Override void batteryHub122(){
    base("🔋 BATTERI & OPTIMERING","Kryptonian-idéer • analysera först • ändra bara reversibelt",true);
    note(shStatus());
    note("Endast mätbara och återställningsbara tester. Ingen force-idle, ingen aggressiv Doze, ingen GOS-avstängning, ingen permanent avinstallation och inga modem/IMS-ändringar.");
    btn("🧠 Kryptonian säkerhetsanalys 1.2",()->kryptonAnalyze123());
    btn("🔋 Smart batterianalys 1.1",()->smartBatteryPage());
    btn("🛡️ Säkra batteritweaks 1.0",()->batteryOptimizerPage());
    btn("📶 Test: Wi-Fi scan throttling = PÅ",()->confirmWifiThrottle123());
    btn("↩️ Återställ Wi-Fi scan throttling",()->restoreWifiThrottle123());
    btn("📶 Test: mobil data alltid aktiv = AV",()->confirmMobileDataAlwaysOn122());
    btn("↩️ Återställ mobil data alltid aktiv",()->restoreMobileDataAlwaysOn122());
  }

  String cleanFlag123(String v){
    if(v==null||v.trim().isEmpty()||"null".equalsIgnoreCase(v.trim()))return "❔ Ej exponerad av Samsung/Android";
    if("1".equals(v.trim())||"true".equalsIgnoreCase(v.trim()))return "✅ På";
    if("0".equals(v.trim())||"false".equalsIgnoreCase(v.trim()))return "⚪ Av";
    return "❔ "+v.trim();
  }

  void kryptonAnalyze123(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String wifi=g122("wifi_scan_always_enabled");
        String ble=g122("ble_scan_always_enabled");
        String loc=g122("location_background_throttle_interval_ms");
        String mdao=g122("mobile_data_always_on");
        String saver=g122("low_power");
        String adaptive=g122("adaptive_battery_management_enabled");
        String throttle=g122("wifi_scan_throttle_enabled");
        String standby=g122("app_standby_enabled");
        boolean core="0".equals(wifi)&&"0".equals(ble)&&"600000".equals(loc)&&!"1".equals(mdao);
        String advice=core?"✅ Grundtweaks redan optimala":"🟡 Någon grundtweak avviker";
        String throttleAdvice="1".equals(throttle)?"✅ Wi-Fi scan throttling är redan på.":("0".equals(throttle)?"🧪 Valfritt test: aktivera Wi-Fi scan throttling för att begränsa hur ofta appar får initiera Wi-Fi-skanning. Återställ om någon nätverksapp beter sig sämre.":"ℹ️ Wi-Fi scan throttling exponeras inte tydligt på denna firmware.");
        out="🔋 Kryptonian – säker analys 1.2\n\n"+
          "Wi-Fi bakgrundsskanning: "+wifi+"\n"+
          "Bluetooth bakgrundsskanning: "+ble+"\n"+
          "Bakgrundsplats: "+loc+" ms\n"+
          "Mobil data alltid aktiv: "+onoff122(mdao)+"\n"+
          "Energisparläge: "+onoff122(saver)+"\n"+
          "Adaptive Battery: "+cleanFlag123(adaptive)+"\n"+
          "Wi-Fi scan throttling: "+cleanFlag123(throttle)+"\n"+
          "App standby: "+cleanFlag123(standby)+"\n\n"+
          advice+"\n\n"+throttleAdvice+"\n\n"+
          ("1".equals(mdao)?"🧪 Valfritt test: stäng av 'mobil data alltid aktiv'.":"✅ 'Mobil data alltid aktiv' är redan av eller inte aktiverad.")+"\n\n"+
          "⛔ Aggressiv Doze, force-idle, GOS-avstängning och permanent debloat rekommenderas inte.\n\n✅ Endast analys.";
      }catch(Exception e){out="Analysen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Kryptonian – säker analys",x));
    }).start();
  }

  void confirmWifiThrottle123(){
    if(!shOk()){requestSh();return;}
    new AlertDialog.Builder(this)
      .setTitle("Testa Wi-Fi scan throttling = PÅ?")
      .setMessage("Detta är ett frivilligt och återställningsbart test. Android begränsar hur ofta bakgrundsappar får starta Wi-Fi-skanning. Vanlig Wi-Fi fortsätter fungera, men appar som skannar nätverk mycket ofta kan uppdatera långsammare. Nuvarande värde sparas först.")
      .setNegativeButton("Avbryt",null)
      .setPositiveButton("Testa",(d,w)->applyWifiThrottle123()).show();
  }

  void applyWifiThrottle123(){
    new Thread(()->{
      String out;
      try{
        snapshotGlobal("wifi_scan_throttle_enabled","bat_before_wifi_scan_throttle");
        int c=runCode("settings put global wifi_scan_throttle_enabled 1");
        if(c==0){p.edit().putBoolean("bat_wifi_throttle_test",true).apply();log("Batteri: wifi_scan_throttle_enabled satt till 1 för test");out="✅ Wi-Fi scan throttling är aktiverad för test.\n\nDet tidigare värdet är sparat. Vanlig Wi-Fi påverkas inte, men appar som gör täta bakgrundsskanningar kan uppdatera långsammare.";}else out="Android nekade ändringen (kod "+c+").";
      }catch(Exception e){out="Kunde inte ändra värdet: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Wi-Fi scan throttling",x));
    }).start();
  }

  void restoreWifiThrottle123(){
    if(!shOk()){requestSh();return;}
    if(!p.contains("bat_before_wifi_scan_throttle")){toast("Ingen sparad snapshot finns");return;}
    new Thread(()->{
      String out;
      try{restoreGlobal("wifi_scan_throttle_enabled","bat_before_wifi_scan_throttle");p.edit().putBoolean("bat_wifi_throttle_test",false).apply();log("Batteri: wifi_scan_throttle_enabled återställd");out="✅ Wi-Fi scan throttling återställd till telefonens tidigare värde.";}catch(Exception e){out="Återställningen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Återställning",x));
    }).start();
  }
}
