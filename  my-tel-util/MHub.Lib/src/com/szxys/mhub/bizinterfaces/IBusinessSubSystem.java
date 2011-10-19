//Source file: E:\\Java\\Workspace\\RemoteHealthMonitoringPlatform\\src\\com\\szxys\\platfrom\\pfinterface\\IBusinessSubSystem.java

package com.szxys.mhub.bizinterfaces;

/**
 * 业务系统管理层调用各个子业务的接口 由具体子业务实现
 * 
 * @author 黄仕龙
 */
public interface IBusinessSubSystem {
	/**
	 * @return long
	 */
	public long start();

	/**
	 * @return long
	 */
	public long stop();

	/**
	 * @param ctrlID
	 * @param paramIn
	 * @param paramOut
	 * @return long
	 */
	public long control(int ctrlID, Object[] paramIn, Object[] paramOut);

	/**
	 * @return long
	 */
	public long getUserID();

	/**
	 * @return long
	 */
	public long getSubSystemType();
}
