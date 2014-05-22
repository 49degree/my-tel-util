package com.skyeyes.storemonitor.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.activity.bean.ChennalPicBean;

public class ChennalPicViewAdapter extends BaseAdapter {
    private List<ChennalPicBean> list;
    LayoutInflater inflater;
    public ChennalPicViewAdapter(Context context,List<ChennalPicBean> list) {
        this.list=list;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position%list.size());
    }

    @Override
    public long getItemId(int position) {
        return position%list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CacheView cacheView;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.chennal_pic_item_page, null);
            cacheView=new CacheView();
            cacheView.tv_des=(TextView) convertView.findViewById(R.id.chennal_pic_tv);
            cacheView.imgv_img=(ImageView) convertView.findViewById(R.id.chennal_pic_iv);//list.get(position%list.size()).img;
            convertView.setTag(cacheView);
        }else{
            cacheView=(CacheView) convertView.getTag();
        }
        cacheView.tv_des.setText(list.get(position%list.size()).des);
        cacheView.imgv_img.setImageDrawable(list.get(position%list.size()).img.getBackground());

        return convertView;
    }

    private static class CacheView{
        TextView tv_des;
        ImageView imgv_img;
    }
}
