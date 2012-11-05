/***********************************************************************
 * Module:  JposMessageTypeBill99.java
 * Author:  Administrator
 * Purpose: Defines the Class JposMessageTypeBill99
 ***********************************************************************/

package com.a3650.posserver.allinpay.datapackage;

import com.a3650.posserver.core.datapackage.DataMessageType;
import com.a3650.posserver.core.utils.TypeConversion;

/** @pdOid 60e483a8-3186-4238-b8eb-d9c1afe0c259 */
public class DataMessageTypeAllinpay extends DataMessageType {

	//003B6000090000010008002020010000C00008990000000005000932303130303630313130343131303034353131303031320009303030303031303031
	public static DataMessageTypeAllinpay getInstance(){
		DataMessageTypeAllinpay messageType = new DataMessageTypeAllinpay();
		//设置消息头类型
		// 003B60000000900100：003B(长度字节) + 6000000090(TPDU) + 0100(报文版本号)
		messageType.setId((byte)0x60);  
		messageType.setToAddress("0017");
		messageType.setSourceAddress("0000");
		messageType.setTerminalCriterion("02");
		messageType.setTerminalState("0");
		messageType.setRequest("0");
		return messageType;
	}
	// ID
	private byte id ;
	// 目的地址
	private String toAddress ;
	// 源地址
	private String sourceAddress;
	// 应用类别定义
	private String appType;
	// 终端遵循的规范
	private String terminalCriterion;
	// 终端状态
	private String terminalState;
	// 处理要求
	private String request;
	// 保留使用
	private String reservation="000000";
	// 商户号
	private String companyId;
	//终端号
	private String posId;
	


	
	
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
	public String getAppType() {
		return appType;
	}
	public void setAppType(String appType) {
		this.appType = appType;
	}
	public String getTerminalCriterion() {
		return terminalCriterion;
	}
	public void setTerminalCriterion(String terminalCriterion) {
		this.terminalCriterion = terminalCriterion;
	}
	public String getTerminalState() {
		return terminalState;
	}
	public void setTerminalState(String terminalState) {
		this.terminalState = terminalState;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getReservation() {
		return reservation;
	}
	public void setReservation(String reservation) {
		this.reservation = reservation;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getPosId() {
		return posId;
	}
	public void setPosId(String posId) {
		this.posId = posId;
	}
	/**
	 * 数据头的长度，包括长度字段
	 */
	public int getMessageTypeLength(){
		return 38;
	}
	@Override
	public byte[] parseValue() {
		// 003B60000000900100：003B(长度字节) + 6000000090(TPDU) + 0100(报文版本号) 
		// TODO Auto-generated method stub
		
		byte[] lengthbyte = TypeConversion.shortToBytesEx(getDataLength());
		byte[] TPDUbyte = TypeConversion.str2bcd(getToAddress()+getSourceAddress());
		// 报文头,BCD
		String msgHead = getAppType() + getTerminalCriterion() + getTerminalState() + getRequest() + reservation ;
		byte[] msgHeadByte = TypeConversion.str2bcd(msgHead);
		byte[] msgtypebyte = TypeConversion.str2bcd(getMessageType());
		byte[] msgMerchantterminaID = TypeConversion.stringToAscii(getCompanyId()+getPosId());
		
		byte[] result = new byte[getMessageTypeLength()];
		System.arraycopy(lengthbyte, 0, result, 0, 2);
		result[2] = id;
		System.arraycopy(TPDUbyte, 0, result, 3, 4);
		System.arraycopy(msgHeadByte, 0, result, 7, 6);
		// 终端号与商户号
		System.arraycopy(msgMerchantterminaID, 0, result, 13, 23);
		// 消息类型
		System.arraycopy(msgtypebyte, 0, result, 36, 2);
		return result;
		
	}



}