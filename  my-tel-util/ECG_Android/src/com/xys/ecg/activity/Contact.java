package com.xys.ecg.activity;

import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ListView;
import com.xys.ecg.activity.TableAdapter.TableCell;
import com.xys.ecg.activity.TableAdapter.TableRow;

public class Contact extends Activity {
	private  final int TABLE_COUND = 6;
	private  int SCREEN_WIDTH;//��Ļ���
	private  int width;//ÿ����Ԫ���
	private ListView list_Head;
	private ListView list_Body;
	private Button btBack;
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.contact);   
	        SCREEN_WIDTH = this.getWindowManager().getDefaultDisplay().getWidth();
	        width = (SCREEN_WIDTH - 30)/TABLE_COUND ;//������߸���15dp
	        
	        list_Head = (ListView)findViewById(R.id.List_contact_head); 
	        list_Body = (ListView)findViewById(R.id.List_contact_body);
	        createHead();
	        createBody();	
	        
            btBack =  (Button)findViewById(R.id.Back);
	        btBack.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
	        	     
	 }
	 
	 private void createHead(){
		 ArrayList<TableRow> table_Head = new ArrayList<TableRow>();
	     TableCell[] titles = new TableCell[5];//ÿ��4����Ԫ 
	     titles[0] = new TableCell(getText(R.string.msg), width, LayoutParams.FILL_PARENT, TableCell.STRING);
	     titles[1] = new TableCell(getText(R.string.phone), width, LayoutParams.FILL_PARENT, TableCell.STRING);
	     titles[2] = new TableCell(getText(R.string.name), width*2, LayoutParams.FILL_PARENT, TableCell.STRING);
	     titles[3] = new TableCell(getText(R.string.phone_number), width*2, LayoutParams.FILL_PARENT, TableCell.STRING);
	     titles[4] = new TableCell(R.string.sms ,width*6, LayoutParams.FILL_PARENT, TableCell.STRING);
	  	    
	     table_Head.add( new TableRow(titles));
	     TableAdapter tableAdapter = new TableAdapter(this, table_Head);
	     list_Head.setAdapter(tableAdapter);
	     list_Head.setClickable(false);  
		 
	 }
	 
	 private void createBody(){
		 
		 ArrayList<TableRow> table_Body = new ArrayList<TableRow>();
		 
		 for(int i=0; i<100; i++){
		     TableCell[] titles = new TableCell[4];//ÿ��4����Ԫ 
	         titles[0] = new TableCell(R.drawable.check, width, LayoutParams.FILL_PARENT, TableCell.IMAGE);
	         titles[1] = new TableCell(R.drawable.check, width, LayoutParams.FILL_PARENT, TableCell.IMAGE);
	         titles[2] = new TableCell("����ƽ", width*2, LayoutParams.FILL_PARENT, TableCell.STRING);
	         titles[3] = new TableCell("13480988448", width*2, LayoutParams.FILL_PARENT, TableCell.STRING);
	       //  titles[4] = new TableCell("������֤ȯ��Ȩ��������",width*6, LayoutParams.FILL_PARENT, TableCell.STRING);
	         table_Body.add( new TableRow(titles));  
		 }
		  TableAdapter tableAdapter = new TableAdapter(this, table_Body);
		 list_Body.setAdapter(tableAdapter);
		 
	 }
}
