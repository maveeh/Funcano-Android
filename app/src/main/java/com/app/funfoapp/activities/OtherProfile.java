package com.app.funfoapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.common.CircularImageView;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.RoundedImage;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.parser.JSONParser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class OtherProfile extends AppCompatActivity implements View.OnClickListener {

    Context context = OtherProfile.this;
    Toolbar toolbar;
    String mUserId = "", mUserType = "", mFirstName = "", mLastName = "", mEmailId = "",
            mPhone = "", mGender = "", mAbout = "", mContactName = "", mWebsite = "",
            mAddress = "", mDob = "", mTicketUrl = "", profilePic = "", mType = "", mLocation = "";
    RoundedImageView img_basic_userimage;
    TextView txt_userName, txt_otherprofile_Email, txt_otherprofile_email, txt_otherprofile_City, txt_otherprofile_city, txt_otherprofile_AboutMe,
            txt_otherprofile_aboutme, txt_otherprofile_DOB, txt_otherprofile_dob, txt_otherprofile_Gender, txt_otherprofile_gender, txt_otherprofile_ContName, txt_otherprofile_contname, txt_otherprofile_Website, txt_otherprofile_website,
            txt_otherprofile_Ticket, txt_otherprofile_ticket, txt_otherprofile_Address, txt_otherprofile_address, txt_otherprofile_Phone, txt_otherprofile_phone;
    LinearLayout ll_otherprofile_city, ll_otherprofile_aboutme, ll_otherprofile_dob, ll_otherprofile_gender, ll_otherprofile_contname,
            ll_otherprofile_website, ll_otherprofile_ticket, ll_otherprofile_address, ll_profile_back, ll_otherprofile_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        mUserId = getIntent().getExtras().getString("userId");
        mUserType = getIntent().getExtras().getString("uType");
        setToolbar();
        init();
        loadWS();
    }

    //set Toolbar
    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        ll_profile_back = (LinearLayout) toolbar.findViewById(R.id.ll_profile_back);
        ll_profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Initilization Ui Variables
    public void init() {
        img_basic_userimage = (RoundedImageView) findViewById(R.id.img_basic_userimage);
        txt_userName = (TextView) findViewById(R.id.txt_userName);
        txt_otherprofile_Email = (TextView) findViewById(R.id.txt_otherprofile_Email);
        txt_otherprofile_email = (TextView) findViewById(R.id.txt_otherprofile_email);
        txt_otherprofile_City = (TextView) findViewById(R.id.txt_otherprofile_City);
        txt_otherprofile_city = (TextView) findViewById(R.id.txt_otherprofile_city);
        txt_otherprofile_AboutMe = (TextView) findViewById(R.id.txt_otherprofile_AboutMe);
        txt_otherprofile_aboutme = (TextView) findViewById(R.id.txt_otherprofile_aboutme);
        txt_otherprofile_DOB = (TextView) findViewById(R.id.txt_otherprofile_DOB);
        txt_otherprofile_dob = (TextView) findViewById(R.id.txt_otherprofile_dob);
        txt_otherprofile_Gender = (TextView) findViewById(R.id.txt_otherprofile_Gender);
        txt_otherprofile_gender = (TextView) findViewById(R.id.txt_otherprofile_gender);
        txt_otherprofile_ContName = (TextView) findViewById(R.id.txt_otherprofile_ContName);
        txt_otherprofile_contname = (TextView) findViewById(R.id.txt_otherprofile_contname);
        txt_otherprofile_Website = (TextView) findViewById(R.id.txt_otherprofile_Website);
        txt_otherprofile_website = (TextView) findViewById(R.id.txt_otherprofile_website);
        txt_otherprofile_Ticket = (TextView) findViewById(R.id.txt_otherprofile_Ticket);
        txt_otherprofile_ticket = (TextView) findViewById(R.id.txt_otherprofile_ticket);
        txt_otherprofile_Address = (TextView) findViewById(R.id.txt_otherprofile_Address);
        txt_otherprofile_address = (TextView) findViewById(R.id.txt_otherprofile_address);
        txt_otherprofile_Phone = (TextView) findViewById(R.id.txt_otherprofile_Phone);
        txt_otherprofile_phone = (TextView) findViewById(R.id.txt_otherprofile_phone);
        ll_otherprofile_city = (LinearLayout) findViewById(R.id.ll_otherprofile_city);
        ll_otherprofile_aboutme = (LinearLayout) findViewById(R.id.ll_otherprofile_aboutme);
        ll_otherprofile_dob = (LinearLayout) findViewById(R.id.ll_otherprofile_dob);
        ll_otherprofile_gender = (LinearLayout) findViewById(R.id.ll_otherprofile_gender);
        ll_otherprofile_address = (LinearLayout) findViewById(R.id.ll_otherprofile_address);
        ll_otherprofile_contname = (LinearLayout) findViewById(R.id.ll_otherprofile_contname);
        ll_otherprofile_website = (LinearLayout) findViewById(R.id.ll_otherprofile_website);
        ll_otherprofile_ticket = (LinearLayout) findViewById(R.id.ll_otherprofile_ticket);
        ll_otherprofile_phone = (LinearLayout) findViewById(R.id.ll_otherprofile_phone);
        setFont();
    }

    //set Font
    public void setFont() {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        txt_userName.setTypeface(face);
        txt_otherprofile_email.setTypeface(face);
        txt_otherprofile_city.setTypeface(face);
        txt_otherprofile_dob.setTypeface(face);
        txt_otherprofile_gender.setTypeface(face);
        txt_otherprofile_contname.setTypeface(face);
        txt_otherprofile_website.setTypeface(face);
        txt_otherprofile_ticket.setTypeface(face);
        txt_otherprofile_address.setTypeface(face);
        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        txt_otherprofile_Email.setTypeface(face1);
        txt_otherprofile_City.setTypeface(face1);
        txt_otherprofile_AboutMe.setTypeface(face1);
        txt_otherprofile_aboutme.setTypeface(face1);
        txt_otherprofile_DOB.setTypeface(face1);
        txt_otherprofile_Gender.setTypeface(face1);
        txt_otherprofile_ContName.setTypeface(face1);
        txt_otherprofile_Website.setTypeface(face1);
        txt_otherprofile_Ticket.setTypeface(face1);
        txt_otherprofile_Address.setTypeface(face1);
    }

    @Override
    public void onClick(View v) {

    }

    public void loadWS() {
        if (Validations.isNetworkAvailable((Activity) context)) {
            new ProfileAsync().execute();
        } else {
            Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
        }
    }

    //webservice to get Profile Detail
    public class ProfileAsync extends AsyncTask<Void, Void, Void> {
        Dialog dl;
        int success = 0;
        String strErrorMsg = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl = Functions.getProgressIndicator(context);
            dl.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String url = getResources().getString(R.string.base_url) + "getProfileOfUser/?userId=" + mUserId;
                //Log.e("tag", "response " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                //Log.e("tag", "response " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    mFirstName = new JSONObject(obj.getString("0")).getString("uFirstName");
                    mLastName = new JSONObject(obj.getString("0")).getString("uLastName");
                    mEmailId = new JSONObject(obj.getString("0")).getString("email");
                    mGender = new JSONObject(obj.getString("0")).getString("uGender");
                    mAbout = new JSONObject(obj.getString("0")).getString("uAboutUs");
                    mContactName = new JSONObject(obj.getString("0")).getString("uContactName");
                    mType = new JSONObject(obj.getString("0")).getString("uType");
                    mWebsite = new JSONObject(obj.getString("0")).getString("uWebsite");
                    mAddress = new JSONObject(obj.getString("0")).getString("uAddress");
                    mDob = new JSONObject(obj.getString("0")).getString("uDob");
                    mTicketUrl = new JSONObject(obj.getString("0")).getString("uTicketingUrl");
                    mPhone = new JSONObject(obj.getString("0")).getString("phone");
                    profilePic = new JSONObject(obj.getString("0")).getString("userImage");
                    mLocation = new JSONObject(obj.getString("0")).getString("uLocation");
                } else {
                    strErrorMsg = new JSONObject(obj.getString("0")).getString("status");
                }
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
            if (success == 1) {
                if (mType.equals("basic") || mType.equals("")) {
                    ll_otherprofile_ticket.setVisibility(View.GONE);
                    ll_otherprofile_contname.setVisibility(View.GONE);
                    ll_otherprofile_website.setVisibility(View.GONE);
                    ll_otherprofile_phone.setVisibility(View.GONE);
                } else {
                    ll_otherprofile_ticket.setVisibility(View.VISIBLE);
                    ll_otherprofile_contname.setVisibility(View.VISIBLE);
                    ll_otherprofile_website.setVisibility(View.VISIBLE);
                    ll_otherprofile_phone.setVisibility(View.VISIBLE);
                }
                if (mPhone.equals(""))
                    mPhone = "Not Available";
                if (mWebsite.equals(""))
                    mWebsite = "Not Available";
                if (mTicketUrl.equals(""))
                    mTicketUrl = "Not Available";
                if (mContactName.equals(""))
                    mContactName = "Not Available";
                txt_userName.setText(mFirstName + " " + mLastName);
                txt_otherprofile_email.setText(mEmailId);
                if (profilePic.contains("http")) {
                    Picasso.with(context)
                            .load(profilePic)
                            .into(img_basic_userimage);
                } else {
                    Picasso.with(context)
                            .load(getResources().getString(R.string.base_url_profile_image) + profilePic)
                            .into(img_basic_userimage);
                }
                if (mGender.equals("")) {
                    ll_otherprofile_gender.setVisibility(View.GONE);
                } else {
                    ll_otherprofile_gender.setVisibility(View.VISIBLE);
                    txt_otherprofile_gender.setText(mGender);
                }
                if (mAbout.equals("")) {
                    txt_otherprofile_aboutme.setText("Not Available");
                } else {
                    txt_otherprofile_aboutme.setText(mAbout);
                    ll_otherprofile_aboutme.setVisibility(View.VISIBLE);
                }
                if (mDob.equals("")) {
                    ll_otherprofile_dob.setVisibility(View.GONE);
                } else {
                    txt_otherprofile_dob.setText(mDob);
                    ll_otherprofile_dob.setVisibility(View.VISIBLE);
                }
                if (mLocation.equals("")) {
                    txt_otherprofile_city.setText("Not Available");
                } else {
                    ll_otherprofile_city.setVisibility(View.VISIBLE);
                    txt_otherprofile_city.setText(mLocation);
                }
                if (mAddress.equals("")) {
                    txt_otherprofile_address.setText("Not Available");
                } else {
                    ll_otherprofile_address.setVisibility(View.VISIBLE);
                    txt_otherprofile_address.setText(mAddress);
                }
                txt_otherprofile_ticket.setText(mTicketUrl);
                txt_otherprofile_contname.setText(mContactName);
                txt_otherprofile_website.setText(mWebsite);
                txt_otherprofile_phone.setText(mPhone);

            } else {

            }
        }
    }
}
