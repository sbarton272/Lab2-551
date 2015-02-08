package com.spencerbarton.lab2_551;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class VideoProcessingActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "VideoProcessingActivity";
    private static final int BLUR_SZ = 25;
    private static final int CANNY_THRS_1 = 300;
    private static final int CANNY_THRS_2 = 600;
    private static final int CANNY_APTR_SZ = 5;
    private static final boolean CANNY_GRAD = true;
    private static enum ViewMode {REGULAR, BLUR, EDGE};
    private volatile ViewMode mViewMode = ViewMode.REGULAR;
    private PortraitCameraView mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_processing);
        mOpenCvCameraView = (PortraitCameraView) findViewById(R.id.video_surface);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    //------------ Img Processing -------------------------------------------

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat imgMat = inputFrame.rgba();

        ViewMode mode = mViewMode;
        switch (mode) {
            case BLUR:
                Imgproc.blur(imgMat, imgMat, new Size(BLUR_SZ, BLUR_SZ));
                break;
            case EDGE:
                Imgproc.Canny(imgMat, imgMat, CANNY_THRS_1, CANNY_THRS_2, CANNY_APTR_SZ, CANNY_GRAD);
                break;
            default:
                break;
        }

        return imgMat;
    }

    //------------ Btns -------------------------------------------

    public void onResetBtn(View view) {
        Log.i(TAG, "Reset button");
        mViewMode = ViewMode.REGULAR;
    }

    public void onBlurBtn(View view) {
        Log.i(TAG, "Blur button");
        mViewMode = ViewMode.BLUR;
    }

    public void onEdgeBtn(View view) {
        Log.i(TAG, "Edge button");
        mViewMode = ViewMode.EDGE;
    }
}