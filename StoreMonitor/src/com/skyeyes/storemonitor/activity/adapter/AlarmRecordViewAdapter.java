package com.skyeyes.storemonitor.activity.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyeyes.base.bean.AlarmInfoBean;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.storemonitor.R;

public class AlarmRecordViewAdapter extends BaseAdapter {
	ArrayList<AlarmInfoBean> list;
    LayoutInflater inflater;
    Context mContext;
    public AlarmRecordViewAdapter(Context context,ArrayList<AlarmInfoBean> list) {
    	mContext = context;
        this.list=list;
        this.inflater=LayoutInflater.from(context);
    }

    
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CacheView cacheView;
        if(convertView==null){
        	cacheView=new CacheView();
            convertView=inflater.inflate(R.layout.alarm_record_item_page, null);
            cacheView.time=(TextView) convertView.findViewById(R.id.alarm_record_time);
            cacheView.img=(ImageView) convertView.findViewById(R.id.alarm_record_img);      
            cacheView.type=(TextView) convertView.findViewById(R.id.alarm_record_type);
            convertView.setTag(cacheView);
        }else{
            cacheView=(CacheView) convertView.getTag();
        }
        Log.e("position", position+":"+list.size()+":"+cacheView.time);
        
        cacheView.time.setText(DateUtil.getTimeStringFormat(list.get(position).time, DateUtil.TIME_FORMAT_YMDHMS));
        cacheView.type.setText(String.valueOf(list.get(position).type));
        if(list.get(position).pic!=null){
        	if(cacheView.imgBitmap!=null)
        		cacheView.imgBitmap.recycle();
        	cacheView.imgBitmap = BitmapFactory.decodeByteArray(list.get(position).pic,0,list.get(position).pic.length);
        	cacheView.img.setImageBitmap(cacheView.imgBitmap);
        }
        
        return convertView;
    }

    private static class CacheView{
        TextView time;
        ImageView img;
        Bitmap imgBitmap;
        TextView type;
    }
}
