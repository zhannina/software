package com.example.zsarsenbayev.demoiaps;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.graphics.PixelFormat;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.hardware.camera2.*;

import java.util.Date;

public class CameraService extends Service implements SurfaceHolder.Callback{

    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private SurfaceHolder surfHolder;
    private CameraDevice camera = null;
    private MediaRecorder mediaRecorder = null;

    public CameraService() {
    }

    @Override
    public void onCreate() {

        // Start foreground service to avoid unexpected kill
//        Notification notification = new Notification.Builder(this)
//                .setContentTitle("Background Video Recorder")
//                .setContentText("")
//                .setSmallIcon(R.drawable.joy_emoji)
//                .build();
//        startForeground(1234, notification);

        // Create new SurfaceView, set its size to 1x1, move it to the top left corner and set this service as a callback
        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        surfaceView = new SurfaceView(this);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                1, 1,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(surfaceView, layoutParams);
        surfaceView.getHolder().addCallback(this);

    }


    // Stop recording and remove SurfaceView
    @Override
    public void onDestroy() {
        windowManager.removeView(surfaceView);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surfHolder = surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
