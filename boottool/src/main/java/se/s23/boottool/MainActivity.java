package se.s23.boottool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {
    private static final int REQ_SHIZUKU = 1001;
    private TextView status;

    private final Shizuku.OnRequestPermissionResultListener permissionListener = (requestCode, grantResult) -> {
        if (requestCode == REQ_SHIZUKU) updateStatus();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shizuku.addRequestPermissionResultListener(permissionListener);
        setContentView(buildUi());
        updateStatus();
    }

    @Override
    protected void onDestroy() {
        Shizuku.removeRequestPermissionResultListener(permissionListener);
        super.onDestroy();
    }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        root.setBackgroundColor(Color.rgb(16, 19, 26));

        TextView title = text("SAMSUNG BOOT TOOL", 26, true);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        root.addView(title, full(dp(54)));

        TextView subtitle = text("Galaxy S23 Ultra • Shizuku", 15, false);
        subtitle.setTextColor(Color.rgb(160, 170, 185));
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle, full(dp(38)));

        status = text("Kontrollerar Shizuku…", 15, true);
        status.setTextColor(Color.rgb(124, 183, 255));
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sp = full(dp(58));
        sp.setMargins(0, dp(8), 0, dp(16));
        root.addView(status, sp);

        Button permission = button("GE SHIZUKU-BEHÖRIGHET");
        permission.setOnClickListener(v -> requestPermission());
        root.addView(permission, buttonParams());

        Button download = button("⬇  DOWNLOAD MODE");
        download.setOnClickListener(v -> confirm("Download Mode", "Mobilen startas om till Samsungs Download Mode.", "reboot download"));
        root.addView(download, buttonParams());

        Button recovery = button("🛠  RECOVERY MODE");
        recovery.setOnClickListener(v -> confirm("Recovery Mode", "Mobilen startas om till Recovery Mode.", "reboot recovery"));
        root.addView(recovery, buttonParams());

        Button reboot = button("⟳  VANLIG OMSTART");
        reboot.setOnClickListener(v -> confirm("Starta om", "Vill du starta om mobilen nu?", "reboot"));
        root.addView(reboot, buttonParams());

        TextView info = text("Kräver att Shizuku är startat. Appen ändrar inte firmware och låser inte upp bootloadern.", 13, false);
        info.setTextColor(Color.rgb(145, 152, 164));
        info.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams ip = full(-2);
        ip.setMargins(0, dp(22), 0, 0);
        root.addView(info, ip);
        return root;
    }

    private void requestPermission() {
        try {
            if (Shizuku.isPreV11()) {
                toast("För gammal Shizuku-version.");
                return;
            }
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                toast("Shizuku-behörighet är redan godkänd.");
                updateStatus();
                return;
            }
            Shizuku.requestPermission(REQ_SHIZUKU);
        } catch (Throwable e) {
            status.setText("Shizuku är inte startat");
            toast("Starta Shizuku och försök igen.");
        }
    }

    private void updateStatus() {
        try {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                status.setText("✓ Shizuku ansluten och godkänd");
            } else {
                status.setText("Shizuku-behörighet saknas");
            }
        } catch (Throwable e) {
            status.setText("Shizuku är inte startat");
        }
    }

    private void confirm(String title, String message, String command) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Avbryt", null)
                .setPositiveButton("Fortsätt", (d, w) -> runCommand(command))
                .show();
    }

    private void runCommand(String command) {
        try {
            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                toast("Ge appen Shizuku-behörighet först.");
                requestPermission();
                return;
            }

            // Shizuku 13 keeps newProcess for migration but marks it private/deprecated.
            // Reflection lets this tiny utility execute the same ADB-shell reboot command.
            Method m = Shizuku.class.getDeclaredMethod("newProcess", String[].class, String[].class, String.class);
            m.setAccessible(true);
            Object process = m.invoke(null, new Object[]{new String[]{"sh", "-c", command}, null, null});
            if (process == null) throw new IllegalStateException("Kunde inte starta shell-process");
            toast("Kommandot skickades: " + command);
        } catch (Throwable e) {
            status.setText("Kommandot misslyckades");
            toast("Samsung/firmware blockerade kommandot eller Shizuku saknar åtkomst.");
        }
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(16);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(42, 91, 145)));
        return b;
    }

    private TextView text(String s, int size, boolean bold) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextSize(size);
        if (bold) v.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        return v;
    }

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams p = full(dp(58));
        p.setMargins(0, dp(7), 0, dp(7));
        return p;
    }

    private LinearLayout.LayoutParams full(int height) {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
