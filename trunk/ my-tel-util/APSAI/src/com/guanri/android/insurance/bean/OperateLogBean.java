package com.guanri.android.insurance.bean;

/**
 * 
 * @author wuxiang
 *
 */
public class OperateLogBean {
	//日志记录ID
	public int Log_id;
	//操作工号
	public String Operator_id;
	// 操作姓名
	public String Operator_name;
	// 操作员类型
	public String Operator_type;
	// 操作时间
	public String Operate_time;
	// 操作描述
	public String Operate_memo;
	// 是否删除
	public boolean Is_del = false;
	
	public int getLog_id() {
		return Log_id;
	}
	public void setLog_id(int log_id) {
		Log_id = log_id;
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
	public String getOperator_type() {
		return Operator_type;
	}
	public void setOperator_type(String operator_type) {
		Operator_type = operator_type;
	}
	public String getOperate_time() {
		return Operate_time;
	}
	public void setOperate_time(String operate_time) {
		Operate_time = operate_time;
	}
	public String getOperate_memo() {
		return Operate_memo;
	}
	public void setOperate_memo(String operate_memo) {
		Operate_memo = operate_memo;
	}
	public boolean getIs_del() {
		return Is_del;
	}
	public void setIs_del(boolean is_del) {
		Is_del = is_del;
	}
	@Override
	public String toString() {
		return "OperateLogBean [Log_id=" + Log_id + ", Operator_id="
				+ Operator_id + ", Operator_name=" + Operator_name
				+ ", Operator_type=" + Operator_type + ", Operate_time="
				+ Operate_time + ", Operate_memo=" + Operate_memo + ", Is_del="
				+ Is_del + "]";
	}

	
}
