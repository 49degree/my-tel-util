package com.szxys.mhub.ui.mets.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.bean.Drinkandurine;
import com.szxys.mhub.subsystem.mets.dao.DrinkUrineDao;
import com.szxys.mhub.subsystem.mets.db.GotobedGetup;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;
import com.szxys.mhub.ui.base.ListViewAdapter;
import com.szxys.mhub.ui.base.MHubActivity;
import com.szxys.mhub.ui.base.TableViewAdapter;

/**
 * 排尿记录
 * @author Administrator
 *
 */
public class UrineRecordCountActivity  extends MHubActivity implements OnClickListener{
	private final static int MYREQUEST_CODE_RECORD = 1;
	private final static int MYREQUEST_CODE_COUNT = 2;
	private final static int COUNT_DEFAULT_TIME_TYPE = 0;//默认统计周期类型（当天）
	
	public final static String PARAM_RECORD_ID = "recordId";//Intent中的参数名称，记录ID
	public final static String urineUnit = "ML";//Intent中的参数名称，记录ID
	Button record_pic_btn = null;//排尿日记按钮
	Button count_pic_btn = null;//排尿统计按钮
	LinearLayout record_time_layout = null;//排尿日记日期layout
	TextView record_time = null;//排尿日记日期 mets_urine_record_time
	TextView record_week = null;//排尿日记星期几

	LinearLayout urine_record_layout = null;//排尿日记layout
	LinearLayout urine_count_layout = null;//排尿统计layout
	TextView count_start_time = null;//排尿统计 mets_urine_count_start_time
	TextView count_end_time = null;//排尿统计 mets_urine_count_end_time
	ImageView record_round = null;//@+id/mets_urine_record_round
	ImageButton count_change_time = null;//修改时间按钮@+id/mets_urine_count_change_time
	
	ListView record_list = null;//排尿日记列表
	ListView count_list = null;//排尿统计列表
	
	private ListViewAdapter recordAdapter = null;
	private TableViewAdapter countAdapter = null;
	private LinearLayout mLoadLayout = null;// 生成表头Layout
	private String[] thedaytimes=new String[]{"",""};
    /*
     * 表头布局
     */
    private final LayoutParams mProgressBarLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mets_urine_record_count);
		
		record_pic_btn = (Button)this.findViewById(R.id.mets_urine_record_pic_btn);//排尿日记按钮
		count_pic_btn = (Button)this.findViewById(R.id.mets_urine_count_pic_btn);//排尿统计按钮
		record_time_layout = (LinearLayout)this.findViewById(R.id.mets_urine_record_time_layout);//排尿日记日期layout
		urine_record_layout = (LinearLayout)this.findViewById(R.id.mets_urine_record_layout);//排尿日记layout
	    urine_count_layout = (LinearLayout)this.findViewById(R.id.mets_urine_count_layout);//排尿统计layout
		record_list = (ListView)this.findViewById(R.id.mets_urine_record_list);;//排尿日记列表
		count_list = (ListView)this.findViewById(R.id.mets_urine_count_list);;//排尿统计列表
		
		
		record_time = (TextView)this.findViewById(R.id.mets_urine_record_time);//排尿日记日期 mets_urine_record_time
		record_week = (TextView)this.findViewById(R.id.mets_urine_record_week);//排尿日记星期几
		record_round = (ImageView)this.findViewById(R.id.mets_urine_record_round);//右边的图标
		
		count_start_time = (TextView)this.findViewById(R.id.mets_urine_count_start_time);//排尿统计 mets_urine_count_start_time
		count_end_time = (TextView)this.findViewById(R.id.mets_urine_count_end_time);//排尿统计 mets_urine_count_end_time
		
		count_change_time = (ImageButton)this.findViewById(R.id.mets_urine_count_change_time);//右边的图标;//修改时间按钮@+id/mets_urine_count_change_time

		
	    record_pic_btn.setOnClickListener(this);
	    count_pic_btn.setOnClickListener(this);
	    record_time_layout.setOnClickListener(this);
	    count_change_time.setOnClickListener(this);
	    record_list.setOnItemClickListener(new RecordItemClick());//排尿记录按事件
	    
		setRecordDateTime(null);//设置排尿日记查询日期
		setCountDateTime(UrineRecordCountActivity.COUNT_DEFAULT_TIME_TYPE);
		addCountListHeader();
	    selectRecordBtn();
	}
	
	
	@Override
	public void onRestart() {
		super.onRestart();

	}
	
	@Override
	public void onStart() {
		super.onStart();
		record_round.setBackgroundResource(R.drawable.mets_phone_round_undropdown);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 这里的resultCode得到的就是Activity1.java中设的RESULT_OK=-1，data就是mIntent
		if (resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			if (requestCode == MYREQUEST_CODE_RECORD) {// 排尿记录时间
				if (extras != null) {
					String dateStr = extras.getString(ModifyTimeActivity.TIME_TEXT);
					setRecordDateTime(dateStr);// 设置排尿日记查询日期
					selectRecordBtn();
				}
			} else if (requestCode == MYREQUEST_CODE_COUNT) {// 排尿统计时间
				if (extras != null) {
					int dateStr = extras.getInt(UrineCountDateChoiceActivity.RETURN_DATE_TIME_TYPE);
					setCountDateTime(dateStr);// 设置排尿日记查询日期
					selectedCountBtn();
				}
			}
		}
	}
	
	
	@Override
	public void onClick(View v){
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.mets_urine_record_pic_btn://排尿日记按钮
			selectRecordBtn();
			//finish();
			break;
		case R.id.mets_urine_count_pic_btn://排尿统计按钮
			selectedCountBtn();
			//finish();
			break;
		case R.id.mets_urine_record_time_layout://排尿日记记录时间修改
			record_round.setBackgroundResource(R.drawable.mets_phone_round_dropdown);
			Intent intent1 = new Intent(this,ModifyTimeActivity.class);
			intent1.putExtra(ModifyTimeActivity.TIME_TEXT, record_time.getText().toString());			
			intent1.putExtra(ModifyTimeActivity.TIME_FORMAT, TimeUtils.formatDate2);
			startActivityForResult(intent1, MYREQUEST_CODE_RECORD); // 启动这个intent跳转
		case R.id.mets_urine_count_change_time://排尿统计修改日期
			record_round.setBackgroundResource(R.drawable.mets_phone_round_dropdown);
			Intent intent = new Intent(this,UrineCountDateChoiceActivity.class);
			startActivityForResult(intent, MYREQUEST_CODE_COUNT); // 启动这个intent跳转
			break;				
		default :
			break;
		}
	}
	
	/**获取当前页面名称(仅用于测试）*/
	public String getSysName(){
		return this.getString(R.string.mets_app_name);
	}
	
	/**
	 * 选中排尿记录
	 */
	protected void selectRecordBtn(){
		urine_record_layout.setVisibility(View.VISIBLE);
		urine_count_layout.setVisibility(View.INVISIBLE);
		record_pic_btn.setBackgroundResource(R.drawable.mets_phone_tab_lable_press);
		count_pic_btn.setBackgroundResource(R.drawable.mets_phone_tab_lable);
		setStyleObject(record_pic_btn,Color.WHITE);
		setStyleObject(count_pic_btn,Color.BLACK);
		
		ArrayList<HashMap<String,Object>> drinkandurinesMapList = getDrinkandurinesByTime(this,record_time.getText().toString());
		String[] from = {"mets_urine_record_time","mets_urine_record_info","mets_urine_record_rete_pic"};
		int[] to = {R.id.mets_urine_record_time,R.id.mets_urine_record_info,R.id.mets_urine_record_rete_pic};
		recordAdapter = new ListViewAdapter(this, drinkandurinesMapList, R.layout.mets_urine_record_item,from, to);
		record_list.setAdapter(recordAdapter);
	}
	/**
	 * 选中排尿统计
	 */
	private void selectedCountBtn(){
		urine_record_layout.setVisibility(View.INVISIBLE);
		urine_count_layout.setVisibility(View.VISIBLE);
		count_pic_btn.setBackgroundResource(R.drawable.mets_phone_tab_lable_press);
		record_pic_btn.setBackgroundResource(R.drawable.mets_phone_tab_lable);
		setStyleObject(record_pic_btn,Color.BLACK);
		setStyleObject(count_pic_btn,Color.WHITE);
		

        /*
         * layout :mlistview的适配器 ，内容为mlistview 中所包含的控件
         */      
        ArrayList<HashMap<String,Object>> countData = getCountData();
        String[] from = new String[] {"mets_urine_count_header_date", "mets_urine_count_header_times", "mets_urine_count_header_value",
        		"mets_urine_count_header_night_rate","mets_urine_count_header_urgency","mets_urine_count_header_rincontinence"};
        int[] to = new int[] {R.id.mets_urine_count_header_date, R.id.mets_urine_count_header_times, R.id.mets_urine_count_header_value, 
        		R.id.mets_urine_count_header_night_rate,R.id.mets_urine_count_header_urgency,R.id.mets_urine_count_header_rincontinence};
        countAdapter = new TableViewAdapter(this,countData, R.layout.mets_urine_count_item, from, to);
        countAdapter.setOP_Count(countData.size());// 设置每页显示记录条数
        count_list.setAdapter(countAdapter);

	}	
	
	/**
	 * 设置排尿统计周期
	 * @param checkDateType
	 */
	private void setRecordDateTime(String dateStr) {
		//String date = record_time.getText().toString();//获取输入框中的时间
		// 如果没有则获取当前时间
		try{
			if (dateStr==null||dateStr.equals("")) {
				dateStr = TimeUtils.getTimeString(new Date(),TimeUtils.formatDate2);
			}
			record_time.setText(dateStr);
			record_week.setText(TimeUtils.getWeekTimeFromString(dateStr, TimeUtils.formatDate2));
			thedaytimes=GotobedGetup.getGetUpGotobedTimes(this,dateStr);
		}catch(Exception e){
			
		}
	}
	
	
	/**
	 * 设置排尿统计周期
	 * @param checkDateType
	 */
	private void setCountDateTime(int checkDateType) {
		Calendar c = Calendar.getInstance(); 
		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		switch (checkDateType) {
		case UrineRecordCountActivity.COUNT_DEFAULT_TIME_TYPE:
			count_start_time.setText(TimeUtils.getTimeString(new Date(), TimeUtils.formatDate2));
			count_end_time.setText(TimeUtils.getTimeString(new Date(), TimeUtils.formatDate2));
			break;
		case R.id.mets_urine_count_time_radio1:
			c.set(Calendar.DAY_OF_MONTH, dayOfMonth-7);
			count_start_time.setText(TimeUtils.getTimeString(c.getTime(), TimeUtils.formatDate2));
			count_end_time.setText(TimeUtils.getTimeString(new Date(), TimeUtils.formatDate2));
			break;
		case R.id.mets_urine_count_time_radio2:
			c.set(Calendar.DAY_OF_MONTH, dayOfMonth-10);
			count_start_time.setText(TimeUtils.getTimeString(c.getTime(), TimeUtils.formatDate2));
			count_end_time.setText(TimeUtils.getTimeString(new Date(), TimeUtils.formatDate2));
			break;
		case R.id.mets_urine_count_time_radio3:
			c.set(Calendar.DAY_OF_MONTH, dayOfMonth-15);
			count_start_time.setText(TimeUtils.getTimeString(c.getTime(), TimeUtils.formatDate2));
			count_end_time.setText(TimeUtils.getTimeString(new Date(), TimeUtils.formatDate2));
			break;
		case R.id.mets_urine_count_time_radio4:
			c.set(Calendar.MONTH, c.get(Calendar.MONTH)-1);
			count_start_time.setText(TimeUtils.getTimeString(c.getTime(), TimeUtils.formatDate2));
			count_end_time.setText(TimeUtils.getTimeString(new Date(), TimeUtils.formatDate2));
			break;
		case R.id.mets_urine_count_time_radio5:
			break;			
		default:
			break;
		}
	}
	
	/**
	 * 设置头部按钮样式
	 * @param strs
	 * @param color
	 * @return
	 */
	private void setStyleObject(Button button,int color){
		button.setTextColor(color);
		button.setTextSize(20);
		
		TextPaint tp = button.getPaint();
        tp.setFakeBoldText(true);
	}
	
	/**
	 * 根据日期查询数据
	 * @param 查询日期 dateString="yyyy-MM-dd"
	 * @return ArrayList<Drinkandurine> 实体类集合
	 */
	private ArrayList<HashMap<String,Object>> getDrinkandurinesByTime(Context context,String dateString){
		ArrayList<Drinkandurine> drinkandurines =DrinkUrineDao.getUrineList(dateString);//查询排尿记录
		ArrayList<HashMap<String,Object>> drinkandurinesMapList = new ArrayList<HashMap<String,Object>>(drinkandurines.size());

		HashMap<String,Object> hashMap = null;
		StringBuffer urineType = null;
		int i = 0;
		for(Drinkandurine drinkandurine:drinkandurines){
			hashMap = new HashMap<String,Object>(3);
			urineType = new StringBuffer();
			hashMap.put(UrineRecordCountActivity.PARAM_RECORD_ID, drinkandurine.getC_Id());//传入记录ID
			hashMap.put("mets_urine_record_ufrid", drinkandurine.getC_UfrId());//传入尿流率标志
			if(i==1){
				hashMap.put("mets_urine_record_ufrid", "1");//传入尿流率标志
			}
			
			hashMap.put("mets_urine_record_time", dateString);
			// SMALLINT NOT NULL DEFAULT 0, [0 drink(饮水) 1 urine(排尿) 2 urinary urgency(尿急) 3 urine loss(尿失禁)]
			String drink_text = getResources().getString(R.string.mets_drink_text);
			String urine_text = getResources().getString(R.string.mets_urine_text);
			String urine_emergent =getResources().getString(R.string.mets_urine_emergent);//"尿急";
			String urine_loss = getResources().getString(R.string.mets_urinary_incontinence_text);//"(失)"; //尿失禁
			//构造排尿详细信息
			if(drinkandurine.getC_Type()==0){
				urineType.append(drink_text).append(drinkandurine.getC_Quantity()).append(UrineRecordCountActivity.urineUnit);
			}else if(drinkandurine.getC_Type()==1){
				urineType.append(urine_text).append(drinkandurine.getC_Quantity()).append(UrineRecordCountActivity.urineUnit);
			}else if(drinkandurine.getC_Type()==2){
				urineType.append(urine_emergent).append(drinkandurine.getC_Quantity()).append(UrineRecordCountActivity.urineUnit);
			}else if(drinkandurine.getC_Type()==3){
				urineType.append(urine_loss);
			}
			
			hashMap.put("mets_urine_record_info", urineType.toString());	
			
			if(i++==1||drinkandurine.getC_UfrId()>0){
				hashMap.put("mets_urine_record_rete_pic", new Integer(R.drawable.mets_rate_button_pic));
			}
			drinkandurinesMapList.add(hashMap);
		}
		return drinkandurinesMapList;
	}
	

	/**
	 * 排尿记录点击事件
	 * @author Administrator
	 *
	 */
	public class RecordItemClick implements OnItemClickListener{
		@Override
		public void onItemClick(final AdapterView<?> arg0, View arg1,final int arg2, long arg3) {
			Map<String, Object> prodMap = (Map<String, Object>)arg0.getAdapter().getItem(arg2);//prodList.get(arg2);
			String ufrid = String.valueOf(prodMap.get("mets_urine_record_ufrid"));
			String urineId = String.valueOf(prodMap.get(UrineRecordCountActivity.PARAM_RECORD_ID));
			try{
				if(Integer.parseInt(ufrid)>0){//为尿流率记录
					Intent rateIntent = new Intent(UrineRecordCountActivity.this,UrineRateActivity.class);
					//rateIntent.putExtra(UrineRecordActivity.PARAM_RECORD_ID, drinkandurineFinal.getC_Id());
					rateIntent.putExtra(UrineRecordActivity.PARAM_RECORD_ID, urineId); //Edited by WXG @Thu May 11,2011
					startActivity(rateIntent);//打开尿流率页面
				}else{//为排尿饮水等其他记录
					Intent intent = new Intent(UrineRecordCountActivity.this,UrineRecordOperateActivity.class);
					intent.putExtra(UrineRecordCountActivity.PARAM_RECORD_ID,urineId);
				    startActivity(intent);
				}
			}catch(Exception e){
				
			}

		}
	}
	
	private void addCountListHeader(){
	    /*
         * "加载项"布局，此布局被添加到ListView的Footer中。
         */
        mLoadLayout = new LinearLayout(this);
        mLoadLayout.setMinimumHeight(60);
        mLoadLayout.setGravity(Gravity.CENTER);
        mLoadLayout.setOrientation(LinearLayout.HORIZONTAL);
        /*
         * 向"加载项"布局中添加一个TableHead
         */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float mDensity = metrics.density;// 获取逻辑分辨率
        int mWidth = metrics.widthPixels;// 获取屏幕宽度
        //Log.v(TAG, "the density is:" + mDensity + ";and the w is:" + mWidth);
        TextView tv1 = new TextView(this);
        tv1.setText(getString(R.string.mets_urine_count_header_date));
        tv1.setGravity(Gravity.CENTER);
        tv1.setWidth(mWidth /7);
        tv1.setBackgroundResource(R.drawable.pf_tableview_bg);
        TextView tv2 = new TextView(this);
        tv2.setText(getString(R.string.mets_urine_count_header_times));
        tv2.setBackgroundResource(R.drawable.pf_tableview_bg);
        tv2.setWidth(mWidth*3 /14);
        tv2.setGravity(Gravity.CENTER);
        TextView tv3 = new TextView(this);
        tv3.setText(getString(R.string.mets_urine_count_header_value));
        tv3.setBackgroundResource(R.drawable.pf_tableview_bg);
        tv3.setWidth(mWidth*3 / 14);
        tv3.setGravity(Gravity.CENTER);
        TextView tv4 = new TextView(this);
        tv4.setText(getString(R.string.mets_urine_count_header_night_rate));
        tv4.setBackgroundResource(R.drawable.pf_tableview_bg);
        tv4.setWidth(mWidth / 7);
        tv4.setGravity(Gravity.CENTER);

        TextView tv5 = new TextView(this);
        tv5.setText(getString(R.string.mets_urine_count_header_urgency));
        tv5.setBackgroundResource(R.drawable.pf_tableview_bg);
        tv5.setWidth(mWidth / 7);
        tv5.setGravity(Gravity.CENTER);
        
        TextView tv6 = new TextView(this);
        tv6.setText(getString(R.string.mets_urine_count_header_rincontinence));
        tv6.setBackgroundResource(R.drawable.pf_tableview_bg);
        tv6.setWidth(mWidth / 7);
        tv6.setGravity(Gravity.CENTER);        
        
        mLoadLayout.addView(tv1, mProgressBarLayoutParams);
        mLoadLayout.addView(tv2, mProgressBarLayoutParams);
        mLoadLayout.addView(tv3, mProgressBarLayoutParams);
        mLoadLayout.addView(tv4, mProgressBarLayoutParams);
        mLoadLayout.addView(tv5, mProgressBarLayoutParams);
        mLoadLayout.addView(tv6, mProgressBarLayoutParams);
        
        count_list.addHeaderView(mLoadLayout);
	}
	
	/**
	 * 根据日期查询数据
	 * @param datas 查询日期
	 * @return
	 */
	private ArrayList<HashMap<String,Object>> getCountData(){		
		
		String fromDate = count_start_time.getText().toString(); //"yyyy-MM-dd" 统计开始日期
		String toDate = count_start_time.getText().toString(); //"yyyy-MM-dd"  统计结束日期

		//ArrayList<String> statisticsList=DrinkAndUrine.getMetsStatistics(UrineCountActivity.this, fromDate, toDate);
		// String="统计日#(白/夜/总)排尿次数#(白/夜/总)排尿量#夜尿指数#尿急#尿失禁"
		ArrayList<String> statisticsList=DrinkUrineDao.getUrineStatisticsInfo(fromDate, toDate);
		Log.d("UrineStatistics", "From "+fromDate+" to "+toDate);

		int rowCount=statisticsList.size();
		ArrayList<HashMap<String,Object>> mData = new ArrayList<HashMap<String,Object>>(rowCount);
		HashMap<String,Object> dataMap = null;
		
		if (statisticsList!=null && !statisticsList.isEmpty()) {
			for(int i=0;i<statisticsList.size();i++){
				String[] rowStrings=statisticsList.get(i).split("#");//rowStrings.length=6  yyyy/MM/dd # int/int/int # int/int/int # ?% # int # int # 
				dataMap = new HashMap<String,Object>();//增加记录详细信息
				TextView headerDate = new TextView(this);			
				headerDate.setText(rowStrings[0]);
				String dateStr = rowStrings[0]==null?"":rowStrings[0];
				dateStr = dateStr.length()==10?dateStr.substring(6):dateStr;
				
				dataMap.put("mets_urine_count_header_date", dateStr);//日期	
				dataMap.put("mets_urine_count_header_times", rowStrings[1]);//尿尿次数(白天/夜晚/总次数)
				dataMap.put("mets_urine_count_header_value", rowStrings[2]);//尿量(白天/夜晚/总量)
				dataMap.put("mets_urine_count_header_night_rate", rowStrings[3]);//夜尿指数
				dataMap.put("mets_urine_count_header_urgency", rowStrings[4]);//尿急(总次数)
				dataMap.put("mets_urine_count_header_rincontinence", rowStrings[5]);//尿失禁(总次数)
				mData.add(dataMap);
			}
		}
		return mData;
	}
}
