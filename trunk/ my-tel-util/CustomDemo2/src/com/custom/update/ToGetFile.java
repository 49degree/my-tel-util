package com.custom.update;

import java.io.RandomAccessFile;

import com.custom.utils.Constant;
import com.custom.utils.MainApplication;

public class ToGetFile {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			System.out.print(System.getProperty("user.dir"));
			RandomAccessFile file = new RandomAccessFile(System.getProperty("user.dir")+"/test.txt","rw");
			
			file.setLength((int)(1.7*1024*1024*1024));//java.io.IOException磁盘空间不足。
//			file.write("aaaa".getBytes());
//			file.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public void downFileFromzip(String filePath){
		try{
			new ZipToFile().upZipFile(filePath,MainApplication.getInstance().getFilesDir().getAbsolutePath(),false,Constant.path);
		}catch(Exception e){
			
		}
		
	}
	
	
	
}
