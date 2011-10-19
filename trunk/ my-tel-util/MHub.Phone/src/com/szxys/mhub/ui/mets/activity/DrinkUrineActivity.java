package com.szxys.mhub.ui.mets.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.bean.Drinkandurine;
import com.szxys.mhub.subsystem.mets.dao.DrinkUrineDao;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;
import com.szxys.mhub.ui.mets.main.MetsMainActivity;

/**
 * 饮水量， 尿急， 尿失禁  
 * 
 * @author Administrator
 * 
 */
public class DrinkUrineActivity extends Activity implements OnClickListener, OnTouchListener{
	
	private static final int MYREQUEST_CODE = 12345;
	
	public static final int MODIFY_TIME_CODE = 2;

	private int businessId = 0;//业务ID
	//---
	private TextView topTitle = null;	//标题
	
	private TextView timeText = null;	//饮水时间， 尿急时间， 尿失禁时间
	private Button modifyButton = null; //修改按钮
//	private TextView showTime = null; //时间显示    5.17和修改按钮合并
	//  时间的格式显示： 0：yyyy年MM月dd日 HH:mm 1：yyyy年MM月dd日 HH时mm分   2： yyyy-MM-dd HH:mm
	private static int show_format = 1; 
	private String td_format;//存储当前的时间日期各式
	
	private TextView quantityText = null; //饮水，尿失禁 的量
	private EditText inputQuantity = null;  //输入量
	
	private Button saveButton = null;
	private Button cancelButton = null;
	
	LinearLayout linear = null;
	View  viewHide = null;
	
	private Handler time_handler = null;
	
	private static final String TAG = "DrinkUrineActivity---";
	private String err_EarlierThanNow="提交时间不能比现在晚";
	private String err_TooBigQuantityString="数量输入太大,请重新输入";
	private String err_WrongNum="数量输入不正确";
	private String err_NumRequired="请输入数值";
	

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
		inputQuantity.setInputType(InputType.TYPE_CLASS_NUMBER); //调用数字键盘，并且只能输入数字
		
		saveButton = (Button) this.findViewById(R.id.mets_bt_save);
		cancelButton = (Button) this.findViewById(R.id.mets_bt_cancel);
		saveButton.setSelected(true);
		
		modifyButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		
		saveButton.setOnTouchListener(this);
		cancelButton.setOnTouchListener(this);
		
		//如果是尿急界面的话，隐藏这个LinearLayout
		linear = (LinearLayout) this.findViewById(R.id.urine_urgency_hide);
		viewHide = this.findViewById(R.id.urine_urgency_hide_view);

		err_EarlierThanNow=getResources().getString(R.string.mets_submit_Later_thanNow);//"提交时间不能比现在晚";
		err_TooBigQuantityString=getResources().getString(R.string.mets_quantity_morethan65535);//"数量输入太大,请重新输入";
		err_WrongNum=getResources().getString(R.string.mets_not_quantity_number);//"数量输入不正确";
		err_NumRequired=getResources().getString(R.string.mets_required_number);//"请输入数值";
		
		init();
	}

	private void init() {
		
		Bundle extras=getIntent().getExtras(); //获取打开参数
		if(extras==null)
			return ;
		
		businessId = extras.getInt(MetsMainActivity.MainStatic.BusinessIdString);
		
		if(businessId == MetsMainActivity.MainStatic.DRINK){//饮水
			topTitle.setText(R.string.mets_drink_toptitle);
			timeText.setText(R.string.mets_drink_time_text);
			quantityText.setText(R.string.mets_drink_quantity_text);
			
		} else if (businessId == MetsMainActivity.MainStatic.URINE){//尿量
			topTitle.setText(R.string.mets_urine_toptitle);
			timeText.setText(R.string.mets_urine_time_text);
			quantityText.setText(R.string.mets_urine_quantity_text);
			
		} else if (businessId == MetsMainActivity.MainStatic.URINARY_URGENCY){//尿急
			linear.setVisibility(View.INVISIBLE); //不可见  ？？？？？
			viewHide.setVisibility(View.INVISIBLE);
//			linear.setVisibility(View.GONE); //不可见  ？？？？？
			timeText.setText(R.string.mets_urinary_urgency_time_text);
			topTitle.setText(R.string.mets_urinary_urgency_toptitle);
			quantityText.setText(R.string.mets_urinary_urgency_quantity_text);
		
		} else if (businessId == MetsMainActivity.MainStatic.URINARY_INCONTINENCE){//尿失禁
			topTitle.setText(R.string.mets_urinary_incontinence_toptitle);
			timeText.setText(R.string.mets_urinary_incontinence_time_text);
			quantityText.setText(R.string.mets_urinary_incontinence_quantity_text);
		}
		
		
		//设置时间的显示各式
		if(0 == show_format) {
			td_format = TimeUtils.format;
		} else if (1 == show_format) {
			td_format = TimeUtils.format1;
		} else if (2 == show_format) {
			td_format = TimeUtils.format2;
		}
		
		
		//currentTime  获取系统的当前时间
		String currentTime = TimeUtils.getTimeString(new Date(), td_format);
		
		modifyButton.setText(currentTime);
		
		
//		time_handler = new Handler( ) {
//			
//			@Override
//			public void handleMessage(Message msg) {
//				// TODO Auto-generated method stub
//			}
//		};
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 这里的resultCode得到的就是Activity1.java中设的RESULT_OK=-1，data就是mIntent
		//modify按钮 启动的Activity 回调后
		if (MODIFY_TIME_CODE == requestCode ) {// 
			
			if (RESULT_OK == resultCode) {
				Bundle extras = data.getExtras();
				if (extras != null) {
//					showTime.setText(extras.getString(ModifyTimeActivity.TIME_TEXT));
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
			
			String drinkQuantity = inputQuantity.getText().toString(); //"饮水量";	
			String dtSaved = modifyButton.getText().toString().trim();	//2011年05月09日 16时03分		
			Drinkandurine objInAndOut=new Drinkandurine();
			int second=new Date().getSeconds();
			//String dtString=dtSaved+":"+(second<10?("0"+second):second); //饮水时间 yyyy-MM-dd hh:mm:ss
			Date dtSubmit=TimeUtils.getDateFromString(dtSaved, TimeUtils.format1); //TimeUtils.getDateFromString(dtString, "yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			String dtInput=new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dtSubmit)+":"+(second<10?("0"+second):second);
			Date fullDate=TimeUtils.getDateFromFulltimeString(dtInput);
			objInAndOut.setC_DateTime(dtInput);
			objInAndOut.setC_UniqueId(formatter.format(fullDate)+SystemClock.uptimeMillis());
			objInAndOut.setC_Units(1);
			if(businessId == MetsMainActivity.MainStatic.DRINK){//保存饮水 信息
				objInAndOut.setC_Type(0);				
			}else if(businessId == MetsMainActivity.MainStatic.URINE){//保存 尿流量 信息
				objInAndOut.setC_Type(1);
				//objInAndOut.setC_Proportion(c_Proportion);
				
			}else if(businessId == MetsMainActivity.MainStatic.URINARY_URGENCY){//保存 尿急信息
				objInAndOut.setC_Quantity(0);
				objInAndOut.setC_Type(2);
				
			}else if(businessId == MetsMainActivity.MainStatic.URINARY_INCONTINENCE){//保存 尿失禁 信息
				objInAndOut.setC_Type(3);
			
		}
			float quantity=0;
			long row=0;
			if (drinkQuantity.trim().length()>0) {
				boolean isFloatQuantity=true;
				try {
					quantity=Float.parseFloat(drinkQuantity);
					objInAndOut.setC_Quantity(quantity);
				} catch (Exception e) {
					isFloatQuantity=false;
					Log.e("DrinkUrineAct", "parseFloat param="+drinkQuantity);
				}
				if (isFloatQuantity && quantity>0) {
					if (quantity<65536) {
						row=DrinkUrineDao.saveDrinkOrUrine(objInAndOut);
						if (row>0) {
							Toast.makeText(DrinkUrineActivity.this, "Submitted data successfully", Toast.LENGTH_LONG).show();
						}else {
							Toast.makeText(DrinkUrineActivity.this, "Failed to submit data", Toast.LENGTH_LONG).show();
						}
					}else {
						Toast.makeText(DrinkUrineActivity.this, err_TooBigQuantityString, Toast.LENGTH_LONG).show();
						return;
					}
					
				}else {
					Toast.makeText(DrinkUrineActivity.this,err_WrongNum, Toast.LENGTH_LONG).show();
					inputQuantity.setText("");
					return;
				}
			}else {
				if (objInAndOut.getC_Type()==2) {
					row=DrinkUrineDao.saveDrinkOrUrine(objInAndOut);
					if (row>0) {
						Toast.makeText(DrinkUrineActivity.this, "Submitted data successfully", Toast.LENGTH_LONG).show();
					}else {
						Toast.makeText(DrinkUrineActivity.this, "Failed to submit data", Toast.LENGTH_LONG).show();
					}
				}else {
					Toast.makeText(DrinkUrineActivity.this,err_NumRequired, Toast.LENGTH_LONG).show();
					return;
				}				
			}			
			finish();
			break;
		case R.id.mets_bt_cancel://取消按钮事件
				finish();
			break;
		case R.id.mets_bt_modify:// 修改按钮事件
			Intent intent1 = new Intent(DrinkUrineActivity.this,
					ModifyTimeActivity.class);
			intent1.putExtra(ModifyTimeActivity.TIME_TEXT, modifyButton.getText().toString());
			intent1.putExtra(ModifyTimeActivity.TIME_FORMAT, td_format);
			
			startActivityForResult(intent1, MODIFY_TIME_CODE); // 启动这个intent跳转
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()){
		case R.id.mets_bt_save:
			saveButton.setSelected(true);
			cancelButton.setSelected(false);
			break;
		case R.id.mets_bt_cancel:
			saveButton.setSelected(false);
			cancelButton.setSelected(true);
			break;
		}
		return false;
	}
	
	
}
