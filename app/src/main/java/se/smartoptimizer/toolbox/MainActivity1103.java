package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;
import java.util.*;

public class MainActivity1103 extends MainActivity1102 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}

  @Override void show(){
    base("S23 ULTRA TOOLBOX 11.3","Tydligare S23 Debloat • funktion • påverkan • återställning",false);
    dashboard();
    card("📡 Nätverk & signal","Vimla, LTE/5G, signal, CA och IMS.",()->networkHub122());
    card("🔋 Batteri & optimering","Smart batteristatus, appanalys och säkra tester.",()->batteryHub122());
    card("🛡️ Appar & debloat","Tydliga funktionsnamn, påverkan och valbar säker debloat.",()->appsHub122());
    card("🌡️ Prestanda & temperatur","Thermal Watch, laddtest och Smart Advisor.",()->performanceHub122());
    card("📱 Telefon & jobb","GPS/Jobbläge, DeX och kamera.",()->phoneHub122());
    card("🧯 Recovery & diagnostik","Återställning, backup, historik och Shizuku.",()->recoveryHub122());
    card("🧰 Fler verktyg","Rapporter, historik, diagnostik och äldre verktyg.",()->legacyHome122());
  }

  @Override void appsHub122(){
    base("🛡️ APPAR & DEBLOAT","11.3 • tydligare val • påverkan visas före ändring",true);
    note(shStatus());
    note("🟢 Lågrisk = fristående extrafunktioner. 🟡 Valfritt = kan påverka en funktion du använder. Telefon/IMS, Wi‑Fi, NFC, kamera, setup och säkerhet är skyddade.");
    btn("🟢 Välj lågriskpaket",()->chooseDebloat1102("LOW"));
    btn("🟡 Välj valfria paket",()->chooseDebloat1102("OPTIONAL"));
    btn("↩️ Återställ Toolbox-inaktiverade paket",()->restoreDebloat1102());
    btn("🧠 S23 Debloat-granskning 1.0",()->debloatReview1101());
    btn("🛡️ Smart Debloat 8.0",()->debloat());
    btn("📦 App Manager 2.0",()->apps());
  }

  @Override void chooseDebloat1102(String level){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      try{
        Set<String> installed=packageSet1102("pm list packages");
        Set<String> enabled=packageSet1102("pm list packages -e");
        ArrayList<DebItem> items=new ArrayList<>();
        for(DebItem d:DEB1102){
          String actualLevel=d.level;
          if(d.pkg.equals("com.samsung.storyservice")) actualLevel="OPTIONAL";
          if(actualLevel.equals(level)&&installed.contains(d.pkg)&&enabled.contains(d.pkg)) items.add(d);
        }
        runOnUiThread(()->showChoice1103(level,items));
      }catch(Exception e){runOnUiThread(()->dialog118("Debloat","Kunde inte läsa paketstatus: "+e.getClass().getSimpleName()));}
    }).start();
  }

  void showChoice1103(String level,ArrayList<DebItem> items){
    if(items.isEmpty()){dialog118("Debloat","Inga aktiva installerade paket i den här kategorin hittades.");return;}
    String[] rows=new String[items.size()]; boolean[] checked=new boolean[items.size()];
    for(int i=0;i<items.size();i++) rows[i]=prettyRow1103(items.get(i));
    String title="LOW".equals(level)?"🟢 Välj lågriskpaket":"🟡 Välj valfria paket";
    AlertDialog dlg=new AlertDialog.Builder(this).setTitle(title)
      .setMultiChoiceItems(rows,checked,(d,w,is)->checked[w]=is)
      .setNegativeButton("Avbryt",null).setPositiveButton("Inaktivera valda",null).create();
    dlg.setOnShowListener(x->dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
      ArrayList<DebItem> sel=new ArrayList<>();for(int i=0;i<checked.length;i++)if(checked[i])sel.add(items.get(i));
      if(sel.isEmpty()){toast("Välj minst ett paket");return;} dlg.dismiss(); confirmDisable1102(sel);
    }));dlg.show();
  }

  String prettyRow1103(DebItem d){
    return d.name+"\n"+d.desc+"\n"+impact1103(d.pkg)+"\n↳ "+d.pkg+"\n";
  }

  String impact1103(String p){
    if(p.equals("com.android.egg")) return "✅ Ingen påverkan på telefoni eller normal användning";
    if(p.equals("com.android.dreams.phototable")) return "✅ Påverkar bara äldre skärmsläckarfunktion";
    if(p.equals("com.android.traceur")) return "✅ Påverkar bara utvecklarverktyget System Tracing";
    if(p.contains("aremoji")||p.contains("facearavatar")||p.contains("sticker")) return "🎭 Påverkar AR Emoji/stickers – inte vanlig kamera";
    if(p.equals("com.samsung.storyservice")) return "📸 Kan påverka automatiska minnen/berättelser i Galleri";
    if(p.startsWith("com.facebook.")) return "📦 Kan påverka Metas förinstallerade bakgrundskomponenter";
    if(p.contains("gametools")) return "🎮 Kan påverka Game Booster/spelverktyg";
    if(p.contains("themestore")) return "🎨 Kan påverka Galaxy Themes";
    if(p.contains("watchmanager")) return "⌚ Kan påverka Galaxy Wearable/Watch";
    if(p.contains("skydrive")) return "☁️ Kan påverka OneDrive och Galleri-synk";
    if(p.contains("appmanager")) return "💻 Kan påverka Länk till Windows";
    if(p.contains("hiya")) return "📞 Kan påverka spam-/nummeridentifiering";
    if(p.contains("routines")) return "⚙️ Kan påverka Modes & Routines";
    if(p.contains("spayfw")) return "💳 Kan påverka Samsung Wallet/Pay";
    if(p.contains("securefolder")) return "🔐 Kan påverka Säker mapp";
    if(p.contains("scloud")) return "☁️ Kan påverka Samsung Cloud/synk";
    if(p.contains("swiftkey")) return "⌨️ Påverkar SwiftKey-tangentbordet";
    return "ℹ️ Påverkar endast den angivna appen/funktionen – välj om du inte använder den";
  }
}
