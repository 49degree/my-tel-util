package com.szxys.mhub.subsystem.mets.bean;

public class Patientgrade {
	//Entity class of TABLE tb_PatientGrade
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	private int c_P_SurveyId;// INTEGER NOT NULL 
	private int c_Q_TopicId;// INTEGER NOT NULL 
	private int c_Q_GradeId;// INTEGER NOT NULL
	private String c_Value;// TEXT
	
	public void setC_Id(int c_Id) {
		this.c_Id = c_Id;
	}
	public int getC_Id() {
		return c_Id;
	}
	public void setC_P_SurveyId(int c_P_SurveyId) {
		this.c_P_SurveyId = c_P_SurveyId;
	}
	public int getC_P_SurveyId() {
		return c_P_SurveyId;
	}
	public void setC_Q_TopicId(int c_Q_TopicId) {
		this.c_Q_TopicId = c_Q_TopicId;
	}
	public int getC_Q_TopicId() {
		return c_Q_TopicId;
	}
	public void setC_Q_GradeId(int c_Q_GradeId) {
		this.c_Q_GradeId = c_Q_GradeId;
	}
	public int getC_Q_GradeId() {
		return c_Q_GradeId;
	}
	public void setC_Value(String c_Value) {
		this.c_Value = c_Value;
	}
	public String getC_Value() {
		return c_Value;
	}
}
