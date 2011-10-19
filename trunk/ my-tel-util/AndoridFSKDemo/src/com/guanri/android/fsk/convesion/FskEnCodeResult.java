package com.guanri.android.fsk.convesion;

public class FskEnCodeResult {

	
	public byte[] code = null;//编码结果
	public int index = 0;//当前填充位置
	public int modeValue = 0;//采样点余数
	public double degree = 0;//当前角度
	public double signal0Degree = 0d;
	public double signal1Degree = 0d;
	public int sampleMaxValue = 0;
	
	public ParseValueImp parseValueImp = null;

	/**
	 * 插入采样数据到code中
	 * @param sampleValue
	 */
	public void insertCode(byte[] sampleValue){
		System.arraycopy(sampleValue, 0, code, index, sampleValue.length);
		index += sampleValue.length;
	}
	
	public void insertCode(byte sampleValue){
		code[index] = sampleValue;
		index ++;
	}
	
	
	public ParseValueImp getParseValueImp() {
		return parseValueImp;
	}


	public void setParseValueImp(ParseValueImp parseValueImp) {
		this.parseValueImp = parseValueImp;
	}


	/**
	 * 解析采样数据接口
	 * @author Administrator
	 *
	 */
	public interface ParseValueImp{
		public void parseValueToByte(double value);
	}
	
	
}
