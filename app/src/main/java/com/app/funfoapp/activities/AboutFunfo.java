package com.app.funfoapp.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.funfoapp.R;

public class AboutFunfo extends Activity {

    Context context = AboutFunfo.this;
    Toolbar toolbar;
    LinearLayout ll_about_back;
    TextView txt_policy,txt_policy1,txt_policy2,txt_policy3,txt_policy4,txt_policy5,txt_policy6,txt_policy7,txt_policy8,txt_policy9,txt_policy10,
            txt_policy11,txt_policy12,txt_policy13,txt_policy14,txt_policy15,txt_policy16,txt_policy17,txt_policy18,txt_policy19,txt_policy20,
            txt_policy21,txt_policy22,txt_policy23,txt_policy24,txt_policy25,txt_policy26,txt_policy27,txt_policy28,txt_policy29,txt_policy30,
            txt_policy31,txt_policy32,txt_policy33,txt_policy34,txt_policy35,txt_policy36,txt_policy37,txt_policy38,txt_policy39,txt_policy40,
            txt_policy41,txt_policy42,txt_policy43,txt_policy44,txt_policy45,txt_policy46,txt_policy47,txt_policy48,txt_policy49;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_funfo);
        toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        ll_about_back = (LinearLayout) toolbar.findViewById(R.id.ll_about_back);
        ll_about_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }

    //Initilization Ui Variables
    public void init() {
        txt_policy = (TextView)findViewById(R.id.txt_policy);
        txt_policy1 = (TextView)findViewById(R.id.txt_policy1);
        txt_policy2 = (TextView)findViewById(R.id.txt_policy2);
        txt_policy3 = (TextView)findViewById(R.id.txt_policy3);
        txt_policy4 = (TextView)findViewById(R.id.txt_policy4);
        txt_policy5 = (TextView)findViewById(R.id.txt_policy5);
        txt_policy6 = (TextView)findViewById(R.id.txt_policy6);
        txt_policy7 = (TextView)findViewById(R.id.txt_policy7);
        txt_policy8 = (TextView)findViewById(R.id.txt_policy8);
        txt_policy9 = (TextView)findViewById(R.id.txt_policy9);
        txt_policy10 = (TextView)findViewById(R.id.txt_policy10);
        txt_policy11 = (TextView)findViewById(R.id.txt_policy11);
        txt_policy12 = (TextView)findViewById(R.id.txt_policy12);
        txt_policy13 = (TextView)findViewById(R.id.txt_policy13);
        txt_policy14 = (TextView)findViewById(R.id.txt_policy14);
        txt_policy15 = (TextView)findViewById(R.id.txt_policy15);
        txt_policy16 = (TextView)findViewById(R.id.txt_policy16);
        txt_policy17 = (TextView)findViewById(R.id.txt_policy17);
        txt_policy18 = (TextView)findViewById(R.id.txt_policy18);
        txt_policy19 = (TextView)findViewById(R.id.txt_policy19);
        txt_policy20 = (TextView)findViewById(R.id.txt_policy20);
        txt_policy21 = (TextView)findViewById(R.id.txt_policy21);
        txt_policy22 = (TextView)findViewById(R.id.txt_policy22);
        txt_policy23 = (TextView)findViewById(R.id.txt_policy23);
        txt_policy24 = (TextView)findViewById(R.id.txt_policy24);
        txt_policy25 = (TextView)findViewById(R.id.txt_policy25);
        txt_policy26 = (TextView)findViewById(R.id.txt_policy26);
        txt_policy27 = (TextView)findViewById(R.id.txt_policy27);
        txt_policy28 = (TextView)findViewById(R.id.txt_policy28);
        txt_policy29 = (TextView)findViewById(R.id.txt_policy29);
        txt_policy30 = (TextView)findViewById(R.id.txt_policy30);
        txt_policy31 = (TextView)findViewById(R.id.txt_policy31);
        txt_policy32 = (TextView)findViewById(R.id.txt_policy32);
        txt_policy33 = (TextView)findViewById(R.id.txt_policy33);
        txt_policy34 = (TextView)findViewById(R.id.txt_policy34);
        txt_policy35 = (TextView)findViewById(R.id.txt_policy35);
        txt_policy36 = (TextView)findViewById(R.id.txt_policy36);
        txt_policy37 = (TextView)findViewById(R.id.txt_policy37);
        txt_policy38 = (TextView)findViewById(R.id.txt_policy38);
        txt_policy39 = (TextView)findViewById(R.id.txt_policy39);
        txt_policy40 = (TextView)findViewById(R.id.txt_policy40);
        txt_policy41 = (TextView)findViewById(R.id.txt_policy41);
        txt_policy42 = (TextView)findViewById(R.id.txt_policy42);
        txt_policy43 = (TextView)findViewById(R.id.txt_policy43);
        txt_policy44 = (TextView)findViewById(R.id.txt_policy44);
        txt_policy45 = (TextView)findViewById(R.id.txt_policy45);
        txt_policy46 = (TextView)findViewById(R.id.txt_policy46);
        txt_policy47 = (TextView)findViewById(R.id.txt_policy47);
        txt_policy48 = (TextView)findViewById(R.id.txt_policy48);
        txt_policy49 = (TextView)findViewById(R.id.txt_policy49);
        setFont();
    }

    //Set Font to TextView
    public void setFont() {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        txt_policy.setTypeface(face);
        txt_policy2.setTypeface(face);
        txt_policy4.setTypeface(face);
        txt_policy6.setTypeface(face);
        txt_policy8.setTypeface(face);
        txt_policy10.setTypeface(face);
        txt_policy12.setTypeface(face);
        txt_policy14.setTypeface(face);
        txt_policy16.setTypeface(face);
        txt_policy18.setTypeface(face);
        txt_policy20.setTypeface(face);
        txt_policy22.setTypeface(face);
        txt_policy24.setTypeface(face);
        txt_policy26.setTypeface(face);
        txt_policy29.setTypeface(face);
        txt_policy31.setTypeface(face);
        txt_policy33.setTypeface(face);
        txt_policy35.setTypeface(face);
        txt_policy37.setTypeface(face);
        txt_policy39.setTypeface(face);
        txt_policy41.setTypeface(face);
        txt_policy43.setTypeface(face);
        txt_policy45.setTypeface(face);
        txt_policy47.setTypeface(face);
        txt_policy49.setTypeface(face);
        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        txt_policy1.setTypeface(face1);
        txt_policy3.setTypeface(face1);
        txt_policy5.setTypeface(face1);
        txt_policy7.setTypeface(face1);
        txt_policy9.setTypeface(face1);
        txt_policy11.setTypeface(face1);
        txt_policy13.setTypeface(face1);
        txt_policy15.setTypeface(face1);
        txt_policy17.setTypeface(face1);
        txt_policy19.setTypeface(face1);
        txt_policy21.setTypeface(face1);
        txt_policy23.setTypeface(face1);
        txt_policy25.setTypeface(face1);
        txt_policy27.setTypeface(face1);
        txt_policy28.setTypeface(face1);
        txt_policy30.setTypeface(face1);
        txt_policy32.setTypeface(face1);
        txt_policy34.setTypeface(face1);
        txt_policy36.setTypeface(face1);
        txt_policy38.setTypeface(face1);
        txt_policy40.setTypeface(face1);
        txt_policy42.setTypeface(face1);
        txt_policy44.setTypeface(face1);
        txt_policy46.setTypeface(face1);
        txt_policy48.setTypeface(face1);

    }

}
