package com.szxys.mhub.base.communication;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import android.util.Log;
import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.base.btdevice.BthTransportAttribute;
import com.szxys.mhub.base.manager.Consts;
import com.szxys.mhub.interfaces.Collector;
import com.szxys.mhub.interfaces.DeviceConfig;
import com.szxys.mhub.interfaces.IPlatFormCallBack;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.interfaces.RequestIdentifying;

/**
 * 蓝牙通信调度类。
 */
class BthDeviceScheduler implements ICommunication {
	/**
	 * 蓝牙通信调度类单例。
	 */
	private static BthDeviceScheduler mSingleton = null;

	/**
	 * 用户、采集器类型、采集器字典。
	 */
	private ConcurrentMap<Integer, ConcurrentMap<Byte, BthDeviceWorker>> mUserAndDevice;

	/**
	 * 蓝牙通信调度是否运行。
	 */
	private volatile boolean mIsRunning = false;

	static {
		mSingleton = new BthDeviceScheduler();
	}

	/**
	 * 初始化蓝牙通信调度的实例。
	 */
	BthDeviceScheduler() {
		this.mUserAndDevice = new ConcurrentHashMap<Integer, ConcurrentMap<Byte, BthDeviceWorker>>();
		this.mIsRunning = true;
	}

	/**
	 * 获取蓝牙通信调度类单例。
	 */
	static BthDeviceScheduler getSingleton() {
		return mSingleton;
	}

	/**
	 * 获取指定蓝牙通信实例的已发送的数据量。
	 * 
	 * @param userId
	 *            ：用户ID。
	 * @param deviceType
	 *            ：采集器类型。
	 * @return 数据量。
	 */
	public long getTotalSentDataAmount(int userId, byte deviceType) {
		BthDeviceWorker worker = getBthDeviceWorker(userId, deviceType);
		if (worker != null) {
			return worker.getTotalSentDataAmount();
		} else {
			return 0;
		}
	}

	/**
	 * 获取指定蓝牙通信实例的已接收的数据量。
	 * 
	 * @param userId
	 *            ：用户ID。
	 * @param deviceType
	 *            ：采集器类型。
	 * @return 数据量。
	 */
	public long getTotalReceivedDataAmount(int userId, byte deviceType) {
		BthDeviceWorker worker = getBthDeviceWorker(userId, deviceType);
		if (worker != null) {
			return worker.getTotalReceivedDataAmount();
		} else {
			return 0;
		}
	}

	@Override
	public DeviceConfig[] getDeviceConfig(int userId, int subSystemId) {
		Collector collector = BlueToothUtilInner.getCollector(userId,
				(byte) subSystemId);
		if (collector != null) {
			DeviceConfig config = new DeviceConfig();
			config.byDevType = collector.DeviceType;
			config.strDeviceAddr = collector.Mac;

			return new DeviceConfig[] { config };
		} else {
			return null;
		}
	}

	@Override
	public DeviceConfig[] getRegisterDeviceConfig(int userId, int subSystemId) {
		if (this.mIsRunning && this.mUserAndDevice.containsKey(userId)) {
			ConcurrentMap<Byte, BthDeviceWorker> workers = this.mUserAndDevice
					.get(userId);
			if (workers != null) {
				BthDeviceWorker worker = null;
				if ((worker = workers.get(subSystemId)) != null) {
					return new DeviceConfig[] { worker.getDeviceConfig() };
				}
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

		BthDeviceWorker worker = getBthDeviceWorker(reqIdentifying.userID,
				reqIdentifying.devType);
		// 添加数据接收器流程
		if (worker == null) {
			// 第一次创建，到数据库读取采集器信息
			Collector collector = BlueToothUtilInner.getCollector(
					reqIdentifying.userID, reqIdentifying.devType);
			if (collector == null) {
				// 采集器为NULL
				Log.i("BthDeviceScheduler",
						"'addDataReceiver' getCollector failed!");

				if (dataReceiver != null) {
					dataReceiver.onStarted(reqIdentifying,
							Platform.ERROR_INVALID_PARAMETER);
				}
				return 0;
			}

			if (reqIdentifying.reserved1 == Platform.CMD_DEVICE_GRNAT) {
				// 主动连接终端的采集器发放时，需要修改连接模式为终端主动连接采集器
				collector.PassiveMode = false;
			}

			worker = createBthDeviceWorker(reqIdentifying.userID, collector);
			if (worker == null) {
				// 创建工作实例失败
				Log.i("BthDeviceScheduler",
						"'addDataReceiver' createBthDeviceWorker failed!");

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
						Log.i("BthDeviceScheduler",
								"addDataReceiver succeeded!");

						if (tmpDataReceiver != null) {
							tmpDataReceiver.onStarted(tmpReqIdentifying,
									Platform.SUCCEEDED);
						}
					} else {
						Log.i("BthDeviceScheduler", "addDataReceiver failed!");

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
				Log.i("BthDeviceScheduler", "addDataReceiver succeeded!");

				if (dataReceiver != null) {
					dataReceiver.onStarted(reqIdentifying, Platform.SUCCEEDED);
				}
			} else {
				Log.i("BthDeviceScheduler", "addDataReceiver failed!");

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
		BthDeviceWorker worker = getBthDeviceWorker(reqIdentifying.userID,
				reqIdentifying.devType);
		// 找不到对应的工作实例
		if (worker == null) {
			return Platform.ERROR_INVALID_PARAMETER;
		}

		// 移除回调函数
		IPlatFormCallBack platFormCallBack = worker
				.removePlatFormCallBack(reqIdentifying.subSystemID);

		ConcurrentMap<Integer, IPlatFormCallBack> callBacks = worker
				.getAllPlatFormCallBack();
		// 如果数据接收器已被清空，则停止运行该蓝牙通信实例。
		if (callBacks == null || callBacks.size() == 0) {
			removeBthDeviceWorker(reqIdentifying.userID, reqIdentifying.devType);
			worker.shutdown();
		}

		if (platFormCallBack != null) {
			// 移除成功
			Log.i("BthDeviceScheduler", "removeDataReceiver succeeded!");
			return Platform.SUCCEEDED;
		} else {
			// 移除失败
			Log.i("BthDeviceScheduler",
					"removeDataReceiver failed!'reqIdentifying.subSystemID' is "
							+ reqIdentifying.subSystemID);
			return Platform.ERROR_STOP_DATA_RECEIVER_FAILED;
		}
	}

	@Override
	public long send(RequestIdentifying reqIdentifying, int lSendIndex,
			int lChannel, int lMainCmd, int lSubCmd, byte[] data, int dataLength) {
		if (!this.mIsRunning) {
			// return Platform.ERROR_NO_RUNNING;
			return 0;
		}

		BthDeviceWorker worker = getBthDeviceWorker(reqIdentifying.userID,
				reqIdentifying.devType);
		if (worker == null) {
			Log.i("BthDeviceScheduler",
					"(send)Insert queue failed:'reqIdentifying' is invalid!"
							+ "\r\nreqIdentifying.subSystemID:"
							+ reqIdentifying.subSystemID
							+ "\r\nreqIdentifying.devType:"
							+ reqIdentifying.devType);
			// return Platform.ERROR_INVALID_PARAMETER;
			return 0;
		}

		Buffer buffer = new Buffer();
		buffer.ReqIdentifying = reqIdentifying;
		buffer.Channel = lChannel;
		buffer.SendIndex = lSendIndex;
		if (data != null && dataLength < data.length) {
			buffer.Data = new byte[dataLength];
			System.arraycopy(data, 0, buffer.Data, 0, dataLength);
		} else {
			buffer.Data = data;
		}

		IPlatFormCallBack callback = null;
		ConcurrentMap<Integer, IPlatFormCallBack> callbacks = worker
				.getAllPlatFormCallBack();
		if (callbacks != null) {
			callback = callbacks.get(reqIdentifying.subSystemID);
		}

		try {
			if (worker.send(buffer)) {
				Log.i("BthDeviceScheduler",
						"Insert queue succeeded(send buffer)!");
				return 1;
			} else {
				Log.i("BthDeviceScheduler",
						"Insert queue overflowed(send buffer)!");

				if (callback != null) {
					callback.onSent(reqIdentifying, lSendIndex,
							Platform.ERROR_QUEUE_OVERFLOW);
				}
				return 0;
			}
		} catch (InterruptedException e) {
			Log.e("BthDeviceScheduler", "Insert queue failed(send buffer)!", e);

			if (callback != null) {
				callback.onSent(reqIdentifying, lSendIndex,
						Platform.ERROR_INSERT_QUEUE_FAILED);
			}
			return 0;
		}
	}

	@Override
	public long repeatSend(RequestIdentifying reqIdentifying, int spaceTime,
			int mainCmd, int subCmd, byte[] bySendData, int length) {
		return 0;
	}

	@Override
	public long download(String url, String savepath, String user, String pwd) {
		return 0;
	}

	@Override
	public long control(int userID, int subSystemID, int ctrlID,
			Object[] paramIn, Object[] paramOut) {
		switch (ctrlID) {
		case Consts.CMD_BTH_GET_TOTAL_SENT_DATA_AMOUNT:
			try {
				paramOut[0] = getTotalSentDataAmount(userID, (Byte) paramIn[0]);
				return 1;
			} catch (Exception e) {
				Log.e("BthDeviceScheduler",
						"Failed to GET_TOTAL_SENT_DATA_AMOUNT!", e);
				return 0;
			}
		case Consts.CMD_BTH_GET_TOTAL_RECEIVED_DATA_AMOUNT:
			try {
				paramOut[0] = getTotalReceivedDataAmount(userID,
						(Byte) paramIn[0]);
				return 1;
			} catch (Exception e) {
				Log.e("BthDeviceScheduler",
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
			Log.e("BthDeviceScheduler", "Clear failed!", e);

			this.mIsRunning = false;
			this.mUserAndDevice = null;
			return 0;
		}
	}

	/**
	 * 获取指定用户、设备类型的蓝牙通信工作实例，如果找不到则返回null。
	 * 
	 * @param userId
	 *            ：用户ID。
	 * @param deviceType
	 *            ：采集器类型。
	 * @return 对应的 BthDeviceWorker 实例。
	 */
	private BthDeviceWorker getBthDeviceWorker(int userId, byte deviceType) {
		ConcurrentMap<Byte, BthDeviceWorker> workers = null;
		if (this.mIsRunning
				&& (workers = this.mUserAndDevice.get(userId)) != null) {
			return workers.get(deviceType);
		} else {
			return null;
		}
	}

	/**
	 * 创建蓝牙通信工作实例。
	 * 
	 * @param userId
	 *            ：用户ID。
	 * @param collector
	 *            ：采集器。
	 * @return 创建的 BthDeviceWorker 实例。
	 */
	private BthDeviceWorker createBthDeviceWorker(int userId,
			Collector collector) {
		if (!this.mIsRunning || collector == null) {
			return null;
		}

		byte deviceType = collector.DeviceType;
		ConcurrentMap<Byte, BthDeviceWorker> workers = null;
		if ((workers = this.mUserAndDevice.get(userId)) == null) {
			// 如果不存在指定用户，则添加
			this.mUserAndDevice.putIfAbsent(userId,
					new ConcurrentHashMap<Byte, BthDeviceWorker>());
			workers = this.mUserAndDevice.get(userId);
		}

		BthDeviceWorker worker = null;
		if ((worker = workers.get(deviceType)) == null) {
			// 如果不存在指定类型的采集器，则添加
			BthTransportAttribute attr = new BthTransportAttribute();
			attr.fMacAddress = collector.Mac;
			attr.fHeartBeatFrequency = collector.HeartBeatInterval;
			attr.fPairingCode = collector.PairingCode;
			attr.fProtocolType = collector.ProtocolType;
			attr.fIsPassiveMode = collector.PassiveMode;
			attr.fNumOfChannels = collector.NumOfChannels;
			attr.fDeviceType = collector.DeviceType;
			attr.fAppContext = MhubApplication.getInstance()
					.getApplicationContext();

			try {
				workers.putIfAbsent(deviceType, new BthDeviceWorker(attr,
						userId, deviceType));
				worker = workers.get(deviceType);
			} catch (IllegalArgumentException e) {
				Log.e("BthDeviceScheduler",
						"BthDeviceWorker failed to initialize(in createBthDeviceWorker)!",
						e);
			}
		}
		return worker;
	}

	/**
	 * 移除蓝牙通信工作实例。
	 * 
	 * @param userId
	 *            ：用户ID。
	 * @param deviceType
	 *            ：采集器类型。
	 * @return 找到对应的 BthDeviceWorker 并移除成功则为true，否则为false。
	 */
	private boolean removeBthDeviceWorker(int userId, byte deviceType) {
		ConcurrentMap<Byte, BthDeviceWorker> workers = null;
		if (!this.mIsRunning
				|| (workers = this.mUserAndDevice.get(userId)) == null) {
			return false;
		}

		boolean result = workers.remove(deviceType) != null;

		// 如果该用户对应的所有采集器已经被移除，则将该用户从字典中移除
		if (workers.size() == 0) {
			Log.i("BthDeviceScheduler",
					"Clear the user's BthDeviceWorker!UserID is " + userId);
			this.mUserAndDevice.remove(userId);
		}
		return result;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		this.mIsRunning = false;

		if (this.mUserAndDevice != null) {
			for (ConcurrentMap<Byte, BthDeviceWorker> workers : this.mUserAndDevice
					.values()) {
				if (workers != null) {
					for (BthDeviceWorker worker : workers.values()) {
						try {
							worker.shutdown();
						} catch (Exception e) {
							Log.e("BthDeviceScheduler",
									"BthDeviceWorker failed to shutdown!", e);
						}
					}
				}
			}
		}
		this.mUserAndDevice = null;
	}
}