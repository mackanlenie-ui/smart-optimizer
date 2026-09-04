package se.smartoptimizer.toolbox;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity90 extends MainActivity89 {
  Runnable liveRun; boolean liveActive=false; final int LIVE_MS=2000;

  @Override void show(){
    stopLive();
    base("S23 ULTRA TOOLBOX 9.0","Control Center • Live Monitor • Health Check • Change Watch",false);
    dashboard90();
    if(p.getBoolean("update",false)) card("🆕 Efter Samsung-uppdatering","Kontrollera återaktiverade paket och appförändringar.",()->afterUpdate());
    card("📡 Live Performance Monitor","Temperatur, RAM, batteri och laddning i realtid.",()->liveMonitor());
    card("🚨 Thermal Alerts","Egna temperaturgränser för varning och hög varning.",()->thermalAlerts());
    card("🩺 One-tap Health Check","Kör signering, Shizuku, temperatur, lagring, RAM och batteri på en gång.",()->healthCheck());
    card("🧠 Smart Advisor 6.0","Baslinje + senaste hälsokontroll + tydliga prioriteringar.",()->advisor90());
    card("🛡️ Debloat Safety Scanner","Granska risk och beroenden innan något avaktiveras.",()->debloatSafety());
    card("📦 App Change Watch","Ser nya, borttagna och uppdaterade appar sedan senaste snapshot.",()->appChangeWatch());
    card("💾 Storage Analyzer","Visar största installerade APK-filerna och snabbvägar till lagring.",()->storageAnalyzer());
    card("🔋 Charging Efficiency","Jämför sparade laddtester mot temperaturökning.",()->chargingEfficiency());
    card("⚙️ Performance Profiles 2.0","Standardprofiler + två egna profilplatser.",()->profiles90());
    card("🚗 GPS-läge 2.0","Bil/GPS-profil, snabbstart av GPS-appar och återställning.",()->gpsMode());
    card("🖥️ DeX Monitor","Live status när extern skärm används.",()->dexMonitor());
    card("🔐 Toolbox Integrity Check","Version, signering, Shizuku och grundstatus.",()->integrityCheck());
    card("🛡️ Smart Debloat 7.1","Samsung • Google • Meta • Recovery.",()->debloat());
    card("📦 App Manager 2.1","Sök, filtrera och se nyligen uppdaterade appar.",()->apps89());
    card("🌡️ Thermal Watch 4.1","Historik och temperaturgraf.",()->thermal89());
    card("🔋 Battery Health","Rapporterade batterivärden utan påhittad hälsoprocent.",()->batteryHealth());
    card("⚡ Charging Test Pro 2.0","Namngivna laddtester.",()->battery89());
    card("🧯 Recovery Center 2.1","Ångra och återställ Toolbox-ändringar.",()->recovery());
    card("💾 Backup / Restore","Exportera och importera Toolbox-data.",()->backupPage());
    card("🔧 Shizuku Tools",shStatus(),()->shPage());
    card("📸 Camera Guide 5.2","Praktiska S23 Ultra-råd.",()->camera89());
    card("🕘 Historik","Senaste Toolbox-åtgärder.",()->history());
  }

  void dashboard90(){
    LinearLayout a=new LinearLayout(this);a.setOrientation(LinearLayout.HORIZONTAL);a.addView(chip("🌡️ "+fmt(temp())+"°C\n"+tname()));a.addView(chip("🔋 "+level()+"%\n"+charge()));root.addView(a);
    LinearLayout b=new LinearLayout(this);b.setOrientation(LinearLayout.HORIZONTAL);b.addView(chip("🧠 RAM "+ram()+"%\n"+(ram()>90?"Hög":"Normal")));b.addView(chip("💾 "+free()+" GB\nledigt"));root.addView(b);
    LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.HORIZONTAL);c.addView(chip("🔧 Shizuku\n"+(shOk()?"Behörig":"Ej redo")));c.addView(chip("🔐 Signering\n"+(signatureOk()?"Verifierad":"Kontrollera")));root.addView(c);
    String last=p.getString("health_last","Ingen kontroll ännu");note("Health "+score()+"/100 • Profil: "+p.getString("profile","Standard")+"\nSenaste Health Check: "+last);
  }

  void liveMonitor(){
    stopLive(); base("📡 LIVE PERFORMANCE MONITOR","Uppdateras varannan sekund",true);
    final TextView v=text("Startar…",18,true);v.setPadding(10,12,10,12);v.setBackgroundColor(CARD);root.addView(v);
    btn("Stoppa monitor",()->{stopLive();show();});
    liveActive=true;liveRun=new Runnable(){public void run(){if(!liveActive)return;float t=temp();long u=ua();double w=volt()/1000.0*Math.abs(u)/1000000.0;String ext=externalDisplays()>1?" • DeX/extern skärm": "";v.setText("🌡️ "+fmt(t)+"°C  •  🧠 RAM "+ram()+"%\n🔋 "+level()+"%  •  "+charge()+"  •  "+String.format(Locale.ROOT,"%.2f W",w)+ext+"\n💾 "+free()+" GB ledigt");checkThermalAlert(t);h.postDelayed(this,LIVE_MS);}};h.post(liveRun);
  }
  void stopLive(){liveActive=false;if(liveRun!=null){h.removeCallbacks(liveRun);liveRun=null;}}
  int warnTemp(){return p.getInt("thermal_warn",40);}int hotTemp(){return p.getInt("thermal_hot",43);} 
  void checkThermalAlert(float t){long now=System.currentTimeMillis(),last=p.getLong("thermal_alert_time",0);if(now-last<60000)return;if(t>=hotTemp()){p.edit().putLong("thermal_alert_time",now).apply();toast("🔥 Hög temperatur: "+fmt(t)+"°C");}else if(t>=warnTemp()){p.edit().putLong("thermal_alert_time",now).apply();toast("🌡️ Temperaturvarning: "+fmt(t)+"°C");}}
  void thermalAlerts(){base("🚨 THERMAL ALERTS","Varningar används i Live Monitor",true);note("Nuvarande: varning "+warnTemp()+"°C • hög varning "+hotTemp()+"°C");LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);EditText w=new EditText(this);w.setInputType(2);w.setHint("40");w.setTextColor(Color.WHITE);EditText x=new EditText(this);x.setInputType(2);x.setHint("43");x.setTextColor(Color.WHITE);row.addView(w,new LinearLayout.LayoutParams(0,-2,1));row.addView(x,new LinearLayout.LayoutParams(0,-2,1));root.addView(row);btn("Spara gränser",()->{try{int a=w.getText().toString().trim().isEmpty()?warnTemp():Integer.parseInt(w.getText().toString());int b=x.getText().toString().trim().isEmpty()?hotTemp():Integer.parseInt(x.getText().toString());if(a<30||b<=a||b>55){toast("Välj rimliga gränser");return;}p.edit().putInt("thermal_warn",a).putInt("thermal_hot",b).apply();log("Thermal Alerts: "+a+"/"+b+"°C");thermalAlerts();}catch(Exception e){toast("Ogiltigt värde");}});}

  void healthCheck(){base("🩺 ONE-TAP HEALTH CHECK","Samlad kontroll av telefon och Toolbox",true);int ok=0,total=7;boolean sig=signatureOk(),sh=shOk(),tp=temp()<43,st=free()>=10,rm=ram()<95,bat=batteryHealthCode()!=BatteryManager.BATTERY_HEALTH_OVERHEAT,perm=Settings.System.canWrite(this);if(sig)ok++;if(sh)ok++;if(tp)ok++;if(st)ok++;if(rm)ok++;if(bat)ok++;if(perm)ok++;testLine("Signering",sig,sig?"Verifierad":"Avvikelse");testLine("Shizuku",sh,sh?"Redo":"Inte redo");testLine("Temperatur",tp,fmt(temp())+"°C");testLine("Lagring",st,free()+" GB ledigt");testLine("RAM",rm,ram()+"%");testLine("Batteristatus",bat,batteryHealthName());testLine("Ändra systeminställningar",perm,perm?"Tillåtet":"Behörighet saknas");String result=ok+"/"+total+" godkända";p.edit().putString("health_last",new SimpleDateFormat("MM-dd HH:mm",Locale.getDefault()).format(new Date())+" • "+result).apply();log("Health Check: "+result);sec(result);}

  void advisor90(){base("🧠 SMART ADVISOR 6.0","Baslinje + Health Check + prioritering",true);float bt=p.getFloat("base_temp",temp()),br=p.getFloat("base_ram",ram());note("Nu: "+fmt(temp())+"°C • RAM "+ram()+"% • "+free()+" GB ledigt\nBaslinje: "+fmt(bt)+"°C • RAM "+fmt(br)+"%\nHealth Check: "+p.getString("health_last","Inte körd"));sec("Prioriterade råd");int n=0;if(temp()>=hotTemp())note(++n+". 🔥 Hög temperatur. Pausa tung belastning och laddning tills telefonen svalnat.");else if(temp()>=warnTemp())note(++n+". 🌡️ Telefonen är varmare än din valda varningsgräns.");if(!signatureOk())note(++n+". 🔐 Signeringen avviker från Toolbox fasta certifikat.");if(!shOk())note(++n+". 🔧 Starta Shizuku för avancerade funktioner.");if(free()<20)note(++n+". 💾 Lagringen börjar bli låg. Öppna Storage Analyzer.");if(ram()>br+18)note(++n+". 🧠 RAM ligger tydligt över din baslinje.");if(level()<20&&status()!=BatteryManager.BATTERY_STATUS_CHARGING)note(++n+". 🔋 Ladda före ett längre GPS/DeX-pass.");if(n==0)note("✅ Inga viktiga åtgärder just nu.");btn("Kör Health Check",()->healthCheck());btn("Storage Analyzer",()->storageAnalyzer());log("Advisor 6.0: "+n+" råd");}

  void debloatSafety(){base("🛡️ DEBLOAT SAFETY SCANNER","Granska varje känd kandidat innan ändring",true);int n=0;for(String[]a:PK)if(installed(a[1])&&enabled(a[1])){n++;sec(a[0]+" • "+a[2]);note(a[4]+"\nPaket: "+a[1]+"\nBedömning: "+(a[2].equals("Säker")?"Låg risk i Toolbox-listan":a[2].equals("Valfritt")?"Beror på om du använder funktionen":"Kontrollera noga – funktioner kan påverkas"));}if(n==0)note("✅ Inga aktiva kandidater från Toolbox-listan.");btn("Öppna Smart Debloat",()->debloat());}

  void appChangeWatch(){base("📦 APP CHANGE WATCH","Jämför installerade appar med föregående snapshot",true);Map<String,String> now=packageSnapshot();String oldRaw=p.getString("pkg_snapshot","");Map<String,String> old=parseSnapshot(oldRaw);if(old.isEmpty()){note("Ingen tidigare snapshot. Skapa en nu för framtida jämförelser.");btn("Skapa första snapshot",()->savePackageSnapshot(now));return;}ArrayList<String> added=new ArrayList<>(),removed=new ArrayList<>(),updated=new ArrayList<>();for(String k:now.keySet()){if(!old.containsKey(k))added.add(k);else if(!Objects.equals(now.get(k),old.get(k)))updated.add(k);}for(String k:old.keySet())if(!now.containsKey(k))removed.add(k);sec("Resultat");note("Nya: "+added.size()+" • Borttagna: "+removed.size()+" • Uppdaterade: "+updated.size());if(!added.isEmpty()){sec("Nya");note(joinLimited(added,30));}if(!removed.isEmpty()){sec("Borttagna");note(joinLimited(removed,30));}if(!updated.isEmpty()){sec("Uppdaterade");note(joinLimited(updated,30));}btn("Spara nuläget som ny snapshot",()->savePackageSnapshot(now));}
  Map<String,String> packageSnapshot(){Map<String,String> m=new TreeMap<>();PackageManager pm=getPackageManager();for(ApplicationInfo a:pm.getInstalledApplications(0))try{PackageInfo pi=pm.getPackageInfo(a.packageName,0);m.put(a.packageName,pi.versionName+"|"+pi.lastUpdateTime);}catch(Exception e){}return m;}
  Map<String,String> parseSnapshot(String raw){Map<String,String>m=new HashMap<>();for(String l:raw.split("\n")){int i=l.indexOf('=');if(i>0)m.put(l.substring(0,i),l.substring(i+1));}return m;}
  void savePackageSnapshot(Map<String,String> m){StringBuilder b=new StringBuilder();for(Map.Entry<String,String>e:m.entrySet())b.append(e.getKey()).append('=').append(e.getValue()).append('\n');p.edit().putString("pkg_snapshot",b.toString()).putLong("pkg_snapshot_time",System.currentTimeMillis()).apply();log("App snapshot sparad: "+m.size()+" paket");toast("Snapshot sparad");appChangeWatch();}
  String joinLimited(List<String>x,int max){StringBuilder b=new StringBuilder();for(int i=0;i<Math.min(max,x.size());i++)b.append("• ").append(x.get(i)).append('\n');if(x.size()>max)b.append("… +").append(x.size()-max).append(" till");return b.toString().trim();}

  void storageAnalyzer(){base("💾 STORAGE ANALYZER","Största installerade APK-filerna",true);note("Android låter inte en vanlig app läsa all privat appdata. Därför visar Toolbox installations-APK-storlek, inte total appdata/cache.");PackageManager pm=getPackageManager();ArrayList<ApplicationInfo> list=new ArrayList<>(pm.getInstalledApplications(0));list.sort((a,b)->Long.compare(apkSize(b),apkSize(a)));int n=Math.min(40,list.size());String[]rows=new String[n];for(int i=0;i<n;i++){ApplicationInfo a=list.get(i);rows[i]=pm.getApplicationLabel(a)+" • "+human(apkSize(a))+"\n"+a.packageName;}btn("Visa 40 största APK-filer",()->new AlertDialog.Builder(this).setTitle("Största APK-filer").setItems(rows,(d,i)->inspect(list.get(i).packageName)).setNegativeButton("Stäng",null).show());btn("Öppna lagringsinställningar",()->open(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));note("Ledigt utrymme: "+free()+" GB");}
  long apkSize(ApplicationInfo a){try{return new File(a.sourceDir).length();}catch(Exception e){return 0;}}String human(long b){if(b>1073741824)return String.format(Locale.ROOT,"%.1f GB",b/1073741824.0);return String.format(Locale.ROOT,"%.1f MB",b/1048576.0);}

  void chargingEfficiency(){base("🔋 CHARGING EFFICIENCY","Jämför laddtester",true);String s=p.getString("charge_tests","");if(s.isEmpty()){note("Inga sparade tester ännu.");btn("Starta Charging Test Pro",()->battery89());return;}note("Högre effekt är inte alltid bättre om temperaturen stiger mycket. Toolbox visar därför testerna tillsammans med max-temperatur.");sec("Sparade tester");note(s);btn("Nytt 5 min jämförelsetest",()->askChargeName(60));btn("Battery Health",()->batteryHealth());}

  void profiles90(){base("⚙️ PERFORMANCE PROFILES 2.0","Standardprofiler + egna profilplatser",true);note("Aktiv profil: "+p.getString("profile","Standard"));btn("Balanserad",()->profile("Balanserad",60000,true,true,false));btn("Batteri",()->profile("Batteri",30000,true,false,false));btn("Gaming",()->profile("Gaming",120000,false,true,false));btn("Bil/GPS",()->profile("Bil/GPS",600000,true,true,true));btn("DeX",()->profile("DeX",600000,true,true,false));sec("Egna profiler");btn("Spara aktuella inställningar i Egen 1",()->saveCustomProfile(1));btn("Aktivera Egen 1",()->applyCustomProfile(1));btn("Spara aktuella inställningar i Egen 2",()->saveCustomProfile(2));btn("Aktivera Egen 2",()->applyCustomProfile(2));btn("Återställ tidigare läge",()->restoreProfile());}
  void saveCustomProfile(int slot){try{p.edit().putInt("c"+slot+"_timeout",Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,60000)).putInt("c"+slot+"_bright",Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,1)).putInt("c"+slot+"_rotate",Settings.System.getInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,1)).putBoolean("c"+slot+"_saved",true).apply();log("Egen profil "+slot+" sparad");toast("Egen "+slot+" sparad");}catch(Exception e){toast("Kunde inte spara profilen");}}
  void applyCustomProfile(int slot){if(!p.getBoolean("c"+slot+"_saved",false)){toast("Egen "+slot+" är inte sparad");return;}if(!writeOk())return;remember();try{Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,p.getInt("c"+slot+"_timeout",60000));Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,p.getInt("c"+slot+"_bright",1));Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,p.getInt("c"+slot+"_rotate",1));p.edit().putString("profile","Egen "+slot).apply();log("Egen profil "+slot+" aktiverad");toast("Egen "+slot+" aktiverad");}catch(Exception e){toast("Kunde inte aktivera profilen");}}

  void gpsMode(){base("🚗 GPS-LÄGE 2.0","Bil/GPS-profil + snabbstart + återställning",true);note("Aktiv profil: "+p.getString("profile","Standard"));btn("Aktivera Bil/GPS-läge",()->profile("Bil/GPS",600000,true,true,true));String[][] apps={{"Google Maps","com.google.android.apps.maps"},{"Waze","com.waze"},{"Android Auto","com.google.android.projection.gearhead"},{"Rutt GPS","se.ruttgps"},{"GPS Ruttinspelare","se.gpsruttinspelare"}};sec("GPS-appar");int n=0;for(String[]a:apps)if(installed(a[1])){n++;btn("Öppna "+a[0],()->openPkg(a[1]));}if(n==0)note("Inga av de vanliga GPS-apparna hittades via kända paketnamn.");btn("Platsinställningar",()->open(Settings.ACTION_LOCATION_SOURCE_SETTINGS));btn("Återställ tidigare läge",()->restoreProfile());}

  void dexMonitor(){stopLive();base("🖥️ DEX MONITOR","Live status för extern skärm",true);final TextView v=text("Startar…",18,true);v.setPadding(10,12,10,12);v.setBackgroundColor(CARD);root.addView(v);btn("Aktivera DeX-profil",()->profile("DeX",600000,true,true,false));btn("Stoppa och tillbaka",()->{stopLive();show();});liveActive=true;liveRun=new Runnable(){public void run(){if(!liveActive)return;int d=externalDisplays();v.setText((d>1?"✅ Extern skärm upptäckt":"⚪ Ingen extern skärm")+"\nSkärmar: "+d+" • 🌡️ "+fmt(temp())+"°C • 🧠 RAM "+ram()+"%\n🔋 "+level()+"% • "+charge()+" • 💾 "+free()+" GB");h.postDelayed(this,LIVE_MS);}};h.post(liveRun);}
  int externalDisplays(){try{android.hardware.display.DisplayManager d=(android.hardware.display.DisplayManager)getSystemService(DISPLAY_SERVICE);return d==null?1:d.getDisplays().length;}catch(Exception e){return 1;}}

  void integrityCheck(){base("🔐 TOOLBOX INTEGRITY CHECK","Kontrollerar appens grundintegritet",true);boolean sig=signatureOk();testLine("Version",true,"9.0 (900)");testLine("Fast signeringscertifikat",sig,signatureFingerprint());testLine("Shizuku-provider",shInstalled(),shStatus());testLine("Systeminställningsbehörighet",Settings.System.canWrite(this),Settings.System.canWrite(this)?"Tillåten":"Saknas");testLine("Lokal data",p!=null,"SharedPreferences OK");note(sig?"✅ Signeringen matchar Toolbox fasta releasecertifikat.":"⚠️ Signeringen matchar inte det förväntade certifikatet. Installera inte framtida uppdateringar ovanpå denna version innan det är utrett.");}

  @Override protected void onDestroy(){stopLive();super.onDestroy();}
}
