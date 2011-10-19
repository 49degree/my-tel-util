package com.szxys.mhub.base.communication;

/**
 * 服务器通信对象抽象工厂。
 */
public abstract class AbstractWebHostFactory {
	/**
	 * 根据远程服务器终结点创建一个服务器通信对象。
	 * 
	 * @param remoteEndPoint
	 *            ：远程服务器终结点。
	 */
	public abstract IWebHost createWebHost(WebHostEndPoint remoteEndPoint);
}