/*
 * 文 件 名:  TableViewAdapter.java
 * 版    权:  New Element Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  yangzhao
 * 修改时间:  2011-5-3
 * 修改内容:  <修改内容>
 */

package com.szxys.mhub.ui.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DigitalClock;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * TableView适配器 ； 使ListView显示样式为TableView样式
 * 
 * @author yangzhao
 * @version [版本号V01, 2011-5-3]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class TableViewAdapter extends SimpleAdapter
{
    private static final String TAG = "DataListaDapter";
    
    private LayoutInflater mInflater;
    
    private ArrayList<HashMap<String, Object>> listItem;// 所有传入数据
    
    private int selectedId = -1;// 当前选项ID
    
    private int resource;// 资源ID
    
    private int[] id;// 控件ID
    
    private ViewHolder holder;// 控件构造类
    
    private String[] keys;// 控件的id名称
    
    private int page = 0;// 当前页数
    
    private int itmeCount = 0;// 数据总数
    
    private int OP_Count = 10;// 每页显示item数
    
    private int pageCount = 0;// 每页显示记录数，只有计数功能
    
    /**
     * 构造函数
     */
    @SuppressWarnings("unchecked")
    public TableViewAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
    {
        super(context, data, resource, from, to);
        this.listItem = (ArrayList<HashMap<String, Object>>)data;
        this.mInflater = LayoutInflater.from(context);
        this.resource = resource;
        this.keys = from;
        this.id = to;
        itmeCount = listItem.size();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        
        convertView = mInflater.inflate(resource, null);
        holder = new ViewHolder();
        initView(convertView, id);
        convertView.setTag(resource);
        if (holder.tv1 != null)
        {
            holder.tv1.setText((String)listItem.get(page * OP_Count + position).get(keys[0]));
        }
        if (holder.tv2 != null)
        {
            holder.tv2.setText((String)listItem.get(page * OP_Count + position).get(keys[1]));
        }
        if (holder.tv3 != null)
        {
            holder.tv3.setText((String)listItem.get(page * OP_Count + position).get(keys[2]));
        }
        if (holder.tv4 != null)
        {
            holder.tv4.setText((String)listItem.get(page * OP_Count + position).get(keys[3]));
        }
        Log.v(TAG, "The position is:" + position + "and the select is:" + getSelectedId());
        if (position == selectedId - 1)
        {
            if (holder.tv1 != null)
            {
                holder.tv1.setBackgroundColor(Color.YELLOW);
            }
            if (holder.tv2 != null)
            {
                holder.tv2.setBackgroundColor(Color.YELLOW);
            }
            if (holder.tv3 != null)
            {
                holder.tv3.setBackgroundColor(Color.YELLOW);
            }
            if (holder.tv4 != null)
            {
                holder.tv4.setBackgroundColor(Color.YELLOW);
            }
        }
        // else {
        // holder.tv1.setBackgroundColor(R.drawable.pf_tableview_bg);
        // holder.tv2.setBackgroundColor(R.drawable.pf_tableview_bg1);
        // holder.tv3.setBackgroundColor(R.drawable.pf_tableview_bg);
        // holder.tv4.setBackgroundColor(R.drawable.pf_tableview_bg1);
        // }
        // }
        return convertView;
    }
    
    @Override
    public Object getItem(int position)
    {
        return listItem.get(position + page * OP_Count - 1);
    }
    
    @Override
    public long getItemId(int position)
    {
        return position + page * OP_Count - 1;
    }
    
    @Override
    public int getCount()
    {
        return OP_Count;
    }
    
    /**
     * 
     * 控件构造内部类 ； 实例化相应的控件
     * 
     * @author yangzhao
     * @version [版本号V01, 2011-5-6]
     * @see [相关类/方法]
     * @since [产品/模块版本]
     */
    private final class ViewHolder
    {
        
        public ImageView img1;
        
        public TextView tv1;
        
        public TextView tv2;
        
        public TextView tv3;
        
        public TextView tv4;
        
        public Button btn1;
        
        public Spinner spn1;
        
        public CheckBox cbx1;
        
        public ImageButton imbtn1;
        
        public RadioButton rdbtn1;
        
        public AnalogClock alclk1;
        
        public DigitalClock dgtclk1;
        
        public ToggleButton tgbtn1;
    }
    
    /**
     * 初始化类 ； 初始化构造类包含的控件
     * 
     * @param convertView
     * @param id
     * @see [类、类#方法、类#成员]
     */
    private void initView(View convertView, int[] id)
    {
        
        int k = 0;
        for (int i = 0; i < id.length; i++)
        {
            
            View mView = convertView.findViewById(id[i]);
            if (mView instanceof TextView)
            {
                k++;
            }
            if (mView instanceof TextView && k == 1)
            {
                holder.tv1 = (TextView)mView.findViewById(id[i]);
            }
            else if (mView instanceof TextView && k == 2)
            {
                holder.tv2 = (TextView)mView.findViewById(id[i]);
            }
            else if (mView instanceof TextView && k == 3)
            {
                holder.tv3 = (TextView)mView.findViewById(id[i]);
            }
            else if (mView instanceof TextView && k == 4)
            {
                holder.tv4 = (TextView)mView.findViewById(id[i]);
            }
            else if (mView instanceof ImageView)
            {
                holder.img1 = (ImageView)mView.findViewById(id[i]);
            }
            else if (mView instanceof DigitalClock)
            {
                holder.dgtclk1 = (DigitalClock)mView.findViewById(id[i]);
            }
            if (mView instanceof Button)
            {
                holder.btn1 = (Button)mView.findViewById(id[i]);
            }
            if (mView instanceof Spinner)
            {
                holder.spn1 = (Spinner)mView.findViewById(id[i]);
            }
            if (mView instanceof CheckBox)
            {
                holder.cbx1 = (CheckBox)mView.findViewById(id[i]);
            }
            if (mView instanceof ImageButton)
            {
                holder.imbtn1 = (ImageButton)mView.findViewById(id[i]);
            }
            if (mView instanceof RadioButton)
            {
                holder.rdbtn1 = (RadioButton)mView.findViewById(id[i]);
            }
            if (mView instanceof AnalogClock)
            {
                holder.alclk1 = (AnalogClock)mView.findViewById(id[i]);
            }
            if (mView instanceof ToggleButton)
            {
                holder.tgbtn1 = (ToggleButton)mView.findViewById(id[i]);
            }
            
        }
    }
    
    /**
     * 得到当前页数 ； <功能详细描述>
     * 
     * @return int
     * @see [类、类#方法、类#成员]
     */
    public int getPage()
    {
        return page;
    }
    
    /**
     * 设置当前显示第几页数据 ； <功能详细描述>
     * 
     * @param page ：int
     * @see [类、类#方法、类#成员]
     */
    public void setPage(int page)
    {
        if (page == itmeCount / pageCount + 1)
        {
            OP_Count = itmeCount % pageCount;
            this.page = page;
        }
        else
        {
            OP_Count = pageCount;
            this.page = page - 1;
        }
        
    }
    
    /**
     * 取得当前选择item的ID ； <功能详细描述>
     * 
     * @return int
     * @see [类、类#方法、类#成员]
     */
    public int getSelectedId()
    {
        return selectedId + page * OP_Count - 1;
    }
    
    /**
     * 设置当前选择item的ID ； <功能详细描述>
     * 
     * @param selectedId ：int
     * @see [类、类#方法、类#成员]
     */
    public void setSelectedId(int selectedId)
    {
        this.selectedId = selectedId;
    }
    
    /**
     * 取得每页显示记录数 ； <功能详细描述>
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public int getOP_Count()
    {
        return OP_Count;
    }
    
    /**
     * 设置每页显示记录数 ； <功能详细描述>
     * 
     * @param oP_Count
     * @see [类、类#方法、类#成员]
     */
    public void setOP_Count(int oP_Count)
    {
        OP_Count = oP_Count;
        pageCount = oP_Count;
    }
}
