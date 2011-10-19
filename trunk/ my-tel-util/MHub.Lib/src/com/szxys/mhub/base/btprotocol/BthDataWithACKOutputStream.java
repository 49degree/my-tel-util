package com.szxys.mhub.base.btprotocol;

import java.io.OutputStream;
class BthDataWithACKOutputStream implements IBthDataOutputStream
{
	/**
	 * @param aOutputStream
	 * @param aWaitTimeOut in milliseconds
	 */
	public BthDataWithACKOutputStream(OutputStream aOutputStream,int aWaitTimeOut)
	{
		fOutputStream = new BthDataWithoutACKOutputStream(aOutputStream);
		fAckHandler = new WithAckHandler();
		fTimeOutMillions = aWaitTimeOut;
		BthLog.i(TAG,"set standard wait time = " + fTimeOutMillions);
		//signal
		fEvent = new Object();
		
		//set status
		fGetAck = false;
	}
	@Override
	public boolean send(byte[] aPacketData)
	{
		if ( fOutputStream.send(aPacketData) == false )
		{
			return false;
		}		
		synchronized ( fEvent )
		{
			try
			{				
//				if ( compareAndSwapBoolean(true,false) == true )
//				{
//					return true;
//				}
				//wait the specified limit time
				//the condition of waking up
				//1.time out without notified by getting ACK: fGetAck = false
				//2.notified by getting ACK: fGetAck = true
				
				fWaitBegin = System.currentTimeMillis();
				fEvent.wait(fTimeOutMillions);
				fWaitEnd = System.currentTimeMillis();
				BthLog.d(TAG,"wait time = " + (fWaitEnd-fWaitBegin));
			}
			catch (Exception exception) 
			{//do nothing				
			}	
		}
		
		return compareAndSwapBoolean(true,false);
	}
	
	@Override
	public void releaseResource()
	{
		fOutputStream.releaseResource();
		fOutputStream = null;
		
		fAckHandler = null;		
	}
	
	public IAckEventHandler getAckEventHandler()
	{
		return fAckHandler;
	}
	public BthDataWithoutACKOutputStream getWithoutACKOutputStream()
	{
		return fOutputStream;		
	}
	
	
	
	//CAS 操作包含三个操作数 —— 内存位置（V）、预期原值（A）和新值(B)。
	//如果内存位置的值与预期原值相匹配，那么处理器会自动将该位置值更新为新值。
	//否则，处理器不做任何操作
	/**
	 * if the value {@value aPredictaleValue} is equal to the field {@link fGetAck} value,
	 * then set the field value to new value {@value aNewValue}, otherwise do nothing
	 * @param aPredictaleValue the predict value
	 * @param aNewValue the reset new value
	 * @return the old value of the field {@link fGetAck}
	 */
	private synchronized boolean compareAndSwapBoolean(boolean aPredictaleValue,boolean aNewValue)
	{
		if ( fGetAck == aPredictaleValue )
		{
			fGetAck = aNewValue;
			return aPredictaleValue;
		}
		return fGetAck;
	}
	
	private static final String TAG = "bt.WithACKOutputStream";
	private BthDataWithoutACKOutputStream fOutputStream;
	private WithAckHandler fAckHandler;
	private int fTimeOutMillions;
	private Object fEvent;
	private boolean fGetAck;
	
	private long fWaitBegin;
	private long fWaitEnd;
	
	//inner class	
	class WithAckHandler implements IAckEventHandler
	{	
		@Override
		public void onGetAck()
		{
			synchronized ( fEvent )
			{
				BthDataWithACKOutputStream.this.compareAndSwapBoolean(false,true);
				fEvent.notify();
			}			
		}
	}
}
