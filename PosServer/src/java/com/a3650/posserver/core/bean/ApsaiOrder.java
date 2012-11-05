package com.a3650.posserver.core.bean;

import java.io.Serializable;

public class ApsaiOrder implements Serializable{  
	private String apsaiId;
	private String customName;
	private String customAddr;
	private String customLink;
	private long apsaiAmount;
	public String getApsaiId() {
		return apsaiId;
	}
	public void setApsaiId(String apsaiId) {
		this.apsaiId = apsaiId;
	}
	public String getCustomName() {
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	public String getCustomAddr() {
		return customAddr;
	}
	public void setCustomAddr(String customAddr) {
		this.customAddr = customAddr;
	}
	public String getCustomLink() {
		return customLink;
	}
	public void setCustomLink(String customLink) {
		this.customLink = customLink;
	}
	public long getApsaiAmount() {
		return apsaiAmount;
	}
	public void setApsaiAmount(long apsaiAmount) {
		this.apsaiAmount = apsaiAmount;
	}


}
