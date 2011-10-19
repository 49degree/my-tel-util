package com.xys.ecg.activity;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;


import org.xml.sax.SAXException;

import com.xys.ecg.file.EcgXmlFileOperate;
import com.xys.ecg.sqlite.UserDB;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.xys.ecg.bean.XmlNodeEntity;
import com.xys.ecg.business.EcgBusiness;

public class Run_Config extends Activity {
     private EditText edit_font = null;       //文字大小编辑框
     private EditText edit_lead = null;       //分析导联编辑框
     private EditText edit_save = null;       //省点模式编辑框
     private EditText edit_store = null;      //可存储空间
	 private CheckBox check_flatten = null;   //基线CheckBox
     private CheckBox check_smoothing = null;   //平滑CheckBox
     private CheckBox check_sound = null;       //声音CheckBox
     private CheckBox check_shock = null;       //震动CheckBox
     private Button btn_back = null;            //返回控件
     private EcgXmlFileOperate efo = null;
     
     
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.run_config);
	        
	        efo = new EcgXmlFileOperate("ECGConfig",Run_Config.this);
	        
	        edit_font = (EditText)findViewById(R.id.xml_font);
	        edit_lead = (EditText)findViewById(R.id.xml_lead);
	        edit_save = (EditText)findViewById(R.id.xml_save);
	        edit_store = (EditText)findViewById(R.id.xml_store);
	        check_flatten = (CheckBox)findViewById(R.id.xml_flatten);
	        check_smoothing = (CheckBox)findViewById(R.id.xml_smoothing);
	        check_sound = (CheckBox)findViewById(R.id.xml_sound);
	        check_shock = (CheckBox)findViewById(R.id.xml_shock);
	        btn_back = (Button)findViewById(R.id.Btn_back);
	        
	        try {
	        	edit_font.setText(efo.selectEcgXmlNode("FontSize").getParentNodeAttributeValue());
	        	
	        	
	        	edit_lead.setText(efo.selectEcgXmlNode("Lead").getParentNodeAttributeValue());
	        	edit_save.setText(efo.selectEcgXmlNode("EconomyrMode").getParentNodeAttributeValue());
	        	edit_store.setText(efo.selectEcgXmlNode("MPLowSpace").getParentNodeAttributeValue());
				
	        	if(efo.selectEcgXmlNode("BaseLineDrift").getParentNodeAttributeValue().equals("TRUE"))
	        	{
	        	 check_flatten.setChecked(true);
	        	}
	        	if(efo.selectEcgXmlNode("RemoveNoise").getParentNodeAttributeValue().equals("TRUE"))
	        	{
	        		check_smoothing.setChecked(true);
	        	}
			    if(efo.selectEcgXmlNode("AlarmSound").getParentNodeAttributeValue().equals("TRUE"))
			    {
			    	check_sound.setChecked(true);
			    }
				if(efo.selectEcgXmlNode("AlarmShock").getParentNodeAttributeValue().equals("TRUE"))
				{
					check_shock.setChecked(true);
				}
				
				
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
			}
	 
	        
	 }
	
	// 监听键盘事件
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				
				 efo = new EcgXmlFileOperate("ECGConfig",Run_Config.this);	
				
		         String stredit_font = efo.selectEcgXmlNode("FontSize").getParentNodeAttributeValue();
		         String	stredit_lead = efo.selectEcgXmlNode("Lead").getParentNodeAttributeValue();
		         String	stredit_save = efo.selectEcgXmlNode("EconomyrMode").getParentNodeAttributeValue();
		         String	stredit_store = efo.selectEcgXmlNode("MPLowSpace").getParentNodeAttributeValue();
	        	 String strflattenup ="";
					if(check_flatten.isChecked())
					{
						strflattenup = "TRUE";
					}
					else
					{
						strflattenup = "FALSE";
					}
					
					String strsmoothingup = "";
					if(check_smoothing.isChecked())
					{
						strsmoothingup = "TRUE";
					}
					else
					{
						strsmoothingup = "FALSE";
					}
					
					String strsoundup = "";
					if(check_sound.isChecked())
					{
						strsoundup = "TRUE";
					}
					else
					{
						strsoundup = "FALSE";
					}
					
					
					String strshockup = "";
					if(check_shock.isChecked())
					{
						strshockup = "TRUE";
					}
					else
					{
						strshockup = "FALSE";
					}
		         
		         if(edit_font.getText().toString().trim().equals(stredit_font) &&
		            edit_lead.getText().toString().trim().equals(stredit_lead) &&
		            edit_save.getText().toString().trim().equals(stredit_save) &&
		            edit_store.getText().toString().trim().equals(stredit_store) &&
		            efo.selectEcgXmlNode("BaseLineDrift").getParentNodeAttributeValue().equals(strflattenup) &&
				    efo.selectEcgXmlNode("RemoveNoise").getParentNodeAttributeValue().equals(strsmoothingup) &&
					efo.selectEcgXmlNode("AlarmSound").getParentNodeAttributeValue().equals(strsoundup)      &&
				    efo.selectEcgXmlNode("AlarmShock").getParentNodeAttributeValue().equals(strshockup)
		         ){
							efo.close();
							finish();

		         }
		         else			
		         {
				
							final AlertDialog.Builder builder = new Builder(Run_Config.this);
							builder.setTitle("提示");
							builder.setMessage("需要保存设置吗 ?").setPositiveButton("是",new DialogInterface.OnClickListener() {			
								public void onClick(DialogInterface dialog, int which) {
									try {
										if(efo == null)
										{
											efo = new EcgXmlFileOperate("ECGConfig",Run_Config.this);
										}
										efo.updateEcgXmlCurrentNode("FontSize", edit_font.getText().toString());
										efo.updateEcgXmlCurrentNode("Lead", edit_lead.getText().toString());
										efo.updateEcgXmlCurrentNode("EconomyrMode", edit_save.getText().toString());
										efo.updateEcgXmlCurrentNode("MPLowSpace", edit_store.getText().toString());
										String strflatten ="";
										if(check_flatten.isChecked())
										{
											strflatten = "TRUE";
										}
										else
										{
											strflatten = "FALSE";
										}
										efo.updateEcgXmlCurrentNode("BaseLineDrift",strflatten);
										
										String strsmoothing = "";
										if(check_smoothing.isChecked())
										{
											strsmoothing = "TRUE";
										}
										else
										{
											strsmoothing = "FALSE";
										}
										efo.updateEcgXmlCurrentNode("RemoveNoise",strsmoothing);
										
										String strsound = "";
										if(check_sound.isChecked())
										{
											strsound = "TRUE";
										}
										else
										{
											strsound = "FALSE";
										}
										efo.updateEcgXmlCurrentNode("AlarmSound",strsound);
										
										String strshock = "";
										if(check_shock.isChecked())
										{
											strshock = "TRUE";
										}
										else
										{
											strshock = "FALSE";
										}
										efo.updateEcgXmlCurrentNode("AlarmShock",strshock);
										
									} catch (Exception e) {
										
									}
									efo.close();
									finish();
								}
							 }).setNegativeButton("否", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									efo.close();
			                        finish();
								}
							}).show();
							
					}
			}
			return false;
		
		}

}
