package com.szxys.mhub.subsystem.virtual;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.base.communication.WebHostUtils;
import com.szxys.mhub.base.manager.DeviceManager;
import com.szxys.mhub.bizinterfaces.BusinessSubSystem;
import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;
import com.szxys.mhub.common.Logcat;
import com.szxys.mhub.interfaces.LightUser;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.interfaces.RequestIdentifying;

/**
 * 虚拟子业务：管理平台相关的网络数据处理，设备管理，升级管理，配置管理，互动消息等通用模块
 * 
 * @author 黄仕龙
 * 
 */
public class VirtualSubSystem extends BusinessSubSystem {
	public static final int VSS_USERID = 0;
	public static final int CTRL_GetUserNames = 1;
	public static final int CTRL_GetLightUserS = 1;
	public static final int CTRL_SetRunningUser = 2;

	private HandlerThread msgThread = null;
	private Looper vssLooper = null;
	private VirtualSubSystemHandler vssHandler = null;

	public static final int VSS_INIT = 1;

	private final RequestIdentifying netDatareqIdentifying;
	private final VirtualSubSystemPFCallBack vssPFCallBack;

	public VirtualSubSystem(int inUserID, ISubSystemCallBack ssCallBack) {
		super(Platform.SUBBIZ_VIRTUAL, inUserID, ssCallBack);
		vssPFCallBack = new VirtualSubSystemPFCallBack(this, pfInterface);

		netDatareqIdentifying = new RequestIdentifying();
		netDatareqIdentifying.userID = this.userID;
		netDatareqIdentifying.subSystemID = this.subSystemID;
		netDatareqIdentifying.devType = Platform.DATATYPE_XYS_NETWORK;
	}

	class VirtualSubSystemHandler extends Handler {
		public VirtualSubSystemHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// 根据what字段判断是哪个消息
			switch (msg.what) {
			case VSS_INIT:
				init();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 启动虚拟子业务
	 * 
	 * @return long
	 */
	@Override
	public long start() {
		if (null == msgThread) {
			msgThread = new HandlerThread("MessageDemoThread",
					Process.THREAD_PRIORITY_BACKGROUND);
			msgThread.start();
			// 获取msgThread线程中的looper对象
			vssLooper = msgThread.getLooper();
			// 创建Handler对象，把looper传递过来使得handler、
			// looper和messageQueue三者建立联系
			vssHandler = new VirtualSubSystemHandler(vssLooper);

			// 注册网络数据接收器
			pfInterface.startDataReceiver(netDatareqIdentifying, vssPFCallBack);

			// 异步进行与网络相关的初始化
			Message msg = vssHandler.obtainMessage();
			msg.what = VSS_INIT;
			vssHandler.sendMessage(msg);

			return 0;
		}
		return 1;
	}

	/**
	 * @return long
	 */
	@Override
	public long stop() {
		return pfInterface.stopDataReceiver(netDatareqIdentifying);
	}

	/**
	 * @param lCtrlID
	 * @param paramIn
	 * @param paramOut
	 * @return long
	 */
	@SuppressWarnings("unchecked")
	@Override
	public long control(int lCtrlID, Object[] paramIn, Object[] paramOut) {
		int testUserID = 3;//测试使用的UserID 由调用者传过来
		switch (lCtrlID) {
		case CTRL_GetLightUserS:// 获取所有用户基本信息
			List<LightUser> user = pfInterface.getAllLightUser();
			paramOut[0] = user;
			break;
		case Ctrl_Com_Code.GET_MSG_FROMDB: {
			
			paramOut[0] = MsgInterationResuestData.getInstance()
					.getDoctorBetweenPatienMsg(paramIn[0]);
			MemoryDataFromDB.saveMhubMsg((ArrayList<MhubMessage>) paramOut[0]);
			MsgInterationResuestData.getInstance().sendUpLoadData(pfInterface,testUserID);
			MsgInterationResuestData.getInstance().sendDownLoadRequestData(pfInterface,testUserID);
		}
			break;
		case Ctrl_Com_Code.DEL_MSG_FROMDB: {
			MsgInterationResuestData.getInstance()
					.deleteMsgComplain(paramIn[0]);
			MsgInterationResuestData.getInstance().deleteDoctorMsg(paramIn[1]);
		}

			break;
		case Ctrl_Com_Code.SAVE_COMPLAIN_TODB: {
			MsgInterationResuestData.getInstance().saveMsgComplain(paramIn[0]);
			MsgInterationResuestData.getInstance().sendUpLoadData(pfInterface,testUserID);
		}
			break;
		case Ctrl_Com_Code.SEARCH_DATA:// 获取所有异常信息
			paramOut[0] = new EMSRequestData(pfInterface, subSystemCallBack)
					.searchErrorMessageList();
			DataUtil.saveMsg((ArrayList<ErrorMessageEntity>) paramOut[0]);
			break;
		case Ctrl_Com_Code.DEL_DATA:// 删除异常信息数据
			new EMSRequestData(pfInterface, subSystemCallBack)
					.delErrorMessageById(paramIn[0]);
			break;
		case Ctrl_Com_Code.SAVE_DATA:// 保存异常信息
			new EMSRequestData(pfInterface, subSystemCallBack)
					.saveErrorMessage((ErrorMessageEntity) paramIn[0]);
			break;
		}
		return 0;
	}

	// 系统初始化相关网络数据
	public void init() {
		if (true) {
			byte[] by = TestData.getDeviceBindDataForTest();
			if (true == DeviceManager.updateLatestBindInfo(by, by.length, null)) {
				closeLoadingActivity();
			} else {
				// 保存绑定信息失败
			}
			return;
		}

		// 通过用户管理模块，判断用户信息是否已经初始化
		List<LightUser> allUser = pfInterface.getAllLightUser();
		if (null != allUser) {
			// 已经初始化，关闭加载窗口，显示主界面
			closeLoadingActivity();
		} else {
			// 获取用户相关数据
			this.subSystemCallBack.onReceived(
					MhubApplication.MAINCMD_LOADING_USERINFO, 0, null, 0);
		}

		// 获取最新的绑定信息
		if (0 == WebHostUtils.sendMsgOfGetLatestBindInfo(pfInterface)) {
			// 获取最新绑定信息失败！做出相应提示
			Logcat.e("VirtualSubSystem", "获取最新绑定信息失败！做出相应提示");
			return;
		}

		// 获取子业务相关的服务器地址
		if (0 == WebHostUtils.sendMsgOfGetServerAdress(pfInterface)) {
			// 获取子业务相关的服务器地址失败，做出相应的提示！！
		}

		for (int i = 0; i < allUser.size(); ++i) {
			// 获取用户Token
			WebHostUtils.sendMsgOfGetUserToken(pfInterface, allUser.get(i).ID);
		}

		// 设置监护参数更新定时检查
		// 设置互动消息定时请求
		// 启动升级管理模块
	}

	// 关闭loadingActivity窗口
	public void closeLoadingActivity() {
		this.subSystemCallBack.onReceived(MhubApplication.MAINCMD_LOADING_END,
				0, null, 0);
	}
}
