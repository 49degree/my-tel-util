package com.szxys.mhub.subsystem.mets.bean;

public class Questionnairegrade {
	////Entity class of TABLE tb_QuestionnaireGrade
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	private int c_Q_TypeId;// INTEGER NOT NULL 
	private int c_Q_TopicId;// INTEGER NOT NULL 
	private int c_GradeId;// INTEGER NOT NULL, 
	private String c_Describe;// TEXT, 
	private String c_Value;// TEXT
	
	public void setC_Id(int c_Id) {
		this.c_Id = c_Id;
	}
	public int getC_Id() {
		return c_Id;
	}
	public void setC_Q_TypeId(int c_Q_TypeId) {
		this.c_Q_TypeId = c_Q_TypeId;
	}
	public int getC_Q_TypeId() {
		return c_Q_TypeId;
	}
	public void setC_Q_TopicId(int c_Q_TopicId) {
		this.c_Q_TopicId = c_Q_TopicId;
	}
	public int getC_Q_TopicId() {
		return c_Q_TopicId;
	}
	public void setC_GradeId(int c_GradeId) {
		this.c_GradeId = c_GradeId;
	}
	public int getC_GradeId() {
		return c_GradeId;
	}
	public void setC_Describe(String c_Describe) {
		this.c_Describe = c_Describe;
	}
	public String getC_Describe() {
		return c_Describe;
	}
	public void setC_Value(String c_Value) {
		this.c_Value = c_Value;
	}
	public String getC_Value() {
		return c_Value;
	}
}
