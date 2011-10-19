package com.szxys.mhub.base.btprotocol;

public class ProcessorOfNopHeartBeat implements IProcessorOfHeartBeat
{

	@Override
	public void start() throws Exception
	{
	}

	@Override
	public void stop() throws Exception
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}

	@Override
	public boolean isPaused()
	{
		return false;
	}

	@Override
	public boolean isStarted()
	{
		return true;
	}

	@Override
	public boolean isStopped()
	{
		return false;
	}

}
