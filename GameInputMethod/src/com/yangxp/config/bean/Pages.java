package com.yangxp.config.bean;

public class Pages {
	public int _id = -1;
	public String  name;
	public int mapId;
	public int isdefault;
	
	

	public int get_id() {
		return _id;
	}



	public void set_id(int _id) {
		this._id = _id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getMapId() {
		return mapId;
	}



	public void setMapId(int mapId) {
		this.mapId = mapId;
	}



	public int getIsdefault() {
		return isdefault;
	}



	public void setIsdefault(int isdefault) {
		this.isdefault = isdefault;
	}



	@Override
	public String toString() {
		return "Pages [id=" + _id + ", name=" + name + ", mapId=" + mapId
				+ ", isdefault=" + isdefault + "]";
	}
	
	
	
	
	
	
	

}
