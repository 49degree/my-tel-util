package com.szxys.mhub.ui.mets.activity;

import java.util.Date;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.bean.Sysconfig;
import com.szxys.mhub.subsystem.mets.db.SysConfig;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;

/**
 * 配置信息
 * 
 * @author Administrator
 * 
 */
public class ConfigInfoActivity extends Activity implements OnClickListener {
	
	private TextView hospital_name_content;
	private TextView doctor_name_content;
	private TextView patient_name_content;
	private TextView pad_name_content;
	private TextView urienGather_content;
	private TextView autocloseBT;
	private TextView communicationtime_interval_content;
	private TextView default_getuptime_content;
	private TextView default_gotobedtime_content;
	
	private Button returnButton;
	
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mets_config_info);
		
		hospital_name_content = (TextView) this.findViewById(R.id.mets_hospital_name_content); 
		doctor_name_content = (TextView) this.findViewById(R.id.mets_doctor_name_content); 
		patient_name_content = (TextView) this.findViewById(R.id.mets_patient_name_content); 
		pad_name_content = (TextView) this.findViewById(R.id.mets_pad_name_content); 
		urienGather_content = (TextView) this.findViewById(R.id.mets_urineGather_name_content); 
		autocloseBT= (TextView) this.findViewById(R.id.mets_closebluetooth_content);
		communicationtime_interval_content = (TextView) this.findViewById(R.id.mets_communicationtime_interval_content); 
		default_getuptime_content = (TextView) this.findViewById(R.id.mets_default_getuptime_content); 
		default_gotobedtime_content = (TextView) this.findViewById(R.id.mets_default_gotobedtime_content);  
		
		returnButton = (Button) this.findViewById(R.id.mets_bt_return);
		
		returnButton.setOnClickListener(this);
		init();
	}
	
	//初始化数据      需要传入配置信息表中的数据  -------------------------------
	
	//初始化数据      需要传入配置信息表中的数据  -------------------------------
	
	private void  init() {		
		Sysconfig objConfig=SysConfig.getSysConfigObj(ConfigInfoActivity.this);
		if (objConfig!=null) {
			hospital_name_content.setText(objConfig.getC_Hospital());
			doctor_name_content.setText(objConfig.getC_DoctorsName()); 
			patient_name_content.setText(objConfig.getC_PatientName()); 
			pad_name_content.setText(objConfig.getC_MobileId()); 
			urienGather_content.setText(objConfig.getC_CollectorId()); 
			autocloseBT.setText(objConfig.getC_AutoCloseBt()==1?(getResources().getString(R.string.mets_promptmessage_ok)):(getResources().getString(R.string.mets_promptmessage_negative)));
			communicationtime_interval_content.setText(objConfig.getC_SendDtInterval()); 
			default_getuptime_content.setText(objConfig.getC_GetUpAlarm()); 
			default_gotobedtime_content.setText(objConfig.getC_GotoBedAlarm()); 
		}else {
			hospital_name_content.setText("Shenzhen Hospital"); 
			doctor_name_content.setText("zhaoling"); 
			patient_name_content.setText("ANDROID"); 
			pad_name_content.setText("iPad2"); 
			urienGather_content.setText("HOMT"); 
			autocloseBT.setText(getResources().getString(R.string.mets_promptmessage_ok));
			communicationtime_interval_content.setText("60"); 
			default_getuptime_content.setText("07:12"); 
			default_gotobedtime_content.setText("23:12"); 
		}	
		
	}



	/**
	 * 事件处理函数
	 */
	@Override
	public void onClick(View v) {
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.mets_bt_return:
			finish();
			break;
		
		default:
			break;
		}
	}

}
