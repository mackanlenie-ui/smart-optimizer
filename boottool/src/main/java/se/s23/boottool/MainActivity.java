package se.s23.boottool;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private TextView status;
    private EditText pairPort, pairCode, connectPort;
    private String adbPath;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        adbPath = getApplicationInfo().nativeLibraryDir + "/libadb.so";
        setContentView(buildUi());
        pairPort.setText(getPreferences(0).getString("pairPort", ""));
        connectPort.setText(getPreferences(0).getString("connectPort", ""));
        status.setText(new File(adbPath).exists() ? "✓ Lokal ADB-motor laddad" : "ADB-motorn saknas");
    }

    private ScrollView buildUi() {
        ScrollView sv = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(26), dp(22), dp(30));
        root.setBackgroundColor(Color.rgb(16,19,26));
        sv.addView(root);

        TextView title = text("SAMSUNG BOOT TOOL", 25, true); title.setTextColor(Color.WHITE); title.setGravity(Gravity.CENTER); root.addView(title);
        TextView sub = text("Galaxy S23 Ultra • Wireless ADB • v1.3", 14, false); sub.setTextColor(Color.rgb(160,170,185)); sub.setGravity(Gravity.CENTER); root.addView(sub, margins(dp(42),0,0,dp(8)));
        status = text("Startar…", 15, true); status.setTextColor(Color.rgb(124,183,255)); status.setGravity(Gravity.CENTER); root.addView(status, margins(dp(54),0,0,dp(12)));

        TextView help = text("1. Öppna Utvecklaralternativ → Trådlös felsökning.\n2. Tryck 'Parkoppla enhet med parkopplingskod'.\n3. Ange parkopplingsport + 6-siffrig kod här.\n4. Ange sedan ANSLUTNINGSPORTEN som visas på huvudskärmen för Trådlös felsökning.", 14, false);
        help.setTextColor(Color.LTGRAY); root.addView(help, margins(-2,0,0,dp(12)));

        pairPort = field("Parkopplingsport", false); root.addView(pairPort, margins(dp(54),0,0,dp(6)));
        pairCode = field("6-siffrig parkopplingskod", true); root.addView(pairCode, margins(dp(54),0,0,dp(8)));
        Button pair = button("🔗  PARKOPPLA MED ADB"); pair.setOnClickListener(v -> pair()); root.addView(pair, margins(dp(56),0,0,dp(12)));

        connectPort = field("Anslutningsport", false); root.addView(connectPort, margins(dp(54),0,0,dp(8)));
        Button connect = button("✓  ANSLUT LOKALT"); connect.setOnClickListener(v -> connect()); root.addView(connect, margins(dp(56),0,0,dp(10)));

        Button download = button("⬇  DOWNLOAD MODE VIA ADB"); download.setOnClickListener(v -> confirmDownload()); root.addView(download, margins(dp(60),0,0,dp(8)));
        Button recovery = button("🛠  RECOVERY MODE VIA ADB"); recovery.setOnClickListener(v -> runAdbReboot("recovery")); root.addView(recovery, margins(dp(56),0,0,dp(8)));
        Button reboot = button("⟳  VANLIG OMSTART VIA ADB"); reboot.setOnClickListener(v -> runAdbReboot("")); root.addView(reboot, margins(dp(56),0,0,dp(16)));

        TextView note = text("v1.3 använder en riktig lokal ADB-klient, inte Shizuku. ADB-motorn kommer från det öppna LADB-projektet (GPLv3).", 12, false);
        note.setTextColor(Color.rgb(140,148,160)); note.setGravity(Gravity.CENTER); root.addView(note);
        return sv;
    }

    private EditText field(String hint, boolean code) {
        EditText e = new EditText(this); e.setHint(hint); e.setHintTextColor(Color.GRAY); e.setTextColor(Color.WHITE);
        e.setSingleLine(true); e.setPadding(dp(14),0,dp(14),0);
        e.setInputType(code ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_NUMBER);
        e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(75,100,130)));
        return e;
    }

    private void pair() {
        final String port = pairPort.getText().toString().trim(); final String code = pairCode.getText().toString().trim();
        if (port.isEmpty() || code.length() != 6) { toast("Ange parkopplingsport och den 6-siffriga koden."); return; }
        getPreferences(0).edit().putString("pairPort", port).apply();
        status.setText("Parkopplar… håll parkopplingsrutan öppen");
        new Thread(() -> {
            try {
                runSimple("kill-server");
                Process p = adbProcess("pair", "localhost:" + port);
                Thread.sleep(900);
                OutputStreamWriter w = new OutputStreamWriter(p.getOutputStream()); w.write(code + "\n"); w.flush();
                boolean done = p.waitFor(15, TimeUnit.SECONDS);
                String out = readAll(p);
                if (!done) p.destroyForcibly();
                boolean ok = out.toLowerCase().contains("successfully paired") || (done && p.exitValue() == 0);
                runOnUiThread(() -> status.setText(ok ? "✓ ADB parkopplad" : "Parkoppling misslyckades: " + shortOut(out)));
            } catch (Throwable e) { runOnUiThread(() -> status.setText("Parkoppling misslyckades: " + e.getClass().getSimpleName())); }
        }).start();
    }

    private void connect() {
        final String port = connectPort.getText().toString().trim();
        if (port.isEmpty()) { toast("Ange anslutningsporten från Trådlös felsökning."); return; }
        getPreferences(0).edit().putString("connectPort", port).apply(); status.setText("Ansluter till localhost:" + port + "…");
        new Thread(() -> {
            try {
                runSimple("start-server");
                String out = runSimple("connect", "localhost:" + port);
                String dev = runSimple("devices");
                boolean ok = dev.contains("localhost:" + port + "\tdevice") || out.toLowerCase().contains("connected to");
                runOnUiThread(() -> status.setText(ok ? "✓ Wireless ADB ansluten" : "ADB kunde inte ansluta: " + shortOut(out)));
            } catch (Throwable e) { runOnUiThread(() -> status.setText("ADB-anslutning misslyckades")); }
        }).start();
    }

    private void confirmDownload() {
        new AlertDialog.Builder(this).setTitle("Download Mode via ADB")
            .setMessage("Detta kör samma typ av kommando som i Termux: adb reboot download. Mobilen startas om direkt.")
            .setNegativeButton("Avbryt", null).setPositiveButton("FORTSÄTT", (d,w) -> runAdbReboot("download")).show();
    }

    private void runAdbReboot(String target) {
        final String port = connectPort.getText().toString().trim();
        if (port.isEmpty()) { toast("Anslut Wireless ADB först."); return; }
        status.setText("Skickar ADB-kommandot…");
        new Thread(() -> {
            try {
                List<String> a = new ArrayList<>(); a.add("-s"); a.add("localhost:" + port); a.add("reboot"); if (!target.isEmpty()) a.add(target);
                String out = runSimple(a.toArray(new String[0]));
                runOnUiThread(() -> status.setText("ADB-kommandot skickat" + (out.isEmpty() ? "" : ": " + shortOut(out))));
            } catch (Throwable e) { runOnUiThread(() -> status.setText("ADB-kommandot misslyckades")); }
        }).start();
    }

    private Process adbProcess(String... args) throws Exception {
        List<String> cmd = new ArrayList<>(); cmd.add(adbPath); for (String a:args) cmd.add(a);
        ProcessBuilder pb = new ProcessBuilder(cmd); pb.directory(getFilesDir()); pb.redirectErrorStream(true);
        pb.environment().put("HOME", getFilesDir().getAbsolutePath()); pb.environment().put("TMPDIR", getCacheDir().getAbsolutePath());
        return pb.start();
    }
    private String runSimple(String... args) throws Exception { Process p=adbProcess(args); p.waitFor(20,TimeUnit.SECONDS); String s=readAll(p); if(p.isAlive())p.destroyForcibly(); return s; }
    private String readAll(Process p) throws Exception { BufferedReader r=new BufferedReader(new InputStreamReader(p.getInputStream())); StringBuilder b=new StringBuilder(); String l; while((l=r.readLine())!=null)b.append(l).append('\n'); return b.toString().trim(); }
    private String shortOut(String s) { if(s==null||s.trim().isEmpty()) return "inget svar"; s=s.replace('\n',' '); return s.length()>90?s.substring(0,90)+"…":s; }
    private Button button(String s){ Button b=new Button(this); b.setText(s); b.setTextColor(Color.WHITE); b.setTextSize(15); b.setAllCaps(false); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(42,91,145))); return b; }
    private TextView text(String s,int z,boolean bold){ TextView v=new TextView(this); v.setText(s); v.setTextSize(z); if(bold)v.setTypeface(android.graphics.Typeface.DEFAULT_BOLD); return v; }
    private LinearLayout.LayoutParams margins(int h,int l,int t,int b){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,h); p.setMargins(l,t,0,b); return p; }
    private int dp(int v){ return (int)(v*getResources().getDisplayMetrics().density+.5f); }
    private void toast(String s){ Toast.makeText(this,s,Toast.LENGTH_LONG).show(); }
}
