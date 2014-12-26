package com.yangxp.rtsp.attribute;

import java.lang.reflect.ParameterizedType;

import org.apache.log4j.Logger;

public abstract class AttributeBase<T> implements Attribute<T> {
	static Logger logger = Logger.getLogger(AttributeBase.class);
	protected String name;
	protected T value;
	protected Class<T> entityClass;
	
	private void init(){
		entityClass =(Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		//logger.debug(entityClass);
		if(!entityClass.equals(Integer.class)&&
				!entityClass.equals(String.class)){
			throw new IllegalArgumentException("value type just support String and int: error type is " + entityClass);
		}
	}
	
	public AttributeBase(String attribute) {
		init();
		int colon = attribute.indexOf(':');
		if (colon == -1)
			name = attribute;
		else {
			name = attribute.substring(0, colon);
			initValue(attribute.substring(++colon).trim());
		}
	}
	public AttributeBase(String name, T value) {
		init();
		this.name = name;
		this.value = value;
	}
	
	public String getAttributeString(){
		return new StringBuffer(name).append(":").append(value).toString();
	}

	
	public abstract void initValue(String value);
	
	@Override
	public void setValue(T value){
		this.value = value;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public T getValue(){
		return value;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof AttributeBase){
			if(equals(obj))
				return true;
			if(name==null&&((AttributeBase)obj).name!=null)
				return false;
			if(value==null&&((AttributeBase)obj).value!=null)
				return false;
			if(name.equals(((AttributeBase)obj).name)
					&&value.equals(((AttributeBase)obj).value))
				return true;
		}
		return false;

	}
}
