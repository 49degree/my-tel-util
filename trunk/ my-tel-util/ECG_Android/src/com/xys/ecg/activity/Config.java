package com.xys.ecg.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Config extends Activity {
     private Button btBack;
     private Button btRunConfig;
     private Button btEquipInfo;
     private Button btContact;
     private Button btAbout;
     private Button btEqpConfig;
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.config);

    btBack = (Button)findViewById(R.id.Btn_back);
    btRunConfig = (Button)findViewById(R.id.Btn_run_config);
    btEquipInfo = (Button)findViewById(R.id.Btn_info);
    btContact = (Button)findViewById(R.id.Btn_contact);
    btAbout = (Button)findViewById(R.id.Btn_about);
    btEqpConfig = (Button)findViewById(R.id.Btn_Eqiup_Config);
    
	 //返回
	 btBack.setOnClickListener(new OnClickListener(){
		 public void onClick(View v){
			/* Intent inBack = new Intent(Config.this, ECG_Android.class);
				startActivity(inBack);
				*/
			 finish();
		 }
	 });
	 //运行参数
	 btRunConfig.setOnClickListener(new OnClickListener(){
		 public void onClick(View v){
			 Intent inRunConfig = new Intent(Config.this, Run_Config.class);
			 startActivity(inRunConfig);
		 }
	 });
	  //设备信息
	 btEquipInfo.setOnClickListener(new OnClickListener(){
		 public void onClick(View v){
			 Intent inEquipInfo = new Intent(Config.this, Equip_Info.class);
			 startActivity(inEquipInfo);
		 }
	 });
	 //紧急联系人
	 btContact.setOnClickListener(new OnClickListener(){
		 public void onClick(View v){
			 Intent inContact = new Intent(Config.this, Contact.class);
			 startActivity(inContact);
		 }
	 });
	 //关于
	 btAbout.setOnClickListener(new OnClickListener(){
		 public void onClick(View v){
			 Intent inAbout = new Intent(Config.this, About.class);
			 startActivity(inAbout);
	
		 }
	 });
	 //设备发放
	 btEqpConfig.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intEquipConfig = new Intent(Config.this, Equip_Config.class);
				startActivity(intEquipConfig);
				
			}
     	
     });
	 
	 
    }
}
