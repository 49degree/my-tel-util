package com.yangxp.rtsp.message;

public interface Content {
	public byte[] getContentBuffer();
	public String getContent();
	public String getEncodeType();
	public Content setContent(byte[] buffer);
	public Content setContent(String content);
}
