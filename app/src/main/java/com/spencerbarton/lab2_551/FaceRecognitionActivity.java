package com.spencerbarton.lab2_551;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class FaceRecognitionActivity extends Activity {

    private static final String TAG = "FaceRecognitionActivity";
    private static final int FACE_IMG_W = 64;
    private static final int FACE_IMG_H = 85;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TEST_IMG_DIR = "test";
    private static final int BUTTON_ID = Math.abs(UUID.randomUUID().hashCode());
    private FaceImg mCurFaceImg;
    private static final String mNativeLib = "faceRecognitionIpca";
    private static final String TRAIN_IMG_DIR = "train";
    private static final int NUM_PCA_COMP = 6;
    private File mTrainDir;
    private File mTestDir;

    //---- Native Library Definitions ------------------

    static {
        System.loadLibrary(mNativeLib);
    }

    private native int IPCAtest(String trainFileRoot, String imgName);
    private native int IPCAtrain(String trainFileRoot, int numPcaCmp);
    private native int ClassifyGender(String imgName, String modelPath);

    private int classifyGender(String imgName) {
        InputStream is = getResources().openRawResource(R.raw.model);
        File model = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS + File.separator + "model.txt");
        try{
            OutputStream os = new FileOutputStream(model);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return ClassifyGender(imgName, model.getAbsolutePath());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        // Create dir for test/train imgs
        mTestDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + File.separator + TEST_IMG_DIR);
        createNewDir(mTestDir);
        mTrainDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + File.separator + TRAIN_IMG_DIR);
        createNewDir(mTrainDir);

    }

    private void createNewDir(File folder) {
        if (folder.exists()) {
            deleteRecursive(folder);
        }
        boolean r = folder.mkdir();
        Log.i(TAG, "Folder created " + folder.getAbsolutePath() + " (" + r + ")");
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
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

        // Create new dir for imgs
        File folder = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + File.separator + TRAIN_IMG_DIR +
                File.separator + classId);
        if (!folder.exists()) {
            boolean r = folder.mkdir();
            Log.i(TAG, "Folder created " + folder.getAbsolutePath() + " (" + r + ")");
        }

        // Create new linear layout for classes
        LinearLayout classLayout = new LinearLayout(this);
        classLayout.setOrientation(LinearLayout.VERTICAL);
        classLayout.setId(classId);

        Log.i(TAG, "New class:" + Integer.toString(classLayout.getId()));

        // Create text button header
        Button classHeader = new Button(this);
        classHeader.setId(BUTTON_ID);
        classHeader.setText(className);
        final Context that = this;
        classHeader.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Create new training image
                FaceImg faceImg = new TrainImg(classId, that, FACE_IMG_W, FACE_IMG_H);
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

    public class TrainImg extends FaceImg{
        private int mClassId;
        private ImageView mView;

        public TrainImg(int classId, Context context, int imgSizeW, int imgSizeH) {
            super(TRAIN_IMG_DIR + File.separator + Integer.toString(classId), imgSizeW, imgSizeH);
            mClassId = classId;

            // Create new image preview
            mView = new ImageView(context);
            LinearLayout targetLayout = (LinearLayout) findViewById(mClassId);
            targetLayout.addView(mView);

        }

        @Override
        public void process() {
            Log.i(TAG, "Processing image " + mFile.getName());

            if (!mCaptured) {
                mCaptured = true;

                Bitmap img = loadImg();
                Bitmap faceImg = findFace(img);

                if (faceImg != null) {
                    saveImg(faceImg);

                    // Add image to view
                    mView.setImageBitmap(faceImg);

                    String gender = (classifyGender(mFile.getAbsolutePath()) == 1) ? "male" : "female";
                    Toast toast = Toast.makeText(FaceRecognitionActivity.this, gender, Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        }
    }

    public class FaceImg {

        private final static int MAX_FACES = 1;

        protected File mFile;
        protected boolean mCaptured = false;
        private int mImgSizeW;
        private int mImgSizeH;
        private final static float FACE_WIDTH = 1.2f;
        private final static float FACE_HEIGHT = 1.2f;
        private final static float FACE_HEIGHT_RATIO = .75f;
        private RecognitionCallback mRecognitionCallback;

        public FaceImg(String imgDir, int imgSizeW, int imgSizeH, RecognitionCallback recognitionCallback) {
            this(imgDir, imgSizeW, imgSizeH);
            mRecognitionCallback = recognitionCallback;
        }

        public FaceImg(String imgDir, int imgSizeW, int imgSizeH) {
        mImgSizeW = imgSizeW;
        mImgSizeH = imgSizeH;

            // Create new image in local storage
            try {
                mFile = createImageFile(imgDir);
                mFile.deleteOnExit();
                Log.i(TAG, "Created new file " + mFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private File createImageFile(String imgDir) throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = imgDir + File.separator + timeStamp + "_";
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

                Bitmap img = loadImg();
                Bitmap faceImg = findFace(img);

                if (faceImg != null) {
                    saveImg(faceImg);

                    if (mRecognitionCallback != null) {
                        mRecognitionCallback.recognition(mFile.getAbsolutePath());
                    }
                }

            }
        }

        protected Bitmap loadImg() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
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

                // Get max threshold face
                FaceDetector.Face face = faces[0];

                // http://stackoverflow.com/questions/9578097/android-face-detection-maxnumdetectedfaces
                PointF midEyes = new PointF();
                face.getMidPoint( midEyes );
                Log.i( TAG,
                        "Found face. Confidence: " + face.confidence() + ". Eye Distance: " + face.eyesDistance() + " Pose: ("
                                + face.pose( FaceDetector.Face.EULER_X ) + "," + face.pose( FaceDetector.Face.EULER_Y ) + ","
                                + face.pose( FaceDetector.Face.EULER_Z ) + "). Eye Midpoint: (" + midEyes.x + "," + midEyes.y + ")" );

                float eyedist = face.eyesDistance();
                PointF lt = new PointF( midEyes.x - eyedist * FACE_WIDTH, midEyes.y - eyedist * FACE_HEIGHT * FACE_HEIGHT_RATIO );
                // Create rectangle around face.  Create a box based on the eyes and add some padding.
                Rect faceRect =  new Rect(
                        Math.max( (int) ( lt.x ), 0 ),
                        Math.max( (int) ( lt.y ), 0 ),
                        Math.min( (int) ( lt.x + eyedist * FACE_WIDTH * 2 ), img.getWidth() ),
                        Math.min( (int) ( lt.y + eyedist * FACE_HEIGHT * 2 ), img.getHeight() )
                );

                // Extract face from img
                BitmapRegionDecoder decoder = null;
                try {
                    decoder = BitmapRegionDecoder.newInstance(mFile.getAbsolutePath(), false);
                    faceImg = decoder.decodeRegion(faceRect, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Resize
                faceImg = Bitmap.createScaledBitmap(faceImg, mImgSizeW, mImgSizeH, false);
            }

            return faceImg;
        }

        protected void saveImg(Bitmap img) {

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(mFile);
                img.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public String getPath() {
            return mFile.getAbsolutePath();
        }
    }

    //=================================================================
    // Training
    //=================================================================

    public void onTrainClick(View view) {
        int success = IPCAtrain(mTrainDir.getAbsolutePath(), NUM_PCA_COMP);
        Log.i(TAG, "Success: " + success);
    }

    //=================================================================
    // Testing
    //=================================================================

    public void onTestClick(View view) {
        FaceImg faceImg = new FaceImg(TEST_IMG_DIR, FACE_IMG_W, FACE_IMG_H, new RecognitionCallback(this));
        mCurFaceImg = faceImg;
        faceImg.capture();

    }

    public class RecognitionCallback {

        private Context mContext;

        public RecognitionCallback(Context context) {
            mContext = context;
        }

        public void recognition(String path) {

            Log.i(TAG, "Recognizing " + path);

            // Call native method
            int classId = IPCAtest(mTrainDir.getAbsolutePath(), path);
            String gender = (classifyGender(path) == 1) ? "male" : "female";

            // Display to user
            LinearLayout classLayout = (LinearLayout) findViewById(classId);
            Button classBtn = (Button) classLayout.findViewById(BUTTON_ID);
            Resources res = getResources();
            String className = res.getString(R.string.default_class);
            if (classBtn != null) {
                className = classBtn.getText().toString();
            }

            Log.i(TAG, "Recognized: " + className + " (" + classId + ")" + " Gender " + gender);

            // Dialog
            CharSequence msg = res.getString(R.string.recognition_msg) + ": " + className + " Gender: " + gender;
            Toast toast = Toast.makeText(mContext, msg , Toast.LENGTH_LONG);
            toast.show();
        }
    }

}
