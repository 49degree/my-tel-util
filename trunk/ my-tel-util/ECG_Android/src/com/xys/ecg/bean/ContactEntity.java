package com.xys.ecg.bean;

public class ContactEntity {
	private int contactID;
	private int userID;
	private String contactName;
	private int sMS;
	private int phone;
	private String phoneNum;
	private String mSMContent;

	public ContactEntity(int userID ,String contactName ,int sMS ,int phone ,String phoneNum ,String mSMContent)
	{
		this.userID = userID;
		this.contactName = contactName;
		this.sMS = sMS;
		this.phone = phone;
		this.phoneNum = phoneNum;
		this.mSMContent = mSMContent;
	}
	
	public int getContactID() {
		return contactID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public int getsMS() {
		return sMS;
	}

	public void setsMS(int sMS) {
		this.sMS = sMS;
	}

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getmSMContent() {
		return mSMContent;
	}

	public void setmSMContent(String mSMContent) {
		this.mSMContent = mSMContent;
	}

}
