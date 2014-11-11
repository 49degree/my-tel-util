package com.yangxp.rtsp.attribute;

public interface Attribute<T> {
	public void setValue(T value);
	public String getName();
	public T getValue();
	public String getAttributeString();
}
