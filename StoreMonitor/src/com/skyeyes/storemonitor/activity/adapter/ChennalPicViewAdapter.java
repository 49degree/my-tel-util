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

import com.skyeyes.base.util.DateUtil;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.activity.MainPageActivity;
import com.skyeyes.storemonitor.activity.VideoPlayActivity;
import com.skyeyes.storemonitor.activity.bean.ChennalPicBean;

public class ChennalPicViewAdapter extends BaseAdapter {
	
    private List<ChennalPicBean> list;
    LayoutInflater inflater;
    Context mContext;
    int mType;//0实时，1历史
    
    String historyStartTime;
    String historyLong;
    
    public ChennalPicViewAdapter(Context context,List<ChennalPicBean> list,int type) {
    	mContext = context;
        this.list=list;
        this.inflater=LayoutInflater.from(context);
        mType =type;
    }
    
    public void setHistoryInfo(String historyStartTime, String historyLong){
    	this.historyStartTime = historyStartTime;
    	this.historyLong = historyLong;
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
        
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
        	cacheView.imgv_img.setBackgroundDrawable(list.get(position).img);
        } else {
        	cacheView.imgv_img.setBackground(list.get(position).img);
        }
        //((ImageView)cacheView.imgv_img.findViewById(R.id.chennal_pic_iv)).setImageBitmap(list.get(position).imgBitmap);
        
        cacheView.imgv_img.setLayoutParams(list.get(position).ivLp);
        final byte chennalId = (byte)(list.get(position).chennalId);
        cacheView.imgv_img.findViewById(R.id.chennal_pic_iv).setOnClickListener(new OnClickListener(){
        	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i("MainPageActivity", "iv.setOnClickListener(new OnClickListener()================");
				Intent it = new Intent(mContext,VideoPlayActivity.class);
				if(mType==0){
					
					it.putExtra("chennalId", chennalId);
					it.putExtra("videoType", 0);
				}else{
					it.putExtra("chennalId", (byte)0);
					it.putExtra("videoType", 1);
					try {
						it.putExtra("startTime", DateUtil.getTimeStringFormat(MainPageActivity.format.parse(historyStartTime),DateUtil.TIME_FORMAT_YMDHMS));
						it.putExtra("videoLong", (short)(Short.parseShort(historyLong)*60));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
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
