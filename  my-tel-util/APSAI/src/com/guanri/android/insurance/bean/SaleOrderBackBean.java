package com.guanri.android.insurance.bean;

/**
 * 4.1.6	退单记录
 * @author wuxiang
 *
 */
public class SaleOrderBackBean {
	// 单证号
	public String Paper_number;
	// 保单号
	public String Insu_no;
	// 投保单号 
	public String ProposalForm_No;
	// 退单金额
	public int Back_sum;
	// 操作工号
	public String Operator_id;
	// 操作时间
	public String Operate_time;
	// 备注
	public String memo;
	// 对账标志
	public boolean Check_state;
	
	public String getProposalForm_No() {
		return ProposalForm_No;
	}
	public void setProposalForm_No(String proposalForm_No) {
		ProposalForm_No = proposalForm_No;
	}
	//批次号
	public int Check_id;
	 
	public int getCheck_id() {
		return Check_id;
	}
	public void setCheck_id(int check_id) {
		Check_id = check_id;
	}
	public String getPaper_number() {
		return Paper_number;
	}
	public void setPaper_number(String paper_number) {
		Paper_number = paper_number;
	}
	public String getInsu_no() {
		return Insu_no;
	}
	public void setInsu_no(String insu_no) {
		Insu_no = insu_no;
	}
	public int getBack_sum() {
		return Back_sum;
	}
	public void setBack_sum(int back_sum) {
		Back_sum = back_sum;
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
