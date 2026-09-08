package se.smartoptimizer.toolbox;

import android.os.Bundle;

public class MainActivity127 extends MainActivity126 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}

  @Override void show(){
    super.show();
    try{((android.widget.TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.27");}catch(Throwable ignored){}
  }

  @Override void legacyHome122(){
    base("🧰 FLER VERKTYG","Aktuella verktyg som inte redan finns i huvudkategorierna",true);
    note("10.27 har städat bort gamla och dubbla versioner. Senaste versionen av varje analys ligger i respektive kategori.");
    btn("🕘 Historik",()->history());
    btn("💾 Backup / Diagnostik",()->diag());
    btn("🔧 Shizuku Tools",()->shPage());
  }

  @Override void batteryHub122(){
    base("🔋 BATTERI & OPTIMERING","Kryptonian 1.5 • smart appanalys • selektiv optimering",true);
    note(shStatus());
    note("Gamla/dubbla analyser är bortstädade. Här visas bara den senaste relevanta versionen av varje verktyg.");
    btn("🧠 Smart app- & Doze-analys 1.0",()->smartStandby126());
    btn("🎯 Selektiv appoptimering 1.0",()->selective127());
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

  void selective127(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      try{
        String b=runShellText("am get-standby-bucket");
        StringBuilder s=new StringBuilder("🎯 SELEKTIV APPOPTIMERING 1.0\n\n");
        s.append("Utifrån din senaste analys är telefonen redan väloptimerad.\n\n🟡 GRANSKA VID BEHOV\n");
        addCandidate127(s,b,"com.facebook.katana","Facebook");
        addCandidate127(s,b,"com.zhiliaoapp.musically","TikTok");
        addCandidate127(s,b,"com.google.android.apps.photos","Google Foto");
        s.append("\n🛡️ SKYDDAS\nWhatsApp, Google Messages, Nordea, Swish, Gmail/e-post, telefon/IMS, kalender, alarm/klocka och autentiseringsappar ändras inte automatiskt.\n\n🌙 Appar som redan ligger Frequent/Rare/Restricted/Never lämnas orörda.\n\nℹ️ One UI/Android kan själv flytta appar mellan buckets efter användning. Hårda ändringar kan försena notiser eller bakgrundsjobb.\n\n✅ Ingen ändring gjord.");
        final String x=s.toString();runOnUiThread(()->dialog118("Selektiv appoptimering",x));
      }catch(Exception e){
        final String err="Kunde inte läsa standby-status: "+e.getMessage();
        runOnUiThread(()->dialog118("Selektiv appoptimering",err));
      }
    }).start();
  }

  void addCandidate127(StringBuilder s,String all,String pkg,String name){
    if(all==null||!all.contains(pkg+":"))return;
    int pos=all.indexOf(pkg+":"); int end=all.indexOf('\n',pos); if(end<0)end=all.length();
    s.append("• ").append(name).append(" — ").append(all.substring(pos,end).trim()).append("\n");
  }
}
