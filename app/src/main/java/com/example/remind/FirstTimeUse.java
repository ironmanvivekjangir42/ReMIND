package com.example.remind;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class FirstTimeUse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_use);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(FirstTimeUse.this, OTP.class);
                FirstTimeUse.this.startActivity(mainIntent);
                FirstTimeUse.this.finish();
            }
        }, 2000);
    }
}
