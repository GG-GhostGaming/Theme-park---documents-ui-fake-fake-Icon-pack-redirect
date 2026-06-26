package com.user.iconpack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    private static final int REQUEST_OPEN_DOCUMENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Always try to open the system Documents UI immediately when the activity starts.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        try {
            // Prefer startActivityForResult so we can return here if needed, then finish.
            startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);
        } catch (Exception e1) {
            try {
                // Fallback: start as a new task (some launchers/runtimes require this)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // We launched the documents UI; finish to avoid staying visible.
                finish();
            } catch (Exception e2) {
                // If all else fails, just finish the activity.
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // We don't need to do anything with the picked document; close immediately.
        finish();
    }
}
