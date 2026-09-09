package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity1106 extends MainActivity1105 {
  static final String GM_PREF="google_mode_disabled_116";
  static final String[][] GM={
    {"Samsung Internet","com.sec.android.app.sbrowser","Chrome kan användas i stället."},
    {"Samsung Internet-komponent","com.samsung.android.app.sbrowseredge","Tillhör Samsung Internet."},
    {"Samsung E-post","com.samsung.android.email.provider","Gmail kan användas i stället. Stäng inte av om du använder Samsung E-post."},
    {"Samsung Kalender","com.samsung.android.calendar","Google Kalender kan användas i stället."},
    {"Samsung Meddelanden","com.samsung.android.messaging","Google Messages kan användas i stället. Byt standardapp först."},
    {"Samsung Reminder","com.samsung.android.app.reminder","Valfri Samsung-påminnelsefunktion."},
    {"Samsung Weather","com.sec.android.daemonapp","Samsung väder och väderwidget påverkas."},
    {"Samsung Weather-widget","com.samsung.android.weather","Samsung väderfunktioner påverkas."},
    {"Samsung Free","com.samsung.android.app.spage","Samsung Free/mediepanel."},
    {"Samsung Global Goals","com.samsung.sree","Fristående Samsung-app."},
    {"Samsung Kids","com.samsung.android.kidsinstaller","Barnläge; valfri om det inte används."},
    {"AR Emoji","com.samsung.android.aremoji","AR Emoji påverkas, inte vanlig kamera."},
    {"AR Emoji Editor","com.samsung.android.aremojieditor","Redigering av AR Emoji påverkas."},
    {"AR-avatarstickers","com.samsung.android.app.camera.sticker.facearavatar.preload","AR-avatarstickers påverkas, inte vanlig kamera."},
    {"Sticker Center","com.samsung.android.stickercenter","Samsung stickers påverkas."},
    {"Samsung Video","com.samsung.android.video","Separat Samsung-videospelare."},
    {"Video Trim","com.samsung.app.newtrim","Samsung videotrimning påverkas."},
    {"Galaxy Themes","com.samsung.android.themestore","Temabutik påverkas; Galaxy Store och One UI lämnas orörda."},
    {"Dynamic Lock Screen","com.samsung.android.dynamiclock","Dynamiska låsskärmsbilder påverkas."},
    {"Smart Suggestions","com.samsung.android.smartsuggestions","Samsung Smart Suggestions påverkas."},
    {"Samsung Interpreter","com.samsung.android.app.interpreter","Samsung Interpreter påverkas."},
    {"Modes and Routines","com.samsung.android.app.routines","Stäng bara av om du inte använder Modes & Routines."},
    {"Game Tools","com.samsung.android.game.gametools","Game Booster/spelverktyg påverkas."},
    {"Samsung Cloud","com.samsung.android.scloud","Samsung Cloud/synk påverkas. Google-backup ersätter inte alla Samsung-synkfunktioner."},
    {"Samsung Wallet-framework","com.samsung.android.spayfw","Samsung Wallet/Pay påverkas."},
    {"Smart Call / Hiya","com.hiya.star","Nummeridentifiering/spamskydd från Samsung/Hiya påverkas."},
    {"OneDrive","com.microsoft.skydrive","Microsoft OneDrive; Google Photos/Drive kan användas i stället."},
    {"Link to Windows","com.microsoft.appmanager","Microsoft Link to Windows påverkas."}
  };

  @Override public void onCreate(Bundle b){super.onCreate(b);}

  @Override void show(){
    base("S23 ULTRA TOOLBOX 11.6","11.5 stable + säkert Google-läge",false);
    dashboard();
    card("📡 Nätverk & signal","Vimla, LTE/5G, signal, CA och IMS.",()->networkHub122());
    card("🔋 Batteri & optimering","Smart batteristatus, appanalys och säkra tester.",()->batteryHub122());
    card("🛡️ Appar & debloat","Google-läge, sök/filter och säker selektiv debloat.",()->appsHub122());
    card("🌡️ Prestanda & temperatur","Thermal Watch, laddtest, Smart Advisor och Grafik & Vulkan.",()->performanceHub122());
    card("📱 Telefon & jobb","GPS/Jobbläge, DeX och kamera.",()->phoneHub122());
    card("🧯 Recovery & diagnostik","Återställning, backup, historik och Shizuku.",()->recoveryHub122());
    card("🧰 Fler verktyg","Rapporter, historik, diagnostik och äldre verktyg.",()->legacyHome122());
  }

  @Override void appsHub122(){
    base("🛡️ APPAR & DEBLOAT","11.6 • Google-läge • sök/filter • återställningsbart",true);
    note(shStatus());
    btn("🧹 Google-läge – Samsung-appanalys",()->googleMode116());
    btn("🔎 Sök / filtrera debloat-paket",()->searchDebloat1104());
    btn("🟢 Välj lågriskpaket",()->chooseDebloat1102("LOW"));
    btn("🟡 Välj valfria paket",()->chooseDebloat1102("OPTIONAL"));
    btn("↩️ Återställ Toolbox-inaktiverade paket",()->restoreDebloat1102());
    btn("🧠 S23 Debloat-granskning 1.0",()->debloatReview1101());
    btn("🛡️ Smart Debloat 8.1",()->debloat());
    btn("📦 App Manager 2.0",()->apps());
  }

  void googleMode116(){
    if(!shOk()){requestSh();return;}
    base("🧹 GOOGLE-LÄGE","Analys först • endast valda appar • helt återställningsbart",true);
    note("🔒 Skyddas alltid: Galaxy Store, Samsung Notes, Kamera, Galleri, One UI/System UI, telefoni/IMS, NFC, OMC/operatör, uppdateringar, DeX och andra systemkritiska komponenter.");
    note("📸 Samsung Galleri behålls för kameraintegration. Google Photos kan användas parallellt för backup och visning.");
    note("Google-läge använder pm disable-user --user 0. Inget avinstalleras. Bara installerade och aktiva valbara appar visas.");
    btn("🔎 Analysera Samsung-appar",()->analyzeGoogleMode116());
    btn("🧹 Välj appar att stänga av",()->chooseGoogleMode116());
    btn("↩️ Återställ Google-läge",()->restoreGoogleMode116());
  }

  void analyzeGoogleMode116(){
    new Thread(()->{
      StringBuilder b=new StringBuilder();int active=0,off=0,missing=0;
      for(String[]x:GM){if(!installed(x[1])){missing++;continue;}if(enabled(x[1])){active++;b.append("🟢 ");}else{off++;b.append("⚫ ");}b.append(x[0]).append("\n   ").append(x[2]).append("\n   ").append(x[1]).append("\n\n");}
      String head="Google-läge analys\n\n🟢 Aktiva valbara: "+active+"\n⚫ Redan inaktiverade: "+off+"\nEj installerade: "+missing+"\n\n🔒 Kamera, Galleri, Galaxy Store och Samsung Notes är skyddade och visas aldrig som valbara.\n\n";
      final String out=head+b.toString();runOnUiThread(()->dialog118("Samsung-appanalys",out));
    }).start();
  }

  void chooseGoogleMode116(){
    new Thread(()->{
      ArrayList<String[]> rows=new ArrayList<>();
      for(String[]x:GM)if(installed(x[1])&&enabled(x[1]))rows.add(x);
      runOnUiThread(()->{
        if(rows.isEmpty()){toast("Inga aktiva valbara Samsung-appar hittades");return;}
        String[] labels=new String[rows.size()];boolean[] checked=new boolean[rows.size()];
        for(int i=0;i<rows.size();i++)labels[i]="🟢 "+rows.get(i)[0]+"\n"+rows.get(i)[2]+"\n↳ "+rows.get(i)[1];
        new AlertDialog.Builder(this).setTitle("Välj appar för Google-läge")
          .setMessage("Inget är förvalt. Markera bara funktioner du vill ersätta eller inte använder. Kamera, Galleri, Galaxy Store och Samsung Notes finns inte i listan.")
          .setMultiChoiceItems(labels,checked,(d,w,c)->checked[w]=c)
          .setNegativeButton("Avbryt",null).setPositiveButton("Fortsätt",(d,w)->confirmGoogleMode116(rows,checked)).show();
      });
    }).start();
  }

  void confirmGoogleMode116(ArrayList<String[]> rows,boolean[] checked){
    ArrayList<String[]> sel=new ArrayList<>();for(int i=0;i<checked.length;i++)if(checked[i])sel.add(rows.get(i));
    if(sel.isEmpty()){toast("Inget valt");return;}
    StringBuilder b=new StringBuilder("Följande stängs av endast för användare 0:\n\n");for(String[]x:sel)b.append("• ").append(x[0]).append("\n");b.append("\nInget avinstalleras. Toolbox sparar exakt vad Google-läget ändrar så att det kan återställas.");
    new AlertDialog.Builder(this).setTitle("Aktivera Google-läge?").setMessage(b.toString()).setNegativeButton("Avbryt",null).setPositiveButton("Stäng av valda",(d,w)->applyGoogleMode116(sel)).show();
  }

  void applyGoogleMode116(ArrayList<String[]> sel){
    new Thread(()->{
      Set<String> saved=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));int ok=0,fail=0;
      for(String[]x:sel){
        try{int c=runCode("pm disable-user --user 0 "+x[1]);if(c==0){saved.add(x[1]);ok++;}else fail++;}
        catch(Exception e){fail++;}
      }
      p.edit().putStringSet(GM_PREF,new HashSet<>(saved)).apply();log("Google-läge: "+ok+" paket avaktiverade");
      final int a=ok,f=fail;runOnUiThread(()->dialog118("Google-läge","✅ Avaktiverade: "+a+(f>0?"\n⚠️ Misslyckades: "+f:"")+"\n\n🔒 Galaxy Store, Samsung Notes, Kamera och Galleri har inte ändrats."));
    }).start();
  }

  void restoreGoogleMode116(){
    Set<String> cur=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));if(cur.isEmpty()){toast("Google-läge har inget sparat att återställa");return;}
    new AlertDialog.Builder(this).setTitle("Återställ Google-läge?").setMessage("Aktiverar endast paket som Google-läget i Toolbox själv har stängt av.").setNegativeButton("Avbryt",null).setPositiveButton("Återställ",(d,w)->new Thread(()->{
      int ok=0,fail=0;Set<String> left=new HashSet<>();
      for(String pkg:cur){
        try{int c=runCode("pm enable --user 0 "+pkg);if(c!=0)c=runCode("pm enable "+pkg);if(c==0)ok++;else{fail++;left.add(pkg);}}
        catch(Exception e){fail++;left.add(pkg);}
      }
      p.edit().putStringSet(GM_PREF,new HashSet<>(left)).apply();log("Google-läge återställning: "+ok+" paket");
      final int a=ok,f=fail;runOnUiThread(()->dialog118("Google-läge återställt","✅ Återställda: "+a+(f>0?"\n⚠️ Kunde inte återställa: "+f:"")));
    }).start()).show();
  }
}
