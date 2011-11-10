package com.guanri.fsk.audio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.guanri.fsk.conversion.FskEnCodeResult;
import com.guanri.fsk.conversion.FskEncode;

public class FskOutputStream extends OutputStream{
	
	AudioOperatorImp audioOperatorImp = null;
	FskEncode fskEncode = null;
	ByteBuffer sendBuffer = null;
	Byte buffBlock = 0 ;
	
	public FskOutputStream(AudioOperatorImp audioOperatorImp) throws FskOutputStreamInitException{
		this.audioOperatorImp = audioOperatorImp;
		if(!this.audioOperatorImp.startInitPlay()){
			throw new FskOutputStreamInitException("Play Audio device open failed");
		}
		fskEncode = new FskEncode(this.audioOperatorImp.fskCodeParams);
		sendBuffer = ByteBuffer.allocate(512);
		sendBuffer.limit(512);
	}
	
	/**
     * Writes a single byte to this stream. Only the least significant byte of
     * the integer {@code oneByte} is written to the stream.
     *
     * @param oneByte
     *            the byte to be written.
     * @throws IOException
     *             if an error occurs while writing to this stream.
     */
    public void write(int oneByte) throws IOException{
    	synchronized (buffBlock) {
    		sendBuffer.put((byte)oneByte);
    		
    		if(sendBuffer.position()>128){
    			this.flush();
    		}
		}
    }
    
    
    /**
     * Closes this stream. Implementations of this method should free any
     * resources used by the stream. This implementation does nothing.
     *
     * @throws IOException
     *             if an error occurs while closing this stream.
     */
    public void close() throws IOException {
    	flush();
		try{
			Thread.sleep(1500);
		}catch(Exception e){
			e.printStackTrace();
		}
    	if(this.audioOperatorImp!=null){
    		this.audioOperatorImp.stopPlayAudio();
    	}
    }

    /**
     * Flushes this stream. Implementations of this method should ensure that
     * any buffered data is written out. This implementation does nothing.
     *
     * @throws IOException
     *             if an error occurs while flushing this stream.
     */
    public void flush() throws IOException {
    	synchronized (buffBlock) {
        	byte[] data = new byte[sendBuffer.position()];
        	sendBuffer.position(0);
        	sendBuffer.get(data,0,data.length);
            if(sendBuffer.position()>0){
            	FskEnCodeResult fskEnCodeResult = fskEncode.encode(data);
            	audioOperatorImp.playAudio(fskEnCodeResult.code, fskEnCodeResult.index);
            }
            sendBuffer.position(0);
    	}
    }
    
	public class FskOutputStreamInitException extends Exception{
		public FskOutputStreamInitException(String msg){
			super(msg);
		}
	}
}
