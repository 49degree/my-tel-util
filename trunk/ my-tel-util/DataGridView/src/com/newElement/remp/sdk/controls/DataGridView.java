/**
 * 
 */
package com.newElement.remp.sdk.controls;


import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author Administrator
 *
 */
public class DataGridView extends RelativeLayout{

	
	
	//视图容器
	private HorizontalScrollView mHorizontalContainer = null;//第一层容器
	private LinearLayout  mWrapper = null;
	private ScrollView mTableContainer = null;//表格内容容器
	private TableLayout mTable = null;//表格
	DataGridViewPanel mPanel = null;

	//定义样式变量
	private int mBorderSize = 1;//边框
	private int mBorderClr  = Color.argb(255, 64, 64, 64);//边框颜色
	private int mRowClr     = Color.argb(255, 0, 0, 0);//单行背景
	private int mRowSwapClr = Color.argb(255, 0x22, 0x22, 0x22);//双行背景
	private int mRowTextGravity = Gravity.CENTER;//文本对齐方式
	private int mToolbarPanelHeiht = 40;//工具栏高度
	private int mHeaderHeiht = 20;//表格头部高度

	private TextView mTvPageIndicator = null;//页码
	private Button mBnNextPage = null;//下页按钮
	private Button mBnPrevPage = null;//上页按钮
	
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
	 * 获取工具栏的高度
	 * @return
	 */
	public int getMToolbarPanelHeiht() { 
		return mToolbarPanelHeiht;
	}

	/**
	 * 设置工具栏的高度
	 * @param mCyToolbarPanel 工具栏的高度
	 */
	public void setMToolbarPanelHeiht(int mToolbarPanelHeiht) {
		this.mToolbarPanelHeiht = mToolbarPanelHeiht;
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
		init();
		makeTable(mPageDataAdapter.getMCurPage());
		mTableContainer.setOnTouchListener(new View.OnTouchListener(){
		    public boolean onTouch(View view, MotionEvent event) {
		    	if(view.getHeight()-event.getY()<getMToolbarPanelHeiht()){
		    		mPanel.setVisibility(View.VISIBLE); 
		    	}else{
		    		mPanel.setVisibility(View.INVISIBLE); 

		    	}
		    	return false;
		    }
		});
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
		
		//mHorizontalContainer.setBackgroundColor(mBorderClr);
		//mHorizontalContainer.setPadding(0, 1, 0, 0);
		mHorizontalContainer.setLayoutParams(hlp);
		
		//创建布局
		mWrapper = new LinearLayout(context);
		mWrapper.setOrientation(LinearLayout.VERTICAL);
		mWrapper.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		mWrapper.setBackgroundColor(mBorderClr);
		
		mHorizontalContainer.addView(mWrapper);//增加布局到外层容器
		
		mWrapper.addView(makeColumnHeader());//在布局中增加表头
		//创建数据表格容器
		mTableContainer = new ScrollView(context);
		mTableContainer.setLayoutParams(
				new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mWrapper.addView(mTableContainer);//在布局中增加表格容器
		this.addView(mHorizontalContainer);
		this.addView(makeToolPanel());
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
			for( int i = 0; i < mPageDataAdapter.getMColumnHeaders().length; i ++ ) {
				//创建表头TextView
				String[] c = mPageDataAdapter.getMColumnHeaders()[i];
				TextView tvColumn = new TextView(ctx);
				tvColumn.setText(getColumnHeaderText(i, c[0]));
				tvColumn.setGravity(mRowTextGravity);
				tvColumn.setEllipsize(TruncateAt.END);
				tvColumn.setSingleLine(); 
				
				TableRow rowColumn = new TableRow(ctx);
				TableRow.LayoutParams lp = new TableRow.LayoutParams();
				lp.topMargin = mBorderSize;
				lp.leftMargin = mBorderSize;
				lp.bottomMargin = mBorderSize;
				lp.height = this.mHeaderHeiht;
				lp.width = Integer.parseInt(c[1]);
				if(i==mPageDataAdapter.getMColumnHeaders().length-1){
					lp.rightMargin = mBorderSize;
				}
				rowColumn.setBackgroundColor(mRowSwapClr);
				rowColumn.setLayoutParams(lp);
				rowColumn.addView(tvColumn);
				rowColumn.setGravity(mRowTextGravity);
				header.addView(rowColumn);
			}
			mColumnHeader.addView(header);
		}
		return mColumnHeader;
		
	}

	/**
	 * 构建表格及填充数据
	 * @param page
	 */
	private void makeTable(int mCurPage) {
		if(mCurPage>mPageDataAdapter.getMTotalPages()) return ;
		mTableContainer.removeAllViews();//移除原有视图
		for(int i=0;(mTable!=null&&i<mTable.getChildCount());i++){
			TableRow row = (TableRow)mTable.getChildAt(i);
			for(int j=0;(row!=null&&j<row.getChildCount());j++){
				((TableRow)row.getChildAt(j)).removeAllViews();//移除原有关联视图
			}
			row.removeAllViews();//移除原有关联视图
		}
		
		Context ctx = this.getContext();
		mTable = new TableLayout(ctx);
		mTable.setBackgroundColor(mBorderClr);

		int startPos = (mCurPage - 1)*mPageDataAdapter.getMRowsPerPage();
		int endPos   = startPos + mPageDataAdapter.getMRowsPerPage();
		if(endPos > mPageDataAdapter.getMTotalRows()) {
			endPos = mPageDataAdapter.getMTotalRows();
		}
		boolean swapCr = false;
		if(mPageDataAdapter.getMDataRemot()){//取远程数据
			mPageDataAdapter.initCurPageData(startPos,endPos);//获取当前页数据
			startPos = 0;
			endPos = mPageDataAdapter.getMData().size();
		}
		
		if(mPageDataAdapter.getMData()!=null&&mPageDataAdapter.getMData().size()>=endPos){
			for( int i = startPos; i < endPos; i ++ ) {
				TableRow row = new TableRow(ctx);
				List<View> co  = mPageDataAdapter.getMData().get(i);
				swapCr = (i%2 == 1);
				//创建表格
				for( int c = 0; c < mPageDataAdapter.getMColumnHeaders().length; c++ ) {
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
				    
					if(swapCr) {//设置背景颜色
						rowChild.setBackgroundColor(mRowSwapClr); 
					}else {
						rowChild.setBackgroundColor(mRowClr);
					}
					row.addView(rowChild);
				}
				mTable.addView(row);
			}
		}
		mTableContainer.addView(mTable);
	}


	/**
	 * 构建工具栏
	 * @return
	 */
	private DataGridViewPanel makeToolPanel() {
		Context context = this.getContext();
		mPanel = new DataGridViewPanel(context);
		RelativeLayout.LayoutParams lp = 
			new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, mToolbarPanelHeiht);
		lp.addRule(ALIGN_PARENT_BOTTOM);
		mPanel.setOrientation(LinearLayout.HORIZONTAL);
		mPanel.setPadding(mToolbarPanelHeiht/2, mToolbarPanelHeiht/8, mToolbarPanelHeiht/2, mToolbarPanelHeiht/8);
		
		mBnPrevPage = new Button(context);
		mBnPrevPage.setBackgroundResource(R.drawable.prev_page_button);
		mBnPrevPage.setOnClickListener( new OnClickListener(){
			public void onClick(View v) {
				prevPage(); 
			}
		});
		
		mBnNextPage = new Button(context);
		
		
		mBnNextPage.setOnClickListener( new OnClickListener(){
			
			public void onClick(View v) {
				nextPage();
			}			
		});
		mBnNextPage.setBackgroundResource(R.drawable.next_page_button);

		mTvPageIndicator = new TextView(context);
		int mClr2  = Color.argb(255, 64, 0, 64);//边框颜色
//		mTvPageIndicator.setBackgroundColor(mClr2);
//		mTvPageIndicator.setHeight(mCyToolbarPanelHeiht);
//		mTvPageIndicator.setPadding(0, 0, 0, 0);
		
		mTvPageIndicator.setHeight(mToolbarPanelHeiht);
//		mTvPageIndicator.setGravity(CENTER_IN_PARENT);
		updatePageIndicator();
		mPanel.addView(mBnPrevPage);
		mPanel.addView(mTvPageIndicator);
		mPanel.addView(mBnNextPage);
		mPanel.setLayoutParams(lp);
		this.setPageButtonState();
		return mPanel;
	}
	
	// override-able
	private CharSequence getColumnHeaderText(int iHeader, String headerText) {
		return Html.fromHtml("<b>" + headerText + "</b>");
	}
	
	// override-able
	private void updatePageIndicator() {
		//mTvPageIndicator.setText(mPageDataAdapter.getMCurPage() + "/" + mPageDataAdapter.getMTotalPages());
		mTvPageIndicator.setText(Html.fromHtml("<b >" + mPageDataAdapter.getMCurPage() + "/" + mPageDataAdapter.getMTotalPages() + "</b>"));
	}
	

	/**
	 * 下页操作
	 */
	private void nextPage() {
		if(mPageDataAdapter.getMCurPage()<mPageDataAdapter.getMTotalPages()){
			mPageDataAdapter.setMCurPage(mPageDataAdapter.getMCurPage()+1);
			makeTable(mPageDataAdapter.getMCurPage());
			updatePageIndicator();	
		}
		setPageButtonState();
	}
	
	/**
	 * 上页操作
	 */
	private void prevPage() {
		if(mPageDataAdapter.getMCurPage()>1){
			mPageDataAdapter.setMCurPage(mPageDataAdapter.getMCurPage()-1);
			makeTable(mPageDataAdapter.getMCurPage());
			updatePageIndicator();
		}
		setPageButtonState();
	}
	
	/**
	 * 设置翻页按钮状态
	 */
	private void setPageButtonState(){
		if(mPageDataAdapter.getMCurPage()<=1){
			mBnPrevPage.setEnabled(false);
		}else{
			mBnPrevPage.setEnabled(true);
		}
		if(mPageDataAdapter.getMCurPage() >= mPageDataAdapter.getMTotalPages()) {
			mBnNextPage.setEnabled(false);
	    }else{
	    	mBnNextPage.setEnabled(true);
	    }
	}

	public DataGridViewAdapter getMPageDataAdapter() {
		return mPageDataAdapter;
	}

	public void setMPageDataAdapter(DataGridViewAdapter mPageDataAdapter) {
		this.mPageDataAdapter = mPageDataAdapter;
	}
	


}
