package com.xys.ecg.bean;
public class UserEntity {

	private int userID;        //用户ID
	private String userName;   //用户名


	public UserEntity(int userID,String userName)
	{
		this.userID = userID;
		this.userName = userName;
	}
	
	public int getUserID()
	{
		return this.userID;
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public void setUserID(int userID)
	{
		this.userID = userID;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
}
