package com.guanri.fsk.utils.MEAPsoft;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.guanri.fsk.conversion.FskCodeParams;
import com.guanri.fsk.conversion.FskEnCodeResult;
import com.guanri.fsk.conversion.FskEncode;
import com.guanri.fsk.view.CureLineBean;
import com.guanri.fsk.view.WaveAnalyse;

public class FFTDecoder {
	public static final int RECTANGULAR = 0;
	public static final int HANN = 1;
	public static final int HAMMING = 2;
	public static final int BLACKMANN = 3;
	public static final double TWO_PI = Math.PI*2;
	
	public static final void forwardMagnitude(double[] c,double[] s) {
		int N = c.length;
		double[] mag = new double[N];
		double twoPi = 2*Math.PI;
		double[] input = new double[N];
		System.arraycopy(c, 0, input, 0, N);
		Arrays.fill(c, 0);
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				c[i] += input[j]*Math.cos(i*j*twoPi/N);
				s[i] -= input[j]*Math.sin(i*j*twoPi/N);
			}
			c[i]/=N;
			s[i]/=N;
			
			mag[i]=Math.sqrt(c[i]*c[i]+s[i]*s[i]);
		}
	}
	
	
	public static final double forwardFSK(int freq,double[] c,double[] s) {
		int length = c.length;
		double twoPi = 2*Math.PI/length;
		
		double re = 0;
		double im = 0;
		for(int j=0; j<length; j++) {
			re += c[j]*Math.cos(freq*j*twoPi);
			im -= c[j]*Math.sin(freq*j*twoPi);
		}
		return Math.sqrt(re*re+im*im);
	}
	public static final double[] window(double[] input, int type) {
		int N = input.length;
		double[] windowed = new double[N];
		
		switch(type) {
		case RECTANGULAR:
			return input;
		case HANN:
			for(int n=0; n<N; n++) {
				windowed[n] = 0.5*(1-Math.cos(2*Math.PI*n/(N-1))) * input[n];
			}
			break;
		case HAMMING:
			for (int n = 0; n < input.length; n++) {
				windowed[n] = (0.53836-0.46164*Math.cos(TWO_PI*n/(N-1))) * input[n];
			}
		case BLACKMANN:
			for(int n=0; n<N; n++) {
				windowed[n] = (0.42-0.5*Math.cos(2*Math.PI*n/(N-1))+0.08*Math.cos(4*Math.PI*n/(N-1)) ) * input[n];
			}
			break;
		}
		
		return windowed;
	}
	
	
	
	
	
	static FskCodeParams fskCodeParams = new FskCodeParams(2200, 1200, 11025,2, 1200);

	public static FskEnCodeResult encode() {

		FskEncode fskEncode = new FskEncode(fskCodeParams);
		// 进行编码
		byte[] s = ("FskEnCodeResult").getBytes();

		FskEnCodeResult fskEnCodeResult = fskEncode.encode(s);

		return fskEnCodeResult;
	}
	
	
	public static void decode() {
		FskEnCodeResult fskEnCodeResult = encode();
		int winLength = fskCodeParams.getSampleF()/fskCodeParams.getBoundRate();
		double[] re = new double[winLength];
		double[] im = new double[winLength];
		int freq0 = fskCodeParams.getF0()*winLength/fskCodeParams.getSampleF();
		int freq1 = fskCodeParams.getF1()*winLength/fskCodeParams.getSampleF();
		double value0= 0 , value1 = 0;
		
		for(int j=0;j<60;j++){
			for(int i=0;i<winLength;i++){
				re[i] = fskEnCodeResult.code[i+j*winLength];
				im[i]= 0;
			}
			
			re=window(re, BLACKMANN);
			
			value0=forwardFSK(freq0,re,im);
			value1=forwardFSK(freq1,re,im);
			//System.out.println((int)value0+":"+(int)value1);
			if(value0>=value1){
				System.out.print(0+" ");
			}else{
				System.out.print(1+" ");
			}
			

		}
		
	}
	
	
	public static void fft(){
		FskEnCodeResult fskEnCodeResult = encode();
		int logLen = (int)(Math.log(fskEnCodeResult.index)/Math.log(2));
		int N = (1<<logLen<fskEnCodeResult.index?(1<<logLen+1):1<<logLen);
		System.out.println(logLen+":"+N);
		FFT fft = new FFT(N);
		double[] window = fft.getWindow();
		
		//快速傅立叶变换
		double[] re = new double[N];
		double[] im = new double[N];
		for(int j=0;j<N;j++){
			if(j>=fskEnCodeResult.index){
				break;
			}
			re[j] = fskEnCodeResult.code[j]*window[j];
			im[j] = 0;
		}
		fft.fft(re, im);
		
		int freq = 1200;
		double value = Math.sqrt(re[freq]*re[freq]+im[freq]*im[freq]);
		System.out.println(value);
		freq = 2200;
		value = Math.sqrt(re[freq]*re[freq]+im[freq]*im[freq]);
		System.out.println(value);
		int[] values = new int[N];
		for(int j=0;j<N;j++){
			freq = j;
			values[j] = (int)Math.sqrt(re[freq]*re[freq]+im[freq]*im[freq]);
			if(j>N/2)
				values[j] =-values[j] ;
			//System.out.println(" "+freq+":"+value);
		}
		
		//绘图
		List<CureLineBean> list = new ArrayList<CureLineBean>();

		CureLineBean cureLineBean = new CureLineBean(values,Color.RED);
		list.add(cureLineBean);
		
		WaveAnalyse test = new WaveAnalyse(list);
		test.setVisible(true);
	}
	
	public static void dft(){
		FskEnCodeResult fskEnCodeResult = encode();
		int logLen = (int)(Math.log(fskEnCodeResult.index)/Math.log(2));
		int N = (1<<logLen<fskEnCodeResult.index?(1<<logLen+1):1<<logLen);
		
		double[] re = new double[N];
		double[] im = new double[N];
		
		//普通傅立叶变换运算
		for(int j=0;j<N;j++){
			if(j>=fskEnCodeResult.index){
				break;
			}
			re[j] = fskEnCodeResult.code[j];
			im[j] = 0;
		}
		//加窗
		re=window(re, BLACKMANN);
		
		forwardMagnitude(re,im);
	    
		int freq = 1200;
		double value = Math.sqrt(re[freq]*re[freq]+im[freq]*im[freq]);
		System.out.println(value);
		freq = 2200;
		value = Math.sqrt(re[freq]*re[freq]+im[freq]*im[freq]);
		System.out.println(value);
	    
		int[] values = new int[N];
		for(int j=0;j<N;j++){
			freq = j;
			re[freq] = re[freq]*N;
			im[freq] = im[freq]*N;
			
			values[j] = (int)Math.sqrt(re[freq]*re[freq]+im[freq]*im[freq]);
			if(j>N/2)
				values[j] =-values[j] ;
		}
	    
		//绘图
		List<CureLineBean> list = new ArrayList<CureLineBean>();

		CureLineBean cureLineBean = new CureLineBean(values,Color.RED);
		list.add(cureLineBean);
		
		WaveAnalyse test = new WaveAnalyse(list);
		test.setVisible(true);	
		
	}

	
	public static void testSingle(){
		int N = 18;
		int f2 = 3200;
		int f0 = 2200;
		int f1 = 1200;
		int fs = 11025;
		
		double[] data = new double[N];
		double fd = 0d;
		for(int i=0;i<2*data.length;i++){
			fd = fd+2*Math.PI*i*f0/fs;
		}
		for(int i=0;i<2*data.length;i++){
			fd = fd+2*Math.PI*i*f1/fs;
		}
		for(int i=0;i<data.length;i++){
			fd = fd+2*Math.PI*i*f0/fs;
			data[i] = (Short.MAX_VALUE*Math.sin(fd));
		}
		double[] im = new double[N];
		//加窗
		data=window(data, BLACKMANN);
		
		forwardMagnitude(data,im);
		
		
		int freq = 0;
	    
		int[] values = new int[N];
		for(int j=0;j<N;j++){
			freq = j;
			data[freq] = data[freq]*N;
			im[freq] = im[freq]*N;
			
			values[j] = (int)Math.sqrt(data[freq]*data[freq]+im[freq]*im[freq]);
			if(j>N/2)
				values[j] =-values[j] ;
			System.out.println(freq+":"+values[j]);
		}
	    
		//绘图
		List<CureLineBean> list = new ArrayList<CureLineBean>();

		CureLineBean cureLineBean = new CureLineBean(values,Color.RED);
		list.add(cureLineBean);
		
		WaveAnalyse test = new WaveAnalyse(list);
		test.setVisible(true);	
		
	}
	
	public static void main(String[] args) {
		//decode();
		//dft();
		//fft();
		
		testSingle();
		
		
		
		
		

	}

}
