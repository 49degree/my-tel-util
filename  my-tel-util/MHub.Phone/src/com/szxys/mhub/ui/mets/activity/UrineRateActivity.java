package com.szxys.mhub.ui.mets.activity;

import java.text.DecimalFormat;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.bean.UFRrecord;
import com.szxys.mhub.subsystem.mets.dao.UFRecordDao;
import com.szxys.mhub.ui.mets.components.CurveChart;

public class UrineRateActivity extends Activity implements OnClickListener{
	private final int DRAW_BG = -2130507570;//绘图区域背景颜色
	private int recordId = -1;//记录ID
	private LinearLayout mLinPic;//尿流率曲线
	private LinearLayout mLinCount;//尿流率统计
	private Button mLinPicBtn;//尿流率曲线按钮
	private Button mLinCountBtn;//尿流率统计按钮
	private CurveChart curveChart = null;
	private TextView returnText = null;//返回
	
	private TextView maxRateValue = null;//最大尿流率
	private TextView averRateValue = null;//平均尿流率
	private TextView urineTimeValue = null;//排尿时间
	private TextView urinaryTimeValue = null;//尿流时间
	private TextView maxTimeValue = null;//达峰时间
	private TextView urineValueValue = null;//尿流量
	private TextView twoSecRateValue = null;//2秒尿流率

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mets_urine_rate);
		
		Bundle extras=getIntent().getExtras(); //获取打开参数
		if(extras==null)
			return ;
		
		recordId = extras.getInt(UrineRecordActivity.PARAM_RECORD_ID);//获取记录ID
		mLinPic = (LinearLayout)findViewById(R.id.mets_urine_rate_pic_layout);
		mLinCount = (LinearLayout)findViewById(R.id.mets_urine_rate_count_layout);
		
		mLinPicBtn = (Button)findViewById(R.id.mets_urine_rate_pic_btn);
		mLinCountBtn = (Button)findViewById(R.id.mets_urine_rate_count_btn);
		
		//returnText = (TextView)findViewById(R.id.mets_urine_rate_return_text);
		
		mLinPicBtn.setOnClickListener(this);
		mLinCountBtn.setOnClickListener(this);
		//returnText.setOnClickListener(this);
		
		mLinPic.setVisibility(View.VISIBLE);
		mLinCount.setVisibility(View.INVISIBLE);
		mLinPicBtn.setSelected(true);
		mLinCountBtn.setSelected(false);
		
		//以下为绘图数据 ，由于平台另外提供绘图控件，现在先只是把数据整理出来
		curveChart = (CurveChart)findViewById(R.id.mets_urine_rate_pic_draw);
		
		
		curveChart.setBackgroundColor(DRAW_BG);
		curveChart.setHasGridView(true);
		
        maxRateValue = (TextView)findViewById(R.id.mets_urine_rate_max_rate_value);//最大尿流率
        averRateValue = (TextView)findViewById(R.id.mets_urine_rate_aver_rate_value);//平均尿流率
        urineTimeValue = (TextView)findViewById(R.id.mets_urine_rate_urine_time_value);//排尿时间
        urinaryTimeValue = (TextView)findViewById(R.id.mets_urine_rate_urinary_time_value);//尿流时间
        maxTimeValue = (TextView)findViewById(R.id.mets_urine_rate_max_time_value);//达峰时间
        urineValueValue = (TextView)findViewById(R.id.mets_urine_rate_urine_value_value);//尿流量
        twoSecRateValue = (TextView)findViewById(R.id.mets_urine_rate_2s_rate_value);//2秒尿流率
        
        selectPicBtn();
        initChart();//initChart()
        initCount();//展示统计信息

        


		
	}
	

	/**
	 * 展示统计信息
	 */
	public void initCount(){
        maxRateValue.setText("20.1");//最大尿流率
        averRateValue.setText("2.53");//平均尿流率
        urineTimeValue.setText("20.1");//排尿时间
        urinaryTimeValue.setText("20.1");//尿流时间
        maxTimeValue.setText("20.1");//达峰时间
        urineValueValue.setText("736");//尿流量
        twoSecRateValue.setText("290");//2秒尿流率
	}
	
	/**
	 * 绘图
	 */
	public void initChart(){
	    float[] datax0={ 1.55f,2.07f,2.55f,3.175f,4.075f,5.075f,6.1f,7.275f,8.34f,9.11f,10.125f,11.48f,12.55f,13.475f,14.575f,15.45f,15.95f,16.475f,16.85f,16.975f,17.175f,17.2f,17.175f,17.275f,17.25f,17.575f,18.2f,18.65f,19.45f,20.35f,20.875f,21.75f,23.225f,24.95f,26.4f,27.225f,28.075f,29.05f,29.3f,29.05f,29.125f,29.4f,29.275f,28.825f,28.65f,28.625f,28.35f,28.05f,28.275f,28.925f,29.1f,28.825f,28.65f,28.2f,27.425f,27.325f,27.725f,27.7f,27.625f,27.825f,28.025f,28.2f,28.55f,29.025f,29.525f,30,30.55f,31.3375f,31.9f,32.2875f,32.9f,33.65f,34.25f,34.35f,34.225f,33.975f,33.25f,32.6f,32.7f,33.125f,33.325f,33.05f,32.65f,32.825f,33.175f,33.225f,33.5f,33.85f,33.4f,32.4f,31.975f,32.9f,34.325f,35.15f,35.675f,36.25f,36.825f,37.45f,38.4f,39.3f,39.15f,38,37.2f,37f,36.575f,36.1f,35.775f,35.575f,35.7f,35.925f,36.025f,35.8f,34.975f,34.45f,35.3f,36.6f,37.1125f,36.8438f,36.075f,35.3688f,35.275f,35.4f,35.575f,36.125f,35.95f,34.75f,34.225f,34.5938f,34.9875f,35.2938f,35.575f,35.75f,35.575f,34.625f,33.625f,33.1f,32.2875f,31.25f,30.4125f,30f,29.85f,29.7f,29.825f,30.575f,31.55f,31.925f,31.8f,31.55f,31.175f,30.225f,28.55f,27.075f,26.3f,24.8f,21.875f,19.15f,16.875f,14.25f,11.5f,9.2f,7.6f,5.975f,3.65f,5.55f,5.6f};
	    float[] datax1= new float[datax0.length];
	    for(int i=0;i<datax0.length;i++){
	    	datax1[i]=0.5f*i;
	    }
	    
	    //以上测试数据
	    if (recordId>0) {
	    	UFRrecord objUFR=UFRecordDao.getUfRrecordById(String.valueOf(recordId));
	    	if (objUFR!=null) {//尿量和尿流率数据是String由","分隔
				datax0=getChartFloat(objUFR.getC_QuantityData());
				datax1=getChartFloat(objUFR.getC_RateData());
				
			}
		}
	    
	    //获取尿流率峰值数据
		float max = 0f;
		int maxtime = 0;
	    for(int i=0;i<datax0.length;i++){
	    	if(max<datax0[i]){
	    		max=datax0[i];
	    		maxtime = i;
	    	}
	    }
	    curveChart.addCurveLine(1,datax0,Color.GREEN,max);//曲线1
	    curveChart.addCurveLine(2,datax1,Color.RED,0.5f*datax0.length);//曲线2
	    setChartStyle(datax0.length,max,0.5f*datax0.length,maxtime);
        //curveChart.postInvalidate();
	}
	
	@Override
	public void onClick(View v){
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.mets_urine_count_return_btn://返回按钮事件
			finish();
			break;
		case R.id.mets_urine_rate_pic_btn://尿流率曲线
			selectPicBtn();
			break;		
		case R.id.mets_urine_rate_count_btn://尿流率统计按钮
			selectedCountBtn();
			break;				
		default :
			break;
		}
	}
	
	
	protected void selectPicBtn(){
		mLinPic.setVisibility(View.VISIBLE);
		mLinCount.setVisibility(View.INVISIBLE);
		mLinPicBtn.setSelected(true);
		mLinCountBtn.setSelected(false);
	}
	
	private void selectedCountBtn(){
		mLinPic.setVisibility(View.INVISIBLE);
		mLinCount.setVisibility(View.VISIBLE);
		mLinPicBtn.setSelected(false);
		mLinCountBtn.setSelected(true);
	}	

	/**
	 * @param pointNum
	 * @param max1 尿流率最大值
	 * @param max2 尿量最大值
	 * @param maxTime尿流率最大值时间
	 */
	private void setChartStyle(int pointNum,float max1,float max2,int maxTime) {
		//curveChart.setXAxisRange(0f, 36f);
		float times = (pointNum*1.0f)/6;
		float max = max1>max2?max1:max2;
		float yAxis = max1/6;
		float yTAxis = max2/6;
		float axis = max/6;
		DecimalFormat   formator   =   new   DecimalFormat(".#"); 
		curveChart.setXYMaxMinValue(0, pointNum, 0, max);
		
		for(int i=0;i<6;i++){
			if((pointNum-i*times*1.0f)<times/2){//如果最后一个刻度与末尾靠的太近则不进行绘制
				break;
			}
			curveChart.addXAxisMarkLabel(i*times*1.0f, i*times*1.0f,1, 2, Color.GRAY, formator.format((i*times*1.0f)/10), Color.GRAY, 8);
			//i+=times;
		}
		curveChart.addXAxisMarkLabel(pointNum*1.0f, pointNum*1.0f,1, 2, Color.GRAY, formator.format((pointNum*1.0f)/10), Color.GRAY, 8);

		for(int i=0;i<6;){
			curveChart.addYAxisMarkLabel(i*axis, 1, 2, Color.GRAY, formator.format((i*yAxis*1.0f)), Color.GRAY, 8);
			i++;
		}
		curveChart.addYAxisMarkLabel(max, 1, 2, Color.GRAY, formator.format(max1), Color.GRAY, 8);
		for(int i=0;i<6;){
			curveChart.addYTAxisMarkLabel(i*axis, 1, 2, Color.GRAY, formator.format((i*yTAxis*1.0f)), Color.GRAY, 8);
			i++;
		}
		
		curveChart.addYTLine(maxTime);//尿流率峰值线
		
		curveChart.addYTAxisMarkLabel(max, 1, 2, Color.GRAY, formator.format(max2), Color.GRAY, 8);
        curveChart.addDrawText(0.5f, max-max/20, 20, 30, "(mL/S)"+getString(R.string.mets_urine_rate_top_info)+"--", 10, Color.GREEN, false, false);
        curveChart.addDrawText(pointNum-30, max-max/20, 20, 30, getString(R.string.mets_urine_record_header_out)+"(mL)"+" --", 10, Color.RED, false, false);

        curveChart.addDrawText(pointNum-20, 0, 20, 30, getString(R.string.mets_urine_rate_continu_time)+"(S)", 10, Color.GRAY, false, false);

	}
	
	
	private float[] getChartFloat(String urineStrings) {
		float[] flowrateSet = null;
		if (urineStrings.length()>10) {
			String[] flowrates=urineStrings.split(",");
			int COUNT=flowrates.length;			
			flowrateSet=new float[COUNT];
			for (int i = 0; i < COUNT; i++) {
				flowrateSet[i]=Float.parseFloat(flowrates[i]);
			}
		}else {//Just for TEST!! If urineString is null,the TableRow will not allowed to click
			flowrateSet=new float[]{ 1.55f,2.07f,2.55f,3.175f,4.075f,5.075f,6.1f,7.275f,8.34f,9.11f,10.125f,11.48f,12.55f,13.475f,14.575f,15.45f,15.95f,16.475f,16.85f,16.975f,17.175f,17.2f,17.175f,17.275f,17.25f,17.575f,18.2f,18.65f,19.45f,20.35f,20.875f,21.75f,23.225f,24.95f,26.4f,27.225f,28.075f,29.05f,29.3f,29.05f,29.125f,29.4f,29.275f,28.825f,28.65f,28.625f,28.35f,28.05f,28.275f,28.925f,29.1f,28.825f,28.65f,28.2f,27.425f,27.325f,27.725f,27.7f,27.625f,27.825f,28.025f,28.2f,28.55f,29.025f,29.525f,30,30.55f,31.3375f,31.9f,32.2875f,32.9f,33.65f,34.25f,34.35f,34.225f,33.975f,33.25f,32.6f,32.7f,33.125f,33.325f,33.05f,32.65f,32.825f,33.175f,33.225f,33.5f,33.85f,33.4f,32.4f,31.975f,32.9f,34.325f,35.15f,35.675f,36.25f,36.825f,37.45f,38.4f,39.3f,39.15f,38,37.2f,37f,36.575f,36.1f,35.775f,35.575f,35.7f,35.925f,36.025f,35.8f,34.975f,34.45f,35.3f,36.6f,37.1125f,36.8438f,36.075f,35.3688f,35.275f,35.4f,35.575f,36.125f,35.95f,34.75f,34.225f,34.5938f,34.9875f,35.2938f,35.575f,35.75f,35.575f,34.625f,33.625f,33.1f,32.2875f,31.25f,30.4125f,30f,29.85f,29.7f,29.825f,30.575f,31.55f,31.925f,31.8f,31.55f,31.175f,30.225f,28.55f,27.075f,26.3f,24.8f,21.875f,19.15f,16.875f,14.25f,11.5f,9.2f,7.6f,5.975f,3.65f,1.55f,0.6f};	        
		}
		return flowrateSet;
	}

}
