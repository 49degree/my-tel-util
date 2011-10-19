package com.szxys.mhub.ui.mets.activity;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import javax.security.auth.callback.Callback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.bean.Drinkandurine;
import com.szxys.mhub.subsystem.mets.dao.DrinkUrineDao;
import com.szxys.mhub.subsystem.mets.db.DrinkAndUrine;
import com.szxys.mhub.subsystem.mets.db.SysConfig;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;

/**
 * 修改 饮水量， 尿急， 尿失禁  
 * 
 * @author Administrator
 * 
 */
public class ModifyDrinkUrineActivity extends Activity implements OnClickListener {
	
	private static final int MYREQUEST_CODE = 12345;
	
	public static final int MODIFY_TIME_CODE = 2;

	private int recordId = 0;//业务ID
	//---
	private TextView topTitle = null;	//标题
	
	private TextView timeText = null;	//饮水时间， 尿急时间， 尿失禁时间
	private Button modifyButton = null; //修改按钮
//	private TextView showTime = null; //时间显示  直接在modifyButton中显示时间
	//  时间的格式显示： 0：yyyy年MM月dd日 HH:mm 1：yyyy年MM月dd日 HH时mm分   2： yyyy-MM-dd HH:mm
	private static int show_format = 1; 
	private String td_format;//存储当前的时间日期各式
	
	private TextView quantityText = null; //饮水，尿失禁 的量
	private EditText inputQuantity = null;  //输入量
	
	private Button saveButton = null;
	private Button cancelButton = null;
	
	LinearLayout linear = null;	
	
	private static final String TAG = "ModifyDrinkUrineActivity---";
	
	private String success_Submitted="提交成功";
	private String failed_Submitted="提交失败";
	private String invalid_Quantity="请输入正确的数值";
	private String later_than_Now="保存时间不能晚于现在";
	private String earlier_than_DeviceGrant="保存时间不能早于设备发放时间";//getResources().getString(R.string.mets_app_name);
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mets_drink_urine);
		
		topTitle = (TextView) this.findViewById(R.id.mets_drink_urine_toptitle);
		timeText = (TextView) this.findViewById(R.id.mets_drink_urine_time_text);
		modifyButton = (Button) this.findViewById(R.id.mets_bt_modify);
//		showTime = (TextView) this.findViewById(R.id.mets_drink_urine_showtime);
		quantityText = (TextView) this.findViewById(R.id.mets_drink_urine_quantity);
		inputQuantity = (EditText) this.findViewById(R.id.mets_drink_urine_quantity_ed);
		
		saveButton = (Button) this.findViewById(R.id.mets_bt_save);
		cancelButton = (Button) this.findViewById(R.id.mets_bt_cancel);
		
		modifyButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		
		//如果是尿急界面的话，隐藏这个LinearLayout
		linear = (LinearLayout) this.findViewById(R.id.urine_urgency_hide);
		init();
	}

	/**
	 * 初始化界面
	 */
	private void init() {
		Bundle extras=getIntent().getExtras(); //获取打开参数
		if(extras==null)
			return ;
		
		recordId = extras.getInt(UrineRecordActivity.PARAM_RECORD_ID);//获取记录ID
		//Drinkandurine drinkandurine = null;
		
		//根据ID查询记录  Edited by wxg
		//ArrayList<String> thedetails=DrinkAndUrine.getDrinkAndUrineInfoByCid(ModifyDrinkUrineActivity.this, String.valueOf(recordId));
		Drinkandurine objInAndOut=DrinkUrineDao.getDrinkUrineObjectByCid(recordId);
		String[] details=null;
		if (objInAndOut!=null) {
			Date theDate=TimeUtils.convertToDate(objInAndOut.getC_DateTime().trim());
			if(objInAndOut.getC_Type()==0){//修改饮水
				topTitle.setText(getResources().getString(R.string.mets_modify_drink_toptitle));
				timeText.setText(getResources().getString(R.string.mets_drink_time_text));
				quantityText.setText(R.string.mets_drink_quantity_text);
				modifyButton.setText(TimeUtils.getTimeString(theDate, TimeUtils.format1));
				inputQuantity.setText(String.valueOf(objInAndOut.getC_Quantity()));
			} else if (objInAndOut.getC_Type()==1){//修改尿量
				topTitle.setText(getResources().getString(R.string.mets_modify_urine_toptitle));
				timeText.setText(R.string.mets_urine_time_text);
				quantityText.setText(R.string.mets_urine_quantity_text);
				modifyButton.setText(TimeUtils.getTimeString(theDate, TimeUtils.format1));
				inputQuantity.setText(String.valueOf(objInAndOut.getC_Quantity()));
			} else if (objInAndOut.getC_Type()==2){//修改尿急
				linear.setVisibility(View.INVISIBLE); //不可见 
				topTitle.setText(getResources().getString(R.string.mets_modify_urinary_urgency_toptitle));
				timeText.setText(R.string.mets_urinary_urgency_time_text);
				quantityText.setText(R.string.mets_urinary_urgency_quantity_text);
				modifyButton.setText(TimeUtils.getTimeString(theDate, TimeUtils.format1));
				inputQuantity.setText(String.valueOf(objInAndOut.getC_Quantity()));
			} else if (objInAndOut.getC_Type()==3){//修改尿失禁
				topTitle.setText(getResources().getString(R.string.mets_modify_urinary_incontinence_toptitle));
				timeText.setText(R.string.mets_urinary_incontinence_time_text);
				quantityText.setText(R.string.mets_urinary_incontinence_quantity_text);
				modifyButton.setText(TimeUtils.getTimeString(theDate, TimeUtils.format1));
				inputQuantity.setText(String.valueOf(objInAndOut.getC_Quantity()));
			}
		}
		
		//drinkandurine = new Drinkandurine();//模拟数据
		//drinkandurine.setC_DateTime(TimeUtils.getTimeString(new Date(), TimeUtils.format1));//模拟数据
		//drinkandurine.setC_Type(3);//模拟数据

		

		//设置时间的显示各式
//		if(0 == show_format) {
//			td_format = TimeUtils.format;
//		} else if (1 == show_format) {
//			td_format = TimeUtils.format1;
//		} else if (2 == show_format) {
//			td_format = TimeUtils.format2;
//		}
//		
//		if(details[2]!="2"){//设置输入的值
//			//inputQuantity.setText(String.valueOf(drinkandurine.getC_Quantity()));
//			inputQuantity.setText(details[1]);
//		}
//		//modifyButton.setText(drinkandurine.getC_DateTime());//设置时间
//		modifyButton.setText(details[0]);
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 这里的resultCode得到的就是Activity1.java中设的RESULT_OK=-1，data就是mIntent
		//modify按钮 启动的Activity 回调后
		if (MODIFY_TIME_CODE == requestCode ) {// 
			
			if (RESULT_OK == resultCode) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					modifyButton.setText(extras.getString(ModifyTimeActivity.TIME_TEXT));
				}
			}
		}
	}


	/**
	 * 事件处理函数
	 */
	@Override
	public void onClick(View v) {
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.mets_bt_save://保存按钮
			ContentValues values=new ContentValues();
			Date dtSubmit=new Date();
			int seconds=dtSubmit.getSeconds();			
			String savedDtString=modifyButton.getText().toString().trim()+(seconds<10?("0"+seconds):seconds)+"秒"; //format4 = "yyyy年MM月dd日 HH时mm分ss秒";
			String quantity=inputQuantity.getText().toString().trim();
			Date DtTobeSaved=TimeUtils.getDateFromString(savedDtString,TimeUtils.format4);
			//比较DtTobeSaved 跟设备发放的时间和现在的时间
			Date dtDeviceGrant=SysConfig.getDeviceGrantTime(ModifyDrinkUrineActivity.this);
			if (DtTobeSaved.after(dtSubmit)) {
				Toast.makeText(ModifyDrinkUrineActivity.this,later_than_Now, Toast.LENGTH_LONG).show();
				return;
			}
			if (dtDeviceGrant!=null) {
				if (!DtTobeSaved.after(dtDeviceGrant)) {
					Toast.makeText(ModifyDrinkUrineActivity.this,earlier_than_DeviceGrant, Toast.LENGTH_LONG).show();
					return;
				}
			}			
			float savedQuantity=0;
			try {
				savedQuantity=Float.parseFloat(quantity);
			} catch (Exception e) {
				savedQuantity=-1;
			}
			//String where="c_Id=?";
			//String[] whereArgs={String.valueOf(recordId)};
			if (savedQuantity>0) {
				values.put("c_DateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DtTobeSaved));
				values.put("c_Quantity", savedQuantity);
				values.put("c_UniqueId", new SimpleDateFormat("yyyyMMddHHmmss").format(DtTobeSaved)+SystemClock.uptimeMillis());
				//int row=DrinkAndUrine.Update(ModifyDrinkUrineActivity.this, values, where, whereArgs);
				int row=DrinkUrineDao.modifyDrinkOrUrine(String.valueOf(recordId), values);
				if (row>0) {
					Toast.makeText(ModifyDrinkUrineActivity.this,success_Submitted, Toast.LENGTH_LONG).show();
				}else {
					Toast.makeText(ModifyDrinkUrineActivity.this,failed_Submitted, Toast.LENGTH_LONG).show();
					return;
				}
			}else {
				Toast.makeText(ModifyDrinkUrineActivity.this,invalid_Quantity, Toast.LENGTH_LONG).show();
				return;
			}			
			finish();
			break;
		case R.id.mets_bt_cancel://取消按钮事件
			finish();
			break;
		case R.id.mets_bt_modify:// 修改按钮事件
			Intent intent1 = new Intent(ModifyDrinkUrineActivity.this,ModifyTimeActivity.class);
			intent1.putExtra(ModifyTimeActivity.TIME_TEXT, modifyButton.getText().toString());
			//intent1.putExtra(ModifyTimeActivity.TIME_FORMAT, td_format);
			
			intent1.putExtra(ModifyTimeActivity.TIME_FORMAT, TimeUtils.format1);
			//Log.e(TAG, "modify bt --" + modifyButton.getText());
			/**
			 * 产生了格式问题，导致第二次提交时，Calend c.setTime() 出错
			 */
			startActivityForResult(intent1, MODIFY_TIME_CODE); // 启动这个intent跳转
			break;
		default:
			break;
		}
	}
	
	
}
