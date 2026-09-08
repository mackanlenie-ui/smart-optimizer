package se.smartoptimizer.toolbox;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity120 extends MainActivity119 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}
  @Override void show(){
    super.show();
    try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.20");}catch(Throwable ignored){}
    card("📶 Vimla nätanalys 1.0","Aktivt SIM • kort sammanfattning • signal • CA • IMS • read-only.",()->vimlaPage120());
  }

  void vimlaPage120(){
    base("📶 VIMLA NÄTANALYS 1.0","Aktivt SIM • inga kilometerlånga dumpar",true);
    note(shStatus());
    note("Analyserar det aktiva mobilabonnemanget och visar bara relevanta värden. Rå telephony-data öppnas inte automatiskt. Inga radio-, band- eller IMS-inställningar ändras.");
    btn("🔎 Analysera Vimla / aktivt SIM",()->active120());
    btn("☎️ Kort IMS-kontroll",()->ims120());
  }

  String firstMatch120(String src,String... keys){
    if(src==null)return "";
    for(String line:src.split("\\n")){
      String l=line.trim();
      for(String k:keys)if(l.toLowerCase().contains(k.toLowerCase()))return l;
    }
    return "";
  }
  String val120(String s,String fallback){return s==null||s.trim().isEmpty()?fallback:s.trim();}
  String ca120(String s){String x=firstMatch120(s,"isUsingCarrierAggregation=true","isUsingCarrierAggregation=false");if(x.contains("=true"))return "✅ Ja";if(x.contains("=false"))return "❌ Nej";return "❔ Ej rapporterat";}
  String bool120(String src,String key){String x=firstMatch120(src,key);if(x.toLowerCase().contains("true"))return "✅ Ja";if(x.toLowerCase().contains("false"))return "❌ Nej";return "❔ Okänt";}

  void active120(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String op=runShellText("getprop gsm.operator.alpha").trim();
        String net=runShellText("getprop gsm.network.type").trim();
        String mcc=runShellText("getprop gsm.sim.operator.numeric").trim();
        String r=runShellText("dumpsys telephony.registry 2>/dev/null");
        String cc=runShellText("dumpsys carrier_config 2>/dev/null");
        String lte=firstMatch120(r,"CellSignalStrengthLte");
        String nr=firstMatch120(r,"CellSignalStrengthNr");
        String oper=firstMatch120(r,"mOperatorAlphaLong=","operatorAlphaLong=");
        String data=firstMatch120(r,"mDataNetworkType=","dataNetworkType=");
        String voice=firstMatch120(r,"mVoiceNetworkType=","voiceNetworkType=");
        String sub=firstMatch120(r,"mSubId=","subId=");
        int score=0;
        if(!lte.isEmpty())score++;
        if(!nr.isEmpty())score++;
        if(ca120(r).contains("Ja"))score++;
        String verdict=score>=3?"🟢 Mycket bra radiostatus":score>=1?"🟢 Radioanslutning aktiv":"🟡 Begränsad status från Samsung";
        out="📱 Aktivt SIM / Vimla\n\n"+
          "Operatör: "+val120(oper,val120(op,"Ej rapporterat"))+"\n"+
          "Nät: "+val120(data,val120(net,"Ej rapporterat"))+"\n"+
          "Röstnät: "+val120(voice,"Ej rapporterat")+"\n"+
          "MCC/MNC: "+val120(mcc,"Ej rapporterat")+"\n"+
          "Subscription: "+val120(sub,"Ej rapporterat")+"\n\n"+
          "📡 LTE-signal\n"+val120(lte,"Ingen LTE-signalrad exponerad")+"\n\n"+
          "📶 5G/NR\n"+val120(nr,"Ingen aktiv NR-signalrad exponerad")+"\n\n"+
          "🔗 Carrier Aggregation: "+ca120(r)+"\n\n"+
          "☎️ Operatörsstöd\nVoLTE: "+bool120(cc,"carrier_volte_available_bool")+"\nWi-Fi-samtal: "+bool120(cc,"carrier_wfc_ims_available_bool")+"\n\n"+
          "🧠 Bedömning\n"+verdict+"\n\n✅ Endast läsning.";
      }catch(Exception e){out="Analysen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Vimla – nätanalys",x));
    }).start();
  }

  void ims120(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String cc=runShellText("dumpsys carrier_config 2>/dev/null");
        String svc=runShellText("cmd phone ims get-ims-service -s 0 -d 2>/dev/null; cmd phone ims get-ims-service -s 0 -c 2>/dev/null").trim();
        String pkg=runShellText("pm path com.sec.imsservice 2>/dev/null").trim();
        out="☎️ Kort IMS-kontroll\n\nSamsung IMS-service: "+(pkg.isEmpty()?"🟡 Ej hittad":"✅ Installerad")+"\nIMS-tjänsteval: "+(svc.isEmpty()?"❔ Ej exponerat":one(svc))+"\nVoLTE i CarrierConfig: "+bool120(cc,"carrier_volte_available_bool")+"\nWi-Fi-samtal i CarrierConfig: "+bool120(cc,"carrier_wfc_ims_available_bool")+"\n\nℹ️ CarrierConfig visar operatörsstöd, inte bevis för en pågående IMS-registrering.\n\n✅ Ingen inställning har ändrats.";
      }catch(Exception e){out="IMS-kontrollen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Vimla – IMS",x));
    }).start();
  }
}
