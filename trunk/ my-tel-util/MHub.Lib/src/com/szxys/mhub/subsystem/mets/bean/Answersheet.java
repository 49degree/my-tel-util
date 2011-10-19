package com.szxys.mhub.subsystem.mets.bean;

import java.util.ArrayList;

/**
 * 答卷(问卷调查填写的结果)
 * @author Administrator
 *
 */
public class Answersheet {
	private String PatientID;
	private String TypeId;
	private String BeginTime;
	private String EndTime;
	private String TotalScore;
	private ArrayList<String> TopicIDs;
	private ArrayList<String> GradeIDs;
	private ArrayList<String> GradeTypes;
	private ArrayList<String> GradeValues;
	
	
	public void setPatientID(String patientID) {
		PatientID = patientID;
	}
	public String getPatientID() {
		return PatientID;
	}
	public void setTypeId(String typeId) {
		TypeId = typeId;
	}
	public String getTypeId() {
		return TypeId;
	}
	public void setBeginTime(String beginTime) {
		BeginTime = beginTime;
	}
	public String getBeginTime() {
		return BeginTime;
	}
	public void setEndTime(String endTime) {
		EndTime = endTime;
	}
	public String getEndTime() {
		return EndTime;
	}
	public void setTotalScore(String totalScore) {
		TotalScore = totalScore;
	}
	public String getTotalScore() {
		return TotalScore;
	}
	public void setTopicID(ArrayList<String> topicID) {
		TopicIDs = topicID;
	}
	public ArrayList<String> getTopicID() {
		return TopicIDs;
	}
	public void setGradeType(ArrayList<String> gradeType) {
		GradeTypes = gradeType;
	}
	public ArrayList<String> getGradeType() {
		return GradeTypes;
	}
	public void setGradeValue(ArrayList<String> gradeValue) {
		GradeValues = gradeValue;
	}
	public ArrayList<String> getGradeValue() {
		return GradeValues;
	}
	public void setGradeIDs(ArrayList<String> gradeIDs) {
		GradeIDs = gradeIDs;
	}
	public ArrayList<String> getGradeIDs() {
		return GradeIDs;
	}
}
