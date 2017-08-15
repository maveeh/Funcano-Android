package com.app.funfoapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.funfoapp.R;
import com.app.funfoapp.utility.DbOp;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;

public class Splash extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Context context = Splash.this;
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;
    private boolean signedInUser;
    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (!isLogin()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this, LoginActivity.class));
                    finish();
                }
            }, 3000);
        } else {
            new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            startActivity(new Intent(Splash.this, MainActivity.class));
                            finish();
                    }
                }, 3000);
        }
    }

    String loginType = "";

    public boolean isLogin() {
        DbOp db = new DbOp(context);
        String id = db.getSessionId();
        loginType = db.getlogintype();
        db.close();
        if (id.equals("-1")) {
            return false;
        } else {
            return true;
        }
    }

    private void resolveSignInError() {
        if (loginType.equals("bygmail"))
            if (mGoogleApiClient != null)
                if (mConnectionResult.hasResolution()) {
                    try {
                        mIntentInProgress = true;
                        mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    } catch (IntentSender.SendIntentException e) {
                        mIntentInProgress = false;
                        mGoogleApiClient.connect();
                    }
                }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (loginType.equals("bygmail")) {
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
    }

    @Override
    public void onConnected(Bundle arg0) {
        if (loginType.equals("bygmail")) {
            signedInUser = false;
            //Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
            getProfileInformation();
        }
    }

    private void updateProfile(boolean isSignedIn) {
        if (isSignedIn) {
            Log.e("tag", "login");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this, MainActivity.class));
                    finish();
                }
            }, 3000);

        } else {
            Log.e("tag", "not login");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this, LoginActivity.class));
                    finish();
                }
            }, 3000);
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                updateProfile(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (loginType.equals("bygmail")) {
            mGoogleApiClient.connect();
            updateProfile(false);
        }
    }
}
