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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.bean.Drinkandurine;
import com.szxys.mhub.subsystem.mets.bean.Question;
import com.szxys.mhub.subsystem.mets.bean.Questionnairetopic;
import com.szxys.mhub.subsystem.mets.db.DrinkAndUrine;
import com.szxys.mhub.subsystem.mets.db.GotobedGetup;
import com.szxys.mhub.subsystem.mets.db.QuestionnaireTopic;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;
import com.szxys.mhub.ui.mets.components.DataGridView;
import com.szxys.mhub.ui.mets.components.DataGridViewAdapter;

/**
 * 显示问卷调查的ListView的适配器
 * @author Administrator
 *
 */
public class QuestionnaireTopicAdapter extends BaseAdapter{
	
//	private  ArrayList<HashMap> questionnaire = new ArrayList<HashMap>(); //问卷调查
//	private  ArrayList<HashMap> topicList = new ArrayList<HashMap>(); //存放题目
	
	HashMap<Integer,String> topicItem = new HashMap<Integer, String>();
	public  static  int rb_temp_id = -1; //当前RadioButton被选中的Id
	private LayoutInflater inflater;
	private Context context;

	//-------------------------------update------------------------------------
	private ArrayList<Integer> gradeIdList; //选项ID
	private ArrayList<String> describeList; //选项内容
	private ArrayList<String> valueList; //选项分值
	//-------------------------------update------------------------------------
	
	public  void prepareData(Question question) {
		gradeIdList = question.getGradeIdList();
		describeList = question.getDescribeList(); //获得选项内容的列表
		valueList = question.getValueList();
	}

	
	//Bean对象，用来封装listview中的一行数据
    public class ViewHolder{
    	public RadioButton radioButton;
    	public TextView option;
    	
    	
    }
	
	public QuestionnaireTopicAdapter(Context context, Question question) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		prepareData(question);
	}
	@Override
	public int getCount() {
		//由于没有用对象，而 每个topicItem中有5个东西，而其中只有三个是我放置的题目
		return this.describeList.size(); 
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return describeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	public View getView(int arg0, View arg1, ViewGroup arg2) {
			arg1 = inflater.inflate(R.layout.mets_questionnaire_listview_item, null);
			
			RadioButton radioButton = (RadioButton) arg1.findViewById(R.id.mets_questionnaire_rbtn);
			TextView option = (TextView) arg1.findViewById(R.id.mets_questionnaire_option_tv);
			
//			radioButton.setId(arg0 + 1); //从 1 开始
			radioButton.setId(gradeIdList.get(arg0)); //每个选项的Id值都不同
			
			if(rb_temp_id == radioButton.getId()) { //如果上次被选中，这此次构造视图的时候依然标志为选中状态
				radioButton.setChecked(true);
			}
			
			option.setText(describeList.get(arg0).toString()); //设置选项内容
			
			radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        //这段代码来实现单选功能 
                        if(rb_temp_id != -1){
                        	  RadioButton tempButton = (RadioButton) ((Activity) context).findViewById(rb_temp_id);//拿到上次选中的RadioButton
                            if(tempButton != null){
                               tempButton.setChecked(false); //上次选中的设置为未选中
                            }
                        }
                        
                        rb_temp_id = buttonView.getId();  //保存当前选中的RadioButton的Id   
                        
                        //把选中的radio即时传递到 QuestionnaireTopic页面，QuestionnaireTopic知道哪个选项被选中了
                        QuestionnaireTopicActivity.rb_checked_id = rb_temp_id;
                        
                        System.out.println("temp" + rb_temp_id);
                        Log.e("kkkkk","you are women- -   " + isChecked + "   " + rb_temp_id);
                        
                    }
                }
			});
			
		return arg1;
	}   	
	
	
	
	
}