package com.guanri.android.jpos.iso.bill99;

import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 块钱消息类型
 * @author Administrator
 *
 */
public class JposMessageType99Bill extends JposMessageType{
	
	// 报文长度
	private Short pageLength;
	// ID
	private String ID ;
	// 目的地址
	private String ServerAddress ;
	// 源地址
	private String Address;
	// 报文版本号
	private String pagever;
	
	public Short getPageLength() {
		return pageLength;
	}
	public void setPageLength(Short pageLength) {
		this.pageLength = pageLength;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
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

	@Override
	public byte[] parseValue() {
		// 003B60000000900100：003B(长度字节) + 6000000090(TPDU) + 0100(报文版本号) 
		// TODO Auto-generated method stub
		
		byte[] lengthbyte = TypeConversion.shortToBytesEx(getPageLength());
		byte[] TPDUbyte = TypeConversion.str2bcd(getID() + "" + getServerAddress()+getAddress());
		byte[] pageverbyte = TypeConversion.str2bcd(getPagever());
		byte[] msgtypebyte = TypeConversion.str2bcd(String.valueOf(getMessageType()));
		
		byte[] result = new byte[
		                  11];
		System.arraycopy(result, 0, lengthbyte, 0, 2);
		
		System.arraycopy(result, 0, TPDUbyte, 2, 5);
		
		System.arraycopy(result, 0, pageverbyte, 7, 2);
		
		System.arraycopy(result, 0, msgtypebyte, 9, 2);
		
		return result;
		
	}

}
