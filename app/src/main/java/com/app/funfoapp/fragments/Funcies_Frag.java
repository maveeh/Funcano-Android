package com.app.funfoapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.funfoapp.Model.SearchWords;
import com.app.funfoapp.R;
import com.app.funfoapp.activities.Funncies;
import com.app.funfoapp.adapters.SearchList;
import com.app.funfoapp.common.CircularImageView;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;
import com.facebook.login.LoginManager;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pooja_Pollysys on 4/7/16.
 */
public class Funcies_Frag extends Fragment implements View.OnClickListener {

    Context context;
    View v;
    RoundedImageView img_userimage;
    TextView txt_funcies_username;
    Button btn_searchfuncies;
    String mUserId = "", mKeyWordId = "", mFirstName = "", mLastName = "", mImageName = "", uType = "";
    ListView listview_search;
    FlowLayout flowLayout;
    ArrayList<HashMap<Object, String>> mSearchList = new ArrayList<>();
    LinearLayout ll_emptyview;
    ScrollView parent_ScrollView;
    public static boolean addFuncies = false;
    ImageView img_add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.funcies_frag, container, false);
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
        init(v);
        LoadWS();
    }

    //Initilization Ui Variables
    public void init(View v) {
        img_userimage = (RoundedImageView) v.findViewById(R.id.img_userimage);
        txt_funcies_username = (TextView) v.findViewById(R.id.txt_funcies_username);
        listview_search = (ListView) v.findViewById(R.id.listview_search);
        btn_searchfuncies = (Button) v.findViewById(R.id.btn_searchfuncies);
        img_add = (ImageView) v.findViewById(R.id.img_add);
        flowLayout = (FlowLayout) v.findViewById(R.id.ll);
        ll_emptyview = (LinearLayout) v.findViewById(R.id.ll_emptyview);
        parent_ScrollView = (ScrollView) v.findViewById(R.id.parent_ScrollView);
        addClickListener();
        setFont();
    }

    //add click listener
    public void addClickListener() {
        btn_searchfuncies.setOnClickListener(this);
        img_add.setOnClickListener(this);
    }

    //set Font
    public void setFont() {
        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        txt_funcies_username.setTypeface(face1);
        btn_searchfuncies.setTypeface(face1);
    }

    //method to load webservice
    public void LoadWS() {
        if (Validations.isNetworkAvailable((Activity) context)) {
            new AllSearchWordAsync().execute();
        } else {
            Functions.showAlert(context, "Internet Connection Error", "Please check your internet connection and try again.");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_searchfuncies:
                startActivity(new Intent(context, Funncies.class));
                break;

            case R.id.img_add:
                startActivity(new Intent(context, Funncies.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (addFuncies == true) {
            flowLayout.removeAllViews();
            addFuncies = false;
            LoadWS();
        }

    }

    //webservice for all search words
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
                    mFirstName = jsonObject.getString("uFirstName");
                    mLastName = jsonObject.getString("uLastName");
                    mImageName = jsonObject.getString("imageName");
                    uType = jsonObject.getString("uType");
                    JSONArray jsonArray = new JSONObject(obj.getString("0")).getJSONArray("keyword");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("id", object.getString("id"));
                            hashMap.put("sKeywords", object.getString("sKeywords"));
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
                txt_funcies_username.setText(mFirstName + " " + mLastName);
                if (mImageName.contains("http")) {
                    Picasso.with(context)
                            .load(mImageName)
                            .into(img_userimage);
                } else {
                    Picasso.with(context)
                            .load(getResources().getString(R.string.base_url_profile_image) + mImageName)
                            .into(img_userimage);
                }
                if (mSearchList != null && !mSearchList.isEmpty()) {
                    for (int i = 0; i < mSearchList.size(); i++) {
                        flowLayout.addView(createNewTextView(mSearchList.get(i).get("sKeywords"), i));
                    }
                    ll_emptyview.setVisibility(View.GONE);
                } else {
                    ll_emptyview.setVisibility(View.VISIBLE);
                }
            } else {
                Functions.showToast(context, strErrorMsg);
            }
        }
    }

    //webservice for delete search word
    public class DeleteSearchWordAsync extends AsyncTask<Void, Void, Void> {
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
                String url = getResources().getString(R.string.base_url) + "deleteKeyword/?keyid=" + mKeyWordId;
                Log.e("tag", "response " + url);
                JSONParser parser = new JSONParser();
                String res = parser.getJSONFromUrl(url, "GET", null);
                Log.e("tag", "response " + res);
                JSONObject obj = new JSONObject(res);
                success = obj.getInt("success");
                if (success == 1) {
                    strErrorMsg = obj.getString("status");
                } else {
                    strErrorMsg = obj.getString("status");
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
                flowLayout.removeAllViews();
                if (mSearchList != null && !mSearchList.isEmpty()) {
                    for (int i = 0; i < mSearchList.size(); i++) {
                        flowLayout.addView(createNewTextView(mSearchList.get(i).get("sKeywords"), i));
                    }
                } else {
                    ll_emptyview.setVisibility(View.VISIBLE);
                }
            } else {

            }
        }
    }

    View view;

    private View createNewTextView(String text, final int position) {
        view = getActivity().getLayoutInflater().inflate(R.layout.searchview, null);
        TextView address = (TextView) view.findViewById(R.id.txt_searchwords);
        address.setText(text);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                // set title
                alertDialogBuilder.setTitle("Delete");
                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to delete funcies?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mKeyWordId = mSearchList.get(position).get("id");
                                if (Validations.isNetworkAvailable((Activity) context)) {
                                    mSearchList.remove(position);
                                    new DeleteSearchWordAsync().execute();
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
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });
        view.setTag(position);
        return view;
    }
}
