package com.guanri.fsk.utils;

import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.guanri.fsk.conversion.FskCodeParams;
import com.guanri.fsk.conversion.FskDecode;
import com.guanri.fsk.conversion.FskDecodeResult;
import com.guanri.fsk.conversion.FskEnCodeResult;
import com.guanri.fsk.conversion.FskEncode;
import com.guanri.fsk.conversion.SourceQueue;
import com.guanri.fsk.pc.AudioOperator;
import com.guanri.fsk.pc.AudioOperator.AudioReceiveDataHandler;

public class AutoTest {
	static FskCodeParams fskCodeParams = new FskCodeParams(2200,1200,11025,2,1200);
	/**
	 * 解码
	 * @param begin
	 * @param sourceQueue
	 */
	static boolean running = true; 
	static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public  static void decode(SourceQueue sourceQueue) throws Exception{
		final RandomAccessFile randomAccessWriter = new RandomAccessFile(System.getProperty("user.dir")+"/auto_test_result.text", "rw");
		
		
		final FskDecodeResult fskDecodeResult = new FskDecodeResult(true);
		final FskDecode fskDecode = new FskDecode(fskCodeParams,sourceQueue,fskDecodeResult);
		new Thread(){
			public void run(){
				fskDecode.beginDecode();
			}
		}.start();

		
		new Thread(){
			public void run(){
				String receivStr = "";
				while(running){
					if(fskDecodeResult.getDataIndex()>0){
						receivStr += new String(fskDecodeResult.getData()); 
						if(!receivStr.equals(sendString)){
							try{
								System.out.println("接收字符串："+receivStr);
								randomAccessWriter.write((sf.format(new Date())+"接收字符串错误："+receivStr+"/n").getBytes());
							}catch(Exception e){
								
							}
						}
					}
					try{
						Thread.sleep(1000);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				fskDecode.isContinue = false;
			}
		}.start();	
	}
	
	
	static String sendString = "receivStr += new String(fskDecodeResult.data,2,fskDecodeResult.dataIndex-2)"; 
	
	
	public static void autoTest(){
		final AudioOperator audioOperator = new AudioOperator(fskCodeParams);
		final SourceQueue sourceQueue = new SourceQueue();
		final FskEncode fskEncode = new FskEncode(fskCodeParams);
		//接收数据回调方法
		audioOperator.setAudioReceiveDataHandler(new AudioReceiveDataHandler(){
			public void handler(byte[] data){
				sourceQueue.put(data);
			}
		});
		//启动解码线程
		try{
			decode(sourceQueue);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		//发送数据线程
		Thread sendTask = new Thread(){
			public void run(){
				while(running){
					System.out.println("发送数据："+sendString);
					FskEnCodeResult  fskEnCodeResult = fskEncode.encode(sendString.getBytes());
					audioOperator.playAudio(fskEnCodeResult.code,fskEnCodeResult.index);
					try{
						Thread.sleep(1000);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		};
		sendTask.start();
	}
	
	public static void main(String[] args){
		autoTest();
	}
}
