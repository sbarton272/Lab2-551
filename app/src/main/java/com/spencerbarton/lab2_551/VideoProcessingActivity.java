package com.spencerbarton.lab2_551;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;


public class VideoProcessingActivity extends Activity {

    private static final String TAG = "VideoProcessingActivity";
private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
    @Override
    public void onManagerConnected(int status) {
        switch (status) {
            case LoaderCallbackInterface.SUCCESS: {
                Log.i(TAG, "OpenCV loaded successfully");
            }
            break;
            default: {
                super.onManagerConnected(status);
            }
            break;
        }
    }
};
    private static final int mPreviewSizeWidth = 640;
    private static final int mPreviewSizeHeight = 480;
    private CameraProcessing mCamProcessing;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_processing);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        mCamProcessing = new CameraProcessing(surfaceView.getHolder(), mPreviewSizeWidth, mPreviewSizeHeight);

    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    /*=======================================================
      OpenCV
      =======================================================*/

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamProcessing != null)
            mCamProcessing.onPause();
    }

}
