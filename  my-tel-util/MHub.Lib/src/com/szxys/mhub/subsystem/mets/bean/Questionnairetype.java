package com.szxys.mhub.subsystem.mets.bean;

public class Questionnairetype {

	//Entity class of TABLE tb_QuestionnaireType
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	private int c_TypeId;// INTEGER NOT NULL UNIQUE, 
	private String c_Title;// TEXT, 
	private String c_Describe;// TEXT, 
	private String c_CreateTime;// TIMESTAMP NOT NULL DEFAULT '', 
	private String c_UpdateTime;// TIMESTAMP DEFAULT'', \
	private int c_IsEnable=0;// BOOL NOT NULL DEFAULT 0, 
	private int c_IsComplete=0;// BOOL NOT NULL DEFAULT 0
	
	public void setC_Id(int c_Id) {
		this.c_Id = c_Id;
	}
	public int getC_Id() {
		return c_Id;
	}
	public void setC_TypeId(int c_TypeId) {
		this.c_TypeId = c_TypeId;
	}
	public int getC_TypeId() {
		return c_TypeId;
	}
	public void setC_Title(String c_Title) {
		this.c_Title = c_Title;
	}
	public String getC_Title() {
		return c_Title;
	}
	public void setC_Describe(String c_Describe) {
		this.c_Describe = c_Describe;
	}
	public String getC_Describe() {
		return c_Describe;
	}
	public void setC_CreateTime(String c_CreateTime) {
		this.c_CreateTime = c_CreateTime;
	}
	public String getC_CreateTime() {
		return c_CreateTime;
	}
	public void setC_UpdateTime(String c_UpdateTime) {
		this.c_UpdateTime = c_UpdateTime;
	}
	public String getC_UpdateTime() {
		return c_UpdateTime;
	}
	public void setC_IsEnable(int c_IsEnable) {
		this.c_IsEnable = c_IsEnable;
	}
	public int getC_IsEnable() {
		return c_IsEnable;
	}
	public void setC_IsComplete(int c_IsComplete) {
		this.c_IsComplete = c_IsComplete;
	}
	public int getC_IsComplete() {
		return c_IsComplete;
	}
}
