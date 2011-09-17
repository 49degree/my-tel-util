package com.guanri.android.jpos.iso.unionpay;

import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 银联消息类型
 * @author Administrator
 *
 */
public class JposMessageTypeUnionPay extends JposMessageType{
	
	private String ID ;
	// 目的地址
	private String ServerAddress ;
	// 源地址
	private String Address;
	// 应用类型定义
	private String AppType ;
	// 软件版本号
	private String SoftVer ;
	// 终端状态
	private String Posstate ;
	// 处理请求
	private String Disposal;
	// 保留使用
	private String Preserving;
	
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
	public String getAppType() {
		return AppType;
	}
	public void setAppType(String appType) {
		AppType = appType;
	}
	public String getSoftVer() {
		return SoftVer;
	}
	public void setSoftVer(String softVer) {
		SoftVer = softVer;
	}
	public String getPosstate() {
		return Posstate;
	}
	public void setPosstate(String posstate) {
		Posstate = posstate;
	}
	public String getDisposal() {
		return Disposal;
	}
	public void setDisposal(String disposal) {
		Disposal = disposal;
	}
	public String getPreserving() {
		return Preserving;
	}
	public void setPreserving(String preserving) {
		Preserving = preserving;
	}
	
	
	public int getMessageTypeLength(){
		return 21;
	}
	/**
	 * 22
	 * 组装消息类型
	 */
	@Override
	public byte[] parseValue() {
		// TODO Auto-generated method stub
		byte[] temp = new byte[21];
		// ID
		System.arraycopy(TypeConversion.str2bcd(getID()), 0, temp, 0, 1);
		// 目的地址
		System.arraycopy(TypeConversion.str2bcd(getServerAddress()), 0, temp, 1, 2);
		// 源地址
		System.arraycopy(TypeConversion.str2bcd(getAddress()), 0, temp, 3, 2);
		
		
		// 应用类型定义
		System.arraycopy(TypeConversion.str2bcd(getAppType()), 0, temp, 5, 2);
		// 软件版本号
		System.arraycopy(TypeConversion.str2bcd(getSoftVer()), 0, temp, 7, 2);
		// 终端状态
		System.arraycopy(TypeConversion.str2bcd(getPosstate()), 0, temp, 9, 1);
		// 处理请求
		System.arraycopy(TypeConversion.str2bcd(getDisposal()), 0, temp, 10, 1);
		// 保留使用
		System.arraycopy(TypeConversion.str2bcd(getPreserving()), 0, temp, 11, 6);
		
		// 消息类型
		System.arraycopy(TypeConversion.str2bcd(String.valueOf(getMessageType())), 0, temp, 17, 4);
		
		return temp;
	}
	
	

}
