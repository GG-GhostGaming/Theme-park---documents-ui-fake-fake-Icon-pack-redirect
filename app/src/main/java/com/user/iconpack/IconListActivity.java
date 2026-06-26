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

import java.util.ArrayList;
import java.util.List;

public class IconListActivity extends Activity {
    private static final String TAG = "IconListActivity";
    private static final String PREFS = "iconpack_prefs";
    private static final String ICONS_KEY = "icons_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = new ListView(this);
        setContentView(listView);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String raw = prefs.getString(ICONS_KEY, "");

        final List<String> display = new ArrayList<>();
        final List<String> uriList = new ArrayList<>();

        if (!raw.isEmpty()) {
            String[] items = raw.split(";");
            for (String it : items) {
                // format: timestamp|uri|name
                String[] parts = it.split("\\|", 3);
                if (parts.length >= 2) {
                    String uri = parts[1];
                    String name = parts.length == 3 ? parts[2] : parts[0];
                    display.add(name + " (" + parts[0] + ")");
                    uriList.add(uri);
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
                String uriString = uriList.get(position);
                try {
                    Uri u = Uri.parse(uriString);
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
