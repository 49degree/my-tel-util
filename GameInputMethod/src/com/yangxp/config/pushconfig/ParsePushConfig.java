package com.yangxp.config.pushconfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.yangxp.config.MainApplication;
import com.yangxp.config.business.FileController;
import com.yangxp.config.common.TypeConversion;
import com.yangxp.config.exception.ConfigStringErrorException;
import com.yangxp.config.exception.HaveAppConfigException;

public class ParsePushConfig {
	String TAG = "ParsePushConfig";
	private String mPackName;
	public final static String CONFIGFILE_ASSETS_PATH = "gamepad/";
	public ParsePushConfig(String packName){
		mPackName = packName;
		parse();
	}
	
	private void parse(){

		//查询是否存在配置文件
		Context context = null;
		try {
			try{
				context = MainApplication.getInstance().createPackageContext(mPackName,
						Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
			}catch(Exception e){ 
				e.printStackTrace();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			PackageInfo info = MainApplication.getInstance().getPackageManager().getPackageInfo(mPackName, 0);  
			
			 InputStream fin = context.getAssets().open(CONFIGFILE_ASSETS_PATH+mPackName+"_"+info.versionCode+".data");
			 Log.e(TAG,"info:"+info.packageName);
			 
				BufferedReader r = new BufferedReader(new InputStreamReader(fin,TypeConversion.DEFAULT_ENCODE));
				String line ;
				StringBuffer configString = new StringBuffer();
				while((line = r.readLine())!=null){
					configString.append(line);
				}
				try {
					new FileController(configString.toString()).saveOrUpdate();
				} catch (ConfigStringErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HaveAppConfigException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

	}
}
