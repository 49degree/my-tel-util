package com.szxys.mhub.ui.mets.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.szxys.mhub.subsystem.mets.bean.Questionnairetopic;

/**
 * 
 * @author Administrator
 *
 */
public class QuestionnaireData  {
	
	public static ArrayList<Questionnairetopic> getQuestionareTopic() {
		
		ArrayList<Questionnairetopic>  questionareList = new ArrayList<Questionnairetopic>();
			for(int i=0; i<1; i++) {
				
				Questionnairetopic questionnairetopic = new Questionnairetopic();
				questionnairetopic.setC_Id(i);
				questionnairetopic.setC_Q_TypeId(0);
				questionnairetopic.setC_Title("第" + i + "题：");
				questionnairetopic.setC_Describe("这里是描述信息" + i);
				questionnairetopic.setC_GradeCalcType(0);
				questionnairetopic.setC_TopicId(0);
				questionareList.add(questionnairetopic);
			}
			
			return questionareList;
		}
	
   public static ArrayList<HashMap> getQuestionareInfo() {
	   
	   ArrayList<HashMap> questionnaire= new ArrayList<HashMap>();
	   
	   HashMap<String,Object> questionnaireInfo = new HashMap<String, Object>();//
	   ArrayList<HashMap> topicList = new ArrayList<HashMap>(); //存放题目
	   
	   HashMap<String,String> topicitem = new HashMap<String, String>();
	   HashMap<String,String> topicitem2 = new HashMap<String, String>();
	   
	   
	   topicitem.put("topic", "topic:排尿次数");
	   topicitem.put("content", "content: 从早上起床到晚上入睡总共排尿多少次");
	   topicitem.put("A", "<=7");
	   topicitem.put("B", "8-15");
	   topicitem.put("C", ">=16");
	   
	   topicitem2.put("topic", "topic:饮水次数");
	   topicitem2.put("content", "content: 从早上起床到晚上入睡总共饮水多少次");
	   topicitem2.put("A", "<=5");
	   topicitem2.put("B", "5-13");
	   topicitem2.put("C", ">=14");
	   topicList.add(topicitem);
	   topicList.add(topicitem2);
	   
	   questionnaireInfo.put("id",1); //问卷调查的Id
	   questionnaireInfo.put("type","IPSS"); //题目类型
	   questionnaireInfo.put("topic", topicList); //题目具体内容
	   
	   
	   HashMap<String,Object> questionnaireInfo2 = new HashMap<String, Object>();
	   ArrayList<HashMap> topicList2 = new ArrayList<HashMap>(); //存放题目
	   HashMap<String,String> topicitem3 = new HashMap<String, String>();
	   HashMap<String,String> topicitem4 = new HashMap<String, String>();
	   
	   
	   topicitem3.put("topic", "topic:问卷2 题目一");
	   topicitem3.put("content", "content: 你喜欢那个动物");
	   topicitem3.put("A", "猫");
	   topicitem3.put("B", "狗");
	   topicitem3.put("C", "熊");
	   
	   topicitem4.put("topic", "topic:问卷2 题目二");
	   topicitem4.put("content", "content: 你在干什么");
	   topicitem4.put("A", "发呆");
	   topicitem4.put("B", "看电视");
	   topicitem4.put("C", "吃东西");
	   
	   topicList2.add(topicitem3);
	   topicList2.add(topicitem4);
	   
	   questionnaireInfo2.put("id",2);
	   questionnaireInfo2.put("type","BIOS");
	   questionnaireInfo2.put("topic", topicList2);
	   
	   
	  
	  
	   
	   questionnaire.add(questionnaireInfo);
	   questionnaire.add(questionnaireInfo2);
	   
	   return questionnaire;
   }  
	
}
