package com.user.iconpack;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IconListActivity extends Activity {
    private static final String TAG = "IconListActivity";
    private static final String PREFS = "iconpack_prefs";
    private static final String ICONS_KEY = "icons_list";
    private static final String AUTHORITY = "com.user.iconpack.fileprovider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent incoming = getIntent();
        String action = incoming != null ? incoming.getAction() : null;
        int flags = incoming != null ? incoming.getFlags() : 0;
        String caller = getCallingPackage();
        Log.i(TAG, "IconListActivity started action=" + action + " flags=0x" + Integer.toHexString(flags) + " caller=" + caller);

        ListView listView = new ListView(this);
        setContentView(listView);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String raw = prefs.getString(ICONS_KEY, "");

        final List<String> display = new ArrayList<>();
        final List<Uri> uriList = new ArrayList<>();

        if (!raw.isEmpty()) {
            String[] items = raw.split(";");
            for (String it : items) {
                // format: timestamp|originalUri|localPath|publicUri(optional)
                String[] parts = it.split("\\|", 4);
                if (parts.length >= 3) {
                    String timestamp = parts[0];
                    String original = parts[1];
                    String localPath = parts[2];
                    String publicUri = parts.length >= 4 ? parts[3] : "";

                    String name = localPath != null ? localPath : timestamp;
                    display.add(name + " (" + timestamp + ")");

                    // Prefer publicUri (if present) so other apps can access it. Fall back to FileProvider for local copies.
                    if (publicUri != null && !publicUri.isEmpty()) {
                        uriList.add(Uri.parse(publicUri));
                    } else {
                        try {
                            File f = new File(getFilesDir(), localPath);
                            Uri fp = FileProvider.getUriForFile(this, AUTHORITY, f);
                            uriList.add(fp);
                        } catch (Exception e) {
                            Log.w(TAG, "Failed to get FileProvider URI for local file, falling back to file://", e);
                            uriList.add(Uri.fromFile(new File(getFilesDir(), localPath)));
                        }
                    }
                }
            }
        }

        if (display.isEmpty()) display.add("No saved icons yet");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (uriList.isEmpty()) return;
                Uri u = uriList.get(position);
                try {
                    Intent result = new Intent();
                    result.setData(u);

                    // Grant read permission on the returned URI so the caller (launcher/Theme Park) can access it
                    result.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    // Add ClipData for broader compatibility with some launchers
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ClipData clip = ClipData.newRawUri("icon", u);
                        result.setClipData(clip);
                    }

                    // If we know who called us, explicitly grant URI permission to that package
                    try {
                        String caller = getCallingPackage();
                        if (caller != null) grantUriPermission(caller, u, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Log.i(TAG, "Granted URI permission to " + caller + " for " + u);
                    } catch (Exception ignore) {
                        Log.w(TAG, "Could not grant explicit URI permission to caller", ignore);
                    }

                    setResult(RESULT_OK, result);
                } catch (Exception e) {
                    Log.w(TAG, "Failed to return selected URI", e);
                    Toast.makeText(IconListActivity.this, "Failed to select icon", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });
    }
}
