package com.szxys.mhub.base.communication;

import android.util.Log;

import com.szxys.mhub.base.communication.webservice.WebServiceFactory;
import com.szxys.mhub.base.communication.webservice.WebUtils;
import com.szxys.mhub.base.manager.ConfigManager;
import com.szxys.mhub.base.manager.Consts;
import com.szxys.mhub.base.manager.DeviceManager;
import com.szxys.mhub.interfaces.IPlatFormInterface;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.interfaces.RequestIdentifying;

/**
 * 服务器通信管理帮助类。
 */
public class WebHostUtils {
	static {
		ConfigManager instance = ConfigManager.getInstance();

		// 测试时用的代码
		// instance.write(ConfigManager.CONFIG_KEY_SERVER_ADRESS,
		// Consts.MAIN_SERVER_ADRESS);

		// 发布用的代码
		String adress = instance.read(ConfigManager.CONFIG_KEY_SERVER_ADRESS);
		if (adress == null || adress.equals("")) {
			instance.write(ConfigManager.CONFIG_KEY_SERVER_ADRESS,
					Consts.MAIN_SERVER_ADRESS);
		}
	}

	/**
	 * 获取服务器通信调度单例。
	 */
	public static ICommunication getWebHostInstance() {
		return WebHostScheduler.getSingleton();
	}

	/**
	 * 向Web端发送请求最新绑定信息的消息。
	 * 
	 * @param pfInstance
	 *            ：平台通信实例。
	 * @return long： 1表示发送成功，0表示发送失败。
	 */
	public static long sendMsgOfGetLatestBindInfo(IPlatFormInterface pfInstance) {
		if (pfInstance == null) {
			return 0;
		}

		try {
			RequestIdentifying reqIdentifying = new RequestIdentifying();
			reqIdentifying.subSystemID = Platform.SUBBIZ_VIRTUAL;
			byte[] data = DeviceManager.getLocalMobile().Mac.getBytes("UTF-8");

			return pfInstance.send(reqIdentifying, -1,
					Platform.NETDATA_REALTIME, Consts.CMD_WEB_GET_BIND_INFO,
					-1, data, data.length);
		} catch (Exception e) {
			Log.e("WebHostUtils", "send CMD_WEB_GET_BIND_INFO message!", e);
			return 0;
		}
	}

	/**
	 * 向Web端发送请求各业务服务器地址的消息。
	 * 
	 * @param pfInstance
	 *            ：平台通信实例。
	 * @return long： 1表示发送成功，0表示发送失败。
	 */
	public static long sendMsgOfGetServerAdress(IPlatFormInterface pfInstance) {
		if (pfInstance == null) {
			return 0;
		}

		try {
			int dstPos = -4;
			byte[] data = new byte[4 * 14];
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_VIRTUAL), 0, data,
					(dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_ECG), 0, data,
					(dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_METS), 0, data,
					(dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_UFR), 0, data,
					(dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_DMFS), 0, data,
					(dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_PFUS), 0, data,
					(dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_PROSTATITISFU), 0,
					data, (dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_BPHFU), 0, data,
					(dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_ABPMS), 0, data,
					(dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_APS), 0, data,
					(dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_HM), 0, data,
					(dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_HEALTHRECORD), 0,
					data, (dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_FETALHEART), 0,
					data, (dstPos = dstPos + 4), 4);
			System.arraycopy(WebUtils.toLH(Platform.SUBBIZ_RMBGMS), 0, data,
					(dstPos = dstPos + 4), 4);

			RequestIdentifying reqIdentifying = new RequestIdentifying();
			reqIdentifying.subSystemID = Platform.SUBBIZ_VIRTUAL;
			return pfInstance.send(reqIdentifying, -1,
					Platform.NETDATA_REALTIME,
					Consts.CMD_WEB_GET_SERVER_ADRESS, 0, data, data.length);
		} catch (Exception e) {
			Log.e("WebHostUtils", "send CMD_WEB_GET_SERVER_ADRESS message!", e);
			return 0;
		}
	}

	/**
	 * 向Web端发送请求指定用户 Token 的消息。
	 * 
	 * @param pfInstance
	 *            ：平台通信实例。
	 * @param userId
	 *            ：用户ID。
	 * @return long： 1表示发送成功，0表示发送失败。
	 */
	public static long sendMsgOfGetUserToken(IPlatFormInterface pfInstance,
			int userId) {
		if (pfInstance == null) {
			return 0;
		}

		try {
			RequestIdentifying reqIdentifying = new RequestIdentifying();
			reqIdentifying.userID = userId;
			reqIdentifying.subSystemID = Platform.SUBBIZ_VIRTUAL;
			byte[] data = WebUtils.toLH(userId);
			return pfInstance.send(reqIdentifying, -1,
					Platform.NETDATA_REALTIME, Consts.CMD_WEB_GET_USER_TOKEN,
					0, data, data.length);
		} catch (Exception e) {
			Log.e("WebHostUtils", "send CMD_WEB_GET_USER_TOKEN message!", e);
			return 0;
		}
	}

	/**
	 * 向Web端发送删除指定用户 Token 的消息。
	 * 
	 * @param pfInstance
	 *            ：平台通信实例。
	 * @param userId
	 *            ：用户ID。
	 * @return long： 1表示发送成功，0表示发送失败。
	 */
	public static long sendMsgOfDeleteUserToken(IPlatFormInterface pfInstance,
			int userId) {
		if (pfInstance == null) {
			return 0;
		}

		try {
			RequestIdentifying reqIdentifying = new RequestIdentifying();
			reqIdentifying.userID = userId;
			reqIdentifying.subSystemID = Platform.SUBBIZ_VIRTUAL;
			byte[] data = WebUtils.toLH(userId);
			return pfInstance.send(reqIdentifying, -1,
					Platform.NETDATA_REALTIME, Consts.CMD_WEB_DEL_USER_TOKEN,
					0, data, data.length);
		} catch (Exception e) {
			Log.e("WebHostUtils", "send CMD_WEB_DEL_USER_TOKEN message!", e);
			return 0;
		}
	}

	/**
	 * 向Web端发送请求指定用户信息的消息。
	 * 
	 * @param pfInstance
	 *            ：平台通信实例。
	 * @param userId
	 *            ：用户ID。
	 * @return long： 1表示发送成功，0表示发送失败。
	 */
	public static long sendMsgOfGetUser(IPlatFormInterface pfInstance,
			int userId) {
		if (pfInstance == null) {
			return 0;
		}

		try {
			RequestIdentifying reqIdentifying = new RequestIdentifying();
			reqIdentifying.userID = userId;
			reqIdentifying.subSystemID = Platform.SUBBIZ_VIRTUAL;
			byte[] data = WebUtils.toLH(userId);
			return pfInstance.send(reqIdentifying, -1,
					Platform.NETDATA_REALTIME, Consts.CMD_WEB_GET_USER_INFO, 0,
					data, data.length);
		} catch (Exception e) {
			Log.e("WebHostUtils", "send CMD_WEB_GET_USER_INFO message!", e);
			return 0;
		}
	}

	/**
	 * 保存各业务服务器地址。
	 * 
	 * @param pfInstance
	 *            ：平台通信实例。
	 * @param byRecvData
	 *            ：待保存数据。
	 * @param length
	 *            ：数据长度。
	 */
	public static void saveServerAdress(IPlatFormInterface pfInstance,
			byte[] byRecvData, int length) {
		if (pfInstance == null) {
			return;
		}

		try {
			int i = 0;
			while (i < length) {
				try {
					byte[] data = new byte[4];
					System.arraycopy(byRecvData, i, data, 0, 4);
					int appId = WebUtils.lBytesToInt(data);
					i = i + 4;
					data = new byte[byRecvData[i]];
					i = i + 1;
					System.arraycopy(byRecvData, i, data, 0, data.length);
					i = i + data.length;
					String adress = new String(data, "UTF-8");

					if (adress != null && !adress.equals("")) {
						pfInstance.config(-1, appId,
								Consts.CMD_WEBHOST_SAVE_WEBHOST_ENDPOINT,
								new WebHostEndPoint[] { new WebHostEndPoint(
										adress) }, null);
					}
				} catch (Exception e) {
					Log.e("WebHostUtils", "save ServerAdress!", e);
				}
			}
		} catch (Exception e) {
			Log.e("WebHostUtils", "save ServerAdress!", e);
		}
	}

	/**
	 * 保存用户 Token 。
	 * 
	 * @param pfInstance
	 *            ：平台通信实例。
	 * @param byRecvData
	 *            ：待保存数据。
	 * @param length
	 *            ：数据长度。
	 */
	public static long saveUserToken(IPlatFormInterface pfInstance,
			byte[] byRecvData, int length) {
		if (pfInstance == null) {
			return 0;
		}

		try {
			int userId = WebUtils.getReverseBytesInt(byRecvData, 0);
			long token = WebUtils.getLong(byRecvData, 4);
			return pfInstance.config(userId, Platform.SUBBIZ_VIRTUAL,
					Consts.CMD_WEBHOST_SAVE_TOKEN,
					new Long[] { new Long(token) }, null);
		} catch (Exception e) {
			Log.e("WebHostUtils", "save UserToken!", e);
			return 0;
		}
	}
}

/**
 * 服务器通信管理内部帮助类。
 */
class WebHostUtilInner {
	/**
	 * 使用 WebService 通信方式的服务器通信工厂。
	 */
	static AbstractWebHostFactory mWebServiceFactory = null;

	/**
	 * 根据远程服务器终结点创建一个使用 WebService 进行通信的服务器通信对象。
	 * 
	 * @param remoteEndPoint
	 *            ：远程服务器终结点。
	 */
	static IWebHost creatWebServiceHost(WebHostEndPoint remoteEndPoint) {
		if (mWebServiceFactory == null) {
			mWebServiceFactory = new WebServiceFactory();
		}
		return mWebServiceFactory.createWebHost(remoteEndPoint);
	}
}