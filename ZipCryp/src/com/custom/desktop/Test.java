package com.custom.desktop;

import java.awt.SystemTray;
import java.io.File;
import java.io.RandomAccessFile;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			System.out.print(System.getProperty("user.dir"));
			RandomAccessFile file = new RandomAccessFile(System.getProperty("user.dir")+"/test.txt","rw");
			
			file.setLength((int)(0.5*1024*1024*1024));//java.io.IOException磁盘空间不足。
//			file.write("aaaa".getBytes());
//			file.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
