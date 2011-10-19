package com.xys.ecg.activity;

import com.xys.ecg.file.EcgDataFileOperate;
import com.xys.ecg.file.EcgXmlFileOperate;
import com.xys.ecg.log.Logger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Equip_Info extends Activity {
	private Button btBack;
	private TextView tvVersion;
	private TextView tvBTAddress;
	private TextView tvUserName;
	public static Logger logger = Logger.getLogger(Equip_Info.class);
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.equip_info);
	        btBack = (Button)findViewById(R.id.Btn_back);
	        tvVersion = (TextView)findViewById(R.id.Text_version);
	        tvBTAddress = (TextView)findViewById(R.id.Text_bt_address);
	        tvUserName = (TextView)findViewById(R.id.Text_user);
	        String strVersion = null;
	        EcgXmlFileOperate xmlOperate =  new EcgXmlFileOperate("ECGConfig",ECGApplication.getInstance());
			try {			
				strVersion = xmlOperate.selectEcgXmlNode("Version").getParentNodeAttributeValue();
				xmlOperate.close();
			} catch (Exception e) {
				//∂¡»°XML ß∞‹
				logger.debug("Read software in XML faild. " + e.getMessage());
			}
	        tvVersion.setText(strVersion);
	        
	        String strBTAdress = null;
	        String strUserName = null;
	        EcgXmlFileOperate btOperate = new EcgXmlFileOperate("Device", ECGApplication.getInstance());
	        try{
	        	strBTAdress = btOperate.selectEcgXmlNode("CollectorBtAddr").getParentNodeAttributeValue();
	        	strUserName = btOperate.selectEcgXmlNode("PatientName").getParentNodeAttributeValue();
	        	btOperate.close();
	        	
	        }catch(Exception e){
	        	logger.debug("Read BTAdress in XML faild. " + e.getMessage());
	        }
	        tvBTAddress.setText(strBTAdress);
	        tvUserName.setText(strUserName);
	        
	        btBack.setOnClickListener(new View.OnClickListener(){				
				public void onClick(View v) {
				finish();
				}
			});
	 }

}
