package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

public class MainActivity112 extends MainActivity111 {
  @Override void show(){
    super.show();
    try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.12");}catch(Throwable ignored){}
    card("🎮 Grafik & Vulkan","HWUI, Adreno/Vulkan, ANGLE och säkert A/B-test.",()->graphicsPage());
  }

  void graphicsPage(){
    base("🎮 GRAFIK & VULKAN","Diagnostik • Shizuku • återställningsbart test",true);
    note("Android "+Build.VERSION.RELEASE+" • SDK "+Build.VERSION.SDK_INT+"\n"+shStatus());
    sec("Aktuell grafikstatus");
    note("Tryck Läs grafikstatus för att läsa HWUI-renderare, EGL/Vulkan-drivrutin och ANGLE-inställningar via Android-shell.");
    btn("🔍 Läs grafikstatus",()->readGraphicsStatus());
    sec("Vulkan-test");
    note("Detta tvingar endast Androids HWUI-renderare till Skia Vulkan (skiavk). Det är ett testläge – inte en garanterad batterioptimering. En omstart krävs för ett rättvist test.");
    btn("🧪 Förbered Vulkan-test (skiavk)",()->confirmRenderer("skiavk"));
    btn("↩️ Återställ Android-standard",()->confirmRenderer(""));
    sec("A/B temperatur & batteri");
    note("Kör samma användning under samma tid före och efter Vulkan-ändringen. Toolbox sparar startvärden så du kan jämföra temperatur och batterinivå.");
    btn("▶️ Starta A/B-referens",()->startGraphicsBaseline());
    btn("⏹️ Visa resultat sedan start",()->finishGraphicsBaseline());
  }

  void readGraphicsStatus(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String cmd="echo HWUI=$(getprop debug.hwui.renderer); echo EGL=$(getprop ro.hardware.egl); echo VULKAN=$(getprop ro.hardware.vulkan); echo ANGLE_ALL=$(settings get global angle_gl_driver_all_angle 2>/dev/null); echo ANGLE_PKGS=$(settings get global angle_gl_driver_selection_pkgs 2>/dev/null); echo ANGLE_VALUES=$(settings get global angle_gl_driver_selection_values 2>/dev/null)";
        out=runShellText(cmd);
        if(out.contains("HWUI=\n")||out.startsWith("HWUI=\r\n"))out=out.replace("HWUI=\n","HWUI=Android-standard\n").replace("HWUI=\r\n","HWUI=Android-standard\r\n");
      }catch(Exception e){out="Kunde inte läsa grafikstatus: "+e.getClass().getSimpleName();}
      final String x=out;
      runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Grafikstatus").setMessage(x).setPositiveButton("OK",null).show());
    }).start();
  }

  String runShellText(String cmd)throws Exception{
    java.lang.Process q=proc(cmd);
    BufferedReader br=new BufferedReader(new InputStreamReader(q.getInputStream()));
    StringBuilder b=new StringBuilder();String l;
    while((l=br.readLine())!=null)b.append(l).append('\n');
    q.waitFor();return b.toString().trim();
  }

  void confirmRenderer(String value){
    if(!shOk()){requestSh();return;}
    String title=value.isEmpty()?"Återställ grafikstandard?":"Testa Skia Vulkan?";
    String msg=value.isEmpty()?"Tar bort Toolbox renderer-override och återgår till Android/Samsungs standard efter omstart.":"Toolbox sparar nuvarande renderer och sätter debug.hwui.renderer=skiavk. Starta om telefonen efteråt. Om något ser fel ut kan du återställa här.";
    new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setNegativeButton("Avbryt",null).setPositiveButton(value.isEmpty()?"Återställ":"Aktivera test",(d,w)->setRenderer(value)).show();
  }

  void setRenderer(String value){
    new Thread(()->{
      String result;
      try{
        String old=runShellText("getprop debug.hwui.renderer");
        if(!p.contains("graphics_renderer_before"))p.edit().putString("graphics_renderer_before",old).apply();
        String cmd=value.isEmpty()?"setprop debug.hwui.renderer ''":"setprop debug.hwui.renderer "+value;
        java.lang.Process q=proc(cmd);int code=q.waitFor();
        String now=runShellText("getprop debug.hwui.renderer");
        if(code==0){log(value.isEmpty()?"Grafikrenderer återställd till Android-standard":"Grafikrenderer satt till skiavk för test");result=(value.isEmpty()?"Återställd":"Vulkan-test förberett")+".\nNuvarande värde: "+(now.isEmpty()?"Android-standard":now)+"\n\nStarta om telefonen innan du jämför.";}
        else result="Ändringen nekades av Android (kod "+code+"). Ingen ändring antas vara gjord.";
      }catch(Exception e){result="Kunde inte ändra renderer: "+e.getClass().getSimpleName();}
      final String x=result;runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Grafik & Vulkan").setMessage(x).setPositiveButton("OK",null).show());
    }).start();
  }

  void startGraphicsBaseline(){
    p.edit().putLong("gfx_start_time",System.currentTimeMillis()).putInt("gfx_start_battery",level()).putFloat("gfx_start_temp",temp()).apply();
    toast("A/B-referens startad");
    log("Grafik A/B start: "+level()+"% • "+fmt(temp())+"°C");
  }

  void finishGraphicsBaseline(){
    long t=p.getLong("gfx_start_time",0);
    if(t==0){toast("Starta en A/B-referens först");return;}
    int b0=p.getInt("gfx_start_battery",level()),b1=level();float t0=p.getFloat("gfx_start_temp",temp()),t1=temp();
    long min=Math.max(0,(System.currentTimeMillis()-t)/60000);
    String r=String.format(Locale.ROOT,"Tid: %d min\nBatteri: %d%% → %d%% (%d procentenheter)\nTemperatur: %.1f°C → %.1f°C (%+.1f°C)\n\nJämför med ett lika långt test med samma appar, ljusstyrka och belastning.",min,b0,b1,b1-b0,t0,t1,t1-t0);
    new AlertDialog.Builder(this).setTitle("A/B-resultat").setMessage(r).setPositiveButton("OK",null).show();
  }
}
