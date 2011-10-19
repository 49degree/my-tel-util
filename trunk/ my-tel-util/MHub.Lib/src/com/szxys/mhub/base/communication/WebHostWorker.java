package com.szxys.mhub.base.communication;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import android.util.Log;

import com.szxys.mhub.interfaces.DeviceConfig;
import com.szxys.mhub.interfaces.IPlatFormCallBack;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.interfaces.RequestIdentifying;

/**
 * 服务器通信工作类，每一个服务器连接点对应一个实例，通过发送和接收线程，发送和处理接收到的数据。
 */
class WebHostWorker {
	/**
	 * 服务器通信实例。
	 */
	private IWebHost mWebHost;

	/**
	 * 子业务回调函数字典。
	 */
	private ConcurrentMap<Integer, IPlatFormCallBack> mPlatFormCallBack;

	/**
	 * 紧急发送数据队列。
	 */
	private BlockingQueue<WebDataCell> mEmergentQueue;

	/**
	 * 实时发送数据队列。
	 */
	private BlockingQueue<WebDataCell> mRealTimeQueue;

	/**
	 * 非实时发送数据队列。
	 */
	private BlockingQueue<WebDataCell> mNonRealTimeQueue;

	/**
	 * 接收数据队列。
	 */
	private BlockingQueue<WebDataCell> mReceivedQueue;

	/**
	 * 发送数据线程。
	 */
	private Thread mSendDataThread;

	/**
	 * 发送紧急数据线程。
	 */
	private Thread mSendEmergentDataThread;

	/**
	 * 接收数据线程。
	 */
	private Thread mReceiveDataThread;

	/**
	 * 服务器通信模块 run() 函数有结果后执行的回调。
	 */
	private Action<Boolean, Boolean> mRanAction = null;

	/**
	 * 发送、接收线程是否运行。
	 */
	private volatile boolean mIsRunning = false;

	/**
	 * 是否处于正在连接服务器状态。
	 */
	private volatile boolean mIsConnecting = false;

	/**
	 * 启动、结束运行的排它锁。
	 */
	private final Object mSwitchLock = new Object();

	/**
	 * 当前服务器通信实例已发送的数据量。
	 */
	private AtomicLong mTotalSentDataAmount;

	/**
	 * 当前服务器通信实例已接收的数据量。
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
	 * 尝试重新连接服务器的次数。
	 */
	private final static int TRY_CONNECT_COUNT = 3;

	/**
	 * 服务器通信工作类的构造函数，进行一系列初始化工作。
	 * 
	 * @param webHost
	 *            ：服务器通信实例。
	 */
	public WebHostWorker(IWebHost webHost) {
		// 初始化已发送和已接收的数据计数器
		this.mTotalSentDataAmount = new AtomicLong(0);
		this.mTotalReceivedDataAmount = new AtomicLong(0);

		// 初始化发送、接收队列
		this.mEmergentQueue = new ArrayBlockingQueue<WebDataCell>(
				QUEUE_MAX_CAPACITY);
		this.mRealTimeQueue = new ArrayBlockingQueue<WebDataCell>(
				QUEUE_MAX_CAPACITY);
		this.mNonRealTimeQueue = new ArrayBlockingQueue<WebDataCell>(
				QUEUE_MAX_CAPACITY);
		this.mReceivedQueue = new ArrayBlockingQueue<WebDataCell>(
				3 * QUEUE_MAX_CAPACITY);

		// 初始化子业务回调函数字典
		this.mPlatFormCallBack = new ConcurrentHashMap<Integer, IPlatFormCallBack>();

		// 设置服务器通信实例
		this.mWebHost = webHost;
		this.mWebHost.setWebHostEventHandler(new WebHostEventHandlerImpl());
	}

	/**
	 * 获取当前服务器通信实例的已发送的数据量。
	 */
	public long getTotalSentDataAmount() {
		return this.mTotalSentDataAmount == null ? 0
				: this.mTotalSentDataAmount.get();
	}

	/**
	 * 获取当前服务器通信实例的已接收的数据量。
	 */
	public long getTotalReceivedDataAmount() {
		return this.mTotalReceivedDataAmount == null ? 0
				: this.mTotalReceivedDataAmount.get();
	}

	/**
	 * 获取当前服务器通信实例的终结点信息。
	 */
	public DeviceConfig getWebConfig() {
		if (this.mWebHost != null) {
			WebHostEndPoint ep = this.mWebHost.getRemoteEndPoint();

			DeviceConfig config = new DeviceConfig();
			config.byDevType = Platform.DATATYPE_XYS_NETWORK;
			config.strDeviceAddr = ep.Adress;
			config.nNetServicePort = ep.Port;
			return config;
		} else {
			return null;
		}
	}

	/**
	 * 获取所有的子业务回调函数。
	 */
	public ConcurrentMap<Integer, IPlatFormCallBack> getAllPlatFormCallBack() {
		return this.mPlatFormCallBack;
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
	 * 服务器通信开始工作。
	 * 
	 * @param ranAction
	 *            ：服务器通信模块run()函数出结果后执行的回调函数。
	 */
	public void run(Action<Boolean, Boolean> ranAction) {
		if (!this.mIsRunning) {
			synchronized (this.mSwitchLock) {
				if (!this.mIsRunning) {
					// 初始化发送线程
					this.mSendDataThread = new SendDataThread();
					this.mSendEmergentDataThread = new SendEmergentDataThread();

					// 初始化接收线程
					this.mReceiveDataThread = new ReceiveDataThread();

					this.mSendDataThread.setDaemon(true);
					this.mSendEmergentDataThread.setDaemon(true);
					this.mReceiveDataThread.setDaemon(true);

					// 初始化回调函数
					this.mRanAction = ranAction;

					this.mIsConnecting = true;
					this.mIsRunning = true;

					// 启动发送、接收线程
					this.mSendDataThread.start();
					this.mSendEmergentDataThread.start();
					this.mReceiveDataThread.start();

					this.mWebHost.run();
				}
			}
		}
	}

	/**
	 * 当前服务器通信实例是否运行。
	 */
	public boolean isRunning() {
		return this.mIsRunning && this.mWebHost.isRunning();
	}

	/**
	 * 服务器通信停止工作。
	 */
	public void shutdown() {
		if (this.mIsRunning) {
			synchronized (this.mSwitchLock) {
				if (this.mIsRunning) {
					this.mIsRunning = false;
					this.mIsConnecting = false;

					this.mWebHost.shutdown();

					this.mRanAction = null;

					this.mSendDataThread = null;
					this.mSendEmergentDataThread = null;
					this.mReceiveDataThread = null;

					this.mEmergentQueue.clear();
					this.mRealTimeQueue.clear();
					this.mNonRealTimeQueue.clear();
					this.mReceivedQueue.clear();
				}
			}
		}
	}

	/**
	 * 将数据根据优先级存入发送队列中。
	 * 
	 * @param data
	 *            ：数据。
	 * @throws InterruptedException
	 *             ：当队列达到最大容量时，等待过程发生中断所抛出的异常。
	 * @return 将数据成功存入发送队列为true，否则为false。
	 */
	public boolean send(WebDataCell data) throws InterruptedException {
		if (this.mIsRunning) {
			switch (data.Priority) {
			case Platform.NETDATA_EXIGENT:
				return this.mEmergentQueue.offer(data, INSERT_QUEUE_TIMEOUT,
						TimeUnit.MILLISECONDS);
			case Platform.NETDATA_REALTIME:
				return this.mRealTimeQueue.offer(data, INSERT_QUEUE_TIMEOUT,
						TimeUnit.MILLISECONDS);
			case Platform.NETDATA_NONREALTIME:
				return this.mNonRealTimeQueue.offer(data, INSERT_QUEUE_TIMEOUT,
						TimeUnit.MILLISECONDS);
			default:
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 通过当前服务器通信实例发送数据。
	 * 
	 * @param data
	 *            ：数据。
	 */
	private boolean sendInner(WebDataCell data) {
		Log.i("WebHostWorker", "Really send data!");

		boolean isSuccess = false;
		try {
			if (this.mIsRunning) {
				if (this.mWebHost.send(data)) {
					this.mTotalSentDataAmount.addAndGet(data.Data == null ? 0
							: data.Data.length);
					isSuccess = true;
				} else {
					this.mIsConnecting = true;
					this.mWebHost.run();
				}
			}
		} catch (Exception ex) {
			Log.e("WebHostWorker", "Failed to send data!", ex);
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
	private void receiveInner(WebDataCell data) {
		Log.i("WebHostWorker", "Receive data!");

		int len = data.Data == null ? 0 : data.Data.length;
		this.mTotalReceivedDataAmount.addAndGet(len);
		if (this.mPlatFormCallBack != null) {
			IPlatFormCallBack callback = this.mPlatFormCallBack
					.get(data.ReqIdentifying.subSystemID);
			if (callback != null) {
				callback.onReceived(-1, data.MainCmd, data.SubCmd, data.Data,
						len);
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		this.shutdown();

		this.mWebHost = null;
		this.mSendDataThread = null;
		this.mSendEmergentDataThread = null;
		this.mReceiveDataThread = null;
		this.mEmergentQueue = null;
		this.mRealTimeQueue = null;
		this.mNonRealTimeQueue = null;
		this.mReceivedQueue = null;
		this.mPlatFormCallBack = null;
		this.mTotalSentDataAmount = null;
		this.mTotalReceivedDataAmount = null;
	}

	/**
	 * 服务器通信回调函数接口实现。
	 */
	class WebHostEventHandlerImpl implements IWebHostEventHandler {
		private volatile int mCount = 0;

		@Override
		public void onError(int errorCode, String errorMessage) {
			Log.i("WebHostWorker", "The error Code of 'onError' is "
					+ errorCode);

			if (WebHostWorker.this.mIsRunning) {
				this.mCount++;
				if (this.mCount <= TRY_CONNECT_COUNT) {
					WebHostWorker.this.mIsConnecting = true;
					WebHostWorker.this.mWebHost.run();
				} else {
					this.mCount = 0;
					if (WebHostWorker.this.mPlatFormCallBack != null) {
						for (IPlatFormCallBack callback : WebHostWorker.this.mPlatFormCallBack
								.values()) {
							callback.onMessage(errorCode, -1, -1, errorMessage);
						}
					}
				}
			}
		}

		@Override
		public void onConnected(boolean isSuccess) {
			Log.i("WebHostWorker", "The result of 'onConnected' is "
					+ isSuccess);

			Action<Boolean, Boolean> action = WebHostWorker.this.mRanAction;
			if (action != null) {
				WebHostWorker.this.mRanAction = null;
				action.action(isSuccess);
			}

			if (isSuccess) {
				WebHostWorker.this.mIsConnecting = false;
				this.mCount = 0;
			} else if (WebHostWorker.this.mIsRunning) {
				this.mCount++;
				if (this.mCount <= TRY_CONNECT_COUNT) {
					WebHostWorker.this.mIsConnecting = true;
					WebHostWorker.this.mWebHost.run();
				} else {
					this.mCount = 0;
					if (WebHostWorker.this.mPlatFormCallBack != null) {
						for (IPlatFormCallBack callback : WebHostWorker.this.mPlatFormCallBack
								.values()) {
							callback.onMessage(Platform.MSG_ABORT, -1, -1,
									"与服务器建立连接失败！");
						}
					}
				}
			}
		}

		@Override
		public void onReceived(int subSystemID, int mainCmd, int subCmd,
				byte[] data) {
			if (WebHostWorker.this.mIsRunning) {
				try {
					WebDataCell dataCell = new WebDataCell();
					dataCell.ReqIdentifying = new RequestIdentifying();
					dataCell.ReqIdentifying.subSystemID = subSystemID;
					dataCell.MainCmd = mainCmd;
					dataCell.SubCmd = subCmd;
					dataCell.Data = data;

					WebHostWorker.this.mReceivedQueue.put(dataCell);
				} catch (InterruptedException e) {
					Log.e("WebHostWorker",
							"Failed to insert data into the receive queue!", e);
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
			while (WebHostWorker.this.mIsRunning) {
				try {
					WebDataCell data = null;
					if (!WebHostWorker.this.mIsConnecting
							&& WebHostWorker.this.mEmergentQueue.size() == 0) {
						if ((data = WebHostWorker.this.mRealTimeQueue.poll()) != null) {
							if (sendInner(data)) {
								continue;
							}
						} else if ((data = WebHostWorker.this.mNonRealTimeQueue
								.poll()) != null) {
							if (sendInner(data)) {
								continue;
							}
						}
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					Log.e("WebHostWorker", "SendDataThread failed!", e);
				}
			}
		}
	}

	/**
	 * 发送紧急数据线程。
	 */
	class SendEmergentDataThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (WebHostWorker.this.mIsRunning) {
				try {
					WebDataCell data = null;
					if (!WebHostWorker.this.mIsConnecting
							&& (data = WebHostWorker.this.mEmergentQueue.poll()) != null) {
						if (sendInner(data)) {
							continue;
						}
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					Log.e("WebHostWorker", "SendEmergentDataThread failed!", e);
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
			while (WebHostWorker.this.mIsRunning) {
				try {
					WebDataCell data = null;
					if ((data = WebHostWorker.this.mReceivedQueue.poll()) != null) {
						receiveInner(data);
					} else {
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					Log.e("WebHostWorker", "ReceiveDataThread failed!", e);
				}
			}
		}
	}
}