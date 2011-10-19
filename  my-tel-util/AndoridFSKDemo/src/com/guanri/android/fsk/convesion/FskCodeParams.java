package com.guanri.android.fsk.convesion;
/**
 * FSK协议参数
 * @author Administrator
 *
 */
public class FskCodeParams {
	private int f0 = 0;//0的波形频率
	private int f1 = 0;//1的波形频率
	private int sampleF = 0;//采样频率
	private int sampleByteLength = 0;//采样大小 单位为字节
	private int boundRate = 0;// 波特率
	
	public FskCodeParams() {
	}
	
	public FskCodeParams(int f0, int f1, int sampleF,int sampleByteLength,int boundRate) {
		this.f0 = f0;
		this.f1 = f1;
		this.sampleF = sampleF;
		this.sampleByteLength = sampleByteLength; 
		this.boundRate = boundRate;
		//分别计算0和1采样点之间的角度差 1/f0表示0信号的周期(s),1/f1表示1信号的周期(s),1/boundRate表示1波特周期(s,即发送一位数据的时间)
		
	}
	
	public int getF0() {
		return f0;
	}
	public void setF0(int f0) {
		this.f0 = f0;
	}
	public int getF1() {
		return f1;
	}
	public void setF1(int f1) {
		this.f1 = f1;
	}
	public int getSampleF() {
		return sampleF;
	}
	public void setSampleF(int sampleF) {
		this.sampleF = sampleF;
	}

	public int getSampleByteLength() {
		return sampleByteLength;
	}

	public void setSampleByte(int sampleByteLength) {
		this.sampleByteLength = sampleByteLength;
	}

	public int getBoundRate() {
		return boundRate;
	}

	public void setBoundRate(int boundRate) {
		this.boundRate = boundRate;
	}


	
	
}
