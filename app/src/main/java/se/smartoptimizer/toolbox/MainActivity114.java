package se.smartoptimizer.toolbox;

import android.os.Bundle;
import android.graphics.Color;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity114 extends MainActivity112 {
  @Override public void onCreate(Bundle b){
    PK = new String[][]{
      {"Samsung Free","com.samsung.android.app.spage","Säker","Samsung","Nyhets-/innehållspanel. Påverkar inte kärnfunktioner."},
      {"Global Goals","com.samsung.sree","Säker","Samsung","Samsung Global Goals."},
      {"Samsung Kids","com.samsung.android.kidsinstaller","Säker","Samsung","Samsung Kids-installerare."},
      {"Bixby Voice","com.samsung.android.bixby.agent","Säker","Samsung","Bixby-röst. Ingår i din profil eftersom du inte använder Bixby."},
      {"Bixby Wakeup","com.samsung.android.bixby.wakeup","Säker","Samsung","Bixby röstväckning. Ingår i din profil eftersom du inte använder Bixby."},
      {"Bixby Vision Framework","com.samsung.android.bixbyvision.framework","Säker","Samsung","Bixby Vision-ramverk. Ingår i din profil eftersom du inte använder Bixby."},
      {"AR Emoji","com.samsung.android.aremoji","Valfritt","Samsung","AR Emoji. Behåll om du använder AR-avatarer i kameran."},
      {"AR Avatar preload","com.samsung.android.app.camera.sticker.facearavatar.preload","Valfritt","Samsung","Förinstallerade AR-avatarresurser."},
      {"Sticker Center","com.samsung.android.stickercenter","Valfritt","Samsung","Samsung-klistermärken och kameraeffekter."},
      {"Live Wallpaper","com.samsung.android.wallpaper.live","Valfritt","Samsung","Samsung levande bakgrunder."},
      {"Safety Information","com.samsung.android.safetyinformation","Valfritt","Samsung","Informationsapp för säkerhets-/regeltexter."},
      {"Samsung Members","com.samsung.android.voc","Valfritt","Samsung","Support och diagnostik."},
      {"SmartThings","com.samsung.android.oneconnect","Försiktig","Samsung","Påverkar SmartThings och anslutna enheter."},
      {"Samsung Health","com.sec.android.app.shealth","Försiktig","Samsung","Påverkar Samsung Health och vissa Watch-funktioner."},
      {"Samsung Cloud","com.samsung.android.scloud","Försiktig","Samsung","Kan påverka synk och backup."},
      {"Magnifier","com.sec.android.app.magnifier","Valfritt","Samsung","Förstoringsverktyg. Behåll om du använder tillgänglighetsfunktionen."},
      {"Easy Mode Contacts","com.sec.android.widgetapp.easymodecontactswidget","Valfritt","Samsung","Kontaktwidget för Enkelt läge."},
      {"Google TV","com.google.android.videos","Säker","Google","Google TV."},
      {"Gemini","com.google.android.apps.bard","Valfritt","Google","Google Gemini. Behåll om du använder Gemini."},
      {"Google Feedback","com.google.android.feedback","Valfritt","Google","Feedback-/felrapporteringskomponent."},
      {"Google Meet","com.google.android.apps.tachyon","Valfritt","Google","Google Meet."},
      {"YouTube Music","com.google.android.apps.youtube.music","Valfritt","Google","YouTube Music."},
      {"Google One","com.google.android.apps.subscriptions.red","Valfritt","Google","Google One och backup."},
      {"Android Auto","com.google.android.projection.gearhead","Försiktig","Google","Behåll om du använder Android Auto."},
      {"Microsoft Link","com.microsoft.appmanager","Valfritt","Microsoft","Länk till Windows/Phone Link-komponent."},
      {"OneDrive","com.microsoft.skydrive","Valfritt","Microsoft","Behåll om du använder OneDrive eller Galleri-synk."},
      {"LinkedIn","com.linkedin.android","Säker","Övrigt","LinkedIn."},
      {"Meta App Manager","com.facebook.appmanager","Säker","Meta","Meta bakgrundskomponent. Facebook-appen kan installeras/användas separat."},
      {"Meta Services","com.facebook.services","Säker","Meta","Meta bakgrundstjänster."},
      {"Meta Installer","com.facebook.system","Säker","Meta","Meta systeminstallerare."}
    };
    super.onCreate(b);
  }

  @Override void show(){
    base("S23 ULTRA TOOLBOX 10.14","Dashboard 4.0 • Smart Debloat 8.0 • Recovery 2.0",false);
    dashboard();
    if(p.getBoolean("update",false))card("🆕 Efter Samsung-uppdatering","Kontrollera om paket eller inställningar ändrats.",()->afterUpdate());
    card("🧠 Smart Advisor 4.0","Jämför nuläget med din lokala historik.",()->advisor());
    card("🛡️ Smart Debloat 8.0","S23 Ultra Safe-profil • Bixby • risk • Recovery.",()->debloat());
    card("📦 App Manager 2.0","Sök, filtrera och inspektera installerade appar.",()->apps());
    card("🌡️ Thermal Watch 4.0","Trend, min/max/medel och sparad historik.",()->thermal());
    card("⚡ Charging Test Pro","1/5/10 min • effekt • temperatur • min/medel/max.",()->battery());
    card("🚗 GPS/Jobbläge","Skärm vaken, rotation och återställning.",()->profiles());
    card("🖥️ DeX Center 2.0","Extern skärm, temperatur och snabbval.",()->dex());
    card("🧯 Recovery Center 2.0","Ångra senaste och se exakt vad Toolbox ändrat.",()->recovery());
    card("💾 Backup / Diagnostik","Exportera rapport och Toolbox-status.",()->diag());
    card("🔧 Shizuku Tools",shStatus(),()->shPage());
    card("📸 Camera Guide 5.1","S23 Ultra-råd för foto och video.",()->camera());
    card("🕘 Historik","Vad har Toolbox ändrat?",()->history());
    card("🎮 Grafik & Vulkan","HWUI, Adreno/Vulkan, ANGLE och säkert A/B-test.",()->graphicsPage());
  }

  @Override void debloat(){
    base("🛡️ SMART DEBLOAT 8.0","S23 Ultra Safe • Samsung • Google • Meta • Recovery",true);
    note(shStatus());
    note("Safe-profil 10.14: kärnfunktioner som DeX, kamera, S Pen, telefoni/IMS, Wi‑Fi, Bluetooth, Knox, Samsung Pass, positionering och systemuppdatering finns inte i säker batch.");
    btn("✨ Kör S23 Ultra Safe batch",()->recommended());
    String last="";
    for(String[]a:PK)if(installed(a[1])){
      if(!a[3].equals(last)){sec(a[3]);last=a[3];}
      TextView z=text(a[0]+" • "+a[2],18,true);
      z.setTextColor(a[2].equals("Försiktig")?AMBER:GREEN);
      root.addView(z);
      note(a[4]+"\n"+a[1]+"\nStatus: "+(enabled(a[1])?"✅ Aktiv":"⛔ Avaktiverad"));
      if(enabled(a[1]))btn("Avaktivera "+a[0],()->ask(a));
      else btn("Återställ "+a[0],()->shell("pm enable --user 0 "+a[1],a[1],false));
    }
    sec("Skydd");
    note("Den säkra batchen använder avaktivering per användare, skapar snapshot först och kan återställas i Recovery Center. Valfria och försiktiga paket körs aldrig automatiskt.");
  }
}
