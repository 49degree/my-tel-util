package com.szxys.mhub.base.btdevice;


import android.content.Context;
import android.content.IntentFilter;

import com.szxys.mhub.base.btprotocol.BthLog;

/**
 * this class is a Singleton mode
 * @author Administrator
 *
 */
class BthPassiveModeListener
{
	public static boolean isListening()
	{
		return getListeningStatus();
	}
	//lazy load
	public static BthPassiveModeListener getInstance()
	{
		return InnerInstance.LISTENER;
	}
	private static class InnerInstance
	{
		public static BthPassiveModeListener LISTENER = new BthPassiveModeListener(5);
	}
	
	public synchronized void addEventHandler(String mac,IConnectEventHandler handler) throws Exception
	{
		if ( handler == null )
		{
			throw new NullPointerException("handler is null pointer");
		}
		
		pairingRequest.addHandler(mac,handler);
		serverListener.addHandler(mac,handler);
	}
	public synchronized void removeEventHandler(IConnectEventHandler handler)
	{
		pairingRequest.removeHandler(handler);
		serverListener.removeHandler(handler);
	}
	
	public synchronized boolean listen(Context aContext)
	{
		if ( getListeningStatus() == true )
			return true;
		
		if ( aContext == null )
		{
			BthLog.e(TAG,"the parameter of listen(Context aContext) is null");
			return false;
		}
		//dereference first
		fContext = null;
		//reset second
		fContext = aContext;
		
		//register to listen system broadcast:
		//"android.bluetooth.device.action.PAIRING_REQUEST"
		IntentFilter intentFilter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
		pairingRequest = BthPairingRequest.getInstance();
		fContext.registerReceiver(pairingRequest,intentFilter);
		
		//listen to server
		serverListener = BthServerListener.getInstance();
		if ( serverListener.isStarted() == false )
		{
			if ( serverListener.startServer() == false )
			{
				BthLog.e(TAG,"start server failed");
				return false;
			}
		}
		//server started
		
		setListeningStatus(true);
		
		return true;
	}	
	public synchronized void stopListen()
	{
		if ( getListeningStatus() == true )
		{
			setListeningStatus(false);
			//unregister
			fContext.unregisterReceiver(pairingRequest);
			fContext = null;
			
			serverListener.stopServer();			
		}
	}	
	private static synchronized void setListeningStatus(boolean aListening)
	{
		fListening = aListening;
	}
	private static synchronized boolean getListeningStatus()
	{
		return fListening;
	}
	
	private BthPassiveModeListener(int capacity)
	{
	}

	Context fContext;
	private BthPairingRequest pairingRequest;
	private BthServerListener serverListener;
	
	private final static String TAG = "bt.BthPassiveModeListener";
	private static boolean fListening = false;
}
