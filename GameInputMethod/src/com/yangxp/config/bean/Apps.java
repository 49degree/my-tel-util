package com.yangxp.config.bean;

public class Apps {
	public int _id = -1;
	public  String name;


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


	@Override
	public String toString() {
		return "Apps [id=" + _id + ", name=" + name + "]";
	}

	
	
	

}
