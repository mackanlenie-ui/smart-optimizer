package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;

public class MainActivity122 extends MainActivity121 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}

  @Override void show(){
    base("S23 ULTRA TOOLBOX 10.22","Ren startsida • kategorier • säkra och återställningsbara tester",false);
    dashboard();
    card("📡 Nätverk & signal","Vimla, LTE/5G, signal, CA och IMS.",()->networkHub122());
    card("🔋 Batteri & optimering","Kryptonian-analys, säkra tweaks och full återställning.",()->batteryHub122());
    card("🛡️ Appar & debloat","Smart Debloat, App Manager och rekommendationer.",()->appsHub122());
    card("🌡️ Prestanda & temperatur","Thermal Watch, laddtest och Smart Advisor.",()->performanceHub122());
    card("📱 Telefon & jobb","GPS/Jobbläge, DeX och kamera.",()->phoneHub122());
    card("🧯 Recovery & diagnostik","Återställning, backup, historik och Shizuku.",()->recoveryHub122());
    card("🧰 Alla verktyg – äldre vy","Öppnar den tidigare fulla verktygslistan.",()->legacyHome122());
  }

  void networkHub122(){
    base("📡 NÄTVERK & SIGNAL","Samlad nätverksanalys",true);
    note("De senaste analyserna ligger här. De äldre Radio/IMS-korten visas inte längre på startsidan.");
    btn("📶 Vimla – ren nätanalys",()->vimlaPage121());
    btn("📡 Samsung Radio & IMS",()->samsungImsPage());
    btn("☎️ Ren IMS-kontroll",()->ims121());
  }

  void batteryHub122(){
    base("🔋 BATTERI & OPTIMERING","Kryptonian-idéer • analysera först • ändra bara reversibelt",true);
    note(shStatus());
    note("Vi behåller endast tweaks som är mätbara och återställningsbara. Ingen force-idle, ingen aggressiv Doze, ingen GOS-avstängning, ingen pm uninstall och inga modem/IMS-ändringar.");
    btn("🧠 Kör Kryptonian säkerhetsanalys",()->kryptonAnalyze122());
    btn("🔋 Smart batterianalys 1.1",()->smartBatteryPage());
    btn("🛡️ Säkra batteritweaks 1.0",()->batteryOptimizerPage());
    btn("📶 Test: stäng av 'mobil data alltid aktiv'",()->confirmMobileDataAlwaysOn122());
    btn("↩️ Återställ 'mobil data alltid aktiv'",()->restoreMobileDataAlwaysOn122());
  }

  void appsHub122(){base("🛡️ APPAR & DEBLOAT","Säker avaktivering • full recovery",true);btn("🛡️ Smart Debloat 8.0",()->debloat());btn("📦 App Manager 2.0",()->apps());btn("🧠 Smart Advisor 4.0",()->advisor());}
  void performanceHub122(){base("🌡️ PRESTANDA & TEMPERATUR","Mät före du ändrar",true);btn("🌡️ Thermal Watch 4.0",()->thermal());btn("⚡ Charging Test Pro",()->battery());btn("🧠 Smart Advisor 4.0",()->advisor());}
  void phoneHub122(){base("📱 TELEFON & JOBB","Praktiska S23 Ultra-funktioner",true);btn("🚗 GPS/Jobbläge",()->profiles());btn("🖥️ DeX Center 2.0",()->dex());btn("📸 Camera Guide 5.1",()->camera());}
  void recoveryHub122(){base("🧯 RECOVERY & DIAGNOSTIK","Se och ångra Toolbox-ändringar",true);btn("🧯 Recovery Center 2.0",()->recovery());btn("💾 Backup / Diagnostik",()->diag());btn("🕘 Historik",()->history());btn("🔧 Shizuku Tools",()->shPage());}
  void legacyHome122(){super.show();}

  String g122(String key){try{return runShellText("settings get global "+key).trim();}catch(Exception e){return "?";}}
  String s122(String key){try{return runShellText("settings get secure "+key).trim();}catch(Exception e){return "?";}}
  String onoff122(String v){return "1".equals(v)?"✅ På":"0".equals(v)?"⚪ Av":"❔ "+v;}

  void kryptonAnalyze122(){
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
        boolean core="0".equals(wifi)&&"0".equals(ble)&&"600000".equals(loc);
        String advice=core?"✅ Grundtweaks redan optimala":"🟡 Någon grundtweak avviker";
        out="🔋 Kryptonian – säker analys\n\n"+
          "Wi-Fi bakgrundsskanning: "+wifi+"\n"+
          "Bluetooth bakgrundsskanning: "+ble+"\n"+
          "Bakgrundsplats: "+loc+" ms\n"+
          "Mobil data alltid aktiv: "+onoff122(mdao)+"\n"+
          "Energisparläge: "+onoff122(saver)+"\n"+
          "Adaptive Battery-flagga: "+onoff122(adaptive)+"\n\n"+
          advice+"\n\n"+
          ("1".equals(mdao)?"🧪 Valfritt test: stäng av 'mobil data alltid aktiv'. Det kan minska standby-förbrukning på Wi-Fi men kan göra växlingen Wi-Fi→mobilnät något långsammare.":"✅ 'Mobil data alltid aktiv' är redan av eller inte aktiverad.")+"\n\n"+
          "⛔ Aggressiv Doze, force-idle, GOS-avstängning och permanent debloat rekommenderas inte.\n\n✅ Endast analys.";
      }catch(Exception e){out="Analysen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Kryptonian – säker analys",x));
    }).start();
  }

  void confirmMobileDataAlwaysOn122(){
    if(!shOk()){requestSh();return;}
    new AlertDialog.Builder(this).setTitle("Testa mobil data alltid aktiv = AV?")
      .setMessage("Detta är ett frivilligt, reversibelt test. På Wi-Fi kan telefonen släppa den parallella mobildataanslutningen. Det kan spara lite standby-batteri, men växling från Wi-Fi till mobilnät kan bli något långsammare. Nuvarande värde sparas först.")
      .setNegativeButton("Avbryt",null).setPositiveButton("Testa",(d,w)->applyMobileDataAlwaysOn122()).show();
  }

  void applyMobileDataAlwaysOn122(){
    new Thread(()->{
      String out;
      try{
        snapshotGlobal("mobile_data_always_on","bat_before_mobile_data_always_on");
        int c=runCode("settings put global mobile_data_always_on 0");
        if(c==0){p.edit().putBoolean("bat_mobile_data_test",true).apply();log("Batteri: mobile_data_always_on satt till 0 för test");out="✅ Test aktiverat.\n\nmobile_data_always_on = 0\n\nDet tidigare värdet är sparat. Testa batteritid och Wi-Fi→mobilnät-växling. Återställ om du märker fördröjning.";}else out="Android nekade ändringen (kod "+c+").";
      }catch(Exception e){out="Kunde inte ändra värdet: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Batteritest",x));
    }).start();
  }

  void restoreMobileDataAlwaysOn122(){
    if(!shOk()){requestSh();return;}
    if(!p.contains("bat_before_mobile_data_always_on")){toast("Ingen sparad snapshot finns");return;}
    new Thread(()->{
      String out;
      try{restoreGlobal("mobile_data_always_on","bat_before_mobile_data_always_on");p.edit().putBoolean("bat_mobile_data_test",false).apply();log("Batteri: mobile_data_always_on återställd");out="✅ Återställt till värdet telefonen hade före testet.";}catch(Exception e){out="Återställningen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Återställning",x));
    }).start();
  }
}
