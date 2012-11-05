package com.a3650.posserver.core.bean;

import java.io.Serializable;

public class CheckBatchInfo implements Serializable{  
	public enum CheckBatchResult {
		normal('0'),less('1'), more('2');
		private final char value;

		private CheckBatchResult(char value) {
			this.value = value;
		}

		public char value() {
			return this.value;
		}
	}
	
	private long checkId;
	private String batchNumber;
	private int payNum;
	private long payAmount;
	private int backNum;
	private long backAmount;
	private char checkResult;
	private String checkTime;
	public long getCheckId() {
		return checkId;
	}
	public void setCheckId(long checkId) {
		this.checkId = checkId;
	}
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public int getPayNum() {
		return payNum;
	}
	public void setPayNum(int payNum) {
		this.payNum = payNum;
	}
	public long getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(long payAmount) {
		this.payAmount = payAmount;
	}
	public int getBackNum() {
		return backNum;
	}
	public void setBackNum(int backNum) {
		this.backNum = backNum;
	}
	public long getBackAmount() {
		return backAmount;
	}
	public void setBackAmount(long backAmount) {
		this.backAmount = backAmount;
	}
	public char getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(char checkResult) {
		this.checkResult = checkResult;
	}
	public String getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}
	
	
}
