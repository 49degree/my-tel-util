package com.szxys.mhub.base.btprotocol;

interface IProcessorOfHeartBeat
{
	void start() throws Exception;
	void stop() throws Exception;
	void pause();
	void resume();
	boolean isPaused();
	boolean isStarted();
	boolean isStopped();
}
