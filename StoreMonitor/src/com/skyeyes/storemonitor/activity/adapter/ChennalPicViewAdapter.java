package com.skyeyes.storemonitor.activity.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.activity.VideoPlayActivity;
import com.skyeyes.storemonitor.activity.bean.ChennalPicBean;

public class ChennalPicViewAdapter extends BaseAdapter {
    private List<ChennalPicBean> list;
    LayoutInflater inflater;
    Context mContext;
    public ChennalPicViewAdapter(Context context,List<ChennalPicBean> list) {
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
            convertView=inflater.inflate(R.layout.chennal_pic_item_page, null);
            cacheView.tv_des=(TextView) convertView.findViewById(R.id.chennal_pic_tv);
            cacheView.imgv_img=(LinearLayout) convertView.findViewById(R.id.chennal_pic_ll);
            convertView.setTag(cacheView);
        }else{
            cacheView=(CacheView) convertView.getTag();
        }
        cacheView.tv_des.setText(list.get(position).des);
        cacheView.imgv_img.setBackgroundDrawable(list.get(position).img);
        cacheView.imgv_img.setLayoutParams(list.get(position).ivLp);
        final byte chennalId = (byte)(list.get(position).chennalId);
        cacheView.imgv_img.findViewById(R.id.chennal_pic_iv).setOnClickListener(new OnClickListener(){
        	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i("MainPageActivity", "iv.setOnClickListener(new OnClickListener()================");
				Intent it = new Intent(mContext,VideoPlayActivity.class);
				it.putExtra("chennalId", chennalId);
				mContext.startActivity(it);
			}
		});
        return convertView;
    }

    private static class CacheView{
        TextView tv_des;
        LinearLayout imgv_img;
    }
}
