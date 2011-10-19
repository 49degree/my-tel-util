package com.szxys.mhub.bizinterfaces;

import com.szxys.mhub.interfaces.IPlatFormInterface;
import com.szxys.mhub.interfaces.PlatFormInterface;

/**
 * 子业务基类
 * 
 * @author 黄仕龙
 * 
 */

public class BusinessSubSystem implements IBusinessSubSystem {
	protected int subSystemID = 0;
	protected int userID = 0;
	// 反馈数据到子业务界面的回调对象
	protected ISubSystemCallBack subSystemCallBack = null;
	// 平台接口对象
	protected IPlatFormInterface pfInterface = null;

	public BusinessSubSystem(int SSID, int inUserID,
			ISubSystemCallBack ssCallBack) {
		subSystemID = SSID;
		userID = inUserID;
		subSystemCallBack = ssCallBack;
		pfInterface = PlatFormInterface.getIPlatFormInterface();
	}

	@Override
	public long start() {
		// 由派生类实现
		return 0;
	}

	@Override
	public long stop() {
		// 由派生类实现
		return 0;
	}

	@Override
	public long control(int ctrlID, Object[] paramIn, Object[] paramOut) {
		// 由派生类实现
		return 0;
	}

	@Override
	public long getUserID() {
		return userID;
	}

	@Override
	public long getSubSystemType() {
		return subSystemID;
	}

}
