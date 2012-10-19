package com.blue;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bluetooth.TypeConversion;
import com.log.Logger;

public class BluetoothTesterActivity extends Activity {
	private static Logger logger = Logger.getLogger(BluetoothTesterActivity.class);
	// Debugging
	private static final String TAG = "BluetoothChatService";

	private Button find = null;
	private Button startServer = null;
	private EditText edit = null;
	private EditText edit_start = null;

	private BluetoothDeviceImp mBluetooth = null;
	private BluetoothServer bluetoothServer = null;
	protected ProgressDialog progress = null; 
	private EditText clickEdit = null;
	private byte[] buffer = new byte[1024];
	private int readLength = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		find = (Button) findViewById(R.id.find);
		edit = (EditText)this.findViewById(R.id.edit);
		edit_start = (EditText)this.findViewById(R.id.edit_start);
		
		startServer = (Button)this.findViewById(R.id.start);
		
		

	}
	
	public void onStart(){
		super.onStart();
		startServer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				bluetoothServer = new BluetoothServer();
				bluetoothServer._serverWorker.setDaemon(true);
				bluetoothServer._serverWorker.start();
				arg0.setEnabled(false);
			}
		});
		
		final EditViewClickListen editViewClickListen = new EditViewClickListen();
		find.setOnClickListener(editViewClickListen);
		//失去焦点是判断值是否超出范围
		edit.setOnFocusChangeListener(new OnFocusChangeListener() { 
			@Override
			public void onFocusChange(View v, boolean hasFocus) { 
				if(hasFocus) {
					editViewClickListen.onClick(v);
				}
			}
		});
		//失去焦点是判断值是否超出范围
		edit_start.setOnFocusChangeListener(new OnFocusChangeListener() { 
			@Override
			public void onFocusChange(View v, boolean hasFocus) { 
				if(hasFocus) {
					editViewClickListen.onClick(v);
				}
			}
		});
	
		
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
        if (keyCode == KeyEvent.KEYCODE_BACK ) { 
        	logger.error("onKeyDown+++++++++++++");
        	onDespear();
        }  
		return false;
	}
	
	public void onDestroy(){
		logger.error("onDestroy+++++++++++++");
		onDespear();
		super.onDestroy();
	}


	private void onDespear(){
		if(bluetoothServer!=null)
			bluetoothServer.shutdownServer();
		if(mBluetooth!=null){
			logger.error("mBluetooth disConnected+++++++++++++");
			mBluetooth.disConnected();
		}
	}
	
	/**
	 * 获取蓝牙扫描对象
	 * @author xueping.yang
	 *
	 */
	private class EditViewClickListen implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			logger.error("view:"+arg0.getClass());
			if(mBluetooth==null||!mBluetooth.isConnected()){
				ScanPrinterDialog queryDialog = 
						new ScanPrinterDialog(BluetoothTesterActivity.this,new BluetoothChoice());
				queryDialog.displayDlg();
			}
			if(!(arg0 instanceof EditText)){
				return ;
			}
			if(!arg0.equals(clickEdit)){
				readLength = 0;
				clickEdit = (EditText)arg0;
				clickEdit.setText("");
			}
			
			
			switch (arg0.getId()) {
			case R.id.edit:
				break;
			case R.id.edit_start:
				break;
			default:
				break;
			}
		}
	}
	

	
	//选中蓝牙设备处理对象
	Handler connectBthHandler = new Handler(){
		public void handleMessage(Message msg) {

			switch(msg.what){
			case BluetoothDeviceImp.BLUE_THOOTH_CONNECT_RESULE:
				logger.error("progress.dismiss() begin");
				if(progress!=null&&progress.isShowing())
					progress.dismiss();
				logger.error("progress.dismiss() end");
				if (!(Boolean)msg.obj) {//连接失败，是否重新连接
					if(mBluetooth!=null){
						mBluetooth.cancelBondProcess();
						mBluetooth.disConnected();
					}
					mBluetooth = null;
					com.DialogUtils.showErrorAlertDlg(BluetoothTesterActivity.this, "提示", "连接失败");
				}
				break;
			case BluetoothDeviceImp.BLUE_THOOTH_READ_RESULE:
				if(clickEdit!=null){
					buffer[readLength++] = ((Integer)msg.obj).byteValue();
					clickEdit.setText(new String(buffer,0,readLength));
				    logger.error("++++++++++数据："+TypeConversion.byte2hex(buffer,0,readLength));
				}
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 选中蓝牙设备
	 * @author xueping.yang
	 */
	public class BluetoothChoice implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
        	Map<String,String> map = lstDevices.get(position);
        	mBluetooth = new BluetoothDeviceImp(map.get("address"),"0000");
			mBluetooth.beginConnectBth(connectBthHandler);
			progress = ProgressDialog.show(BluetoothTesterActivity.this, "请稍候", "正在连接....");
			progress.setCanceledOnTouchOutside(false);
			progress.setOnKeyListener(new android.content.DialogInterface.OnKeyListener(){
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					// TODO Auto-generated method stub
	                if (keyCode == KeyEvent.KEYCODE_BACK ) {   
	                	progress.dismiss();
						if(mBluetooth!=null){
							mBluetooth.cancelBondProcess();
							mBluetooth.disConnected();
						}
	                    return true;   
	                }  
					return false;
				}
		    });
			progress.setOnDismissListener(new OnDismissListener(){
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
				}
			});
		}
	}
	
	
	private List<Map<String,String>> lstDevices = new ArrayList<Map<String,String>>();//蓝牙设备列表
	/**
	 * 扫描蓝牙设备对话框
	 * @author 
	 */
	public class ScanPrinterDialog extends Dialog implements OnClickListener {
		private Button mCancelBtn = null;
		private Context context=null;
		private BluetoothAdapter mBtAdapter;//蓝牙适配器
		private ListView bluetoothlist = null;//蓝牙设备列表
		
		private SimpleAdapter recordAdapter = null;
		private OnItemClickListener onItemClickListener = null;//蓝牙设备列表点击相应事件
		
		public ScanPrinterDialog(Context context,OnItemClickListener onItemClickListener) {
			super(context);
			this.context =context;
			this.onItemClickListener = onItemClickListener;
			lstDevices.clear();
			setOnDismissListener(new OnDismissListener(){
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					logger.error("撤销蓝牙搜索++++++++++++++++");
					ScanPrinterDialog.this.context.unregisterReceiver(searchDevices);
					if(mBtAdapter!=null){
						mBtAdapter.cancelDiscovery();
					}
				}
			});
		}

		public void displayDlg() {
			//requestWindowFeature(Window.FEATURE_NO_TITLE);
			requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			this.setTitle(R.string.apsai_systeminfo_print_blth);
//			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//					WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
			setContentView(R.layout.sys_scan_printer_dialog);// 设置对话框的布局
	        getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS,Window.PROGRESS_VISIBILITY_ON);
	        
			mCancelBtn = (Button) findViewById(R.id.setting_out);
			mCancelBtn.setOnClickListener(this);
			//配置蓝牙设备列表
			bluetoothlist = (ListView)this.findViewById(R.id.bluetoothlist);
			String[] queryParam = {"name","address"};
	        int[] to = new int[] {R.id.bluetooth_name, R.id.bluetooth_address};
	        recordAdapter = new SimpleAdapter(context, lstDevices, R.layout.sys_printer_bluetooth_item,queryParam, to);
	        bluetoothlist.setAdapter(recordAdapter);   
			bluetoothlist.setOnItemClickListener(new BluetoothOnItemClickListener());

			// 注册Receiver来获取蓝牙设备相关的结果   
	        IntentFilter intent = new IntentFilter();   
	        intent.addAction(BluetoothDevice.ACTION_FOUND);   
	        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);   
			show();// 显示对话框
			try{
				mBtAdapter = BluetoothAdapter.getDefaultAdapter();// 初始化本机蓝牙功能   
		        // 开始搜索   
				if(mBtAdapter!=null){
					if(!mBtAdapter.isEnabled()){
						mBtAdapter.enable();
					}
					context.registerReceiver(searchDevices, intent);
			        mBtAdapter.startDiscovery();
				}
			}catch(Exception e){
				
			}
		}

		//蓝牙设备查询结果广播接收器
		private BroadcastReceiver searchDevices = new BroadcastReceiver() {   
	        public void onReceive(Context context, Intent intent) {   
	            String action = intent.getAction();   
	            Bundle b = intent.getExtras();   
	            Object[] lstName = b.keySet().toArray();   
	  
	            // 显示所有收到的消息及其细节   
	            for (int i = 0; i < lstName.length; i++) {   
	                String keyName = lstName[i].toString();   
	                Log.e(keyName, String.valueOf(b.get(keyName)));   
	            }   
	            // 搜索设备时，取得设备的MAC地址   
	            if (BluetoothDevice.ACTION_FOUND.equals(action)) {   
	                BluetoothDevice device = intent   
	                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);   

	            	Map<String,String> map = new HashMap<String,String>();
	            	map.put("name", device.getName());
	            	map.put("address", device.getAddress());
	            	lstDevices.add(map); // 获取设备名称和mac地址   
	            	recordAdapter.notifyDataSetChanged();
	                //bluetoothlist.setAdapter(recordAdapter); 
	            }   
	        }   
	    };   
	    
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.setting_out) {
				dismiss();
			}
		}
		
		/**
		 * 蓝牙列表点击事件对象
		 * @author Administrator
		 *
		 */
		private class BluetoothOnItemClickListener implements OnItemClickListener {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				dismiss();
				if(onItemClickListener != null){
					onItemClickListener.onItemClick(arg0, arg1, arg2, arg3);
				}
				
			}
		}
	}
	
	/**
	 * 蓝牙服务端
	 * @author xueping.yang
	 *
	 */
	public class BluetoothServer{
		/* 取得默认的蓝牙适配器 */
		public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
		public static final String UUIDStr = "A60F35F0-B93A-11DE-8A39-08002009C666";
		private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
		/* 蓝牙服务器 */
		private BluetoothServerSocket _serverSocket;

		/* 线程-监听客户端的链接 */
		private Thread _serverWorker = new Thread() {
			public void run() {
				listen();
			};
		};
		/* 停止服务器 */
		private void shutdownServer() {
			new Thread() {
				public void run() {
					_serverWorker.interrupt();
					if (_serverSocket != null) {
						try {
							/* 关闭服务器 */
							_serverSocket.close();
						} catch (IOException e) {
							Log.e(TAG, "", e);
						}
						_serverSocket = null;
					}
					logger.error("服务关闭---------------");
				};
			}.start();
		}
		protected void listen() {
			try {
				/* 创建一个蓝牙服务器 
				 * 参数分别：服务器名称、UUID
				 */
				_serverSocket = _bluetooth.listenUsingRfcommWithServiceRecord(
						PROTOCOL_SCHEME_RFCOMM,UUID.fromString(UUIDStr));
				while(_serverSocket!=null){
					/* 接受客户端的连接请求 */
					logger.error("服务启动+++++++++++++++++++");
					BluetoothSocket socket = _serverSocket.accept();
					try{
						logger.error("客户端接入+++++++++++++++++++"+socket.getRemoteDevice().getAddress()+""+socket.getRemoteDevice().getName());
						/* 处理请求内容 */
						if (socket != null) {
							OutputStream outputStream = socket.getOutputStream();
							
							if(edit_start.getText().toString().trim().equals("")){
								outputStream.write("has null datas".getBytes());
							}else{
								outputStream.write(edit_start.getText().toString().getBytes());
							}
							outputStream.write(new byte[]{(int)-1});
							outputStream.flush();
							
							Thread.sleep(2000);
							outputStream.close();
							socket.close();
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "", e);
			} finally {
			}
		}
	}


}