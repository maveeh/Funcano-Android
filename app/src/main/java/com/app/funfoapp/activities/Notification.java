package com.app.funfoapp.activities;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;

import org.json.JSONObject;

public class Notification extends AppCompatActivity implements View.OnClickListener {

    Context context = Notification.this;
    Toolbar toolbar;
    LinearLayout ll_noti_back, ll_sounds, ll_email,ll_wildcard;
    CheckBox checkbox_email, checkbox_sounds,checkbox_wildcard;
    TextView txt_email,txt_email1, txt_sounds,txt_sounds1,txt_wildcard,txt_wildcard1;
    String noti = "",notiWildcard="";
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setToobar();
        init();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        noti = sp.getString("noti", null);
        new OnWildcardNotifications(true).execute();
        if (noti == null)
            noti = "true";

        checkbox_sounds.setChecked(Boolean.parseBoolean(noti));

        checkbox_sounds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    noti = "true";

                } else {
                    noti = "false";
                }

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("noti", noti);
                editor.commit();
            }
        });
        checkbox_wildcard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notiWildcard = "true";
                } else {
                    notiWildcard = "false";
                }
                new OnWildcardNotifications(false).execute();
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("notiWildcard", noti);
                editor.commit();
            }
        });
    }

    //webservice for set Notification status on backend
    class OnWildcardNotifications extends AsyncTask<Void,Void,String>{
        Dialog dl;
        int success;
        boolean getStatus;

        public OnWildcardNotifications(boolean getStatus){
            this.getStatus=getStatus;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl= Functions.getProgressIndicator(context);
            dl.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                String url="";
                DbOp db=new DbOp(context);
                String id=db.getSessionId();
                db.close();
                if(getStatus){
                    url = getResources().getString(R.string.base_url)+"getUserNotificationStatus/?userId="+id ;
                }else{
                    if(notiWildcard.equals("true")){
                        url = getResources().getString(R.string.base_url)+"updateNotificationStatus/?userId="+id+"&isNotify=1" ;
                    }else{
                        url = getResources().getString(R.string.base_url)+"updateNotificationStatus/?userId="+id+"&isNotify=0" ;
                    }
                }
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                //Log.e("tag", "response " + res);
                JSONObject obj=new JSONObject(res);
                success=obj.getInt("success");
                if(success==1){
                    if(getStatus){
                        if(new JSONObject(obj.getString("0")).getString("status").equals("1")){
                            notiWildcard="true";
                        }else{
                            notiWildcard="false";
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(dl!=null && dl.isShowing()){
                dl.dismiss();
                checkbox_wildcard.setChecked(Boolean.parseBoolean(notiWildcard));
            }
        }
    }

    //Initilization Ui Variables
    public void init() {
        ll_sounds = (LinearLayout) findViewById(R.id.ll_sounds);
        ll_email = (LinearLayout) findViewById(R.id.ll_email);
        ll_wildcard = (LinearLayout) findViewById(R.id.ll_wildcard);
        checkbox_sounds = (CheckBox) findViewById(R.id.checkbox_sounds);
        checkbox_email = (CheckBox) findViewById(R.id.checkbox_email);
        checkbox_wildcard = (CheckBox) findViewById(R.id.checkbox_wildcard);
        txt_email = (TextView) findViewById(R.id.txt_email);
        txt_email1 = (TextView) findViewById(R.id.txt_email1);
        txt_sounds = (TextView) findViewById(R.id.txt_sounds);
        txt_sounds1 = (TextView) findViewById(R.id.txt_sounds1);
        txt_wildcard = (TextView) findViewById(R.id.txt_wildcard);
        txt_wildcard1 = (TextView) findViewById(R.id.txt_wildcard1);
        addClickListener();
        setFont();
    }

    //add Click listener
    public void addClickListener() {
        ll_sounds.setOnClickListener(this);
    }

    //set Font
    public void setFont() {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        txt_email.setTypeface(face);
        txt_email1.setTypeface(face1);
        txt_sounds.setTypeface(face);
        txt_sounds1.setTypeface(face1);
        txt_wildcard.setTypeface(face);
        txt_wildcard1.setTypeface(face1);
    }

    //set Toolbar
    public void setToobar() {
        toolbar = (Toolbar) findViewById(R.id.noti_toolbar);
        ll_noti_back = (LinearLayout) toolbar.findViewById(R.id.ll_noti_back);
        ll_noti_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_sounds:
                break;

            default:
                break;
        }
    }
}
