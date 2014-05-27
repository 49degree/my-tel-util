package com.skyeyes.base.bean;

public class OpenCloseDoorInfoBean {
	public int _id;
	public String eventCode;
	public int type;//1开门，2关门,3异常开门，4异常关门
	public long time;
	
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

	
	
}
