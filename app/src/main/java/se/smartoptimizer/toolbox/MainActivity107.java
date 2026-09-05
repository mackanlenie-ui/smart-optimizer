package se.smartoptimizer.toolbox;

import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.*;
import android.widget.TextView;
import java.util.*;

public class MainActivity107 extends MainActivity106 {
  @Override void show(){
    super.show();
    try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.7");}catch(Throwable ignored){}
  }

  @Override void network106(){
    base("📶 NÄTVERK & SIGNAL","S23 Ultra • Vimla/Telenor • live radioinfo",true);
    TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
    boolean phone=checkSelfPermission(Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED;
    boolean loc=checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED;
    if(!phone||!loc){note("För full signal- och cellinformation behöver Toolbox Telefon + Plats. Plats används bara lokalt för Androids cell-API och skickas ingenstans.");btn("Ge nätverksbehörighet",()->requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_FINE_LOCATION},NET_PERM));}
    sec("Aktuell anslutning");
    String op="Okänd",code="";try{op=tm.getNetworkOperatorName();code=tm.getNetworkOperator();}catch(Throwable ignored){}
    String plmn=code!=null&&code.length()>=5?code.substring(0,3)+"-"+code.substring(3):"Okänd";int nt=0;try{if(phone)nt=tm.getDataNetworkType();}catch(Throwable ignored){}
    note("Operatör som Android visar: "+(op==null||op.isEmpty()?"Okänd":op)+"\nAbonnemangs-PLMN: "+plmn+"\nDatanät: "+netName(nt));
    signalBlock(tm);cellBlock(tm,loc);
    sec("Smart rekommendation");int dbm=bestDbm(tm);
    if(dbm==Integer.MIN_VALUE)note("Ge behörighet och tryck Uppdatera för en personlig rekommendation.");
    else if(dbm>=-100)note("✅ "+dbm+" dBm: använd normalt 5G preferred. 4G får fortfarande användas när 5G inte ger nytta.");
    else if(dbm>=-110)note("🟡 "+dbm+" dBm: användbar men medel signal. Behåll 5G preferred om anslutningen är stabil; prova 4G preferred vid hoppig 5G.");
    else note("🔴 "+dbm+" dBm: svag signal. 4G preferred kan ibland bli stabilare, särskilt inomhus.");
    note("ℹ️ Vimla använder Telenors nät. Abonnemanget kan visas som 240-08 medan den registrerade radiocellen rapporterar 240-07 (Tele2) i det gemensamma Tele2/Telenor-radionätet. Det är inte i sig ett fel. APN ändrar inte radiosignalstyrkan.");
    sec("Snabbval");btn("🔄 Uppdatera mätning",()->network106());btn("📱 Öppna mobilnätsinställningar",()->openMobileSettings());btn("🧪 Jämför 5G preferred / 4G preferred",()->networkTest106());btn("🔧 Shizuku Tools",()->shPage());
  }

  @Override void signalBlock(TelephonyManager tm){
    sec("Signal & kvalitet");
    try{if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){note("Telefonbehörighet saknas.");return;}SignalStrength s=tm.getSignalStrength();if(s==null){note("Ingen signalmätning tillgänglig just nu.");return;}List<CellSignalStrength> list=s.getCellSignalStrengths();if(list==null||list.isEmpty()){note("Signal finns men Android gav inga detaljer.");return;}
      for(CellSignalStrength c:list){if(c instanceof CellSignalStrengthLte){CellSignalStrengthLte l=(CellSignalStrengthLte)c;int r=l.getRsrp(),q=l.getRsrq(),sn=l.getRssnr();note("4G LTE • "+l.getDbm()+" dBm\n\n"+rsrpGrade(r)+" RSRP  "+r+" dBm\n"+rsrqGrade(q)+" RSRQ  "+q+" dB\n"+snrGrade(sn)+" SNR   "+snr(sn)+"\n\nHelhetsbetyg: "+overall(r,q,sn));}else if(c instanceof CellSignalStrengthNr){CellSignalStrengthNr n=(CellSignalStrengthNr)c;note("5G NR • "+n.getDbm()+" dBm\nSS-RSRP "+n.getSsRsrp()+" dBm • SS-RSRQ "+n.getSsRsrq()+" dB • SS-SINR "+n.getSsSinr()+" dB");}}
    }catch(Throwable e){note("Kunde inte läsa signalen: "+e.getClass().getSimpleName());}
  }

  @Override void cellBlock(TelephonyManager tm,boolean loc){
    sec("Cell / mastinfo");if(!loc){note("Platsbehörighet krävs av Android för cell-ID och frekvensinfo.");return;}
    try{List<CellInfo> cells=tm.getAllCellInfo();if(cells==null||cells.isEmpty()){note("Ingen cellista tillgänglig just nu.");return;}int shown=0;for(CellInfo ci:cells){if(!ci.isRegistered())continue;if(ci instanceof CellInfoLte){CellIdentityLte x=((CellInfoLte)ci).getCellIdentity();int ear=x.getEarfcn();note("📡 REGISTRERAD LTE\n\n🗼 eNB / sektor: "+lteCell(x.getCi())+"\nPCI "+x.getPci()+" • TAC "+x.getTac()+"\nEARFCN "+ear+" • "+lteBand(ear)+"\nBandbredd: "+bandwidth(x.getBandwidth())+"\nRadiocell MCC/MNC: "+safe(x.getMccString())+"/"+safe(x.getMncString()));shown++;}else if(ci instanceof CellInfoNr){CellIdentityNr x=(CellIdentityNr)((CellInfoNr)ci).getCellIdentity();note("📡 REGISTRERAD 5G NR\nNCI "+x.getNci()+" • PCI "+x.getPci()+" • TAC "+x.getTac()+"\nNR-ARFCN "+x.getNrarfcn()+" • "+nrBandHint(x.getNrarfcn()));shown++;}}if(shown==0)note("Ingen registrerad LTE/5G-cell rapporterades av Android.");}catch(Throwable e){note("Kunde inte läsa cellinfo: "+e.getClass().getSimpleName());}
  }

  String bandwidth(int hz){if(hz<=0||hz==Integer.MAX_VALUE||hz>100000000)return "okänd (Android rapporterade inget giltigt värde)";return String.format(Locale.ROOT,"%.1f MHz",hz/1000000f);}
  String rsrpGrade(int v){if(v>=-90)return "🟢 Utmärkt";if(v>=-100)return "🟢 Bra";if(v>=-110)return "🟡 Medel";return "🔴 Svag";}
  String rsrqGrade(int v){if(v>=-10)return "🟢 Bra";if(v>=-15)return "🟡 Medel";return "🔴 Svag";}
  String snrGrade(int v){if(v==Integer.MAX_VALUE||v==99)return "⚪ Okänd";float d=v/10f;if(d>=13)return "🟢 Bra";if(d>=0)return "🟡 Medel";return "🔴 Svag";}
  String overall(int r,int q,int sn){int score=0;score+=r>=-100?2:r>=-110?1:0;score+=q>=-10?2:q>=-15?1:0;if(sn!=Integer.MAX_VALUE&&sn!=99){float d=sn/10f;score+=d>=13?2:d>=0?1:0;}return score>=5?"🟢 BRA":score>=3?"🟡 MEDEL":"🔴 SVAG";}
}
