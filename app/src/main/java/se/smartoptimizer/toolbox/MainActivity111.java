package se.smartoptimizer.toolbox;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.*;
import android.telephony.*;
import android.widget.TextView;
import java.util.*;

public class MainActivity111 extends MainActivity110 {
  final Handler testHandler=new Handler(Looper.getMainLooper());

  @Override void base(String a,String b,boolean back){
    super.base(a,b,back);
    // Extra safe-area for One UI / Android 16 edge-to-edge status bar.
    root.setPadding(18,Math.max(100,26+bar()),18,50);
  }

  @Override void show(){
    super.show();
    try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.11");}catch(Throwable ignored){}
    card("📶 Wi‑Fi Diagnostik","Signal, frekvens, länkhastighet och enkel kvalitetsbedömning.",()->wifiPage());
    card("🔋 Batteri & temperatur","Batterinivå, temperatur, spänning, laddstatus och råd.",()->batteryHealthPage());
    card("🩺 S23 Ultra Hälsokontroll","Samlad kontroll av batteri, temperatur, lagring, RAM, nät och Shizuku.",()->healthCheckPage());
  }

  @Override void bestPlacePage(){
    base("🏠 BÄSTA PLATSEN","10 sekunders mätning per plats • jämför upp till tre platser",true);
    SharedPreferences p=getSharedPreferences("net110",MODE_PRIVATE);
    String a=p.getString("place_a",""),b=p.getString("place_b",""),c=p.getString("place_c","");
    note("A: "+(a.isEmpty()?"inte mätt":prettyPlace(a))+"\n\nB: "+(b.isEmpty()?"inte mätt":prettyPlace(b))+"\n\nC: "+(c.isEmpty()?"inte mätt":prettyPlace(c)));
    note("Varje plats mäts 10 gånger under cirka 10 sekunder. Håll telefonen still på ungefär samma höjd för en rättvis jämförelse.");
    btn("📍 Mät plats A – 10 sek",()->measurePlace("place_a","A"));
    btn("📍 Mät plats B – 10 sek",()->measurePlace("place_b","B"));
    btn("📍 Mät plats C – 10 sek",()->measurePlace("place_c","C"));
    if(!a.isEmpty()&&!b.isEmpty())note(bestPlaceAdvice(a,b,c));
    btn("🗑️ Nollställ platser",()->{p.edit().clear().apply();bestPlacePage();});
  }

  void measurePlace(String key,String label){
    base("📍 MÄTER PLATS "+label,"10 mätningar • håll telefonen still",true);
    final TextView status=note("Startar mätningen…");
    final ArrayList<Integer> r=new ArrayList<>(),q=new ArrayList<>(),sn=new ArrayList<>();
    final int[] n={0};
    Runnable task=new Runnable(){public void run(){
      try{
        TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED){
          SignalStrength ss=tm.getSignalStrength();
          if(ss!=null)for(CellSignalStrength cs:ss.getCellSignalStrengths())if(cs instanceof CellSignalStrengthLte){
            CellSignalStrengthLte l=(CellSignalStrengthLte)cs;r.add(l.getRsrp());q.add(l.getRsrq());int x=l.getRssnr();if(x!=99&&x!=Integer.MAX_VALUE)sn.add(x);break;
          }
        }
      }catch(Throwable ignored){}
      n[0]++;status.setText("Mätning "+n[0]+"/10");
      if(n[0]<10)testHandler.postDelayed(this,1000);else{
        if(r.isEmpty()){status.setText("Kunde inte läsa LTE-signalen. Kontrollera Telefon/Plats-behörighet.");return;}
        int rr=Math.round(avg(r)),qq=Math.round(avg(q)),ssn=sn.isEmpty()?99:Math.round(avg(sn));
        String v="RSRP="+rr+",RSRQ="+qq+",SNR10="+ssn+",SPREAD="+(Collections.max(r)-Collections.min(r))+",TIME="+tf.format(new Date());
        getSharedPreferences("net110",MODE_PRIVATE).edit().putString(key,v).apply();
        bestPlacePage();
      }
    }};testHandler.post(task);
  }

  @Override String prettyPlace(String x){
    int r=val(x,"RSRP"),q=val(x,"RSRQ"),sn=val(x,"SNR10"),sp=val(x,"SPREAD");
    String spread=sp==9999?"":" • variation "+sp+" dB";
    return "RSRP "+r+" • RSRQ "+q+" • SNR "+(sn==99||sn==Integer.MAX_VALUE?"?":String.format(Locale.ROOT,"%.1f",sn/10f))+" dB"+spread;
  }

  void wifiPage(){
    base("📶 WI‑FI DIAGNOSTIK","Aktuell Wi‑Fi-anslutning",true);
    try{
      WifiManager wm=(WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
      WifiInfo wi=wm==null?null:wm.getConnectionInfo();
      if(wm==null||wi==null){note("Wi‑Fi-information är inte tillgänglig.");return;}
      int r=wi.getRssi(),f=wi.getFrequency(),ls=wi.getLinkSpeed();
      String band=f>=5925?"6 GHz":f>=4900?"5 GHz":f>=2400?"2,4 GHz":"Okänt band";
      String grade=r>=-55?"🟢 Utmärkt":r>=-67?"🟢 Bra":r>=-75?"🟡 Medel":"🔴 Svag";
      sec("Anslutning");
      note("Signal: "+r+" dBm • "+grade+"\nFrekvens: "+f+" MHz • "+band+"\nLänkhastighet: "+ls+" Mbit/s");
      sec("Bedömning");
      if(r>=-67)note("✅ Bra Wi‑Fi-signal. Om internet ändå känns långsamt är flaskhalsen sannolikt inte själva Wi‑Fi-signalstyrkan.");
      else if(r>=-75)note("🟡 Användbar signal. Närmare routern eller 5/6 GHz med fri sikt kan förbättra kapaciteten.");
      else note("🔴 Svag Wi‑Fi-signal. Prova närmare routern eller en annan placering av router/mesh-nod.");
      if(f>0&&f<3000)note("ℹ️ 2,4 GHz når längre men har ofta mer störningar. 5 GHz ger normalt högre kapacitet på kortare avstånd.");
      btn("🔄 Uppdatera Wi‑Fi-mätning",()->wifiPage());
      btn("⚙️ Öppna Wi‑Fi-inställningar",()->{try{startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));}catch(Throwable e){}});
    }catch(Throwable e){note("Kunde inte läsa Wi‑Fi: "+e.getClass().getSimpleName());}
  }

  void batteryHealthPage(){
    base("🔋 BATTERI & TEMPERATUR","Livevärden från S23 Ultra",true);
    int lv=level();float t=temp();int v=volt();long cur=ua();
    sec("Batteri");
    note("Nivå: "+lv+"%\nTemperatur: "+fmt(t)+" °C • "+tname()+"\nSpänning: "+(v>0?String.format(Locale.ROOT,"%.2f V",v/1000f):"okänd")+"\nStatus: "+charge()+"\nStröm: "+(cur==0?"ej tillgänglig":String.format(Locale.ROOT,"%.0f mA",cur/1000f)));
    sec("Bedömning");
    if(t>=43)note("🔴 Telefonen är mycket varm. Undvik spel, kamera i hög belastning och snabbladdning tills temperaturen sjunkit.");
    else if(t>=40)note("🟠 Telefonen är varm. Prestanda och laddhastighet kan begränsas tills den kyls ned.");
    else if(t>=35)note("🟡 Normal till varm temperatur vid användning/laddning.");
    else note("🟢 Bra temperatur.");
    if(lv<=15)note("🔋 Låg batterinivå. Energisparläge kan vara lämpligt om du behöver lång drifttid.");
    btn("🔄 Uppdatera",()->batteryHealthPage());
  }

  void healthCheckPage(){
    base("🩺 S23 ULTRA HÄLSOKONTROLL","Samlad lokal kontroll • inga data skickas",true);
    int points=100;StringBuilder issues=new StringBuilder();float t=temp();int rr=ram();long fs=free();
    if(t>=43){points-=20;issues.append("🔴 Hög temperatur: ").append(fmt(t)).append(" °C\n");}
    else if(t>=40){points-=10;issues.append("🟠 Varm telefon: ").append(fmt(t)).append(" °C\n");}
    if(rr>92){points-=8;issues.append("🟡 Hög RAM-användning: ").append(rr).append("%\n");}
    if(fs<20){points-=10;issues.append("🟡 Lite ledigt lagringsutrymme: ").append(fs).append(" GB\n");}
    if(level()<15){points-=5;issues.append("🟡 Låg batterinivå\n");}
    if(!shOk()){points-=3;issues.append("ℹ️ Shizuku är inte redo\n");}
    try{
      TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);SignalStrength ss=tm.getSignalStrength();
      if(ss!=null)for(CellSignalStrength cs:ss.getCellSignalStrengths())if(cs instanceof CellSignalStrengthLte){CellSignalStrengthLte l=(CellSignalStrengthLte)cs;int r=l.getRsrp(),q=l.getRsrq(),sn=l.getRssnr();if(r<-110){points-=7;issues.append("🟡 Svag LTE-signal: ").append(r).append(" dBm\n");}if(q<-15||(sn!=99&&sn!=Integer.MAX_VALUE&&sn<0)){points-=7;issues.append("🟡 Störd mobil radiokvalitet\n");}break;}
    }catch(Throwable ignored){}
    points=Math.max(0,points);
    String grade=points>=90?"🟢 Mycket bra":points>=75?"🟡 Bra":points>=60?"🟠 Behöver ses över":"🔴 Åtgärd rekommenderas";
    sec("Resultat");note(grade+" • "+points+"/100");
    sec("Kontrollerat");note("🌡️ Temperatur "+fmt(t)+" °C\n🔋 Batteri "+level()+"%\n🧠 RAM "+rr+"%\n💾 Ledigt "+fs+" GB\n🔧 Shizuku "+(shOk()?"redo":"ej redo")+"\n📶 Mobil radio analyserad när Android tillåter det");
    sec("Att tänka på");note(issues.length()==0?"✅ Inga viktiga problem upptäcktes just nu.":issues.toString().trim());
    btn("📶 Öppna Nätverk & signal",()->network106());
    btn("📶 Öppna Wi‑Fi Diagnostik",()->wifiPage());
    btn("🔋 Öppna Batteri & temperatur",()->batteryHealthPage());
    btn("🔄 Kör kontrollen igen",()->healthCheckPage());
  }
}
