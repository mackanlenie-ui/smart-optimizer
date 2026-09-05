package se.samsungfirmware.downloader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;

public class MainActivity extends Activity {
    static { System.loadLibrary("firmware_native"); }

    private EditText modelField, regionField;
    private TextView status, progressText, firmwareText;
    private ProgressBar progressBar;
    private Button checkButton, downloadButton, permissionButton;
    private long totalBytes = 0;

    private native boolean nativeInit(Object context);
    private native String nativeCheck(String model, String region);
    private native String nativeDownload(String model, String region, String outDir);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(10,14,22));
        getWindow().setNavigationBarColor(Color.rgb(10,14,22));
        buildUi();
        boolean ok = nativeInit(getApplicationContext());
        status.setText(ok ? "Redo • Samsung FUS initierad" : "TLS-initiering misslyckades");
        refreshPermission();
    }

    private TextView text(String s, int sp, boolean bold) {
        TextView v = new TextView(this);
        v.setText(s); v.setTextSize(sp); v.setTextColor(Color.WHITE);
        if (bold) v.setTypeface(null, android.graphics.Typeface.BOLD);
        v.setPadding(0, 8, 0, 8);
        return v;
    }

    private Button button(String s) {
        Button b = new Button(this);
        b.setText(s); b.setTextSize(15); b.setAllCaps(false);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 8, 0, 8); b.setLayoutParams(p);
        return b;
    }

    private void buildUi() {
        ScrollView sv = new ScrollView(this); sv.setFillViewport(true); sv.setBackgroundColor(Color.rgb(10,14,22));
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(32,48,32,48);
        sv.addView(root);

        TextView title = text("Samsung Firmware Downloader", 25, true); root.addView(title);
        TextView sub = text("Direkt från Samsung FUS • laddar ner och dekrypterar", 14, false); sub.setTextColor(Color.LTGRAY); root.addView(sub);

        modelField = new EditText(this); modelField.setHint("Modell"); modelField.setText("SM-S918B"); modelField.setTextColor(Color.WHITE); modelField.setHintTextColor(Color.GRAY); root.addView(modelField);
        regionField = new EditText(this); regionField.setHint("Region / CSC"); regionField.setText("EUX"); regionField.setTextColor(Color.WHITE); regionField.setHintTextColor(Color.GRAY); root.addView(regionField);

        permissionButton = button("Ge åtkomst till Download-mappen"); root.addView(permissionButton);
        permissionButton.setOnClickListener(v -> requestAllFiles());

        checkButton = button("Kontrollera senaste firmware"); root.addView(checkButton);
        downloadButton = button("Ladda ner firmware"); root.addView(downloadButton);
        downloadButton.setEnabled(false);

        firmwareText = text("Firmware: inte kontrollerad", 14, false); firmwareText.setTextColor(Color.rgb(180,210,255)); root.addView(firmwareText);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal); progressBar.setMax(1000); root.addView(progressBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 40));
        progressText = text("0 %", 15, true); progressText.setGravity(Gravity.CENTER_HORIZONTAL); root.addView(progressText);
        status = text("Startar…", 14, false); status.setTextColor(Color.LTGRAY); root.addView(status);

        TextView note = text("Filen sparas som färdig dekrypterad ZIP i /Download. Appen flashar inte telefonen.", 13, false); note.setTextColor(Color.GRAY); root.addView(note);

        checkButton.setOnClickListener(v -> checkFirmware());
        downloadButton.setOnClickListener(v -> startDownload());
        setContentView(sv);
    }

    private void requestAllFiles() {
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            try {
                Intent i = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(i);
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
            }
        }
    }

    private boolean hasFileAccess() {
        return android.os.Build.VERSION.SDK_INT < 30 || Environment.isExternalStorageManager();
    }

    @Override protected void onResume() { super.onResume(); refreshPermission(); }

    private void refreshPermission() {
        if (permissionButton == null) return;
        boolean ok = hasFileAccess();
        permissionButton.setText(ok ? "✓ Download-åtkomst klar" : "Ge åtkomst till Download-mappen");
        downloadButton.setEnabled(ok && firmwareText.getText().toString().contains("S918"));
    }

    private String model() { return modelField.getText().toString().trim().toUpperCase(Locale.ROOT); }
    private String region() { return regionField.getText().toString().trim().toUpperCase(Locale.ROOT); }

    private void checkFirmware() {
        setBusy(true); status.setText("Kontaktar Samsung FUS…");
        new Thread(() -> {
            String r = nativeCheck(model(), region());
            runOnUiThread(() -> {
                setBusy(false);
                if (r.startsWith("OK|")) {
                    String[] p = r.split("\\|", 4);
                    firmwareText.setText("Firmware: " + (p.length > 1 ? p[1] : "okänd") + "\nFil: " + (p.length > 2 ? p[2] : ""));
                    status.setText("Senaste firmware hittad hos Samsung ✓");
                    refreshPermission();
                } else {
                    firmwareText.setText("Firmware: fel"); status.setText(r);
                }
            });
        }).start();
    }

    private void startDownload() {
        if (!hasFileAccess()) { requestAllFiles(); return; }
        setBusy(true); downloadButton.setEnabled(false); progressBar.setProgress(0); progressText.setText("0 %");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        status.setText("Startar nedladdningen…");
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        new Thread(() -> {
            String r = nativeDownload(model(), region(), dir.getAbsolutePath());
            runOnUiThread(() -> {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                setBusy(false);
                if (r.startsWith("OK|")) {
                    progressBar.setProgress(1000); progressText.setText("100 %");
                    status.setText("KLART ✓\n" + r.substring(3));
                } else status.setText(r);
                refreshPermission();
            });
        }).start();
    }

    private void setBusy(boolean busy) {
        checkButton.setEnabled(!busy); modelField.setEnabled(!busy); regionField.setEnabled(!busy); permissionButton.setEnabled(!busy);
    }

    public void onNativeLength(long total) {
        totalBytes = total;
        runOnUiThread(() -> status.setText(String.format(Locale.US, "Laddar ner %.2f GB med 8 anslutningar…", total / 1_000_000_000.0)));
    }

    public void onNativeProgress(long position, long total) {
        runOnUiThread(() -> {
            long t = total > 0 ? total : totalBytes;
            if (t <= 0) return;
            int x = (int)Math.min(1000, (position * 1000L) / t);
            progressBar.setProgress(x);
            progressText.setText(String.format(Locale.US, "%.1f %%  •  %.2f / %.2f GB", x / 10.0, position / 1_000_000_000.0, t / 1_000_000_000.0));
        });
    }

    public void onNativeStatus(String s) { runOnUiThread(() -> status.setText(s)); }
}
