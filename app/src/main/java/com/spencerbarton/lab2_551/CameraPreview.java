package com.spencerbarton.lab2_551;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

/**
 * Created by Spencer on 2/5/2015.
 * From http://ibuzzlog.blogspot.com/2012/08/how-to-do-real-time-image-processing-in.html
 */
public class CameraPreview implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final int MATRIX_DATA_TYPE = CvType.CV_8UC3;
    private static final int CANNY_THRESH_1 = 300;
    private static final int CANNY_THRESH_2 = 600;
    private static final int CANNY_SOBEL_APERTURE_SZ = 5;
    private Camera mCamera = null;
    private ImageView mCameraPreview = null;
    private Bitmap mDisplayBitmap = null;
    private byte[] mFrameData = null;
    private Mat mImgMat = null;
    private Mat mProcessedImgMat = null;
    private int mImageFormat;
    private int mPreviewSizeWidth;
    private int mPreviewSizeHeight;
    private boolean mProcessing = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public CameraPreview(int PreviewlayoutWidth, int PreviewlayoutHeight,
                         ImageView CameraPreview)
    {
        mPreviewSizeWidth = PreviewlayoutWidth;
        mPreviewSizeHeight = PreviewlayoutHeight;
        mCameraPreview = CameraPreview;
        mDisplayBitmap = Bitmap.createBitmap(mPreviewSizeWidth, mPreviewSizeHeight, Bitmap.Config.ARGB_8888);
        mImgMat = new Mat(mPreviewSizeWidth, mPreviewSizeHeight, MATRIX_DATA_TYPE);
        mProcessedImgMat = new Mat(mPreviewSizeWidth, mPreviewSizeHeight, MATRIX_DATA_TYPE);
    }

    @Override
    public void onPreviewFrame(byte[] arg0, Camera arg1)
    {
        // At preview mode, the frame data will push to here.
        if (mImageFormat == ImageFormat.NV21)
        {
            //We only accept the NV21(YUV420) format.
            if ( !mProcessing )
            {
                mFrameData = arg0;
                mHandler.post(DoImageProcessing);
            }
        }
    }

    public void onPause()
    {
        mCamera.stopPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
    {
        Parameters parameters;

        parameters = mCamera.getParameters();
        // Set the camera preview size
        parameters.setPreviewSize(mPreviewSizeWidth, mPreviewSizeHeight);

        mImageFormat = parameters.getPreviewFormat();

        mCamera.setParameters(parameters);

        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0)
    {
        mCamera = Camera.open();
        try
        {
            // If did not set the SurfaceHolder, the preview area will be black.
            mCamera.setPreviewDisplay(arg0);
            mCamera.setPreviewCallback(this);
        }
        catch (IOException e)
        {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0)
    {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private Runnable DoImageProcessing = new Runnable()
    {
        public void run()
        {
            Log.i("MyRealTimeImageProcessing", "DoImageProcessing():");
            mProcessing = true;
            imageProcessing(mPreviewSizeWidth, mPreviewSizeHeight, mFrameData, mDisplayBitmap);

            // Set to view
            mCameraPreview.setImageBitmap(mDisplayBitmap);
            mProcessing = false;
        }
    };

    public void imageProcessing(int width, int height, byte[] frameData, Bitmap bitmap) {
        // Read in frame data and output in pixels

        // TODO thresholds as vars
        // Copy over data
        mImgMat.put(0, 0, frameData);

        // Edge detection
        Imgproc.Canny(mImgMat, mProcessedImgMat, CANNY_THRESH_1, CANNY_THRESH_2,
                CANNY_SOBEL_APERTURE_SZ, true);

        // Put into output format
        Utils.matToBitmap(mProcessedImgMat, bitmap);
    }
}