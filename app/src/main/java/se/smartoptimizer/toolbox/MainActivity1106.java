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
    {"Samsung Reminder","com.samsung.android.app.reminder","Valfri Samsung-påminnelsefunktion."},
    {"Samsung Weather","com.sec.android.daemonapp","Google-väder kan användas i stället; Samsung väderwidget påverkas."},
    {"Samsung Weather-widget","com.samsung.android.weather","Samsung väderfunktioner påverkas."},
    {"Samsung Free","com.samsung.android.app.spage","Samsung Free/mediepanel."},
    {"Samsung Global Goals","com.samsung.sree","Fristående Samsung-app."},
    {"Samsung Kids","com.samsung.android.kidsinstaller","Barnläge; valfri om det inte används."},
    {"AR Emoji","com.samsung.android.aremoji","AR Emoji påverkas, inte vanlig kamera."},
    {"AR-avatarstickers","com.sec.android.mimage.avatarstickers","AR-avatarstickers påverkas."},
    {"Sticker Center","com.samsung.android.stickercenter","Samsung stickers påverkas."},
    {"Samsung Video","com.samsung.android.video","Separat Samsung-videospelare."},
    {"Video Trim","com.samsung.app.newtrim","Samsung videotrimning påverkas."},
    {"Galaxy Themes","com.samsung.android.themestore","Temabutik påverkas; One UI och Galaxy Store lämnas orörda."},
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

  @Override void appsHub122(){
    base("🛡️ APPAR & DEBLOAT","11.6 • säker debloat • Google-läge",true);
    btn("🧹 Google-läge – Samsung-appanalys",()->googleMode116());
    btn("🛡️ Smart Debloat 8.1",()->debloat1102());
    btn("📦 App Manager 2.0",()->apps());
    btn("🧠 Smart Advisor 4.0",()->advisor());
  }

  void googleMode116(){
    if(!shOk()){requestSh();return;}
    base("🧹 GOOGLE-LÄGE","Analys först • endast valda appar • helt återställningsbart",true);
    note("🔒 Skyddas alltid: Galaxy Store, Samsung Notes, Kamera, Galleri, One UI/System UI, telefoni/IMS, NFC, OMC/operatör, uppdateringar, DeX och andra systemkritiska komponenter. Google Photos kan användas parallellt med Samsung Galleri.");
    note("Google-läge använder pm disable-user --user 0. Inget avinstalleras. Bara appar som faktiskt finns installerade och är aktiva visas som valbara.");
    btn("🔎 Analysera Samsung-appar",()->analyzeGoogleMode116());
    btn("🧹 Välj appar att stänga av",()->chooseGoogleMode116());
    btn("↩️ Återställ Google-läge",()->restoreGoogleMode116());
  }

  void analyzeGoogleMode116(){
    new Thread(()->{
      StringBuilder b=new StringBuilder();int active=0,off=0,missing=0;
      for(String[]x:GM){if(!installed(x[1])){missing++;continue;}if(enabled(x[1])){active++;b.append("🟢 ");}else{off++;b.append("⚫ ");}b.append(x[0]).append("\n   ").append(x[2]).append("\n   ").append(x[1]).append("\n\n");}
      String head="Google-läge analys\n\n🟢 Aktiva valbara: "+active+"\n⚫ Redan inaktiverade: "+off+"\nEj installerade: "+missing+"\n\n";
      final String out=head+b.toString();runOnUiThread(()->dialog118("Samsung-appanalys",out));
    }).start();
  }

  void chooseGoogleMode116(){
    new Thread(()->{
      ArrayList<String[]> rows=new ArrayList<>();
      for(String[]x:GM)if(installed(x[1])&&enabled(x[1]))rows.add(x);
      runOnUiThread(()->{if(rows.isEmpty()){toast("Inga aktiva valbara Samsung-appar hittades");return;}String[] labels=new String[rows.size()];boolean[] checked=new boolean[rows.size()];for(int i=0;i<rows.size();i++)labels[i]="🟢 "+rows.get(i)[0]+"\n"+rows.get(i)[2]+"\n"+rows.get(i)[1];new AlertDialog.Builder(this).setTitle("Välj appar för Google-läge").setMessage("Inget är förvalt. Markera bara funktioner du vill ersätta eller inte använder. Kamera, Galleri, Galaxy Store och Samsung Notes finns inte i listan.").setMultiChoiceItems(labels,checked,(d,w,c)->checked[w]=c).setNegativeButton("Avbryt",null).setPositiveButton("Fortsätt",(d,w)->confirmGoogleMode116(rows,checked)).show();});
    }).start();
  }

  void confirmGoogleMode116(ArrayList<String[]> rows,boolean[] checked){
    ArrayList<String[]> sel=new ArrayList<>();for(int i=0;i<checked.length;i++)if(checked[i])sel.add(rows.get(i));
    if(sel.isEmpty()){toast("Inget valt");return;}
    StringBuilder b=new StringBuilder("Toolbox stänger endast av följande för användare 0:\n\n");for(String[]x:sel)b.append("• ").append(x[0]).append("\n");b.append("\nInget avinstalleras och endast Toolbox-valen kan återställas med knappen Återställ Google-läge.");
    new AlertDialog.Builder(this).setTitle("Aktivera Google-läge?").setMessage(b.toString()).setNegativeButton("Avbryt",null).setPositiveButton("Stäng av valda",(d,w)->applyGoogleMode116(sel)).show();
  }

  void applyGoogleMode116(ArrayList<String[]> sel){
    new Thread(()->{
      Set<String> saved=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));int ok=0,fail=0;
      for(String[]x:sel){if(runCode("pm disable-user --user 0 "+x[1])==0){saved.add(x[1]);ok++;}else fail++;}
      p.edit().putStringSet(GM_PREF,saved).apply();log("Google-läge: "+ok+" paket avaktiverade");final int a=ok,f=fail;runOnUiThread(()->dialog118("Google-läge","✅ Avaktiverade: "+a+(f>0?"\n⚠️ Misslyckades: "+f:"")+"\n\nGalaxy Store, Samsung Notes, Kamera och Galleri har inte ändrats."));
    }).start();
  }

  void restoreGoogleMode116(){
    Set<String> cur=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));if(cur.isEmpty()){toast("Google-läge har inget sparat att återställa");return;}
    new AlertDialog.Builder(this).setTitle("Återställ Google-läge?").setMessage("Aktiverar endast paket som Google-läget i Toolbox själv har stängt av.").setNegativeButton("Avbryt",null).setPositiveButton("Återställ",(d,w)->new Thread(()->{int ok=0,fail=0;Set<String> left=new HashSet<>();for(String pkg:cur){if(runCode("pm enable "+pkg)==0)ok++;else{fail++;left.add(pkg);}}p.edit().putStringSet(GM_PREF,left).apply();log("Google-läge återställning: "+ok+" paket");final int a=ok,f=fail;runOnUiThread(()->dialog118("Google-läge återställt","✅ Återställda: "+a+(f>0?"\n⚠️ Kunde inte återställa: "+f:"")));}).start()).show();
  }
}
