package com.app.funfoapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignIn extends Activity implements View.OnClickListener {

    Context context=SignIn.this;
    Button logInBtn;
    EditText edtEmail,edtPwd;
    LinearLayout patentLinear;
    TextView txt_back,txt_forgot;
    String mEmail="",mPwd="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
    }

    //Initilization Ui Variables
    public void init()
    {
        logInBtn=(Button)findViewById(R.id.logInBtn);
        txt_back=(TextView)findViewById(R.id.txtBack);
        edtEmail=(EditText)findViewById(R.id.edt_email);
        edtPwd=(EditText)findViewById(R.id.edt_pass);
        txt_forgot=(TextView)findViewById(R.id.txtForgot);
        patentLinear=(LinearLayout)findViewById(R.id.patentLinear);
        addClickListener();
        setFont();
    }

    //set Font
    public void setFont()
    {
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Semibold.ttf");
        logInBtn.setTypeface(face);
        txt_back.setTypeface(face);
        txt_forgot.setTypeface(face);
    }

    //add click listener
    public void addClickListener()
    {
        logInBtn.setOnClickListener(this);
        txt_back.setOnClickListener(this);
        txt_forgot.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backKeyHandling();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void backKeyHandling(){
        Intent i=new Intent(this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.txtBack:
                backKeyHandling();
                break;

            case R.id.logInBtn:
                    if(edtEmail.getText().toString().equals("")) {
                        Functions.showToast(context, "Please enter Email.");
                    }
                        else if (!isValidEmail(edtEmail.getText().toString())) {
                            Functions.showToast(context, "Please enter a valid Email.");
                        }else if (edtPwd.getText().toString().equals("")) {
                            Functions.showToast(context, "Please enter Password.");
                        }
                        else {
                        if(Validations.isNetworkAvailable(this)) {
                            try {
                                mEmail = URLEncoder.encode(edtEmail.getText().toString(), "UTF-8");
                                mPwd = URLEncoder.encode(edtPwd.getText().toString(), "UTF-8");
                                new LoginAsync().execute();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Functions.showAlert(context,"Internet Connection Error","Please check your internet connection and try again.");
                        }
                        }
                break;

            case R.id.txtForgot:
                startActivity(new Intent(this,ForgotPassword.class));
                finish();
                break;


            default:
                break;
        }
    }

    //validation on email
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

    //webservice for Login
    public class LoginAsync extends AsyncTask<Void,Void,Void> {
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
                DbOp db=new DbOp(context);
                String url = getResources().getString(R.string.base_url)+"users/uLogin/?uEmail=" + mEmail+ "&upwd="+mPwd ;
                //Log.e("tag", "response " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                JSONObject obj=new JSONObject(res);
                //Log.e("tag", "response " + res);
                success=obj.getInt("success");
                if(success==1){
                    for (int i=0;i<obj.length()-1;i++){
                        JSONObject jObj=obj.getJSONObject(String.valueOf(i));
                        db.insertSessionId(new JSONObject(obj.getString("0")).getString("userId"), new JSONObject(obj.getString("0")).getString("userName"),new JSONObject(obj.getString("0")).getString("uType"),"byemail",new JSONObject(obj.getString("0")).getString("uDistance"));
                    }
                }else{
                    strErrorMsg=new JSONObject(obj.getString("0")).getString("Status");
                    title=new JSONObject(obj.getString("0")).getString("eTitle");
                }
                db.close();
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
                Intent intent=new Intent(context,MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Functions.showAlert(context,title,strErrorMsg);
            }
        }
    }
}
