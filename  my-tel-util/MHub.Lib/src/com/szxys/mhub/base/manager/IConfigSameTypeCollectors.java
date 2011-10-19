package com.szxys.mhub.base.manager;

/**
 * 绑定多个同类型采集器接口。
 */
public interface IConfigSameTypeCollectors {
	/**
	 * 绑定多个同类型采集器。
	 * 
	 * @param mobile
	 *            ：待绑定的 Mobile 对象。
	 * @return boolean ：true表示绑定成功，false表示绑定失败。
	 */
	public boolean config(Mobile mobile);
}
