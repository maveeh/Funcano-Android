package com.app.funfoapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    Context context = LoginActivity.this;
    LinearLayout llsignIn, llfacebook, llGoogle;
    TextView createTextView, txt_fb, txt_google, txt_email;
    CallbackManager callbackManager;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    public String loginType = "";

    private static final int RC_SIGN_IN = 9001;
    private boolean mIntentInProgress;
    private boolean signedInUser;
    private ConnectionResult mConnectionResult;
    String profilePicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
        init();
        addClickListener();
    }

    //Initilization Ui Variables
    public void init() {
        llfacebook = (LinearLayout) findViewById(R.id.llfacebook);
        llGoogle = (LinearLayout) findViewById(R.id.llgoogle);
        llsignIn = (LinearLayout) findViewById(R.id.llsignIn);
        createTextView = (TextView) findViewById(R.id.createAcc_TextView);
        txt_fb = (TextView) findViewById(R.id.txtFb);
        txt_google = (TextView) findViewById(R.id.txtGoogle);
        txt_email = (TextView) findViewById(R.id.txtEmail);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold.ttf");
        txt_email.setTypeface(face);
        txt_google.setTypeface(face);
        txt_fb.setTypeface(face);
    }

    //Add click Listener
    public void addClickListener() {
        llfacebook.setOnClickListener(this);
        llGoogle.setOnClickListener(this);
        llsignIn.setOnClickListener(this);
        createTextView.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.llsignIn:
                Intent intent = new Intent(this, SignIn.class);
                startActivity(intent);
                finish();
                break;
            case R.id.createAcc_TextView:
                Intent intent1 = new Intent(this, SignUp.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.llfacebook:
                if (Validations.isNetworkAvailable(this)) {
                    loginType = "byfacebook";
                    type = "facebook";
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","user_friends", "email"));
                    fbLogin();
                } else {
                    Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
                }
                break;
            case R.id.llgoogle:
                if (Validations.isNetworkAvailable(this)) {
                    dialogVisible = false;
                    loginType = "bygmail";
                    type = "gmail";
                    if(allowPermission()) {
                        googlePlusLogin();

                        new RegisterNewAsync().execute();
                    }
                } else {
                    Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
                }
                break;
        }

    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    boolean dialogVisible = false;

    private void resolveSignInError() {
        if (mConnectionResult != null) {
            if (mConnectionResult.hasResolution() && dialogVisible == false) {
                dialogVisible = true;

                try {
                    mIntentInProgress = true;
                    mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            } else {
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // store mConnectionResult
            mConnectionResult = result;
            if (signedInUser) {
                resolveSignInError();
            }
        }
    }

    private void googlePlusLogout() {

        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateProfile(false);
        }
    }


    @Override
    public void onConnected(Bundle arg0) {
        signedInUser = false;
        getProfileInformation();
    }

    private void updateProfile(boolean isSignedIn) {

    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String[] name;
                name= currentPerson.getDisplayName().split(" ");
                str_firstname = name[0];
                str_lastname = name[1];
                profilePicUrl = currentPerson.getImage().getUrl();
                dob = currentPerson.getBirthday();
                if(currentPerson.getGender() == 0)
                    gender = "male";
                else
                    gender = "female";
                str_email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                str_id = currentPerson.getId();
                sp = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("image", profilePicUrl);
                editor.commit();
                // update profile frame with new info about Google Account
                // profile
                DbOp db = new DbOp(context);
                String id = db.getSessionId();
                db.close();
                if (loginType.equals("bygmail"))
                    new RegisterNewAsync().execute();
                updateProfile(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
        updateProfile(false);
    }

    private void googlePlusLogin() {
        if (!mGoogleApiClient.isConnecting()) {

            signedInUser = true;
            resolveSignInError();
        }
    }

    String str_email = "", str_firstname = "", str_lastname = "", str_id = "", type = "",dob="",gender = "";
    SharedPreferences sp;

    public void fbLogin() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject json,
                                            GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");
                                            try {
                                                String jsonresult = String.valueOf(json);
                                                profilePicUrl = json.getJSONObject("picture").getJSONObject("data").getString("url");
                                                str_email = json.getString("email");
                                                str_id = json.getString("id");
                                                gender = json.getString("gender");
                                                String[] name;
                                                name = json.getString("name").split(" ");
                                                str_firstname = name[0];
                                                str_lastname = name[1];
                                                sp = PreferenceManager.getDefaultSharedPreferences(context);
                                                SharedPreferences.Editor editor = sp.edit();
                                                editor.putString("image", profilePicUrl);
                                                editor.commit();
                                                new RegisterNewAsync().execute();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,birthday,picture.type(large),gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });
    }

    //Webservice for registor user
    public class RegisterNewAsync extends AsyncTask<Void, Void, Void> {
        Dialog dl;
        int success = 0;
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
                DbOp db = new DbOp(context);
                String url = getResources().getString(R.string.base_url) + "uRegistrationLogin";
                //Log.e("tag", "response " + url);
//                String url = getResources().getString(R.string.base_url) + "uRegistrationLogin/?uemail=" + str_email + "&uFirstName=" + URLEncoder.encode(str_firstname, "UTF-8")
//                        + "&uLastName=" + str_lastname + "&socialId=" + str_id + "&signUpType=" + type + "&socialImage=" + URLEncoder.encode(profilePicUrl, "UTF-8") + "&dob=" + dob + "&gender=" + gender + "&uPostalCode=&uOs=Android&uDeviceId=&uLat=&uLong=";

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("uemail", str_email));
                params.add(new BasicNameValuePair("uFirstName", str_firstname));
                params.add(new BasicNameValuePair("uLastName", str_lastname));
                params.add(new BasicNameValuePair("socialId", str_id));
                params.add(new BasicNameValuePair("signUpType", type));
                params.add(new BasicNameValuePair("socialImage", profilePicUrl));
                params.add(new BasicNameValuePair("dob", ""));
                params.add(new BasicNameValuePair("gender", gender));
                params.add(new BasicNameValuePair("uPostalCode", ""));
                params.add(new BasicNameValuePair("uOs", "Android"));
                params.add(new BasicNameValuePair("uDeviceId", ""));
                params.add(new BasicNameValuePair("uLat", ""));
                params.add(new BasicNameValuePair("uLong", ""));

                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", params);
                //Log.e("tag", "response " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    db.insertSessionId(String.valueOf(new JSONObject(obj.getString("0")).getInt("userId")), str_firstname, new JSONObject(obj.getString("0")).getString("uType"), loginType,new JSONObject(obj.getString("0")).getString("uDistance"));
                } else {
                    strErrorMsg = new JSONObject(obj.getString("0")).getString("error");
                    title = new JSONObject(obj.getString("0")).getString("eTitle");
                }
                db.close();
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
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Functions.showAlert(context, title, strErrorMsg);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            //if (resultCode == RESULT_OK) {
                signedInUser = false;
            //}
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }
    public boolean allowPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.GET_ACCOUNTS)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.GET_ACCOUNTS}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                try {
                    googlePlusLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

