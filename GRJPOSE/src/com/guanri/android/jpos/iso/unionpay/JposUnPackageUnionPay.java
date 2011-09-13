package com.guanri.android.jpos.iso.unionpay;

import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.iso.JposUnPackageFather;
import com.guanri.android.jpos.iso.bill99.JposMessageType99Bill;

/**
 * 块钱解包类
 * @author Administrator
 *
 */

public class JposUnPackageUnionPay extends JposUnPackageFather{
	public final static String PARSE_METHOD = "parseFeild";
	
	public JposUnPackageUnionPay(byte[] data) throws PacketException{
		super(data);
	}
	
	/**
	 * 解析协议头部信息
	 */
	@Override
	protected void creatMessageType(){
	}
	/**
	 * 解析协议头部信息
	 */
	@Override
	protected void parseMessageType(){
		
	}
	
	
	/**
	 * 子类根据定义的解析位数据的方法名构造方法名称，调用父类中的解析方法
	 * 方法名最好与位标志有规律
	 * 例如方法名为parseFeild1，parseFeild2，parseFeild3，1、2、3分别表示位代码
	 * 
	 */
	@Override
	protected Object parseBitValue(int position){ 
		String methodName = PARSE_METHOD+position;//解析相应位数据的方法名称
		return parseBitValue(position,methodName);
	}
}
