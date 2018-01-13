package com.lamphongstore.lamphong.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.lamphongstore.lamphong.ApiLamPhong;
import com.lamphongstore.lamphong.R;

import static com.lamphongstore.lamphong.ApiLamPhong.API_LOGIN;

public class LauchScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApiLamPhong.setup(this);
        Log.e("version", API_LOGIN);
        setContentView(R.layout.activity_lauch_screen);

        Handler handler = new Handler();
        long TIME_OUT = 2000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoMainActivity();
            }
        }, TIME_OUT);

    }

    private void gotoMainActivity() {
        Intent intent = new Intent(LauchScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
