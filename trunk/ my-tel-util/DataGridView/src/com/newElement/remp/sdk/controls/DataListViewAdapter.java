package com.newElement.remp.sdk.controls;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.SimpleAdapter.ViewBinder;

public class DataListViewAdapter extends BaseAdapter{
    private ViewBinder mViewBinder;
    private List<? extends Map<String, View>> mData;
    private LayoutInflater mInflater;
    private Context mContext = null;
    //private SimpleFilter mFilter;
    private String[] mKey = null;
    
    
    
	//定义样式变量
	private int mBorderSize = 1;//边框
	private int mBorderClr  = Color.argb(255, 64, 64, 64);//边框颜色
	private int mRowClr     = Color.argb(255, 0, 0, 0);//单行背景
	private int mRowSwapClr = Color.argb(255, 0x22, 0x22, 0x22);//双行背景
	private int mRowTextGravity = Gravity.CENTER;//文本对齐方式
	    
    public DataListViewAdapter(Context context, List<? extends Map<String, View>> data,String[] key) {
    	mContext = context;
        mData = data;
        mKey = key;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		 return mData.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
    /**
     * @see android.widget.Adapter#getView(int, View, ViewGroup)
     */
	private int visitFlag = 0;
    public View getView(int position, View convertView, ViewGroup parent) {
    	//特殊处理，防止列表绘制不完全
    	if(visitFlag!=-1){
    		if(visitFlag<1&&position==0){
    			visitFlag++;
    		}else if(visitFlag==1&&position==0){
    			visitFlag = -1;
    		}
    		return new View(mContext);
    	}

    	
		// 创建行视图
		LinearLayout itemLayout = new LinearLayout(mContext);
		AbsListView.LayoutParams itemLP = new AbsListView.LayoutParams(parent
				.getWidth(), AbsListView.LayoutParams.FILL_PARENT);
		itemLayout.setLayoutParams(itemLP);
		// 视图中的表格
		TableLayout table = new TableLayout(mContext);
		TableLayout.LayoutParams tableLP = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.FILL_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
		table.setLayoutParams(tableLP);
		table.setBackgroundColor(Color.RED);
		// 创建表中的行
		TableRow row = new TableRow(mContext);
		// 单元格的布局
		TableRow.LayoutParams lp = new TableRow.LayoutParams(
				(parent.getWidth()/ mKey.length) - 1,LinearLayout.LayoutParams.FILL_PARENT);
		lp.topMargin = mBorderSize;
		lp.leftMargin = mBorderSize;
		if(mData.size()==(position+1)){
			lp.bottomMargin = mBorderSize;
		}
		
		for (int i = 0; mKey != null && i < mKey.length; i++) {
			// 创建单元格，并给单元格加入VIEW
			TableRow rowColumn = new TableRow(mContext);
			rowColumn.setBackgroundColor(mRowClr);
			rowColumn.setLayoutParams(lp);
			rowColumn.setGravity(mRowTextGravity);
			if (mData.get(position).get(mKey[i]).getParent() != null) {
				((TableRow) mData.get(position).get(mKey[i]).getParent()).removeView(mData.get(position).get(mKey[i]));
			}
		
			rowColumn.addView(mData.get(position).get(mKey[i]));
			row.addView(rowColumn);
		}
		
		
		table.addView(row);
		itemLayout.addView(table);
		System.out.println(convertView);
		return itemLayout;
		
    	
    	
        //return createViewFromResource(position, convertView, parent);
    }
}
