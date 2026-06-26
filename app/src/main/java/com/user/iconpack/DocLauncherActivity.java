package com.user.iconpack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DocLauncherActivity extends Activity {
    private static final int REQUEST_OPEN_DOCUMENT = 1001;
    private static final String TAG = "DocLauncherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Prepare an ACTION_OPEN_DOCUMENT intent and start for result so we can forward the result back to the caller (e.g., Theme Park)
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            // Preserve incoming action/data/extras where appropriate
            Intent incoming = getIntent();
            if (incoming != null) {
                // Preserve data only if present
                if (incoming.getData() != null) intent.setData(incoming.getData());
                // Copy extras so pickers that respect extras may see them
                if (incoming.getExtras() != null) intent.putExtras(incoming.getExtras());
            }

            // Do NOT add FLAG_ACTIVITY_NEW_TASK when starting for result
            startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);

        } catch (Exception e) {
            Log.w(TAG, "Failed to open document picker", e);
            Toast.makeText(this, "Failed to open document picker: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Return failure to caller
            setResult(RESULT_CANCELED);
            finish();
        }

        // Do not finish here; wait for the result in onActivityResult
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OPEN_DOCUMENT) {
            try {
                // Forward the result back to the original caller (Theme Park)
                setResult(resultCode, data);
            } catch (Exception e) {
                Log.w(TAG, "Failed to forward document picker result", e);
                setResult(RESULT_CANCELED);
            }
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();
    }
}
