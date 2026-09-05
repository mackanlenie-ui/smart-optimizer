package se.smartoptimizer.toolbox;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.*;
import android.telephony.*;
import android.widget.TextView;
import java.util.*;

public class MainActivity110 extends MainActivity109 {
  @Override void show(){super.show();try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.10");}catch(Throwable ignored){}}

  @Override void network106(){
    super.network106();
    sec("Platsjämförelse");
    btn("🏠 Hitta bästa platsen",()->bestPlacePage());
  }

  @Override void signalBlock(TelephonyManager tm){
    sec("Signal & kvalitet");
    try{
      if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){note("Telefonbehörighet saknas.");return;}
      SignalStrength s=tm.getSignalStrength(); if(s==null){note("Ingen signalmätning tillgänglig just nu.");return;}
      List<CellSignalStrength> list=s.getCellSignalStrengths(); if(list==null||list.isEmpty()){note("Signal finns men Android gav inga detaljer.");return;}
      for(CellSignalStrength c:list){
        if(c instanceof CellSignalStrengthLte){
          CellSignalStrengthLte l=(CellSignalStrengthLte)c;int r=l.getRsrp(),q=l.getRsrq(),raw=l.getRssnr();
          note("4G LTE • "+l.getDbm()+" dBm\n\n"+rsrpGrade(r)+" RSRP  "+r+" dBm\n"+rsrqGrade(q)+" RSRQ  "+q+" dB\n"+snrGrade(raw)+" SNR   "+snr(raw)+"\n\nHelhetsbetyg: "+overall(r,q,raw));
          if(raw!=Integer.MAX_VALUE&&raw!=99){float d=raw/10f;if(d<1)note("⚠️ SNR är mycket låg. Signalen når fram, men brus/störningar begränsar kvaliteten.");else if(d<5)note("ℹ️ SNR är låg. Flytta telefonen någon meter eller närmare fönster och mät igen.");}
        }else if(c instanceof CellSignalStrengthNr){CellSignalStrengthNr n=(CellSignalStrengthNr)c;note("5G NR • "+n.getDbm()+" dBm\nSS-RSRP "+n.getSsRsrp()+" dBm • SS-RSRQ "+n.getSsRsrq()+" dB • SS-SINR "+n.getSsSinr()+" dB");}
      }
    }catch(Throwable e){note("Kunde inte läsa signalen: "+e.getClass().getSimpleName());}
  }

  @Override void cellBlock(TelephonyManager tm,boolean loc){
    sec("Cell / mastinfo"); if(!loc){note("Platsbehörighet krävs av Android för cell-ID och frekvensinfo.");return;}
    try{
      List<CellInfo> cells=tm.getAllCellInfo(); if(cells==null||cells.isEmpty()){note("Ingen cellista tillgänglig just nu.");return;}
      int shown=0;
      for(CellInfo ci:cells){if(!ci.isRegistered())continue;
        if(ci instanceof CellInfoLte){CellIdentityLte x=((CellInfoLte)ci).getCellIdentity();int ear=x.getEarfcn();String bw=bandwidth(x.getBandwidth());
          note("📡 REGISTRERAD LTE\n\n🗼 eNB / sektor: "+lteCell(x.getCi())+"\nPCI "+x.getPci()+" • TAC "+x.getTac()+"\nEARFCN "+ear+" • "+lteBand(ear)+"\n"+(bw.startsWith("okänd")?"Bandbredd ej tillgänglig":"Bandbredd: "+bw)+"\nRadiocell MCC/MNC: "+safe(x.getMccString())+"/"+safe(x.getMncString()));shown++;
        }else if(ci instanceof CellInfoNr){CellIdentityNr x=(CellIdentityNr)((CellInfoNr)ci).getCellIdentity();note("📡 REGISTRERAD 5G NR\nNCI "+x.getNci()+" • PCI "+x.getPci()+" • TAC "+x.getTac()+"\nNR-ARFCN "+x.getNrarfcn()+" • "+nrBandHint(x.getNrarfcn()));shown++;}
      }
      if(shown==0)note("Ingen registrerad LTE/5G-cell rapporterades av Android.");
    }catch(Throwable e){note("Kunde inte läsa cellinfo: "+e.getClass().getSimpleName());}
  }

  @Override void showStabilityResult(List<Integer>r,List<Integer>q,List<Integer>s){
    base("✅ STABILITET KLAR","60-sekundersresultat",true);
    if(r.isEmpty()){note("Inga LTE-värden kunde läsas under testet.");return;}
    int rMin=Collections.min(r),rMax=Collections.max(r),qMin=Collections.min(q),qMax=Collections.max(q);float rav=avg(r),qav=avg(q);float sav=s.isEmpty()?Float.NaN:avg(s)/10f;int spread=rMax-rMin;
    String grade=spread<=4?"🟢 Mycket stabil":spread<=8?"🟡 Ganska stabil":"🔴 Orolig signal";
    note(grade+"\n\nRSRP medel "+fmt(rav)+" dBm\nBäst "+rMax+" • Sämst "+rMin+" • Variation "+spread+" dB\n\nRSRQ medel "+fmt(qav)+" dB\nBäst "+qMax+" • Sämst "+qMin+" • Variation "+(qMax-qMin)+" dB\n\nSNR medel "+(Float.isNaN(sav)?"okänd":fmt(sav)+" dB")+"\n\n"+(spread>8?"Telefonen växlar mycket i signalnivå här. Prova annan placering eller jämför 4G/5G.":"Signalnivån håller sig relativt jämn på den här platsen."));
    appendPref("stab_hist",tf.format(new Date())+"  "+grade+" • RSRP "+fmt(rav)+" • Δ"+spread+" dB",15);
  }

  void bestPlacePage(){
    base("🏠 BÄSTA PLATSEN","Jämför upp till tre platser hemma",true);
    SharedPreferences p=getSharedPreferences("net110",MODE_PRIVATE);
    String a=p.getString("place_a",""),b=p.getString("place_b",""),c=p.getString("place_c","");
    note("A: "+(a.isEmpty()?"inte sparad":prettyPlace(a))+"\n\nB: "+(b.isEmpty()?"inte sparad":prettyPlace(b))+"\n\nC: "+(c.isEmpty()?"inte sparad":prettyPlace(c)));
    btn("📍 Spara plats A",()->{p.edit().putString("place_a",radioSnapshot()).apply();bestPlacePage();});
    btn("📍 Spara plats B",()->{p.edit().putString("place_b",radioSnapshot()).apply();bestPlacePage();});
    btn("📍 Spara plats C",()->{p.edit().putString("place_c",radioSnapshot()).apply();bestPlacePage();});
    if(!a.isEmpty()&&!b.isEmpty())note(bestPlaceAdvice(a,b,c));
    btn("🗑️ Nollställ platser",()->{p.edit().clear().apply();bestPlacePage();});
  }

  String radioSnapshot(){
    try{TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);SignalStrength s=tm.getSignalStrength();if(s!=null)for(CellSignalStrength c:s.getCellSignalStrengths())if(c instanceof CellSignalStrengthLte){CellSignalStrengthLte l=(CellSignalStrengthLte)c;return "RSRP="+l.getRsrp()+",RSRQ="+l.getRsrq()+",SNR10="+l.getRssnr()+",TIME="+tf.format(new Date());}}catch(Throwable ignored){}return "kunde inte läsa";
  }
  String prettyPlace(String x){int r=val(x,"RSRP"),q=val(x,"RSRQ"),sn=val(x,"SNR10");return "RSRP "+r+" • RSRQ "+q+" • SNR "+(sn==99||sn==Integer.MAX_VALUE?"?":String.format(Locale.ROOT,"%.1f",sn/10f))+" dB";}
  int placeScore(String x){int r=val(x,"RSRP"),q=val(x,"RSRQ"),sn=val(x,"SNR10");if(r==9999||q==9999)return -99999;int s=(r+140)*3+(q+30)*4;if(sn!=99&&sn!=Integer.MAX_VALUE&&sn!=9999)s+=sn;return s;}
  String bestPlaceAdvice(String a,String b,String c){String[] names=c.isEmpty()?new String[]{"A","B"}:new String[]{"A","B","C"};String[] vals=c.isEmpty()?new String[]{a,b}:new String[]{a,b,c};int best=-99999,idx=0;for(int i=0;i<vals.length;i++){int sc=placeScore(vals[i]);if(sc>best){best=sc;idx=i;}}return "🏆 Bästa plats just nu: "+names[idx]+"\n"+prettyPlace(vals[idx])+"\n\nMät på ungefär samma höjd och håll telefonen still några sekunder före varje sparning för rättvis jämförelse.";}
}
