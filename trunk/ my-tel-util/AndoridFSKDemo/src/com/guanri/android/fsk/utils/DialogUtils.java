package com.guanri.android.fsk.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.widget.DatePicker;
import android.widget.TimePicker;


public class DialogUtils {



	
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
			builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
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
	public static void showChoiceAlertDlg(final Context ctx, String title, String msg,final OnAlertDlgSureBtn dlgSureBtn,final OnAlertDlgCancelBtn dlgCancelBtn)
	{
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setMessage(msg);
			builder.setTitle(title);
			
			builder.setNegativeButton("是",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(dlgCancelBtn!=null){
						dlgCancelBtn.OnCancelBtn();
					}
				}
			});
			
			builder.setPositiveButton("否",new DialogInterface.OnClickListener() {
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
