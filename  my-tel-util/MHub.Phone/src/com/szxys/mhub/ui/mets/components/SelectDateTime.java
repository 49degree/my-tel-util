package com.szxys.mhub.ui.mets.components;

import java.util.Calendar;

import com.szxys.mhub.subsystem.mets.utils.TimeUtils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

public class SelectDateTime extends LinearLayout{

	private Context mContext;
    // where we display the selected date and time
    private EditText mDateDisplay;
    
    // date and time
    private int mYear ;
    private int mMonth;
    private int mDay;

    static final int DATE_DIALOG_ID = 1;
    
    static final int FORMAT_DATE1 = 1;   //yyyy年MM月dd日;
    static final int FORMAT_DATE2 = 2;	//yyyy-MM-dd
	
    private int format_flag = 2;
    
	public SelectDateTime(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public SelectDateTime(Context context) {
		super(context);
		mContext = context;
	}
	
	/**
	 * 设置日期控件的时间值
	 * @param date
	 */
	public void setDateText(String date)
	{
		mDateDisplay.setText(date);
	}
	
	/**
	 * 获取日期控件的时间值
	 * @param date
	 */
	public String getDateText()
	{
		return mDateDisplay.getText().toString();
	}
	
	public void init(Calendar c, String dateFormat){
		
		if(TimeUtils.format.equalsIgnoreCase(dateFormat) ||
				TimeUtils.format1.equalsIgnoreCase(dateFormat)) {
			format_flag = 1;
		} else {
			format_flag = 2;
		}

		mDateDisplay = new EditText(mContext);
		this.addView(mDateDisplay,new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		mDateDisplay.setOnClickListener(editDateClick);
		
		mDateDisplay.setOnTouchListener(new OnTouchListener() {  
            
            public boolean onTouch(View v, MotionEvent event) {
                int inType = mDateDisplay.getInputType(); // backup the input type  
                mDateDisplay.setInputType(InputType.TYPE_NULL); // disable soft input      
                mDateDisplay.onTouchEvent(event); // call native handler      
                mDateDisplay.setInputType(inType); // restore input type     
                mDateDisplay.setSelection(mDateDisplay.getText().length());  
                return true;  
                  
            }  
        });  
		
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        updateDisplay();
	}
	
	View.OnClickListener editDateClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
				createDialog(DATE_DIALOG_ID).show();
		}
	};
	
	protected Dialog createDialog(int id) {
         return new DatePickerDialog(mContext,
                            mDateSetListener,
                            mYear, mMonth, mDay);
    }
	
	
	private void updateDisplay() {
        // Month is 0 based so add 1
		if(FORMAT_DATE1 == format_flag) {
			StringBuilder str= new StringBuilder()
			.append(mYear).append("年")
	        .append(formatString(mMonth + 1)).append("月")
	        .append(formatString(mDay)).append("日");
			
			mDateDisplay.setText(str.toString());
			
		} else if (FORMAT_DATE2 == format_flag) {
			StringBuilder str= new StringBuilder()
			.append(mYear).append("-")
	        .append(formatString(mMonth + 1)).append("-")
	        .append(formatString(mDay));
			
			mDateDisplay.setText(str.toString());
		}
		
	    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateDisplay();
            }
        };

    
    /**
     * 处理时间的month和day,如果是一位，则格式化成2位
     * @param parm
     * @return
     */
    private String formatString(int parm){
    	String strParm = String.valueOf(parm);
		if(parm<10){
			strParm = "0"+parm;
		}
		return strParm;
    }
}
