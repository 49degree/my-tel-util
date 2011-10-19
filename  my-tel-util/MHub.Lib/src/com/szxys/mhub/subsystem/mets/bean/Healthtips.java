package com.szxys.mhub.subsystem.mets.bean;

public class Healthtips {
	//Entity class of TABLE tb_HealthTips
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	private String c_WebUniqueId; //  TEXT, 
	private String c_Datetime; //  TIMESTAMP NOT NULL, 
	private String c_Sender; //  TEXT, 
	private String c_Content; // TEXT);
	
	public void setC_Id(int c_Id) {
		this.c_Id = c_Id;
	}
	public int getC_Id() {
		return c_Id;
	}
	public void setC_WebUniqueId(String c_WebUniqueId) {
		this.c_WebUniqueId = c_WebUniqueId;
	}
	public String getC_WebUniqueId() {
		return c_WebUniqueId;
	}
	public void setC_Datetime(String c_Datetime) {
		this.c_Datetime = c_Datetime;
	}
	public String getC_Datetime() {
		return c_Datetime;
	}
	public void setC_Sender(String c_Sender) {
		this.c_Sender = c_Sender;
	}
	public String getC_Sender() {
		return c_Sender;
	}
	public void setC_Content(String c_Content) {
		this.c_Content = c_Content;
	}
	public String getC_Content() {
		return c_Content;
	}
}
