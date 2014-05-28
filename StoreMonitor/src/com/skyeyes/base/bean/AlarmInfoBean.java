package com.skyeyes.base.bean;

public class AlarmInfoBean {
	public int _id;
	public String eventCode;
	public int type;
	
	public long time;
	public byte[] pic;
	public boolean hasLook;
	public String des;
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getEventCode() {
		return eventCode;
	}
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public byte[] getPic() {
		return pic;
	}
	public void setPic(byte[] pic) {
		this.pic = pic;
	}
	public boolean getHasLook() {
		return hasLook;
	}
	public void setHasLook(boolean hasLook) {
		this.hasLook = hasLook;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}

	
	
}
