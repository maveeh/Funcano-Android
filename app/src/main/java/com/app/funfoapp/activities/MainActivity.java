package com.app.funfoapp.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.app.funfoapp.common.Functions;
import com.app.funfoapp.common.Validations;
import com.app.funfoapp.fragments.Flyers_Frag;
import com.app.funfoapp.fragments.Funcies_Frag;
import com.app.funfoapp.fragments.Home_Frag;
import com.app.funfoapp.fragments.Profile_Frag;
import com.app.funfoapp.fragments.Search_Frag;
import com.app.funfoapp.interfaces.NotifyFragment;
import com.app.funfoapp.parser.JSONParser;
import com.app.funfoapp.utility.DbOp;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Pooja_Pollysys on 4/7/16.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, NotifyFragment {

    Context context = MainActivity.this;
    Fragment[] fragments;
    int curPOs = -1;
    LinearLayout llHome, llSearch, llFuncies, llFlyers, llProfile;
    Toolbar mToolbar;
    TextView txt_toolbar;
    String uType="";
    Home_Frag homeFrag;
    private Flyers_Frag flyerFrag;
    private ImageView img_logo;
    private int test = 112;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("logo");
        homeFrag=new Home_Frag();
        flyerFrag=new Flyers_Frag();
        fragments = new Fragment[]{homeFrag, new Search_Frag(), new Funcies_Frag(), flyerFrag, new Profile_Frag()};
        initAllVar();
        addListener();
        llHome.performClick();
        test = 2;
    }

    //Initilization Ui Variables
    public void initAllVar() {
        llHome = (LinearLayout) findViewById(R.id.ll_home);
        llSearch = (LinearLayout) findViewById(R.id.ll_search);
        llFuncies = (LinearLayout) findViewById(R.id.ll_funcies);
        llFlyers = (LinearLayout) findViewById(R.id.ll_flyers);
        llProfile = (LinearLayout) findViewById(R.id.ll_profile);
        txt_toolbar = (TextView)mToolbar.findViewById(R.id.txt_toolbar);
        img_logo = (ImageView) mToolbar.findViewById(R.id.img_logo);

    }

    //Add click Listener
    public void addListener() {
        llHome.setOnClickListener(this);
        llSearch.setOnClickListener(this);
        llFuncies.setOnClickListener(this);
        llFlyers.setOnClickListener(this);
        llProfile.setOnClickListener(this);
    }

    public void switchFrag(int pos) {
        if (curPOs != pos) {
            invalidateOptionsMenu();
            switchImages(pos);
            curPOs = pos;
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.ffContainer, fragments[pos]);
            ft.commit();
        }
    }

    public void switchImages(int pos) {
        switch (pos) {
            case 0:
                llHome.setBackgroundColor(Color.parseColor("#fff838"));
                llSearch.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llFuncies.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llFlyers.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llProfile.setBackgroundColor(Color.parseColor("#e0e0e0"));
                break;
            case 1:
                llHome.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llSearch.setBackgroundColor(Color.parseColor("#fff838"));
                llFuncies.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llFlyers.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llProfile.setBackgroundColor(Color.parseColor("#e0e0e0"));
                break;
            case 2:
                llHome.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llSearch.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llFuncies.setBackgroundColor(Color.parseColor("#fff838"));
                llFlyers.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llProfile.setBackgroundColor(Color.parseColor("#e0e0e0"));
                break;
            case 3:
                llHome.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llSearch.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llFuncies.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llFlyers.setBackgroundColor(Color.parseColor("#fff838"));
                llProfile.setBackgroundColor(Color.parseColor("#e0e0e0"));
                break;
            case 4:
                llHome.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llSearch.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llFuncies.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llFlyers.setBackgroundColor(Color.parseColor("#e0e0e0"));
                llProfile.setBackgroundColor(Color.parseColor("#fff838"));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home:
                txt_toolbar.setText("Funcano Logo");
                txt_toolbar.setVisibility(View.GONE);
                img_logo.setVisibility(View.VISIBLE);
                switchFrag(0);
                break;
            case R.id.ll_search:
                txt_toolbar.setText("Search");
                txt_toolbar.setVisibility(View.VISIBLE);
                img_logo.setVisibility(View.GONE);
                switchFrag(1);
                break;
            case R.id.ll_funcies:
                txt_toolbar.setText("Funcies");
                txt_toolbar.setVisibility(View.VISIBLE);
                img_logo.setVisibility(View.GONE);
                switchFrag(2);
                break;
            case R.id.ll_flyers:
                txt_toolbar.setText("My Flyers");
                txt_toolbar.setVisibility(View.VISIBLE);
                img_logo.setVisibility(View.GONE);
                switchFrag(3);
                break;
            case R.id.ll_profile:
                txt_toolbar.setText("Profile");
                txt_toolbar.setVisibility(View.VISIBLE);
                img_logo.setVisibility(View.GONE);
                switchFrag(4);
                break;
        }
    }

    MenuItem mm,mm1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        mm = menu.findItem(R.id.action_settings);
        mm1 = menu.findItem(R.id.action_edit);
        if (curPOs == 0) {
            mToolbar.setPadding(96,0,0,0);
            mm.setVisible(true);
            mm1.setVisible(false);
        }
        else if(curPOs == 4) {
            mToolbar.setPadding(96,0,0,0);
            mm1.setVisible(true);
            mm.setVisible(false);
        }
        else {
            mToolbar.setPadding(0,0,0,0);
            mm.setVisible(false);
            mm1.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                DbOp dbOp=new DbOp(context);
                uType = dbOp.getRole();
                if(uType.equals(""))
                    uType = "basic";
                Intent i = new Intent(this, Create_New_Event.class);
                i.putExtra("uType", uType);
                i.putExtra("eImage", "");
                i.putExtra("eId", "");
                i.putExtra("eName", "");
                i.putExtra("eLocation", "");
                i.putExtra("eStartDate", "");
                i.putExtra("eStartTime", "");
                i.putExtra("eEndDate", "");
                i.putExtra("eEndTime", "");
                i.putExtra("eSearchKeywords", "");
                i.putExtra("eCatId", "");
                i.putExtra("ecatName", "");
                i.putExtra("eDesc", "");
                i.putExtra("eMinCost", "");
                i.putExtra("eMaxCost", "");
                i.putExtra("eLat", "");
                i.putExtra("eLong", "");
                i.putExtra("eUsername", "");
                startActivity(i);
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(this, EditProfile.class);
                intent.putExtra("uType",uType);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    boolean doubleBackToExitPressedOnce = false;
    int pos = -1;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (doubleBackToExitPressedOnce && pos == curPOs) {
                return super.onKeyDown(keyCode, event);
            }
            doubleBackToExitPressedOnce = true;
            pos = curPOs;
            Functions.showToast(this, "Press again to exit from application.");
        }
        return false;
    }


    @Override
    public void isNotifyFrag() {
        switchFrag(2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(curPOs==0 && homeFrag!=null)
            homeFrag.onRequestPermissionsResult(requestCode, permissions, grantResults);
        else if(curPOs==3 && flyerFrag!=null)
            flyerFrag.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
