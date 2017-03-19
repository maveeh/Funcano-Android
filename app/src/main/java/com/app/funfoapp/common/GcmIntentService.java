package com.app.funfoapp.common;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.app.funfoapp.R;
import com.app.funfoapp.activities.Flyer_Detail;
import com.app.funfoapp.activities.MainActivity;
import com.app.funfoapp.activities.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GcmIntentService extends IntentService {
    Context context = getBaseContext();
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    String msg, userName, startDate, endDate, startTime, endTime, catName, desc, distance, eventName, lat, lon, maxCost, minCost, searchWords, userImage, eventId,
            eventImage, eventLoc,uType,uPhone,uTicketingUrl,eZip;
    SharedPreferences sp;
    String noti;
    Uri uri;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String json = intent.getStringExtra("message");
//        JSONObject jsonObject;
//        try {
//            jsonObject = new JSONObject(json);
//            Log.e("test", "jsonObject----" + jsonObject);
//            msg = jsonObject.getString("msg");
//            userName = jsonObject.getString("cUserName");
//            startDate = jsonObject.getString("cEstartDate");
//            endDate = jsonObject.getString("cEendDate");
//            startTime = jsonObject.getString("cEStartTime");
//            endTime = jsonObject.getString("cEEndTime");
//            catName = jsonObject.getString("cEcatName");
//            desc = jsonObject.getString("cEDesc");
//            distance = jsonObject.getString("cEDistance");
//            eventName = jsonObject.getString("cEname");
//            lat = jsonObject.getString("cELat");
//            lon = jsonObject.getString("cELong");
//            maxCost = jsonObject.getString("cEMaxCost");
//            minCost = jsonObject.getString("cEMinCost");
//            searchWords = jsonObject.getString("cESearchKeywords");
//            userImage = jsonObject.getString("cEUImage");
//            eventId = jsonObject.getString("cEventId");
//            eventImage = jsonObject.getString("eEImage");
//            eventLoc = jsonObject.getString("eLocation");
//            uType = jsonObject.getString("uType");
//            eZip = jsonObject.getString("eZip");
//            uPhone = jsonObject.getString("uPhone");
//            uTicketingUrl = jsonObject.getString("uTicketingUrl");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        sendNotification(json);
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent myintent = new Intent(this, Flyer_Detail.class);
        myintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        myintent.putExtra("eId", eventId);
        myintent.putExtra("eDistance", distance);
        myintent.putExtra("eStartDate", startDate);
        myintent.putExtra("eEndDate", endDate);
        myintent.putExtra("eStartTime", startTime);
        myintent.putExtra("eEndTime", endTime);
        myintent.putExtra("eName", eventName);
        myintent.putExtra("eLocation", eventLoc);
        myintent.putExtra("eZip", eZip);
        myintent.putExtra("ecatName", catName);
        myintent.putExtra("eSearchKeywords", searchWords);
        myintent.putExtra("eImage", eventImage);
        myintent.putExtra("eMinCost", minCost);
        myintent.putExtra("eMaxCost", maxCost);
        myintent.putExtra("eLat", lat);
        myintent.putExtra("eLong", lon);
        myintent.putExtra("eDesc", desc);
        myintent.putExtra("eUsername", userName);
        myintent.putExtra("eUImage", userImage);
        myintent.putExtra("uType", uType);
        myintent.putExtra("uPhone", uPhone);
        myintent.putExtra("uTicketingUrl", uTicketingUrl);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myintent, PendingIntent.FLAG_ONE_SHOT);
        noti = sp.getString("noti", null);
        if (noti == null || noti.equals("true"))
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        else
            uri = null;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Funcano")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg).setTicker(msg)
                        .setAutoCancel(true).setSound(uri);
        mBuilder.setPriority(android.app.Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= 21)
            mBuilder.setVibrate(new long[0]);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
