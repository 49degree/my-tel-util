package com.xiaoma.piccut.demo;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageFilter {
	private Bitmap image;

	private int iw, ih;

	private int[] pixels;

	public ImageFilter(Bitmap image) {
		this.image = image;
		iw = image.getWidth();
		ih = image.getHeight();
		pixels = new int[iw * ih];
		image.getPixels(pixels, 0, iw, 0, 0, iw, ih);
	}


	//===============灰度化====================================================================//
	/**
	 * 加权平均值
	 */
	public void addAvaGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);

			// 增加了图像的亮度
			red = green = blue = (int)(red*0.299+green*0.587+blue*0.144);

			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	
	/**
	 * 最大值法
	 */
	public void maxGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);
			
			// 增加了图像的亮度
			red = red>green?red:green;
			red = green = blue = red>blue?red:blue;
			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	/**
	 * 最小值法
	 */
	public void minGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);
			
			// 增加了图像的亮度
			red = red<green?red:green;
			red = green = blue = red<blue?red:blue;
			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	/**
	 * 平均值法
	 */
	public void avaGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);
			
			// 增加了图像的亮度
			red = red<green?red:green;
			red = green = blue = (red+blue+blue)/3;
			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	//=============== 去噪====================================================================//
	/**
	 *对灰度图进行均值滤波 
	 *窗口3*3
	 */
	public void avaFilter(int minValue,int maxValue) {
		int temp = 0;
		for (int i = 1; i < ih - 1; i++) {
			for (int j = 1; j < iw - 1; j++) {
				int alpha = Color.alpha(pixels[i*iw+j]);
				if(Color.red(pixels[(i-1)*iw+j-1])>minValue&&Color.red(pixels[(i-1)*iw+j-1])<maxValue){
					temp = Color.red(pixels[(i-1)*iw+j-1])
				       +Color.red(pixels[(i-1)*iw+j])
				       + Color.red(pixels[(i-1)*iw+j+1])
				       +Color.red(pixels[i*iw+j-1])
				       +Color.red(pixels[i*iw+j+1])
				       +Color.red(pixels[(i+1)*iw+j-1])
				       + Color.red(pixels[(i+1)*iw+j])
				       + Color.red(pixels[(i+1)*iw+j+1]);
					pixels[i*iw+j] = Color.argb(alpha, temp, temp, temp);
				}

			}
		}
	}

	/**
	 *对灰度图进行中值滤波 
	 *窗口3*3
	 */
	public void medianFilter(int minValue,int maxValue) {
		int[] temp = new int[9];
		for (int i = 1; i < ih - 1; i++) {
			for (int j = 1; j < iw - 1; j++) {
				int alpha = Color.alpha(pixels[i*iw+j]);
				if(Color.red(pixels[(i-1)*ih+j-1])>minValue&&Color.red(pixels[(i-1)*iw+j-1])<maxValue){
					temp[0] = Color.red(pixels[(i-1)*iw+j-1]);
					temp[1] = Color.red(pixels[(i-1)*iw+j]);
					temp[2] = Color.red(pixels[(i-1)*iw+j+1]);
					
					temp[3] = Color.red(pixels[i*iw+j-1]);
					temp[4] = Color.red(pixels[i*iw+j]);
					temp[5] = Color.red(pixels[i*iw+j+1]);
					
					temp[6] = Color.red(pixels[(i+1)*iw+j-1]);
					temp[7] = Color.red(pixels[(i+1)*iw+j]);
					temp[8] = Color.red(pixels[(i+1)*iw+j+1]);
					sort(temp);
					pixels[i*iw+j] = Color.argb(alpha, temp[4], temp[4], temp[4]);
				}

			}
		}
	}	
	/**
	 * 排序
	 * @param data
	 */
	private void sort(int[] data){
		for(int i=0;i<data.length;i++){
			for(int j=i+1;j<data.length;j++){
				if(data[i]>=data[j]){
					data[i] = data[i]^data[j];
					data[j]=data[j]^data[i];
					data[i] = data[j]^data[i];
				}
			}
		}
			
	}
	
	
	//=============== 二值化====================================================================//
	/** 
	 * 图像二值化
	 */
	public void changeGrey(int grey) {
		// 设定二值化的域值，默认值为100
		// 对图像进行二值化处理，Alpha值保持不变
		int red;
		for (int i = 0; i < iw * ih; i++) {
			
			int alpha = Color.alpha(pixels[i]);
			if (Color.red(pixels[i]) > grey) {
				red = 255;
			} else {
				red = 0;
			}
			pixels[i] = Color.argb(alpha, red, red, red);
		}
	}
	
	//===================================================================================//
	
	
	
	
	
	public void myFilter(){
		for (int y = 0; y < ih; y++) {
		    for (int x = 0; x < iw; x++) {
		        int rgb = pixels[y*iw+x];
		        
		        //1.首先灰度化，灰度值=0.3R+0.59G+0.11B： 
		        int gray = (int) (0.3 * Color.red(rgb) + 0.59
		            * Color.green(rgb) + 0.11 * Color.blue(rgb));
		        //2.其次是灰度反转：
		        gray = Color.rgb( Color.red(gray),
		        		Color.green(gray),
		        		Color.blue(gray));
		        //3.再次是二值化，取图片的平均灰度作为阈值，低于该值的全都为0，高于该值的全都为255：
		        
		        int value = Color.blue(gray);
		        if (value > 100) {
		        	gray = Color.argb(0xFF,255,255,255);
		        } else {
		        	gray = Color.argb(0xFF,0,0,0);
		        }
		        pixels[y*iw+x]=gray;
		    }
		}
	}
	
	public Bitmap getImage() {
		return image;
	}

	public int[] getPixels() {
		return pixels;
	}

	
}
