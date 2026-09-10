package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity1108 extends MainActivity1107 {
  static final String[][] CORE118={
    {"Galaxy Store","com.sec.android.app.samsungapps"},
    {"Samsung Notes","com.samsung.android.app.notes"},
    {"Samsung Kamera","com.sec.android.app.camera"},
    {"Samsung Galleri","com.sec.android.gallery3d"},
    {"One UI Home","com.sec.android.app.launcher"},
    {"System UI","com.android.systemui"},
    {"Telefoni","com.android.phone"},
    {"Samsung Telefon","com.samsung.android.dialer"},
    {"Samsung IMS","com.sec.imsservice"},
    {"DeX","com.sec.android.desktopmode"},
    {"NFC","com.android.nfc"},
    {"OMC/operatör","com.samsung.android.app.omcagent"},
    {"Samsung uppdatering","com.wssyncmldm"}
  };

  static final String[][] GOOGLE118={
    {"Chrome","com.android.chrome"},
    {"Gmail","com.google.android.gm"},
    {"Google Kalender","com.google.android.calendar"},
    {"Google Messages","com.google.android.apps.messaging"},
    {"Gboard","com.google.android.inputmethod.latin"},
    {"Google Photos","com.google.android.apps.photos"}
  };

  @Override public void onCreate(Bundle b){super.onCreate(b);}

  @Override void show(){
    base("S23 ULTRA TOOLBOX 11.8","11.7 + tydligare Google-läge, profil och efterkontroll",false);
    dashboard();
    card("📡 Nätverk & signal","Vimla, LTE/5G, signal, CA och IMS.",()->networkHub122());
    card("🔋 Batteri & optimering","Smart batteristatus, appanalys och säkra tester.",()->batteryHub122());
    card("🛡️ Appar & debloat","Google-läge med rekommendationer, profil, efterkontroll och tydlig återställning.",()->appsHub122());
    card("🌡️ Prestanda & temperatur","Thermal Watch, laddtest, Smart Advisor och Grafik & Vulkan.",()->performanceHub122());
    card("📱 Telefon & jobb","GPS/Jobbläge, DeX och kamera.",()->phoneHub122());
    card("🧯 Recovery & diagnostik","Återställning, backup, historik och Shizuku.",()->recoveryHub122());
    card("🧰 Fler verktyg","Rapporter, historik, diagnostik och äldre verktyg.",()->legacyHome122());
  }

  @Override void appsHub122(){
    base("🛡️ APPAR & DEBLOAT","11.8 • Google-läge • profil • efterkontroll • tydlig återställning",true);
    note(shStatus());
    btn("🧹 Google-läge – Samsung-appar",()->googleMode116());
    btn("👤 Min Google-profil",()->myGoogleProfile118());
    btn("✅ Efterkontroll – viktiga Samsung-delar",()->postCheck118());
    btn("↩️ Återställningsöversikt",()->restoreOverview118());
    sec("Övrig debloat");
    btn("🔎 Sök / filtrera debloat-paket",()->searchDebloat1104());
    btn("🟢 Välj lågriskpaket",()->chooseDebloat1102("LOW"));
    btn("🟡 Välj valfria paket",()->chooseDebloat1102("OPTIONAL"));
    btn("🧠 S23 Debloat-granskning 1.0",()->debloatReview1101());
    btn("🛡️ Smart Debloat 8.1",()->debloat());
    btn("📦 App Manager 2.0",()->apps());
  }

  String tag118(String pkg){
    if(pkg.equals("com.samsung.android.video")||pkg.equals("com.samsung.app.newtrim")||pkg.equals("com.samsung.android.scloud"))return "🔒 BEHÅLL";
    if(pkg.equals("com.samsung.android.messaging"))return "🟡 VALFRITT – byt standard-SMS först";
    if(pkg.equals("com.samsung.android.themestore")||pkg.equals("com.samsung.android.app.routines")||pkg.equals("com.samsung.android.spayfw")||pkg.equals("com.hiya.star"))return "🟡 VALFRITT";
    return "✅ REKOMMENDERAS I GOOGLE-LÄGE";
  }

  boolean keep118(String pkg){return tag118(pkg).startsWith("🔒");}

  @Override void googleMode116(){
    if(!shOk()){requestSh();return;}
    base("🧹 GOOGLE-LÄGE","11.8 • rekommendation på varje app • direkt av/på • återställningsbart",true);
    note("🔒 Skyddas alltid: Galaxy Store, Samsung Notes, Kamera, Galleri, One UI/System UI, telefoni/IMS, NFC, OMC/operatör, uppdateringar, DeX och andra systemkritiska komponenter.");
    note("📸 Samsung Galleri behålls för kameraintegration. Google Photos kan användas parallellt.");
    note("✅ = passar målet Google-baserad telefon. 🟡 = valfritt/beroende av hur du använder telefonen. 🔒 = behåll för funktioner vi uttryckligen vill skydda.");
    note(coreSummary118());

    Set<String> tracked=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));
    int active=0,off=0,recOff=0;
    for(String[]x:GM)if(installed(x[1])){if(enabled(x[1]))active++;else{off++;if(tag118(x[1]).startsWith("✅"))recOff++;}}
    note("📊 Valbara på telefonen: "+(active+off)+" • 🟢 Aktiva: "+active+" • ⚫ Inaktiverade: "+off+"\n✅ Rekommenderade redan avstängda: "+recOff);
    btn("👤 Visa min Google-profil",()->myGoogleProfile118());
    btn("✅ Kör detaljerad efterkontroll",()->postCheck118());
    btn("☑️ Välj flera rekommenderade/valfria",()->chooseGoogleMode116());

    sec("Samsung-appar");
    for(String[]x:GM){
      if(!installed(x[1]))continue;
      boolean en=enabled(x[1]);
      String tag=tag118(x[1]);
      sec((en?"🟢 ":"⚫ ")+x[0]+" • "+tag);
      note(x[2]+"\n"+x[1]+"\nStatus: "+(en?"Aktiv":"Inaktiverad"));
      if(en&&!keep118(x[1]))btn("🧹 Stäng av "+x[0],()->confirmSingle117(x));
      else if(en)note("🔒 Toolbox rekommenderar att den här lämnas aktiv i din nuvarande profil.");
      else if(tracked.contains(x[1]))btn("↩️ Återställ "+x[0],()->restoreSingle117(x));
      else note("ℹ️ Paketet var redan inaktiverat utanför Google-läget och ändras därför inte automatiskt här.");
    }
    sec("Återställning");
    btn("↩️ Återställ hela Google-läget",()->restoreGoogleMode116());
    btn("↩️ Visa all återställning",()->restoreOverview118());
  }

  @Override void chooseGoogleMode116(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      ArrayList<String[]> rows=new ArrayList<>();
      for(String[]x:GM)if(installed(x[1])&&enabled(x[1])&&!keep118(x[1]))rows.add(x);
      runOnUiThread(()->{
        if(rows.isEmpty()){toast("Inga aktiva rekommenderade/valfria appar hittades");return;}
        String[] labels=new String[rows.size()];boolean[] checked=new boolean[rows.size()];
        for(int i=0;i<rows.size();i++)labels[i]=tag118(rows.get(i)[1])+"\n"+rows.get(i)[0]+"\n"+rows.get(i)[2]+"\n↳ "+rows.get(i)[1];
        AlertDialog dlg=new AlertDialog.Builder(this).setTitle("☑️ Välj flera Samsung-appar")
          .setMultiChoiceItems(labels,checked,(d,w,c)->checked[w]=c)
          .setNegativeButton("Avbryt",null).setPositiveButton("Fortsätt",null).create();
        dlg.setOnShowListener(v->dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v2->{
          ArrayList<String[]> sel=new ArrayList<>();for(int i=0;i<checked.length;i++)if(checked[i])sel.add(rows.get(i));
          if(sel.isEmpty()){toast("Välj minst en app");return;}dlg.dismiss();
          boolean[] selected=new boolean[rows.size()];for(int i=0;i<checked.length;i++)selected[i]=checked[i];
          confirmGoogleMode116(rows,selected);
        }));dlg.show();
      });
    }).start();
  }

  String coreSummary118(){
    int seen=0,ok=0,bad=0;
    for(String[]x:CORE118)if(installed(x[1])){seen++;if(enabled(x[1]))ok++;else bad++;}
    if(seen==0)return "⚪ Skyddskontroll: inga kända kärnpaket kunde matchas.";
    return bad==0?"✅ Skyddskontroll: "+ok+"/"+seen+" kontrollerade kärnpaket aktiva.":"⚠️ Skyddskontroll: "+bad+" av "+seen+" kontrollerade kärnpaket är inaktiverade.";
  }

  void postCheck118(){
    base("✅ EFTERKONTROLL","Read-only • verifierar paketstatus efter debloat",true);
    int seen=0,ok=0,bad=0;
    for(String[]x:CORE118){
      if(!installed(x[1]))continue;seen++;
      boolean en=enabled(x[1]);if(en)ok++;else bad++;
      note((en?"✅ ":"⚠️ ")+x[0]+"\n"+x[1]+"\nStatus: "+(en?"Aktiv":"INAKTIVERAD"));
    }
    sec("Resultat");
    if(seen==0)note("⚪ Inga kända kärnpaket kunde matchas på denna firmware.");
    else if(bad==0)note("✅ "+ok+"/"+seen+" kontrollerade viktiga paket är aktiva. Google-läget ser säkert ut på paketnivå.");
    else note("⚠️ "+bad+" av "+seen+" viktiga paket är inaktiverade. Återställ dem innan du gör mer debloat.");
    note("ℹ️ Detta verifierar paketstatus – inte att IMS är registrerat, att ett samtal är uppkopplat eller att varje DeX/kamerafunktion har funktionstestats.");
    btn("🧹 Till Google-läget",()->googleMode116());
    btn("↩️ Återställningsöversikt",()->restoreOverview118());
  }

  void myGoogleProfile118(){
    base("👤 MIN GOOGLE-PROFIL","Din nuvarande blandning av Google + skyddade Samsung-delar",true);
    int installedGm=0,disabledGm=0,recTotal=0,recDisabled=0,optional=0,kept=0;
    for(String[]x:GM)if(installed(x[1])){
      installedGm++;String t=tag118(x[1]);boolean en=enabled(x[1]);if(!en)disabledGm++;
      if(t.startsWith("✅")){recTotal++;if(!en)recDisabled++;}
      else if(t.startsWith("🟡"))optional++;else if(t.startsWith("🔒")&&en)kept++;
    }
    Set<String> gm=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));Set<String> normal=tracked1102();
    note("🧹 Samsung-paket i Google-läget: "+installedGm+"\n⚫ Inaktiverade: "+disabledGm+"\n✅ Rekommenderade avstängda: "+recDisabled+"/"+recTotal+"\n🟡 Valfria installerade: "+optional+"\n🔒 Rekommenderade behållna och aktiva: "+kept);
    note("Toolbox-spårning:\n• Google-läge: "+gm.size()+" paket\n• Vanlig selektiv debloat: "+normal.size()+" paket");
    sec("Google-appar");
    for(String[]x:GOOGLE118){boolean ins=installed(x[1]);boolean en=ins&&enabled(x[1]);note((en?"✅ ":ins?"⚫ ":"➖ ")+x[0]+" • "+(en?"aktiv":ins?"installerad men inaktiverad":"inte installerad"));}
    sec("Skyddade Samsung-delar");note(coreSummary118());
    btn("✅ Detaljerad efterkontroll",()->postCheck118());
    btn("🧹 Google-läge",()->googleMode116());
    btn("↩️ Återställningsöversikt",()->restoreOverview118());
  }

  String gmName118(String pkg){for(String[]x:GM)if(x[1].equals(pkg))return x[0];return pkg;}

  void restoreOverview118(){
    base("↩️ ÅTERSTÄLLNINGSÖVERSIKT","Google-läge och vanlig debloat hålls isär",true);
    Set<String> gm=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));Set<String> normal=tracked1102();
    sec("🧹 Google-läge");
    note("Toolbox spårar "+gm.size()+" paket som Google-läget själv har stängt av.");
    if(gm.isEmpty())note("✅ Inget Google-läge-paket väntar på återställning.");else{StringBuilder b=new StringBuilder();for(String pkg:gm)b.append("• ").append(gmName118(pkg)).append("\n");note(b.toString().trim());btn("↩️ Återställ hela Google-läget",()->restoreGoogleMode116());}
    sec("🛡️ Vanlig selektiv debloat");
    note("Toolbox spårar "+normal.size()+" paket från lågrisk/valfri debloat.");
    if(normal.isEmpty())note("✅ Inga vanliga debloat-paket väntar på återställning.");else{StringBuilder b=new StringBuilder();for(String pkg:normal)b.append("• ").append(nameFor1102(pkg)).append("\n");note(b.toString().trim());btn("↩️ Välj vanliga paket att återställa",()->restoreDebloat1102());}
    sec("Kontroll");note(coreSummary118());btn("✅ Kör efterkontroll",()->postCheck118());
  }
}
