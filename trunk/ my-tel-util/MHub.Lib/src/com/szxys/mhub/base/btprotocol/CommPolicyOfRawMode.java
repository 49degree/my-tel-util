package com.szxys.mhub.base.btprotocol;

import java.io.IOException;
import java.io.InputStream;

import com.szxys.mhub.base.btdevice.BthTransportAttribute;

import android.bluetooth.BluetoothSocket;

class CommPolicyOfRawMode implements CommPolicy
{
	public CommPolicyOfRawMode(BthTransportAttribute aBthTransAttribute,IProtocolInformHandler aInformHandler,
			BluetoothSocket aBtSocket) throws Exception
	{
		if ( aInformHandler == null || aBtSocket == null || aBthTransAttribute.fNumOfChannels != 0 )
		{
			BthLog.e(TAG, "at least one parameter is invalid");
			throw new Exception("at least one parameter is invalid");
		}

		fAttributes = new CommAttributes();
		fAttributes.fInformHandler = aInformHandler;
		fAttributes.fBtSocket = aBtSocket;
		fAttributes.fInputStream = aBtSocket.getInputStream();
		fAttributes.fOutputStream = aBtSocket.getOutputStream();
		fAttributes.fTransportAttribute = aBthTransAttribute;

		setRunning(false);
	}

	@Override
	public synchronized boolean run()
	{
		if ( isRunning() == false )
		{			
			fBthDataOutputStream = new BthDataWithoutACKOutputStream(fAttributes.fOutputStream);
			fReceiver = new RawModeReceiver(fAttributes.fInputStream);
			new Thread(fReceiver,TAG+" : receiver").start();
			setRunning(true);
		}
		
		return true;
	}

	@Override
	public synchronized boolean postData(byte[] aSendData, int aChannel)
	{
		if ( isRunning() == false )
			return false;
		
		return fBthDataOutputStream.send(aSendData);
	}

	@Override
	public synchronized void shutDown()
	{	
		if ( isRunning() == true )
		{
			setRunning(false);
			fReceiver.cancel();
			try
			{
				fAttributes.fBtSocket.close();
			}
			catch (IOException exception)
			{
				exception.printStackTrace();
			}
			
			fBthDataOutputStream.releaseResource();
			
			fBthDataOutputStream = null;
			fReceiver = null;
		}
	}
	
	private synchronized void processReceiveError(boolean aIsCancelled)
	{
		if ( aIsCancelled == false )//receive error
		{
			shutDown();
			fAttributes.fInformHandler.inform(IProtocolInformHandler.E_RECEIVE_ERROR);
			BthLog.e(TAG,"receiver thread exit with exception");
		}
		else
		{
			BthLog.e(TAG,"receiver thread exit normally");
		}
	}
	
	private synchronized boolean isRunning()
	{
		return fIsRunning;
	}
	private synchronized void setRunning(boolean aIsRunning)
	{
		fIsRunning = aIsRunning;
	}
	private boolean fIsRunning;
	
	private final static String TAG = "bt.BthProCommRawMode";
	private CommAttributes fAttributes;
	private IBthDataOutputStream fBthDataOutputStream;
	
	private RawModeReceiver fReceiver;
	
	//inner class
	private class RawModeReceiver implements Runnable
	{
		public RawModeReceiver(InputStream aBtStream)
		{
			fCancelled = false;
			fBtSocketInputStream = aBtStream;
		}

		@Override
		public void run()
		{
			int bufLength = 4096;
			byte[] readBuf = new byte[bufLength];
			int realRead = 0;
			byte[] temp = null;
			while ( fCancelled == false )
			{
				try
				{
					realRead = fBtSocketInputStream.read(readBuf,0,bufLength);
					if ( realRead > 0 )
					{
						temp = new byte[realRead];
						System.arraycopy(readBuf,0,temp,0,realRead);
						fAttributes.fInformHandler.onDataReceived(temp,-1);
					}
				}
				catch (Exception exception)
				{
					if ( fCancelled == false )
					{
						exception.printStackTrace();
					}
					CommPolicyOfRawMode.this.processReceiveError(fCancelled);
					break;//exit the thread
				}
			}
		}
		
		public void cancel()
		{
			fCancelled = true;
		}
		
		// a sign : when stop the thread, set this to true
		private boolean fCancelled;
		// use the stream to read message
		private InputStream fBtSocketInputStream;
		
	}
}
