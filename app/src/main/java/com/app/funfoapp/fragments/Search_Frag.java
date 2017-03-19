package com.app.funfoapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.activities.EventByCategory;
import com.app.funfoapp.activities.Flyer_Detail;
import com.app.funfoapp.adapters.GetCat_Adapter;
import com.app.funfoapp.adapters.GetEvents_Adapter;
import com.app.funfoapp.adapters.GetMyFlyer_Adapter;
import com.app.funfoapp.adapters.No_Events_Adapter;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;
import com.app.funfoapp.utility.GPSTracker;
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
public class Search_Frag extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    View v;
    Context context;
    public ArrayList<HashMap<String, ?>> arrEvents = new ArrayList<>();
    public ArrayList<HashMap<String, ?>> SearchEvent = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    ListView lvEvents, search_listview;
    GetCat_Adapter adapter;
    EditText auto_search;
    String mSearchWord = "", mlonditude = "", mlatitude = "";
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    GetEvents_Adapter adapter1;
    GPSTracker gps;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(
                R.layout.get_category, container, false);
        return v;
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Profile_Frag.ProfileUpdate = true;
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
        init(v);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        getEvents();
                                        checkPlayServices();
                                    }
                                }
        );

        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, EventByCategory.class);
                intent.putExtra("catId", arrEvents.get(position).get("catId").toString());
                intent.putExtra("catName", arrEvents.get(position).get("catName").toString());
                startActivity(intent);
            }
        });

        search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SearchEvent != null && !SearchEvent.isEmpty()) {
                    Intent i = new Intent(context, Flyer_Detail.class);
                    i.putExtra("eId", SearchEvent.get(position).get("eId").toString());
                    i.putExtra("eDistance", SearchEvent.get(position).get("eDistance").toString());
                    i.putExtra("eStartDate", SearchEvent.get(position).get("eStartDate").toString());
                    i.putExtra("eEndDate", SearchEvent.get(position).get("eEndDate").toString());
                    i.putExtra("eStartTime", SearchEvent.get(position).get("eStartTime").toString());
                    i.putExtra("eEndTime", SearchEvent.get(position).get("eEndTime").toString());
                    i.putExtra("eName", SearchEvent.get(position).get("eName").toString());
                    i.putExtra("eLocation", SearchEvent.get(position).get("eLocation").toString());
                    i.putExtra("ecatName", SearchEvent.get(position).get("ecatName").toString());
                    i.putExtra("eSearchKeywords", SearchEvent.get(position).get("eSearchKeywords").toString());
                    i.putExtra("eImage", SearchEvent.get(position).get("eImage").toString());
                    i.putExtra("eMinCost", SearchEvent.get(position).get("eMinCost").toString());
                    i.putExtra("eMaxCost", SearchEvent.get(position).get("eMaxCost").toString());
                    i.putExtra("eLat", SearchEvent.get(position).get("eLat").toString());
                    i.putExtra("eLong", SearchEvent.get(position).get("eLong").toString());
                    i.putExtra("eDesc", SearchEvent.get(position).get("eDesc").toString());
                    i.putExtra("eUsername", SearchEvent.get(position).get("eUsername").toString());
                    i.putExtra("eUImage", SearchEvent.get(position).get("eUImage").toString());
                    i.putExtra("eLocation", SearchEvent.get(position).get("eLocation").toString());
                    i.putExtra("eZip", SearchEvent.get(position).get("eZip").toString());
                    i.putExtra("uType", SearchEvent.get(position).get("uType").toString());
                    startActivity(i);
                }
            }
        });

        auto_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search_listview.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    mSearchWord = auto_search.getText().toString();
                    if (mSearchWord.equals("")) {
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                    } else if (mlatitude.equals("") && mlonditude.equals("")) {
                        showAlertDialog();
                    } else {
                        getSearchEvents();
                    }
                    return true;
                }
                return false;
            }
        });

        auto_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    if(SearchEvent!=null && !SearchEvent.isEmpty())
                        SearchEvent.clear();
                    search_listview.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    //Initilization Ui Variables
    public void init(View v) {
        auto_search = (AutoCompleteTextView) v.findViewById(R.id.auto_search);
        lvEvents = (ListView) v.findViewById(R.id.lvEvents);
        search_listview = (ListView) v.findViewById(R.id.search_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
    }

    @Override
    public void onRefresh() {
        displayLocation();
        getEvents();
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

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
        if(search_listview.getVisibility()!=View.VISIBLE){
            auto_search.setText("");
            if (SearchEvent != null && !SearchEvent.isEmpty())
                SearchEvent.clear();
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        gps = new GPSTracker(context);

        // check if GPS enabled
        if(gps.canGetLocation()){

            mlatitude = String.valueOf(gps.getLatitude());;
            mlonditude = String.valueOf(gps.getLongitude());

            if(Validations.isNetworkAvailable((Activity) context)) {

            }
            else{
                swipeRefreshLayout.setRefreshing(false);
                Functions.showToast(context, "Please check internet connection !!");
            }

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
////        Log.e("tag", "displayLocation" + mLastLocation);
//        if (mLastLocation != null) {
//            //Log.e("lat",String.valueOf(mLastLocation.getLatitude()));
//            mlatitude = String.valueOf(mLastLocation.getLatitude());
//            //Log.e("long", String.valueOf(mLastLocation.getLongitude()));
//            mlonditude = String.valueOf(mLastLocation.getLongitude());
//            //getCompleteAddressString(Double.parseDouble(mlatitude),Double.parseDouble(mlonditude));
//            if(Validations.isNetworkAvailable((Activity) context)) {
//
//            }
//            else{
//                swipeRefreshLayout.setRefreshing(false);
//                Functions.showToast(context, "Please check internet connection !!");
//            }
//
//        } else {
//            if (alert11 != null && alert11.isShowing()) {
//
//            } else
//                showAlertDialog();
//            //Toast.makeText(context,"Enable location first", Toast.LENGTH_LONG).show();
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

    public void getSearchEvents() {
        if (Validations.isNetworkAvailable(getActivity())) {
            new SearchEvents().execute();
        } else {
            Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
        }
    }

    public void getEvents() {
        if (arrEvents != null && !arrEvents.isEmpty()) {
            arrEvents.clear();
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
        if (Validations.isNetworkAvailable((Activity)context)) {
            new GetCategories().execute();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //webservice to get All Category
    class GetCategories extends AsyncTask<Void, Void, Void> {
        Dialog dl;
        int success = 0;

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
                if (arrEvents != null && arrEvents.size() > 0) {
                    arrEvents.clear();
                }
                String url = context.getResources().getString(R.string.base_url) + "getAllCategory";
                //Log.e("tag", "url " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1)
                    for (int i = 0; i < obj.length() - 1; i++) {
                        JSONObject job = obj.getJSONObject(String.valueOf(i));
                        HashMap<String, Object> hm = new HashMap<>();
                        hm.put("catName", job.getString("catName"));
                        hm.put("catId", job.getString("catId"));
                        hm.put("catImage", job.getString("catImage"));
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

            if (success != 0) {
                adapter = new GetCat_Adapter(context, arrEvents, R.layout.get_category_view, new String[]{}, new int[]{});
                lvEvents.setAdapter(adapter);
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    //Webservice to search from events
    class SearchEvents extends AsyncTask<Void, Void, Void> {
        Dialog dl;
        int success = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl = Functions.getProgressIndicator(context);
            dl.show();
        }

        //http://demoforclients.net/funfo/index.php/users/searchEvents/?sKeyword=new
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (SearchEvent != null && SearchEvent.size() > 0) {
                    SearchEvent.clear();
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String strTime = sdf.format(c.getTime());
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                String strDate = sdf1.format(c.getTime());
                DbOp dbOp = new DbOp(context);
                String distance = dbOp.getDistance();
                dbOp.close();
                String url = getResources().getString(R.string.base_url) + "searchEvents/?sKeyword=" + mSearchWord + "&eLat=" + mlatitude + "&eLong=" + mlonditude + "&curDate=" + URLEncoder.encode(strDate, "UTF-8") + "&curTime=" + URLEncoder.encode(strTime, "UTF-8") + "&distance=" + distance;
                //Log.e("tag", "url " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    JSONArray jsonArray = new JSONObject(obj.getString("0")).getJSONArray("matchedEvents");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject job = jsonArray.getJSONObject(i);
                        HashMap<String, Object> hm = new HashMap<>();
                        hm.put("eId", job.getString("id"));
                        hm.put("eName", job.getString("eName"));
                        hm.put("userId", job.getString("userId"));
                        hm.put("eUsername", job.getString("userName"));
                        hm.put("uType", job.getString("uType"));
                        hm.put("eUImage", job.getString("userImage"));
                        hm.put("eImage", job.getString("eImage"));
                        hm.put("eLocation", job.getString("eLocation"));
                        hm.put("eZip", job.getString("eZip"));
                        hm.put("eStartDate", job.getString("eStartDate"));
                        hm.put("eEndDate", job.getString("eEndDate"));
                        hm.put("eStartTime", job.getString("eStartTime"));
                        hm.put("eEndTime", job.getString("eEndTime"));
                        hm.put("eSearchKeywords", job.getString("eSearchWords"));
                        hm.put("eCatId", job.getString("eCatId"));
                        hm.put("ecatName", job.getString("CatName"));
                        hm.put("eDesc", job.getString("eDescription"));
                        hm.put("eMinCost", job.getString("eMinCost"));
                        hm.put("eMaxCost", job.getString("eMaxCost"));
                        hm.put("eLat", job.getString("eLat"));
                        hm.put("eLong", job.getString("eLong"));
                        hm.put("eStatus", job.getString("eStatus"));
                        hm.put("eDistance", job.getString("eDistance"));
                        SearchEvent.add(hm);
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
            search_listview.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            if (success != 0) {
                adapter1 = new GetEvents_Adapter(context, SearchEvent, R.layout.get_event_design, new String[]{}, new int[]{});
                search_listview.setAdapter(adapter1);
            } else {
                ArrayList<HashMap<String, Object>> arr = new ArrayList<>();
                HashMap<String, Object> hm = new HashMap<>();
                hm.put("key", "Tell everyone about an event by creating one in the Homepage.");
                arr.add(hm);
                No_Events_Adapter adapter1 = new No_Events_Adapter(context, arr, R.layout.placeholder_empty, new String[]{}, new int[]{});
                search_listview.setAdapter(adapter1);

            }

        }
    }
}
