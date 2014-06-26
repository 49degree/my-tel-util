package com.skyeyes.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.exception.NetworkException;
import com.skyeyes.base.network.SkyeyeNetworkClient;
import com.skyeyes.base.network.SocketHandler;
import com.skyeyes.base.network.impl.SkyeyeSocketClient;

/**
 * 转换回调线程o
 * @author Administrator
 *
 */
public abstract class BaseSocketHandler implements SocketHandler {
	private Looper mLooper = null;
	protected Handler mHandler = null;
	protected SkyeyeNetworkClient mSkyeyeSocketClient = null;
	public BaseSocketHandler(Looper looper){
		// TODO Auto-generated constructor stub
		mLooper = looper;
		mHandler = new Handler(mLooper){
			public void handleMessage(Message msg){
				handleMessageEx(msg);
			}
		};
		
		
	}
	
	public BaseSocketHandler(){
		// TODO Auto-generated constructor stub
		this(Looper.getMainLooper());
	}

	public abstract void onReceiveCmdEx(ReceiveCmdBean receiveCmdBean);

	public abstract void onCmdExceptionEx(CommandParseException ex);

	public abstract void onSocketExceptionEx(NetworkException ex);

	public abstract void onSocketClosedEx();
	
	public abstract void handleMessageEx(Message msg);
	
	
	@Override
	public void setSkyeyeSocketClient(SkyeyeNetworkClient skyeyeSocketClient){
		mSkyeyeSocketClient = skyeyeSocketClient;
	}
	
	@Override
	public void onReceiveCmd(final ReceiveCmdBean receiveCmdBean) {
		// TODO Auto-generated method stub
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				onReceiveCmdEx(receiveCmdBean);
			}
			
		});
	}

	public void onCmdException(final CommandParseException ex){
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				onCmdExceptionEx(ex);
			}
			
		});
	}
	public void onSocketException(final NetworkException ex){
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				onSocketExceptionEx(ex);
			}
			
		});
	}
	
	
	public void onSocketClosed(){
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				onSocketClosedEx();
			}
			
		});
	}
	
	
	
}
