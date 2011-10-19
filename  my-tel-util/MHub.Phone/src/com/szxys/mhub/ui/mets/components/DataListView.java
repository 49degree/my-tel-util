package com.szxys.mhub.ui.mets.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;

public class DataListView extends LinearLayout{
	private Context mContext = null;
	/**
	 * 构造器
	 * @param context
	 */
	public DataListView(Context context) {
		super(context);
		this.mContext =  context;
		//init();
	}
	
	/**
	 * 构造器
	 * @param context
	 * @param attrs
	 */
	public DataListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext =  context;
		//init();
	}
	
	
	public void setAdapter(DataListViewAdapter simpleAdapter){
		ListView listView = new ListView(mContext);
		listView.setDividerHeight(0);
		
		listView.setAdapter(simpleAdapter);
		this.addView(listView);
	}
	
	int choiceId = 0;
	/**
	 * 初始化
	 */
	public void init() {
		ArrayList<Map<String,View>> valueList = new ArrayList<Map<String,View>>();
		for(int i=0;i<20;i++){
			HashMap<String,View> map = new HashMap<String,View>();

			
			
			RadioButton one = new RadioButton(mContext);
			one.setId(i);

			
//			one.setOnClickListener(new OnClickListener(){
//				
//				
//				public void onClick(View v){
//					Log.d("test",choiceId+":"+v.getId());
//					if(choiceId!=v.getId()){
//						choiceId = v.getId();
//						RadioButton tempBtn = (RadioButton)findViewById(choiceId);
//						tempBtn.setChecked(false);
//						((RadioButton)v).setChecked(true);
//					}
//				}
//			});
			
			Button two = new Button(mContext);
			two.setText("button"+i);
			two.setHeight(30);
			map.put("one", one);
			map.put("two", two);
			valueList.add(map);
		}

		
		//LinearLayout listLayout = new LinearLayout(mContext);
		
		ListView listView = new ListView(mContext);
		listView.setDividerHeight(0);
		DataListViewAdapter simpleAdapter = new DataListViewAdapter(mContext, valueList,new String[][]{{"one","80"},{"two","100"}});
		listView.setAdapter(simpleAdapter);
		listView.setBackgroundColor(Color.BLUE);
		// 增加单击事件
		//listView.setOnItemClickListener(new OnItemClickListenerProd());
		listView.setAdapter(simpleAdapter);
		
		//listLayout.addView(listView);
		this.addView(listView);
	}
	
}
