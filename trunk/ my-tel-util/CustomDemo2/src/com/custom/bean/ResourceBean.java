package com.custom.bean;

import java.util.HashMap;
import java.util.List;

public class ResourceBean {
	public enum ResourceType{
		fold,apk,swf,pic
	}
	public static class ResourceRaws{
		String rawPath = null;
		ResourceType type = null;
		
		
		public ResourceRaws(String rawPath, ResourceType type) {
			super();
			this.rawPath = rawPath;
			this.type = type;
		}
		public String getRawPath() {
			return rawPath;
		}
		public void setRawPath(String rawPath) {
			this.rawPath = rawPath;
		}
		public ResourceType getType() {
			return type;
		}
		public void setType(ResourceType type) {
			this.type = type;
		}
		
	}
	
	String btnKey = null;
	String btnPic = null;
	String name = null;
	String foldPath = null;
	int foldDepth = 0;
	int x;
	int y;
	List<ResourceRaws> raws = null;
	
	
	

	public String getBtnKey() {
		return btnKey;
	}
	public void setBtnKey(String btnKey) {
		this.btnKey = btnKey;
	}
	public String getBtnPic() {
		return btnPic;
	}
	public void setBtnPic(String btnPic) {
		this.btnPic = btnPic;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFoldPath() {
		return foldPath;
	}
	public void setFoldPath(String foldPath) {
		this.foldPath = foldPath;
	}
	public int getFoldDepth() {
		return foldDepth;
	}
	public void setFoldDepth(int foldDepth) {
		this.foldDepth = foldDepth;
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
	public List<ResourceRaws> getRaws() {
		return raws;
	}
	public void setRaws(List<ResourceRaws> raws) {
		this.raws = raws;
	}

	
	
}
