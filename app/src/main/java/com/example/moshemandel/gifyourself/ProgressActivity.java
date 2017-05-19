package com.example.moshemandel.gifyourself;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        Intent intent = getIntent();
        String imgPath = intent.getExtras().getString("imgPath");
        ServerComm serverComm = new ServerComm(this);

        serverComm.execute(imgPath);

    }
}
