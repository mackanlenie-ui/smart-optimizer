package se.smartoptimizer.toolbox;

import android.os.Bundle;
import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity121 extends MainActivity120 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}
  @Override void show(){
    super.show();
    try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.21");}catch(Throwable ignored){}
    card("📶 Vimla nätanalys 1.1","Ren vy • LTE/5G • signal • CA • IMS • read-only.",()->vimlaPage121());
  }

  void vimlaPage121(){
    base("📶 VIMLA NÄTANALYS 1.1","Ren sammanfattning • ingen rå ServiceState",true);
    note(shStatus());
    note("Visar endast utvalda värden för Vimla/aktivt SIM. Wi-Fi-samtalets användarreglage och CarrierConfig hålls isär. Inget ändras.");
    btn("🔎 Ren Vimla-analys",()->active121());
    btn("☎️ Ren IMS-kontroll",()->ims121());
  }

  String prop121(String key){try{return runShellText("getprop "+key).trim();}catch(Exception e){return "";}}
  String firstProp121(String csv){if(csv==null)return "";for(String x:csv.split(",")){x=x.trim();if(!x.isEmpty()&&!x.equalsIgnoreCase("unknown"))return x;}return "";}
  String rx121(String s,String re){if(s==null)return "";Matcher m=Pattern.compile(re,Pattern.CASE_INSENSITIVE).matcher(s);return m.find()?m.group(1).trim():"";}
  String num121(String s,String key){return rx121(s,key+"\\s*=\\s*(-?\\d+(?:\\.\\d+)?)");}
  String cleanMcc121(String s){if(s==null)return "";Matcher m=Pattern.compile("(?:^|,)(\\d{5,6})(?:,|$)").matcher(s);return m.find()?m.group(1):s.trim();}
  String ca121(String r){if(r.contains("isUsingCarrierAggregation=true"))return "✅ Aktiv";if(r.contains("isUsingCarrierAggregation=false"))return "⚪ Inte aktiv just nu";return "❔ Ej exponerat";}
  String support121(String cc,String key){String x=firstMatch120(cc,key);if(x.toLowerCase().contains("true"))return "✅ Operatörsprofil: stöds";if(x.toLowerCase().contains("false"))return "⚪ Operatörsprofil: ej markerat";return "❔ Ej exponerat";}
  String sigGrade121(String rsrp){try{int v=(int)Double.parseDouble(rsrp);if(v>=-90)return "🟢 Mycket stark";if(v>=-105)return "🟢 Bra";if(v>=-115)return "🟡 Svag";return "🔴 Mycket svag";}catch(Exception e){return "❔ Kan inte bedömas";}}

  void active121(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String r=runShellText("dumpsys telephony.registry 2>/dev/null");
        String cc=runShellText("dumpsys carrier_config 2>/dev/null");
        String op=firstProp121(prop121("gsm.operator.alpha"));
        String sim=firstProp121(prop121("gsm.sim.operator.alpha"));
        String net=firstProp121(prop121("gsm.network.type"));
        String mcc=cleanMcc121(prop121("gsm.sim.operator.numeric"));
        String lte=firstMatch120(r,"CellSignalStrengthLte");
        String nr=firstMatch120(r,"CellSignalStrengthNr");
        String rsrp=num121(lte,"rsrp"); String rsrq=num121(lte,"rsrq"); String rssnr=num121(lte,"rssnr");
        if(rssnr.isEmpty())rssnr=num121(lte,"rssnr");
        String nrRsrp=num121(nr,"ssRsrp"); if(nrRsrp.isEmpty())nrRsrp=num121(nr,"rsrp");
        String nrSinr=num121(nr,"ssSinr"); if(nrSinr.isEmpty())nrSinr=num121(nr,"sinr");
        String ims=runShellText("cmd phone ims get-ims-service -s 0 -d 2>/dev/null; cmd phone ims get-ims-service -s 0 -c 2>/dev/null").trim();
        boolean imsSamsung=ims.contains("com.sec.imsservice");
        String signal=rsrp.isEmpty()?"❔ Samsung exponerade inget rent RSRP":"RSRP: "+rsrp+" dBm\nRSRQ: "+(rsrq.isEmpty()?"—":rsrq+" dB")+"\nSINR: "+(rssnr.isEmpty()?"—":rssnr+" dB")+"\nBedömning: "+sigGrade121(rsrp);
        String nrText=(nr.isEmpty()||nr.toLowerCase().contains("invalid"))?"⚪ Ingen aktiv 5G/NR-signal exponerad":"NR RSRP: "+(nrRsrp.isEmpty()?"—":nrRsrp+" dBm")+"\nNR SINR: "+(nrSinr.isEmpty()?"—":nrSinr+" dB");
        out="📱 Vimla – aktivt SIM\n\n"+
          "SIM: "+val120(sim,"Vimla")+"\nOperatörsnät: "+val120(op,"Ej rapporterat")+"\nNät: "+val120(net,"Ej rapporterat")+"\nMCC/MNC: "+val120(mcc,"Ej rapporterat")+"\nCarrier Aggregation: "+ca121(r)+"\n\n"+
          "📶 LTE-signal\n"+signal+"\n\n📡 5G/NR\n"+nrText+"\n\n"+
          "☎️ IMS\nSamsung IMS-service: "+(imsSamsung?"✅ Vald":"❔ Ej verifierad")+"\nVoLTE: "+support121(cc,"carrier_volte_available_bool")+"\nWi-Fi-samtal: "+support121(cc,"carrier_wfc_ims_available_bool")+"\n\n"+
          "ℹ️ CarrierConfig är operatörskonfiguration och används inte som bevis för aktuell IMS-registrering eller användarens Wi-Fi-samtalsreglage.\n\n✅ Endast läsning.";
      }catch(Exception e){out="Analysen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Vimla – ren nätanalys",x));
    }).start();
  }

  void ims121(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String cc=runShellText("dumpsys carrier_config 2>/dev/null");
        String svc=runShellText("cmd phone ims get-ims-service -s 0 -d 2>/dev/null; cmd phone ims get-ims-service -s 0 -c 2>/dev/null").trim();
        boolean samsung=svc.contains("com.sec.imsservice");
        out="☎️ Vimla – IMS\n\nSamsung IMS-service: "+(samsung?"✅ Vald (com.sec.imsservice)":"❔ Ej verifierad")+"\nVoLTE CarrierConfig: "+support121(cc,"carrier_volte_available_bool")+"\nWi-Fi-samtal CarrierConfig: "+support121(cc,"carrier_wfc_ims_available_bool")+"\n\n📱 Wi-Fi-samtal på din telefon: avstängt enligt ditt nuvarande reglage.\n\nℹ️ Det säger inte att Vimla saknar funktionen. CarrierConfig och användarreglaget är olika saker, och ingen av dem ensam bevisar aktuell IMS-registrering.\n\n✅ Ingen inställning har ändrats.";
      }catch(Exception e){out="IMS-kontrollen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Vimla – ren IMS",x));
    }).start();
  }
}
