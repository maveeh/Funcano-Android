package com.app.funfoapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.activities.Flyer_Detail;
import com.app.funfoapp.adapters.GetEvents_Adapter;
import com.app.funfoapp.adapters.No_Events_Adapter;
import com.app.funfoapp.adapters.RecyclerAdapter;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;
import com.app.funfoapp.utility.GPSTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Pooja_Pollysys on 4/7/16.
 */
public class Home_Frag extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    View v;
    ListView lvEvents;
    Context context;
    public ArrayList<HashMap<String, ?>> arrEvents = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    GetEvents_Adapter adapter;
    TextView txtToday, txtThisWeek, txtNextWeek;
    RecyclerView recycler;
    String mlonditude = "", mlatitude = "", type = "today", SENDER_ID = "271653157938", mDeviceId = "", mOs = "Android", mUserId = "";
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    public static boolean isRefresh = false;
    GoogleCloudMessaging gcm;
    int updateDeviceId = 0;
    int page_count = 0;
    boolean bLoad = true;
    GPSTracker gps;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(
                R.layout.get_all_event, container, false);
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {

            if (permissions[0].equals("android.permission.ACCESS_FINE_LOCATION") && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayLocation();
                swipeRefreshLayout.setRefreshing(true);
            }
            if (permissions[0].equals("android.permission.WRITE_CALENDAR") && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    adapter.addEvent(context, adapter.listposition);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public boolean allowPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Profile_Frag.ProfileUpdate = true;
        allowPermission();
        gcm = GoogleCloudMessaging.getInstance(context);
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
        init();
        swipeRefreshLayout.setOnRefreshListener(this);

        if (type.equals("today")) {
            switchImages(1);
        } else if (type.equals("next week")) {
            switchImages(3);
        } else {
            switchImages(2);
        }
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e("tag", "type  " + type);
//                                        if (arrEvents != null && !arrEvents.isEmpty()) {
//                                            arrEvents.clear();
//                                        } else {
//
//                                        }
                                        checkPlayServices();
                                    }
                                }
        );

        final android.support.v7.widget.Toolbar mToolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);

        lvEvents.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrEvents != null && !arrEvents.isEmpty()) {
                    Intent i = new Intent(context, Flyer_Detail.class);
                    i.putExtra("eId", arrEvents.get(position).get("eId").toString());
                    i.putExtra("eDistance", arrEvents.get(position).get("eDistance").toString());
                    i.putExtra("eStartDate", arrEvents.get(position).get("eStartDate").toString());
                    i.putExtra("eEndDate", arrEvents.get(position).get("eEndDate").toString());
                    i.putExtra("eStartTime", arrEvents.get(position).get("eStartTime").toString());
                    i.putExtra("eEndTime", arrEvents.get(position).get("eEndTime").toString());
                    i.putExtra("eName", arrEvents.get(position).get("eName").toString());
                    i.putExtra("eLocation", arrEvents.get(position).get("eLocation").toString());
                    i.putExtra("eZip", arrEvents.get(position).get("eZip").toString());
                    i.putExtra("ecatName", arrEvents.get(position).get("ecatName").toString());
                    i.putExtra("eSearchKeywords", arrEvents.get(position).get("eSearchKeywords").toString());
                    i.putExtra("eImage", arrEvents.get(position).get("eImage").toString());
                    i.putExtra("eMinCost", arrEvents.get(position).get("eMinCost").toString());
                    i.putExtra("eMaxCost", arrEvents.get(position).get("eMaxCost").toString());
                    i.putExtra("eLat", arrEvents.get(position).get("eLat").toString());
                    i.putExtra("eLong", arrEvents.get(position).get("eLong").toString());
                    i.putExtra("eDesc", arrEvents.get(position).get("eDesc").toString());
                    i.putExtra("eUsername", arrEvents.get(position).get("eUsername").toString());
                    i.putExtra("eUImage", arrEvents.get(position).get("eUImage").toString());
                    i.putExtra("uType", arrEvents.get(position).get("uType").toString());
                    i.putExtra("uPhone", arrEvents.get(position).get("uPhone").toString());
                    i.putExtra("uTicketingUrl", arrEvents.get(position).get("uTicketingUrl").toString());
                    startActivity(i);
                }
            }
        });
    }

    //Initilization Ui Variables
    public void init() {
        lvEvents = (ListView) v.findViewById(R.id.lvEvents);
        recycler = (RecyclerView) v.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        txtToday = (TextView) v.findViewById(R.id.txt_today);
        txtThisWeek = (TextView) v.findViewById(R.id.txt_this_week);
        txtNextWeek = (TextView) v.findViewById(R.id.txt_next_week);
        addClickListener();
    }

    //add click listener
    public void addClickListener() {
        txtToday.setOnClickListener(this);
        txtThisWeek.setOnClickListener(this);
        txtNextWeek.setOnClickListener(this);
    }

    //webservice to registor device id
    class RegisterBackground extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... arg0) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                mDeviceId = gcm.register(SENDER_ID);
                sendRegistrationIdToBackend(mDeviceId);

            } catch (IOException ex) {

            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {

        }
    }

    private void sendRegistrationIdToBackend(String regid) {

        try {
            DbOp db = new DbOp(context);
            mUserId = db.getSessionId();
            db.close();
            String url = getResources().getString(R.string.base_url) + "updateDeviceId/?userId=" + mUserId + "&uDeviceId=" + regid + "&uOs=" + mOs + "&uLat=" + mlatitude + "&uLong=" + mlonditude;
            JSONParser parser = new JSONParser();
            String res = parser.getJSONFromUrl(url, "GET", null);
            JSONObject obj = new JSONObject(res);
            //Log.e("test", "obj " + url);
            if (obj.getInt("success") == 1) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
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
//        if (arrEvents != null && !arrEvents.isEmpty()) {
//            arrEvents.clear();
//            if (adapter != null)
//                adapter.notifyDataSetChanged();
//        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        gps = new GPSTracker(context);

        // check if GPS enabled
        if(gps.canGetLocation()){

            mlatitude = String.valueOf(gps.getLatitude());;
            mlonditude = String.valueOf(gps.getLongitude());

            setDeviceId();
            getEvents();

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            swipeRefreshLayout.setRefreshing(false);
            if (alert11 != null && alert11.isShowing()) {

            } else
                showAlertDialog();
        }

//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            mlatitude = String.valueOf(mLastLocation.getLatitude());
//            mlonditude = String.valueOf(mLastLocation.getLongitude());
//            setDeviceId();
//            getEvents();
//        } else {
//            swipeRefreshLayout.setRefreshing(false);
//            if (alert11 != null && alert11.isShowing()) {
//
//            } else
//                showAlertDialog();
//        }
    }

    AlertDialog alert11;

    //Alert View
    public void showAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        // set title
        dialog.setTitle("Alert...");
        // set dialog message
        dialog.setMessage("Please Enable Location Services.");
        dialog.setCancelable(true);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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

    @Override
    public void onStart() {
        if (type.equals("today")) {
            switchImages(1);
        } else if (type.equals("this week")) {
            switchImages(2);
        } else if (type.equals("next week")) {
            switchImages(3);
        }
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (arrEvents != null && !arrEvents.isEmpty()) {
            if (isRefresh) {
                isRefresh = false;
                displayLocation();
            }
        } else {
            displayLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRefresh() {
        if (allowPermission()) {

            if (bLoad)
                page_count++;

            displayLocation();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    public void setDeviceId() {
        if (updateDeviceId == 0) {
            updateDeviceId = 1;
            if (Validations.isNetworkAvailable((Activity) context)) {
                new RegisterBackground().execute();
            } else {
                Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
            }
        }
    }

    public void getEvents() {
//        if (arrEvents != null && !arrEvents.isEmpty()) {
//            arrEvents.clear();
//            if (adapter != null)
//                adapter.notifyDataSetChanged();
//        }
        if (Validations.isNetworkAvailable(getActivity())) {
            getAllEvents = new GetEvents();
            getAllEvents.execute();
        } else {
            swipeRefreshLayout.setRefreshing(false);

        }
    }

    int pos = 0;
    GetEvents getAllEvents = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_today:
                type = "today";
                if (pos == 1) {
                    if (arrEvents != null && !arrEvents.isEmpty()) {
                        arrEvents.clear();
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                    }
                } else {
                    if (getAllEvents != null) {
                        if (!getAllEvents.isCancelled()) {
                            getAllEvents.cancel(true);
                        }
                    }
                    pos = 1;
                    displayLocation();
                    switchImages(1);
                }
                break;
            case R.id.txt_this_week:
                type = "this week";
                if (pos == 2) {
                    if (arrEvents != null && !arrEvents.isEmpty()) {
                        arrEvents.clear();
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                    }
                } else {
                    if (getAllEvents != null) {
                        if (!getAllEvents.isCancelled()) {
                            getAllEvents.cancel(true);
                        }
                    }
                    pos = 2;
                    displayLocation();
                    switchImages(2);
                }
                break;
            case R.id.txt_next_week:
                type = "next week";
                if (pos == 3) {
                    if (arrEvents != null && !arrEvents.isEmpty()) {
                        arrEvents.clear();
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                    }
                } else {
                    if (getAllEvents != null) {
                        if (!getAllEvents.isCancelled()) {
                            getAllEvents.cancel(true);
                        }
                    }
                    pos = 3;
                    displayLocation();
                    switchImages(3);
                }
                break;
        }
    }

    public void switchImages(int pos) {
        switch (pos) {
            case 1:
                txtToday.setBackgroundColor(Color.parseColor("#fff838"));
                txtThisWeek.setBackgroundColor(0x00000000);
                txtNextWeek.setBackgroundColor(0x00000000);
                break;
            case 2:
                txtToday.setBackgroundColor(0x00000000);
                txtThisWeek.setBackgroundColor(Color.parseColor("#fff838"));
                txtNextWeek.setBackgroundColor(0x00000000);
                break;
            case 3:
                txtToday.setBackgroundColor(0x00000000);
                txtThisWeek.setBackgroundColor(0x00000000);
                txtNextWeek.setBackgroundColor(Color.parseColor("#fff838"));
                break;
        }
    }

    //webservice for get All Events
    public class GetEvents extends AsyncTask<Void, Void, Void> {
        int success = 0;
        String userId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
//            if (arrEvents != null && !arrEvents.isEmpty()) {
//                arrEvents.clear();
//                if (adapter != null)
//                    adapter.notifyDataSetChanged();
//            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String strTime = sdf.format(c.getTime());
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                String strDate = sdf1.format(c.getTime());
                DbOp con = new DbOp(context);
                String distance = con.getDistance();
                //http://demoforclients.net/funfo/index.php/users/getEventsByUser/?userId=151&curDate=2016-04-08&curTime=10:59:00%20AM
                String url = getResources().getString(R.string.base_url) + "getAllEvents/?userId=" + "&curDate=" + URLEncoder.encode(strDate, "UTF-8") + "&curTime=" + URLEncoder.encode(strTime, "UTF-8") + "&type=" + URLEncoder.encode(type, "UTF-8") + "&uLat=" + mlatitude + "&uLong=" + mlonditude + "&cId=&distance=10000&page_count=" + page_count;
                con.close();
                Log.e("tag", "get event url " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success != 0) {
                    JSONArray events = obj.getJSONArray("events");

                    if (events.length() < 3) {
                        bLoad = false;
                    }

                    for (int i = 0; i < events.length(); i++) {
                        JSONObject jobj = events.getJSONObject(i);
                        HashMap<String, Object> hm = new HashMap<>();
                        hm.put("eId", jobj.getString("eId"));
                        hm.put("eLocation", jobj.getString("eLocation"));
                        hm.put("eZip", jobj.getString("eZip"));
                        hm.put("eDistance", jobj.getString("eDistance"));
                        hm.put("eCatId", jobj.getString("eCatId"));
                        hm.put("ecatName", jobj.getString("catName"));
                        hm.put("eStartDate", jobj.getString("eStartDate"));
                        hm.put("eEndDate", jobj.getString("eEndDate"));
                        hm.put("eStartTime", jobj.getString("eStartTime"));
                        hm.put("eEndTime", jobj.getString("eEndTime"));
                        hm.put("eName", jobj.getString("eName"));
                        hm.put("eSearchKeywords", jobj.getString("eSearchKeywords"));
                        hm.put("eImage", jobj.getString("eImage"));
                        hm.put("eMinCost", jobj.getString("eMinCost"));
                        hm.put("eMaxCost", jobj.getString("eMaxCost"));
                        hm.put("eLat", jobj.getString("eLat"));
                        hm.put("eLong", jobj.getString("eLong"));
                        hm.put("eDesc", jobj.getString("eDescription"));
                        hm.put("eUsername", jobj.getString("userName"));
                        hm.put("uType", jobj.getString("uType"));
                        hm.put("eUImage", jobj.getString("userImage"));
                        hm.put("uPhone", jobj.getString("uPhone"));
                        hm.put("uTicketingUrl", jobj.getString("uTicketingUrl"));
                        arrEvents.add(hm);
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

            if (success != 0) {
                adapter = new GetEvents_Adapter(context, arrEvents, R.layout.get_event_design, new String[]{}, new int[]{});
                lvEvents.setAdapter(adapter);
            } else {
                ArrayList<HashMap<String, Object>> arr = new ArrayList<>();
                HashMap<String, Object> hm = new HashMap<>();
                hm.put("key", "Tell everyone about an event by creating one in the Homepage.");
                arr.add(hm);
                No_Events_Adapter adapter1 = new No_Events_Adapter(context, arr, R.layout.placeholder_empty, new String[]{}, new int[]{});
                lvEvents.setAdapter(adapter1);

            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
