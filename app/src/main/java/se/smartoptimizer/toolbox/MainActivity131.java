package se.smartoptimizer.toolbox;

import android.os.Bundle;

public class MainActivity131 extends MainActivity130 {
  @Override public void onCreate(Bundle b){super.onCreate(b);}

  @Override void show(){
    base("S23 ULTRA TOOLBOX 10.31","Ren startsida • kategorier • säkra och återställningsbara tester",false);
    dashboard();
    card("📡 Nätverk & signal","Vimla, LTE/5G, signal, CA och IMS.",()->networkHub122());
    card("🔋 Batteri & optimering","Smart batteristatus, appanalys och selektiv optimering.",()->batteryHub122());
    card("🛡️ Appar & debloat","Smart Debloat, App Manager och rekommendationer.",()->appsHub122());
    card("🌡️ Prestanda & temperatur","Thermal Watch, laddtest och Smart Advisor.",()->performanceHub122());
    card("📱 Telefon & jobb","GPS/Jobbläge, DeX och kamera.",()->phoneHub122());
    card("🧯 Recovery & diagnostik","Återställning, backup, historik och Shizuku.",()->recoveryHub122());
    card("🧰 Fler verktyg","Rapporter, historik, diagnostik och Shizuku-verktyg.",()->legacyHome122());
  }

  @Override void batteryHub122(){
    base("🔋 BATTERI & OPTIMERING","Smart status • appanalys • selektiv optimering",true);
    note(shStatus());
    note("Här visas bara aktuella batteriverktyg och reversibla tester.");
    btn("🔋 Smart batteristatus 2.0",()->smartBatteryStatus131());
    btn("🧠 Smart app- & Doze-analys 1.0",()->smartStandby126());
    btn("🎯 Selektiv appoptimering 1.0",()->selective127());
    btn("📊 Avancerad Standby/Doze-status",()->standbyDoze125());
    btn("🧠 Kryptonian säkerhetsanalys 1.3",()->kryptonAnalyze124());
    btn("🛡️ Säkra batteritweaks 1.0",()->batteryOptimizerPage());
    btn("🧪 Avancerade batteritester",()->advancedBatteryTests130());
  }

  void smartBatteryStatus131(){
    if(!shOk()){requestSh();return;}
    new Thread(()->{
      String out;
      try{
        String wifi=g122("wifi_scan_always_enabled");
        String ble=g122("ble_scan_always_enabled");
        String loc=g122("location_background_throttle_interval_ms");
        String mobile=g122("mobile_data_always_on");
        String standby=g122("app_standby_enabled");
        String saver=g122("low_power");
        String users=runShellText("pm list packages -3");
        String buckets=runShellText("am get-standby-bucket");
        int userCount=0, active=0, sleeping=0;
        java.util.HashSet<String> up=new java.util.HashSet<>();
        for(String l:users.split("\\n"))if(l.startsWith("package:")){userCount++;up.add(l.substring(8).trim());}
        for(String l:buckets.split("\\n")){
          int c=l.lastIndexOf(':'); if(c<=0)continue;
          String pkg=l.substring(0,c).trim(); if(!up.contains(pkg))continue;
          try{int b=Integer.parseInt(l.substring(c+1).trim()); if(b<=20)active++; else if(b>=30)sleeping++;}catch(Throwable ignored){}
        }
        boolean core="0".equals(wifi)&&"0".equals(ble)&&"600000".equals(loc)&&!"1".equals(mobile)&&"1".equals(standby);
        String grade=core?"✅ OPTIMALT":"🟡 KAN FÖRBÄTTRAS";
        StringBuilder s=new StringBuilder("🔋 SMART BATTERISTATUS 2.0\n\n");
        s.append(grade).append("\n\n");
        s.append("Wi-Fi bakgrundsskanning: ").append(wifi).append("\n");
        s.append("Bluetooth bakgrundsskanning: ").append(ble).append("\n");
        s.append("Bakgrundsplats: ").append(loc).append(" ms\n");
        s.append("Mobil data alltid aktiv: ").append(onoff122(mobile)).append("\n");
        s.append("App standby: ").append(cleanFlag123(standby)).append("\n");
        s.append("Energisparläge: ").append(onoff122(saver)).append("\n\n");
        s.append("Användarappar: ").append(userCount).append("\n");
        s.append("Active/Working Set: ").append(active).append("\n");
        s.append("Frequent/Rare/Restricted/Never: ").append(sleeping).append("\n\n");
        if(core)s.append("✅ Inga globala batteriändringar rekommenderas just nu. Nästa möjliga vinst finns i selektiv appoptimering.\n");
        else s.append("🟡 Kör Kryptonian säkerhetsanalys och säkra batteritweaks för att se vad som avviker.\n");
        s.append("\n🛡️ Inga inställningar ändras av den här analysen.");
        out=s.toString();
      }catch(Exception e){out="Batteristatus kunde inte läsas: "+e.getClass().getSimpleName();}
      final String x=out;runOnUiThread(()->dialog118("Smart batteristatus 2.0",x));
    }).start();
  }
}
