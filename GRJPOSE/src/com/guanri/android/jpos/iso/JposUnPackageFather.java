package com.guanri.android.jpos.iso;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;

import com.guanri.android.exception.PacketException;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 解包父类
 * @author Administrator
 *
 */
public abstract class JposUnPackageFather {
	Logger logger = Logger.getLogger(JposUnPackageFather.class);
	protected  byte[] data;
	protected int index=0;
	
	protected TreeMap<Integer,Object> mReturnMap = null;//表示需要分包的数据
	
	private JposBitMap jposBitMap = null;
	protected JposMessageType mMessageType = null;
	
	
	public JposUnPackageFather(byte[] data) throws PacketException{
		if(data==null)
			throw new PacketException("数据为空！");
		this.data = data;
		mReturnMap = new TreeMap<Integer,Object>();
		
	}
	
	
	
	/**
	 * 解析数据
	 */
	public void unPacketed(){
		this.parseMessageType();
		
		this.parseBitMap();
		
		//解析基本位图数据
		for(int i=1;i<64;i++){
			if(jposBitMap.getBitmapBaseValue(i)){
				mReturnMap.put(i+1, parseBitValue(i+1));
			}
		}
		//解析扩展位图数据
		if(jposBitMap.getBitmapBaseValue(0)){
			for(int i=0;i<64;i++){
				if(jposBitMap.getBitmapExtendValue(i)){
					mReturnMap.put(i+65, parseBitValue(i+65));
				}
			}
		}
		
	}
	
	/**
	 * 解析协议头部信息
	 */
	protected abstract void parseMessageType();
	
	/**
	 * 解析位图信息
	 */
	private void parseBitMap(){
		jposBitMap = new JposBitMap();
		byte[] bitMap = new byte[8];
		System.arraycopy(data, index, bitMap, 0, 8);
		index +=8;
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(((bitMap[i]>>(7-j))&0x01)==1){
					jposBitMap.setBitmapBase(i*8+j);
					//logger.debug("位图数据22："+(i*8+j));
				}
			}
		}
		
		if(jposBitMap.getBitmapBaseValue(0)){
			if(!jposBitMap.isExtendFlag()){//如需要的位图大于64，则加入扩展位图
				jposBitMap.addBitmapExtend();
			}
			System.arraycopy(data, index, bitMap, 0, 8);
			index +=8;
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					if(bitMap[i]>>(7-j)==1){
						jposBitMap.setBitmapExtend(i*8+j);
					}
				}
			}
		}
		
		
		logger.debug("位图数据11："+TypeConversion.byteTo0XString(bitMap, 0, bitMap.length));
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
			logger.debug("位置:"+position);
			Method method = this.getClass().getMethod(methodName,null);
			
			Object value = method.invoke(this, null);
			logger.debug("数据:"+value);
			return value;
		}catch(NoSuchMethodException e){
			e.printStackTrace();
			//NoSuchMethodException, SecurityException

		}catch(InvocationTargetException ive){
			ive.printStackTrace();
		}catch(IllegalAccessException ile){
			ile.printStackTrace();
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
	 * 前面放数据长度，后面放数据的BCD数据转换为十进制数据
	 * @param data
	 * @param lengthBit
	 * @return
	 */
	protected  String floatbytetoint(int lengthBit){
		int length = lengthBit;
		if((lengthBit %2)!=0){
			length ++;
		}
		length = length / 2;
		//活动数据具体长度
		int length1 = Integer.valueOf(TypeConversion.bcd2string(data,index,length));
		// 指针移动到数据位
		index = index + length;
		if((length1 %2)!=0){
			length1 ++;
		}
		length1 = length1 / 2;

		String intdata = TypeConversion.bcd2string(data,index,length1);
		//指针移动到数据位以后
		index = index + length1;
		return intdata;
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
		try {
			return TypeConversion.asciiToString(data, index-length1, length1); 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public TreeMap<Integer, Object> getmReturnMap() {
		return mReturnMap;
	}



	public void setmReturnMap(TreeMap<Integer, Object> mReturnMap) {
		this.mReturnMap = mReturnMap;
	}



	public JposBitMap getJposBitMap() {
		return jposBitMap;
	}



	public void setJposBitMap(JposBitMap jposBitMap) {
		this.jposBitMap = jposBitMap;
	}



	public JposMessageType getmMessageType() {
		return mMessageType;
	}



	public void setmMessageType(JposMessageType mMessageType) {
		this.mMessageType = mMessageType;
	}
}
