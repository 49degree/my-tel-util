package com.guanri.android.jpos.bean;

public class SaleDataLogBean {
	// POS流水号 POS终端系统跟踪号
	public String PosNo;
	// 终端MAC 终端上传交易包的MAC值，转换成HEX
	public String PosMac;
	// 交易类型　消费，撤销，退货
	public String TransactionType;
	// 消息处理码
	public String MsgTypeCode;
	// 交易状态  建立,发送,接收,成功,已冲正,已冲正成功
	// 0  已发送
	// 1 后台已返回
	// 2 收到后台回执
	public int TransactionState;
	// 交易金额
	public int TransactionMoney;
	// 订单编号
	public String OrderNo;
	// 卡号
	public String CardNo;
	// 有效期
	public String CardPeriod;
	// 2磁道数据
	public String Track2;
	// 3磁道数据
	public String Track3;
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
	public int getTransactionMoney() {
		return TransactionMoney;
	}
	public void setTransactionMoney(int transactionMoney) {
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
	public String getCardPeriod() {
		return CardPeriod;
	}
	public void setCardPeriod(String cardPeriod) {
		CardPeriod = cardPeriod;
	}
	public String getTrack2() {
		return Track2;
	}
	public void setTrack2(String track2) {
		Track2 = track2;
	}
	public String getTrack3() {
		return Track3;
	}
	public void setTrack3(String track3) {
		Track3 = track3;
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
	
	
}
