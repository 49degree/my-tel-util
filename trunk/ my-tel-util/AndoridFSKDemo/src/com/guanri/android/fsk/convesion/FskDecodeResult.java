package com.guanri.android.fsk.convesion;

import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

public class FskDecodeResult {
	Logger logger = Logger.getLogger(FskDecodeResult.class);
	
    public static byte[] DATA_START_FLAG = {0X55,0X55,0X55,0X55,0X55};//数据开始标志
    public static byte[] DATA_END_FLAG = {0X0F,0X0F,0X0F,0X0F,0X0F};//数据结束标志
    
	private boolean recordInfo = false;//是否记录解码过程数据
	
	public byte[] data = null;//解码后的数据
	public int dataIndex = 0;
	
	int sourceIndex = 0;
	public int[] sourceValue = null;//元素数据数组
	public int[] singleFilter = null;//第一次滤波后数组
	public int[] boundFilter = null;//第二次滤波后数组
	public int[] maxAverage = null;//最大平均值*0.55
	
	public FskDecodeResult(){
		this(false);
	}
	
	public FskDecodeResult(boolean recordInfo){
		this.recordInfo =  recordInfo ;
	}

	public boolean isRecordDecodeInfo(){
		return this.recordInfo;
	}
	
	public void addSourceValue(int source,int singleFilterValue,int boundFilterValue,int maxAverageValue){
		if(!this.recordInfo)
			return ;
		
		if(sourceValue == null){
			sourceValue = new int[1024];
			singleFilter = new int[1024];
			boundFilter = new int[1024];
			maxAverage = new int[1024];
			sourceIndex = 0;
		}else if(sourceIndex>=sourceValue.length){
			int[] temp = sourceValue;
			sourceValue = new int[sourceIndex+1024];
			System.arraycopy(temp, 0, sourceValue, 0, sourceIndex);
			
			temp = singleFilter;
			singleFilter = new int[sourceIndex+1024];
			System.arraycopy(temp, 0, singleFilter, 0, sourceIndex);
			
			temp = boundFilter;
			boundFilter = new int[sourceIndex+1024];
			System.arraycopy(temp, 0, boundFilter, 0, sourceIndex);
			
			temp = maxAverage;
			maxAverage = new int[sourceIndex+1024];
			System.arraycopy(temp, 0, maxAverage, 0, sourceIndex);
			temp = null;
		}
		
		sourceValue[sourceIndex] = source;
		singleFilter[sourceIndex] = singleFilterValue;
		boundFilter[sourceIndex] = boundFilterValue;
		maxAverage[sourceIndex] = maxAverageValue;
		sourceIndex++;
	}
	
	
	
	/**
	 * 加入解码结果
	 * @param value
	 */
	public synchronized void addResult(byte value){
		if(data == null){
			data = new byte[1024];
			dataIndex = 0;
		}else if(dataIndex>=data.length){
			byte[] temp = data;
			data = new byte[dataIndex+1024];
			System.arraycopy(temp, 0, data, 0, dataIndex);
			temp = null;
		}
		data[dataIndex] = value;
		dataIndex++;
	}
	
    
    private boolean receiveHeader = false;
    private int startIndex = -1;
    private int endIndex = -1;
    /**
     * 解析数据
     */
    public void parseDate(){
    	if(!receiveHeader){//没有接收到开始位标志，判断是否有开始位标志
    		
    		if((startIndex=TypeConversion.byte2hex(data,0,dataIndex).indexOf(TypeConversion.byte2hex(DATA_START_FLAG)))>-1){
    			receiveHeader = true;
//    			byte[] temp = new byte[data.length];
//    			System.arraycopy(data, index+DATA_START_FLAG.length, temp, 0, dataIndex-(index+DATA_START_FLAG.length));
//    			data = temp;
//    			dataIndex = dataIndex -(index+DATA_START_FLAG.length);
    		}
    	}else{//接收到开始位标志，判断是否有结束位标志
    		if((endIndex=TypeConversion.byte2hex(data,0,dataIndex).indexOf(TypeConversion.byte2hex(DATA_END_FLAG)))>-1){
    			byte[] temp = new byte[endIndex-startIndex-DATA_START_FLAG.length];
    			System.arraycopy(data, startIndex+DATA_START_FLAG.length, temp, 0, temp.length);
    			
    		}
    	}
    }
}
