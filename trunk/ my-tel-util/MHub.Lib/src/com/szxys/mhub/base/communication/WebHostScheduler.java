package com.szxys.mhub.base.communication;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import android.util.Log;

import com.szxys.mhub.base.manager.ConfigManager;
import com.szxys.mhub.base.manager.Consts;
import com.szxys.mhub.interfaces.DeviceConfig;
import com.szxys.mhub.interfaces.IPlatFormCallBack;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.interfaces.RequestIdentifying;

/**
 * 服务器通信调度类。
 */
class WebHostScheduler implements ICommunication {
	/**
	 * 服务器通信调度类单例。
	 */
	private static WebHostScheduler mSingleton = null;

	/**
	 * 子系统和服务器终结点字典。
	 */
	private ConcurrentMap<Integer, WebHostEndPoint> mSubSystemAndWebHost;

	/**
	 * 服务器终结点与服务器通信工作实例字典。
	 */
	private ConcurrentMap<WebHostEndPoint, WebHostWorker> mWebHostWorkers;

	/**
	 * 用户 Token 字典。
	 */
	private ConcurrentMap<Integer, Long> mUserTokens;

	/**
	 * 循环执行的任务字典。
	 */
	private ConcurrentMap<Integer, TimerTask> mTimerTasks;

	/**
	 * 计时器。
	 */
	private Timer mTimer;

	/**
	 * 服务器通信调度是否运行。
	 */
	private volatile boolean mIsRunning = false;

	static {
		mSingleton = new WebHostScheduler();
		mSingleton.mSubSystemAndWebHost.put(
				Platform.SUBBIZ_VIRTUAL,
				new WebHostEndPoint(ConfigManager.getInstance().read(
						ConfigManager.CONFIG_KEY_SERVER_ADRESS)));
	}

	/**
	 * 初始化服务器通信调度的实例。
	 */
	WebHostScheduler() {
		this.mUserTokens = new ConcurrentHashMap<Integer, Long>();
		this.mSubSystemAndWebHost = new ConcurrentHashMap<Integer, WebHostEndPoint>();
		this.mWebHostWorkers = new ConcurrentHashMap<WebHostEndPoint, WebHostWorker>();
		this.mTimerTasks = new ConcurrentHashMap<Integer, TimerTask>();
		this.mTimer = new Timer(true);
		this.mIsRunning = true;
	}

	/**
	 * 获取服务器通信调度类单例。
	 */
	static WebHostScheduler getSingleton() {
		return mSingleton;
	}

	/**
	 * 给指定的用户、子业务系统保存服务器终结点信息。
	 * 
	 * @param reqIdentifying
	 *            ：数据接收器对应的用户、子业务系统标识。
	 * @param remoteEndPoint
	 *            ：服务器终结点。
	 * @return 保存信息成功为true，否则为false。
	 */
	public boolean saveWebHostEndPoint(RequestIdentifying reqIdentifying,
			WebHostEndPoint remoteEndPoint) {
		if (this.mIsRunning) {
			this.mSubSystemAndWebHost.put(reqIdentifying.subSystemID,
					remoteEndPoint);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存指定的用户 Token。
	 * 
	 * @param userId
	 *            ：用户ID。
	 * @param token
	 *            ：用户Token。
	 * @return 保存用户 Token 成功为true，否则为false。
	 */
	public boolean saveUserToken(int userId, long token) {
		if (this.mIsRunning) {
			this.mUserTokens.put(userId, token);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除指定的用户 Token。
	 * 
	 * @param userId
	 *            ：用户ID。
	 * @return 删除用户 Token 成功为true，否则为false。
	 */
	public boolean deleteUserToken(int userId) {
		if (this.mIsRunning) {
			return this.mUserTokens.remove(userId) != null;
		} else {
			return false;
		}
	}

	/**
	 * 获取指定服务器通信实例的已发送的数据量。
	 * 
	 * @param remoteEndPoint
	 *            ：服务器终结点信息。
	 * @return 数据量。
	 */
	public long getTotalSentDataAmount(WebHostEndPoint remoteEndPoint) {
		if (this.mIsRunning) {
			WebHostWorker worker = this.mWebHostWorkers.get(remoteEndPoint);
			if (worker != null) {
				return worker.getTotalSentDataAmount();
			}
		}

		return 0;
	}

	/**
	 * 获取指定服务器通信实例的已接收的数据量。
	 * 
	 * @param remoteEndPoint
	 *            ：服务器终结点信息。
	 * @return 数据量。
	 */
	public long getTotalReceivedDataAmount(WebHostEndPoint remoteEndPoint) {
		if (this.mIsRunning) {
			WebHostWorker worker = this.mWebHostWorkers.get(remoteEndPoint);
			if (worker != null) {
				return worker.getTotalReceivedDataAmount();
			}
		}

		return 0;
	}

	@Override
	public DeviceConfig[] getDeviceConfig(int userId, int subSystemId) {
		if (this.mIsRunning) {
			WebHostEndPoint ep = this.mSubSystemAndWebHost.get(subSystemId);
			if (ep != null) {
				DeviceConfig config = new DeviceConfig();
				config.byDevType = Platform.DATATYPE_XYS_NETWORK;
				config.strDeviceAddr = ep.Adress;
				config.nNetServicePort = ep.Port;
				return new DeviceConfig[] { config };
			}
		}
		return null;
	}

	@Override
	public DeviceConfig[] getRegisterDeviceConfig(int userId, int subSystemId) {
		if (this.mIsRunning
				&& this.mSubSystemAndWebHost.containsKey(subSystemId)) {
			WebHostWorker worker = getWebHostWorker(subSystemId);
			DeviceConfig config = null;
			if (worker != null && (config = worker.getWebConfig()) != null) {
				return new DeviceConfig[] { config };
			}
		}
		return null;
	}

	@Override
	public long addDataReceiver(RequestIdentifying reqIdentifying,
			IPlatFormCallBack dataReceiver) {
		if (!this.mIsRunning) {
			// 模块未运行
			if (dataReceiver != null) {
				dataReceiver.onStarted(reqIdentifying,
						Platform.ERROR_NO_RUNNING);
			}
			return 0;
		}

		WebHostWorker worker = getWebHostWorker(reqIdentifying.subSystemID);
		if (worker == null) {
			// 第一次创建
			worker = createWebHostWorker(reqIdentifying.subSystemID);
			if (worker == null) {
				// 创建工作实例失败
				Log.e("WebHostScheduler",
						"'addDataReceiver' createWebHostWorker failed!'reqIdentifying.subSystemID' is "
								+ reqIdentifying.subSystemID);

				if (dataReceiver != null) {
					dataReceiver.onStarted(reqIdentifying,
							Platform.ERROR_INVALID_PARAMETER);
				}
				return 0;
			}

			// 添加数据接收器
			final boolean result = worker.addPlatFormCallBack(
					reqIdentifying.subSystemID, dataReceiver);

			final RequestIdentifying tmpReqIdentifying = reqIdentifying;
			final IPlatFormCallBack tmpDataReceiver = dataReceiver;

			// Run成功后执行的回调
			Action<Boolean, Boolean> action = new Action<Boolean, Boolean>() {

				@Override
				public Boolean action(Boolean ranSucceeded) {
					// 返回结果
					if (result && ranSucceeded) {
						Log.i("WebHostScheduler", "addDataReceiver succeeded!");

						if (tmpDataReceiver != null) {
							tmpDataReceiver.onStarted(tmpReqIdentifying,
									Platform.SUCCEEDED);
						}
					} else {
						Log.e("WebHostScheduler", "addDataReceiver failed!");

						if (tmpDataReceiver != null) {
							tmpDataReceiver.onStarted(tmpReqIdentifying,
									Platform.ERROR_START_DATA_RECEIVER_FAILED);
						}
					}
					return result && ranSucceeded;
				}
			};

			// 将工作实例运行起来
			worker.run(action);
			return 1;
		} else {
			// 添加数据接收器
			if (worker.addPlatFormCallBack(reqIdentifying.subSystemID,
					dataReceiver)) {
				Log.i("WebHostScheduler", "addDataReceiver succeeded!");

				if (dataReceiver != null) {
					dataReceiver.onStarted(reqIdentifying, Platform.SUCCEEDED);
				}
			} else {
				Log.e("WebHostScheduler", "addDataReceiver failed!");

				if (dataReceiver != null) {
					dataReceiver.onStarted(reqIdentifying,
							Platform.ERROR_START_DATA_RECEIVER_FAILED);
				}
			}
			return 1;
		}
	}

	@Override
	public long removeDataReceiver(RequestIdentifying reqIdentifying) {
		WebHostWorker worker = getWebHostWorker(reqIdentifying.subSystemID);
		// 找不到对应的工作实例
		if (worker == null) {
			return Platform.ERROR_INVALID_PARAMETER;
		}

		// 移除回调函数
		IPlatFormCallBack platFormCallBack = worker
				.removePlatFormCallBack(reqIdentifying.subSystemID);
		removeWebHostWorker(reqIdentifying.subSystemID);

		// 如果数据接收器已被清空，则停止运行该服务器通信实例。
		ConcurrentMap<Integer, IPlatFormCallBack> callBacks = worker
				.getAllPlatFormCallBack();
		if (callBacks == null || callBacks.size() == 0) {
			worker.shutdown();
		}

		if (platFormCallBack != null) {
			// 移除成功
			Log.i("WebHostScheduler", "removeDataReceiver succeeded!");
			return Platform.SUCCEEDED;
		} else {
			// 移除失败
			Log.i("WebHostScheduler",
					"removeDataReceiver failed!'reqIdentifying.subSystemID' is "
							+ reqIdentifying.subSystemID);
			return Platform.ERROR_STOP_DATA_RECEIVER_FAILED;
		}
	}

	@Override
	public long send(RequestIdentifying reqIdentifying, int lSendIndex,
			int lPriority, int lMainCmd, int lSubCmd, byte[] data,
			int dataLength) {
		if (!this.mIsRunning) {
			// return Platform.ERROR_NO_RUNNING;
			return 0;
		}

		WebHostWorker worker = getWebHostWorker(reqIdentifying.subSystemID);
		if (worker == null) {
			Log.e("WebHostScheduler",
					"(send)Insert queue failed:'reqIdentifying' is invalid!'reqIdentifying.subSystemID' is "
							+ reqIdentifying.subSystemID);
			// return Platform.ERROR_INVALID_PARAMETER;
			return 0;
		}

		IPlatFormCallBack callback = null;
		ConcurrentMap<Integer, IPlatFormCallBack> callbacks = worker
				.getAllPlatFormCallBack();
		if (callbacks != null) {
			callback = callbacks.get(reqIdentifying.subSystemID);
		}

		Long token = getUserToken(reqIdentifying.userID);
		if (token == null) {
			if (lMainCmd == Consts.CMD_WEB_GET_SERVER_ADRESS
					|| lMainCmd == Consts.CMD_WEB_GET_BIND_INFO
					|| lMainCmd == Consts.CMD_WEB_GET_UPDATE_INFO
					|| lMainCmd == Consts.CMD_WEB_GET_USER_TOKEN) {
				token = (long) 0;
			} else {
				Log.e("WebHostScheduler",
						"Failed to get user's token(send dataCell)!");
				if (callback != null) {
					callback.onSent(reqIdentifying, lSendIndex,
							Platform.ERROR_WEB_NO_USER_TOKEN);
				}
				return 0;
			}
		}

		WebDataCell dataCell = new WebDataCell();
		dataCell.ReqIdentifying = reqIdentifying;
		dataCell.Priority = lPriority;
		dataCell.SendIndex = lSendIndex;
		dataCell.MainCmd = lMainCmd;
		dataCell.SubCmd = lSubCmd;
		dataCell.Token = token;
		if (data != null && dataLength < data.length) {
			dataCell.Data = new byte[dataLength];
			System.arraycopy(data, 0, dataCell.Data, 0, dataLength);
		} else {
			dataCell.Data = data;
		}

		try {
			if (worker.send(dataCell)) {
				Log.i("WebHostScheduler",
						"Insert queue succeeded(send dataCell)!");
				return 1;
			} else {
				Log.e("WebHostScheduler",
						"Insert queue overflowed(send dataCell)!");

				if (callback != null) {
					callback.onSent(reqIdentifying, lSendIndex,
							Platform.ERROR_QUEUE_OVERFLOW);
				}
				return 0;
			}
		} catch (InterruptedException e) {
			Log.e("WebHostScheduler", "Insert queue failed(send dataCell)!", e);

			if (callback != null) {
				callback.onSent(reqIdentifying, lSendIndex,
						Platform.ERROR_INSERT_QUEUE_FAILED);
			}
			return 0;
		}
	}

	@Override
	public long repeatSend(RequestIdentifying reqIdentifying, int lSendCycle,
			int lMainCmd, int lSubCmd, byte[] data, int dataLength) {
		if (!this.mIsRunning) {
			return 0;
		}

		WebDataCell dataCell = new WebDataCell();
		dataCell.ReqIdentifying = reqIdentifying;
		dataCell.Priority = Platform.NETDATA_NONREALTIME;
		dataCell.SendCycle = lSendCycle;
		dataCell.SendIndex = -1;
		dataCell.MainCmd = lMainCmd;
		dataCell.SubCmd = lSubCmd;
		if (data != null && dataLength < data.length) {
			dataCell.Data = new byte[dataLength];
			System.arraycopy(data, 0, dataCell.Data, 0, dataLength);
		} else {
			dataCell.Data = data;
		}

		try {
			int key = dataCell.getHashKey();
			if (dataCell.SendCycle == 0) {
				// 如果时间间隔为 0 ，表示需要移除任务
				TimerTask task = this.mTimerTasks.remove(key);
				if (task != null) {
					try {
						task.cancel();
					} catch (Exception e) {
						Log.e("WebHostScheduler",
								"Failure to cancel the task!", e);
					}
					task = null;
				}
				Log.i("WebHostScheduler", "'repeatSend' remove task succeeded!");
			} else {
				// 添加任务
				TimerTask task = new WebHostTimerTask(dataCell);
				TimerTask preTask = this.mTimerTasks.put(key, task);
				if (preTask != null) {
					// 如果同一个 Key 已经存在定时任务，则将原来的撤销掉
					try {
						preTask.cancel();
					} catch (Exception ex) {
						Log.e("WebHostScheduler",
								"Failure to cancel the previous task!", ex);
					}
					preTask = null;
				}

				this.mTimer.schedule(task, 0, dataCell.SendCycle * 1000);
				Log.i("WebHostScheduler", "'repeatSend' start task succeeded!");
			}
			return 1;
		} catch (Exception e) {
			Log.e("WebHostScheduler", "Failed to submet the task!", e);
			return 0;
		}
	}

	@Override
	public long download(String url, String savepath, String user, String pwd) {
		long result = 0;

		FTPDownloadUtil ftp = new FTPDownloadUtil(url, user, pwd);
		if (ftp.downloadFile(url, savepath)) {
			result = 1;
		}

		return result;
	}

	@Override
	public long control(int userID, int subSystemID, int ctrlID,
			Object[] paramIn, Object[] paramOut) {
		switch (ctrlID) {
		case Consts.CMD_WEBHOST_SAVE_TOKEN:
			try {
				saveUserToken(userID, (Long) paramIn[0]);
				return 1;
			} catch (Exception e) {
				Log.e("WebHostScheduler", "Failed to save token!", e);
				return 0;
			}
		case Consts.CMD_WEBHOST_DEL_TOKEN:
			try {
				deleteUserToken(userID);
				return 1;
			} catch (Exception e) {
				Log.e("WebHostScheduler", "Failed to delete token!", e);
				return 0;
			}
		case Consts.CMD_WEBHOST_SAVE_WEBHOST_ENDPOINT:
			try {
				RequestIdentifying reqIdentifying = new RequestIdentifying();
				reqIdentifying.subSystemID = subSystemID;
				saveWebHostEndPoint(reqIdentifying,
						(WebHostEndPoint) paramIn[0]);
				return 1;
			} catch (Exception e) {
				Log.e("WebHostScheduler", "Failed to save WebHostEndPoint!", e);
				return 0;
			}
		case Consts.CMD_WEBHOST_GET_TOTAL_SENT_DATA_AMOUNT:
			try {
				paramOut[0] = getTotalSentDataAmount(this.mSubSystemAndWebHost
						.get(subSystemID));
				return 1;
			} catch (Exception e) {
				Log.e("WebHostScheduler",
						"Failed to GET_TOTAL_SENT_DATA_AMOUNT!", e);
				return 0;
			}
		case Consts.CMD_WEBHOST_GET_TOTAL_RECEIVED_DATA_AMOUNT:
			try {
				paramOut[0] = getTotalReceivedDataAmount(this.mSubSystemAndWebHost
						.get(subSystemID));
				return 1;
			} catch (Exception e) {
				Log.e("WebHostScheduler",
						"Failed to GET_TOTAL_RECEIVED_DATA_AMOUNT!", e);
				return 0;
			}
		default:
			return 0;
		}
	}

	@Override
	public long clear() {
		try {
			this.finalize();
			return 1;
		} catch (Throwable e) {
			Log.e("WebHostScheduler", "Clear failed!", e);

			this.mIsRunning = false;
			this.mWebHostWorkers = null;
			this.mSubSystemAndWebHost = null;
			this.mTimerTasks = null;
			this.mUserTokens = null;
			return 0;
		}
	}

	/**
	 * 获取指定的用户 Token，用户不存在时返回 null。
	 * 
	 * @param userId
	 *            ：用户ID。
	 */
	private Long getUserToken(int userId) {
		Long token = this.mUserTokens.get(userId);
		return token;
	}

	/**
	 * 获取指定用户、子系统的服务器通信工作实例，如果找不到则返回null。
	 * 
	 * @param subSystemId
	 *            ：子系统的业务ID。
	 * @return 对应的 WebHostWorker 实例。
	 */
	private WebHostWorker getWebHostWorker(int subSystemId) {
		WebHostEndPoint ep = null;
		if (this.mIsRunning
				&& (ep = this.mSubSystemAndWebHost.get(subSystemId)) != null) {
			return this.mWebHostWorkers.get(ep);
		} else {
			return null;
		}
	}

	/**
	 * 创建服务器通信工作实例。
	 * 
	 * @param subSystemId
	 *            ：子系统的业务ID。
	 * @return 创建的 WebHostWorker 实例。
	 */
	private WebHostWorker createWebHostWorker(int subSystemId) {
		if (!this.mIsRunning) {
			return null;
		}

		WebHostEndPoint ep = null;
		if ((ep = this.mSubSystemAndWebHost.get(subSystemId)) == null) {
			// 服务器地址是动态获取的，如果获取不到指定业务系统的服务器地址，则说明Web端配置错误
			Log.e("WebHostScheduler",
					"Failed to get server adress!subSystemId is " + subSystemId);
			return null;
		}

		WebHostWorker worker = null;
		if ((worker = this.mWebHostWorkers.get(ep)) == null) {
			try {
				this.mWebHostWorkers.putIfAbsent(ep, new WebHostWorker(
						WebHostUtilInner.creatWebServiceHost(ep)));
				worker = this.mWebHostWorkers.get(ep);
			} catch (Exception e) {
				Log.e("WebHostScheduler",
						"WebHostWorker failed to initialize!", e);
			}
		}
		return worker;
	}

	/**
	 * 移除服务器通信工作实例。
	 * 
	 * @param subSystemId
	 *            ：子系统的业务ID。
	 * @return 找到对应的 WebHostWorker 并移除成功则为true，否则为false。
	 */
	private boolean removeWebHostWorker(int subSystemId) {
		WebHostEndPoint ep = null;
		if (this.mIsRunning
				&& (ep = this.mSubSystemAndWebHost.get(subSystemId)) != null) {
			boolean result = this.mSubSystemAndWebHost.remove(subSystemId) != null;
			// 如果所有系统都不再与该服务器通信，则移除该服务器通信实例
			if (!this.mSubSystemAndWebHost.containsValue(ep)) {
				Log.i("WebHostScheduler",
						"Remove the WebHostWorker!EndPoint is " + ep);
				this.mWebHostWorkers.remove(ep);
			}
			return result;
		} else {
			return false;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		this.mIsRunning = false;

		this.mTimerTasks = null;
		if (this.mTimer != null) {
			try {
				this.mTimer.cancel();
			} catch (Exception e) {
				Log.e("WebHostScheduler",
						"Failed to cancel the Timer and all scheduled tasks!",
						e);
			}
		}
		this.mTimer = null;

		if (this.mWebHostWorkers != null) {
			for (WebHostWorker worker : this.mWebHostWorkers.values()) {
				if (worker != null) {
					try {
						worker.shutdown();
					} catch (Exception e) {
						Log.e("WebHostScheduler", "Failed to shutdown!", e);
					}
				}
			}
		}
		this.mUserTokens = null;
		this.mWebHostWorkers = null;
		this.mSubSystemAndWebHost = null;
	}

	/**
	 * 服务器通信定时任务类。
	 */
	class WebHostTimerTask extends TimerTask {
		/**
		 * 任务数据。
		 */
		public WebDataCell Data = null;

		/**
		 * 初始化一个服务器通信定时任务。
		 * 
		 * @param data
		 *            ：任务数据。
		 */
		public WebHostTimerTask(WebDataCell data) {
			super();
			this.Data = data;
		}

		@Override
		public void run() {
			if (this.Data != null) {
				send(this.Data.ReqIdentifying, this.Data.SendIndex,
						this.Data.Priority, this.Data.MainCmd,
						this.Data.SubCmd, this.Data.Data, Integer.MAX_VALUE);
			}
		}
	}
}