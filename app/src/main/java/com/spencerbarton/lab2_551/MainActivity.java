package com.spencerbarton.lab2_551;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void startFaceRecognition(View view) {
        Intent intent = new Intent(this, FaceRecognitionActivity.class);
        startActivity(intent);
    }

    public void startVideoProcessing(View view) {
        Intent intent = new Intent(this, VideoProcessingActivity.class);
        startActivity(intent);
    }

}
