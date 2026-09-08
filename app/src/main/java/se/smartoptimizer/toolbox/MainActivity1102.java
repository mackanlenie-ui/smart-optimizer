package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;
import java.util.*;

public class MainActivity1102 extends MainActivity1101 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}

  static class DebItem {
    final String pkg,name,desc,level;
    DebItem(String p,String n,String d,String l){pkg=p;name=n;desc=d;level=l;}
    String row(){return name+"\n"+desc+"\n"+pkg;}
  }

  static final DebItem[] DEB1102={
    new DebItem("com.android.egg","Android Easter Egg","Dold Android-demo. Påverkar inte normal användning.","LOW"),
    new DebItem("com.android.dreams.phototable","Photo Table-skärmsläckare","Äldre Daydream/fotobordsfunktion.","LOW"),
    new DebItem("com.android.traceur","System Tracing","Utvecklarverktyg för systemspårning.","LOW"),
    new DebItem("com.facebook.appmanager","Meta App Manager","Meta/Facebook-förinstallerad uppdateringskomponent.","LOW"),
    new DebItem("com.facebook.services","Meta Services","Bakgrundstjänst för Meta/Facebook-förinstallation.","LOW"),
    new DebItem("com.facebook.system","Meta System","Systemdel för Meta/Facebook-förinstallation.","LOW"),
    new DebItem("com.samsung.android.aremoji","AR Emoji","Samsung AR Emoji-funktioner.","LOW"),
    new DebItem("com.samsung.android.aremojieditor","AR Emoji Editor","Redigering av Samsung AR Emoji.","LOW"),
    new DebItem("com.samsung.android.app.camera.sticker.facearavatar.preload","AR-avatarstickers","Förinstallerade kamera-/avatarstickers.","LOW"),
    new DebItem("com.samsung.android.stickercenter","Sticker Center","Samsung-tjänst för stickers.","LOW"),
    new DebItem("com.samsung.storyservice","Samsung Story Service","Automatiska berättelser/minnen i Samsung-ekosystemet.","LOW"),

    new DebItem("com.samsung.android.game.gametools","Game Tools","Spelpanel, inspelning och spelverktyg. Behåll om du spelar.","OPTIONAL"),
    new DebItem("com.samsung.android.themestore","Galaxy Themes","Teman, ikoner och bakgrunder från Galaxy Themes.","OPTIONAL"),
    new DebItem("com.samsung.android.kidsinstaller","Samsung Kids","Installerare för Samsung Kids.","OPTIONAL"),
    new DebItem("com.samsung.android.app.watchmanagerstub","Galaxy Wearable-stub","Hjälper anslutning till Galaxy Watch/Wearable.","OPTIONAL"),
    new DebItem("com.samsung.android.video","Samsung Video","Samsung-komponent för videohantering/uppspelning.","OPTIONAL"),
    new DebItem("com.samsung.app.newtrim","Video Trim","Samsung-funktion för enkel videotrimning.","OPTIONAL"),
    new DebItem("com.microsoft.skydrive","Microsoft OneDrive","Molnlagring och eventuell galleri-synk.","OPTIONAL"),
    new DebItem("com.microsoft.appmanager","Länk till Windows","Microsoft Phone Link/Länk till Windows.","OPTIONAL"),
    new DebItem("com.hiya.star","Hiya/Smart Call","Identifiering av okända samtal och spamfunktioner.","OPTIONAL"),
    new DebItem("com.android.chrome","Google Chrome","Webbläsare. Inaktivera bara om du använder en annan.","OPTIONAL"),
    new DebItem("com.google.android.gm","Gmail","Google Gmail. Kan påverka e-postnotiser.","OPTIONAL"),
    new DebItem("com.google.android.youtube","YouTube","YouTube-appen.","OPTIONAL"),
    new DebItem("com.google.android.googlequicksearchbox","Google-appen","Sök, Discover, Assistant/Gemini-integrationer kan påverkas.","OPTIONAL"),
    new DebItem("com.google.ar.core","Google Play Services for AR","Behövs av appar med AR-funktioner.","OPTIONAL"),
    new DebItem("com.samsung.android.app.interpreter","Samsung Interpreter","Tolkläge/översättning på enheten.","OPTIONAL"),
    new DebItem("com.samsung.android.app.routines","Modes and Routines","Samsung Modes & Routines. Behåll om du använder automationer.","OPTIONAL"),
    new DebItem("com.samsung.android.bixby.agent","Bixby","Samsung Bixby-tjänst.","OPTIONAL"),
    new DebItem("com.samsung.android.bixby.wakeup","Bixby Wakeup","Röstväckning för Bixby.","OPTIONAL"),
    new DebItem("com.samsung.android.dynamiclock","Dynamic Lock Screen","Dynamiska låsskärmsbakgrunder.","OPTIONAL"),
    new DebItem("com.samsung.android.scloud","Samsung Cloud","Samsung Cloud/synk. Behåll om du använder Samsung-backup/synk.","OPTIONAL"),
    new DebItem("com.samsung.android.smartsuggestions","Smart Suggestions","Samsung smarta förslag i systemet.","OPTIONAL"),
    new DebItem("com.samsung.android.spayfw","Samsung Wallet-ramverk","Kan behövas av Samsung Wallet/Pay. Inaktivera bara om du inte använder det.","OPTIONAL"),
    new DebItem("com.samsung.knox.securefolder","Secure Folder","Samsung Säker mapp. Inaktivera bara om du inte använder den.","OPTIONAL"),
    new DebItem("com.sec.android.easyMover","Smart Switch","Överföring av data mellan telefoner.","OPTIONAL"),
    new DebItem("com.sec.android.easyMover.Agent","Smart Switch Agent","Bakgrundskomponent för Smart Switch.","OPTIONAL"),
    new DebItem("com.touchtype.swiftkey","Microsoft SwiftKey","Tangentbord. Inaktivera bara om du använder annat tangentbord.","OPTIONAL")
  };

  @Override void show(){
    base("S23 ULTRA TOOLBOX 11.2","Interaktiv S23 Debloat • begripliga namn • full återställning",false);
    dashboard();
    card("📡 Nätverk & signal","Vimla, LTE/5G, signal, CA och IMS.",()->networkHub122());
    card("🔋 Batteri & optimering","Smart batteristatus, appanalys och säkra tester.",()->batteryHub122());
    card("🛡️ Appar & debloat","Välj enskilda paket, se funktion och återställ när du vill.",()->appsHub122());
    card("🌡️ Prestanda & temperatur","Thermal Watch, laddtest och Smart Advisor.",()->performanceHub122());
    card("📱 Telefon & jobb","GPS/Jobbläge, DeX och kamera.",()->phoneHub122());
    card("🧯 Recovery & diagnostik","Återställning, backup, historik och Shizuku.",()->recoveryHub122());
    card("🧰 Fler verktyg","Rapporter, historik, diagnostik och äldre verktyg.",()->legacyHome122());
  }

  @Override void appsHub122(){
    base("🛡️ APPAR & DEBLOAT","Välj själv • disable-user • Toolbox minns exakt vad den ändrar",true);
    note(shStatus());
    note("🟢 Lågrisk = normalt fristående extrafunktioner. 🟡 Valfritt = inaktivera bara om du vet att du inte använder funktionen. Telefon/IMS, Wi-Fi, NFC, kamera, setup, säkerhet och andra kritiska systempaket är inte valbara här.");
    btn("🟢 Välj lågriskpaket",()->chooseDebloat1102("LOW"));
    btn("🟡 Välj valfria paket",()->chooseDebloat1102("OPTIONAL"));
    btn("↩️ Återställ Toolbox-inaktiverade paket",()->restoreDebloat1102());
    btn("🧠 S23 Debloat-granskning 1.0",()->debloatReview1101());
    btn("🛡️ Smart Debloat 8.0",()->debloat());
    btn("📦 App Manager 2.0",()->apps());
  }

  void chooseDebloat1102(String level){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      try{
        Set<String> installed=packageSet1102("pm list packages");
        Set<String> enabled=packageSet1102("pm list packages -e");
        ArrayList<DebItem> items=new ArrayList<>();
        for(DebItem d:DEB1102) if(d.level.equals(level)&&installed.contains(d.pkg)&&enabled.contains(d.pkg)) items.add(d);
        runOnUiThread(()->showChoice1102(level,items));
      }catch(Exception e){runOnUiThread(()->dialog118("Debloat","Kunde inte läsa paketstatus: "+e.getClass().getSimpleName()));}
    }).start();
  }

  void showChoice1102(String level,ArrayList<DebItem> items){
    if(items.isEmpty()){dialog118("Debloat","Inga aktiva installerade paket i den här kategorin hittades.");return;}
    String[] rows=new String[items.size()]; boolean[] checked=new boolean[items.size()];
    for(int i=0;i<items.size();i++) rows[i]=items.get(i).row();
    String title="LOW".equals(level)?"🟢 Välj lågriskpaket":"🟡 Välj valfria paket";
    AlertDialog dlg=new AlertDialog.Builder(this).setTitle(title)
      .setMultiChoiceItems(rows,checked,(d,w,is)->checked[w]=is)
      .setNegativeButton("Avbryt",null)
      .setPositiveButton("Inaktivera valda",null).create();
    dlg.setOnShowListener(x->dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
      ArrayList<DebItem> sel=new ArrayList<>();for(int i=0;i<checked.length;i++)if(checked[i])sel.add(items.get(i));
      if(sel.isEmpty()){toast("Välj minst ett paket");return;}
      dlg.dismiss();confirmDisable1102(sel);
    }));dlg.show();
  }

  void confirmDisable1102(ArrayList<DebItem> sel){
    StringBuilder s=new StringBuilder("Följande kommer att inaktiveras för användare 0:\n\n");
    for(DebItem d:sel)s.append("• ").append(d.name).append("\n");
    s.append("\nIngen app avinstalleras. Toolbox sparar paketnamnen så att de kan återställas.");
    new AlertDialog.Builder(this).setTitle("Bekräfta debloat").setMessage(s.toString()).setNegativeButton("Avbryt",null).setPositiveButton("Inaktivera",(d,w)->applyDisable1102(sel)).show();
  }

  void applyDisable1102(ArrayList<DebItem> sel){
    new Thread(()->{
      int ok=0,fail=0;Set<String> tracked=tracked1102();StringBuilder r=new StringBuilder();
      for(DebItem d:sel){try{int c=runCode("pm disable-user --user 0 "+d.pkg);if(c==0){ok++;tracked.add(d.pkg);r.append("✅ ").append(d.name).append("\n");}else{fail++;r.append("⚠️ ").append(d.name).append(" (kod ").append(c).append(")\n");}}catch(Exception e){fail++;r.append("⚠️ ").append(d.name).append("\n");}}
      saveTracked1102(tracked);log("S23 Debloat 11.2: "+ok+" paket inaktiverade, "+fail+" misslyckades");
      String out="Klart: "+ok+" inaktiverade"+(fail>0?", "+fail+" misslyckades":"")+".\n\n"+r+"\n↩️ Du kan återställa dessa via återställningsknappen.";
      runOnUiThread(()->dialog118("S23 Debloat 11.2",out));
    }).start();
  }

  void restoreDebloat1102(){
    if(!shOk()){requestSh();return;}
    Set<String> t=tracked1102();
    if(t.isEmpty()){dialog118("Återställ debloat","Toolbox 11.2 har inga sparade debloat-paket att återställa.");return;}
    ArrayList<String> pkgs=new ArrayList<>(t);Collections.sort(pkgs);String[] rows=new String[pkgs.size()];boolean[] chk=new boolean[pkgs.size()];
    for(int i=0;i<pkgs.size();i++){rows[i]=nameFor1102(pkgs.get(i))+"\n"+pkgs.get(i);chk[i]=true;}
    AlertDialog dlg=new AlertDialog.Builder(this).setTitle("↩️ Återställ paket").setMultiChoiceItems(rows,chk,(d,w,is)->chk[w]=is).setNegativeButton("Avbryt",null).setPositiveButton("Återställ valda",null).create();
    dlg.setOnShowListener(x->dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{ArrayList<String> sel=new ArrayList<>();for(int i=0;i<chk.length;i++)if(chk[i])sel.add(pkgs.get(i));if(sel.isEmpty()){toast("Välj minst ett paket");return;}dlg.dismiss();applyRestore1102(sel);}));dlg.show();
  }

  void applyRestore1102(ArrayList<String> sel){
    new Thread(()->{
      Set<String> tracked=tracked1102();int ok=0,fail=0;StringBuilder r=new StringBuilder();
      for(String pkg:sel){try{int c=runCode("pm enable --user 0 "+pkg);if(c!=0)c=runCode("pm enable "+pkg);if(c==0){ok++;tracked.remove(pkg);r.append("✅ ").append(nameFor1102(pkg)).append("\n");}else{fail++;r.append("⚠️ ").append(nameFor1102(pkg)).append("\n");}}catch(Exception e){fail++;r.append("⚠️ ").append(nameFor1102(pkg)).append("\n");}}
      saveTracked1102(tracked);log("S23 Debloat 11.2 återställning: "+ok+" återställda, "+fail+" misslyckades");String out="Återställt: "+ok+(fail>0?" • misslyckades: "+fail:"")+"\n\n"+r;runOnUiThread(()->dialog118("Återställ debloat",out));
    }).start();
  }

  Set<String> packageSet1102(String cmd) throws Exception {Set<String>s=new HashSet<>();for(String l:runShellText(cmd).split("\\n"))if(l.startsWith("package:"))s.add(l.substring(8).trim());return s;}
  Set<String> tracked1102(){return new HashSet<>(p.getStringSet("debloat_1102_disabled",Collections.emptySet()));}
  void saveTracked1102(Set<String>s){p.edit().putStringSet("debloat_1102_disabled",new HashSet<>(s)).apply();}
  String nameFor1102(String pkg){for(DebItem d:DEB1102)if(d.pkg.equals(pkg))return d.name;return pkg;}
}
