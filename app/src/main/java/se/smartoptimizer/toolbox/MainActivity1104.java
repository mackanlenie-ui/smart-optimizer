package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import java.util.*;

public class MainActivity1104 extends MainActivity1103 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}

  @Override void show(){
    base("S23 ULTRA TOOLBOX 11.4","Debloat-sökning • status Aktiv/Inaktiverad • återställningsbart",false);
    dashboard();
    card("📡 Nätverk & signal","Vimla, LTE/5G, signal, CA och IMS.",()->networkHub122());
    card("🔋 Batteri & optimering","Smart batteristatus, appanalys och säkra tester.",()->batteryHub122());
    card("🛡️ Appar & debloat","Sök/filter, tydlig paketstatus och säker selektiv debloat.",()->appsHub122());
    card("🌡️ Prestanda & temperatur","Thermal Watch, laddtest och Smart Advisor.",()->performanceHub122());
    card("📱 Telefon & jobb","GPS/Jobbläge, DeX och kamera.",()->phoneHub122());
    card("🧯 Recovery & diagnostik","Återställning, backup, historik och Shizuku.",()->recoveryHub122());
    card("🧰 Fler verktyg","Rapporter, historik, diagnostik och äldre verktyg.",()->legacyHome122());
  }

  @Override void appsHub122(){
    base("🛡️ APPAR & DEBLOAT","11.4 • sök/filter • Aktiv/Inaktiverad • säkra val",true);
    note(shStatus());
    note("🟢 Aktiv = paketet körs/är tillgängligt. ⚫ Inaktiverad = avstängt för användare 0. Sökningen ändrar ingenting.");
    btn("🔎 Sök / filtrera debloat-paket",()->searchDebloat1104());
    btn("🟢 Välj lågriskpaket",()->chooseDebloat1102("LOW"));
    btn("🟡 Välj valfria paket",()->chooseDebloat1102("OPTIONAL"));
    btn("↩️ Återställ Toolbox-inaktiverade paket",()->restoreDebloat1102());
    btn("🧠 S23 Debloat-granskning 1.0",()->debloatReview1101());
    btn("🛡️ Smart Debloat 8.0",()->debloat());
    btn("📦 App Manager 2.0",()->apps());
  }

  void searchDebloat1104(){
    if(!shOk()){requestSh();return;}
    EditText e=new EditText(this);e.setHint("T.ex. Bixby, AR, Samsung Cloud, com.google...");e.setSingleLine(true);e.setInputType(InputType.TYPE_CLASS_TEXT);
    new AlertDialog.Builder(this).setTitle("🔎 Sök debloat-paket").setMessage("Sök på namn, funktion eller paketnamn. Lämna tomt för att visa alla kända paket.").setView(e).setNegativeButton("Avbryt",null).setPositiveButton("Sök",(d,w)->runSearch1104(e.getText().toString())).show();
  }

  void runSearch1104(String query){
    new Thread(()->{
      try{
        Set<String> installed=packageSet1102("pm list packages");
        Set<String> enabled=packageSet1102("pm list packages -e");
        String q=query==null?"":query.trim().toLowerCase(Locale.ROOT);
        ArrayList<String> rows=new ArrayList<>();
        for(DebItem x:DEB1102){
          if(!installed.contains(x.pkg)) continue;
          String hay=(x.name+" "+x.desc+" "+x.pkg+" "+impact1103(x.pkg)).toLowerCase(Locale.ROOT);
          if(!q.isEmpty()&&!hay.contains(q)) continue;
          String level=x.pkg.equals("com.samsung.storyservice")?"OPTIONAL":x.level;
          String status=enabled.contains(x.pkg)?"🟢 Aktiv":"⚫ Inaktiverad";
          String cat="LOW".equals(level)?"🟢 Lågrisk":"🟡 Valfritt";
          rows.add(status+" • "+cat+"\n"+x.name+"\n"+x.desc+"\n"+impact1103(x.pkg)+"\n↳ "+x.pkg);
        }
        runOnUiThread(()->showSearchResults1104(query,rows));
      }catch(Exception ex){runOnUiThread(()->dialog118("Debloat-sökning","Kunde inte läsa paketstatus: "+ex.getClass().getSimpleName()));}
    }).start();
  }

  void showSearchResults1104(String q,ArrayList<String> rows){
    if(rows.isEmpty()){dialog118("Debloat-sökning","Inga installerade träffar för: "+(q==null||q.trim().isEmpty()?"alla paket":q));return;}
    String[] a=rows.toArray(new String[0]);
    new AlertDialog.Builder(this).setTitle("🔎 Träffar: "+rows.size()).setItems(a,(d,w)->{}).setPositiveButton("OK",null).show();
  }

  @Override void showChoice1103(String level,ArrayList<DebItem> items){
    if(items.isEmpty()){dialog118("Debloat","Inga aktiva installerade paket i den här kategorin hittades.");return;}
    String[] rows=new String[items.size()]; boolean[] checked=new boolean[items.size()];
    for(int i=0;i<items.size();i++) rows[i]="🟢 Aktiv\n"+prettyRow1103(items.get(i));
    String title="LOW".equals(level)?"🟢 Välj lågriskpaket":"🟡 Välj valfria paket";
    AlertDialog dlg=new AlertDialog.Builder(this).setTitle(title)
      .setMultiChoiceItems(rows,checked,(d,w,is)->checked[w]=is)
      .setNegativeButton("Avbryt",null).setPositiveButton("Inaktivera valda",null).create();
    dlg.setOnShowListener(x->dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
      ArrayList<DebItem> sel=new ArrayList<>();for(int i=0;i<checked.length;i++)if(checked[i])sel.add(items.get(i));
      if(sel.isEmpty()){toast("Välj minst ett paket");return;}dlg.dismiss();confirmDisable1102(sel);
    }));dlg.show();
  }
}
