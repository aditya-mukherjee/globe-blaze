package com.example.google.playservices.placepicker;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent i=getIntent();
        String success=i.getStringExtra("Success");
        String time=i.getStringExtra("Time");

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(success+time);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
        layout.addView(textView);
    }

}
