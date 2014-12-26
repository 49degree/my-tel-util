package com.yangxp.rtsp.attribute;

public class AttributeInt extends AttributeBase<Integer>{

	public AttributeInt(String attribute) {
		super(attribute);
		// TODO Auto-generated constructor stub
	}
	
	public AttributeInt(String name,Integer value) {
		super(name,value);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initValue(String value) {
		// TODO Auto-generated method stub
		this.value = Integer.parseInt(value);
	}

}
