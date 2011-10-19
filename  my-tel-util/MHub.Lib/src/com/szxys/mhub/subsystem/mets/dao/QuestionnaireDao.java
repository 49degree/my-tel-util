package com.szxys.mhub.subsystem.mets.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.util.Log;

import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.subsystem.mets.bean.Answersheet;
import com.szxys.mhub.subsystem.mets.bean.Patientgrade;
import com.szxys.mhub.subsystem.mets.bean.Patientsurvey;
import com.szxys.mhub.subsystem.mets.bean.Question;
import com.szxys.mhub.subsystem.mets.bean.Questionnairegrade;
import com.szxys.mhub.subsystem.mets.bean.Questionnairetopic;
import com.szxys.mhub.subsystem.mets.bean.Questionnairetype;
import com.szxys.mhub.subsystem.mets.db.PatientGrade;
import com.szxys.mhub.subsystem.mets.db.PatientSurvey;
import com.szxys.mhub.subsystem.mets.db.QuestionnaireGrade;
import com.szxys.mhub.subsystem.mets.db.QuestionnaireTopic;
import com.szxys.mhub.subsystem.mets.db.QuestionnaireType;

public class QuestionnaireDao {
	
	/**
	 * (手机端Activity)显示问卷列表
	 * @return HashMap<Integer, String>
	 */
	public static ArrayList<Questionnairetype> getQuestionList() {
		ArrayList<Questionnairetype> titles=QuestionnaireType.getTitleList(MhubApplication.getInstance());
		
		return titles;
	}
	/**
	 * (手机端Activity)取题目By TypeId
	 */
	public static ArrayList<Question> getQuestionById(String Q_TypeId) {
		//问卷状态c_IsEdit  c_IsUpload
		Patientsurvey objSurvey=PatientSurvey.getSurveyInfoByTypeId(MhubApplication.getInstance(), Q_TypeId);
		//问卷信息(状态等)
		// c_Id  c_S_PatientId  c_Q_TypeId  c_BeginTime  c_EndTime c_TotalScore  c_IsEdit  c_IsUpload
		//  5         27          221         2011-04-13 15:34:35      9.0          0           1
	    //  6         27          222         2011-04-13 15:42:46      16.0         0           1
		String surveyId="";
		boolean isUploaded=false; //已上传
		boolean isEdited=false; //问卷已填写并保存了
		String patientId="";
		if (objSurvey!=null) {
			surveyId=String.valueOf(objSurvey.getC_Id());
			patientId=objSurvey.getC_S_PatientId();
			isEdited=(objSurvey.getC_IsEdit()==0)?false:true;
			isUploaded=(objSurvey.getC_IsUpload()==0)?false:true;
		}else {
			Log.d("Q Dao", "objSurvey=null");
		}
		if (isEdited) {
			
		}
		int state=0;//0问卷未作答，1已作答已经保存但没有上传，2已经上传了(不能修改)
		ArrayList<Question> questionList=new ArrayList<Question>();
		Question question=null;
		String topicId="";
		ArrayList<Questionnairetopic> topics=QuestionnaireTopic.getTopicById(MhubApplication.getInstance(), Q_TypeId);
		//问卷题型
		// c_Id  c_Q_TypeId  c_TopicId  c_Title  c_Describe c_GradeType  c_GradeCalcType
		//  15       221       722      ajlfjlas   gasfasf      1             1
	    //  16       221       723      nmjhkesat   khu886      1             1
		if (!topics.isEmpty()) {
			for (int i = 0; i < topics.size(); i++) {
				topicId=String.valueOf(topics.get(i).getC_TopicId());
				
				question=new Question();
				if (surveyId.trim().length()>0) {
					question.setSurveyID(Integer.parseInt(surveyId));//如果问卷已作答且保存
				}
				
				question.setC_TypeId(Integer.parseInt(Q_TypeId));
				question.setC_TopicId(Integer.parseInt(topicId));
				question.setC_Title(topics.get(i).getC_Title());
				question.setC_Describe(topics.get(i).getC_Describe());
				
				question.setC_GradeType(topics.get(i).getC_GradeType());
				question.setC_GradeCalcType(topics.get(i).getC_GradeCalcType());
//				if (isEdited) {
					ArrayList<Questionnairegrade> grades=QuestionnaireGrade.getGradesByTopicId(MhubApplication.getInstance(), Q_TypeId, topicId);
					//问题及分值
					// c_Id  c_Q_TypeId  c_TopicId  c_GradeId  c_Describe   c_Value
					//  74       221       722      1091         gasfasf      0
				    //  75       221       722      1092         khu886       1
				    //  76       221       722      1093         gas3asf      2
				    //  77       221       723      1094         khu886       0
				    //  78       221       723      1095         gasfasf      2
				    //  79       221       723      1096         khu886       4
					int selectedGradeID=0;
					if (grades!=null && !grades.isEmpty()) {
						ArrayList<Integer> gradeIdList=new ArrayList<Integer>();
						ArrayList<String> gradeDescribeList=new ArrayList<String>();
						ArrayList<String> gradeValueList=new ArrayList<String>();
						int gradeCount=grades.size();
						Log.d("QuestionDao", "QuestionnaireGrade("+Q_TypeId+","+topicId+") size="+gradeCount);
						//Questionnairegrade objGrade=null;
						for (int j = 0; j < grades.size(); j++) {
							//objGrade=grades.get(j);
							gradeIdList.add(grades.get(j).getC_GradeId());
							gradeDescribeList.add(grades.get(j).getC_Describe());
							gradeValueList.add(grades.get(j).getC_Value());
						}
						question.setGradeIdList(gradeIdList);
						question.setDescribeList(gradeDescribeList);
						question.setValueList(gradeValueList);
						
						selectedGradeID=PatientGrade.getSelectedItemID(MhubApplication.getInstance(), surveyId, topicId);
					}
					
					if (selectedGradeID>0) {
						question.setSelectedGradeId(selectedGradeID);
					}
					
					//答卷结果
					// c_Id  c_P_SurveyId  c_TopicId  c_GradeId  c_Value
					//  5       5        722         1093          2
				    //  6       5        723         1095          1
				    //  7       5        724         1099          2
				    //  8       5        725         1107          4
				    //  9       6        747         1154          3
				    //  10      6        748         1159          2
					if (isUploaded) {
						state=2;
					}
					question.setState(state);
//				} //if (isEdited)
				questionList.add(question);
			}
		}
		
		return questionList;		
	}
	/** 获取未上传的答卷
	 * <Survey PatientId="" TypeId="" BeginTime="" EndTime="" TotalScore="">
        <Topic TopicId="" GradeType="" GradeVal=""/>
        <Topic TopicId="" GradeType="" GradeVal=""/>        
    * </Survey>
	 * @return  ArrayList<Answersheet>
	 */
	public static ArrayList<Answersheet> getUploadAnswers() {
		ArrayList<Answersheet> sheets=new ArrayList<Answersheet>();
		ArrayList<Patientsurvey> surveyList=PatientSurvey.getSurveyList(MhubApplication.getInstance());
		// tb_PatientSurvey  c_Id=5 c_S_PatientId=27 c_Q_TypeId=221 && c_BeginTime c_EndTime c_TotalScore
		// c_Id=5 AS PatientSurveyID  ->> PatientGrade [Q_TopicId  Q_GradeId  value]
		if (!surveyList.isEmpty()) {
			Answersheet answersheet=null;
			for (int i = 0; i < surveyList.size(); i++) {
				answersheet=new Answersheet();
				String typeId=String.valueOf(surveyList.get(i).getC_Q_TypeId());
				answersheet.setPatientID(surveyList.get(i).getC_S_PatientId());
				answersheet.setTypeId(typeId);
				String surveyId=String.valueOf(surveyList.get(i).getC_Id());
				
				ArrayList<Questionnairetopic> topicList=QuestionnaireTopic.getTopicById(MhubApplication.getInstance(), typeId);
				ArrayList<String> topicIDlist=new ArrayList<String>();
				ArrayList<String> gradeTypelist=new ArrayList<String>();
				ArrayList<String> gradeIDlist=new ArrayList<String>();
				ArrayList<String> gradeValuelist=new ArrayList<String>();
				if (!topicList.isEmpty()) {// GradeType
					for (int j = 0; j < topicList.size(); j++) {
						gradeTypelist.add(String.valueOf(topicList.get(j).getC_GradeType()));
					}
				}				
				ArrayList<Patientgrade> gradeDetails=PatientGrade.getGradesBySurveyId(MhubApplication.getInstance(), surveyId);
				//get TopicID by C_Q_TypeId from tb_QuestionnaireTopic
				//eg. Q_TypeId=221 => TopicID={722,723,724,725} topicId=722 ->> gradeId && value [此处不采用]
				if (!gradeDetails.isEmpty()) {
					for (int x = 0; x < gradeDetails.size(); x++) {
						topicIDlist.add(String.valueOf(gradeDetails.get(x).getC_Q_TopicId()));
						gradeIDlist.add(String.valueOf(gradeDetails.get(x).getC_Q_GradeId()));
						gradeValuelist.add(gradeDetails.get(x).getC_Value());
					}
				}				
				answersheet.setTopicID(topicIDlist);
				answersheet.setGradeType(gradeTypelist);
				answersheet.setGradeValue(gradeValuelist);
				answersheet.setBeginTime(surveyList.get(i).getC_BeginTime());
				answersheet.setEndTime(surveyList.get(i).getC_EndTime());
				answersheet.setTotalScore(String.valueOf(surveyList.get(i).getC_TotalScore()));
				
				sheets.add(answersheet);
			}
		}
		return sheets;
	}
	public static long saveGrades(Patientgrade objGrade) {
		//Insert or Update
		long result=-7;
		if (objGrade!=null) {
			String surveyID=String.valueOf(objGrade.getC_P_SurveyId());
			String topicID=String.valueOf(objGrade.getC_Q_TopicId());
			if (PatientGrade.getExistRecord(MhubApplication.getInstance(), surveyID, topicID)) {
				ContentValues values=new ContentValues();
				values.put("c_Q_GradeId", objGrade.getC_Q_GradeId());
				values.put("c_Value", objGrade.getC_Value());
				String where="c_P_SurveyId=? and c_Q_TopicId=?";
				String[] whereArgs={surveyID,topicID};
				result=PatientGrade.Update(MhubApplication.getInstance(), values, where, whereArgs);
			}else {
				result=PatientGrade.Insert(MhubApplication.getInstance(), objGrade);
			}
		}else {
			result=-2;
			Log.d("Q Dao", "objGrade = null");
		}
		
		return PatientGrade.Insert(MhubApplication.getInstance(), objGrade);
	}
	public static boolean lockSurveyOnEdit(String Q_typeId) {
		return PatientSurvey.setSurveyLocked(MhubApplication.getInstance(), Q_typeId);
	}
	public static boolean unlockSurvey(String Q_typeId) {
		return PatientSurvey.unLockSurvey(MhubApplication.getInstance(), Q_typeId);
	}
	public static long saveSurveyInfo(Patientsurvey objSurvey) {
		return PatientSurvey.Insert(MhubApplication.getInstance(), objSurvey);
	}
}
