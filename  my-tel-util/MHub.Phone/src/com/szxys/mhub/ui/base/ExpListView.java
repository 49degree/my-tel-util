/*
 * 文 件 名:  ExpListView.java
 * 版    权:  New Element Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  yangzhao
 * 修改时间:  2011-5-11
 * 修改内容:  <修改内容>
 */
package com.szxys.mhub.ui.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.szxys.mhub.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpListView extends Activity
{
    
    List<Map<String, Object>> groupData = new ArrayList<Map<String, Object>>();
    
    List<List<Map<String, Object>>> childData = new ArrayList<List<Map<String, Object>>>();
    
    private ExpandableListView elistview;
    
    private ExpListViewAdapter mAdapter;
    
    private TextView tv;
    
    private static final String KEY1 = "img1";
    
    private static final String KEY2 = "tv1";
    
    private static final String KEY3 = "tv2";
    
    private static final String KEY4 = "tv3";
    
    private static final String KEY5 = "imgbtn1";
    
    /**
     * 当前打开的父节点
     */
    private int the_group_expand_position = -1;
    
    /**
     * 打开的父节点所与的子节点数
     */
    private int position_child_count = 0;
    
    /**
     * 是否有打开的父节点
     */
    private boolean isExpanding = false;
    
    /**
     * 构造数据
     */
    public void getData()
    {
        for (int i = 0; i < 20; i++)
        {
            Map<String, Object> curGroupMap = new HashMap<String, Object>();
            groupData.add(curGroupMap);
            curGroupMap.put(KEY1, android.R.drawable.ic_dialog_email);
            curGroupMap.put(KEY2, "Group " + i);
            curGroupMap.put(KEY3, (i % 2 == 0) ? "This group is even" : "This group is odd");
            curGroupMap.put(KEY4, (i % 2 == 0) ? "This " + i : "This group null");
            curGroupMap.put(KEY5, android.R.drawable.ic_dialog_alert);
            
            List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
            for (int j = 0; j < 15; j++)
            {
                Map<String, Object> curChildMap = new HashMap<String, Object>();
                children.add(curChildMap);
                curChildMap.put(KEY1, android.R.drawable.ic_dialog_email);
                curChildMap.put(KEY2, "Child " + j);
                curChildMap.put(KEY3, (j % 2 == 0) ? "This child is even" : "This child is odd");
                curChildMap.put(KEY4, (j % 2 == 0) ? "This" + j : "This child null");
                curChildMap.put(KEY5, android.R.drawable.ic_dialog_alert);
            }
            childData.add(children);
        }
    }
    
    public void onCreate(Bundle saveBundle)
    {
        super.onCreate(saveBundle);
        setContentView(R.layout.pf_explistview);
        
        elistview = (ExpandableListView)findViewById(R.id.exp_listview);
        // 替换ExpandableListView的打开关闭时的箭头图标
        // elistview.setGroupIndicator(this.getResources().getDrawable(android.R.drawable.btn_default_small));
        tv = (TextView)findViewById(R.id.list_textview);
        /*
         * 滑动子列表时在上方显示父节点的view
         */
        final LinearLayout linear = (LinearLayout)findViewById(R.id.gone_linear);
        
        /*
         * 监听父节点打开的事件
         */
        elistview.setOnGroupExpandListener(new OnGroupExpandListener()
        {
            
            @Override
            public void onGroupExpand(int groupPosition)
            {
                the_group_expand_position = groupPosition;
                position_child_count = childData.get(groupPosition).size();
                isExpanding = true;
            }
            
        });
        
        /*
         * 监听父节点关闭的事件
         */
        elistview.setOnGroupCollapseListener(new OnGroupCollapseListener()
        {
            
            @Override
            public void onGroupCollapse(int groupPosition)
            {
                if (linear.getVisibility() == View.VISIBLE)
                {
                    linear.setVisibility(View.GONE);
                }
                isExpanding = false;
            }
            
        });
        /*
         * 设置右边显示
         */
        Display newDisplay = getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();
        elistview.setIndicatorBounds(width - 50, width);
        
        linear.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                linear.setVisibility(View.GONE);
                elistview.collapseGroup(the_group_expand_position);
            }
            
        });
        
        /*
         * 通过setOnScrollListener来监听列表上下滑动时item显示和消失的事件
         */

        elistview.setOnScrollListener(new OnScrollListener()
        {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if (isExpanding)
                {
                    // 当当前第一个item id小于打开的父节点id 或大于打开的父节点id和它的子节点总数之和时
                    if (firstVisibleItem < the_group_expand_position
                            || firstVisibleItem > (the_group_expand_position + position_child_count))
                    {
                        linear.setVisibility(View.GONE);
                    }
                    else
                    {
                        linear.setVisibility(View.VISIBLE);
                        tv.setText(groupData.get(the_group_expand_position).get(KEY2).toString());
                    }
                }
            }
            
        });
        
        getData();
        
        // Set up our adapter
        mAdapter = new ExpListViewAdapter(this, groupData, R.layout.pf_listview_item, new String[] {KEY1, KEY2, KEY3,
                KEY4, KEY5}, new int[] {R.id.img1, R.id.tv1, R.id.tv2, R.id.tv3, R.id.imbtn1}, childData,
                R.layout.pf_listview_item, new String[] {KEY1, KEY2, KEY3, KEY4, KEY5}, new int[] {R.id.img1, R.id.tv1,
                        R.id.tv2, R.id.tv3, R.id.imbtn1});
        elistview.setAdapter(mAdapter);
        
    }
}
