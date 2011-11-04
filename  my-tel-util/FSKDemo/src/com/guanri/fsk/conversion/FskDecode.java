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
	private float splitParmats = 0.65f;//0，1分割参数
	
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
		

		
		
		//System.out.println("sampleValue:"+sampleValue);
		
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
	    
	    splitValue = averageMaxValue*splitParmats;
	    if (! started) {
	    	if(!decodeHeader()){
	    		return result;
	    	}
	    	
	    	if (boundFilterValue < splitValue)
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
	
	
	
	
	//判断前导码 80对01和40个1
	private int headerSingle = 2;
	private int header01Index = 0;
	private int header1Index = 0;
	private boolean hasHeader = false;
	private boolean startHeader = false;
	private boolean decodeHeader(){
		boolean result = false;
    	
		if (startHeader) {
			position++;
			if (position >= nextSinglePosition) {
				calcNextSigPos2(false);
				isSingle0 = boundFilterValue < splitValue;
			}
		} else {
			if (boundFilterValue < splitValue)
				position++;
			else
				position = 0;
			if (position >= (boundTlength - singleTArraylength + 1)) {
				calcNextSigPos2(true);
				startHeader = true;
				isSingle0 = true;
			}
		}
		
		
    	//判断前导码 80对01和40个1
		if (!hasHeader) {
			System.out.println(header01Index+":"+header1Index+":"+hasHeader);
			if(startHeader){
				if (header01Index < 80) {
					if (headerSingle > 0 && isSingle0) {
						headerSingle = 0;
						header01Index++;
					} else if (headerSingle == 0 && isSingle0) {
						headerSingle = 1;
						header01Index++;
					} else {
						header01Index = 0;
					}
				} else {
					if (isSingle0) {
						header1Index++;
					} else {
						header1Index = 0;
					}
					if (header1Index >= 10) {
						hasHeader = true;
					}
				}
			}

		}else{
    		result = true;
    	}
    	return result;
	}
	
	
	/**
	 * 计算下一个采样点的信息
	 */
	int modeValue = 0;
	private void calcNextSigPos2(boolean isStart){
		if(isStart){
			nextSinglePosition = boundTlength-((boundTlength - singleTArraylength)/2);
		}else{
			//波特周期/采样周期=(1/boundRate)/(1/sampleF)=sampleF/boundRate表示一波特周期要采集几个点,
			//因为采样点个数只能取整数，所以会有余数fskEnCodeResult.modeValue，为了增加可靠性，把余数加入下一次进行计算
			nextSinglePosition = (fskCodeParams.getSampleF()+modeValue)/fskCodeParams.getBoundRate();
			modeValue = (fskCodeParams.getSampleF()+modeValue)%fskCodeParams.getBoundRate();
		}
		position = 0;
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
					
//					short[] data = new short[source.length/2];
//					for(int i=0;i<source.length/2;i++){
//						data[i] = TypeConversion.bytesToShort(source, i*2);
//						
//					}
//					new Analyze().analyze1(data, data.length);
				}
				
				
			
			}
		}
	}
	
	
	
	public class Analyze{
		private int mDebug = 1;
		private int stat;
		private int sign;
		private int sSign;
		private int oSign;
		private int width;
		private int tWidth;
		private int widthBuf[] = new int[4];
		private int timing;
		private int mTiming;
		private int mLimitWidth;
		
		private int mHighLowThreshold;
		
		private int bData;
	    private void analyze1(short aword0[], int i)
	    {
	        int j = 0;
	        int ai[];
	        int ai1[];
	        int ai2[];
	        int k;
	        int l;
	        int i1;
	        int k1;
	        int l1;
	        int i2;
	        int j2;
	        int k2;
	        int l2;
	        int i3;
	        int j3;
	        int k3;
	        int l3;
	        int i4;
	        int j4;
	        int k4;
	        int l4;
	        do
	        {
	            if(j >= i)
	                return;
	            
	    	    //记录解码过程数据，是否记录在addSourceValue方法中进行判断
	    	    if(fskDecodeResult.isRecordDecodeInfo()){
	    	    	fskDecodeResult.addSourceValue(aword0[j], 0, 0, 0);
	    	    }
	            if(stat == 0 && (aword0[j] > 16384 || -aword0[j] > 16384))
	            {
	                stat = 1;

	                if(aword0[j] > 0)
	                    sSign = 1;
	                else
	                    sSign = -1;
	                width = 0;
	                ai = widthBuf;
	                ai1 = widthBuf;
	                ai2 = widthBuf;
	                widthBuf[3] = 0;
	                ai2[2] = 0;
	                ai1[1] = 0;
	                ai[0] = 0;
	            }
	            if(stat != 0)
	            {
	                if(aword0[j] > 0)
	                    sign = 1;
	                else
	                if(aword0[j] < 0)
	                {
	                    sign = -1;
	                } else
	                {
	                    int i5 = oSign;
	                    sign = i5;
	                }
	                k = oSign;
	                l = sign;
	                if(k != l && j + 1 < i)
	                {
	                    i1 = j + 1;
	                    int j1;
	                    if(aword0[i1] > 0)
	                    {
	                        j1 = 1;
	                    } else
	                    {
	                        int j5 = j + 1;
	                        if(aword0[j5] < 0)
	                            j1 = -1;
	                        else
	                            j1 = -sign;
	                    }
	                    k1 = sign;
	                    if(j1 != k1)
	                    {
	                        l1 = oSign;
	                        sign = l1;
	                    }
	                }
	                i2 = width + 1;
	                width = i2;
	                j2 = width;
	                k2 = mLimitWidth;
	                if(j2 > k2)
	                {
	                    if(mDebug > 2)
	                        System.out.println("end (limit width)");
	                    lostCarrier();
	                }
	                l2 = sign;
	                i3 = oSign;
	                if(l2 != i3)
	                {
	                    j3 = oSign;
	                    k3 = sSign;
	                    if(j3 == k3)
	                    {
	                        l3 = width;
	                        tWidth = l3;
	                    } else
	                    {
	                        int k5 = tWidth;
	                        int l5 = width;
	                        int i6 = k5 + l5;
	                        tWidth = i6;
	                        int ai3[] = widthBuf;
	                        int j6 = widthBuf[2];
	                        ai3[3] = j6;
	                        int ai4[] = widthBuf;
	                        int k6 = widthBuf[1];
	                        ai4[2] = k6;
	                        int ai5[] = widthBuf;
	                        int l6 = widthBuf[0];
	                        ai5[1] = l6;
	                        int ai6[] = widthBuf;
	                        int i7 = tWidth;
	                        ai6[0] = i7;
	                        if(stat == 1)
	                        {
	                            if(mDebug > 3)
	                            {
	                                StringBuilder stringbuilder = new StringBuilder("tWidth ");
	                                int j7 = tWidth;
	                                String s = stringbuilder.append(j7).toString();
	                                System.out.println( s);
	                            }
	                            if(widthBufCheck() == 1)
	                            {
	                                stat = 2;
	                                int k7 = widthBuf[0];
	                                int l7 = widthBuf[1];
	                                int i8 = k7 + l7;
	                                int j8 = widthBuf[2];
	                                int k8 = i8 + j8;
	                                int l8 = widthBuf[3] / 2;
	                                int i9 = k8 + l8;
	                                timing = i9;
	                            }
	                        } else if(mDebug > 2)
	                        {
	                            StringBuilder stringbuilder1 = new StringBuilder("tWidth ");
	                            int j9 = tWidth;
	                            String s1 = stringbuilder1.append(j9).toString();
	                            System.out.println( s1);
	                        }
	                    }
	                    width = 0;
	                    i4 = sign;
	                    oSign = i4;
	                }
	                if(stat >= 2 && stat <= 11)
	                {
	                    j4 = timing + 1;
	                    timing = j4;
	                    k4 = timing;
	                    l4 = mTiming;
	                    if(k4 >= l4)
	                    {
	                        boolean flag;
	                        if(widthBufCheck() == 0)
	                            flag = true;
	                        else
	                            flag = false;
	                        byteDecode(flag);
	                        if(mDebug > 2)
	                        	System.out.println( "-----");
	                        timing = 0;
	                    }
	                }
	            }
	            j++;
	        } while(true);
	    }
		
	    private void lostCarrier()
	    {
	        if(mDebug > 1)
	        {
	            StringBuilder stringbuilder = new StringBuilder("lostCarrier status:");
	            int i = stat;
	            String s = stringbuilder.append(i).toString();
	            System.out.println(  s);
	        }
	        stat = 0;
	        width = 0;
	        int ai[] = widthBuf;
	        int ai1[] = widthBuf;
	        int ai2[] = widthBuf;
	        widthBuf[3] = 0;
	        ai2[2] = 0;
	        ai1[1] = 0;
	        ai[0] = 0;
	    }
	    
	    private void byteDecode(boolean flag)
	    {
	        if(stat != 2) {
	            if(stat >= 3 && stat <= 10) {
	                int j = bData >> 1;
	                bData = j;
	                if(flag)
	                {
	                    int k = bData | 0x80;
	                    bData = k;
	                }
	                int l = stat + 1;
	                stat = l;
	            } else if(stat == 11){
	            	fskDecodeResult.addResult((byte)bData);//记录解码结果
	                if(mDebug > 0) {
	                    StringBuilder stringbuilder = new StringBuilder("byteData ");
	                    String s = Integer.toHexString(bData & 0xff);
	                    String s1 = stringbuilder.append(s).toString();
	                    System.out.println(s1);
	                }
	                bData = 0;
	                stat = 0;
	            }
	        }else{
	            bData = 0;
	            int i = stat + 1;
	            stat = i;
	        }
	        return ;

	    }
	    
	    private int widthBufCheck()
	    {
	      int[] arrayOfInt = this.widthBuf;
	      int j = arrayOfInt.length;
	      int k = 0;
	      int i;

	      while (true){
	          if (k >= j)
	          {
	            i = 1;
	          }else{
	              int m = arrayOfInt[k];
	              if (m == 0)
	              {
	                i = -1;
	                continue;
	              }
	              int n = mHighLowThreshold;
	              if (m > n){
	              	i = 0;
	              	break;
	              }
	          }
	          k += 1;
	      }
	      return i;
	    }
		
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
