package com.guanri.android.insurance.bean;

/**
 * 对账记录表
 * @author wuxiang
 *
 */
public class SaleCheckBean {
	// 批次
	public int Check_id;
	// 操作员ID
	public String Operator_id;
	// 出单量
	public int Order_count;
	// 出单金额
	public int Order_sum;
	// 退单量
	public int Order_back_count;
	// 退单金额
	public int Order_back_sum;
	// 废单量
	public int Order_useless_count;
	// 对账时间
	public String Check_time;
	public int getCheck_id() {
		return Check_id;
	}
	public void setCheck_id(int check_id) {
		Check_id = check_id;
	}
	public String getOperator_id() {
		return Operator_id;
	}
	public void setOperator_id(String operator_id) {
		Operator_id = operator_id;
	}
	public int getOrder_count() {
		return Order_count;
	}
	public void setOrder_count(int order_count) {
		Order_count = order_count;
	}
	public int getOrder_sum() {
		return Order_sum;
	}
	public void setOrder_sum(int order_sum) {
		Order_sum = order_sum;
	}
	public int getOrder_back_count() {
		return Order_back_count;
	}
	public void setOrder_back_count(int order_back_count) {
		Order_back_count = order_back_count;
	}
	public int getOrder_back_sum() {
		return Order_back_sum;
	}
	public void setOrder_back_sum(int order_back_sum) {
		Order_back_sum = order_back_sum;
	}
	public int getOrder_useless_count() {
		return Order_useless_count;
	}
	public void setOrder_useless_count(int order_useless_count) {
		Order_useless_count = order_useless_count;
	}
	public String getCheck_time() {
		return Check_time;
	}
	public void setCheck_time(String check_time) {
		Check_time = check_time;
	}
	
	
	
}
