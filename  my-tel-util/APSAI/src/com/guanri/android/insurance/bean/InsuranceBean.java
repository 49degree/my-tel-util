package com.guanri.android.insurance.bean;

/**
 * 保险类型定义
 * @author 杨雪平
 *
 */
public class InsuranceBean {
	private String insuPlanFile = null;//保险方案文件名称
	private String insuPrintFile = null;//打印方案文件名称
	private String insuViewPlanFile = null;//界面方案文件名称
	
	private InsuPlanBean insuPlanBean = null;//具体业务方案对象
	private InsuPrintBean insuPrintBean = null;//具体打印方案对象
	private InsuViewPlanBean insuViewPlanBean = null;//具体视图方案对象
	
	public String getInsuPlanFile() {
		return insuPlanFile;
	}
	public void setInsuPlanFile(String insuPlanFile) {
		this.insuPlanFile = insuPlanFile;
	}
	public String getInsuPrintFile() {
		return insuPrintFile;
	}
	public void setInsuPrintFile(String insuPrintFile) {
		this.insuPrintFile = insuPrintFile;
	}
	public String getInsuViewPlanFile() {
		return insuViewPlanFile;
	}
	public void setInsuViewPlanFile(String insuViewPlanFile) {
		this.insuViewPlanFile = insuViewPlanFile;
	}
	public InsuPlanBean getInsuPlanBean() {
		return insuPlanBean;
	}
	public void setInsuPlanBean(InsuPlanBean insuPlanBean) {
		this.insuPlanBean = insuPlanBean;
	}
	public InsuPrintBean getInsuPrintBean() {
		return insuPrintBean;
	}
	public void setInsuPrintBean(InsuPrintBean insuPrintBean) {
		this.insuPrintBean = insuPrintBean;
	}
	public InsuViewPlanBean getInsuViewPlanBean() {
		return insuViewPlanBean;
	}
	public void setInsuViewPlanBean(InsuViewPlanBean insuViewPlanBean) {
		this.insuViewPlanBean = insuViewPlanBean;
	}
}

