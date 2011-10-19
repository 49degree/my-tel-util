package com.szxys.mhub.subsystem.mets.bluetooth;


import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;
import com.szxys.mhub.interfaces.IPlatFormCallBack;
import com.szxys.mhub.interfaces.IPlatFormInterface;
import com.szxys.mhub.interfaces.PlatFormInterfaceImpl;
import com.szxys.mhub.interfaces.RequestIdentifying;
import com.szxys.mhub.subsystem.mets.business.MetsSubSystem;

/**
 * 蓝牙接收、发送回调类
 * 
 * 由子业务生成并设置给平台，由平台调用，把数据回调给子业务。与IPlatFormInterface相呼应
 * @author Administrator
 *
 */
public class BlueToothPFCallBack implements IPlatFormCallBack {
	//protected IPlatFormInterface pfInterface = PlatFormInterfaceImpl.instance();
	/**
	 * @param reqIdentifying
	 * @param lErrorCode
	 * @return long
	 */
	public long onStarted(RequestIdentifying reqIdentifying, long lErrorCode){
		return 0;
	}

	/**
	 * @param reqIdentifying
	 * @param lErrorCode
	 * @return long
	 */
	public long onStopped(RequestIdentifying reqIdentifying, long lErrorCode){
		return 0;
	}

	/**
	 * @param reqIdentifying
	 * @param sendIndex
	 * @param lErrorCode
	 * @return long
	 */
	public long onSent(RequestIdentifying reqIdentifying, int sendIndex,
			long lErrorCode){
		ISubSystemCallBack metsSSCallBack = MetsSubSystem.metsSSCallBack;
		if(metsSSCallBack!=null){
			//接收到数据返回UI
			metsSSCallBack.onReceived(1, 1, "send success".getBytes(), "send success".getBytes().length);
		}
		return 0;
	}
	/**
	 * @param lChannel
	 * @param mainCmd
	 * @param subCmd
	 * @param byRecvData
	 * @param length
	 * @return long
	 */
	public long onReceived(int lChannel, int mainCmd, int subCmd,
			byte[] byRecvData, int length){
		ISubSystemCallBack metsSSCallBack = MetsSubSystem.metsSSCallBack;
		if(metsSSCallBack!=null){
			//接收到数据返回UI
			metsSSCallBack.onReceived(mainCmd, subCmd, byRecvData, length);
		}
		return 0;
	}
	
	@Override
	public long onMessage(int errorCode, int mainCmd, int subCmd,
			Object strError) {
		// TODO Auto-generated method stub
		return 0;
	}
}
