package com.app.funfoapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.adapters.GetEvents_Adapter;
import com.app.funfoapp.adapters.No_Events_Adapter;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.fragments.Profile_Frag;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    Context context = Settings.this;
    Toolbar toolbar;
    LinearLayout ll_settings_back, ll_settings_noti, ll_settings_changepass,ll_settings_legal, ll_settings_privacy, ll_settings_terms;
    TextView txt_settings_noti, txt_settings_changepass,txt_settings_legal, txt_settings_privacy, txt_settings_terms;
    String  uType = "",uRole="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Profile_Frag.ProfileUpdate = false;
        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        ll_settings_back = (LinearLayout) toolbar.findViewById(R.id.ll_settings_back);
        ll_settings_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
        DbOp dbOp = new DbOp(context);
        uType = dbOp.getRole();
        uRole = dbOp.getlogintype();
        dbOp.close();
        if(uRole.equals("byfacebook") || uRole.equals("bygmail")) {
            ll_settings_changepass.setVisibility(View.GONE);
        }
        else {
            ll_settings_changepass.setVisibility(View.VISIBLE);
        }
    }

    //Initilization Ui Variables
    public void init() {
        ll_settings_noti = (LinearLayout) findViewById(R.id.ll_settings_noti);
        ll_settings_changepass = (LinearLayout) findViewById(R.id.ll_settings_changepass);
        ll_settings_legal = (LinearLayout) findViewById(R.id.ll_settings_legal);
        ll_settings_privacy = (LinearLayout) findViewById(R.id.ll_settings_privacy);
        ll_settings_terms = (LinearLayout) findViewById(R.id.ll_settings_terms);
        txt_settings_noti = (TextView) findViewById(R.id.txt_settings_noti);
        txt_settings_changepass = (TextView) findViewById(R.id.txt_settings_changepass);
        txt_settings_legal = (TextView) findViewById(R.id.txt_settings_legal);
        txt_settings_privacy = (TextView) findViewById(R.id.txt_settings_privacy);
        txt_settings_terms = (TextView) findViewById(R.id.txt_settings_terms);
        addClickListener();
        setFont();
    }

    //add click listener
    public void addClickListener() {
        ll_settings_noti.setOnClickListener(this);
        ll_settings_changepass.setOnClickListener(this);
        ll_settings_legal.setOnClickListener(this);
        ll_settings_privacy.setOnClickListener(this);
        ll_settings_terms.setOnClickListener(this);
    }

    //set Font
    public void setFont() {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        txt_settings_noti.setTypeface(face);
        txt_settings_changepass.setTypeface(face);
        txt_settings_legal.setTypeface(face);
        txt_settings_privacy.setTypeface(face);
        txt_settings_terms.setTypeface(face);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_settings_changepass:
                startActivity(new Intent(context, ChangePassword.class));
                break;

            case R.id.ll_settings_privacy:
                startActivity(new Intent(context,AboutFunfo.class));
                break;

            case R.id.ll_settings_terms:
                startActivity(new Intent(context,Terms.class));
                break;

            case R.id.ll_settings_noti:
                startActivity(new Intent(context, Notification.class));
                break;

            case R.id.ll_settings_legal:
                startActivity(new Intent(context,Help.class));
                break;

            default:
                break;
        }
    }
}
