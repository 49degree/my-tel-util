package com.yangxp.rtsp.client.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.yangxp.net.tcp.SocketClient;
import com.yangxp.net.tcp.impl.SocketHandlerClient;
import com.yangxp.rtsp.RtspSession;
import com.yangxp.rtsp.attribute.AttributeInt;
import com.yangxp.rtsp.attribute.AttributeString;
import com.yangxp.rtsp.client.RtspClient;
import com.yangxp.rtsp.impl.RtspSessionImpl;
import com.yangxp.rtsp.message.ContentBase;
import com.yangxp.rtsp.message.Message;
import com.yangxp.rtsp.message.RequestMessage;
import com.yangxp.rtsp.message.ResponseMessage;
import com.yangxp.rtsp.message.Message.Method;
import com.yangxp.rtsp.message.Message.RESULT;
import com.yangxp.rtsp.utils.MyStreamHandlerFactory;
import com.yangxp.rtsp.utils.UUIDGenerator;

public class RtspClientImpl implements RtspClient{
	static Logger logger = Logger.getLogger(RtspClientImpl.class);
	static{
		try{
			URL.setURLStreamHandlerFactory(new MyStreamHandlerFactory());
		}catch(Error e){
			
		}
	}
	
	SocketClient mSocketClient = null;
	RtspSession mRtspSession = null;
	int cseq = 1;
	
	HashMap<Integer,RequestMessage> requestList = new HashMap<Integer,RequestMessage>();
	String mRstpURL = null;
	URL mURL = null;
	
	public RtspClientImpl(String rstpURL){
		mRstpURL = rstpURL;
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
	
	public RtspClientImpl(SocketClient socketClient){
		mSocketClient = socketClient;
		mSocketClient.setSocketHandler(new SocketHandlerClient(){
			RequestMessage requestMessage = null;
			//ResponseMessage responseMessage = null;
			@Override
			public void onReceiveCmd(ByteBuffer buffer) {
				// TODO Auto-generated method stubge);
				if(requestMessage==null)
					requestMessage = new RequestMessage();
				try {
					requestMessage.parseInputStream(buffer);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					requestMessage = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					requestMessage = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				while(requestMessage!=null&&
						requestMessage.isComplete()){
					onRequest(requestMessage);
					Message message = null;
					try {
						message = requestMessage.nextMessage();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(message!=null){
						if(message instanceof RequestMessage){
							requestMessage = (RequestMessage)message;
							continue;
						}
					}
					requestMessage = null;
				}
			}
		});
	}
	

	

	private void sendMessage(Message message) {
		// TODO Auto-generated method stub
		String messageStr = message.packageMessage();
		ByteBuffer buffer = ByteBuffer.wrap((messageStr).getBytes());
		
		
		mSocketClient.sendBuffer(buffer);
	}

	@Override
	public void request(RequestMessage request){
		// TODO Auto-generated method stub
		request.setCseq(cseq++);
		request.setUrl(mURL);
		requestList.put(request.getCseq().getValue(),request);
		sendMessage(request);
	}
	
	@Override
	public void response(ResponseMessage response){
		// TODO Auto-generated method stub
		sendMessage(response);
	}
	
	@Override
	public void onResponse(ResponseMessage response){
		// TODO Auto-generated method stub
		AttributeInt cseqAttr = response.getCseq();
		if(cseqAttr==null||requestList.get(cseqAttr.getValue())==null)
			return;
		
		AttributeString session = response.getSession();
		if(session!=null){
			RtspSession temp = new RtspSessionImpl(session.getValue());
			if(mRtspSession!=null&&mRtspSession.equals(temp))
				return;
		}
		logger.debug(response.packageMessage());	
	}

	private void requestSession(RequestMessage requestMessage){
		String sessionid = UUIDGenerator.getUUID();
		mRtspSession = new RtspSessionImpl(sessionid);
	}
	
	@Override
	public void onRequest(RequestMessage request) {
		logger.debug("request:\n"+request.packageMessage().toString());
		
		// TODO Auto-generated method stub
		/* ********************************************************************************** */
		/* ********************************* Method DESCRIBE ******************************** */
		/* ********************************************************************************** */
		requestSession(request);
		ResponseMessage responseMessage = new ResponseMessage();
		
		if (request.getMethod().equals(Method.DESCRIBE)) {
			// Parse the requested URI and configure the session

		}

		/* ********************************************************************************** */
		/* ********************************* Method OPTIONS ********************************* */
		/* ********************************************************************************** */
		else if (request.getMethod().equals(Method.OPTIONS)) {
			responseMessage.setResult(RESULT.STATUS_OK);
			responseMessage.setCseq(request.getCseq().getValue());
			responseMessage.setContent(ContentBase.Factory.getDefault().setContent("Public: DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE\r\n"));
		}

		/* ********************************************************************************** */
		/* ********************************** Method SETUP ********************************** */
		/* ********************************************************************************** */
		else if (request.getMethod().equals(Method.SETUP)) {


		}

		/* ********************************************************************************** */
		/* ********************************** Method PLAY *********************************** */
		/* ********************************************************************************** */
		else if (request.getMethod().equals(Method.PLAY)) {

		}

		/* ********************************************************************************** */
		/* ********************************** Method PAUSE ********************************** */
		/* ********************************************************************************** */
		else if (request.getMethod().equals(Method.PAUSE)) {
		}

		/* ********************************************************************************** */
		/* ********************************* Method TEARDOWN ******************************** */
		/* ********************************************************************************** */
		else if (request.getMethod().equals(Method.TEARDOWN)) {
		}

		/* ********************************************************************************** */
		/* ********************************* Unknown method ? ******************************* */
		/* ********************************************************************************** */
		else {
			logger.error("Command unknown: "+request);
		}
		this.response(responseMessage);
	}
}
