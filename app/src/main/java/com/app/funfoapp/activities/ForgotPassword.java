package com.app.funfoapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    Context context = ForgotPassword.this;
    EditText edt_fp_email;
    Button forgotBtn;
    TextView txt_fp_Back;
    String mEmail="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        init();
        addClickListener();
        setFont();
    }
    //Initilization Ui Variables
    public void init() {
        edt_fp_email = (EditText) findViewById(R.id.edt_fp_email);
        txt_fp_Back = (TextView) findViewById(R.id.txt_fp_Back);
        forgotBtn = (Button) findViewById(R.id.forgotBtn);
    }

    //Add click Listener
    public void addClickListener() {
        txt_fp_Back.setOnClickListener(this);
        forgotBtn.setOnClickListener(this);
    }

    //set Font
    public void setFont() {
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold.ttf");
        forgotBtn.setTypeface(face);
        txt_fp_Back.setTypeface(face);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgotBtn:

                    if(edt_fp_email.getText().toString().equals("")) {
                        Functions.showToast(context, "Please enter Email.");
                    }
                    else if (!isValidEmail(edt_fp_email.getText().toString())) {
                        Functions.showToast(context, "Please enter a valid Email.");
                    } else {
                        if (Validations.isNetworkAvailable(this)) {
                        try {
                            mEmail = URLEncoder.encode(edt_fp_email.getText().toString(), "UTF-8");
                            new ForgotAsync().execute();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        } else {
                            Functions.showAlert(context,"Internet Connection Error","Please check your internet connection and try again.");
                        }
                    }
                break;

            case R.id.txt_fp_Back:
                backKeyHandling();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backKeyHandling();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void backKeyHandling() {
        Intent i = new Intent(this, SignIn.class);
        startActivity(i);
        finish();
    }

    //Validation for email
    public boolean isValidEmail(String str_email){
        if(str_email.equals("")){
            return false;
        }
        else if(!Validations.isValidEmail(str_email)){
            return false;
        }
        else{
            return true;
        }
    }

    //webservice for forgot Password
    public class ForgotAsync extends AsyncTask<Void,Void,Void> {
        Dialog dl;
        int success=0;
        String strErrorMsg="",title="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl= Functions.getProgressIndicator(context);
            dl.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String url = getResources().getString(R.string.base_url)+"checkUser/?uEmail=" + mEmail ;
                Log.e("tag", "response " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                Log.e("tag", "response " + res);
                JSONObject obj=new JSONObject(res);
                success=obj.getInt("success");
                if(success==1){
                    strErrorMsg=new JSONObject(obj.getString("0")).getString("status");
                    title=new JSONObject(obj.getString("0")).getString("eTitle");
                }else{
                    strErrorMsg=new JSONObject(obj.getString("0")).getString("status");
                    title=new JSONObject(obj.getString("0")).getString("eTitle");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(dl!=null && dl.isShowing())
                dl.dismiss();
            if(success==1){
                showAlert(context,title,strErrorMsg);

            }else{
                Functions.showAlert(context,title,strErrorMsg);
            }
        }
    }

    public void showAlert(final Context context,String title,String msg)
    {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(msg);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Intent intent=new Intent(context,SignIn.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
