package com.guanri.fsk.conversion;

import com.guanri.android.lib.utils.TypeConversion;

public class FskDecode {
	private FskCodeParams fskCodeParams = null;//采样参数
	private SourceQueue sourceQueue = null;//数据员队列
	FskDecodeResult fskDecodeResult = null;//结果数据对象
	
	private int[] boundTArray = null;//一波特周期内的采样点数组
	private int singleTArraylength = 0;//频率较高的波形在一个周期内的采样点数
	private int boundTlength = 0;//1波特周期内的采样点数 
	private int singleTlength0 = 0;
	private int singleTlength1 = 0;
	
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
		singleTlength0 = (int)Math.round(fskCodeParams.getSampleF()*1.0f/fskCodeParams.getF0());
		singleTlength1 = (int)Math.round(fskCodeParams.getSampleF()*1.0f/fskCodeParams.getF1());
		
		singleTArraylength = singleTlength0<singleTlength1?singleTlength0:singleTlength1;
		boundTlength = (int) Math.round(fskCodeParams.getSampleF()*1.0f/ fskCodeParams.getBoundRate());
		
		boundTArray = new int[boundTlength];//一波特周期内的采样点数组
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
		
		nextSinglePosition = nextSinglePosition -  singleCount*fskCodeParams.getSampleF()/ fskCodeParams.getBoundRate();
		singleCount++;
		nextSinglePosition = nextSinglePosition + singleCount*fskCodeParams.getSampleF()/ fskCodeParams.getBoundRate();
	    if (singleCount > fskCodeParams.getBoundRate()){
	    	singleCount -= fskCodeParams.getBoundRate();
	    	nextSinglePosition -= position;
	    	position = 0;
	    }
	}
	

	

	/**
	 * 进行虑波
	 */
	private int singleTIndex = 0;
	private boolean isSingle0 = false;
	
	
	int times = 0;
	public boolean decode2(int sampleValue){
		boolean result = false;
		times++;
//		singleTIndex = singleTIndex % boundTlength;
//		boundTArray[singleTIndex] = sampleValue;
//		singleTIndex ++;
		
	    for (int i = boundTlength - 1; i > 0; i--)
	    	boundTArray[i] = boundTArray[i - 1];     

	    boundTArray[0] = sampleValue;
		
		
	    //记录解码过程数据，是否记录在addSourceValue方法中进行判断
	    if(fskDecodeResult.isRecordDecodeInfo()){
	    	fskDecodeResult.addSourceValue(sampleValue, 0, 0,0);
	    }
		
		//解码相关参数
		double amp = 0,minError=-1, sum=0, data=0 ,rad=0;

		double[] singleDistance = {2*Math.PI*fskCodeParams.getF0()/fskCodeParams.getSampleF(),2*Math.PI*fskCodeParams.getF1()/fskCodeParams.getSampleF()}; 
		double[] singleResult = new double[2];
		
		for(int i=0;i<boundTlength;i++){
		    data = boundTArray[i];
		    sum +=  data * data;	
		}
		amp = Math.sqrt((sum / boundTlength) * 2);
		//  Amplitude := Amp;

		//判断与0或1更接近
		for(int type = 0;type<2;type++){
			minError = -1;
			rad = 0;
			while(rad < (2 * Math.PI)){
				sum = 0;
				for(int i=0;i<boundTlength;i++){
					data= (boundTArray[i] - amp * Math.sin(rad - i * singleDistance[type]));
					sum = sum + data * data;
				}
			    if (minError > sum || minError < 0) {
			    	minError = sum;
			    }
			    rad = rad + singleDistance[type] / 2;
			}
			singleResult[type] = minError;
		}
//		if(times<100){
//			System.out.print(singleResult[0]<singleResult[1]?"0":"1");
//		}
		

		if (!started) {
			if (singleResult[0]<singleResult[1])
				position++;
			else
				position = 0;

			
			if (position >= singleTArraylength) {
				reviseNextSigPos();
				started = true;
				isSingle0 = true;
				result = true;
			}
		} else {
	    	position ++;
	    	if (position >= nextSinglePosition) {
	    		calcNextSigPos();
	    		isSingle0 = singleResult[0]<singleResult[1];
	    		result = true;
	    	}
	    }
		
		if(result){
			System.out.print(isSingle0?"0":"1");
		}
		return result;
	}
	
	
	//判断前导码 80对01和40个1
	private int headerSingle = 2;
	private int header01Index = 0;
	private int header1Index = 0;
	private boolean hasHeader = false;
	private boolean decodeHeader(){
    	//判断前导码 80对01和40个1
		//先找联系20个01,再找连续20个1，找到则认为开始
		if(!hasHeader){
			if (header01Index < 40) {//判断是否找到20个01
				if ((headerSingle==2||headerSingle==1) && isSingle0) {
					headerSingle = 0;
					header01Index++;
				} else if (headerSingle==0 && !isSingle0) {
					headerSingle = 1;
					header01Index++;
				} else {
					header01Index = 0;
					headerSingle = 2;
				}
			} else {
				if (!isSingle0) {
					header1Index++;
				} else {
					header1Index = 0;
				}
				if (header1Index >= 10) {//判断是否找到20个01
					hasHeader = true;
				}
			}
		}
		return hasHeader;

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
		if (decode2(sampleValue)&&decodeHeader()) {
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
	private boolean isDecodeContinue = true;//是否停止
	public void beginDecode(){
		while(isDecodeContinue){
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
							//判断数据是否接收完成
							if(fskDecodeResult.getDataIndex()==0){
								hasHeader = false;
							}
							
						}
					}
				}
			
			}
		}
	}
	
	/**
	 * 停止解码
	 */
	public void stopDecode(){
		isDecodeContinue = false;
	}
	
	/**
	 * 获取解码状态
	 */
	public boolean getDecodeState(){
		return isDecodeContinue;
	}
}
