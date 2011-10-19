package com.guanri.android.insurance.activity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.bean.InsuPlanBean;
import com.guanri.android.insurance.bean.InsuPrintBean;
import com.guanri.android.insurance.bean.InsuViewPlanBean;
import com.guanri.android.insurance.bean.InsuranceBean;
import com.guanri.android.insurance.insuplan.PareFileToObject;
import com.guanri.android.insurance.printer.BluetoothPrinter;
import com.guanri.android.insurance.printer.PrinterController;
import com.guanri.android.insurance.printer.PrinterEngine;
import com.guanri.android.insurance.service.InsuPlanManagerService;
import com.guanri.android.lib.bluetooth.BluetoothPool;
import com.guanri.android.lib.context.HandlerWhat;
import com.guanri.android.lib.network.NetWorkTools;

public class BluetoothPrintActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	EditText connectInfo = null;
	Button connectButton = null;
	Button connectButton2 = null;
	Button disconnectButton = null;
	Button printButton = null;
	Button netConnectButton = null;
	Button getInsuList = null;

	NetWorkTools netWorkTools = null;

	BluetoothPrinter bluetoothPrinter = null;

	private MainEventHandler mainHandler = null;

	private ProgressDialog btDialog = null;

	public static String strBTAddress1 = "00:1F:B7:02:4A:F4";// 蓝牙设备地址
	String strBTAddress2 = "00:1F:B7:03:D5:BF";// 蓝牙设备地址

	private final static Class printerClass = BluetoothPrinter.class;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		connectInfo = (EditText) this.findViewById(R.id.show_info);
		connectButton = (Button) this.findViewById(R.id.connect);
		connectButton2 = (Button) this.findViewById(R.id.connect2);

		disconnectButton = (Button) this.findViewById(R.id.disconnect);
		printButton = (Button) this.findViewById(R.id.print);
		netConnectButton = (Button) this.findViewById(R.id.network_connect);

		//getInsuList = (Button) this.findViewById(R.id.insulistdownload);

		connectButton.setOnClickListener(this);
		connectButton2.setOnClickListener(this);
		disconnectButton.setOnClickListener(this);
		printButton.setOnClickListener(this);
		netConnectButton.setOnClickListener(this);
		getInsuList.setOnClickListener(this);
		mainHandler = new MainEventHandler();// 初始化主线程消息队列
		netWorkTools = new NetWorkTools(this, mainHandler);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.connect:
			bluetoothPrinter = (BluetoothPrinter) BluetoothPool.getInstance()
					.getBluetoothFactory(strBTAddress1, printerClass);
			bluetoothPrinter.beginConnectBth(mainHandler);
			btDialog = new ProgressDialog(this);
			btDialog.setTitle("连接打印机1"); // title
			btDialog.setMessage("请等待连接 ...");// 进度是否是不确定的，这只和创建进度条有关
			btDialog.setButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					bluetoothPrinter.disConnected(mainHandler);// 通知蓝牙打印模块进行业务处理
					btDialog.dismiss();
				}
			});
			btDialog.show();
			break;
		case R.id.connect2:
			bluetoothPrinter = (BluetoothPrinter) BluetoothPool.getInstance()
					.getBluetoothFactory(strBTAddress2, printerClass);
			bluetoothPrinter.beginConnectBth(mainHandler);
			btDialog = new ProgressDialog(this);
			btDialog.setTitle("连接打印机2"); // title
			btDialog.setMessage("请等待连接 ...");// 进度是否是不确定的，这只和创建进度条有关
			btDialog.setButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					bluetoothPrinter.disConnected(mainHandler);// 通知蓝牙打印模块进行业务处理
					btDialog.dismiss();
				}
			});
			btDialog.show();
			break;
		case R.id.disconnect:
			BluetoothPool.getInstance().releasBluetooth(mainHandler);// 释放蓝牙连接
			btDialog = new ProgressDialog(this);
			btDialog.setTitle("断开打印机"); // title
			btDialog.setMessage("请等待断开连接 ...");// 进度是否是不确定的，这只和创建进度条有关
			btDialog.setButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					btDialog.dismiss();
				}
			});
			btDialog.show();
			break;
		case R.id.print:

			/*
			 * try{
			 * bluetoothPrinter.sendData(mainHandler,EncodingUtils.getBytes(
			 * "进度是否是不确定的，这只和创建进度条有关","GBK")); for(int i=0;i<10;i++){
			 * bluetoothPrinter.sendData(mainHandler,"\n".getBytes()); }
			 * 
			 * }catch(Exception e){ e.printStackTrace(); }
			 */

			InsuViewPlanBean insuViewPlanBean = PareFileToObject
					.pareInsuViewPlanBean("HYX_TK00.edt");
			InsuPrintBean insuPrintBean = PareFileToObject
					.pareInsuPrintBean("HYX_TK01.prn");
			InsuPlanBean insuPlanBean = PareFileToObject
					.pareInsuPlanBean("MUS1000704.txt");

			InsuranceBean insuranceBean = new InsuranceBean();
			insuranceBean.setInsuPlanBean(insuPlanBean);
			insuranceBean.setInsuPrintBean(insuPrintBean);
			insuranceBean.setInsuViewPlanBean(insuViewPlanBean);

			Map<String, String> inputValueMap = new HashMap<String, String>();

//			PrinterEngine prtEngine = new PrinterEngine(mainHandler,
//					insuranceBean, inputValueMap);
//			prtEngine.printInsuOrder();

			// BluetoothFactory.getInstance().printInfo(mainHandler,
			// "print test");
			break;
		case R.id.network_connect:
			if (netWorkTools.getNetWorkInfo() == netWorkTools.NET_TYPE_NONE) {
				new Thread() {
					public void run() {
						netWorkTools.autoConnect();
					}
				}.start();
				btDialog = new ProgressDialog(this);
				btDialog.setTitle("配置网络连接"); // title
				btDialog.setMessage("请等待配置网络连接 ...");// 进度是否是不确定的，这只和创建进度条有关
				btDialog.setButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						btDialog.dismiss();
					}
				});
				btDialog.show();
			}
			break;
//		case R.id.insulistdownload: {
//			Intent intent = new Intent();
//			intent.setClass(this, InsuPlanManagerActivity.class);
//			this.startActivity(intent);
//		}
//			break;
		default:
			break;
		}
	}

	public void onDestroy() {
		super.onDestroy();
		netWorkTools.cancelMmonitor();
	}

	// 监听键盘事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("确定要退出吗?");
			builder.setTitle("提示");
			builder.setPositiveButton("确认",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							try {
								netWorkTools.cancelMmonitor();
								BluetoothPool.getInstance().releasBluetooth(
										mainHandler);// 释放蓝牙连接
								finish();// 退出程序
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			builder.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();

		}
		return false;
	}

	public class MainEventHandler extends Handler {
		public MainEventHandler() {
			super();
		}

		public void handleMessage(Message msg) {
			if (msg.what == HandlerWhat.BLUE_THOOTH_CONNECT_RESULE) {
				if (msg.obj.equals(new Boolean(true))) {
					connectInfo.setText("蓝牙已经连接");
				} else {
					connectInfo.setText("蓝牙已经断开");
				}
			} else {
				if (msg.obj.equals(new Boolean(true))) {
					connectInfo.setText("网络已经连接");
				} else {
					connectInfo.setText("网络已经断开");
				}
			}
			if (btDialog != null && btDialog.isShowing()) {
				btDialog.dismiss();
			}

		}
	}

	public void printSomeThing() {
		try {

			byte[] cmd = PrinterController
					.getPrinterCmd(PrinterController.PRINTER_CMD_ESC_W);
			bluetoothPrinter.sendData(mainHandler, cmd);
			InputStream fileIn = this.getApplication().getResources()
					.getAssets().open("order.txt");

			byte[] fileBytes = new byte[1024];
			int readLth = 0;

			while ((readLth = fileIn.read(fileBytes)) > 0) {
				bluetoothPrinter.sendData(mainHandler, EncodingUtils.getBytes(
						new String(fileBytes, 0, readLth, "GBK"), "GBK"));
			}
			byte[] cmd2 = PrinterController
					.getPrinterCmd(PrinterController.PRINTER_CMD_ESC_AT);
			bluetoothPrinter.sendData(mainHandler, cmd2);
			bluetoothPrinter.sendData(mainHandler, "\n".getBytes());
			fileIn = this.getApplication().getResources().getAssets()
					.open("order.txt");

			while ((readLth = fileIn.read(fileBytes)) > 0) {
				bluetoothPrinter.sendData(mainHandler, EncodingUtils.getBytes(
						new String(fileBytes, 0, readLth, "GBK"), "GBK"));
			}

			for (int i = 0; i < 10; i++) {
				bluetoothPrinter.sendData(mainHandler, "\n".getBytes());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
