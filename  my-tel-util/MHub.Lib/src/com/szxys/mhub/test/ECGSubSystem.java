package com.szxys.mhub.test;

import java.nio.ByteBuffer;

import android.util.Log;

import com.szxys.mhub.bizinterfaces.BusinessSubSystem;
import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;
import com.szxys.mhub.interfaces.IPlatFormCallBack;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.interfaces.RequestIdentifying;

/**
 * 测试用的心电业务逻辑处理模块
 * 
 * @author 黄仕龙
 */
public class ECGSubSystem extends BusinessSubSystem {

	private final int iNetIndex = 1;
	private final RequestIdentifying netDatareqIdentifying;
	private final RequestIdentifying ecgDatareqIdentifying;

	public ECGSubSystem(int inUserID, ISubSystemCallBack ssCallBack) {

		super(Platform.SUBBIZ_ECG, inUserID, ssCallBack);

		netDatareqIdentifying = new RequestIdentifying();
		netDatareqIdentifying.userID = this.userID;
		netDatareqIdentifying.subSystemID = this.subSystemID;
		netDatareqIdentifying.devType = Platform.DATATYPE_XYS_NETWORK;

		ecgDatareqIdentifying = new RequestIdentifying();
		ecgDatareqIdentifying.userID = this.userID;
		ecgDatareqIdentifying.subSystemID = this.subSystemID;
		ecgDatareqIdentifying.devType = Platform.DATATYPE_XYS_WRM;
	}

	@Override
	public long start() {
		Log.i(Platform.PFLOG_INFO, "ECGSubSystem::start()");
		ECGNetPfCallBack netDataReciver = new ECGNetPfCallBack();
		ECGWfmPfCallBack wfmReciver = new ECGWfmPfCallBack();

		// long lResult = pfInterface.startDataReceiver(netDatareqIdentifying,
		// netDataReciver);
		// if (0 == lResult) {
		// Log.i(Platform.PFLOG_INFO,
		// "netDatareqIdentifying ECGSubSystem::start() Error");
		// } else {
		// Log.i(Platform.PFLOG_INFO,
		// "netDatareqIdentifying ECGSubSystem::start() OK!");
		// }

		long lResult = pfInterface.startDataReceiver(ecgDatareqIdentifying,
				wfmReciver);
		if (0 == lResult) {
			Log.i(Platform.PFLOG_INFO,
					"ecgDatareqIdentifying ECGSubSystem::start() Error");
		} else {
			Log.i(Platform.PFLOG_INFO,
					"ecgDatareqIdentifying ECGSubSystem::start() OK!");
		}
		return lResult;
	}

	@Override
	public long stop() {
		return 0;
	}

	@Override
	public long control(int ctrlID, Object[] paramIn, Object[] paramOut) {
		return 0;
	}

	/**
	 * 网络数据回调处理器
	 * 
	 * @author 黄仕龙
	 * 
	 */
	class ECGNetPfCallBack implements IPlatFormCallBack {

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
			ECGSubSystem.this.subSystemCallBack.onReceived(mainCmd, subCmd,
					byRecvData, length);
			return 0;
		}

		@Override
		public long onMessage(int msgCode, int mainCmd, int subCmd, Object obj) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	/**
	 * 心电蓝牙数据回调处理器
	 * 
	 * @author 黄仕龙
	 * 
	 */
	class ECGWfmPfCallBack implements IPlatFormCallBack {

		@Override
		public long onStarted(RequestIdentifying reqIdentifying, long lErrorCode) {
			Log.i(Platform.PFLOG_INFO, "ECGWfmPfCallBack::onStarted");

			sendConfigReq();

			return 0;
		}

		@Override
		public long onStopped(RequestIdentifying reqIdentifying, long lErrorCode) {
			Log.i(Platform.PFLOG_INFO, "ECGWfmPfCallBack::onStopped");
			return 0;
		}

		@Override
		public long onSent(RequestIdentifying reqIdentifying, int sendIndex,
				long lErrorCode) {
			Log.i(Platform.PFLOG_INFO, "ECGWfmPfCallBack::onSent result = "
					+ lErrorCode);
			return 0;
		}

		@Override
		public long onReceived(int lChannel, int mainCmd, int subCmd,
				byte[] byRecvData, int length) {
			Log.i(Platform.PFLOG_INFO,
					"ECGWfmPfCallBack::onReceived, length = " + length);
			try {
				ECGSubSystem.this.subSystemCallBack.onReceived(mainCmd, subCmd,
						byRecvData, length);

				if ((0x02 == byRecvData[0]) && (0x01 == byRecvData[1])) {
					sendGDIPReq();// 心电GDIP请求包

					String strData = byteToHexString(byRecvData);
					Log.i(Platform.PFLOG_INFO, strData);
				} else if ((0x04 == byRecvData[0]) && (0x01 == byRecvData[1])) {
					// 心电数据start开始请求包
					byte[] byID = new byte[4];
					for (int i = 0; i < 4; ++i) {
						byID[i] = byRecvData[2 + i];
					}
					sendDataStartReq(byID);

					String strData = byteToHexString(byRecvData);
					Log.i(Platform.PFLOG_INFO, strData);
				}

			} catch (Exception e) {
				Log.e(Platform.PFLOG_ERROR, "ECGWfmPfCallBack::onReceived()");
			}

			// ECGSubSystem.this.pfInterface.send(netDatareqIdentifying,
			// iNetIndex++, 1, 99, 1, byRecvData, 10);
			return 0;
		}

		/**
		 * 配置命令包
		 */
		private void sendConfigReq() {
			final int wfmCfgLength = 8;
			// 配置信息
			byte[] byWFMCfg = new byte[wfmCfgLength];
			byWFMCfg[0] = 0x01;
			long lSysTm = System.currentTimeMillis() / 1000;
			for (int i = 1; i < 5; ++i) {
				byWFMCfg[i] = (byte) ((lSysTm >> (8 * (i - 1))) & 0x000000ff);
			}
			byWFMCfg[5] = 10;
			byWFMCfg[6] = 0;
			byWFMCfg[7] = 0;

			byte[] bytes = new byte[8];

			ByteBuffer byteBuffer = ByteBuffer.allocate(8);
			byteBuffer.putLong(lSysTm);

			byte[] bySrc = byteBuffer.array();
			int length = bySrc.length;
			for (int i = 0; i < length; i++) {
				bytes[length - i - 1] = bySrc[i];
			}

			ECGSubSystem.this.pfInterface.send(ecgDatareqIdentifying, 1, 1, 1,
					1, byWFMCfg, byWFMCfg.length);
		}

		/**
		 * 心电GDIP请求
		 */
		private void sendGDIPReq() {
			final int wfmRequestLength = 2;
			byte[] byWFMReq = new byte[wfmRequestLength];
			byWFMReq[0] = 0x03;
			byWFMReq[1] = 0x01;
			ECGSubSystem.this.pfInterface.send(ecgDatareqIdentifying, 1, 1, 1,
					1, byWFMReq, byWFMReq.length);
			Log.e(Platform.PFLOG_INFO,
					"ECGWfmPfCallBack::onReceived() send WFM Request package!");
		}

		/**
		 * 心电数据start开始请求包
		 */
		private void sendDataStartReq(byte[] byID) {
			final int wfmStartRequestLength = 5;
			byte[] byWFMDataStartReq = new byte[wfmStartRequestLength];
			byWFMDataStartReq[0] = 0x05;
			for (int i = 1; i < 5; ++i) {
				byWFMDataStartReq[i] = byID[i - 1];
			}
			ECGSubSystem.this.pfInterface.send(ecgDatareqIdentifying, 1, 1, 1,
					1, byWFMDataStartReq, byWFMDataStartReq.length);
			Log.e(Platform.PFLOG_INFO,
					"ECGWfmPfCallBack::onReceived() send WFM data Start Request package!");
		}

		@Override
		public long onMessage(int errorCode, int mainCmd, int subCmd, Object obj) {
			return 0;
		}
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 * 
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static String byteToHexString(byte[] b) {
		StringBuffer returnValue = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
			returnValue.append(hex.toUpperCase() + " ");
		}

		return "[" + returnValue.toString() + "]";
	}
}
