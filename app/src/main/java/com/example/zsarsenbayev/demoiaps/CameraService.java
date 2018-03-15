package com.example.zsarsenbayev.demoiaps;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.graphics.PixelFormat;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.List;

public class CameraService extends Service implements Detector.ImageListener, CameraDetector.CameraEventListener{

    private SurfaceView cameraPreview;
    private CameraDetector detector;
    private int previewWidth = 0;
    private int previewHeight = 0;
    private boolean isSDKStarted = false;
    private WindowManager windowManager;
    private static final String TAG = "LIVE_STREAM";

    public CameraService() {
    }

    @Override
    public void onCreate() {

        cameraPreview = new SurfaceView(this);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                1, 1,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(cameraPreview, layoutParams);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectAllEmotions(true);
        detector.setDetectSmile(true);
        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);

    }

    public int onStartCommand(Intent intent, int flags, int startId){
        startDetector();
        return START_STICKY;
    }

    // Stop recording and remove SurfaceView
    @Override
    public void onDestroy() {
        stopDetector();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public void onCameraSizeSelected(int width, int height, Frame.ROTATE rotate) {
        if (rotate == Frame.ROTATE.BY_90_CCW || rotate == Frame.ROTATE.BY_90_CW) {
            previewWidth = height;
            previewHeight = width;
        } else {
            previewHeight = height;
            previewWidth = width;
        }
        cameraPreview.requestLayout();
    }

    @Override
    public void onImageResults(List<Face> list, Frame frame, float v) {
        if (list == null)
            return;
        if (list.size() == 0) {
            Log.d(TAG, "No face detected");
        } else {
            final Face face = list.get(0);
            Log.d(TAG, "contempt: " + face.emotions.getContempt());
            Log.d(TAG, "disgust: " + face.emotions.getDisgust());
            Log.d(TAG, "fear: " + face.emotions.getFear());
            Log.d(TAG, "joy: " + face.emotions.getJoy());
            Log.d(TAG, "sadness: " + face.emotions.getSadness());
            Log.d(TAG, "surprise: " + face.emotions.getSurprise());
            Log.d(TAG, "valence: " + face.emotions.getValence());
            Log.d(TAG, "smile: " + face.expressions.getSmile());

        }
    }

    void startDetector() {
        if (!detector.isRunning()) {
            detector.start();
        }
    }

    void stopDetector() {
        if (detector.isRunning()) {
            detector.stop();
        }
    }
}
