package com.yangxp.config.business;

import java.util.List;

import android.content.Intent;
import android.util.Log;

import com.yangxp.config.MainApplication;
import com.yangxp.config.bean.AppMap;
import com.yangxp.config.bean.Apps;
import com.yangxp.config.bean.KeyMapping;
import com.yangxp.config.bean.Mappings;
import com.yangxp.config.bean.Pages;
import com.yangxp.config.db.DBBean;
import com.yangxp.config.db.DBOperator;

public abstract class Controller {
	static String TAG = "Controller";
	protected Apps app;
	protected AppMap appMap;
	protected Pages pages;
	protected List<Mappings> mappings;
	protected String mAppPkgName ;
	
	
	public void saveDb(String dbName){
		if(app._id<0){
			long rowId = DBOperator.getInstance(dbName).insert(DBBean.TBApps, app);
			Log.e(TAG,"app rowId:"+rowId);
			if(rowId>=0)
				app.set_id((int)rowId);
		}
		
		if(appMap._id<0){
			appMap.appId = app._id;
			long rowId = DBOperator.getInstance(dbName).insert(DBBean.TBAppMap, appMap);
			Log.e(TAG,"appMap rowId:"+rowId);
			if(rowId>=0)
				appMap.set_id((int)rowId);
		}else{
			DBOperator.getInstance(dbName).update(DBBean.TBAppMap, appMap);
		}
		
		if(pages._id<0){
			pages.mapId = appMap._id;
			long rowId = DBOperator.getInstance(dbName).insert(DBBean.TBPages, pages);
			Log.e(TAG,"pages rowId:"+rowId);
			if(rowId>=0)
				pages.set_id((int)rowId);
		}else{
			DBOperator.getInstance(dbName).update(DBBean.TBPages, pages);
		}
		
		for(Mappings mapping:mappings){
			if(mapping.key<=0){
				continue;
			}
			//手型图标
			if(mapping.type == KeyMapping.KEY_MAP_TYPE_MOVE_MOUSE){
				if(mapping.keyClick<=0){//未定义点击键
					continue;
				}
			}
			
			if(mapping._id<0){
				mapping.pageId = pages._id;
				long rowId = DBOperator.getInstance(dbName).insert(DBBean.TBMappings, mapping);
				Log.e(TAG,"mapping rowId:"+rowId);
				if(rowId>0)
					mapping.set_id((int)rowId);
			}else{
				long rowId =DBOperator.getInstance(dbName).update(DBBean.TBMappings, mapping);
				Log.e(TAG,"mapping:"+mapping._id+":rowId:"+rowId);
			}
			
		}
		if(DBBean.DB_SETTING.equals(dbName)){
			sendBroadcast();
		}
	}
	
	public List<Mappings> getMappings(){
		return this.mappings;
	}


	public Apps getApp() {
		return app;
	}


	public AppMap getAppMap() {
		return appMap;
	}


	public Pages getPages() {
		return pages;
	}

	
	protected void sendBroadcast(){
		Intent in = new Intent("MappinggsModifyBroadcast");
		in.putExtra("packageName", mAppPkgName);
		MainApplication.getInstance().sendBroadcast(in);
	}
}
