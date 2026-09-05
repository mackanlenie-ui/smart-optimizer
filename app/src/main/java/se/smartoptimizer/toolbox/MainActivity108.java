package se.smartoptimizer.toolbox;

import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.*;
import android.widget.TextView;
import java.util.*;

public class MainActivity108 extends MainActivity107 {
  @Override void show(){super.show();try{((TextView)root.getChildAt(0)).setText("S23 ULTRA TOOLBOX 10.8");}catch(Throwable ignored){}}

  @Override void network106(){
    base("📶 NÄTVERK & SIGNAL","S23 Ultra • Vimla/Telenor • live radioinfo",true);
    TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
    boolean phone=checkSelfPermission(Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED;
    boolean loc=checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED;
    if(!phone||!loc){note("För full signal- och cellinformation behöver Toolbox Telefon + Plats. Plats används bara lokalt för Androids cell-API och skickas ingenstans.");btn("Ge nätverksbehörighet",()->requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_FINE_LOCATION},NET_PERM));}
    sec("Aktuell anslutning");String op="Okänd",code="";try{op=tm.getNetworkOperatorName();code=tm.getNetworkOperator();}catch(Throwable ignored){}String plmn=code!=null&&code.length()>=5?code.substring(0,3)+"-"+code.substring(3):"Okänd";int nt=0;try{if(phone)nt=tm.getDataNetworkType();}catch(Throwable ignored){}note("Operatör som Android visar: "+(op==null||op.isEmpty()?"Okänd":op)+"\nAbonnemangs-PLMN: "+plmn+"\nDatanät: "+netName(nt));
    signalBlock(tm);cellBlock(tm,loc);sec("Smart rekommendation");note(smartRadioAdvice(tm));
    note("ℹ️ Vimla använder Telenors nät. Abonnemanget kan visas som 240-08 medan radiocellen rapporterar 240-07 (Tele2) i det gemensamma Tele2/Telenor-radionätet. APN ändrar inte radiosignalstyrkan.");
    sec("Snabbval");btn("🔄 Uppdatera mätning",()->network106());btn("📱 Öppna mobilnätsinställningar",()->openMobileSettings());btn("🧪 Jämför 5G preferred / 4G preferred",()->networkTest106());btn("🔧 Shizuku Tools",()->shPage());
  }

  String smartRadioAdvice(TelephonyManager tm){
    try{if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)return "Ge Telefon-behörighet och tryck Uppdatera för en personlig rekommendation.";SignalStrength s=tm.getSignalStrength();if(s==null)return "Ingen aktuell radiomätning finns ännu.";
      for(CellSignalStrength c:s.getCellSignalStrengths())if(c instanceof CellSignalStrengthLte){CellSignalStrengthLte l=(CellSignalStrengthLte)c;int r=l.getRsrp(),q=l.getRsrq(),raw=l.getRssnr();float sinr=(raw==Integer.MAX_VALUE||raw==99)?Float.NaN:raw/10f;
        boolean strong=r>=-100, weak=r<-110, noisy=q<-15||(!Float.isNaN(sinr)&&sinr<0), qualityPoor=q<-10||(!Float.isNaN(sinr)&&sinr<5);
        String vals="\n\nRSRP "+r+" dBm • RSRQ "+q+" dB • SNR "+(Float.isNaN(sinr)?"okänd":String.format(Locale.ROOT,"%.1f dB",sinr));
        if(strong&&qualityPoor)return "🟡 Bra signalstyrka men störd signalkvalitet. Behåll 5G preferred. Om hastigheten eller stabiliteten är dålig, prova annan placering (gärna närmare fönster) och jämför sedan 5G/4G igen."+vals;
        if(weak)return "🔴 Svag radiosignal. Prova annan placering och jämför 5G preferred mot 4G preferred. 4G kan bli stabilare inomhus, men välj det läge som ger bäst faktisk stabilitet."+vals;
        if(noisy)return "🟠 Signalen når telefonen men radiomiljön är störd. Behåll normalt 5G preferred och testa annan placering innan du ändrar nätläge."+vals;
        if(r>=-100&&q>=-10&&(Float.isNaN(sinr)||sinr>=5))return "🟢 Bra signal och bra radiokvalitet. Behåll 5G preferred; telefonen kan själv använda 4G när 5G inte ger någon fördel."+vals;
        return "🟡 Användbar signal med medelhög kvalitet. Behåll normalt 5G preferred. Jämför 4G endast om du märker instabilitet, låg hastighet eller onormal batteriförbrukning."+vals;
      }return "Ingen registrerad LTE-signal kunde vägas samman just nu.";
    }catch(Throwable e){return "Kunde inte skapa rekommendation: "+e.getClass().getSimpleName();}
  }
}
