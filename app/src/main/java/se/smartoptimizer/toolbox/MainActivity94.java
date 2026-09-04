package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;
import java.io.*;
import java.util.*;

public class MainActivity94 extends MainActivity93 {
  @Override void debloat(){
    base("🛡️ SMART DEBLOAT 7.4","Samsung • Google • Meta • Android 16 • Recovery",true);
    note(shStatus());
    note("Android 16-skydd: Toolbox verifierar varje ändring. Om Samsung blockerar shell-ändringen öppnas appens systeminställning som säker återställningsväg.");
    btn("✨ Rekommenderad säker batch",()->recommended());
    String last="";
    for(String[] a:PK) if(installed(a[1])){
      if(!a[3].equals(last)){sec(a[3]);last=a[3];}
      TextView z=text(a[0]+" • "+a[2],18,true);z.setTextColor(a[2].equals("Försiktig")?AMBER:GREEN);root.addView(z);
      boolean en=enabled(a[1]);
      note(a[4]+"\n"+a[1]+"\nStatus: "+(en?"✅ Aktiv":"⛔ Avaktiverad"));
      if(en) btn("Avaktivera "+a[0],()->ask(a));
      else btn("Återställ "+a[0],()->shell("pm enable --user 0 "+a[1],a[1],false));
    }
    sec("Android 16 / Samsung-skydd");
    note("Shizuku via trådlös felsökning kör med shell-UID. En UserService får samma shell-identitet och kan därför inte kringgå en PackageManager SecurityException. Toolbox försöker inte låtsas att root-behörighet finns.");
  }

  @Override void shell(String cmd,String pkg,boolean dis){
    if(!pkg.matches("[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+")){toast("Ogiltigt paketnamn");return;}
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      boolean before=actualEnabled(pkg); String stateBefore=packageState93(pkg); String detail=""; int code=-1;
      try{
        java.lang.Process q=proc(dis?"pm disable-user --user 0 "+pkg:"pm enable --user 0 "+pkg);
        String out=readAll(q.getInputStream()), err=readAll(q.getErrorStream()); code=q.waitFor();
        detail=(err.trim().isEmpty()?out:err).replace('\n',' ').trim();
      }catch(Throwable e){detail=e.getClass().getSimpleName()+": "+String.valueOf(e.getMessage());}
      try{Thread.sleep(300);}catch(Exception ignored){}
      boolean after=actualEnabled(pkg); String stateAfter=packageState93(pkg);
      boolean success=dis?!after:after;
      final boolean ok=success; final int ec=code; final String d=detail; final String sb=stateBefore, sa=stateAfter;
      runOnUiThread(()->{
        if(ok){
          Set<String>s=new HashSet<>(p.getStringSet("disabled",new HashSet<>())); if(dis)s.add(pkg);else s.remove(pkg);p.edit().putStringSet("disabled",s).apply();
          log((dis?"Avaktiverade ":"Återställde ")+pkg+" • verifierad");toast(dis?"Avaktiverad och verifierad":"Återställd och verifierad");debloat();return;
        }
        String shortD=d; int si=shortD.indexOf("SecurityException:"); if(si>=0)shortD=shortD.substring(si); if(shortD.length()>220)shortD=shortD.substring(0,220)+"…";
        boolean blocked=shortD.contains("SecurityException")||ec==255;
        AlertDialog.Builder b=new AlertDialog.Builder(this).setTitle(dis?"Ändringen blockerades":"Android blockerade automatisk återställning")
          .setMessage("Före: "+sb+"\nEfter: "+sa+"\nExit: "+ec+(shortD.isEmpty()?"":"\n\n"+shortD)+"\n\n"+(blocked?"Shizuku är aktiv, men Android/Samsung nekar shell-identiteten denna paketändring. Det kan inte lösas genom att byta till Shizuku UserService eftersom den fortfarande kör som shell när Shizuku startats via ADB/trådlös felsökning.":"Paketets status ändrades inte."))
          .setNegativeButton("Stäng",null);
        if(!dis) b.setPositiveButton("Öppna appinställningar",(x,w)->openAppSettings94(pkg));
        b.show();
      });
    }).start();
  }

  void openAppSettings94(String pkg){
    try{Intent i=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+pkg));startActivity(i);}
    catch(Throwable e){Toast.makeText(this,"Kunde inte öppna appinställningar",Toast.LENGTH_SHORT).show();}
  }
}
