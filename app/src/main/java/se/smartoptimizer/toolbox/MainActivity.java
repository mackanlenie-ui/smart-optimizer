package se.smartoptimizer.toolbox;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {
  LinearLayout root;
  TextView status;
  boolean onHome = true;
  final int SHIZUKU_REQ = 4201;
  SharedPreferences prefs;
  interface Go { void run(); }

  final String[][] SAFE = {
    {"Samsung Free","com.samsung.android.app.spage","Säker"},
    {"Samsung Global Goals","com.samsung.sree","Säker"},
    {"AR Zone","com.samsung.android.arzone","Säker"},
    {"Samsung Kids","com.samsung.android.kidsinstaller","Säker"},
    {"Bixby Voice","com.samsung.android.bixby.agent","Försiktig"},
    {"SmartThings","com.samsung.android.oneconnect","Försiktig"}
  };

  final Shizuku.OnRequestPermissionResultListener permListener = (requestCode, grantResult) -> {
    if(requestCode==SHIZUKU_REQ){
      toast(grantResult==PackageManager.PERMISSION_GRANTED ? "Shizuku-behörighet beviljad" : "Shizuku-behörighet nekad");
      if(!onHome) shizukuPage(); else showHome();
    }
  };

  @Override public void onCreate(Bundle b){
    super.onCreate(b);
    prefs=getSharedPreferences("toolbox",MODE_PRIVATE);
    try{ Shizuku.addRequestPermissionResultListener(permListener); }catch(Throwable ignored){}
    showHome();
  }

  @Override protected void onDestroy(){
    try{ Shizuku.removeRequestPermissionResultListener(permListener); }catch(Throwable ignored){}
    super.onDestroy();
  }

  @Override public void onBackPressed(){ if(!onHome) showHome(); else super.onBackPressed(); }

  TextView text(String s,int z,boolean bold){ TextView v=new TextView(this); v.setText(s); v.setTextSize(z); v.setTextColor(Color.WHITE); v.setTypeface(null,bold?1:0); return v; }

  void base(String title,String subtitle,boolean back){
    onHome=!back;
    ScrollView sv=new ScrollView(this);
    root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(26,26,26,50); root.setBackgroundColor(Color.rgb(14,18,24));
    if(back){ Button b=new Button(this); b.setText("← Tillbaka"); b.setAllCaps(false); b.setOnClickListener(v->showHome()); root.addView(b); }
    TextView h=text(title,26,true); h.setPadding(0,back?18:0,0,0); root.addView(h);
    TextView s=text(subtitle,14,false); s.setTextColor(Color.rgb(120,255,150)); s.setPadding(0,4,0,18); root.addView(s);
    sv.addView(root); setContentView(sv);
  }

  void showHome(){
    base("S23 ULTRA TOOLBOX 8.2","Samsung S23 Ultra • Shizuku • analys • tweaks",false);
    status=text(summary(),15,false); status.setPadding(18,16,18,16); status.setBackgroundColor(Color.rgb(25,32,42)); root.addView(status);
    addCard("🔎 Analysera min telefon","Ger personliga rekommendationer och en optimeringspoäng.",()->analysisPage());
    addCard("⚡ Smart Optimize","Profiler som faktiskt ändrar tillåtna systeminställningar.",()->optimizePage());
    addCard("🛡️ Smart Debloat","Avaktivera och återställ valfria paket via Shizuku.",()->debloatPage());
    addCard("🚀 Performance Center","Skärm, respons, timeout och prestandaprofil.",()->performancePage());
    addCard("🔋 Battery Guardian","Batteridata och batterisnåla snabbval.",()->batteryPage());
    addCard("🎮 Gaming Mode","Spelprofil, volym, rotation och skärmtimeout.",()->gamingPage());
    addCard("🖥️ DeX Toolbox","Praktiska DeX-profiler och snabbval.",()->dexPage());
    addCard("📸 Camera Assistant","S23 Ultra-profiler för foto, zoom och video.",()->cameraPage());
    addCard("🔊 Audio Center","Volymreglage direkt i appen.",()->audioPage());
    addCard("🌡️ Device Monitor","Batteri, temperatur, RAM och lagring.",()->monitorPage());
    addCard("🔧 Shizuku Tools",shizukuStatus(),()->shizukuPage());
    addCard("🕘 Historik & skydd","Se utförda åtgärder och skyddade paket.",()->historyPage());
    addCard("📖 S23 Tips & Tricks","Kamera, skärm, batteri, S Pen och DeX.",()->tipsPage());
  }

  void addCard(String a,String b,Go g){
    LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(20,16,20,16); c.setBackgroundColor(Color.rgb(24,30,40));
    TextView h=text(a,18,true); c.addView(h); TextView d=text(b,13,false); d.setTextColor(Color.LTGRAY); d.setPadding(0,5,0,5); c.addView(d); c.setOnClickListener(v->g.run());
    LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,14,0,0); root.addView(c,lp);
  }

  void section(String t){ TextView v=text(t,19,true); v.setPadding(0,22,0,8); v.setTextColor(Color.rgb(120,255,150)); root.addView(v); }
  void note(String s){ TextView v=text(s,14,false); v.setTextColor(Color.LTGRAY); v.setPadding(8,8,8,8); root.addView(v); }
  Button button(String s,Go g){ Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setOnClickListener(v->g.run()); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,8,0,0); root.addView(b,lp); return b; }

  String summary(){
    Intent i=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    int level=i==null?-1:i.getIntExtra("level",-1), temp=i==null?0:i.getIntExtra("temperature",0);
    ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE); ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo(); am.getMemoryInfo(mi);
    long total=mi.totalMem, used=total-mi.availMem; int ram= total>0 ? (int)(used*100/total) : 0;
    android.os.StatFs fs=new android.os.StatFs(getFilesDir().getPath());
    return "📱 "+Build.MANUFACTURER+" "+Build.MODEL+" • Android "+Build.VERSION.RELEASE+"\n🔋 "+level+"% • 🌡️ "+(temp/10f)+"°C • 🧠 RAM "+ram+"% • 💾 "+(fs.getAvailableBytes()/1073741824L)+" GB ledigt\n"+(hasShizukuPermission()?"✅ Shizuku behörig":"⚠️ Shizuku ej behörig");
  }

  void analysisPage(){
    base("🔎 TELEFONANALYS","Personliga rekommendationer för just din S23 Ultra",true);
    Intent i=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED)); int level=i==null?-1:i.getIntExtra("level",-1), temp=i==null?0:i.getIntExtra("temperature",0);
    ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE); ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo(); am.getMemoryInfo(mi); int ram=(int)((mi.totalMem-mi.availMem)*100/Math.max(1,mi.totalMem));
    int timeout=60000; try{ timeout=Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT); }catch(Exception ignored){}
    int optional=0; for(String[] p:SAFE) if(isInstalled(p[1]) && !isProtected(p[1])) optional++;
    int score=100; if(temp>=420) score-=15; else if(temp>=390) score-=7; if(ram>=85) score-=10; if(timeout>120000) score-=8; if(optional>0) score-=Math.min(15,optional*2); if(!hasShizukuPermission()) score-=5;
    section("Optimeringspoäng: "+Math.max(0,score)+" / 100");
    note("Batteri "+level+"% • temperatur "+(temp/10f)+"°C • RAM-belastning "+ram+"% • "+optional+" valfria paket hittades.");
    section("Rekommendationer");
    if(temp>=420) note("🔥 Telefonen är varm. Undvik tung laddning/spel tills temperaturen sjunker."); else note("✅ Temperaturen ser normal ut.");
    if(timeout>120000) note("⏱️ Skärmtimeouten är lång. 1–2 minuter sparar batteri."); else note("✅ Skärmtimeouten är rimlig.");
    if(optional>0) note("🛡️ Smart Debloat har "+optional+" valfria installerade paket att granska.");
    if(!hasShizukuPermission()) note("🔧 Ge Shizuku-behörighet för direkt debloat och återställning.");
    button("Tillämpa rekommenderad balanserad profil",()->applyProfile(60000,true,true));
    button("Öppna Smart Debloat",()->debloatPage());
    log("Analys körd • poäng "+Math.max(0,score));
  }

  void optimizePage(){
    base("⚡ SMART OPTIMIZE","Säkra profiler med tydliga ändringar",true);
    note("Profilerna ändrar bara inställningar som Android tillåter. Ingen falsk RAM-rensning eller överklockning.");
    section("Profiler");
    button("⚖️ Balanserad • 1 min • adaptiv ljusstyrka",()->applyProfile(60000,true,true));
    button("🔋 Batteri • 30 sek • adaptiv ljusstyrka",()->applyProfile(30000,true,false));
    button("🎬 Media / DeX • 2 min • rotation",()->applyProfile(120000,true,true));
    button("🎮 Gaming • 2 min • rotation",()->applyProfile(120000,false,true));
  }

  void applyProfile(int timeout,boolean adaptive,boolean rotate){
    if(!ensureWrite()) return;
    try{
      Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,timeout);
      Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,adaptive?Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC:Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
      Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,rotate?1:0);
      toast("Profilen är aktiverad"); log("Profil aktiverad • timeout "+(timeout/1000)+" s");
    }catch(Exception e){ toast("Profilen kunde inte tillämpas fullt ut"); }
  }

  void debloatPage(){
    base("🛡️ SMART DEBLOAT","Shizuku-styrd avaktivering med risknivå och återställning",true);
    if(!hasShizukuPermission()){
      note("Shizuku måste vara igång och S23 Ultra Toolbox måste få behörighet innan direkt avaktivering fungerar.");
      button("Ge Shizuku-behörighet",()->requestShizuku());
    } else note("✅ Shizuku-behörighet aktiv. Paket kan avaktiveras och återställas direkt.");
    for(String[] p:SAFE){ if(isInstalled(p[1])) packageControl(p[0],p[1],p[2]); }
    section("Skydd"); note("Viktiga paket kan skyddas så att du inte råkar avaktivera dem.");
  }

  void packageControl(String name,String pkg,String risk){
    section((isProtected(pkg)?"🔒 ":"")+name+" • "+risk);
    note(pkg);
    LinearLayout row=new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
    Button disable=new Button(this); disable.setText("Avaktivera"); disable.setAllCaps(false); disable.setEnabled(!isProtected(pkg));
    disable.setOnClickListener(v->confirmDisable(name,pkg)); row.addView(disable,new LinearLayout.LayoutParams(0,-2,1));
    Button enable=new Button(this); enable.setText("Återställ"); enable.setAllCaps(false); enable.setOnClickListener(v->runPm("pm enable --user 0 "+pkg,"Återställde "+name)); row.addView(enable,new LinearLayout.LayoutParams(0,-2,1)); root.addView(row);
    button(isProtected(pkg)?"Ta bort skydd":"Skydda paket",()->{ setProtected(pkg,!isProtected(pkg)); debloatPage(); });
  }

  void confirmDisable(String name,String pkg){
    new AlertDialog.Builder(this).setTitle("Avaktivera "+name+"?").setMessage("Paket: "+pkg+"\n\nDu kan återställa det igen från samma meny.")
      .setNegativeButton("Avbryt",null).setPositiveButton("Avaktivera",(d,w)->runPm("pm disable-user --user 0 "+pkg,"Avaktiverade "+name)).show();
  }

  void runPm(String command,String success){
    if(!hasShizukuPermission()){ requestShizuku(); return; }
    new Thread(()->{
      try{
        Method m=Shizuku.class.getDeclaredMethod("newProcess",String[].class,String[].class,String.class); m.setAccessible(true);
        Process p=(Process)m.invoke(null,new Object[]{new String[]{"sh","-c",command},null,null}); int code=p.waitFor();
        runOnUiThread(()->{ if(code==0){ toast(success); log(success); } else toast("Åtgärden misslyckades (kod "+code+")"); });
      }catch(Throwable e){ runOnUiThread(()->toast("Shizuku-kommandot kunde inte köras")); }
    }).start();
  }

  void performancePage(){
    base("🚀 PERFORMANCE CENTER","Skärm och respons utan låtsas-tweaks",true);
    note("Samsung låser CPU/GPU-klockor för vanliga appar. Här ändras bara sådant som faktiskt är tillgängligt säkert.");
    section("Direkt i appen");
    writeSwitch("Auto-rotera",Settings.System.ACCELEROMETER_ROTATION,1,0);
    writeSwitch("Adaptiv ljusstyrka",Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    button("30 sek timeout",()->setTimeout(30000)); button("1 min timeout",()->setTimeout(60000)); button("2 min timeout",()->setTimeout(120000));
    section("S23 Ultra rekommendation"); note("QHD+ + Adaptiv 120 Hz ger bäst skärpa och flyt. Samsung kräver sin egen skärmmeny för de två valen.");
    button("Öppna exakt skärmmeny",()->open(Settings.ACTION_DISPLAY_SETTINGS));
  }

  void batteryPage(){
    base("🔋 BATTERY GUARDIAN","Batteri och skärmtid",true); note(summary());
    writeSwitch("Adaptiv ljusstyrka",Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    button("Batteriprofil • 30 sek",()->applyProfile(30000,true,false));
    button("Batterisparläge",()->open(Settings.ACTION_BATTERY_SAVER_SETTINGS));
  }

  void gamingPage(){
    base("🎮 GAMING MODE","Spelprofil",true); button("Aktivera spelprofil",()->applyProfile(120000,false,true));
    section("Media-volym"); addVolume("Spel / media",AudioManager.STREAM_MUSIC); button("Öppna Game Booster",()->game());
  }

  void dexPage(){
    base("🖥️ DEX TOOLBOX","Gör S23 Ultra till en liten dator",true);
    note("Tips: använd extern skärm, tangentbord/mus och 2 min timeout. Samsung styr DeX-upplösning i sin egen meny.");
    button("Aktivera DeX-arbetsprofil",()->applyProfile(120000,true,true)); button("Skärminställningar",()->open(Settings.ACTION_DISPLAY_SETTINGS)); button("Ljudutgång",()->open(Settings.ACTION_SOUND_SETTINGS));
  }

  void cameraPage(){
    base("📸 CAMERA ASSISTANT","Praktiska S23 Ultra-profiler",true);
    section("Vardag"); note("12 MP • bäst balans mellan HDR, hastighet och mörkerprestanda.");
    section("Detalj"); note("200 MP • använd i bra ljus och på stilla motiv.");
    section("Zoom"); note("3x och 10x använder dedikerade telekameror och ger oftast bättre resultat än mellanlägen.");
    section("Video"); note("4K60 är ett starkt standardläge. 8K passar främst när du verkligen behöver extra detalj och har gott om ljus.");
    button("Starta kameran",()->camera());
  }

  void audioPage(){ base("🔊 AUDIO CENTER","Volym direkt i Toolbox",true); addVolume("Media",AudioManager.STREAM_MUSIC); addVolume("Ringsignal",AudioManager.STREAM_RING); addVolume("Notiser",AudioManager.STREAM_NOTIFICATION); addVolume("Alarm",AudioManager.STREAM_ALARM); button("Dolby Atmos / fler ljudval",()->open(Settings.ACTION_SOUND_SETTINGS)); }

  void monitorPage(){ base("🌡️ DEVICE MONITOR","Livevärden",true); section("Aktuellt"); note(summary()); button("Uppdatera",()->monitorPage()); }

  void shizukuPage(){
    base("🔧 SHIZUKU TOOLS","Riktig API-status och behörighet",true);
    section("Status"); note(shizukuStatus());
    if(shizukuBinderAlive() && !hasShizukuPermission()) button("Begär Shizuku-behörighet",()->requestShizuku());
    if(shizukuInstalled()) button("Öppna Shizuku",()->openPkg("moe.shizuku.privileged.api"));
    note("Shizuku används för Smart Debloat. ADB/Shizuku har fortfarande begränsningar som varierar mellan Android-versioner.");
  }

  void historyPage(){
    base("🕘 HISTORIK & SKYDD","Dina senaste Toolbox-åtgärder",true);
    String h=prefs.getString("history","Ingen aktivitet ännu."); section("Historik"); note(h);
    section("Skyddade paket"); StringBuilder b=new StringBuilder(); for(String[] p:SAFE) if(isProtected(p[1])) b.append("🔒 ").append(p[0]).append("\n"); note(b.length()==0?"Inga paket är skyddade.":b.toString());
    button("Rensa historik",()->{ prefs.edit().remove("history").apply(); historyPage(); });
  }

  void tipsPage(){
    base("📖 S23 ULTRA TIPS & TRICKS","Rekommenderade inställningar",true);
    section("Skärm"); note("• QHD+ när du vill ha maximal skärpa.\n• Adaptiv 120 Hz för mjukast känsla.\n• Mörkt läge kvällstid.");
    section("Kamera"); note("• 12 MP för vardag/mörker.\n• 200 MP i bra ljus.\n• 3x/10x för bäst telekvalitet.\n• 4K60 som bra videostandard.\n• Expert RAW när du vill efterbehandla mer.");
    section("Batteri"); note("• 30–60 sek skärmtimeout när du inte behöver längre.\n• Adaptiv ljusstyrka.\n• Undvik tung belastning när telefonen redan är varm.");
    section("DeX"); note("• Extern skärm + tangentbord/mus.\n• 2 min timeout för arbete.\n• Använd fönsterläge för flera appar.");
    section("S Pen"); note("• Använd Screen off memo för snabba anteckningar.\n• Air Command ger snabb åtkomst till smart markering och översättning.");
  }

  void writeSwitch(String label,String key,int on,int off){
    Switch sw=new Switch(this); sw.setText(label); sw.setTextColor(Color.WHITE); sw.setTextSize(16); sw.setPadding(8,10,8,10);
    try{ sw.setChecked(Settings.System.getInt(getContentResolver(),key,on)==on); }catch(Exception ignored){}
    sw.setOnCheckedChangeListener((b,c)->{ if(!ensureWrite()){ b.setChecked(!c); return; } try{ Settings.System.putInt(getContentResolver(),key,c?on:off); log(label+" = "+(c?"på":"av")); }catch(Exception e){ toast("Kunde inte ändra inställningen"); } }); root.addView(sw);
  }

  void addVolume(String label,int stream){ AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE); section(label); SeekBar s=new SeekBar(this); s.setMax(am.getStreamMaxVolume(stream)); s.setProgress(am.getStreamVolume(stream)); s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ public void onProgressChanged(SeekBar b,int p,boolean f){ if(f) am.setStreamVolume(stream,p,0); } public void onStartTrackingTouch(SeekBar b){} public void onStopTrackingTouch(SeekBar b){} }); root.addView(s); }

  boolean ensureWrite(){ if(Build.VERSION.SDK_INT<23 || Settings.System.canWrite(this)) return true; toast("Tillåt S23 Ultra Toolbox att ändra systeminställningar"); try{ startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,Uri.parse("package:"+getPackageName()))); }catch(Exception e){ open(Settings.ACTION_SETTINGS); } return false; }
  void setTimeout(int ms){ if(!ensureWrite()) return; try{ Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,ms); toast("Skärmtimeout: "+(ms/1000)+" sek"); log("Skärmtimeout "+(ms/1000)+" s"); }catch(Exception e){ toast("Kunde inte ändra timeout"); } }

  boolean shizukuInstalled(){ try{ getPackageManager().getPackageInfo("moe.shizuku.privileged.api",0); return true; }catch(Exception e){ return false; } }
  boolean shizukuBinderAlive(){ try{ return Shizuku.pingBinder(); }catch(Throwable e){ return false; } }
  boolean hasShizukuPermission(){ try{ return shizukuBinderAlive() && Shizuku.checkSelfPermission()==PackageManager.PERMISSION_GRANTED; }catch(Throwable e){ return false; } }
  String shizukuStatus(){ if(!shizukuInstalled()) return "❌ Shizuku är inte installerad"; if(!shizukuBinderAlive()) return "⚠️ Shizuku installerad men tjänsten är inte igång"; return hasShizukuPermission()?"✅ Shizuku aktiv och behörig":"🟡 Shizuku aktiv • behörighet krävs"; }
  void requestShizuku(){ try{ if(!shizukuBinderAlive()){ toast("Starta Shizuku först"); openPkg("moe.shizuku.privileged.api"); return; } if(Shizuku.checkSelfPermission()==PackageManager.PERMISSION_GRANTED){ toast("Shizuku är redan behörig"); return; } if(!Shizuku.shouldShowRequestPermissionRationale()) Shizuku.requestPermission(SHIZUKU_REQ); else toast("Tillåt behörigheten i Shizuku"); }catch(Throwable e){ toast("Kunde inte begära Shizuku-behörighet"); } }

  boolean isInstalled(String p){ try{ getPackageManager().getPackageInfo(p,0); return true; }catch(Exception e){ return false; } }
  boolean isProtected(String p){ return prefs.getBoolean("protect_"+p,false); }
  void setProtected(String p,boolean b){ prefs.edit().putBoolean("protect_"+p,b).apply(); log((b?"Skyddade ":"Tog bort skydd från ")+p); }
  void log(String s){ String old=prefs.getString("history",""); String now=new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault()).format(new Date()); String n=now+" • "+s+(old.isEmpty()?"":"\n"+old); String[] lines=n.split("\n"); StringBuilder out=new StringBuilder(); for(int i=0;i<Math.min(25,lines.length);i++) out.append(lines[i]).append(i<Math.min(25,lines.length)-1?"\n":""); prefs.edit().putString("history",out.toString()).apply(); }

  void open(String a){ try{ startActivity(new Intent(a)); }catch(Exception e){ startActivity(new Intent(Settings.ACTION_SETTINGS)); } }
  boolean openPkg(String p){ try{ Intent i=getPackageManager().getLaunchIntentForPackage(p); if(i!=null){ startActivity(i); return true; } }catch(Exception ignored){} return false; }
  void game(){ if(!openPkg("com.samsung.android.game.gamehome")) if(!openPkg("com.samsung.android.game.gametools")) open(Settings.ACTION_SETTINGS); }
  void camera(){ try{ startActivity(new Intent("android.media.action.STILL_IMAGE_CAMERA")); }catch(Exception e){ open(Settings.ACTION_APPLICATION_SETTINGS); } }
  void toast(String s){ Toast.makeText(this,s,Toast.LENGTH_SHORT).show(); }
}
