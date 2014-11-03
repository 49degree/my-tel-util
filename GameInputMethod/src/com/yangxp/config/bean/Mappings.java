package com.yangxp.config.bean;

import java.util.StringTokenizer;

import android.util.Log;

public class Mappings {
	public int _id = -1;
	public int pageId;
	public int toPage;
	public int type;
	public int key;
	public int keyDrag;
	public int keyClick;
	public int x;
	public int y;
	public int radius;
	public byte[]  record;
	

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	public int getToPage() {
		return toPage;
	}

	public void setToPage(int toPage) {
		this.toPage = toPage;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getKeyDrag() {
		return keyDrag;
	}

	public void setKeyDrag(int keyDrag) {
		this.keyDrag = keyDrag;
	}

	public int getKeyClick() {
		return keyClick;
	}

	public void setKeyClick(int keyClick) {
		this.keyClick = keyClick;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public byte[] getRecord() {
		return record;
	}

	public void setRecord(byte[] record) {
		this.record = record;
	}

	@Override
	public String toString() {
		return "Mappings [id=" + _id + ", pageId=" + pageId + ", toPage="
				+ toPage + ", type=" + type + ", key=" + key + ", keyDrag="
				+ keyDrag + ", keyClick=" + keyClick + ", x=" + x + ", y=" + y
				+ ", radius=" + radius + ", record=" + record + "]";
	}
	
	public static Mappings copyFromKeyMapping(KeyMapping value) throws Exception{
		if(value==null)
			throw new Exception("value is null");
		Mappings mappings = new Mappings();
		mappings._id =value.id;
		mappings.pageId = value.pageId;
		mappings.toPage = value.toPage;
		mappings.type = value.type;
		mappings.key = value.key;
		mappings.keyDrag = value.keyDrag;
		mappings.keyClick = value.keyClick;
		mappings.x = value.x;
		mappings.y = value.y;
		mappings.radius = value.radius;
		mappings.record = value.record;
		return mappings;
	}
	
}
