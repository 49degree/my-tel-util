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
			
			re=window(re, HANN);
			
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
		
		int f0 = 2200;
		int f1 = 1200;
		int fs = 8000;
		int fb = 1200;
		
		int modeValue = 0;
		int point = 0;
		
		int N = fs/fb;
		double[] data = new double[N];
		double fd = 0d;
		
		double single0 = 2*Math.PI*f0/fs;
		double single1 = 2*Math.PI*f1/fs;
		
		
		
//		for(int j=0;j<15;j++){
//			point = (fs+modeValue)/fb;
//			modeValue = (fs+modeValue)%fb;
//			for(int i=0;i<point;i++){
//				fd += single0;
//			}
//			
//			point = (fs+modeValue)/fb;
//			modeValue = (fs+modeValue)%fb;
//			for(int i=0;i<point;i++){
//				fd += single1;
//			}
//		}
		
		
		double degree = 0;
		boolean is0 = false;
		for(int times = 0;times<20;times++){
			if(times%2==0){
				degree = single0;
				is0 = true;
				System.out.print("single0:");	
			}else{
				degree = single1;
				is0 = false;
				System.out.print("single1:");
			}
			point = (fs+modeValue)/fb;
			modeValue = (fs+modeValue)%fb;
			for(int i=0;i<point;i++){
				fd = fd+degree;
				if(i>=N)
					break;
				data[i] = (Short.MAX_VALUE*Math.sin(fd));
				System.out.println(i+":"+data[i]);
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
			
			if(values[1]>=values[2]){
				if(is0){
					System.out.println("+++++++++++++++++++++++++++need zero");
				}
				System.out.println(1+" ");
			}else{
				if(!is0){
					System.out.println("+++++++++++++++++++++++++++need one");
				}
				System.out.println(0+" ");
			}
		}

		
	    
		//绘图
//		List<CureLineBean> list = new ArrayList<CureLineBean>();
//
//		CureLineBean cureLineBean = new CureLineBean(values,Color.RED);
//		list.add(cureLineBean);
//		
//		WaveAnalyse test = new WaveAnalyse(list);
//		test.setVisible(true);	
		
	}
	
	
	
	public static void testSingle2(){
		

		
		int f0 = 2200;
		int f1 = 1200;
		int fs = 8000;
		int fb = 1200;
		
		int modeValue = 0;
		int point = 0;
		
		int N = fs/fb;
		
		double fd = 0d;
		
		double single0 = 2*Math.PI*f0/fs;
		double single1 = 2*Math.PI*f1/fs;
		

		//编码
		double[] source = new double[10240];
		double[] data = new double[N];
		int sourceIndex = 0;
		double degree = 0;
		boolean is0 = true;
		int createTimes= 0;
		while(true){
			if(createTimes++%2==0){
				degree = single0;
				//System.out.print("single0:");	
			}else{
				degree = single1;
				//System.out.print("single1:");
			}
			point = (fs+modeValue)/fb;
			modeValue = (fs+modeValue)%fb;
			if(sourceIndex>=source.length)
				break;
			if(sourceIndex==0){
				fd = fd-degree;
			}
			if(createTimes<20){
				System.out.println("++++++++++++++++++"+createTimes);	
			}
			for(int i=0;i<point;i++){
				fd = fd+degree;
				if(sourceIndex>=source.length)
					break;
				source[sourceIndex++] = 1000*Math.sin(fd);
				if(createTimes<20){
					System.out.println(sourceIndex+":"+source[sourceIndex-1]);	
				}
			}
		}
		
//		int[] sourceData = new int[source.length];
//		for(int i=0;i<source.length;i++){
//			sourceData[i] = (int)source[i];
//		}
//		
//		List<CureLineBean> list = new ArrayList<CureLineBean>();
//		CureLineBean cureLineBean = new CureLineBean(sourceData,Color.RED);
//	    list.add(cureLineBean);
//	    WaveAnalyse test = new WaveAnalyse(list);
//		test.setVisible(true);	

		//解码
		int getDataIndex = 0;
		for(int times = 0;times<100;times++){
			if(getDataIndex+N+1>=sourceIndex)
				break;
			
//			if(times%6==0)
//				getDataIndex++;
			if(times%3>0)
				getDataIndex++;
			
			System.arraycopy(source, getDataIndex, data, 0, N);
			


			
			System.out.println("++++++++++++++++++"+times);
			
			for(int freq=0;freq<N;freq++){
				//System.out.println(freq+":"+data[freq]);
			}
			
			
			double[] im = new double[N];
			//加窗
			data=window(data, HANN);
			
			forwardMagnitude(data,im);
			
			
			int[] values = new int[N];
			double maxValue = 0;
			for(int freq=0;freq<N;freq++){
				data[freq] = data[freq]*N;
				im[freq] = im[freq]*N;
				
				values[freq] = (int)Math.sqrt(data[freq]*data[freq]+im[freq]*im[freq]);
				if(freq>N/2)
					values[freq] =-values[freq] ;
				else{
					maxValue=(maxValue<values[freq])?values[freq]:maxValue;
				}
					
				System.out.println(freq+":"+values[freq]);
			}
			
//			if(values[0]<maxValue&&values[1]<maxValue){
//				getDataIndex++;
//				continue;
//			}
			
			getDataIndex+=N;	
			
			

			
//			if(Math.abs(values[1]-values[2])<5000){
//				getDataIndex++;
//				//System.out.println("==============================");
//				continue;
//			}else{
//				getDataIndex+=N;
//				
//			}
			
			if(is0){
				System.out.print("single0:");	
			}else{
				System.out.print("single1:");
			}
			//if(values[1]>=values[2]){
			if(values[0]>=values[1]){
				
				
				System.out.println(1+" ");
				if(is0){
					System.out.println("++++++++++++++++++need zero");
				}
			}else{
				System.out.println(0+" ");
				if(!is0){
					System.out.println("++++++++++++++++++need one");

				}
			}
			is0 = !is0;
		}

		
	    
		//绘图
//		List<CureLineBean> list = new ArrayList<CureLineBean>();
//
//		CureLineBean cureLineBean = new CureLineBean(values,Color.RED);
//		list.add(cureLineBean);
//		
//		WaveAnalyse test = new WaveAnalyse(list);
//		test.setVisible(true);	
		
	}
	
	
	public static void testSingle3(){
		int f0 = 2200;
		int f1 = 1200;
		float fs = 8000;
		int fb = 1200;
		
		double[] data = new double[100];
		for(int i=0;i<data.length;i++){
			data[i] = Math.sin((f1/fs)*2*Math.PI*i)*2000;
			
		}
		
		
		double[] im = new double[data.length];
		//加窗
		data=window(data, HANN);
		
		forwardMagnitude(data,im);
		
		int[] values = new int[data.length];
		for(int freq=0;freq<data.length;freq++){
			data[freq] = data[freq]*data.length;
			im[freq] = im[freq]*data.length;
			
			values[freq] = (int)Math.sqrt(data[freq]*data[freq]+im[freq]*im[freq]);
			
			if(freq>data.length/2)
				values[freq] =-values[freq] ;
			
			System.out.println(freq+":"+values[freq]);
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
		
		testSingle3();
		
		
		
		
		

	}

}
