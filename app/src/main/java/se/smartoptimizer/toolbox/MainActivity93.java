package se.smartoptimizer.toolbox;

import android.content.pm.PackageManager;
import android.widget.Toast;
import java.io.*;
import java.util.*;

public class MainActivity93 extends MainActivity91 {
  @Override void show(){super.show();}

  @Override void shell(String cmd,String pkg,boolean dis){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String before=packageState93(pkg);
      ArrayList<String> attempts=new ArrayList<>();
      if(dis){ attempts.add("pm disable-user --user 0 "+pkg); }
      else {
        if(before.contains("uninstalled")) attempts.add("cmd package install-existing --user 0 "+pkg);
        if(before.contains("suspended")) attempts.add("pm unsuspend "+pkg);
        if(before.contains("hidden")) attempts.add("pm unhide "+pkg);
        attempts.add("pm enable --user 0 "+pkg);
      }
      StringBuilder details=new StringBuilder(); boolean ok=false; int code=-1;
      for(String attempt:attempts){
        try{
          java.lang.Process q=proc(attempt); String out=readAll(q.getInputStream()); String err=readAll(q.getErrorStream()); code=q.waitFor();
          details.append(shortCmd(attempt)).append(" → ").append(code==0?"OK":"fel "+code);
          if(!err.trim().isEmpty()) details.append(" • ").append(shortError(err));
          details.append('\n'); Thread.sleep(250);
          String now=packageState93(pkg); boolean en=actualEnabled(pkg);
          if((dis&&!en)||(!dis&&en)){ok=true;break;}
        }catch(Throwable e){details.append(shortCmd(attempt)).append(" → ").append(e.getClass().getSimpleName()).append('\n');}
      }
      final boolean success=ok; final String after=packageState93(pkg); final String info=details.toString();
      runOnUiThread(()->{
        if(success){
          Set<String>s=new HashSet<>(p.getStringSet("disabled",new HashSet<>())); if(dis)s.add(pkg);else s.remove(pkg);
          p.edit().putStringSet("disabled",s).apply(); log((dis?"Avaktiverade ":"Återställde ")+pkg+" • verifierad");
          Toast.makeText(this,dis?"Avaktiverad och verifierad":"Återställd och verifierad",Toast.LENGTH_SHORT).show(); debloat();
        }else{
          log("Paketändring nekad: "+pkg+" • "+before+" -> "+after);
          String msg="Före: "+before+"\nEfter: "+after+"\n\n"+info+"\nAndroid nekade ändringen med shell-identiteten. Toolbox gör inga fler osäkra försök. Om paketet fortfarande är disabled-user kan det återställas via Inställningar > Appar, eller via ADB/root om Samsung kräver högre behörighet.";
          new android.app.AlertDialog.Builder(this).setTitle("Återställning blockerad av Android").setMessage(msg).setPositiveButton("OK",null).show();
        }
      });
    }).start();
  }

  String packageState93(String pkg){
    try{
      StringBuilder s=new StringBuilder();
      java.lang.Process q=proc("dumpsys package "+pkg+" | grep -E 'enabled=|suspended=|hidden=|installed=' | head -12");
      String out=readAll(q.getInputStream()); q.waitFor();
      int st=getPackageManager().getApplicationEnabledSetting(pkg);
      if(st==PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER)s.append("disabled-user");
      else if(st==PackageManager.COMPONENT_ENABLED_STATE_DISABLED)s.append("disabled");
      else if(st==PackageManager.COMPONENT_ENABLED_STATE_ENABLED)s.append("enabled");
      else s.append("default");
      String l=out.toLowerCase(Locale.ROOT); if(l.contains("installed=false"))s.append(" • uninstalled"); if(l.contains("suspended=true"))s.append(" • suspended"); if(l.contains("hidden=true"))s.append(" • hidden");
      return s.toString();
    }catch(Throwable e){return "okänd";}
  }
  String shortCmd(String x){return x.length()>72?x.substring(0,72)+"…":x;}
  String shortError(String x){x=x.replace('\n',' ').trim();int i=x.indexOf("SecurityException:");if(i>=0)x=x.substring(i);return x.length()>180?x.substring(0,180)+"…":x;}
}
