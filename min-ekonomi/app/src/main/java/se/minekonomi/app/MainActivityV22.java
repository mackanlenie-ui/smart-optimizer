package se.minekonomi.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.biometrics.BiometricPrompt;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.json.*;
import java.text.*;
import java.util.*;

public class MainActivityV22 extends Activity {
    private SharedPreferences p;
    private LinearLayout content;
    private int tab = 0;
    private final int BG = Color.rgb(7,18,28);
    private final int NAV = Color.rgb(17,31,43);
    private final int CARD = Color.rgb(28,47,64);
    private final int CARD2 = Color.rgb(23,62,52);
    private final int ACC = Color.rgb(72,207,145);
    private final int RED = Color.rgb(255,100,119);
    private final int MUTED = Color.rgb(164,177,190);
    private final int BLUE = Color.rgb(87,139,190);
    private final int AMBER = Color.rgb(255,184,72);
    private final String[] CATS = {"Mat","Boende","Transport","Shopping","Räkningar","Resor","Presenter","Hälsa","Övrigt"};

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        p=getSharedPreferences("min_ekonomi",MODE_PRIVATE);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(0);
        if(p.getBoolean("biometricLock",false)) authenticate(); else showTab(0);
    }

    private void authenticate(){
        if(Build.VERSION.SDK_INT<28){showTab(0);return;}
        try{
            BiometricPrompt bp=new BiometricPrompt.Builder(this)
                    .setTitle("Lås upp Min Ekonomi")
                    .setSubtitle("Verifiera dig för att visa din ekonomi")
                    .setNegativeButton("Avbryt",getMainExecutor(),(d,w)->finish())
                    .build();
            bp.authenticate(new CancellationSignal(),getMainExecutor(),new BiometricPrompt.AuthenticationCallback(){
                @Override public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult r){showTab(0);}
                @Override public void onAuthenticationError(int c,CharSequence m){
                    if(c==BiometricPrompt.BIOMETRIC_ERROR_NO_BIOMETRICS || c==BiometricPrompt.BIOMETRIC_ERROR_HW_NOT_PRESENT || c==BiometricPrompt.BIOMETRIC_ERROR_HW_UNAVAILABLE) showTab(0);
                }
            });
        }catch(Exception e){showTab(0);}
    }

    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    private TextView tv(String s,int sp,boolean bold,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(sp);v.setTextColor(color);if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;}
    private GradientDrawable bg(int color,int radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;}
    private LinearLayout card(){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(16),dp(14),dp(16),dp(14));c.setBackground(bg(CARD,22));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(7),0,dp(7));content.addView(c,lp);return c;}
    private void section(String s){TextView v=tv(s,21,true,Color.WHITE);v.setPadding(dp(2),dp(18),0,dp(6));content.addView(v);}
    private Button bigButton(String label,View.OnClickListener l){Button b=new Button(this);b.setAllCaps(false);b.setText(label);b.setTextSize(18);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setTextColor(Color.rgb(8,28,24));b.setBackground(bg(ACC,22));b.setOnClickListener(l);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(58));lp.setMargins(0,dp(10),0,dp(8));content.addView(b,lp);return b;}
    private Button smallButton(String label,View.OnClickListener l){Button b=new Button(this);b.setAllCaps(false);b.setText(label);b.setTextSize(14);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setTextColor(Color.WHITE);b.setBackground(bg(Color.rgb(54,87,118),14));b.setOnClickListener(l);return b;}

    private void showTab(int t){tab=t;LinearLayout shell=new LinearLayout(this);shell.setOrientation(LinearLayout.VERTICAL);shell.setBackgroundColor(BG);
        ScrollView sv=new ScrollView(this);sv.setFillViewport(true);content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(dp(18),dp(10),dp(18),dp(28));content.setBackgroundColor(BG);sv.addView(content);shell.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
        if(t==0) overview(); else if(t==1) posts(); else if(t==2) budget(); else if(t==3) bills(); else stats();
        shell.addView(navbar(),new LinearLayout.LayoutParams(-1,dp(76)));setContentView(shell);
    }

    private LinearLayout navbar(){LinearLayout n=new LinearLayout(this);n.setOrientation(LinearLayout.HORIZONTAL);n.setGravity(Gravity.CENTER);n.setPadding(dp(6),dp(5),dp(6),dp(5));n.setBackgroundColor(NAV);
        String[] labs={"⌂\nÖversikt","↕\nPoster","◎\nBudget","▤\nRäkningar","▥\nStatistik"};
        for(int i=0;i<5;i++){final int x=i;TextView b=tv(labs[i],12,i==tab,i==tab?ACC:MUTED);b.setGravity(Gravity.CENTER);b.setPadding(dp(2),dp(5),dp(2),dp(5));if(i==tab)b.setBackground(bg(Color.rgb(31,71,58),16));b.setOnClickListener(v->showTab(x));n.addView(b,new LinearLayout.LayoutParams(0,-1,1));}
        return n;
    }

    private void title(){TextView h=tv("Min Ekonomi",30,true,Color.WHITE);content.addView(h);}
    private String monthLabel(){return new SimpleDateFormat("MMMM yyyy",new Locale("sv","SE")).format(new Date());}
    private String today(){return new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());}
    private String ym(){return new SimpleDateFormat("yyyy-MM",Locale.US).format(new Date());}
    private String money(double v){return String.format(new Locale("sv","SE"),"%,.2f kr",v);}
    private double num(Object x){try{return Double.parseDouble(String.valueOf(x).replace(",","."));}catch(Exception e){return 0;}}
    private JSONArray arr(String k){try{return new JSONArray(p.getString(k,"[]"));}catch(Exception e){return new JSONArray();}}
    private void save(String k,JSONArray a){p.edit().putString(k,a.toString()).apply();}

    private double monthIncome(String m){double n=0;JSONArray a=arr("tx");for(int i=0;i<a.length();i++)try{JSONObject o=a.getJSONObject(i);if(o.optString("date").startsWith(m)&&"Inkomst".equalsIgnoreCase(o.optString("type")))n+=o.optDouble("amount");}catch(Exception ignored){}return n;}
    private double monthExpense(String m){double n=0;JSONArray a=arr("tx");for(int i=0;i<a.length();i++)try{JSONObject o=a.getJSONObject(i);if(o.optString("date").startsWith(m)&&!"Inkomst".equalsIgnoreCase(o.optString("type")))n+=o.optDouble("amount");}catch(Exception ignored){}return n;}
    private double currentFree(){return monthIncome(ym())-monthExpense(ym());}
    private long dateMs(String s){try{return new SimpleDateFormat("yyyy-MM-dd",Locale.US).parse(s).getTime();}catch(Exception e){return Long.MAX_VALUE;}}
    private int salaryDay(){return Math.max(1,Math.min(28,p.getInt("salaryDay",25)));}
    private Calendar nextSalary(){Calendar now=Calendar.getInstance();Calendar c=(Calendar)now.clone();c.set(Calendar.DAY_OF_MONTH,salaryDay());c.set(Calendar.HOUR_OF_DAY,0);c.set(Calendar.MINUTE,0);c.set(Calendar.SECOND,0);c.set(Calendar.MILLISECOND,0);if(c.before(now)){c.add(Calendar.MONTH,1);c.set(Calendar.DAY_OF_MONTH,salaryDay());}return c;}
    private Calendar prevSalary(){Calendar c=nextSalary();c.add(Calendar.MONTH,-1);return c;}
    private int daysToSalary(){long d=nextSalary().getTimeInMillis()-System.currentTimeMillis();return Math.max(1,(int)Math.ceil(d/86400000.0));}
    private double unpaidUntilSalary(){long lim=nextSalary().getTimeInMillis()+86399999L;double n=0;JSONArray a=arr("bills");for(int i=0;i<a.length();i++)try{JSONObject o=a.getJSONObject(i);if(!o.optBoolean("paid")&&!o.optBoolean("paused")&&dateMs(o.optString("due",o.optString("date")))<=lim)n+=o.optDouble("amount");}catch(Exception ignored){}return n;}
    private double plannedSavings(){return p.getFloat("plannedSavings",0);}
    private double forecast(){return currentFree()-unpaidUntilSalary()-plannedSavings();}
    private double dailySafe(){return Math.max(0,forecast()/daysToSalary());}
    private double plannedTotal(){double n=0;JSONArray a=arr("planned");for(int i=0;i<a.length();i++)try{JSONObject o=a.getJSONObject(i);if(!o.optBoolean("done"))n+=o.optDouble("amount");}catch(Exception ignored){}return n;}

    private void overview(){title();
        LinearLayout month=new LinearLayout(this);month.setGravity(Gravity.CENTER_VERTICAL);Button l=smallButton("‹",v->toast("Månadsbläddring kommer i nästa steg"));Button r=smallButton("›",v->toast("Månadsbläddring kommer i nästa steg"));TextView m=tv(cap(monthLabel()),16,false,MUTED);m.setGravity(Gravity.CENTER);month.addView(l,new LinearLayout.LayoutParams(dp(54),dp(50)));month.addView(m,new LinearLayout.LayoutParams(0,dp(50),1));month.addView(r,new LinearLayout.LayoutParams(dp(54),dp(50)));content.addView(month);

        LinearLayout hero=card();hero.setBackground(bg(Color.rgb(22,66,53),26));hero.addView(tv("Kvar att spendera",16,false,MUTED));TextView free=tv(money(currentFree()),31,true,ACC);free.setPadding(0,dp(5),0,dp(10));hero.addView(free);LinearLayout row=new LinearLayout(this);TextView inc=tv("Inkomster\n"+money(monthIncome(ym())),14,true,ACC);TextView exp=tv("Utgifter\n"+money(monthExpense(ym())),14,true,RED);row.addView(inc,new LinearLayout.LayoutParams(0,-2,1));row.addView(exp,new LinearLayout.LayoutParams(0,-2,1));hero.addView(row);

        section("Ekonomiläge");LinearLayout state=card();double d=dailySafe();String mode=forecast()<0?"Rött läge":d<50?"Gult läge":"Grönt läge";int mc=forecast()<0?RED:d<50?AMBER:ACC;state.addView(tv("●  "+mode,16,true,mc));String msg=forecast()<0?"Utgifterna fram till nästa lön överstiger det fria saldot.":"Cirka "+money(d)+" per dag fram till nästa lön.";TextView sm=tv(msg,14,false,MUTED);sm.setPadding(0,dp(7),0,dp(8));state.addView(sm);state.addView(tv("Prognos efter räkningar och planerat sparande:\n"+money(forecast()),14,true,forecast()>=0?ACC:RED));

        section("Löneperiod");LinearLayout pay=card();String from=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(prevSalary().getTime());String to=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(nextSalary().getTime());pay.addView(tv(from+" – "+to+" • lönedag "+salaryDay()+":e",13,false,MUTED));pay.addView(tv(money(forecast()),28,true,forecast()>=0?ACC:RED));pay.addView(tv("Efter räkningar och planerat sparande",13,false,MUTED));LinearLayout pr=new LinearLayout(this);pr.setPadding(0,dp(10),0,dp(10));pr.addView(tv("Kvar per dag\n"+money(dailySafe()),14,true,ACC),new LinearLayout.LayoutParams(0,-2,1));pr.addView(tv("Dagar kvar\n"+daysToSalary(),14,true,Color.WHITE),new LinearLayout.LayoutParams(0,-2,1));pay.addView(pr);Button ps=smallButton("⚙ Lönedag & planerat sparande",v->salaryDialog());pay.addView(ps,new LinearLayout.LayoutParams(-1,dp(46)));

        section("Konton & efter allt är betalt");LinearLayout all=card();LinearLayout ar=new LinearLayout(this);ar.addView(tv("Totalt saldo\n"+money(currentFree()),14,true,BLUE),new LinearLayout.LayoutParams(0,-2,1));ar.addView(tv("Räkningar kvar\n"+money(unpaidUntilSalary()),14,true,RED),new LinearLayout.LayoutParams(0,-2,1));all.addView(ar);TextView ff=tv("Fritt kvar: "+money(forecast()),22,true,forecast()>=0?ACC:RED);ff.setPadding(0,dp(12),0,0);all.addView(ff);

        section("Smarta insikter");LinearLayout ins=card();ins.addView(tv(smartInsight(),14,true,MUTED));TextView safe=tv("Du kan spendera ungefär "+money(dailySafe())+" idag och ändå hålla nuvarande prognos.",14,true,ACC);safe.setPadding(0,dp(9),0,0);ins.addView(safe);

        section("Planerade köp");LinearLayout pc=card();pc.addView(tv("Planerat totalt: "+money(plannedTotal()),16,true,Color.WHITE));pc.addView(tv("Fritt kvar om alla planerade köp görs: "+money(forecast()-plannedTotal()),13,false,(forecast()-plannedTotal())>=0?ACC:RED));pc.setOnClickListener(v->showPlanned());
        bigButton("＋  Lägg till transaktion",v->txDialog());

        section("Kommande räkningar");addUpcoming();section("Senaste");addRecent();
    }

    private String smartInsight(){Calendar c=Calendar.getInstance();c.add(Calendar.MONTH,-1);String pm=new SimpleDateFormat("yyyy-MM",Locale.US).format(c.getTime());double now=monthExpense(ym()),old=monthExpense(pm);String first=old<=0?"Fortsätt registrera poster – jämförelsen blir smartare med mer historik.":(now<=old?"Du har spenderat "+money(old-now)+" mindre än förra månaden.":"Du har spenderat "+money(now-old)+" mer än förra månaden.");HashMap<String,Double> map=new HashMap<>();JSONArray a=arr("tx");for(int i=0;i<a.length();i++)try{JSONObject o=a.getJSONObject(i);if(o.optString("date").startsWith(ym())&&!"Inkomst".equalsIgnoreCase(o.optString("type"))){String c0=o.optString("category","Övrigt");map.put(c0,map.getOrDefault(c0,0.0)+o.optDouble("amount"));}}catch(Exception ignored){}String best="–";double bv=0;for(String k:map.keySet())if(map.get(k)>bv){bv=map.get(k);best=k;}return first+"\nStörsta utgiftskategori: "+best+(bv>0?" ("+money(bv)+")":"")+"\nPrognos för nästa lön: "+money(forecast());}

    private void addUpcoming(){JSONArray a=arr("bills");ArrayList<JSONObject> list=new ArrayList<>();for(int i=0;i<a.length();i++)try{JSONObject o=a.getJSONObject(i);if(!o.optBoolean("paid")&&!o.optBoolean("paused"))list.add(o);}catch(Exception ignored){}Collections.sort(list,(x,y)->Long.compare(dateMs(x.optString("due",x.optString("date"))),dateMs(y.optString("due",y.optString("date")))));if(list.isEmpty()){TextView e=tv("Inga kommande räkningar.",15,false,MUTED);e.setGravity(Gravity.CENTER);e.setPadding(0,dp(15),0,dp(15));content.addView(e);return;}for(int i=0;i<Math.min(3,list.size());i++){JSONObject o=list.get(i);LinearLayout c=card();c.addView(tv(o.optString("name","Räkning"),16,true,Color.WHITE));c.addView(tv(o.optString("due",o.optString("date"))+"   "+money(o.optDouble("amount")),14,true,RED));}}
    private void addRecent(){JSONArray a=arr("tx");if(a.length()==0){TextView e=tv("Inga transaktioner ännu.",15,false,MUTED);e.setGravity(Gravity.CENTER);e.setPadding(0,dp(15),0,dp(15));content.addView(e);return;}for(int i=a.length()-1,c=0;i>=0&&c<5;i--,c++)try{JSONObject o=a.getJSONObject(i);LinearLayout r=card();LinearLayout line=new LinearLayout(this);line.setGravity(Gravity.CENTER_VERTICAL);TextView icon=tv("Inkomst".equalsIgnoreCase(o.optString("type"))?"↑":"•",20,true,Color.WHITE);icon.setGravity(Gravity.CENTER);icon.setBackground(bg(Color.rgb(31,54,73),14));line.addView(icon,new LinearLayout.LayoutParams(dp(48),dp(48)));LinearLayout txt=new LinearLayout(this);txt.setOrientation(LinearLayout.VERTICAL);txt.setPadding(dp(12),0,0,0);txt.addView(tv(o.optString("name","Post"),16,true,Color.WHITE));txt.addView(tv(o.optString("category","Övrigt")+" • "+o.optString("account","Lönekonto")+" •\n"+o.optString("date"),12,false,MUTED));line.addView(txt,new LinearLayout.LayoutParams(0,-2,1));boolean inc="Inkomst".equalsIgnoreCase(o.optString("type"));line.addView(tv((inc?"+ ":"− ")+money(o.optDouble("amount")),16,true,inc?ACC:RED));r.addView(line);}catch(Exception ignored){}
    }

    private void posts(){title();section("Poster");bigButton("＋  Lägg till transaktion",v->txDialog());JSONArray a=arr("tx");if(a.length()==0){content.addView(tv("Inga poster ännu.",15,false,MUTED));return;}for(int i=a.length()-1;i>=0;i--){final int idx=i;try{JSONObject o=a.getJSONObject(i);LinearLayout c=card();boolean inc="Inkomst".equalsIgnoreCase(o.optString("type"));c.addView(tv(o.optString("name","Post"),17,true,Color.WHITE));c.addView(tv(o.optString("date")+" • "+o.optString("category","Övrigt"),13,false,MUTED));c.addView(tv((inc?"+ ":"− ")+money(o.optDouble("amount")),18,true,inc?ACC:RED));c.setOnLongClickListener(v->{confirmDelete("tx",idx,()->showTab(1));return true;});}catch(Exception ignored){}}
    }

    private void budget(){title();section("Budget");LinearLayout b=card();double lim=p.getFloat("budget",0),spent=monthExpense(ym());b.addView(tv("Månadsbudget",15,false,MUTED));b.addView(tv(lim>0?money(lim):"Ingen budget satt",25,true,Color.WHITE));if(lim>0)b.addView(tv("Använt: "+money(spent)+" • Kvar: "+money(lim-spent),14,true,(lim-spent)>=0?ACC:RED));Button eb=smallButton("Ändra total budget",v->budgetDialog());b.addView(eb,new LinearLayout.LayoutParams(-1,dp(46)));
        section("Planerade köp");LinearLayout pl=card();pl.addView(tv("Planerat totalt: "+money(plannedTotal()),17,true,Color.WHITE));pl.addView(tv("Se, markera genomfört eller ta bort planerade köp.",13,false,MUTED));pl.setOnClickListener(v->showPlanned());
        section("Sparmål");LinearLayout sg=card();JSONArray g=arr("goals");sg.addView(tv(g.length()==0?"Inga sparmål ännu.":g.length()+" sparmål",17,true,Color.WHITE));Button ag=smallButton("＋ Lägg till sparmål",v->goalDialog());sg.addView(ag,new LinearLayout.LayoutParams(-1,dp(46)));
        section("Kategoribudget");renderCategories();
    }

    private void renderCategories(){JSONObject cb;try{cb=new JSONObject(p.getString("catbudgets","{}"));}catch(Exception e){cb=new JSONObject();}for(String c:CATS){double spend=catSpend(c);double lim=cb.optDouble(c,0);LinearLayout x=card();x.addView(tv(c,16,true,Color.WHITE));x.addView(tv("Utgift: "+money(spend)+(lim>0?" • Budget: "+money(lim):""),13,false,lim>0&&spend>lim?RED:MUTED));}}
    private double catSpend(String cat){double n=0;JSONArray a=arr("tx");for(int i=0;i<a.length();i++)try{JSONObject o=a.getJSONObject(i);if(o.optString("date").startsWith(ym())&&cat.equals(o.optString("category"))&&!"Inkomst".equalsIgnoreCase(o.optString("type")))n+=o.optDouble("amount");}catch(Exception ignored){}return n;}

    private void bills(){title();section("Räkningar");bigButton("＋  Lägg till räkning",v->billDialog());JSONArray a=arr("bills");if(a.length()==0){content.addView(tv("Inga räkningar sparade.",15,false,MUTED));return;}for(int i=0;i<a.length();i++){final int idx=i;try{JSONObject o=a.getJSONObject(i);LinearLayout c=card();c.addView(tv((o.optBoolean("paid")?"✓ ":"")+o.optString("name","Räkning"),17,true,Color.WHITE));c.addView(tv("Förfaller "+o.optString("due",o.optString("date"))+" • "+money(o.optDouble("amount")),14,false,o.optBoolean("paid")?MUTED:RED));if(!o.optBoolean("paid")){Button pay=smallButton("Markera betald",v->payBill(idx));c.addView(pay,new LinearLayout.LayoutParams(-1,dp(44)));}c.setOnLongClickListener(v->{confirmDelete("bills",idx,()->showTab(3));return true;});}catch(Exception ignored){}}
    }

    private void stats(){title();section("Statistik");double cur=monthExpense(ym());Calendar c=Calendar.getInstance();c.add(Calendar.MONTH,-1);String pm=new SimpleDateFormat("yyyy-MM",Locale.US).format(c.getTime());double old=monthExpense(pm);LinearLayout a=card();a.addView(tv("Den här månaden",14,false,MUTED));a.addView(tv(money(cur),26,true,RED));a.addView(tv("Förra månaden: "+money(old),14,false,MUTED));if(old>0)a.addView(tv(cur<=old?"↓ "+money(old-cur)+" mindre":"↑ "+money(cur-old)+" mer",15,true,cur<=old?ACC:RED));section("Utgifter per kategori");for(String x:CATS){double v=catSpend(x);if(v>0){LinearLayout cc=card();cc.addView(tv(x,16,true,Color.WHITE));cc.addView(tv(money(v),18,true,RED));}}
        section("Inställningar & backup");LinearLayout s=card();Button bio=smallButton("Biometriskt applås: "+(p.getBoolean("biometricLock",false)?"PÅ":"AV"),v->{p.edit().putBoolean("biometricLock",!p.getBoolean("biometricLock",false)).apply();showTab(4);});s.addView(bio,new LinearLayout.LayoutParams(-1,dp(46)));TextView note=tv("Skärmbilder är tillåtna i v2.2. Ekonomiska data ligger lokalt på telefonen.",13,false,MUTED);note.setPadding(0,dp(10),0,0);s.addView(note);
    }

    private void showPlanned(){LinearLayout shell=new LinearLayout(this);shell.setOrientation(LinearLayout.VERTICAL);shell.setBackgroundColor(BG);ScrollView sv=new ScrollView(this);content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(dp(18),dp(12),dp(18),dp(30));content.setBackgroundColor(BG);sv.addView(content);shell.addView(sv,new LinearLayout.LayoutParams(-1,0,1));TextView h=tv("Planerade köp",30,true,Color.WHITE);content.addView(h);bigButton("＋ Nytt planerat köp",v->plannedDialog());section("Planerade");JSONArray a=arr("planned");if(a.length()==0)content.addView(tv("Inga planerade köp.",15,false,MUTED));for(int i=0;i<a.length();i++){final int idx=i;try{JSONObject o=a.getJSONObject(i);LinearLayout c=card();c.addView(tv(o.optString("name","Planerat köp"),18,true,Color.WHITE));c.addView(tv(money(o.optDouble("amount")),22,true,AMBER));c.addView(tv("Fritt kvar efter köpet: "+money(forecast()-o.optDouble("amount")),14,false,(forecast()-o.optDouble("amount"))>=0?ACC:RED));if(!o.optBoolean("done")){Button done=smallButton("Markera som genomfört",v->completePlanned(idx));done.setBackground(bg(Color.rgb(31,105,67),14));c.addView(done,new LinearLayout.LayoutParams(-1,dp(48)));}Button del=smallButton("Ta bort",v->confirmDelete("planned",idx,this::showPlanned));del.setBackground(bg(Color.rgb(83,45,51),14));LinearLayout.LayoutParams dl=new LinearLayout.LayoutParams(-1,dp(44));dl.setMargins(0,dp(7),0,0);c.addView(del,dl);}catch(Exception ignored){}}Button back=smallButton("← Tillbaka till Budget",v->showTab(2));back.setTextColor(MUTED);shell.addView(back,new LinearLayout.LayoutParams(-1,dp(52)));setContentView(shell);}

    private void txDialog(){LinearLayout l=form();EditText name=input("Namn"),amount=input("Belopp"),date=input("Datum (ÅÅÅÅ-MM-DD)");date.setText(today());Spinner type=spinner(new String[]{"Utgift","Inkomst"});Spinner cat=spinner(CATS);l.addView(name);l.addView(amount);l.addView(date);l.addView(type);l.addView(cat);new AlertDialog.Builder(this).setTitle("Ny transaktion").setView(l).setNegativeButton("Avbryt",null).setPositiveButton("Spara",(d,w)->{try{JSONObject o=new JSONObject();o.put("name",name.getText().toString());o.put("amount",num(amount.getText()));o.put("date",date.getText().toString());o.put("type",type.getSelectedItem().toString());o.put("category",cat.getSelectedItem().toString());o.put("account","Lönekonto");JSONArray a=arr("tx");a.put(o);save("tx",a);showTab(tab);}catch(Exception ignored){}}).show();}
    private void billDialog(){LinearLayout l=form();EditText name=input("Räkningens namn"),amount=input("Belopp"),due=input("Förfallodatum (ÅÅÅÅ-MM-DD)");due.setText(today());l.addView(name);l.addView(amount);l.addView(due);new AlertDialog.Builder(this).setTitle("Ny räkning").setView(l).setNegativeButton("Avbryt",null).setPositiveButton("Spara",(d,w)->{try{JSONObject o=new JSONObject();o.put("name",name.getText().toString());o.put("amount",num(amount.getText()));o.put("due",due.getText().toString());o.put("paid",false);o.put("paused",false);JSONArray a=arr("bills");a.put(o);save("bills",a);showTab(3);}catch(Exception ignored){}}).show();}
    private void plannedDialog(){LinearLayout l=form();EditText name=input("Vad vill du köpa?"),amount=input("Belopp"),date=input("Planerat datum (ÅÅÅÅ-MM-DD)");date.setText(today());l.addView(name);l.addView(amount);l.addView(date);new AlertDialog.Builder(this).setTitle("Nytt planerat köp").setView(l).setNegativeButton("Avbryt",null).setPositiveButton("Spara",(d,w)->{try{JSONObject o=new JSONObject();o.put("name",name.getText().toString());o.put("amount",num(amount.getText()));o.put("date",date.getText().toString());o.put("done",false);JSONArray a=arr("planned");a.put(o);save("planned",a);showPlanned();}catch(Exception ignored){}}).show();}
    private void salaryDialog(){LinearLayout l=form();EditText day=input("Lönedag (1–28)"),save=input("Planerat sparande per lön");day.setText(String.valueOf(salaryDay()));save.setText(String.valueOf(plannedSavings()));l.addView(day);l.addView(save);new AlertDialog.Builder(this).setTitle("Lönedag & planerat sparande").setView(l).setNegativeButton("Avbryt",null).setPositiveButton("Spara",(d,w)->{int x=(int)num(day.getText());double sv=num(save.getText());p.edit().putInt("salaryDay",Math.max(1,Math.min(28,x))).putFloat("plannedSavings",(float)sv).apply();showTab(0);}).show();}
    private void budgetDialog(){EditText e=input("Månadsbudget");e.setText(String.valueOf(p.getFloat("budget",0)));new AlertDialog.Builder(this).setTitle("Månadsbudget").setView(e).setNegativeButton("Avbryt",null).setPositiveButton("Spara",(d,w)->{p.edit().putFloat("budget",(float)num(e.getText())).apply();showTab(2);}).show();}
    private void goalDialog(){LinearLayout l=form();EditText n=input("Sparmål"),target=input("Målbelopp");l.addView(n);l.addView(target);new AlertDialog.Builder(this).setTitle("Nytt sparmål").setView(l).setNegativeButton("Avbryt",null).setPositiveButton("Spara",(d,w)->{try{JSONObject o=new JSONObject();o.put("name",n.getText().toString());o.put("target",num(target.getText()));o.put("saved",0);JSONArray a=arr("goals");a.put(o);save("goals",a);showTab(2);}catch(Exception ignored){}}).show();}

    private void completePlanned(int idx){try{JSONArray a=arr("planned");JSONObject o=a.getJSONObject(idx);o.put("done",true);save("planned",a);JSONObject t=new JSONObject();t.put("name",o.optString("name"));t.put("amount",o.optDouble("amount"));t.put("date",today());t.put("type","Utgift");t.put("category","Shopping");t.put("account","Lönekonto");t.put("fromPlanned",true);JSONArray tx=arr("tx");tx.put(t);save("tx",tx);toast("Köpet markerades som genomfört och bokfördes som utgift.");showPlanned();}catch(Exception ignored){}}
    private void payBill(int idx){try{JSONArray a=arr("bills");JSONObject o=a.getJSONObject(idx);o.put("paid",true);save("bills",a);JSONObject t=new JSONObject();t.put("name",o.optString("name"));t.put("amount",o.optDouble("amount"));t.put("date",today());t.put("type","Utgift");t.put("category","Räkningar");t.put("account","Lönekonto");t.put("fromBill",true);JSONArray tx=arr("tx");tx.put(t);save("tx",tx);showTab(3);}catch(Exception ignored){}}
    private void confirmDelete(String key,int idx,Runnable after){new AlertDialog.Builder(this).setTitle("Ta bort?").setMessage("Det här går inte att ångra.").setNegativeButton("Avbryt",null).setPositiveButton("Ta bort",(d,w)->{try{JSONArray src=arr(key),dst=new JSONArray();for(int i=0;i<src.length();i++)if(i!=idx)dst.put(src.get(i));save(key,dst);after.run();}catch(Exception ignored){}}).show();}

    private LinearLayout form(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(18),dp(4),dp(18),0);return l;}
    private EditText input(String hint){EditText e=new EditText(this);e.setHint(hint);e.setSingleLine(true);e.setPadding(dp(10),dp(10),dp(10),dp(10));return e;}
    private Spinner spinner(String[] a){Spinner s=new Spinner(this);s.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,a));return s;}
    private String cap(String s){if(s==null||s.length()==0)return s;return Character.toUpperCase(s.charAt(0))+s.substring(1);}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
    @Override public void onBackPressed(){if(tab!=0)showTab(0);else super.onBackPressed();}
}
