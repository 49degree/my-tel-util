package com.guanri.android.insurance.service;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 系统升级
 * 
 * @author Administrator
 * 
 */
public class SystemUpdateService {
	private int version;
	private Context context = null;

	/**
	 * @roseuid 4DF8330E0222
	 */
	public SystemUpdateService(Context context) {
		this.context = context;
	}

	/**
	 * @roseuid 4DF81AA60203
	 */
	public void getNewVersion() {
		
	}

	/**
	 * @roseuid 4DF81ADF000F
	 */
	public void update() {

	}

	/**
	 * @roseuid 4DF81B0903C8
	 */
	public void getNowVersion() {
		try {
			version = ((Activity) context).getPackageManager().getPackageInfo(
					"com.guanri.android.insurance", 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
