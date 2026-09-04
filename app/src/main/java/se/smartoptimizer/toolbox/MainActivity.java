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
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {
  LinearLayout root;
  boolean onHome=true;
  final int SHIZUKU_REQ=4201;
  SharedPreferences prefs;
  interface Go { void run(); }

  final String[][] PACKAGES={
    {"Samsung Free","com.samsung.android.app.spage","Säker","Samsung"},
    {"Samsung Global Goals","com.samsung.sree","Säker","Samsung"},
    {"AR Zone","com.samsung.android.arzone","Säker","Samsung"},
    {"Samsung Kids","com.samsung.android.kidsinstaller","Säker","Samsung"},
    {"Samsung Members","com.samsung.android.voc","Valfritt","Samsung"},
    {"Samsung Shop","com.samsung.ecomm.global","Valfritt","Samsung"},
    {"Bixby Voice","com.samsung.android.bixby.agent","Försiktig","Samsung"},
    {"Bixby Vision","com.samsung.android.visionintelligence","Valfritt","Samsung"},
    {"SmartThings","com.samsung.android.oneconnect","Försiktig","Samsung"},
    {"Samsung Health","com.sec.android.app.shealth","Valfritt","Samsung"},
    {"Samsung Internet","com.sec.android.app.sbrowser","Valfritt","Samsung"},
    {"Samsung Wallet","com.samsung.android.spay","Försiktig","Samsung"},
    {"Google TV","com.google.android.videos","Säker","Google"},
    {"Google Meet","com.google.android.apps.tachyon","Valfritt","Google"},
    {"YouTube Music","com.google.android.apps.youtube.music","Valfritt","Google"},
    {"Android Auto","com.google.android.projection.gearhead","Försiktig","Google"}
  };

  final Shizuku.OnRequestPermissionResultListener permListener=(requestCode,grantResult)->{
    if(requestCode==SHIZUKU_REQ){ toast(grantResult==PackageManager.PERMISSION_GRANTED?"Shizuku-behörighet beviljad":"Shizuku-behörighet nekad"); showHome(); }
  };

  @Override public void onCreate(Bundle b){ super.onCreate(b); prefs=getSharedPreferences("toolbox",MODE_PRIVATE); try{Shizuku.addRequestPermissionResultListener(permListener);}catch(Throwable ignored){} showHome(); }
  @Override protected void onDestroy(){ try{Shizuku.removeRequestPermissionResultListener(permListener);}catch(Throwable ignored){} super.onDestroy(); }
  @Override public void onBackPressed(){ if(!onHome) showHome(); else super.onBackPressed(); }

  TextView text(String s,int z,boolean bold){ TextView v=new TextView(this); v.setText(s); v.setTextSize(z); v.setTextColor(Color.WHITE); v.setTypeface(null,bold?1:0); return v; }
  void section(String s){ TextView v=text(s,19,true); v.setTextColor(Color.rgb(120,255,150)); v.setPadding(0,22,0,8); root.addView(v); }
  void note(String s){ TextView v=text(s,14,false); v.setTextColor(Color.LTGRAY); v.setPadding(8,7,8,7); root.addView(v); }
  Button button(String s,Go g){ Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setOnClickListener(v->g.run()); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,8,0,0); root.addView(b,lp); return b; }

  void base(String title,String subtitle,boolean back){
    onHome=!back; getWindow().setStatusBarColor(Color.rgb(14,18,24)); getWindow().setNavigationBarColor(Color.rgb(14,18,24));
    ScrollView sv=new ScrollView(this); root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(14,18,24));
    root.setPadding(26,26+statusBarHeight(),26,50);
    if(Build.VERSION.SDK_INT>=30){ sv.setOnApplyWindowInsetsListener((v,insets)->{ android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars()); root.setPadding(26,26+bars.top,26,50+bars.bottom); return insets; }); }
    if(back){ Button b=new Button(this); b.setText("← Tillbaka"); b.setAllCaps(false); b.setOnClickListener(v->showHome()); root.addView(b); }
    TextView h=text(title,26,true); h.setPadding(0,back?14:0,0,0); root.addView(h); TextView st=text(subtitle,14,false); st.setTextColor(Color.rgb(120,255,150)); st.setPadding(0,4,0,18); root.addView(st); sv.addView(root); setContentView(sv);
  }
  int statusBarHeight(){ int id=getResources().getIdentifier("status_bar_height","dimen","android"); return id>0?getResources().getDimensionPixelSize(id):0; }

  void showHome(){
    base("S23 ULTRA TOOLBOX 8.3","Samsung S23 Ultra • Shizuku • analys • tweaks",false);
    TextView s=text(summary(),15,false); s.setPadding(18,16,18,16); s.setBackgroundColor(Color.rgb(25,32,42)); root.addView(s);
    addCard("🔎 Analysera min telefon","Djupare analys med förklaringar och optimeringspoäng.",()->analysisPage());
    addCard("⚡ Smart Optimize 4.0","Förhandsgranska ändringar innan en profil aktiveras.",()->optimizePage());
    addCard("🛡️ Smart Debloat 2.0","Fler Samsung/Google-paket, sökning, status och återställning.",()->debloatPage(""));
    addCard("🚀 Performance Center","Balanserad, Max respons, Gaming, Batteri och DeX.",()->performancePage());
    addCard("🔋 Battery Guardian Pro","Batteridata, laddning, temperatur och energisnåla val.",()->batteryPage());
    addCard("🎮 Gaming Dashboard","Temperatur, RAM, batteri och spelprofil.",()->gamingPage());
    addCard("🖥️ DeX Center","Arbetsprofil och praktiska DeX-snabbval.",()->dexPage());
    addCard("📸 Camera Assistant Pro","Profiler för vardag, natt, zoom, porträtt och video.",()->cameraPage());
    addCard("🔊 Audio Center","Volymreglage direkt i appen.",()->audioPage());
    addCard("🌡️ Device Monitor Pro","Livevärden och lokal temperaturhistorik.",()->monitorPage());
    addCard("🔧 Shizuku Tools",shizukuStatus(),()->shizukuPage());
    addCard("↩️ Återställ & Backup","Återställ Toolbox-ändringar och säkerhetskopiera profiler.",()->restorePage());
    addCard("🔄 Efter systemuppdatering","Kontrollera om Android/Samsung har ändrat något.",()->updateCheckPage());
    addCard("📖 Tips & Tricks 2.0","Sökbar guide för kamera, S Pen, DeX, batteri och skärm.",()->tipsPage(""));
    addCard("🕘 Historik & skydd","Utförda åtgärder och skyddade paket.",()->historyPage());
  }

  void addCard(String a,String b,Go g){ LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(20,16,20,16); c.setBackgroundColor(Color.rgb(24,30,40)); TextView h=text(a,18,true); c.addView(h); TextView d=text(b,13,false); d.setTextColor(Color.LTGRAY); d.setPadding(0,5,0,4); c.addView(d); c.setOnClickListener(v->g.run()); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,12,0,0); root.addView(c,lp); }

  Intent batteryIntent(){ return registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED)); }
  int ramPercent(){ ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE); ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo(); am.getMemoryInfo(mi); return mi.totalMem>0?(int)((mi.totalMem-mi.availMem)*100/mi.totalMem):0; }
  String summary(){ Intent i=batteryIntent(); int level=i==null?-1:i.getIntExtra(BatteryManager.EXTRA_LEVEL,-1), temp=i==null?0:i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0); StatFs fs=new StatFs(getFilesDir().getPath()); return "📱 "+Build.MANUFACTURER+" "+Build.MODEL+" • Android "+Build.VERSION.RELEASE+"\n🔋 "+level+"% • 🌡️ "+(temp/10f)+"°C • 🧠 RAM "+ramPercent()+"% • 💾 "+(fs.getAvailableBytes()/1073741824L)+" GB ledigt\n"+(hasShizukuPermission()?"✅ Shizuku behörig":"⚠️ Shizuku ej behörig")+" • Profil: "+prefs.getString("profile","Ingen"); }

  void analysisPage(){
    base("🔎 TELEFONANALYS","Kontrollerar värden och förklarar varje rekommendation",true); Intent i=batteryIntent(); int level=i==null?-1:i.getIntExtra(BatteryManager.EXTRA_LEVEL,-1), temp=i==null?0:i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0), ram=ramPercent(); int timeout=60000; try{timeout=Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT);}catch(Exception ignored){} int optional=0,disabled=0; for(String[] p:PACKAGES) if(isInstalled(p[1])){optional++;if(!isPackageEnabled(p[1]))disabled++;} int score=100; if(temp>=430)score-=18;else if(temp>=400)score-=8;if(ram>=88)score-=10;else if(ram>=80)score-=5;if(timeout>120000)score-=7;if(!hasShizukuPermission())score-=5;if(level<15)score-=3;
    section("Optimeringspoäng: "+Math.max(score,0)+" / 100"); note("Batteri "+level+"% • temperatur "+(temp/10f)+"°C • RAM "+ram+"% • timeout "+(timeout/1000)+" s • "+disabled+" av "+optional+" listade paket avaktiverade."); section("Varför"); note(temp>=430?"🔥 Temperaturen är hög och kan sänka prestandan.":temp>=400?"🟠 Telefonen är varm men inte kritisk.":"✅ Temperaturen ser normal ut."); note(ram>=88?"🧠 RAM-belastningen är hög. Android hanterar minnet själv; Toolbox fejkar inte RAM-rensning.":"✅ RAM-belastningen är inom rimligt område."); note(timeout>120000?"⏱️ Lång skärmtimeout kan dra onödigt batteri.":"✅ Skärmtimeouten är rimlig."); note(hasShizukuPermission()?"✅ Shizuku är redo för Debloat.":"🔧 Shizuku-behörighet saknas."); button("Förhandsgranska balanserad profil",()->confirmProfile("Balanserad",60000,true,true)); button("Granska Smart Debloat",()->debloatPage("")); log("Analys körd • poäng "+Math.max(score,0));
  }

  void optimizePage(){ base("⚡ SMART OPTIMIZE 4.0","Analys → förhandsgranskning → tillämpning",true); note("Profilerna visar exakt vad som ändras. CPU/GPU-klockor och Samsung-låsta funktioner fejkas inte."); button("⚖️ Balanserad",()->confirmProfile("Balanserad",60000,true,true)); button("🚀 Max respons",()->confirmProfile("Max respons",120000,false,true)); button("🔋 Batteri",()->confirmProfile("Batteri",30000,true,false)); button("🎮 Gaming",()->confirmProfile("Gaming",120000,false,true)); button("🖥️ DeX",()->confirmProfile("DeX",120000,true,true)); }
  void confirmProfile(String name,int timeout,boolean adaptive,boolean rotate){ String msg="Profil: "+name+"\n\n• Skärmtimeout: "+(timeout/1000)+" sek\n• Adaptiv ljusstyrka: "+(adaptive?"På":"Av")+"\n• Auto-rotera: "+(rotate?"På":"Av")+"\n\nInga CPU/GPU-klockor ändras."; new AlertDialog.Builder(this).setTitle("Tillämpa profil?").setMessage(msg).setNegativeButton("Avbryt",null).setPositiveButton("Tillämpa",(d,w)->applyProfile(name,timeout,adaptive,rotate)).show(); }
  void applyProfile(String name,int timeout,boolean adaptive,boolean rotate){ if(!ensureWrite())return; try{ Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,timeout); Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,adaptive?1:0); Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,rotate?1:0); prefs.edit().putString("profile",name).apply(); toast(name+" aktiverad"); log("Profil aktiverad: "+name); }catch(Exception e){toast("Profilen kunde inte tillämpas fullt ut");} }

  void performancePage(){ base("🚀 PERFORMANCE CENTER","Profiler och verkliga systemval",true); note("Samsung låser CPU/GPU-klockor för vanliga appar. Toolbox ändrar bara det Android faktiskt tillåter."); button("⚖️ Balanserad",()->confirmProfile("Balanserad",60000,true,true)); button("🚀 Max respons",()->confirmProfile("Max respons",120000,false,true)); button("🎮 Gaming",()->confirmProfile("Gaming",120000,false,true)); button("🔋 Batteri",()->confirmProfile("Batteri",30000,true,false)); button("🖥️ DeX",()->confirmProfile("DeX",120000,true,true)); section("Direktreglage"); writeSwitch("Auto-rotera",Settings.System.ACCELEROMETER_ROTATION,1,0); writeSwitch("Adaptiv ljusstyrka",Settings.System.SCREEN_BRIGHTNESS_MODE,1,0); button("Öppna Samsungs skärmmeny för QHD+/120 Hz",()->open(Settings.ACTION_DISPLAY_SETTINGS)); }

  void debloatPage(String query){
    base("🛡️ SMART DEBLOAT 2.0","Sök • risknivå • aktiv status • återställning",true); if(!hasShizukuPermission()){note("⚠️ Shizuku måste vara igång och behörig.");button("Ge Shizuku-behörighet",()->requestShizuku());}else note("✅ Shizuku är aktiv och behörig."); EditText search=new EditText(this); search.setHint("Sök app eller paket…"); search.setText(query); search.setTextColor(Color.WHITE); search.setHintTextColor(Color.GRAY); root.addView(search); button("Sök",()->debloatPage(search.getText().toString().trim())); section("Paket"); String q=query.toLowerCase(Locale.ROOT); int shown=0; for(String[] p:PACKAGES){if(!isInstalled(p[1]))continue;if(!q.isEmpty()&&!(p[0]+" "+p[1]+" "+p[2]+" "+p[3]).toLowerCase(Locale.ROOT).contains(q))continue;packageControl(p[0],p[1],p[2],p[3]);shown++;} if(shown==0)note("Inga matchande installerade paket hittades."); section("Återställ"); button("Återställ allt som Toolbox har avaktiverat",()->confirmRestoreAll());
  }
  void packageControl(String name,String pkg,String risk,String group){ boolean enabled=isPackageEnabled(pkg); section((isProtected(pkg)?"🔒 ":"")+name+" • "+risk); note(group+" • "+pkg+"\nStatus: "+(enabled?"✅ Aktiv":"⛔ Avaktiverad")); LinearLayout row=new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL); Button d=new Button(this);d.setText("Avaktivera");d.setAllCaps(false);d.setEnabled(enabled&&!isProtected(pkg));d.setOnClickListener(v->confirmDisable(name,pkg));row.addView(d,new LinearLayout.LayoutParams(0,-2,1));Button e=new Button(this);e.setText("Återställ");e.setAllCaps(false);e.setEnabled(!enabled);e.setOnClickListener(v->runPmTracked("pm enable --user 0 "+pkg,"Återställde "+name,pkg,false));row.addView(e,new LinearLayout.LayoutParams(0,-2,1));root.addView(row);button(isProtected(pkg)?"Ta bort skydd":"Skydda paket",()->{setProtected(pkg,!isProtected(pkg));debloatPage("");}); }
  void confirmDisable(String name,String pkg){new AlertDialog.Builder(this).setTitle("Avaktivera "+name+"?").setMessage("Paket: "+pkg+"\n\nDet kan återställas från samma meny.").setNegativeButton("Avbryt",null).setPositiveButton("Avaktivera",(d,w)->runPmTracked("pm disable-user --user 0 "+pkg,"Avaktiverade "+name,pkg,true)).show();}
  void runPmTracked(String command,String success,String pkg,boolean disabled){ if(!hasShizukuPermission()){requestShizuku();return;} new Thread(()->{try{Method m=Shizuku.class.getDeclaredMethod("newProcess",String[].class,String[].class,String.class);m.setAccessible(true);java.lang.Process p=(java.lang.Process)m.invoke(null,new Object[]{new String[]{"sh","-c",command},null,null});int code=p.waitFor();runOnUiThread(()->{if(code==0){setDisabledTracked(pkg,disabled);toast(success);log(success);}else toast("Åtgärden misslyckades (kod "+code+")");});}catch(Throwable e){runOnUiThread(()->toast("Shizuku-kommandot kunde inte köras"));}}).start(); }
  void setDisabledTracked(String pkg,boolean disabled){Set<String>s=new HashSet<>(prefs.getStringSet("disabled",new HashSet<>()));if(disabled)s.add(pkg);else s.remove(pkg);prefs.edit().putStringSet("disabled",s).apply();}
  void confirmRestoreAll(){new AlertDialog.Builder(this).setTitle("Återställ alla?").setMessage("Alla paket som Toolbox har markerat som avaktiverade återaktiveras.").setNegativeButton("Avbryt",null).setPositiveButton("Återställ",(d,w)->restoreAll()).show();}
  void restoreAll(){Set<String>s=new HashSet<>(prefs.getStringSet("disabled",new HashSet<>()));if(s.isEmpty()){toast("Inget att återställa");return;}for(String pkg:s)runPmTracked("pm enable --user 0 "+pkg,"Återställde "+pkg,pkg,false);}

  void batteryPage(){base("🔋 BATTERY GUARDIAN PRO","Batteri, laddning och värme",true);Intent i=batteryIntent();int lvl=i==null?-1:i.getIntExtra(BatteryManager.EXTRA_LEVEL,-1),t=i==null?0:i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0),mv=i==null?0:i.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0),plug=i==null?0:i.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);section("Status");note("Batteri: "+lvl+"%\nTemperatur: "+(t/10f)+"°C ("+tempLabel(t/10f)+")\nSpänning: "+(mv/1000f)+" V\nLaddning: "+(plug==0?"Inte ansluten":"Ansluten"));button("Aktivera batteriprofil",()->confirmProfile("Batteri",30000,true,false));button("Öppna batterianvändning",()->open(Settings.ACTION_BATTERY_SAVER_SETTINGS));note("Samsungs privata batteristatistik kräver systembehörighet och öppnas därför i systemmenyn när det behövs.");}
  String tempLabel(float t){return t>=43?"Mycket varm":t>=40?"Varm":t>=35?"Normal/varm":"Normal";}
  void gamingPage(){base("🎮 GAMING DASHBOARD","Livevärden före och under spel",true);Intent i=batteryIntent();float t=i==null?0:i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0)/10f;int lvl=i==null?-1:i.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);section("Dashboard");note("🔋 "+lvl+"% • 🌡️ "+t+"°C ("+tempLabel(t)+") • 🧠 RAM "+ramPercent()+"%\nAktiv profil: "+prefs.getString("profile","Ingen"));button("Aktivera Gaming-profil",()->confirmProfile("Gaming",120000,false,true));addVolume("Spel / media",AudioManager.STREAM_MUSIC);button("Öppna Samsung Game Booster",()->game());button("Uppdatera dashboard",()->gamingPage());}
  void dexPage(){base("🖥️ DEX CENTER","Arbetsprofil för extern skärm",true);note("DeX-profilen använder 2 min timeout, adaptiv ljusstyrka och rotation. Upplösning och vissa DeX-val är Samsung-låsta.");button("Aktivera DeX-profil",()->confirmProfile("DeX",120000,true,true));button("Skärminställningar",()->open(Settings.ACTION_DISPLAY_SETTINGS));button("Ljudutgång",()->open(Settings.ACTION_SOUND_SETTINGS));}

  void cameraPage(){base("📸 CAMERA ASSISTANT PRO","Profiler för olika motiv",true);cameraProfile("Vardag","12 MP • Auto HDR • 1x/3x • snabbast och mest pålitligt.");cameraProfile("Natt","12 MP • håll still • nattläge på stilla motiv • undvik 200 MP i mörker.");cameraProfile("Barn / djur","12 MP • bra ljus • kortare slutartid i Pro-läge vid rörelse.");cameraProfile("Landskap","200 MP i bra dagsljus på stilla motiv; annars 12 MP för bättre HDR.");cameraProfile("10× zoom","Använd 10x i bra ljus och stöd telefonen.");cameraProfile("Måne","10x-optiken + stabilt stöd. Sänk exponeringen så månen inte blir utfrätt.");cameraProfile("Porträtt","3x ger ofta naturligare perspektiv än 1x.");cameraProfile("Video","4K60 för rörelse och detalj, 4K30 i svagare ljus, 8K bara vid behov.");button("Starta kameran",()->camera());}
  void cameraProfile(String name,String tip){section(name);note(tip);}
  void audioPage(){base("🔊 AUDIO CENTER","Volym direkt i Toolbox",true);addVolume("Media",AudioManager.STREAM_MUSIC);addVolume("Ringsignal",AudioManager.STREAM_RING);addVolume("Notiser",AudioManager.STREAM_NOTIFICATION);addVolume("Alarm",AudioManager.STREAM_ALARM);button("Dolby Atmos / ljudinställningar",()->open(Settings.ACTION_SOUND_SETTINGS));}

  void monitorPage(){base("🌡️ DEVICE MONITOR PRO","Livevärden + lokal temperaturhistorik",true);Intent i=batteryIntent();float t=i==null?0:i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0)/10f;String now=new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date());String hist=prefs.getString("temp_history","");hist=now+"  "+t+"°C"+(hist.isEmpty()?"":"\n"+hist);String[]lines=hist.split("\n");StringBuilder out=new StringBuilder();for(int x=0;x<Math.min(12,lines.length);x++)out.append(lines[x]).append(x<Math.min(12,lines.length)-1?"\n":"");prefs.edit().putString("temp_history",out.toString()).apply();section("Aktuellt");note(summary());section("Temperaturhistorik");note(out.toString());button("Uppdatera",()->monitorPage());button("Rensa temperaturhistorik",()->{prefs.edit().remove("temp_history").apply();monitorPage();});}
  void shizukuPage(){base("🔧 SHIZUKU TOOLS","API-status och behörighet",true);section("Status");note(shizukuStatus());if(shizukuBinderAlive()&&!hasShizukuPermission())button("Begär Shizuku-behörighet",()->requestShizuku());if(shizukuInstalled())button("Öppna Shizuku",()->openPkg("moe.shizuku.privileged.api"));note("Shizuku används för Smart Debloat. Android/Samsung kan fortfarande skydda vissa systempaket.");}

  void restorePage(){base("↩️ ÅTERSTÄLL & BACKUP","Återställ Debloat och spara Toolbox-inställningar",true);Set<String>s=prefs.getStringSet("disabled",new HashSet<>());note("Toolbox-spårade avaktiverade paket: "+s.size()+"\nAktiv profil: "+prefs.getString("profile","Ingen"));button("Återställ alla avaktiverade paket",()->confirmRestoreAll());section("Backupkod");String code=backupCode();note(code);button("Kopiera backupkod",()->{((android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("S23 Toolbox backup",code));toast("Backupkod kopierad");});button("Importera backupkod",()->importBackupDialog());}
  String backupCode(){Set<String>d=prefs.getStringSet("disabled",new HashSet<>());Set<String>p=new HashSet<>();for(String[]x:PACKAGES)if(isProtected(x[1]))p.add(x[1]);return"S23TB83|profile="+prefs.getString("profile","Ingen")+"|disabled="+join(d)+"|protected="+join(p);}
  String join(Set<String>s){StringBuilder b=new StringBuilder();for(String x:s){if(b.length()>0)b.append(",");b.append(x);}return b.toString();}
  void importBackupDialog(){final EditText e=new EditText(this);e.setHint("Klistra in backupkod");new AlertDialog.Builder(this).setTitle("Importera backup").setView(e).setNegativeButton("Avbryt",null).setPositiveButton("Importera",(d,w)->importBackup(e.getText().toString())).show();}
  void importBackup(String code){if(!code.startsWith("S23TB83|")){toast("Ogiltig backupkod");return;}try{String[]parts=code.split("\\|");SharedPreferences.Editor ed=prefs.edit();for(String part:parts){if(part.startsWith("profile="))ed.putString("profile",part.substring(8));if(part.startsWith("disabled=")){Set<String>s=new HashSet<>();String v=part.substring(9);if(!v.isEmpty())s.addAll(Arrays.asList(v.split(",")));ed.putStringSet("disabled",s);}if(part.startsWith("protected=")){for(String[]x:PACKAGES)ed.putBoolean("protect_"+x[1],false);String v=part.substring(10);if(!v.isEmpty())for(String p:v.split(","))ed.putBoolean("protect_"+p,true);}}ed.apply();toast("Backup importerad");log("Backup importerad");}catch(Exception ex){toast("Kunde inte importera backup");}}

  void updateCheckPage(){base("🔄 EFTER SYSTEMUPPDATERING","Kontrollera ändringar efter Samsung/Android-uppdatering",true);String current=Build.FINGERPRINT,previous=prefs.getString("fingerprint","");if(previous.isEmpty())note("Ingen tidigare systemversion sparad ännu.");else if(previous.equals(current))note("✅ Samma system-build som vid senaste kontrollen.");else note("🟡 System-build har ändrats. Granska Debloat och profiler igen.");int changed=0;for(String[]p:PACKAGES)if(prefs.getStringSet("disabled",new HashSet<>()).contains(p[1])&&isInstalled(p[1])&&isPackageEnabled(p[1]))changed++;section("Toolbox-kontroll");note(changed==0?"✅ Inga spårade Debloat-paket verkar ha återaktiverats.":"⚠️ "+changed+" tidigare avaktiverade paket verkar vara aktiva igen.");button("Öppna Smart Debloat",()->debloatPage(""));button("Spara denna systemversion som kontrollerad",()->{prefs.edit().putString("fingerprint",current).apply();toast("Systemversion sparad");log("Systemuppdateringskontroll sparad");});}

  void tipsPage(String query){base("📖 TIPS & TRICKS 2.0","Sökbar S23 Ultra-guide",true);EditText e=new EditText(this);e.setHint("Sök t.ex. kamera, DeX, S Pen…");e.setText(query);e.setTextColor(Color.WHITE);e.setHintTextColor(Color.GRAY);root.addView(e);button("Sök",()->tipsPage(e.getText().toString().trim()));tipBlock("Skärm","QHD+ för maximal skärpa. Adaptiv 120 Hz för bäst flyt. Mörkt läge minskar bländning kvällstid.",query);tipBlock("Kamera","12 MP för vardag/mörker. 200 MP i bra ljus. 3x/10x för tele. 4K60 för rörelse. Expert RAW för efterbehandling.",query);tipBlock("Batteri","30–60 sek timeout, adaptiv ljusstyrka och undvik tung belastning när mobilen redan är varm.",query);tipBlock("DeX","Extern skärm + tangentbord/mus. Använd fönsterläge och DeX-profilen för arbete.",query);tipBlock("S Pen","Screen off memo för snabba anteckningar. Air Command för smart markering, översättning och genvägar.",query);tipBlock("Säkerhet","Använd biometrik + stark PIN, håll systemet uppdaterat och ge bara Shizuku-behörighet till appar du litar på.",query);}
  void tipBlock(String title,String body,String q){String all=(title+" "+body).toLowerCase(Locale.ROOT);if(q.isEmpty()||all.contains(q.toLowerCase(Locale.ROOT))){section(title);note(body);}}
  void historyPage(){base("🕘 HISTORIK & SKYDD","Senaste Toolbox-åtgärder",true);section("Historik");note(prefs.getString("history","Ingen aktivitet ännu."));section("Skyddade paket");StringBuilder b=new StringBuilder();for(String[]p:PACKAGES)if(isProtected(p[1]))b.append("🔒 ").append(p[0]).append("\n");note(b.length()==0?"Inga paket skyddade.":b.toString());button("Rensa historik",()->{prefs.edit().remove("history").apply();historyPage();});}

  void writeSwitch(String label,String key,int on,int off){Switch sw=new Switch(this);sw.setText(label);sw.setTextColor(Color.WHITE);sw.setTextSize(16);try{sw.setChecked(Settings.System.getInt(getContentResolver(),key,on)==on);}catch(Exception ignored){}sw.setOnCheckedChangeListener((b,c)->{if(!ensureWrite()){b.setChecked(!c);return;}try{Settings.System.putInt(getContentResolver(),key,c?on:off);log(label+" = "+(c?"på":"av"));}catch(Exception e){toast("Kunde inte ändra inställningen");}});root.addView(sw);}
  void addVolume(String label,int stream){AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);section(label);SeekBar s=new SeekBar(this);s.setMax(am.getStreamMaxVolume(stream));s.setProgress(am.getStreamVolume(stream));s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int p,boolean f){if(f)am.setStreamVolume(stream,p,0);}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});root.addView(s);}

  boolean ensureWrite(){if(Build.VERSION.SDK_INT<23||Settings.System.canWrite(this))return true;toast("Tillåt S23 Ultra Toolbox att ändra systeminställningar");try{startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,Uri.parse("package:"+getPackageName())));}catch(Exception e){open(Settings.ACTION_SETTINGS);}return false;}
  boolean shizukuInstalled(){try{getPackageManager().getPackageInfo("moe.shizuku.privileged.api",0);return true;}catch(Exception e){return false;}}
  boolean shizukuBinderAlive(){try{return Shizuku.pingBinder();}catch(Throwable e){return false;}}
  boolean hasShizukuPermission(){try{return shizukuBinderAlive()&&Shizuku.checkSelfPermission()==PackageManager.PERMISSION_GRANTED;}catch(Throwable e){return false;}}
  String shizukuStatus(){if(!shizukuInstalled())return"❌ Shizuku är inte installerad";if(!shizukuBinderAlive())return"⚠️ Shizuku installerad men tjänsten är inte igång";return hasShizukuPermission()?"✅ Shizuku aktiv och behörig":"🟡 Shizuku aktiv • behörighet krävs";}
  void requestShizuku(){try{if(!shizukuBinderAlive()){toast("Starta Shizuku först");openPkg("moe.shizuku.privileged.api");return;}if(Shizuku.checkSelfPermission()==PackageManager.PERMISSION_GRANTED){toast("Shizuku är redan behörig");return;}Shizuku.requestPermission(SHIZUKU_REQ);}catch(Throwable e){toast("Kunde inte begära Shizuku-behörighet");}}
  boolean isInstalled(String p){try{getPackageManager().getPackageInfo(p,0);return true;}catch(Exception e){return false;}}
  boolean isPackageEnabled(String p){try{ApplicationInfo ai=getPackageManager().getApplicationInfo(p,0);int st=getPackageManager().getApplicationEnabledSetting(p);return ai.enabled&&st!=PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER&&st!=PackageManager.COMPONENT_ENABLED_STATE_DISABLED;}catch(Exception e){return false;}}
  boolean isProtected(String p){return prefs.getBoolean("protect_"+p,false);} void setProtected(String p,boolean b){prefs.edit().putBoolean("protect_"+p,b).apply();log((b?"Skyddade ":"Tog bort skydd från ")+p);}
  void log(String s){String old=prefs.getString("history","");String now=new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault()).format(new Date());String n=now+" • "+s+(old.isEmpty()?"":"\n"+old);String[]lines=n.split("\n");StringBuilder out=new StringBuilder();for(int i=0;i<Math.min(30,lines.length);i++)out.append(lines[i]).append(i<Math.min(30,lines.length)-1?"\n":"");prefs.edit().putString("history",out.toString()).apply();}
  void open(String a){try{startActivity(new Intent(a));}catch(Exception e){startActivity(new Intent(Settings.ACTION_SETTINGS));}}
  boolean openPkg(String p){try{Intent i=getPackageManager().getLaunchIntentForPackage(p);if(i!=null){startActivity(i);return true;}}catch(Exception ignored){}return false;}
  void game(){if(!openPkg("com.samsung.android.game.gamehome"))if(!openPkg("com.samsung.android.game.gametools"))open(Settings.ACTION_SETTINGS);}
  void camera(){try{startActivity(new Intent("android.media.action.STILL_IMAGE_CAMERA"));}catch(Exception e){open(Settings.ACTION_APPLICATION_SETTINGS);}}
  void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
