package se.smartoptimizer.toolbox;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.provider.Settings;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity100 extends MainActivity95 {
  @Override void show(){
    stopLive();
    base("S23 ULTRA TOOLBOX 10.0","Smart Control Center • S23 Ultra • Android 16",false);
    dashboard90();
    smartSummary100();
    card("🧠 Smart Advisor 7.0","Prioriterar bara sådant som faktiskt behöver din uppmärksamhet.",()->advisor100());
    card("🚗 Jobb / GPS-läge 3.0","Förkontroll inför navigation, GPS-profil och snabbstart.",()->jobGps100());
    card("🛡️ Smart Debloat 8.0","Säkerhetsnivåer, effektiv status och Android 16 Recovery.",()->debloat());
    card("🧯 Recovery Snapshot","Spara nuläget för Toolbox-paketen före större ändringar.",()->recoverySnapshot100());
    card("📦 App Watch 2.0","Kontrollera förändringar efter Samsung- och Play-uppdateringar.",()->appWatch100());
    card("🌡️ Thermal Intelligence","Temperaturtrend och varningsnivåer.",()->thermalIntelligence100());
    card("🔋 Battery & Charging Lab 3.0","Batteristatus, laddtester och effektivitet samlat.",()->batteryLab100());
    card("🖥️ DeX Center 3.0","Extern skärm, temperatur, RAM och snabbkontroller.",()->dex100());
    card("🔧 Toolbox Self-Test 2.0","Kontrollera viktiga funktioner efter systemuppdatering.",()->selfTest100());
    card("📸 Camera Guide 6.0","Snabbguide för vardag, natt, zoom, rörelse och video.",()->camera100());
    sec("Fler verktyg");
    card("📡 Live Performance Monitor","Live temperatur, RAM, batteri och laddning.",()->liveMonitor());
    card("💾 Storage Analyzer","Största installerade APK-filer och lagringsgenvägar.",()->storageAnalyzer());
    card("⚙️ Performance Profiles 2.0","Standardprofiler och egna profilplatser.",()->profiles90());
    card("📦 App Manager 2.1","Sök och inspektera installerade appar.",()->apps89());
    card("💾 Backup / Restore","Exportera och importera Toolbox-data.",()->backupPage());
    card("🔧 Shizuku Tools",shStatus(),()->shPage());
    card("🕘 Historik","Senaste Toolbox-åtgärder.",()->history());
  }

  void smartSummary100(){
    int issues=issueCount100();
    note((issues==0?"✅ Telefonstatus: bra":"⚠️ "+issues+" sak"+(issues==1?"":"er")+" att kontrollera")+"\n🌡️ "+fmt(temp())+"°C • 🧠 RAM "+ram()+"% • 💾 "+free()+" GB • 🔋 "+level()+"%\nProfil: "+p.getString("profile","Standard")+" • "+(shOk()?"Shizuku redo":"Shizuku ej redo"));
  }
  int issueCount100(){int n=0;if(temp()>=warnTemp())n++;if(ram()>=95)n++;if(free()<10)n++;if(!shOk())n++;if(!signatureOk())n++;return n;}

  void advisor100(){
    base("🧠 SMART ADVISOR 7.0","Prioriterad S23 Ultra-kontroll",true);int n=0;
    if(!signatureOk())note(++n+". 🔐 Toolbox-signeringen behöver kontrolleras.");
    if(!shOk())note(++n+". 🔧 Starta Shizuku för avancerade funktioner.");
    if(temp()>=hotTemp())note(++n+". 🔥 Temperaturen är hög: "+fmt(temp())+"°C. Minska belastning/laddning en stund.");else if(temp()>=warnTemp())note(++n+". 🌡️ Temperaturen är över din varningsnivå: "+fmt(temp())+"°C.");
    if(free()<10)note(++n+". 💾 Bara "+free()+" GB ledigt. Kör Storage Analyzer.");
    if(ram()>=95)note(++n+". 🧠 RAM-användningen är mycket hög: "+ram()+"%.");
    if(level()<20 && status()!=BatteryManager.BATTERY_STATUS_CHARGING)note(++n+". 🔋 Låg batterinivå inför längre GPS/DeX-pass.");
    int disabled=disabledKnown100();if(disabled>0)note(++n+". 🛡️ "+disabled+" Toolbox-paket är avaktiverade. Recovery Assistant kan återställa dem.");
    if(n==0)note("✅ Inga viktiga åtgärder just nu.");
    btn("Kör full Health Check",()->healthCheck());btn("Recovery Assistant",()->recoveryAssistant95());btn("Storage Analyzer",()->storageAnalyzer());
  }
  int disabledKnown100(){int n=0;for(String[]a:PK)if(installed(a[1])&&!enabled(a[1]))n++;return n;}

  void jobGps100(){
    base("🚗 JOBB / GPS-LÄGE 3.0","Förkontroll inför navigation",true);
    boolean tempOk=temp()<hotTemp(),batOk=level()>=25||status()==BatteryManager.BATTERY_STATUS_CHARGING,storageOk=free()>=5;
    note("GPS-pass: "+(tempOk&&batOk&&storageOk?"✅ Redo":"⚠️ Kontrollera nedan")+"\n🌡️ "+fmt(temp())+"°C "+(tempOk?"✓":"!")+" • 🔋 "+level()+"% "+(batOk?"✓":"!")+" • 💾 "+free()+" GB "+(storageOk?"✓":"!"));
    btn("Aktivera Bil/GPS-profil",()->apply("Bil / GPS",300000,1,180));
    btn("Starta GPS Ruttinspelare",()->launchAny90(new String[]{"se.gpsruttinspelare","se.gps.ruttinspelare"},"GPS Ruttinspelare"));
    btn("Starta Google Maps",()->launchAny90(new String[]{"com.google.android.apps.maps"},"Google Maps"));
    btn("Platsinställningar",()->open(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    btn("Återställ föregående profil",()->restorePrev90());
  }

  @Override void debloat(){
    base("🛡️ SMART DEBLOAT 8.0","Säker • Valfri • Försiktig • Recovery",true);note(shStatus());
    note("Toolbox verifierar varje ändring. Om Android 16 blockerar automatisk återställning öppnas rätt appinställning i stället.");
    btn("📸 Spara Recovery Snapshot först",()->saveRecoverySnapshot100());btn("🧯 Recovery Assistant",()->recoveryAssistant95());btn("✨ Rekommenderad säker batch",()->recommended());
    String last="";for(String[]a:PK)if(installed(a[1])){if(!a[3].equals(last)){sec(a[3]);last=a[3];}boolean en=enabled(a[1]);String risk=a[2].equals("Försiktig")?"⚠️ Försiktig":a[2].equals("Säker")?"✅ Säker":"ℹ️ Valfri";note(a[0]+" • "+risk+"\n"+a[4]+"\n"+a[1]+"\nStatus: "+effectiveState95(a[1]));if(en)btn("Avaktivera "+a[0],()->ask(a));else if(p.getBoolean("manual_restore_"+a[1],false))btn("Återställ "+a[0]+" i appinställningar",()->openAppSettings94(a[1]));else btn("Återställ "+a[0],()->shell("pm enable --user 0 "+a[1],a[1],false));}
  }

  void recoverySnapshot100(){base("📸 RECOVERY SNAPSHOT","Tillstånd för Toolbox-paketen",true);String s=p.getString("recovery_snapshot100","");if(s.isEmpty())note("Ingen snapshot sparad ännu.");else note("Senast sparad: "+p.getString("recovery_snapshot_time100","okänd tid")+"\n\n"+snapshotSummary100(s));btn("Spara nytt nuläge",()->saveRecoverySnapshot100());btn("Öppna Recovery Assistant",()->recoveryAssistant95());}
  void saveRecoverySnapshot100(){StringBuilder b=new StringBuilder();int n=0;for(String[]a:PK)if(installed(a[1])){b.append(a[1]).append('=').append(enabled(a[1])?"1":"0").append('\n');n++;}String t=new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault()).format(new Date());p.edit().putString("recovery_snapshot100",b.toString()).putString("recovery_snapshot_time100",t).apply();log("Recovery Snapshot: "+n+" paket");toast("Recovery Snapshot sparad");}
  String snapshotSummary100(String s){int on=0,off=0;for(String l:s.split("\n")){if(l.endsWith("=1"))on++;else if(l.endsWith("=0"))off++;}return "Aktiva: "+on+" • Avaktiverade: "+off+"\nSnapshot används som referens vid återställning.";}

  void appWatch100(){base("📦 APP WATCH 2.0","Efter Samsung / Play-systemuppdateringar",true);note("Jämför installerade paket och versionsdata mot föregående snapshot. Bra att köra efter större uppdateringar.");btn("Kör App Change Watch",()->appChangeWatch());btn("Efter Samsung-uppdatering",()->afterUpdate());btn("Spara Recovery Snapshot",()->saveRecoverySnapshot100());}

  void thermalIntelligence100(){base("🌡️ THERMAL INTELLIGENCE","Trend • gränser • live",true);float now=temp(),baseT=p.getFloat("thermal_last100",now);String trend=now>baseT+1.5?"↗️ Stigande":now<baseT-1.5?"↘️ Sjunkande":"➡️ Stabil";note("Nu: "+fmt(now)+"°C\nSenaste referens: "+fmt(baseT)+"°C\nTrend: "+trend+"\nVarning: "+warnTemp()+"°C • Hög: "+hotTemp()+"°C");p.edit().putFloat("thermal_last100",now).apply();btn("Live Monitor",()->liveMonitor());btn("Ändra temperaturgränser",()->thermalAlerts());btn("Thermal Watch historik",()->thermal89());}

  void batteryLab100(){base("🔋 BATTERY & CHARGING LAB 3.0","Batteri • laddning • temperatur",true);note("Batteri: "+level()+"% • "+batteryHealthName()+"\nTemperatur: "+fmt(temp())+"°C • "+charge());btn("Battery Health",()->batteryHealth());btn("Charging Test Pro",()->battery89());btn("Charging Efficiency",()->chargingEfficiency());}

  void dex100(){base("🖥️ DEX CENTER 3.0","Extern skärm • prestanda • snabbkontroller",true);note("Skärmar: "+externalDisplays()+"\n🌡️ "+fmt(temp())+"°C • 🧠 RAM "+ram()+"% • 🔋 "+level()+"%\n"+(externalDisplays()>1?"✅ Extern skärm upptäckt":"ℹ️ Ingen extern skärm upptäckt"));btn("Live Performance Monitor",()->liveMonitor());btn("Performance Profiles",()->profiles90());btn("Display-inställningar",()->open(Settings.ACTION_DISPLAY_SETTINGS));}

  void selfTest100(){base("🔧 TOOLBOX SELF-TEST 2.0","Efter Android / Samsung-uppdateringar",true);boolean[]ok={signatureOk(),shOk(),Settings.System.canWrite(this),temp()<50,free()>2};String[]n={"Signering","Shizuku","Systeminställningar","Temperatursensor","Lagring"};int pass=0;for(int i=0;i<ok.length;i++){if(ok[i])pass++;testLine(n[i],ok[i],ok[i]?"OK":"Kontrollera");}sec(pass+"/"+ok.length+" godkända");btn("Kör One-tap Health Check",()->healthCheck());btn("Toolbox Integrity Check",()->integrityCheck());}

  void camera100(){base("📸 CAMERA GUIDE 6.0","Praktiska S23 Ultra-inställningar",true);sec("Vardag / människor");note("12 MP är bästa standardläget. Använd 3x för naturligare porträtt och håll HDR/automatiken aktiv när ljuset varierar.");sec("Rörelse / djur");note("Prioritera bra ljus, 12 MP och kortare avstånd. Undvik 200 MP för snabba motiv eftersom det kräver mer ljus och bearbetning.");sec("Natt");note("Håll mobilen stabil och använd nattläge när motivet är stilla. För rörliga personer är vanligt fotoläge ofta bättre.");sec("Zoom");note("3x är starkt för porträtt. 10x fungerar bäst i gott ljus; stöd mobilen för finare detaljer.");sec("200 MP");note("Använd främst i bra dagsljus när du vill kunna beskära efteråt. För vardagsbilder ger 12 MP snabbare och jämnare resultat.");sec("Video");note("4K/60 för rörelse och bra ljus. 4K/30 är ett bra allroundval och lättare för temperatur och lagring.");btn("Öppna kameran",()->{try{startActivity(new Intent("android.media.action.STILL_IMAGE_CAMERA"));}catch(Exception e){toast("Kunde inte öppna kameran");}});}
}
