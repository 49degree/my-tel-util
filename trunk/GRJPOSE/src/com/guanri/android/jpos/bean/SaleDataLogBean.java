package com.guanri.android.jpos.bean;

public class SaleDataLogBean {
	// 主键
	public int logid;
	// POS流水号 POS终端系统跟踪号
	public String PosNo;
	// 终端MAC 终端上传交易包的MAC值，转换成HEX
	public String PosMac;
	// POS交易类型码　消费，撤销，退货
	public String TransactionType;
	// 服务器消息类型码
	public String MsgTypeCode;
	// 交易状态  建立,发送,接收,成功,已冲正,已冲正成功
	// 0  已发送
	// 1 后台已返回
	// 2 收到后台回执
	public int TransactionState;
	// 交易金额
	public long TransactionMoney;
	// 订单编号
	public String OrderNo;
	// 卡号
	public String CardNo;
	// 检索参考号
	public String SearchNo;
	// 授权码  
	public String AuthorizationNo;
	// 日期 MMDD
	public String DataStr;
	// 时间 hhmmss
	public String TimeStr;
	// 批次号
	public String BatchNo;
	// 服务器消息处理码
	public String ProcessCode;
	
	public String getMsgTypeCode() {
		return MsgTypeCode;
	}
	public void setMsgTypeCode(String msgTypeCode) {
		MsgTypeCode = msgTypeCode;
	}
	public String getBatchNo() {
		return BatchNo;
	}
	public void setBatchNo(String batchNo) {
		BatchNo = batchNo;
	}
	public String getPosNo() {
		return PosNo;
	}
	public void setPosNo(String posNo) {
		PosNo = posNo;
	}
	public String getPosMac() {
		return PosMac;
	}
	public void setPosMac(String posMac) {
		PosMac = posMac;
	}
	public String getTransactionType() {
		return TransactionType;
	}
	public void setTransactionType(String transactionType) {
		TransactionType = transactionType;
	}
	public int getTransactionState() {
		return TransactionState;
	}
	public void setTransactionState(int transactionState) {
		TransactionState = transactionState;
	}
	public long getTransactionMoney() {
		return TransactionMoney;
	}
	public void setTransactionMoney(long transactionMoney) {
		TransactionMoney = transactionMoney;
	}
	public String getOrderNo() {
		return OrderNo;
	}
	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}
	public String getCardNo() {
		return CardNo;
	}
	public void setCardNo(String cardNo) {
		CardNo = cardNo;
	}
	public String getSearchNo() {
		return SearchNo;
	}
	public void setSearchNo(String searchNo) {
		SearchNo = searchNo;
	}
	public String getAuthorizationNo() {
		return AuthorizationNo;
	}
	public void setAuthorizationNo(String authorizationNo) {
		AuthorizationNo = authorizationNo;
	}
	public String getDataStr() {
		return DataStr;
	}
	public void setDataStr(String dataStr) {
		DataStr = dataStr;
	}
	public String getTimeStr() {
		return TimeStr;
	}
	public void setTimeStr(String timeStr) {
		TimeStr = timeStr;
	}
	public int getLogid() {
		return logid;
	}
	public void setLogid(int logid) {
		this.logid = logid;
	}
	public String getProcessCode() {
		return ProcessCode;
	}
	public void setProcessCode(String processCode) {
		ProcessCode = processCode;
	}
	
	
}
