package com.yangxp.config.bean;
public class AppCloud {
	public   int _id;
	public   String  app_id;
	public   String  app_version;
	public   String  query_time;
	public   String  update_time;
	
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	public String getQuery_time() {
		return query_time;
	}

	public void setQuery_time(String query_time) {
		this.query_time = query_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	@Override
	public String toString() {
		return "AppCloud [id=" + _id + ", AppId=" + app_id + ", AppVersion="
				+ app_version + ", QueryTime=" + query_time + ", UpdateTime="
				+ update_time + "]";
	}
	

}
