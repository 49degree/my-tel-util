package com.szxys.mhub.base.btprotocol;

class ProcessorOfHeartBeat implements IProcessorOfHeartBeat
{
	/** 
	 * @param aTimerInterval the timer interval to send heart beat packet, in milliseconds
	 * @param aTimerPeriod the clock period, in milliseconds
	 * @param aHandler to receive the message of you can send a heart beat packet
	 */
	public ProcessorOfHeartBeat(int aTimerInterval,int aTimerPeriod,IHeartBeatEventHandler aHandler)
	{		
		//fTimerInterval = aTimerInterval;
		fTimerPeriod = aTimerPeriod;
		int times = aTimerInterval / aTimerPeriod;
		//must be bigger than 0
		fTotalTimes = times == 0 ? 1 : times;
		
		fHandler = aHandler;
		fStarted = false;
		fStopped = false;
	}
	
	/**
	 * * start the processor, only start once, the first call is valid
	 * @throws Exception start again or it has been stopped
	 */
	public synchronized void start() throws Exception
	{
		if ( fStarted || fStopped )
		{
			throw new Exception("it has been stated already or has been stopped before");			
		}
		else 
		{
			fTimerTask = new HeartBeatTimerTask(fTimerPeriod);
			fTimerElapsed = 0;
			//period
			new Thread(fTimerTask,"HeartBeatPolicy").start();
			
			fStarted = true;
			fPaused = false;
		}
	}
	
	public synchronized void stop() throws Exception
	{
		if ( fStarted && !fStopped )
		{
			resetTimer();
			fTimerTask.cancel();
			fStopped = true;
			fTimerTask = null;
			fHandler = null;
		}
		else
		{
			throw new Exception("it has been stop already or hasn't been start");
		}
	}
	
	public synchronized void pause()
	{		
		fPaused = true;
		resetTimer();
	}
	public synchronized void resume()
	{
		fPaused = false;
	}
	public synchronized boolean isPaused()
	{
		return fPaused;
	}
	
	public synchronized boolean isStarted()
	{
		return fStarted;
	}
	public synchronized boolean isStopped()
	{
		return fStopped;
	}
	/**
	 * reset the timer to zero
	 */
	private synchronized void resetTimer()
	{
		fTimerElapsed = 0;
	}
	private synchronized int increaseElapsedTimer()
	{
		return ++fTimerElapsed;
	}
	
	private void timeout()
	{
		if ( !isPaused() && (increaseElapsedTimer() == fTotalTimes) )
		{
			fHandler.inform(IHeartBeatEventHandler.ETYPE_CAN_SEND_PACKET);
		}		
	}
	//
	private boolean fStarted;
	private boolean fStopped;	
	private boolean fPaused;
	//timer
	//private int fTimerInterval;
	private int fTimerPeriod;
	private int fTimerElapsed;
	private final int fTotalTimes;
	private HeartBeatTimerTask fTimerTask;
	
	//a handler to inform the owner
	private IHeartBeatEventHandler fHandler;
		
	//inner class
	class HeartBeatTimerTask implements Runnable
	{
		public HeartBeatTimerTask(int aWaitTime)
		{
			this.fTimeOut = aWaitTime;
			this.fCancelled = false;
		}
		@Override
		public void run()
		{
			while ( fCancelled == false )
			{
				try
				{
					Thread.sleep(this.fTimeOut);
					if ( fCancelled == false )
						ProcessorOfHeartBeat.this.timeout();
				}
				catch (Exception exception)
				{//do nothing
				}				
			}
		}
		
		public void cancel()
		{
			fCancelled = true;
		}
		
		private int fTimeOut;
		private boolean fCancelled;
	}	
}

interface IHeartBeatEventHandler
{
	void inform(int aEventType);	
	//Event Type
	public final static int ETYPE_CAN_SEND_PACKET = 1;
}
