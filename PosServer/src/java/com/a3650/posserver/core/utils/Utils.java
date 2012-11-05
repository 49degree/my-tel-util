package com.a3650.posserver.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils {
	public final static String timeFormat = "yyyyMMdd HHmmss";
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
	
	/**
	 * 封装数据头
	 * 	 * @param source
	 * @return
	 */
	public static byte[] codeHeader(byte[] source){
			byte[] data = new byte[source.length+5];
			data[0] = 0x4D;//头
			System.arraycopy(TypeConversion.shortToBytesEx((short)(source.length+1)), 0, data, 1, 2);//长度
			data[3] = 0x00;//命令ID
			System.arraycopy(source, 0, data, 4, source.length);//长度
			System.out.println("发送数据："+TypeConversion.byte2hex(data));
			return data;
	}
	
	/**
	 * 计算校验和
	 * @param buffer
	 * @return
	 */
	public static byte calCheckSum(byte[] buffer){
		if(buffer==null||buffer.length<1)
			return -1;
		byte result = buffer[0];
		for(int i=1;i<buffer.length;i++)
			result ^= buffer[i];
		return result;
	}
	
	public static String getTimeString(Date time,String formatStr){
		SimpleDateFormat sf = new SimpleDateFormat(formatStr);
		return sf.format(time);
	}

}
