package com.a3650.posserver.core.bean;

import java.io.Serializable;

public class PosTerminalCheckIn implements Serializable{  
    /**  
     *   
     */ 
    private static final long serialVersionUID = 1L;  

    private String posId; 
    
    private String checkInTime;
    
    private int rootKeyId = -1;
    
    private String pik;
    private String mak;
    private String trk;
    private String sendEncodeKey;//下发的密钥密文
    private String referenceNumber;//交易参考号
    private String batchNumber;//交易批次号
    private String sysAuditNumber;//受卡方系统跟踪号
    private char state;//‘0’未签退，‘1’已签退



	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public String getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(String checkInTime) {
		this.checkInTime = checkInTime;
	}

	public int getRootKeyId() {
		return rootKeyId;
	}

	public void setRootKeyId(int rootKeyId) {
		this.rootKeyId = rootKeyId;
	}

	public String getPik() {
		return pik;
	}

	public void setPik(String pik) {
		this.pik = pik;
	}

	public String getMak() {
		return mak;
	}

	public void setMak(String mak) {
		this.mak = mak;
	}

	public String getTrk() {
		return trk;
	}

	public void setTrk(String trk) {
		this.trk = trk;
	}

	public String getSendEncodeKey() {
		return sendEncodeKey;
	}

	public void setSendEncodeKey(String sendEncodeKey) {
		this.sendEncodeKey = sendEncodeKey;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public char getState() {
		return state;
	}

	public void setState(char state) {
		this.state = state;
	}

	public String getSysAuditNumber() {
		return sysAuditNumber;
	}

	public void setSysAuditNumber(String sysAuditNumber) {
		this.sysAuditNumber = sysAuditNumber;
	}


	
	
}
