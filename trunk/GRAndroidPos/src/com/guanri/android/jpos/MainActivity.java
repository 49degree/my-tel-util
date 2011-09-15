package com.guanri.android.jpos;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.guanri.android.jpos.pos.SerialPortAndroid;
import com.guanri.android.jpos.pos.data.TerminalLinks.TAndroidCommTerminalLink;
import com.guanri.android.jpos.pos.data.TerminalParsers.TTerminalParser;
import com.guanri.android.jpos.services.GrPosService;
import com.guanri.android.lib.log.Logger;

public class MainActivity extends Activity implements OnClickListener {
	
	private EditText comm_state,pos_to_pad,pad_to_pos,pad_to_server,server_to_pad;
	Button btn_query,btn_login,btn_sale,btn_receive;
	final Logger logger = new Logger(MainActivity.class);
	StringBuffer result = new StringBuffer();
	
	/**
	 * 获取services绑定对象
	 */
	boolean mIsRemoteBound = false;
    private GrPosService mRemoteService;
	private ServiceConnection mRemoteConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mRemoteService = GrPosService .Stub.asInterface(service);
		}
		public void onServiceDisconnected(ComponentName className) {
			mRemoteService = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.querymoney);
		btn_receive = (Button)findViewById(R.id.btn_receive);
		//获取日志信息框对象
		comm_state = (EditText)findViewById(R.id.edt_log);
		pos_to_pad = (EditText)findViewById(R.id.edt_pos_to_pad);
		pad_to_pos = (EditText)findViewById(R.id.edt_pad_to_pos);
		pad_to_server = (EditText)findViewById(R.id.edt_pad_to_server);
		server_to_pad = (EditText)findViewById(R.id.edt_server_to_pad);
		comm_state.setText("mIsRemoteBound:"+mIsRemoteBound);
		
		//绑定服务
//		if (!mIsRemoteBound) {
//			bindService(new Intent("com.guanri.android.jpos.services.GrPosService"), mRemoteConnection,
//					Context.BIND_AUTO_CREATE);
//			mIsRemoteBound = !mIsRemoteBound;
//		}

		btn_receive.setOnClickListener(this);
		
	}
	
	int openTimes = 1;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_receive:
			if(!stopTask&&task!=null){
				stopTask = true;
				task = null;
				btn_receive.setText("打开接收数据");
				updateUI.sendMessage(updateUI.obtainMessage(1, "终端解析器 close..."));
			}else{
				
				task = new Thread(){
					public void run(){
						TAndroidCommTerminalLink TerminalLink = new TAndroidCommTerminalLink();
						TerminalLink.CommName = "/dev/ttyUSB0";
						TerminalLink.ReadTimeout = 5000;
						try{
							//循环直到打开串口
//							while(!stopTask&&!TerminalLink.GetConnected()){
//								if(SerialPortAndroid.findAndroidDevice(TerminalLink.CommName)){
//									TerminalLink.Connect();
//								}else{
//									updateUI.sendMessage(updateUI.obtainMessage(1, "串口不存在！"));
//									if(!TerminalLink.GetConnected()){
//										Thread.sleep(1000);
//									}
//								}
//							}
							if(SerialPortAndroid.findAndroidDevice(TerminalLink.CommName)){
								TerminalLink.Connect();
								stopTask = false;
								openTimes = 1;
							}else{
								updateUI.sendMessage(updateUI.obtainMessage(1, "串口不存在:"+openTimes++));
								stopTask = true;
							}
							if(!stopTask&&TerminalLink.GetConnected()){
								TTerminalParser TerminalParser = new TTerminalParser();
								TerminalParser.SetTerminalLink(TerminalLink);
								updateUI.sendMessage(updateUI.obtainMessage(1, "终端解析器正在运行..."));
								updateUI.sendMessage(updateUI.obtainMessage(0, "关闭接收数据"));
								while (!stopTask) {
									TerminalParser.ParseRequest();
								}
							}

						}catch(SecurityException se){
							updateUI.sendMessage(updateUI.obtainMessage(1, "open comm failed..."));
						}catch(Exception e){
							updateUI.sendMessage(updateUI.obtainMessage(1, "comm failed...:"+e.getMessage()));
						}finally{
							if(TerminalLink!=null){
								TerminalLink.Disconnect();
							}
						}
						stopTask = true;
					}
				};
				task.start();
				
			}
			

			break;
		default:
			break;
		}
	}
	
	private Thread task = null;
	public boolean stopTask = true;
	
    /**
     * 回调更新界面
     */
    public Handler updateUI = new Handler(){
        public void handleMessage(Message msg) {
        	if(msg.obj==null){
        		return;
        	}
        	if(msg.what==0){
        		btn_receive.setText((String)msg.obj);
        	}else if(msg.what==1){
        		comm_state.setText((String)msg.obj);
        	}else if(msg.what==2){
        		pos_to_pad.setText((String)msg.obj);
        	}else if(msg.what==3){
        		pad_to_pos.setText((String)msg.obj);
        	}else if(msg.what==4){
        		pad_to_server.setText((String)msg.obj);
        	}else if(msg.what==5){
        		server_to_pad.setText((String)msg.obj);
        	}
        }
    };
    

	
}
