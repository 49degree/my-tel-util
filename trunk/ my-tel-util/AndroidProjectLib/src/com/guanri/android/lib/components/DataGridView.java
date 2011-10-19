/**
 * 
 */
package com.guanri.android.lib.components;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.guanri.android.lib.context.MainApplication;

/**
 * @author Administrator
 *
 */
public class DataGridView extends RelativeLayout{

	
	
	//视图容器
	private HorizontalScrollView mHorizontalContainer = null;//第一层容器
	private LinearLayout  mWrapper = null;
	public ScrollView mTableContainer = null;//表格内容容器
	public TableLayout mTable = null;//表格

	//定义样式变量
	private int mBorderSize = 1;//边框
	private int mBackgroundColor = Color.WHITE;//背景颜色
	private int mTextColor = Color.BLACK;//字体颜色
	private int mBorderClr  = Color.argb(255, 64, 64, 64);//边框颜色
	private int mRowClr     = Color.argb(255, 0, 0, 0);//单行背景
	private int mRowSwapClr = Color.argb(255, 0x22, 0x22, 0x22);//双行背景
	private int mRowTextGravity = Gravity.CENTER;//文本对齐方式
	private int mHeaderHeiht = 20;//表格头部高度
	
	
	private DataGridViewAdapter mPageDataAdapter = null;//数据适配器
	
	/**
	 * 获取表格头部高度
	 * @return
	 */
	public int getMHeaderHeiht() {
		return mHeaderHeiht;
	}
	/**
	 * 设置表格头部高度
	 * @param mCyToolbarPanel 表格头部高度
	 */
	public void setMHeaderHeiht(int mHeaderHeiht) {
		this.mHeaderHeiht = mHeaderHeiht;
	}


	/**
	 * 配置边框
	 * @param borderSize 宽度
	 * @param borderColor 颜色
	 */
	public void setStyleBorder(int borderSize, int borderColor) {
		mBorderSize = borderSize;
		mBorderClr  = borderColor;
	}
	
	/**
	 * 配置表格数据的样式
	 * @param rowClr 单数行背景
	 * @param rowSwapClr 双数行背景
	 * @param rowTextGravity 排列方式
	 */
	public void setStyleRow(int rowClr, int rowSwapClr, int rowTextGravity) {
		this.mRowClr = rowClr;
		this.mRowSwapClr = rowSwapClr;
		this.mRowTextGravity = rowTextGravity;
	}
	
	public DataGridViewAdapter getMPageDataAdapter() {
		return mPageDataAdapter;
	}

	public void setMPageDataAdapter(DataGridViewAdapter mPageDataAdapter) {
		this.mPageDataAdapter = mPageDataAdapter;
	}
	
	/**
	 * 构造器
	 * @param context
	 */
	public DataGridView(Context context) {
		super(context);
	}
	
	/**
	 * 构造器
	 * @param context
	 * @param attrs
	 */
	public DataGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void resetContent() {
		this.removeAllViews();
	}
	
	public void buildDatagrid() {
		Log.d("test","buildDatagrid");
		init();
		makeTable();
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		Context context = this.getContext();
		//创建容器
		mHorizontalContainer = new HorizontalScrollView(context);
		HorizontalScrollView.LayoutParams hlp = 
			new HorizontalScrollView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		hlp.gravity = Gravity.CENTER;
		mHorizontalContainer.setBackgroundColor(mBackgroundColor);
		mHorizontalContainer.setPadding(mBorderSize, mBorderSize, mBorderSize, mBorderSize);
		mHorizontalContainer.setLayoutParams(hlp);
		
		
		//创建布局
		mWrapper = new LinearLayout(context);
		mWrapper.setOrientation(LinearLayout.VERTICAL);
		mWrapper.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mWrapper.setGravity(Gravity.CENTER);
		mHorizontalContainer.addView(mWrapper);//增加布局到外层容器
		
		mWrapper.addView(makeColumnHeader());//在布局中增加表头
		//创建数据表格容器
		mTableContainer = new ScrollView(context);
		mTableContainer.setLayoutParams(
				new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mWrapper.addView(mTableContainer);//在布局中增加表格容器
		this.addView(mHorizontalContainer);
	}

	/**
	 * 构建表头
	 * @return
	 */
	private TableLayout makeColumnHeader() {
		Context ctx = this.getContext();
		TableLayout mColumnHeader = new TableLayout(ctx);
		mColumnHeader.setBackgroundColor(mBorderClr);
		TableRow header = new TableRow(ctx);
		if(mPageDataAdapter.getMColumnHeaders()!=null){
			int screenWidth = MainApplication.getInstance().getScreenWidth();
			int headerSize = mPageDataAdapter.getMColumnHeaders().length;
			int allLength = 0;
			for( int i = 0; i < headerSize; i ++ ) {
				String[] c = mPageDataAdapter.getMColumnHeaders()[i];
				allLength+=Integer.parseInt(c[1]);
			}
			if(allLength+2*mBorderSize<screenWidth){
				for( int i = 0; i < headerSize; i ++ ) {
					String[] c = mPageDataAdapter.getMColumnHeaders()[i];
					c[1] = String.valueOf(new Float((screenWidth-2*mBorderSize)*(Float.parseFloat(c[1])/allLength)).intValue());
				}
			}
			
			for( int i = 0; i < mPageDataAdapter.getMColumnHeaders().length; i ++ ) {
				//创建表头TextView
				String[] c = mPageDataAdapter.getMColumnHeaders()[i];
				TextView tvColumn = new TextView(ctx);
				tvColumn.setText(getColumnHeaderText(i, c[0]));
				tvColumn.setGravity(mRowTextGravity);
				tvColumn.setEllipsize(TruncateAt.END);
				tvColumn.setSingleLine(); 
				tvColumn.setTextColor(mTextColor);
				
				TableRow rowColumn = new TableRow(ctx);
				TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.FILL_PARENT);
				lp.topMargin = mBorderSize;
				lp.leftMargin = mBorderSize;
				lp.bottomMargin = mBorderSize;
				lp.height = this.mHeaderHeiht;
				lp.width = Integer.parseInt(c[1])-1;
				if(i==mPageDataAdapter.getMColumnHeaders().length-1){
					lp.rightMargin = mBorderSize;
					lp.width = Integer.parseInt(c[1])+1;
				}
				rowColumn.setBackgroundColor(mBackgroundColor);
				rowColumn.setLayoutParams(lp);
				rowColumn.addView(tvColumn);
				rowColumn.setGravity(mRowTextGravity);
				header.addView(rowColumn);
			}
			mColumnHeader.addView(header);
		}
		return mColumnHeader;
		
	}

	int choiceId = 0;
	/**
	 * 构建表格及填充数据
	 * @param page
	 */
	private void makeTable() {
		mTableContainer.removeAllViews();//移除原有视图
		
//		for(int i=0;(mTable!=null&&i<mTable.getChildCount());i++){
//			TableRow row = (TableRow)mTable.getChildAt(i);
//			for(int j=0;(row!=null&&j<row.getChildCount());j++){
//				((TableRow)row.getChildAt(j)).removeAllViews();//移除原有关联视图
//			}
//			row.removeAllViews();//移除原有关联视图
//		}
	
		Context ctx = this.getContext();
		mTable = new TableLayout(ctx);
		mTable.setBackgroundColor(mBorderClr);


		boolean swapCr = false;
		if(mPageDataAdapter.getMData()!=null){
			int dataLength = mPageDataAdapter.getMData().size();
			int cellNum = mPageDataAdapter.getMColumnHeaders().length;
			for( int i = 0; i < dataLength; i ++ ) {
				TableRow row = new TableRow(ctx);
				List<View> co  = mPageDataAdapter.getMData().get(i);
				swapCr = (i%2 == 1);
				//创建表格
				for( int c = 0; c < cellNum; c++ ) {
					TableRow rowChild = new TableRow(ctx);
					View v = co.get(c);
					rowChild.addView(v);
					TableRow.LayoutParams mp = new TableRow.LayoutParams();
					mp.leftMargin = mBorderSize;
					mp.bottomMargin = mBorderSize;
					mp.height = TableRow.LayoutParams.FILL_PARENT;
				    mp.width = Integer.parseInt(mPageDataAdapter.getMColumnHeaders()[c][1]);
				    //mp.gravity = mRowTextGravity;
				    rowChild.setLayoutParams(mp);
				    
				    rowChild.setGravity(this.mRowTextGravity);
				    
//					if(swapCr) {//设置背景颜色
//						rowChild.setBackgroundColor(mRowSwapClr); 
//					}else {
//						rowChild.setBackgroundColor(mRowClr);
//						
//					}
					rowChild.setBackgroundColor(Color.WHITE);
					row.addView(rowChild);
				}
				mTable.addView(row);
			}
		}
		mTableContainer.addView(mTable);
	}


	// override-able
	private CharSequence getColumnHeaderText(int iHeader, String headerText) {
		return Html.fromHtml("<b>" + headerText + "</b>");
	}


}

