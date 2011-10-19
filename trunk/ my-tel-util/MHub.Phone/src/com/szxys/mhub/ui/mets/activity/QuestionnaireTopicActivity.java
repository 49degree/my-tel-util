package com.szxys.mhub.ui.mets.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.bean.Patientgrade;
import com.szxys.mhub.subsystem.mets.bean.Patientsurvey;
import com.szxys.mhub.subsystem.mets.bean.Question;
import com.szxys.mhub.subsystem.mets.dao.QuestionnaireDao;
import com.szxys.mhub.subsystem.mets.dao.SysConfigDao;
import com.szxys.mhub.subsystem.mets.db.PatientGrade;
import com.szxys.mhub.subsystem.mets.db.PatientSurvey;
import com.szxys.mhub.ui.mets.components.PromptMessageActivity;

/**
 * 排尿记录
 * @author Administrator
 *
 */
public class QuestionnaireTopicActivity  extends Activity implements OnClickListener{
	
	
	
		
		//------------------update--------------------------------------
		private ArrayList<Question> questionare = new ArrayList<Question>(); //一个问卷调查，里面有一系列的题目
		private Question question = new Question(); //一个题目
//		private HashMap<String,Object> record = new HashMap<String, Object>(); //存储一个题目的记录值， 题目Id,选项Id，选项分值
		private ArrayList record = new ArrayList(); //存储一个题目的记录值， 题目Id,选项Id，选项分值
		private ArrayList<ArrayList> recordList = new ArrayList<ArrayList> (); //存储一个题目的记录值， 题目Id,选项Id，选项分值
		public static int FLAG_EDIT = -1; //0  未编辑    1  编辑保存未提交 -- 还可以编辑，进入页面后能显示上次的记录  2 已提交 不能修改
		public static int UN_EDIT = 0; //0  未编辑    1  编辑保存未提交 -- 还可以编辑，进入页面后能显示上次的记录  2 已提交 不能修改
		public static int HAS_EDIT = 1; //0  未编辑    1  编辑保存未提交 -- 还可以编辑，进入页面后能显示上次的记录  2 已提交 不能修改
		public static int HAS_UPLOAD = 2; //0  未编辑    1  编辑保存未提交 -- 还可以编辑，进入页面后能显示上次的记录  2 已提交 不能修改
		
		/**
		 * Added by wxg
		 */
		//问卷状态字段（是否已上传 是否已编辑保存）
		private boolean isEdit = false;  //是否保存问卷调查的结果
		private boolean isUpload=false;  //是否已经上传了
		// The end of 问卷状态字段
		
		int questionareInfo_id=0; //TypeID
		String beginTime="";//答卷开始时间
		String endTime="";//答卷保存时间
		
		
		//------------------update--------------------------------------
		
		HashMap<String,Object> questionareInfo = null; //问卷调查
		ArrayList<HashMap> topicList = new ArrayList<HashMap>(); //问卷调查中存放的题目列表
		HashMap<String,String> topicItem = new HashMap<String, String>(); //存放问卷调查中具体的一个题目
		
		private TextView questionnaireType;
		private TextView questionnaireTopic;
		private TextView questionnaireContent;
		private TextView questionnairePageInfo;
		private Button pageNextButton;
		private Button pageLastButton;
		private static final int finishId = 1;

		private ListView optionsListView = null;
		private QuestionnaireTopicAdapter topicAdapter;
		
		private  String questionnaire_type;
		private  String questionnaire_topic;
		private  String questionnaire_content;
		
		private static final int SAVE_CODE = 1; //点击保存问卷调查时的请求参数请求参数
		
//		private int requestCode = -1;// 
		
		/**
		 * //保存页面的选项：
		 * 保存选项： 保存第i题得选项，choice.add() //按循序保存  上一页后更改选项，则不好更改数据，故用HashMap
		 * 取出： choice.get(i)
		 */
//		private ArrayList<Integer> choice = new ArrayList<Integer>(); 
		
		private HashMap<Integer,Integer> choice = new HashMap<Integer, Integer>();
		

		
		private static int page_flag = 1; //第几个题目 -- 页码标签    刚进入的时候页码显示第一页
		private static int page_total = 0; //题目的数量-- 页码的总数
		public  static int rb_checked_id = -1; //当前RadioButton被选中的Id
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.mets_questionnaire_topic);
			
			//里面制作数据的处理，不要将视图的显示放置在里面
			prepareData(); 
			questionnaireType = (TextView) this.findViewById(R.id.mets_questionnaire_subtitle);
			questionnaireTopic = (TextView) this.findViewById(R.id.mets_questionnaire_topic);
			questionnaireContent = (TextView) this.findViewById(R.id.mets_questionnaire_content);
			questionnairePageInfo = (TextView) this.findViewById(R.id.mets_questionnaire_page_info);
			
			pageNextButton = (Button) this.findViewById(R.id.mets_questionnaire_next_btn);
			pageLastButton = (Button) this.findViewById(R.id.mets_questionnaire_last_btn);
			pageLastButton.setVisibility(View.GONE); 
			
			//根据题目数判断出现哪些Button
			changeButton();
			
			pageNextButton.setOnClickListener(this);
			pageLastButton.setOnClickListener(this);
			
		
			
		
			//放置在prepareData()之后，不然没数据
			questionnaireType.setText(questionnaire_type);
			questionnaireTopic.setText(questionnaire_topic);
			questionnaireContent.setText(questionnaire_content);
			questionnairePageInfo.setText("" +page_flag);
			
//			questionnaireType.setText(questionnaire_type);
//			questionnaireTopic.setText(questionnaire_topic);
//			questionnaireContent.setText(questionnaire_content);
//			questionnairePageInfo.setText("" +page_flag);
			
			
			
			optionsListView = (ListView) this.findViewById(R.id.mets_options_lv);
			
//			topicAdapter = new QuestionnaireTopicAdapter(this,topicItem);
			topicAdapter = new QuestionnaireTopicAdapter(this,question); 
			optionsListView.setAdapter(topicAdapter);
			
			
		}

		//数据的准备
		private void prepareData() {
			
			page_flag = 1; // 进来后就进入第一页
			
			//问卷调查的Id  通过问卷调查点击编辑的时候传递过来
			questionareInfo_id = this.getIntent().getIntExtra("id", 0); 
			
			//获取问卷调查 其中含有所有题目
			questionare = QuestionnaireDao.getQuestionById(String.valueOf(questionareInfo_id));
			Log.d("QuestionaireTopic", "Activity showed TopicId="+questionareInfo_id);
//锁定问卷	QuestionnaireDao.lockSurveyOnEdit(String.valueOf(questionareInfo_id)); //编辑时锁定问卷
			beginTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			//获取问卷调查的类型
			questionnaire_type = "Q Test";//questionareInfo.get("type").toString();
			
			//String patientID=SysConfigDao.getPatientID();
			page_total = questionare.size();//题目数量 
			
			Log.d("QuestionaireTopic", "Questions num="+page_total);
//			topicItem = topicList.get(page_flag -1);//得到页码对应的题目所有内容
//			questionnaire_topic = topicItem.get("topic").toString(); //得到题标题
//			questionnaire_content = topicItem.get("content").toString(); //得到题的内容
			
			question = questionare.get(page_flag -1);//得到页码对应的题目
//			if (question!=null) {
//				if (!question.getDescribeList().isEmpty()) {
//					int testCount=question.getDescribeList().size();
//					Log.d("Q Topic Activity", "objQuestion DescribeList count="+testCount);
//				}
//			}
			questionnaire_topic = question.getC_Title(); //得到题标题
			questionnaire_content = question.getC_Describe(); //得到题的内容
			
			
			//初始化choice
			if (!isEdit) {
				for(int i= 1; i<= page_total;i++) {
					choice.put(i, -1); //所有的页面的题目都是没有选中的
				}
			}
			
		}


		//上一页或者下一页切换题目时的数据改变
		private void changeData() {
			
			question = questionare.get(page_flag -1);//得到页码对应的题目
			questionnaire_topic = question.getC_Title(); //得到题标题
			questionnaire_content = question.getC_Describe(); //得到题的内容
			
			//更换题目切换后的显示内容
			questionnaireType.setText(questionnaire_type);
			questionnaireTopic.setText(questionnaire_topic);
			questionnaireContent.setText(questionnaire_content);
			questionnairePageInfo.setText("" +page_flag);
			
			//适配器中的数据更换
			topicAdapter = new QuestionnaireTopicAdapter(this,question);
			optionsListView.setAdapter(topicAdapter);
			
			//更换和显示Button
			changeButton();
			
		}

		private void changeButton() {
			if(1 == page_total) { //总共只有一页，那就不显示上一页，和下一页，直接显示 完成按钮
				pageLastButton.setVisibility(View.GONE);
				pageNextButton.setId(finishId);
				pageNextButton.setText(getResources().getString(R.string.mets_questionnaire_finish_btn_text));
			} else {
				//是否更换button
				if(page_flag == page_total) {//当做完题目时： 保存数据，并且结束当前页面
					pageLastButton.setVisibility(View.VISIBLE);
					pageNextButton.setId(finishId);
					pageNextButton.setText(getResources().getString(R.string.mets_questionnaire_finish_btn_text));
					pageNextButton.setOnClickListener(this);
					
				}else if(page_flag == 1) {  //第一页，不显示上一页的Button， 显示下一页的Button
					pageLastButton.setVisibility(View.GONE);
					pageNextButton.setId(R.id.mets_questionnaire_next_btn);
					pageNextButton.setText(getResources().getString(R.string.mets_questionnaire_next_btn_text));
				}else {
					pageNextButton.setId(R.id.mets_questionnaire_next_btn);
					pageNextButton.setText(getResources().getString(R.string.mets_questionnaire_next_btn_text));
					pageLastButton.setVisibility(View.VISIBLE);
				}
			}
		}


		@Override
		public void onClick(View v) {
			
			switch(v.getId()) {
			case R.id.mets_questionnaire_last_btn :
				
				saveChoice(); //保存当前页面的选择
//			    rb_checked_id = -1; //清空，否则下一题不选择，就会去上一页的选项答案，如果以后设置了数据库的保存，就不必要清空了，不用此flag了
				
			    //页面数 -1
			    page_flag = (page_flag -1 > 0 ? page_flag -1 : page_flag); 
			    readChoice(); //读取上一页是否保存过数据
			    changeData(); //更新上一页的显示内容
			  
				break;
			case R.id.mets_questionnaire_next_btn :
				
				System.out.println("mets_questionnaire_next_btn --page_flag =" + page_flag);
				if(-1 == rb_checked_id) { //当前页面没有选题，不让离开
					Toast.makeText(this, "您还没有选择答案", Toast.LENGTH_SHORT).show();
					break;
				}
				
				saveChoice(); //保存当前页面的选择
//				rb_checked_id = -1;
				page_flag = (page_flag + 1 > page_total ? page_flag : page_flag+1); //页数增加
				readChoice(); //读取下一页是否保存过数据
				changeData(); //更新下一页的页面内容
				break;
			case finishId:
				if(-1 == rb_checked_id) { //当前页面没有选题，不让离开
					Toast.makeText(this, "您还没有选择答案", Toast.LENGTH_SHORT).show();
					break;
				}
				saveChoice(); //保存当前页面的选择
				//清空放在保存按钮后，否则，如果用户取消保存回到这个界面的时候，最后一题的选中状态就没有了，因为被点击完成时给清空了
//				QuestionnaireTopicAdapter.rb_temp_id = -1;//适配器的的也要清空
//				rb_checked_id = -1; 
				
				//提示是否保存---------
				Intent intent = new Intent(QuestionnaireTopicActivity.this, PromptMessageActivity.class);
				intent.putExtra(PromptMessageActivity.PROMPT_MESSAGE_TYPE, PromptMessageActivity.PROMPT_MESSAGE_QUESTIONNAIRE_SAVE);
				startActivityForResult(intent, SAVE_CODE);
				
				//-- 保存本次问卷调查的数据-----
				
				//-- 保存本次问卷调查的数据-----
				
				break;
			default: break;
			}
			
		}

		private void readChoice() {
			
			//如果当此页面以前保存过选项，则读取出来，并且让RadioButton显示出以前保存的选项   
			if(choice.get(page_flag)!= null || choice.get(page_flag) != -1) {
				//适配器适配数据时，显示我选择过的数据
				QuestionnaireTopicAdapter.rb_temp_id = choice.get(page_flag);
				rb_checked_id = choice.get(page_flag);
				System.out.println("choice.get(page_flag)" +choice.get(page_flag));
			}else {
				rb_checked_id = -1; //页面没有任何题目被选中
			}
			
		}

		private void saveChoice() {
			choice.put(page_flag, rb_checked_id); 
			
			//这里的保存肯定还有问题，要考虑用户来回切换题目会不会多次保存，重复保存等，以及下一次用户再次进入的时候怎么保存
			//record.add(question.getC_TopicId()); //// 当前页面的题目Id，
			//record.add(rb_checked_id); //选项Id
			//record.add(question.get)); //分值
			//recordList.add(record);
			question.setSelectedGradeId(rb_checked_id);
			Toast.makeText(this, "第" + page_flag+ "题！！！我选择了第" + rb_checked_id + "个答案", Toast.LENGTH_SHORT).show();
			
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			
			if(SAVE_CODE == requestCode) {
				if(RESULT_OK == resultCode) {
					
					QuestionnaireTopicAdapter.rb_temp_id = -1;//适配器的的也要清空
					rb_checked_id = -1;
					
					//-------------保存问卷调查 题目 的答案-----------
					saveData();
					//-------------保存问卷调查 题目 的答案-----------
					finish(); //退出问卷调查做题 的界面，回到问卷调查主界面
				}
			}
		}

		private void saveData() {
//			int surveyId=0;
			String patientId=SysConfigDao.getPatientID();
			endTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			long surveyRowId=0;
			int totalScore=0;
			if (!questionare.isEmpty()) {
				//PatientSurvey加入一条记录；再保存 答卷
				ArrayList<Patientgrade> savedgradeList=new ArrayList<Patientgrade>();
				Patientgrade grade=null;
//				boolean flag=true;
				for (int i = 0; i < questionare.size(); i++) {
					grade=new Patientgrade();
//					grade.setC_P_SurveyId(questionare.get(i).getSurveyID());
//					if (flag) {
//						surveyId=questionare.get(i).getSurveyID();
//						flag=false;
//					}
					grade.setC_Q_TopicId(questionare.get(i).getC_TopicId());
					grade.setC_Q_GradeId(questionare.get(i).getSelectedGradeId());
					int index=questionare.get(i).getGradeIdList().indexOf(questionare.get(i).getSelectedGradeId());
					String selectedValue=questionare.get(i).getValueList().get(index);
					grade.setC_Value(selectedValue);
					totalScore+=Integer.parseInt(selectedValue);
					savedgradeList.add(grade);
				}
				
				if (questionareInfo_id>0 && patientId.trim().length()>0) {
					//QuestionnaireDao.unlockSurvey(String.valueOf(questionareInfo_id));
					Patientsurvey objSurvey=new Patientsurvey();
					objSurvey.setC_S_PatientId(patientId);
					objSurvey.setC_Q_TypeId(questionareInfo_id);
					objSurvey.setC_BeginTime(beginTime);
					objSurvey.setC_EndTime(endTime);
					objSurvey.setC_TotalScore(totalScore);//总分值(CalType ?)
					objSurvey.setC_IsEdit(0);
					objSurvey.setC_IsUpload(0);
					surveyRowId=QuestionnaireDao.saveSurveyInfo(objSurvey);
				}
				if (surveyRowId>0) {
					long resultOfInsertGrade=0;
					int surveyID=(int)surveyRowId;
					if (!savedgradeList.isEmpty()) {
						Patientgrade objGrade=null;
						for (int j = 0; j < savedgradeList.size(); j++) {
							objGrade=savedgradeList.get(j);
							objGrade.setC_P_SurveyId(surveyID);
							resultOfInsertGrade=QuestionnaireDao.saveGrades(objGrade);
							if (resultOfInsertGrade>0) {
								Log.d("Save answersheet", "SurveyId="+surveyRowId+" TopicId"+savedgradeList.get(j).getC_Q_TopicId()+" selected"+savedgradeList.get(j).getC_Q_GradeId()+"#"+savedgradeList.get(j).getC_Value());
							}
						}
					
					}
					
					
				}
			}
			Log.d("Save answersheet", "From "+beginTime+"~"+endTime);
		}
		
		
	}