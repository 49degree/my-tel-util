package com.szxys.mhub.interfaces;

import java.util.List;

import android.database.Cursor;

/**
 * 
 * 平台暴露给子业务的接口， 子业务通过该接口通知平台进行相应的操作， 通过该接口从平台获取相应数据
 * 该接口由平台实现，由平台生成单例对象，并设置给子业务，由子业务调用子业务需要哪类数据，就去“连接”哪个服务器取哪类数据，具体连接由通讯管理层控制
 * 
 * @author 黄仕龙
 */

public interface IPlatFormInterface {

	/**
	 * 获取所有用户的轻量级信息。
	 */
	public List<LightUser> getAllLightUser();

	/**
	 * 获取指定用户的所有信息。
	 * 
	 * @param userId
	 *            ：用户编码。
	 */
	public User getUser(int userId);

	/**
	 * 
	 * 子业务间数据共享通用接口
	 * 
	 * @param subSystemID
	 *            所请求的子业务ID
	 * @param lDataIdentifying
	 *            数据标识符
	 * @param listString
	 *            数据标识值
	 * @return Cursor
	 */
	public Cursor getDBSharedData(int subSystemID, int dataIdentifying,
			List<String> listString);

	/**
	 * 获取子业务文件存放路径
	 * 
	 * @param subSystemID
	 * @return String
	 */
	public String getFileSavePath(int subSystemID);

	/**
	 * 获得指定用户所关联的所有蓝牙设备信息以及指定业务所使用的网络服务器地址
	 * 
	 * @param userID
	 * @param subSystemID
	 * @return DeviceConfig[]
	 */
	public DeviceConfig[] getDeviceConfig(int userID, int subSystemID);

	/**
	 * 获取指定用户当前已经注册数据接收器的设备（也就是已经启动连接的设备）
	 * 
	 * @param userID
	 * @param subSystemID
	 * @return com.szxys.platfrom.pfinterface.DeviceConfig[]
	 */
	public DeviceConfig[] getRegisterDeviceInfo(int userID, int subSystemID);

	/**
	 * 注册蓝牙、网络数据之外的数据接收器。当平台有除蓝牙、网络相关数据之外的数据、
	 * 
	 * 消息需要知会业务的时候，通过该回调对象传递数据给业务
	 * 
	 * @param userID
	 *            用户ID
	 * @param subSystemID
	 *            业务类型
	 * @param dataReciver
	 *            业务相关消息接收器
	 */
	public long registerPlatformDataReciver(int userID, int subSystemID,
			IPlatFormCallBack dataReciver);

	/**
	 * 子业务注册自己所关心的数据接收器，启动数据处理对象，具体调度及实现由蓝牙通讯管理、服务器通讯管理模块实现。 网络数据需要调用一次，
	 * 想获取多少个采集器的数据就调用多少次。
	 * 
	 * @param reqIdentifying
	 *            子业务相关标识
	 * @param dataReciver
	 *            业务数据接收器
	 * @return long
	 */
	public long startDataReceiver(RequestIdentifying reqIdentifying,
			IPlatFormCallBack dataReciver);

	/**
	 * 子业务取消自己所关心的数据接收器，关闭数据处理对象，具体调度及实现由蓝牙通讯管理、服务器通讯管理模块实现。
	 * 调用次数需要与startDataReceiver次数相同。
	 * 
	 * @param reqIdentifying
	 *            子业务相关标识，与startDataReceiver()调用时使用的数据一致
	 * @return long
	 */
	public long stopDataReceiver(RequestIdentifying reqIdentifying);

	/**
	 * 
	 * 通过指定的通讯链路进行数据发送
	 * 
	 * @param reqIdentifying
	 *            请求指定的子业务标识数据
	 * @param lSendIndex
	 *            发送的数据包编号
	 * @param lChannel
	 * @param lMainCmd
	 *            主控指令
	 * @param lSubCmd
	 *            子指令
	 * @param bySendData
	 *            需要发送的数据
	 * @param length
	 *            需要发送数据的长度
	 * @return long
	 */
	public long send(RequestIdentifying reqIdentifying, int sendIndex,
			int channel, int mainCmd, int subCmd, byte[] bySendData, int length);

	/**
	 * @param reqIdentifying
	 * @param spaceTime
	 * @param mainCmd
	 * @param subCmd
	 * @param bySendData
	 * @param length
	 * @return long
	 */
	public long repeatSend(RequestIdentifying reqIdentifying, int spaceTime,
			int mainCmd, int subCmd, byte[] bySendData, int length);

	/**
	 * 把指定的文件下载指定的文件下载到指定位置
	 * 
	 * @param url
	 *            需要下载的文件具体的URL地址，譬如：ftp://1.2.3.4/download/text.pdf
	 * 
	 * @param savepath
	 *            下载下来文件的保存路径，譬如：/SDCard/download/
	 * @param user
	 *            下载帐号
	 * @param pwd
	 *            下载密码
	 * @return long
	 */
	public long download(String url, String savepath, String user, String pwd);

	/**
	 * 子业务传递一些配置信息到平台，目前暂时未使用
	 * 
	 * @param userID
	 * @param subSystemID
	 * @param ctrlID
	 * @param paramIn
	 * @param paramOut
	 * @return long
	 */
	public long config(int userID, int subSystemID, int ctrlID,
			Object[] paramIn, Object[] paramOut);

	/**
	 * 检查指定用户及子业务的监护参数是否存在变更
	 * 
	 * @param userID
	 *            指定用户ID
	 * @param subSystemID
	 *            指定的子业务ID
	 * @return
	 * 
	 *         true : 存在变更，需要子业务去获取新的监护参数
	 * 
	 *         false: 监护参数没有变更
	 * 
	 */
	public boolean checkParameterMonitor(int userID, int subSystemID);

}
