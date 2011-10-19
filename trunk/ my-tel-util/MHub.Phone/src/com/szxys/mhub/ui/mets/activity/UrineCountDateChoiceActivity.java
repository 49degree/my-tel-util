package com.szxys.mhub.ui.mets.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.szxys.mhub.R;
import com.szxys.mhub.ui.base.MHubActivity;

/**
 * 排尿记录
 * @author Administrator
 *
 */
public class UrineCountDateChoiceActivity  extends MHubActivity implements OnClickListener{
	//mets_urine_record_operate_modify
	public final static String RETURN_DATE_TIME_TYPE = "return_date_time_type";
	private static final String TAG = "UrineCountDateChoiceActivity";
	Button date_sure_btn = null;
	Button date_cannel_btn = null;
	//RadioGroup time_radio = null;
	
	RadioButton mets_urine_count_time_radio1 = null; //mets_urine_count_time_radio1
	RadioButton mets_urine_count_time_radio2 = null; //mets_urine_count_time_radio1
	RadioButton mets_urine_count_time_radio3 = null; //mets_urine_count_time_radio1
	RadioButton mets_urine_count_time_radio4 = null; //mets_urine_count_time_radio1
	RadioButton mets_urine_count_time_radio5 = null; //mets_urine_count_time_radio1
	
	
	
	Bundle extras= null;
	int checkButtonId = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mets_urine_count_date_choice);
		date_sure_btn = (Button)this.findViewById(R.id.mets_urine_count_date_sure_btn);
		date_cannel_btn = (Button)this.findViewById(R.id.mets_urine_count_date_cannel_btn);
		//time_radio = (RadioGroup)this.findViewById(R.id.mets_urine_count_time_radio);//mets_urine_count_time_radio
		
		mets_urine_count_time_radio1 = (RadioButton)this.findViewById(R.id.mets_urine_count_time_radio1); //
		mets_urine_count_time_radio2 = (RadioButton)this.findViewById(R.id.mets_urine_count_time_radio2); //
		mets_urine_count_time_radio3 = (RadioButton)this.findViewById(R.id.mets_urine_count_time_radio3); //
		mets_urine_count_time_radio4 = (RadioButton)this.findViewById(R.id.mets_urine_count_time_radio4); //
		mets_urine_count_time_radio5 = (RadioButton)this.findViewById(R.id.mets_urine_count_time_radio5); //
		
		date_sure_btn.setOnClickListener(this);
		date_cannel_btn.setOnClickListener(this);

		mets_urine_count_time_radio1.setOnClickListener(this);
		mets_urine_count_time_radio2.setOnClickListener(this);
		mets_urine_count_time_radio3.setOnClickListener(this);
		mets_urine_count_time_radio4.setOnClickListener(this);
		mets_urine_count_time_radio5.setOnClickListener(this);
		
		date_sure_btn.setSelected(true);
	}
	
	
	@Override
	public void onRestart() {
		super.onRestart();

	}
	
	@Override
	public void onClick(View v){
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.mets_urine_count_date_sure_btn://确定
			returnActivity(checkButtonId);
			finish();
			break;
		case R.id.mets_urine_count_date_cannel_btn://取消
		    finish();
		    //operate_delete.setBackgroundColor(Color.argb(0, 0, 255, 0)); //背景透明度
			break;
		case R.id.mets_urine_count_time_radio1://选中其中一个
		case R.id.mets_urine_count_time_radio2://选中其中一个
		case R.id.mets_urine_count_time_radio3://选中其中一个
		case R.id.mets_urine_count_time_radio4://选中其中一个
		case R.id.mets_urine_count_time_radio5://选中其中一个
			clearRadioButton();
			((RadioButton)this.findViewById(buttonId)).setChecked(true);
			checkButtonId = buttonId;
			//returnActivity(buttonId);
			break;
		default :
			break;
		}
	}
	
	private void clearRadioButton(){
		mets_urine_count_time_radio1.setChecked(false);
		mets_urine_count_time_radio2.setChecked(false);
		mets_urine_count_time_radio3.setChecked(false);
		mets_urine_count_time_radio4.setChecked(false);
		mets_urine_count_time_radio5.setChecked(false);
	}
	
	
	private void returnActivity(int viewId){
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt(RETURN_DATE_TIME_TYPE, viewId);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		//结束Activity
		finish();
	}
	/**获取当前页面名称(仅用于测试）*/
	public String getSysName(){
		return this.getString(R.string.mets_app_name);
	}
	
	
}
