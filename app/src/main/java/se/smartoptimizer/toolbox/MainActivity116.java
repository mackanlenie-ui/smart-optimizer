package se.smartoptimizer.toolbox;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity116 extends MainActivity114 {
  private static final String K_WIFI_SCAN="wifi_scan_always_enabled";
  private static final String K_BLE_SCAN="ble_scan_always_enabled";
  private static final String K_LOC_THROTTLE="location_background_throttle_interval_ms";

  @Override public void onCreate(Bundle b){ super.onCreate(b); }

  @Override void show(){
    super.show();
    try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.16");}catch(Throwable ignored){}
    card("🔋 Batterioptimering 1.0","Säkra Kryptonian-idéer • exakt snapshot • full återställning.",()->batteryOptimizerPage());
  }

  void batteryOptimizerPage(){
    base("🔋 BATTERIOPTIMERING 1.0","S23 Ultra • säkert • mätbart • återställningsbart",true);
    note(shStatus());
    sec("Säker profil");
    note("Den säkra profilen stänger av ständig Wi‑Fi- och Bluetooth-skanning i bakgrunden. Toolbox sparar telefonens exakta värden innan första ändringen och kan återställa dem senare.\n\nVi ändrar INTE GOS, modem/IMS, Device Idle-konstanter eller tvingar force-idle.");
    btn("🔍 Läs aktuell batteristatus",()->readBatteryOptimizerStatus());
    btn("✨ Aktivera säker batteriprofil",()->confirmSafeBatteryProfile());
    btn("↩️ Återställ säker batteriprofil",()->restoreSafeBatteryProfile());

    sec("Extra – bakgrundsplats");
    note("Valfritt test: begränsar hur ofta bakgrundsappar får nya platsuppdateringar till minst 10 minuter. Kan spara lite batteri men kan påverka appar som behöver tät bakgrundspositionering. Ingår INTE i säker profil.");
    btn("🧪 Testa 10 min bakgrundsplats",()->confirmLocationThrottle());
    btn("↩️ Återställ bakgrundsplats",()->restoreLocationThrottle());

    sec("Det vi medvetet inte använder");
    note("• Ingen avstängning av Samsung GOS\n• Ingen dumpsys deviceidle force-idle\n• Inga aggressiva Device Idle/Doze-konstanter\n• Ingen permanent pm uninstall\n• Ingen ändring av 5G/IMS/VoLTE eller radiosändeffekt\n\nDebloat görs fortsatt separat i Smart Debloat med pm disable-user och Recovery.");
  }

  void confirmSafeBatteryProfile(){
    if(!shOk()){requestSh();return;}
    new AlertDialog.Builder(this)
      .setTitle("Aktivera säker batteriprofil?")
      .setMessage("Toolbox sparar nuvarande Wi‑Fi/Bluetooth-skanningsvärden och sätter båda till 0. Detta kan minska bakgrundsskanning. Vanlig Wi‑Fi och Bluetooth fungerar fortfarande.")
      .setNegativeButton("Avbryt",null)
      .setPositiveButton("Aktivera",(d,w)->applySafeBatteryProfile())
      .show();
  }

  void applySafeBatteryProfile(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String result;
      try{
        snapshotGlobal(K_WIFI_SCAN,"bat_before_wifi_scan");
        snapshotGlobal(K_BLE_SCAN,"bat_before_ble_scan");
        int a=runCode("settings put global "+K_WIFI_SCAN+" 0");
        int b=runCode("settings put global "+K_BLE_SCAN+" 0");
        if(a==0&&b==0){
          p.edit().putBoolean("bat_safe_active",true).apply();
          log("Batteri: säker profil aktiverad (Wi-Fi/BLE background scan av)");
          result="Säker batteriprofil aktiverad.\n\nWi‑Fi background scan: 0\nBluetooth background scan: 0\n\nDe tidigare värdena är sparade för exakt återställning.";
        } else result="Android nekade minst en ändring. Kod: Wi‑Fi="+a+", BLE="+b+".";
      }catch(Exception e){result="Kunde inte aktivera profilen: "+e.getClass().getSimpleName();}
      final String x=result;runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Batterioptimering").setMessage(x).setPositiveButton("OK",null).show());
    }).start();
  }

  void restoreSafeBatteryProfile(){
    if(!shOk()){requestSh();return;}
    if(!p.contains("bat_before_wifi_scan")&&!p.contains("bat_before_ble_scan")){
      toast("Ingen sparad batterisnapshot finns");return;
    }
    new Thread(()->{
      String result;
      try{
        restoreGlobal(K_WIFI_SCAN,"bat_before_wifi_scan");
        restoreGlobal(K_BLE_SCAN,"bat_before_ble_scan");
        p.edit().putBoolean("bat_safe_active",false).apply();
        log("Batteri: säker profil återställd till sparad snapshot");
        result="Wi‑Fi- och Bluetooth-skanning har återställts till värdena telefonen hade innan Toolbox ändrade dem.";
      }catch(Exception e){result="Kunde inte återställa profilen: "+e.getClass().getSimpleName();}
      final String x=result;runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Återställning").setMessage(x).setPositiveButton("OK",null).show());
    }).start();
  }

  void confirmLocationThrottle(){
    if(!shOk()){requestSh();return;}
    new AlertDialog.Builder(this)
      .setTitle("Testa 10 min bakgrundsplats?")
      .setMessage("Detta kan påverka appar som behöver täta platsuppdateringar i bakgrunden, till exempel vissa tränings-, spårnings- eller automationsappar. Nuvarande värde sparas först.")
      .setNegativeButton("Avbryt",null)
      .setPositiveButton("Testa",(d,w)->applyLocationThrottle())
      .show();
  }

  void applyLocationThrottle(){
    new Thread(()->{
      String result;
      try{
        snapshotGlobal(K_LOC_THROTTLE,"bat_before_loc_throttle");
        int c=runCode("settings put global "+K_LOC_THROTTLE+" 600000");
        if(c==0){
          p.edit().putBoolean("bat_loc_active",true).apply();
          log("Batteri: bakgrundsplats satt till 600000 ms för test");
          result="Bakgrundsplats satt till minst 10 minuter (600000 ms) för test. Återställ om någon platsapp beter sig sämre.";
        }else result="Android nekade ändringen (kod "+c+").";
      }catch(Exception e){result="Kunde inte ändra bakgrundsplats: "+e.getClass().getSimpleName();}
      final String x=result;runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Bakgrundsplats").setMessage(x).setPositiveButton("OK",null).show());
    }).start();
  }

  void restoreLocationThrottle(){
    if(!shOk()){requestSh();return;}
    if(!p.contains("bat_before_loc_throttle")){toast("Ingen sparad plats-snapshot finns");return;}
    new Thread(()->{
      String result;
      try{
        restoreGlobal(K_LOC_THROTTLE,"bat_before_loc_throttle");
        p.edit().putBoolean("bat_loc_active",false).apply();
        log("Batteri: bakgrundsplats återställd");
        result="Bakgrundsplats har återställts till telefonens tidigare värde.";
      }catch(Exception e){result="Kunde inte återställa bakgrundsplats: "+e.getClass().getSimpleName();}
      final String x=result;runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Återställning").setMessage(x).setPositiveButton("OK",null).show());
    }).start();
  }

  void readBatteryOptimizerStatus(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String result;
      try{
        String wifi=runShellText("settings get global "+K_WIFI_SCAN);
        String ble=runShellText("settings get global "+K_BLE_SCAN);
        String loc=runShellText("settings get global "+K_LOC_THROTTLE);
        String idle=runShellText("dumpsys deviceidle | grep -m1 'mState=' 2>/dev/null");
        result="Wi‑Fi background scan: "+wifi+"\nBluetooth background scan: "+ble+"\nBackground location throttle: "+loc+" ms\n"+(idle.isEmpty()?"":idle+"\n")+"\nSäker profil sparad: "+(p.contains("bat_before_wifi_scan")||p.contains("bat_before_ble_scan")?"ja":"nej")+"\nSäker profil aktiv enligt Toolbox: "+(p.getBoolean("bat_safe_active",false)?"ja":"nej");
      }catch(Exception e){result="Kunde inte läsa status: "+e.getClass().getSimpleName();}
      final String x=result;runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Batteristatus").setMessage(x).setPositiveButton("OK",null).show());
    }).start();
  }

  void snapshotGlobal(String key,String pref)throws Exception{
    if(!p.contains(pref)){
      String old=runShellText("settings get global "+key).trim();
      if(old.isEmpty())old="null";
      p.edit().putString(pref,old).apply();
    }
  }

  void restoreGlobal(String key,String pref)throws Exception{
    if(!p.contains(pref))return;
    String old=p.getString(pref,"null");
    if(old==null||old.isEmpty()||"null".equals(old))runCode("settings delete global "+key);
    else runCode("settings put global "+key+" "+old.replaceAll("[^0-9-]",""));
  }

  int runCode(String cmd)throws Exception{
    java.lang.Process q=proc(cmd);return q.waitFor();
  }
}
