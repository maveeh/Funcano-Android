package com.app.funfoapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.adapters.GetCat_Adapter;
import com.app.funfoapp.common.CircularImageView;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.fragments.Home_Frag;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;
import com.app.funfoapp.utility.GPSTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.PlusShare;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pooja_Pollysys on 4/21/16.
 */
public class Flyer_Detail extends ActionBarActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Context context = Flyer_Detail.this;
    TextView txtTitle, txtStartTime, txtStartDate, txtCost, txtDistance, txtDesc, txtShareIt, txtUName, txtComment, txtLocation, txtCatName;
    ImageView img_event, img_wa, img_fb, img_go, img_tw, img_share;
    RoundedImageView img_person;
    LinearLayout llRemindme, llComment, llShare;
    ProgressBar pb1;
    public ArrayList<HashMap<String, ?>> arrEvents = new ArrayList<>();
    public ArrayList<HashMap<String, ?>> eventDetails = new ArrayList<>();
    LinearLayout ll_event_back;
    Toolbar mToolbar;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String mlonditude = "", mlatitude = "";
    SupportMapFragment mapFragment;
    LinearLayout ll_main;
    String eventId = "";
    GPSTracker gps;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_edetail);
        eventId = getIntent().getStringExtra("eId");
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
        setToolbar();
        init();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //Set Toolbar
    public void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.event_toolbar);
        ll_event_back = (LinearLayout) mToolbar.findViewById(R.id.ll_event_back);
        ll_event_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Intialization Ui Variables
    public void init() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        ll_main = (LinearLayout) findViewById(R.id.ll_main);
        txtTitle = (TextView) findViewById(R.id.txtEventName);
        txtStartTime = (TextView) findViewById(R.id.txtStartTime);
        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        txtCost = (TextView) findViewById(R.id.txtCost);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtDesc = (TextView) findViewById(R.id.txtDesc);
        txtShareIt = (TextView) findViewById(R.id.txtShareIt);
        txtUName = (TextView) findViewById(R.id.txtUsername);
        txtComment = (TextView) findViewById(R.id.txtComment);

        llCmntContainer = (LinearLayout) findViewById(R.id.llCommentContainer);
        llRemindme = (LinearLayout) findViewById(R.id.llRemind);
        llComment = (LinearLayout) findViewById(R.id.llComment);
        llShare = (LinearLayout) findViewById(R.id.llShare);

        pb1 = (ProgressBar) findViewById(R.id.pb1);
        img_event = (ImageView) findViewById(R.id.img_event_main);
        img_person = (RoundedImageView) findViewById(R.id.img_person);
        img_wa = (ImageView) findViewById(R.id.img_wa);
        img_fb = (ImageView) findViewById(R.id.img_fb);
        img_go = (ImageView) findViewById(R.id.img_go);
        img_tw = (ImageView) findViewById(R.id.img_tw);
        img_share = (ImageView) findViewById(R.id.img_share);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtCatName = (TextView) findViewById(R.id.txtCatName);
        addClickListener();
        setFont();
    }

    //Set Font
    public void setFont() {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        txtTitle.setTypeface(face);
        txtShareIt.setTypeface(face);
        txtUName.setTypeface(face);
        txtComment.setTypeface(face);
        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        txtStartTime.setTypeface(face1);
        txtStartDate.setTypeface(face1);
        txtCost.setTypeface(face1);
        txtDistance.setTypeface(face1);
        txtDesc.setTypeface(face1);
        txtLocation.setTypeface(face1);
        txtCatName.setTypeface(face1);
    }

    //Add click listener
    public void addClickListener() {
        llRemindme.setOnClickListener(this);
        llComment.setOnClickListener(this);
        llShare.setOnClickListener(this);
        img_wa.setOnClickListener(this);
        img_fb.setOnClickListener(this);
        img_go.setOnClickListener(this);
        img_tw.setOnClickListener(this);
        img_share.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng loc = new LatLng(Double.parseDouble(eventDetails.get(0).get("eLat").toString()), Double.parseDouble(eventDetails.get(0).get("eLong").toString()));
        MarkerOptions marker = new MarkerOptions().position(loc);
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
        map.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Double.parseDouble(eventDetails.get(0).get("eLat").toString()), Double.parseDouble(eventDetails.get(0).get("eLong").toString()))).zoom(18).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    //Set Data
    public void setData() {
        if (eventDetails.get(0).get("eZip").toString().equals("")) {
            txtLocation.setText(eventDetails.get(0).get("eLocation").toString());
        } else {
            txtLocation.setText(eventDetails.get(0).get("eLocation").toString() + ", " + eventDetails.get(0).get("eZip").toString());
        }
        txtCatName.setText(eventDetails.get(0).get("ecatName").toString());
        txtTitle.setText(eventDetails.get(0).get("eName").toString());
        txtUName.setText(eventDetails.get(0).get("eUsername").toString());
        txtDesc.setText(eventDetails.get(0).get("eDesc").toString());
        if (eventDetails.get(0).get("eStartTime").toString().charAt(0) == '0') {
            if (eventDetails.get(0).get("eEndTime").toString().charAt(0) == '0') {
                txtStartTime.setText(eventDetails.get(0).get("eStartTime").toString().substring(1) + " - " + eventDetails.get(0).get("eEndTime").toString().substring(1));
            } else {
                txtStartTime.setText(eventDetails.get(0).get("eStartTime").toString().substring(1) + " - " + eventDetails.get(0).get("eEndTime").toString());
            }
        } else {
            if (eventDetails.get(0).get("eEndTime").toString().charAt(0) == '0') {
                txtStartTime.setText(eventDetails.get(0).get("eStartTime").toString() + " - " + eventDetails.get(0).get("eEndTime").toString().substring(1));
            } else {
                txtStartTime.setText(eventDetails.get(0).get("eStartTime").toString() + " - " + eventDetails.get(0).get("eEndTime").toString());
            }
        }

        txtStartDate.setText(eventDetails.get(0).get("eStartDate").toString());
        if (eventDetails.get(0).get("eMaxCost").toString().equals(""))
            txtCost.setText("£" + eventDetails.get(0).get("eMinCost").toString());
        else
            txtCost.setText("£" + eventDetails.get(0).get("eMinCost").toString() + " - " + "£" + eventDetails.get(0).get("eMaxCost").toString());
        if (eventDetails.get(0).get("eMinCost").toString().equals(""))
            txtCost.setText("Free");

        Float litersOfPetrol = Float.parseFloat(eventDetails.get(0).get("eDistance").toString());
        DecimalFormat df = new DecimalFormat("0");
        df.setMaximumFractionDigits(0);
        txtDistance.setText(df.format(litersOfPetrol) + "m");

        if (!eventDetails.get(0).get("eImage").toString().equals("0") && !eventDetails.get(0).get("eImage").toString().equals("")) {
            if (eventDetails.get(0).get("eImage").toString().contains("http"))
                Picasso.with(context)
                        .load(eventDetails.get(0).get("eImage").toString())
                        .into(img_event);
            else
                Picasso.with(context)
                        .load(context.getResources().getString(R.string.base_url_profile_image) + eventDetails.get(0).get("eImage").toString())
                        .into(img_event);
        }

        if (!eventDetails.get(0).get("eUImage").toString().equals("0") && !eventDetails.get(0).get("eUImage").toString().equals("")) {
            if (eventDetails.get(0).get("eUImage").toString().contains("http"))
                Picasso.with(context)
                        .load(eventDetails.get(0).get("eUImage").toString())
                        .into(img_person);
            else
                Picasso.with(context)
                        .load(context.getResources().getString(R.string.base_url_profile_image) + eventDetails.get(0).get("eUImage").toString())
                        .into(img_person);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return true;
    }

    LinearLayout llCmntContainer;
    CircularImageView img_user;
    TextView txtname, txtCmnt;
    ProgressBar pb;

    public void inflateView(String msg, String name, String image, final int position) {
        View v = getLayoutInflater().inflate(R.layout.comment_list_design, null);
        txtname = (TextView) v.findViewById(R.id.txt_name);
        txtCmnt = (TextView) v.findViewById(R.id.txt_cmnt);
        img_user = (CircularImageView) v.findViewById(R.id.img_user);
        pb = (ProgressBar) v.findViewById(R.id.pb1);

        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        txtname.setTypeface(face);

        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        txtCmnt.setTypeface(face1);
        if (image.contains("http")) {

        } else {
            image = getResources().getString(R.string.base_url_profile_image) + image;
        }
        if (!image.equals("0") && !image.equals("")) {
            Picasso.with(context)
                    .load(image)
                    .into(img_user);
        }
        txtname.setText(name);
        txtCmnt.setText(msg);
//        if(llCmntContainer.getChildCount()>0)
//            llCmntContainer.removeAllViews();
        llCmntContainer.addView(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for Other User profile
                Intent intent = new Intent(context, OtherProfile.class);
                intent.putExtra("userId", arrEvents.get(position).get("userId").toString());
                intent.putExtra("uType", arrEvents.get(position).get("uType").toString());
                startActivity(intent);
            }
        });
        v.setTag(position);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llComment:
                //show dialog
                detailDialog();
                break;

            case R.id.llEmail:
                Intent emailintent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", "info@loriumparam.com", null));
                emailintent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailintent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailintent, "Send email"));
                break;

            case R.id.llCall:
                if (!eventDetails.get(0).get("uPhone").toString().equals("0") || !eventDetails.get(0).get("uPhone").toString().equals("")) {
                    Uri number = Uri.parse("tel:" + eventDetails.get(0).get("uPhone").toString());
                    Intent numintent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(numintent);
                }
                break;

            case R.id.llWeb:
                if (!eventDetails.get(0).get("uTicketingUrl").toString().equals("NA")) {
                    Intent browseintent = new Intent(Intent.ACTION_VIEW);
                    browseintent.addCategory(Intent.CATEGORY_BROWSABLE);
                    browseintent.setData(Uri.parse("https://" + eventDetails.get(0).get("uTicketingUrl").toString()));
                    startActivity(browseintent);
                }
                break;

            case R.id.txtSend:
                if (Validations.isNetworkAvailable(this)) {
                    strCmnt = edtComment.getText().toString();
                    if (strCmnt.equals("")) {
                        Functions.showToast(this, "Write something to post.");
                    } else {
                        try {
                            new SendMsgs(URLEncoder.encode(strCmnt, "UTF-8")).execute();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
                }
                break;

            case R.id.img_wa:
                try {
                    PackageManager pm = getPackageManager();
                    PackageInfo info = pm.getPackageInfo("com.whatsapp",
                            PackageManager.GET_META_DATA);
                    String url4 = "";
                    if (!eventDetails.get(0).get("eImage").toString().contains("http:")) {
                        url4 = context.getResources().getString(R.string.base_url_profile_image);
                    }
                    new ShareImagesViaWhats(url4 + eventDetails.get(0).get("eImage").toString(), eventDetails.get(0).get("eName").toString()).execute();
                } catch (PackageManager.NameNotFoundException e) {
                    Functions.showToast(this, "You haven't installed Whats App on your device.");
                }
                break;
            case R.id.img_fb:
                shareDataToFB();
                break;
            case R.id.img_go:
                String url = "";
                if (!eventDetails.get(0).get("eImage").toString().contains("http:")) {
                    url = context.getResources().getString(R.string.base_url_profile_image);
                }
                new ShareImagesViaGmail(url + eventDetails.get(0).get("eImage").toString(), eventDetails.get(0).get("eName").toString()).execute();
                break;

            case R.id.img_tw:
                String url1 = "";
                if (!eventDetails.get(0).get("eImage").toString().contains("http:")) {
                    url1 = context.getResources().getString(R.string.base_url_profile_image);
                }
                new ShareImagesViaTweet(url1 + eventDetails.get(0).get("eImage").toString(), eventDetails.get(0).get("eName").toString()).execute();
                break;
            case R.id.img_share:
                String url2 = "";
                if (!eventDetails.get(0).get("eImage").toString().contains("http:")) {
                    url2 = context.getResources().getString(R.string.base_url_profile_image);
                }
                new ShareImages(url2 + eventDetails.get(0).get("eImage").toString(), eventDetails.get(0).get("eName").toString()).execute();
                break;
            case R.id.llShare:
                String url3 = "";
                if (!eventDetails.get(0).get("eImage").toString().contains("http:")) {
                    url3 = context.getResources().getString(R.string.base_url_profile_image);
                }
                new ShareImages(url3 + eventDetails.get(0).get("eImage").toString(), eventDetails.get(0).get("eName").toString()).execute();
                break;

            case R.id.llRemind:
                showAlert(context, "Add to Calendar");
                break;
            default:
                break;

        }
    }

    public void showAlert(final Context context, String title) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage("Add this flyer to your calendar?");
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            if (allowPermission())
                                addEvent(context);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder1.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    //without calendar open set Alarm
    public void addEvent(Context context) throws ParseException {
        final String appPackageName = getPackageName();
        Calendar calStartDate = null, calEndDate = null;
        SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("E d MMM yy h:mm a");
        String strStartTime = "", strEndTime = "";
        if (Functions.getTime().contains("a.m.") || Functions.getTime().contains("p.m.")) {
            String[] startdate = eventDetails.get(0).get("eStartTime").toString().split(" ");
            String[] enddate = eventDetails.get(0).get("eEndTime").toString().split(" ");
            if (eventDetails.get(0).get("eStartTime").toString().contains("am"))
                strStartTime = startdate[0] + " " + "a.m.";
            else
                strStartTime = startdate[0] + " " + "p.m.";
            if (eventDetails.get(0).get("eEndTime").toString().contains("am"))
                strEndTime = enddate[0] + " " + "a.m.";
            else
                strEndTime = enddate[0] + " " + "p.m.";
        } else {
            strStartTime = eventDetails.get(0).get("eStartTime").toString();
            strEndTime = eventDetails.get(0).get("eEndTime").toString();
        }
        Date StartDate = simpleDateTimeFormat.parse(eventDetails.get(0).get("eStartDate").toString() + " " + strStartTime);
        Date EndDate = simpleDateTimeFormat.parse(eventDetails.get(0).get("eEndDate").toString() + " " + strEndTime);
        calStartDate = Calendar.getInstance();
        calEndDate = Calendar.getInstance();
        calStartDate.setTime(StartDate);
        calEndDate.setTime(EndDate);
        try {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, calStartDate.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, calEndDate.getTimeInMillis());
            values.put(CalendarContract.Events.TITLE, eventDetails.get(0).get("eName").toString());
            values.put(CalendarContract.Events.DESCRIPTION, eventDetails.get(0).get("eName").toString() + " at " + eventDetails.get(0).get("eLocation").toString() + " on " +
                    eventDetails.get(0).get("eStartDate").toString() + " " + eventDetails.get(0).get("eStartTime").toString() + " brought to you by " + "https://play.google.com/store/apps/details?id=" + appPackageName);
            values.put(CalendarContract.Events.EVENT_LOCATION, eventDetails.get(0).get("eLocation").toString());
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // Save the eventId into the Task object for possible future delete.
            Long eventId = Long.parseLong(uri.getLastPathSegment());
            // Add a 5 minute, 1 hour and 1 day reminders (3 reminders)
            setReminder(cr, eventId, 0);
            setReminder(cr, eventId, 120);
            //Functions.showToast(context, "Alarm set successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReminder(ContentResolver cr, long eventID, int timeBefore) {
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
            Cursor c = CalendarContract.Reminders.query(cr, eventID,
                    new String[]{CalendarContract.Reminders.MINUTES});
            if (c.moveToFirst()) {
                System.out.println("calendar"
                        + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Flyer_Detail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (eventDetails != null && !eventDetails.isEmpty()) {

        } else
            displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void getEvents() {
        if (eventDetails != null && !eventDetails.isEmpty()) {
            eventDetails.clear();
        }
        if (arrEvents != null && arrEvents.size() > 0) {
            arrEvents.clear();
        }
        if (Validations.isNetworkAvailable(this)) {
            new GetCategories().execute();
        } else {
            Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context, 1000).show();
            } else {
                Functions.showToast(context, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private void displayLocation() {
        if (eventDetails != null && !eventDetails.isEmpty()) {
            eventDetails.clear();
        }
        if (arrEvents != null && arrEvents.size() > 0) {
            arrEvents.clear();
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        gps = new GPSTracker(context);

        // check if GPS enabled
        if(gps.canGetLocation()){

            mlatitude = String.valueOf(gps.getLatitude());;
            mlonditude = String.valueOf(gps.getLongitude());

            getEvents();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            if (alert11 != null && alert11.isShowing()) {

            } else
                showAlertDialog();
        }

//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            mlatitude = String.valueOf(mLastLocation.getLatitude());
//            mlonditude = String.valueOf(mLastLocation.getLongitude());
//            getEvents();
//        } else {
//            if (alert11 != null && alert11.isShowing()) {
//
//            } else
//                showAlertDialog();
//        }
    }

    AlertDialog alert11;

    public void showAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        // set title
        dialog.setTitle("Alert...");
        // set dialog message
        dialog.setMessage("Please Enable Location Services.");
        dialog.setCancelable(true);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent myIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(myIntent);
                mGoogleApiClient.connect();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create alert dialog
        alert11 = dialog.create();
        alert11.show();

    }

    private class ShareImages extends AsyncTask<Object, Object, Object> {
        private String requestUrl, imagename_;
        private Bitmap bitmap;
        InputStream stream = null;

        private ShareImages(String requestUrl, String _imagename_) {
            this.requestUrl = requestUrl;
            this.imagename_ = _imagename_;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            try {
                Log.e("tag", "requestUrl--" + requestUrl);
                URL url = new URL(requestUrl);
                URLConnection conn = url.openConnection();
                try {
                    HttpURLConnection httpConnection = (HttpURLConnection) conn;
                    httpConnection.setRequestMethod("GET");
                    httpConnection.connect();
                    if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        stream = httpConnection.getInputStream();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("downloadImage" + ex.toString());
                }
                bitmap = BitmapFactory.decodeStream(stream);
                return bitmap;
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            final String appPackageName = getPackageName();
            mBitmap = (Bitmap) o;
            Uri bmpUri = null;
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, eventDetails.get(0).get("eName").toString());
            shareIntent.putExtra(Intent.EXTRA_TEXT, eventDetails.get(0).get("eName").toString() + " at " + eventDetails.get(0).get("eLocation").toString() + " on " +
                    eventDetails.get(0).get("eStartDate").toString() + " " + eventDetails.get(0).get("eStartTime").toString() + "\nhttps://play.google.com/store/apps/details?id=" + appPackageName);
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share"));
        }
    }

    Bitmap mBitmap = null;

    public static File getImage(String imagename) {

        File mediaImage = null;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root);
            if (!myDir.exists())
                return null;

            mediaImage = new File(myDir.getPath() + "/.your_specific_directory/" + imagename);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mediaImage;
    }

    public static boolean checkifImageExists(String imagename) {
        Bitmap b = null;
        File file = getImage("/" + imagename + ".jpg");
        String path = file.getAbsolutePath();

        if (path != null)
            b = BitmapFactory.decodeFile(path);

        if (b == null || b.equals("")) {
            return false;
        }
        return true;
    }


    private class ShareImagesViaTweet extends AsyncTask<Object, Object, Object> {
        private String requestUrl, imagename_;
        private Bitmap bitmap;
        InputStream stream = null;

        private ShareImagesViaTweet(String requestUrl, String _imagename_) {
            this.requestUrl = requestUrl;
            this.imagename_ = _imagename_;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            try {

                URL url = new URL(requestUrl);
                URLConnection conn = url.openConnection();
                try {
                    HttpURLConnection httpConnection = (HttpURLConnection) conn;
                    httpConnection.setRequestMethod("GET");
                    httpConnection.connect();
                    if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        stream = httpConnection.getInputStream();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("downloadImage" + ex.toString());
                }
                bitmap = BitmapFactory.decodeStream(stream);
                return bitmap;
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            final String appPackageName = getPackageName();
            try {
                mBitmap = (Bitmap) o;
                Uri bmpUri = null;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, eventDetails.get(0).get("eName").toString());
                intent.putExtra(Intent.EXTRA_TEXT, eventDetails.get(0).get("eName").toString() + " at " + eventDetails.get(0).get("eLocation").toString() + " on " +
                        eventDetails.get(0).get("eStartDate").toString() + " " + eventDetails.get(0).get("eStartTime").toString() + "\nhttps://play.google.com/store/apps/details?id=" + appPackageName);
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.close();
                    bmpUri = Uri.fromFile(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                intent.setType("image/jpeg");
                intent.setPackage("com.twitter.android");
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Functions.showToast(context, "You haven't installed twitter on your device.");
            }
        }
    }

    private class ShareImagesViaGmail extends AsyncTask<Object, Object, Object> {
        private String requestUrl, imagename_;
        private Bitmap bitmap;
        InputStream stream = null;

        private ShareImagesViaGmail(String requestUrl, String _imagename_) {
            this.requestUrl = requestUrl;
            this.imagename_ = _imagename_;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            try {
                URL url = new URL(requestUrl);
                URLConnection conn = url.openConnection();
                try {
                    HttpURLConnection httpConnection = (HttpURLConnection) conn;
                    httpConnection.setRequestMethod("GET");
                    httpConnection.connect();
                    if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        stream = httpConnection.getInputStream();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("downloadImage" + ex.toString());
                }
                bitmap = BitmapFactory.decodeStream(stream);
                return bitmap;
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            final String appPackageName = getPackageName();
            try {
                mBitmap = (Bitmap) o;
                Uri bmpUri = null;
                PlusShare.Builder share = new PlusShare.Builder(context);
                share.setText(eventDetails.get(0).get("eName").toString() + " at " + eventDetails.get(0).get("eLocation").toString() + " on " +
                        eventDetails.get(0).get("eStartDate").toString() + " " + eventDetails.get(0).get("eStartTime").toString());
                share.setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.close();
                    bmpUri = Uri.fromFile(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                share.addStream(bmpUri);
                share.setType("image/*");
                startActivity(share.getIntent());
            } catch (ActivityNotFoundException e) {
                Functions.showToast(context, "You haven't installed google+ on your device.");
            }
        }
    }

    private class ShareImagesViaWhats extends AsyncTask<Object, Object, Object> {
        private String requestUrl, imagename_;
        private Bitmap bitmap;
        InputStream stream = null;

        private ShareImagesViaWhats(String requestUrl, String _imagename_) {
            this.requestUrl = requestUrl;
            this.imagename_ = _imagename_;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            try {
                URL url = new URL(requestUrl);
                URLConnection conn = url.openConnection();
                try {
                    HttpURLConnection httpConnection = (HttpURLConnection) conn;
                    httpConnection.setRequestMethod("GET");
                    httpConnection.connect();
                    if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        stream = httpConnection.getInputStream();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("downloadImage" + ex.toString());
                }
                bitmap = BitmapFactory.decodeStream(stream);
                return bitmap;
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            final String appPackageName = getPackageName();
            mBitmap = (Bitmap) o;
            Uri bmpUri = null;
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, eventDetails.get(0).get("eName").toString());
            shareIntent.putExtra(Intent.EXTRA_TEXT, eventDetails.get(0).get("eName").toString() + " at " + eventDetails.get(0).get("eLocation").toString() + " on " +
                    eventDetails.get(0).get("eStartDate").toString() + " " + eventDetails.get(0).get("eStartTime").toString() + "\nhttps://play.google.com/store/apps/details?id=" + appPackageName);

            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            shareIntent.setPackage("com.whatsapp");
            context.startActivity(Intent.createChooser(shareIntent, "Share"));
        }
    }

    public void shareDataToFB() {
        String url = "";
        if (!eventDetails.get(0).get("eImage").toString().contains("http"))
            url = getResources().getString(R.string.base_url_profile_image);
        final String appPackageName = getPackageName();
        FacebookSdk.sdkInitialize(context);
        CallbackManager callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog((Activity) context);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(eventDetails.get(0).get("eName").toString())
                    .setContentDescription(eventDetails.get(0).get("eName").toString() + " at " + eventDetails.get(0).get("eLocation").toString() + " on " +
                            eventDetails.get(0).get("eStartDate").toString() + " " + eventDetails.get(0).get("eStartTime").toString())
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)).setImageUrl(Uri.parse(url + eventDetails.get(0).get("eImage").toString()))
                    .build();
            shareDialog.show(linkContent);
        }
    }

    TextView txtHeader, txtHeader1, email, web, call, txtEmail, txtCall, txtWeb, txtSend;
    LinearLayout llEmail, llWEB, llCall, ll_call, ll_ticket;
    EditText edtComment;
    String strCmnt = "";
    Dialog pDialog;
    String uType = "";

    public void detailDialog() {
        pDialog = new Dialog(context,
                android.R.style.Theme_Holo_NoActionBar);
        pDialog.setContentView(R.layout.post_comment);
        pDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        txtHeader = (TextView) pDialog.findViewById(R.id.txtHeader);
        txtHeader1 = (TextView) pDialog.findViewById(R.id.txtHeader1);
        email = (TextView) pDialog.findViewById(R.id.email);
        web = (TextView) pDialog.findViewById(R.id.ticket);
        call = (TextView) pDialog.findViewById(R.id.call);
        txtEmail = (TextView) pDialog.findViewById(R.id.txtEmail);
        txtCall = (TextView) pDialog.findViewById(R.id.txtCall);
        txtWeb = (TextView) pDialog.findViewById(R.id.txtWeb);
        txtSend = (TextView) pDialog.findViewById(R.id.txtSend);
        edtComment = (EditText) pDialog.findViewById(R.id.edt_comment);
        llEmail = (LinearLayout) pDialog.findViewById(R.id.llEmail);
        llCall = (LinearLayout) pDialog.findViewById(R.id.llCall);
        llWEB = (LinearLayout) pDialog.findViewById(R.id.llWeb);
        ll_call = (LinearLayout) pDialog.findViewById(R.id.ll_call);
        ll_ticket = (LinearLayout) pDialog.findViewById(R.id.ll_ticket);
        if (eventDetails.get(0).get("uType").toString().equals("premium")) {
            ll_call.setVisibility(View.VISIBLE);
            ll_ticket.setVisibility(View.VISIBLE);
            if (eventDetails.get(0).get("uPhone").toString().equals("") || eventDetails.get(0).get("uPhone").toString().equals("0")) {
                txtCall.setText("NA");
            } else {
                txtCall.setText(eventDetails.get(0).get("uPhone").toString());
            }
            if (eventDetails.get(0).get("uTicketingUrl").toString().equals("")) {
                txtWeb.setText("NA");
            } else {
                txtWeb.setText(eventDetails.get(0).get("uTicketingUrl").toString());
            }
        } else {
            ll_call.setVisibility(View.GONE);
            ll_ticket.setVisibility(View.GONE);
        }
        llEmail.setOnClickListener(this);
        llCall.setOnClickListener(this);
        llWEB.setOnClickListener(this);
        txtSend.setOnClickListener(this);

        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        txtHeader.setTypeface(face);
        txtHeader1.setTypeface(face);

        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        email.setTypeface(face1);
        web.setTypeface(face1);
        call.setTypeface(face1);
        txtEmail.setTypeface(face1);
        txtCall.setTypeface(face1);
        txtWeb.setTypeface(face1);
        txtSend.setTypeface(face);

        pDialog.show();
    }

    //webservice for get Event detail
    class GetCategories extends AsyncTask<Void, Void, Void> {
        Dialog dl;
        int success = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl = Functions.getProgressIndicator(context);
            dl.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (arrEvents != null && arrEvents.size() > 0) {
                    arrEvents.clear();
                }
                DbOp con = new DbOp(context);
                String id = con.getSessionId();
                con.close();
                String url = getResources().getString(R.string.base_url) + "getEventDetails/?userId=" + id + "&eId=" + eventId + "&uLat=" + mlatitude + "&uLong=" + mlonditude;
                Log.e("tag", "url--- " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                Log.e("tag", "response--- " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    JSONObject jsonObject = obj.getJSONObject("0");
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("eId", jsonObject.getString("eId"));
                    hashMap.put("userId", jsonObject.getString("userId"));
                    hashMap.put("eUsername", jsonObject.getString("userName"));
                    hashMap.put("eUImage", jsonObject.getString("userImage"));
                    hashMap.put("uType", jsonObject.getString("uType"));
                    hashMap.put("uPhone", jsonObject.getString("uPhone"));
                    hashMap.put("uTicketingUrl", jsonObject.getString("uTicketingUrl"));
                    hashMap.put("eName", jsonObject.getString("eName"));
                    hashMap.put("eLocation", jsonObject.getString("eLocation"));
                    hashMap.put("eZip", jsonObject.getString("eZip"));
                    hashMap.put("eLat", jsonObject.getString("eLat"));
                    hashMap.put("eLong", jsonObject.getString("eLong"));
                    hashMap.put("eDesc", jsonObject.getString("eDescription"));
                    hashMap.put("eCatId", jsonObject.getString("eCatId"));
                    hashMap.put("ecatName", jsonObject.getString("catName"));
                    hashMap.put("eStartDate", jsonObject.getString("eStartDate"));
                    hashMap.put("eEndDate", jsonObject.getString("eEndDate"));
                    hashMap.put("eDistance", jsonObject.getString("eDistance"));
                    hashMap.put("eMinCost", jsonObject.getString("eMinCost"));
                    hashMap.put("eMaxCost", jsonObject.getString("eMaxCost"));
                    hashMap.put("eImage", jsonObject.getString("eImage"));
                    hashMap.put("eStartTime", jsonObject.getString("eStartTime"));
                    hashMap.put("eEndTime", jsonObject.getString("eEndTime"));
                    hashMap.put("eSearchKeywords", jsonObject.getString("eSearchKeywords"));
                    eventDetails.add(hashMap);
                    JSONArray array = obj.getJSONObject(String.valueOf(0)).getJSONArray("Comments");
                    if (array.length() > 0) {
                        success = 1;
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject job = array.getJSONObject(i);
                            HashMap<String, Object> hm = new HashMap<>();
                            hm.put("id", job.getString("id"));
                            hm.put("userId", job.getString("userId"));
                            hm.put("eId", job.getString("eId"));
                            hm.put("eComment", job.getString("eComment"));
                            hm.put("uName", job.getString("uFirstName") + " " + job.getString("uLastName"));
                            hm.put("imageName", job.getString("imageName"));
                            hm.put("uType", job.getString("uType"));
                            arrEvents.add(hm);
                        }
                    } else {
                        success = 0;
                    }
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
            mapFragment.getMapAsync((OnMapReadyCallback) context);

            if (eventDetails != null && !eventDetails.isEmpty()) {
                ll_main.setVisibility(View.VISIBLE);
                setData();
            } else {
                ll_main.setVisibility(View.GONE);
            }

            if (success != 0) {
                txtComment.setVisibility(View.VISIBLE);
                Collections.sort(arrEvents, new Comparator<HashMap<String, ?>>() {
                    @Override
                    public int compare(HashMap<String, ?> lhs, HashMap<String, ?> rhs) {
                        Double firstValue = Double.parseDouble(lhs.get("id").toString());
                        Double secondValue = Double.parseDouble(rhs.get("id").toString());
                        return secondValue.compareTo(firstValue);
                    }
                });

                for (int i = 0; i < arrEvents.size(); i++) {
                    if (i == 20)
                        break;
                    inflateView(arrEvents.get(i).get("eComment").toString(), arrEvents.get(i).get("uName").toString(), arrEvents.get(i).get("imageName").toString(), i);
                }
            } else {
                txtComment.setVisibility(View.GONE);
            }
        }
    }

    int success = 0;

    //webservice for send msg on particular event
    public class SendMsgs extends AsyncTask<Void, Void, Void> {
        Dialog dl;
        String strComment = "";

        public SendMsgs(String title) {
            this.strComment = title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl = Functions.getProgressIndicator(context);
            dl.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                DbOp con = new DbOp(context);
                String id = con.getSessionId();
                con.close();
                String url = getResources().getString(R.string.base_url) + "addComment/?userId=" + id + "&eId=" + eventId + "&eComment=" + strComment;
                Log.e("tag", "url " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    JSONObject job = obj.getJSONObject(String.valueOf(0));
                    HashMap<String, Object> hm = new HashMap<>();
                    hm.put("eComment", strCmnt);
                    hm.put("eId", eventId);
                    hm.put("id", String.valueOf(job.getInt("id")));
                    hm.put("uName", job.getString("userName"));
                    hm.put("imageName", job.getString("userImage"));
                    hm.put("userId", job.getString("userId"));
                    hm.put("uType", job.getString("uType"));
                    hm.put("eId", job.getString("eId"));
                    arrEvents.add(hm);
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
                pDialog.dismiss();
            }
            edtComment.setText("");
            txtComment.setVisibility(View.VISIBLE);

            if (llCmntContainer.getChildCount() > 0) {
                llCmntContainer.removeAllViews();
            }

            Collections.sort(arrEvents, new Comparator<HashMap<String, ?>>() {
                @Override
                public int compare(HashMap<String, ?> lhs, HashMap<String, ?> rhs) {
                    Double firstValue = Double.parseDouble(lhs.get("id").toString());
                    Double secondValue = Double.parseDouble(rhs.get("id").toString());
                    return secondValue.compareTo(firstValue);
                }
            });

            for (int i = 0; i < arrEvents.size(); i++) {
                if (i == 20)
                    break;
                inflateView(arrEvents.get(i).get("eComment").toString(), arrEvents.get(i).get("uName").toString(), arrEvents.get(i).get("imageName").toString(), i);
            }

        }
    }

    public boolean allowPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            if (permissions[0].equals("android.permission.WRITE_CALENDAR") && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    addEvent(context);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
