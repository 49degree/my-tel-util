package com.newElement.remp.sdk.controls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DataListView extends LinearLayout{
	private Context mContext = null;
	/**
	 * 构造器
	 * @param context
	 */
	public DataListView(Context context) {
		super(context);
		this.mContext =  context;
		init();
	}
	
	/**
	 * 构造器
	 * @param context
	 * @param attrs
	 */
	public DataListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext =  context;
		init();
	}
	
	
	
	
	/**
	 * 初始化
	 */
	private void init() {
		ArrayList<Map<String,View>> valueList = new ArrayList<Map<String,View>>();
		for(int i=0;i<20;i++){
			HashMap<String,View> map = new HashMap<String,View>();
			TextView one = new TextView(mContext);
			one.setHeight(30);
			one.setText("value"+i);
			
			Button two = new Button(mContext);
			two.setText("button"+i);
			two.setWidth(40);
			two.setHeight(20);
			map.put("one", one);
			map.put("two", two);
			valueList.add(map);
		}

		
//		LinearLayout listLayout = new LinearLayout(mContext);
//		this.addView(listLayout);
		
		
		ListView listView = new ListView(mContext);
		listView.setDividerHeight(0);
		DataListViewAdapter simpleAdapter = new DataListViewAdapter(mContext, valueList,new String[]{"one","two"});
		// 增加单击事件
		//listView.setOnItemClickListener(new OnItemClickListenerProd());
		listView.setAdapter(simpleAdapter);
		
		this.addView(listView);
		

		
	}
}
