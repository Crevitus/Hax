package com.crevitus.hax;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String TOAST_WARNING = "Call permission not granted";
        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED)
        {
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            int abort = sharedPref.getInt(context.getString(R.string.DoS), 0);
            if(abort == 1) {
                abortBroadcast();
                setResultData(null);
            }
        }
        else {
            Toast.makeText(context, TOAST_WARNING, Toast.LENGTH_LONG).show();
        }
    }
}
