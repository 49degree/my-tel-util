package com.szxys.mhub.subsystem.virtual;

import com.szxys.mhub.base.manager.Consts;
import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;

/**
 * 监护参数对服务器端返回的数据进行解析
 * 
 * @author 张丹
 * 
 */
public class MTPSSubSystemCallBack implements ISubSystemCallBack {
	private MTPSDB db = null;

	private ISubSystemCallBack ssCallBack = null;

	public MTPSSubSystemCallBack(ISubSystemCallBack sscb) {
		db = new MTPSDB();
		ssCallBack = sscb;
	}

	@Override
	public long onReceived(int mainCmd, int subCmd, byte[] data, int length) {
		if (mainCmd == Consts.CMD_WEB_GET_MONITORING_PARAMETERS) {
			if (data.length > 0) {
				int i = 0;
				int j = 0;
				int apID[] = new int[length / 4];
				while (i < data.length) {
					byte[] temp = new byte[4];
					System.arraycopy(data, i, temp, 0, 4);
					apID[j] = byteToInt2(temp);
					db.UpdateIsChangeByAppID(apID[j]);
					// 通知虚拟子业务
					// ssCallBack.onReceived(mainCmd, subCmd, data,length)
					j++;
					i = i + 4;
				}
			}
		}
		return 0;
	}

	public static int byteToInt2(byte[] b) {

		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 3; i >=0; i--) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
		return n;
	}

	@Override
	public long onReceived(int mainCmd, int subCmd, Object obj) {
		return 0;
	}
}
