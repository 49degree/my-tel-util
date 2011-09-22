package com.guanri.android.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class Update extends Activity {
	public ProgressDialog pBar;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.update);

		Bundle bundle = this.getIntent().getExtras();
		final String filePath = bundle.getString("filePath");
		final String fileName = bundle.getString("fileName");
		final String updateMsg = bundle.getString("updateMsg");
		
		Dialog dialog = new AlertDialog.Builder(Update.this).setTitle(
				"系统更新").setMessage("发现新版本，请更新！\n"+updateMsg)
				.setPositiveButton("确定",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								pBar = new ProgressDialog(Update.this);
								pBar.setTitle("正在下载");
								pBar.setMessage("请稍候...");
								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								downFile(filePath,fileName);
							}
				}).setNegativeButton("取消",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int whichButton) {//点击"取消"按钮之后退出程序
								finish();
							}
				}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	/**
	 * 文件下载
	 * @param url
	 * @param fileName
	 */
	void downFile(final String urlString,final String fileName) {
		pBar.show();
		new Thread() {
			public void run() {
				Log.d("UPdate",urlString+":"+fileName);
				try {
					URL url = new URL(urlString);   
					HttpURLConnection conn =(HttpURLConnection) url.openConnection();   
					conn.setDoInput(true);   
					conn.connect();   
					Log.d("UPdate conn.getResponseCode()",conn.getResponseCode()+":");
					if( conn.getResponseCode() == HttpURLConnection.HTTP_OK){
						InputStream is = conn.getInputStream(); 
						FileOutputStream fileOutputStream = null;
						if (is != null) {
							File file = new File(Environment.getExternalStorageDirectory(), fileName);
							fileOutputStream = new FileOutputStream(file);
							byte[] buf = new byte[1024];
							int ch = -1;
							int count = 0;
							while ((ch = is.read(buf)) != -1) {
								Log.d("update count",count+"");
								fileOutputStream.write(buf, 0, ch);
								count += ch;
							}
						}
						if (fileOutputStream != null) {
							fileOutputStream.flush();
							fileOutputStream.close();
						}
						if(is!=null){
							is.close();
						}
						install(fileName);//安装
					}else{//下载失败
						handler.post(new Runnable() {
							public void run() {
								pBar.cancel();
								showSureDialog("失败","连接错误！",true);
							}
						});
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					pBar.cancel();
					showSureDialog("失败","连接错误！",true);
				}
			}
		}.start();
	}
	/**
	 * 安装线程
	 */
	void install(final String fileName) {
		handler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				try{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(new File("/sdcard/"+fileName)),
							"application/vnd.android.package-archive");
					startActivity(intent);
				}catch(Exception e){
					showSureDialog("失败","安装失败，请稍后再试或者联系系统维护人员！",true);
				}
			}
		});
	}


	/**
	 * 提示信息弹出框
	 * @param title
	 * @param msg
	 */
	public void showSureDialog(String title,String msg,final boolean exit) {
		new AlertDialog.Builder(Update.this).setTitle(title).setMessage(msg)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						setResult(RESULT_OK);// 确定按钮事件
						if(exit){
							finish();
						}
					}
				}).show();
	}
}