package com.szxys.mhub.bizinterfaces;

/**
 * 子业务逻辑处理层与界面层交互数据的接口
 * 
 * @author 黄仕龙
 * 
 */

public interface ISubSystemCallBack {

	/**
	 * @param lMainCmd
	 *            主指令码
	 * @param lSubCmd
	 *            从指令码
	 * @param data
	 *            数据
	 * @param length
	 *            有效数据长度
	 * @return long
	 */
	public long onReceived(int mainCmd, int subCmd, byte[] data, int length);

	/**
	 * @param lMainCmd
	 *            主指令码
	 * @param lSubCmd
	 *            从指令码
	 * @param obj
	 *            数据对象
	 * 
	 * @return long
	 */
	public long onReceived(int mainCmd, int subCmd, Object obj);
}
