package com.a3650.posserver.core.bean;

import java.io.Serializable;

public class ApsaiOrderPayInfo implements Serializable {
	public enum ApsaiOrderPayInfoResult {
		faild('0'), suss('1'),back('2');
		private final char value;

		private ApsaiOrderPayInfoResult(char value) {
			this.value = value;
		}

		public char value() {
			return this.value;
		}

	}
	
	private long payId;
	private String apsaiId;
	private char payResult;//0失败 1：成功；
	private long payAmount;
	private String payTime;

	public long getPayId() {
		return payId;
	}

	public void setPayId(long payId) {
		this.payId = payId;
	}

	public String getApsaiId() {
		return apsaiId;
	}

	public void setApsaiId(String apsaiId) {
		this.apsaiId = apsaiId;
	}

	public char getPayResult() {
		return payResult;
	}

	public void setPayResult(char payResult) {
		this.payResult = payResult;
	}



	public long getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(long payAmount) {
		this.payAmount = payAmount;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

}
