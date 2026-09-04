package se.smartoptimizer.toolbox;

import android.content.pm.PackageManager;
import android.widget.Toast;
import java.io.*;
import java.util.*;

public class MainActivity91 extends MainActivity90 {
  @Override void show(){super.show();}

  @Override void shell(String cmd,String pkg,boolean dis){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      StringBuilder details=new StringBuilder();
      boolean ok=false;
      int code=-1;
      String[] attempts;
      if(dis){
        attempts=new String[]{"pm disable-user --user 0 "+pkg,"cmd package set-enabled-setting "+pkg+" disabled-user 0"};
      }else{
        attempts=new String[]{"pm enable --user 0 "+pkg,"pm enable "+pkg,"cmd package set-enabled-setting "+pkg+" enabled 0"};
      }
      for(String attempt:attempts){
        try{
          java.lang.Process q=proc(attempt);
          String out=readAll(q.getInputStream());
          String err=readAll(q.getErrorStream());
          code=q.waitFor();
          details.append(attempt).append("\nexit=").append(code);
          if(!out.trim().isEmpty())details.append("\nout: ").append(out.trim());
          if(!err.trim().isEmpty())details.append("\nerr: ").append(err.trim());
          details.append("\n\n");
          Thread.sleep(250);
          boolean state=actualEnabled(pkg);
          if((dis&&!state)||(!dis&&state)){ok=true;break;}
        }catch(Throwable e){details.append(attempt).append("\nexception: ").append(e).append("\n\n");}
      }
      final boolean success=ok; final int exit=code; final String info=details.toString();
      runOnUiThread(()->{
        if(success){
          Set<String>s=new HashSet<>(p.getStringSet("disabled",new HashSet<>()));
          if(dis)s.add(pkg);else s.remove(pkg);
          p.edit().putStringSet("disabled",s).putString("last_pkg",pkg).putBoolean("last_disabled",dis).putString("last_change",(dis?"Avaktiverade ":"Återställde ")+pkg).apply();
          log((dis?"Avaktiverade ":"Återställde ")+pkg+" • verifierad status");
          Toast.makeText(this,dis?"Avaktiverad och verifierad":"Återställd och verifierad",Toast.LENGTH_SHORT).show();
          debloat();
        }else{
          log("Paketkommando misslyckades för "+pkg+" • exit "+exit+" • "+info.replace('\n',' '));
          new android.app.AlertDialog.Builder(this).setTitle("Kunde inte ändra paketet").setMessage("Toolbox provade flera säkra metoder och kontrollerade status efteråt.\n\n"+info).setPositiveButton("OK",null).show();
        }
      });
    }).start();
  }

  boolean actualEnabled(String pkg){
    try{
      int s=getPackageManager().getApplicationEnabledSetting(pkg);
      if(s==PackageManager.COMPONENT_ENABLED_STATE_DISABLED||s==PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER||s==PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED)return false;
      if(s==PackageManager.COMPONENT_ENABLED_STATE_ENABLED)return true;
      return getPackageManager().getApplicationInfo(pkg,0).enabled;
    }catch(Throwable e){return false;}
  }

  String readAll(InputStream in){
    try{BufferedReader b=new BufferedReader(new InputStreamReader(in));StringBuilder s=new StringBuilder();String l;while((l=b.readLine())!=null){if(s.length()>0)s.append('\n');s.append(l);}return s.toString();}catch(Exception e){return "[kunde inte läsa utdata: "+e+"]";}
  }
}
