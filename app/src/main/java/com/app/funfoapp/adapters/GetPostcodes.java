package com.app.funfoapp.adapters;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class GetPostcodes extends ArrayAdapter<String> implements Filterable {
    ArrayList<String> autoCompleteList=new ArrayList<>();
    ArrayList<String> cityZipList;
    public GetPostcodes(Context context, int textViewResourceId, ArrayList<String> data) {
        super(context, textViewResourceId);
        this.cityZipList=data;
    }

    @Override
    public int getCount() {
        if(autoCompleteList!=null)
        return autoCompleteList.size();
        else
            return 0;
    }

    @Override
    public String getItem(int position) {
        return autoCompleteList.get(position).toString();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<String> autoCompleteList = new ArrayList<String>();
                if (constraint != null) {
                    for (int i = 0; i < cityZipList.size(); i++) {
                        if (cityZipList.get(i).toString().startsWith(
                                constraint.toString().toUpperCase(Locale.getDefault()))) {
                            autoCompleteList.add(cityZipList.get(i).toString());
                        }
                    }
                    // Now assign the values and count to the FilterResults
                    // object
                    filterResults.values = autoCompleteList;

                    filterResults.count = autoCompleteList.size();

                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                autoCompleteList=(ArrayList<String>) results.values;
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
