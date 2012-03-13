package com.custom.bean;

public class ResourceBean {
	public enum ResourceType{
		fold,apk,swf
	}
	String btnPic = null;
	String name = null;
	ResourceType type = null;
	String resourcePath = null;
	
	
	

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
	public ResourceType getType() {
		return type;
	}
	public void setType(ResourceType type) {
		this.type = type;
	}
	public String getResourcePath() {
		return resourcePath;
	}
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	
}
