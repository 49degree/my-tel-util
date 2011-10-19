package com.guanri.android.insurance.activity.dialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.guanri.android.insurance.R;
import com.guanri.android.lib.utils.StringUtils;


public class LogQueryDialog extends Dialog implements
		android.view.View.OnClickListener {
	
	private CheckBox queryNoNameChb = null;
	private CheckBox queryDateChb = null;
	
	private Button mOkBtn = null;
	private Button mCancelBtn = null;

	private EditText EdtUserNo = null;
	private EditText EdtUserName = null;
	private Button beginDateBtn = null;
	private Button beginTimeBtn = null;
	private Button endDateBtn = null;
	private Button endTimeBtn = null;
	
	private Context context;
	
	private Map<String,String> params =null;

	QueryInterface queryInterface=null;

	public Map<String, String> getParams() {
		return params;
	}
	
	

	public LogQueryDialog(Context context,QueryInterface queryInterface) {
		super(context);
		// TODO Auto-generated constructor stub
		params = new HashMap<String,String>();
		this.context = context;
		this.queryInterface = queryInterface;
	}
	
	public void displayDlg() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		setContentView(R.layout.log_info_manager_query_dialog);// 设置对话框的布局

		mOkBtn = (Button) findViewById(R.id.setting_in);
		mCancelBtn = (Button) findViewById(R.id.setting_out);

		queryNoNameChb = (CheckBox)findViewById(R.id.query_no_name_btn);
		queryDateChb = (CheckBox)findViewById(R.id.query_date_btn);
		
		EdtUserNo = (EditText) findViewById(R.id.edt_no);
		EdtUserName = (EditText) findViewById(R.id.edt_name);
		
		beginDateBtn = (Button)findViewById(R.id.query_begin_date_btn);
		beginTimeBtn =(Button)findViewById(R.id.query_begin_time_btn);
		
		endDateBtn = (Button)findViewById(R.id.query_end_date_btn);
		endTimeBtn =(Button)findViewById(R.id.query_end_time_btn);
		
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		
		beginDateBtn.setOnClickListener(this);
		beginTimeBtn.setOnClickListener(this);
		endDateBtn.setOnClickListener(this);
		endTimeBtn.setOnClickListener(this);		
		show();// 显示对话框
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.query_begin_date_btn:{
			int year = Integer.valueOf(beginDateBtn.getText().toString().substring(0, 4));
			int month = Integer.valueOf(beginDateBtn.getText().toString().substring(5, 7)) -1;
			int day = Integer.valueOf(beginDateBtn.getText().toString().substring(8, 10));
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_log_query_begin_date));
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.sale_date_dialog, null);
			final DatePicker dtp_begin_date;
			dtp_begin_date = (DatePicker)view.findViewById(R.id.btp_date);
			Calendar c =Calendar.getInstance();
			dtp_begin_date.init(year,month,day,null); 
			builder.setNeutralButton(StringUtils.getStringFromValue(R.string.apsai_common_sure), new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int which){
    				//
    				int monthint = dtp_begin_date.getMonth() +1 ;
    				String yearstr,daystr, monthstr; 
    				if(monthint>9){
    					monthstr = "-" + monthint;
    				}else{
    					monthstr = "-0" + monthint;
    				}
    				if(dtp_begin_date.getDayOfMonth()>9){
    					daystr = "-" + dtp_begin_date.getDayOfMonth() ;
    				}
    				else{
    					daystr = "-0" + dtp_begin_date.getDayOfMonth();
    				}
    				String datestr = dtp_begin_date.getYear() + monthstr + daystr;
    				beginDateBtn.setText(datestr);    				
    			}
    		});
			builder.setNegativeButton(StringUtils.getStringFromValue(R.string.apsai_common_cancall), null);
			builder.setView(view);
			builder.create().show();
			}	
			break;
		case R.id.query_begin_time_btn:{
			int currentHour = Integer.valueOf(beginTimeBtn.getText().toString().substring(0, 2));
			int currentMinute = Integer.valueOf(beginTimeBtn.getText().toString().substring(3, 5));
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_log_query_select_date));
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.sale_time_dialog, null);
			final TimePicker tmp_begintime;
			tmp_begintime = (TimePicker)view.findViewById(R.id.btp_time);
			tmp_begintime.setIs24HourView(true);
			tmp_begintime.setCurrentHour(currentHour);
			tmp_begintime.setCurrentMinute(currentMinute); 
			builder.setNeutralButton(StringUtils.getStringFromValue(R.string.apsai_common_sure), new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int which){
    				//
    				String timestr;
    				if(tmp_begintime.getCurrentMinute()>9){
    					timestr = tmp_begintime.getCurrentHour() + ":" + tmp_begintime.getCurrentMinute();
    				}
    				else{
    					timestr = tmp_begintime.getCurrentHour() + ":0" + tmp_begintime.getCurrentMinute();
    				}
    				
    				beginTimeBtn.setText(timestr);    				
    			}
    		});
			builder.setNegativeButton(StringUtils.getStringFromValue(R.string.apsai_common_cancall), null);
			builder.setView(view);
			builder.create().show();
		}	
		break;
		case R.id.query_end_date_btn:{
			int year = Integer.valueOf(endDateBtn.getText().toString().substring(0, 4));
			int month = Integer.valueOf(endDateBtn.getText().toString().substring(5, 7)) -1;
			int day = Integer.valueOf(endDateBtn.getText().toString().substring(8, 10));
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_log_query_begin_date));
			LayoutInflater inflater = LayoutInflater.from(context);
			View end_date_view = inflater.inflate(R.layout.sale_date_dialog, null);
			final DatePicker dtp_end_date;
			dtp_end_date = (DatePicker)end_date_view.findViewById(R.id.btp_date);
			Calendar c =Calendar.getInstance();
			dtp_end_date.init(year,month,day,null); 
			builder.setNeutralButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					int monthint = dtp_end_date.getMonth() +1 ;
    				String yearstr,daystr, monthstr; 
    				if(monthint>9){
    					monthstr = "-" + monthint;
    				}else{
    					monthstr = "-0" + monthint;
    				}
    				if(dtp_end_date.getDayOfMonth()>9){
    					daystr = "-" +dtp_end_date.getDayOfMonth() ;
    				}
    				else{
    					daystr = "-0" + dtp_end_date.getDayOfMonth();
    				}
    				String datestr = dtp_end_date.getYear() + monthstr + daystr;
    				endDateBtn.setText(datestr);  
				}
				
			});
			builder.setNegativeButton(StringUtils.getStringFromValue(R.string.apsai_common_cancall), null);
			builder.setView(end_date_view);
			builder.create().show();
			}	
			break;
		case R.id.query_end_time_btn:{
			int currentHour = Integer.valueOf(endTimeBtn.getText().toString().substring(0, 2));
			int currentMinute = Integer.valueOf(endTimeBtn.getText().toString().substring(3, 5));
			
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_log_query_select_date));
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.sale_time_dialog, null);
			final TimePicker tmp_begintime;
			tmp_begintime = (TimePicker)view.findViewById(R.id.btp_time);
			tmp_begintime.setIs24HourView(true);
			tmp_begintime.setCurrentHour(currentHour);
			tmp_begintime.setCurrentMinute(currentMinute); 
			builder.setNeutralButton(StringUtils.getStringFromValue(R.string.apsai_common_sure), new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int which){
    				//
    				String timestr;
    				if(tmp_begintime.getCurrentMinute()>9){
    					timestr = tmp_begintime.getCurrentHour() + ":" + tmp_begintime.getCurrentMinute();
    				}
    				else{
    					timestr = tmp_begintime.getCurrentHour() + ":0" + tmp_begintime.getCurrentMinute();
    				}
    				
    				endTimeBtn.setText(timestr); 
    				
    				
    			}
    		});
			builder.setNegativeButton(StringUtils.getStringFromValue(R.string.apsai_common_cancall), null);
			builder.setView(view);
			builder.create().show();
		}	
		break;	
		case R.id.setting_in:
			if(queryNoNameChb.isChecked()){
				if((EdtUserNo.getText().toString().length()>0)&&(EdtUserNo.getText().toString() != null)){
					params.put("Operator_id=", EdtUserNo.getText().toString());
				}
				if((EdtUserName.getText().toString().length()>0)&&(EdtUserName.getText().toString() != null)){
					params.put("Operator_name=", EdtUserName.getText().toString());
				}
			}
			if(queryDateChb.isChecked()){
				params.put("Operate_time>=", this.beginDateBtn.getText().toString() + " " +beginTimeBtn.getText().toString()+":00");
				params.put("Operate_time<", this.endDateBtn.getText().toString() + " " +endTimeBtn.getText().toString()+":00");
			}			
			queryInterface.query(params);
			dismiss();
			break;
		case R.id.setting_out:
			dismiss();
			break;

		default:
			break;
		}
	}
	
	public interface QueryInterface{
		
		public void query(Map<String,String> params);
	}

}
