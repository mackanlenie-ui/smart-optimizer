package se.smartoptimizer.toolbox;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.*;
import android.telephony.*;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity109 extends MainActivity108 {
  final Handler netHandler=new Handler(Looper.getMainLooper());
  final SimpleDateFormat tf=new SimpleDateFormat("HH:mm:ss",Locale.getDefault());

  @Override void show(){super.show();try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.9");}catch(Throwable ignored){}}

  @Override void network106(){
    super.network106();
    sec("Avancerad radioanalys");
    btn("📈 Signalhistorik",()->signalHistoryPage());
    btn("🗼 Cellhistorik",()->cellHistoryPage());
    btn("⏱️ Stabilitetstest 60 sek",()->startStabilityTest());
    btn("⚖️ Spara/jämför 5G och 4G",()->comparePage());
    saveLiveSample();
  }

  void saveLiveSample(){
    try{
      TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
      if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)return;
      SignalStrength s=tm.getSignalStrength(); if(s==null)return;
      for(CellSignalStrength c:s.getCellSignalStrengths()) if(c instanceof CellSignalStrengthLte){
        CellSignalStrengthLte l=(CellSignalStrengthLte)c;
        appendPref("sig_hist",tf.format(new Date())+"  RSRP "+l.getRsrp()+"  RSRQ "+l.getRsrq()+"  SNR "+snr(l.getRssnr()),40);
        break;
      }
      if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
        List<CellInfo> cells=tm.getAllCellInfo(); if(cells!=null)for(CellInfo ci:cells)if(ci.isRegistered()&&ci instanceof CellInfoLte){
          CellIdentityLte x=((CellInfoLte)ci).getCellIdentity(); String now=lteCell(x.getCi())+" • "+lteBand(x.getEarfcn())+" • PCI "+x.getPci();
          SharedPreferences p=getSharedPreferences("net109",MODE_PRIVATE); String last=p.getString("last_cell","");
          if(!now.equals(last)){appendPref("cell_hist",tf.format(new Date())+"  "+now,30);p.edit().putString("last_cell",now).apply();}
          break;
        }
      }
    }catch(Throwable ignored){}
  }

  void appendPref(String key,String row,int max){
    SharedPreferences p=getSharedPreferences("net109",MODE_PRIVATE); String old=p.getString(key,"");
    ArrayList<String> a=new ArrayList<>(); if(!old.isEmpty())a.addAll(Arrays.asList(old.split("\\n"))); a.add(0,row); while(a.size()>max)a.remove(a.size()-1);
    p.edit().putString(key,join(a)).apply();
  }
  String join(List<String>a){StringBuilder b=new StringBuilder();for(int i=0;i<a.size();i++){if(i>0)b.append('\n');b.append(a.get(i));}return b.toString();}

  void signalHistoryPage(){
    base("📈 SIGNALHISTORIK","Senaste mätningarna sparas lokalt",true);
    String h=getSharedPreferences("net109",MODE_PRIVATE).getString("sig_hist","");
    note(h.isEmpty()?"Ingen historik ännu. Öppna Nätverk & signal och tryck Uppdatera några gånger.":h);
    btn("🗑️ Rensa signalhistorik",()->{getSharedPreferences("net109",MODE_PRIVATE).edit().remove("sig_hist").apply();signalHistoryPage();});
  }

  void cellHistoryPage(){
    base("🗼 CELLHISTORIK","Visar när telefonen bytt LTE-cell/band",true);
    String h=getSharedPreferences("net109",MODE_PRIVATE).getString("cell_hist","");
    note(h.isEmpty()?"Inga cellbyten sparade ännu.":h);
    btn("🗑️ Rensa cellhistorik",()->{getSharedPreferences("net109",MODE_PRIVATE).edit().remove("cell_hist").remove("last_cell").apply();cellHistoryPage();});
  }

  void startStabilityTest(){
    base("⏱️ STABILITETSTEST","60 sekunder • 30 mätningar",true);
    note("Stå still på samma plats. Testet mäter variation i RSRP, RSRQ och SNR varannan sekund.");
    final ArrayList<Integer> rsrp=new ArrayList<>(),rsrq=new ArrayList<>(),snr10=new ArrayList<>();
    final TextView status=text("Startar…",16,true); status.setPadding(8,12,8,12); root.addView(status);
    final int[] n={0};
    Runnable r=new Runnable(){public void run(){
      try{TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);SignalStrength s=tm.getSignalStrength();if(s!=null)for(CellSignalStrength c:s.getCellSignalStrengths())if(c instanceof CellSignalStrengthLte){CellSignalStrengthLte l=(CellSignalStrengthLte)c;rsrp.add(l.getRsrp());rsrq.add(l.getRsrq());if(l.getRssnr()!=Integer.MAX_VALUE&&l.getRssnr()!=99)snr10.add(l.getRssnr());break;}}catch(Throwable ignored){}
      n[0]++; status.setText("Mätning "+n[0]+"/30");
      if(n[0]<30)netHandler.postDelayed(this,2000);else showStabilityResult(rsrp,rsrq,snr10);
    }}; netHandler.post(r);
  }

  void showStabilityResult(List<Integer>r,List<Integer>q,List<Integer>s){
    base("✅ STABILITET KLAR","60-sekundersresultat",true);
    if(r.isEmpty()){note("Inga LTE-värden kunde läsas under testet.");return;}
    int rMin=Collections.min(r),rMax=Collections.max(r),qMin=Collections.min(q),qMax=Collections.max(q);float rav=avg(r),qav=avg(q);float sav=s.isEmpty()?Float.NaN:avg(s)/10f;int spread=rMax-rMin;
    String grade=spread<=4?"🟢 Mycket stabil":spread<=8?"🟡 Ganska stabil":"🔴 Orolig signal";
    note(grade+"\n\nRSRP snitt "+fmt(rav)+" dBm • variation "+spread+" dB\nRSRQ snitt "+fmt(qav)+" dB • spann "+(qMax-qMin)+" dB\nSNR snitt "+(Float.isNaN(sav)?"okänd":fmt(sav)+" dB")+"\n\n"+(spread>8?"Telefonen växlar mycket i signalnivå här. Prova annan placering eller jämför 4G/5G.":"Signalnivån håller sig relativt jämn på den här platsen."));
    appendPref("stab_hist",tf.format(new Date())+"  "+grade+" • RSRP "+fmt(rav)+" • Δ"+spread+" dB",15);
  }
  float avg(List<Integer>a){long s=0;for(int v:a)s+=v;return a.isEmpty()?0:(float)s/a.size();}
  String fmt(float v){return String.format(Locale.ROOT,"%.1f",v);}

  void comparePage(){
    base("⚖️ 5G / 4G JÄMFÖRELSE","Spara två mätningar på exakt samma plats",true);
    SharedPreferences p=getSharedPreferences("net109",MODE_PRIVATE); String a=p.getString("cmp_a","");String b=p.getString("cmp_b","");
    note("A: "+(a.isEmpty()?"inte sparad":a)+"\n\nB: "+(b.isEmpty()?"inte sparad":b));
    btn("1️⃣ Spara aktuell som 5G preferred (A)",()->{p.edit().putString("cmp_a",snapshot()).apply();comparePage();});
    btn("2️⃣ Spara aktuell som 4G preferred (B)",()->{p.edit().putString("cmp_b",snapshot()).apply();comparePage();});
    if(!a.isEmpty()&&!b.isEmpty())note(compareSnapshots(a,b));
    btn("📱 Öppna mobilnätsinställningar",()->openMobileSettings());
    btn("🗑️ Nollställ jämförelse",()->{p.edit().remove("cmp_a").remove("cmp_b").apply();comparePage();});
  }

  String snapshot(){
    try{TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);SignalStrength s=tm.getSignalStrength();if(s!=null)for(CellSignalStrength c:s.getCellSignalStrengths())if(c instanceof CellSignalStrengthLte){CellSignalStrengthLte l=(CellSignalStrengthLte)c;return "RSRP="+l.getRsrp()+",RSRQ="+l.getRsrq()+",SNR10="+l.getRssnr()+",TIME="+tf.format(new Date());}}catch(Throwable ignored){}return "kunde inte läsa";
  }
  int val(String s,String k){try{int i=s.indexOf(k+"=");if(i<0)return 9999;i+=k.length()+1;int e=s.indexOf(',',i);if(e<0)e=s.length();return Integer.parseInt(s.substring(i,e));}catch(Throwable e){return 9999;}}
  String compareSnapshots(String a,String b){int ar=val(a,"RSRP"),br=val(b,"RSRP"),aq=val(a,"RSRQ"),bq=val(b,"RSRQ"),as=val(a,"SNR10"),bs=val(b,"SNR10");if(ar==9999||br==9999)return "Kunde inte jämföra mätningarna.";int scoreA=ar*2+aq*2+(as==99?0:as/5);int scoreB=br*2+bq*2+(bs==99?0:bs/5);if(Math.abs(scoreA-scoreB)<4)return "🤝 Resultaten är mycket lika. Behåll 5G preferred för flexibilitet.";return scoreA>scoreB?"🏆 5G preferred gav bäst samlad radiokvalitet i testet.":"🏆 4G preferred gav bäst samlad radiokvalitet i testet. Om skillnaden är stabil över flera test kan 4G vara bättre just här.";}
}
