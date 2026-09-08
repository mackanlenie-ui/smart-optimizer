package se.smartoptimizer.toolbox;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity126 extends MainActivity125 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}

  @Override void show(){
    super.show();
    try{((android.widget.TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.26");}catch(Throwable ignored){}
  }

  @Override void batteryHub122(){
    base("🔋 BATTERI & OPTIMERING","Kryptonian 1.5 • smart appanalys • filtrerad Doze",true);
    note(shStatus());
    note("10.26 filtrerar bort systembruset och fokuserar på installerade användarappar. Analysen ändrar ingenting och rekommenderar inte begränsning av meddelande-, bank- eller samtalsappar.");
    btn("🧠 Smart app- & Doze-analys 1.0",()->smartStandby126());
    btn("📊 Avancerad rå Standby/Doze-lista",()->standbyDoze125());
    btn("🧠 Kryptonian säkerhetsanalys 1.3",()->kryptonAnalyze124());
    btn("📦 Kryptonian ZIP – full granskning 1.0",()->kryptonFullReview125());
    btn("🛡️ Samsung Pass – status / valfri debloat",()->samsungPass124());
    btn("🔋 Smart batterianalys 1.1",()->smartBatteryPage());
    btn("🛡️ Säkra batteritweaks 1.0",()->batteryOptimizerPage());
    btn("📶 Test: Wi-Fi scan throttling = PÅ",()->confirmWifiThrottle123());
    btn("↩️ Återställ Wi-Fi scan throttling",()->restoreWifiThrottle123());
    btn("📶 Test: mobil data alltid aktiv = AV",()->confirmMobileDataAlwaysOn122());
    btn("↩️ Återställ mobil data alltid aktiv",()->restoreMobileDataAlwaysOn122());
  }

  String bucketName126(int v){
    if(v<=5)return "Exempt";
    if(v==10)return "Active";
    if(v==20)return "Working Set";
    if(v==30)return "Frequent";
    if(v==40)return "Rare";
    if(v==45)return "Restricted";
    if(v>=50)return "Never/unused";
    return "Bucket "+v;
  }

  String friendly126(String p){
    if("com.google.android.youtube".equals(p))return "YouTube";
    if("se.nordea.mobilebank".equals(p))return "Nordea";
    if("com.whatsapp".equals(p))return "WhatsApp";
    if("com.openai.chatgpt".equals(p))return "ChatGPT";
    if("com.google.android.apps.messaging".equals(p))return "Google Messages";
    if("se.bankgirot.swish".equals(p))return "Swish";
    if("com.instagram.android".equals(p))return "Instagram";
    if("com.microsoft.skydrive".equals(p))return "OneDrive";
    if("org.zwanoo.android.speedtest".equals(p))return "Speedtest";
    if("com.google.android.gm".equals(p))return "Gmail";
    if("com.touchtype.swiftkey".equals(p))return "SwiftKey";
    if("se.smartoptimizer.toolbox".equals(p))return "S23 Ultra Toolbox";
    return p;
  }

  boolean protect126(String p){
    return p.contains("whatsapp") || p.contains("messaging") || p.contains("swish") || p.contains("nordea") ||
      p.contains("bank") || p.contains("auth") || p.contains("phone") || p.contains("dialer") || p.contains("ims") ||
      p.contains("alarm") || p.contains("clock") || p.contains("calendar") || p.contains("mail") || p.equals("com.google.android.gm");
  }

  void smartStandby126(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String users=runShellText("pm list packages -3");
        String buckets=runShellText("am get-standby-bucket");
        String whitelist=runShellText("dumpsys deviceidle whitelist");
        String idle=runShellText("dumpsys deviceidle | grep -E 'mState=|mLightState=|mForceIdle=|mScreenOn=|mCharging=' | head -n 12");

        Set<String> userPkgs=new HashSet<>();
        for(String l:users.split("\\n")){
          l=l.trim();
          if(l.startsWith("package:"))userPkgs.add(l.substring(8).trim());
        }

        Map<String,Integer> map=new HashMap<>();
        for(String l:buckets.split("\\n")){
          int c=l.lastIndexOf(':');
          if(c<=0)continue;
          String pkg=l.substring(0,c).trim();
          try{map.put(pkg,Integer.parseInt(l.substring(c+1).trim()));}catch(Throwable ignored){}
        }

        ArrayList<String> active=new ArrayList<>(), sleeping=new ArrayList<>(), protectedApps=new ArrayList<>(), dozeUsers=new ArrayList<>(), unknown=new ArrayList<>();
        for(String pkg:userPkgs){
          Integer b=map.get(pkg);
          String n=friendly126(pkg);
          boolean wl=whitelist!=null && whitelist.contains(pkg);
          String row="• "+n+(n.equals(pkg)?"":" ("+pkg+")")+" — "+(b==null?"okänd bucket":bucketName126(b)+" ["+b+"]")+(wl?" • Doze-undantag":"");
          if(wl)dozeUsers.add(row);
          if(protect126(pkg))protectedApps.add(row+" • skyddas från automatiska råd");
          else if(b==null)unknown.add(row);
          else if(b<=20)active.add(row);
          else sleeping.add(row);
        }

        StringBuilder s=new StringBuilder();
        s.append("🧠 SMART APP- & DOZE-ANALYS 1.0\n\n");
        s.append("Installerade användarappar: ").append(userPkgs.size()).append("\n");
        s.append("Aktiva/Working Set: ").append(active.size()).append("\n");
        s.append("Frequent/Rare/Restricted/Never: ").append(sleeping.size()).append("\n");
        s.append("Användarappar i Doze-undantag: ").append(dozeUsers.size()).append("\n\n");

        s.append("🔎 AKTIVA APPAR ATT GRANSKA\n");
        if(active.isEmpty())s.append("✅ Inga tydliga kandidater.\n");
        else for(String r:active)s.append(r).append("\n");

        s.append("\n🛡️ VIKTIGA APPAR – ÄNDRA INTE AUTOMATISKT\n");
        if(protectedApps.isEmpty())s.append("Inga särskilt skyddade användarappar hittades.\n");
        else for(String r:protectedApps)s.append(r).append("\n");

        s.append("\n🌙 REDAN BEGRÄNSADE / SOVANDE\n");
        if(sleeping.isEmpty())s.append("Inga.\n");
        else{
          int n=0;for(String r:sleeping){if(n++>=20){s.append("… fler finns i rålistan\n");break;}s.append(r).append("\n");}
        }

        s.append("\n⚪ DOZE-UNDANTAG BLAND ANVÄNDARAPPAR\n");
        if(dozeUsers.isEmpty())s.append("✅ Inga ovanliga användarappar hittades i whitelist.\n");
        else for(String r:dozeUsers)s.append(r).append("\n");

        if(!unknown.isEmpty())s.append("\nℹ️ ").append(unknown.size()).append(" användarappar saknade tydlig bucket på denna firmware.\n");
        s.append("\n📱 DEVICEIDLE JUST NU\n").append(idle==null||idle.trim().isEmpty()?"Ej exponerat.":idle.trim()).append("\n\n");
        s.append("✅ Analysen är read-only. Rekommendation: optimera bara appar som ligger Active/Working Set och som du inte behöver notiser eller bakgrundsarbete från. Systemappar visas inte i huvudlistan.");
        out=s.toString();
      }catch(Exception e){out="Analysen misslyckades: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Smart app- & Doze-analys",x));
    }).start();
  }
}
