package com.guanri.android.jpos.iso;

import java.util.TreeMap;

import com.guanri.android.exception.PacketException;

import com.guanri.android.jpos.iso.bill99.JposMessageType99Bill;
import com.guanri.android.jpos.iso.bill99.JposPackage99Bill;
import com.guanri.android.jpos.iso.bill99.JposUnPackage99Bill;
import com.guanri.android.jpos.iso.unionpay.JposMessageTypeUnionPay;
import com.guanri.android.jpos.iso.unionpay.JposPackageUnionPay;
import com.guanri.android.lib.utils.TypeConversion;

public class Test {
	static byte[] data1 = null;

	
	public static void testpage(){
		data1 = loginTest();
		JposUnPackage99Bill jposUnPackage99Bill;
		
		try {
			
			jposUnPackage99Bill = new JposUnPackage99Bill(data1);
			JposBitMap bitmap = jposUnPackage99Bill.getJposBitMap();
			
			jposUnPackage99Bill.unPacketed();
		} catch (PacketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


	public static void main(String[] args) {
		System.out.println("a");
		testpage();
	}
	
	public static byte[] loginTest(){
		//构造签到所需各域
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		sendMap.put(2, "1234567890123456789");
		// 域3 处理码
		sendMap.put(3, "880000");
		sendMap.put(4, "000000000100");
		// 域11 流水号
		sendMap.put(11, "000001");
		// 域 12 本地交易时间
		sendMap.put(12, "1057");
		// 域13 本地交易日期
		sendMap.put(13, "0906");
		sendMap.put(14, "1305");
		sendMap.put(19, "12");
		sendMap.put(20, "00000000000000100000");
		sendMap.put(22, "001");
		sendMap.put(23, "002");
		
		// 域24 网络信息码
		sendMap.put(24, "123");
		sendMap.put(25, "10");
		
		sendMap.put(34, "0123456789012345678901234567");
		//sendMap.put
		// 域37 系统参考号
		sendMap.put(37, "ABCABCABC");
		// 域41 终端代码
		sendMap.put(41, "00000001");
		// 域42 商户代码
		sendMap.put(42, "000000000000001");
		sendMap.put(44, "1234");
		// 域46 附加数据
		sendMap.put(46, "abc");
		// 域47 附加数据
		//sendMap.put(47, "cba");
		sendMap.put(48, "abcd");
		sendMap.put(49, "156");
		sendMap.put(53, "");
		// 域60 自定义域     60.1 交易类型码 00  60.2 批次号  000001 网络管理信息码 001
		sendMap.put(60, "00000001001");
		// 域64 消息签订码
		sendMap.put(63, "0101010101");
		
		
		JposMessageType99Bill messageType = new JposMessageType99Bill();
		//设置消息头类型
		messageType.setMessageType(800);
		
		JposPackage99Bill jposPackage99Bill = new JposPackage99Bill(sendMap,messageType);
	 
		return jposPackage99Bill.packaged();
		
	}
	
	/**
	 * 签到测试
	 */
	public static byte[] login(){
		//构造签到所需各域
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		sendMap.put(2, "1234567890123456789");
		// 域3 处理码
		sendMap.put(3, "990000");
		//sendMap.put(4, "000000000100");
		// 域11 流水号
		sendMap.put(11, "000001");
		// 域 12 本地交易时间
		sendMap.put(12, "1455");
		// 域13 本地交易日期
		sendMap.put(13, "0908");
		
		// 域24 网络信息码
		sendMap.put(24, "123");
		// 域41 终端代码
		sendMap.put(41, "00000001");
		// 域42 商户代码
		sendMap.put(42, "000000000000001");
		sendMap.put(44, "1234");
		// 域46 附加数据
		sendMap.put(46, "abc");
		// 域47 附加数据
		//sendMap.put(47, "cba");
		sendMap.put(48, "abcd");
		sendMap.put(49, "156");
		sendMap.put(53, "");
		// 域60 自定义域     60.1 交易类型码 00  60.2 批次号  000001 网络管理信息码 001
		sendMap.put(60, "00000001001");		
		
		JposMessageType99Bill messageType = new JposMessageType99Bill();
		//设置消息头类型
		messageType.setMessageType(800);
		
		JposPackage99Bill jposPackage99Bill = new JposPackage99Bill(sendMap,messageType);
	 
		return jposPackage99Bill.packaged();
	}
	
	public byte[] queryMoney(){
		//构造签到所需各域
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		sendMap.put(2, "1234567890123456789");
		// 域3 处理码
		sendMap.put(3, "310000");
		//sendMap.put(4, "000000000100");
		// 域11 流水号
		sendMap.put(11, "000001");
		// 域 12 本地交易时间
		sendMap.put(12, "1455");
		// 域13 本地交易日期
		sendMap.put(13, "0908");
		
		// 域24 网络信息码
		sendMap.put(24, "123");
		// 域41 终端代码
		sendMap.put(41, "00000001");
		// 域42 商户代码
		sendMap.put(42, "000000000000001");
		sendMap.put(44, "1234");
		// 域46 附加数据
		sendMap.put(46, "abc");
		// 域47 附加数据
		//sendMap.put(47, "cba");
		sendMap.put(48, "abcd");
		sendMap.put(49, "156");
		sendMap.put(53, "");
		// 域60 自定义域     60.1 交易类型码 00  60.2 批次号  000001 网络管理信息码 001
		sendMap.put(60, "00000001001");		
		
		JposMessageType99Bill messageType = new JposMessageType99Bill();
		//设置消息头类型
		messageType.setMessageType(800);
		
		JposPackage99Bill jposPackage99Bill = new JposPackage99Bill(sendMap,messageType);
	 
		return jposPackage99Bill.packaged();
		
	}
	
	
	public void responelogin(){
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		
	}
}
