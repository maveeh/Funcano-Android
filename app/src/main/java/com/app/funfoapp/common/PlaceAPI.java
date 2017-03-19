package com.app.funfoapp.common;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Pooja_Pollysys on 4/14/16.
 */
public class PlaceAPI {

    private static final String TAG = PlaceAPI.class.getSimpleName();

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDSza4frfgn1UK0klIoPwsFZapqAoPzxe0";
    JSONArray predsJsonArray;

    public JSONArray autocomplete(String input) {
        ArrayList<String> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
//            sb.append("&types=address");
            sb.append("&components=country:uk");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return predsJsonArray;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return predsJsonArray;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Log.d(TAG, jsonResults.toString());
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            //Log.e("tag", "google api" + jsonObj);
            predsJsonArray = jsonObj.getJSONArray("predictions");
            //Log.e("tag","status "+jsonObj);
            // Extract the Place descriptions from the results
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return predsJsonArray;
    }
}
