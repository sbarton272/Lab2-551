package com.spencerbarton.lab2_551;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class FaceRecognitionActivity extends Activity {

    private static final String TAG = "FaceRecognitionActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Map<Integer, List<FaceImg>> mFaceImgs;
    private FaceImg mCurFaceImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        mFaceImgs = new HashMap<>();
    }

    //=================================================================
    // Add new class
    //=================================================================

    // http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
    public void onAddClassClick(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(R.string.class_in_popup_title);
        alert.setMessage(R.string.class_in_popup_msg);

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newClass = input.getText().toString();

                // Only accept non-empty strings
                if (newClass.length() > 0) {
                    addNewClass(newClass);
                }
            }
        });

        alert.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.i(TAG, "Canceled class name input");
            }
        });

        alert.show();
    }

    private void addNewClass(String className) {
        LinearLayout targetLayout = (LinearLayout) findViewById(R.id.layout_class_samples);

        // Generate unique id for class
        final int classId = Math.abs(UUID.randomUUID().hashCode());

        // Add class to training images
        mFaceImgs.put(classId, new ArrayList<FaceImg>());

        // Create new linear layout for classes
        LinearLayout classLayout = new LinearLayout(this);
        classLayout.setOrientation(LinearLayout.VERTICAL);
        classLayout.setId(classId);

        Log.i(TAG, Integer.toString(classLayout.getId()));

        // Create text button header
        Button classHeader = new Button(this);
        classHeader.setText(className);
        final Context that = this;
        classHeader.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Create new training image, save to map
                FaceImg faceImg = new FaceImg(classId, that);
                mFaceImgs.get(classId).add(faceImg);
                mCurFaceImg = faceImg;
                faceImg.capture();
            }
        });

        // Add elements together
        classLayout.addView(classHeader);
        targetLayout.addView(classLayout);

    }

    //=================================================================
    // Capture face images
    //=================================================================

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(requestCode==REQUEST_IMAGE_CAPTURE  && resultCode==RESULT_OK)
        {
            mCurFaceImg.process();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    public class FaceImg {

        private final static int MAX_FACES = 1;

        private ImageView mView;
        private File mFile;
        private int mClassId;
        private Context mContext;
        private boolean mCaptured = false;

        public FaceImg(int classId, Context context) {
            mClassId = classId;
            mContext = context;

            // Create new image preview
            mView = new ImageView(mContext);
            LinearLayout targetLayout = (LinearLayout) findViewById(mClassId);
            targetLayout.addView(mView);

            // Create new image in local storage
            try {
                mFile = createImageFile(classId);
                mFile.deleteOnExit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private File createImageFile(int classId) throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = classId + "_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            return File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }

        // http://developer.android.com/training/camera/photobasics.html
        public void capture() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null &&
                    mFile != null) {

                // Capture image and save to file
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

        public void process() {
            Log.i(TAG, "Processing image " + mFile.getName());

            if (!mCaptured) {
                mCaptured = true;

                // Read img
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig=Bitmap.Config.RGB_565;
                Bitmap img = BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
                Bitmap faceImg = findFace(img);

                if (faceImg != null) {

                    // TODO Save image

                    // Add image to view
                    mView.setImageBitmap(faceImg);
                }

            }
        }

        public Bitmap findFace(Bitmap img) {

            Bitmap faceImg = null;

            // Face detection
            FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];
            FaceDetector faceDetector = new FaceDetector(img.getWidth(), img.getHeight(),
                    MAX_FACES);
            int numFaces = faceDetector.findFaces(img, faces);

            Log.i(TAG, "Faces found " + numFaces);

            // Process found face
            if (numFaces > 0) {
                FaceDetector.Face face = faces[0];

                // http://stackoverflow.com/questions/9578097/android-face-detection-maxnumdetectedfaces
                PointF midEyes = new PointF();
                face.getMidPoint( midEyes );
                Log.i( TAG,
                        "Found face. Confidence: " + face.confidence() + ". Eye Distance: " + face.eyesDistance() + " Pose: ("
                                + face.pose( FaceDetector.Face.EULER_X ) + "," + face.pose( FaceDetector.Face.EULER_Y ) + ","
                                + face.pose( FaceDetector.Face.EULER_Z ) + "). Eye Midpoint: (" + midEyes.x + "," + midEyes.y + ")" );

                float eyedist = face.eyesDistance();
                PointF lt = new PointF( midEyes.x - eyedist * 2.0f, midEyes.y - eyedist * 2.5f );
                // Create rectangle around face.  Create a box based on the eyes and add some padding.
                // The ratio of head height to width is generally 9/5 but that makes the rect a bit to tall.
                Rect faceRect =  new Rect(
                        Math.max( (int) ( lt.x ), 0 ),
                        Math.max( (int) ( lt.y ), 0 ),
                        Math.min( (int) ( lt.x + eyedist * 4.0f ), img.getWidth() ),
                        Math.min( (int) ( lt.y + eyedist * 5.5f ), img.getHeight() )
                );

                // Extract face from img
                BitmapRegionDecoder decoder = null;
                try {
                    decoder = BitmapRegionDecoder.newInstance(mFile.getAbsolutePath(), false);
                    faceImg = decoder.decodeRegion(faceRect, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return faceImg;
        }

    }

    //=================================================================
    // Training
    //=================================================================

    public void onTrainClick(View view) {
    }

    //=================================================================
    // Testing
    //=================================================================


    public void onTestClick(View view) {
    }

}
