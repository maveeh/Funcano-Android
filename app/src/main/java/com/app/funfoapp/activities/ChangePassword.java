package com.app.funfoapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.funfoapp.R;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    Context context = ChangePassword.this;
    Toolbar toolbar;
    LinearLayout ll_changepass_back;
    String mUserId = "", mOldPass = "", mNewPass = "";
    EditText edt_curr_pass, edt_pass, edt_reenter_pass;
    Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        toolbar = (Toolbar) findViewById(R.id.changepass_toolbar);
        ll_changepass_back = (LinearLayout) toolbar.findViewById(R.id.ll_changepass_back);

        ll_changepass_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
        addClickListener();
    }

    //Initilization Ui Variables
    public void init() {
        edt_curr_pass = (EditText) findViewById(R.id.edt_curr_pass);
        edt_pass = (EditText) findViewById(R.id.edt_pass);
        edt_reenter_pass = (EditText) findViewById(R.id.edt_reenter_pass);
        updateBtn = (Button) findViewById(R.id.updateBtn);
    }

    //Add click Listener
    public void addClickListener() {
        updateBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateBtn:
                    if (edt_curr_pass.getText().toString().equals(""))
                        Functions.showToast(context, "Please enter your old password.");
                    else if (edt_pass.getText().toString().equals(""))
                        Functions.showToast(context, "Please enter your new password.");
                    else if (edt_reenter_pass.getText().toString().equals(""))
                        Functions.showToast(context, "Please Re-enter new password.");
                    else if (!edt_pass.getText().toString().equals(edt_reenter_pass.getText().toString()))
                        Functions.showToast(context, "Password doesn't match.");
                    else {
                        if (Validations.isNetworkAvailable((Activity) context)) {
                        try {
                            mOldPass = URLEncoder.encode(edt_curr_pass.getText().toString(), "UTF-8");
                            mNewPass = URLEncoder.encode(edt_pass.getText().toString(), "UTF-8");
                            new UpdatePass().execute();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        } else {
                            Functions.showAlert(context,"Internet Connection Error","Please check your internet connection and try again.");
                        }
                    }
                break;

            default:
                break;
        }
    }

    //Webservice to Update Password
    public class UpdatePass extends AsyncTask<Void, Void, Void> {
        int success = 0;
        Dialog dl;
        String strErrorMsg = "",title="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl = Functions.getProgressIndicator(context);
            dl.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                DbOp dbOp = new DbOp(context);
                mUserId = dbOp.getSessionId();
                dbOp.close();
                String url = getResources().getString(R.string.base_url) + "updatePassword/?userId=" + mUserId + "&oldPass=" + mOldPass + "&newPass=" + mNewPass;
                Log.e("tag", "get change url " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success != 0) {
                    strErrorMsg = new JSONObject(obj.getString("0")).getString("status");
                    title = new JSONObject(obj.getString("0")).getString("eTitle");
                } else
                    strErrorMsg = new JSONObject(obj.getString("0")).getString("status");
                    title = new JSONObject(obj.getString("0")).getString("eTitle");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dl != null && dl.isShowing())
                dl.dismiss();
            if (success != 0) {
                showAlert(context, title, strErrorMsg);
            }
            else {
                Functions.showAlert(context,title, strErrorMsg);
            }

        }
    }

    //Alert View
    public void showAlert(final Context context,String title,String msg)
    {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(msg);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
