package com.app.funfoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.funfoapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pooja_Pollysys on 4/7/16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<HashMap<String, ?>> mItemList=new ArrayList<>();


    public RecyclerAdapter(ArrayList<HashMap<String, ?>> arrEvents) {
        mItemList = arrEvents;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_event_design, parent, false);
        return RecyclerItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;
        String itemText = String.valueOf(mItemList.get(position).get("key"));
        holder.setItemText(itemText);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

}