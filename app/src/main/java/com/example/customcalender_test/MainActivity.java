package com.example.customcalender_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startIntentbBtn = (Button) findViewById(R.id.openCalender);
        startIntentbBtn.setOnClickListener((view)->{
            startActivity(CalendarViewWithNotesActivity.makeIntent(MainActivity.this));
        });
    }
}
