package com.guanri.android.insurance.bean;

public class OperatorRecordBean {
	public int Record_id;		
	public String Operator_id=null;	
	public String Operator_name	=null;
	public int getRecord_id() {
		return Record_id;
	}
	public void setRecord_id(int record_id) {
		Record_id = record_id;
	}
	public String getOperator_id() {
		return Operator_id;
	}
	public void setOperator_id(String operator_id) {
		Operator_id = operator_id;
	}
	public String getOperator_name() {
		return Operator_name;
	}
	public void setOperator_name(String operator_name) {
		Operator_name = operator_name;
	}

}
