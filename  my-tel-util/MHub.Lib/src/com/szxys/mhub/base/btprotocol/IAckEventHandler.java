package com.szxys.mhub.base.btprotocol;

/**
 * receive protocol status of ACK
 * @author Administrator
 *
 */
interface IAckEventHandler
{
//	/**
//	 * when received a ACK status
//	 * @param aChannel
//	 * @param aActSN
//	 */
//	void onReceiveAck(Channel aAck);
	
	void onGetAck();
}
