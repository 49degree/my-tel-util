package com.guanri.android.fsk.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

import com.guanri.android.lib.log.Logger;

public class AudioReceive {
	Logger logger = Logger.getLogger(AudioReceive.class);
	
	public enum State {INITIALIZING, READY, RECORDING, ERROR, STOPPED};//录音设备状态类型
	AudioRecord  audioRecorder = null;
    int audioSource = AudioSource.MIC;//声音来源, 
    int sampleRateInHz = 11025;//采样频率,
    int channelCounts = 1;//声道，1表示单声道, 
    int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;//声道类型
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;//声音采样类型, 
    int bSamples = 16;//声音采样大小（位 ） 
    int bufferSizeInBytes = 0;//采样缓冲大小
    private byte[] buffer;//采样数据
    private int framePeriod;// Number of frames written to file on each output(only in uncompressed mode)
    private static final int TIMER_INTERVAL = 120;// 
    private State  state;
    private int  payloadSize;//记录声音数据的总大小
    
    AudioReceiveOperatorImp audioReceiveOperatorImp = null;
	
    /**
     * 记录声音
     */
    private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener(){
        public void onPeriodicNotification(AudioRecord recorder){
            //audioRecorder.read(buffer, 0, buffer.length); // Fill buffer
            //audioReceiveOperatorImp.receive(buffer);//处理数据
            //payloadSize += buffer.length;
            //logger.error( "OnRecordPositionUpdateListener payloadSize："+payloadSize);
        }

        public void onMarkerReached(AudioRecord recorder){
            // NOT USED
        }
    };
    
	public AudioReceive()  throws Exception{
        framePeriod = sampleRateInHz * TIMER_INTERVAL / 1000;
        bufferSizeInBytes = framePeriod * 2 * bSamples * channelCounts / 8;
        if (bufferSizeInBytes < AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)){ 
        	bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
            // Set frame period and timer interval accordingly
            framePeriod = bufferSizeInBytes / ( 2 * sampleRateInHz * channelCounts / 8 );
        }
        
        buffer = new byte[bufferSizeInBytes];
        
        audioRecorder = new AudioRecord(AudioSource.MIC, sampleRateInHz, channelConfig, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
        
        
        if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED)
            throw new Exception("AudioRecord initialization failed");
        //audioRecorder.setRecordPositionUpdateListener(updateListener);
        //audioRecorder.setPositionNotificationPeriod(framePeriod);
        state = State.INITIALIZING;
	}
	
    /**
     * 准备录音
     */
    public void prepare(){
    	if(audioReceiveOperatorImp==null){
    		state = State.ERROR;
    		return ;
    	}
        try{
        	if(state == State.STOPPED){
                audioRecorder = new AudioRecord(AudioSource.MIC, sampleRateInHz, channelConfig, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
                state = State.INITIALIZING;
            }
        	if (state == State.INITIALIZING){
                if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED){
                	audioReceiveOperatorImp.prepare(this);
                    state = State.READY;
                }else{
                	logger.error( "prepare() method called on uninitialized recorder");
                    state = State.ERROR;
                }
            }else{
            	logger.error( "prepare() method called on illegal state");
            	stop();
                state = State.ERROR;
            }
        }catch(Exception e){
        	state = State.ERROR;
        	e.printStackTrace();
        	logger.error( "Unknown error occured in prepare()");
        }
    }

    /**
     * 
     * 开始采集
     * 
     */
    public void start(){
        if (state == State.READY) {
            payloadSize = 0;
            audioRecorder.startRecording();
            state = State.RECORDING;
            new Thread(){
            	public void run(){
            		while(state==AudioReceive.State.RECORDING){
                        audioRecorder.read(buffer, 0, buffer.length);
                        audioReceiveOperatorImp.receive(buffer);//处理数据
                        payloadSize += buffer.length;
            		}

            	}
            }.start();
            //logger.error( "payloadSize："+payloadSize);
            
        }else{
        	logger.error( "start() called on illegal state");
            state = State.ERROR;
        }
    }
    
    
    /**
     * 停止采集
     */
    public void stop() {

        if (state == State.RECORDING){
        	state = State.STOPPED;
            if (audioRecorder != null){
            	audioRecorder.stop();
                audioRecorder.release();
                audioReceiveOperatorImp.stop();
            }
        }
    }
    
    /**
     * @return recorder state
     */
    public State getState(){
        return state;
    }
    
    /**
     * @return recorder state
     */
    public int getPayloadSize(){
        return payloadSize;
    }
    
    
    public AudioReceiveOperatorImp getAudioReceiveOperatorImp() {
		return audioReceiveOperatorImp;
	}

	public void setAudioReceiveOperatorImp(
			AudioReceiveOperatorImp audioReceiveOperatorImp) {
		this.audioReceiveOperatorImp = audioReceiveOperatorImp;
	}
	
	public interface AudioReceiveOperatorImp{
		public abstract void prepare(AudioReceive audioReceive);
		public abstract void receive(byte[] audioBuff);
		public abstract void stop();
	}
}

