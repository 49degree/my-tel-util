package com.szxys.mhub.subsystem.mets.bean;

import java.util.ArrayList;


/**
 * 问卷题目
 * @author Administrator
 *
 */
public class Question {
	private int surveyID;
	private int c_TypeId; //问卷ID
	private int c_TopicId; //主题ID
	private String c_Title;//题目内容
	private String c_Describe; //题目描述
	private int c_GradeType; 
	private int c_GradeCalcType;
	
	private ArrayList<Integer> gradeIdList; //选项ID
	private ArrayList<String> describeList; //选项内容
	private ArrayList<String> valueList; //选项分值
	private int selectedGradeId=0; //已作答  选中ID
	private int state=0; //0问卷未作答，1已作答已经保存但没有上传，2已经上传了(不能修改)
	//private String[] selectedGradeIDs; //已选ID (编辑状态)
	//private boolean isUploaded=false; //已经上传了
	private String c_CreateTime;
	
	public void setC_TypeId(int c_TypeId) {
		this.c_TypeId = c_TypeId;
	}
	public int getC_TypeId() {
		return c_TypeId;
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
	public void setGradeIdList(ArrayList<Integer> gradeList) {
		this.gradeIdList = gradeList;
	}
	public ArrayList<Integer> getGradeIdList() {
		return gradeIdList;
	}
	public void setDescribeList(ArrayList<String> describeList) {
		this.describeList = describeList;
	}
	public ArrayList<String> getDescribeList() {
		return describeList;
	}
	public void setValueList(ArrayList<String> valueList) {
		this.valueList = valueList;
	}
	public ArrayList<String> getValueList() {
		return valueList;
	}
	public void setC_CreateTime(String c_CreateTime) {
		this.c_CreateTime = c_CreateTime;
	}
	public String getC_CreateTime() {
		return c_CreateTime;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	public int getState() {
		return state;
	}
	public void setSelectedGradeId(int selectedGradeId) {
		this.selectedGradeId = selectedGradeId;
	}
	public int getSelectedGradeId() {
		return selectedGradeId;
	}
	public void setSurveyID(int surveyID) {
		this.surveyID = surveyID;
	}
	public int getSurveyID() {
		return surveyID;
	}
}
