package com.yangxp.config.pushconfig;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.yangxp.config.business.FileController;
import com.yangxp.config.common.TypeConversion;
import com.yangxp.config.exception.ConfigStringErrorException;
import com.yangxp.config.exception.HaveAppConfigException;

public class ParseDownConfig {
	String TAG = "ParsePushConfig";
	private String mFilePathAndName;
	
	public ParseDownConfig(String filePathAndName){
		mFilePathAndName = filePathAndName;
		parse();
	}
	
	private void parse(){

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
				new FileController(configString.toString()).saveOrUpdate();
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
}
