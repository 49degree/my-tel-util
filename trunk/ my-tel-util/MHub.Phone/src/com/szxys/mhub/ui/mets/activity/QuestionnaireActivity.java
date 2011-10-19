package com.szxys.mhub.ui.mets.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.bean.Drinkandurine;
import com.szxys.mhub.subsystem.mets.bean.Questionnairetopic;
import com.szxys.mhub.subsystem.mets.bean.Questionnairetype;
import com.szxys.mhub.subsystem.mets.dao.QuestionnaireDao;
import com.szxys.mhub.subsystem.mets.db.DrinkAndUrine;
import com.szxys.mhub.subsystem.mets.db.GotobedGetup;
import com.szxys.mhub.subsystem.mets.db.QuestionnaireTopic;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;
import com.szxys.mhub.ui.mets.components.DataGridView;
import com.szxys.mhub.ui.mets.components.DataGridViewAdapter;

/**
 * 问卷调查
 * @author Administrator
 *
 */
public class QuestionnaireActivity  extends Activity implements OnClickListener{

	int choiceId = -1;//被选中记录ID
	int choiceIndex = -1;//被选中的行号
	int urineRecordListX = 0;//表格滚动条的 横轴的位置
	int urineRecordListY = 0;//表格滚动条的 纵轴的位置
	private final Handler mHandler = new Handler(); 
	
	
	private Button getNewBtn = null;//修改按钮
	private Button returnBtn = null;//返回按钮
	
	private TextView urineCountLink = null;//排尿统计连接	
	
	ArrayList<Questionnairetopic> qustionTopicList = null;  //存放问卷调查的
	ArrayList<Questionnairetype> questionnaire = null;  //存放问卷调查的
	HashMap<String,Object> questionareInfo = null;
	HashMap<Integer,String> number_id = new HashMap<Integer, String>();  //存放编号和问卷调查Id的对应关系
	
	private int flag = 0; // 0:未完成  1， 完成未上传， 2，完成已上传    分别用三张图片
	private  DataGridView  questionnaireList = null;//表格控件
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mets_questionnaire_main);
		
		questionnaireList = (DataGridView)findViewById(R.id.mets_questionnaire_list_view);
		getNewBtn = (Button)this.findViewById(R.id.mets_questionnaire_getNew_btn);
		returnBtn = (Button)this.findViewById(R.id.mets_questionnaire_return_btn);
		
		
		getNewBtn.setOnClickListener(this);
		returnBtn.setOnClickListener(this);
		
//		Cursor cursor = QuestionnaireTopic.Select(this, new String[]{"c_Id","c_Title"}, null, null, null, null, null);
		
		//拿到所有的问卷调查题目及其信息
//		qustionTopicList = QuestionnaireData.getQuestionareTopic();
		
		//questionnaire = QuestionnaireData.getQuestionareInfo();
		initGrid();//绘制表格
	}
	
	
	@Override
	public void onRestart() {
		super.onRestart();
		initGrid();//绘制表格
	}
		
	
	/**
	 * 根据数据构造表格及数据
	 */
	public void initGrid(){
		questionnaire=QuestionnaireDao.getQuestionList();
		ArrayList<ArrayList<View>> mDataViews = getMdata(questionnaire);//根据排尿日记数据获取表格VIEW
        
		DataGridViewAdapter simpleAdapter = new DataGridViewAdapter();//表格控件数据适配器
        //为一个二维数组 第二维长度为2,分别表示列的名称和列的宽度
        String[][] mColumnHeaders = new String[][]{
        		{"问卷数","80"},
        		{"问卷类型","80"},
        		{"提交状态","80"}
        };
        simpleAdapter.setMColumnHeaders(mColumnHeaders);//配置表头 
        simpleAdapter.setMData(mDataViews); //配置表的数据
        questionnaireList.setMPageDataAdapter(simpleAdapter);//设置表格数据
        questionnaireList.buildDatagrid();//构建表格
       
	}

	private ArrayList<ArrayList<View>> getMdata(ArrayList<Questionnairetype> questionlist){
		
		ArrayList<ArrayList<View>> mData = new ArrayList<ArrayList<View>>();
		int datasLength = questionlist.size(); 

		for(int i=0;i<datasLength;i++){ 
			
//			questionareInfo = new HashMap<String, Object>();
//			questionareInfo = datas.get(i); //取出一个问卷调查
			//String id = questionareInfo.get("id").toString();
			
			//number_id.put(i, id); //存放对应关系
			
			ArrayList<View> itemView = new ArrayList<View>();
			
			//增加记录详细信息
			TextView numberView = new TextView(this);//问卷个数
			TextView questionareTypeView = new TextView(this);//问卷类型
			ImageView statusView = new ImageView(this);//是否填写
//			statusView.setId(i);
			//statusView.setId((Integer)(questionareInfo.get("id"))); //问卷调查的Id
			statusView.setId(questionlist.get(i).getC_TypeId());
			numberView.setText("" + (i+1));
			questionareTypeView.setText(questionlist.get(i).getC_Title());
			flag=questionlist.get(i).getC_IsComplete();
			if(1 == flag) {
				statusView.setImageResource(R.drawable.mets_before_write);
			} else if (0 == flag){
				statusView.setImageResource(R.drawable.mets_after_write);
			}
			
			statusView.setOnClickListener(new OnClickListener(){//
				public void onClick(View v){
//					System.out.println("v.getId()" + v.getId());
					if(0 <= flag) {
						Intent intent = new Intent(QuestionnaireActivity.this, QuestionnaireTopicActivity.class);
						intent.putExtra("id", v.getId());
						startActivity(intent);
					} else if (1 == flag){
						Toast.makeText(QuestionnaireActivity.this, "已经做完", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			itemView.add(numberView);
			itemView.add(questionareTypeView);
			itemView.add(statusView);

			mData.add(itemView);
		}
		return mData;
	}
	
	@Override
	public void onClick(View v){
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.mets_questionnaire_getNew_btn://获取新的问卷调查
			Toast.makeText(this, "没有新的问卷调查", Toast.LENGTH_SHORT).show();
			break;
		case R.id.mets_questionnaire_return_btn://删除按钮事件
			finish();
			break;
		default :
			break;
		}
	}
	
}