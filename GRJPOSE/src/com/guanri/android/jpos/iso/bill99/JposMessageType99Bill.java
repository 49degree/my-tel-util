package com.guanri.android.jpos.iso.bill99;

import com.guanri.android.jpos.constant.JposConstant.MessageTypeDefine99Bill;
import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 块钱消息类型
 * @author Administrator
 *
 */
public class JposMessageType99Bill extends JposMessageType{
	//003B6000090000010008002020010000C00008990000000005000932303130303630313130343131303034353131303031320009303030303031303031
	public static JposMessageType99Bill getInstance(){
		JposMessageType99Bill messageType = new JposMessageType99Bill();
		//设置消息头类型

		// 003B60000000900100：003B(长度字节) + 6000000090(TPDU) + 0100(报文版本号)
		messageType.setPageLength((short)59);
		messageType.setId((byte)0x60);  
		messageType.setServerAddress("0000");
		messageType.setServerAddress("0000");
		messageType.setAddress("0090");
		messageType.setPagever("0100");
		return messageType;
	}

	// ID
	private byte id ;
	// 目的地址
	private String ServerAddress ;
	// 源地址
	private String Address;
	// 报文版本号
	private String pagever;

	public byte getId() {
		return id;
	}
	public void setId(byte id) { 
		this.id = id;
	}
	public String getServerAddress() {
		return ServerAddress;
	}

	public void setServerAddress(String serverAddress) {
		ServerAddress = serverAddress;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getPagever() {
		return pagever;
	}

	public void setPagever(String pagever) {
		this.pagever = pagever;
	}
	
	/**
	 * 数据头的长度，包括长度字段
	 */
	public int getMessageTypeLength(){
		return 11;
	}
	@Override
	public byte[] parseValue() {
		// 003B60000000900100：003B(长度字节) + 6000000090(TPDU) + 0100(报文版本号) 
		// TODO Auto-generated method stub
		
		byte[] lengthbyte = TypeConversion.shortToBytesEx(getPageLength());
		byte[] TPDUbyte = TypeConversion.str2bcd(getServerAddress()+getAddress());
		byte[] pageverbyte = TypeConversion.str2bcd(getPagever());
		byte[] msgtypebyte = TypeConversion.str2bcd(getMessageType());
		
		byte[] result = new byte[11];
		System.arraycopy(lengthbyte, 0, result, 0, 2);
		result[2] = id;
		System.arraycopy(TPDUbyte, 0, result, 3, 4);
		
		System.arraycopy(pageverbyte, 0, result, 7, 2);
		
		System.arraycopy(msgtypebyte, 0, result, 9, 2);
		
		return result;
		
	}

}
