package com.guanri.android.lib.utils;

import java.text.NumberFormat;

import android.util.FloatMath;

public class Utils {
	/**
	 * 分转元
	 * @param moneyFeng
	 * @return
	 */
	public static float fengToYuan(int moneyFeng){
		 return (float)moneyFeng/100;
	}
	/**
	 * 用于把一个byte数组复制到另一个数组
	 * @param source 被填充的目的数组
	 * @param index  填充的开始位置
	 * @param newSource 将要填充的数据
	 * @param begin     开始取数据的位置 
	 * @param length    取数据长度
	 * @param addLength 当被填充数组不够大时额外增加的长度
	 * @return
	 */
	public static byte[] insertEnoughLengthBuffer(byte[] source,int index,byte[] newSource,int begin,int length,int addLength){
		//判断长度是否足够
		if(source.length<index+length){
			byte[] temp = new byte[index+length+addLength];
			System.arraycopy(source, 0, temp, 0, source.length);
			source = temp;
		}
		System.arraycopy(newSource, begin, source, index, length);
		return source;
	}
}
