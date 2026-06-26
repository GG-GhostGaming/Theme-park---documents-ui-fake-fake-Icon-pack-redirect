package com.user.iconpack;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TARGET_PACKAGE = "ginlemon.iconpackstudio";
    private static final String TARGET_CLASS = "ginlemon.iconpackstudio.editor.configPickerActivity.IconPickerActivity";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Attempt 1: explicit component with ACTION_MAIN (preferred)
        Intent explicit = new Intent(Intent.ACTION_MAIN);
        explicit.setComponent(new ComponentName(TARGET_PACKAGE, TARGET_CLASS));
        explicit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(explicit);
            finish();
            return;
        } catch (Exception e) {
            Log.w(TAG, "Explicit component launch failed", e);
        }

        // Attempt 2: package + MAIN/LAUNCHER (launches the app's main activity)
        try {
            Intent pkgMain = new Intent(Intent.ACTION_MAIN);
            pkgMain.addCategory(Intent.CATEGORY_LAUNCHER);
            pkgMain.setPackage(TARGET_PACKAGE);
            pkgMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(pkgMain);
            finish();
            return;
        } catch (Exception e) {
            Log.w(TAG, "Package MAIN launch failed", e);
        }

        // Attempt 3: implicit VIEW with package (best-effort)
        try {
            Intent implicitView = new Intent(Intent.ACTION_VIEW);
            implicitView.setPackage(TARGET_PACKAGE);
            implicitView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(implicitView);
            finish();
            return;
        } catch (Exception e) {
            Log.w(TAG, "Implicit VIEW launch failed", e);
        }

        // Fallback: open system document picker
        try {
            Intent docIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            docIntent.addCategory(Intent.CATEGORY_OPENABLE);
            docIntent.setType("*/*");
            docIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(docIntent);
        } catch (Exception e) {
            Log.w(TAG, "Document picker fallback failed", e);
            Toast.makeText(this, "Failed to open target activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        finish();
    }
}
