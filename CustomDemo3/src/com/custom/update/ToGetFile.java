package com.custom.update;

import java.io.File;

import com.custom.utils.MainApplication;

public class ToGetFile {
	
	public void downFileFromzip(String filePath){
		try{
			new ZipToFile().upZipFile(filePath,
					MainApplication.getInstance().getFilesDir().getAbsolutePath(),false,"custom"); 
			System.out.println(filePath);
			delteDownFile(filePath);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void delteDownFile(String filePath){
		try{
			File file = new File(filePath);
			if(file.exists())
				file.delete();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}	
	
}
