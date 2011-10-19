package com.szxys.mhub.base.btprotocol;

interface CommPolicy
{
	/**
	 * initialize all the works before communication
	 */
	boolean run();
	
	/**
	 * send raw data
	 * @param aSSID the type of subsystem
	 * @param aSendData raw data
	 * @param aChannel channel to used
	 * @return
	 */
	boolean postData(byte[] aSendData,int aChannel);
	
	/**
	 * stop communicate, and release all the resources
	 */
	void shutDown();
}
