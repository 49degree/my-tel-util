package com.szxys.mhub.base.communication;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.szxys.mhub.base.btdevice.BthCommStatus;
import com.szxys.mhub.base.btdevice.BthDeviceFactory;
import com.szxys.mhub.base.btdevice.BthTransportAttribute;
import com.szxys.mhub.base.btdevice.IBluetoothDevice;
import com.szxys.mhub.base.btdevice.IBthDeviceEventHandler;
import com.szxys.mhub.interfaces.DeviceConfig;
import com.szxys.mhub.interfaces.IPlatFormCallBack;
import com.szxys.mhub.interfaces.Platform;

import android.util.Log;

/**
 * 蓝牙通信工作类，每一个采集器对应一个实例，通过发送和接收线程，发送和处理接收到的数据。
 */
public class BthDeviceWorker {
	/**
	 * 蓝牙通信实例。
	 */
	private IBluetoothDevice mBthDevice;

	/**
	 * 采集器当前用户ID。
	 */
	private int mUserId;

	/**
	 * 采集器类型。
	 */
	private byte mDeviceType;

	/**
	 * 子业务回调函数字典。
	 */
	private ConcurrentMap<Integer, IPlatFormCallBack> mPlatFormCallBack;

	/**
	 * 发送数据队列。
	 */
	private BlockingQueue<Buffer> mSendQueue;

	/**
	 * 接收数据队列。
	 */
	private BlockingQueue<Buffer> mReceivedQueue;

	/**
	 * 发送数据线程。
	 */
	private Thread mSendDataThread;

	/**
	 * 接收数据线程。
	 */
	private Thread mReceiveDataThread;

	/**
	 * 蓝牙通信模块 run() 函数有结果后执行的回调。
	 */
	private Action<Boolean, Boolean> mRanAction = null;

	/**
	 * 发送、接收线程是否运行。
	 */
	private volatile boolean mIsRunning = false;

	/**
	 * 是否处于正在连接蓝牙设备状态。
	 */
	private volatile boolean mIsConnecting = false;

	/**
	 * 启动、结束运行的排它锁。
	 */
	private final Object mSwitchLock = new Object();

	/**
	 * 当前蓝牙通信实例已发送的数据量。
	 */
	private AtomicLong mTotalSentDataAmount;

	/**
	 * 当前蓝牙通信实例已接收的数据量。
	 */
	private AtomicLong mTotalReceivedDataAmount;

	/**
	 * 队列的最大容量。
	 */
	private final static int QUEUE_MAX_CAPACITY = 100;

	/**
	 * 当队列达到最大容量时，继续添加数据等待的时间（单位：毫秒），超过该时间则认为添加失败。
	 */
	private final static long INSERT_QUEUE_TIMEOUT = 1000;

	/**
	 * 尝试重连蓝牙设备的次数。
	 */
	private final static int TRY_CONNECT_COUNT = 3;

	/**
	 * 蓝牙通信工作类的构造函数，进行一系列初始化工作。
	 * 
	 * @param attr
	 *            ：采集器属性。
	 * @param userId
	 *            ：用户ID。
	 * @param deviceType
	 *            ：采集器类型。
	 */
	public BthDeviceWorker(BthTransportAttribute attr, int userId,
			byte deviceType) {
		// 初始化已发送和已接收的数据计数器
		this.mTotalSentDataAmount = new AtomicLong(0);
		this.mTotalReceivedDataAmount = new AtomicLong(0);

		// 初始化发送、接收队列
		this.mSendQueue = new ArrayBlockingQueue<Buffer>(QUEUE_MAX_CAPACITY);
		this.mReceivedQueue = new ArrayBlockingQueue<Buffer>(
				3 * QUEUE_MAX_CAPACITY);

		// 初始化子业务回调函数字典、当前用户ID、采集器类型
		this.mPlatFormCallBack = new ConcurrentHashMap<Integer, IPlatFormCallBack>();
		this.mUserId = userId;
		this.mDeviceType = deviceType;

		// 设置蓝牙通信实例
		this.mBthDevice = BthDeviceFactory.createBthDevice(attr,
				new BthDeviceEventHandlerImpl());
		if (this.mBthDevice == null) {
			throw new IllegalArgumentException(
					"Part of the parameters is invalid.");
		}
	}

	/**
	 * 获取当前蓝牙通信实例的已发送的数据量。
	 */
	public long getTotalSentDataAmount() {
		return this.mTotalSentDataAmount == null ? 0
				: this.mTotalSentDataAmount.get();
	}

	/**
	 * 获取当前蓝牙通信实例的已接收的数据量。
	 */
	public long getTotalReceivedDataAmount() {
		return this.mTotalReceivedDataAmount == null ? 0
				: this.mTotalReceivedDataAmount.get();
	}

	/**
	 * 获取当前蓝牙通信实例的设备信息。
	 */
	public DeviceConfig getDeviceConfig() {
		if (this.mBthDevice != null) {
			DeviceConfig config = new DeviceConfig();
			config.strDeviceAddr = this.mBthDevice.getMac();
			config.byDevType = this.mDeviceType;
			return config;
		} else {
			return null;
		}
	}

	/**
	 * 获取当前蓝牙通信实例的用户ID。
	 */
	public int getUserId() {
		return this.mUserId;
	}

	/**
	 * 获取所有的子业务回调函数。
	 */
	public ConcurrentMap<Integer, IPlatFormCallBack> getAllPlatFormCallBack() {
		return this.mPlatFormCallBack;
	}

	/**
	 * 获取外部蓝牙设备是否是被动模式。
	 */
	public boolean getPassiveMode() {
		return this.mBthDevice == null ? true : this.mBthDevice
				.getPassiveMode();
	}

	/**
	 * 添加子业务回调函数。
	 * 
	 * @param subSystemId
	 *            ：子系统的业务ID。
	 * @param platFormCallBack
	 *            ：回调函数。
	 */
	public boolean addPlatFormCallBack(int subSystemId,
			IPlatFormCallBack platFormCallBack) {
		if (this.mPlatFormCallBack != null) {
			this.mPlatFormCallBack.put(subSystemId, platFormCallBack);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 移除子业务回调函数。
	 * 
	 * @param subSystemId
	 *            ：子系统的业务ID。
	 * @return 已移除的回调函数，如果没有指定键的回调函数则返回 null。
	 */
	public IPlatFormCallBack removePlatFormCallBack(int subSystemId) {
		if (this.mPlatFormCallBack != null) {
			return this.mPlatFormCallBack.remove(subSystemId);
		} else {
			return null;
		}
	}

	/**
	 * 蓝牙通信开始工作。
	 * 
	 * @param ranAction
	 *            ：蓝牙通信模块run()函数出结果后执行的回调函数。
	 */
	public void run(Action<Boolean, Boolean> ranAction) {
		if (!this.mIsRunning) {
			synchronized (this.mSwitchLock) {
				if (!this.mIsRunning) {
					// 初始化发送线程
					this.mSendDataThread = new SendDataThread();

					// 初始化接收线程
					this.mReceiveDataThread = new ReceiveDataThread();

					this.mSendDataThread.setDaemon(true);
					this.mReceiveDataThread.setDaemon(true);

					// 初始化回调函数
					this.mRanAction = ranAction;

					this.mIsConnecting = true;
					this.mIsRunning = true;

					// 启动发送、接收线程
					this.mSendDataThread.start();
					this.mReceiveDataThread.start();

					this.mBthDevice.run();
				}
			}
		}
	}

	/**
	 * 当前蓝牙通信实例是否运行。
	 */
	public boolean isRunning() {
		return this.mIsRunning
				&& this.mBthDevice.getStatus() == BthCommStatus.STATUS_COMMUNICATING;
	}

	/**
	 * 蓝牙通信停止工作。
	 */
	public void shutdown() {
		if (this.mIsRunning) {
			synchronized (this.mSwitchLock) {
				if (this.mIsRunning) {
					this.mIsRunning = false;
					this.mIsConnecting = false;

					this.mBthDevice.shutDown();

					this.mRanAction = null;

					this.mSendDataThread = null;
					this.mReceiveDataThread = null;

					this.mSendQueue.clear();
					this.mReceivedQueue.clear();
				}
			}
		}
	}

	/**
	 * 将数据存入发送队列中。
	 * 
	 * @param data
	 *            ：数据。
	 * @throws InterruptedException
	 *             ：当队列达到最大容量时，等待过程发生中断所抛出的异常。
	 * @return 将数据成功存入发送队列为true，否则为false。
	 */
	public boolean send(Buffer data) throws InterruptedException {
		if (this.mIsRunning) {
			return this.mSendQueue.offer(data, INSERT_QUEUE_TIMEOUT,
					TimeUnit.MILLISECONDS);
		} else {
			return false;
		}
	}

	/**
	 * 通过当前蓝牙通信实例发送数据。
	 * 
	 * @param data
	 *            ：数据。
	 */
	private boolean sendInner(Buffer data) {
		Log.i("BthDeviceWorker", "Really send data!");

		boolean isSuccess = false;
		try {
			if (this.mIsRunning) {
				if (this.mBthDevice.postData(data.Data, data.Channel)) {
					this.mTotalSentDataAmount.addAndGet(data.Data == null ? 0
							: data.Data.length);
					isSuccess = true;
				} else {
					this.mIsConnecting = true;
					this.mBthDevice.run();
				}
			}
		} catch (Exception ex) {
			Log.e("BthDeviceWorker", "sendInner failed!", ex);
		}

		if (this.mPlatFormCallBack != null) {
			IPlatFormCallBack callback = this.mPlatFormCallBack
					.get(data.ReqIdentifying.subSystemID);
			if (callback != null) {
				callback.onSent(data.ReqIdentifying, data.SendIndex,
						isSuccess ? Platform.SUCCEEDED
								: Platform.ERROR_SENT_FAILED);
			}
		}

		return isSuccess;
	}

	/**
	 * 通过回调函数返回接收到的数据。
	 * 
	 * @param data
	 *            ：数据。
	 */
	private void receiveInner(Buffer data) {
		Log.i("BthDeviceWorker", "Receive data!");

		int len = data.Data != null ? data.Data.length : 0;
		this.mTotalReceivedDataAmount.addAndGet(len);
		if (this.mPlatFormCallBack != null) {
			for (IPlatFormCallBack callback : this.mPlatFormCallBack.values()) {
				callback.onReceived(data.Channel, -1, -1, data.Data, len);
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		this.shutdown();

		this.mBthDevice = null;
		this.mSendDataThread = null;
		this.mReceiveDataThread = null;
		this.mSendQueue = null;
		this.mReceivedQueue = null;
		this.mPlatFormCallBack = null;
		this.mTotalSentDataAmount = null;
		this.mTotalReceivedDataAmount = null;
	}

	/**
	 * 蓝牙通信回调函数接口实现。
	 */
	class BthDeviceEventHandlerImpl implements IBthDeviceEventHandler {
		private volatile int mCount = 0;

		@Override
		public void onError(int aError) {
			Log.i("BthDeviceWorker", "The error Code of 'onError' is " + aError);

			if (BthDeviceWorker.this.mIsRunning) {
				this.mCount++;
				if (this.mCount <= TRY_CONNECT_COUNT) {
					BthDeviceWorker.this.mIsConnecting = true;
					BthDeviceWorker.this.mBthDevice.run();
				} else {
					String msg;
					int errorCode;
					switch (aError) {
					case BthCommStatus.CONNECTION_ABORT:
						errorCode = Platform.MSG_ABORT;
						msg = "连接已中断，稍后将重新连接蓝牙设备。";
						break;
					case BthCommStatus.RECV_ERR:
						errorCode = Platform.MSG_RECEIVED_FAILED;
						msg = "接收数据时出现异常，连接已中断，稍后将重新连接蓝牙设备。";
						break;
					default:
						errorCode = Platform.MSG_ABORT;
						msg = "蓝牙通信时出现异常，连接已中断，稍后将重新连接蓝牙设备。";
						break;
					}

					this.mCount = 0;
					if (BthDeviceWorker.this.mPlatFormCallBack != null) {
						for (IPlatFormCallBack callback : BthDeviceWorker.this.mPlatFormCallBack
								.values()) {
							callback.onMessage(errorCode, -1, -1, msg);
						}
					}
				}
			}
		}

		@Override
		public void onReceived(byte[] aReceivedData, int aChannel) {
			if (BthDeviceWorker.this.mIsRunning) {
				try {
					BthDeviceWorker.this.mReceivedQueue.put(new Buffer(
							aChannel, aReceivedData));
				} catch (InterruptedException e) {
					Log.e("BthDeviceWorker",
							"Failed to insert data into the receive queue!", e);
				}
			}
		}

		@Override
		public void onRun(boolean aSuccess) {
			Log.i("BthDeviceWorker", "The result of 'onRun' is " + aSuccess);

			Action<Boolean, Boolean> action = BthDeviceWorker.this.mRanAction;
			if (action != null) {
				BthDeviceWorker.this.mRanAction = null;
				action.action(aSuccess);
			}

			if (aSuccess) {
				BthDeviceWorker.this.mIsConnecting = false;
				this.mCount = 0;
			} else if (BthDeviceWorker.this.mIsRunning) {
				this.mCount++;
				if (this.mCount <= TRY_CONNECT_COUNT) {
					BthDeviceWorker.this.mIsConnecting = true;
					BthDeviceWorker.this.mBthDevice.run();
				} else {
					this.mCount = 0;
					if (BthDeviceWorker.this.mPlatFormCallBack != null) {
						for (IPlatFormCallBack callback : BthDeviceWorker.this.mPlatFormCallBack
								.values()) {
							callback.onMessage(Platform.MSG_ABORT, -1, -1,
									"与外部蓝牙采集器建立连接失败！");
						}
					}
				}
			}
		}
	}

	/**
	 * 发送数据线程。
	 */
	class SendDataThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (BthDeviceWorker.this.mIsRunning) {
				try {
					Buffer data = null;
					if (!BthDeviceWorker.this.mIsConnecting
							&& (data = BthDeviceWorker.this.mSendQueue.poll()) != null) {
						if (sendInner(data)) {
							continue;
						}
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					Log.e("BthDeviceWorker", "SendDataThread failed!", e);
				}
			}
		}
	}

	/**
	 * 接收数据线程。
	 */
	class ReceiveDataThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (BthDeviceWorker.this.mIsRunning) {
				try {
					Buffer data = null;
					if ((data = BthDeviceWorker.this.mReceivedQueue.poll()) != null) {
						receiveInner(data);
					} else {
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					Log.e("BthDeviceWorker", "ReceiveDataThread failed!", e);
				}
			}
		}
	}
}