package com.yangxp.config.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

import com.yangxp.config.bean.AppMap;
import com.yangxp.config.bean.Apps;
import com.yangxp.config.bean.GameAppConfigData;
import com.yangxp.config.bean.Mappings;
import com.yangxp.config.bean.Pages;
import com.yangxp.config.db.DBBean;
import com.yangxp.config.db.DBOperator;
import com.yangxp.config.exception.ConfigStringErrorException;
import com.yangxp.config.exception.HaveAppConfigException;

public class FileController extends Controller {

	private final static String TAG = "FileController";
	
	
	private GameAppConfigData gameAppConfigData;

	private JSONObject configJson = null;

	public FileController(String configInfo) throws ConfigStringErrorException,HaveAppConfigException{
		try {
			configJson = new JSONObject(configInfo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ConfigStringErrorException(e.getMessage());
		}
		initConfigData();
	}

	private void initConfigData() throws ConfigStringErrorException,HaveAppConfigException{
		gameAppConfigData = GameAppConfigData.parse(configJson);
		mAppPkgName = gameAppConfigData.getPackageName();
		
		//查询该使用数据是否存在
		Map<String, String> params = new HashMap<String,String>();
		params.put("name=", mAppPkgName);
		List<Object> returnList = DBOperator.getInstance(DBBean.DB_FILE).queryBeanList(DBBean.TBApps, params);
		if(returnList.size()==0){
			app = new Apps();
			app.name = mAppPkgName;
		}else{
			app = (Apps)returnList.get(0);
			params = new HashMap<String,String>();
			params.put("appId=", String.valueOf(app._id)); 
			params.put("appVersion=", String.valueOf(gameAppConfigData.getVersionCode()));
			 
			Log.e(TAG, "appId="+app._id+":version="+gameAppConfigData.getVersionCode());
			
			returnList = DBOperator.getInstance(DBBean.DB_FILE).queryBeanList(DBBean.TBAppMap, params);
			if(returnList.size()>0){
				throw new HaveAppConfigException("has config for appName:"+mAppPkgName+",version:"+gameAppConfigData.getVersionCode());
			}
		}
		
		appMap = new AppMap();
		appMap.setAppVersion(gameAppConfigData.getVersionCode());
		pages = new Pages();
		pages.setName(gameAppConfigData.getPageName());
		
		mappings = new ArrayList<Mappings>();
		JSONArray jArray = gameAppConfigData.getGameConfig();
		 for(int i=0;i<jArray.length();i++){
			 JSONObject jobj = null;
			try {
				jobj = jArray.getJSONObject(i);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			 Mappings tempMapping = new Mappings();
			 try{
				 tempMapping.pageId = jobj.getInt("pageid");
				 tempMapping.toPage = jobj.getInt("to_page");
				 tempMapping.type = jobj.getInt("type");
				 tempMapping.key = jobj.getInt("key");
				 tempMapping.keyDrag = jobj.getInt("keydrag");
				 tempMapping.keyClick = jobj.getInt("keyclick");
				 tempMapping.x = jobj.getInt("x");
				 tempMapping.y = jobj.getInt("y");
				 tempMapping.radius = jobj.getInt("radius");
				 
				 String recordString =  jobj.getString("record");
				 if(recordString!=null&&!"".equals(recordString));{
					 tempMapping.record = Base64.decode(recordString,Base64.DEFAULT);
				 }
				 mappings.add(tempMapping);
			 }catch(Exception e){
				 throw new ConfigStringErrorException(e.getMessage());
			 }
		 }
		Log.e(TAG, app._id+":"+appMap._id+":"+pages._id+":"+mappings.size());
	}
	
	public void saveOrUpdate(){
		saveDb(DBBean.DB_FILE);
		//查询配置数据库是否存在数据
		Map<String, String> params = new HashMap<String,String>();
		params.put("name=", mAppPkgName);
		List<Object> returnList = DBOperator.getInstance(DBBean.DB_SETTING).queryBeanList(DBBean.TBApps, params);
		if(returnList.size()>0){
			app = (Apps)returnList.get(0);
		}else
			app._id = -1;
		params = new HashMap<String,String>();
		params.put("appId=", String.valueOf(app._id)); 
		params.put("appVersion=", String.valueOf(gameAppConfigData.getVersionCode()));
		returnList = DBOperator.getInstance(DBBean.DB_SETTING).queryBeanList(DBBean.TBAppMap, params);
		if(returnList.size()>0){
			appMap = (AppMap)returnList.get(0);
		}else{
			appMap._id = -1;
		}
		params = new HashMap<String,String>();
		params.put("mapId=", String.valueOf(appMap._id));
		returnList = DBOperator.getInstance(DBBean.DB_SETTING).queryBeanList(DBBean.TBPages, params);
		if(returnList.size()>0){
			pages = (Pages)returnList.get(0);
		}else
			pages._id = -1;
		
		params = new HashMap<String,String>();
		params.put("pageId=", String.valueOf(pages._id));
		returnList = DBOperator.getInstance(DBBean.DB_SETTING).queryBeanList(DBBean.TBMappings, params);
		if(returnList.size()>0){
			return;
		}else{
			for(Mappings mapping:mappings){
				mapping._id = -1;
			}
			saveDb(DBBean.DB_SETTING);
		}
	}
	

}
