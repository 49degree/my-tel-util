package com.szxys.mhub.bizmanager;

/**
 * 业务系统管理层暴露给上层的接口 （主要由平台主界面层调用）
 * 
 * @author 黄仕龙
 */
public interface IBusinessManagerEx {

	/**
	 * 判断当前系统是否允许启动子业务
	 * 
	 * @return boolean
	 */
	public boolean isEnableCreateSubSystem();

	/**
	 * 平台系统初始化操作，在业务系统管理层实现
	 */
	public void init();

	/**
	 * 平台系统销毁操作，在业务系统管理层实现
	 */
	public void release();
}
