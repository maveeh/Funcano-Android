package com.app.funfoapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.app.funfoapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pooja_Pollysys on 3/30/16.
 */
public class No_Events_Adapter extends SimpleAdapter {

    Context context;
    ArrayList<HashMap<String,Object>> arrJobs =new ArrayList<>();
    public No_Events_Adapter(Context context, ArrayList<HashMap<String, Object>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context=context;
        this.arrJobs =data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=convertView;
        if(v==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            v=inflater.inflate(R.layout.placeholder_empty,null,false);
            ViewHolder holder=new ViewHolder();
            holder.txtNoFlyer=(TextView)v.findViewById(R.id.txtNo);
            holder.txtCreate=(TextView)v.findViewById(R.id.txtCreate);
            v.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) v.getTag();
        Typeface face1=Typeface.createFromAsset(context.getAssets(),"fonts/OpenSans-Regular.ttf");
        holder.txtNoFlyer.setTypeface(face1);
        holder.txtCreate.setTypeface(face1);

        holder.txtCreate.setText(arrJobs.get(position).get("key").toString());
        return v;
    }

    class ViewHolder{
        Button btnAnswer;
        TextView txtNoFlyer,txtCreate;
    }
}
