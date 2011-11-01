package com.guanri.fsk.utils;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

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
	public static void main1(String[] args){
//		for(int i=0;i<9;i++){
//			System.out.println(Math.round(Math.sin(i*1.2799081181291752)*Short.MAX_VALUE));
//		}
		try{
			RandomAccessFile randomAccessWriter = new RandomAccessFile("E:\\workgroup\\FSKDemo\\RIFF.txt", "rw");

	        randomAccessWriter.setLength(0); // Set file length to 0, to prevent unexpected behavior in case the file already existed
	        randomAccessWriter.writeBytes("RIFF");
	        randomAccessWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	static void putASC(byte[] Data) {
		int k = 0;
		//System.out.println("解码字符: " +  new String(Data, 0, Data.length));	
		for (int i = 0; i < Data.length; i ++) {
			if ((Data[i] >= 32)) Data[k ++] = Data[i];
		}
		System.out.println("解码字符: " +  new String(Data, 0, k));	
	}
	public static FskDecode fskDecode = null; 
	public static void main(String[] args)
	{
		FskCodeParams fskCodeParams = new FskCodeParams(2200,1200,11025,2,1200);
		FskEncode fskEncode = new FskEncode(fskCodeParams);
		//进行编码
		byte[] s = "  hello world".getBytes();
		byte[] bu = new byte[2+s.length];
		System.arraycopy(s, 0, bu, 0, s.length);
		FskEnCodeResult fskEnCodeResult = fskEncode.encode(bu);
		//编码结束
		//保存wave文件
		WaveFileParams waveFileParams = new WaveFileParams(fskCodeParams,fskEnCodeResult);
		byte[] waveByte = waveFileParams.parseWaveToByte();
		String fileName = System.getProperty("user.dir")+"/in_record_1319252344687.wav";
		try{
			File waveFile = new File(fileName);
			if(!waveFile.exists()){
				waveFile.createNewFile();
			}
			FileOutputStream fout = new FileOutputStream(waveFile);
			fout.write(waveByte);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//进行解码
		byte[] read = null; 
		try{
			File waveFile = new File(fileName);
			FileInputStream inf = new FileInputStream(waveFile);
			read = new byte[(int)waveFile.length()-44];
			inf.read(read, 0, 44);
			inf.read(read,0,(int)waveFile.length()-44);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//进行解码
		SourceQueue sourceQueue = new SourceQueue();
		sourceQueue.put(read);
		FskDecodeResult fskDecodeResult = new FskDecodeResult(true);
		fskDecode = new FskDecode(fskCodeParams,sourceQueue,fskDecodeResult);
		new Thread(){
			public void run(){
				fskDecode.beginDecode();
			}
		}.start();
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		fskDecode.isContinue = false;
		System.out.println(new String(fskDecodeResult.data,0,fskDecodeResult.dataIndex));
		
		
		//进行解码
//		fileName = "E:/test/in_1312879664569.wav";
//		read = null; 
//		try{
//			File waveFile = new File(fileName);
//			FileInputStream inf = new FileInputStream(waveFile);
//			read = new byte[(int)waveFile.length()-44];
//			inf.read(read, 0, 44);
//			inf.read(read,0,(int)waveFile.length()-44);
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		sourceQueue = new SourceQueue();
//		sourceQueue.put(read);
//		fskDecode = new FskDecode(fskCodeParams,sourceQueue,fskDecodeResult);
//		
//		new Thread(){
//			public void run(){
//				fskDecode.beginDecode();
//			}
//		}.start();
//		
//		
//		try{
//			Thread.sleep(1000);
//		}catch(InterruptedException e){
//			e.printStackTrace();
//		}
//		fskDecode.isContinue = false;
//		putASC(fskDecodeResult.data);
//		System.out.println(new String(fskDecodeResult.data));

		
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
}
