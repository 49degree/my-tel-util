package com.szxys.mhub.ui.mets.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.szxys.mhub.R;
import com.szxys.mhub.ui.base.MHubActivity;

/**
 * 排尿记录
 * @author Administrator
 *
 */
public class UrineRecordOperateActivity  extends MHubActivity implements OnClickListener{
	//mets_urine_record_operate_modify
	TextView operate_modify = null;
	TextView operate_delete = null;
	Bundle extras= null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mets_urine_record_operate);
		operate_modify = (TextView)this.findViewById(R.id.mets_urine_record_operate_modify);
		operate_delete = (TextView)this.findViewById(R.id.mets_urine_record_operate_delete);
		operate_modify.setOnClickListener(this);
		operate_delete.setOnClickListener(this);
		
		extras=getIntent().getExtras(); //获取打开参数
		if(extras==null)
			return ;
	}
	
	
	@Override
	public void onRestart() {
		super.onRestart();

	}
		
	@Override
	public void onClick(View v){
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.mets_urine_record_operate_modify://修改
			Intent modifyIntent = new Intent(this,ModifyDrinkUrineActivity.class);
			modifyIntent.putExtra(UrineRecordCountActivity.PARAM_RECORD_ID,extras.getString((UrineRecordCountActivity.PARAM_RECORD_ID)));//Intent中的参数名称，记录ID
			startActivity(modifyIntent);
			finish();
			break;
		case R.id.mets_urine_record_operate_delete://删除
			Intent deleteIntent = new Intent(this,UrinePromptActivity.class);
			deleteIntent.putExtra(UrinePromptActivity.PARAM_BUSSNESS_ID, UrinePromptActivity.URINE_RECORD_DELETE);
			deleteIntent.putExtra(UrineRecordCountActivity.PARAM_RECORD_ID,extras.getString((UrineRecordCountActivity.PARAM_RECORD_ID)));
		    startActivity(deleteIntent);
		    finish();
		    //operate_delete.setBackgroundColor(Color.argb(0, 0, 255, 0)); //背景透明度
			break;
		case R.id.mets_urine_rate_count_layout://排尿日记layout
			break;				
		default :
			break;
		}
	}
	
	/**获取当前页面名称(仅用于测试）*/
	public String getSysName(){
		return this.getString(R.string.mets_app_name);
	}
	
	
}
