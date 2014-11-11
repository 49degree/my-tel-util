package com.yangxp.rtsp.message;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.yangxp.rtsp.attribute.Attribute;
import com.yangxp.rtsp.attribute.AttributeInt;
import com.yangxp.rtsp.attribute.AttributeString;

public abstract class MessageBase implements Message{
	static Logger logger = Logger.getLogger(MessageBase.class);
	String pVersion = Message.RTSP_PROTOCOL_VERSION1;
	URL url;
	StringBuffer messageStr = new StringBuffer();
	ContentBase content;
	boolean mComplete = false;
	
	HashMap<String,Attribute> attributes = new HashMap<String,Attribute>();

	public MessageBase(){

	}
	
	public MessageBase(byte[] messageStr){
		this(new ByteArrayInputStream(messageStr));
	}
	
	public MessageBase(InputStream instream){
		try {
			parseInputStream(instream);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	abstract StringBuffer packageHeader();
	
	abstract void paresHeader(String headerStr);
	
	public Message nextMessage() throws IOException, IllegalStateException {
		Message message = null;
		if(nextMessageString==null){
			return null;
		}else{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(nextMessageString.getBytes())));
			String headerLine = null;
			do{
				headerLine = reader.readLine();
				logger.debug("headerLine:"+headerLine);
			}while(headerLine!=null&&headerLine.trim().equals(""));
			
			if(headerLine==null)
				return null;
			Matcher matcher = regexMethod.matcher(headerLine);
			if(matcher.find()){
				message = new RequestMessage();
			}else{
				matcher = regexResultCode.matcher(headerLine);
				if(matcher.find()){
					message = new ResponseMessage();
				}
			}
			if(message!=null)
				message.parseInputString(nextMessageString);
		}
		return message ;
	}
	String nextMessageString = null;
	public void parseInputString(String input) throws IOException, IllegalStateException {
		messageStr.append(input);
		char lastChar = messageStr.charAt(messageStr.length()-1);
		if(lastChar!='\r'&&lastChar!='\n')
			return;
		logger.debug("messageStr:"+messageStr);
		mComplete = false;

		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(messageStr.toString().getBytes())));
		parseInputStream(reader);
		
		if(this.isComplete()){
			char[] buffer = new char[messageStr.length()];
			int len = 0;
			if( (len = reader.read(buffer, len, buffer.length))>0 ) {
				nextMessageString = new String(buffer,0,len);
			}

		}
	}
	
	public void parseInputStream(ByteBuffer input) throws IOException, IllegalStateException {
		parseInputString(new String(input.array(),input.position(),input.limit()-input.position()));
	}
	
	public void parseInputStream(InputStream input) throws IOException, IllegalStateException {
		parseInputStream(new BufferedReader(new InputStreamReader(input)));
	}
	
	/** Parse the method, uri & headers of a RTSP request */
	public void parseInputStream(BufferedReader input) throws IOException, IllegalStateException {
		if(this.isComplete())
			throw new IOException("messeage receive complete");
		String line;
		Matcher matcher;
		// Parsing request method & uri
		do{
			line = input.readLine();
			logger.debug("headerLine:"+line);
		}while(line==null||line.trim().equals(""));
		paresHeader(line);
	
		
		// Parsing headers of the request
		
		while ( (line = input.readLine()) != null&&line.length()>0) {

			if(line.trim().length()==0)
				continue;
			matcher = rexegHeader.matcher(line);
			if(!matcher.find())
				break;
			
			if(matcher.group(1).toLowerCase(Locale.US).equals(Message.serverName)){
				attributes.put(matcher.group(1).toLowerCase(Locale.US),new AttributeString(matcher.group(1).toLowerCase(Locale.US),matcher.group(2)));
			}else{
				try{
					attributes.put(matcher.group(1).toLowerCase(Locale.US),new AttributeInt(matcher.group(1).toLowerCase(Locale.US),Integer.parseInt(matcher.group(2).trim())));
				}catch(Exception e){
					attributes.put(matcher.group(1).toLowerCase(Locale.US),new AttributeString(matcher.group(1).toLowerCase(Locale.US),matcher.group(2)));
				}
			}

		}
		
		// Parsing headers of the body
		if(attributes.containsKey(Message.contentLengthName)){
			
			content = ContentBase.Factory.getDefault();
			if(attributes.containsKey(Message.contentTypeName))
				content.setContentType(getContentType().getValue());
			if(attributes.containsKey(Message.contentBaseName))
				content.setContentBase(getContentBase().getValue());
			
			int contentLength = getContentLength().getValue();
			char[] buffer = new char[contentLength];
			char[] temp = line.toCharArray();
			System.arraycopy(temp, 0, buffer, 0, temp.length);
			int readlen = temp.length;

			int len = 0;
			contentLength-=readlen;
			while ( (len = input.read(buffer, readlen, contentLength))>0 ) {

				readlen +=len;
				contentLength-=readlen;
				if(contentLength<=0)
					break;
			}
			content.setContent(String.valueOf(buffer));
			if(contentLength<=0){
				mComplete = true;
			}
		}else{
			mComplete = true;
		}
	}
	
	void initURL(String urlStr){
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public String packageMessage(){
		StringBuffer messageString = packageHeader();
		if(messageString==null){
			messageString = new StringBuffer();
		}else{
			messageString.append("\r\n");
		}
		String contentStr = "";
		if(content!=null){
			contentStr = content.getContent();
		}

		for(Attribute attribute:attributes.values()){
			messageString.append(attribute.getAttributeString()).append("\r\n");
		}
		messageString.append("\r\n");
		messageString.append(contentStr).append("\r\n");
		return messageString.toString();
	}
	

	
	public boolean isComplete() {
		return mComplete;
	}

	public String getPVersion() {
		return pVersion;
	}

	public void setPVersion(String pVersion) {
		this.pVersion = pVersion;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public ContentBase getContent() {
		return content;
	}

	public void setContent(ContentBase content) {
		this.content = content;
		this.setContentLength(content.getContentBuffer().length);
		if(content.getContentType()!=null)
			setContentType(content.getContentType());
		if(content.getContentBase()!=null)
			setContentBase(content.getContentBase());
	}

	public void setAttribute(Attribute attr){
		attributes.put(attr.getName(), attr);
	}
	
	public Attribute setAttribute(String attrStr){
		int colon = attrStr.indexOf(':');
		if (colon == -1)
			return null;
		else{
			String name = attrStr.substring(0, colon).trim().toLowerCase();
			String value = attrStr.substring(++colon).trim();
			try{
				attributes.put(name,new AttributeInt(name,Integer.parseInt(value)));
			}catch(Exception e){
				attributes.put(name,new AttributeString(name,value));
			}
			return attributes.get(name);
		}

	}
	
	public AttributeInt setAttribute(String name,int value){
		attributes.put(name,new AttributeInt(name,value));
		return (AttributeInt)attributes.get(name);
	}
	
	public AttributeString setAttribute(String name,String value){
		attributes.put(name,new AttributeString(name,value));
		return (AttributeString)attributes.get(name);
	}

	
	public AttributeString getServer() {
		return (AttributeString)attributes.get(serverName);
	}

	public AttributeInt getCseq() {
		return (AttributeInt)attributes.get(cseqName);
	}

	public AttributeInt getContentLength() {
		return (AttributeInt)attributes.get(contentLengthName);
	}

	public AttributeString getContentType() {
		return (AttributeString)attributes.get(contentTypeName);
	}

	public AttributeString getContentBase() {
		return (AttributeString)attributes.get(contentBaseName);
	}
	
	public AttributeString getSession() {
		return (AttributeString)attributes.get(sessionName);
	}
	
	public AttributeString setSession(String value) {
		return setAttribute(sessionName,value);
	}
	
	public AttributeString setServer(String value) {
		return setAttribute(serverName,value);
	}

	public AttributeInt setCseq(int value) {
		return setAttribute(cseqName,value);
	}

	public AttributeInt setContentLength(int value) {
		return setAttribute(contentLengthName,value);
	}

	public AttributeString setContentType(String value) {
		return setAttribute(contentTypeName,value);
	}

	public AttributeString setContentBase(String value) {
		return setAttribute(contentBaseName,value);
	}
	

}
