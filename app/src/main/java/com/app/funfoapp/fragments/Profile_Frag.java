package com.app.funfoapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.app.funfoapp.R;
import com.app.funfoapp.activities.EditProfile;
import com.app.funfoapp.activities.LoginActivity;
import com.app.funfoapp.activities.Settings;
import com.app.funfoapp.activities.SignIn;
import com.app.funfoapp.common.CircularImageView;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.InAppBilling;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.interfaces.NotifyFragment;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * Created by Pooja_Pollysys on 4/7/16.
 */
public class Profile_Frag extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,InAppBilling.InAppBillingListener {

    View rootView;
    Context context;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean signedInUser;
    private ConnectionResult mConnectionResult;
    private static final int RC_SIGN_IN = 0;
    String type = "", mUserId = "", mFirstName = "",mLastName= "", mEmailId = "", mPhone = "",mGender ="",
            mAbout ="",mContactName="",mWebsite = "",mAddress ="",mDob ="",mTicketUrl="",profilePic="",uType="";
    LinearLayout ll_profile_logout, ll_profile_address, ll_profile_website, ll_profile_ticket, ll_profile_contact, ll_profile_funcies, ll_profile_share,
            ll_profile_love, ll_profile_upgrade, ll_profile_settings, ll_profile_phone;
    ImageView img_coverpic;
    RoundedImageView img_userimage;
    TextView txt_profile_username, txt_profile_Email, txt_profile_email, txt_profile_Phone, txt_profile_phone, txt_profile_Address, txt_profile_address, txt_profile_Website, txt_profile_website, txt_profile_Ticket, txt_profile_ticket, txt_profile_Contact, txt_profile_contact, txt_profile_funcies, txt_profile_share, txt_profile_love, txt_profile_upgrade, txt_profile_settings, txt_profile_logout;

    NotifyFragment notifyCallBack;
    IInAppBillingService mService;
    public static boolean ProfileUpdate=true;
    String mMode="";
    private InAppBilling inAppBilling;
    private TextView		msgBox;
    private float			mmToPixels;

    // purchase request code
    private static final int PURCHASE_REQUEST_CODE = 1;

    // product code
    private static final String	PRODUCT_SKU = "product0001";

    // google assigned application public key
// NOTE: You must replace this key with your own key
    private static final String	applicationPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhNtHXxJCs8zVZgkmdNjFjNYGXpnOesFCzKa0BFPKPVJ" +
                    "PAmD8s8emQ9q7WcO60zmyLYhFstMFF6KWl/jdERcKTcUma5M5c3bgxl8p7EQMbkAjjvvrfkDu529cVMXBCR" +
                    "P2Q6X1spKdEFciD/ADJxeA+ihI/Uo1BlAO8Q8AH3rQBLE2HsMzvJ5k2qoHVgBDk7ksCLlU4PyPusLHrCOUr" +
                    "Fnm9oPXvccEi9wu7V3xYhltuSMjY/NWG+QvompNKG563ZVKyLBo7FfTEFvhQdP0RMZ5ZUptW/HVMYcSodhQKT" +
                    "jK+LWyz6XdhDPwlYZsx1xhgk84AFKwwlfKbMjO8ntdo0yDsQIDAQAB";

    // children views id
    private static final int	TITLE1_ID = 1;
    private static final int	TITLE2_ID = 2;
    private static final int	TITLE3_ID = 3;
    private static final int	BUY_BUTTON_ID = 4;
    private static final int	CONSUME_BUTTON_ID = 5;
    private static final int	MESSAGE_BOX_ID = 6;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.profile_frag, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FacebookSdk.sdkInitialize(context);
        notifyCallBack = (NotifyFragment) context;
        DbOp dbOp = new DbOp(getActivity());
        type = dbOp.getlogintype();
        dbOp.close();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
        init(rootView);
    }

    //method to load wevbservice
    public void loadWS() {
        if (Validations.isNetworkAvailable(getActivity())) {
            new ProfileAsync().execute();
        } else {
            Functions.showAlert(context,"Internet Connection Error","Please check your internet connection and try again.");
        }
    }

    //Initilization Ui Variables
    public void init(View v) {
        ll_profile_phone = (LinearLayout) v.findViewById(R.id.ll_profile_phone);
        ll_profile_address = (LinearLayout) v.findViewById(R.id.ll_profile_address);
        ll_profile_website = (LinearLayout) v.findViewById(R.id.ll_profile_website);
        ll_profile_ticket = (LinearLayout) v.findViewById(R.id.ll_profile_ticket);
        ll_profile_contact = (LinearLayout) v.findViewById(R.id.ll_profile_contact);
        img_coverpic = (ImageView) v.findViewById(R.id.img_coverpic);
        img_userimage = (RoundedImageView) v.findViewById(R.id.img_userimage);
        txt_profile_username = (TextView) v.findViewById(R.id.txt_profile_username);
        txt_profile_Email = (TextView) v.findViewById(R.id.txt_profile_Email);
        txt_profile_email = (TextView) v.findViewById(R.id.txt_profile_email);
        txt_profile_Phone = (TextView) v.findViewById(R.id.txt_profile_Phone);
        txt_profile_phone = (TextView) v.findViewById(R.id.txt_profile_phone);
        txt_profile_Address = (TextView) v.findViewById(R.id.txt_profile_Address);
        txt_profile_address = (TextView) v.findViewById(R.id.txt_profile_address);
        txt_profile_Website = (TextView) v.findViewById(R.id.txt_profile_Website);
        txt_profile_website = (TextView) v.findViewById(R.id.txt_profile_website);
        txt_profile_Ticket = (TextView) v.findViewById(R.id.txt_profile_Ticket);
        txt_profile_ticket = (TextView) v.findViewById(R.id.txt_profile_ticket);
        txt_profile_Contact = (TextView) v.findViewById(R.id.txt_profile_Contact);
        txt_profile_contact = (TextView) v.findViewById(R.id.txt_profile_contact);
        ll_profile_funcies = (LinearLayout) v.findViewById(R.id.ll_profile_funcies);
        ll_profile_share = (LinearLayout) v.findViewById(R.id.ll_profile_share);
        ll_profile_love = (LinearLayout) v.findViewById(R.id.ll_profile_love);
        ll_profile_upgrade = (LinearLayout) v.findViewById(R.id.ll_profile_upgrade);
        ll_profile_settings = (LinearLayout) v.findViewById(R.id.ll_profile_settings);
        ll_profile_logout = (LinearLayout) v.findViewById(R.id.ll_profile_logout);
        txt_profile_funcies = (TextView) v.findViewById(R.id.txt_profile_funcies);
        txt_profile_share = (TextView) v.findViewById(R.id.txt_profile_share);
        txt_profile_love = (TextView) v.findViewById(R.id.txt_profile_love);
        txt_profile_upgrade = (TextView) v.findViewById(R.id.txt_profile_upgrade);
        txt_profile_settings = (TextView) v.findViewById(R.id.txt_profile_settings);
        txt_profile_logout = (TextView) v.findViewById(R.id.txt_profile_logout);
        addClickListener();
        setFont();
    }

    //set Font
    public void setFont() {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        txt_profile_email.setTypeface(face);
        txt_profile_phone.setTypeface(face);
        txt_profile_address.setTypeface(face);
        txt_profile_website.setTypeface(face);
        txt_profile_ticket.setTypeface(face);
        txt_profile_contact.setTypeface(face);
        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        txt_profile_username.setTypeface(face1);
        txt_profile_Email.setTypeface(face1);
        txt_profile_Phone.setTypeface(face1);
        txt_profile_Address.setTypeface(face1);
        txt_profile_Website.setTypeface(face1);
        txt_profile_Ticket.setTypeface(face1);
        txt_profile_Contact.setTypeface(face1);
        txt_profile_funcies.setTypeface(face1);
        txt_profile_share.setTypeface(face1);
        txt_profile_love.setTypeface(face1);
        txt_profile_upgrade.setTypeface(face1);
        txt_profile_settings.setTypeface(face1);
        txt_profile_logout.setTypeface(face1);
    }

    //add click listener
    public void addClickListener() {
        ll_profile_funcies.setOnClickListener(this);
        ll_profile_share.setOnClickListener(this);
        ll_profile_love.setOnClickListener(this);
        ll_profile_upgrade.setOnClickListener(this);
        ll_profile_settings.setOnClickListener(this);
        ll_profile_logout.setOnClickListener(this);
        img_userimage.setOnClickListener(this);
    }

    // For In-App Billing
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(ProfileUpdate == true)
        loadWS();
        ProfileUpdate = false;
    }

    boolean shouldLogout = false;
    SharedPreferences sp;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_profile_logout:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                // set title
                alertDialogBuilder.setTitle("Logout");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sp = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("image", null);
                                editor.commit();
                                DbOp dbOp = new DbOp(getActivity());
                                    if (Validations.isNetworkAvailable(getActivity())) {
                                        if (type.equals("byfacebook")) {
                                            //dbOp.insertSessionId("-1", "", "", "");
                                            LoginManager.getInstance().logOut();
                                            AccessToken.setCurrentAccessToken(null);
                                            shouldLogout = true;
                                        } else if (type.equals("bygmail")) {
                                            //dbOp.insertSessionId("-1", "", "", "");
                                            googlePlusLogout();
                                            shouldLogout = true;
                                        }else {
                                            shouldLogout = true;
                                            //dbOp.insertSessionId("-1", "", "", "");
                                        }
                                    } else {
                                        shouldLogout = false;
                                        Functions.showAlert(context,"Internet Connection Error","Please check your internet connection and try again.");
                                    }

                                dbOp.close();
                                if (shouldLogout) {
                                    new LogOutAsync().execute();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;

            case R.id.ll_profile_funcies:
                notifyCallBack.isNotifyFrag();
                break;

            case R.id.ll_profile_share:
                final String appPackageName = context.getPackageName();
                Intent intent2 = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + appPackageName);
                startActivity(Intent.createChooser(intent2, "Share"));
                break;

            case R.id.ll_profile_love:
                if (Validations.isNetworkAvailable(getActivity())) {
                    //insert date
                    final String appPackageName1 = context.getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName1)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName1)));
                    }
                } else {
                    Functions.showAlert(context,"Internet Connection Error","Please check your internet connection and try again.");
                }
                break;

            case R.id.ll_profile_settings:
                Intent intent = new Intent(context, Settings.class);
                startActivity(intent);
                break;

            case R.id.ll_profile_upgrade:

//                if(inAppBilling == null)
//                {
//                    // create in-app billing object
//                    inAppBilling = new InAppBilling(getActivity(), this, applicationPublicKey, PURCHASE_REQUEST_CODE);
//                }
//
//                // InAppBilling initialization
//                // NOTE: if inAppBilling is already active, the call is ignored
//                // you can use isActive() to test for class active,
//                // or test the result value of the call to find out
//                // if start service connection was done (true) or the class was active (false)
//                inAppBilling.startServiceConnection(InAppBilling.ITEM_TYPE_ONE_TIME_PURCHASE,
//                        PRODUCT_SKU, InAppBilling.ACTIVITY_TYPE_PURCHASE);


                AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(getActivity());
                // set title
                alertDialogBuilder1.setTitle("Alert");
                // set dialog message
                alertDialogBuilder1
                        .setMessage("Upgrade your Account")
                        .setCancelable(true)
                        .setPositiveButton("Basic", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mMode = "basic";
                                if (Validations.isNetworkAvailable(getActivity())) {
                                    new UpdateUserTypeAsync().execute();
                                } else {
                                    Functions.showAlert(context,"Internet Connection Error","Please check your internet connection and try again.");
                                }
                            }
                        })
                        .setNegativeButton("Premium", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mMode = "premium";
                                if (Validations.isNetworkAvailable(getActivity())) {
                                    new UpdateUserTypeAsync().execute();
                                } else {
                                    Functions.showAlert(context,"Internet Connection Error","Please check your internet connection and try again.");
                                }
                            }
                        });
                AlertDialog alertDialog1 = alertDialogBuilder1.create();
                alertDialog1.show();

//                Intent serviceIntent =
//                        new Intent("com.android.vending.billing.InAppBillingService.BIND");
//                serviceIntent.setPackage("com.android.vending");
//                context.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
                break;

            case R.id.img_userimage:
                Intent intent1 = new Intent(context, EditProfile.class);
                intent1.putExtra("uType",uType);
                startActivity(intent1);
                break;

            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (type.equals("bygmail"))
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (type.equals("bygmail"))
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
    }

    @Override
    public void onDestroy() {
        {
            if(inAppBilling != null) inAppBilling.dispose();
            super.onDestroy();
            return;
        }


//        super.onDestroy();
//        if (mService != null) {
//            context.unbindService(mServiceConn);
//        }
    }

    //    private void resolveSignInError() {
//        if(type.equals("bygmail"))
//        if (mConnectionResult.hasResolution()) {
//            try {
//                mIntentInProgress = true;
//                mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
//            } catch (IntentSender.SendIntentException e) {
//                mIntentInProgress = false;
//                mGoogleApiClient.connect();
//            }
//        }
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // purchase request code
        if(inAppBilling != null && requestCode == PURCHASE_REQUEST_CODE)
        {
            // Pass on the activity result to inAppBilling for handling
            inAppBilling.onActivityResult(resultCode, data);
        }
        else
        {
            // default onActivityResult
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (type.equals("bygmail")) {
            if (!result.hasResolution()) {
                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(), 0).show();
                return;
            }

            if (!mIntentInProgress) {
                mConnectionResult = result;
                if (signedInUser) {
                    //resolveSignInError();
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        if (type.equals("bygmail")) {
            signedInUser = false;
        }
    }


    @Override
    public void onConnectionSuspended(int cause) {
        if (type.equals("bygmail")) {
            mGoogleApiClient.connect();
        }
    }

    public void logout(View v) {
        googlePlusLogout();
    }

    private void googlePlusLogout() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void inAppBillingBuySuccsess() {
        msgBox.setText("In App purchase successful");
    }

    @Override
    public void inAppBillingItemAlreadyOwned() {
        msgBox.setText("Product is already owned.\nPurchase was not initiated.");
    }

    @Override
    public void inAppBillingCanceled() {
        msgBox.setText("Purchase was canceled by user");
    }

    @Override
    public void inAppBillingConsumeSuccsess() {
        msgBox.setText("In App consume product successful");
    }

    @Override
    public void inAppBillingItemNotOwned() {
        msgBox.setText("Product is not owned.\nConsume failed.");
    }

    @Override
    public void inAppBillingFailure(String errorMessage) {
        msgBox.setText("Purchase or consume process failed.\n" + errorMessage);
    }

    //webservice for get Profile detail
    public class ProfileAsync extends AsyncTask<Void, Void, Void> {
        Dialog dl;
        int success = 0;
        String strErrorMsg = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl = Functions.getProgressIndicator(context);
            dl.setCancelable(false);
            dl.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                DbOp dbOp = new DbOp(context);
                mUserId = dbOp.getSessionId();
                String url = getResources().getString(R.string.base_url) + "getProfileOfUser/?userId=" + mUserId;
                Log.e("tag", "response " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                Log.e("tag", "response " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    mFirstName = new JSONObject(obj.getString("0")).getString("uFirstName");
                    mLastName = new JSONObject(obj.getString("0")).getString("uLastName");
                    mEmailId = new JSONObject(obj.getString("0")).getString("email");
                    mGender = new JSONObject(obj.getString("0")).getString("uGender");
                    mAbout = new JSONObject(obj.getString("0")).getString("uAboutUs");
                    mContactName = new JSONObject(obj.getString("0")).getString("uContactName");
                    mWebsite = new JSONObject(obj.getString("0")).getString("uWebsite");
                    mAddress = new JSONObject(obj.getString("0")).getString("uAddress");
                    mDob = new JSONObject(obj.getString("0")).getString("uDob");
                    mTicketUrl = new JSONObject(obj.getString("0")).getString("uTicketingUrl");
                    mPhone = new JSONObject(obj.getString("0")).getString("phone");
                    profilePic = new JSONObject(obj.getString("0")).getString("userImage");
                    uType = new JSONObject(obj.getString("0")).getString("uType");
                } else {
                    strErrorMsg = new JSONObject(obj.getString("0")).getString("status");
                }
                dbOp.close();
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
                if(uType.equals("basic")) {
                    ll_profile_phone.setVisibility(View.GONE);
                    ll_profile_website.setVisibility(View.GONE);
                    ll_profile_ticket.setVisibility(View.GONE);
                    ll_profile_contact.setVisibility(View.GONE);
                }
                else {
                    ll_profile_phone.setVisibility(View.VISIBLE);
                    ll_profile_website.setVisibility(View.VISIBLE);
                    ll_profile_ticket.setVisibility(View.VISIBLE);
                    ll_profile_contact.setVisibility(View.VISIBLE);
                }
                if(mPhone.equals(""))
                    mPhone = "Not Available";
                if(mWebsite.equals(""))
                    mWebsite = "Not Available";
                if(mTicketUrl.equals(""))
                    mTicketUrl = "Not Available";
                if(mContactName.equals(""))
                    mContactName = "Not Available";
                txt_profile_phone.setText(mPhone);
                txt_profile_website.setText(mWebsite);
                txt_profile_ticket.setText(mTicketUrl);
                txt_profile_contact.setText(mContactName);
                txt_profile_username.setText(mFirstName + " " + mLastName);
                txt_profile_email.setText(mEmailId);
                if(profilePic.contains("http")) {
                    Picasso.with(context)
                            .load(profilePic)
                            .into(img_userimage);
                }
                else {
                    Picasso.with(context)
                            .load(getResources().getString(R.string.base_url_profile_image)+profilePic)
                            .into(img_userimage);
                }
                if(mAddress.equals("")) {
                    ll_profile_address.setVisibility(View.GONE);
                }
                else {
                    ll_profile_address.setVisibility(View.VISIBLE);
                    txt_profile_address.setText(mAddress);
                }
            } else {

            }
        }
    }

    //webservice to update UserType "Basic or Premium"
    public class UpdateUserTypeAsync extends AsyncTask<Void, Void, Void> {
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
                DbOp dbOp = new DbOp(context);
                mUserId = dbOp.getSessionId();
                dbOp.close();
                //http://demoforclients.net/funfo/index.php/users/updateUserType/?userId=202&uType=basic
                String url = getResources().getString(R.string.base_url) + "updateUserType/?userId=" + mUserId + "&uType=" + mMode;
                Log.e("tag", "response " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                Log.e("tag", "response " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    strErrorMsg = new JSONObject(obj.getString("0")).getString("status");
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
                DbOp dbOp = new DbOp(context);
                dbOp.insertSessionId(dbOp.getSessionId(),"",mMode,dbOp.getlogintype(),dbOp.getDistance());
                dbOp.close();
                loadWS();
                Functions.showToast(context,strErrorMsg);
            } else {
                Functions.showToast(context,strErrorMsg);
            }
        }
    }


    //Webservice for Logout
    public class LogOutAsync extends AsyncTask<Void, Void, Void> {
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
                DbOp dbOp = new DbOp(context);
                mUserId = dbOp.getSessionId();
                dbOp.close();
                //http://demoforclients.net/funfo/index.php/users/userSignOut/?userId=203
                String url = getResources().getString(R.string.base_url) + "userSignOut/?userId=" + mUserId;
                Log.e("tag", "response " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                Log.e("tag", "response " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    strErrorMsg = new JSONObject(obj.getString("0")).getString("status");
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
                DbOp dbOp = new DbOp(getActivity());
                dbOp.insertSessionId("-1", "", "", "","");
                dbOp.close();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                ((Activity) context).finish();
                Functions.showToast(context,strErrorMsg);
            } else {
                Functions.showToast(context,strErrorMsg);
            }
        }
    }
}
