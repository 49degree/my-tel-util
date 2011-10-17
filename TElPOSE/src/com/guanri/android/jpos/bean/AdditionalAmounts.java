package com.guanri.android.jpos.bean;

/**
 * 返回的余额对象
 * @author Administrator
 *
 */
public class AdditionalAmounts {
//	0－1处理码3－4或5－6位定义的帐号类型  
//	2－3金额类型：01－帐户金额  
//	02－可用金额  
//	03－拥有金额  
//	04－应付金额  
//	40－可用取款限额  
//	56－可用转帐限额  
//	4－6金额的货币代码  
//	7‘D’－借记金额，’C’－贷记金额  
//	8－19余额数目  
	public static final String ACCOUNTMONEY = "01";
	public static final String MONEYUSECAN = "02";
	public static final String ALLMONEY = "03";
	public static final String PAYMONEY = "04";
	public static final String CANWITHDRAWALS = "40";
	public static final String CANTRANSFER = "56";
	
	
	String code = null;//处理码
	String amountType = null;//金额类型
	String huobiCode = null;//货币代码
	String banlanceType = null;//‘D’－借记金额，’C’－贷记金额  
	String amount = null;//余额数目  
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAmountType() {
		return amountType;
	}
	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}
	public String getHuobiCode() {
		return huobiCode;
	}
	public void setHuobiCode(String huobiCode) {
		this.huobiCode = huobiCode;
	}
	public String getBanlanceType() {
		return banlanceType;
	}
	public void setBanlanceType(String banlanceType) {
		this.banlanceType = banlanceType;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	
	
	
}
