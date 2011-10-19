package com.szxys.mhub.base.manager;

public class DBConstDef {

	/**
	 * 配置信息表。
	 */
	public final static String TABLE_CONFIG = "mb_config";

	/**
	 * 采集器表。
	 */
	public final static String TABLE_COLLECTOR = "mb_collector";

	/**
	 * 用户表。
	 */
	public final static String TABLE_USER = "mb_user";

	/**
	 * 组织表。
	 */
	public final static String TABLE_ORGANIZATION = "mb_organization";

	/**
	 * 用户组织关系表。
	 */

	public final static String TABLE_USER_ORG_RELATIONAL = "mb_user_org_relational";

	/**
	 * 用户采集器关系表。
	 */

	public final static String TABLE_USER_COLLECTOR_RELATIONAL = "mb_user_collector_relational";

	/**
	 * 异常信息表
	 */
	public final static String TABLE_ERRORMESSAGE = "errorInfo";

	/**
	 * 监护参数表
	 */
	public final static String TABLE_Monitoring_Parameters = "mb_uonitoring_parameters";

	/**
	 * 医生消息
	 */
	public final static String TABLE_INCOMING_MSG = "mb_incoming";

	/**
	 * 病人消息
	 */
	public final static String TABLE_OUTGOING_MSG = "mb_outgoing";

	/**
	 * 配置信息表创建SQL语句。
	 */
	public static final String TABLE_CONFIG_CREATE_SQL = " CREATE TABLE IF NOT EXISTS "
			+ TABLE_CONFIG
			+ " (Key VARCHAR(20) PRIMARY KEY, "
			+ "  Value VARCHAR(100) NOT NULL)";

	/**
	 * 采集器表创建SQL语句。
	 */
	public static final String TABLE_COLLETOR_CREATE_SQL = " CREATE TABLE IF NOT EXISTS "
			+ TABLE_COLLECTOR
			+ " (ID INTEGER PRIMARY KEY, "
			+ "  HeartBeatInterval INTEGER NOT NULL, "
			+ "  PhysicalCode NVARCHAR(30) NOT NULL, "
			+ "  DeviceType SMALLINT NOT NULL, "
			+ "  Mac VARCHAR(18) NOT NULL UNIQUE, "
			+ "  NumOfChannels SMALLINT NOT NULL, "
			+ "  PairingCode VARCHAR(10) NOT NULL, "
			+ "  PassiveMode SMALLINT NOT NULL, "
			+ "  ProtocolType INTEGER NOT NULL, " + "  Desc NVARCHAR(50))";

	/**
	 * 用户表创建SQL语句。
	 */
	public static final String TABLE_USER_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_USER
			+ " (ID INTEGER PRIMARY KEY, "
			+ "  Name NVARCHAR(30) NOT NULL, "
			+ "  MemberID VARCHAR(30) NOT NULL, "
			+ "  TreatmentID VARCHAR(30), "
			+ "  Sex NVARCHAR(2), "
			+ "  Birthday DATE, "
			+ "  MaritalStatus NVARCHAR(2), "
			+ "  Nationality NVARCHAR(20), "
			+ "  AreaID SMALLINT, "
			+ "  Nation NVARCHAR(20), "
			+ "  BornPlace NVARCHAR(20), "
			+ "  Education NVARCHAR(10), "
			+ "  CredNO VARCHAR(20), "
			+ "  CredType NVARCHAR(20), "
			+ "  Address NVARCHAR(100), "
			+ "  Postalcode VARCHAR(8), "
			+ "  Urgency1 NVARCHAR(20), "
			+ "  UrgentPhone1 VARCHAR(20), "
			+ "  Urgency2 NVARCHAR(20), "
			+ "  UrgentPhone2 VARCHAR(20), "
			+ "  Phone VARCHAR(30), "
			+ "  Mobile VARCHAR(30), "
			+ "  WorkPhone VARCHAR(20), "
			+ "  Email VARCHAR(30), "
			+ "  WorkOrg NVARCHAR(50), "
			+ "  Job NVARCHAR(20), "
			+ "  Creator NVARCHAR(20), "
			+ "  PayType NVARCHAR(10), "
			+ "  RegDate DATE, "
			+ "  BeginDate DATE, "
			+ "  EndDate DATE, "
			+ "  Status NVARCHAR(10))";

	/**
	 * 组织表创建SQL语句。
	 */
	public static final String TABLE_ORGANIZATION_CREATE_SQL = " CREATE TABLE IF NOT EXISTS "
			+ TABLE_ORGANIZATION
			+ " (ID INTEGER PRIMARY KEY, "
			+ "  IsHospital SMALLINT NOT NULL, "
			+ "  ParentOrgID INTEGER NOT NULL, "
			+ "  Name NVARCHAR(50) NOT NULL) ";

	/**
	 * 用户组织关系表创建SQL语句。
	 */
	public static final String TABLE_USER_ORG_RELATION_CREATE_SQL = " CREATE TABLE IF NOT EXISTS "
			+ TABLE_USER_ORG_RELATIONAL
			+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "  UserID INTEGER NOT NULL, " + "  OrgID INTEGER NOT NULL) ";

	/**
	 * 用户采集器关系表创建SQL语句。
	 */
	public static final String TABLE_USER_COLLECTOR_RELATION_CREATE_SQL = " CREATE TABLE IF NOT EXISTS "
			+ TABLE_USER_COLLECTOR_RELATIONAL
			+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "  UserID INTEGER NOT NULL, "
			+ "  DeviceType SMALLINT NOT NULL, "
			+ "  CollectorID INTEGER NOT NULL) ";
	/**
	 * 异常信息表创建SQL语句。
	 */
	public static final String TABLE_ERRORMESSAGE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_ERRORMESSAGE
			+ "(_id integer primary key autoincrement, "
			+ "appId integer,alarmType varchar(10), "
			+ "alarmLevelId int,alarmDescription varchar(256), "
			+ "AlarmTime varchar(20))";

	public static final String TABLE_Monitoring_Parameters_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_Monitoring_Parameters
			+ "(_id integer primary key autoincrement,appID integer,DownTime varchar(10),IsChange int)";

	public static final String TABLE_INCOMMINGMSG_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_INCOMING_MSG
			+ "(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+ "c_AlertId TEXT NOT NULL, c_DoctorsNo TEXT, "
			+ "c_DoctorsName TEXT ,c_PatientNo TEXT, c_PatientName TEXT,c_Content TEXT,"
			+ "c_ReceiveDt TIMESTAMP NOT NULL DEFAULT '',c_SourceMsgId TEXT NOT NULL,"
			+ "c_AppType SMALLINT NOT NULL DEFAULT 0,c_IsRead BOOL NOT NULL DEFAULT 0)";

	public static final String TABLE_OUTGOINT_MSG_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_OUTGOING_MSG
			+ "(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+ "c_DoctorsNo TEXT NOT NULL, c_DoctorsName TEXT ,"
			+ "c_PatientNo TEXT  NOT NULL, c_PatientName TEXT,c_Content TEXT,"
			+ "c_SendDt TIMESTAMP NOT NULL DEFAULT '',c_SourceMsgId TEXT NOT NULL,"
			+ "c_AppType SMALLINT NOT NULL DEFAULT 0,c_IsSend BOOL NOT NULL DEFAULT 0)";
}
