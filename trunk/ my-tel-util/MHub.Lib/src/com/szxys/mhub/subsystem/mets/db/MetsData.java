package com.szxys.mhub.subsystem.mets.db;

public class MetsData {
	public class Tables {
		public static final String SysConfig = "tb_SystemConfig";//系统配置表"tb_SystemConfig"
		public static final String DrinkUrine = "tb_InAndOut";//饮水和排尿信息表"tb_InAndOut"
		public static final String DoctorAdvice = "tb_DoctorAdviceInfo";//温馨提醒"tb_DoctorAdviceInfo"
		public static final String GotoBedGetUp = "tb_GotoBedGetUp";//起床睡觉时间"tb_GotoBedGetUp"
		public static final String HealthTips = "tb_HealthTips";//健康提示"tb_HealthTips"	
		public static final String UrineProportion = "tb_UrineProportion";//尿比重"tb_UrineProportion"
		public static final String UrineRateStatistics = "tb_UrineRecord";//尿流率统计项"tb_UrineRecord"
		public static final String UrineIntervalTime = "tb_UrineIntervalTime";//尿流率间歇"tb_UrineIntervalTime"
		public static final String UrineFlowRecord = "tb_UFR_Record";//尿流率数据"tb_UFR_Record"
		public static final String QuestionnaireType = "tb_QuestionnaireType";//问卷调查模板"tb_QuestionnaireType"
		public static final String QuestionnaireTopic = "tb_QuestionnaireTopic";//问卷问题模板"tb_QuestionnaireTopic"
		public static final String QuestionnaireGrade = "tb_QuestionnaireGrade";//问卷评分模板"tb_QuestionnaireGrade"
		public static final String PatientSurvey = "tb_PatientSurvey";//病人问卷"tb_PatientSurvey"
		public static final String PatientGrade = "tb_PatientGrade";//病人问卷评分"tb_PatientGrade"
	}
	public class SqliteScript {
		public static final String tSysConfig = "CREATE TABLE tb_SystemConfig(c_Hospital TEXT, c_DoctorsNo TEXT, c_DoctorsName TEXT ," 
			+"c_PatientNo TEXT NOT NULL, c_PatientName TEXT, c_DevType SMALLINT NOT NULL, c_MobileId TEXT NOT NULL, c_MobileName TEXT, c_CollectorId TEXT NOT NULL ,"
			+"c_CollectorName TEXT, c_RationDateTime TIMESTAMP DEFAULT '', c_RationGuDt TIMESTAMP DEFAULT '', c_RecyDateTime TIMESTAMP DEFAULT '' ,"
			+"c_CollectDtInterval SMALLINT NOT NULL , c_MaxDuration SMALLINT DEFAULT 160, c_NoDataTime SMALLINT DEFAULT 600 ,"
			+"c_SendDtInterval SMALLINT NOT NULL, c_MeasuringCupWeight SMALLINT NOT NULL, c_Version TEXT, c_Copyright TEXT, c_WebServiceUrl TEXT NOT NULL ,"
			+"c_IsRecycling BOOL NOT NULL, c_IsRegister BOOL NOT NULL, c_AutoCloseBt BOOL NOT NULL, c_HaveSpecificGravity BOOL NOT NULL, c_RebootTimer SMALLINT DEFAULT -1 ,"
			+"c_TimeInitialLead SMALLINT DEFAULT 0, c_GetUpAlarm TIMESTAMP DEFAULT '', c_GotoBedAlarm TIMESTAMP DEFAULT '', c_GprsGuid TEXT ,"
			+"c_LastNetCommTime TIMESTAMP)";//系统配置表"tb_SystemConfig"
		public static final String tDrinkUrine = "CREATE TABLE tb_InAndOut(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+"c_Units SMALLINT NOT NULL DEFAULT 0,c_DateTime TIMESTAMP NOT NULL DEFAULT '', c_Quantity FLOAT NOT NULL DEFAULT 0, c_Type SMALLINT NOT NULL DEFAULT 0, c_IsUpload BOOL NOT NULL DEFAULT 0,"
			+"c_Proportion FLOAT, c_UfrId INTEGER DEFAULT 0, c_CollectType SMALLINT NOT NULL DEFAULT 0,"
			+"c_UniqueId TEXT UNIQUE NOT NULL, c_Status SMALLINT NOT NULL DEFAULT 0)";//饮水和排尿信息表"tb_InAndOut"
		public static final String tDoctorAdvice = "CREATE TABLE tb_DoctorAdviceInfo(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+"c_Type SMALLINT NOT NULL,"
			+"c_InfoId TEXT NOT NULL DEFAULT '', c_RecvDt TIMESTAMP NOT NULL DEFAULT '', c_SendDt TIMESTAMP NOT NULL DEFAULT '', c_DoctorName TEXT, c_Content TEXT,"
			+"c_IsRead BOOL NOT NULL DEFAULT 0, c_IsReply BOOL NOT NULL DEFAULT 0, c_IsUpload BOOL NOT NULL DEFAULT 0, c_RemindTime TIMESTAMP, c_Interval TEXT, c_ExpireTime TIMESTAMP, c_NextRemindTime TIMESTAMP)";//温馨提醒"tb_DoctorAdviceInfo"
		public static final String tGotoBedGetUp = "CREATE TABLE tb_GotoBedGetUp(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+"c_Date TIMESTAMP NOT NULL DEFAULT '', c_GetUpTime TIMESTAMP DEFAULT '', c_GotoBedTime TIMESTAMP DEFAULT '', c_IsUpload BOOL NOT NULL DEFAULT 0,"
			+"c_UniqueId TEXT UNIQUE NOT NULL, c_Status SMALLINT NOT NULL DEFAULT 0)";//起床睡觉时间"tb_GotoBedGetUp"
		public static final String tHealthTips = "CREATE TABLE tb_HealthTips(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, c_WebUniqueId TEXT, "
			+"c_DateTime TIMESTAMP NOT NULL, c_Sender TEXT, c_Content TEXT)";//健康提示"tb_HealthTips"	
		public static final String tUrineProportion = "CREATE TABLE tb_UrineProportion(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, c_DateTime TIMESTAMP NOT NULL DEFAULT '', c_Proportion FLOAT, c_IsMatch BOOL NOT NULL DEFAULT 0)";//尿比重"tb_UrineProportion"
		public static final String vMicturition="CREATE VIEW vw_Micturition AS SELECT * FROM tb_InAndOut WHERE c_Type=1";

		public static final String tUrineRateStatistics = "CREATE TABLE tb_UrineRecord(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+"c_Units SMALLINT NOT NULL DEFAULT 0, c_DateTime TIMESTAMP NOT NULL DEFAULT '', c_Duration SMALLINT NOT NULL DEFAULT 0, c_MeanFlow FLOAT,"
			+"c_Q90 FLOAT, c_PeakFlow FLOAT, c_2SecFlow FLOAT, c_VoidingTime FLOAT, c_FlowTime FLOAT, c_T90 FLOAT, c_TimeToPeak FLOAT,"
			+"c_VoidVolume FLOAT, c_StartPos INTEGER, c_EndPos INTEGER, c_IsUpload BOOL NOT NULL DEFAULT 0)";//尿流率统计项"tb_UrineRecord"
		public static final String tUrineIntervalTime = "CREATE TABLE tb_UrineIntervalTime(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+"c_UrineId INTEGER NOT NULL CONSTRAINT fk_UrineIntervalTime_UrineRecord_Id REFERENCES tb_UrineRecord(c_Id) ON DELETE CASCADE, c_BeginPos FLOAT, c_EndPos FLOAT)";//尿流率间歇"tb_UrineIntervalTime"
		
		public static final String triURINE_INTERVAL_TIME_INSERT="CREATE TRIGGER fki_UrineIntervalTime_UrineRecord_Id BEFORE INSERT ON "
			+"tb_UrineIntervalTime FOR EACH ROW BEGIN "
			+"SELECT RAISE(ROLLBACK, 'insert on table tb_UrineIntervalTime violates foreign key constraint fk_UrineIntervalTime_UrineRecord_Id') "
			+"WHERE (SELECT c_Id FROM tb_UrineRecord WHERE c_Id = NEW.c_UrineId) IS NULL; END;";
		
		public static final String triURINE_INTERVAL_TIME_UPDATE="CREATE TRIGGER fku_UrineIntervalTime_UrineRecord_Id BEFORE UPDATE ON "
			+"tb_UrineIntervalTime FOR EACH ROW BEGIN "
		+"SELECT RAISE(ROLLBACK, 'update on table tb_UrineIntervalTime violates foreign key constraint fk_UrineIntervalTime_UrineRecord_Id') "
		+"WHERE (SELECT c_Id FROM tb_UrineRecord WHERE c_Id = NEW.c_UrineId) IS NULL; END;";
		
		public static final String triURINE_INTERVAL_TIME_DELETE="CREATE TRIGGER fkd_UrineIntervalTime_UrineRecord_Id BEFORE DELETE ON "
			+"tb_UrineRecord FOR EACH ROW BEGIN DELETE FROM tb_UrineIntervalTime WHERE c_UrineId = OLD.c_Id; END;";		
		
		public static final String tUrineFlowRecord = "CREATE TABLE tb_UFR_Record(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+"c_UrineId INTEGER NOT NULL CONSTRAINT fk_UrineRecord_Id REFERENCES tb_UrineRecord(c_Id) ON DELETE CASCADE, c_OrgData TEXT,"
			+"c_FinalData TEXT, c_QuantityData TEXT, c_RateData TEXT)";//尿流率数据"tb_UFR_Record"
		public static final String triUFR_INSERT="CREATE TRIGGER fki_UFR_UrineId_UrineRecord_Id BEFORE INSERT ON tb_UFR_Record FOR EACH ROW BEGIN "
			+"SELECT RAISE(ROLLBACK, 'insert on table tb_UFR_Record violates foreign key constraint fk_UrineRecord_Id') WHERE (SELECT c_Id FROM tb_UrineRecord "
			+"WHERE c_Id = NEW.c_UrineId) IS NULL; END;";
		public static final String triUFR_UPDATE="CREATE TRIGGER fku_UFR_UrineId_UrineRecord_Id BEFORE UPDATE ON tb_UFR_Record FOR EACH ROW BEGIN "
			+"SELECT RAISE(ROLLBACK, 'update on table tb_UFR_Record violates foreign key constraint fk_UrineRecord_Id') WHERE (SELECT c_Id FROM tb_UrineRecord "
			+"WHERE c_Id = NEW.c_UrineId) IS NULL; END;";
		public static final String triUFR_DELETE="CREATE TRIGGER fkd_UFR_UrineId_UrineRecord_Id BEFORE DELETE ON tb_UrineRecord FOR EACH ROW BEGIN "
			+"DELETE FROM tb_UFR_Record WHERE c_UrineId = OLD.c_Id; END;";
		
		public static final String tQuestionnaireType = "CREATE TABLE tb_QuestionnaireType(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, c_TypeId INTEGER "
			+"NOT NULL UNIQUE, c_Title TEXT, c_Describe TEXT, c_CreateTime TIMESTAMP NOT NULL DEFAULT '', c_UpdateTime TIMESTAMP DEFAULT'',"
			+"c_IsEnable BOOL NOT NULL DEFAULT 0, c_IsComplete BOOL NOT NULL DEFAULT 0)";//问卷调查模板"tb_QuestionnaireType"
		public static final String tQuestionnaireTopic = "CREATE TABLE tb_QuestionnaireTopic(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, c_Q_TypeId "
			+"INTEGER NOT NULL CONSTRAINT fk_Q_Type_Q_Topic_TypeId REFERENCES tb_QuestionnaireType(c_TypeId) ON DELETE CASCADE, c_TopicId INTEGER NOT NULL,"
			+"c_Title TEXT, c_Describe TEXT, c_GradeType SMALLINT NOT NULL, c_GradeCalcType SMALLINT NOT NULL)";//问卷问题模板"tb_QuestionnaireTopic"
		
		public static final String triQ_TYPE_Q_TOPIC_INSERT="CREATE TRIGGER fki_Q_Type_Q_Topic_TypeId BEFORE INSERT ON tb_QuestionnaireTopic FOR EACH ROW BEGIN "
			+"SELECT RAISE(ROLLBACK, 'insert on table tb_QuestionnaireTopic violates foreign key constraint fk_Q_Type_Q_Topic_TypeId') WHERE (SELECT c_TypeId "
			+"FROM tb_QuestionnaireType WHERE c_TypeId = NEW.c_Q_TypeId) IS NULL; END;";
		public static final String triQ_TYPE_Q_TOPIC_UPDATE="CREATE TRIGGER fku_Q_Type_Q_Topic_TypeId BEFORE UPDATE ON tb_QuestionnaireTopic FOR EACH ROW BEGIN "
			+"SELECT RAISE(ROLLBACK, 'update on table tb_QuestionnaireTopic violates foreign key constraint fk_Q_Type_Q_Topic_TypeId') WHERE (SELECT c_TypeId "
			+"FROM tb_QuestionnaireType WHERE c_TypeId = NEW.c_Q_TypeId) IS NULL; END;";
		public static final String triQ_TYPE_Q_TOPIC_DELETE="CREATE TRIGGER fkd_Q_Type_Q_Topic_TypeId BEFORE DELETE ON tb_QuestionnaireType FOR EACH ROW BEGIN "
			+"DELETE FROM tb_QuestionnaireTopic WHERE c_Q_TypeId = OLD.c_TypeId; END;";
		
		public static final String tQuestionnaireGrade = "CREATE TABLE tb_QuestionnaireGrade(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, c_Q_TypeId "
			+"INTEGER NOT NULL CONSTRAINT fk_Q_Type_Q_Grade_TypeId REFERENCES tb_QuestionnaireType(c_TypeId) ON DELETE CASCADE, c_Q_TopicId INTEGER NOT NULL CONSTRAINT "
			+"fk_Q_Topic_Q_Grade_TopicId REFERENCES tb_QuestionnaireTopic(c_TopicId) ON DELETE CASCADE, c_GradeId INTEGER NOT NULL, c_Describe TEXT, c_Value TEXT)";//闂傤喖宓庣拠鍕瀻濡剝婢�tb_QuestionnaireType"
		public static final String triQ_TYPE_Q_GRADE_INSERT="CREATE TRIGGER fki_Q_Type_Q_Grade_TypeId BEFORE INSERT ON tb_QuestionnaireGrade FOR EACH ROW BEGIN "
			+"SELECT RAISE(ROLLBACK, 'insert on table tb_QuestionnaireGrade violates foreign key constraint fk_Q_Type_Q_Grade_TypeId') WHERE (SELECT c_TypeId "
			+"FROM tb_QuestionnaireType WHERE c_TypeId = NEW.c_Q_TypeId) IS NULL; END;";
		public static final String triQ_TYPE_Q_GRADE_UPDATE="CREATE TRIGGER fku_Q_Type_Q_Grade_TypeId BEFORE UPDATE ON tb_QuestionnaireGrade FOR EACH ROW BEGIN "
			+"SELECT RAISE(ROLLBACK, 'update on table tb_QuestionnaireGrade violates foreign key constraint fk_Q_Type_Q_Grade_TypeId') WHERE (SELECT c_TypeId "
			+"FROM tb_QuestionnaireType WHERE c_TypeId = NEW.c_Q_TypeId) IS NULL; END;";
		public static final String triQ_TYPE_Q_GRADE_DELETE="CREATE TRIGGER fkd_Q_Type_Q_Grade_TypeId BEFORE DELETE ON tb_QuestionnaireType FOR EACH ROW BEGIN "
			+"DELETE FROM tb_QuestionnaireGrade WHERE c_Q_TypeId = OLD.c_TypeId; END;";
		
		public static final String triQ_TOPIC_Q_GRADE_INSERT="CREATE TRIGGER fki_Q_Topic_Q_Grade_TopicId BEFORE INSERT ON tb_QuestionnaireGrade FOR EACH ROW BEGIN "
			+"SELECT RAISE(ROLLBACK, 'insert on table tb_QuestionnaireGrade violates foreign key constraint fk_Q_Topic_Q_Grade_TopicId') WHERE (SELECT c_TopicId "
			+"FROM tb_QuestionnaireTopic WHERE c_TopicId = NEW.c_Q_TopicId) IS NULL; END;";
		public static final String triQ_TOPIC_Q_GRADE_UPDATE="CREATE TRIGGER fku_Q_Topic_Q_Grade_TopicId BEFORE UPDATE ON tb_QuestionnaireGrade FOR EACH ROW BEGIN "
			+"SELECT RAISE(ROLLBACK, 'update on table tb_QuestionnaireGrade violates foreign key constraint fk_Q_Topic_Q_Grade_TopicId') WHERE (SELECT c_TopicId "
			+"FROM tb_QuestionnaireTopic WHERE c_TopicId = NEW.c_Q_TopicId) IS NULL; END;";
		public static final String triQ_TOPIC_Q_GRADE_DELETE="CREATE TRIGGER fkd_Q_Topic_Q_Grade_TopicId BEFORE DELETE ON tb_QuestionnaireTopic FOR EACH ROW BEGIN "
			+"DELETE FROM tb_QuestionnaireGrade WHERE c_Q_TopicId = OLD.c_TopicId; END;";
		
		public static final String tPatientSurvey = "CREATE TABLE tb_PatientSurvey(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, c_S_PatientId TEXT NOT NULL,"
			+"c_Q_TypeId INTEGER NOT NULL CONSTRAINT fk_Q_Type_P_Survey_TypeId REFERENCES tb_QuestionnaireType(c_TypeId) ON DELETE CASCADE, c_BeginTime TIMESTAMP NOT NULL DEFAULT '', "
			+"c_EndTime TIMESTAMP NOT NULL DEFAULT '', c_TotalScore FLOAT, c_IsEdit BOOL DEFAULT 0, c_IsUpload BOOL NOT NULL DEFAULT 0)";//病人问卷"tb_PatientSurvey"
		public static final String tPatientGrade = "CREATE TABLE tb_PatientGrade(c_Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, c_P_SurveyId INTEGER NOT NULL CONSTRAINT fk_P_Survey_P_Grade_SurveyId REFERENCES tb_PatientSurvey(c_Id) ON DELETE CASCADE, c_Q_TopicId INTEGER NOT NULL CONSTRAINT "
			+"fk_Q_Topic_P_Grade_TopicId REFERENCES tb_QuestionnaireTopic(c_TopicId) ON DELETE CASCADE, c_Q_GradeId INTEGER NOT NULL CONSTRAINT fk_Q_Grade_P_Grade_GradeId "
			+"REFERENCES tb_QuestionnaireGrade(c_GradeId) ON DELETE CASCADE, c_Value TEXT)";//病人问卷评分"tb_PatientGrade"	
		
		
	}	
	public static String[] SqliteObj={
		SqliteScript.tSysConfig,
		SqliteScript.tDrinkUrine,
		SqliteScript.tDoctorAdvice,
		SqliteScript.tGotoBedGetUp,
		SqliteScript.tHealthTips,
		SqliteScript.tUrineProportion,		
		SqliteScript.tUrineRateStatistics,
		SqliteScript.tUrineIntervalTime,		
		SqliteScript.tUrineFlowRecord,			
		SqliteScript.tQuestionnaireType,
		SqliteScript.tQuestionnaireTopic,
		SqliteScript.tQuestionnaireGrade,			
		SqliteScript.tPatientSurvey,
		SqliteScript.tPatientGrade,
		SqliteScript.vMicturition,
		
		SqliteScript.triURINE_INTERVAL_TIME_INSERT,
		SqliteScript.triURINE_INTERVAL_TIME_UPDATE,
		SqliteScript.triURINE_INTERVAL_TIME_DELETE,
		
		SqliteScript.triUFR_INSERT,
		SqliteScript.triUFR_UPDATE,
		SqliteScript.triUFR_DELETE,
		
		SqliteScript.triQ_TYPE_Q_TOPIC_INSERT,
		SqliteScript.triQ_TYPE_Q_TOPIC_UPDATE,
		SqliteScript.triQ_TYPE_Q_TOPIC_DELETE,
		
		SqliteScript.triQ_TYPE_Q_GRADE_INSERT,			
		SqliteScript.triQ_TYPE_Q_GRADE_UPDATE,
		SqliteScript.triQ_TYPE_Q_GRADE_DELETE,
		SqliteScript.triQ_TOPIC_Q_GRADE_INSERT,
		SqliteScript.triQ_TOPIC_Q_GRADE_UPDATE,
		SqliteScript.triQ_TOPIC_Q_GRADE_DELETE
		};
}
