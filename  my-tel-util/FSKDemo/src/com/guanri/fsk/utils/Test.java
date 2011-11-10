package com.guanri.fsk.utils;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.guanri.android.lib.utils.TypeConversion;
import com.guanri.fsk.conversion.FskCodeParams;
import com.guanri.fsk.conversion.FskDecode;
import com.guanri.fsk.conversion.FskDecodeResult;
import com.guanri.fsk.conversion.FskEnCodeResult;
import com.guanri.fsk.conversion.FskEncode;
import com.guanri.fsk.conversion.SourceQueue;
import com.guanri.fsk.conversion.WaveFileParams;
import com.guanri.fsk.view.CureLineBean;
import com.guanri.fsk.view.WaveAnalyse;

public class Test {

	
	static void putASC(byte[] Data) {
		int k = 0;
		//System.out.println("解码字符: " +  new String(Data, 0, Data.length));	
		for (int i = 0; i < Data.length; i ++) {
			if ((Data[i] >= 32)) Data[k ++] = Data[i];
		}
		System.out.println("解码字符: " +  new String(Data, 0, k));	
	}

	
	public static void encode(String fileName){
		
		FskEncode fskEncode = new FskEncode(fskCodeParams);
		//进行编码
		byte[] s = ("4safdafFskCodeParams5FskCodeParams").getBytes(); 

		FskEnCodeResult fskEnCodeResult = fskEncode.encode(s);

		try{
			final WaveFileParams waveFileParams = new WaveFileParams(fskCodeParams);
			waveFileParams.createFile(fileName);
			
			waveFileParams.appendData(fskEnCodeResult.code,fskEnCodeResult.index);
			
			waveFileParams.closeFile();
		}catch(Exception e){
			e.printStackTrace();
		}

		
	}
	static byte[] read = null;
	public static void decode(String fileName){
		
		//读取文件
		 
		try{
			File waveFile = new File(fileName);
			FileInputStream inf = new FileInputStream(waveFile);
			read = new byte[(int)waveFile.length()-44];
			inf.read(read, 0, 40);
			inf.read(read, 0, 4);
			System.out.println("文件大小："+waveFile.length());
			System.out.println("数据长度："+TypeConversion.bytesToInt(read,0));
			
			inf.read(read,0,(int)waveFile.length()-44);
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println("read"+read.length);
		final SourceQueue sourceQueue = new SourceQueue();
		sourceQueue.put(read);
		

		FskDecodeResult fskDecodeResult = new FskDecodeResult(true);
		final FskDecode fskDecode = new FskDecode(fskCodeParams,sourceQueue,fskDecodeResult);
		new Thread(){
			public void run(){
				fskDecode.beginDecode();
			}
		}.start();
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		fskDecode.stopDecode();
		byte[] dataResult = fskDecodeResult.getData();
		while(dataResult!=null){
			System.out.println("解码结果："+TypeConversion.byteTo0XString(dataResult,0,dataResult.length));//,0,fskDecodeResult.dataIndex));
			System.out.println("解码结果："+TypeConversion.byte2hex(dataResult,0,dataResult.length));
			putASC(dataResult);
			dataResult = fskDecodeResult.getData();
			
		}
		//绘图
		List<CureLineBean> list = new ArrayList<CureLineBean>();

		CureLineBean cureLineBean = new CureLineBean(fskDecodeResult.sourceValue,Color.RED);
		list.add(cureLineBean);

		cureLineBean = new CureLineBean(fskDecodeResult.singleFilter,Color.green);
		list.add(cureLineBean);


		cureLineBean = new CureLineBean(fskDecodeResult.boundFilter,Color.yellow);
		list.add(cureLineBean);

		cureLineBean = new CureLineBean(fskDecodeResult.maxAverage,Color.darkGray);
		list.add(cureLineBean);
		
		WaveAnalyse test = new WaveAnalyse(list);
		test.setVisible(true);			 
	}
	

	
	
	
	static FskCodeParams fskCodeParams = new FskCodeParams(2200,1200,11025,2,1200);
	public static void main(String[] args){
		String fileName = System.getProperty("user.dir")+"/in_record_1320831661311.wav";//"/"+new Date().getTime()+".wav";
		fileName = "C:/Users/Administrator/Desktop/wav/lin_1320832451395.wav";//"/"+new Date().getTime()+".wav";
		fileName = System.getProperty("user.dir")+"/"+new Date().getTime()+".wav";
		encode(fileName);
		decode(fileName); 
		
	}
}
