package com.szxys.mhub.base.communication;

import com.szxys.mhub.interfaces.RequestIdentifying;

/**
 * 蓝牙通信数据包。
 */
public class Buffer {
	/**
	 * 请求标识。
	 */
	public RequestIdentifying ReqIdentifying;

	/**
	 * 发送编号。
	 */
	public int SendIndex;

	/**
	 * 通道号。
	 */
	public int Channel;

	/**
	 * 数据。
	 */
	public byte[] Data;

	/**
	 * 初始化蓝牙通信数据包。
	 */
	public Buffer() {

	}

	/**
	 * 初始化蓝牙通信数据包。
	 * 
	 * @param channel
	 *            ：通道号。
	 * @param data
	 *            ：数据。
	 */
	public Buffer(int channel, byte[] data) {
		this(null, -1, channel, data);
	}

	/**
	 * 初始化蓝牙通信数据包。
	 * 
	 * @param reqIdentifying
	 *            ：请求标识。
	 * @param sendIndex
	 *            ：发送编号。
	 * @param channel
	 *            ：通道号。
	 * @param data
	 *            ：数据。
	 */
	public Buffer(RequestIdentifying reqIdentifying, int sendIndex,
			int channel, byte[] data) {
		this.ReqIdentifying = reqIdentifying;
		this.SendIndex = sendIndex;
		this.Channel = channel;
		this.Data = data;
	}
}