package com.szxys.mhub.base.communication;

/**
 * Action抽象类。
 */
public abstract class Action<TResult, T> {
	/**
	 * 执行方法。
	 * 
	 * @param param
	 *            ：参数。
	 */
	public abstract TResult action(T param);
}
