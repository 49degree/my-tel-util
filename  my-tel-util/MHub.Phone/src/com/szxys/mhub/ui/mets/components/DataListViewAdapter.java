package com.szxys.mhub.ui.mets.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TableLayout;
import android.widget.TableRow;

public class DataListViewAdapter extends BaseAdapter {
	private ViewBinder mViewBinder;
	private List<? extends Map<String, View>> mData;
	private LayoutInflater mInflater;
	private Context mContext = null;
	// private SimpleFilter mFilter;
	private String[][] mKey = null;

	private HashMap<String, View> mBinderView = null;

	// 定义样式变量
	private int mBorderSize = 1;// 边框
	private int mBorderClr = Color.argb(255, 64, 64, 64);// 边框颜色
	private int mRowClr = Color.argb(255, 0, 0, 0);// 单行背景
	private int mRowSwapClr = Color.argb(255, 0x22, 0x22, 0x22);// 双行背景
	private int mRowTextGravity = Gravity.CENTER;// 文本对齐方式

	public DataListViewAdapter(Context context,
			List<? extends Map<String, View>> data, String[][] key) {
		mContext = context;
		mData = data;
		mKey = key;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mBinderView = new HashMap<String, View>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private int checkStep = 0;
	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub\
//		if(checkStep!=-1){
//			if(position ==0&&checkStep==0){
//				checkStep++;
//			}else if(position ==0&&checkStep==1){
//				checkStep=-1;
//			}
//			return new View(mContext);
//		}
		
		//convertView = new View(mContext);  //解决办法： 每次都重新获取View


		// 创建行视图
//		LinearLayout itemLayout = new LinearLayout(mContext);
		AbsListView.LayoutParams itemLP = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.FILL_PARENT);
//		itemLayout.setLayoutParams(itemLP);
		// 视图中的表格
		TableLayout table = new TableLayout(mContext);
		table.setLayoutParams(itemLP);
		table.setBackgroundColor(Color.RED);

		// 创建表中的行
		TableRow row = new TableRow(mContext);
		int dataSize = mData.size();
		for (int i = 0; mKey != null && i < mKey.length; i++) {
			// 单元格的布局
			TableRow.LayoutParams lp = new TableRow.LayoutParams(Integer.parseInt(mKey[i][1]),
					LinearLayout.LayoutParams.FILL_PARENT);
			lp.topMargin = mBorderSize;
			lp.leftMargin = mBorderSize;
			if(i==mKey.length-1){
				lp.rightMargin = mBorderSize;
			}
			if(position == dataSize-1){
				lp.bottomMargin = mBorderSize;
			}
			
			// 创建单元格，并给单元格加入VIEW
			TableRow rowColumn = new TableRow(mContext);
			rowColumn.setBackgroundColor(mRowClr);
			rowColumn.setLayoutParams(lp);
			rowColumn.setGravity(mRowTextGravity);

			if (mData.get(position).get(mKey[i][0]).getParent() != null) {
				((TableRow) mData.get(position).get(mKey[i][0]).getParent()).removeView(mData.get(position).get(mKey[i][0]));
			}
			
			if(mData.get(position).get(mKey[i][0]) instanceof RadioButton){
				mData.get(position).get(mKey[i][0]).setOnClickListener(new OnClickListener(){
					public void onClick(View v){
						Log.d("test",checkStep+":"+v.getId());
						if(checkStep!=v.getId()){
							checkStep = v.getId();
							if(parent!=null){
								Log.d("test","checkStep");
								RadioButton tempBtn = (RadioButton)parent.findViewById(checkStep);
								if(tempBtn!=null){
									Log.d("test","checkStep22");
									tempBtn.setChecked(false);
								}
								
								((RadioButton)v).setChecked(true);
							}
	
						}
					}
				});
			}
			
			
			rowColumn.addView(mData.get(position).get(mKey[i][0]));
			row.addView(rowColumn);
		}
		table.addView(row);
		//itemLayout.addView(table);
		return table;

	}
}
