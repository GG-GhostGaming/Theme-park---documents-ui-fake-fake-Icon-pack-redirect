package com.user.iconpack;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_PICKER = 2001;
    private static final String PREFS = "iconpack_prefs";
    private static final String ICONS_KEY = "icons_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the external icon picker (ginlemon) for result so we can receive the URI it returns
        try {
            Intent intent = new Intent();
            intent.setClassName("ginlemon.iconpackstudio", "ginlemon.iconpackstudio.editor.configPickerActivity.IconPickerActivity");

            // Start for result so we can receive the selected file URI
            startActivityForResult(intent, REQUEST_PICKER);
        } catch (Exception e) {
            Log.w(TAG, "Could not start ginlemon picker; falling back to system picker", e);
            Toast.makeText(this, "Falling back to system picker", Toast.LENGTH_SHORT).show();

            // Fallback to system picker
            Intent pick = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pick.addCategory(Intent.CATEGORY_OPENABLE);
            pick.setType("*/*");
            startActivityForResult(pick, REQUEST_PICKER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_PICKER) {
            finish();
            return;
        }

        if (resultCode != RESULT_OK || data == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Uri uri = data.getData();
        if (uri == null) {
            Toast.makeText(this, "Picker returned no URI", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            // Persist permission so we can access the content later
            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            ContentResolver resolver = getContentResolver();
            resolver.takePersistableUriPermission(uri, takeFlags);

            // Optionally copy the content into our app private storage for faster access and to have a local copy
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File iconsDir = new File(getFilesDir(), "icons");
            if (!iconsDir.exists()) iconsDir.mkdirs();
            File outFile = new File(iconsDir, "icon_" + timestamp);

            try (InputStream in = resolver.openInputStream(uri); FileOutputStream out = new FileOutputStream(outFile)) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }

            // Save metadata: store the persisted content URI and a display name in SharedPreferences
            String entry = timestamp + "|" + uri.toString() + "|" + outFile.getName();
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            String existing = prefs.getString(ICONS_KEY, "");
            String updated = existing.isEmpty() ? entry : (existing + ";" + entry);
            prefs.edit().putString(ICONS_KEY, updated).apply();

            Toast.makeText(this, "Saved icon for use in icon picker", Toast.LENGTH_SHORT).show();

            // Launch IconListActivity so the user (or launcher) can pick one of our saved icons
            Intent show = new Intent(this, IconListActivity.class);
            startActivity(show);

        } catch (Exception e) {
            Log.w(TAG, "Failed to persist or save picked file", e);
            Toast.makeText(this, "Failed to save selected file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Finish the initial flow (IconListActivity will be visible)
        finish();
    }
}
