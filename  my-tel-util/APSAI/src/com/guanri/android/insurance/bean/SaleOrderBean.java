package com.guanri.android.insurance.bean;

/**
 * 
 * @author wuxiang
 *4.1.4	保单销售记录
 */
public class SaleOrderBean {
	// 单证号
	public String BillNo;

	// 保险起期
	public String Insu_begin_date;
	// 保险起期时间
	public String Insu_begin_time;
	// 保险止期
	public String Insu_end_date;
	// 保险止期时间
	public String Insu_end_time;
	// 投保人
	public String Plyh_name;
	// 投保人性别
	public int Plyh_sex;
	// 投保人生日
	public String Plyh_Brithday;
	// 投保人证件类型
	public int Plyh_Card_type;
	// 投保人证件编号
	public String Plyh_Card_no;
	// 被保人姓名
	public String Insured_name;
	// 被保人性别
	public int Insured_Sex;
	// 被保人生日
	public String Insured_brithday;
	// 被保人证件类型
	public int Insured_card_type;
	// 被保人证件号码
	public String Insured_card_No;
	// 被保人与投保人关系
	public int Insured_relation;
	// 受益人
	public String Beneficlary_name;

	// 保险金额(赔偿多少)
	public int Insured_money;
	// 保险费(交的多少)
	public int Insured_amount;
	// 学校
	public String School;
	// 班级
	public String SchoolClass;
	// 22.	航班号/客运班次
	public String Trainnumber;
	// 23.	客票/门票号码，卡号等
	public String Trainticket;
	// 保险期间
	public String Insu_period;
	// 保险期间值
	public String Insu_values;
	//26.	投保人职业序号(暂时未使用)
	public String Plyh_WorkNo;
	//27.	被保人职业序号(暂时未使用)
	public String Insured_WorkNo;
	// 联系电话
	public String phone;
	// 备注字段, 各类不同总类的保险所需字段相差较大,用于保存所以其他字段
	public String remark;
	// 操作工号
	public String Operator_id;
	// 操作时间
	public String Operate_time;
	// 保单号  服务器返回
	public String Insu_No;
	// 投保单号 服务器返回
	public String ProposalForm_No;

	// 是否实时保单
	public boolean Is_realtime;
	// 是否已上传服务器
	public boolean Is_submit;
	// 打印状态
	public String Print_state;
	// 销售时间
	public String sale_time;
	// 对账状态
	public boolean Check_state;
	// 销售状态
	public boolean Sale_state; 
	//批次号
	public int Check_id;
	// 保单名称
	public String Insu_name;
	// 业务方案代码 
	public String CardCode;
	// 业务方案序号
	public String PlanNo;
	// 组合序号
	public int InsuAssembled;
	
	
	public String getCardCode() {
		return CardCode;
	}
	public void setCardCode(String cardCode) {
		CardCode = cardCode;
	}
	public String getPlanNo() {
		return PlanNo;
	}
	public void setPlanNo(String planNo) {
		PlanNo = planNo;
	}
	public int getInsuAssembled() {
		return InsuAssembled;
	}
	public void setInsuAssembled(int insuAssembled) {
		InsuAssembled = insuAssembled;
	}

	public String getProposalForm_No() {
		return ProposalForm_No;
	}
	public void setProposalForm_No(String proposalForm_No) {
		ProposalForm_No = proposalForm_No;
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
	public String getInsu_No() {
		return Insu_No;
	}
	public void setInsu_No(String insu_No) {
		Insu_No = insu_No;
	}
	public String getInsu_name() {
		return Insu_name;
	}
	public void setInsu_name(String insu_name) {
		Insu_name = insu_name;
	}

	public String getInsu_begin_date() {
		return Insu_begin_date;
	}
	public void setInsu_begin_date(String insu_begin_date) {
		Insu_begin_date = insu_begin_date;
	}
	public String getInsu_begin_time() {
		return Insu_begin_time;
	}
	public void setInsu_begin_time(String insu_begin_time) {
		Insu_begin_time = insu_begin_time;
	}
	public String getInsu_end_date() {
		return Insu_end_date;
	}
	public void setInsu_end_date(String insu_end_date) {
		Insu_end_date = insu_end_date;
	}
	public String getInsu_end_time() {
		return Insu_end_time;
	}
	public void setInsu_end_time(String insu_end_time) {
		Insu_end_time = insu_end_time;
	}
	public String getPlyh_name() {
		return Plyh_name;
	}
	public void setPlyh_name(String plyh_name) {
		Plyh_name = plyh_name;
	}
	public int getPlyh_sex() {
		return Plyh_sex;
	}
	public void setPlyh_sex(int plyh_sex) {
		Plyh_sex = plyh_sex;
	}
	public String getPlyh_Brithday() {
		return Plyh_Brithday;
	}
	public void setPlyh_Brithday(String plyh_Brithday) {
		Plyh_Brithday = plyh_Brithday;
	}

	public String getPlyh_Card_no() {
		return Plyh_Card_no;
	}
	public void setPlyh_Card_no(String plyh_Card_no) {
		Plyh_Card_no = plyh_Card_no;
	}
	public String getInsured_name() {
		return Insured_name;
	}
	public void setInsured_name(String insured_name) {
		Insured_name = insured_name;
	}
	public int getInsured_Sex() {
		return Insured_Sex;
	}
	public void setInsured_Sex(int insured_Sex) {
		Insured_Sex = insured_Sex;
	}
	public String getInsured_brithday() {
		return Insured_brithday;
	}
	public void setInsured_brithday(String insured_brithday) {
		Insured_brithday = insured_brithday;
	}

	public String getInsured_card_No() {
		return Insured_card_No;
	}
	public void setInsured_card_No(String insured_card_No) {
		Insured_card_No = insured_card_No;
	}

	public String getBillNo() {
		return BillNo;
	}
	public void setBillNo(String billNo) {
		BillNo = billNo;
	}
	public int getPlyh_Card_type() {
		return Plyh_Card_type;
	}
	public void setPlyh_Card_type(int plyh_Card_type) {
		Plyh_Card_type = plyh_Card_type;
	}
	public int getInsured_card_type() {
		return Insured_card_type;
	}
	public void setInsured_card_type(int insured_card_type) {
		Insured_card_type = insured_card_type;
	}
	public int getInsured_relation() {
		return Insured_relation;
	}
	public void setInsured_relation(int insured_relation) {
		Insured_relation = insured_relation;
	}
	public String getSchool() {
		return School;
	}
	public void setSchool(String school) {
		School = school;
	}
	public String getSchoolClass() {
		return SchoolClass;
	}
	public void setSchoolClass(String schoolClass) {
		SchoolClass = schoolClass;
	}
	public String getTrainnumber() {
		return Trainnumber;
	}
	public void setTrainnumber(String trainnumber) {
		Trainnumber = trainnumber;
	}
	public String getTrainticket() {
		return Trainticket;
	}
	public void setTrainticket(String trainticket) {
		Trainticket = trainticket;
	}
	public int getCheck_id() {
		return Check_id;
	}
	public void setCheck_id(int check_id) {
		Check_id = check_id;
	}
	public String getBeneficlary_name() {
		return Beneficlary_name;
	}
	public void setBeneficlary_name(String beneficlary_name) {
		Beneficlary_name = beneficlary_name;
	}
	public int getInsured_money() {
		return Insured_money;
	}
	public void setInsured_money(int insured_money) {
		Insured_money = insured_money;
	}
	public int getInsured_amount() {
		return Insured_amount;
	}
	public void setInsured_amount(int insured_amount) {
		Insured_amount = insured_amount;
	}
	public String getInsu_period() {
		return Insu_period;
	}
	public void setInsu_period(String insu_period) {
		Insu_period = insu_period;
	}
	public String getInsu_values() {
		return Insu_values;
	}
	public void setInsu_values(String insu_values) {
		Insu_values = insu_values;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public boolean getIs_realtime() {
		return Is_realtime;
	}
	public void setIs_realtime(boolean is_realtime) {
		Is_realtime = is_realtime;
	}
	public boolean getIs_submit() {
		return Is_submit;
	}
	public void setIs_submit(boolean is_submit) {
		Is_submit = is_submit;
	}
	public String getPrint_state() {
		return Print_state;
	}
	public void setPrint_state(String print_state) {
		Print_state = print_state;
	}
	public String getSale_time() {
		return sale_time;
	}
	public void setSale_time(String sale_time) {
		this.sale_time = sale_time;
	}
	public boolean getCheck_state() {
		return Check_state;
	}
	public void setCheck_state(boolean check_state) {
		Check_state = check_state;
	}
	
	public boolean getSale_state() {
		return Sale_state;
	}
	public void setSale_state(boolean sale_state) {
		Sale_state = sale_state;
	}

	public String getPlyh_WorkNo() {
		return Plyh_WorkNo;
	}
	public void setPlyh_WorkNo(String plyh_WorkNo) {
		Plyh_WorkNo = plyh_WorkNo;
	}
	public String getInsured_WorkNo() {
		return Insured_WorkNo;
	}
	public void setInsured_WorkNo(String insured_WorkNo) {
		Insured_WorkNo = insured_WorkNo;
	}
	
}
