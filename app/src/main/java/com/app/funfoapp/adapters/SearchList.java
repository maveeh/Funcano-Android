package com.app.funfoapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.app.funfoapp.Model.SearchWords;
import com.app.funfoapp.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Pooja_Pollysys on 20/07/16.
 */
public class SearchList extends BaseAdapter{

    Context context;
    ArrayList<SearchWords> arrJobs = new ArrayList<>();
    ArrayList<SearchWords> searchJobs = new ArrayList<>();

    public SearchList(Context context,ArrayList<SearchWords> data) {
        this.context=context;
        this.arrJobs =data;
        searchJobs.addAll(arrJobs);
    }

    @Override
    public int getCount() {
        if (arrJobs != null) return arrJobs.size();
        else return 0;
    }

    @Override
    public Object getItem(int position) {
        if (arrJobs != null) return arrJobs.get(position);
        else return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.searchitem, parent, false);
            // view holder
            ViewHolder holder = new ViewHolder();
            holder.txt_searchitem = (TextView) view.findViewById(R.id.txt_searchitem);
            view.setTag(holder);
        }
        final String word = arrJobs.get(position).getName();

        if (word != null) {
            // view holder
            ViewHolder holder = (ViewHolder) view.getTag();
            // content
            holder.txt_searchitem.setText(word);
        }
        return view;
    }

    static class ViewHolder {
        TextView txt_searchitem;
    }

    //Method for search
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrJobs.clear();
        if (charText.length() == 0) {
            arrJobs.addAll(searchJobs);
        } else {
            for (SearchWords arrayList : searchJobs) {
                if (arrayList.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrJobs.add(arrayList);
                }
            }
        }
        notifyDataSetChanged();
    }
}
