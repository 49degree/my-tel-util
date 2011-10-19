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
    
	 //����
	 btBack.setOnClickListener(new OnClickListener(){
		 public void onClick(View v){
			/* Intent inBack = new Intent(Config.this, ECG_Android.class);
				startActivity(inBack);
				*/
			 finish();
		 }
	 });
	 //���в���
	 btRunConfig.setOnClickListener(new OnClickListener(){
		 public void onClick(View v){
			 Intent inRunConfig = new Intent(Config.this, Run_Config.class);
			 startActivity(inRunConfig);
		 }
	 });
	  //�豸��Ϣ
	 btEquipInfo.setOnClickListener(new OnClickListener(){
		 public void onClick(View v){
			 Intent inEquipInfo = new Intent(Config.this, Equip_Info.class);
			 startActivity(inEquipInfo);
		 }
	 });
	 //������ϵ��
	 btContact.setOnClickListener(new OnClickListener(){
		 public void onClick(View v){
			 Intent inContact = new Intent(Config.this, Contact.class);
			 startActivity(inContact);
		 }
	 });
	 //����
	 btAbout.setOnClickListener(new OnClickListener(){
		 public void onClick(View v){
			 Intent inAbout = new Intent(Config.this, About.class);
			 startActivity(inAbout);
	
		 }
	 });
	 //�豸����
	 btEqpConfig.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intEquipConfig = new Intent(Config.this, Equip_Config.class);
				startActivity(intEquipConfig);
				
			}
     	
     });
	 
	 
    }
}
