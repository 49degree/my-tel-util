package com.yangxp.rtsp.client.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.yangxp.net.tcp.SocketClient;
import com.yangxp.net.tcp.impl.SocketHandlerClient;
import com.yangxp.rtsp.impl.RtspSessionImpl;
import com.yangxp.rtsp.message.ContentBase;
import com.yangxp.rtsp.message.Message;
import com.yangxp.rtsp.message.MessageBase;
import com.yangxp.rtsp.message.RequestMessage;
import com.yangxp.rtsp.message.ResponseMessage;
import com.yangxp.rtsp.message.Message.Method;
import com.yangxp.rtsp.message.Message.RESULT;
import com.yangxp.rtsp.utils.UUIDGenerator;

public class ResponseClient extends RtspClientImpl{
	public ResponseClient(SocketClient socketClient){
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
	
	void requestSession(MessageBase requestMessage){
		if(mRtspSession!=null)
			return;
		String sessionid = null;
		if(requestMessage.getSession()==null){
			sessionid = UUIDGenerator.getUUID();
		}else{
			sessionid = requestMessage.getSession().getAttributeString();
		}
		mRtspSession = new RtspSessionImpl(sessionid);
	}
	
	@Override
	public void response(ResponseMessage response){
		// TODO Auto-generated method stub
		sendMessage(response);
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
		responseMessage.setCseq(request.getCseq().getValue());
		if (request.getMethod().equals(Method.DESCRIBE)) {
			// Parse the requested URI and configure the session
			responseMessage.setResult(RESULT.STATUS_OK);
		}

		/* ********************************************************************************** */
		/* ********************************* Method OPTIONS ********************************* */
		/* ********************************************************************************** */
		else if (request.getMethod().equals(Method.OPTIONS)) {
			responseMessage.setResult(RESULT.STATUS_OK);
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
