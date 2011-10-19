package com.guanri.android.insurance.bean;

//Source file: E:\\workgroup\\Test\\src\\UpCommandBean.java

/**
 * 上行命令实体类，对应有命令头各个属性和命令体
 */
public class UpCommandBean {
	// 命令长度
	private int len;
	// 命令码
	private String commandCode;
	// 终端ID
	private String posID;
	// 命令序列
	private String comSeq;
	// 数字签名
	private String mac;
	// 保留字段 默认20个空格
	private String mark = "";
	// 命令体
	private byte[] body;

	public int getLen() {
		return len;
	}

	private void setLen(int len) {
		this.len = len;
	}

	public String getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}

	public String getPosID() {
		return posID;
	}

	private void setPosID(String posID) {
		this.posID = posID;
	}

	public String getComSeq() {
		return comSeq;
	}

	private void setComSeq(String comSeq) {
		this.comSeq = comSeq;
	}

	public String getMac() {
		return mac;
	}

	private void setMac(String mac) {
		this.mac = mac;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	/**
	 * @roseuid 4DF8330C0167
	 */
	public UpCommandBean() {

	}
}
