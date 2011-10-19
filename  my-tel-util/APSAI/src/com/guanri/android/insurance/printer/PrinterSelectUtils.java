package com.guanri.android.insurance.printer;

import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.dialog.ScanPrinterDialog;
import com.guanri.android.insurance.common.DialogUtils;
import com.guanri.android.insurance.common.DialogUtils.OnAlertDlgCancelBtn;
import com.guanri.android.insurance.common.DialogUtils.OnAlertDlgSureBtn;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.lib.bluetooth.BluetoothDeviceImp;
import com.guanri.android.lib.bluetooth.BluetoothPool;
import com.guanri.android.lib.context.HandlerWhat;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;

public class PrinterSelectUtils {
	private static Logger logger = Logger.getLogger(PrinterSelectUtils.class);
	/**
	 * 打印对账信息
	 * 可以打印对账信息（可选、也可以不打印，如果打印，要提示使用者可能要换纸），
	 * 要打印的信息： 终端业务ID、操作员ID、对账时间、批次号、售单比数、售单金额（到分）、
	 * 退单比数、退单金额、废单比数；打印后提示使用者是否打印详单
	 */
	private static BluetoothPrinter mBluetoothPrinter = null;
	private static String printerAddr = null;
	private static Handler connectBthHandler = null;//蓝牙连接结果处理对象
	private static OnItemClickListener itemClickMethod = null;//选中蓝牙设备处理对象
	private final static Class<BluetoothPrinter> printerClass = BluetoothPrinter.class;
	private static boolean isCancel = false;
	public static void printCheckResult(final Context context,final OnPrintConnectedListen onPrintConnectedListen) {
		final ProgressDialog btDialog = new ProgressDialog(context);
		btDialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_blth_printer)); // title     
		btDialog.setMessage(StringUtils.getStringFromValue(R.string.apsai_common_blth_print_connecting));//进度是否是不确定的，这只和创建进度条有关
		btDialog.show();
		isCancel = false; 
		btDialog.setOnKeyListener(new OnKeyListener(){
			//拦截系统返回键
			 public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
		        if (keyCode == KeyEvent.KEYCODE_BACK) {
//		        	dialog.dismiss();
//		        	logger.error("_______________");
//					if(mBluetoothPrinter!=null){
//						logger.error("++++++++++++++++++");  
//						isCancel = true;
//						mBluetoothPrinter.cancelBondProcess(); 
//					}
		        	return true;
		        }
		        return false;
		    }
		});
		
		printerAddr = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.PRINTER_INFO,SharedPreferencesUtils.PRINTER_ADD);
		//选中蓝牙设备处理对象
		connectBthHandler = new Handler(){
			public void handleMessage(Message msg) {
				if (msg.what == HandlerWhat.BLUE_THOOTH_CONNECT_RESULE) {
					if ((Boolean)msg.obj) {//连接成功
						SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.PRINTER_INFO, SharedPreferencesUtils.PRINTER_ADD, printerAddr);
						onPrintConnectedListen.onPrintConnected();
					}else{//连接失败，是否重新连接
						if(!isCancel){//如果已经取消连接则不进行提示
							StringBuffer disConnStr = new StringBuffer(StringUtils.getStringFromValue(R.string.apsai_common_blth_printer));
							disConnStr.append(printerAddr).append(StringUtils.getStringFromValue(R.string.apsai_common_blth_printer_reconnect));
							DialogUtils.showChoiceAlertDlg(context, context.getString(R.string.apsai_common_blth_printer),
									disConnStr.toString(), new OnAlertDlgSureBtn(){
				    			public void OnSureBtn(){
									ScanPrinterDialog scanPrinterDialog = new ScanPrinterDialog(context,itemClickMethod);
									scanPrinterDialog.displayDlg();
				    			}
				    		});
						}
					}
					btDialog.dismiss();
				}
			}
		};
		
		//选中蓝牙设备处理对象
		itemClickMethod = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Map<String,String> item = (Map<String,String>)arg0.getItemAtPosition(arg2);
				printerAddr = item.get("address");
				mBluetoothPrinter = (BluetoothPrinter) BluetoothPool.getInstance().getBluetoothFactory(printerAddr,printerClass);//获取打印机对象
				if(!mBluetoothPrinter.isConnected()){
					btDialog.setMessage(context.getString(R.string.apsai_common_blth_print_connecting));
					btDialog.show();
					mBluetoothPrinter.beginConnectBth(connectBthHandler,1);
				}else{
					SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.PRINTER_INFO, SharedPreferencesUtils.PRINTER_ADD, printerAddr);
					onPrintConnectedListen.onPrintConnected();
					btDialog.dismiss();
				}
			}
		};
		
		if(printerAddr==null||"".equals(printerAddr)){//没有配置蓝牙打印机
			ScanPrinterDialog scanPrinterDialog = new ScanPrinterDialog(context,itemClickMethod);
			scanPrinterDialog.displayDlg();
			btDialog.dismiss();
		}else{
			mBluetoothPrinter = (BluetoothPrinter) BluetoothPool.getInstance().getBluetoothFactory(printerAddr,printerClass);//获取打印机对象
			if(!mBluetoothPrinter.isConnected()){
				//是否试用当前配置中的打印机
				StringBuffer disConnStr = new StringBuffer(context.getString(R.string.apsai_common_blth_print_use_now));
				disConnStr.append(printerAddr).append("?");
				DialogUtils.showChoiceAlertDlg(context, context.getString(R.string.apsai_common_blth_printer),
						disConnStr.toString(), new OnAlertDlgSureBtn(){
	    			public void OnSureBtn(){
	    				btDialog.show();
	    				mBluetoothPrinter.beginConnectBth(connectBthHandler,1);
	    			}
	    		},new OnAlertDlgCancelBtn(){
	    			public void OnCancelBtn(){
	    				ScanPrinterDialog scanPrinterDialog = new ScanPrinterDialog(context,itemClickMethod);
	    				scanPrinterDialog.displayDlg();
	    			}
	    		});
			}else{
				onPrintConnectedListen.onPrintConnected();
			}
			btDialog.dismiss();
		}
	}
	
	public interface OnPrintConnectedListen{
		public void onPrintConnected();
	}
}
