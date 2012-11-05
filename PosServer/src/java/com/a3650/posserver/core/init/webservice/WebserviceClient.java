/***********************************************************************
 * Module:  WebserviceClient.java
 * Author:  Administrator
 * Purpose: Defines the Class WebserviceClient
 ***********************************************************************/

package com.a3650.posserver.core.init.webservice;

import com.a3650.posserver.core.init.Client;

/** @pdOid 3d3cf2aa-6237-4848-9742-f813b01bbcca */
public class WebserviceClient implements Client {

	@Override
	public byte[] receiveData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean returnData(byte[] buffer) {
		// TODO Auto-generated method stub
		return false;
	}
	public synchronized boolean close(){
		return false;
	}
}