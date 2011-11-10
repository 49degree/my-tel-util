package com.guanri.fsk.audio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import com.guanri.fsk.audio.AudioOperatorImp.AudioReceiveDataHandler;
import com.guanri.fsk.conversion.FskDecode;
import com.guanri.fsk.conversion.FskDecodeResult;
import com.guanri.fsk.conversion.SourceQueue;

public class FskInputStream extends InputStream {

	AudioOperatorImp audioOperatorImp = null;
	SourceQueue sourceQueue = null;
	FskDecodeResult fskDecodeResult = null;
	FskDecode fskDecode = null;
	ByteBuffer readBuffer = null;
	boolean isEnable = true;
	long outTimeMill = 10000;//默认10秒
	Byte readBufferClock = 0;

	public FskInputStream(AudioOperatorImp audioOperatorImp) throws FskInputStreamInitException{
		readBuffer = ByteBuffer.allocate(1024);
		readBuffer.limit(0);
		this.audioOperatorImp = audioOperatorImp;
		this.sourceQueue = new SourceQueue();
		this.audioOperatorImp.setAudioReceiveDataHandler(new ReceiveDataHandler(sourceQueue));
		
		if(!this.audioOperatorImp.startCaptureAudio()){
			throw new FskInputStreamInitException("Capture Audio device open failed");
		}
		
		
		fskDecodeResult = new FskDecodeResult(true);
		fskDecode = new FskDecode(audioOperatorImp.fskCodeParams,sourceQueue,fskDecodeResult);
		new Thread(){
			public void run(){
				fskDecode.beginDecode();
			}
		}.start();
	}
	
	/**
	 * 设置读取超时时间
	 * @param outTimeMill
	 */
	public void setTimeOutTime(long outTimeMill){
		this.outTimeMill = outTimeMill;
	}

	/**
	 * 读入数据
	 **/
	long spendTime = 0;
	public int read() throws IOException {
		synchronized (readBufferClock) {
			byte[] temp = null;
			spendTime = outTimeMill;
			while(true){
				//如果输入流已经关闭，则抛出异常
				if(!isEnable){
					throw new IOException("FskDecode is close");
				}
				try{
					return readBuffer.get();
				}catch(BufferOverflowException  be){
					if(spendTime<0){//读取数据超时
						throw new FskReadTimeOutException("read time out");
					}
					if((temp=fskDecodeResult.getData())!=null){
						spendTime = outTimeMill;
						if(readBuffer.capacity()<temp.length)
							readBuffer = ByteBuffer.allocate(temp.length);
						readBuffer.position(0);
						readBuffer.put(temp, 0, temp.length);
						readBuffer.limit(temp.length);
						break;
					}else{
						try{
							spendTime -=500;
							Thread.sleep(500);
						}catch(InterruptedException ine){
							ine.printStackTrace();
							break;
						}
					}
				}
			}
			return -1;
		}

	}
	

    /**
     * Returns an estimated number of bytes that can be read or skipped without blocking for more
     * input.
     * @return the estimated number of bytes available
     * @throws IOException if this stream is closed or an error occurs
     */
    public int available() throws IOException {
    	synchronized (readBufferClock) {
        	byte[] newReadByte = null;
        	int leastLength = 0;
        	int tempPos = 0;
        	int tempLimit = 0;
        	byte[] tempArray = null; 
        	System.out.println("c查询是否有缓冲数据开始");
    		while((newReadByte=fskDecodeResult.getData())!=null){
    			tempPos = readBuffer.position();//当前缓冲器指针位置
    			tempLimit = readBuffer.limit();//当前缓冲器可读取大小
    			leastLength = tempLimit-tempPos;//当前缓冲器还未读取数据长度
    			
    			tempArray = readBuffer.array();
    			if(readBuffer.capacity()<newReadByte.length+leastLength){
    				readBuffer = ByteBuffer.allocate(newReadByte.length+leastLength);//新长度
    			}
    			readBuffer.position(0);
    			readBuffer.limit(newReadByte.length+leastLength);
    			System.out.println("缓冲区位置:"+readBuffer.remaining());
    			
    			readBuffer.put(tempArray, tempPos, leastLength);
    			readBuffer.put(newReadByte, 0, newReadByte.length);
    			readBuffer.position(0);
    		}
    		System.out.println("c查询是否有缓冲数据j结束:"+(readBuffer.limit()-readBuffer.position()));
    		
            return readBuffer.limit()-readBuffer.position();
		}
    }

    /**
     * Closes this stream. Concrete implementations of this class should free
     * any resources during close. This implementation does nothing.
     *
     * @throws IOException
     *             if an error occurs while closing this stream.
     */
    public void close() throws IOException {
    	isEnable = false;
    	if(fskDecode!=null){
    		fskDecode.stopDecode();
    	}
    	if(this.audioOperatorImp!=null){
    		this.audioOperatorImp.stopCaptureAudio();
    	}
    	
    }
    
    /**
     * 获取解码结果对象
     * @return
     */
    public FskDecodeResult getFskDecodeResult(){
    	return fskDecodeResult;
    }
    

	/**
	 * 处理收集到得数据
	 * 
	 * @author Administrator
	 * 
	 */
	public class ReceiveDataHandler implements AudioReceiveDataHandler {
		SourceQueue sourceQueue = null;

		public ReceiveDataHandler(SourceQueue sourceQueue) {
			this.sourceQueue = sourceQueue;
		}

		public void handler(byte[] data) {
			sourceQueue.put(data);
		}
	}
	
	
	public class FskReadTimeOutException extends IOException{
		public FskReadTimeOutException(String msg){
			super(msg);
		}
	}
	
	public class FskInputStreamInitException extends Exception{
		public FskInputStreamInitException(String msg){
			super(msg);
		}
	}
}
