package com.szxys.mhub.ui.mets.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;
import com.szxys.mhub.ui.mets.components.SelectDateTime;
import com.szxys.mhub.ui.mets.components.ShowTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class ModifyTimeActivity extends Activity implements OnTouchListener {

	private SelectDateTime mSelectDate;
	private ShowTime mShowDateTime;
	private Button mTvOk;
	private Button mTvCancel;
	private int mCollectType; 
	
	public final static String TIME_TEXT = "timeText";
	public final static String TIME_FORMAT = "timeFormat";
	
	//定义两套时间各式，回传时需要哪种各式就可以显示哪种各式, -- 更好的处理： 传递过来的是那一种各式，则返回相应的格式
	public final static int TIME_DATE_FLAG_1 = 0;  //"yyyy-MM-dd HH:mm"
	public final static int TIME_DATE_FLAG_2 = 1;  //"yyyy年MM月dd日 HH:mm"
	
	private final static String TAG = "ModifyTimeActivity2----------";
	Bundle bundle ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.e(TAG, "onCreate()------ ");
		
		setContentView(R.layout.mets_time_modify);

		init();
	}
	

	/**
	 * 初始化页面控件内容及事件绑定    获得从上一个Activity跳转过来的时间
	 */
	private void init(){
		
		//获得从上一个Activity跳转过来的时间
		bundle = getIntent().getExtras();
		String timeText = bundle.getString(TIME_TEXT);
		String timeFormat = bundle.getString(TIME_FORMAT);
		
		//把时间各式 的逻辑判断权交给 具体的时间控件和日期控件
//		if(TimeUtils.format == timeFormat) {  // yyyy年MM月dd日 HH:mm
//			
//		}
		
		Date timeDate = TimeUtils.getDateFromString(timeText, timeFormat);
		
		Calendar c = Calendar.getInstance();
		c.setTime(timeDate);
		
		mSelectDate = (SelectDateTime)findViewById(R.id.selectdate);
		mSelectDate.init(c, timeFormat);
		
		mShowDateTime = (ShowTime)findViewById(R.id.showtime);
		mShowDateTime.init(c, timeFormat);
		
		
		mTvOk = (Button)findViewById(R.id.mets_ok_btn);
		mTvCancel = (Button)findViewById(R.id.mets_cancel_btn);
		mTvOk.setSelected(true);
		
		mTvCancel.setOnClickListener(cancelClick);
		mTvOk.setOnClickListener(okClick);
		
	}
	
	
	//确定提交修改
	View.OnClickListener okClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
			//获取选择的日期和时间
			String date = mSelectDate.getDateText();
			String time = mShowDateTime.getDateText();
			String datetime = new StringBuffer().append(date).append(" ").append(time).toString();
			
			Date modifyDate = TimeUtils.getDateFromString(datetime, bundle.getString(TIME_FORMAT));
			datetime = TimeUtils.getTimeString(modifyDate,  bundle.getString(TIME_FORMAT));
			
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString(TIME_TEXT, datetime);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			//结束Activity
			finish();
			
		}
	};

	//取消
	View.OnClickListener cancelClick = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			
			finish();
		}
	};
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()){
		case R.id.mets_ok_btn:
			mTvOk.setSelected(true);
			mTvCancel.setSelected(false);
			break;
		case R.id.mets_bt_cancel:
			mTvCancel.setSelected(true);
			mTvOk.setSelected(false);
			break;
		}
		return false;
	}

}
