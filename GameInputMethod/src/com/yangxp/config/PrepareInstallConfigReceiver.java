package com.yangxp.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yangxp.config.common.TypeConversion;
import com.yangxp.config.pushconfig.ParseDownConfig;
import com.yangxp.config.pushconfig.ParsePushConfig;

public class PrepareInstallConfigReceiver extends BroadcastReceiver{
	static String TAG = "PrepareInstallConfigReceiver";
	public final static String CONFIGFILE_FILE_PATH = "/mnt/sdcard/gameconfig/";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
			String packageName = intent.getDataString();
			packageName = packageName.substring(8);
			saveConfigFile(packageName);
			Log.i("Test", "ACTION_PACKAGE_ADDED---------------" + intent.getExtras());
			ParsePushConfig p = new ParsePushConfig(packageName);
		}else if(intent.getAction().equals("com.qucii.gameconfig.gamefile")){
			String fileName = intent.getExtras().getString("file_name");
			Log.i("Test", "---------------" + "com.qucii.gameconfig.gamefile" + fileName);
			ParseDownConfig p = new ParseDownConfig(CONFIGFILE_FILE_PATH+fileName);
		}else if (intent.getAction().equals(Intent.ACTION_PACKAGE_INSTALL)) {
			String packageName = intent.getDataString();
			Log.i("Test", "ACTION_PACKAGE_INSTALL---------------" + packageName);
		}else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
			String packageName = intent.getDataString();
			Log.i("Test", "---------------" + "PACKAGE_REMOVED" + packageName);
		}
	}
	
	private void saveConfigFile(String appName){
		File file = new File("/mnt/sdcard/installed.txt");
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		FileOutputStream out = null;
		try {
			boolean append = true;
			out = new FileOutputStream(file,append);
			out.write(TypeConversion.stringToAscii(appName));
			out.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(out!=null)
				try {
					out.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

}
