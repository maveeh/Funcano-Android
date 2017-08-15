package com.app.funfoapp.common;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by lapsncomps pc on 3/30/2016.
 */
public class PostCodeList {

    private HashMap<Object,String> mVendorList;
    Context mContext;

    public PostCodeList(Context context, HashMap<Object, String> vendorList) {
        mContext=context;
        mVendorList=vendorList;
    }

    public String getpostcode() {
        return mVendorList.get("postcode");
    }

    public String getlatitude() {
        return mVendorList.get("latitude");
    }

    public String getlongitude() {
        return mVendorList.get("longitude");
    }

}
