package com.a3650.posserver.core.bean;

import java.io.Serializable;

public class ReceiveMsg implements Serializable{  
    /**  
     *   
     */ 
    private static final long serialVersionUID = 2L;  

    private long id; 
    
    private String receiveMsg;  
    
    private String sendMsg;
    private String  receiveTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReceiveMsg() {
		return receiveMsg;
	}

	public void setReceiveMsg(String receiveMsg) {
		this.receiveMsg = receiveMsg;
	}

	public String getSendMsg() {
		return sendMsg;
	}

	public void setSendMsg(String sendMsg) {
		this.sendMsg = sendMsg;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	} 
    
    
}
