package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.TextView;
import java.util.*;

public class MainActivity95 extends MainActivity94 {
  @Override void show(){
    stopLive();
    base("S23 ULTRA TOOLBOX 9.5","Control Center • Recovery Assistant • Android 16",false);
    dashboard90();
    card("🧯 Recovery Assistant 3.0","Hittar avaktiverade Toolbox-appar och väljer automatisk eller manuell återställning.",()->recoveryAssistant95());
    card("🛡️ Smart Debloat 7.5","Korrekt effektiv paketstatus och smart manuell reservväg.",()->debloat());
    card("🩺 One-tap Health Check","Signering, Shizuku, temperatur, lagring, RAM och batteri.",()->healthCheck());
    card("📡 Live Performance Monitor","Temperatur, RAM, batteri och laddning i realtid.",()->liveMonitor());
    card("🧠 Smart Advisor 6.0","Baslinje, hälsa och prioriterade råd.",()->advisor90());
    card("📦 App Change Watch","Nya, borttagna och uppdaterade appar sedan snapshot.",()->appChangeWatch());
    card("💾 Storage Analyzer","Största installerade APK-filer och lagringsgenvägar.",()->storageAnalyzer());
    card("🔋 Charging Efficiency","Laddning kontra temperaturökning.",()->chargingEfficiency());
    card("⚙️ Performance Profiles 2.0","Standardprofiler och egna profilplatser.",()->profiles90());
    card("🚗 GPS-läge 2.0","Bil/GPS-profil och snabbstart av GPS-appar.",()->gpsMode());
    card("🖥️ DeX Monitor","Status för extern skärm, temperatur och RAM.",()->dexMonitor());
    card("🔐 Toolbox Integrity Check","Version, signering, Shizuku och grundstatus.",()->integrityCheck());
    card("📦 App Manager 2.1","Sök, filtrera och inspektera installerade appar.",()->apps89());
    card("🌡️ Thermal Watch 4.1","Historik och temperaturgraf.",()->thermal89());
    card("🚨 Thermal Alerts","Egna temperaturgränser.",()->thermalAlerts());
    card("🔋 Battery Health","Rapporterade batterivärden.",()->batteryHealth());
    card("⚡ Charging Test Pro 2.0","Namngivna laddtester.",()->battery89());
    card("🧯 Recovery Center 2.1","Ångra tidigare Toolbox-ändringar.",()->recovery());
    card("💾 Backup / Restore","Exportera och importera Toolbox-data.",()->backupPage());
    card("🔧 Shizuku Tools",shStatus(),()->shPage());
    card("📸 Camera Guide 5.2","Praktiska S23 Ultra-råd.",()->camera89());
    card("🕘 Historik","Senaste Toolbox-åtgärder.",()->history());
  }

  @Override void debloat(){
    base("🛡️ SMART DEBLOAT 7.5","Samsung • Google • Meta • Android 16 • Recovery",true);
    note(shStatus());
    note("Toolbox visar nu effektiv status. Om Android/Samsung redan har blockerat automatisk återställning för ett paket används appinställningarna direkt nästa gång.");
    btn("🧯 Recovery Assistant",()->recoveryAssistant95());
    btn("✨ Rekommenderad säker batch",()->recommended());
    String last="";
    for(String[] a:PK) if(installed(a[1])){
      if(!a[3].equals(last)){sec(a[3]);last=a[3];}
      TextView z=text(a[0]+" • "+a[2],18,true); z.setTextColor(a[2].equals("Försiktig")?AMBER:GREEN); root.addView(z);
      boolean en=enabled(a[1]);
      if(en && p.getBoolean("manual_restore_"+a[1],false)) p.edit().remove("manual_restore_"+a[1]).apply();
      note(a[4]+"\n"+a[1]+"\nStatus: "+effectiveState95(a[1]));
      if(en) btn("Avaktivera "+a[0],()->ask(a));
      else if(p.getBoolean("manual_restore_"+a[1],false)) btn("Återställ "+a[0]+" i appinställningar",()->openAppSettings94(a[1]));
      else btn("Återställ "+a[0],()->shell("pm enable --user 0 "+a[1],a[1],false));
    }
    sec("Säkerhetsprincip");
    note("Ingen root emuleras. Varje automatisk ändring verifieras efteråt. Blockerade paket skickas till Androids egen appinställning i stället för upprepade shell-försök.");
  }

  String effectiveState95(String pkg){
    boolean effective=enabled(pkg);
    String setting="okänd";
    try{
      int s=getPackageManager().getApplicationEnabledSetting(pkg);
      if(s==0)setting="default"; else if(s==1)setting="enabled"; else if(s==2)setting="disabled"; else if(s==3)setting="disabled-user"; else if(s==4)setting="disabled-until-used"; else setting="läge "+s;
    }catch(Throwable ignored){}
    return (effective?"✅ Aktiv":"⛔ Avaktiverad")+" • setting="+setting;
  }

  void recoveryAssistant95(){
    base("🧯 RECOVERY ASSISTANT 3.0","Säker återställning för Android 16 / Samsung",true);
    note("Android "+Build.VERSION.RELEASE+" • API "+Build.VERSION.SDK_INT+"\n"+shStatus());
    int n=0;
    for(String[] a:PK) if(installed(a[1])&&!enabled(a[1])){
      n++;
      sec(a[0]);
      boolean manual=p.getBoolean("manual_restore_"+a[1],false);
      note(effectiveState95(a[1])+"\n"+a[1]+(manual?"\nℹ️ Android har tidigare blockerat shell-återställning för detta paket.":""));
      if(manual) btn("Öppna appinställningar",()->openAppSettings94(a[1]));
      else btn("Försök säker återställning",()->shell("pm enable --user 0 "+a[1],a[1],false));
    }
    if(n==0) note("✅ Inga avaktiverade paket i Toolbox-listan behöver återställas.");
    sec("Så fungerar det");
    note("1. Toolbox försöker en verifierad Shizuku-åtgärd.\n2. Om Android nekar med SecurityException sparas paketet som manuell återställning.\n3. Nästa gång öppnas rätt appinställning direkt.\n4. När appen är aktiv igen rensas markeringen automatiskt.");
  }

  @Override void shell(String cmd,String pkg,boolean dis){
    if(!pkg.matches("[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+")){toast("Ogiltigt paketnamn");return;}
    if(!dis && p.getBoolean("manual_restore_"+pkg,false)){openAppSettings94(pkg);return;}
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String before=effectiveState95(pkg), detail=""; int code=-1;
      try{
        java.lang.Process q=proc(dis?"pm disable-user --user 0 "+pkg:"pm enable --user 0 "+pkg);
        String out=readAll(q.getInputStream()),err=readAll(q.getErrorStream()); code=q.waitFor();
        detail=(err.trim().isEmpty()?out:err).replace('\n',' ').trim();
      }catch(Throwable e){detail=e.getClass().getSimpleName()+": "+String.valueOf(e.getMessage());}
      try{Thread.sleep(350);}catch(Exception ignored){}
      boolean afterEnabled=enabled(pkg), success=dis?!afterEnabled:afterEnabled;
      String after=effectiveState95(pkg);
      final boolean ok=success; final int ec=code; final String d=detail,beforeF=before,afterF=after;
      runOnUiThread(()->{
        if(ok){
          Set<String>s=new HashSet<>(p.getStringSet("disabled",new HashSet<>())); if(dis)s.add(pkg); else s.remove(pkg);
          p.edit().putStringSet("disabled",s).remove("manual_restore_"+pkg).apply();
          log((dis?"Avaktiverade ":"Återställde ")+pkg+" • verifierad"); toast(dis?"Avaktiverad och verifierad":"Återställd och verifierad"); debloat(); return;
        }
        boolean blocked=d.contains("SecurityException")||ec==255;
        if(blocked && !dis) p.edit().putBoolean("manual_restore_"+pkg,true).apply();
        log("Paketändring misslyckades: "+pkg+" • exit "+ec+" • "+beforeF+" -> "+afterF);
        AlertDialog.Builder b=new AlertDialog.Builder(this);
        if(blocked && !dis){
          b.setTitle("Android kräver manuell återställning")
           .setMessage("Toolbox kunde inte aktivera appen automatiskt eftersom Android/Samsung blockerade shell-behörigheten.\n\nTryck på Öppna appinställningar och aktivera appen där. Toolbox kommer sedan att känna av att den är aktiv.")
           .setPositiveButton("Öppna appinställningar",(x,w)->openAppSettings94(pkg))
           .setNegativeButton("Stäng",null)
           .setNeutralButton("Detaljer",(x,w)->showDetails95(pkg,ec,beforeF,afterF,d));
        }else{
          b.setTitle("Ändringen kunde inte genomföras")
           .setMessage("Paketets status ändrades inte. Ingen ytterligare automatisk åtgärd görs.")
           .setPositiveButton("OK",null)
           .setNeutralButton("Detaljer",(x,w)->showDetails95(pkg,ec,beforeF,afterF,d));
        }
        b.show();
      });
    }).start();
  }

  void showDetails95(String pkg,int code,String before,String after,String detail){
    String d=detail==null?"":detail; int i=d.indexOf("SecurityException:"); if(i>=0)d=d.substring(i); if(d.length()>700)d=d.substring(0,700)+"…";
    new AlertDialog.Builder(this).setTitle("Tekniska detaljer").setMessage("Paket: "+pkg+"\nFöre: "+before+"\nEfter: "+after+"\nExit: "+code+(d.isEmpty()?"":"\n\n"+d)).setPositiveButton("OK",null).show();
  }
}
