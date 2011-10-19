package com.szxys.mhub.base.manager;

/**
 * 网络状态变化监听器。当网络状态发生变化的时候会触发相应的事件。
 * @author xak
 * @version 1.0
 */
public interface OnNetworkStatusChangeListener {

	/**
	 * 网络状态发生变化时调用
	 * 
	 * @param NetworkEvent
	 */
	public void onNetworkStatusChange(int NetworkEvent);

}