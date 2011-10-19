package com.guanri.android.fsk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.LinkedList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.guanri.android.fsk.convesion.Demo;
import com.guanri.android.fsk.convesion.FskCodeParams;
import com.guanri.android.fsk.convesion.FskDecode;
import com.guanri.android.fsk.convesion.FskDecodeResult;
import com.guanri.android.fsk.convesion.FskEnCodeResult;
import com.guanri.android.fsk.convesion.FskEncode;
import com.guanri.android.fsk.convesion.SourceQueue;
import com.guanri.android.fsk.convesion.WaveFileParams;
import com.guanri.android.fsk.utils.AudioReceive;
import com.guanri.android.fsk.utils.DialogUtils;
import com.guanri.android.fsk.utils.AudioReceive.AudioReceiveOperatorImp;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;


public class AndoridFSKDemo extends Activity implements OnClickListener{
	Logger logger = Logger.getLogger(AndoridFSKDemo.class);
	public String TAG = "AndoridFSKDemo";
	private static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "guanri";
    /** Called when the activity is first created. */
	EditText info = null;
	EditText read_info = null;
	EditText resize_params = null;
	Button play = null;
	Button playcid = null;
	//等待提示框
	ProgressDialog btDialog = null;
	
    /* MediaPlayer对象 */  
    public MediaPlayer  mMediaPlayer  = null;  
    AudioReceive audioReceive = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        info = (EditText)this.findViewById(R.id.info);
        read_info = (EditText)this.findViewById(R.id.read_info);
        resize_params = (EditText)this.findViewById(R.id.resize_params);
        play = (Button)this.findViewById(R.id.play); 
        playcid = (Button)this.findViewById(R.id.playcid);
        
        play.setOnClickListener(this);
        playcid.setOnClickListener(this);
        
        /* 构建MediaPlayer对象 */  
        mMediaPlayer  = new MediaPlayer();  
        try{
        	audioReceive = new AudioReceive();
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        btDialog = new ProgressDialog(AndoridFSKDemo.this);
		btDialog.setTitle("发送数据"); // title     
		btDialog.setMessage("请稍等");//进度是否是不确定的，这只和创建进度条有关
		//停止录音
		btDialog.setOnCancelListener(new OnCancelListener(){
			public void onCancel(DialogInterface dialog){
    			try{
    				audioReceive.stop();
    			}catch(Exception e){}
			}
		});
    }
    
    @Override
    public void onClick(View v){
    	switch(v.getId()){
    	case R.id.play://播放String编码
    		btDialog.show();
    		new Thread(){
    			public void run(){
            		try{
                		//开始录音
            			updateUI.sendMessage(updateUI.obtainMessage(2, btDialog));
                		audioReceive.setAudioReceiveOperatorImp(audioReceiveToText);
                		audioReceive.prepare();
                		audioReceive.start();
                		updateUI.sendMessage(updateUI.obtainMessage(3, btDialog));
                		
                		//开始编码发送数据
                		if(info.getText()==null||"".equals(info.getText().toString())){
                        	DialogUtils.showMessageAlertDlg(AndoridFSKDemo.this, "提示", "输入信息为空", null);
                        	return ;
                        }
                		String infotext = info.getText().toString();
                		//String outfile = filePath+File.separator+"out"+new Date().getTime()+".wav";
                		//Demo.EncodeWaveFile(outfile, infotext.getBytes());	
                		//createWave(outfile, infotext.getBytes());
                		//playMusic(outfile);
                		submitString(infotext);//开始编码发送数据结束
                		int time = 2;
                		try{
                			String waittime = info.getText().toString().substring(0,1);
                			time = Integer.parseInt(waittime);
                		}catch(Exception e){
                			e.printStackTrace();
                		}
                		Thread.sleep(time*1000); 
            		}catch(Exception e){
            			e.printStackTrace();
            			btDialog.dismiss();
            		}finally{
            			try{
            				logger.error("audioReceive.stop()");
            				audioReceive.stop();
            			}catch(Exception e){
            				e.printStackTrace();
            			}
            			try{
            				btDialog.dismiss();
            			}catch(Exception e){
            				e.printStackTrace();
            			}
            		}
    			}
    		}.start();
    		break;
    	case R.id.playcid:////播放CID编码
			btDialog.show();
    		new Thread(){
    			public void run(){
    				
            		try{
                		//开始录音
            			updateUI.sendMessage(updateUI.obtainMessage(2, btDialog));
            			
            			logger.error( new Date().getTime()+"");
                		audioReceive.setAudioReceiveOperatorImp(audioReceiveToText);
                		audioReceive.prepare();
                		audioReceive.start();
                		updateUI.sendMessage(updateUI.obtainMessage(3, btDialog));
                		
                		String outfile = filePath+File.separator + "cid.wav";
                		playMusic(outfile);
                		//VoiceUtil.playVoice(AndoridFSKDemo.this, R.raw.cid);
                		//开始编码发送数据结束
                		logger.error( new Date().getTime()+"");
                		int time = 1;
                		try{
                			String waittime = info.getText().toString().substring(0,1);
                			time = Integer.parseInt(waittime);
                		}catch(Exception e){
                			e.printStackTrace();
                		}
                		Thread.sleep(time*1000); 
            		}catch(Exception e){
            			e.printStackTrace();
            			btDialog.dismiss();
            		}finally{
            			try{
            				logger.error("audioReceive.stop()");
            				audioReceive.stop();
            			}catch(Exception e){
            				
            			}
            			try{
            				btDialog.dismiss();
            			}catch(Exception e){
            				
            			}
            		}
    			}
    		}.start();
    		break;
    	default :
    		break;
    	}
    }

    
    /**
     * 采集数据处理对象
     */
    AudioReceiveOperatorImp audioReceiveToText = new AudioReceiveOperatorImp(){
    	RandomAccessFile randomAccessWriter = null;
    	AudioReceive audioReceive = null;
		//进行解码
		FskCodeParams fskCodeParams = new FskCodeParams(2200,1200,11025,2,1200);
		SourceQueue sourceQueue = null;
		FskDecodeResult fskDecodeResult = null;
		FskDecode fskDecode = null;
		
    	public void prepare(AudioReceive audioReceive){
    		this.audioReceive = audioReceive;
    		try{
    			infile = filePath+File.separator+"in_"+new Date().getTime()+".wav";//录制声音文件名称
        		randomAccessWriter = new RandomAccessFile(infile, "rw");
                randomAccessWriter.setLength(0); // Set file length to 0, to prevent unexpected behavior in case the file already existed
                randomAccessWriter.writeBytes("RIFF");
                randomAccessWriter.writeInt(0); // Final file size not known yet, write 0 
                randomAccessWriter.writeBytes("WAVE");
                randomAccessWriter.writeBytes("fmt ");
                randomAccessWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
                randomAccessWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
                randomAccessWriter.writeShort(Short.reverseBytes((short)channelCounts));// Number of channels, 1 for mono, 2 for stereo
                randomAccessWriter.writeInt(Integer.reverseBytes(sampleRateInHz)); // Sample rate
                randomAccessWriter.writeInt(Integer.reverseBytes(sampleRateInHz*bSamples*channelCounts/8)); // Byte rate, SampleRate*NumberOfChannels*BitsPerSample/8
                randomAccessWriter.writeShort(Short.reverseBytes((short)(channelCounts*bSamples/8))); // Block align, NumberOfChannels*BitsPerSample/8
                randomAccessWriter.writeShort(Short.reverseBytes((short)bSamples)); // Bits per sample
                randomAccessWriter.writeBytes("data");
                randomAccessWriter.writeInt(0); // Data chunk size not known yet, write 0
    		}catch(Exception e){
    			
    		}
    		
    		//进行解码
    		sourceQueue = new SourceQueue();
    		fskDecodeResult = new FskDecodeResult();
    		fskDecode = new FskDecode(fskCodeParams,sourceQueue,fskDecodeResult);
    		
    		if(resize_params.getText()!=null){
    			try{
    				fskDecode.splitParmats = Float.parseFloat("0."+resize_params.getText().toString());
    			}catch(Exception e){
    				fskDecode.splitParmats =0.55f;
    			}
    		}
    		
    		new Thread(){
    			public void run(){
    				fskDecode.beginDecode();
    			}
    		}.start();

    	}
    	public void stop(){
            try{
            	Thread.sleep(1000); 
                randomAccessWriter.seek(4); // Write size to RIFF header
                randomAccessWriter.writeInt(Integer.reverseBytes(36+this.audioReceive.getPayloadSize()));
                randomAccessWriter.seek(40); // Write size to Subchunk2Size field
                randomAccessWriter.writeInt(Integer.reverseBytes(this.audioReceive.getPayloadSize()));
                randomAccessWriter.close();
                logger.error( "randomAccessWriter.close():"+this.audioReceive.getPayloadSize());  
                Thread.sleep(1000); 
        		fskDecode.isContinue = false;
				if(fskDecodeResult.data!=null){
					updateUI.sendMessage(updateUI.obtainMessage(1, TypeConversion.getASCString(fskDecodeResult.data,fskDecodeResult.dataIndex)));
				}
            }catch(IOException e){
            	e.printStackTrace();
            	logger.error( "I/O exception occured while closing output file");
            }catch(InterruptedException ine){
            	
            }
    	}
    	
    	public void receive(byte[] audioBuff){
    		byte[] temp = new byte[audioBuff.length];
    		System.arraycopy(audioBuff, 0, temp, 0, audioBuff.length);
    		
    		try{
    			sourceQueue.put(temp);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		try{
    			randomAccessWriter.write(temp); // Write buffer to file
    			//logger.error( "OnRecordPositionUpdateListener payloadSize："+audioBuff.length);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		
			if(fskDecodeResult.data!=null){
				updateUI.sendMessage(updateUI.obtainMessage(1, TypeConversion.getASCString(fskDecodeResult.data,fskDecodeResult.dataIndex)));
			}
    	}
    };

    
    /**
     * 采集数据处理对象
     */
    int audioSource = AudioSource.MIC;//声音来源, 
    int sampleRateInHz = 11025;//采样频率,
    int channelCounts = 1;//声道，1表示单声道, 
    int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;//声道类型
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;//声音采样类型,
    int bSamples = 16;//声音采样大小（位 ） 
    int bufferSizeInBytes = 0;//采样缓冲大小
    String infile = null;
    AudioReceiveOperatorImp audioReceiveToSound = new AudioReceiveOperatorImp(){
    	AudioReceive audioReceive = null;
    	AudioTrack trackplayer = null;
    	public void prepare(AudioReceive audioReceive){
    		this.audioReceive = audioReceive;
            /*
             * 根据采样率，采样精度，单双声道来得到frame的大小。
             * 音频中最常见的是frame这个单位，什么意思？经过多方查找，最后还是在ALSA的wiki中找到解释了。
             * 一个frame就是1个采样点的字节数*声道。为啥搞个frame出来？因为对于多//声道的话，用1个采样点的字节数表示不全，
             * 因为播放的时候肯定是多个声道的数据都要播出来//才行。
             * 所以为了方便，就说1秒钟有多少个frame，这样就能抛开声道数，把意思表示全了
             */
            bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);;
    		//创建AudioTrack
    		trackplayer = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz,
    				channelConfig,
    				audioFormat,
    				bufferSizeInBytes,
    				AudioTrack.MODE_STREAM);//
    		trackplayer.play() ;//开始
    	}
    	public void stop(){
       		trackplayer.stop();//停止播放
    		trackplayer.release();//释放底层资源
    	}
    	
    	public void receive(byte[] audioBuff){
    		try{
    			trackplayer.write(audioBuff, 0, audioBuff.length) ;//往track中写数据
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    };
    

    
    /**
     * 采集数据处理对象，记录音频文件
     */
    LinkedList<byte[]> soundByteList = null;
    AudioReceiveOperatorImp audioReceiveToFile = new AudioReceiveOperatorImp(){
    	RandomAccessFile randomAccessWriter = null;
    	AudioReceive audioReceive = null;
    	public void prepare(AudioReceive audioReceive){
    		this.audioReceive = audioReceive;
    		try{
    			infile = filePath+File.separator+"in_"+new Date().getTime()+".wav";//录制声音文件名称
        		randomAccessWriter = new RandomAccessFile(infile, "rw");
                randomAccessWriter.setLength(0); // Set file length to 0, to prevent unexpected behavior in case the file already existed
                randomAccessWriter.writeBytes("RIFF");
                randomAccessWriter.writeInt(0); // Final file size not known yet, write 0 
                randomAccessWriter.writeBytes("WAVE");
                randomAccessWriter.writeBytes("fmt ");
                randomAccessWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
                randomAccessWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
                randomAccessWriter.writeShort(Short.reverseBytes((short)channelCounts));// Number of channels, 1 for mono, 2 for stereo
                randomAccessWriter.writeInt(Integer.reverseBytes(sampleRateInHz)); // Sample rate
                randomAccessWriter.writeInt(Integer.reverseBytes(sampleRateInHz*bSamples*channelCounts/8)); // Byte rate, SampleRate*NumberOfChannels*BitsPerSample/8
                randomAccessWriter.writeShort(Short.reverseBytes((short)(channelCounts*bSamples/8))); // Block align, NumberOfChannels*BitsPerSample/8
                randomAccessWriter.writeShort(Short.reverseBytes((short)bSamples)); // Bits per sample
                randomAccessWriter.writeBytes("data");
                randomAccessWriter.writeInt(0); // Data chunk size not known yet, write 0
                soundByteList = new LinkedList<byte[]>();
    		}catch(Exception e){
    			
    		}

    	}
    	public void stop(){
            try{
            	Thread.sleep(1000); 
//            	for(byte[] temp:soundByteList){
//            		randomAccessWriter.write(temp); // Write buffer to file
//            	}
                randomAccessWriter.seek(4); // Write size to RIFF header
                randomAccessWriter.writeInt(Integer.reverseBytes(36+this.audioReceive.getPayloadSize()));
                randomAccessWriter.seek(40); // Write size to Subchunk2Size field
                randomAccessWriter.writeInt(Integer.reverseBytes(this.audioReceive.getPayloadSize()));
                randomAccessWriter.close();
                logger.error( "randomAccessWriter.close():"+this.audioReceive.getPayloadSize());  
                //对录制的声音进行解码  
        		byte[] decodeByte = Demo.DecodeWaveFile(infile);//filePath+File.separator+"Test.wav");
        		String decodeStr = new String(decodeByte, 0, decodeByte.length);	
        		updateUI.sendMessage(updateUI.obtainMessage(1, decodeStr));
            }catch(IOException e){
            	e.printStackTrace();
            	logger.error( "I/O exception occured while closing output file");
            }catch(InterruptedException ine){
            	
            }
    	}
    	
    	public void receive(byte[] audioBuff){
    		try{
    			randomAccessWriter.write(audioBuff); // Write buffer to file
//    			byte[] temp = new byte[audioBuff.length];
//    			System.arraycopy(audioBuff, 0, temp, 0, temp.length);
//    			soundByteList.add(temp);
    			
    			logger.error( "OnRecordPositionUpdateListener payloadSize："+audioBuff.length);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		
    	}
    };
    
    
    
    /**
     * 回调更新界面
     */
    public Handler updateUI = new Handler(){
        public void handleMessage(Message msg) {
        	if(msg.what==1&&read_info!=null){
        		read_info.setText((String)msg.obj);
        	}else if(msg.what==2){
        		((ProgressDialog)(msg.obj)).setMessage("正在打开录音设备，请稍等");
        	}if(msg.what==3){
        		((ProgressDialog)(msg.obj)).setMessage("录音设备已经打开，正在录音...");
        	}
        }
    };
    
    

    
    /**
     * 播放音频流
     * @param audioBuffer
     */
    public void submitString(String info){
		FskCodeParams fskCodeParams = new FskCodeParams(2200,1200,11025,2,1200);
		FskEncode fskEncode = new FskEncode(fskCodeParams);
		byte[] buffer = info.getBytes();
		byte[] temp = new byte[30+buffer.length];
		//加入开始标志和结束标志位
		System.arraycopy(FskDecodeResult.DATA_START_FLAG, 0, temp, 14, FskDecodeResult.DATA_START_FLAG.length);
		System.arraycopy(buffer, 0, temp, 19, buffer.length);
		System.arraycopy(FskDecodeResult.DATA_END_FLAG, 0, temp, 19+buffer.length, FskDecodeResult.DATA_END_FLAG.length);
		//进行编码
		FskEnCodeResult fskEnCodeResult = fskEncode.encode(temp);
		//保证播放的编码为偶数
		byte[] audioBuffer = null;
		if (fskEnCodeResult.code.length % 2 > 0) {
			audioBuffer = new byte[1+fskEnCodeResult.code.length];
			System.arraycopy(fskEnCodeResult.code, 0, audioBuffer, 0, fskEnCodeResult.code.length);
		}else{
			audioBuffer = fskEnCodeResult.code;
		}
		fskEnCodeResult.code = audioBuffer;
		fskEnCodeResult.index = fskEnCodeResult.code.length;
		
		playBuffer(fskEnCodeResult.code);
		//保存wave文件
		String outfile = filePath+File.separator+"out"+new Date().getTime()+".wav";
		saveWaveFile(outfile,fskCodeParams,fskEnCodeResult);
		
    }
    
    /**
     * 播放音频流
     * @param audioBuffer
     */
	public void playBuffer(byte[] audioBuffer) {
		if (audioBuffer == null) {
			return;
		}
		/*
		 * 根据采样率，采样精度，单双声道来得到frame的大小。
		 * 音频中最常见的是frame这个单位，什么意思？经过多方查找，最后还是在ALSA的wiki中找到解释了。
		 * 一个frame就是1个采样点的字节数*声道。为啥搞个frame出来？因为对于多//声道的话，用1个采样点的字节数表示不全，
		 * 因为播放的时候肯定是多个声道的数据都要播出来//才行。 所以为了方便，就说1秒钟有多少个frame，这样就能抛开声道数，把意思表示全了
		 */

		int bufsize = AudioTrack.getMinBufferSize(sampleRateInHz,// 采样频率
				AudioFormat.CHANNEL_CONFIGURATION_MONO,// 单声道
				AudioFormat.ENCODING_PCM_16BIT);// 一个采样点16比特-2个字节

		// 注意，按照数字音频的知识，这个算出来的是一秒钟buffer的大小。
		// 创建AudioTrack
		AudioTrack trackplayer = new AudioTrack(AudioManager.STREAM_MUSIC,
				sampleRateInHz, channelConfig, audioFormat, bufsize,
				AudioTrack.MODE_STREAM);//
		trackplayer.play();// 开始
		/**
		 * 没次只能传入指定大小的数据
		 */
		int writeTimes = audioBuffer.length%bufsize>0?audioBuffer.length/bufsize+1:audioBuffer.length/bufsize;
		for(int i=0;i<writeTimes;i++){
			if(i==writeTimes-1&&writeTimes*bufsize>audioBuffer.length){
				trackplayer.write(audioBuffer, i*bufsize, audioBuffer.length%bufsize);// 往track中写数据
			}else{
				trackplayer.write(audioBuffer, i*bufsize, bufsize);// 往track中写数据
			}
			
		}
		
		trackplayer.stop();// 停止播放
		trackplayer.release();// 释放底层资源
	}
    

    /**
     * 进行编码
     * @param filePath
     * @param buffer
     */
    private void createWave(String filePath,byte[] buffer){
		FskCodeParams fskCodeParams = new FskCodeParams(2200,1200,11025,2,1200);
		FskEncode fskEncode = new FskEncode(fskCodeParams);
		//进行编码
		byte[] temp = new byte[20+buffer.length];
		System.arraycopy(buffer, 0, temp, 10, buffer.length);
		
		FskEnCodeResult fskEnCodeResult = fskEncode.encode(temp);
		
		//编码结束
		//保存wave文件
		saveWaveFile(filePath,fskCodeParams,fskEnCodeResult);
    }
    
    /**
     * 进行编码
     * @param filePath
     * @param buffer
     */
    private void saveWaveFile(String filePath,FskCodeParams fskCodeParams,FskEnCodeResult fskEnCodeResult){
		//编码结束
		//保存wave文件
		WaveFileParams waveFileParams = new WaveFileParams(fskCodeParams,fskEnCodeResult);
		byte[] waveByte = waveFileParams.parseWaveToByte();
		FileOutputStream fout = null;
		try{
			File waveFile = new File(filePath);
			if(!waveFile.exists()){
				waveFile.createNewFile();
			}
			fout = new FileOutputStream(waveFile);
			fout.write(waveByte);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			try{
				fout.flush();
				fout.close();
				fout = null;
			}catch(Exception ioe){
				
			}
			
		}
    }
    
    /**
     * 播放文件
     * @param path
     */
    private void playMusic(String path)  
    {  
        try  
        {  
        	Log.e(TAG,"开始播放");
            /* 重置MediaPlayer */  
            mMediaPlayer.reset();  
            /* 设置要播放的文件的路径 */  
            mMediaPlayer.setDataSource(path);  
            /* 准备播放 */  
            mMediaPlayer.prepare();  
            /* 开始播放 */  
            mMediaPlayer.start();  
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener()   
            {  
                public void onCompletion(MediaPlayer arg0)  
                {  
                    //播放完成
                	Log.e(TAG,"播放完成");
                }  
            });  
        }catch (IOException e){
        	e.printStackTrace();
        }  
    } 
    
}