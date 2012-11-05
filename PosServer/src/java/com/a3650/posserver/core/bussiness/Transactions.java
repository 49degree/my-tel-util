package com.a3650.posserver.core.bussiness;

public class Transactions {
	public String transCode;//	处理码
	public String serverCode;//服务点条件码
	public String msgTypeCode;//消息类型
	public String transTypeCode;//交易类型码
	
	public int hashCode;
	public Transactions(){
		
	}
	public Transactions(String transCode, String serverCode,
			String msgTypeCode, String transTypeCode) {
		super();
		this.transCode = transCode;
		this.serverCode = serverCode;
		this.msgTypeCode = msgTypeCode;
		this.transTypeCode = transTypeCode;
	}
	
	public String getTransCode() {
		return transCode;
	}
	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}
	public String getServerCode() {
		return serverCode;
	}
	public void setServerCode(String serverCode) {
		this.serverCode = serverCode;
	}
	public String getMsgTypeCode() {
		return msgTypeCode;
	}
	public void setMsgTypeCode(String msgTypeCode) {
		this.msgTypeCode = msgTypeCode;
	}
	public String getTransTypeCode() {
		return transTypeCode;
	}
	public void setTransTypeCode(String transTypeCode) {
		this.transTypeCode = transTypeCode;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((msgTypeCode == null) ? 0 : msgTypeCode.hashCode());
		result = prime * result
				+ ((serverCode == null) ? 0 : serverCode.hashCode());
		result = prime * result
				+ ((transCode == null) ? 0 : transCode.hashCode());
		result = prime * result
				+ ((transTypeCode == null) ? 0 : transTypeCode.hashCode());
		hashCode = result;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transactions other = (Transactions) obj;
		if (msgTypeCode == null) {
			if (other.msgTypeCode != null)
				return false;
		} else if (!msgTypeCode.equals(other.msgTypeCode))
			return false;
		if (serverCode == null) {
			if (other.serverCode != null)
				return false;
		} else if (!serverCode.equals(other.serverCode))
			return false;
		if (transCode == null) {
			if (other.transCode != null)
				return false;
		} else if (!transCode.equals(other.transCode))
			return false;
		if (transTypeCode == null) {
			if (other.transTypeCode != null)
				return false;
		} else if (!transTypeCode.equals(other.transTypeCode))
			return false;
		return true;
	}
	
	
}
