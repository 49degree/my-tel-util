package com.newElement.remp.sdk.controls;

import java.util.ArrayList;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DataGridViewActivity extends Activity {
	public class Person {
		int    mId;
		String mName;
		int    mAge;
		String mSex;
		String mAddress;
		String mJob;
		String mPosition;
		String mSalary;
		boolean mMarried;
		Person( Person p ) {
			this.mId = p.mId;
			this.mName = p.mName;
			this.mSex = p.mSex;
			this.mAddress = p.mAddress;
			this.mJob = p.mJob;
			this.mPosition = p.mPosition;
			this.mSalary = p.mSalary;
			this.mMarried = p.mMarried;
		}
		Person() {
			
		}
	}
	public Person p =  null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        int numOfRows = 121;
        int rowsPerPage = 30;

    	int mBorderSize = 1;//边框
    	int mBorderClr  = Color.argb(255, 64, 64, 64);//边框颜色
    	int mRowClr     = Color.argb(255, 0, 0, 0);//单行背景
    	int mRowSwapClr = Color.argb(255, 0x22, 0x22, 0x22);//双行背景
    	int mRowTextGravity = Gravity.CENTER;//文本对齐方式
        
        
        //方式一
//    	DataGridViewAdapter pageData = new DataGridViewAdapter();
//        pageData.setMColumnHeaders(this.getDataHeaderMore());//配置表头 为一个二维数组 第二维长度为2,分别表示列的名称和列的宽度
//        pageData.setMDataRemot(false);//配置取数据方式
//        pageData.setMData(this.getCurPageData2(new Integer(0), new Integer(numOfRows)));//配置数据列表
//        
//        pageData.setPageInfo(rowsPerPage,numOfRows);//配置总记录数和每页显示记录数
//        
//        DataGridView dgrid = (DataGridView)findViewById(R.id.dgrid);
//        dgrid.setMPageDataAdapter(pageData);//设置数据适配器
//        
//        //以下代码为设置表格的外观，不设置会有默认值
//        dgrid.setMHeaderHeiht(40);//设置表格头部高度
//        dgrid.setMToolbarPanelHeiht(50);//设置工具栏高度
//        dgrid.setStyleBorder(mBorderSize,mBorderClr);//设置表格边框样式
//        dgrid.setStyleRow(mRowClr,mRowSwapClr,mRowTextGravity);//设置表格样式
//        dgrid.buildDatagrid();
        
        //方式二  翻页需要提供查询方法  如下getCurPageData(Integer startRow,Integer endRow)
//        DataGridViewAdapter pageData2 = new DataGridViewAdapter();
//        
//        pageData2.setMColumnHeaders(this.getDataHeader());//配置表头 为一个二维数组 第二维长度为2,分别表示列的名称和列的宽度
//        pageData2.setMDataRemot(true);//配置取数据方式
//        pageData2.setPageInfo(rowsPerPage,numOfRows);//配置总记录数和每页显示记录数
//        pageData2.setRemotDataOM(this, "getCurPageData");//配置取数据的对象和方法   如下getCurPageData(Integer startRow,Integer endRow)
//        
//        DataGridView dgrid2 = (DataGridView)findViewById(R.id.dgrid2);
//        dgrid.setMToolbarPanelHeiht(60);//设置工具栏高度
//        dgrid2.setMPageDataAdapter(pageData2);//设置数据适配器
//        dgrid2.buildDatagrid();
        
    }
    

	
    /*
     * 根据入参构造TextView
     */
    private TextView createTextView(String value){
        TextView tv = new TextView(this.getApplicationContext());
		//tv.setWidth(width);
		tv.setSingleLine();
		tv.setEllipsize(TruncateAt.END);
		tv.setText(value);
		//tv.setBackgroundColor(Color.RED);
		
		tv.setGravity(Gravity.CENTER);
		return tv;
    	
    }
    
    /*
     * 根据入参构造Button
     */
    private Button createButton(int width,final int id,String text){
        Button bv = new Button(this);
		Button bt = new Button(this); 
		bt.setHeight(10);
		bt.setText(text);
		bt.setGravity(Gravity.CENTER);

        bv.setWidth(width);
        bv.setHeight(20);
        bv.setSingleLine();
        bv.setEllipsize(TruncateAt.END);
        bv.setText(text);
        bv.setGravity(Gravity.CENTER);
        
        bv.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				//do some thing
				Log.d("Onclick","++++++++++++++++++++++++++++++My id is"+id); 
			}
		});
		return bv;
    	
    }
    /**
     * 构建布局
     * @return
     */
    private ViewGroup createViewGroup(){
    	LinearLayout newLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.test_layout,null);
    	return newLayout;
    }
    
    /**
     * 查询数据
     * 查询过程根据具体业务请分别实现，出参和入参请按照该方法
     * @return
     */
    public ArrayList<ArrayList<View>> getCurPageData(Integer startRow,Integer endRow){
	    p = new Person();//示例对象
	    p.mId = 0;
	    p.mName = "HarryLau";
	    p.mAge  = 30;
	    p.mSex  = "Male";
	    p.mAddress = "Nanshan dist. ShenZhen, China";
	    p.mJob  = "IT Professional";
	    p.mPosition = "Programmer";
	    p.mSalary = "100K/M";
	    p.mMarried = true;
    	
    	
    	//查询过程根据具体业务请分别实现，出参和入参请按照该方法
        ArrayList<ArrayList<View>> mData = new ArrayList<ArrayList<View>>(endRow-startRow+1);
        for( int i = startRow; i < endRow; i ++ ) {
        	p.mId = i;
        	ArrayList<View> itemView = new ArrayList<View>();
    		itemView.add(createTextView(String.valueOf(i+1)));
    		itemView.add(createTextView(p.mName));
    		itemView.add(createTextView(String.valueOf(p.mAge)));
    		itemView.add(createTextView(p.mSex));
    		itemView.add(createTextView(p.mAddress));
    		itemView.add(createTextView(p.mJob));
    		itemView.add(createTextView(p.mPosition));
    		itemView.add(createTextView(p.mSalary));
    		itemView.add(createTextView(p.mMarried?"Married":"UnMarried"));
    		mData.add(itemView);
        }
        return mData;
    } 
    
    
    /**
     * 查询数据
     * 查询过程根据具体业务请分别实现，出参和入参请按照该方法
     * @return
     */
    public ArrayList<ArrayList<View>> getCurPageData2(Integer startRow,Integer endRow){
	    p = new Person();//示例对象
	    p.mId = 0;
	    p.mName = "HarryLau";
	    p.mAge  = 30;
	    p.mSex  = "Male";
	    p.mAddress = "Nanshan dist. ShenZhen, China";
	    p.mJob  = "IT Professional";
	    p.mPosition = "Programmer";
	    p.mSalary = "100K/M";
	    p.mMarried = true;
    	
    	
    	//查询过程根据具体业务请分别实现，出参和入参请按照该方法
        ArrayList<ArrayList<View>> mData = new ArrayList<ArrayList<View>>(endRow-startRow+1);
        for( int i = startRow; i < endRow; i ++ ) {
        	p.mId = i;
        	ArrayList<View> itemView = new ArrayList<View>();
    		itemView.add(createTextView(String.valueOf(i+1)));
    		itemView.add(createButton(120,i+1 , "button"));
    		itemView.add(createViewGroup());
    		
    		itemView.add(createTextView(p.mName));
    		itemView.add(createTextView(String.valueOf(p.mAge)));
    		itemView.add(createTextView(p.mSex));
    		itemView.add(createTextView(p.mAddress));
    		itemView.add(createTextView(p.mJob));
    		itemView.add(createTextView(p.mPosition));
    		itemView.add(createTextView(p.mSalary));
    		itemView.add(createTextView(p.mMarried?"Married":"UnMarried"));
    		mData.add(itemView);
        }
        return mData;
    } 
    
    
    public String[][] getDataHeader(){
        String[][] mColumnHeads2 = new String[9][2];
        mColumnHeads2[0]=new String[]{"Id", "40"};
        mColumnHeads2[1]=new String[]{"Name", "120"};
        mColumnHeads2[2]=new String[]{"Age", "40"};
        mColumnHeads2[3]=new String[]{"Sex", "40"};
        
        mColumnHeads2[4]=new String[]{"Address", "140"};
        mColumnHeads2[5]=new String[]{"Job", "100"};
        mColumnHeads2[6]=new String[]{"Position", "100"};
        mColumnHeads2[7]=new String[]{"Salary($/M)", "80"};
        mColumnHeads2[8]=new String[]{"Married", "60"};
        return mColumnHeads2;
    }
    
    
    public String[][] getDataHeaderMore(){
        
        String[][] mColumnHeads2 = new String[11][2];
        mColumnHeads2[0]=new String[]{"Id", "40"};
        
        mColumnHeads2[1]=new String[]{"info button", "120"};
        mColumnHeads2[2]=new String[]{"Test Layout", "300"};
        
        mColumnHeads2[3]=new String[]{"Name", "120"};
        mColumnHeads2[4]=new String[]{"Age1111111111111", "40"};
        mColumnHeads2[5]=new String[]{"Sex", "40"};
        
        mColumnHeads2[6]=new String[]{"Address", "140"};
        mColumnHeads2[7]=new String[]{"Job", "100"};
        mColumnHeads2[8]=new String[]{"Position", "100"};
        mColumnHeads2[9]=new String[]{"Salary($/M)", "80"};
        mColumnHeads2[10]=new String[]{"Married", "60"};
        
        return mColumnHeads2;
    }
    
}