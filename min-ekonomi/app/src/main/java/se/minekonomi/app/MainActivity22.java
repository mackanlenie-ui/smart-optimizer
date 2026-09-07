package se.minekonomi.app;

import android.app.*;
import android.os.*;
import android.widget.*;
import java.text.*;
import java.util.*;

public class MainActivity22 extends MainActivity {

    @Override void showHome(){
        base("Min Ekonomi v2.2","Privat ekonomi • biometriskt lås • smart prognos",false);
        double forecast = forecastToPayday();
        double daily = spendableToday();
        card("💰 Totalt saldo", money(balance())+"\nBeräknat saldo till nästa lön: "+money(forecast), v->showTransactions());
        card("🟢 Vad kan jag spendera idag?", money(daily)+"\nSäkert dagsbelopp efter planerade köp och obetalda räkningar", v->showCoach22());
        card("✨ Smarta insikter", smartInsights(), v->showCoach22());
        card("🧾 Transaktioner","Inkomster och utgifter",v->showTransactions());
        card("📅 Ekonomikalender","Räkningar och planerade köp i datumordning",v->showCalendar());
        card("🛒 Planerade köp",money(plannedOpen())+" kvar planerat",v->showPlanned());
        card("🧷 Räkningar",money(billsOpen())+" obetalt • påminnelser kan schemaläggas",v->showBills());
        card("🎯 Sparmål",savingSummary(),v->showSavings());
        card("📊 Budget & statistik","Månadsbudget, utgifter och utveckling",v->showBudget());
        card("🧠 Ekonomicoach","Prognos, dagsbudget och personliga råd",v->showCoach22());
        card("⚙️ Inställningar","Startsaldo, notiser och sekretess",v->showSettings());
    }

    int payDay(){
        int d=p.getInt("pay_day",25);
        if(d<1||d>31)d=25;
        return d;
    }

    int daysToPayday(){
        Calendar now=Calendar.getInstance();
        Calendar next=(Calendar)now.clone();
        int d=Math.min(payDay(), next.getActualMaximum(Calendar.DAY_OF_MONTH));
        next.set(Calendar.DAY_OF_MONTH,d);
        next.set(Calendar.HOUR_OF_DAY,23); next.set(Calendar.MINUTE,59); next.set(Calendar.SECOND,59);
        if(!next.after(now)){
            next.add(Calendar.MONTH,1);
            d=Math.min(payDay(), next.getActualMaximum(Calendar.DAY_OF_MONTH));
            next.set(Calendar.DAY_OF_MONTH,d);
        }
        long diff=next.getTimeInMillis()-now.getTimeInMillis();
        return Math.max(1,(int)Math.ceil(diff/86400000.0));
    }

    double forecastToPayday(){
        return balance()-plannedOpen()-billsOpen();
    }

    double spendableToday(){
        double free=Math.max(0,forecastToPayday());
        return free/Math.max(1,daysToPayday());
    }

    String smartInsights(){
        double cur=monthExpense(month());
        Calendar c=Calendar.getInstance(); c.add(Calendar.MONTH,-1);
        String prev=new SimpleDateFormat("yyyy-MM",Locale.US).format(c.getTime());
        double old=monthExpense(prev);
        double f=forecastToPayday();
        StringBuilder s=new StringBuilder();
        if(old>0){
            double diff=cur-old;
            if(diff<=0)s.append("Du har spenderat ").append(money(-diff)).append(" mindre än förra månaden.");
            else s.append("Du har spenderat ").append(money(diff)).append(" mer än förra månaden.");
        }else s.append("Du har spenderat ").append(money(cur)).append(" den här månaden.");
        s.append("\nEfter planerat och räkningar beräknas ").append(money(f)).append(" finnas kvar till nästa lön.");
        s.append("\nRekommenderat dagsbelopp: ").append(money(spendableToday())).append(".");
        return s.toString();
    }

    void showCoach22(){
        base("Ekonomicoach","Smarta råd baserade på dina lokala siffror",true);
        card("📅 Till nästa lön",daysToPayday()+" dagar kvar • lönedag "+payDay()+":e",null);
        card("💵 Beräknat saldo",money(forecastToPayday()),null);
        card("🟢 Säkert idag",money(spendableToday()),null);
        card("✨ Analys",smartInsights(),null);
        if(forecastToPayday()<0) note("⚠️ Planerade köp och obetalda räkningar är större än nuvarande saldo.");
        else if(spendableToday()<100) note("💡 Dagsutrymmet är lågt. Vänta gärna med mindre viktiga köp tills nästa lön.");
        else note("✅ Du ligger på plus efter planerade kostnader. Fortsätt följa dagsbeloppet för bättre kontroll.");
    }
}
