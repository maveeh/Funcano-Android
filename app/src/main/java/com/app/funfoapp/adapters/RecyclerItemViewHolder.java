package com.app.funfoapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.app.funfoapp.R;

/**
 * Created by Pooja_Pollysys on 4/7/16.
 */
public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView mItemTextView;

    public RecyclerItemViewHolder(final View parent, TextView itemTextView) {
        super(parent);
        mItemTextView = itemTextView;
    }

    public static RecyclerItemViewHolder newInstance(View parent) {
        TextView itemTextView = (TextView) parent.findViewById(R.id.txtEventName);
        return new RecyclerItemViewHolder(parent, itemTextView);
    }

    public void setItemText(CharSequence text) {
        mItemTextView.setText(text);
    }

}