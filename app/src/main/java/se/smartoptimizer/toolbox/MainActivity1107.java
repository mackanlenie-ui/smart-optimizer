package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity1107 extends MainActivity1106 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}

  @Override void show(){
    base("S23 ULTRA TOOLBOX 11.7","11.6 + direktstyrning i Google-läge",false);
    dashboard();
    card("📡 Nätverk & signal","Vimla, LTE/5G, signal, CA och IMS.",()->networkHub122());
    card("🔋 Batteri & optimering","Smart batteristatus, appanalys och säkra tester.",()->batteryHub122());
    card("🛡️ Appar & debloat","Google-läge med direkt av/på, sök/filter och säker selektiv debloat.",()->appsHub122());
    card("🌡️ Prestanda & temperatur","Thermal Watch, laddtest, Smart Advisor och Grafik & Vulkan.",()->performanceHub122());
    card("📱 Telefon & jobb","GPS/Jobbläge, DeX och kamera.",()->phoneHub122());
    card("🧯 Recovery & diagnostik","Återställning, backup, historik och Shizuku.",()->recoveryHub122());
    card("🧰 Fler verktyg","Rapporter, historik, diagnostik och äldre verktyg.",()->legacyHome122());
  }

  @Override void appsHub122(){
    base("🛡️ APPAR & DEBLOAT","11.7 • Google-läge • sök/filter • återställningsbart",true);
    note(shStatus());
    btn("🧹 Google-läge – Samsung-appar",()->googleMode116());
    btn("🔎 Sök / filtrera debloat-paket",()->searchDebloat1104());
    btn("🟢 Välj lågriskpaket",()->chooseDebloat1102("LOW"));
    btn("🟡 Välj valfria paket",()->chooseDebloat1102("OPTIONAL"));
    btn("↩️ Återställ Toolbox-inaktiverade paket",()->restoreDebloat1102());
    btn("🧠 S23 Debloat-granskning 1.0",()->debloatReview1101());
    btn("🛡️ Smart Debloat 8.1",()->debloat());
    btn("📦 App Manager 2.0",()->apps());
  }

  @Override void googleMode116(){
    if(!shOk()){requestSh();return;}
    base("🧹 GOOGLE-LÄGE","Stäng av direkt här • endast valda appar • helt återställningsbart",true);
    note("🔒 Skyddas alltid: Galaxy Store, Samsung Notes, Kamera, Galleri, One UI/System UI, telefoni/IMS, NFC, OMC/operatör, uppdateringar, DeX och andra systemkritiska komponenter.");
    note("📸 Samsung Galleri behålls för kameraintegration. Google Photos kan användas parallellt för backup och visning.");
    note("Google-läge använder pm disable-user --user 0. Inget avinstalleras permanent.");

    Set<String> tracked=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));
    int active=0,off=0;
    for(String[]x:GM)if(installed(x[1])){if(enabled(x[1]))active++;else off++;}
    note("📊 Valbara på telefonen: "+(active+off)+" • 🟢 Aktiva: "+active+" • ⚫ Inaktiverade: "+off);
    btn("🔎 Visa detaljerad analys",()->analyzeGoogleMode116());
    btn("☑️ Välj flera samtidigt",()->chooseGoogleMode116());

    sec("Samsung-appar du kan styra direkt");
    for(String[]x:GM){
      if(!installed(x[1]))continue;
      boolean en=enabled(x[1]);
      sec((en?"🟢 ":"⚫ ")+x[0]);
      note(x[2]+"\n"+x[1]+"\nStatus: "+(en?"Aktiv":"Inaktiverad"));
      if(en)btn("🧹 Stäng av "+x[0],()->confirmSingle117(x));
      else if(tracked.contains(x[1]))btn("↩️ Återställ "+x[0],()->restoreSingle117(x));
      else note("ℹ️ Paketet var redan inaktiverat utanför Google-läget och ändras därför inte automatiskt här.");
    }
    sec("Återställning");
    btn("↩️ Återställ hela Google-läget",()->restoreGoogleMode116());
  }

  void confirmSingle117(String[]x){
    new AlertDialog.Builder(this)
      .setTitle("Stäng av "+x[0]+"?")
      .setMessage(x[2]+"\n\n"+x[1]+"\n\nInget avinstalleras. Toolbox kan återställa paketet härifrån.")
      .setNegativeButton("Avbryt",null)
      .setPositiveButton("Stäng av",(d,w)->disableSingle117(x)).show();
  }

  void disableSingle117(String[]x){
    new Thread(()->{
      int c=-1;String err="";
      try{c=runCode("pm disable-user --user 0 "+x[1]);}catch(Exception e){err=e.getClass().getSimpleName();}
      final int code=c;final String error=err;
      if(code==0){
        Set<String>s=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));s.add(x[1]);p.edit().putStringSet(GM_PREF,new HashSet<>(s)).apply();log("Google-läge: avaktiverade "+x[0]+" ("+x[1]+")");
        runOnUiThread(()->{toast("✅ "+x[0]+" avstängd");googleMode116();});
      }else runOnUiThread(()->dialog118("Google-läge","⚠️ Kunde inte stänga av "+x[0]+(error.isEmpty()?" (kod "+code+")":" ("+error+")")));
    }).start();
  }

  void restoreSingle117(String[]x){
    new Thread(()->{
      int c=-1;String err="";
      try{c=runCode("pm enable --user 0 "+x[1]);if(c!=0)c=runCode("pm enable "+x[1]);}catch(Exception e){err=e.getClass().getSimpleName();}
      final int code=c;final String error=err;
      if(code==0){
        Set<String>s=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));s.remove(x[1]);p.edit().putStringSet(GM_PREF,new HashSet<>(s)).apply();log("Google-läge: återställde "+x[0]+" ("+x[1]+")");
        runOnUiThread(()->{toast("✅ "+x[0]+" återställd");googleMode116();});
      }else runOnUiThread(()->dialog118("Google-läge","⚠️ Kunde inte återställa "+x[0]+(error.isEmpty()?" (kod "+code+")":" ("+error+")")));
    }).start();
  }

  @Override void chooseGoogleMode116(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      ArrayList<String[]> rows=new ArrayList<>();
      for(String[]x:GM)if(installed(x[1])&&enabled(x[1]))rows.add(x);
      runOnUiThread(()->{
        if(rows.isEmpty()){toast("Inga aktiva valbara Samsung-appar hittades");return;}
        String[] labels=new String[rows.size()];boolean[] checked=new boolean[rows.size()];
        for(int i=0;i<rows.size();i++)labels[i]="🟢 "+rows.get(i)[0]+"\n"+rows.get(i)[2]+"\n↳ "+rows.get(i)[1];
        AlertDialog dlg=new AlertDialog.Builder(this)
          .setTitle("☑️ Välj flera Samsung-appar")
          .setMultiChoiceItems(labels,checked,(d,w,c)->checked[w]=c)
          .setNegativeButton("Avbryt",null)
          .setPositiveButton("Fortsätt",null).create();
        dlg.setOnShowListener(v->dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v2->{
          ArrayList<String[]> sel=new ArrayList<>();for(int i=0;i<checked.length;i++)if(checked[i])sel.add(rows.get(i));
          if(sel.isEmpty()){toast("Välj minst en app");return;}
          dlg.dismiss();
          boolean[] selected=new boolean[rows.size()];for(int i=0;i<checked.length;i++)selected[i]=checked[i];
          confirmGoogleMode116(rows,selected);
        }));
        dlg.show();
      });
    }).start();
  }

  @Override void applyGoogleMode116(ArrayList<String[]> sel){
    new Thread(()->{
      Set<String> saved=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));int ok=0,fail=0;
      for(String[]x:sel){try{if(runCode("pm disable-user --user 0 "+x[1])==0){saved.add(x[1]);ok++;}else fail++;}catch(Exception e){fail++;}}
      p.edit().putStringSet(GM_PREF,new HashSet<>(saved)).apply();log("Google-läge: "+ok+" paket avaktiverade");
      final int a=ok,f=fail;runOnUiThread(()->{toast("✅ Avaktiverade: "+a+(f>0?" • misslyckades: "+f:""));googleMode116();});
    }).start();
  }

  @Override void restoreGoogleMode116(){
    Set<String> cur=new HashSet<>(p.getStringSet(GM_PREF,new HashSet<>()));
    if(cur.isEmpty()){toast("Google-läge har inget sparat att återställa");return;}
    new AlertDialog.Builder(this).setTitle("Återställ hela Google-läget?")
      .setMessage("Aktiverar endast paket som Google-läget i Toolbox själv har stängt av.")
      .setNegativeButton("Avbryt",null)
      .setPositiveButton("Återställ",(d,w)->new Thread(()->{
        int ok=0,fail=0;Set<String> left=new HashSet<>();
        for(String pkg:cur){try{int c=runCode("pm enable --user 0 "+pkg);if(c!=0)c=runCode("pm enable "+pkg);if(c==0)ok++;else{fail++;left.add(pkg);}}catch(Exception e){fail++;left.add(pkg);}}
        p.edit().putStringSet(GM_PREF,new HashSet<>(left)).apply();log("Google-läge återställning: "+ok+" paket");
        final int a=ok,f=fail;runOnUiThread(()->{toast("✅ Återställda: "+a+(f>0?" • misslyckades: "+f:""));googleMode116();});
      }).start()).show();
  }
}
