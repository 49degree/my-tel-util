package com.yangxp.rtsp.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

public interface Message {
	public static final String CHAR_ENCODE = "UTF-8";
	
	public static final String RTSP_PROTOCOL_VERSION1 = "RTSP/1.0";
	public static final String RTSP_PROTOCOL_VERSION2 = "RTSP/2.0";
	
	public static final String serverName = "server";
	public static final String cseqName = "cseq";
	public static final String contentLengthName = "content-length";
	public static final String contentTypeName = "content-type";
	public static final String contentBaseName = "content-base";
	public static final String sessionName = "session";
	
	// Parse method & uri
	public static final Pattern regexMethod = Pattern.compile("(\\w+) (\\S+) (RTSP/\\d.\\d)",Pattern.CASE_INSENSITIVE);
	//public static final Pattern regexMethod = Pattern.compile("(\\w+) (\\S+) (RTSP/\\d.\\d)",Pattern.CASE_INSENSITIVE);
	
	// Parse response result code
	public static final Pattern regexResultCode = Pattern.compile("(RTSP/\\d.\\d) (\\d+) (\\w+)",Pattern.CASE_INSENSITIVE);
	// Parse a request header
	public static final Pattern rexegHeader = Pattern.compile("(\\S+):(.+)",Pattern.CASE_INSENSITIVE);
	// Parses method & uri
	public static final Pattern regexStatus = Pattern.compile("RTSP/\\d.\\d (\\d+) (\\w+)",Pattern.CASE_INSENSITIVE);
	// Parses a WWW-Authenticate header
	public static final Pattern rexegAuthenticate = Pattern.compile("realm=\"(.+)\",\\s+nonce=\"(\\w+)\"",Pattern.CASE_INSENSITIVE);
	// Parses a Session header
	public static final Pattern rexegSession = Pattern.compile("(\\d+)",Pattern.CASE_INSENSITIVE);
	// Parses a Transport header
	public static final Pattern rexegTransport = Pattern.compile("client_port=(\\d+)-(\\d+).+server_port=(\\d+)-(\\d+)",Pattern.CASE_INSENSITIVE);
	
	public enum Method {
		OPTIONS("OPTIONS"), DESCRIBE("OPTIONS"), SETUP("OPTIONS"), PLAY("OPTIONS"),PAUSE("PAUSE"), RECORD("RECORD"), TEARDOWN("TEARDOWN"),NULL("");
	    private final String name;
	    private Method(String name){
	        this.name = name;
	    }
	    public String getName(){
	        return this.name;
	    }
	    public static Method parseMethod(String name){
	    	if(name.equals(OPTIONS.getName()))
	    		return OPTIONS;
	    	else if(name.equals(DESCRIBE.getName()))
	    		return DESCRIBE;
	    	else if(name.equals(SETUP.getName()))
	    		return SETUP;
	    	else if(name.equals(DESCRIBE.getName()))
	    		return OPTIONS;
	    	else if(name.equals(PLAY.getName()))
	    		return PLAY;
	    	else if(name.equals(RECORD.getName()))
	    		return RECORD;
	    	else if(name.equals(TEARDOWN.getName()))
	    		return TEARDOWN;
	    	else
	    		return NULL;
	    	
	    }
	}

	public enum RESULT{
		// Status code definitions
		STATUS_OK(200,"OK"),STATUS_BAD_REQUEST(400 ,"Bad Request"),STATUS_NOT_FOUND(404 ,"Not Found"),STATUS_INTERNAL_SERVER_ERROR(500 ,"Internal Server Error");
		public int resultCode;
		public String resultMsg;
		private RESULT(int resultCode,String resultMsg){
			this.resultCode = resultCode;
			this.resultMsg = resultMsg;
		}
		
		public String toString(){
			return resultCode+" "+resultMsg;
		}
	}
	
	public void parseInputStream(ByteBuffer input) throws IOException, IllegalStateException;
	public void parseInputStream(InputStream input) throws IOException, IllegalStateException;
	public void parseInputStream(BufferedReader input) throws IOException, IllegalStateException;
	public void parseInputString(String input) throws IOException, IllegalStateException;
	public String packageMessage();
	public Message nextMessage() throws IOException, IllegalStateException ;
}
