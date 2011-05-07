package com.yang.android.tel.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

	
	
	//��ͼ����
	private HorizontalScrollView mHorizontalContainer = null;//��һ������
	private LinearLayout  mWrapper = null;
	public ScrollView mTableContainer = null;//�����������
	public TableLayout mTable = null;//���

	//������ʽ����
	private int mBorderSize = 1;//�߿�
	private int mBorderClr  = Color.argb(255, 64, 64, 64);//�߿���ɫ
	private int mRowClr     = Color.argb(255, 0, 0, 0);//���б���
	private int mRowSwapClr = Color.argb(255, 0x22, 0x22, 0x22);//˫�б���
	private int mRowTextGravity = Gravity.CENTER;//�ı����뷽ʽ
	private int mHeaderHeiht = 20;//���ͷ���߶�
	
	private DataGridViewAdapter mPageDataAdapter = null;//����������
	
	/**
	 * ��ȡ���ͷ���߶�
	 * @return
	 */
	public int getMHeaderHeiht() {
		return mHeaderHeiht;
	}
	/**
	 * ���ñ��ͷ���߶�
	 * @param mCyToolbarPanel ���ͷ���߶�
	 */
	public void setMHeaderHeiht(int mHeaderHeiht) {
		this.mHeaderHeiht = mHeaderHeiht;
	}


	/**
	 * ���ñ߿�
	 * @param borderSize ���
	 * @param borderColor ��ɫ
	 */
	public void setStyleBorder(int borderSize, int borderColor) {
		mBorderSize = borderSize;
		mBorderClr  = borderColor;
	}
	
	/**
	 * ���ñ�����ݵ���ʽ
	 * @param rowClr �����б���
	 * @param rowSwapClr ˫���б���
	 * @param rowTextGravity ���з�ʽ
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
	 * ������
	 * @param context
	 */
	public DataGridView(Context context) {
		super(context);
	}
	
	/**
	 * ������
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
	 * ��ʼ��
	 */
	private void init() {
		Context context = this.getContext();
		//��������
		mHorizontalContainer = new HorizontalScrollView(context);
		HorizontalScrollView.LayoutParams hlp = 
			new HorizontalScrollView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		//mHorizontalContainer.setBackgroundColor(mBorderClr);
		//mHorizontalContainer.setPadding(0, 1, 0, 0);
		mHorizontalContainer.setLayoutParams(hlp);
		
		//��������
		mWrapper = new LinearLayout(context);
		mWrapper.setOrientation(LinearLayout.VERTICAL);
		mWrapper.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		mWrapper.setBackgroundColor(mBorderClr);
		
		mHorizontalContainer.addView(mWrapper);//���Ӳ��ֵ��������
		
		mWrapper.addView(makeColumnHeader());//�ڲ��������ӱ�ͷ
		//�������ݱ������
		mTableContainer = new ScrollView(context);
		mTableContainer.setLayoutParams(
				new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mWrapper.addView(mTableContainer);//�ڲ��������ӱ������
		this.addView(mHorizontalContainer);
	}

	/**
	 * ������ͷ
	 * @return
	 */
	private TableLayout makeColumnHeader() {
		Context ctx = this.getContext();
		TableLayout mColumnHeader = new TableLayout(ctx);
		mColumnHeader.setBackgroundColor(mBorderClr);
		TableRow header = new TableRow(ctx);
		if(mPageDataAdapter.getMColumnHeaders()!=null){
			for( int i = 0; i < mPageDataAdapter.getMColumnHeaders().length; i ++ ) {
				//������ͷTextView
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

	int choiceId = 0;
	/**
	 * ��������������
	 * @param page
	 */
	private void makeTable() {
		mTableContainer.removeAllViews();//�Ƴ�ԭ����ͼ
		
//		for(int i=0;(mTable!=null&&i<mTable.getChildCount());i++){
//			TableRow row = (TableRow)mTable.getChildAt(i);
//			for(int j=0;(row!=null&&j<row.getChildCount());j++){
//				((TableRow)row.getChildAt(j)).removeAllViews();//�Ƴ�ԭ�й�����ͼ
//			}
//			row.removeAllViews();//�Ƴ�ԭ�й�����ͼ
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
				//�������
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
				    
					if(swapCr) {//���ñ�����ɫ
						rowChild.setBackgroundColor(mRowSwapClr); 
					}else {
						rowChild.setBackgroundColor(mRowClr);
					}
					row.setGravity(this.mRowTextGravity);
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

