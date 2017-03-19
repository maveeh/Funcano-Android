package com.app.funfoapp.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.adapters.GetPostcodes;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DataBaseHelper;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SignUp extends Activity implements View.OnClickListener{
    Context context=SignUp.this;
    EditText edt_signUp_email,edt_singUp_pass,edt_singUp_fName,edt_singUp_lName;
    Spinner spinner_SignUp;
    Button createBtn;
    TextView txt_SignUp_Back;
    String mEmail="",mPwd="",mFirst="",mLast="",mDob="",mGender="Male",mPostalCode="",strPostCode="";
    String[] gender = {"Male","Female"};
    ArrayAdapter adapter;
    private ArrayList<String> categoryList = new ArrayList<>();
    GetPostcodes homePageLocate;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, gender);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_SignUp.setAdapter(adapter);
        spinner_SignUp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGender = gender[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getCurrentDateTime();
    }

    //Initilization Ui Variables
    public void init()
    {
        edt_signUp_email=(EditText)findViewById(R.id.edt_signUp_email);
        edt_singUp_pass=(EditText)findViewById(R.id.edt_singUp_pass);
        edt_singUp_fName=(EditText)findViewById(R.id.edt_singUp_fName);
        edt_singUp_lName=(EditText)findViewById(R.id.edt_singUp_lName);
        spinner_SignUp=(Spinner)findViewById(R.id.spinner_SignUp);
        createBtn=(Button)findViewById(R.id.createBtn);
        txt_SignUp_Back=(TextView)findViewById(R.id.txt_SignUp_Back);
        addClickListener();
        setFont();
    }

    //add click listener
    public void addClickListener()
    {
        createBtn.setOnClickListener(this);
        txt_SignUp_Back.setOnClickListener(this);
    }

    //set Font
    public void setFont()
    {
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Semibold.ttf");
        createBtn.setTypeface(face);
        txt_SignUp_Back.setTypeface(face);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createBtn:
                    if (edt_signUp_email.getText().toString().equals("")) {
                        Functions.showToast(context, "Please enter Email.");
                    }else
                    if (!isValidEmail(edt_signUp_email.getText().toString())) {
                        Functions.showToast(context, "Please enter a valid Email.");
                    }else if (edt_singUp_pass.getText().toString().equals("")) {
                        Functions.showToast(context, "Please enter Password.");
                    }else if (edt_singUp_pass.getText().toString().length()<6) {
                        Functions.showToast(context, "Your password must have at least 6 characters, please.");
                    }
                    else if (edt_singUp_fName.getText().toString().equals("")) {
                        Functions.showToast(context, "Please enter your First Name.");
                    }
                    else if (edt_singUp_lName.getText().toString().equals("")) {
                        Functions.showToast(context, "Please enter your Last Name.");
                    }else if (mGender.toString().equals("")) {
                        Functions.showToast(context, "Please select Gender.");
                    }
                    else {
                        if(Validations.isNetworkAvailable(this)) {
                        try {
                            mEmail = URLEncoder.encode(edt_signUp_email.getText().toString(), "UTF-8");
                            mPwd = URLEncoder.encode(edt_singUp_pass.getText().toString(), "UTF-8");
                            mFirst = URLEncoder.encode(edt_singUp_fName.getText().toString(), "UTF-8");
                            mLast = URLEncoder.encode(edt_singUp_lName.getText().toString(), "UTF-8");
                            mGender = URLEncoder.encode(mGender.toString(), "UTF-8");
                            new RegisterAsync().execute();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        }else{
                            Functions.showAlert(context,"Internet Connection Error","Please check your internet connection and try again.");
                        }
                    }
                break;

            case R.id.txt_SignUp_Back:
                backKeyHandling();
                break;

            default:
                break;
        }

    }
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub

            DatePickerDialog dpd = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myDateListener, year, month, day)
            {
                @Override
                public void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            };
            return dpd;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month-1);
        String format = new SimpleDateFormat("d MMM,yyyy").format(cal.getTime());
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
            showDialog(999);
    }
    public boolean checkpostCode(){
            if(strPostCode.equals("")){
                Functions.showToast(context, "Please select postcode.");
                return false;
            }else{
                boolean isValidPostCode=false;
                try {
                    DataBaseHelper db = new DataBaseHelper(context);
                    isValidPostCode=db.checkPostCodeAvailabiity(strPostCode.toUpperCase(Locale.getDefault()));
                    db.close();
                    if (!isValidPostCode) {
                        Functions.showToast(context, "Oops.Please select a valid postcode.");
                        return false;
                    }else{
                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Functions.showToast(context, "Oops.Please select a valid postcode.");
                    return false;
                }
            }
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E d MMM,yyyy");
    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("h:mm a");
    SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("E d MMM,yyyy h:mm a");

    public boolean compareWithCurrentTimeDate(String strDate,String strTime,int gap){
        Date currentDate = null,oldDate= null;
        Calendar calendar=Calendar.getInstance();
        String currentdate = simpleDateFormat.format(calendar.getTime());
        try {
            currentDate = simpleDateFormat.parse(strDate);
            oldDate = simpleDateFormat.parse(currentdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int difference=
                ((int)((oldDate.getTime()/(24*60*60*1000))
                        -(int)(currentDate.getTime()/(24*60*60*1000))));
        if(difference <= gap){
            Functions.showToast(context,"To use our service you must be at least 10 years old.");
            return false;
        }else{
            return true;
        }
    }
    private boolean getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();


        if(ageInt >= 0 && ageInt<10) {
            Functions.showToast(context,"To be eligible to sign up for Funcano, you must be at least 10 years old.");
            return false;
        }else if(ageInt<0){
            Functions.showToast(context,"Date of birth cannot after today.");
            return false;
        }
        else
        return true;

    }
    public void getCurrentDateTime(){
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

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


    public class RegisterAsync extends AsyncTask<Void,Void,Void> {
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
                //DbOp db=new DbOp(context);
                String url = getResources().getString(R.string.base_url)+"uRegistrationByEmail/?uemail=" + mEmail+ "&upwd="+mPwd + "&uFirstName="+mFirst
                        + "&uLastName="+mLast + "&uGender=" + mGender + "&uDob=&uPostalCode=&uOs=Android&uDeviceId=&uLat=&uLong=";
                //Log.e("tag", "response " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                //Log.e("tag", "response " + res);
                JSONObject obj=new JSONObject(res);
                success=obj.getInt("success");
                if(success==1){
                    strErrorMsg=new JSONObject(obj.getString("0")).getString("error");
                    title=new JSONObject(obj.getString("0")).getString("eTitle");
                }else{
                    strErrorMsg=obj.getString("error");
                    title=obj.getString("eTitle");
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
                Intent intent=new Intent(context,MainActivity.class);
                startActivity(intent);
                finish();
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
                        Intent intent=new Intent(context,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
