package com.guanri.android.jpos.network.test;
import java.io.File;
import java.io.FileInputStream;

import com.guanri.android.lib.utils.TypeConversion;


public class ReadFile {
	
	
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
	
	private static byte[] recvAllBuffer = new byte[2048];//接收到的数据缓冲区
	private static byte[] recvbuf = new byte[1024];
	private static int recvAllBufferIndex = 0;
	
	public static byte[] read(String filePath){
		File file = new File(filePath);
		try{
			FileInputStream in = new FileInputStream(file);
			int readSzie = 0;
			while((readSzie=in.read(recvbuf))>0){
				//填充数据到缓存
				recvAllBuffer = insertEnoughLengthBuffer(recvAllBuffer, recvAllBufferIndex, recvbuf, 0, readSzie, 512);
				recvAllBufferIndex +=readSzie;
			}
			//System.out.println(TypeConversion.byte2hex(recvAllBuffer));

			byte[] data = new byte[recvAllBufferIndex];
			System.arraycopy(recvAllBuffer, 0, data, 0, recvAllBufferIndex);
			return data;
		}catch(Exception e){
			return null;
		}
	}
	
	public static void main(String[] args){
		System.out.println(TypeConversion.byte2hex(ReadFile.read("E:\\yang_workgroup\\workgroup\\TestJavaProject\\src\\Req.bin")));
	}
}
