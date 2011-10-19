package com.szxys.mhub.subsystem.mets.bean;

public class UFRrecord {
	//Entity class of TABLE tb_UFR_Record
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	private int c_UrineId;// INTEGER NOT NULL 
	private String c_OrgData="";// TEXT, 
	private String c_FinalData="";// TEXT, 
	private String c_QuantityData="";// TEXT, 
	private String c_RateData="";// TEXT
	
	public void setC_Id(int c_Id) {
		this.c_Id = c_Id;
	}
	public int getC_Id() {
		return c_Id;
	}
	public void setC_UrineId(int c_UrineId) {
		this.c_UrineId = c_UrineId;
	}
	public int getC_UrineId() {
		return c_UrineId;
	}
	public void setC_OrgData(String c_OrgData) {
		this.c_OrgData = c_OrgData;
	}
	public String getC_OrgData() {
		return c_OrgData;
	}
	public void setC_FinalData(String c_FinalData) {
		this.c_FinalData = c_FinalData;
	}
	public String getC_FinalData() {
		return c_FinalData;
	}
	public void setC_QuantityData(String c_QuantityData) {
		this.c_QuantityData = c_QuantityData;
	}
	public String getC_QuantityData() {
		return c_QuantityData;
	}
	public void setC_RateData(String c_RateData) {
		this.c_RateData = c_RateData;
	}
	public String getC_RateData() {
		return c_RateData;
	}
}
