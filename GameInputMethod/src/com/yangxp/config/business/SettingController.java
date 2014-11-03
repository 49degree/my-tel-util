package com.yangxp.config.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.yangxp.config.MainApplication;
import com.yangxp.config.bean.AppMap;
import com.yangxp.config.bean.Apps;
import com.yangxp.config.bean.GameAppConfigData;
import com.yangxp.config.bean.Mappings;
import com.yangxp.config.bean.Pages;
import com.yangxp.config.common.TypeConversion;
import com.yangxp.config.db.DBBean;
import com.yangxp.config.db.DBOperator;

public class SettingController extends Controller{
	private final static String TAG = "SettingController";
	public final static String filePath = Environment.getExternalStorageDirectory().getPath()+File.separator;//+"gamepad"+File.separator+"configdata"+File.separator;
	
	private PackageInfo mPackageinfo;  
	private boolean mCheckVersion = false;
	
	public SettingController(String appPkgName){
		this(appPkgName,false);
	}
	
	public SettingController(String appPkgName,boolean checkVersion){
		mAppPkgName = appPkgName;
		mCheckVersion = checkVersion;
		try {
			mPackageinfo = MainApplication.getInstance().getPackageManager().getPackageInfo(mAppPkgName, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		mappings = new ArrayList<Mappings>();
		initDatabaseData();
	}
	
	public void addMapperBean(Mappings mapping){
		mappings.add(mapping);
	}
	
	private void initDatabaseData(){
		Map<String, String> params = new HashMap<String,String>();
		params.put("name=", mAppPkgName);
		
		List<Object> returnList = DBOperator.getInstance(DBBean.DB_SETTING).queryBeanList(DBBean.TBApps, params);
		
		Log.e(TAG,"mAppPkgName:"+mAppPkgName+":"+returnList.size());
		
		if(returnList.size()==0){
			app = new Apps();
			app.name = mAppPkgName;
			appMap = new AppMap();
			appMap.appVersion = mPackageinfo.versionCode;
			pages = new Pages();
			
		}else{
			app = (Apps)returnList.get(0);
			params = new HashMap<String,String>();
			params.put("appId=", String.valueOf(app._id));
			returnList = DBOperator.getInstance(DBBean.DB_SETTING).queryBeanList(DBBean.TBAppMap, params);
			if(returnList.size()>0){
				for(Object temp:returnList){
					if(((AppMap)temp).appVersion==mPackageinfo.versionCode){
						appMap = (AppMap)temp;
						break;
					}
					if(!mCheckVersion){
						if(appMap==null){
							appMap = (AppMap)temp;
						}else if(appMap.appVersion<((AppMap)temp).appVersion){
							appMap = (AppMap)temp;
						}
					}
					continue;
				}
			}
			if(appMap==null){
				appMap = new AppMap();
				appMap.appId = app._id;
			}
			
			params = new HashMap<String,String>();
			params.put("mapId=", String.valueOf(appMap._id));
			returnList = DBOperator.getInstance(DBBean.DB_SETTING).queryBeanList(DBBean.TBPages, params);
			if(returnList.size()==0){
				pages = new Pages();
				pages.mapId = appMap._id;
			}else{
				pages = (Pages)returnList.get(0);
			}
			params = new HashMap<String,String>();
			params.put("pageId=", String.valueOf(pages._id));
			returnList = DBOperator.getInstance(DBBean.DB_SETTING).queryBeanList(DBBean.TBMappings, params);
			if(returnList.size()>0){
				for(Object mapping:returnList){
					mappings.add((Mappings)mapping);
				}
			}
		}
		
		Log.e(TAG, app._id+":"+appMap._id+":"+pages._id+":"+mappings.size());
	}
	
	
	public void saveOrUpdate(){
		/**
		 * 如果应用版本不一致，则另存数据
		 */
		if(appMap.appVersion != mPackageinfo.versionCode){
			appMap._id = -1;
			appMap.appVersion = mPackageinfo.versionCode;
			pages._id = -1;
			for(Mappings temp:mappings){
				temp._id = -1;
			}
		}
		saveDb(DBBean.DB_SETTING);
		//saveConfigFile();
	}
	
	/**
	 * 保存配置文件
	 */
	public void backupConfigFile(String appName){
		saveConfigFile(appName);
	}
	
	public void deleteMappings(Mappings mapping){
		if(mapping._id<0){
			mappings.remove(mapping);
			return;
		}
		if(mapping._id>0&&DBOperator.getInstance(DBBean.DB_SETTING).del(DBBean.TBMappings, mapping)>0){
			mappings.remove(mapping);
		}
		sendBroadcast();
	}
	
	private void saveConfigFile(String appName){
		GameAppConfigData gameAppConfigData = getGameAppConfigData();
		
		
		
		JSONObject jobj = gameAppConfigData.toJSONObject();
		String text = jobj.toString();
		File dirFile = new File(filePath);
		Log.e(TAG,"dirFile1:"+dirFile.exists()+":"+dirFile.getPath()+":"+gameAppConfigData.getPageName());
		if(!dirFile.exists()){
			Log.i(TAG,"dirFile.mkdirs():"+dirFile.mkdirs());
		}
			
		Log.e(TAG,"dirFile:"+dirFile.exists());
		//File file = new File(filePath+mAppPkgName+"_"+appMap.appVersion+".data");
		File file = new File(filePath+appName+"_按键设置"+".data");
		Log.e(TAG,"file:"+file.toString());
		if(file.exists())
			file.delete();
		
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(TypeConversion.stringToAscii(text));
			out.flush();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(out!=null)
				try {
					out.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	

//	 {“packageName”:”pack”,” versionCode”:”v1.0”,”appMapId”:1,
//	 ”gameConfig”:{
//	 ["pageid":"data1","to_page":"data2","type":"data3","key":"data4","keydrag":"data5","keyclick":"data6","x":"data7","y":"data8","radius":"data9","record":"data10"]
	private GameAppConfigData getGameAppConfigData(){
		 GameAppConfigData gameAppConfigData = new GameAppConfigData();
		 gameAppConfigData.setPackageName(app.getName());
		 gameAppConfigData.setVersionCode(appMap.getAppVersion());
		 gameAppConfigData.setAppMapId(appMap.get_id());
		 gameAppConfigData.setPageName(pages.getName());
		 JSONArray jSONArray = new JSONArray();

		 for(int i=0;i<mappings.size();i++){
			 Mappings tempMapping = mappings.get(i);
			 JSONObject jobj = new JSONObject();
			 try{
				 jobj.put("pageid", tempMapping.pageId);
				 jobj.put("to_page", tempMapping.toPage);
				 jobj.put("type", tempMapping.type);
				 jobj.put("key", tempMapping.key);
				 jobj.put("keydrag", tempMapping.keyDrag);
				 jobj.put("keyclick", tempMapping.keyClick);
				 jobj.put("x", tempMapping.x);
				 jobj.put("y", tempMapping.y);
				 jobj.put("radius", tempMapping.radius);
				 if(tempMapping.getRecord()!=null&&tempMapping.getRecord().length>0){
					 jobj.put("record", Base64.encodeToString(tempMapping.getRecord(), Base64.DEFAULT));
				 }else{
					 jobj.put("record","");
				 }
			 }catch(Exception e){
				 
			 }

			 jSONArray.put(jobj);
		 }
		 gameAppConfigData.setGameConfig(jSONArray);
		 Log.e("", gameAppConfigData.toJSONObject().toString());
		 return gameAppConfigData;
	}
	
}
