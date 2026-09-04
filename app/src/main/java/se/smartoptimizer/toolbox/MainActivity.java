package se.smartoptimizer.toolbox;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
  LinearLayout root;
  TextView status;
  interface Go { void run(); }

  @Override public void onCreate(Bundle b){ super.onCreate(b); build(); }

  TextView text(String s,int z,boolean bold){
    TextView v=new TextView(this);
    v.setText(s); v.setTextSize(z); v.setTextColor(Color.WHITE); v.setTypeface(null,bold?1:0);
    return v;
  }

  void build(){
    ScrollView sv=new ScrollView(this);
    root=new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(28,28,28,40);
    root.setBackgroundColor(Color.rgb(14,18,24));

    root.addView(text("SMART OPTIMIZER 8.0",26,true));
    TextView sub=text("S23 Ultra Toolbox",14,false);
    sub.setTextColor(Color.rgb(120,255,150)); sub.setPadding(0,4,0,18); root.addView(sub);

    status=text(summary(),15,false);
    status.setPadding(18,16,18,16);
    status.setBackgroundColor(Color.rgb(25,32,42));
    root.addView(status);

    add("⚡ 1‑Tap Optimize","Öppnar Samsung Enhetsvård och gör en säker minnesrensning.",()->{ Runtime.getRuntime().gc(); openCare(); });
    add("🔋 Battery Guardian","Batterisparläge och batterianvändning.",()->open(Settings.ACTION_BATTERY_SAVER_SETTINGS));
    add("🚀 Performance Center","Skärm, 120 Hz och upplösningsinställningar.",()->open(Settings.ACTION_DISPLAY_SETTINGS));
    add("🧹 Deep Cleaner","Lagringsöversikt och säker filrensning.",()->open(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
    add("📦 Debloat Pro","Öppnar applistan för säker avaktivering utan automatisk radering.",()->open(Settings.ACTION_APPLICATION_SETTINGS));
    add("📸 Camera Assistant","Startar kameran snabbt.",()->camera());
    add("🎮 Gaming Mode","Öppnar Samsung Game Launcher/Game Booster när det finns.",()->game());
    add("🖥️ DeX Toolbox","Skärm- och DeX-relaterade inställningar.",()->open(Settings.ACTION_DISPLAY_SETTINGS));
    add("🔊 Audio Center","Ljud, Dolby, vibration och volym.",()->open(Settings.ACTION_SOUND_SETTINGS));
    add("🔐 Privacy Center","Behörigheter och integritetskontroller.",()->open(Settings.ACTION_PRIVACY_SETTINGS));
    add("🌡️ Device Monitor","Uppdaterar batteri, temperatur, RAM och lagring.",()->status.setText(summary()));
    add("🔧 Shizuku Tools",shizuku()?"Shizuku hittad":"Shizuku är inte installerad",()->openPkg("moe.shizuku.privileged.api"));
    add("📖 S23 Tips & Tricks","Kameratips, 120 Hz, QHD+, DeX och mer.",()->tips());

    sv.addView(root);
    setContentView(sv);
  }

  void add(String a,String b,Go g){
    LinearLayout c=new LinearLayout(this);
    c.setOrientation(LinearLayout.VERTICAL);
    c.setPadding(20,16,20,16);
    c.setBackgroundColor(Color.rgb(24,30,40));
    TextView h=text(a,18,true); c.addView(h);
    TextView d=text(b,13,false); d.setTextColor(Color.LTGRAY); d.setPadding(0,5,0,5); c.addView(d);
    c.setOnClickListener(v->g.run());
    LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,14,0,0);
    root.addView(c,lp);
  }

  String summary(){
    Intent i=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    int level=i==null?-1:i.getIntExtra("level",-1), temp=i==null?0:i.getIntExtra("temperature",0);
    ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
    ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo(); am.getMemoryInfo(mi);
    android.os.StatFs fs=new android.os.StatFs(getFilesDir().getPath());
    return "Batteri: "+level+"%  •  Temp: "+(temp/10f)+"°C\nLedigt RAM: "+(mi.availMem/1073741824L)+" GB  •  Ledigt lagringsutrymme: "+(fs.getAvailableBytes()/1073741824L)+" GB";
  }

  void open(String a){ try{ startActivity(new Intent(a)); }catch(Exception e){ startActivity(new Intent(Settings.ACTION_SETTINGS)); } }
  boolean openPkg(String p){ try{ Intent i=getPackageManager().getLaunchIntentForPackage(p); if(i!=null){ startActivity(i); return true; } }catch(Exception e){} return false; }
  void openCare(){ if(!openPkg("com.samsung.android.lool")) if(!openPkg("com.samsung.android.sm")) open(Settings.ACTION_SETTINGS); }
  void game(){ if(!openPkg("com.samsung.android.game.gamehome")) if(!openPkg("com.samsung.android.game.gametools")) open(Settings.ACTION_SETTINGS); }
  void camera(){ try{ startActivity(new Intent("android.media.action.STILL_IMAGE_CAMERA")); }catch(Exception e){ open(Settings.ACTION_APPLICATION_SETTINGS); } }
  boolean shizuku(){ try{ getPackageManager().getPackageInfo("moe.shizuku.privileged.api",0); return true; }catch(Exception e){ return false; } }

  void tips(){
    new AlertDialog.Builder(this)
      .setTitle("S23 Ultra – Tips & Tricks")
      .setMessage("• QHD+ ger maximal skärpa.\n• Adaptiv 120 Hz ger bäst flyt.\n• 200 MP passar bäst i bra ljus och stilla motiv.\n• 3x och 10x ger oftast bättre zoomresultat än mellanlägen.\n• 4K60 är ett bra standardläge för video.\n• DeX + extern skärm gör mobilen till en liten dator.\n• Avaktivera hellre än att radera vid debloat.")
      .setPositiveButton("OK",null)
      .show();
  }
}
