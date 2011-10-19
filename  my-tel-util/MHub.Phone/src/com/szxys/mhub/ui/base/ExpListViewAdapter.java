/*
 * 文 件 名:  ExpListViewAdapter.java
 * 版    权:  New Element Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  yangzhao
 * 修改时间:  2011-5-10
 * 修改内容:  <修改内容>
 */

package com.szxys.mhub.ui.base;

import java.util.List;
import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.BaseExpandableListAdapter;
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
 * 可扩展的ListView ； 实现可扩展的ListView，类似下拉菜单的样式
 * 
 * @author yangzhao
 * @version [版本号V01, 2011-5-10]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class ExpListViewAdapter extends BaseExpandableListAdapter
{
    
    private List<? extends Map<String, ?>> groupData;
    
    private int groupLayout;
    
    private String[] groupFrom;
    
    private int[] groupTo;
    
    private List<? extends List<? extends Map<String, ?>>> childData;
    
    private int childLayout;
    
    private String[] childFrom;
    
    private int[] childTo;
    
    private LayoutInflater mInflater;
    
    private ViewHolder holder;
    
    public ExpListViewAdapter(Context context, List<? extends Map<String, ?>> groupData, int groupLayout,
            String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData,
            int childLayout, String[] childFrom, int[] childTo)
    {
        this.mInflater = LayoutInflater.from(context);
        this.groupData = groupData;
        this.groupLayout = groupLayout;
        this.groupFrom = groupFrom;
        this.groupTo = groupTo;
        this.childData = childData;
        this.childLayout = childLayout;
        this.childFrom = childFrom;
        this.childFrom = childFrom;
        this.childTo = childTo;
    }
    
    @Override
    public Object getGroup(int groupPosition)
    {
        return groupData.get(groupPosition);
    }
    
    @Override
    public int getGroupCount()
    {
        return groupData.size();
    }
    
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }
    
    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return childData.get(groupPosition).get(childPosition);
    }
    
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }
    
    @Override
    public int getChildrenCount(int groupPosition)
    {
        return childData.get(groupPosition).size();
    }
    
    @Override
    public boolean hasStableIds()
    {
        return false;
    }
    
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        holder = new ViewHolder();
        View v;
        if (convertView == null)
        {
            v = mInflater.inflate(groupLayout, null);
            v.setTag(groupLayout);
        }
        else
        {
            v = convertView;
        }
        initView(v, groupData.get(groupPosition), groupFrom, groupTo);
        return v;
    }
    
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
            ViewGroup parent)
    {
        holder = new ViewHolder();
        View v;
        if (convertView == null)
        {
            v = mInflater.inflate(childLayout, null);
            v.setTag(childLayout);
        }
        else
        {
            v = convertView;
        }
        initView(v, childData.get(groupPosition).get(childPosition), childFrom, childTo);
        return v;
    }
    
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
    
    /**
     * 初始化Listview的Item显示内容 ； <功能详细描述>
     * 
     * @param v ：View
     * @param object ：Map<String, ?>
     * @param From ：map的key值
     * @param To ：控件ID
     * @see [类、类#方法、类#成员]
     */
    
    private void initView(View view, Map<String, ?> data, String[] From, int[] To)
    {
        
        int len = To.length;
        
        for (int i = 0; i < len; i++)
        {
            int k = 0;
            View v = view.findViewById(To[i]);
            if (v instanceof TextView)
            {
                k++;
            }
            if (v instanceof TextView && k == 1)
            {
                System.out.println("TextView:" + From[i]);
                holder.tv1 = (TextView)v.findViewById(To[i]);
                holder.tv1.setText((String)data.get(From[i]));
            }
            else if (v instanceof TextView && k == 2)
            {
                System.out.println("TextView:" + From[i]);
                holder.tv2 = (TextView)v.findViewById(To[i]);
                holder.tv2.setText((String)data.get(From[i]));
            }
            else if (v instanceof TextView && k == 3)
            {
                System.out.println("TextView:" + From[i]);
                holder.tv3 = (TextView)v.findViewById(To[i]);
                holder.tv3.setText((String)data.get(From[i]));
            }
            else if (v instanceof TextView && k == 4)
            {
                System.out.println("TextView:" + From[i]);
                holder.tv4 = (TextView)v.findViewById(To[i]);
                holder.tv4.setText((String)data.get(From[i]));
            }
            else if (v instanceof ImageView)
            {
                System.out.println("ImageView:" + From[i]);
                holder.img1 = (ImageView)v.findViewById(To[i]);
                holder.img1.setBackgroundResource((Integer)data.get(From[i]));
            }
            else if (v instanceof DigitalClock)
            {
                System.out.println("DigitalClock:" + From[i]);
                holder.dgtclk1 = (DigitalClock)v.findViewById(To[i]);
                holder.dgtclk1.setBackgroundResource((Integer)data.get(From[i]));
            }
            if (v instanceof Button)
            {
                System.out.println("Button:" + From[i]);
                holder.btn1 = (Button)v.findViewById(To[i]);
                holder.btn1.setText((String)data.get(From[i]));
                holder.btn1.setFocusable(false);
                holder.btn1.setFocusableInTouchMode(false);
            }
            if (v instanceof Spinner)
            {
                System.out.println("Spinner:" + From[i]);
                holder.spn1 = (Spinner)v.findViewById(To[i]);
                holder.spn1.setAdapter((SimpleAdapter)data.get(From[i]));
                holder.spn1.setFocusable(false);
                holder.spn1.setFocusableInTouchMode(false);
            }
            if (v instanceof CheckBox)
            {
                System.out.println("CheckBox:" + From[i]);
                holder.cbx1 = (CheckBox)v.findViewById(To[i]);
                holder.cbx1.setText((String)data.get(From[i]));
                holder.cbx1.setFocusable(false);
                holder.cbx1.setFocusableInTouchMode(false);
            }
            if (v instanceof ImageButton)
            {
                System.out.println("ImageButton:" + From[i]);
                holder.imbtn1 = (ImageButton)v.findViewById(To[i]);
                holder.imbtn1.setBackgroundResource((Integer)data.get(From[i]));
                holder.imbtn1.setFocusable(false);
                holder.imbtn1.setFocusableInTouchMode(false);
            }
            if (v instanceof RadioButton)
            {
                System.out.println("RadioButton:" + From[i]);
                holder.rdbtn1 = (RadioButton)v.findViewById(To[i]);
                holder.rdbtn1.setBackgroundResource((Integer)data.get(From[i]));
                holder.rdbtn1.setFocusable(false);
                holder.rdbtn1.setFocusableInTouchMode(false);
            }
            if (v instanceof AnalogClock)
            {
                System.out.println("AnalogClock:" + From[i]);
                holder.alclk1 = (AnalogClock)v.findViewById(To[i]);
                holder.alclk1.setBackgroundResource((Integer)data.get(From[i]));
            }
            if (v instanceof ToggleButton)
            {
                System.out.println("ToggleButton:" + From[i]);
                holder.tgbtn1 = (ToggleButton)v.findViewById(To[i]);
                holder.tgbtn1.setChecked((Boolean)data.get(From[i]));
                holder.tgbtn1.setFocusable(false);
                holder.tgbtn1.setFocusableInTouchMode(false);
            }
            
        }
        
    }
    
    /**
     * 
     * ListItem构造类 ； <功能详细描述>
     * 
     * @author yangzhao
     * @version [版本号V01, 2011-5-10]
     * @see [相关类/方法]
     * @since [产品/模块版本]
     */
    public final class ViewHolder
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
}
