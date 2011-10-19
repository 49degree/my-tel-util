package com.guanri.android.insurance.bean;

/**
 * 方案定义
 * @author Administrator
 *
 */
public class InsuPlanRecordBean 
{
   
   /**
   @roseuid 4DF8330F003E
    */
   public InsuPlanRecordBean() 
   {
    
   }
   
// 业务代码
	public String CardCode;
	// 业务序号
	public String Planno;
	// 名称
	public String Name;
	// 状态 (激活/禁止)
	public boolean useable;
	// 模板是否文件下载
	public boolean File_downloaded;
	// 操作员ID
	public String Operator_id;
	// 操作时间
	public String Operate_time;
	// 编辑模板文件名
	public String InsuEdit_name;
	// 打印模板文件名
	public String InsuPrt_name;
	// 完整的业务方案名称
	public String Insu_name; 
	
	
	public String getCardCode() {
		return CardCode;
	}
	public void setCardCode(String cardCode) {
		CardCode = cardCode;
	}
	public String getPlanno() {
		return Planno;
	}
	public void setPlanno(String planno) {
		Planno = planno;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public boolean getUseable() {
		return useable;
	}
	public void setUseable(boolean useable) {
		this.useable = useable;
	}
	public boolean getFile_downloaded() {
		return File_downloaded;
	}
	public void setFile_downloaded(boolean file_downloaded) {
		File_downloaded = file_downloaded;
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
	public String getInsuEdit_name() {
		return InsuEdit_name;
	}
	public void setInsuEdit_name(String insuEdit_name) {
		InsuEdit_name = insuEdit_name;
	}
	public String getInsuPrt_name() {
		return InsuPrt_name;
	}
	public void setInsuPrt_name(String insuPrt_name) {
		InsuPrt_name = insuPrt_name;
	}
	public String getInsu_name() {
		return Insu_name;
	}
	public void setInsu_name(String insu_name) {
		Insu_name = insu_name;
	}
	
	
}
