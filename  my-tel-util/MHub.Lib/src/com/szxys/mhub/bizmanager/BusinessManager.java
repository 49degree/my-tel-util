package com.szxys.mhub.bizmanager;

/**
 * 业务管理操作接口类
 * 
 * @author 黄仕龙
 * 
 */

public class BusinessManager {
	public BusinessManager() {
	}

	/**
	 * @return com.szxys.platfrom.pfinterface.IBusinessManager
	 */
	static public IBusinessManager getIBusinessManager() {
		return BusinessManagerImpl.instance();
	}

	/**
	 * @return com.szxys.platfrom.pfinterface.IBusinessManagerEx
	 */
	static public IBusinessManagerEx getIBusinessManagerEx() {
		return BusinessManagerImpl.instance();
	}
}
