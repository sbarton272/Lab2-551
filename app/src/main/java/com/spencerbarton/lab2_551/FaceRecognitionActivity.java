package com.spencerbarton.lab2_551;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class FaceRecognitionActivity extends Activity {

    private static final String TAG = "FaceRecognitionActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Map<Integer, List<String>> mTrainingImgs;
    private ImageView mCurTrainImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        mTrainingImgs = new HashMap<>();
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
        final int classId = UUID.randomUUID().hashCode();

        // Add class to training images
        mTrainingImgs.put(classId, new ArrayList<String>());

        // Create new linear layout for classes
        LinearLayout classLayout = new LinearLayout(this);
        classLayout.setOrientation(LinearLayout.VERTICAL);
        classLayout.setId(classId);

        // Create text button header
        Button classHeader = new Button(this);
        classHeader.setText(className);
        classHeader.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addNewTrainImg(classId);
            }
        });

        // Add elements together
        classLayout.addView(classHeader);
        targetLayout.addView(classLayout);

    }

    //=================================================================
    // Capture training images
    //=================================================================

    // http://developer.android.com/training/camera/photobasics.html
    private void addNewTrainImg(int classId) {

        // Create new image preview
        ImageView trainImg = new ImageView(this);
        LinearLayout targetLayout = (LinearLayout) findViewById(classId);
        targetLayout.addView(trainImg);

        mCurTrainImg = trainImg;

        // Capture image
        dispatchTakePictureIntent();

        // Face detection

        // Save image

        // Store image path

        // Add image to view

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mCurTrainImg.setImageBitmap(imageBitmap);
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
