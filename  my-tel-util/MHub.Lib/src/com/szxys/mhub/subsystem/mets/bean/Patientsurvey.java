package com.szxys.mhub.subsystem.mets.bean;

public class Patientsurvey {
	//Entity class of TABLE tb_PatientSurvey
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	private String c_S_PatientId;// TEXT NOT NULL 
	private int c_Q_TypeId;// INTEGER NOT NULL 
	private String c_BeginTime;// TIMESTAMP NOT NULL DEFAULT '',
	private String c_EndTime;// TIMESTAMP NOT NULL DEFAULT '', 
	private float c_TotalScore=0;// FLOAT, c_IsEdit BOOL DEFAULT 0, 
	private int c_IsEdit=0;// BOOL DEFAULT 0, 
	private int c_IsUpload=0;// BOOL NOT NULL DEFAULT 0
	
	public void setC_Id(int c_Id) {
		this.c_Id = c_Id;
	}
	public int getC_Id() {
		return c_Id;
	}
	public void setC_S_PatientId(String c_S_PatientId) {
		this.c_S_PatientId = c_S_PatientId;
	}
	public String getC_S_PatientId() {
		return c_S_PatientId;
	}
	public void setC_Q_TypeId(int c_Q_TypeId) {
		this.c_Q_TypeId = c_Q_TypeId;
	}
	public int getC_Q_TypeId() {
		return c_Q_TypeId;
	}
	public void setC_BeginTime(String c_BeginTime) {
		this.c_BeginTime = c_BeginTime;
	}
	public String getC_BeginTime() {
		return c_BeginTime;
	}
	public void setC_EndTime(String c_EndTime) {
		this.c_EndTime = c_EndTime;
	}
	public String getC_EndTime() {
		return c_EndTime;
	}
	public void setC_TotalScore(float c_TotalScore) {
		this.c_TotalScore = c_TotalScore;
	}
	public float getC_TotalScore() {
		return c_TotalScore;
	}
	public void setC_IsEdit(int c_IsEdit) {
		this.c_IsEdit = c_IsEdit;
	}
	public int getC_IsEdit() {
		return c_IsEdit;
	}
	public void setC_IsUpload(int c_IsUpload) {
		this.c_IsUpload = c_IsUpload;
	}
	public int getC_IsUpload() {
		return c_IsUpload;
	}
}
