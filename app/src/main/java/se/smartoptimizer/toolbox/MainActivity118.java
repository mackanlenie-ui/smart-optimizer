package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity118 extends MainActivity117 {
  @Override public void onCreate(Bundle b){ super.onCreate(b); }

  @Override void show(){
    super.show();
    try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.18");}catch(Throwable ignored){}
    card("📡 Radio & IMS Analyzer 1.1","Filtrerad SIM-status • CA • LTE/NR • IMS • separat rådata.",()->radioImsPage118());
  }

  void radioImsPage118(){
    base("📡 RADIO & IMS ANALYZER 1.1","Filtrerad • dual-SIM • read-only",true);
    note(shStatus());
    note("Visar en kort sammanfattning först. Vimla/Fello och aktiva abonnemang försöker hållas isär. Inga modem-, IMS-, band- eller operatörsinställningar ändras.");
    btn("📶 Smart radioanalys",()->smartRadio118());
    btn("☎️ Smart IMS-analys",()->smartIms118());
    sec("Avancerat");
    btn("🧾 Visa rådata – radio",()->raw118(false));
    btn("🧾 Visa rådata – IMS",()->raw118(true));
  }

  void smartRadio118(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String props=runShellText("echo 'Operatör:'; getprop gsm.operator.alpha; echo 'Nät:'; getprop gsm.network.type; echo 'SIM MCC/MNC:'; getprop gsm.sim.operator.numeric").trim();
        String sub=runShellText("dumpsys telephony.registry 2>/dev/null | grep -E -m40 'mSubId=|mPhoneId=|mDataConnectionState=|mDataNetworkType=|mVoiceNetworkType=|mSignalStrength=|isUsingCarrierAggregation=|mOperatorAlphaLong=' ").trim();
        String ca=runShellText("dumpsys telephony.registry 2>/dev/null | grep -o -m1 'isUsingCarrierAggregation=[^, ]*'").trim();
        String nr=runShellText("dumpsys telephony.registry 2>/dev/null | grep -iE -m8 'nrState|CellSignalStrengthNr|NR_SS|nrarfcn' ").trim();
        String verdict="🟢 Radioanslutningen kunde läsas";
        if(sub.isEmpty())verdict="🟡 Begränsad radiodata från firmware";
        out=verdict+"\n\n📱 SIM / operatör\n"+props+"\n\n📡 Aktiv telephony-status\n"+(sub.isEmpty()?"Ingen filtrerad status tillgänglig.":sub)+"\n\n🔗 Carrier Aggregation\n"+(ca.isEmpty()?"Ej rapporterad":ca)+"\n\n5G/NR\n"+(nr.isEmpty()?"Ingen tydlig NR-rad exponerad just nu.":nr)+"\n\n✅ Endast läsning.";
      }catch(Exception e){out="Radioanalysen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Smart radioanalys",x));
    }).start();
  }

  void smartIms118(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String ims=runShellText("dumpsys ims 2>/dev/null | grep -iE -m45 'subId|phoneId|registered|registration|mmtel|volte|vowifi|wifi.call|sms|capab' ").trim();
        String reg=runShellText("dumpsys ims 2>/dev/null | grep -iE -m12 'registered|registration' ").trim();
        String volte=runShellText("dumpsys ims 2>/dev/null | grep -iE -m12 'volte|voice.*lte|mmtel' ").trim();
        String vowifi=runShellText("dumpsys ims 2>/dev/null | grep -iE -m12 'vowifi|wifi.call|iwlan' ").trim();
        String verdict=ims.isEmpty()?"🟡 Samsung exponerar begränsad IMS-status":"🟢 IMS-data hittad";
        out=verdict+"\n\n📞 Registrering\n"+(reg.isEmpty()?"Ingen entydig registreringsrad hittades.":reg)+"\n\n☎️ VoLTE/MMTEL\n"+(volte.isEmpty()?"Ingen entydig VoLTE-rad hittades.":volte)+"\n\n📶 Wi-Fi-samtal\n"+(vowifi.isEmpty()?"Ingen entydig VoWiFi/IWLAN-rad hittades.":vowifi)+"\n\n🔎 Filtrerad IMS-data\n"+(ims.isEmpty()?"Använd rådata-knappen om vi behöver felsöka vidare.":ims)+"\n\n✅ Inget har ändrats.";
      }catch(Exception e){out="IMS-analysen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Smart IMS-analys",x));
    }).start();
  }

  void raw118(boolean ims){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String x;
      try{x=runShellText(ims?"dumpsys ims 2>/dev/null":"dumpsys telephony.registry 2>/dev/null");if(x.trim().isEmpty())x="Ingen rådata exponerades av firmware.";}catch(Exception e){x="Kunde inte läsa rådata: "+e.getClass().getSimpleName();}
      final String y=x;runOnUiThread(()->dialog118(ims?"IMS-rådata":"Radio-rådata",y));
    }).start();
  }

  void dialog118(String title,String msg){new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setPositiveButton("OK",null).show();}
}
