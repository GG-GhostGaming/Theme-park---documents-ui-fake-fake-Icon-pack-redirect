package com.user.iconpack;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Try to launch the external IconPickerActivity in the ginlemon.iconpackstudio app.
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "ginlemon.iconpackstudio",
                "ginlemon.iconpackstudio.editor.configPickerActivity.IconPickerActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
            // Successfully launched external activity — finish our activity so it doesn't stay visible.
            finish();
            return;
        } catch (Exception e) {
            // If the external activity isn't available or fails to start, fall back to requesting the system document picker.
            try {
                Intent docIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                docIntent.addCategory(Intent.CATEGORY_OPENABLE);
                docIntent.setType("*/*");
                startActivity(docIntent);
            } catch (Exception ex) {
                // If everything fails, just finish.
            }
            finish();
        }
    }
}
