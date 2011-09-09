package com.guanri.android.jpos.iso.bill99;

public class TLVType {
	// 24 商户名称
	public String merchant_name;
	// 25 电话号码1
	public String tel1;
	// 26 电话号码2
	public String tel2;
	public String getMerchant_name() {
		return merchant_name;
	}
	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}
	public String getTel1() {
		return tel1;
	}
	public void setTel1(String tel1) {
		this.tel1 = tel1;
	}
	public String getTel2() {
		return tel2;
	}
	public void setTel2(String tel2) {
		this.tel2 = tel2;
	}
	
	
}
