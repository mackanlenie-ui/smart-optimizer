package se.smartoptimizer.toolbox;

import android.os.Bundle;
import java.util.*;

public class MainActivity1101 extends MainActivity1100 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}

  static final String[] SAFE1101={
    "com.android.egg","com.android.dreams.phototable","com.android.traceur",
    "com.facebook.appmanager","com.facebook.services","com.facebook.system",
    "com.samsung.android.aremoji","com.samsung.android.aremojieditor",
    "com.samsung.android.app.camera.sticker.facearavatar.preload","com.samsung.android.stickercenter",
    "com.samsung.android.game.gametools","com.samsung.android.kidsinstaller","com.samsung.android.app.watchmanagerstub",
    "com.samsung.android.themestore","com.samsung.android.video","com.samsung.app.newtrim","com.samsung.storyservice",
    "com.microsoft.skydrive","com.microsoft.appmanager","com.hiya.star"
  };
  static final String[] OPTIONAL1101={
    "com.android.chrome","com.google.android.apps.bard","com.google.android.apps.tachyon","com.google.android.gm",
    "com.google.android.googlequicksearchbox","com.google.android.youtube","com.google.ar.core",
    "com.google.android.apps.accessibility.voiceaccess","com.google.android.apps.aiwallpapers",
    "com.samsung.android.app.interpreter","com.samsung.android.app.routines","com.samsung.android.app.taskedge",
    "com.samsung.android.bixby.agent","com.samsung.android.bixby.wakeup","com.samsung.android.bixbyvision.framework",
    "com.samsung.android.dynamiclock","com.samsung.android.easysetup","com.samsung.android.scloud",
    "com.samsung.android.smartsuggestions","com.samsung.android.smartcallprovider","com.samsung.android.mobileservice",
    "com.samsung.android.allshare.service.mediashare","com.samsung.android.audiomirroring",
    "com.samsung.android.accessory.budsunitemgr","com.samsung.android.spayfw","com.samsung.knox.securefolder",
    "com.sec.android.easyMover","com.sec.android.easyMover.Agent","com.sec.android.easyonehand","com.sec.android.app.magnifier",
    "com.touchtype.swiftkey"
  };
  static final String[] PROTECT1101={
    "com.android.calllogbackup","com.android.devicediagnostics","com.android.ons","com.android.providers.contactkeys",
    "com.android.providers.userdictionary","com.android.wallpaperbackup","com.android.dynsystem",
    "com.google.android.configupdater","com.google.android.setupwizard","com.google.android.onetimeinitializer",
    "com.google.android.partnersetup","com.google.android.safetycenter.resources","com.google.android.healthconnect.controller",
    "com.google.android.health.connect.backuprestore","com.google.android.tts","com.google.android.apps.messaging",
    "com.samsung.android.authfw","com.samsung.android.app.omcagent","com.samsung.android.dialer","com.samsung.android.messaging",
    "com.samsung.android.nfc","com.samsung.android.networkdiagnostic","com.samsung.android.net.wifi.wifiguider",
    "com.samsung.android.sdm.config","com.samsung.android.scpm","com.samsung.android.server.wifi.mobilewips",
    "com.sec.android.RilServiceModeApp","com.sec.android.app.SecSetupWizard","com.sec.android.app.camera",
    "com.sec.android.app.servicemodeapp","com.sec.android.desktopmode.uiservice","com.sec.android.gallery3d",
    "com.sec.android.iaft","com.sec.android.llmpolicy","com.sec.imslogger","com.sec.location.nfwlocationprivacy",
    "com.sec.modem.settings","com.sec.phone","com.sec.unifiedwfc","com.sec.usbsettings"
  };

  @Override void show(){
    base("S23 ULTRA TOOLBOX 11.1","S23-specifik säker debloat • analys först • återställningsbart",false);
    dashboard();
    card("📡 Nätverk & signal","Vimla, LTE/5G, signal, CA och IMS.",()->networkHub122());
    card("🔋 Batteri & optimering","Smart batteristatus, appanalys och säker scriptkontroll.",()->batteryHub122());
    card("🛡️ Appar & debloat","Smart Debloat + granskning av den stora S23/One UI-listan.",()->appsHub122());
    card("🌡️ Prestanda & temperatur","Thermal Watch, laddtest och Smart Advisor.",()->performanceHub122());
    card("📱 Telefon & jobb","GPS/Jobbläge, DeX och kamera.",()->phoneHub122());
    card("🧯 Recovery & diagnostik","Återställning, backup, historik och Shizuku.",()->recoveryHub122());
    card("🧰 Fler verktyg","Rapporter, historik, diagnostik och Shizuku-verktyg.",()->legacyHome122());
  }

  @Override void appsHub122(){
    base("🛡️ APPAR & DEBLOAT","S23-specifik analys • disable-user • skyddade systempaket",true);
    note(shStatus());
    note("11.1 använder den stora debloat-listan som referens – aldrig som blind masslista. Systemkritiska paket skyddas.");
    btn("🧠 S23 Debloat-granskning 1.0",()->debloatReview1101());
    btn("🛡️ Smart Debloat 8.0",()->smartDebloat());
    btn("📦 App Manager 2.0",()->appManager());
    btn("↩️ Recovery Center 2.0",()->recoveryCenter());
  }

  void debloatReview1101(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String all=runShellText("pm list packages");
      HashSet<String> installed=new HashSet<>();
      for(String l:all.split("\\n")) if(l.startsWith("package:")) installed.add(l.substring(8).trim());
      StringBuilder s=new StringBuilder("🧠 S23 DEBLOAT-GRANSKNING 1.0\n\n");
      appendGroup1101(s,"🟢 LÅGRISK – kan övervägas om funktionen inte används",SAFE1101,installed);
      appendGroup1101(s,"🟡 VALFRITT – beror på hur du använder telefonen",OPTIONAL1101,installed);
      appendGroup1101(s,"🔴 SKYDDAT – Toolbox rekommenderar inte debloat",PROTECT1101,installed);
      s.append("\n🛡️ Övriga paket i referenslistan klassas inte automatiskt som säkra. Paket som AI Core, Knox, IMS/telefon, NFC, Wi-Fi, setup, säkerhet, Health/credentials och systemproviders kräver separat bedömning.\n\n");
      s.append("Toolbox använder disable-user för valda lågriskpaket i stället för pm uninstall. Ingen ändring gjord av denna analys.");
      String x=s.toString(); runOnUiThread(()->dialog118("S23 Debloat-granskning",x));
    }).start();
  }

  void appendGroup1101(StringBuilder s,String title,String[] arr,Set<String> installed){
    s.append(title).append("\n"); int n=0;
    for(String p:arr) if(installed.contains(p)){s.append("• ").append(p).append("\n");n++;}
    if(n==0)s.append("• Inga installerade träffar\n");
    s.append("\n");
  }
}
