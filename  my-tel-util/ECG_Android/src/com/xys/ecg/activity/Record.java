package com.xys.ecg.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xys.ecg.activity.TableAdapter.TableCell;
import com.xys.ecg.activity.TableAdapter.TableRow;
import com.xys.ecg.log.Logger;
import com.xys.ecg.sqlite.RecordDB;
import com.xys.ecg.upload.UploadEcgInfoTool;

public class Record extends Activity {
	private TextView tishi = null;     //��ʾѡ�еڼ���
	private CheckBox check_all = null;      //���� CheckBox
	private CheckBox check_lead = null;     //����ʽ  CheckBox
    private CheckBox check_touch = null;    //����ʽ  CheckBox
    private EditText edit_date = null;      //����ѡ���
    private Calendar calendar; // java��Calendar��
    private RecordDB recordDB =null;
    private Cursor cousor;
    private Button btOperate;
    public static Logger logger = Logger.getLogger(Record.class);
    private int changerow;  //ѡ���˵ڼ���
    private boolean ischeck_all = false;  //�Ƿ�ѡ������ ���CheckBox
    private boolean ischeck_lead = true;  //�Ƿ�ѡ�е���ʽ���CheckBox
    private boolean ischeck_touch = true;  //�Ƿ�ѡ�д���ʽ���CheckBox
    private String uploadFileName = null;
    private boolean isUploadAll = false;
    private int eyear ;
    private int emonth ;
    private int edate ;
    private long starttimeUTC;
    private ArrayList<Integer> hm = new ArrayList<Integer>();
    private TableAdapter tableAdapter;
    private View viewOldSelect = null;
    

    
	//private ListView lv_Head;
	private ListView lv_Body;
	private EditText et_Date;
	private Button btn_back;
	//private void CreateTable();
	private  int SCREEN_WIDTH;//��Ļ���
	private  int width;//ÿ����Ԫ���
	private  final int TABLE_COUND = 7;
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.record);
	       
	        recordDB = new RecordDB(Record.this);
	        
	        tishi = (TextView )findViewById(R.id.tishi);
	        edit_date = (EditText)findViewById(R.id.Edit_date);
	        //check_all = (CheckBox)findViewById(R.id.Check_all);
	        check_lead = (CheckBox)findViewById(R.id.Check_lead);
	        check_touch = (CheckBox)findViewById(R.id.Check_touch);
	        btn_back = (Button)findViewById(R.id.Btn_back);
	        btOperate = (Button)findViewById(R.id.Btn_operate);
	        check_lead.setChecked(true);
	        check_touch.setChecked(true);
	        

	        
	        //���������
	        btn_back.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					finish();

				}
			});
	        final String[] items = {"�ϴ�", "ȫѡ", "ɾ��", "ȡ��"};
	        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        btOperate.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
				builder.setTitle("��ѡ��").setItems(items, new DialogInterface.OnClickListener() {			
					public void onClick(DialogInterface dialog, int which) {						
						switch(which){
						case 0:
							Toast.makeText(Record.this, "��ѡ�����ϴ�",   
								     2000).show(); 
								//tishi.setText("��ѡ�����ϴ�");
								if(tishi.getText().toString().equals("��ѡ����������"))
								{
									//�ϴ����е���
									updateFile(-1);   //-1 ��ʾ�ϴ����е���
									changerow = 0; //�ϴ��ɹ�������
								}
								else
								{
									//�ϴ�ѡ�����
									if(changerow > 0)
									{
									  updateFile(--changerow);  //�ϴ�ѡ����У�����ж�Ӧ���ݿ��е���
									  changerow = 0; //�ϴ��ɹ�������
									}
								}
							
							break;
						case 1:
							tishi.setText("��ѡ����������");
							showList(starttimeUTC ,true ,ischeck_lead ,ischeck_touch);
							Toast.makeText(Record.this, "��ѡ����������",   
								     2000).show(); 
							break;
						case 2:
							if(tishi.getText().toString() == "��ѡ����������")
							{
								//ɾ�����е���
								deleteRow(true);
								
							}
							else
							{
								//ɾ��ѡ�����
								deleteRow(false); //ɾ������
							}
							break;
						case 3:   //ȡ��
							break;
						default:
							break;
						}
						
					}
				}).create().show();
				

				}
			});

	        
	        edit_date.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					 Calendar c=Calendar.getInstance();
					Dialog dialog=new DatePickerDialog(Record.this, new DatePickerDialog.OnDateSetListener() 
					{
						public void onDateSet(DatePicker view, int year, int monthOfYear,
								int dayOfMonth) {
			                      // Toast.makeText(Record.this, "��:"+String.valueOf(year)+";month:"+monthOfYear, Toast.LENGTH_LONG).show();
							     eyear = year - 1900;
							     emonth = monthOfYear ;
							     edate = dayOfMonth;
							     starttimeUTC = Date.UTC(eyear,emonth,edate,0-8,0,0);					
							     edit_date.setText(String.valueOf(year) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(dayOfMonth));
//							     showList(0 ,ischeck_all ,ischeck_lead ,ischeck_touch);
							     //�Ѹ�ѡ��ťȫ������Ϊfalse
							    // check_all.setChecked(false);
							     check_lead.setChecked(true);
							     check_touch.setChecked(true);
							     
							     showList(starttimeUTC ,false,true,true);
							     cousor = recordDB.getAllRecord();
							     logger.error("StartUTC=" + starttimeUTC+long2Date(starttimeUTC));
							     logger.error("StartTime ="+ starttimeUTC+"");
							     while(cousor.moveToNext()){
							    	 
							    	 long miao = cousor.getLong(cousor.getColumnIndex("StartTime"));
							    	 logger.error(miao+""+"ID="+cousor.getInt(cousor.getColumnIndex("UserID")));
							    	 
							     }
							     logger.error("EndTime = "+(starttimeUTC+24*60*60*1000));
							     
			                   }
					 }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
					
					dialog.show();
				}
	        });
	          
	        check_lead.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					         ischeck_lead = isChecked;
							 String strdate = edit_date.getText().toString();
							 String[] dates = strdate.split("-");
						     eyear = Integer.parseInt(dates[0]) - 1900;
						     emonth = Integer.parseInt(dates[1]) - 1;
						     edate = Integer.parseInt(dates[2]);
						     starttimeUTC = Date.UTC(eyear,emonth,edate,0-8,0,0);						   
						     showList(starttimeUTC ,false ,ischeck_lead ,ischeck_touch);
				        }
    		        }		
	        );
	        
	        
	        check_touch.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
				           	ischeck_touch = isChecked;
							 String strdate = edit_date.getText().toString();
							 String[] dates = strdate.split("-");
						     eyear = Integer.parseInt(dates[0]) - 1900;
						     emonth = Integer.parseInt(dates[1]) - 1;
						     edate = Integer.parseInt(dates[2]);
						     starttimeUTC = Date.UTC(eyear,emonth,edate,0-8,0,0);
                             showList(starttimeUTC ,false ,ischeck_lead ,ischeck_touch);
				        }
    		        }		
	        );
	        
	        
	        SCREEN_WIDTH = this.getWindowManager().getDefaultDisplay().getWidth();
	        width = (SCREEN_WIDTH - 30)/TABLE_COUND ;//������߸���15px
	        et_Date = (EditText)findViewById(R.id.Edit_date);
	        String strDate;
	        Calendar c = Calendar.getInstance(); 
	        int mYear = c.get(Calendar.YEAR) ; //��ȡ��ǰ��� 
	        int mMonth = c.get(Calendar.MONTH) + 1;//��ȡ��ǰ�·� 
	        int mDay = c.get(Calendar.DAY_OF_MONTH);//��ȡ��ǰ�·ݵ����ں��� 
	        int mHour = c.get(Calendar.HOUR_OF_DAY);//��ȡ��ǰ��Сʱ�� 
	        int mMinute = c.get(Calendar.MINUTE);//��ȡ��ǰ�ķ�����   
	        strDate = mYear + "-" + mMonth + "-" + mDay ;
	        et_Date.setText(strDate);
	        
	     //   lv_Head = (ListView)findViewById(R.id.List_head);//��ͷ
	        lv_Body = (ListView)findViewById(R.id.List_record);//��¼��
	        
	        
	      //  createTableHead();
	        //showList();
    
	        starttimeUTC = Date.UTC(mYear - 1900,mMonth-1,mDay,0-8,0,0);
	        showList(starttimeUTC ,false ,true ,true);
	         
	      registerForContextMenu(lv_Body);  //ע���Ӳ˵�
	      
	      lv_Body.setOnItemClickListener(new AdapterView.OnItemClickListener(){  //������Ϣ
	          
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					lv_Body.clearChoices();
					ImageView ivSelect = (ImageView)arg1.findViewById(R.id.Img_CheckBox);
					TextView tvMode = (TextView)arg1.findViewById(R.id.Lead);					
					TextView tvStatus = (TextView)arg1.findViewById(R.id.Status);
					TextView tvStartTime = (TextView)arg1.findViewById(R.id.StartTime);				
					TextView tvTimelength = (TextView)arg1.findViewById(R.id.TimeLength);
					TextView tvFilePath = (TextView)arg1.findViewById(R.id.FilePath);
					
					changerow = arg2 + 1;
					uploadFileName = tvFilePath.getText().toString();
					logger.error(uploadFileName);
					
					if(ivSelect.getBackground()==null){
												
						ivSelect.setBackgroundResource(R.drawable.checkbox);
					}else{
						ivSelect.setBackgroundResource(0);
					}
					
					if(viewOldSelect!=null){
						if(arg1==viewOldSelect){
							
						}else{
							ImageView ivOldSelect = (ImageView)viewOldSelect.findViewById(R.id.Img_CheckBox);			
							ivOldSelect.setBackgroundResource(0);	
						}						
					}
					
					changerow = arg2 + 1;
					viewOldSelect = arg1;
															   
				}
				  
			  } );
	                     
	 }
	 

	 
	private void deleteRow(boolean isAll)
	{	
		if(!isAll){
			try {
				File ecg = new File(uploadFileName);
				   ecg.delete();
				   recordDB.deleteRecordByFilePath(uploadFileName);
				 //  drawTableBody(starttimeUTC ,false ,ischeck_lead ,ischeck_touch);
			} catch (Exception e) {
				  logger.debug("Delete Record if failed!");
				e.printStackTrace();
			}
			
		}else{
			//TODO:ɾ������
			int size = lv_Body.getAdapter().getCount();
			for(int i = 0; i< size; i++){
			  HashMap<String, Object> map = new HashMap<String, Object>();
			  map =  (HashMap<String, Object>) lv_Body.getAdapter().getItem(i);
			  String strFilePath = (String)map.get("ItemFilePath");	
			  try{
				  File ecg = new File(strFilePath);
				  ecg.delete();
				  recordDB.deleteRecordByFilePath(strFilePath);
			  }catch(Exception e){
				  
			  }			  
			}
			
		}
		showList(starttimeUTC ,false ,ischeck_lead ,ischeck_touch);
	}
	
   
	//�ϴ��ļ�
	private void updateFile(int row)
	{
		ArrayList<String> addressArray = new ArrayList<String>(); //Ҫ�ϴ�����
		cousor = recordDB.selectRecordByWhere(starttimeUTC, ischeck_all, ischeck_lead, ischeck_touch);
		String fileName = null;
		String strFilePath = null;
		String strFileName = null;
		
		Handler handler = new Handler(){
			 public void handleMessage(Message msg) {
				 logger.info((String)msg.obj);
				 //TODO:
				 Toast.makeText(Record.this,(String)msg.obj,Toast.LENGTH_LONG).show();
			 }
		 };
		if(row!=-1){
			
			 fileName = uploadFileName;
			 strFilePath = fileName.substring(0,fileName.lastIndexOf('/'));
			 strFileName = fileName.substring(fileName.lastIndexOf('/')+1);
			logger.debug("Enter File upload");
			//TODO:������Ϣ�ж�
			
			boolean bRet = UploadEcgInfoTool.sendEcgFile(strFilePath,strFileName, handler); 
			if(bRet){
				Toast.makeText(Record.this, "�ϴ��ɹ�һ����¼",   
					     2000).show(); 	
			}
			
		}else{
			int size = lv_Body.getAdapter().getCount();
			for(int i = 0; i< size; i++){
			  HashMap<String, Object> map = new HashMap<String, Object>();
			  map =  (HashMap<String, Object>) lv_Body.getAdapter().getItem(i);
			  fileName = (String)map.get("ItemFilePath");	
			  strFilePath = fileName.substring(0,fileName.lastIndexOf('/'));
			  strFileName = fileName.substring(fileName.lastIndexOf('/')+1);
			  logger.debug("Enter File upload");
			  boolean bRet = UploadEcgInfoTool.sendEcgFile(strFilePath,strFileName, handler); 
				if(bRet){
					Toast.makeText(Record.this, "�ϴ��ɹ�һ����¼",   
						     2000).show(); 	
				}	  
			}//end for
			
			
		}
	}
		
	/*	
		//TODO:�ϴ��ļ�
		 int i = 0 ; // ��� 
		 cousor.moveToFirst();
		 while(cousor.moveToNext())
		 {
			 if(row != -1)
			 {
				 if(i == row)  //�б���ʾ�����������ݿ���һ��
				 {
					 addressArray.add(cousor.getString(cousor.getColumnIndex("FilePath")));
					 break;
				 }
			 }
			 else
			 {
				 addressArray.add(cousor.getString(cousor.getColumnIndex("FilePath")));
			 }
			 i++;
		 }		
		 if(addressArray.size() > 0)
		 {
			 boolean bRet = false;
			 
			 Handler handler = new Handler(){
				 public void handleMessage(Message msg) {
					 logger.info((String)msg.obj);
					 //TODO:
					 Toast.makeText(Record.this,(String)msg.obj,Toast.LENGTH_LONG).show();
				 }
			 };
			 
			 //����WebServce�ϴ�
			 String strFilePath = null;
			 String strFileName = null;
			for(int j = 0; j < addressArray.size(); j++){
				String fileName = addressArray.get(j).toString();
				strFilePath = fileName.substring(0,fileName.lastIndexOf('/'));
				strFileName = fileName.substring(fileName.lastIndexOf('/')+1);
				logger.debug("Enter File upload");
				//TODO:������Ϣ�ж�
				bRet = UploadEcgInfoTool.sendEcgFile(strFilePath,strFileName, handler); 
				if(bRet){
					Toast.makeText(Record.this, "�ϴ��ɹ�һ����¼",   
						     2000).show(); 	
				}
				
			}
	 
			 // �ϴ��ɹ������ArrayList
			 addressArray.clear();
		 }
		 
	}
*/
	private void drawTableBody(long starttimeUTC ,boolean ischeck_all ,boolean ischeck_lead ,boolean ischeck_touch)    //���б�
	 {
		 
		 ArrayList<TableRow> table_Body = new ArrayList<TableRow>(); 
		 
		 try
		 {
		 cousor = recordDB.selectRecordByWhere(starttimeUTC, ischeck_all, ischeck_lead, ischeck_touch);
		 }catch(Exception ex)
		 {
	
		 }
            int i=0;
			 while(cousor.moveToNext())
			 {
				 System.out.println(cousor.getString(cousor.getColumnIndex("Mode")));
				 
				 hm.add(cousor.getInt(cousor.getColumnIndex("RecordID")));
				 i++;
				 
				 TableCell[] cells = new TableCell[6];//ÿ��5����Ԫ  
				 cells[0] = new TableCell(R.drawable.checkbox, 5, LayoutParams.FILL_PARENT, TableCell.IMAGE);
				 //cousor.getString(cousor.getColumnIndex("Mode"))==7 ��ʾ����ʽ,8��ʾ����ʽ
				 cells[1] = new TableCell(cousor.getInt(cousor.getColumnIndex("Mode")) == 7 ? "����":"����", width, LayoutParams.FILL_PARENT, TableCell.STRING);
				 //cousor.getString(cousor.getColumnIndex("Uploaded"))==
				 cells[2] = new TableCell(cousor.getInt(cousor.getColumnIndex("Uploaded"))==0?"δͬ��":"ͬ��", width*2, LayoutParams.FILL_PARENT, TableCell.STRING);			
				 long miao = cousor.getLong(cousor.getColumnIndex("StartTime"));
				 Date date = new Date(miao);
				 SimpleDateFormat sfEx = new SimpleDateFormat("yyyyMMddHH:mm:ss");
				 //addTime = sfEx.format(new Date(date));	
				 String strStime = sfEx.format(date);
				 String stime = strStime.substring(8, 16);
				// String stime = date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
				 System.out.println("time long ---------"+stime);
				 cells[3] = new TableCell(stime, width*2, LayoutParams.FILL_PARENT, TableCell.STRING);
				// ----------------------------------------------------------------------------------------
				 String path = cousor.getString(cousor.getColumnIndex("FilePath"));
				 //����·�����㿪ʼʱ��
				 cells[4] = new TableCell(getTimeLength(path), width*2, LayoutParams.FILL_PARENT, TableCell.STRING);
				 cells[5] = new TableCell(path,1,LayoutParams.FILL_PARENT, TableCell.STRING);
				 table_Body.add(new TableRow(cells));
				
			 }
			// if(cousor.getCount() >0 )
			// {
			   tableAdapter = new TableAdapter(this, table_Body);
			   lv_Body.setAdapter(tableAdapter);
			// }
			   
			// cousor.close();		 
	 }
	
	private void showList(long starttimeUTC ,boolean ischeck_all ,boolean ischeck_lead ,boolean ischeck_touch){    //���б�
		
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		
		SimpleAdapter listItemAdapter = null;
		try
		 {
		 cousor = recordDB.selectRecordByWhere(starttimeUTC, false, ischeck_lead, ischeck_touch);
		 }catch(Exception ex)
		 {
			 logger.debug("Show listTable is failed!");
		 }
		 
		 while(cousor.moveToNext()){
			 HashMap<String, Object> map = new HashMap<String, Object>();
			 if(ischeck_all){
				 map.put("ItemCheck", R.drawable.checkbox); 
			 }else{
				 map.put("ItemCheck", null);
			 }
			 
			 map.put("ItemMode", cousor.getInt(cousor.getColumnIndex("Mode")) == 7 ? "����":"����");
			 map.put("ItemStatus", cousor.getInt(cousor.getColumnIndex("Uploaded"))==0?"δͬ��":"ͬ��");
			 
			 long miao = cousor.getLong(cousor.getColumnIndex("StartTime"));
			 Date date = new Date(miao);
			 SimpleDateFormat sfEx = new SimpleDateFormat("yyyyMMddHH:mm:ss");			 
			 String strtime = sfEx.format(date);
			 String strTime = strtime.substring(8, 16);			 		 
			 map.put("ItemStartTime", strTime);
			 
			 String path = cousor.getString(cousor.getColumnIndex("FilePath"));
			 map.put("ItemTimeLength", getTimeLength(path));
			 
			 map.put("ItemFilePath", path);
			 
			 listItem.add(map);
			 
		 }
		 
		 
		 listItemAdapter = new SimpleAdapter(Record.this,
					listItem, R.layout.list_record_item, new String[] {
							"ItemCheck", "ItemMode", "ItemStatus", "ItemStartTime",
							"ItemTimeLength", "ItemFilePath" }, new int[] {
							R.id.Img_CheckBox, R.id.Lead, R.id.Status,
							R.id.StartTime, R.id.TimeLength, R.id.FilePath });
		 
		
		lv_Body.setAdapter(listItemAdapter);
		cousor.close();		 

	}
	
	private String getTimeLength(String filePath){ 
		if(filePath.equals(null)){
			logger.debug("The file name is NULL");
		}
		String nTime = null;
		long hours = 0;
		long minute = 0;
		long second = 0;
		String strHours = null;
		String strMinute = null;
		String strSecond = null;
		double length = 0;
		try{
			File file = new File(filePath); 
			length = (((file.length() - 32)/(288 + 3*72))*0.96);
			hours = (long)length/3600;//Сʱ
			minute = (long)length%3600/60;//����
			second = (long)length%60;//�� 		
			if(hours<10){
				strHours = "0"+hours;
			}else{
				strHours = hours + "";
			}
			if(minute<10){
				strMinute = "0" + minute;
			}else {
				strMinute = minute +"";
			}
			if(second<10){
				strSecond = "0" + second;
			}else{
				strSecond = second +"";
			}
			nTime = strHours + ":" + strMinute + ":" + strSecond; 
		
		}catch(Exception e){
			logger.debug("getTimeLeng() faile");
			return null;
		}
		return nTime;
	}
	 
	 private void createTableHead(){        //������ͷ  
		    
		 ArrayList<TableRow> table_Head = new ArrayList<TableRow>();
	     TableCell[] titles = new TableCell[4];//ÿ��4����Ԫ 
	     titles[0] = new TableCell(getText(R.string.mode_table), width, LayoutParams.WRAP_CONTENT, TableCell.STRING);
	     titles[1] = new TableCell(getText(R.string.state), width*2, LayoutParams.FILL_PARENT, TableCell.STRING);
	     titles[2] = new TableCell(getText(R.string.start_time), width*2, LayoutParams.FILL_PARENT, TableCell.STRING);
	     titles[3] = new TableCell(getText(R.string.time_length), width*2, LayoutParams.FILL_PARENT, TableCell.STRING);
	  	    
	     table_Head.add( new TableRow(titles));
	     TableAdapter tableAdapter = new TableAdapter(this, table_Head);
//	     lv_Head.setAdapter(tableAdapter);
//	     lv_Head.setClickable(false);
	     		 
	 }
	 //������
	
	 
		
		// ���������¼�
			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
					if(cousor!=null&&!cousor.isClosed()){
						cousor.close();
					}
					finish();
				}
				
				return false;
			}
			
			public static String long2Date(long time){
				String strTime = null;
				try{
					SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
					strTime = sf.format(new Date(time));
				}catch(Exception e){
					
				}
				return strTime;
			}
	 

}
