package com.yangxp.rtsp.message;

import java.io.UnsupportedEncodingException;

public abstract class ContentBase implements Content{
	String encodeType;
	String contentStr;
	String contentType="application/sdp";
	String contentBase;
	
	public static class Factory{
		public static ContentBase getDefault(){
			return new DefaultContent(Message.CHAR_ENCODE);
		}
	}
	
	public ContentBase(String encodeType){
		try {
			new String(new byte[2],encodeType);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException("UnsupportedEncodingException -->" + e.getMessage());
		}
		this.encodeType = encodeType;
	}
	
	public ContentBase(String encodeType,String contentStr){
		this(encodeType);
		this.contentStr = contentStr;
	}

	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return contentStr;
	}
	
	@Override
	public byte[] getContentBuffer() {
		// TODO Auto-generated method stub
		try {
			return contentStr.getBytes(encodeType);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ContentBase setContent(byte[] buffer) {
		// TODO Auto-generated method stub
		try {
			contentStr=new String(buffer,encodeType);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
		
	}

	@Override
	public ContentBase setContent(String content) {
		// TODO Auto-generated method stub
		contentStr=content;
		return this;
		
	}

	@Override
	public String getEncodeType() {
		// TODO Auto-generated method stub
		return encodeType;
	}
	
	public void setEncodeType(String encodeType) {
		this.encodeType = encodeType;
	}

	public String getContentStr() {
		return contentStr;
	}

	public void setContentStr(String contentStr) {
		this.contentStr = contentStr;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentBase() {
		return contentBase;
	}

	public void setContentBase(String contentBase) {
		this.contentBase = contentBase;
	}
}
