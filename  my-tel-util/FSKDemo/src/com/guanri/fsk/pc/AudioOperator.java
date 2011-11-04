package com.guanri.fsk.pc;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import com.guanri.fsk.conversion.FskCodeParams;


public class AudioOperator{
	protected boolean running = false;
	FskCodeParams fskCodeParams = null;
	AudioReceiveDataHandler audioReceiveDataHandler = null;
	public AudioOperator(FskCodeParams fskCodeParams){
		this.fskCodeParams = fskCodeParams;
	}
	
	public void setAudioReceiveDataHandler(AudioReceiveDataHandler audioReceiveDataHandler){
		this.audioReceiveDataHandler = audioReceiveDataHandler;
	}

	public void start(){
		if(!running){
			captureAudio();
			initPlay();
			running = true;
		}
	}
	
	public void stop(){
		if(running){
			closetPlay();
			running = false;
		}
	}
	private void captureAudio(){
		try{

			final AudioFormat format = getFormat();
			
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			
            //判断参数是否正确
			if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line matching " + info + " not supported.");
                return;
            }			

	                        
            // get and open the target data line for capture.
			final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            try {
                line.open(format, line.getBufferSize());
            } catch (LineUnavailableException ex) { 
            	ex.printStackTrace();
                return;
            } catch (SecurityException ex) { 
                ex.printStackTrace();
                return;
            } catch (Exception ex) { 
            	ex.printStackTrace();
                return;
            }
			
			line.start();
			Runnable runner = new Runnable(){
				//计算读取缓存大小
				int frameSizeInBytes = format.getFrameSize();
	            int bufferLengthInFrames = line.getBufferSize() / 8;
	            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
	            byte[] data = new byte[bufferLengthInBytes];
				
				byte[] saveData = null;
				public void run(){
					running = true;
					try{
						while (running){
							int count = line.read(data, 0, data.length);
							if (count > 0){
								saveData = new byte[count];
								System.arraycopy(data, 0, saveData, 0, count);
								
								audioReceiveDataHandler.handler(saveData);


							}
						}

					}catch (Exception e){
						e.printStackTrace();
						System.err.println("I/O problems: " + e);
						System.exit(-1);
					}
					System.err.println("begin close++++++: ");
					line.drain();
					line.close();
					System.err.println("end close++++++: ");
				}
			};
			Thread captureThread = new Thread(runner);
			captureThread.start();
		}catch (LineUnavailableException e){
			e.printStackTrace();
			System.err.println("Line unavailable: " + e);
			System.exit(-2);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	SourceDataLine line = null;
//	WaveFileParams wavePlayFileParams = null;
	private void initPlay(){
		try{

			
			AudioFormat format = getFormat();
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();
		}catch (LineUnavailableException e){
			System.err.println("Line unavailable: " + e);
			System.exit(-4);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	private void closetPlay(){
		try{
			line.drain();
			line.close();
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("I/O problems: " + e);
			System.exit(-3);
		}	
	}	
	
	
	public void playAudio(byte[] data,int length){
		try{
			line.write(data,0,length);
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("I/O problems: " + e);
			System.exit(-3);
		}
	}

	private AudioFormat getFormat(){
//		float sampleRate = 11025;
//		int sampleSizeInBits = 16;
//		int channels = 1;
//		boolean signed = true;
//		boolean bigEndian = true;
		
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = fskCodeParams.getSampleF();
        int sampleSize = fskCodeParams.getSampleByteLength()*8;
        boolean bigEndian = false;
        int channels = 1;


        return new AudioFormat(encoding, rate, sampleSize, 
                       channels, (sampleSize/8)*channels, rate, bigEndian);
		 
		
//		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
//				bigEndian);
	}

	public interface AudioReceiveDataHandler{
		public void handler(byte[] data);
	}
	
	
	public static void main(String args[]){

	}
}
