package com.guanri.fsk.pc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import com.guanri.fsk.conversion.FskCodeParams;
import com.guanri.fsk.conversion.FskDecode;
import com.guanri.fsk.conversion.FskDecodeResult;
import com.guanri.fsk.conversion.FskEnCodeResult;
import com.guanri.fsk.conversion.FskEncode;
import com.guanri.fsk.conversion.SourceQueue;
import com.guanri.fsk.conversion.WaveFileParams;
import com.guanri.fsk.view.CureLineBean;
import com.guanri.fsk.view.RecordPlayView.ImagePanel;

public class AudioOperator{
	protected boolean running = false;
	ImagePanel receiveImagePanel = null;
	ImagePanel playImagePanel = null;
	SourceQueue sourceQueue = null;
	FskCodeParams fskCodeParams = null;
	public AudioOperator(ImagePanel playImagePanel,ImagePanel receiveImagePanel,SourceQueue sourceQueue,FskCodeParams fskCodeParams){
		this.receiveImagePanel = receiveImagePanel;
		this.playImagePanel = playImagePanel;
		this.sourceQueue =  sourceQueue;
		this.fskCodeParams = fskCodeParams;
	}

	public void start(byte[] data){
		if(!running){
			captureAudio();
			//playAudio(data);
			running = true;
		}
	}
	
	public void stop(){
		if(running){
			running = false;
		}
	}
	private void captureAudio(){
		try{

			
			final AudioFormat format = getFormat();
			
			final WaveFileParams waveFileParams = new WaveFileParams(fskCodeParams);
			waveFileParams.createFile(System.getProperty("user.dir")+"/in_record_"+new Date().getTime()+".wav");
			
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
            	System.out.println("Unable to open the line: " + ex);
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
								waveFileParams.appendData(saveData);
								sourceQueue.put(saveData);
								//绘图
								List<CureLineBean> list = new ArrayList<CureLineBean>();
								CureLineBean cureLineBean = new CureLineBean(saveData,Color.RED);
								list.add(cureLineBean);
								receiveImagePanel.setCureLineBean(list);
								receiveImagePanel.repaint();

							}
						}
						waveFileParams.closeFile();
					}catch (Exception e){
						System.err.println("I/O problems: " + e);
						System.exit(-1);
					}
				}
			};
			Thread captureThread = new Thread(runner);
			captureThread.start();
		}catch (LineUnavailableException e){
			System.err.println("Line unavailable: " + e);
			System.exit(-2);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	
	private void playAudio(byte[] data){
		try{
			final AudioFormat format = getFormat();
			
			final WaveFileParams waveFileParams = new WaveFileParams(fskCodeParams);
			waveFileParams.createFile(System.getProperty("user.dir")+"/out_record_"+new Date().getTime()+".wav");
			final FskEncode fskEncode = new FskEncode(fskCodeParams);
			
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();

			
			Runnable runner = new Runnable(){
				int bufferSize = (int) format.getSampleRate()
						* format.getFrameSize();
				public void run(){
					try{
						//while (running){
							FskEnCodeResult  fskEnCodeResult = fskEncode.encode(String.valueOf(new Date().getTime()).getBytes());
							
							line.write(fskEnCodeResult.code,0,fskEnCodeResult.code.length);
							//line.drain();
							waveFileParams.appendData(fskEnCodeResult.code);
							waveFileParams.closeFile();
							
							//绘图
							List<CureLineBean> list = new ArrayList<CureLineBean>();
							CureLineBean cureLineBean = new CureLineBean(fskEnCodeResult.code,Color.RED);
							list.add(cureLineBean);
							playImagePanel.setCureLineBean(list);
							playImagePanel.repaint();
							Thread.sleep(1000);
						//}
						line.drain();
						line.close();
					}catch (Exception e){
						e.printStackTrace();
						System.err.println("I/O problems: " + e);
						System.exit(-3);
					}
				}
			};
			Thread playThread = new Thread(runner);
			playThread.start();
		}catch (LineUnavailableException e){
			System.err.println("Line unavailable: " + e);
			System.exit(-4);
		}catch(Exception e){
			e.printStackTrace();
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

	public static void main(String args[]){

	}
}
