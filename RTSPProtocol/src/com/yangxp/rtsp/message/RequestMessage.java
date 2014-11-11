package com.yangxp.rtsp.message;

import java.io.InputStream;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

public class RequestMessage extends MessageBase{
	static Logger logger = Logger.getLogger(RequestMessage.class);
	Message.Method method;
	public RequestMessage(){
		super();
	}
	
	public RequestMessage(String messageStr){
		this(messageStr.getBytes());
	}
	
	public RequestMessage(byte[] messageStr){
		super(messageStr);
	}
	
	public RequestMessage(InputStream instream){
		super(instream);
	}
	
	@Override
	StringBuffer packageHeader() {
		// TODO Auto-generated method stub
		return new StringBuffer().append(method.getName()).append(" ").append(url.toString()).append(" ").append(pVersion);
	}

	@Override
	void paresHeader(String headerStr) {
		// TODO Auto-generated method stub
		logger.debug("paresHeader-headerStr->"+headerStr);
		Matcher matcher = regexMethod.matcher(headerStr);
		
		logger.debug("paresHeader-find->"+matcher.find());
		
		method = Message.Method.parseMethod(matcher.group(1));
		logger.debug("paresHeader-method->"+method);
		initURL(matcher.group(2));
		logger.debug("paresHeader-url->"+url);
		pVersion = matcher.group(3);
		logger.debug("paresHeader-version->"+pVersion);
	}

	public Message.Method getMethod() {
		return method;
	}

	public void setMethod(Message.Method method) {
		this.method = method;
	}
	
	
}
