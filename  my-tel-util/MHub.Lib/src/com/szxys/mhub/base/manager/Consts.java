package com.szxys.mhub.base.manager;

/**
 * 平台内部定义的常量类。
 */
public class Consts {
	/************************* 与Web端通信协议指令 start ********************************/

	/**
	 * 与Web端通信协议：获取终端绑定信息。
	 */
	public static final int CMD_WEB_GET_BIND_INFO = 100;

	/**
	 * 与Web端通信协议：获取用户信息。
	 */
	public static final int CMD_WEB_GET_USER_INFO = 101;

	/**
	 * 与Web端通信协议：获取用户 Token。
	 */
	public static final int CMD_WEB_GET_USER_TOKEN = 102;

	/**
	 * 与Web端通信协议：注销用户 Token。
	 */
	public static final int CMD_WEB_DEL_USER_TOKEN = 103;

	/**
	 * 与Web端通信协议：获取升级信息。
	 */
	public static final int CMD_WEB_GET_UPDATE_INFO = 200;

	/**
	 * 与Web端通信协议：上传主诉。
	 */
	public static final int CMD_WEB_SEND_USER_MSG = 300;

	/**
	 * 与Web端通信协议：下载最新互动信息。
	 */
	public static final int CMD_WEB_GET_DOCTOR_MSG = 301;

	/**
	 * 与Web端通信协议：下载最新互动信息成功的通知指令。
	 */
	public static final int CMD_WEB_GET_DOCTOR_MSG_SUCCEEDED = 302;

	/**
	 * 与Web端通信协议：获取监护参数信息。
	 */
	public static final int CMD_WEB_GET_MONITORING_PARAMETERS = 400;

	/**
	 * 与Web端通信协议：获取服务器地址、端口号信息。
	 */
	public static final int CMD_WEB_GET_SERVER_ADRESS = 500;

	/************************* 与Web服务器通信协议指令 end **********************************/

	/************************* 服务器通信管理模块的命令 start ********************************/

	public static final int CONFIG_CMD_WEBHOST_BEGIN = 1000;
	public static final int CONFIG_CMD_WEBHOST_END = 1999;
	/**
	 * 服务器通信管理模块：保存用户 Token 的命令。
	 */
	public static final int CMD_WEBHOST_SAVE_TOKEN = 1000;

	/**
	 * 服务器通信管理模块：删除用户 Token 的命令。
	 */
	public static final int CMD_WEBHOST_DEL_TOKEN = 1001;

	/**
	 * 服务器通信管理模块：保存服务器终结点的命令。
	 */
	public static final int CMD_WEBHOST_SAVE_WEBHOST_ENDPOINT = 1002;

	/**
	 * 服务器通信管理模块：获取指定服务器通信实例的已发送的数据量的命令。
	 */
	public static final int CMD_WEBHOST_GET_TOTAL_SENT_DATA_AMOUNT = 1003;

	/**
	 * 服务器通信管理模块：获取指定服务器通信实例的已接收的数据量的命令。
	 */
	public static final int CMD_WEBHOST_GET_TOTAL_RECEIVED_DATA_AMOUNT = 1004;

	/************************* 服务器通信管理模块的命令 end **********************************/

	/************************* 蓝牙通信管理模块的命令 start ********************************/

	public static final int CONFIG_CMD_BTH_BEGIN = 2000;
	public static final int CONFIG_CMD_BTH_END = 2999;
	/**
	 * 获取指定蓝牙通信实例的已发送的数据量的命令。
	 */
	public static final int CMD_BTH_GET_TOTAL_SENT_DATA_AMOUNT = 2000;

	/**
	 * 获取指定蓝牙通信实例的已接收的数据量的命令。
	 */
	public static final int CMD_BTH_GET_TOTAL_RECEIVED_DATA_AMOUNT = 2001;

	/************************* 蓝牙通信管理模块的命令 end **********************************/
	
	/**
	 * 主服务器地址。
	 */
	public static final String MAIN_SERVER_ADRESS = "http://172.18.14.31:8888/Services/RpcService.ashx";
}
