package com.szxys.mhub.base.communication;

/**
 * 服务器通信接口，由服务器通信模块具体实现。
 */
public interface IWebHost {
	/**
	 * 获取服务器终结点信息。
	 */
	public WebHostEndPoint getRemoteEndPoint();

	/**
	 * 服务器通信实例开始运行。
	 */
	public void run();

	/**
	 * 服务器通信实例是否运行。
	 */
	public boolean isRunning();

	/**
	 * 服务器通信实例停止运行。
	 */
	public void shutdown();

	/**
	 * 向服务器发送数据。
	 * 
	 * @param data
	 *            ：数据。
	 */
	public boolean send(WebDataCell data);

	/**
	 * 设置服务器通信回调函数。
	 * 
	 * @param handler
	 *            ：服务器通信回调函数。
	 */
	public void setWebHostEventHandler(IWebHostEventHandler handler);
}