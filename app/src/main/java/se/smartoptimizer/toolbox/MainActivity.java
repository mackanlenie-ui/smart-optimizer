package se.smartoptimizer.toolbox;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.io.File;
import java.util.*;

public class MainActivity extends Activity {
  LinearLayout root;
  TextView status;
  boolean onHome = true;
  interface Go { void run(); }

  @Override public void onCreate(Bundle b){ super.onCreate(b); showHome(); }
  @Override public void onBackPressed(){ if(!onHome) showHome(); else super.onBackPressed(); }

  TextView text(String s,int z,boolean bold){
    TextView v=new TextView(this); v.setText(s); v.setTextSize(z); v.setTextColor(Color.WHITE); v.setTypeface(null,bold?1:0); return v;
  }

  void base(String title,String subtitle,boolean back){
    onHome=!back;
    ScrollView sv=new ScrollView(this);
    root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(26,26,26,40); root.setBackgroundColor(Color.rgb(14,18,24));
    if(back){ Button b=new Button(this); b.setText("← Tillbaka"); b.setOnClickListener(v->showHome()); root.addView(b); }
    TextView h=text(title,26,true); h.setPadding(0,back?18:0,0,0); root.addView(h);
    TextView s=text(subtitle,14,false); s.setTextColor(Color.rgb(120,255,150)); s.setPadding(0,4,0,18); root.addView(s);
    sv.addView(root); setContentView(sv);
  }

  void showHome(){
    base("SMART OPTIMIZER 8.1","S23 Ultra Toolbox • nu med egna menyer",false);
    status=text(summary(),15,false); status.setPadding(18,16,18,16); status.setBackgroundColor(Color.rgb(25,32,42)); root.addView(status);
    addCard("⚡ 1‑Tap Optimize","Egna profiler och säkra snabbval i appen.",()->optimizePage());
    addCard("🔋 Battery Guardian","Batteriprofil, skärmtid och ljusstyrning.",()->batteryPage());
    addCard("🚀 Performance Center","Prestandaprofiler, skärm och respons.",()->performancePage());
    addCard("🧹 Deep Cleaner","Lagringsstatus och rensningsverktyg.",()->cleanerPage());
    addCard("📦 Debloat Pro","Samsung-appar sorterade i tydliga grupper.",()->debloatPage());
    addCard("📸 Camera Assistant","Fotoprofiler, råd och snabbstart.",()->cameraPage());
    addCard("🎮 Gaming Mode","Spelprofil med ljus, timeout och ljud.",()->gamingPage());
    addCard("🖥️ DeX Toolbox","DeX-tips och skärmrelaterade snabbval.",()->dexPage());
    addCard("🔊 Audio Center","Volymreglage direkt i Smart Optimizer.",()->audioPage());
    addCard("🔐 Privacy Center","Integritetsöversikt och behörighetsverktyg.",()->privacyPage());
    addCard("🌡️ Device Monitor","Batteri, temperatur, RAM och lagring.",()->monitorPage());
    addCard("🔧 Shizuku Tools",shizuku()?"Shizuku hittad • öppna verktygssidan":"Shizuku är inte installerad",()->shizukuPage());
    addCard("📖 S23 Tips & Tricks","Praktiska inställningar för skärm, kamera och DeX.",()->tipsPage());
  }

  void addCard(String a,String b,Go g){
    LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(20,16,20,16); c.setBackgroundColor(Color.rgb(24,30,40));
    TextView h=text(a,18,true); c.addView(h); TextView d=text(b,13,false); d.setTextColor(Color.LTGRAY); d.setPadding(0,5,0,5); c.addView(d); c.setOnClickListener(v->g.run());
    LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,14,0,0); root.addView(c,lp);
  }

  void section(String t){ TextView v=text(t,19,true); v.setPadding(0,22,0,8); v.setTextColor(Color.rgb(120,255,150)); root.addView(v); }
  void note(String s){ TextView v=text(s,14,false); v.setTextColor(Color.LTGRAY); v.setPadding(8,8,8,8); root.addView(v); }
  Button button(String s,Go g){ Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setOnClickListener(v->g.run()); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,8,0,0); root.addView(b,lp); return b; }

  void writeSwitch(String label,String key,int on,int off){
    Switch sw=new Switch(this); sw.setText(label); sw.setTextColor(Color.WHITE); sw.setTextSize(16); sw.setPadding(8,10,8,10);
    try{ sw.setChecked(Settings.System.getInt(getContentResolver(),key,on)==on); }catch(Exception e){}
    sw.setOnCheckedChangeListener((b,c)->{ if(!ensureWrite()){ b.setChecked(!c); return; } try{ Settings.System.putInt(getContentResolver(),key,c?on:off); toast("Ändrat"); }catch(Exception e){ toast("Kunde inte ändra den här inställningen"); } });
    root.addView(sw);
  }

  boolean ensureWrite(){
    if(Build.VERSION.SDK_INT<23 || Settings.System.canWrite(this)) return true;
    toast("Tillåt Smart Optimizer att ändra systeminställningar");
    try{ startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:"+getPackageName()))); }catch(Exception e){ open(Settings.ACTION_SETTINGS); }
    return false;
  }

  void optimizePage(){
    base("⚡ 1‑Tap Optimize","Profiler som kan tillämpas direkt från appen",true);
    note("Profilerna ändrar bara vanliga systeminställningar som Android tillåter. De låtsas inte rensa RAM eller överklocka telefonen.");
    if(Build.VERSION.SDK_INT>=23 && !Settings.System.canWrite(this)) button("Ge behörighet för direktstyrning",()->ensureWrite());
    section("Profiler");
    button("Balanserad profil",()->applyProfile(60000,true,true));
    button("Batterisnål profil",()->applyProfile(30000,true,false));
    button("Skärm / media-profil",()->applyProfile(120000,true,true));
    section("Samsung Enhetsvård"); button("Öppna Enhetsvård",()->openCare());
  }

  void applyProfile(int timeout,boolean adaptive,boolean rotate){
    if(!ensureWrite()) return;
    try{
      Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,timeout);
      Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,adaptive?Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC:Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
      Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,rotate?1:0);
      toast("Profilen är aktiverad");
    }catch(Exception e){ toast("Profilen kunde inte tillämpas fullt ut"); }
  }

  void batteryPage(){
    base("🔋 Battery Guardian","Batterival utan att lämna appen",true);
    note(summary());
    section("Direkta tweaks");
    writeSwitch("Adaptiv ljusstyrka",Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    writeSwitch("Auto-rotera",Settings.System.ACCELEROMETER_ROTATION,1,0);
    section("Skärmen släcks efter");
    button("30 sekunder",()->setTimeout(30000)); button("1 minut",()->setTimeout(60000)); button("2 minuter",()->setTimeout(120000));
    section("Systemfunktion"); button("Batterisparläge",()->open(Settings.ACTION_BATTERY_SAVER_SETTINGS));
  }

  void performancePage(){
    base("🚀 Performance Center","Tweaks för känsla, skärm och respons",true);
    note("Samsung låser CPU/GPU-klockor för vanliga appar. Här samlar vi sådant som faktiskt kan påverkas säkert.");
    section("Skärm & respons");
    writeSwitch("Auto-rotera",Settings.System.ACCELEROMETER_ROTATION,1,0);
    writeSwitch("Adaptiv ljusstyrka",Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    button("Håll skärmen vaken 2 min",()->setTimeout(120000));
    button("Öppna 120 Hz / upplösning",()->open(Settings.ACTION_DISPLAY_SETTINGS));
    section("Rekommenderad S23 Ultra-profil");
    note("QHD+ + Adaptiv 120 Hz ger den bästa kombinationen av skärpa och flyt när batteritid inte är högsta prioritet.");
  }

  void cleanerPage(){
    base("🧹 Deep Cleaner","Lagringsöversikt med säkra verktyg",true);
    section("Status"); note(storageSummary());
    button("Uppdatera lagringsstatus",()->cleanerPage());
    section("Säker rensning");
    note("Smart Optimizer raderar inte filer automatiskt. Det minskar risken att foton, nedladdningar eller appdata försvinner av misstag.");
    button("Öppna lagringshanteraren",()->open(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
    button("Hantera nedladdningar",()->{ try{ startActivity(new Intent(Intent.ACTION_VIEW).setType("resource/folder")); }catch(Exception e){ open(Settings.ACTION_INTERNAL_STORAGE_SETTINGS); } });
  }

  void debloatPage(){
    base("📦 Debloat Pro","Se installerade Samsung-paket innan du gör något",true);
    note("Grönt = vanligtvis valfritt. Gult = kontrollera först. Smart Optimizer avinstallerar inget automatiskt i 8.1.");
    section("Vanligtvis valfria");
    pkgRow("Samsung Free","com.samsung.android.app.spage");
    pkgRow("Samsung Global Goals","com.samsung.sree");
    pkgRow("AR Zone","com.samsung.android.arzone");
    pkgRow("Samsung Kids","com.samsung.android.kidsinstaller");
    section("Var försiktig");
    pkgRow("Bixby Voice","com.samsung.android.bixby.agent");
    pkgRow("Samsung Pass","com.samsung.android.samsungpass");
    pkgRow("SmartThings","com.samsung.android.oneconnect");
    note("Nästa steg för Debloat Pro är riktig Shizuku-styrning för avaktivera/återaktivera direkt i appen.");
  }

  void pkgRow(String name,String pkg){
    boolean installed=isInstalled(pkg); addCard((installed?"● ":"○ ")+name,installed?pkg+" • installerad":pkg+" • ej installerad",()->appDetails(pkg));
  }

  void cameraPage(){
    base("📸 Camera Assistant","Fotoprofiler och praktiska S23 Ultra-val",true);
    section("Snabbprofiler");
    note("Vardag: 12 MP för snabbast bildtagning och bäst HDR.\nDetalj i bra ljus: 200 MP när motivet står still.\nZoom: använd helst 3x eller 10x.\nVideo: 4K60 är ett starkt standardval.");
    button("Starta kameran",()->camera());
    section("Kameratips");
    note("Rengör linserna före 10x. Håll mobilen stilla vid 200 MP. Vid mörker ger 12 MP oftast bättre resultat än 200 MP.");
  }

  void gamingPage(){
    base("🎮 Gaming Mode","En egen spelpanel",true);
    section("Spelprofil");
    button("Aktivera 2 min skärmtimeout",()->setTimeout(120000));
    writeSwitch("Auto-rotera",Settings.System.ACCELEROMETER_ROTATION,1,0);
    button("Öppna Game Launcher / Booster",()->game());
    section("Ljud"); addVolume("Media",AudioManager.STREAM_MUSIC);
  }

  void dexPage(){
    base("🖥️ DeX Toolbox","S23 Ultra som liten dator",true);
    section("Bra DeX-val");
    note("Använd extern skärm, tangentbord och mus. Håll appar uppdaterade och välj hög upplösning på den externa skärmen när bildskärmen stöder det.");
    button("Skärminställningar",()->open(Settings.ACTION_DISPLAY_SETTINGS));
    button("Ljudutgång",()->open(Settings.ACTION_SOUND_SETTINGS));
    section("Arbetsprofil"); button("Sätt 2 min skärmtimeout",()->setTimeout(120000));
  }

  void audioPage(){
    base("🔊 Audio Center","Volym direkt i appen",true);
    addVolume("Media",AudioManager.STREAM_MUSIC); addVolume("Ringsignal",AudioManager.STREAM_RING); addVolume("Notiser",AudioManager.STREAM_NOTIFICATION); addVolume("Alarm",AudioManager.STREAM_ALARM);
    button("Fler ljudinställningar / Dolby Atmos",()->open(Settings.ACTION_SOUND_SETTINGS));
  }

  void addVolume(String label,int stream){
    AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE); section(label);
    SeekBar s=new SeekBar(this); s.setMax(am.getStreamMaxVolume(stream)); s.setProgress(am.getStreamVolume(stream)); s.setPadding(8,4,8,8); s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ public void onProgressChanged(SeekBar b,int p,boolean f){ if(f) am.setStreamVolume(stream,p,0); } public void onStartTrackingTouch(SeekBar b){} public void onStopTrackingTouch(SeekBar b){} }); root.addView(s);
  }

  void privacyPage(){
    base("🔐 Privacy Center","Samlad integritetsöversikt",true);
    section("Smart Optimizer"); note("Appen behöver ingen internetåtkomst för sina nuvarande funktioner. WRITE_SETTINGS används bara när du själv aktiverar direkta systemtweaks.");
    button("Behörighetshanteraren",()->open(Settings.ACTION_PRIVACY_SETTINGS));
    button("Smart Optimizers appinfo",()->appDetails(getPackageName()));
  }

  void monitorPage(){
    base("🌡️ Device Monitor","Livevärden från telefonen",true);
    section("Aktuellt"); note(summary()); note(storageSummary());
    button("Uppdatera",()->monitorPage());
  }

  void shizukuPage(){
    base("🔧 Shizuku Tools","Status och förberedelse för djupare tweaks",true);
    section("Status"); note(shizuku()?"Shizuku är installerad på telefonen.":"Shizuku hittades inte.");
    if(shizuku()) button("Öppna Shizuku",()->openPkg("moe.shizuku.privileged.api"));
    else note("Installera och starta Shizuku först för framtida funktioner som riktig debloat direkt från Smart Optimizer.");
    section("8.1"); note("Den här versionen använder ännu inte Shizuku-API:t för att köra privilegierade kommandon. Menyn är nu förberedd så nästa steg kan byggas på rätt sätt.");
  }

  void tipsPage(){
    base("📖 S23 Tips & Tricks","Praktiska rekommendationer",true);
    section("Skärm"); note("• QHD+ för maximal skärpa.\n• Adaptiv 120 Hz för mjukaste känslan.\n• Mörkt läge kan vara behagligare kvällstid.");
    section("Kamera"); note("• 12 MP till vardags.\n• 200 MP i bra ljus och stilla motiv.\n• 3x och 10x är de viktigaste optiska zoomlägena.\n• 4K60 är ett bra video-standardläge.");
    section("Batteri"); note("• Kortare skärmtimeout ger märkbar besparing.\n• Adaptiv ljusstyrka fungerar bra för de flesta.\n• Kontrollera appar med hög bakgrundsanvändning.");
    section("DeX"); note("• Anslut skärm + tangentbord + mus.\n• Flera appar kan köras sida vid sida.\n• Bra för webbläsare, dokument och fjärrskrivbord.");
  }

  void setTimeout(int ms){ if(!ensureWrite()) return; try{ Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,ms); toast("Skärmtimeout ändrad"); }catch(Exception e){ toast("Kunde inte ändra timeout"); } }

  String summary(){
    Intent i=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    int level=i==null?-1:i.getIntExtra("level",-1), temp=i==null?0:i.getIntExtra("temperature",0);
    ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE); ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo(); am.getMemoryInfo(mi);
    return "Batteri: "+level+"%  •  Temp: "+(temp/10f)+"°C\nLedigt RAM: "+String.format(Locale.US,"%.1f",mi.availMem/1073741824.0)+" GB";
  }

  String storageSummary(){ android.os.StatFs fs=new android.os.StatFs(getFilesDir().getPath()); double free=fs.getAvailableBytes()/1073741824.0, total=fs.getTotalBytes()/1073741824.0; return "Lagring: "+String.format(Locale.US,"%.1f",free)+" GB ledigt av "+String.format(Locale.US,"%.1f",total)+" GB"; }
  void toast(String s){ Toast.makeText(this,s,Toast.LENGTH_SHORT).show(); }
  void open(String a){ try{ startActivity(new Intent(a)); }catch(Exception e){ startActivity(new Intent(Settings.ACTION_SETTINGS)); } }
  boolean openPkg(String p){ try{ Intent i=getPackageManager().getLaunchIntentForPackage(p); if(i!=null){ startActivity(i); return true; } }catch(Exception e){} return false; }
  boolean isInstalled(String p){ try{ getPackageManager().getPackageInfo(p,0); return true; }catch(Exception e){ return false; } }
  void appDetails(String p){ try{ startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:"+p))); }catch(Exception e){ open(Settings.ACTION_APPLICATION_SETTINGS); } }
  void openCare(){ if(!openPkg("com.samsung.android.lool")) if(!openPkg("com.samsung.android.sm")) open(Settings.ACTION_SETTINGS); }
  void game(){ if(!openPkg("com.samsung.android.game.gamehome")) if(!openPkg("com.samsung.android.game.gametools")) open(Settings.ACTION_SETTINGS); }
  void camera(){ try{ startActivity(new Intent("android.media.action.STILL_IMAGE_CAMERA")); }catch(Exception e){ open(Settings.ACTION_APPLICATION_SETTINGS); } }
  boolean shizuku(){ return isInstalled("moe.shizuku.privileged.api"); }
}
