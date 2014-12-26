package com.yangxp.rtsp.client.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;

import com.yangxp.net.tcp.SocketClient;
import com.yangxp.net.tcp.impl.SocketHandlerClient;
import com.yangxp.rtp.UDPClient;
import com.yangxp.rtsp.attribute.AttributeInt;
import com.yangxp.rtsp.attribute.AttributeString;
import com.yangxp.rtsp.attribute.TransportAttribute;
import com.yangxp.rtsp.impl.RtspSessionImpl;
import com.yangxp.rtsp.message.Message;
import com.yangxp.rtsp.message.Message.Method;
import com.yangxp.rtsp.message.MessageBase;
import com.yangxp.rtsp.message.RequestMessage;
import com.yangxp.rtsp.message.ResponseMessage;

public class RequestClient extends RtspClientImpl{
	String userAgent = "User-Agent:yangxp client";
	
	public RequestClient(String rstpURL,String rtspVersion){
		mRstpURL = rstpURL;
		mRtspVersion = rtspVersion;
		try {
			mURL = new URL(rstpURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalArgumentException("rtsp url is error");
		}
		
		mSocketClient = SocketClient.Factory.getDefault(new InetSocketAddress(mURL.getHost(), mURL.getPort())); 
		mSocketClient.setSocketHandler(new SocketHandlerClient(){
			ResponseMessage responseMessage = null;
			@Override
			public void onReceiveCmd(ByteBuffer buffer) {
				// TODO Auto-generated method stub
				if(responseMessage==null)
					responseMessage = new ResponseMessage();
				try {
					responseMessage.parseInputStream(buffer);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					responseMessage = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					responseMessage = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return;
				}

				while(responseMessage!=null&&
						responseMessage.isComplete()){
					onResponse(responseMessage);
					Message message = null;
					try {
						message = responseMessage.nextMessage();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(message!=null){
						if(message instanceof ResponseMessage){
							responseMessage = (ResponseMessage)message;
							continue;
						}
					}
					responseMessage = null;
				}
					
			}
		});
		mSocketClient.doOpen();
	}
	
	
	void requestSession(MessageBase message){
		if(mRtspSession!=null)
			return;
		else
			mRtspSession = new RtspSessionImpl();
	}
	
	@Override
	public void request(RequestMessage request){
		// TODO Auto-generated method stub
		request.setPVersion(mRtspVersion);
		request.setCseq(cseq++);
		
		request.setAttribute(userAgent);
		requestList.put(request.getCseq().getValue(),request);
		sendMessage(request);
	}
	

	
	@Override
	public void onResponse(ResponseMessage response){
		// TODO Auto-generated method stub
		AttributeInt cseqAttr = response.getCseq();
		if(cseqAttr==null||requestList.get(cseqAttr.getValue())==null)
			return;
		RequestMessage request = requestList.get(cseqAttr.getValue());
		
		if (request.getMethod().equals(Method.DESCRIBE)) {
			// Parse the requested URI and configure the session
			requestSession(response);
			setup();
		}

		/* ********************************************************************************** */
		/* ********************************* Method OPTIONS ********************************* */
		/* ********************************************************************************** */
		else if (request.getMethod().equals(Method.OPTIONS)) {
			allowMethod.clear();
			//Public: DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE
			String content = response.getContent().getContent();
			if(content.indexOf(":")>-1){
				String methodStr = content.substring(content.indexOf(":"));
				String[] methods = methodStr.split(",");
				for(String method:methods){
					allowMethod.put(method.trim(), Message.Method.parseMethod(method.trim()));
				}
			}
			
		}
		
			
		logger.debug(response.packageMessage());	
	}
	
	HashMap<String,Message.Method> allowMethod = new HashMap<String,Message.Method>();
	
	public void option(){
		RequestMessage inta = new RequestMessage();
		inta.setMethod(Message.Method.OPTIONS);
		inta.setUrl(mURL);
		request(inta);
	}
	
	public void describe(){
		RequestMessage inta = new RequestMessage();
		inta.setMethod(Message.Method.DESCRIBE);
		inta.setUrl(mURL);
		inta.setAttribute(new AttributeString("Accept: application/sdp"));
		
		request(inta);
	}
	
	public void setup(){
		UDPClient UDPClient1 = new UDPClient(5006);
		UDPClient1.init();
//		UDPClient UDPClient2 = new UDPClient(5009);
//		UDPClient2.init();
		
		RequestMessage inta = new RequestMessage();
		inta.setMethod(Message.Method.SETUP);
		try {
			inta.setUrl(new URL("rtsp://"+mURL.getHost()+":"+mURL.getPort()+"/trackID=1"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inta.setAttribute(new TransportAttribute("Transport: RTP/AVP;unicast;client_port=5006-5007"));
		request(inta);
	}
	
	
}
