package com.guanri.android.insurance.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.guanri.android.insurance.R;
import com.guanri.android.lib.utils.StringUtils;

public class DialogUtils {

	
	/**
	 * 显示录入错误信息
	 * @param ctx
	 * @param title
	 * @param msg
	 */
	public static void showErrorAlertDlg(Context ctx, String title, String msg)
	{
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setMessage(msg);
			builder.setTitle(title);
			builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			builder.create().show();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	
	/**
	 * 显示录入提示信息
	 * @param ctx
	 * @param title
	 * @param msg
	 */
	public static void showMessageAlertDlg(Context ctx, int title, int msg){
		showMessageAlertDlg(ctx,title,msg,null);
	}
	
	/**
	 * 显示录入提示信息
	 * @param ctx
	 * @param title
	 * @param msg
	 */
	public static void showMessageAlertDlg(Context ctx, int title, int msg,OnAlertDlgSureBtn dlgSureBtn){
		String titleStr = StringUtils.getStringFromValue(title);
		String msgStr = StringUtils.getStringFromValue(msg);
		showMessageAlertDlg(ctx,titleStr,msgStr,dlgSureBtn);
	}
	
	
	/**
	 * 显示录入提示信息
	 * @param ctx
	 * @param title
	 * @param msg
	 */
	public static void showMessageAlertDlg(Context ctx, String title, String msg){
		showMessageAlertDlg(ctx,title,msg,null);
	}
	
	/**
	 * 显示录入提示信息
	 * @param ctx
	 * @param title
	 * @param msg
	 */
	public static void showMessageAlertDlg(Context ctx, String title, String msg,final OnAlertDlgSureBtn dlgSureBtn)
	{
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setMessage(msg);
			builder.setTitle(title);
			builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(dlgSureBtn!=null){
						dlgSureBtn.OnSureBtn();
					}
				}
			});
			
			builder.create().show();
		}catch(Exception e){
			e.printStackTrace();
		}
	}	

	/**
	 * 选择是否提示信息
	 * @param ctx
	 * @param title
	 * @param msg
	 */	
	public static void showChoiceAlertDlg(Context ctx, int title, int msg,OnAlertDlgSureBtn dlgSureBtn){
		showChoiceAlertDlg(ctx,title,msg,dlgSureBtn,null);
	}
	
	/**
	 * 选择是否提示信息
	 * @param ctx
	 * @param title
	 * @param msg
	 */	
	public static void showChoiceAlertDlg(Context ctx, int title, int msg,OnAlertDlgSureBtn dlgSureBtn,OnAlertDlgCancelBtn dlgCancelBtn){
		String titleStr = StringUtils.getStringFromValue(title);
		String msgStr = StringUtils.getStringFromValue(msg);
		showChoiceAlertDlg(ctx,titleStr,msgStr,dlgSureBtn,dlgCancelBtn);
	}
	
	/**
	 * 选择是否提示信息
	 * @param ctx
	 * @param title
	 * @param msg
	 */	
	public static void showChoiceAlertDlg(final Context ctx, String title, String msg,final OnAlertDlgSureBtn dlgSureBtn){
		showChoiceAlertDlg(ctx,title,msg,dlgSureBtn,null);
	}
	
	
	/**
	 * 选择是否提示信息
	 * @param ctx
	 * @param title
	 * @param msg
	 */	
	public static void showChoiceAlertDlg(final Context ctx, String title, String msg,final OnAlertDlgSureBtn dlgSureBtn,final OnAlertDlgCancelBtn dlgCancelBtn)
	{
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setMessage(msg);
			builder.setTitle(title);
			
			builder.setNegativeButton(StringUtils.getStringFromValue(R.string.apsai_insu_manager_exit_no),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(dlgCancelBtn!=null){
						dlgCancelBtn.OnCancelBtn();
					}
				}
			});
			
			builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_insu_manager_exit_yes),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(dlgSureBtn!=null){
						dlgSureBtn.OnSureBtn();
					}
				}
			});
			
			builder.create().show();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 选择日期控件
	 * @param ctx
	 * @param title
	 * @param msg
	 */	
	public static void showChoiceDateDlg(Context ctx,String olDate,String dateFormat,final OnDatePickDlgBtn onDatePickDlgBtn)
	{
		try{
			// TODO Auto-generated method stub
			Date date = null;
			if (olDate == null|| "".equals(olDate.trim())) {
				date = new Date();
			}else{
				try{
					date = new SimpleDateFormat(dateFormat).parse(olDate);
				}catch(Exception e){
					date = new Date();
				}
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setTitle("选择日期");
			LayoutInflater inflater = LayoutInflater.from(ctx);
			View view = inflater.inflate(R.layout.sale_date_dialog, null);
			final DatePicker dtpDate = (DatePicker) view.findViewById(R.id.btp_date);
			dtpDate.init(date.getYear()+1900, date.getMonth(), date.getDate(), null);
			builder.setNeutralButton("确认",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					onDatePickDlgBtn.OnPickBtn(dtpDate);
				}});
			builder.setNegativeButton("取消", null);
			builder.setView(view);
			builder.create().show();
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	/**
	 * 选择日期控件
	 * @param ctx
	 * @param title
	 * @param msg
	 */	
	public static void showChoiceTimeDlg(Context ctx,String oldTime,String timeFormat,final OnTimePickDlgBtn onTimePickDlgBtn)
	{
		try{
			// TODO Auto-generated method stub
			Date date = null;
			if (oldTime == null|| "".equals(oldTime.trim())) {
				date = new Date();
			}else{
				try{
					date = new SimpleDateFormat(timeFormat).parse(oldTime);
				}catch(Exception e){
					date = new Date();
				}
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setTitle("选择时间");

			LayoutInflater inflater = LayoutInflater.from(ctx);
			View view = inflater.inflate(R.layout.sale_time_dialog, null);
			final TimePicker iniTime = (TimePicker)view.findViewById(R.id.btp_time);
			
			iniTime.setIs24HourView(true);
			iniTime.setCurrentHour(date.getHours());
			iniTime.setCurrentMinute(date.getMinutes()); 
			
			builder.setNeutralButton("确认",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					onTimePickDlgBtn.OnPickBtn(iniTime);
				}});
			builder.setNegativeButton("取消", null);
			builder.setView(view);
			builder.create().show();
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	public interface OnDatePickDlgBtn{
		public void OnPickBtn(DatePicker dtpDate);
	}
	public interface OnTimePickDlgBtn{
		public void OnPickBtn(TimePicker dtpTime);
	}
	public interface OnAlertDlgSureBtn{
		public void OnSureBtn();
	}
	
	public interface OnAlertDlgCancelBtn{
		public void OnCancelBtn();
	}
}
