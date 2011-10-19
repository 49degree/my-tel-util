package com.szxys.mhub.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.database.Cursor;

import com.szxys.mhub.base.communication.BlueToothUtils;
import com.szxys.mhub.base.communication.ICommunication;
import com.szxys.mhub.base.communication.WebHostUtils;
import com.szxys.mhub.base.manager.Consts;
import com.szxys.mhub.base.manager.UserManager;
import com.szxys.mhub.subsystem.virtual.VirtualSubSystemPFCallBack;

/**
 * 平台接口的实现类,接口参数含义详见接口定义文件：IPlatFormInterface.java
 * 
 * @author 黄仕龙
 */
public class PlatFormInterfaceImpl implements IPlatFormInterface {
	private static PlatFormInterfaceImpl pfInterfaceImpl = new PlatFormInterfaceImpl();

	// 虚拟子业务接收其他子业务平台消息接收器的回调函数
	private VirtualSubSystemPFCallBack vssSSCallBack = null;

	static public IPlatFormInterface instance() {
		return pfInterfaceImpl;
	}

	private PlatFormInterfaceImpl() {

	}

	@Override
	public List<LightUser> getAllLightUser() {
		Map<Integer, LightUser> allUser = UserManager.getAllLightUsers();
		List<LightUser> user = null;
		if (allUser.size() > 0) {
			user = new ArrayList<LightUser>(allUser.values());
		}
		return user;
	}

	@Override
	public User getUser(int userId) {
		return UserManager.getUser(userId);
	}

	@Override
	public DeviceConfig[] getDeviceConfig(int userID, int subSystemID) {
		// 网络服务器
		DeviceConfig[] webDev = WebHostUtils.getWebHostInstance()
				.getDeviceConfig(userID, subSystemID);
		// 蓝牙设备
		DeviceConfig[] bthDev = BlueToothUtils.getBlueToothInstance()
				.getDeviceConfig(userID, subSystemID);

		int length = ((webDev != null) ? webDev.length : 0)
				+ ((bthDev != null) ? bthDev.length : 0);
		DeviceConfig[] result = null;
		if (length > 0) {
			result = new DeviceConfig[length];
			int i = 0;
			if (null != webDev) {
				for (int j = 0; j < webDev.length; ++j) {
					result[i++] = webDev[j];
				}
			}
			if (null != bthDev) {
				for (int j = 0; j < bthDev.length; ++j) {
					result[i++] = bthDev[j];
				}
			}
		}

		return result;
	}

	@Override
	public Cursor getDBSharedData(int subSystemID, int lDataIdentifying,
			List<String> listString) {
		return null;
	}

	@Override
	public String getFileSavePath(int subSystemID) {
		return null;
	}

	@Override
	public DeviceConfig[] getRegisterDeviceInfo(int userID, int subSystemID) {
		// 网络服务器
		DeviceConfig[] webDev = WebHostUtils.getWebHostInstance()
				.getRegisterDeviceConfig(userID, subSystemID);
		// 蓝牙设备
		DeviceConfig[] bthDev = BlueToothUtils.getBlueToothInstance()
				.getRegisterDeviceConfig(userID, subSystemID);

		int length = ((webDev != null) ? webDev.length : 0)
				+ ((bthDev != null) ? bthDev.length : 0);
		DeviceConfig[] result = null;
		if (length > 0) {
			result = new DeviceConfig[length];
			int i = 0;
			if (null != webDev) {
				for (int j = 0; j < webDev.length; ++j) {
					result[i++] = webDev[j];
				}
			}
			if (null != bthDev) {
				for (int j = 0; j < bthDev.length; ++j) {
					result[i++] = bthDev[j];
				}
			}
		}
		return result;
	}

	@Override
	public long registerPlatformDataReciver(int userID, int subSystemID,
			IPlatFormCallBack dataReciver) {
		// 目前通过把接收器转发到虚拟子业务，由虚拟子业务把平台产生的消息、通知发送到子业务
		if (Platform.SUBBIZ_VIRTUAL == subSystemID) {
			vssSSCallBack = (VirtualSubSystemPFCallBack) dataReciver;
		} else {
			if (null != vssSSCallBack) {
				vssSSCallBack.subSystemregisterDataReciver(userID, subSystemID,
						dataReciver);
			}
		}
		return 1;
	}

	@Override
	public long startDataReceiver(RequestIdentifying reqIdentifying,
			IPlatFormCallBack dataReciver) {
		return getInstance(reqIdentifying.devType).addDataReceiver(
				reqIdentifying, dataReciver);
	}

	@Override
	public long stopDataReceiver(RequestIdentifying reqIdentifying) {
		return getInstance(reqIdentifying.devType).removeDataReceiver(
				reqIdentifying);
	}

	@Override
	public long send(RequestIdentifying reqIdentifying, int sendIndex,
			int channel, int mainCmd, int subCmd, byte[] bySendData, int length) {

		return getInstance(reqIdentifying.devType).send(reqIdentifying,
				sendIndex, channel, mainCmd, subCmd, bySendData, length);

	}

	@Override
	public long repeatSend(RequestIdentifying reqIdentifying, int spaceTime,
			int mainCmd, int subCmd, byte[] bySendData, int length) {

		return getInstance(reqIdentifying.devType).repeatSend(reqIdentifying,
				spaceTime, mainCmd, subCmd, bySendData, length);
	}

	@Override
	public long config(int userID, int subSystemID, int ctrlID,
			Object[] paramIn, Object[] paramOut) {
		long lResult = 0;
		if ((ctrlID >= Consts.CONFIG_CMD_WEBHOST_BEGIN)
				&& (ctrlID <= Consts.CONFIG_CMD_WEBHOST_END)) {
			return WebHostUtils.getWebHostInstance().control(userID,
					subSystemID, ctrlID, paramIn, paramOut);
		} else if ((ctrlID >= Consts.CONFIG_CMD_BTH_BEGIN)
				&& (ctrlID <= Consts.CONFIG_CMD_BTH_END)) {
			BlueToothUtils.getBlueToothInstance().control(userID, subSystemID,
					ctrlID, paramIn, paramOut);
		} else {
			lResult = 1;
		}
		return lResult;
	}

	@Override
	public long download(String url, String savepath, String user, String pwd) {
		WebHostUtils.getWebHostInstance().download(url, savepath, user, pwd);
		return 0;
	}

	/**
	 * 根据数据类型选择具体操作的单例
	 * 
	 * @param dataType
	 * @return
	 */
	private ICommunication getInstance(byte dataType) {
		if (Platform.DATATYPE_XYS_NETWORK == dataType) {
			return WebHostUtils.getWebHostInstance();
		}

		return BlueToothUtils.getBlueToothInstance();
	}

	@Override
	public boolean checkParameterMonitor(int userID, int subSystemID) {
		// TODO Auto-generated method stub
		return true;
	}
}
