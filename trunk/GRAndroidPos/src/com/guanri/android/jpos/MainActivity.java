package com.guanri.android.jpos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.guanri.android.dilog.CheckPSWDialog;
import com.guanri.android.dilog.UpdateUserDialog;
import com.guanri.android.jpos.services.AidlRunService;
import com.guanri.android.jpos.services.GrPosService;
import com.guanri.android.lib.log.Logger;


public class MainActivity extends Activity implements OnClickListener {
	
	private EditText comm_state,pos_to_pad,pad_to_pos,pad_to_server,server_to_pad;
	Button btn_query,btn_login,btn_sale,btn_receive,btn_stop,btn_modify_server,btn_modify_pwd;
	final Logger logger = new Logger(MainActivity.class);
	StringBuffer result = new StringBuffer();
	public LogTask logTask= null;
	
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
		setContentView(R.layout.main);
		//btn_receive = (Button)findViewById(R.id.btn_receive);
		btn_stop  = (Button)findViewById(R.id.btn_stop);
		btn_modify_server  = (Button)findViewById(R.id.btn_modify_server);
		btn_modify_pwd  = (Button)findViewById(R.id.btn_modify_pwd);
		
		//获取日志信息框对象
		comm_state = (EditText)findViewById(R.id.edt_log);
		pos_to_pad = (EditText)findViewById(R.id.edt_pos_to_pad);
		//pad_to_pos = (EditText)findViewById(R.id.edt_pad_to_pos);
		pad_to_server = (EditText)findViewById(R.id.edt_pad_to_server);
		//server_to_pad = (EditText)findViewById(R.id.edt_server_to_pad);
		comm_state.setText("mIsRemoteBound:"+mIsRemoteBound);
		
		logTask = new LogTask();
		logTask.start();

		//btn_receive.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btn_modify_pwd.setOnClickListener(this);
		btn_modify_server.setOnClickListener(this);
		
		Intent service = new Intent(this,AidlRunService.class);
		this.startService(service);
		//绑定服务
		bindService();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, "查询数据库");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		if (item.getItemId() == 1) {
			// 重新下载服务列表
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setMessage("数据库查询");
//			builder.setTitle("数据库查询");
//			builder.create().show();
			logger.debug("test");
		}
		return true;
	}
	
	public void onDestroy(){
		super.onDestroy();
		stopLog = true;
		if(logTask!=null)
			logTask.interrupt();
		
	}
	
	public boolean stopTask = true;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		/**
		case R.id.btn_receive:
			if(!stopTask){//关闭
				stopTask = true;
				//停止运行
				try{
					mRemoteService.stopPos();
				}catch(Exception e){
					e.printStackTrace();
				}
				btn_receive.setText("开始接收数据");
			}else{//打开
				if(hasCommPort){
					try{
						mRemoteService.startPos();
						stopTask = false;
						btn_receive.setText("停止接收数据");
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					displayMsg("失败","POS终端未找到");
				}
			}
			break;*/
		case R.id.btn_modify_server:
			logger.error("修改地址");
			new CheckPSWDialog(this).displayDlg();//先打开验证密码对话框，密码正确再打开服务器信息修改对话框
			break;
		case R.id.btn_modify_pwd:
			new UpdateUserDialog(this).displayDlg();
			break;			
		case R.id.btn_stop:
			try{
				unBindService();
			}catch(Exception e){
				e.printStackTrace();
			}
			Intent service = new Intent(this,AidlRunService.class);
			this.stopService(service);
			finish();
			break;
		default:
			break;
		}
	}
	/**
	 * 绑定服务
	 */
	public void bindService(){
		//绑定服务
		if (!mIsRemoteBound) {
			bindService(new Intent("com.guanri.android.jpos.services.GrPosService"), mRemoteConnection,
					Context.BIND_AUTO_CREATE);
			mIsRemoteBound = !mIsRemoteBound;
		}
	}
	
	/**
	 * 解绑服务
	 */
	public void unBindService(){
		//解绑定服务
		if (mIsRemoteBound) {
			this.unbindService(mRemoteConnection);
			mIsRemoteBound = !mIsRemoteBound;
		}
	}
	boolean stopLog = false;
	boolean hasCommPort = false;
    public class LogTask extends Thread{
    	public void run(){
    		// 循环直到打开串口
    		while (!stopLog) {
    			try {
    				//logger.error("LogTask.........11:"+mRemoteService.operate("pos_to_pad")+":"+(mRemoteService==null?"":mRemoteService.operate("LOG_INFO")));
    				updateUI.sendMessage(updateUI.obtainMessage(1,mRemoteService.operate("LOG_INFO")));
    				updateUI.sendMessage(updateUI.obtainMessage(2, mRemoteService.operate("pos_to_pad")));
    				updateUI.sendMessage(updateUI.obtainMessage(3, mRemoteService.operate("pad_to_pos")));
    				updateUI.sendMessage(updateUI.obtainMessage(4, mRemoteService.operate("pad_to_server")));
    				updateUI.sendMessage(updateUI.obtainMessage(5, mRemoteService.operate("server_to_pad")));
    				if(mRemoteService.hasCommPort()){
    					hasCommPort = true;
    				}else{
    					hasCommPort = false;
    				}
    				Thread.sleep(500);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
	
	
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
        	}/*else if(msg.what==2){
        		if(!((String)msg.obj).equals(pos_to_pad.getText().toString())){
            		pos_to_pad.setText((String)msg.obj);
            		pos_to_pad.setSelection(pos_to_pad.length());//将光标移至文字末尾
        		}

        	}else if(msg.what==3){
        		//pad_to_pos.setText((String)msg.obj);
        		//pad_to_pos.setSelection(pad_to_pos.length());//将光标移至文字末尾
        	}else if(msg.what==4){
        		if(!((String)msg.obj).equals(pad_to_server.getText().toString())){
        			pad_to_server.setText((String)msg.obj);
        			pad_to_server.setSelection(pad_to_server.length());//将光标移至文字末尾
        		}
        	}else if(msg.what==5){
        		//server_to_pad.setText((String)msg.obj);
        		//server_to_pad.setSelection(server_to_pad.length());//将光标移至文字末尾
        	}
        	*/
        }
    };
    
	private void displayMsg(String title,String msg) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Error");
		b.setMessage(msg);
		b.setPositiveButton(title, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		b.show();
	}
	
}
