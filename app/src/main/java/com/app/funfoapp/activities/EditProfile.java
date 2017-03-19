package com.app.funfoapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.funfoapp.R;
import com.app.funfoapp.common.CircularImageView;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.fragments.Profile_Frag;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    Context context = EditProfile.this;
    Toolbar toolbar;
    LinearLayout ll_edit_back, ll_edit_contactname, ll_edit_website, ll_edit_ticketurl, ll_edit_phone;
    RoundedImageView img_premium_userimage;
    EditText edt_premium_lastname, edt_premium_email, edt_premium_location, edt_premium_aboutme,
            edt_premium_contactname, edt_premium_website, edt_premium_ticketurl,
            edt_premium_address, edt_premium_firstname, edt_premium_phone;
    TextView txtPremiumProfile, txt_premium_dob, txt_pre_search, txt_pre_dist, txt_pre_distmiles;
    Spinner spinner_premium_gender;
    String profilePic, mUserId = "", mFirstName = "", mLastName = "", mEmail = "", mAbout = "", mLocation = "", mDob = "",
            mGender = "", mAddress = "", mContactName = "", mWebsite = "", mTicketUrl = "", mPhone = "", mDistance = "", mEmail1 = "";
    int mProgress = 0;
    String[] gender = {"Male", "Female"};
    ArrayAdapter adapter;
    ImageView img_edt_userimage;
    private int year, month, day;
    byte[] ba1 = null;
    String uType = "", type = "", mChangeEmail = "";
    SeekBar pre_seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setToolbar();
        init();
        Profile_Frag.ProfileUpdate = false;
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, gender);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_premium_gender.setAdapter(adapter);
        spinner_premium_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

                                                         {
                                                             @Override
                                                             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                 mGender = gender[position];
                                                             }

                                                             @Override
                                                             public void onNothingSelected(AdapterView<?> parent) {

                                                             }
                                                         }

        );
        loadWS();
        DbOp dbOp = new DbOp(context);
        type = dbOp.getlogintype();
        dbOp.close();
    }

    //set Toolbar
    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.editPermium_toolbar);
        ll_edit_back = (LinearLayout) toolbar.findViewById(R.id.ll_edit_back);
        ll_edit_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Initilization Ui Variables
    public void init() {
        img_premium_userimage = (RoundedImageView) findViewById(R.id.img_premium_userimage);
        img_edt_userimage = (ImageView) findViewById(R.id.img_edt_userimage);
        edt_premium_firstname = (EditText) findViewById(R.id.edt_premium_firstname);
        edt_premium_lastname = (EditText) findViewById(R.id.edt_premium_lastname);
        edt_premium_email = (EditText) findViewById(R.id.edt_premium_email);
//        edt_premium_location = (EditText) findViewById(R.id.edt_premium_location);
        edt_premium_aboutme = (EditText) findViewById(R.id.edt_premium_aboutme);
//        txt_premium_dob = (TextView) findViewById(R.id.txt_premium_dob);
        edt_premium_contactname = (EditText) findViewById(R.id.edt_premium_contactname);
        edt_premium_website = (EditText) findViewById(R.id.edt_premium_website);
        edt_premium_ticketurl = (EditText) findViewById(R.id.edt_premium_ticketurl);
//        edt_premium_address = (EditText) findViewById(R.id.edt_premium_address);
        edt_premium_phone = (EditText) findViewById(R.id.edt_premium_phone);
        txtPremiumProfile = (TextView) findViewById(R.id.txtPremiumProfile);
        spinner_premium_gender = (Spinner) findViewById(R.id.spinner_premium_gender);
        ll_edit_contactname = (LinearLayout) findViewById(R.id.ll_edit_contactname);
        ll_edit_website = (LinearLayout) findViewById(R.id.ll_edit_website);
        ll_edit_ticketurl = (LinearLayout) findViewById(R.id.ll_edit_ticketurl);
        ll_edit_phone = (LinearLayout) findViewById(R.id.ll_edit_phone);
        txt_pre_search = (TextView) findViewById(R.id.txt_pre_search);
        txt_pre_dist = (TextView) findViewById(R.id.txt_pre_dist);
        txt_pre_distmiles = (TextView) findViewById(R.id.txt_pre_distmiles);
        pre_seekbar = (SeekBar) findViewById(R.id.pre_seekbar);
        addClickListener();
        setFont();
    }

    //Add click Listener
    public void addClickListener() {
        txtPremiumProfile.setOnClickListener(this);
//        txt_premium_dob.setOnClickListener(this);
        img_edt_userimage.setOnClickListener(this);
        img_premium_userimage.setOnClickListener(this);
    }

    //Set Font to TextView
    public void setFont() {
        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        edt_premium_firstname.setTypeface(face1);
        edt_premium_lastname.setTypeface(face1);
        edt_premium_email.setTypeface(face1);
//        edt_premium_location.setTypeface(face1);
        edt_premium_aboutme.setTypeface(face1);
//        txt_premium_dob.setTypeface(face1);
        edt_premium_contactname.setTypeface(face1);
        edt_premium_website.setTypeface(face1);
        edt_premium_ticketurl.setTypeface(face1);
//        edt_premium_address.setTypeface(face1);
        txtPremiumProfile.setTypeface(face1);
        txt_pre_search.setTypeface(face1);
        txt_pre_dist.setTypeface(face1);
        txt_pre_distmiles.setTypeface(face1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtPremiumProfile:
                if (edt_premium_firstname.getText().toString().equals(""))
                    Functions.showToast(context, "Please enter your First Name.");
                else if (edt_premium_lastname.getText().toString().equals(""))
                    Functions.showToast(context, "Please enter your Last Name.");
                else if (edt_premium_email.getText().toString().equals(""))
                    Functions.showToast(context, "Please enter Email.");
                else if (!isValidEmail(edt_premium_email.getText().toString()))
                    Functions.showToast(context, "Please enter a valid Email.");
//                else if (edt_premium_location.getText().toString().equals(""))
//                    Functions.showToast(context, "Please enter your location.");
                else if (edt_premium_aboutme.getText().toString().equals(""))
                    Functions.showToast(context, "Please enter something about yourself.");
                else {
                    if (mEmail1.equals(edt_premium_email.getText().toString())) {
                        mChangeEmail = "0";
                        mEmail = edt_premium_email.getText().toString();
                        loadEditWS();
                    } else {
                        showAlert(context, "Changing your email address will also change your Funcano email login.Are you sure you want to continue?");
                    }
                }
                break;

            case R.id.img_premium_userimage:
                if(checkAndRequestPermissions())
                chooseAttachment();
                break;

            case R.id.img_edt_userimage:
                if(checkAndRequestPermissions())
                chooseAttachment();
                break;

//            case R.id.txt_premium_dob:
//                setDate(v);
//                break;

            default:
                break;
        }
    }

    //method to load Webservice
    public void loadEditWS() {
        if (Validations.isNetworkAvailable((Activity) context)) {
            try {
                mFirstName = URLEncoder.encode(edt_premium_firstname.getText().toString(), "UTF-8");
                mLastName = URLEncoder.encode(edt_premium_lastname.getText().toString(), "UTF-8");
                mAbout = URLEncoder.encode(edt_premium_aboutme.getText().toString(), "UTF-8");
//                mLocation = URLEncoder.encode(edt_premium_location.getText().toString(), "UTF-8");
//                mDob = URLEncoder.encode(txt_premium_dob.getText().toString(), "UTF-8");
                mEmail = URLEncoder.encode(mEmail.toString(), "UTF-8");
                mGender = URLEncoder.encode(mGender.toString(), "UTF-8");
                mContactName = URLEncoder.encode(edt_premium_contactname.getText().toString(), "UTF-8");
                mWebsite = URLEncoder.encode(edt_premium_website.getText().toString(), "UTF-8");
                mTicketUrl = URLEncoder.encode(edt_premium_ticketurl.getText().toString(), "UTF-8");
//                mAddress = URLEncoder.encode(edt_premium_address.getText().toString(), "UTF-8");
                mPhone = URLEncoder.encode(edt_premium_phone.getText().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            new EditProfileAsync().execute();
        } else {
            Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
        }
    }

    //Alert View
    public void showAlert(final Context context, String msg) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(msg);
        builder1.setPositiveButton("Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        mChangeEmail = "1";
                        mEmail = edt_premium_email.getText().toString();
                        loadEditWS();

                    }
                });
        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mChangeEmail = "0";
                mEmail = mEmail1;
                loadEditWS();

            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    //Validation for email Address
    public boolean isValidEmail(String str_email) {
        if (str_email.equals("")) {
            return false;
        } else if (!Validations.isValidEmail(str_email)) {
            return false;
        } else {
            return true;
        }
    }

    //Alert to choose image from Gallery and Camera
    public void chooseAttachment() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_PICK);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String pickTitle = "Profile picture";             // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
        startActivityForResult(chooserIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            try {
                Uri photoUri = data.getData();
                performCrop(photoUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 2) {
            try {
                Bundle extras = data.getExtras();
                Bitmap image1 = extras.getParcelable("data");
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                image1.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                ba1 = bao.toByteArray();
                img_premium_userimage.setImageBitmap(image1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    final int CROP_PIC = 2;

    private void performCrop(Uri picUri) {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Functions.showToast(context, "This device doesn't support the crop action!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub

        DatePickerDialog dpd = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myDateListener, year, month, day) {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        };
        return dpd;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month - 1);
        String format = new SimpleDateFormat("d MMM,yyyy").format(cal.getTime());
//        if (getAge(year, month - 1, day))
//            txt_premium_dob.setText(format);
    }

    private boolean getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();


        if (ageInt >= 0 && ageInt < 10) {
            Functions.showToast(context, "To be eligible to sign up for Funcano, you must be at least 10 years old.");
            return false;
        } else if (ageInt < 0) {
            Functions.showToast(context, "Date of birth cannot after today.");
            return false;
        } else
            return true;

    }

    public void getCurrentDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("d MMM,yyyy");
        if (mDob.equals("")) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            //txt_premium_dob.setText(format.format(calendar.getTime()));
        } else {
            Calendar cal = Calendar.getInstance();
            Date EndDate = null;
            try {
                EndDate = format.parse(mDob);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cal.setTime(EndDate);
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        }
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    public void loadWS() {
        if (Validations.isNetworkAvailable((Activity) context)) {
            new ProfileAsync().execute();
        } else {
            Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
        }
    }

    //Webservice to update the profile details
    public class EditProfileAsync extends AsyncTask<Void, Void, Void> {
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
                String url = getResources().getString(R.string.base_url) + "editProfile/?userId=" + mUserId + "&uFirstName=" + mFirstName + "&uLastName=" + mLastName +
                        "&uEmail=" + mEmail + "&uAboutUs=" + mAbout + "&uDob=" + mDob + "&uGender=" + mGender + "&uAddress=" + mAddress + "&uContactName=" + mContactName +
                        "&uWebsite=" + mWebsite + "&uTicketingUrl=" + mTicketUrl + "&uPhone=" + mPhone + "&uLocation=" + mLocation + "&distance=" + mDistance + "&changeEmail=" + mChangeEmail;
                Log.e("tag", "response " + url);
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
                String res = EntityUtils.toString(response.getEntity());
                Log.e("tag", "response " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    strErrorMsg = new JSONObject(obj.getString("0")).getString("status");
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
                DbOp dbOp = new DbOp(context);
                dbOp.insertSessionId(dbOp.getSessionId(),"",dbOp.getRole(),dbOp.getlogintype(),mDistance);
                dbOp.close();
                mEmail1 = mEmail;
                Profile_Frag.ProfileUpdate = true;
                finish();
                Functions.showToast(context, strErrorMsg);
            } else {
                Functions.showToast(context, strErrorMsg);
            }
        }
    }

    //Webservice to get the profile details
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
                    mEmail = new JSONObject(obj.getString("0")).getString("email");
                    mGender = new JSONObject(obj.getString("0")).getString("uGender");
                    mAbout = new JSONObject(obj.getString("0")).getString("uAboutUs");
                    mContactName = new JSONObject(obj.getString("0")).getString("uContactName");
                    uType = new JSONObject(obj.getString("0")).getString("uType");
                    mWebsite = new JSONObject(obj.getString("0")).getString("uWebsite");
                    mAddress = new JSONObject(obj.getString("0")).getString("uAddress");
                    mDob = new JSONObject(obj.getString("0")).getString("uDob");
                    mTicketUrl = new JSONObject(obj.getString("0")).getString("uTicketingUrl");
                    profilePic = new JSONObject(obj.getString("0")).getString("userImage");
                    mPhone = new JSONObject(obj.getString("0")).getString("phone");
                    mLocation = new JSONObject(obj.getString("0")).getString("uLocation");
                    mDistance = new JSONObject(obj.getString("0")).getString("uDistance");
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
                mEmail1 = mEmail;
                mProgress = Integer.parseInt(mDistance);
                if (uType.equals("basic") || uType.equals("")) {
                    ll_edit_contactname.setVisibility(View.GONE);
                    ll_edit_website.setVisibility(View.GONE);
                    ll_edit_ticketurl.setVisibility(View.GONE);
                    ll_edit_phone.setVisibility(View.GONE);
                } else {
                    ll_edit_contactname.setVisibility(View.VISIBLE);
                    ll_edit_website.setVisibility(View.VISIBLE);
                    ll_edit_ticketurl.setVisibility(View.VISIBLE);
                    ll_edit_phone.setVisibility(View.VISIBLE);
                }
                if (profilePic.contains("http")) {
                    Picasso.with(context)
                            .load(profilePic)
                            .into(img_premium_userimage);
                } else {
                    Picasso.with(context)
                            .load(getResources().getString(R.string.base_url_profile_image) + profilePic)
                            .into(img_premium_userimage);
                }
                edt_premium_phone.setText(mPhone);
                edt_premium_firstname.setText(mFirstName);
                edt_premium_lastname.setText(mLastName);
//                edt_premium_address.setText(mAddress);
//                edt_premium_location.setText(mLocation);
                edt_premium_email.setText(mEmail);
                edt_premium_contactname.setText(mContactName);
                edt_premium_website.setText(mWebsite);
                edt_premium_ticketurl.setText(mTicketUrl);
//                txt_premium_dob.setText(mDob);
                edt_premium_aboutme.setText(mAbout);
                if (mGender.equals("Male") || mGender.equals("")) {
                    spinner_premium_gender.setSelection(0);
                    mGender = "Male";
                } else {
                    spinner_premium_gender.setSelection(1);
                    mGender = "Female";
                }

                if (uType.equals("basic"))
                    pre_seekbar.setMax(60);
                else
                    pre_seekbar.setMax(100);

                pre_seekbar.setProgress(mProgress);
                txt_pre_dist.setText(mDistance);
                pre_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mProgress = progress;
                        if (progress < 1) {
                /* if seek bar value is lesser than min value then set min value to seek bar */
                            seekBar.setProgress(1);
                        } else {
                            txt_pre_dist.setText(String.valueOf(progress));
                        }

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mDistance = String.valueOf(mProgress);
                        //txt_pre_dist.setText(String.valueOf(mProgress));
                    }
                });

            } else {

            }
            getCurrentDateTime();
        }
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

    public final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

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
