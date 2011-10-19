package com.guanri.android.insurance.bean;

/**
 * 下行命令实体类，对应有命令头各个属性和命令体
 */
public class DownCommandBean {
	// 命令长度
	private int Len;
	// 命令码
	private String commandCode;
	// 命令序列
	private String comSeq;
	// 数字签名
	private byte[] mac;
	// 应答码
	private String answerCode;
	// 应答消息
	private String answerMsg;
	// 应该信息
	private String mark;
	// 命令体
	private byte[] body;

	public int getLen() {
		return Len;
	}

	public void setLen(int len) {
		Len = len;
	}

	public String getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}

	public String getComSeq() {
		return comSeq;
	}

	public void setComSeq(String comSeq) {
		this.comSeq = comSeq;
	}



	public byte[] getMac() {
		return mac;
	}

	public void setMac(byte[] mac) {
		this.mac = mac;
	}

	public String getAnswerCode() {
		return answerCode;
	}

	public void setAnswerCode(String answerCode) {
		this.answerCode = answerCode;
	}

	public String getAnswerMsg() {
		return answerMsg;
	}

	public void setAnswerMsg(String answerMsg) {
		this.answerMsg = answerMsg;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	/**
	 * @roseuid 4DF8330C0251
	 */
	public DownCommandBean() {

	}
}
