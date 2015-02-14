package com.spencerbarton.lab2_551;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class FaceRecognitionActivity extends Activity {

    private static final String TAG = "FaceRecognitionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
    }

    //=================================================================
    // Btn Handlers
    //=================================================================

    //--- Add a new class -------------------------------

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
                Log.i(TAG, newClass);
                addNewClass(newClass);
            }
        });

        alert.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.i(TAG, "Canceled");
            }
        });

        alert.show();
    }

    private void addNewClass(String className) {

    }

    public void onTrainClick(View view) {
    }

    public void onTextClick(View view) {
    }

    //=================================================================
    // View related methods
    //=================================================================



}
