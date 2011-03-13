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

    	int mBorderSize = 1;//�߿�
    	int mBorderClr  = Color.argb(255, 64, 64, 64);//�߿���ɫ
    	int mRowClr     = Color.argb(255, 0, 0, 0);//���б���
    	int mRowSwapClr = Color.argb(255, 0x22, 0x22, 0x22);//˫�б���
    	int mRowTextGravity = Gravity.CENTER;//�ı����뷽ʽ
        
        
        //��ʽһ
//    	DataGridViewAdapter pageData = new DataGridViewAdapter();
//        pageData.setMColumnHeaders(this.getDataHeaderMore());//���ñ�ͷ Ϊһ����ά���� �ڶ�ά����Ϊ2,�ֱ��ʾ�е����ƺ��еĿ��
//        pageData.setMDataRemot(false);//����ȡ���ݷ�ʽ
//        pageData.setMData(this.getCurPageData2(new Integer(0), new Integer(numOfRows)));//���������б�
//        
//        pageData.setPageInfo(rowsPerPage,numOfRows);//�����ܼ�¼����ÿҳ��ʾ��¼��
//        
//        DataGridView dgrid = (DataGridView)findViewById(R.id.dgrid);
//        dgrid.setMPageDataAdapter(pageData);//��������������
//        
//        //���´���Ϊ���ñ�����ۣ������û���Ĭ��ֵ
//        dgrid.setMHeaderHeiht(40);//���ñ��ͷ���߶�
//        dgrid.setMToolbarPanelHeiht(50);//���ù������߶�
//        dgrid.setStyleBorder(mBorderSize,mBorderClr);//���ñ��߿���ʽ
//        dgrid.setStyleRow(mRowClr,mRowSwapClr,mRowTextGravity);//���ñ����ʽ
//        dgrid.buildDatagrid();
        
        //��ʽ��  ��ҳ��Ҫ�ṩ��ѯ����  ����getCurPageData(Integer startRow,Integer endRow)
//        DataGridViewAdapter pageData2 = new DataGridViewAdapter();
//        
//        pageData2.setMColumnHeaders(this.getDataHeader());//���ñ�ͷ Ϊһ����ά���� �ڶ�ά����Ϊ2,�ֱ��ʾ�е����ƺ��еĿ��
//        pageData2.setMDataRemot(true);//����ȡ���ݷ�ʽ
//        pageData2.setPageInfo(rowsPerPage,numOfRows);//�����ܼ�¼����ÿҳ��ʾ��¼��
//        pageData2.setRemotDataOM(this, "getCurPageData");//����ȡ���ݵĶ���ͷ���   ����getCurPageData(Integer startRow,Integer endRow)
//        
//        DataGridView dgrid2 = (DataGridView)findViewById(R.id.dgrid2);
//        dgrid.setMToolbarPanelHeiht(60);//���ù������߶�
//        dgrid2.setMPageDataAdapter(pageData2);//��������������
//        dgrid2.buildDatagrid();
        
    }
    

	
    /*
     * ������ι���TextView
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
     * ������ι���Button
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
     * ��������
     * @return
     */
    private ViewGroup createViewGroup(){
    	LinearLayout newLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.test_layout,null);
    	return newLayout;
    }
    
    /**
     * ��ѯ����
     * ��ѯ���̸��ݾ���ҵ����ֱ�ʵ�֣����κ�����밴�ո÷���
     * @return
     */
    public ArrayList<ArrayList<View>> getCurPageData(Integer startRow,Integer endRow){
	    p = new Person();//ʾ������
	    p.mId = 0;
	    p.mName = "HarryLau";
	    p.mAge  = 30;
	    p.mSex  = "Male";
	    p.mAddress = "Nanshan dist. ShenZhen, China";
	    p.mJob  = "IT Professional";
	    p.mPosition = "Programmer";
	    p.mSalary = "100K/M";
	    p.mMarried = true;
    	
    	
    	//��ѯ���̸��ݾ���ҵ����ֱ�ʵ�֣����κ�����밴�ո÷���
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
     * ��ѯ����
     * ��ѯ���̸��ݾ���ҵ����ֱ�ʵ�֣����κ�����밴�ո÷���
     * @return
     */
    public ArrayList<ArrayList<View>> getCurPageData2(Integer startRow,Integer endRow){
	    p = new Person();//ʾ������
	    p.mId = 0;
	    p.mName = "HarryLau";
	    p.mAge  = 30;
	    p.mSex  = "Male";
	    p.mAddress = "Nanshan dist. ShenZhen, China";
	    p.mJob  = "IT Professional";
	    p.mPosition = "Programmer";
	    p.mSalary = "100K/M";
	    p.mMarried = true;
    	
    	
    	//��ѯ���̸��ݾ���ҵ����ֱ�ʵ�֣����κ�����밴�ո÷���
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