package com.szxys.mhub.base.communication;

import com.szxys.mhub.interfaces.DeviceConfig;
import com.szxys.mhub.interfaces.IPlatFormCallBack;
import com.szxys.mhub.interfaces.RequestIdentifying;

public interface ICommunication {
	/**
	 * 添加数据接收器。 如果是第一次添加对应的数据接收器，则创建通信实例后运行该实例。
	 * 
	 * @param reqIdentifying
	 *            ：数据接收器对应的用户、子业务系统标识。
	 * @param dataReceiver
	 *            ：数据接收器。
	 * @return 1：表示操作成功，0：表示操作失败。
	 */
	public long addDataReceiver(RequestIdentifying reqIdentifying,
			IPlatFormCallBack dataReceiver);

	/**
	 * 移除数据接收器。 如果对应的数据接收器集合已为空，则停止该通信实例工作。
	 * 
	 * @param reqIdentifying
	 *            ：数据接收器对应的用户、子业务系统标识。
	 * @return Platform.SUCCEEDED： 子业务注销自己所关心的数据接收器，关闭数据处理对象成功。
	 *         Platform.ERROR_STOP_DATA_RECEIVER_FAILED：
	 *         子业务注销数据接收器失败（无效参数，找不到对应的数据接收器，检查参数）。
	 *         Platform.ERROR_INVALID_PARAMETER：
	 *         通信时工作实例为NULL（无效参数，找不到对应的工作实例，检查参数）。
	 */
	public long removeDataReceiver(RequestIdentifying reqIdentifying);

	/**
	 * 发送数据（只是将数据提交至发送队列中）。
	 * 
	 * @param reqIdentifying
	 *            ： 请求指定的子业务标识数据。
	 * @param sendIndex
	 *            ： 发送的数据包编号。
	 * @param channel
	 *            ： 蓝牙通信时为指定的通道，服务器通信时为数据优先级。
	 * @param mainCmd
	 *            ： 主控指令。
	 * @param subCmd
	 *            ： 子指令。
	 * @param bySendData
	 *            ： 数据。
	 * @param length
	 *            ： 数据有效长度。
	 * @return 1：表示操作成功，0：表示操作失败。
	 */
	public long send(RequestIdentifying reqIdentifying, int sendIndex,
			int channel, int mainCmd, int subCmd, byte[] bySendData, int length);

	/**
	 * 提交或撤销循环发送数据任务（蓝牙通信暂未使用），当时间间隔为 0 时表示撤销任务。
	 * 
	 * @param reqIdentifying
	 *            ：请求标识。
	 * @param spaceTime
	 *            ：循环发送时间间隔（单位：秒）。
	 * @param mainCmd
	 *            ：主码。
	 * @param subCmd
	 *            ：扩展码。
	 * @param bySendData
	 *            ：数据。
	 * @param length
	 *            ：数据有效长度。
	 * @return 1：表示操作成功，0：表示操作失败。
	 */
	public long repeatSend(RequestIdentifying reqIdentifying, int spaceTime,
			int mainCmd, int subCmd, byte[] bySendData, int length);

	/**
	 * 把指定的文件下载指定的文件下载到指定位置，蓝牙通信暂未使用。
	 * 
	 * @param url
	 *            ： 需要下载的文件具体的URL地址，譬如：ftp://1.2.3.4/download/text.pdf
	 * 
	 * @param savepath
	 *            ： 下载下来文件的保存路径，譬如：/SDCard/download/
	 * @param user
	 *            ： 下载帐号
	 * @param pwd
	 *            ： 下载密码
	 * @return 1：表示下载成功，0：表示下载失败。
	 */
	public long download(String url, String savepath, String user, String pwd);

	/**
	 * 获得指定用户、子业务系统的使用设备的设备信息（服务器通信为 URL或IP和Port，蓝牙通信则为采集器类型和蓝牙地址）。
	 * 
	 * @param userId
	 *            ：用户ID。
	 * @param subSystemId
	 *            ：子系统的业务ID。
	 * @return 设备信息数组，找不到则返回 null 。
	 */
	public DeviceConfig[] getDeviceConfig(int userId, int subSystemId);

	/**
	 * 获取指定用户、子业务系统已经启动的数据接收器所关联的设备信息（服务器通信为 URL或IP和Port，蓝牙通信则为采集器类型和蓝牙地址）。
	 * 
	 * @param userId
	 *            ：用户ID。
	 * @param subSystemId
	 *            ：子系统的业务ID。
	 * @return 设备信息数组，找不到则返回 null 。
	 */
	public DeviceConfig[] getRegisterDeviceConfig(int userId, int subSystemId);

	/**
	 * 根据 ctrlID 定义实际的操作。
	 * 
	 * @param userID
	 *            ：用户ID。
	 * @param subSystemID
	 *            ：子系统业务码。
	 * @param ctrlID
	 *            ：操作码，1000~1999为服务器通信管理模块的命令，2000~2999为蓝牙通信管理模块的命令，具体定义参见
	 *            Consts.java 类。
	 * @param paramIn
	 *            ：传入参数。
	 * @param paramOut
	 *            ：输出参数。
	 * @return 1：表示操作成功，0：表示操作失败。
	 */
	public long control(int userID, int subSystemID, int ctrlID,
			Object[] paramIn, Object[] paramOut);

	/**
	 * 清空所有的工作实例。
	 * 
	 * @return 1：表示清空成功，0：表示清空失败。
	 */
	public long clear();
}
