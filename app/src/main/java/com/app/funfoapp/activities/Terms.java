package com.app.funfoapp.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.funfoapp.R;

public class Terms extends AppCompatActivity {

    Context context=Terms.this;
    Toolbar toolbar;
    LinearLayout ll_terms_back;
    TextView txt_terms,txt_terms1,txt_terms2,txt_terms3,txt_terms4,txt_terms5,txt_terms6,txt_terms7,txt_terms8,txt_terms9,txt_terms10,
            txt_terms11,txt_terms12,txt_terms13,txt_terms14,txt_terms15,txt_terms16,txt_terms17,txt_terms18,txt_terms19,txt_terms20,
            txt_terms21,txt_terms22,txt_terms23,txt_terms24,txt_terms25,txt_terms26,txt_terms27,txt_terms28,txt_terms29,txt_terms30,
            txt_terms31,txt_terms32,txt_terms33,txt_terms34,txt_terms35,txt_terms36,txt_terms37,txt_terms38,txt_terms39,txt_terms40,
            txt_terms41,txt_terms42,txt_terms43,txt_terms44,txt_terms45,txt_terms46,txt_terms47,txt_terms48,txt_terms49,txt_terms50,
            txt_terms51,txt_terms52,txt_terms53,txt_terms54,txt_terms55;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        toolbar=(Toolbar)findViewById(R.id.terms_toolbar);
        ll_terms_back=(LinearLayout)toolbar.findViewById(R.id.ll_terms_back);
        ll_terms_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        init();
    }

    //Initilization Ui Variables
    public void init() {
        txt_terms = (TextView)findViewById(R.id.txt_terms);
        txt_terms1 = (TextView)findViewById(R.id.txt_terms1);
        txt_terms2 = (TextView)findViewById(R.id.txt_terms2);
        txt_terms3 = (TextView)findViewById(R.id.txt_terms3);
        txt_terms4 = (TextView)findViewById(R.id.txt_terms4);
        txt_terms5 = (TextView)findViewById(R.id.txt_terms5);
        txt_terms6 = (TextView)findViewById(R.id.txt_terms6);
        txt_terms7 = (TextView)findViewById(R.id.txt_terms7);
        txt_terms8 = (TextView)findViewById(R.id.txt_terms8);
        txt_terms9 = (TextView)findViewById(R.id.txt_terms9);
        txt_terms10 = (TextView)findViewById(R.id.txt_terms10);
        txt_terms11 = (TextView)findViewById(R.id.txt_terms11);
        txt_terms12 = (TextView)findViewById(R.id.txt_terms12);
        txt_terms13 = (TextView)findViewById(R.id.txt_terms13);
        txt_terms14 = (TextView)findViewById(R.id.txt_terms14);
        txt_terms15 = (TextView)findViewById(R.id.txt_terms15);
        txt_terms16 = (TextView)findViewById(R.id.txt_terms16);
        txt_terms17 = (TextView)findViewById(R.id.txt_terms17);
        txt_terms18 = (TextView)findViewById(R.id.txt_terms18);
        txt_terms19 = (TextView)findViewById(R.id.txt_terms19);
        txt_terms20 = (TextView)findViewById(R.id.txt_terms20);
        txt_terms21 = (TextView)findViewById(R.id.txt_terms21);
        txt_terms22 = (TextView)findViewById(R.id.txt_terms22);
        txt_terms23 = (TextView)findViewById(R.id.txt_terms23);
        txt_terms24 = (TextView)findViewById(R.id.txt_terms24);
        txt_terms25 = (TextView)findViewById(R.id.txt_terms25);
        txt_terms26 = (TextView)findViewById(R.id.txt_terms26);
        txt_terms27 = (TextView)findViewById(R.id.txt_terms27);
        txt_terms28 = (TextView)findViewById(R.id.txt_terms28);
        txt_terms29 = (TextView)findViewById(R.id.txt_terms29);
        txt_terms30 = (TextView)findViewById(R.id.txt_terms30);
        txt_terms31 = (TextView)findViewById(R.id.txt_terms31);
        txt_terms32 = (TextView)findViewById(R.id.txt_terms32);
        txt_terms33 = (TextView)findViewById(R.id.txt_terms33);
        txt_terms34 = (TextView)findViewById(R.id.txt_terms34);
        txt_terms35 = (TextView)findViewById(R.id.txt_terms35);
        txt_terms36 = (TextView)findViewById(R.id.txt_terms36);
        txt_terms37 = (TextView)findViewById(R.id.txt_terms37);
        txt_terms38 = (TextView)findViewById(R.id.txt_terms38);
        txt_terms39 = (TextView)findViewById(R.id.txt_terms39);
        txt_terms40 = (TextView)findViewById(R.id.txt_terms40);
        txt_terms41 = (TextView)findViewById(R.id.txt_terms41);
        txt_terms42 = (TextView)findViewById(R.id.txt_terms42);
        txt_terms43 = (TextView)findViewById(R.id.txt_terms43);
        txt_terms44 = (TextView)findViewById(R.id.txt_terms44);
        txt_terms45 = (TextView)findViewById(R.id.txt_terms45);
        txt_terms46 = (TextView)findViewById(R.id.txt_terms46);
        txt_terms47 = (TextView)findViewById(R.id.txt_terms47);
        txt_terms48 = (TextView)findViewById(R.id.txt_terms48);
        txt_terms49 = (TextView)findViewById(R.id.txt_terms49);
        txt_terms50 = (TextView)findViewById(R.id.txt_terms50);
        txt_terms51 = (TextView)findViewById(R.id.txt_terms51);
        txt_terms52 = (TextView)findViewById(R.id.txt_terms52);
        txt_terms53 = (TextView)findViewById(R.id.txt_terms53);
        txt_terms54 = (TextView)findViewById(R.id.txt_terms54);
        txt_terms55 = (TextView)findViewById(R.id.txt_terms55);
        setFont();
    }

    //set Font
    public void setFont() {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        txt_terms.setTypeface(face);
        txt_terms1.setTypeface(face);
        txt_terms3.setTypeface(face);
        txt_terms5.setTypeface(face);
        txt_terms7.setTypeface(face);
        txt_terms9.setTypeface(face);
        txt_terms11.setTypeface(face);
        txt_terms13.setTypeface(face);
        txt_terms14.setTypeface(face);
        txt_terms15.setTypeface(face);
        txt_terms17.setTypeface(face);
        txt_terms19.setTypeface(face);
        txt_terms20.setTypeface(face);
        txt_terms21.setTypeface(face);
        txt_terms23.setTypeface(face);
        txt_terms25.setTypeface(face);
        txt_terms27.setTypeface(face);
        txt_terms29.setTypeface(face);
        txt_terms31.setTypeface(face);
        txt_terms33.setTypeface(face);
        txt_terms35.setTypeface(face);
        txt_terms36.setTypeface(face);
        txt_terms37.setTypeface(face);
        txt_terms39.setTypeface(face);
        txt_terms41.setTypeface(face);
        txt_terms43.setTypeface(face);
        txt_terms45.setTypeface(face);
        txt_terms46.setTypeface(face);
        txt_terms47.setTypeface(face);
        txt_terms48.setTypeface(face);
        txt_terms49.setTypeface(face);
        txt_terms51.setTypeface(face);
        txt_terms53.setTypeface(face);
        txt_terms54.setTypeface(face);
        txt_terms55.setTypeface(face);
        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        txt_terms2.setTypeface(face1);
        txt_terms4.setTypeface(face1);
        txt_terms6.setTypeface(face1);
        txt_terms8.setTypeface(face1);
        txt_terms10.setTypeface(face1);
        txt_terms12.setTypeface(face1);
        txt_terms16.setTypeface(face1);
        txt_terms18.setTypeface(face1);
        txt_terms22.setTypeface(face1);
        txt_terms24.setTypeface(face1);
        txt_terms26.setTypeface(face1);
        txt_terms28.setTypeface(face1);
        txt_terms30.setTypeface(face1);
        txt_terms32.setTypeface(face1);
        txt_terms34.setTypeface(face1);
        txt_terms38.setTypeface(face1);
        txt_terms40.setTypeface(face1);
        txt_terms42.setTypeface(face1);
        txt_terms44.setTypeface(face1);
        txt_terms50.setTypeface(face1);
        txt_terms52.setTypeface(face1);

    }
}
