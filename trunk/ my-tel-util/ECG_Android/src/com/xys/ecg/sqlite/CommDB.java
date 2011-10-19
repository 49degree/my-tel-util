package com.xys.ecg.sqlite;

import android.database.sqlite.SQLiteDatabase;


public final class CommDB {

	public static final String dataBaseName = "ecg_databse.db";
	
	public static void createTable(SQLiteDatabase db)
	{
		db.execSQL("create table tb_user(UserID integer default 0 ,UserName nvarchar(20) default Anonymous)");    //创建用户表
		db.execSQL("create index userindex on tb_user(UserID)");  //为用户表建立索引
		db.execSQL("create table tb_doctorAdvice(AdviceID integer primary key ,UserID integer ,PublishTime varchar(20) ,ArriveTime varchar(20) ,DoctorName nvarchar(20) ,Content nvarchar(200) ,ReadFlag integer default 0)");   //创建医嘱表
		db.execSQL("create index idToDoctorAdvice on tb_doctorAdvice(AdviceID)");
		db.execSQL("create table tb_record(RecordID integer primary key ,UserID integer ," +
				"StartTime bigint ,FilePath nvarchar(40) ,Mode integer ,State integer default 0 ,Uploaded integer)");
		db.execSQL("create index idToRecord on tb_record(RecordID)");
		db.execSQL("create table tb_Contact(ContactID integer primary key ,UserID integer ,ContactName nvarchar(20) ,SMS integer default 0 ,Phone integer default 0 ,PhoneNum varchar(20) ,MSMContent nvarchar(200))");
		db.execSQL("create index idToContact on tb_Contact(ContactID)");
		
		//建立配置文件
		db.execSQL("create table SMSTemplate(SMStemplateID varchar(30) primary key, SMSContent varchar(30))");
		db.execSQL("create table ECGConfig(Id INTEGER PRIMARY KEY,StoragePath varchar(50),  Logs varchar(50), Lead varchar(20), FontSize varchar(30),BaseLineDrift varchar(20),RemoveNoise varchar(20),AutoUpload varchar(20),Version  varchar(50),EconomyrMode varchar(50),MPLowSpace varchar(20),AlarmShock varchar(20),AlarmSound varchar(20),SDLowSpace varchar(20) )");
		db.execSQL("create table Device( Id INTEGER PRIMARY KEY ,AppTypeID varchar(50),  PatientPkID varchar(50), PatientName varchar(50),DoctorName varchar(50), MobileID varchar(50), MobileBtAddr varchar(50), CollectorID varchar(50),  CollectorBtAddr varchar(50),  NetApnName varchar(50),  NetUserName varchar(50),  NetPassword varchar(150), SetParam varcgar(150), WebServiceAddrOfWan varchar(150), WebServiceAddrOfLan varchar(150), ValidateCode varchar(150))");
		
		db.execSQL("insert into ECGConfig values(1,'/Storage Card/EcgData','5','1','12','FALSE','FALSE','FALSE','','Mode1','5','FALSE','TRUE','50')");
		db.execSQL("insert into Device values(0,1,'125','Peng','CString PkID','Anonymous','CString MbID','CString Addr','00:19:5D:24:CD:90','CString Name','apn Name','CString UserName','CString PSW','http://172.18.17.46/WrmWebService/Ecg/WrmRemoteService.asmx','http://172.18.17.46/WrmWebService/Ecg/WrmRemoteService.asmx','30067816539')");
	}
}
