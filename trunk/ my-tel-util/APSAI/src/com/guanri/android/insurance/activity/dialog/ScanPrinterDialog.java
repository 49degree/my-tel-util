package com.guanri.android.insurance.activity.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.guanri.android.insurance.R;



/**
 * 扫描蓝牙打印机对话框
 * @author 
 *
 */
public class ScanPrinterDialog extends Dialog implements OnClickListener {
	private Button mCancelBtn = null;
	private Context context=null;
	private BluetoothAdapter mBtAdapter;//蓝牙适配器
	private ListView bluetoothlist = null;//蓝牙设备列表
	private List<Map<String,String>> lstDevices = new ArrayList<Map<String,String>>();//蓝牙设备列表
	private SimpleAdapter recordAdapter = null;
	private OnItemClickListener onItemClickListener = null;//蓝牙设备列表点击相应事件
	
	public ScanPrinterDialog(Context context,OnItemClickListener onItemClickListener) {
		super(context,R.style.TANCStyle);
		this.context =context;
		this.onItemClickListener = onItemClickListener;
	}

	public void displayDlg() {
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		this.setTitle(R.string.apsai_systeminfo_print_blth);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
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
//                if (device.getBondState() == BluetoothDevice.BOND_NONE) {   
//
//                }   
                
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
			if(mBtAdapter!=null){
				mBtAdapter.cancelDiscovery(); 
				context.unregisterReceiver(searchDevices);
			}
			dismiss();
		}
	}
	
	/**
	 * 蓝牙列表点击事件对象
	 * @author Administrator
	 *
	 */
	class BluetoothOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if(onItemClickListener != null){
				onItemClickListener.onItemClick(arg0, arg1, arg2, arg3);
			}
			if(mBtAdapter!=null){
				mBtAdapter.cancelDiscovery(); 
				context.unregisterReceiver(searchDevices);
			}
			dismiss();
		}
		
	}
}
