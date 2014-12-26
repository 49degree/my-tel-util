package com.yangxp.rtsp.attribute;

public class AttributeString extends AttributeBase<String>{

	public AttributeString(String attribute) {
		super(attribute);
		// TODO Auto-generated constructor stub
	}
	
	public AttributeString(String name,String value) {
		super(name,value);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initValue(String value) {
		// TODO Auto-generated method stub
		this.value = value;
	}

}
