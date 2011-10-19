package com.szxys.mhub.ui.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SlidingRunnable implements Runnable {
	private final static float ACCELERATION = 0.1f;
	private final static int VELOCITY_FACT = 200;
	private static final int MES_UPDATE = 0;
	private float xOffset;
	private float yOffset;
	private boolean runFlag;
	private int accSignX;
	private int accSignY;
	private float sumOffset;
	private Handler handler;
	
	
	
	public SlidingRunnable(Handler handler) {
		super();
		this.handler = handler;
	}

	public void setXY(float x, float y) {
		xOffset = x/VELOCITY_FACT;
		yOffset = y/VELOCITY_FACT;
		accSignX = xOffset>0? 1:-1;
		accSignY = yOffset>0? 1:-1;
		sumOffset = (float) Math.sqrt(xOffset*xOffset + yOffset*yOffset);
	}

	public void setRunFlag(boolean flag) {
		// TODO Auto-generated method stub
		runFlag = flag;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Message msg;
		Bundle data = new Bundle();	
		float xFact =  Math.abs(xOffset) /sumOffset;
		float yFact =  Math.abs(yOffset) /sumOffset;
		while (runFlag && sumOffset>0) {
			sumOffset -= ACCELERATION;
			xOffset -= ACCELERATION * accSignX * xFact;			
			yOffset -= ACCELERATION * accSignY * yFact ;			
			msg= handler.obtainMessage();
			msg.what = MES_UPDATE;	
			data.putFloat("xOffset", xOffset);
			data.putFloat("yOffset", yOffset);
			msg.setData(data);
			msg.sendToTarget();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}