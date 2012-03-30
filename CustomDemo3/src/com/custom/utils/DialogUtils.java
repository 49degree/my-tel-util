package com.custom.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {

	
	/**
	 * 显示录入提示信息
	 * @param ctx
	 * @param title
	 * @param msg
	 */
	public static void showMessageAlertDlg(Context ctx, String title, String msg,final OnAlertDlgSureBtn dlgSureBtn,final OnAlertDlgCancelBtn dlgCancelBtn)
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
			if(dlgCancelBtn!=null){
				builder.setPositiveButton("取消",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(dlgCancelBtn!=null){
							dlgCancelBtn.OnCancelBtn();
						}
					}
				});
			}
			
			builder.create().show();
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
		
	public interface OnAlertDlgSureBtn{
		public void OnSureBtn();
	}
	
	public interface OnAlertDlgCancelBtn{
		public void OnCancelBtn();
	}
}
