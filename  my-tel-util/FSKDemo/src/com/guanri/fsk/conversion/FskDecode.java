package com.guanri.fsk.conversion;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.guanri.fsk.utils.TypeConversion;
import com.guanri.fsk.view.CureLineBean;
import com.guanri.fsk.view.WaveAnalyse;

public class FskDecode {
	private FskCodeParams fskCodeParams = null;//采样参数
	private SourceQueue sourceQueue = null;//数据员队列
	FskDecodeResult fskDecodeResult = null;//结果数据对象
	
	private int[] singleTArray = null;//频率较高的波形在一个周期内的采样点数组
	private int[] boundTArray = null;//一波特周期内的采样点数组
	private int[] singleMaxArray = null;//固定数量的最大值数组
	private int[] singleMinArray = null;//固定数量的最小值数组
	private int singleMaxArrayLength = 40;//固定数量的最大值数组长度
	private float splitParmats = 0.58f;//0，1分割参数
	
	private int singleTlength0 = 0;//0信号在一个周期内的采样点数 
	private int singleTlength1 = 0;//1信号在一个周期内的采样点数 
	private int singleTArraylength = 0;//频率较高的波形在一个周期内的采样点数
	
	private int boundTlength = 0;//1波特周期内的采样点数 
	
	public FskDecode(FskCodeParams fskCodeParams,SourceQueue sourceQueue,FskDecodeResult fskDecodeResult){
		this.fskCodeParams = fskCodeParams;// new FskCodeParams(2200,1200,11025,2,1200);
		this.sourceQueue = sourceQueue;
		this.fskDecodeResult = fskDecodeResult;
		this.init();
	}
	
	/**
	 * 根据参数FskCodeParams初始化相关数组信息
	 */
	private void init(){
		singleTlength0 = fskCodeParams.getSampleF()/fskCodeParams.getF0();//0信号在一个周期内的采样点数 
		singleTlength1 = fskCodeParams.getSampleF() /fskCodeParams.getF1();//1信号在一个周期内的采样点数 
		singleTArraylength = singleTlength0<singleTlength1?singleTlength0:singleTlength1;
		singleTArray = new int[singleTArraylength];//频率较高的波形在一个周期内的采样点数组
		
		boundTlength = fskCodeParams.getSampleF()/ fskCodeParams.getBoundRate();
		boundTArray = new int[boundTlength];//一波特周期内的采样点数组
			
		singleMaxArray = new int[singleMaxArrayLength];//固定数量的最大值数组
		singleMinArray = new int[singleMaxArrayLength];//固定数量的最小值数组
		
		reviseNextSigPos();
	}
	
	/**
	 * 初始化采样点信息
	 */
	private int singleCount = 0;
	private int nextSinglePosition = 0;
	private int position = 0;
	private boolean started = false;
	private void reviseNextSigPos(){
    	singleCount = 0;
    	nextSinglePosition = 0;
    	position = 0;
    	started = false;
    	calcNextSigPos();
    }	
	
	/**
	 * 计算下一个采样点的信息
	 */
	private void calcNextSigPos(){
		nextSinglePosition = nextSinglePosition +  boundTlength;
		singleCount++;
	    if (singleCount > fskCodeParams.getBoundRate()){
	    	singleCount -= fskCodeParams.getBoundRate();
	    	nextSinglePosition -= position;
	    	position = 0;
	    }
	}
	
	/**
	 * 计算最大平均值
	 */
	private int lastValue = 0;
	private int lastValue1 = 0;
	private int lastValue2 = 0;
	private int maxValue = 0;
	private int maxValueIndex = 0;
	private int totalMaxValue = 0;
	private int averageMaxValue = 0;
	private boolean haveMaxValueFull = false;
	private void calcMaxAverage(int sampleValue) {
		lastValue2 = lastValue1;
		lastValue1 = lastValue;
		lastValue = sampleValue;
		if (lastValue <= lastValue1 && lastValue2 <= lastValue1) { // 判断是否有最大值
			if (lastValue1 < 1000)
				return;
//			if (maxValue > 0) {
//				if (lastValue1 < (maxValue * 0.4))
//					return;
//				if (lastValue1 > (maxValue * 1.8))
//					return;
//			}
			//求最大平均值
			maxValue = lastValue1;
			maxValueIndex = maxValueIndex % singleMaxArrayLength;
			
			totalMaxValue = totalMaxValue - singleMaxArray[maxValueIndex] + maxValue;
			singleMaxArray[maxValueIndex] = maxValue;
			maxValueIndex++;

			if (haveMaxValueFull) {
				averageMaxValue = totalMaxValue / singleMaxArrayLength;
			} else {
				haveMaxValueFull = maxValueIndex >= singleMaxArrayLength;
				averageMaxValue = totalMaxValue / maxValueIndex;
			}
		}
	}

	/**
	 * 进行虑波
	 */
	private int singleTIndex = 0;
	private int singleTTotalValue = 0;
	private int boundTIndex = 0;
	private int boundTTotalValue = 0;
	private boolean isSingle0 = false;
	double splitValue = 0;
	
	private int singleFilterValue = 0;
	private int boundFilterValue = 0;
	
	private int modifyState = 0 ;
	public boolean decode(int sampleValue) {
		boolean result = false;
		//判断信号是否失真被削平
//		if(Math.abs(sampleValue)<=32750){
//			modifyState =0 ;
//		}else if(Math.abs(sampleValue)>32500){
//			if(modifyState==0){
//				sampleValue = Math.round(0.75f*sampleValue);
//				modifyState++;
//			}else if(modifyState==1){
//				modifyState++;
//			}else if(modifyState==2){
//				sampleValue = Math.round(0.75f*sampleValue);
//				modifyState++;
//			}else if(modifyState==3){
//				sampleValue = Math.round(0.55f*sampleValue);
//				modifyState=0;
//			}
//			
//		}
		

		
		
		
	    //根据信号波中0或者1的较大频率的波的周期进行滤波
		singleTIndex = singleTIndex % singleTArraylength;
		singleTTotalValue = singleTTotalValue - singleTArray[singleTIndex] + sampleValue;
		singleTArray[singleTIndex] = sampleValue;
		singleTIndex ++;
		singleFilterValue = singleTTotalValue / singleTArraylength;
		singleFilterValue = Math.abs(singleFilterValue);
	   
	   // 在波的周期滤波基础上进行波特周期滤波
	    boundTIndex = boundTIndex % boundTlength;
	    boundTTotalValue = boundTTotalValue - boundTArray[boundTIndex] + singleFilterValue;
	    boundTArray[boundTIndex] = singleFilterValue;
	    boundTIndex ++;
	    boundFilterValue = boundTTotalValue / boundTlength;
	    
	    calcMaxAverage(boundFilterValue);//求最大平均值
	    
	    //记录解码过程数据，是否记录在addSourceValue方法中进行判断
	    if(fskDecodeResult.isRecordDecodeInfo()){
	    	fskDecodeResult.addSourceValue(sampleValue, singleFilterValue, boundFilterValue, Math.round(averageMaxValue*splitParmats));
	    }
	    
	    //boolean isZero = com.guanri.fsk.utils.Demo.decode(sampleValue);
	    
	    double splitValue = averageMaxValue*splitParmats;
	    if (! started) {
	    	if (boundFilterValue < splitValue)
	    	//if(isZero)
	    		position ++;
	    	else
	    		position = 0;

	      if (position >= (boundTlength - singleTArraylength + 1)) {
	    	  reviseNextSigPos();
	    	  started = true;
	    	  isSingle0 = true;
	    	  result = true;
	      }
	    } else {
	    	position ++;
	    	if (position >= nextSinglePosition) {
	    		calcNextSigPos();
	    		isSingle0 = boundFilterValue < splitValue;
	    		result = true;
	    	}
	    }
		
		return result;
	}
	
	/**
	 * 解码
	 */
	private byte decodeValue = 0;
	private byte state = s_Start;
	private static final byte s_Start = 0;
	private static final byte s_b0 = 1;
	private static final byte s_b1 = 2;
	private static final byte s_b2 = 3;
	private static final byte s_b3 = 4;
	private static final byte s_b4 = 5;
	private static final byte s_b5 = 6;
	private static final byte s_b6 = 7;
	private static final byte s_b7 = 8;
	private static final byte s_Stop = 9;
	public boolean decodeValue(int sampleValue){
		boolean result = false;
		if (decode(sampleValue)) {
			switch (state) {
			case s_Start:
				if (isSingle0) {
					state ++;
					decodeValue = 0;
				} else
					reviseNextSigPos();
				break;
			case s_b0:
			case s_b1:
			case s_b2:
			case s_b3:
			case s_b4:
			case s_b5:
			case s_b6:
			case s_b7:
				state ++;
				decodeValue >>= 1;
	    		if (! isSingle0) decodeValue |= 0x80; else decodeValue &= (0x7F);
				break;
			case s_Stop:
		         result = !isSingle0;
		         state = s_Start;
		         reviseNextSigPos();
				break;
			default:
				state = s_Start;						
			}
		}
		return result;
	}
	
	/**
	 * 开始解码
	 */
	public boolean isContinue = true;//是否停止
	public void beginDecode(){
		while(isContinue){
			if(sourceQueue.size()<=0){
				try{
					Thread.sleep(100);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				//isContinue = false;
			}else{
				byte[] source = sourceQueue.get();
				if(fskCodeParams.getSampleByteLength()==2){//采样精度为2byte
					for(int i=0;i<source.length/2;i++){
						
						if(decodeValue(TypeConversion.bytesToShort(source, i*2))){
							byte temp = decodeValue;
							decodeValue = 0;
							fskDecodeResult.addResult(temp);//记录解码结果
							
						}
					}
				}
			
			}
		}
	}
	
	
	
	
	/**
	 * 测试代码
	 * @param args
	 */
	public static void main(String[] args){
		
		//进行解码
		FskCodeParams fskCodeParams = new FskCodeParams(2200,1200,11025,2,1200);
		final SourceQueue sourceQueue = new SourceQueue();
		final FskDecodeResult fskDecodeResult = new FskDecodeResult(true);
		
		final FskDecode fskDecode = new FskDecode(fskCodeParams,sourceQueue,fskDecodeResult);
		fskDecode.splitParmats = 0.55f;//0，1分割参数
		new Thread(){
			public void run(){
				fskDecode.beginDecode();
			}
		}.start();
		
		new Thread(){
			public void run(){
				String fileName = "F:/eclipse3.5/FSKDemo/test1.wav";
				byte[] read = new byte[5292]; 
				try{
					File waveFile = new File(fileName);
					FileInputStream inf = new FileInputStream(waveFile);
					//read = new byte[(int)waveFile.length()-44];
					//System.out.println("文件长度："+waveFile.length());
					int readLenght = 0;
					readLenght = inf.read(read, 0, 44);
					//inf.read(read,0,(int)waveFile.length()-44);
					while((readLenght=inf.read(read,0,5292))>-1){
						//System.out.println("文件长度："+readLenght);
						if(readLenght<read.length){
							byte[] temp = new byte[readLenght];
							System.arraycopy(read, 0, temp, 0, readLenght);
							sourceQueue.put(temp);
						}else{
							sourceQueue.put(read);
						}
						
						
						try{
							Thread.sleep(1000);
						}catch(InterruptedException e){
							e.printStackTrace();
						}
						
						if(fskDecodeResult.data!=null){
							
							System.out.println("解码HEX："+TypeConversion.byte2hex(fskDecodeResult.data));
							System.out.println("解码结果："+getASCString(fskDecodeResult.data,fskDecodeResult.dataIndex));
							System.out.println("解码结果2："+new String(fskDecodeResult.data,0,fskDecodeResult.dataIndex));
						}
					}
					
					
					fskDecode.isContinue = false;
					//绘图
					List<CureLineBean> list = new ArrayList<CureLineBean>();

					CureLineBean cureLineBean = new CureLineBean(fskDecodeResult.sourceValue,Color.RED);
					list.add(cureLineBean);
					
					System.out.println("采样点数量++++++++++++++++++++：："+fskDecodeResult.sourceIndex);
					
					cureLineBean = new CureLineBean(fskDecodeResult.singleFilter,Color.green);
					list.add(cureLineBean);

					cureLineBean = new CureLineBean(fskDecodeResult.boundFilter,Color.yellow);
					list.add(cureLineBean);

					cureLineBean = new CureLineBean(fskDecodeResult.maxAverage,Color.darkGray);
					list.add(cureLineBean);

					WaveAnalyse test = new WaveAnalyse(list);
					test.setVisible(true);		

				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
		



		

	}
	
	public static String getASCString(byte[] data,int dataIndex) {
		byte[] temp = new byte[dataIndex];
		System.arraycopy(data, 0, temp, 0, temp.length);
		int k = 0;
		//System.out.println("解码字符: " +  new String(Data, 0, Data.length));	
		for (int i = 0; i < temp.length; i ++) {
			if ((temp[i] >= 32)) temp[k ++] = temp[i];
		}
		return new String(temp, 0, k);	
	}

}
