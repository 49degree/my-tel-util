package com.xys.ecg.bean;

public class XmlNodeEntity {


	private String parentNodeName = "";     
	private String parentNodeAttributeName = "Value";
	private String parentNodeAttributeValue = "";
	
	private String childNodeName = "";
	private String childNodeAttributeName = "Value";
	private String childNodeAttributeValue = "";
	
	public XmlNodeEntity()
	{
		
	}
	
	public XmlNodeEntity(String parentNodeName,
			String parentNodeAttributeValue, String childNodeName,
            String childNodeAttributeValue) {
		this.parentNodeName = parentNodeName;
		this.parentNodeAttributeValue = parentNodeAttributeValue;
		this.childNodeName = childNodeName;
		this.childNodeAttributeValue = childNodeAttributeValue;
	}
	
	public String getParentNodeName() {
		return parentNodeName;
	}
	public void setParentNodeName(String parentNodeName) {
		this.parentNodeName = parentNodeName;
	}
	public String getParentNodeAttributeName() {
		return parentNodeAttributeName;
	}

	public String getParentNodeAttributeValue() {
		return parentNodeAttributeValue;
	}
	public void setParentNodeAttributeValue(String parentNodeAttributeValue) {
		this.parentNodeAttributeValue = parentNodeAttributeValue;
	}
	public String getChildNodeName() {
		return childNodeName;
	}
	public void setChildNodeName(String childNodeName) {
		this.childNodeName = childNodeName;
	}
	public String getChildNodeAttributeName() {
		return childNodeAttributeName;
	}

	public String getChildNodeAttributeValue() {
		return childNodeAttributeValue;
	}
	public void setChildNodeAttributeValue(String childNodeAttributeValue) {
		this.childNodeAttributeValue = childNodeAttributeValue;
	}

}
