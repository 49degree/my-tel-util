package com.szxys.mhub.ui.virtualui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.szxys.mhub.R;
import com.szxys.mhub.bizmanager.BusinessManager;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.subsystem.virtual.Ctrl_Com_Code;
import com.szxys.mhub.subsystem.virtual.MhubMessage;
import com.szxys.mhub.ui.base.MHubActivity;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.ViewFlipper;


public class MessageInerationActivity extends MHubActivity implements OnGestureListener {

	private Button btnMyComplain;
	private Button btnDeleteMsg;
	private Handler mhander = new UIhandler();
	private SimpleAdapter adapter;
	private ListView listView;
	private List<HashMap<String, Object>> data = new  ArrayList<HashMap<String, Object>>();
	private ArrayList<MhubMessage> arrayMhubMessage;
	private Object objResult[] = new Object[1];
	private ViewFlipper flipper;
	private GestureDetector detector;
	private int currentPage; //当前页 
	private int totalPage;   //总页数
	private static final int numPerPage = 2;// 每屏显示互动消息的条数
	private Button btnNextPage;
	private Button btnPreviousPage;
	
	private class UIhandler extends Handler
	{
		
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {   
	         switch (msg.what)
	         {
	         case 1:
	        	 arrayMhubMessage = (ArrayList<MhubMessage>)objResult[0];
	        	 totalPage = arrayMhubMessage.size()/numPerPage;
	        	 if(arrayMhubMessage.size()%numPerPage!=0)
	        	 {
	        		 totalPage = totalPage +1;
	        	 }
	        	 currentPage = 1;
	        	 refreshData();
	         	break;
	         case 2:        	                                                                                                                                                                                                                       
	         	break;
	         }
	     }
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pfphone_interation);
		setDisableMenu(1);
		setMenuAlwaysShow(false);
		init();	
		flipper = (ViewFlipper)findViewById(R.id.ViewFlipper01);
		detector = new GestureDetector(this);
		sendrequest();
	}
	protected void init()
	{
		initButton();
		listView =(ListView)findViewById(android.R.id.list);
		adapter = new SimpleAdapter(
				MessageInerationActivity.this, data, R.layout.pfphone_interationitem,
				new String[] {"img","doctorName","time","context"}, 
				new int[] {R.id.imgView, R.id.txt_doctorName,R.id.txt_Time, R.id.txt_context});
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent it = new Intent(MessageInerationActivity.this,MessageDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("Content", arrayMhubMessage.get(arg2).getContent());
				it.putExtras(bundle);
				startActivity(it);
			}
		}
		);
		
	}
	
	protected void initButton()
	{
		btnMyComplain =(Button)findViewById(R.id.complain);
		btnDeleteMsg =(Button)findViewById(R.id.delbtn);
		btnNextPage = (Button)findViewById(R.id.rightbutton);
		btnPreviousPage =(Button)findViewById(R.id.leftbutton);
		btnMyComplain.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(MessageInerationActivity.this,ComplainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("PKID", 0);
				bundle.putString("PKName", "病人");
				bundle.putString("DocID", "3");
				bundle.putString("DocName", "张医生");
				it.putExtras(bundle);
				startActivity(it);
			}
		});
		btnDeleteMsg.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putInt("StartIndex", (currentPage-1)*numPerPage);
				bundle.putInt("Size", data.size());
				Intent intent=new Intent(MessageInerationActivity.this,DelMessageActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		btnNextPage.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			nextPage();
			}
		});
		btnPreviousPage.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			previousPage();
			}
		});
	
	}
	//比对当前时间和数据库中的时间 
	//如果是当天    显示时分
	//如果差一天 显示昨天 加时分
    //如果差两天  显示前天加时分
	//大于2天的的显示 日期
	private String composeSpecialTime(String dateTime)
	{
		String strResult ="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = sdf.format(date);
		String nowTime[] =time.split("-| |:");
		String paramTime[] = dateTime.split("-| |:");
		int temp1 = Integer.valueOf(nowTime[2]);
		int temp2 = Integer.valueOf(paramTime[2]);
		switch (temp1 - temp2) {
		case 0:
			strResult += paramTime[3]+":"+paramTime[4];
			break;
		case 1:
			strResult +="昨天"+" "+paramTime[3]+":"+paramTime[4];
			break;
		case 2:
			strResult +="前天"+" "+paramTime[3]+":"+paramTime[4];
			break;
		default :
			strResult +=paramTime[1]+"-"+paramTime[2];
			break;
		}
		return strResult;
	}

	

	private void refreshData()
	{
		data.clear();
		if(arrayMhubMessage !=null)
		{
			for (int i = (currentPage-1)*numPerPage; i <currentPage*numPerPage&&i<arrayMhubMessage.size(); i++) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("img", R.drawable.pfphone_msgicon);
				item.put("doctorName", arrayMhubMessage.get(i).getDoctorName());
				item.put("time", composeSpecialTime(arrayMhubMessage.get(i).getTime()));
				if(Integer.valueOf(arrayMhubMessage.get(i).getSourceMsgId()) == 0)
				{
					item.put("context"," 【主动诉说】"+ arrayMhubMessage.get(i).getContent());
				}
				else {
					item.put("context"," 【医生建议】"+ arrayMhubMessage.get(i).getContent());
				}
				item.put("imgBtn",R.drawable.pfphone_delete_pressed);
				data.add(item);
			}
			SimpleAdapter sAdapter = (SimpleAdapter)listView.getAdapter();
	        sAdapter.notifyDataSetChanged();
		}

		
	}
	
	//向逻辑业务层发送 请求
	
	private void sendrequest()
	{
		new Thread()
		{
			public void run()
			{
				Object param[] = new Object[2];
				param[0] = 10;
				param[1] = 10;
				BusinessManager.getIBusinessManager().control(0, Platform.SUBBIZ_VIRTUAL, Ctrl_Com_Code.GET_MSG_FROMDB, param, objResult);
				Message msg = new Message();
				msg.what = 1;
				mhander.sendMessage(msg);
			}
		}.start();
	}
	
//	@Override
//	protected void onStart() {
//		sendrequest();
//		super.onStart();
//	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 120) {	
			nextPage();	
			return true;
		} else if (e1.getX() - e2.getX() < -120) {			
			previousPage();
			return true;
		}
		return false;

	}
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void nextPage()
	{
		currentPage ++;	
		if(currentPage >totalPage)
		{
			currentPage = totalPage;
			return;
		}
		this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
		this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
		refreshData();
		this.flipper.showNext();
		convertButtonVisible();
	
	
	}
	
	
	private void convertButtonVisible()
	{
		if(currentPage != totalPage)
		{
			btnNextPage.setVisibility(Button.VISIBLE);
		}
		else {
			btnNextPage.setVisibility(Button.INVISIBLE);
		}
		if(currentPage != 1)
		{
			btnPreviousPage.setVisibility(Button.VISIBLE);
		}
		else {
			btnPreviousPage.setVisibility(Button.INVISIBLE);
		}
	}
	private void previousPage()
	{

		currentPage --;
		if(currentPage <= 0)
		{
			currentPage = 1;
			return;
		}
		refreshData();
		this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
		this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
		this.flipper.showPrevious();
		convertButtonVisible();

	}
	@Override
	protected String getSysName() {
		return null;
	}




}
