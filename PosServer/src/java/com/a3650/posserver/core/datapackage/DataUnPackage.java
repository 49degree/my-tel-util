/***********************************************************************
 * Module:  DataUnPackage.java
 * Author:  Administrator
 * Purpose: Defines the Class DataUnPackage
 ***********************************************************************/

package com.a3650.posserver.core.datapackage;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.exception.PacketException;
import com.a3650.posserver.core.utils.TypeConversion;

/**
 * 解包父类
 * @author Administrator
 *
 */
public abstract class DataUnPackage extends DataPackage{
	Logger logger = Logger.getLogger(DataUnPackage.class);
	protected  byte[] data;
	protected int index=0;
	

	/**
	 * 构造函数
	 * @param data
	 * @throws PacketException
	 */
	public DataUnPackage(byte[] data) throws PacketException{
		creatMessageType();
		if(data==null)
			throw new PacketException("数据为空！");
		else if(data.length<8+mMessageType.getMessageTypeLength()){
			throw new PacketException("数据长度错误！");
		}
		this.data = data;
		mDataMap = new TreeMap<Integer,Object>();
		parseMessageType();
		
	}
	
	public DataMessageType getMessageType(){
		return mMessageType;
	}
	
	/**
	 * 解析数据
	 */
	public void unPacketed(){
		index=mMessageType.getMessageTypeLength();
		this.parseBitMap();
		
		//解析基本位图数据
		for(int i=1;i<64;i++){
			if(mBitMap.getBitmapBaseValue(i)){
				Object o = parseBitValue(i+1);

				if(o!=null){
					mDataMap.put(i+1, o);
				}
				
			}
		}
		//解析扩展位图数据
		if(mBitMap.getBitmapBaseValue(0)){
			for(int i=0;i<64;i++){
				if(mBitMap.getBitmapExtendValue(i)){
					Object o = parseBitValue(i+65);
					if(o!=null){
						mDataMap.put(i+65, o);
					}
					
				}
			}
		}
		
	}
	/**
	 * 解析协议头部信息
	 */
	protected abstract void creatMessageType();
	/**
	 * 解析协议头部信息
	 */
	protected abstract void parseMessageType();
	
	/**
	 * 解析位图信息
	 */
	private void parseBitMap(){
		mBitMap = new DataBitMap();
		byte[] bitMap = new byte[8];
		System.arraycopy(data, index, bitMap, 0, 8);
		index +=8;
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(((bitMap[i]>>(7-j))&0x01)==1){
					mBitMap.setBitmapBase(i*8+j);
					//logger.debug("位图数据22："+(i*8+j));
				}
			}
		}
		
		if(mBitMap.getBitmapBaseValue(0)){
			if(!mBitMap.isExtendFlag()){//如需要的位图大于64，则加入扩展位图
				mBitMap.addBitmapExtend();
			}
			System.arraycopy(data, index, bitMap, 0, 8);
			index +=8;
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					if(bitMap[i]>>(7-j)==1){
						mBitMap.setBitmapExtend(i*8+j);
					}
				}
			}
		}
		
	}
	
	
	/**
	 * 解析数据
	 * @param position
	 * @return
	 */
	protected abstract Object parseBitValue(int position);
	
	/**
	 * 解析位信息
	 * 
	 * 调用相应类的方法
	 * 
	 * 方法名为parseFeild1，parseFeild2，parseFeild3，1、2、3分别表示位代码
	 * 
	 * @param position
	 * @return
	 */
	protected Object parseBitValue(int position,String methodName){
		//String methodName = PARSE_METHOD+position;//解析相应位数据的方法名称
		try{
			Method method = this.getClass().getMethod(methodName,null);
			
			Object value = method.invoke(this, null);
			
			return value;
		}catch(NoSuchMethodException e){
			e.printStackTrace();
			//NoSuchMethodException, SecurityException

		}catch(InvocationTargetException ive){
			ive.printStackTrace();
		}catch(IllegalAccessException ile){
			ile.printStackTrace();
		}catch(Exception e){
			
		}
		return null;
		
	}



	/**
	 * 
     * 定长
     * 以ascii 转换成String
     * @param input
     * @return
     * @throws Exception
     */
	protected String asciiToString(int len) throws UnsupportedEncodingException {
		 	index = index + len;
	        return TypeConversion.asciiToString(data, index-len, len);  
	 }
	 /**
	  * 定长BCD
	  * 转十进制数 用字符串表示
	  * @param len 定长的长度
	  * @return
	  * @throws UnsupportedEncodingException
	  */
	protected String fixBcdToInt(int len) throws UnsupportedEncodingException {
		if((len % 2)!=0){
			len ++;
		}
		len = len / 2;
		//指针移动位置
		index = index + len;
		return TypeConversion.bcd2string(data,index-len,len);
	}
 
	/**
	 * 定长BCD 左对齐的时候
	 * 转十进制数 用字符串表示
	 * @param len
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected String fixBcdToIntL(int len) throws UnsupportedEncodingException{
		boolean odd = false;
		if((len % 2)!=0){
			odd = true;
			len ++;
		}
		len = len / 2;
		//指针移动位置
		index = index + len;
		String str = TypeConversion.bcd2string(data,index-len,len);
		if(odd)
			return str.substring(0, str.length()-1);
		else
			return str;
	}
	
	/**
	 * 前面放数据长度，后面放数据的BCD数据转换为十进制数据
	 * @param lengthBit
	 * @param rightAlign 对齐方式
	 * @return
	 */
	protected  String floatBytetoBcd(int lengthBit,boolean rightAlign){
		int length = lengthBit;
		if((lengthBit %2)!=0){
			length ++;
		}
		length = length / 2;
		//活动数据具体长度
		int dataLength = Integer.valueOf(TypeConversion.bcd2string(data,index,length));
		// 指针移动到数据位
		index = index + length;
		boolean cut = false;
		if((dataLength %2)!=0){
			dataLength ++;
			cut = true;
		}
		dataLength = dataLength / 2;

		String intdata = TypeConversion.bcd2string(data,index,dataLength);
		//指针移动到数据位以后
		index = index + dataLength;
		
		if(cut){
			if(rightAlign)
				return intdata.substring(1, intdata.length());
			else
				return intdata.substring(0, intdata.length()-1);
		}else
			return intdata;
	}
	
	/**
	 * 前面放数据长度，后面放数据的Byte[]
	 * @param lengthBit
	 * @return
	 */
	protected byte[] floatbytetobyte(int lengthBit){
		int length = lengthBit;
		if((lengthBit %2)!=0){
			length ++;
		}
		length = length / 2;
		//活动数据具体长度
		int length1 = Integer.valueOf(TypeConversion.bcd2string(data,index,length));
		// 指针移动到数据位
		index = index + length;
		byte[] temp = new byte[length1];
		System.arraycopy(data, index, temp, 0, temp.length);
		index = index + length1;
		
		return temp;
		
	}
	
	
	/**
	 * 变长ASCII编码转换为字符串
	 * @param data ASCII字节数组
	 * @param lengthBit 变长长度
	 * @return
	 */
	protected  String floatASCIItoStr(int lengthBit){
		int length = lengthBit;
		if((lengthBit %2)!=0){
			length ++;
		}
		length = length / 2;
		//活动数据具体长度
		int length1 = Integer.valueOf(TypeConversion.bcd2string(data,index,length));
		logger.debug(index+":"+length1);
		// 指针移动到数据位
		index = index + length;
		// 移动指针
		index = index + length1;
		return TypeConversion.asciiToString(data, index-length1, length1); 
		
	}
}