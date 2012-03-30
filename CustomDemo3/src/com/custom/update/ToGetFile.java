package com.custom.update;

import com.custom.utils.MainApplication;

public class ToGetFile {
	public void downFileFromzip(String filePath){
		try{
			new ZipToFile().upZipFile(filePath,
					MainApplication.getInstance().getFilesDir().getAbsolutePath(),false,"custom"); 
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
}
