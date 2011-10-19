package com.szxys.mhub.base.communication;

/**
 * 服务器通信回调函数接口，由服务器通信实例提供给基础服务器通信模块。
 */
public interface IWebHostEventHandler {
	/**
	 * 连接远程服务器后执行的回调函数。
	 * 
	 * @param isSuccess
	 *            ：是否连接成功。
	 */
	public void onConnected(boolean isSuccess);

	/**
	 * 发送数据过程中出现异常的回调函数。
	 * 
	 * @param errorCode
	 *            ：错误码。
	 * @param errorMessage
	 *            ：错误信息。
	 */
	public void onError(int errorCode, String errorMessage);

	/**
	 * 基础服务器通信模块接收到数据后执行的回调函数。
	 * 
	 * @param subSystemID
	 *            ：业务ID。
	 * @param mainCmd
	 *            ：主码。
	 * @param subCmd
	 *            ：扩展码。
	 * @param data
	 *            ：数据。
	 */
	public void onReceived(int subSystemID, int mainCmd, int subCmd, byte[] data);
}