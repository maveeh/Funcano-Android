package com.app.funfoapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pooja_Pollysys on 4/21/16.
 */
public class GetCat_Adapter extends SimpleAdapter {

    Context context;
    ArrayList<HashMap<String, ?>> arrJobs = new ArrayList<>();

    public GetCat_Adapter(Context context, ArrayList<HashMap<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.arrJobs = data;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.get_category_view, null, false);
            ViewHolder holder = new ViewHolder();
            holder.imgCat = (ImageView) v.findViewById(R.id.img_cat);
            holder.txtName = (TextView) v.findViewById(R.id.txt_cat_name);
            v.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.txtName.setText(arrJobs.get(pos).get("catName").toString());
        if (!arrJobs.get(pos).get("catImage").toString().equals("0") || !arrJobs.get(pos).get("catImage").toString().equals(""))
            Picasso.with(context)
                    .load(arrJobs.get(pos).get("catImage").toString())
                    .into(holder.imgCat);
        return v;
    }

    class ViewHolder {
        TextView txtName;
        ImageView imgCat;
    }
}