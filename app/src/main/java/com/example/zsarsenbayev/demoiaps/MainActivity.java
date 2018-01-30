package com.example.zsarsenbayev.demoiaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Detector.ImageListener, CameraDetector.CameraEventListener{

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final String TAG = "LIVE_STREAM";

    private Button startButton;
    private Button nextButton;
    private String drawableName = "";
    private ImageView imageView;
    private TextView joyTextView;
    private TextView sadTextView;
    private TextView surpriseTextView;
    private TextView disgustTextView;

    private SurfaceView cameraPreview;
    private RelativeLayout mainLayout;
    private CameraDetector detector;
    private int previewWidth = 0;
    private int previewHeight = 0;
    private int i = 1;
    private boolean isSDKStarted = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        mainLayout = (RelativeLayout) findViewById(R.id.mainActivityLayout);
        imageView = (ImageView) findViewById(R.id.imageView);
        joyTextView = (TextView) findViewById(R.id.joyTextView);
        sadTextView = (TextView) findViewById(R.id.sadTextView);
        surpriseTextView = (TextView) findViewById(R.id.surpriseTextView);
        disgustTextView = (TextView) findViewById(R.id.disgustTextView);

        cameraPreview = new SurfaceView(this) {
            @Override
            public void onMeasure(int widthSpec, int heightSpec) {
                int measureWidth = MeasureSpec.getSize(widthSpec)/100;
                int measureHeight = MeasureSpec.getSize(heightSpec)/100;
                int width;
                int height;
                if (previewHeight == 0 || previewWidth == 0) {
                    width = measureWidth;
                    height = measureHeight;
                } else {
                    float viewAspectRatio = (float)measureWidth/measureHeight;
                    float cameraPreviewAspectRatio = (float) previewWidth/previewHeight;

                    if (cameraPreviewAspectRatio > viewAspectRatio) {
                        width = measureWidth;
                        height =(int) (measureWidth / cameraPreviewAspectRatio);
                    } else {
                        width = (int) (measureHeight * cameraPreviewAspectRatio);
                        height = measureHeight;
                    }
                }
                setMeasuredDimension(width,height);
            }
        };

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        cameraPreview.setLayoutParams(params);
        mainLayout.addView(cameraPreview,0);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSDKStarted) {
                    isSDKStarted = false;
                    stopDetector();
                    startButton.setText("Start Camera");
                } else {
                    isSDKStarted = true;
                    startDetector();
                    startButton.setText("Stop Camera");
                }
            }
        });

        checkPermissions();
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectAllEmotions(true);
        detector.setDetectSmile(true);
        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nextButton.setText("Next");

                drawableName = "img" + String.valueOf(i);
                int resID = getResources().getIdentifier(drawableName, "drawable", getPackageName());
                imageView.setImageResource(resID);
                imageView.setTag(drawableName);
                i++;
                if (i > 10) {
//                    Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_SHORT).show();
//                    nextButton.setEnabled(false);
//                    drawableName = "noPictureDisplayed";
                    i = 1;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
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
            joyTextView.setText(""+String.format("%.2f", face.emotions.getJoy()));
            sadTextView.setText(""+String.format("%.2f", face.emotions.getSadness()));
            surpriseTextView.setText("" + String.format("%.2f", face.emotions.getSurprise()));
            disgustTextView.setText(""+String.format("%.2f", face.emotions.getDisgust()));
            Log.d(TAG, "anger: " + face.emotions.getAnger());
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

    @Override
    protected void onResume() {
        super.onResume();
        if (isSDKStarted) {
            startDetector();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopDetector();
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
