package com.app.funfoapp.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.funfoapp.R;

public class Help extends Activity {

    Context context=Help.this;
    Toolbar toolbar;
    LinearLayout ll_help_back;
    TextView txt_legal,txt_legal1,txt_legal2,txt_legal3,txt_legal4,txt_legal5,txt_legal6,txt_legal7,txt_legal8,txt_legal9,txt_legal10,
            txt_legal11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        toolbar=(Toolbar)findViewById(R.id.help_toolbar);
        ll_help_back=(LinearLayout)toolbar.findViewById(R.id.ll_help_back);
        ll_help_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();

    }

    //Initilization Ui Variables
    public void init() {
        txt_legal = (TextView)findViewById(R.id.txt_legal);
        txt_legal1 = (TextView)findViewById(R.id.txt_legal1);
        txt_legal2 = (TextView)findViewById(R.id.txt_legal2);
        txt_legal3 = (TextView)findViewById(R.id.txt_legal3);
        txt_legal4 = (TextView)findViewById(R.id.txt_legal4);
        txt_legal5 = (TextView)findViewById(R.id.txt_legal5);
        txt_legal6 = (TextView)findViewById(R.id.txt_legal6);
        txt_legal7 = (TextView)findViewById(R.id.txt_legal7);
        txt_legal8 = (TextView)findViewById(R.id.txt_legal8);
        txt_legal9 = (TextView)findViewById(R.id.txt_legal9);
        txt_legal10 = (TextView)findViewById(R.id.txt_legal10);
        txt_legal11 = (TextView)findViewById(R.id.txt_legal11);
        setFont();
    }

    //set Font
    public void setFont() {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        txt_legal1.setTypeface(face);
        txt_legal3.setTypeface(face);
        txt_legal5.setTypeface(face);
        txt_legal7.setTypeface(face);
        txt_legal9.setTypeface(face);
        txt_legal11.setTypeface(face);
        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        txt_legal.setTypeface(face1);
        txt_legal2.setTypeface(face1);
        txt_legal4.setTypeface(face1);
        txt_legal6.setTypeface(face1);
        txt_legal8.setTypeface(face1);
        txt_legal10.setTypeface(face1);

    }

}
