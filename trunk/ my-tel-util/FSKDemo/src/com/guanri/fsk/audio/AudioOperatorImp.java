package com.guanri.fsk.audio;

import com.guanri.fsk.conversion.FskCodeParams;


public abstract class  AudioOperatorImp {
	protected Boolean captureAudioRunning = false;
	protected Boolean playAudioOpended = false;
	
	protected AudioReceiveDataHandler audioReceiveDataHandler = null;
	protected FskCodeParams fskCodeParams = null;
	
	public AudioOperatorImp(FskCodeParams fskCodeParams){
		this.fskCodeParams = fskCodeParams;
	}
	
	/**
	 * 开始录音
	 */
	public boolean startCaptureAudio(){
		synchronized(captureAudioRunning){
			if(!captureAudioRunning){
				captureAudio();
				captureAudioRunning = true;
				return captureAudioRunning;
			}else{
				return false;
			}
		}

	}
	
	/**
	 * 初始化录音设备
	 */
	public boolean startInitPlay(){
		synchronized(playAudioOpended){
			if(!playAudioOpended){
				initPlay();
				playAudioOpended = true;
				return playAudioOpended;
			}else{
				return false;
			}
		}
	}
	/**
	 * 停止录音
	 */
	public void stopCaptureAudio(){
		synchronized(captureAudioRunning){
			if(captureAudioRunning){
				closeCaptureAudio();
				captureAudioRunning = false;
			}
		}
	}
	/**
	 * 关闭播放设备
	 */
	public void stopPlayAudio(){
		synchronized(playAudioOpended){
			if(playAudioOpended){
				closetPlay();
				playAudioOpended = false;
			}
		}
		

	}
	
	public boolean getCaptureAudioState(){
		return captureAudioRunning;
	}
	
	public boolean getPlayAudioState(){
		return playAudioOpended;
	}
	
	public void setAudioReceiveDataHandler(AudioReceiveDataHandler audioReceiveDataHandler){
		this.audioReceiveDataHandler = audioReceiveDataHandler;
	}
	
	public abstract void playAudio(byte[] data,int length);//播放音频
	
	protected abstract void captureAudio();//开启录音线程
	protected abstract void initPlay();//初始化播放设备
	protected abstract void closetPlay();//关闭播放设备
	protected abstract void closeCaptureAudio();//关闭录音设备
	
	public interface AudioReceiveDataHandler{
		public void handler(byte[] data);
	}
}
