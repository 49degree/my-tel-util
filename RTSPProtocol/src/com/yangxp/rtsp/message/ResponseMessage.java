package com.yangxp.rtsp.message;

import java.io.InputStream;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

public class ResponseMessage extends MessageBase{
	static Logger logger = Logger.getLogger(ResponseMessage.class);
	int resultCode;
	String resultMsg;
	public ResponseMessage(){
		super();
	}
	
	public ResponseMessage(String messageStr){
		this(messageStr.getBytes());
	}
	
	public ResponseMessage(byte[] messageStr){
		super(messageStr);
	}
	
	public ResponseMessage(InputStream instream){
		super(instream);
	}
	
	@Override
	StringBuffer packageHeader() {
		// TODO Auto-generated method stub
		return new StringBuffer().append(pVersion).append(" ").append(resultCode).append(" ").append(resultMsg);
	}

	@Override
	void paresHeader(String headerStr) {
		// TODO Auto-generated method stub
		logger.debug("paresHeader-headerStr->"+headerStr);
		Matcher matcher = regexResultCode.matcher(headerStr);
		logger.debug("paresHeader-find->"+matcher.find());
		pVersion = matcher.group(1);
		logger.debug("paresHeader-version->"+pVersion);
		resultCode = Integer.parseInt(matcher.group(2));
		logger.debug("paresHeader-resultCode->"+resultCode);
		resultMsg = matcher.group(3);
		logger.debug("paresHeader-resultMsg->"+resultMsg);
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	
	public void setResult(RESULT result) {
		this.resultCode = result.resultCode;
		this.resultMsg = result.resultMsg;
	}
	
	
}
