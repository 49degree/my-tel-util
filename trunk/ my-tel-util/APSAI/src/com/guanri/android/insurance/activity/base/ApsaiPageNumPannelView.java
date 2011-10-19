package com.guanri.android.insurance.activity.base;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.guanri.android.insurance.R;
import com.guanri.android.lib.log.Logger;

/**
 * 翻页工具栏对象
 * @author Administrator
 *
 */
public class ApsaiPageNumPannelView extends LinearLayout{
	private static Logger  logger = Logger.getLogger(ApsaiPageNumPannelView.class);
	private Context context = null;
	private ApsaiPageNumOperator apsaiPageNumOperator;//页码操作对象
	private ImageButton frontPageBt = null;
	private ImageButton nextPageBt = null;
	private EditText curentPage = null;//apsai_page_num_curent_page
	private EditText nunPerPage = null;//apsai_page_num_per_page
	private TextView pageNumCount = null;//apsai_page_num_count	
	
	
	public ApsaiPageNumPannelView(Context context){
        this(context,null);
	}
	
	public ApsaiPageNumPannelView(Context context, AttributeSet attr){
        super(context, attr);
        this.context = context;
      //至于这个构造函数里边要写一些什么大家就随便啦。  
        LayoutInflater mInflater = LayoutInflater.from(context);
        LinearLayout pannelLayout = (LinearLayout)mInflater.inflate(R.layout.apsai_page_num_pannel, null);
        this.setGravity(Gravity.CENTER);
        this.addView(pannelLayout); 
        
		frontPageBt = (ImageButton)this.findViewById(R.id.apsai_page_front_btn);
		nextPageBt = (ImageButton)this.findViewById(R.id.apsai_page_next_btn);
		curentPage = (EditText)this.findViewById(R.id.apsai_page_num_curent_page);
		nunPerPage = (EditText)this.findViewById(R.id.apsai_page_num_per_page);
		pageNumCount = (TextView)this.findViewById(R.id.apsai_page_num_count);
		
		frontPageBt.setOnClickListener(onClickListener);
		nextPageBt.setOnClickListener(onClickListener);
		curentPage.addTextChangedListener(new PageNumTextWatcher(curentPage));
		nunPerPage.addTextChangedListener(new PageNumTextWatcher(nunPerPage));
		
	}
	
	/**
	 * 设置数据查询对象
	 * @param apsaiPageNumOperator
	 */
	public void initApsaiPageNumOperator(){
		curentPage.setText("1");
		initListAdapter();
	}
	
	/**
	 * 设置数据查询对象
	 * @param apsaiPageNumOperator
	 */
	public void setApsaiPageNumOperator(ApsaiPageNumOperator apsaiPageNumOperator){
		this.apsaiPageNumOperator = apsaiPageNumOperator;
		initListAdapter();
	}
	
	/**
	 * 计算页码信息
	 * 	pageInfo[0]//第几页
	 *	pageInfo[1]//每页行数
	 *	pageInfo[2]//总页数
	 *	pageInfo[3]//总行数
	 * @return
	 */
	public int[] getPageInfo(){
		int allCount = apsaiPageNumOperator.queryAllRows();
		int[] pageInfo = new int[4];
		pageInfo[0] = Integer.parseInt(curentPage.getText().toString());//第几页
		pageInfo[1] = Integer.parseInt(nunPerPage.getText().toString());//每页行数
		pageInfo[2] = allCount%pageInfo[1]==0?allCount/pageInfo[1]:(allCount/pageInfo[1]+1);//总页数
		pageInfo[3] = allCount;//总行数
		return pageInfo;
	}
	
	/**
	 * 更新列表
	 */
	public void initListAdapter(){
		logger.error("initListAdapter");
		int[] pageInfo = getPageInfo();
		pageNumCount.setText(String.valueOf(pageInfo[2]));
		apsaiPageNumOperator.initListAdapter(pageInfo);
	}
	
	/**
	 * 表单被改动事件
	 */
	private class PageNumTextWatcher implements TextWatcher{
		EditText view = null;
		String beforeStr = null;
		public PageNumTextWatcher(EditText view){
			this.view = view;
		}
        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                int arg3) {
        	Log.e("","beforeTextChanged"+s.toString()+":"+arg1+":"+arg2+":"+arg3);
        	beforeStr = s.toString();
        	//curentPage.setText("");
        }
        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2,int arg3) {
        	Log.e("","onTextChanged"+s.toString()+":"+arg1+":"+arg2+":"+arg3);
        	String newStr = s.toString();
        	if(newStr!=null&&!newStr.trim().equals("")&&!newStr.equals(beforeStr)&&!newStr.startsWith("0")){
        		//判断是否为整形数据
        		int newPageNum = 0;
        		try{
        			newPageNum = Integer.parseInt(newStr);
        		}catch(Exception e){
        			Toast.makeText(context,context.getString(R.string.apsai_page_num_error),Toast.LENGTH_SHORT).show();
        			return ;
        		}
        		//根据不同的输入框进行不同的操作
				switch (view.getId()) {
				case R.id.apsai_page_num_curent_page:// 页码输入框
					if ((newPageNum < 1 ||
							newPageNum > Integer.parseInt(pageNumCount.getText().toString()))) {
						Toast.makeText(context,context.getString(R.string.apsai_page_num_error),Toast.LENGTH_SHORT).show();
						curentPage.setText("");
					}else{
						initListAdapter();
					}
					break;
				case R.id.apsai_page_num_per_page:// 每页记录数输入框
					curentPage.setText("1");
					initListAdapter();
					break;
				default:
					break;
				}
        	}
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
	}
	
	/**
	 * 翻页箭头监听器
	 */
	private OnClickListener onClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int vId = v.getId();
			int curentPageNum = Integer.parseInt(curentPage.getText().toString());//第几页
			switch (vId) {
			case R.id.apsai_page_front_btn:
				if(curentPageNum>1){
					curentPage.setText(String.valueOf(--curentPageNum));
				}else{
					Toast.makeText(context, context.getString(R.string.apsai_page_num_error), Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.apsai_page_next_btn:
				if(curentPageNum<Integer.parseInt(pageNumCount.getText().toString())){
					curentPage.setText(String.valueOf( ++curentPageNum));
				}else{
					Toast.makeText(context, context.getString(R.string.apsai_page_num_error), Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 数据查询对象接口
	 * @author Administrator
	 *
	 */
	public interface ApsaiPageNumOperator{
		public int queryAllRows();
		/**
		 * 设置表格数据 
		 * 	pageInfo[0]//第几页
		 *	pageInfo[1]//每页行数
		 *	pageInfo[2]//总页数
		 *	pageInfo[3]//总行数
		 */		
		public void initListAdapter(int[] pageInfo);
	}

}
