package com.szxys.mhub.ui.virtualui;

import android.os.Message;

import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;
import com.szxys.mhub.ui.main.LoadingActivity;

/**
 * 虚拟子业务逻辑层与UI层数据交互对象
 * 
 * @author 黄仕龙
 * 
 */
public class VirtualSystemUISSCallBack implements ISubSystemCallBack {

	private static VirtualSystemUISSCallBack vsUISSCBsingle = new VirtualSystemUISSCallBack();
	// 保存虚拟子业务中当前处于使用状态的UI的数据回调对象
	private ISubSystemCallBack subUICallBack;

	private VirtualSystemUISSCallBack() {
		subUICallBack = null;
	}

	static public VirtualSystemUISSCallBack getInstance() {
		return vsUISSCBsingle;
	}

	public void setCurrentSSCallBace(ISubSystemCallBack SSCB) {
		subUICallBack = SSCB;
	}

	/**
	 * @param lMainCmd
	 * @param lSubCmd
	 * @param data
	 * @return long
	 */
	@Override
	public long onReceived(int mainCmd, int subCmd, byte[] data, int length) {
		switch (mainCmd) {
		case MhubApplication.MAINCMD_LOADING_END:
			onLoadindEnd();
			break;
		}

		// 回调数据到虚拟子业务中具体的某个特定UI
		if (null != subUICallBack) {
			subUICallBack.onReceived(mainCmd, subCmd, data, length);
		}
		return 0;
	}

	// 加载数据结束，启动主界面
	private void onLoadindEnd() {
		Message msg = Message.obtain();
		msg.what = LoadingActivity.UPATE_STATUS;
		LoadingActivity loading = (LoadingActivity) MhubApplication.loadingActivity;
		loading.mHandler.sendMessage(msg);
	}

	@Override
	public long onReceived(int mainCmd, int subCmd, Object obj) {
		// TODO Auto-generated method stub
		return 0;
	}
}
