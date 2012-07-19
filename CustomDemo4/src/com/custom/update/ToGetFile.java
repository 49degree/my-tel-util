package com.custom.update;

import java.io.File;

import com.custom.utils.Logger;

public class ToGetFile {
	private static final Logger logger = Logger.getLogger(ToGetFile.class);
	public void downFileFromzip(String filePath){
		try{
			new ZipToFile().upZipFile(filePath,false,"custom");
			logger.error(filePath);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void delteDownFile(String filePath){
		try{
			File file = new File(filePath);
			if(file.exists()){
				file.delete();
				logger.error(filePath);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}	
	
}
