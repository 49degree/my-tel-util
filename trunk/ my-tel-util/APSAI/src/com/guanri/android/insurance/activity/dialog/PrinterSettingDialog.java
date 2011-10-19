package com.guanri.android.insurance.activity.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.SystemConfigActivity;
import com.guanri.android.insurance.service.SystemConfigService;
import com.guanri.android.lib.utils.StringUtils;



/**
 * 打印机设置对话框
 * @author wuxiang
 *
 */
public class PrinterSettingDialog extends Dialog implements
android.view.View.OnClickListener {
	private Button mOkBtn = null;
	private Button mCancelBtn = null;
	private ImageButton blthBtn = null;
	private Button edtblthaddress = null;
	
	private EditText edtPrintwidth = null;
	private EditText edtPrintleft = null;
	private EditText edtPrintRight = null;

	private Context context=null;
	private SystemConfigService systemConfigDAO;

  
	
	public PrinterSettingDialog(Context context,SystemConfigService systemConfigDAO) {
		super(context);
		this.context =context;
		this.systemConfigDAO = systemConfigDAO;
	}

	public void displayDlg() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		setContentView(R.layout.sys_printer_setting_dialog);// 设置对话框的布局

		mOkBtn = (Button) findViewById(R.id.setting_in);
		mCancelBtn = (Button) findViewById(R.id.setting_out);
		blthBtn = (ImageButton)findViewById(R.id.blth_btn);
		edtblthaddress = (Button)findViewById(R.id.blth_btn_blthaddress);

		edtPrintwidth = (EditText) findViewById(R.id.edt_printwidth);
		edtPrintleft = (EditText) findViewById(R.id.edt_printleft);
		edtPrintRight = (EditText) findViewById(R.id.edt_printright);
		//初始化值
		edtblthaddress.setText(systemConfigDAO.getPrinterInfoBlthAdd());
		edtPrintwidth.setText(systemConfigDAO.getPrinterInfoWidth());
		edtPrintleft.setText(systemConfigDAO.getPrinterInfoLeft());
		edtPrintRight.setText(systemConfigDAO.getPrinterinfoRight());
		
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		blthBtn.setOnClickListener(this);
		
		show();// 显示对话框
	}

 
    
	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.setting_in) {

			String strBlthAddress = edtblthaddress.getText().toString();
			String strPrintwidth = edtPrintwidth.getText().toString();
			String strPrintleft = edtPrintleft.getText().toString();
			String strPrintright = edtPrintRight.getText().toString();
			//检查蓝牙地址合法性
			if(systemConfigDAO.PrinterInfoValuesCheck(strBlthAddress)){
				systemConfigDAO.savePrinterInfoValues(strBlthAddress,
						strPrintwidth,strPrintleft,strPrintright);
				Msgdialog msgdialog = new Msgdialog(context);
				msgdialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise));
				msgdialog.setMsgstr(StringUtils.getStringFromValue(R.string.apsai_insu_manager_data_success));
				msgdialog.setImageid(R.drawable.dialog_success);
				msgdialog.displayDlg();
				dismiss();
			}else{
				Msgdialog msgdialog = new Msgdialog(context);
				msgdialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise));
				msgdialog.setMsgstr(StringUtils.getStringFromValue(R.string.apsai_systeminfo_blthaddress_error));
				msgdialog.setImageid(R.drawable.dialog_failure);
				msgdialog.displayDlg();
			}		
			
		} else if (v.getId() == R.id.setting_out) {
			dismiss();
		} else if (v.getId() == R.id.blth_btn){
			ScanPrinterDialog scanPrinterDialog = new ScanPrinterDialog(context,new BluetoothOnItemClickListener());
			scanPrinterDialog.displayDlg();
		}
	}

	/**
	 * 选择蓝牙设备处理事件
	 * @author Administrator
	 *
	 */
	private class BluetoothOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Map<String,String> item = (Map<String,String>)arg0.getItemAtPosition(arg2);
			String address = item.get("address");
			edtblthaddress.setText(address);

		}
		
	}
	
}
