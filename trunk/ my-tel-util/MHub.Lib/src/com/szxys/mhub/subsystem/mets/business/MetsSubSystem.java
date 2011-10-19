package com.szxys.mhub.subsystem.mets.business;

import android.util.Log;

import com.szxys.mhub.bizinterfaces.BusinessSubSystem;
import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.interfaces.RequestIdentifying;
import com.szxys.mhub.subsystem.mets.bluetooth.BlueToothPFCallBack;
import com.szxys.mhub.subsystem.mets.network.NetworkPFCallBack;
import com.szxys.mhub.subsystem.mets.network.WebService;

public class MetsSubSystem extends BusinessSubSystem {
	private final RequestIdentifying netDataReqIdentifying;// 服务器 XYS-WEB
	private final RequestIdentifying metsValueReqIdentifying;// 尿量采集器 XYS-METS 2
	private final RequestIdentifying metsUfrReqIdentifying;// 尿流率采集器 XYS-UFR 3
	
	public static ISubSystemCallBack metsSSCallBack = null;

	
	public MetsSubSystem(int inUserID, ISubSystemCallBack ssCallBack) {
		super(Platform.SUBBIZ_METS, inUserID, ssCallBack);
		MetsSubSystem.metsSSCallBack = ssCallBack;
		//初始化数据对象
		netDataReqIdentifying = new RequestIdentifying();//服务器 XYS-WEB
		netDataReqIdentifying.userID = inUserID;
		netDataReqIdentifying.subSystemID = this.subSystemID;
		netDataReqIdentifying.devType = Platform.DATATYPE_XYS_NETWORK;

		metsValueReqIdentifying = new RequestIdentifying();// 尿量采集器 XYS-METS 2
		metsValueReqIdentifying.userID = this.userID;
		metsValueReqIdentifying.subSystemID = this.subSystemID;
		metsValueReqIdentifying.devType = Platform.DATATYPE_XYS_METS;
		
		metsUfrReqIdentifying = new RequestIdentifying();// 尿流率采集器 XYS-UFR 3
		metsUfrReqIdentifying.userID = this.userID;
		metsUfrReqIdentifying.subSystemID = this.subSystemID;
		metsUfrReqIdentifying.devType = Platform.DATATYPE_XYS_UFR;
	}

	/**
	 * 启动子业务
	 * 
	 * @return long
	 */
	@Override
	public long start() {
		NetworkPFCallBack netDataReciver = new NetworkPFCallBack();//网络接收器
		BlueToothPFCallBack blueToothReciver = new BlueToothPFCallBack();//蓝牙数据接收器
		long lResult = pfInterface.startDataReceiver(netDataReqIdentifying, netDataReciver);//注册网络接收器
		if (0 == lResult) {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:netDataReqIdentifying:start() Error");
		} else {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:netDataReqIdentifying:start() OK!");
		}
		lResult = pfInterface.startDataReceiver(metsValueReqIdentifying, blueToothReciver);//注册尿量接收器
		
		if (0 == lResult) {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:metsValueReqIdentifying:start() Error");
		} else {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:metsValueReqIdentifying:start() OK!");
		}
		
		lResult = pfInterface.startDataReceiver(metsUfrReqIdentifying, blueToothReciver);//注册尿流率接收器
		
		if (0 == lResult) {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:metsUfrReqIdentifying:start() Error");
		} else {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:metsUfrReqIdentifying:start() OK!");
		}

		
		return 0;
	}

	/**
	 * 停止子业务
	 * @return long
	 */
	@Override
	public long stop() {
		long lResult = pfInterface.stopDataReceiver(netDataReqIdentifying);//注册网络接收器
		if (0 == lResult) {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:netDataReqIdentifying:stop() Error");
		} else {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:netDataReqIdentifying:stop() OK!");
		}
		lResult = pfInterface.stopDataReceiver(metsValueReqIdentifying);//注册尿量接收器
		
		if (0 == lResult) {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:metsValueReqIdentifying:stop() Error");
		} else {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:metsValueReqIdentifying:stop() OK!");
		}
		
		lResult = pfInterface.stopDataReceiver(metsUfrReqIdentifying);//注册尿流率接收器
		
		if (0 == lResult) {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:metsUfrReqIdentifying:stop() Error");
		} else {
			Log.i(Platform.PFLOG_INFO,
					"netDatareqIdentifying MetsSubSystem:metsUfrReqIdentifying:stop() OK!");
		}		
		return 0;
	}

	/**
	 * @param lCtrlID
	 * @param paramIn
	 * @param paramOut
	 * @return long
	 */
	@Override
	public long control(int lCtrlID, Object[] paramIn, Object[] paramOut) {
		switch(lCtrlID){
		case MetsConstant.CTRL_SEND_URINE:
			//if(paramOut!=null){
			//	paramOut[0] = "recive you message is :"+paramIn[0];
				
			//	Log.e("control", (String)paramIn[0]);
				/**
				 * 以下通过pfInterface接口调用平台接口入参主要是数据对象reqIdentifying，
				 * 通过start方法中配置的数据接收对象（如BlueToothPFCallBack、NetworkPFCallBack）可收到返回的数据
				 * 
				 * long lChannel,	使用第几个通道传递数据，该参数传递蓝牙数据时有效
				 * long lMainCmd	数据协议主码
				 * long lSubCmd	数据协议扩展码
				 * byte[] bySendData	需要发送的数据
				 * 
				 */
			
				String sendData = ((WebService)paramIn[0]).strUrinesUnupload();
				if(sendData!=null && sendData.trim().length()>0) {
					
					pfInterface.send(netDataReqIdentifying, Platform.NETDATA_REALTIME, 0, MetsConstant.CTRL_SEND_URINE, MetsConstant.SUB_CMD_TEST, sendData.getBytes(), sendData.getBytes().length);
					//返回数据给UI,这是测试，实际上metsSSCallBack的onReceived方法是BlueToothPFCallBack、NetworkPFCallBack有数据返回的时候调用的
					//MetsSubSystem.metsSSCallBack.onReceived(MetsConstant.MAIN_CMD_TEST, MetsConstant.SUB_CMD_TEST, 
					//		((String)paramOut[0]).getBytes(), ((String)paramOut[0]).getBytes().length);	
				}
				
				break;
		case MetsConstant.CTRL_SEND_WATER:
				if(paramOut!=null){
					paramOut[0] = "recive you message is :"+paramIn[0];
					
					Log.e("control", (String)paramIn[0]);
					/**
					 * 以下通过pfInterface接口调用平台接口入参主要是数据对象reqIdentifying，
					 * 通过start方法中配置的数据接收对象（如BlueToothPFCallBack、NetworkPFCallBack）可收到返回的数据
					 * 
					 * long lChannel,	使用第几个通道传递数据，该参数传递蓝牙数据时有效
					 * long lMainCmd	数据协议主码
					 * long lSubCmd	数据协议扩展码
					 * byte[] bySendData	需要发送的数据
					 * 
					 */
					pfInterface.send(metsValueReqIdentifying, Platform.NETDATA_REALTIME, 0,
							MetsConstant.MAIN_CMD_TEST, MetsConstant.SUB_CMD_TEST, 
							((String)paramOut[0]).getBytes(), ((String)paramOut[0]).getBytes().length);
					//返回数据给UI,这是测试，实际上metsSSCallBack的onReceived方法是BlueToothPFCallBack、NetworkPFCallBack有数据返回的时候调用的
					MetsSubSystem.metsSSCallBack.onReceived(MetsConstant.MAIN_CMD_TEST, MetsConstant.SUB_CMD_TEST, 
							((String)paramOut[0]).getBytes(), ((String)paramOut[0]).getBytes().length);
				
				
				
			}
			break;
		default:
			break;
		
		}
		return 0;
	}

//	/**
//	 * @return long
//	 */
//	@Override
//	public long getUserID() {
//		return 0;
//	}
//
//	/**
//	 * @return long
//	 */
//	@Override
//	public long getSubSystemType() {
//		return 0;
//	}
}
