package com.szxys.mhub.ui.mets.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.dao.GetupSleepDao;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;
import com.szxys.mhub.ui.mets.components.PromptMessageActivity;
import com.szxys.mhub.ui.mets.main.MetsMainActivity;

/**
 * 起床睡觉时间
 * 
 * @author Administrator
 * 
 */
public class GetupSleepActivity extends Activity implements OnClickListener,OnTouchListener{
	private static final int MYREQUEST_CODE = 54321;//修改时间的请求码

	private TextView getUpTopText = null;//标题
	private TextView getUpContentText = null;//时间标题
	private TextView getUpTimeText = null;//时间TEXT ,也可以点击更改时间，故添加事件监听
	
//	private Button getUpTimeText = null;//时间TEXT
	private Button changeBtn = null;//修改按钮
	private Button okBtn = null;//确定按钮
	private Button cancelBtn = null;//取消按钮

	private String currentDt="";
	String[] getGetUpGotobedTime=null;
	private boolean isSaveGetUpTime = false;//数据库是否已经保存相应数据
	private int businessId = 0;//业务ID
	
//  时间的格式显示： 0：yyyy年MM月dd日 HH:mm 1：yyyy年MM月dd日 HH时mm分   2： yyyy-MM-dd HH:mm
	private static int show_format = 0; 
	private String td_format;//存储当前的时间日期各式

	private String EarlierThanYestodaySleep="起床时间必须晚于昨天的睡觉时间";
	private String EarlierThanSleep="起床时间不能晚于今天的睡觉时间";
	private String EarlierThanCurrentTime="起床时间必须是今天且不能晚于现在";
	private String EarlierThanGetup="睡觉时间不能早于今天的起床时间";
	private String EarlierThanNow="睡觉时间不能晚于现在";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mets_getup_sleep);

		getUpTopText = (TextView) this.findViewById(R.id.mets_getup_sleep_top_text);//
		getUpContentText = (TextView) this.findViewById(R.id.mets_getup_sleep_content_text);
		getUpTimeText = (TextView) this.findViewById(R.id.mets_getup_sleep_time_text);
		
		changeBtn = (Button) this.findViewById(R.id.mets_getup_sleep_change_btn);
		okBtn = (Button) this.findViewById(R.id.mets_getup_sleep_ok_btn);
		cancelBtn = (Button) this.findViewById(R.id.mets_getup_sleep_cancel_btn);
		
		changeBtn.setOnClickListener(this);
		getUpTimeText.setOnClickListener(this);
		okBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		
		changeBtn.setOnTouchListener(this);
		getUpTimeText.setOnTouchListener(this);
		okBtn.setOnTouchListener(this);
		cancelBtn.setOnTouchListener(this);
		
		changeBtn.setSelected(true);
		okBtn.setSelected(true);
		
		init();
	}

	private void init() {
		
		//设置时间的显示各式
		if(0 == show_format) {
			td_format = TimeUtils.format;
		} else if (1 == show_format) {
			td_format = TimeUtils.format1;
		} else if (2 == show_format) {
			td_format = TimeUtils.format2;
		}
		EarlierThanYestodaySleep=getResources().getString(R.string.mets_getup_earlier_than_YesSleep);//"起床时间必须晚于昨天的睡觉时间";
		EarlierThanSleep=getResources().getString(R.string.mets_getup_later_than_TodaySleep);//"起床时间不能晚于今天的睡觉时间";
		EarlierThanCurrentTime=getResources().getString(R.string.mets_notToday_orLater_thanNow);//"起床时间必须是今天且不能晚于现在";
		EarlierThanGetup=getResources().getString(R.string.mets_sleep_earlier_than_TodayGetup);///"睡觉时间不能早于今天的起床时间";
		EarlierThanNow=getResources().getString(R.string.mets_sleep_later_thanNow);//"睡觉时间不能晚于现在";
		
		Bundle extras=getIntent().getExtras(); //获取打开参数
		if(extras==null)
			return ;
		businessId = extras.getInt(MetsMainActivity.MainStatic.BusinessIdString);	
		changeBtn.setText(R.string.mets_modify_btn_text);		
		if(businessId == MetsMainActivity.MainStatic.getup){//起床
			getUpTopText.setText(R.string.mets_set_getup_time);
			getUpContentText.setText(R.string.mets_taday_getup_time);
			
			Date dt=GetupSleepDao.getTodayGetupTime();
			String getupTodayString=TimeUtils.getTimeString(dt, TimeUtils.format);
			currentDt=getupTodayString;
			Log.d("mylog", "Getup@"+getupTodayString);				
			getUpTimeText.setText(getupTodayString);
			
		}else if(businessId == MetsMainActivity.MainStatic.sleep){//睡觉
			getUpTopText.setText(R.string.mets_set_sleep_time);
			getUpContentText.setText(R.string.mets_taday_sleep_time);			
			
			Date dt=GetupSleepDao.getTodaySleepTime();
			String gotobedTodayString=TimeUtils.getTimeString(dt, TimeUtils.format);
			currentDt=gotobedTodayString;
			Log.d("mylog", "Gotobed@"+gotobedTodayString);
			getUpTimeText.setText(gotobedTodayString);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 这里的resultCode得到的就是Activity1.java中设的RESULT_OK=-1，data就是mIntent
		if (requestCode == GetupSleepActivity.MYREQUEST_CODE) {// 这里的requestCode程序执行正常就是MYREQUEST_CODE（3）了
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					getUpTimeText.setText(extras.getString(ModifyTimeActivity.TIME_TEXT));
				}
			}
		}
	}


	/**
	 * 事件处理函数
	 */
	@Override
	public void onClick(View v) {
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.mets_getup_sleep_ok_btn://确定按钮事件
			String noneGetupString=getResources().getString(R.string.mets_null_getup_time_text);
			String noneSleepString=getResources().getString(R.string.mets_null_sleep_time_text);
			//String secondName=getResources().getString(R.string.mets_time_second_text);
			Date curDate=new Date();
			int secondScale=curDate.getSeconds();
			String dateHourMinute=getUpTimeText.getText().toString().trim();//2011年05月09日 15:10  TimeUtils.format
			String timeString=dateHourMinute+":"+(secondScale<10?("0"+secondScale):secondScale); //饮水时间 2011年05月09日 15:31:32
			//timeString=dateHourMinute+(secondScale<10?("0"+secondScale):secondScale)+secondName;
			Date dtSaved = TimeUtils.getDateFromString(timeString, TimeUtils.format3); //"yyyy-MM-dd HH:mm:ss";			
			String dateString=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dtSaved);
			if(businessId == MetsMainActivity.MainStatic.getup){
				if (!dateHourMinute.equalsIgnoreCase(noneGetupString)) {
					
					//保存起床时间 yyyy-MM-dd hh:mm:ss
						long result=GetupSleepDao.saveGetupTime(dateString);
						if (result>0) {
							Toast.makeText(GetupSleepActivity.this,
									getResources().getString(R.string.mets_submit_ok),
									Toast.LENGTH_LONG).show();
						}else {
							if (result==-2) {
								Toast.makeText(GetupSleepActivity.this,
										getResources().getString(R.string.mets_notToday_orLater_thanNow),
										Toast.LENGTH_LONG).show();
								return;
							}
							if (result==-3) {
								Toast.makeText(GetupSleepActivity.this,
										getResources().getString(R.string.mets_notToday_orLater_thanNow),
										Toast.LENGTH_LONG).show();
								return;
							}
							if (result==-4) {
								Toast.makeText(GetupSleepActivity.this,
										getResources().getString(R.string.mets_getup_later_than_TodaySleep),
										Toast.LENGTH_LONG).show();
								return;
							}
							if (result==-5) {
								Toast.makeText(GetupSleepActivity.this,
										getResources().getString(R.string.mets_getup_earlier_than_YesSleep), 
										Toast.LENGTH_LONG).show();
								return;
							}
							if (result==0) {
								Toast.makeText(GetupSleepActivity.this,
										getResources().getString(R.string.mets_submit_failed), 
										Toast.LENGTH_LONG).show();
								return;
							}
						}
				}
				
			}else if(businessId == MetsMainActivity.MainStatic.sleep){
				if (!dateHourMinute.equalsIgnoreCase(noneSleepString)) {
					
					//保存睡觉时间yyyy-MM-dd hh:mm:ss
					long result=GetupSleepDao.saveSleepTime(dateString);
					if (result>0) {
						Toast.makeText(GetupSleepActivity.this,
								getResources().getString(R.string.mets_submit_ok), 
								Toast.LENGTH_LONG).show();
					}else {
						if (result==-2) {
							Toast.makeText(GetupSleepActivity.this,
									getResources().getString(R.string.mets_sleep_later_thanNow), 
									Toast.LENGTH_LONG).show();
							return;
						}
						if (result==-3) {
							Toast.makeText(GetupSleepActivity.this,
									getResources().getString(R.string.mets_sleep_earlier_than_TodayGetup), 
									Toast.LENGTH_LONG).show();
							return;
						}
						if (result==-4) {
							Toast.makeText(GetupSleepActivity.this,
									getResources().getString(R.string.mets_sleep_earlier_than_TodayZerotime), 
									Toast.LENGTH_LONG).show();
							return;
						}
						if (result==0) {
							Toast.makeText(GetupSleepActivity.this,
									getResources().getString(R.string.mets_submit_failed), 
									Toast.LENGTH_LONG).show();
							return;
						}
					}
				}
				
			}
			finish();
			break;
		case R.id.mets_getup_sleep_cancel_btn://取消按钮事件
			if(!isSaveGetUpTime){//判断是否已经保存
				Intent intent = new Intent(GetupSleepActivity.this, PromptMessageActivity.class);
				if(businessId == MetsMainActivity.MainStatic.getup) {
					intent.putExtra(PromptMessageActivity.PROMPT_MESSAGE_TYPE, PromptMessageActivity.PROMPT_MESSAGE_GETUP);
				} else if(businessId == MetsMainActivity.MainStatic.sleep) {
					intent.putExtra(PromptMessageActivity.PROMPT_MESSAGE_TYPE, PromptMessageActivity.PROMPT_MESSAGE_SLEEP);
				}
				startActivity(intent);
			} else { //已经保存按取消后直接 退回到主界面
				finish();
			}
			
//			
			break;
		case R.id.mets_getup_sleep_time_text: //利用穿透来解决他们两个的同一监听事件
		case R.id.mets_getup_sleep_change_btn:// 修改按钮事件
			Intent intent1 = new Intent(GetupSleepActivity.this,ModifyTimeActivity.class);
			//intent1.putExtra(ModifyTimeActivity.timeText, getUpTimeText.getText().toString());
			intent1.putExtra(ModifyTimeActivity.TIME_TEXT, currentDt);			
			intent1.putExtra(ModifyTimeActivity.TIME_FORMAT, td_format);

			startActivityForResult(intent1, MYREQUEST_CODE); // 启动这个intent跳转
			break;
		default:
			break;
		}
	}
	
	/**
	 * 取消保存处理逻辑
	 * @return
	 */
	public void cancelMethod() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if(businessId == MetsMainActivity.MainStatic.getup){//起床
			builder.setMessage(R.string.mets_cancel_save_getup_time_text);
		}else if(businessId == MetsMainActivity.MainStatic.sleep){//睡觉
			builder.setMessage(R.string.mets_cancel_save_sleep_time_text);
		}
		
		builder.setTitle(R.string.mets_promptmessage_toptitle);
		builder.setPositiveButton(R.string.mets_promptmessage_ok,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();// 退出程序
					}
				});
		builder.setNegativeButton(R.string.mets_promptmessage_negative,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.mets_getup_sleep_ok_btn:
			okBtn.setSelected(true);
			cancelBtn.setSelected(false);
			changeBtn.setSelected(false);
			break;
		case R.id.mets_getup_sleep_cancel_btn:
			cancelBtn.setSelected(true);
			okBtn.setSelected(false);
			changeBtn.setSelected(false);
			break;
		 
		}
		return false;
	}
}
