package com.example.zsarsenbayev.demoiaps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zsarsenbayev on 3/15/18.
 */

public class PhoneUnlockReceiver extends BroadcastReceiver{

    public static String message = "Phone unlocked";
    public static final String TAG = "Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Phone unlocked");
        startCamerService(context);
    }

    private void startCamerService(Context context) {
        Intent serviceIntent = new Intent(context, CameraService.class);
        context.startService(serviceIntent);
    }
}
