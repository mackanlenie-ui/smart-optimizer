package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity117 extends MainActivity116 {
  @Override public void onCreate(Bundle b){ super.onCreate(b); }

  @Override void show(){
    super.show();
    try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.17");}catch(Throwable ignored){}
    card("🔋 Smart batterianalys 1.1","Doze • whitelist • optimeringsstatus • endast analys.",()->smartBatteryPage());
    card("📡 Radio & IMS Analyzer","LTE/NR • IMS/VoLTE/VoWiFi • read-only diagnostik.",()->radioImsPage());
  }

  void smartBatteryPage(){
    base("🔋 SMART BATTERIANALYS 1.1","Read-only • inga aggressiva Doze-ändringar",true);
    note(shStatus());
    note("Analyserar telefonens aktuella energiläge utan att ändra Device Idle, force-idle eller app-whitelist.");
    btn("📊 Kör full batterianalys",()->runSmartBatteryAnalysis());
    btn("🔋 Öppna Batterioptimering 1.0",()->batteryOptimizerPage());
  }

  void runSmartBatteryAnalysis(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String wifi=runShellText("settings get global wifi_scan_always_enabled").trim();
        String ble=runShellText("settings get global ble_scan_always_enabled").trim();
        String loc=runShellText("settings get global location_background_throttle_interval_ms").trim();
        String idle=runShellText("dumpsys deviceidle | grep -m1 'mState=' 2>/dev/null").trim();
        String white=runShellText("dumpsys deviceidle whitelist 2>/dev/null | head -40").trim();
        String saver=runShellText("settings get global low_power").trim();
        boolean optimal="0".equals(wifi)&&"0".equals(ble)&&"600000".equals(loc);
        String verdict=optimal?"✅ Redan optimalt":"🟡 Kontrollera rekommendationerna";
        out=verdict+"\n\nWi-Fi bakgrundsskanning: "+wifi+"\nBluetooth bakgrundsskanning: "+ble+"\nBakgrundsplats: "+loc+" ms\nEnergisparläge: "+("1".equals(saver)?"aktivt":"inte aktivt")+"\n"+(idle.isEmpty()?"":"\nDoze: "+idle)+"\n\nDoze/batteriundantag (första poster):\n"+(white.isEmpty()?"Kunde inte läsas på denna firmware.":white)+"\n\nToolbox har inte ändrat något.";
      }catch(Exception e){out="Analysen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Smart batterianalys").setMessage(x).setPositiveButton("OK",null).show());
    }).start();
  }

  void radioImsPage(){
    base("📡 RADIO & IMS ANALYZER","Read-only • ändrar inte modem, IMS eller operatörsprofil",true);
    note(shStatus());
    note("Läser Android/Samsungs diagnostik för mobilnät och IMS. Inga band låses och inga VoLTE/VoWiFi/5G-inställningar ändras.");
    btn("📶 Kör radioanalys",()->runRadioAnalysis());
    btn("☎️ Kör IMS-analys",()->runImsAnalysis());
  }

  void runRadioAnalysis(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String tel=runShellText("dumpsys telephony.registry 2>/dev/null | grep -E -m20 'mServiceState|mSignalStrength|mDataConnectionState|mDataNetworkType|mVoiceNetworkType|mPhysicalChannelConfig' ");
        String phone=runShellText("getprop gsm.operator.alpha; getprop gsm.network.type; getprop gsm.sim.operator.numeric");
        out="SIM/operatör:\n"+phone.trim()+"\n\nRadio:\n"+(tel.trim().isEmpty()?"Begränsad av firmware/behörighet.":tel.trim())+"\n\nℹ️ Read-only: inga radioinställningar ändrades.";
      }catch(Exception e){out="Radioanalysen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Radioanalys").setMessage(x).setPositiveButton("OK",null).show());
    }).start();
  }

  void runImsAnalysis(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String svc=runShellText("dumpsys -l 2>/dev/null | grep -i ims | head -20");
        String ims=runShellText("dumpsys ims 2>/dev/null | grep -iE -m30 'register|registered|volte|vowifi|wifi calling|mmtel|feature|capab' ");
        String tel=runShellText("dumpsys telephony.registry 2>/dev/null | grep -iE -m20 'ims|voice|data.network.type' ");
        out="IMS-tjänster:\n"+(svc.trim().isEmpty()?"Ingen separat IMS-tjänst exponerad.":svc.trim())+"\n\nIMS-status:\n"+(ims.trim().isEmpty()?"Samsung exponerade inte detaljer via dumpsys ims.":ims.trim())+"\n\nTelephony:\n"+(tel.trim().isEmpty()?"Inga extra IMS-rader.":tel.trim())+"\n\n✅ Endast läsning. IMS/VoLTE/VoWiFi har inte ändrats.";
      }catch(Exception e){out="IMS-analysen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->new AlertDialog.Builder(this).setTitle("IMS-analys").setMessage(x).setPositiveButton("OK",null).show());
    }).start();
  }
}
