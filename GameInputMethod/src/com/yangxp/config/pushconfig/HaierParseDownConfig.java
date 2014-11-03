package com.yangxp.config.pushconfig;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.yangxp.config.business.HaierFileController;
import com.yangxp.config.common.TypeConversion;
import com.yangxp.config.exception.ConfigStringErrorException;
import com.yangxp.config.exception.HaveAppConfigException;

public class HaierParseDownConfig {
	String TAG = "HaierParseDownConfig";
	private String mFilePathAndName;
	private HaierFileController mHaierFileController;
	
	public HaierParseDownConfig(String filePathAndName){
		mFilePathAndName = filePathAndName;
		parse();
	}
	
	public void parse(){

		//查询是否存在配置文件
		try {
			InputStream fin = new FileInputStream(mFilePathAndName);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin,
					TypeConversion.DEFAULT_ENCODE));
			String line;
			StringBuffer configString = new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				configString.append(line);
			}
			try {
				mHaierFileController = new HaierFileController(configString.toString());
			} catch (ConfigStringErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HaveAppConfigException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	public HaierFileController getFileController() {
		return mHaierFileController;
	}

	
}
