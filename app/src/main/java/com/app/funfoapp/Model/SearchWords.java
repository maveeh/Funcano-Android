package com.app.funfoapp.Model;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Pooja_Pollysys on 13/09/16.
 */
public class SearchWords {

    private HashMap<String, Object> mAudioList;
    Context mContext;

    public SearchWords(Context context, HashMap<String, Object> audioList) {
        mContext = context;
        mAudioList = audioList;
    }

    public String getId() {
        return mAudioList.get("id").toString();
    }

    public String getName() {
        return mAudioList.get("title").toString();
    }
}