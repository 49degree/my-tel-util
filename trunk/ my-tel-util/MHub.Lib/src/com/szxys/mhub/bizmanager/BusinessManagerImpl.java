package com.szxys.mhub.bizmanager;

import java.util.HashMap;

import android.util.Log;

import com.szxys.mhub.bizinterfaces.IBusinessSubSystem;
import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.subsystem.virtual.VirtualSubSystem;
import com.szxys.mhub.test.ECGSubSystem;

/**
 * 业务系统管理类
 * 
 * @author 黄仕龙
 * 
 */
class BusinessManagerImpl implements IBusinessManager, IBusinessManagerEx {

	class BizSubSystemCallBackImpl implements ISubSystemCallBack {
		private final ISubSystemCallBack subSystemCallBack;

		public BizSubSystemCallBackImpl(ISubSystemCallBack subCallBack) {
			subSystemCallBack = subCallBack;
		}

		@Override
		public long onReceived(int mainCmd, int subCmd, byte[] data, int length) {
			subSystemCallBack.onReceived(mainCmd, subCmd, data, length);
			return 0;
		}

		@Override
		public long onReceived(int mainCmd, int subCmd, Object obj) {
			subSystemCallBack.onReceived(mainCmd, subCmd, obj);
			return 0;
		}
	}

	private final HashMap<BizKey, IBusinessSubSystem> subbizMap = new HashMap<BizKey, IBusinessSubSystem>();
	private static BusinessManagerImpl bizManagerSingle = new BusinessManagerImpl();

	private BusinessManagerImpl() {

	}

	/**
	 * @param userID
	 * @param subSystemID
	 * @param subSysCallBack
	 * @return long
	 */
	@Override
	public long startSubSystem(int userID, int subSystemID,
			ISubSystemCallBack subSysCallBack) {
		Log.i(Platform.PFLOG_INFO, "BusinessManagerImpl::startSubSystem");
		IBusinessSubSystem bizSubSystem = findSubSystem(userID, subSystemID);
		if (null == bizSubSystem) {
			bizSubSystem = CreateBizSubSystem(userID, subSystemID,
					subSysCallBack);
			if (null != bizSubSystem) {
				BizKey bizKey = new BizKey(userID, subSystemID);
				subbizMap.put(bizKey, bizSubSystem);
			} else {
				Log.w("BusinessManagerImpl", "CreateBizSubSystem : " + userID
						+ "," + subSystemID + " error!");
			}
		}
		if (null != bizSubSystem) {
			return bizSubSystem.start();
		}

		return 0;
	}

	/**
	 * @param userID
	 * @param subSystemID
	 * @return long
	 */
	@Override
	public long stopSubSystem(int userID, int subSystemID) {
		IBusinessSubSystem bizSubSystem = findSubSystem(userID, subSystemID);
		if (null != bizSubSystem) {
			return bizSubSystem.stop();
		}
		return 0;
	}

	/**
	 * @param userID
	 * @param subSystemID
	 * @param ctrlID
	 * @param paramIn
	 * @param paramOut
	 * @return long
	 */
	@Override
	public long control(int userID, int subSystemID, int lCtrlID,
			Object[] paramIn, Object[] paramOut) {
		IBusinessSubSystem bizSubSystem = findSubSystem(userID, subSystemID);
		if (null != bizSubSystem) {
			return bizSubSystem.control(lCtrlID, paramIn, paramOut);
		}
		return 0;
	}

	/**
	 * 获取业务系统的单例
	 * 
	 * @return com.szxys.platfrom.pfinterface.IBusinessManager
	 */
	static public BusinessManagerImpl instance() {
		return bizManagerSingle;
	}

	/**
	 * 通过系统性能管理模块，判断是否有足够资源启动新的业务子系统
	 * 
	 * @return boolean true 可以启动； false 资源不足
	 */
	@Override
	public boolean isEnableCreateSubSystem() {
		return true;
	}

	/**
	 * 根据子业务ID值，创建子业务逻辑处理对象
	 * 
	 * @param userID
	 * @param subSystemID
	 * @return IBusinessSubSystem
	 */
	private IBusinessSubSystem CreateBizSubSystem(int userID, int subSystemID,
			ISubSystemCallBack subSysCallBack) {
		IBusinessSubSystem bizSubSystem = null;
		ISubSystemCallBack subSysCall = new BizSubSystemCallBackImpl(
				subSysCallBack);
		switch (subSystemID) {
		case Platform.SUBBIZ_VIRTUAL:
			bizSubSystem = new VirtualSubSystem(VirtualSubSystem.VSS_USERID,
					subSysCall);
			break;
		case Platform.SUBBIZ_ECG:
			bizSubSystem = new ECGSubSystem(userID, subSysCall);
			break;
		default:
			break;
		}

		return bizSubSystem;
	}

	/*
	 * 根据用户ID和业务子系统类型在subbizMap中查找子业务逻辑处理对象
	 */
	private IBusinessSubSystem findSubSystem(int userID, int subSystemID) {
		BizKey bizKey = new BizKey(userID, subSystemID);
		IBusinessSubSystem bizSubSystem = subbizMap.get(bizKey);
		return bizSubSystem;
	}

	@Override
	public void init() {
	}

	@Override
	public void release() {
	}
}
