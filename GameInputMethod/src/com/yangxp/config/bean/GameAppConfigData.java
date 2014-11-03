package com.yangxp.config.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yangxp.config.exception.ConfigStringErrorException;

public class GameAppConfigData {
	//static Logger logger = Logger.getLogger(GameAppConfigData.class);
	String packageName;
	int versionCode;
	int appMapId;
	String pageName;
	JSONArray gameConfig;
	
	public static GameAppConfigData parse(JSONObject object) throws ConfigStringErrorException{
		//logger.info(object);
		GameAppConfigData gameAppConfigData = new GameAppConfigData();
		try{
			gameAppConfigData.packageName = object.getString("packageName");
			gameAppConfigData.versionCode = object.getInt("versionCode");
			gameAppConfigData.gameConfig = object.getJSONArray("gameConfig");
			gameAppConfigData.appMapId = object.getInt("appMapId");
			gameAppConfigData.pageName = object.getString("pageName");
		}catch(Exception e){
			throw new ConfigStringErrorException(e.getMessage());
		}
		return gameAppConfigData;
	}
	
	public JSONObject toJSONObject(){
		JSONObject js = new JSONObject();
		try {
			js.put("packageName", packageName);
			js.put("versionCode", versionCode);
			js.put("appMapId", appMapId);
			js.put("pageName", pageName==null?"":pageName);
			js.put("gameConfig", gameConfig);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return js;
	}
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public JSONArray getGameConfig() {
		return gameConfig;
	}
	public void setGameConfig(JSONArray gameConfig) {
		this.gameConfig = gameConfig;
	}

	public int getAppMapId() {
		return appMapId;
	}

	public void setAppMapId(int appMapId) {
		this.appMapId = appMapId;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	
}
