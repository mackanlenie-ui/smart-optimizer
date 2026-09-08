package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity119 extends MainActivity118 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}
  @Override void show(){
    super.show();
    try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.19");}catch(Throwable ignored){}
    card("📡 Samsung Radio & IMS 1.2","Samsung-anpassad • SIM-slot • CarrierConfig • IMS-service • read-only.",()->samsungImsPage());
  }

  void samsungImsPage(){
    base("📡 SAMSUNG RADIO & IMS 1.2","S23 Ultra • filtrerad • read-only",true);
    note(shStatus());
    note("Samsung-anpassad diagnostik. Läser SIM-slot, operatör, nätverk, CarrierConfig och vald IMS-service. Inga radio- eller IMS-inställningar ändras.");
    btn("📱 Analysera SIM 1",()->samsungSlot(0));
    btn("📱 Analysera SIM 2 / eSIM",()->samsungSlot(1));
    btn("☎️ Kontrollera IMS-service",()->imsService119());
    btn("🧾 Exportvänlig diagnos",()->compact119());
  }

  String one(String s){if(s==null)return "";s=s.trim();int n=s.indexOf('\n');return n>=0?s.substring(0,n).trim():s;}
  String yn(String s,String key){if(s.contains(key+" = true")||s.contains(key+"=true"))return "✅ Ja";if(s.contains(key+" = false")||s.contains(key+"=false"))return "❌ Nej";return "❔ Ej rapporterat";}

  void samsungSlot(int slot){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String op=runShellText("getprop gsm.operator.alpha").trim();
        String net=runShellText("getprop gsm.network.type").trim();
        String mcc=runShellText("getprop gsm.sim.operator.numeric").trim();
        String cc=runShellText("dumpsys carrier_config 2>/dev/null | grep -iE -m80 'subId|slot|carrier_volte_available_bool|carrier_wfc_ims_available_bool|carrier_vt_available_bool|editable_wfc_mode_bool|ims' ").trim();
        String reg=runShellText("dumpsys telephony.registry 2>/dev/null | grep -iE -m80 'mPhoneId=|mSubId=|mDataNetworkType=|mVoiceNetworkType=|mOperatorAlphaLong=|isUsingCarrierAggregation=|CellSignalStrengthLte|CellSignalStrengthNr' ").trim();
        String services=runShellText("cmd phone ims get-ims-service -s "+slot+" -d 2>/dev/null; cmd phone ims get-ims-service -s "+slot+" -c 2>/dev/null").trim();
        String title=slot==0?"SIM 1":"SIM 2 / eSIM";
        out="📱 "+title+"\n\nOperatör(er): "+(op.isEmpty()?"Ej rapporterat":op)+"\nNätverk: "+(net.isEmpty()?"Ej rapporterat":net)+"\nSIM MCC/MNC: "+(mcc.isEmpty()?"Ej rapporterat":mcc)+"\n\n📞 CarrierConfig\nVoLTE tillgängligt: "+yn(cc,"carrier_volte_available_bool")+"\nWi-Fi-samtal tillgängligt: "+yn(cc,"carrier_wfc_ims_available_bool")+"\nVideo över IMS: "+yn(cc,"carrier_vt_available_bool")+"\n\n🔧 IMS-service för slot "+slot+"\n"+(services.isEmpty()?"Ej exponerad via cmd phone.":services)+"\n\n📡 Filtrerad radio\n"+(reg.isEmpty()?"Ingen filtrerad telephony-data.":reg)+"\n\nℹ️ Operatörslistan kan innehålla båda SIM. Slot-specifika rader används där Samsung exponerar dem.\n\n✅ Inget har ändrats.";
      }catch(Exception e){out="Analysen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Samsung SIM-analys",x));
    }).start();
  }

  void imsService119(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String x;
      try{
        String list=runShellText("dumpsys -l 2>/dev/null | grep -iE 'ims|telephony' | head -40").trim();
        String p0=runShellText("cmd phone ims get-ims-service -s 0 -d 2>/dev/null; cmd phone ims get-ims-service -s 0 -c 2>/dev/null").trim();
        String p1=runShellText("cmd phone ims get-ims-service -s 1 -d 2>/dev/null; cmd phone ims get-ims-service -s 1 -c 2>/dev/null").trim();
        x="☎️ IMS-service\n\nSIM 1:\n"+(p0.isEmpty()?"Ej exponerad":p0)+"\n\nSIM 2/eSIM:\n"+(p1.isEmpty()?"Ej exponerad":p1)+"\n\nSamsung/Android-tjänster:\n"+(list.isEmpty()?"Inga namn exponerades.":list)+"\n\nDetta visar tjänsteval, inte i sig om ett pågående IMS-register är aktivt.\n\n✅ Read-only.";
      }catch(Exception e){x="Kunde inte läsa IMS-service: "+e.getClass().getSimpleName();}
      final String y=x;runOnUiThread(()->dialog118("IMS-service",y));
    }).start();
  }

  void compact119(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String x;
      try{x=runShellText("echo '=== PROPS ==='; getprop gsm.operator.alpha; getprop gsm.network.type; getprop gsm.sim.operator.numeric; echo '=== IMS SERVICES ==='; cmd phone ims get-ims-service -s 0 -d 2>/dev/null; cmd phone ims get-ims-service -s 0 -c 2>/dev/null; cmd phone ims get-ims-service -s 1 -d 2>/dev/null; cmd phone ims get-ims-service -s 1 -c 2>/dev/null; echo '=== CARRIER CONFIG ==='; dumpsys carrier_config 2>/dev/null | grep -iE -m50 'subId|slot|carrier_volte_available_bool|carrier_wfc_ims_available_bool|carrier_vt_available_bool'; echo '=== RADIO ==='; dumpsys telephony.registry 2>/dev/null | grep -iE -m50 'mPhoneId=|mSubId=|mDataNetworkType=|mVoiceNetworkType=|mOperatorAlphaLong=|isUsingCarrierAggregation=|CellSignalStrengthLte|CellSignalStrengthNr'");if(x.trim().isEmpty())x="Ingen diagnosdata.";}catch(Exception e){x="Diagnosen misslyckades: "+e.getClass().getSimpleName();}
      final String y=x;runOnUiThread(()->dialog118("Kompakt diagnos",y));
    }).start();
  }
}
