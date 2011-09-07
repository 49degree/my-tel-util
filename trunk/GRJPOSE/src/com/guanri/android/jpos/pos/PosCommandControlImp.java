package com.guanri.android.jpos.pos;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 串口通信 抽象父类
 * @author Administrator
 *
 */
public abstract class PosCommandControlImp {
	static Logger logger = Logger.getLogger(PosCommandControlImp.class);
	
	public abstract void sendData(byte[] data,SendDataResultListener sendDataResultListener,long waitTime) throws IOException;
	public abstract void portClose();
	
	public static abstract class SendDataResultListener{
		public abstract void onSendDataResult(byte[] returnData);
		public boolean isDone = false;
	}
}
