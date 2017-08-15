package com.app.funfoapp.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.funfoapp.R;
import com.app.funfoapp.adapters.GetPostcodes;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.PlaceAPI;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.fragments.Home_Frag;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DataBaseHelper;
import com.app.funfoapp.utility.DbOp;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Pooja_Pollysys on 5/13/16.
 */
public class Create_New_Event extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Context context = Create_New_Event.this;
    AutoCompleteTextView atvPlaces, edtPostCode;
    EditText edtEventName, edtEventLocation, edtDescription, edtEventSearchKeys, edtEventMinCost, edtEventMaxCost;
    CheckBox chkRepeatEvent, chkEventCost;
    Spinner spCat;
    TextView txtCreateEvent, txtRepeatType;
    public final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    private Calendar calendar;
    private int year, month, day;
    TextView btnEventStartDate, btnEventEndDate, btnEventStartTime, btnEventEndTime;
    ImageView imgSelEventImage, imgEventImage;
    LinearLayout llEndDate, llRepeat;
    ArrayAdapter<String> adapter;
    String strName = "", strSearchKeys = "", strLocation = "", strDescription = "", strMinCost = "", strMaxCost = "", strStartDate = "", strEndDate = "", strStartTime = "", strEndTime = "";
    String strCat = "Select Category", strCatId = "", strPostCode = "", strlat = "", strLong = "", uType = "";
    String arrCatName[], arrCatId[];
    byte[] ba1 = null;
    GetPostcodes homePageLocate;
    private ArrayList<String> codeList = new ArrayList<String>();
    private ArrayList<String> categoryList = new ArrayList<>();

    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("h:mm a");
    SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("E d MMM yy h:mm a");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E d MMM yy");
    String eImage = "", eId = "", eName = "", eLocation = "", eZip = "", eStartDate = "", eStartTime = "", eEndDate = "",
            eEndTime = "", ecatName = "", eSearchKeywords = "", eCatId = "", eDesc = "", eMinCost = "", eMaxCost = "",
            eLat = "", eLong = "", eUsername = "";

    Dialog dlRepeat;
    LinearLayout ll_newevent_back;
    Toolbar mToolbar;
    TextView txt_title;
    boolean checkTimeFormat = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        getDataFromIntent();
        Home_Frag.isRefresh = false;
        if(Functions.getTime().contains("a.m.") || Functions.getTime().contains("p.m.")) {
            checkTimeFormat = true;
        }
        else {
            checkTimeFormat = false;
        }
        setToolbar();
        init();
        loadData();
        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                strLocation = (String) parent.getItemAtPosition(position);
                new getPostCode(resultListPostCodes.get(position)).execute();

            }
        });

        atvPlaces.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strLocation = s.toString();
            }
        });
        try {
            DataBaseHelper db = new DataBaseHelper(context);
            categoryList = db.getPostCodes();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        homePageLocate = new GetPostcodes(this, android.R.layout.simple_list_item_1, categoryList);
//        edtPostCode.setAdapter(homePageLocate);
//        edtPostCode.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                strPostCode = s.toString();
//            }
//        });

        edtEventSearchKeys.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (isValidWord(s.toString().substring(s.length() - 1))) {
                        if (s.toString().length() == 1) {
                            edtEventSearchKeys.setText("~" + s.toString());
                            //setColor(edtEventSearchKeys,"~" + s.toString(),s.toString().substring(0, s.length() - 1),Color.YELLOW);
                            edtEventSearchKeys.setSelection(edtEventSearchKeys.length());
                        }
                    } else {
                        if (s.toString().length() == 1) {
                            edtEventSearchKeys.setText("");
                            edtEventSearchKeys.setSelection(edtEventSearchKeys.length());

                        } else if (s.toString().charAt(s.length() - 1) == ' ') {
                            boolean isFound = false;
                            List<String> strList = Arrays.asList(s.toString().split("~"));
                            if (strList.size() > 6) {
                                isFound = true;
                            } else {
                                isFound = false;
                            }
                            if (isFound) {
                                String ss = s.toString().substring(0, s.length() - 1) + "";
                                edtEventSearchKeys.setText(ss.toString());
                                edtEventSearchKeys.setSelection(edtEventSearchKeys.length());
                                Functions.showToast(context, "Sorry, you can only add 6 search words.");
                            } else {
                                if (s.toString().charAt(s.length() - 2) == '~') {
                                    String r = "";
                                    for (int i = 0; i < s.length() - 1; i++) {
                                        r = r + s.toString().charAt(i);
                                    }
                                    edtEventSearchKeys.setText(r, EditText.BufferType.SPANNABLE);
                                    edtEventSearchKeys.setSelection(s.length() - 1);
                                } else {
                                    String ss = s.toString().substring(0, s.length() - 1) + "~";
                                    edtEventSearchKeys.setText(ss.toString());
                                    edtEventSearchKeys.setSelection(edtEventSearchKeys.length());
                                }
                            }

                        } else {
                            if (!(s.toString().charAt(s.length() - 1) == ' ') && !(s.toString().charAt(s.length() - 1) == '~')) {
                                String ss = s.toString().substring(0, s.length() - 1);

                                edtEventSearchKeys.setText(ss);
                                edtEventSearchKeys.setSelection(edtEventSearchKeys.length());
                            }
                        }
                    }
                }
            }
        });

        edtEventSearchKeys.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && edtEventSearchKeys.getText().toString().equals("")) {
                } else {
                    if (!edtEventSearchKeys.getText().toString().equals("")) {
                        String ss = edtEventSearchKeys.getText().toString().substring(edtEventSearchKeys.getText().toString().length() - 1);
                        if (ss.equals("~")) {
                            String s = edtEventSearchKeys.getText().toString();
                            s = s.substring(0, s.length() - 1);
                            edtEventSearchKeys.setText(s);
                        } else {
                        }
                    }
                }
            }
        });

        chkEventCost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtEventMinCost.setBackgroundResource(R.drawable.disable_edittext);
                    edtEventMaxCost.setBackgroundResource(R.drawable.disable_edittext);
                    edtEventMaxCost.setText("");
                    edtEventMinCost.setText("");
                    edtEventMaxCost.setClickable(false);
                    edtEventMinCost.setClickable(false);
                    edtEventMaxCost.setFocusable(false);
                    edtEventMinCost.setFocusable(false);
                    edtEventMaxCost.setPadding(10, 0, 0, 0);
                    edtEventMinCost.setPadding(10, 0, 0, 0);
                    edtEventMinCost.setHintTextColor(Color.parseColor("#c4c3c3"));
                    edtEventMaxCost.setHintTextColor(Color.parseColor("#c4c3c3"));
                } else {
                    edtEventMaxCost.setClickable(true);
                    edtEventMinCost.setClickable(true);
                    edtEventMinCost.setFocusableInTouchMode(true);
                    edtEventMaxCost.setFocusableInTouchMode(true);
                    edtEventMinCost.setHintTextColor(Color.parseColor("#9e9e9e"));
                    edtEventMaxCost.setHintTextColor(Color.parseColor("#9e9e9e"));
                    edtEventMinCost.setBackgroundResource(R.drawable.create_event_input_bg);
                    edtEventMaxCost.setBackgroundResource(R.drawable.create_event_input_bg);
                    edtEventMaxCost.setPadding(10, 0, 0, 0);
                    edtEventMinCost.setPadding(10, 0, 0, 0);
                }
            }
        });

        if (Validations.isNetworkAvailable(Create_New_Event.this)) {
            new GetCategories().execute();
        } else {

        }

        spCat.setOnItemSelectedListener(this);
        spCat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (arrCatName != null && arrCatName.length > 1) {
                    } else {
                        if (Validations.isNetworkAvailable(Create_New_Event.this))
                            new GetCategories().execute();
                    }
                }
                return false;
            }
        });
        atvPlaces.setThreshold(1);
        if (strStartDate.equals("") && strEndDate.equals("")) {
            getCurrentDateTime("", "", "date");
        }
    }

    //set Toolbar
    public void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ll_newevent_back = (LinearLayout) mToolbar.findViewById(R.id.ll_newevent_back);
        txt_title = (TextView) mToolbar.findViewById(R.id.txt_title);
        ll_newevent_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Initilization Ui Variables
    public void init() {
        atvPlaces = (AutoCompleteTextView) findViewById(R.id.txtEventLoc);
        atvPlaces.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));

        edtEventName = (EditText) findViewById(R.id.edtEventName);
        edtEventLocation = (EditText) findViewById(R.id.edtEventLoc);
        btnEventStartDate = (TextView) findViewById(R.id.btnEventStartDate);
        btnEventEndDate = (TextView) findViewById(R.id.btnEventEndDate);
        btnEventStartTime = (TextView) findViewById(R.id.btnEventStartTime);
        btnEventEndTime = (TextView) findViewById(R.id.btnEventEndTime);
        edtDescription = (EditText) findViewById(R.id.edtEventDesc);
        edtEventSearchKeys = (EditText) findViewById(R.id.edtEventSearchKeys);
        edtEventMinCost = (EditText) findViewById(R.id.edtEventMinCost);
        edtEventMaxCost = (EditText) findViewById(R.id.edtEventMaxCost);
//        edtPostCode = (AutoCompleteTextView) findViewById(R.id.edtEventPostCode);
        imgEventImage = (ImageView) findViewById(R.id.img_event_main);
        imgSelEventImage = (ImageView) findViewById(R.id.img_sel_event);
        llEndDate = (LinearLayout) findViewById(R.id.ll_endDate);
        llRepeat = (LinearLayout) findViewById(R.id.ll_repeat);
        chkRepeatEvent = (CheckBox) findViewById(R.id.chkRepeat);
        chkEventCost = (CheckBox) findViewById(R.id.chkEventCost);
        txtCreateEvent = (TextView) findViewById(R.id.txtCreateEvent);
        txtRepeatType = (TextView) findViewById(R.id.txtRepeatType);
        spCat = (Spinner) findViewById(R.id.spinner_cat);
        addClickListener();
    }

    //Add click Listener
    public void addClickListener() {

        btnEventStartDate.setOnClickListener(this);
        btnEventEndDate.setOnClickListener(this);
        btnEventStartTime.setOnClickListener(this);
        btnEventEndTime.setOnClickListener(this);
        txtCreateEvent.setOnClickListener(this);
        imgSelEventImage.setOnClickListener(this);
        imgEventImage.setOnClickListener(this);
        llRepeat.setOnClickListener(this);
    }

    //Get Data from Intent
    public void getDataFromIntent() {
        uType = getIntent().getExtras().getString("uType");
        eImage = getIntent().getExtras().getString("eImage");
        eId = getIntent().getExtras().getString("eId");
        eName = getIntent().getExtras().getString("eName");
        eLocation = getIntent().getExtras().getString("eLocation");
        eZip = getIntent().getExtras().getString("eZip");
        eStartDate = getIntent().getExtras().getString("eStartDate");
        eStartTime = getIntent().getExtras().getString("eStartTime");
        eEndDate = getIntent().getExtras().getString("eEndDate");
        eEndTime = getIntent().getExtras().getString("eEndTime");
        ecatName = getIntent().getExtras().getString("ecatName");
        eSearchKeywords = getIntent().getExtras().getString("eSearchKeywords");
        eCatId = getIntent().getExtras().getString("eCatId");
        eDesc = getIntent().getExtras().getString("eDesc");
        eMinCost = getIntent().getExtras().getString("eMinCost");
        eMaxCost = getIntent().getExtras().getString("eMaxCost");
        eLat = getIntent().getExtras().getString("eLat");
        eLong = getIntent().getExtras().getString("eLong");
        eUsername = getIntent().getExtras().getString("eUsername");
    }

    //Load data when it comes from Edit Flyer
    public void loadData() {
        if (!eImage.equals("")) {
            txt_title.setText("Edit Flyer");
            txtCreateEvent.setText("Edit Flyer");
            Picasso.with(context)
                    .load(eImage)
                    .into(imgEventImage);
            if (eImage.contains("default_back"))
                imgSelEventImage.setVisibility(View.VISIBLE);
            else
                imgSelEventImage.setVisibility(View.GONE);

        }

        edtEventName.setText(eName);
        edtEventName.setSelection(eName.length());
//        edtPostCode.setText(eZip);
        atvPlaces.setText(eLocation);
        strStartDate = eStartDate;
        btnEventStartDate.setText(strStartDate);
        if(Functions.getTime().contains("a.m.") || Functions.getTime().contains("p.m.")) {
            checkTimeFormat = true;
        }
        else {
            checkTimeFormat = false;
        }
        if (!eStartTime.equals("") && !eEndTime.equals("")) {
            if (checkTimeFormat) {
                String[] startdate = eStartTime.split(" ");
                String[] enddate = eEndTime.split(" ");
                if (eStartTime.contains("am"))
                    strStartTime = startdate[0] + " " + "a.m.";
                else
                    strStartTime = startdate[0] + " " + "p.m.";
                if (eEndTime.contains("am"))
                    strEndTime = enddate[0] + " " + "a.m.";
                else
                    strEndTime = enddate[0] + " " + "p.m.";
            }
            else {
                strStartTime = eStartTime;
                strEndTime = eEndTime;
            }
            llRepeat.setVisibility(View.GONE);
        }
        else {
            llRepeat.setVisibility(View.VISIBLE);
            strStartTime = eStartTime;
            strEndTime = eEndTime;
        }
        btnEventStartTime.setText(strStartTime);
        strEndDate = eEndDate;
        btnEventEndDate.setText(strEndDate);
        btnEventEndTime.setText(strEndTime);
        edtEventSearchKeys.setText(eSearchKeywords);
        edtDescription.setText(eDesc);
        if (eMaxCost.equals("") && eMinCost.equals("")) {
            chkEventCost.setChecked(true);
            edtEventMinCost.setClickable(false);
            edtEventMaxCost.setClickable(false);
        } else {
            chkEventCost.setChecked(false);
            edtEventMinCost.setText(eMinCost);
            edtEventMaxCost.setText(eMaxCost);
            edtEventMinCost.setClickable(true);
            edtEventMaxCost.setClickable(true);
            edtEventMinCost.setFocusableInTouchMode(true);
            edtEventMaxCost.setFocusableInTouchMode(true);
            edtEventMinCost.setBackgroundResource(R.drawable.create_event_input_bg);
            edtEventMaxCost.setBackgroundResource(R.drawable.create_event_input_bg);
            edtEventMaxCost.setPadding(10, 0, 0, 0);
            edtEventMinCost.setPadding(10, 0, 0, 0);
        }
    }

    /*
    set start date and time to current date time
     */
    public void getCurrentDateTime(String startDate, String strTime, String checkBy) {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        if (strStartDate.equals(""))
            setStartDateTime(simpleDateFormat.format(calendar.getTime()), simpleTimeFormat.format(calendar.getTime()));
        else {
            if (checkBy.equals("date")) {
                if (calculateDaysDifference(simpleDateFormat.format(calendar.getTime()), startDate, simpleTimeFormat.format(calendar.getTime()), strTime, 0)) {
                    setStartDateTime(startDate, strStartTime);
                } else {
                    Functions.showToast(context, "Event Start Date cannot be before Today’s Date.");
                }
            } else if (checkBy.equals("enddate")) {
                if (calculateDaysDifference(strStartDate, startDate, strStartTime, strTime, 0)) {
                    if (strStartDate.equals(startDate)) {
                        int mins = calculateMinsDifference(strStartDate, strStartTime, strEndTime, startDate);
                        if (mins < 0) {
                            setEndDateTime(strStartDate, strStartTime, 60);
                        } else {
                            btnEventEndDate.setText(startDate);
                            strEndDate = startDate;
                        }

                    } else {
                        btnEventEndDate.setText(startDate);
                        strEndDate = startDate;
                    }
                } else {
                    Functions.showToast(context, "Event End Date must be after the Start Date.");
                }
            } else if (checkBy.equals("time")) {
                setStartDateTime(startDate, strTime);
            } else if (checkBy.equals("endtime")) {
                calculateTimeGapStartTimeEndTime(startDate, strEndDate, strStartTime, strTime, 0);
            } else {
                Functions.showToast(context, "Event Start Time must be in the future.");
            }
        }
    }

    /*
    set start date and time
     */

    public void setStartDateTime(String date, String time) {

        String oldStartTime = btnEventStartTime.getText().toString();
        String oldStartDate = btnEventStartDate.getText().toString();

        btnEventStartDate.setText(date);
        btnEventStartTime.setText(time);

        strStartDate = btnEventStartDate.getText().toString();

        strStartTime = btnEventStartTime.getText().toString();
        getMonthDay();
        if (strEndTime.equals(""))
            setEndDateTime(strStartDate, strStartTime, 60);
        else {
            setEndDateTime(strStartDate, strStartTime, calculateMinsDifference(oldStartDate, oldStartTime, strEndTime, strEndDate));
        }

    }

    /*
    set end date and time acc to start date and time
     */
    public void setEndDateTime(String startdate, String starttime, int addMin) {
        Date date = null;
        try {
            if (addMin >= 0) {
                date = simpleDateTimeFormat.parse(startdate + " " + starttime);
            } else {
                date = simpleDateTimeFormat.parse(strEndDate + " " + starttime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, addMin);

        btnEventEndDate.setText(simpleDateFormat.format(calendar.getTime()));
        btnEventEndTime.setText(simpleTimeFormat.format(calendar.getTime()));

        strEndDate = btnEventEndDate.getText().toString();
        strEndTime = btnEventEndTime.getText().toString();

    }

    /*
    calculate days difference
     */

    public boolean calculateDaysDifference(String strStartDate, String strEndDate, String strStartTime, String strEndTime, int difference) {
        Date currentDate = null, oldDate = null;
        try {
            currentDate = simpleDateFormat.parse(strStartDate);
            oldDate = simpleDateFormat.parse(strEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int diff =
                ((int) ((oldDate.getTime() / (24 * 60 * 60 * 1000))
                        - (int) (currentDate.getTime() / (24 * 60 * 60 * 1000))));
        if (diff < difference) {
            return false;
        } else {
            return true;
        }
    }

    /*
        validations for end time
     */

    public boolean calculateTimeGapStartTimeEndTime(String strStartDate, String strEnddate, String strStartTime, String strEndtime, int difference) {

        Date currentDate = null, oldDate = null;

        try {
            currentDate = simpleDateTimeFormat.parse(strStartDate + " " + strStartTime);
            oldDate = simpleDateTimeFormat.parse(strEnddate + " " + strEndtime);
//            currentDate = simpleTimeFormat.parse(strStartTime);
//            oldDate     = simpleTimeFormat.parse(strEndtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        now.setTime(currentDate);
        then.setTime(oldDate);
        // Get the represented date in milliseconds
        long nowMs = now.getTimeInMillis();
        long thenMs = then.getTimeInMillis();
        // Calculate difference in milliseconds
        long diff = thenMs - nowMs;


        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);

        if (diffHours <= 0) {
            if (diffMinutes < 0) {
                Functions.showToast(context, "Event End Time must be after the Start Time.");
                return false;
            } else {
                btnEventEndTime.setText(strEndtime);
                btnEventEndDate.setText(strEnddate);
                strEndDate = btnEventEndDate.getText().toString();
                strEndTime = btnEventEndTime.getText().toString();
                return true;
            }
        } else {
            btnEventEndTime.setText(strEndtime);
            btnEventEndDate.setText(strEnddate);
            strEndDate = btnEventEndDate.getText().toString();
            strEndTime = btnEventEndTime.getText().toString();
            return true;
        }
    }
/*
    calculate time gap for max 6 hours
     */

    public boolean calculateTimeDifference() {

        Date startDate = null;
        Date endDate = null;

        try {
            startDate = simpleDateTimeFormat.parse(strStartDate + " " + strStartTime);
            endDate = simpleDateTimeFormat.parse(strEndDate + " " + strEndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        now.setTime(startDate);
        then.setTime(endDate);
        // Get the represented date in milliseconds
        long nowMs = now.getTimeInMillis();
        long thenMs = then.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = thenMs - nowMs;

        // Calculate difference in minutes
        int diffMinutes = (int) diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);

        if (diffHours <= 0) {
            if (diffMinutes < 0) {
                Functions.showToast(context, "Event End Time must be after the Start Time.");
                return false;
            } else {
                return true;
            }
        } else if (diffHours > 0) {
            if (uType.equals("basic") || uType.equals("")) {
                if (diffHours > 6) {
                    Functions.showToast(context, "Sorry, your event can’t exceed 6 hours.");
                    btnEventEndDate.setError("");
                    btnEventEndTime.setError("");
                    return false;
                } else if (diffMinutes > 360) {
                    Functions.showToast(context, "Sorry, your event can’t exceed 6 hours.");
                    btnEventEndDate.setError("");
                    btnEventEndTime.setError("");
                    return false;
                } else {
                    btnEventEndDate.setError(null);
                    btnEventEndTime.setError(null);
                    return true;
                }
            } else {
                if (diffMinutes > 1440) {
                    Functions.showToast(context, "Sorry, your event can’t exceed 24 hours.");
                    btnEventEndDate.setError("");
                    btnEventEndTime.setError("");
                    return false;
                } else {
                    btnEventEndDate.setError(null);
                    btnEventEndTime.setError(null);
                    return true;
                }
            }
        } else {
            if (diffHours > -18) {
                Functions.showToast(context, "Sorry, your event can’t exceed 6 hours.");
                btnEventEndDate.setError("");
                btnEventEndTime.setError("");
                return false;
            } else if (diffMinutes > -1080) {
                Functions.showToast(context, "Sorry, your event can’t exceed 6 hours.");
                btnEventEndDate.setError("");
                btnEventEndTime.setError("");
                return false;
            } else {
                btnEventEndDate.setError(null);
                btnEventEndTime.setError(null);
                return true;
            }
        }
    }

    /*
    calculate time difference in minutes
     */

    public int calculateMinsDifference(String startdate, String strStartTime, String strEndTime, String strenddate) {

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = simpleDateTimeFormat.parse(startdate + " " + strStartTime);
            endDate = simpleDateTimeFormat.parse(strenddate + " " + strEndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        now.setTime(startDate);
        then.setTime(endDate);
        // Get the represented date in milliseconds
        long nowMs = now.getTimeInMillis();
        long thenMs = then.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = thenMs - nowMs;

        // Calculate difference in minutes
        int diffMinutes = (int) diff / (60 * 1000);
        return diffMinutes;

    }

    /*
        View to select the repetitions
     */
    TextView txtDone, txtDayType;
    RadioGroup rdGroup, rdGroup1;
    LinearLayout llBack;
    EditText edtRepeat;
    boolean isRepeating = false, isRepeat = false;
    String nday = "", nweek = "", eRepeatType = "daily", isMonthlyDayWise = "", repeatDays = "6";
    RelativeLayout rl;
    String strNday = "first", strNweek = "Thu", strRepeatType = "daily", strMonthType = "date";
    Dialog pDialog;

    public void repeatDialog() {
        pDialog = new Dialog(context,
                android.R.style.Theme_Holo_NoActionBar);
        pDialog.setContentView(R.layout.calendar_days_selection);
        pDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        Toolbar toolbar = (Toolbar) pDialog.findViewById(R.id.toolbar);
        llBack = (LinearLayout) toolbar.findViewById(R.id.llback);

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.dismiss();
            }
        });
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        txtDone = (TextView) pDialog.findViewById(R.id.txtDone);
        txtDayType = (TextView) pDialog.findViewById(R.id.txtDayType);
        rl = (RelativeLayout) pDialog.findViewById(R.id.rl_);
        rdGroup = (RadioGroup) pDialog.findViewById(R.id.radiogroup);
        rdGroup1 = (RadioGroup) pDialog.findViewById(R.id.radiogroup1);
        edtRepeat = (EditText) pDialog.findViewById(R.id.edtNoOfRepeat);
        ((TextView) pDialog.findViewById(R.id.txtRepeat)).setTypeface(face);
        ((TextView) pDialog.findViewById(R.id.txtDayType)).setTypeface(face);
        ((RadioButton) rdGroup1.findViewById(R.id.radioSameDate)).setTypeface(face);
        ((RadioButton) rdGroup1.findViewById(R.id.radioMonthDay)).setTypeface(face);
        ((RadioButton) rdGroup.findViewById(R.id.radioDaily)).setTypeface(face);
        ((RadioButton) rdGroup.findViewById(R.id.radioMonth)).setTypeface(face);
        ((RadioButton) rdGroup.findViewById(R.id.radioWeek)).setTypeface(face);
        ((RadioButton) rdGroup.findViewById(R.id.radioNever)).setTypeface(face);
        if (uType.equals("basic") || uType.equals("")) {
            edtRepeat.setMaxEms(1);
        } else {
            edtRepeat.setMaxEms(2);
        }
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRepeating = isRepeat;
                eRepeatType = strRepeatType;
                isMonthlyDayWise = strMonthType;
                nday = strNday;
                nweek = strNweek;
                if (!edtRepeat.getText().toString().equals(""))
                    repeatDays = edtRepeat.getText().toString();
                else {
                    if (uType.equals("basic") || uType.equals("")) {
                        repeatDays = "6";
                    } else {
                        repeatDays = "16";
                    }
                }
                if (isRepeating) {
                    //txtRepeatType.setVisibility(View.VISIBLE);
                    String ss = "";
                    if (isMonthlyDayWise.equals("day") && eRepeatType.equals("monthly")) {
                        ss = " (" + ((RadioButton) rdGroup1.findViewById(R.id.radioMonthDay)).getText().toString() + ")";
                    } else {
                        ss = "";
                    }
                    if (Integer.parseInt(repeatDays) > 1)
                        txtRepeatType.setText(convertFirstCharToUpeerCase(strRepeatType) + ss + " (" + repeatDays + " times)");
                    else
                        txtRepeatType.setText(convertFirstCharToUpeerCase(strRepeatType) + ss + " (" + repeatDays + " time)");
                    chkRepeatEvent.setChecked(true);
                } else {
                    //txtRepeatType.setVisibility(View.GONE);
                    txtRepeatType.setText("One-time event");
                    chkRepeatEvent.setChecked(false);
                }
                pDialog.dismiss();
            }
        });


        edtRepeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (uType.equals("basic") || uType.equals("")) {
                    if (isValidRepetitionForBasic(s.toString())) {
                        setTextViewForDays(txtDayType, s.toString());
                    } else {
                        edtRepeat.setText("");
                        Functions.showToast(context, "The Event can only be repeated upto 6 times.");
                    }
                } else {
                    if (!s.toString().equals("")) {
                        if (Integer.parseInt(s.toString()) <= 16) {
                            setTextViewForDays(txtDayType, s.toString());
                        } else {
                            edtRepeat.setText(s.toString().substring(0, s.length() - 1));
                            edtRepeat.setSelection(edtRepeat.length());
                            Functions.showToast(context, "The Event can only be repeated upto 16 times.");
                        }
                    }
                }
            }
        });

        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch (id) {
                    case R.id.radioNever:
                        strRepeatType = "daily";
                        isRepeat = false;
                        rdGroup1.setVisibility(View.GONE);
                        rl.setVisibility(View.GONE);
                        break;
                    case R.id.radioDaily:
                        strRepeatType = "daily";
                        isRepeat = true;
                        rl.setVisibility(View.VISIBLE);
                        rdGroup1.setVisibility(View.GONE);
                        txtDayType.setText("Day");
                        setTextViewForDays(txtDayType, edtRepeat.getText().toString());
                        break;
                    case R.id.radioWeek:
                        strRepeatType = "weekly";
                        isRepeat = true;
                        rl.setVisibility(View.VISIBLE);
                        rdGroup1.setVisibility(View.GONE);
                        txtDayType.setText("Week");
                        setTextViewForDays(txtDayType, edtRepeat.getText().toString());
                        break;
                    case R.id.radioMonth:
                        strRepeatType = "monthly";
                        isRepeat = true;
                        rl.setVisibility(View.VISIBLE);
                        rdGroup1.setVisibility(View.VISIBLE);
                        txtDayType.setText("Month");
                        setTextViewForDays(txtDayType, edtRepeat.getText().toString());
                        break;
                    default:
                        break;
                }
            }
        });

        rdGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch (id) {
                    case R.id.radioMonthDay:
                        strMonthType = "day";
                        break;
                    case R.id.radioSameDate:
                        strMonthType = "date";
                        break;
                    default:
                        break;
                }
            }
        });
        if (isRepeating) {
            edtRepeat.setText(repeatDays);
            edtRepeat.setSelection(edtRepeat.length());
            if (eRepeatType.equals("daily")) {
                ((RadioButton) rdGroup.findViewById(R.id.radioDaily)).setChecked(true);
                rdGroup.check(rdGroup.getCheckedRadioButtonId());
            } else if (eRepeatType.equals("weekly")) {
                ((RadioButton) rdGroup.findViewById(R.id.radioWeek)).setChecked(true);
                rdGroup.check(rdGroup.getCheckedRadioButtonId());
            } else if (eRepeatType.equals("monthly")) {
                ((RadioButton) rdGroup.findViewById(R.id.radioMonth)).setChecked(true);
                rdGroup.check(rdGroup.getCheckedRadioButtonId());
            }
        } else {
            ((RadioButton) rdGroup.getChildAt(0)).setChecked(true);
            rdGroup.check(rdGroup.getCheckedRadioButtonId());
            nday = "";
            nweek = "";
            eRepeatType = "";
            isMonthlyDayWise = "";
            if (uType.equals("basic") || uType.equals("")) {
                repeatDays = "6";
            } else {
                repeatDays = "16";
            }
            strNday = "first";
            strNweek = "Thursday";
            strRepeatType = "daily";
            strMonthType = "date";
            edtRepeat.setText(repeatDays);
            edtRepeat.setSelection(edtRepeat.length());
        }

        if (isMonthlyDayWise.equals("date") || isMonthlyDayWise.equals("")) {
            ((RadioButton) rdGroup1.findViewById(R.id.radioSameDate)).setChecked(true);
            rdGroup1.check(rdGroup1.getCheckedRadioButtonId());
        } else {
            ((RadioButton) rdGroup1.findViewById(R.id.radioMonthDay)).setChecked(true);
            rdGroup1.check(rdGroup1.getCheckedRadioButtonId());
        }
        Calendar cl = Calendar.getInstance();
        try {
            cl.setTime(simpleDateTimeFormat.parse(strStartDate + " " + strStartTime));
            //cl.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            int maxWeeknumber = cl.getActualMaximum(Calendar.WEEK_OF_MONTH);
            String[] ss = strStartDate.split(" ");
            String weekday = "";
            switch (ss[0]) {
                case "Mon":
                    weekday = "monday";
                    break;
                case "Tue":
                    weekday = "tuesday";
                    break;
                case "Wed":
                    weekday = "wednesday";
                    break;
                case "Thu":
                    weekday = "thursday";
                    break;
                case "Fri":
                    weekday = "friday";
                    break;
                case "Sat":
                    weekday = "saturday";
                    break;
                case "Sun":
                    weekday = "sunday";
                    break;
            }
            try {
                if (cl.get(Calendar.WEEK_OF_MONTH) < maxWeeknumber) {
                    String day = "";
                    switch (cl.get(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                        case 1:
                            day = "first";
                            break;
                        case 2:
                            day = "second";
                            break;
                        case 3:
                            day = "third";
                            break;
                        case 4:
                            day = "fourth";
                            break;
                        case 5:
                            day = "fifth";
                            break;
                        case 6:
                            day = "last";
                            break;
                    }

                    strNday = day;
                    strNweek = weekday;
                    ((RadioButton) rdGroup1.findViewById(R.id.radioMonthDay)).setText("Every " + day + " " + weekday);
                } else {
                    strNday = "last";
                    strNweek = weekday;
                    ((RadioButton) rdGroup1.findViewById(R.id.radioMonthDay)).setText("Every last " + weekday);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pDialog.show();
    }


    public void getMonthDay() {
        Calendar cl = Calendar.getInstance();
        try {
            cl.setTime(simpleDateTimeFormat.parse(strStartDate + " " + strStartTime));
            //cl.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            int maxWeeknumber = cl.getActualMaximum(Calendar.WEEK_OF_MONTH);
            String[] ss = strStartDate.split(" ");
            String weekday = "";
            String ss1 = "";
            switch (ss[0]) {
                case "Mon":
                    weekday = "monday";
                    break;
                case "Tue":
                    weekday = "tuesday";
                    break;
                case "Wed":
                    weekday = "wednesday";
                    break;
                case "Thu":
                    weekday = "thursday";
                    break;
                case "Fri":
                    weekday = "friday";
                    break;
                case "Sat":
                    weekday = "saturday";
                    break;
                case "Sun":
                    weekday = "sunday";
                    break;
            }
            try {
                if (cl.get(Calendar.WEEK_OF_MONTH) < maxWeeknumber) {
                    String day = "";
                    switch (cl.get(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                        case 1:
                            day = "first";
                            break;
                        case 2:
                            day = "second";
                            break;
                        case 3:
                            day = "third";
                            break;
                        case 4:
                            day = "fourth";
                            break;
                        case 5:
                            day = "fifth";
                            break;
                        case 6:
                            day = "last";
                            break;
                    }
                    nday = day;
                    nweek = weekday;
                    ss1 = " (Every " + day + " " + weekday + ") ";
                } else {
                    nday = "last";
                    nweek = weekday;
                    ss1 = " (Every last " + weekday + ") ";
                }
                if (isRepeating) {
                    if (isMonthlyDayWise.equals("day") && eRepeatType.equals("monthly")) {

                    } else {
                        ss1 = "";
                    }
                    if (Integer.parseInt(repeatDays) > 1)
                        txtRepeatType.setText(convertFirstCharToUpeerCase(strRepeatType) + ss1 + " (" + repeatDays + " times)");
                    else
                        txtRepeatType.setText(convertFirstCharToUpeerCase(strRepeatType) + ss1 + " (" + repeatDays + " time)");
                } else {
                    txtRepeatType.setText("One-time event");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String convertFirstCharToUpeerCase(String repeatType) {
        StringBuilder repeatTypeSb = new StringBuilder(repeatType.toLowerCase());
        repeatTypeSb.setCharAt(0, Character.toUpperCase(repeatTypeSb.charAt(0)));
        return repeatType = repeatTypeSb.toString();
    }

    public void setTextViewForDays(TextView txtDayType, String s) {
        if (!s.equals("")) {
            String ss = txtDayType.getText().toString().substring(txtDayType.getText().toString().length() - 1);
            if (Integer.parseInt(s.toString()) > 1 && !ss.equals("s")) {
                txtDayType.setText(txtDayType.getText().toString() + "s");
            } else if (Integer.parseInt(s.toString()) == 1) {
                if (ss.equals("s")) {
                    txtDayType.setText(txtDayType.getText().toString().substring(0, txtDayType.getText().toString().length() - 1));
                }
            }
        }
    }


    public boolean isValidWord(String w) {
        return w.matches("[A-Za-z]*");
    }

    public boolean isValidRepetitionForBasic(String w) {
        return w.matches("[1-6]*");
    }

    int btnPressed = 0, btndate = 0;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_repeat:
                repeatDialog();
                break;
            case R.id.btnEventStartDate:
                btndate = 0;
                showDatePicker();
                break;
            case R.id.btnEventEndDate:
                btndate = 1;
                showDatePicker();
                //setDate(v);
                break;
            case R.id.btnEventStartTime:
                btnPressed = 0;
                showTimePicker();
                //setDate(v);
                break;
            case R.id.btnEventEndTime:
                btnPressed = 1;
                showTimePicker();
                //setDate(v);
                break;

            case R.id.img_sel_event:
                if(checkAndRequestPermissions())
                chooseAttachment();
                break;
            case R.id.img_event_main:
                if(checkAndRequestPermissions())
                chooseAttachment();
                break;
            case R.id.txtCreateEvent:
                if (Validations.isNetworkAvailable(this)) {
                    strName = edtEventName.getText().toString();
                    strLocation = atvPlaces.getText().toString();
                    strDescription = edtDescription.getText().toString();
                    strSearchKeys = edtEventSearchKeys.getText().toString();
                    strStartDate = btnEventStartDate.getText().toString();
                    strEndDate = btnEventEndDate.getText().toString();
                    strStartTime = btnEventStartTime.getText().toString();
                    strEndTime = btnEventEndTime.getText().toString();
                    strMinCost = edtEventMinCost.getText().toString();
                    strMaxCost = edtEventMaxCost.getText().toString();
//                    strPostCode = edtPostCode.getText().toString();
                    if (strName.equals("")) {
                        Functions.showToast(context, "Please enter Flyer Name.");
                    } else if (strLocation.equals("")) {
                        Functions.showToast(context, "Please enter Location.");
                    } else if (strCat.equals("Select Category")) {
                        Functions.showToast(context, "Please select Category.");
                    } else if (strDescription.equals("") || strDescription.length() < 50 || strDescription.length() > 300) {
                        Functions.showToast(context, "Event description should contain more than 50 characters.");
                    } else if (!chkEventCost.isChecked() && strMinCost.equals("")) {
                        Functions.showToast(context, "Please enter Minimum Price or choose Free.");
                    } else if (!calculateTimeDifference()) {

                    } else if (!chkEventCost.isChecked() && !strMinCost.equals("") && !strMaxCost.equals("") && Float.parseFloat(strMinCost) >= Float.parseFloat(strMaxCost)) {
                        Functions.showToast(context, "The Minimum Price should be lower than the Maximum Price.");
                    } else {
                        if (chkEventCost.isChecked()) {
                            strMinCost = "";
                            strMaxCost = "";
                        } else {

                        }
                        new postDataClass().execute();
                    }
                } else {
                    Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
                }
                break;
        }

    }

    /*public boolean checkpostCode() {

        if (edtPostCode.isClickable()) {
            if (strPostCode.equals("")) {
                Functions.showToast(context, "Please select postcode.");
                return false;
            } else {
                boolean isValidPostCode = false;
                try {
                    DataBaseHelper db = new DataBaseHelper(context);
                    isValidPostCode = db.checkPostCodeAvailabiity(strPostCode.toUpperCase(Locale.getDefault()));
                    db.close();
                    if (!isValidPostCode) {
                        Functions.showToast(context, "Please select valid postcode.");
                        return false;
                    } else {
                        DataBaseHelper db1 = new DataBaseHelper(context);
                        strlat = db1.getPostCodeLat(strPostCode.toUpperCase(Locale.getDefault()));
                        strLong = db1.getPostCodeLng(strPostCode.toUpperCase(Locale.getDefault()));
                        db1.close();
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            strPostCode = "";
            strlat = "";
            strLong = "";
            return true;
        }
        return false;
    }*/

    public void updatePostCode(boolean isHavingResult) {
        if (isHavingResult) {
            disableAutoTextPostcode();
        } else {
            ableAutoTextPostcode();
        }
    }

    public void disableAutoTextPostcode() {
//        edtPostCode.setTextColor(Color.parseColor("#9e9e9e"));
//        edtPostCode.setFocusable(false);
//        edtPostCode.setClickable(false);
//        edtPostCode.setBackgroundResource(R.drawable.disable_edittext);
//        edtPostCode.setPadding(10, 0, 0, 0);
    }

    public void ableAutoTextPostcode() {
        if (!strLocation.equals("")) {
//            edtPostCode.setTextColor(Color.BLACK);
//            edtPostCode.setFocusableInTouchMode(true);
//            edtPostCode.setClickable(true);
//            edtPostCode.setBackgroundResource(R.drawable.create_event_input_bg);
//            edtPostCode.setPadding(10, 0, 0, 0);
        }
    }


    public void showDatePicker() {
        btnEventEndDate.setError(null);
        btnEventEndTime.setError(null);
        Calendar c = Calendar.getInstance();
        Date date;
        if (btndate == 0) {
            try {
                date = simpleDateFormat.parse(strStartDate);
                c.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                date = simpleDateFormat.parse(strEndDate);
                c.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myDateListener, year, month, day) {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        };
        dpd.show();
    }


    public void showTimePicker() {
        btnEventEndDate.setError(null);
        btnEventEndTime.setError(null);
        Calendar c = Calendar.getInstance();
        Date date = null;
        if (btnPressed == 0) {
            try {
                //date=simpleDateTimeFormat(strStartDate+" "+strStartTime);
                date = simpleTimeFormat.parse(strStartTime);
                c.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                date = simpleTimeFormat.parse(strEndTime);
                c.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, mTimeSetListener, hour, minute, false) {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        };
        tpd.show();
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, arg1);
            cal.set(Calendar.DAY_OF_MONTH, arg3);
            cal.set(Calendar.MONTH, arg2);
            String format = simpleDateFormat.format(cal.getTime());
            if (btndate == 0) {
                getCurrentDateTime(format, strStartTime, "date");
            } else {
                getCurrentDateTime(format, strEndTime, "enddate");
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay,
                                      int minute) {
                    Log.e("tag", "min " + hourOfDay + " " + minute);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(0, 0, 0, hourOfDay, minute, 0);
                    long timeInMillis = calendar.getTimeInMillis();
                    java.text.DateFormat dateFormatter = new SimpleDateFormat("h:mm a");
                    Date date = new Date();
                    date.setTime(timeInMillis);
                    if (btnPressed == 0) {
                        getCurrentDateTime(strStartDate, dateFormatter.format(date), "time");
                    } else {
                        getCurrentDateTime(strStartDate, dateFormatter.format(date), "endtime");
                    }
                }
            };


    public String removeLeadingZeroFromTime(int hourOfDay,int minute){
        String strTime="",strAM="am",strPM="pm";

        if (checkTimeFormat) {
            strAM="a.m.";
            strPM="p.m.";
        }

        if(hourOfDay>12) {
            strTime=String.valueOf(hourOfDay-12)+ ":"+(String.valueOf(minute)+" "+strPM);
        } else if(hourOfDay==12) {
            strTime="12"+ ":"+(String.valueOf(minute)+" "+strPM);
        } else if(hourOfDay<12) {
            if(hourOfDay!=0) {
                strTime=String.valueOf(hourOfDay) + ":" + (String.valueOf(minute) + " "+strAM);
            } else {
                strTime="12" + ":" + (String.valueOf(minute) + " "+strAM);
            }
        }

        return strTime;
    }

    Calendar now = Calendar.getInstance();
    Calendar then = Calendar.getInstance();

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //((TextView) parent.getChildAt(0)).setTextSize(16);
        if (arrCatName != null && arrCatName.length > 0) {
            strCat = arrCatName[position];
            strCatId = arrCatId[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //ALert for Choose Image from Gallery and Camera
    public void chooseAttachment() {
            Intent pickIntent = new Intent();
            pickIntent.setType("image/*");
            pickIntent.setAction(Intent.ACTION_PICK);
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String pickTitle = "Event picture";             // Or get from strings.xml
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]
                    {takePhotoIntent});
            startActivityForResult(chooserIntent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Bitmap image1 = Functions.getThumbnailBitmap(data.getData(), 600, context);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                image1.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                ba1 = bao.toByteArray();
                imgEventImage.setImageBitmap(image1);
                imgSelEventImage.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //webservices to get All Categories List
    class GetCategories extends AsyncTask<Void, Void, Void> {
        Dialog dl;
        int success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl = Functions.getProgressIndicator(context);
            dl.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (arrCatName != null && arrCatName.length > 0) {
                    arrCatName = null;
                    arrCatId = null;
                }
                String url = getResources().getString(R.string.base_url) + "getAllCategory";
                Log.e("tag", "url " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    boolean isCalled = false;
                    arrCatName = new String[obj.length() - 2];
                    arrCatId = new String[obj.length() - 2];
                    for (int i = 0; i < obj.length() - 1; i++) {
                        JSONObject job = obj.getJSONObject(String.valueOf(i));
                        String ss = job.getString("catName");
                        if (ss.equals("What I Like")) {
                            isCalled = true;
                            continue;
                        }
                        if (isCalled)
                            arrCatName[i - 1] = ss;
                        else {
                            arrCatName[i] = ss;
                        }
                    }
                    isCalled = false;
                    for (int i = 0; i < obj.length() - 1; i++) {
                        JSONObject job = obj.getJSONObject(String.valueOf(i));
                        String ss = job.getString("catId");
                        if (job.getString("catName").equals("What I Like")) {
                            isCalled = true;
                            continue;
                        }
                        if (isCalled)
                            arrCatId[i - 1] = ss;
                        else {
                            arrCatId[i] = ss;
                        }
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
            if (dl != null || dl.isShowing())
                dl.dismiss();
            if (success == 1) {
                if (eCatId.equals("")) {
                    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrCatName);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCat.setAdapter(adapter);
                } else {
                    for (int i = 0; i < arrCatId.length; i++) {
                        if (eCatId.equals(arrCatId[i])) {
                            catPos = i;
                            break;
                        }
                    }
                    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrCatName);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCat.setAdapter(adapter);
                    spCat.setSelection(catPos);
                }
            }
        }
    }

    int catPos;

    ArrayList<String> resultListPostCodes;

    public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

        ArrayList<String> resultList;
        JSONArray array;

        Context mContext;
        int mResource;

        PlaceAPI mPlaceAPI = new PlaceAPI();

        public PlacesAutoCompleteAdapter(Context context, int resource) {
            super(context, resource);

            mContext = context;
            mResource = resource;
        }

        @Override
        public int getCount() {
            // Last item will be the footer
            return resultList.size();
        }

        @Override
        public String getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        array = mPlaceAPI.autocomplete(constraint.toString());
                        resultList = new ArrayList<>();
                        resultListPostCodes = new ArrayList<>();
                        try {
                            String data = "";
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                JSONArray jsonArray = jsonObject.getJSONArray("terms");
                                //resultList.add(predsJsonArray.getJSONObject(i).getString("place_id"));
                                if (jsonArray.length() < 1) {
                                    for (int j = 0; j < jsonArray.length(); j++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                                        data = data + jsonObject1.getString("value");

                                    }
                                } else {
                                    for (int j = 0; j < jsonArray.length() - 1; j++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                                        if (j == jsonArray.length() - 2) {
                                            data = data + jsonObject1.getString("value");
                                        } else {
                                            data = data + jsonObject1.getString("value") + ", ";
                                        }
                                    }
                                }
                                resultListPostCodes.add(array.getJSONObject(i).getString("place_id"));
                                resultList.add(data);
                                data = "";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                        //updatePostCode(true);
                    } else {

                        notifyDataSetInvalidated();
                        // updatePostCode(false);
                    }
                }
            };
            return filter;
        }
    }

    //Webservices for creating Event
    public class postDataClass extends AsyncTask<Void, Void, Void> {

        Dialog dl;
        int id, success;
        String arrId = "";
        String msg = "", title = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dl = Functions.getProgressIndicator(context);
            dl.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String res = postData();
                Log.e("tag", "res " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    JSONObject obj1 = obj.getJSONObject("0");
                    for (int i = 0; i < obj1.getJSONArray("eId").length(); i++) {
                        if (i == 0) {
                            arrId = obj1.getJSONArray("eId").getInt(i) + "";
                        } else {
                            arrId = arrId + "," + obj1.getJSONArray("eId").getInt(i);
                        }
                    }
                } else {
                    JSONObject obj1 = obj.getJSONObject("0");
                    msg = obj1.getString("eMessage");
                    title = obj1.getString("eTitle");
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
                if (ba1 != null) {
                    new PostImage(arrId).execute();
                } else {
                    setDataEmpty();
                    Home_Frag.isRefresh = true;
                    if (eId.equals(""))
                        showAlert(context, "Success", "Great, you have created a flyer.");
                    else
                        showAlert(context, "Success", "Great, you have updated a flyer.");
                }
            } else {
                Functions.showAlert(context, title, msg);
            }

        }

    }

    //Method call to create event with POST Method
    public String postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(getResources().getString(R.string.base_url) + "createNewEvent");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(22);
            DbOp con = new DbOp(context);
            String id = con.getSessionId();
            con.close();
            Date date1 = null;
            String startdate = "", enddate = "";
            try {
                date1 = simpleDateFormat.parse(strStartDate);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                startdate = format.format(date1.getTime());
                date1 = simpleDateFormat.parse(strEndDate);
                enddate = format.format(date1.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String curDate = sdf1.format(c.getTime());

            nameValuePairs.add(new BasicNameValuePair("userId", id));
            nameValuePairs.add(new BasicNameValuePair("eventId", eId));
            nameValuePairs.add(new BasicNameValuePair("curDate", curDate));
            nameValuePairs.add(new BasicNameValuePair("eLocation", strLocation));
            nameValuePairs.add(new BasicNameValuePair("eStartDate", startdate));
            nameValuePairs.add(new BasicNameValuePair("eEndDate", enddate));
            nameValuePairs.add(new BasicNameValuePair("eStartTime", strStartTime));
            nameValuePairs.add(new BasicNameValuePair("eEndTime", strEndTime));
            nameValuePairs.add(new BasicNameValuePair("eSearchWords", strSearchKeys));
            nameValuePairs.add(new BasicNameValuePair("eDescription", strDescription));

            nameValuePairs.add(new BasicNameValuePair("type", eRepeatType));
            if (isRepeating) {
                nameValuePairs.add(new BasicNameValuePair("eRepeat", "1"));
                if (repeatDays.equals("")) {
                    if (uType.equals("basic") || uType.equals("")) {
                        nameValuePairs.add(new BasicNameValuePair("repeat", "6"));
                    } else {
                        nameValuePairs.add(new BasicNameValuePair("repeat", "16"));
                    }
                } else {
                    nameValuePairs.add(new BasicNameValuePair("repeat", repeatDays));
                }
            } else {
                nameValuePairs.add(new BasicNameValuePair("eRepeat", "0"));
                nameValuePairs.add(new BasicNameValuePair("repeat", "0"));
            }
            nameValuePairs.add(new BasicNameValuePair("repeattype", isMonthlyDayWise));
            nameValuePairs.add(new BasicNameValuePair("nday", nweek));
            nameValuePairs.add(new BasicNameValuePair("nweek", nday));

            nameValuePairs.add(new BasicNameValuePair("eFree", "0"));
            nameValuePairs.add(new BasicNameValuePair("eMinPrice", strMinCost));
            nameValuePairs.add(new BasicNameValuePair("eMaxPrice", strMaxCost));
            nameValuePairs.add(new BasicNameValuePair("eName", strName));
            nameValuePairs.add(new BasicNameValuePair("eCatId", strCatId));
            nameValuePairs.add(new BasicNameValuePair("eZip", ""));
            nameValuePairs.add(new BasicNameValuePair("eLat", strlat));
            nameValuePairs.add(new BasicNameValuePair("eLong", strLong));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            String responseText = null;
            try {
                responseText = EntityUtils.toString(response.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseText;

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return null;
    }

    //Webservices for Upload Image for Particular Event
    public class PostImage extends AsyncTask<Void, Void, Void> {
        Dialog dl;
        int success = 0;
        String strErrorMsg = "", eID = "";

        public PostImage(String id) {
            this.eID = id;
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
                String url = getResources().getString(R.string.base_url) + "createEventImage/?eId=" + eID;
                Log.e("tag", "url " + url);
                try {
                    MultipartEntity entity = new MultipartEntity(
                            HttpMultipartMode.BROWSER_COMPATIBLE);
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyyMMdd_HHmmss");
                    String currentDateandTime = sdf.format(new Date());
                    if (ba1 != null)
                        entity.addPart("image", new ByteArrayBody(ba1,
                                currentDateandTime + ".jpg"));
                    else {

                    }
                    httpPost.setEntity(entity);
                    HttpResponse response = client.execute(httpPost);
                    String s = EntityUtils.toString(response.getEntity());
                    JSONObject obj = new JSONObject(s);
                    success = obj.getInt("success");

                } catch (Exception e) {
                    e.printStackTrace();
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
                setDataEmpty();
                Home_Frag.isRefresh = true;
                if (eId.equals(""))
                    showAlert(context, "Success", "Great, you have created a flyer.");
                else
                    showAlert(context, "Success", "Great, you have updated a flyer.");
            } else {

            }
        }
    }

    //Clear data after creating event
    public void setDataEmpty() {
        ba1 = null;
        imgEventImage.setImageResource(R.drawable.default_back);
        imgSelEventImage.setVisibility(View.VISIBLE);
        edtEventName.setText("");
        atvPlaces.setText("");
//        edtPostCode.setText("");
        btnEventStartDate.setText("");
        btnEventEndDate.setText("");
        btnEventStartTime.setText("");
        btnEventEndTime.setText("");
        strStartTime = "";
        strEndTime = "";
        strStartDate = "";
        strEndDate = "";
        getCurrentDateTime("", "", "date");
        edtEventSearchKeys.setText("");
        edtDescription.setText("");
        chkEventCost.setChecked(true);
        edtEventMinCost.setClickable(false);
        edtEventMaxCost.setClickable(false);
        edtEventMinCost.setText("");
        edtEventMaxCost.setText("");
        txtRepeatType.setText("One-time event");
        chkRepeatEvent.setChecked(false);
        spCat.setSelection(0);
        isRepeat = false;
        isRepeating = false;
        nday = "";
        nweek = "";
        eRepeatType = "";
        isMonthlyDayWise = "";
        strNday = "first";
        strNweek = "Thursday";
        strRepeatType = "daily";
        strMonthType = "date";
    }

    private static final String TAG = PlaceAPI.class.getSimpleName();
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String OUT_JSON = "/json";
    private static final String DETAILS = "/details";
    private static final String API_KEY = "AIzaSyDSza4frfgn1UK0klIoPwsFZapqAoPzxe0";
    String place_id;

    //Webservices for get Postcode for particular address
    class getPostCode extends AsyncTask<Void, Void, String> {
        String placeid = "";
        String placeCode = "";

        public getPostCode(String placeid) {
            this.placeid = placeid;
        }

        @Override
        protected String doInBackground(Void... params) {
            strPostCode = getPostalCode(placeid);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            edtPostCode.setText(strPostCode);
        }
    }

    public String getPostalCode(String place_id) {
        HttpURLConnection conn1 = null;
        String postalCode = "";
        StringBuilder jsonResults1 = new StringBuilder();
        try {
            StringBuilder sb1 = new StringBuilder(PLACES_API_BASE + DETAILS + OUT_JSON);
            sb1.append("?placeid=" + place_id);
//            sb.append("&types=address");
            sb1.append("&key=" + API_KEY);

            URL url = new URL(sb1.toString());
            conn1 = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn1.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults1.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {

            return postalCode;
        } catch (IOException e) {

            return postalCode;
        } finally {
            if (conn1 != null) {
                conn1.disconnect();
            }
        }
        try {
            JSONArray addressComponents = new JSONObject(jsonResults1.toString()).getJSONObject("result").getJSONArray("address_components");

            for (int ii = 0; ii < addressComponents.length(); ii++) {
                JSONArray typesArray = addressComponents.getJSONObject(ii).getJSONArray("types");
                for (int j = 0; j < typesArray.length(); j++) {
                    if (typesArray.get(j).toString().equalsIgnoreCase("postal_code")) {
                        postalCode = addressComponents.getJSONObject(ii).getString("long_name");
                    }
                }
            }
            strlat = new JSONObject(jsonResults1.toString()).getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getString("lat");
            strLong = new JSONObject(jsonResults1.toString()).getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getString("lng");
        } catch (JSONException e) {
        }
        return postalCode;
    }

    public void showAlert(final Context context, String title, String msg) {
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

    //Runtime Permissions
    private boolean checkAndRequestPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    //Handle RunTime Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        chooseAttachment();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            showDialogOK("Write/Read and Camera Services Permissions are required for this functionality",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(this, "Please Enable permissions", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
}
