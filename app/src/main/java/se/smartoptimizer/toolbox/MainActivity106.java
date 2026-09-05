package se.smartoptimizer.toolbox;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.*;
import android.widget.TextView;
import java.util.*;

public class MainActivity106 extends MainActivity105 {
  static final int NET_PERM=1061;

  @Override void show(){
    super.show();
    try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.6");}catch(Throwable ignored){}
    card("📶 Nätverk & signal","Live signal, LTE/5G, cell-ID, band och Vimla/Telenor-råd.",()->network106());
  }

  void network106(){
    base("📶 NÄTVERK & SIGNAL","S23 Ultra • Vimla/Telenor • live radioinfo",true);
    TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
    boolean phone=checkSelfPermission(Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED;
    boolean loc=checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED;
    if(!phone||!loc){
      note("För full signal- och cellinformation behöver Toolbox Telefon + Plats. Plats används bara lokalt för Androids cell-API och skickas ingenstans.");
      btn("Ge nätverksbehörighet",()->requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_FINE_LOCATION},NET_PERM));
    }
    sec("Aktuell anslutning");
    String op="Okänd", code="";
    try{op=tm.getNetworkOperatorName();code=tm.getNetworkOperator();}catch(Throwable ignored){}
    String plmn=code!=null&&code.length()>=5?code.substring(0,3)+"-"+code.substring(3):"Okänd";
    int nt=0;try{if(phone)nt=tm.getDataNetworkType();}catch(Throwable ignored){}
    note("Operatör som Android visar: "+(op==null||op.isEmpty()?"Okänd":op)+"\nPLMN: "+plmn+"\nDatanät: "+netName(nt));
    signalBlock(tm);
    cellBlock(tm,loc);
    sec("Smart rekommendation");
    int dbm=bestDbm(tm);
    if(dbm==Integer.MIN_VALUE)note("Ge behörighet och tryck Uppdatera för en personlig rekommendation.");
    else if(dbm>=-100)note("✅ "+dbm+" dBm: använd normalt 5G preferred. 4G får fortfarande användas när 5G inte ger nytta.");
    else if(dbm>=-110)note("🟡 "+dbm+" dBm: signalen är användbar men medel. Behåll 5G preferred om anslutningen är stabil; prova 4G preferred vid hoppig 5G eller hög batteriförbrukning.");
    else note("🔴 "+dbm+" dBm: svag signal. 4G preferred kan ibland bli stabilare, särskilt inomhus. Flytta telefonen mot fönster/högre placering för bättre radioförbindelse.");
    note("Vimla använder Telenors mobilnät. I det delade radionätet kan diagnostikappar ibland visa Tele2/240-07 samtidigt som abonnemanget är Vimla/Telenor. APN ändrar inte själva radiosignalstyrkan.");
    sec("Snabbval");
    btn("🔄 Uppdatera mätning",()->network106());
    btn("📱 Öppna mobilnätsinställningar",()->openMobileSettings());
    btn("🧪 Jämför 5G preferred / 4G preferred",()->networkTest106());
    btn("🔧 Shizuku Tools",()->shPage());
  }

  void signalBlock(TelephonyManager tm){
    sec("Signal");
    try{
      if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){note("Telefonbehörighet saknas.");return;}
      SignalStrength s=tm.getSignalStrength();
      if(s==null){note("Ingen signalmätning tillgänglig just nu.");return;}
      List<CellSignalStrength> list=s.getCellSignalStrengths();
      if(list==null||list.isEmpty()){note("Signal finns men Android gav inga detaljer.");return;}
      for(CellSignalStrength c:list){
        if(c instanceof CellSignalStrengthLte){CellSignalStrengthLte l=(CellSignalStrengthLte)c;note("4G LTE  •  "+l.getDbm()+" dBm\nRSRP "+l.getRsrp()+" dBm  •  RSRQ "+l.getRsrq()+" dB  •  SNR "+snr(l.getRssnr()));}
        else if(c instanceof CellSignalStrengthNr){CellSignalStrengthNr n=(CellSignalStrengthNr)c;note("5G NR  •  "+n.getDbm()+" dBm\nSS-RSRP "+n.getSsRsrp()+" dBm  •  SS-RSRQ "+n.getSsRsrq()+" dB  •  SS-SINR "+n.getSsSinr()+" dB");}
        else note(c.getClass().getSimpleName()+"  •  "+c.getDbm()+" dBm");
      }
    }catch(Throwable e){note("Kunde inte läsa signalen: "+e.getClass().getSimpleName());}
  }

  void cellBlock(TelephonyManager tm,boolean loc){
    sec("Cell / mastinfo");
    if(!loc){note("Platsbehörighet krävs av Android för cell-ID och frekvensinfo.");return;}
    try{
      List<CellInfo> cells=tm.getAllCellInfo();
      if(cells==null||cells.isEmpty()){note("Ingen cellista tillgänglig just nu.");return;}
      int shown=0;
      for(CellInfo ci:cells){
        if(!ci.isRegistered())continue;
        if(ci instanceof CellInfoLte){
          CellIdentityLte x=((CellInfoLte)ci).getCellIdentity();
          int ear=x.getEarfcn();
          note("📡 Registrerad LTE\neNB/cell-ID: "+lteCell(x.getCi())+"\nPCI "+x.getPci()+"  •  TAC "+x.getTac()+"\nEARFCN "+ear+"  •  "+lteBand(ear)+"  •  BW "+mhz(x.getBandwidth())+"\nMCC/MNC "+safe(x.getMccString())+"/"+safe(x.getMncString())); shown++;
        } else if(ci instanceof CellInfoNr){
          CellIdentityNr x=(CellIdentityNr)((CellInfoNr)ci).getCellIdentity();
          note("📡 Registrerad 5G NR\nNCI "+x.getNci()+"  •  PCI "+x.getPci()+"  •  TAC "+x.getTac()+"\nNR-ARFCN "+x.getNrarfcn()+"  •  "+nrBandHint(x.getNrarfcn())+"\nMCC/MNC "+safe(x.getMccString())+"/"+safe(x.getMncString())); shown++;
        }
      }
      if(shown==0)note("Ingen registrerad LTE/5G-cell rapporterades av Android. Detta kan hända med NSA 5G eller vissa Samsung-firmwareversioner.");
    }catch(Throwable e){note("Kunde inte läsa cellinfo: "+e.getClass().getSimpleName());}
  }

  int bestDbm(TelephonyManager tm){
    try{if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)return Integer.MIN_VALUE;SignalStrength s=tm.getSignalStrength();if(s==null)return Integer.MIN_VALUE;int best=-200;for(CellSignalStrength c:s.getCellSignalStrengths())best=Math.max(best,c.getDbm());return best<=-200?Integer.MIN_VALUE:best;}catch(Throwable e){return Integer.MIN_VALUE;}
  }
  String netName(int t){
    switch(t){
      case TelephonyManager.NETWORK_TYPE_NR:return "5G NR";
      case TelephonyManager.NETWORK_TYPE_LTE:return "4G LTE";
      case TelephonyManager.NETWORK_TYPE_HSPAP:return "3G HSPA+";
      case TelephonyManager.NETWORK_TYPE_HSPA:return "3G HSPA";
      case TelephonyManager.NETWORK_TYPE_UMTS:return "3G UMTS";
      case TelephonyManager.NETWORK_TYPE_EDGE:return "2G EDGE";
      case TelephonyManager.NETWORK_TYPE_GPRS:return "2G GPRS";
      case TelephonyManager.NETWORK_TYPE_IWLAN:return "IWLAN / Wi-Fi Calling";
      case TelephonyManager.NETWORK_TYPE_UNKNOWN:return "Okänd";
      default:return "Nättyp "+t;
    }
  }
  String snr(int v){return v==Integer.MAX_VALUE||v==99?"okänd":String.format(Locale.ROOT,"%.1f dB",v/10f);}
  String mhz(int hz){return hz>0?(hz/1000000)+" MHz":"okänd";}
  String safe(String s){return s==null?"?":s;}
  String lteCell(int ci){if(ci==CellInfo.UNAVAILABLE)return "okänd";int enb=ci>>8,cid=ci&255;return enb+":"+cid+" (CI "+ci+")";}
  String lteBand(int e){if(e>=6150&&e<=6449)return "B20 / 800 MHz";if(e>=3450&&e<=3799)return "B8 / 900 MHz";if(e>=1200&&e<=1949)return "B3 / 1800 MHz";if(e>=2750&&e<=3449)return "B7 / 2600 MHz";if(e>=0&&e<=599)return "B1 / 2100 MHz";return "LTE-band okänt";}
  String nrBandHint(int n){if(n>=620000&&n<=680000)return "troligen n78 / 3,5 GHz";if(n>=151600&&n<=160600)return "troligen n28 / 700 MHz";return "5G-band via ARFCN";}

  void openMobileSettings(){
    try{startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));}
    catch(Throwable e){try{startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));}catch(Throwable ignored){toast("Kunde inte öppna inställningen");}}
  }
  void networkTest106(){
    base("🧪 5G / 4G JÄMFÖRELSE","Mät på samma plats för ett rättvist resultat",true);
    note("1. Stå still på samma plats.\n2. Välj 5G preferred i Mobilnät och notera RSRP/RSRQ/SNR här.\n3. Välj 4G preferred och vänta 20–30 sekunder.\n4. Jämför igen.\n\nVälj det läge som ger bäst stabilitet, inte bara högst staplar. Små skillnader på 1–3 dB är ofta normal variation.");
    btn("Öppna mobilnätsinställningar",()->openMobileSettings());
    btn("Tillbaka till live mätning",()->network106());
  }

  @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){super.onRequestPermissionsResult(requestCode,permissions,grantResults);if(requestCode==NET_PERM)network106();}
}
