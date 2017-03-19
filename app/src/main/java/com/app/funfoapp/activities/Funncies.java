package com.app.funfoapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.funfoapp.Model.SearchWords;
import com.app.funfoapp.R;
import com.app.funfoapp.adapters.SearchList;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.fragments.Funcies_Frag;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class Funncies extends AppCompatActivity {

    Context context = Funncies.this;
    Toolbar toolbar;
    LinearLayout ll_funncies_back;
    ArrayList<SearchWords> mAllWords = new ArrayList<>();
    ListView listview_search;
    SearchList searchList;
    AutoCompleteTextView auto_searchfuncies;
    ArrayList<HashMap<Object,String>> mSearchList = new ArrayList<>();
    String mUserId = "",mKeyWord = "",mKeyId="",uType="";
    boolean match = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funncies);
        setToolbar();
        init();
        LoadAllWordWS();
        LoadSavedWordWS();
        listview_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                match = false;
                 mKeyWord = mAllWords.get(position).getName();
                 mKeyId = mAllWords.get(position).getId();
                auto_searchfuncies.setText(mKeyWord);
                auto_searchfuncies.setSelection(mKeyWord.length());
                if(mSearchList!=null && !mSearchList.isEmpty()) {
                    for (int i = 0; i < mSearchList.size(); i++) {
                        if (mKeyId.equals(mSearchList.get(i).get("KeywordId"))) {
                            match = true;
                            break;
                        }
                    }
                }
                addFunnices();
            }
        });
    }

    //set Toolbar
    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.funncies_toolbar);
        ll_funncies_back = (LinearLayout) toolbar.findViewById(R.id.ll_funncies_back);
        ll_funncies_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Initilization Ui Variables
    public void init() {
        auto_searchfuncies = (AutoCompleteTextView) findViewById(R.id.auto_searchfuncies);
        listview_search = (ListView) findViewById(R.id.listview_search);
    }

    //method for Webservice to get All Words
    public void LoadAllWordWS() {
        if (Validations.isNetworkAvailable((Activity) context)) {
            new AllSeachWordsAsync().execute();

        } else {
            Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
        }
    }

    //method for Webservice saved words
    public void LoadSavedWordWS() {
        if (Validations.isNetworkAvailable((Activity) context)) {
            new AllSearchWordAsync().execute();

        } else {
            Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
        }
    }

    public void addFunnices() {
        if(match == true) {
            Functions.showToast(context,"This funcies is already added.");
            match = false;
        }
        else {
            if(uType.equals("basic")) {
                if(mSearchList.size()<16) {
                    if (Validations.isNetworkAvailable((Activity) context)) {
                        new AddSearchWordAsync().execute();

                    } else {
                        Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
                    }
                }
                else {
                    Functions.showToast(context, "You can add maximum 16 funcies.");
                }
            }
            else {
                if (Validations.isNetworkAvailable((Activity) context)) {
                    new AddSearchWordAsync().execute();

                } else {
                    Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
                }
            }
        }
    }

    //webservice to get All Saved Words
    public class AllSeachWordsAsync extends AsyncTask<Void, Void, Void> {
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
                if (mAllWords != null && !mAllWords.isEmpty())
                    mAllWords.clear();
                String url = getResources().getString(R.string.base_url) + "getAllHeadings";
                Log.e("tag", "response " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                Log.e("tag", "response " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    JSONArray jsonArray = new JSONObject(obj.getString("0")).getJSONArray("Headings");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", jsonObject.getString("id"));
                        hashMap.put("title", jsonObject.getString("sKeywords"));
                        SearchWords searchWords = new SearchWords(context, hashMap);
                        mAllWords.add(searchWords);
                    }
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
                searchList = new SearchList(context, mAllWords);
                listview_search.setAdapter(searchList);
                auto_searchfuncies.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().equals("")) {
                            listview_search.setVisibility(View.GONE);
                        } else {
                            listview_search.setVisibility(View.VISIBLE);
                            searchList.filter(s.toString());
                            searchList.notifyDataSetChanged();
                        }
                    }
                });
            } else {

                Functions.showToast(context, strErrorMsg);
            }
            listview_search.setVisibility(View.GONE);
        }
    }

    //webservice to get All Saved Words
    public class AllSearchWordAsync extends AsyncTask<Void, Void, Void> {
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
                dbOp.close();
                if (mSearchList != null && !mSearchList.isEmpty())
                    mSearchList.clear();
                String url = getResources().getString(R.string.base_url) + "userDetailKeyword/?usrid=" + mUserId;
                Log.e("tag", "response " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                Log.e("tag", "response " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    JSONObject jsonObject = new JSONObject(obj.getString("0")).getJSONObject("profile");
                    uType = jsonObject.getString("uType");
                    JSONArray jsonArray = new JSONObject(obj.getString("0")).getJSONArray("keyword");
                    if(jsonArray!=null && jsonArray.length() >0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            HashMap<Object,String> hashMap = new HashMap<>();
                            hashMap.put("id",object.getString("id"));
                            hashMap.put("sKeywords",object.getString("sKeywords"));
                            hashMap.put("KeywordId",object.getString("keyWordId"));
                            mSearchList.add(hashMap);
                        }
                    }
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

            } else {

            }
        }
    }

    //webservice to Add Funncies
    public class AddSearchWordAsync extends AsyncTask<Void, Void, Void> {
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
                String url = getResources().getString(R.string.base_url) + "addKeyword/?usrid=" + mUserId + "&keyword=" + URLEncoder.encode(mKeyWord.toString(), "UTF-8") + "&keyId=" + mKeyId;
                Log.e("tag", "response " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                Log.e("tag", "response " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {

                } else {

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
                match = false;
                Funcies_Frag.addFuncies = true;
            } else {
                match = true;
                Funcies_Frag.addFuncies = false;
            }
            finish();
        }
    }
}
