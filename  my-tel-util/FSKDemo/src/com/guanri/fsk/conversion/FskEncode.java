package com.guanri.fsk.conversion;

import java.io.File;
import java.io.FileOutputStream;

import com.guanri.android.lib.utils.TypeConversion;
import com.guanri.fsk.conversion.FskEnCodeResult.ParseValueImp;

/**
 * 对数据进行编码
 * @author yangxueping
 *
 */
public class FskEncode {
	public final static int SINGLE_ZERO = 0;
	public final static int SINGLE_ONE = 1;
	
	private FskCodeParams fskCodeParams = null;//采样参数

	
	public FskEncode(FskCodeParams fskCodeParams){
		this.fskCodeParams = fskCodeParams;// new FskCodeParams(2200,1200,11025,2,1200);
	}
	
	/**
	 * 对数据source进行编码
	 * @param source
	 * @return
	 */
	public FskEnCodeResult encode(byte[] source,int offset,int length){
		sing0 = 0;
		sing1 = 0;
		
		
		if(length>source.length-offset)
			return null;

		byte[] code = new byte[getFskLength(length)];//编码后的数据
		
		final FskEnCodeResult fskEnCodeResult = new FskEnCodeResult();//编码结果对象
		if(fskCodeParams.getSampleByteLength()==1){//采样长度为1
			fskEnCodeResult.sampleMaxValue = Byte.MAX_VALUE;
			fskEnCodeResult.setParseValueImp(new ParseValueImp(){
				public void parseValueToByte(double value){//value为正玄函数的值
					fskEnCodeResult.insertCode((byte)Math.round(value*fskEnCodeResult.sampleMaxValue));
				}
			});
		}else if(fskCodeParams.getSampleByteLength()==2){//采样长度为2
			fskEnCodeResult.sampleMaxValue = Short.MAX_VALUE/2;
			fskEnCodeResult.setParseValueImp(new ParseValueImp(){
				public void parseValueToByte(double value){//value为正玄函数的值
					short value1 = (short)Math.round(value*fskEnCodeResult.sampleMaxValue);
					byte[] sampleValue = TypeConversion.shortToBytes(value1);
					fskEnCodeResult.insertCode(sampleValue);
				}
			});
		}
		
		fskEnCodeResult.code = code;
		/*
		 分别计算0和1信号区间的弧度, 1/f0表示0信号波的周期(s),1/f1表示1信号波的周期(s),1/sampleF表示采样周期(s,即采集1个点的时间)
		则(1/f0)/(1/sampleF)=sampleF/f0表示一个信号波的周期需要采样点数，则2*pi/(sampleF/f0)=2*pi*f0/sampleF表示0信号采样点间的角度
		波特周期/采样周期=(1/boundRate)/(1/sampleF)=sampleF/boundRate表示一波特周期要采集几个点,
		(2*pi*f0/sampleF)*(sampleF/boundRate)=2*pi*f0/boundRate表示0信号区间的角度
		*/
		fskEnCodeResult.signal0Degree = 2*Math.PI*fskCodeParams.getF0()/fskCodeParams.getBoundRate();//表示0信号区间的角度
		fskEnCodeResult.signal1Degree = 2*Math.PI*fskCodeParams.getF1()/fskCodeParams.getBoundRate();//表示1信号区间的角度
		
		//发送前导码
		encodeGuideCode(fskEnCodeResult);
		//发送数据包头
		encodeHeaderData(fskEnCodeResult,(short)source.length);
		//对数据进行编码
		for(int i=offset;i<offset+length;i++){
			encodeByte(source[i],fskEnCodeResult);
		}
		//发送结束码40个1
		for(int i=0;i<40;i++){
			encode(fskEnCodeResult,FskEncode.SINGLE_ONE);
		}
		
		System.out.println("sing0:"+sing0+":sing1:"+sing1);
		return fskEnCodeResult;
	
		
		
		//byte
	}
	
	/**
	 * 对数据source进行编码
	 * @param source
	 * @return
	 */
	public FskEnCodeResult encode(byte[] source){
		return encode(source,0,source.length);
	}
	
	/**
	 * 发送前导码
	 * @param fskEnCodeResult
	 */
	public void encodeGuideCode(FskEnCodeResult fskEnCodeResult){
		//发送前导码80对01
		for(int i=0;i<80;i++){
			encode(fskEnCodeResult,FskEncode.SINGLE_ZERO);
			encode(fskEnCodeResult,FskEncode.SINGLE_ONE);
		}
		//发送前导码40个1
		for(int i=0;i<40;i++){
			encode(fskEnCodeResult,FskEncode.SINGLE_ONE);
		}
		
	}
	
	/**
	 * 发送数据长度
	 * 数据头：  包头（1字节）	+长度(HEX2)+命令ID (1)
	 * 包头：‘M’是固定的
	 * 长度：除包头、长度和校验和外剩余数据的总长度 
	 *  命令ID：固定为00 
	 * @param fskEnCodeResult
	 */
	public void encodeHeaderData(FskEnCodeResult fskEnCodeResult,short dataLength){
		byte[] lengthSource = TypeConversion.shortToBytesEx(dataLength);
		
		encodeByte((byte)0x4D,fskEnCodeResult);//包头：‘M’是固定的
		//fskEnCodeResult
		for(byte sourceData:lengthSource){
			encodeByte(sourceData,fskEnCodeResult);
		}
		encodeByte((byte)0x00,fskEnCodeResult);//命令ID：固定为00 
		
		
	}
	
	/**
	 * 计算 编码以后的数据长度
	 * 
	 * (波特周期/采样周期)表示一波特周期要采集几个点
	 * 
	 * (波特周期/采样周期)*源数据长度*10*采样数据长度
	 * 
	 * sourceLength+4(数据头)   
	 * 数据头：  包头（1字节）	+长度(HEX2)+命令ID (1)
	 * 包头：‘M’是固定的
	 * 长度：除包头、长度和校验和外剩余数据的总长度 
	 *  命令ID：固定为00 
	 * 
	 * 10表示一个字节8位加上起始位0和结束位1共10位
	 * 采样数据长度 单位为字节
	 * @param sourceLength
	 * @return
	 */
	public int getFskLength(int sourceLength){
		float codeLength = fskCodeParams.getSampleF()*((sourceLength+4)*10+80*2+80)/new Float(fskCodeParams.getBoundRate());//80*2+80前导码长度80对01 40个1(首尾各一段)
		
		return Math.round(new Float(codeLength+0.5))*fskCodeParams.getSampleByteLength();
	}
	
	
	/**
	 * 对一个字节进行编码
	 * @param buffer
	 * @return
	 */
	public void encodeByte(byte buffer,FskEnCodeResult fskEnCodeResult){
		encode(fskEnCodeResult,FskEncode.SINGLE_ZERO);//开始标识0
		for(int i=0;i<8;i++){
			byte temp = (byte)(buffer>>i&0X01);
			if(temp==0){
				encode(fskEnCodeResult,FskEncode.SINGLE_ZERO);
			}else{
				encode(fskEnCodeResult,FskEncode.SINGLE_ONE);
			}
		}
		encode(fskEnCodeResult,FskEncode.SINGLE_ONE);//结束标识1
	}
	
	/**
	 * 对0,1进行编码
	 * @param fskEnCodeResult
	 * @param single 0表示0编码，1表示1编码
	 */
	int sing0 = 0;
	int sing1 = 0;
	public void encode(FskEnCodeResult fskEnCodeResult,int single){
		if(single==0){
			sing0++;
		}else{
			sing1++;
		}
		
		//波特周期/采样周期=(1/boundRate)/(1/sampleF)=sampleF/boundRate表示一波特周期要采集几个点,
		//因为采样点个数只能取整数，所以会有余数fskEnCodeResult.modeValue，为了增加可靠性，把余数加入下一次进行计算
		int point = (fskCodeParams.getSampleF()+fskEnCodeResult.modeValue)/fskCodeParams.getBoundRate();
		fskEnCodeResult.modeValue = (fskCodeParams.getSampleF()+fskEnCodeResult.modeValue)%fskCodeParams.getBoundRate();
		
		double degreePerPoint = (single==0)?fskEnCodeResult.signal0Degree/point:fskEnCodeResult.signal1Degree/point;
		
		if(fskEnCodeResult.degree==0d){
			fskEnCodeResult.degree = -degreePerPoint;//如果是起始点，则前滑一个点的区间
		}
		
		for(int i=1;i<=point;i++){
			fskEnCodeResult.degree += degreePerPoint;
			double value = Math.sin(fskEnCodeResult.degree);
			fskEnCodeResult.parseValueImp.parseValueToByte(value);//解析数据，并插入数据CODE数组中
		}
		//fskEnCodeResult.degree += (single==0)?fskEnCodeResult.signal0Degree:fskEnCodeResult.signal1Degree;
	}
	
	
	/**
	 * 测试代码
	 * @param args
	 */
	public static void main(String[] args){
		
		FskCodeParams fskCodeParams = new FskCodeParams(2200,1200,11025,2,1200);
		
		FskEncode fskEncode = new FskEncode(fskCodeParams);
		
		
		byte[] s = {1};
		byte[] bu = new byte[1+s.length];
		for(int i=0;i<1;i++){
			bu[i] = -1;
		}
		System.arraycopy(s, 0, bu, 1, s.length);
		
		
		FskEnCodeResult fskEnCodeResult = fskEncode.encode(bu);
		
		WaveFileParams waveFileParams = new WaveFileParams(fskCodeParams,fskEnCodeResult);
		
		byte[] waveByte = waveFileParams.parseWaveToByte();
		
		try{
			File waveFile = new File("E:/workgroup/FSKDemo/firstWave.wav");
			if(!waveFile.exists()){
				waveFile.createNewFile();
			}
			
			FileOutputStream fout = new FileOutputStream(waveFile);
			fout.write(waveByte);
		}catch(Exception e){
			e.printStackTrace();
		}

		
		
		System.out.println(fskEnCodeResult.index);
		System.out.println(fskEnCodeResult.code.length);
//		byte a = (byte)25;
//		System.out.println(TypeConversion.byte2hex(new byte[]{a}));
//		for(int i=0;i<8;i++){
//			byte temp = (byte)(a>>i&0X01);
//			System.out.println(temp);
//		}
		
		
		
	}
}
