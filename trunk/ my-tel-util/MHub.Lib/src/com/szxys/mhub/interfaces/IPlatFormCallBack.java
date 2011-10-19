package com.szxys.mhub.interfaces;

/**
 * 子业务需要实现的接口，平台通过该接口的对象反馈数据到子业务
 * 
 * @author 黄仕龙
 * 
 */
public interface IPlatFormCallBack {

	/**
	 * @param reqIdentifying
	 * @param lErrorCode
	 * @return long
	 */
	public long onStarted(RequestIdentifying reqIdentifying, long lErrorCode);

	/**
	 * @param reqIdentifying
	 * @param lErrorCode
	 * @return long
	 */
	public long onStopped(RequestIdentifying reqIdentifying, long lErrorCode);

	/**
	 * @param reqIdentifying
	 * @param sendIndex
	 * @param lErrorCode
	 * @return long
	 */
	public long onSent(RequestIdentifying reqIdentifying, int sendIndex,
			long lErrorCode);

	/**
	 * 平台接收到蓝牙、网络数据后，通过该接口反馈给子业务
	 * 
	 * @param lChannel
	 * @param mainCmd
	 * @param subCmd
	 * @param byRecvData
	 * @param length
	 * @return long
	 */
	public long onReceived(int lChannel, int mainCmd, int subCmd,
			byte[] byRecvData, int length);

	/**
	 * 平台消息反馈接口，平台通过该接口通知子业务某些事件发生了
	 * 
	 * @param msgCode
	 *            消息编号
	 * @param mainCmd
	 *            标识
	 * @param subCmd
	 *            标识
	 * @param obj
	 *            消息相关数据对象
	 * 
	 * @return
	 */
	public long onMessage(int msgCode, int mainCmd, int subCmd, Object obj);
}
