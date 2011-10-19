package com.szxys.mhub.interfaces;

/**
 * 平台子业务及设备类型等常量的定义
 * 
 * @author 黄仕龙
 * 
 */
public final class Platform {

	/**
	 * 具体业务编号
	 */
	/**
	 * 虚拟子业务，对应Web端的:基础平台 10
	 */
	public static final int SUBBIZ_VIRTUAL = 10;

	/**
	 * 心电监护 11
	 */
	public static final int SUBBIZ_ECG = 11;

	/**
	 * 排尿日记12
	 */
	public static final int SUBBIZ_METS = 12;

	/**
	 * 尿动力监测 13
	 */
	public static final int SUBBIZ_UFR = 13;

	/**
	 * 糖尿病随访 14
	 */
	public static final int SUBBIZ_DMFS = 14;

	/**
	 * 膀胱癌随访 15
	 */
	public static final int SUBBIZ_PFUS = 15;

	/**
	 * 前列腺炎随访 16
	 */
	public static final int SUBBIZ_PROSTATITISFU = 16;

	/**
	 * 前列腺增生随访 17 BPH(Benign Prostatic Hyperplasia)
	 */
	public static final int SUBBIZ_BPHFU = 17;

	/**
	 * 血压监护18
	 */
	public static final int SUBBIZ_ABPMS = 18;

	/**
	 * 睡眠监护 19
	 */
	public static final int SUBBIZ_APS = 19;

	/**
	 * 健康管理 20
	 */
	public static final int SUBBIZ_HM = 20;

	/**
	 * 健康档案 21
	 */
	public static final int SUBBIZ_HEALTHRECORD = 21;

	/**
	 * 胎心监护 22
	 */
	public static final int SUBBIZ_FETALHEART = 22;

	/**
	 * 血糖监护23
	 */
	public static final int SUBBIZ_RMBGMS = 23;

	/**
	 * 数据采集器类型定义（网络也作为一种数据采集器类定义）
	 */
	/**
	 * 服务器 XYS-WEB 0
	 */
	public static final byte DATATYPE_XYS_NETWORK = 0;
	/**
	 * 心电采集器 XYS-WRM 1
	 */
	public static final byte DATATYPE_XYS_WRM = 1;
	/**
	 * 尿量采集器 XYS-METS 2
	 */
	public static final byte DATATYPE_XYS_METS = 2;
	/**
	 * 尿流率采集器 XYS-UFR 3
	 */
	public static final byte DATATYPE_XYS_UFR = 3;
	/**
	 * 血糖采集器 XYS-BGLU 4
	 */
	public static final byte DATATYPE_XYS_BGLU = 4;
	/**
	 * 血氧采集器 XYS-BOXY 5
	 */
	public static final byte DATATYPE_XYS_BOXY = 5;
	/**
	 * 血压采集器 XYS-NBP 6
	 */
	public static final byte DATATYPE_XYS_NBP = 6;
	/**
	 * 尿比重采集器 XYS-NBZ 7
	 */
	public static final byte DATATYPE_XYS_NBZ = 7;
	/**
	 * 枕头采集器 XYS-PILLOW 9
	 */
	public static final byte DATATYPE_XYS_PILLOW = 9;

	/**
	 * 网络服务器通信数据优先级。
	 */
	/**
	 * 非实时数据。
	 */
	public static final int NETDATA_NONREALTIME = 0;

	/**
	 * 实时数据。
	 */
	public static final int NETDATA_REALTIME = 1;

	/**
	 * 紧急数据。
	 */
	public static final int NETDATA_EXIGENT = 2;

	/**
	 * Log Tag
	 */
	public static final String PFLOG_INFO = "Mhub Info";
	public static final String PFLOG_DEBUG = "Mhub Debug";
	public static final String PFLOG_WARNING = "Mhub Warning";
	public static final String PFLOG_ERROR = "Mhub Error";

	/**
	 * CMD
	 */
	/**
	 * 主动连接移动终端的采集器的设备发放指令。
	 */
	public static final int CMD_DEVICE_GRNAT = 300;

	/**
	 * Error Code
	 */
	/**
	 * 操作成功。
	 */
	public final static int SUCCEEDED = 100;

	/**
	 * 工作实例未运行或者已被销毁。
	 */
	public final static int ERROR_NO_RUNNING = 101;

	/**
	 * 无效的参数。
	 */
	public final static int ERROR_INVALID_PARAMETER = 102;

	/**
	 * 注册数据接收器失败。
	 */
	public final static int ERROR_START_DATA_RECEIVER_FAILED = 103;

	/**
	 * 注销数据接收器失败。
	 */
	public final static int ERROR_STOP_DATA_RECEIVER_FAILED = 104;

	/**
	 * 将数据提交至数据发送队列被中断。
	 */
	public final static int ERROR_INSERT_QUEUE_FAILED = 105;

	/**
	 * 将数据提交至数据发送队列失败（队列已满）。
	 */
	public final static int ERROR_QUEUE_OVERFLOW = 106;

	/**
	 * 发送数据失败。
	 */
	public final static int ERROR_SENT_FAILED = 107;

	/**
	 * 通信时找不到对应用户的 Token。
	 */
	public final static int ERROR_WEB_NO_USER_TOKEN = 108;
	
	/**
	 * 提示接收数据失败的消息。
	 */
	public final static int MSG_RECEIVED_FAILED = 200;
	
	/**
	 * 提示连接中断的消息。
	 */
	public final static int MSG_ABORT = 201;
}
