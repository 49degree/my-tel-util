package com.szxys.mhub.subsystem.virtual;

import java.util.HashMap;

import com.szxys.mhub.base.communication.WebHostUtils;
import com.szxys.mhub.base.manager.Consts;
import com.szxys.mhub.base.manager.DeviceManager;
import com.szxys.mhub.bizmanager.BizKey;
import com.szxys.mhub.common.Logcat;
import com.szxys.mhub.interfaces.IPlatFormCallBack;
import com.szxys.mhub.interfaces.IPlatFormInterface;
import com.szxys.mhub.interfaces.RequestIdentifying;

/**
 * 虚拟子业务平台回调实现，所有平台相关的网络数据都通过该类来处理、分发
 * 
 * @author 黄仕龙
 * 
 */
public class VirtualSubSystemPFCallBack implements IPlatFormCallBack {

	private HashMap<BizKey, IPlatFormCallBack> subSystemPFCallBackMap;
	private IPlatFormInterface pfInterface = null;
	private VirtualSubSystem owner = null;

	public VirtualSubSystemPFCallBack(VirtualSubSystem vss,
			IPlatFormInterface pf) {
		owner = vss;
		pfInterface = pf;
	}

	/**
	 * 注册子业务消息相应回调函数
	 * 
	 * @param userID
	 *            用户ID
	 * @param subSystemID
	 *            业务类型
	 * @param dataReciver
	 *            子业务相关消息接收器
	 */
	public void subSystemregisterDataReciver(int userID, int subSystemID,
			IPlatFormCallBack dataReciver) {
		BizKey bizKey = new BizKey(userID, subSystemID);
		subSystemPFCallBackMap.put(bizKey, dataReciver);
	}

	/*
	 * 根据用户ID和业务子系统类型在subSystemPFCallBackMap中查找子业务数据回调对象
	 */
	private IPlatFormCallBack findFPCallBack(int userID, int subSystemID) {
		BizKey bizKey = new BizKey(userID, subSystemID);
		IPlatFormCallBack pfcb = subSystemPFCallBackMap.get(bizKey);
		return pfcb;
	}

	@Override
	public long onStarted(RequestIdentifying reqIdentifying, long lErrorCode) {
		return 0;
	}

	@Override
	public long onStopped(RequestIdentifying reqIdentifying, long lErrorCode) {
		return 0;
	}

	@Override
	public long onSent(RequestIdentifying reqIdentifying, int sendIndex,
			long lErrorCode) {
		return 0;
	}

	@Override
	public long onReceived(int lChannel, int mainCmd, int subCmd,
			byte[] byRecvData, int length) {
		Logcat.i("VirtualSubSystemPFCallBack", "onReceived, mainCmd=" + mainCmd);
		switch (mainCmd) {
		// 保存获取的服务器地址
		case Consts.CMD_WEB_GET_SERVER_ADRESS:
			WebHostUtils.saveServerAdress(pfInterface, byRecvData, length);
			break;
		// 保存最新绑定信息
		case Consts.CMD_WEB_GET_BIND_INFO:
			if (true == DeviceManager.updateLatestBindInfo(byRecvData, length,
					null)) {
				owner.closeLoadingActivity();
			} else {
				// 保存绑定信息失败
			}
			break;
		// 保存用户 Token
		case Consts.CMD_WEB_GET_USER_TOKEN:
			WebHostUtils.saveUserToken(pfInterface, byRecvData, length);
			break;
		case Consts.CMD_WEB_GET_DOCTOR_MSG:
			// hsl debug
			// MsgInterationResuestData.getInstance().saveDoctorMsg(byRecvData);
		case Consts.CMD_WEB_SEND_USER_MSG:
			String strErroeMsg = new String(byRecvData);
			int responseCode = byteToInt2(byRecvData);

		}
		/*
		 * switch (mainCmd) { case Consts.CMD_GET_BIND_INFO:// 获取终端绑定信息 100 case
		 * Consts.CMD_GET_USER_INFO:// 获取用户信息 101 case
		 * Consts.CMD_GET_USER_TOKEN:// 获取用户 Token 102 if (null !=
		 * devManagerSSCallBack) { devManagerSSCallBack.onReceived(mainCmd,
		 * subCmd, byRecvData, length); } break; case
		 * Consts.CMD_GET_UPDATE_INFO: // 获取升级信息 200 if (null !=
		 * updateSSCallBack) { updateSSCallBack .onReceived(mainCmd, subCmd,
		 * byRecvData, length); } break; case Consts.CMD_GET_DOCTOR_MSG: //
		 * 下载最新互动信息 301 // 下载最新互动信息成功的通知指令 302 case
		 * Consts.CMD_GET_DOCTOR_MSG_SUCCEEDED: if (null != msgSSCallBack) {
		 * msgSSCallBack.onReceived(mainCmd, subCmd, byRecvData, length); } //
		 * 获取监护参数信息 400 case Consts.CMD_GET_MONITORING_PARAMETERS: if (null !=
		 * mtpsSSCallBack) { mtpsSSCallBack.onReceived(mainCmd, subCmd,
		 * byRecvData, length); } break; case Consts.CMD_GET_SERVER_ADRESS://
		 * 获取服务器地址、端口号信息 500 if (null != devManagerSSCallBack) {
		 * devManagerSSCallBack.onReceived(mainCmd, subCmd, byRecvData, length);
		 * } break; }
		 */
		return 0;
	}

	public static int byteToInt2(byte[] b) {

		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 0; i < 4; i++) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
		return n;
	}

	@Override
	public long onMessage(int msgCode, int mainCmd, int subCmd, Object obj) {
		// TODO Auto-generated method stub
		return 0;
	}

}
