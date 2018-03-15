package com.example.zsarsenbayev.demoiaps;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Screen;

public class ActivateAwareService extends Service {

    private PhoneUnlockReceiver phoneUnlockReceiver;
    private static final String TAG = "Zhanna";

    public ActivateAwareService() {
    }

    @Override
    public void onCreate() {
        phoneUnlockReceiver = new PhoneUnlockReceiver();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
    Intent aware = new Intent(this, Aware.class);
        startService(aware);

        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_SCREEN, true);
        Aware.startScreen(getApplicationContext());

        Applications.isAccessibilityServiceActive(getApplicationContext());// to start applications sensor
        Applications.setSensorObserver(new Applications.AWARESensorObserver() {
            @Override
            public void onForeground(ContentValues data) {
                Log.d(TAG, data.toString());
            }

            @Override
            public void onNotification(ContentValues data) {

            }

            @Override
            public void onCrash(ContentValues data) {

            }

            @Override
            public void onKeyboard(ContentValues data) {

            }

            @Override
            public void onBackground(ContentValues data) {

            }

            @Override
            public void onTouch(ContentValues data) {

            }
        });

        Log.d(TAG, "" + Applications.isAccessibilityServiceActive(getApplicationContext()));
        Log.d(TAG, "" + Applications.getSensorObserver());

        IntentFilter filter = new IntentFilter();
        filter.addAction(Screen.ACTION_AWARE_SCREEN_UNLOCKED);
        registerReceiver(phoneUnlockReceiver, filter);

        return START_STICKY;
    }

    // Stop recording and remove SurfaceView
    @Override
    public void onDestroy() {
        unregisterReceiver(phoneUnlockReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
