package com.guanri.android.insurance.command;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.lib.context.HandlerWhat;
import com.guanri.android.lib.log.Logger;

/**
 * 与服务器交互回调对象
 * @author Administrator
 *
 */
public class UpCommandHandler extends Handler{
	public static Logger logger = Logger.getLogger(UpCommandHandler.class);//日志对象
	
	private UpCommandHandlerListener upCommandHandlerListener = null;
	
	public UpCommandHandler(UpCommandHandlerListener upCommandHandlerListener) {
		super();
		this.upCommandHandlerListener = upCommandHandlerListener;
		
	}
	
	public void handleMessage(Message msg) {
		// 可以根据msg.what执行不同的处理，这里没有这么做
		
		switch (msg.what) {
		case HandlerWhat.NETWORK_REQUEST_ERROR_WHAT://网络通信错误信息
			logger.debug(CommandConstant.COMFIG_POS_ID+"");
			upCommandHandlerListener.handlerErrorMsg((String)msg.obj);
			break;
		case HandlerWhat.NETWORK_REQUEST_TIMEOUT_WHAT://连接访问超时
			upCommandHandlerListener.handlerTimeoutMsg((String)msg.obj);
			break;				
		default ://认为是其他消息
			upCommandHandlerListener.handlerOthorMsg(msg.what,msg.obj);
			break;
		}
	}
	

	
	public static abstract class UpCommandHandlerListener{
		private Context context = null;
		public UpCommandHandlerListener(Context context){
			this.context = context;
		}
		//处理网络通信错误信息
		public void handlerErrorMsg(String msg){
			logger.debug(CommandConstant.COMFIG_POS_ID+"");
			handlerInfo();
			
			showMessage(msg);
		}
		//处理连接访问超时信息
		public void handlerTimeoutMsg(String msg){
			handlerInfo();
			showMessage(msg);
		}
		
		private void showMessage(String msg){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(msg);
			builder.setTitle("命令上传失败");
			builder.setPositiveButton("确认",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			builder.create().show();
		}
		
		
		public abstract void handlerOthorMsg(int what,Object object);//处理具体下行命令
		public abstract void handlerInfo();//处理其他事务，比如隐藏进度条件，结束提示信息
	}
}
