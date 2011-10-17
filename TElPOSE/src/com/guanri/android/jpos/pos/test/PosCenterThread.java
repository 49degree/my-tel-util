package com.guanri.android.jpos.pos.test;

import com.guanri.android.jpos.pos.data.TerminalParsers.TTerminalParser;
import com.guanri.android.lib.log.Logger;

public class PosCenterThread extends Thread{
	static Logger logger = Logger.getLogger(PosCenterThread.class);

	public boolean stopTask = true;
	public static PosCenterThread instance = new PosCenterThread();
	
	public static PosCenterThread getInstance(){
		return instance;
	}
	
	
	private PosCenterThread(){

	}
	
	@Override
	public void run() {
		// 循环直到打开串口
		logger.error("Thread is starting......."+stopTask);
		while (!stopTask) {
			
	    	try{
	    		Thread.sleep(2000);	
	    	}catch(Exception e){
	    		
	    	}
	    	logger.error("Thread is starting.......");
		}
	}
	

	public static void main(String[] args){
    	PosCenterThread.getInstance().stopTask = false;
    	PosCenterThread.getInstance().start();
    	logger.error("Thread is starting222222:"+PosCenterThread.getInstance().isAlive());
    	
    	try{
    		Thread.sleep(1000);
    		//PosCenterThread.getInstance().start();
    		logger.error("Thread is starting:"+PosCenterThread.getInstance().isAlive());
    		PosCenterThread.getInstance().stopTask = true;
    		logger.error("Thread is starting:"+PosCenterThread.getInstance().isAlive());
    		Thread.sleep(2000);
        	logger.error("Thread is starting:"+PosCenterThread.getInstance().isAlive());
        	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
	}


	
}
