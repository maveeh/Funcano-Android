package com.app.funfoapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.activities.Create_New_Event;
import com.app.funfoapp.activities.Flyer_Detail;
import com.app.funfoapp.activities.LoginActivity;
import com.app.funfoapp.adapters.GetEvents_Adapter;
import com.app.funfoapp.adapters.GetMyFlyer_Adapter;
import com.app.funfoapp.adapters.No_Events_Adapter;
import com.app.funfoapp.adapters.RecyclerAdapter;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.swipemenulistview.SwipeMenu;
import com.app.funfoapp.swipemenulistview.SwipeMenuCreator;
import com.app.funfoapp.swipemenulistview.SwipeMenuItem;
import com.app.funfoapp.swipemenulistview.SwipeMenuListView;
import com.app.funfoapp.utility.DbOp;

import com.app.funfoapp.utility.GPSTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Pooja_Pollysys on 4/7/16.
 */
public class Flyers_Frag extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    View v;
    ListView lvEvents;
    Context context;
    public ArrayList<HashMap<String, ?>> arrEvents = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    GetMyFlyer_Adapter adapter;
    String type = "today";
    String mlonditude = "", mlatitude = "", mEventId = "";
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    int page_count = 0;
    GPSTracker gps;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(
                R.layout.my_flyer, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Profile_Frag.ProfileUpdate = true;
        allowPermission();
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
        lvEvents = (ListView) v.findViewById(R.id.lvEvents);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (arrEvents != null && !arrEvents.isEmpty()) {
                                            arrEvents.clear();
                                        } else {

                                        }
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

                //mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
                //mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
                //mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
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

        lvEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                // set title
                alertDialogBuilder.setTitle("Funcano");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Do you want to?")
                        .setCancelable(true)
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(arrEvents.get(position).get("eStatus").toString().equals("Expired")) {
                                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                                    // set title
                                    alertDialogBuilder.setTitle("Alert");
                                    // set dialog message
                                    alertDialogBuilder
                                            .setMessage("You are not able to edit Expired Flyer.")
                                            .setCancelable(true)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                                else {
                                    if (arrEvents != null && !arrEvents.isEmpty()) {
                                        Intent i = new Intent(context, Create_New_Event.class);
                                        i.putExtra("uType", arrEvents.get(position).get("uType").toString());
                                        i.putExtra("eImage", arrEvents.get(position).get("eImage").toString());
                                        i.putExtra("eId", arrEvents.get(position).get("eId").toString());
                                        i.putExtra("eName", arrEvents.get(position).get("eName").toString());
                                        i.putExtra("eLocation", arrEvents.get(position).get("eLocation").toString());
                                        i.putExtra("eZip", arrEvents.get(position).get("eZip").toString());
                                        i.putExtra("eStartDate", arrEvents.get(position).get("eStartDate").toString());
                                        i.putExtra("eStartTime", arrEvents.get(position).get("eStartTime").toString());
                                        i.putExtra("eEndDate", arrEvents.get(position).get("eEndDate").toString());
                                        i.putExtra("eEndTime", arrEvents.get(position).get("eEndTime").toString());
                                        i.putExtra("eSearchKeywords", arrEvents.get(position).get("eSearchKeywords").toString());
                                        i.putExtra("ecatName", arrEvents.get(position).get("ecatName").toString());
                                        i.putExtra("eCatId", arrEvents.get(position).get("eCatId").toString());
                                        i.putExtra("eDesc", arrEvents.get(position).get("eDesc").toString());
                                        i.putExtra("eMinCost", arrEvents.get(position).get("eMinCost").toString());
                                        i.putExtra("eMaxCost", arrEvents.get(position).get("eMaxCost").toString());
                                        i.putExtra("eLat", arrEvents.get(position).get("eLat").toString());
                                        i.putExtra("eLong", arrEvents.get(position).get("eLong").toString());
                                        i.putExtra("eUsername", arrEvents.get(position).get("eUsername").toString());
                                        startActivity(i);
                                    }
                                }

                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final int pos = position;
                                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                                // set title
                                alertDialogBuilder.setTitle("Delete");
                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("Are you sure you want to delete this flyer?")
                                        .setCancelable(true)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mEventId = arrEvents.get(pos).get("eId").toString();
                                                arrEvents.remove(pos);
                                                if (Validations.isNetworkAvailable(getActivity())) {
                                                    new DeleteEvents().execute();
                                                } else {
                                                    Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
                                                }

                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        });
                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

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
        if (arrEvents != null && !arrEvents.isEmpty()) {
            arrEvents.clear();
            if (adapter != null)
                adapter.notifyDataSetChanged();
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
            swipeRefreshLayout.setRefreshing(false);
            showAlertDialog();
        }

//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//
//            mlatitude = String.valueOf(mLastLocation.getLatitude());
//
//            mlonditude = String.valueOf(mLastLocation.getLongitude());
//            getEvents();
//        } else {
//            swipeRefreshLayout.setRefreshing(false);
//            showAlertDialog();
//            //Toast.makeText(context,"Enable location first", Toast.LENGTH_LONG).show();
//        }
    }

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
        dialog.show();

    }

    @Override
    public void onStart() {
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
            if(Home_Frag.isRefresh) {
                Home_Frag.isRefresh=false;
                displayLocation();
            }
        } else
            displayLocation();
    }

    @Override
    public void onPause() {
        mGoogleApiClient.connect();
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
        if (allowPermission()){
            displayLocation();
        }else{
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    //method to load webservice
    public void getEvents() {
        if (arrEvents != null && !arrEvents.isEmpty()) {
            arrEvents.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
        if (Validations.isNetworkAvailable(getActivity())) {
            new GetEvents().execute();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
        }
    }

    //Webservice to get All Events by Users
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
        protected Void doInBackground(Void... voids) {
            try {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                String strTime = sdf.format(c.getTime());
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                String strDate = sdf1.format(c.getTime());
                DbOp con = new DbOp(context);
                //http://demoforclients.net/funfo/index.php/users/getEventsByUser/?userId=151&curDate=2016-04-08&curTime=10:59:00%20AM
                String id = con.getSessionId();
                String url = getResources().getString(R.string.base_url) + "getEventsByUser/?curDate=" + URLEncoder.encode(strDate, "UTF-8") + "&curTime=" + URLEncoder.encode(strTime, "UTF-8") + "&type=" + URLEncoder.encode(type, "UTF-8") + "&uLat=" + mlatitude + "&uLong=" + mlonditude + "&userId=" + id + "&page_count=" + page_count;
                con.close();
                //Log.e("tag", "get event url " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success != 0) {

                    JSONArray events = obj.getJSONArray("events");
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
                        //hm.put("eStatus",jobj.getString("eStatus"));
                        hm.put("eStartTime", jobj.getString("eStartTime"));
                        hm.put("eEndTime", jobj.getString("eEndTime"));
                        hm.put("eName", jobj.getString("eName"));
                        hm.put("eLocation", jobj.getString("eLocation"));
                        hm.put("eSearchKeywords", jobj.getString("eSearchKeywords"));
                        hm.put("eImage", jobj.getString("eImage"));
                        hm.put("eMinCost", jobj.getString("eMinCost"));
                        hm.put("eMaxCost", jobj.getString("eMaxCost"));
                        hm.put("eStatus", jobj.getString("eStatus"));
                        hm.put("eLat", jobj.getString("eLat"));
                        hm.put("eLong", jobj.getString("eLong"));
                        hm.put("eDesc", jobj.getString("eDescription"));
                        hm.put("eUsername", jobj.getString("userName"));
                        hm.put("eUImage", jobj.getString("userImage"));
                        hm.put("uType", jobj.getString("uType"));
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
                adapter = new GetMyFlyer_Adapter(context, arrEvents, R.layout.my_flyer_view, new String[]{}, new int[]{});
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

    //webservice for delete events
    public class DeleteEvents extends AsyncTask<Void, Void, Void> {
        int success = 0;
        String userId;
        Dialog dl;
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
                DbOp con = new DbOp(context);
                userId = con.getSessionId();
                String url = getResources().getString(R.string.base_url) + "deleteEvent/?userId=" + userId + "&eId=" + mEventId;
                con.close();
                //Log.e("tag", "get event url " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                JSONObject obj = new JSONObject(res);
                //Log.e("tag", "response" + res);
                success = obj.getInt("success");
                if (success != 0) {
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
            if (success != 0) {
                Functions.showToast(context, strErrorMsg);
                adapter.notifyDataSetChanged();
            }
            if (arrEvents.size() == 0) {
                ArrayList<HashMap<String, Object>> arr = new ArrayList<>();
                HashMap<String, Object> hm = new HashMap<>();
                hm.put("key", "Tell everyone about an event by creating one in the Homepage.");
                arr.add(hm);
                No_Events_Adapter adapter1 = new No_Events_Adapter(context, arr, R.layout.placeholder_empty, new String[]{}, new int[]{});
                lvEvents.setAdapter(adapter1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayLocation();
                swipeRefreshLayout.setRefreshing(true);
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
}
