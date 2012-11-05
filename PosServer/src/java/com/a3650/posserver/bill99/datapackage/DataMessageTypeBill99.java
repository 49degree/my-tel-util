/***********************************************************************
 * Module:  JposMessageTypeBill99.java
 * Author:  Administrator
 * Purpose: Defines the Class JposMessageTypeBill99
 ***********************************************************************/

package com.a3650.posserver.bill99.datapackage;

import org.apache.log4j.Logger;

import com.a3650.posserver.bill99.bussiness.BussnessBill99;
import com.a3650.posserver.core.datapackage.DataMessageType;
import com.a3650.posserver.core.utils.TypeConversion;

/** @pdOid 60e483a8-3186-4238-b8eb-d9c1afe0c259 */
public class DataMessageTypeBill99 extends DataMessageType {
	static Logger logger =  Logger.getLogger(BussnessBill99.class);
	public static DataMessageTypeBill99 getInstance(){
		DataMessageTypeBill99 messageType = new DataMessageTypeBill99();
		//设置消息头类型
		// 003B60000000900100：003B(长度字节) + 6000000090(TPDU) + 0100(报文版本号)
		messageType.setId((byte)0x60);  
		messageType.setToAddress("0000");
		messageType.setSourceAddress("0090");
		messageType.setVersionNo("0100");
		return messageType;
	}

	// ID
	private byte id ;
	// 目的地址
	private String toAddress ;
	// 源地址
	private String sourceAddress;
	// 报文版本号
	private String versionNo;

	public byte getId() {
		return id;
	}
	public void setId(byte id) { 
		this.id = id;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public String getSourceAddress() {
		return sourceAddress;
	}
	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	/**
	 * 数据头的长度，包括长度字段
	 */
	public int getMessageTypeLength(){
		return 11;
	}
	@Override
	public byte[] parseValue() {
		// 003B60000000900100：003B(长度字节) + 6000000090(TPDU) + 0100(报文版本号)+0000(消息类型码)
		byte[] lengthByte = TypeConversion.shortToBytesEx(getDataLength());
		byte[] TPDUbyte = TypeConversion.str2bcd(getToAddress()+getSourceAddress());
		byte[] versionNoByte = TypeConversion.str2bcd(getVersionNo());
		byte[] messageTypeByte = TypeConversion.str2bcd(getMessageType());
		
		byte[] result = new byte[getMessageTypeLength()];
		System.arraycopy(lengthByte, 0, result, 0, 2);
		result[2] = id;
		System.arraycopy(TPDUbyte, 0, result, 3, 4);
		System.arraycopy(versionNoByte, 0, result, 7, 2);
		System.arraycopy(messageTypeByte, 0, result, 9, 2);
		return result;
	}
}