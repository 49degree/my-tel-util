package com.szxys.mhub.subsystem.mets.bean;

public class Questionnairetopic {
	//Entity class of TABLE tb_QuestionnaireTopic
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	private int c_Q_TypeId;// INTEGER NOT NULL 
	private int c_TopicId;// INTEGER NOT NULL, 
	private String c_Title;// TEXT, 
	private String c_Describe;// TEXT, 
	private int c_GradeType;// SMALLINT NOT NULL, 
	private int c_GradeCalcType;// SMALLINT NOT NULL
	
	
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
	public void setC_TopicId(int c_TopicId) {
		this.c_TopicId = c_TopicId;
	}
	public int getC_TopicId() {
		return c_TopicId;
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
	public void setC_GradeType(int c_GradeType) {
		this.c_GradeType = c_GradeType;
	}
	public int getC_GradeType() {
		return c_GradeType;
	}
	public void setC_GradeCalcType(int c_GradeCalcType) {
		this.c_GradeCalcType = c_GradeCalcType;
	}
	public int getC_GradeCalcType() {
		return c_GradeCalcType;
	}
}
