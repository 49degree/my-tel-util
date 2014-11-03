package com.yangxp.config.bean;

public class AppMap {
	public int _id = -1;
	public int appId;
	public int appVersion;
	public int dataVersion;
	public int resX;
	public int resY;
	public String author;
	public String  updateTime;
	public String  notes;
	public String  GUID;
	public float rating;
	public String  srcGUID;
	public int userChanged;

	
	public int get_id() {
		return _id;
	}


	public void set_id(int _id) {
		this._id = _id;
	}


	public int getAppId() {
		return appId;
	}


	public void setAppId(int appId) {
		this.appId = appId;
	}


	public int getAppVersion() {
		return appVersion;
	}


	public void setAppVersion(int appVersion) {
		this.appVersion = appVersion;
	}


	public int getDataVersion() {
		return dataVersion;
	}


	public void setDataVersion(int dataVersion) {
		this.dataVersion = dataVersion;
	}


	public int getResX() {
		return resX;
	}


	public void setResX(int resX) {
		this.resX = resX;
	}


	public int getResY() {
		return resY;
	}


	public void setResY(int resY) {
		this.resY = resY;
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}


	public String getGUID() {
		return GUID;
	}


	public void setGUID(String gUID) {
		GUID = gUID;
	}


	public float getRating() {
		return rating;
	}


	public void setRating(float rating) {
		this.rating = rating;
	}


	public String getSrcGUID() {
		return srcGUID;
	}


	public void setSrcGUID(String srcGUID) {
		this.srcGUID = srcGUID;
	}


	public int getUserChanged() {
		return userChanged;
	}


	public void setUserChanged(int userChanged) {
		this.userChanged = userChanged;
	}


	@Override
	public String toString() {
		return "AppMap [id=" + _id + ", appId=" + appId + ", appVersion="
				+ appVersion + ", dataVersion=" + dataVersion + ", resX="
				+ resX + ", resY=" + resY + ", author=" + author
				+ ", updateTime=" + updateTime + ", notes=" + notes + ", GUID="
				+ GUID + ", rating=" + rating + ", srcGUID=" + srcGUID
				+ ", userChanged=" + userChanged + "]";
	}
	
	
	
	
	
	
	
	
	

}
