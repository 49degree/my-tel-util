package com.a3650.posserver.core.bean;

import java.io.Serializable;

public class PayInfo implements Serializable{  
	public enum PayInfoState {
		noInsure('0'), insure('1'),back('2');
		private final char value;

		private PayInfoState(char value) {
			this.value = value;
		}

		public char value() {
			return this.value;
		}

	}
	private long payId;
	private String posId;
	private String posOrderId;
	private String referenceNum;
	private String addTime;
	private String insureTime;
	private char payState;
	private String batchNumber;
	private long payAmount;
	public long getPayId() {
		return payId;
	}
	public void setPayId(long payId) {
		this.payId = payId;
	}
	public String getPosId() {
		return posId;
	}
	public void setPosId(String posId) {
		this.posId = posId;
	}
	public String getPosOrderId() {
		return posOrderId;
	}
	public void setPosOrderId(String posOrderId) {
		this.posOrderId = posOrderId;
	}
	public String getReferenceNum() {
		return referenceNum;
	}
	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public char getPayState() {
		return payState;
	}
	public void setPayState(char payState) {
		this.payState = payState;
	}
	public String getInsureTime() {
		return insureTime;
	}
	public void setInsureTime(String insureTime) {
		this.insureTime = insureTime;
	}
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public long getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(long payAmount) {
		this.payAmount = payAmount;
	}
	
	
}
