package com.guanri.android.insurance.bean;

/**
 * 4.1.5	废单记录
 * @author wuxiang
 *
 */
public class SaleOrderUselessBean {
	// 作废开始单证号
	public String Paper_no;
	// 废单数量
	public int number;
	// 操作工号
	public String Operator_id;
	// 操作时间
	public String Operate_time;
	// 备注
	public String memo;
	// 对账标志
	public boolean Check_state;
	//批次号
	public int Check_id;
	 
	public int getCheck_id() {
		return Check_id;
	}
	public void setCheck_id(int check_id) {
		Check_id = check_id;
	}	
	public String getPaper_no() {
		return Paper_no;
	}
	public void setPaper_no(String paper_no) {
		Paper_no = paper_no;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getOperator_id() {
		return Operator_id;
	}
	public void setOperator_id(String operator_id) {
		Operator_id = operator_id;
	}
	public String getOperate_time() {
		return Operate_time;
	}
	public void setOperate_time(String operate_time) {
		Operate_time = operate_time;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public boolean getCheck_state() {
		return Check_state;
	}
	public void setCheck_state(boolean check_state) {
		Check_state = check_state;
	}
	

	
}
