package com.app.funfoapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.app.funfoapp.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pooja_Pollysys on 4/20/16.
 */
public class GetMyFlyer_Adapter extends SimpleAdapter {
    Context context;
    ArrayList<HashMap<String, ?>> arrJobs = new ArrayList<>();

    public GetMyFlyer_Adapter(Context context, ArrayList<HashMap<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.arrJobs = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.my_flyer_view, null, false);
            ViewHolder holder = new ViewHolder();
            initAll(holder, v);
            v.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) v.getTag();
        fillAll(holder, position);
        return v;
    }

    public void initAll(ViewHolder holder, View v) {
        holder.txtTitle = (TextView) v.findViewById(R.id.txtEventName);
        holder.txtSearchKey = (TextView) v.findViewById(R.id.txtSearchKey);
        holder.txtStartTime = (TextView) v.findViewById(R.id.txtStartTime);
        holder.txtStartDate = (TextView) v.findViewById(R.id.txtStartDate);
        holder.txtEndDate = (TextView) v.findViewById(R.id.txtEndDate);
        holder.txtEndTime = (TextView) v.findViewById(R.id.txtEndTime);
        holder.txtCost = (TextView) v.findViewById(R.id.txtCost);
        holder.txtDistance = (TextView) v.findViewById(R.id.txtDistance);
        holder.txtStatus = (TextView) v.findViewById(R.id.txtFstatus);
        holder.pb1 = (ProgressBar) v.findViewById(R.id.pb1);
        holder.img_event = (ImageView) v.findViewById(R.id.img_event);
        holder.llParent = (LinearLayout) v.findViewById(R.id.llParent);

        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        holder.txtTitle.setTypeface(face);
        holder.txtSearchKey.setTypeface(face);
        holder.txtStatus.setTypeface(face);
        Typeface face1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        holder.txtStartTime.setTypeface(face1);
        holder.txtStartDate.setTypeface(face1);
        holder.txtEndDate.setTypeface(face1);
        holder.txtEndTime.setTypeface(face1);
        holder.txtCost.setTypeface(face1);
        holder.txtDistance.setTypeface(face1);

    }

    public void fillAll(ViewHolder holder, int pos) {
        holder.txtTitle.setText(arrJobs.get(pos).get("eName").toString());
        // holder.txtSearchKey.setText(arrJobs.get(pos).get("eSearchKeywords").toString());
        if (arrJobs.get(pos).get("eStartTime").toString().charAt(0) == '0') {
            holder.txtStartTime.setText(arrJobs.get(pos).get("eStartTime").toString().substring(1));
        } else {
            holder.txtStartTime.setText(arrJobs.get(pos).get("eStartTime").toString());
        }

        holder.txtStartDate.setText(arrJobs.get(pos).get("eStartDate").toString());
        holder.txtEndDate.setText(arrJobs.get(pos).get("eEndDate").toString());
        if (arrJobs.get(pos).get("eEndTime").toString().charAt(0) == '0') {
            holder.txtEndTime.setText(arrJobs.get(pos).get("eEndTime").toString().substring(1));
        } else {
            holder.txtEndTime.setText(arrJobs.get(pos).get("eEndTime").toString());
        }

        if (arrJobs.get(pos).get("eStatus").toString().equals("Expired")) {
            holder.txtStatus.setTextColor(Color.parseColor("#fd1212"));
            holder.llParent.setBackgroundResource(R.drawable.expire_bg_flyer);
        } else {
            holder.llParent.setBackgroundResource(R.drawable.bg_flyer);
            holder.txtStatus.setTextColor(Color.parseColor("#191919"));
        }
        holder.txtStatus.setText(arrJobs.get(pos).get("eStatus").toString());
        if (arrJobs.get(pos).get("eMaxCost").toString().equals(""))
            holder.txtCost.setText("£" + arrJobs.get(pos).get("eMinCost").toString());
        else
            holder.txtCost.setText("£" + arrJobs.get(pos).get("eMinCost").toString() + " - " + "£" + arrJobs.get(pos).get("eMaxCost").toString());
        if (arrJobs.get(pos).get("eMinCost").toString().equals(""))
            holder.txtCost.setText("Free");

//        Float litersOfPetrol = Float.parseFloat(arrJobs.get(pos).get("eDistance").toString());
//        DecimalFormat df = new DecimalFormat("0");
//        df.setMaximumFractionDigits(0);
//        holder.txtDistance.setText(df.format(litersOfPetrol) + "m");
        if (!arrJobs.get(pos).get("eImage").toString().equals("0") && !arrJobs.get(pos).get("eImage").toString().equals("")) {
            if (arrJobs.get(pos).get("eImage").toString().contains("http"))
                Picasso.with(context)
                        .load(arrJobs.get(pos).get("eImage").toString())
                        .into(holder.img_event);
            else
                Picasso.with(context)
                        .load(context.getResources().getString(R.string.base_url_profile_image) + arrJobs.get(pos).get("eImage").toString())
                        .into(holder.img_event);
        }
    }

    class ViewHolder {
        TextView txtTitle, txtSearchKey, txtStartTime, txtStartDate, txtCost, txtDistance, txtEndDate, txtEndTime, txtStatus;
        ImageView img_event;
        ProgressBar pb1;
        LinearLayout llParent;
    }
}

