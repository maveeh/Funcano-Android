package com.app.funfoapp.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.app.funfoapp.R;
import com.app.funfoapp.common.Functions;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Pooja_Pollysys on 4/7/16.
 */
public class GetEvents_Adapter extends SimpleAdapter implements View.OnClickListener {
    Context context;
    ArrayList<HashMap<String, ?>> arrJobs = new ArrayList<>();

    public GetEvents_Adapter(Context context, ArrayList<HashMap<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.arrJobs = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.get_event_design, null, false);
            ViewHolder holder = new ViewHolder();
            initAll(holder, v);
            v.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) v.getTag();
        fillAll(holder, position);
        return v;
    }

    public void initAll(final ViewHolder holder, View v) {

        holder.txtTitle = (TextView) v.findViewById(R.id.txtEventName);
        holder.txtLocation = (TextView) v.findViewById(R.id.txtLocation);
        holder.txtStartTime = (TextView) v.findViewById(R.id.txtStartTime);
        holder.txtStartDate = (TextView) v.findViewById(R.id.txtStartDate);
        holder.txtCost = (TextView) v.findViewById(R.id.txtCost);
        holder.txtDistance = (TextView) v.findViewById(R.id.txtDistance);
        holder.pb1 = (ProgressBar) v.findViewById(R.id.pb1);
        holder.img_event = (ImageView) v.findViewById(R.id.img_event);
        holder.llRemindme = (LinearLayout) v.findViewById(R.id.llReminde);
        holder.llFbPost = (LinearLayout) v.findViewById(R.id.llFbPost);
        holder.llshare = (LinearLayout) v.findViewById(R.id.llShare);
        holder.llTweet = (LinearLayout) v.findViewById(R.id.llTweet);

        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        holder.txtTitle.setTypeface(face);
        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        holder.txtStartTime.setTypeface(face1);
        holder.txtLocation.setTypeface(face1);
        holder.txtStartDate.setTypeface(face1);
        holder.txtCost.setTypeface(face1);
        holder.txtDistance.setTypeface(face1);
        holder.llFbPost.setOnClickListener(this);
        holder.llshare.setOnClickListener(this);
        holder.llTweet.setOnClickListener(this);
        holder.llRemindme.setOnClickListener(this);
    }

    public void fillAll(ViewHolder holder, int pos) {
        holder.llFbPost.setTag(pos);
        holder.llshare.setTag(pos);
        holder.llTweet.setTag(pos);
        holder.llRemindme.setTag(pos);
        holder.txtTitle.setText(arrJobs.get(pos).get("eName").toString());
        if (arrJobs.get(pos).get("eZip").toString().equals("")) {
            holder.txtLocation.setText(arrJobs.get(pos).get("eLocation").toString());
        } else {
            holder.txtLocation.setText(arrJobs.get(pos).get("eLocation").toString() + ", " + arrJobs.get(pos).get("eZip").toString());
        }
        // holder.txtSearchKey.setText(arrJobs.get(pos).get("eSearchKeywords").toString());
        if (arrJobs.get(pos).get("eStartTime").toString().charAt(0) == '0') {
            if (arrJobs.get(pos).get("eEndTime").toString().charAt(0) == '0') {
                holder.txtStartTime.setText(arrJobs.get(pos).get("eStartTime").toString().substring(1) + " - " + arrJobs.get(pos).get("eEndTime").toString().substring(1));
            } else {
                holder.txtStartTime.setText(arrJobs.get(pos).get("eStartTime").toString().substring(1) + " - " + arrJobs.get(pos).get("eEndTime").toString());
            }
        } else {
            if (arrJobs.get(pos).get("eEndTime").toString().charAt(0) == '0') {
                holder.txtStartTime.setText(arrJobs.get(pos).get("eStartTime").toString() + " - " + arrJobs.get(pos).get("eEndTime").toString().substring(1));
            } else {
                holder.txtStartTime.setText(arrJobs.get(pos).get("eStartTime").toString() + " - " + arrJobs.get(pos).get("eEndTime").toString());
            }
        }

        holder.txtStartDate.setText(arrJobs.get(pos).get("eStartDate").toString());
        if (arrJobs.get(pos).get("eMaxCost").toString().equals(""))
            holder.txtCost.setText("£" + arrJobs.get(pos).get("eMinCost").toString());
        else
            holder.txtCost.setText("£" + arrJobs.get(pos).get("eMinCost").toString() + " - " + "£" + arrJobs.get(pos).get("eMaxCost").toString());
        if (arrJobs.get(pos).get("eMinCost").toString().equals(""))
            holder.txtCost.setText("Free");
        Float litersOfPetrol = Float.parseFloat(arrJobs.get(pos).get("eDistance").toString());
        DecimalFormat df = new DecimalFormat("0");
        df.setMaximumFractionDigits(0);
        holder.txtDistance.setText(df.format(litersOfPetrol) + "m");

        if (!arrJobs.get(pos).get("eImage").toString().equals("0") && !arrJobs.get(pos).get("eImage").toString().equals("")) {
            String url = "";
            if (!arrJobs.get(pos).get("eImage").toString().contains("http")) {
                url = context.getResources().getString(R.string.base_url_profile_image) + arrJobs.get(pos).get("eImage").toString();
            } else {

                url = arrJobs.get(pos).get("eImage").toString();
            }
            Picasso.with(context)
                    .load(url)
                    //.placeholder(R.drawable.default_back)
                    .into(holder.img_event);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llFbPost:
                int pos = (int) v.getTag();
                shareDialog(pos);
                break;

            case R.id.llReminde:
                int pos111 = (int) v.getTag();
                listposition = pos111;
                showAlert(context, "Add to Calendar", pos111);
                break;

            case R.id.llShare:
                int pos1 = (int) v.getTag();
                String url = "";
                if (!arrJobs.get(pos1).get("eImage").toString().contains("http:")) {
                    url = context.getResources().getString(R.string.base_url_profile_image);
                }
                new ShareImages(url + arrJobs.get(pos1).get("eImage").toString(), arrJobs.get(pos1).get("eName").toString(), pos1).execute();
                break;

            case R.id.llTweet:
                int pos11 = (int) v.getTag();
                String url1 = "";
                if (!arrJobs.get(pos11).get("eImage").toString().contains("http:")) {
                    url1 = context.getResources().getString(R.string.base_url_profile_image);
                }
                new ShareImagesViaTweet(url1 + arrJobs.get(pos11).get("eImage").toString(), arrJobs.get(pos11).get("eName").toString(), pos11).execute();
                break;

            default:
                break;
        }
    }

    private class ShareImages extends AsyncTask<Object, Object, Object> {
        String requestUrl, imagename_;
        Bitmap bitmap = null;
        int pos;
        InputStream stream = null;

        private ShareImages(String requestUrl, String _imagename_, int pos1) {
            this.requestUrl = requestUrl;
            imagename_ = _imagename_;
            pos = pos1;
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
            final String appPackageName = context.getPackageName();
            mBitmap = (Bitmap) o;

            Uri bmpUri = null;
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, arrJobs.get(pos).get("eName").toString());
            shareIntent.putExtra(Intent.EXTRA_TEXT, arrJobs.get(pos).get("eName").toString() + " at " + arrJobs.get(pos).get("eLocation").toString() + " on " +
                    arrJobs.get(pos).get("eStartDate").toString() + " " + arrJobs.get(pos).get("eStartTime").toString() + "\nhttps://play.google.com/store/apps/details?id=" + appPackageName);
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
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
            context.startActivity(Intent.createChooser(shareIntent, "Share"));
        }
    }

    private class ShareImagesViaTweet extends AsyncTask<Object, Object, Object> {
        private String requestUrl, imagename_;
        private Bitmap bitmap;
        int pos;
        InputStream stream = null;

        private ShareImagesViaTweet(String requestUrl, String _imagename_, int pos1) {
            this.requestUrl = requestUrl;
            this.imagename_ = _imagename_;
            pos = pos1;
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

                }
                bitmap = BitmapFactory.decodeStream(stream);

                return bitmap;
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            final String appPackageName = context.getPackageName();
            mBitmap = bitmap;
            try {
                Uri bmpUri = null;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, arrJobs.get(pos).get("eName").toString());
                intent.putExtra(Intent.EXTRA_TEXT, arrJobs.get(pos).get("eName").toString() + " at " + arrJobs.get(pos).get("eLocation").toString() + " on " +
                        arrJobs.get(pos).get("eStartDate").toString() + " " + arrJobs.get(pos).get("eStartTime").toString() + "\nhttps://play.google.com/store/apps/details?id=" + appPackageName);
                intent.setType("text/plain");
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + imagename_ + ".jpg");
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
                }
                intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                intent.setType("image/jpeg");
                intent.setPackage("com.twitter.android");
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Functions.showToast(context, "You haven't installed twitter on your device.");
            }
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

            mediaImage = new File(myDir.getPath() + "/share_image_/" + imagename);
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

    public static int listposition = 0;

    public void showAlert(final Context context, String title, final int pos1) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage("Add this flyer to your calendar?");
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            if (allowPermission())
                                addEvent(context, pos1);
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
    public void addEvent(Context context, int pos1) throws ParseException {
        final String appPackageName = context.getPackageName();
        Calendar calStartDate = null, calEndDate = null;
        String strStartTime = "", strEndTime = "";
        if (Functions.getTime().contains("a.m.") || Functions.getTime().contains("p.m.")) {
            String[] startdate = arrJobs.get(pos1).get("eStartTime").toString().split(" ");
            String[] enddate = arrJobs.get(pos1).get("eEndTime").toString().split(" ");
            if (arrJobs.get(pos1).get("eStartTime").toString().contains("am"))
                strStartTime = startdate[0] + " " + "a.m.";
            else
                strStartTime = startdate[0] + " " + "p.m.";
            if (arrJobs.get(pos1).get("eEndTime").toString().contains("am"))
                strEndTime = enddate[0] + " " + "a.m.";
            else
                strEndTime = enddate[0] + " " + "p.m.";
        } else {
            strStartTime = arrJobs.get(pos1).get("eStartTime").toString();
            strEndTime = arrJobs.get(pos1).get("eEndTime").toString();
        }
        SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("E d MMM yy h:mm a");
        Date StartDate = simpleDateTimeFormat.parse(arrJobs.get(pos1).get("eStartDate").toString() + " " + strStartTime);
        Date EndDate = simpleDateTimeFormat.parse(arrJobs.get(pos1).get("eEndDate").toString() + " " + strEndTime);
        calStartDate = Calendar.getInstance();
        calEndDate = Calendar.getInstance();
        calStartDate.setTime(StartDate);
        calEndDate.setTime(EndDate);
        try {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, calStartDate.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, calEndDate.getTimeInMillis());
            values.put(CalendarContract.Events.TITLE, arrJobs.get(pos1).get("eName").toString());
            values.put(CalendarContract.Events.DESCRIPTION, arrJobs.get(pos1).get("eName").toString() + " at " + arrJobs.get(pos1).get("eLocation").toString() + " on " +
                    arrJobs.get(pos1).get("eStartDate").toString() + " " + arrJobs.get(pos1).get("eStartTime").toString() + " brought to you by " + "https://play.google.com/store/apps/details?id=" + appPackageName);
            values.put(CalendarContract.Events.EVENT_LOCATION, arrJobs.get(pos1).get("eLocation").toString());
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());
            System.out.println(Calendar.getInstance().getTimeZone().getID());
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
            Functions.showToast(context, "Alarm set successfully");

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

    class ViewHolder {
        TextView txtTitle, txtLocation, txtStartTime, txtStartDate, txtCost, txtDistance;
        ImageView img_event;
        LinearLayout llRemindme, llFbPost, llshare, llTweet;
        ProgressBar pb1;
    }

    public void shareDialog(int pos) {
        String url = "";
        if (!arrJobs.get(pos).get("eImage").toString().contains("http"))
            url = context.getResources().getString(R.string.base_url_profile_image);
        final String appPackageName = context.getPackageName();
        FacebookSdk.sdkInitialize(context);
        CallbackManager callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog((Activity) context);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(arrJobs.get(pos).get("eName").toString())
                    .setContentDescription(arrJobs.get(pos).get("eName").toString() + " at " + arrJobs.get(pos).get("eLocation").toString() + " on " +
                            arrJobs.get(pos).get("eStartDate").toString() + " " + arrJobs.get(pos).get("eStartTime").toString())
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)).setImageUrl(Uri.parse(url + arrJobs.get(pos).get("eImage").toString()))
                    .build();

            shareDialog.show(linkContent);
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
}
