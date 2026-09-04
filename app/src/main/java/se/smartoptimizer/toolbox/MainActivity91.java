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
      String before=packageState(pkg), info=""; boolean ok=false;
      String[] attempts=dis ? new String[]{"pm disable-user --user 0 "+pkg} : restoreAttempts(pkg,before);
      for(String a:attempts){
        Result r=run(a); info += a+"\nexit="+r.code+(r.text.isEmpty()?"":"\n"+r.text)+"\n\n";
        try{Thread.sleep(250);}catch(Exception e){}
        String after=packageState(pkg);
        if(dis ? after.contains("disabled-user")||after.contains("disabled") : after.startsWith("enabled")){ok=true;break;}
      }
      String after=packageState(pkg); final boolean success=ok; final String details="Före: "+before+"\nEfter: "+after+"\n\n"+info;
      runOnUiThread(()->{
        if(success){Set<String>s=new HashSet<>(p.getStringSet("disabled",new HashSet<>()));if(dis)s.add(pkg);else s.remove(pkg);p.edit().putStringSet("disabled",s).putString("last_pkg",pkg).putBoolean("last_disabled",dis).putString("last_change",(dis?"Avaktiverade ":"Återställde ")+pkg).apply();log((dis?"Avaktiverade ":"Återställde ")+pkg+" • verifierad status");Toast.makeText(this,dis?"Avaktiverad och verifierad":"Återställd och verifierad",Toast.LENGTH_SHORT).show();debloat();}
        else {log("Paketåtgärd misslyckades: "+pkg+" • "+details.replace('\n',' '));new android.app.AlertDialog.Builder(this).setTitle("Kunde inte ändra paketet").setMessage(details).setPositiveButton("OK",null).show();}
      });
    }).start();
  }

  String[] restoreAttempts(String pkg,String state){
    ArrayList<String>a=new ArrayList<>();
    if(state.contains("not-installed"))a.add("cmd package install-existing --user 0 "+pkg);
    if(state.contains("suspended"))a.add("pm unsuspend --user 0 "+pkg);
    if(state.contains("hidden"))a.add("pm unhide --user 0 "+pkg);
    a.add("pm default-state --user 0 "+pkg);
    a.add("pm enable --user 0 "+pkg);
    return a.toArray(new String[0]);
  }

  String packageState(String pkg){
    Result all=run("pm list packages -u --user 0 "+pkg); if(!all.text.contains("package:"+pkg))return "not-present";
    Result installed=run("pm list packages --user 0 "+pkg); if(!installed.text.contains("package:"+pkg))return "not-installed";
    Result dis=run("pm list packages -d --user 0 "+pkg); if(dis.text.contains("package:"+pkg))return "disabled-user";
    Result sus=run("pm list packages --user 0 --suspended "+pkg); if(sus.code==0&&sus.text.contains("package:"+pkg))return "suspended";
    Result hid=run("pm list packages --user 0 --hidden "+pkg); if(hid.code==0&&hid.text.contains("package:"+pkg))return "hidden";
    return "enabled";
  }

  Result run(String cmd){try{java.lang.Process q=proc(cmd);String out=readAll(q.getInputStream()),err=readAll(q.getErrorStream());int c=q.waitFor();return new Result(c,(out+(err.isEmpty()?"":"\n"+err)).trim());}catch(Throwable e){return new Result(-1,"exception: "+e);}}
  static class Result{int code;String text;Result(int c,String t){code=c;text=t;}}
  String readAll(InputStream in){try{BufferedReader b=new BufferedReader(new InputStreamReader(in));StringBuilder s=new StringBuilder();String l;while((l=b.readLine())!=null){if(s.length()>0)s.append('\n');s.append(l);}return s.toString();}catch(Exception e){return "[läsfel: "+e+"]";}}
}
