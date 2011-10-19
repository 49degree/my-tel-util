/*
 * 文 件 名:  ExpanListViewActivity.java
 * 版    权:  New Element Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  yangzhao
 * 修改时间:  2011-5-10
 * 修改内容:  <修改内容>
 */
package com.szxys.mhub.ui.base;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.szxys.mhub.R;

public class ExpanListViewActivity extends ExpandableListActivity
{
    private static final String KEY1 = "img1";
    
    private static final String KEY2 = "tv1";
    
    private static final String KEY3 = "tv2";
    
    private static final String KEY4 = "tv3";
    
    private static final String KEY5 = "imgbtn1";
    
    private ExpListViewAdapter mAdapter;
    
    List<Map<String, Object>> groupData = new ArrayList<Map<String, Object>>();
    
    List<List<Map<String, Object>>> childData = new ArrayList<List<Map<String, Object>>>();
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        getData();
        // Set up our adapter
        mAdapter = new ExpListViewAdapter(this, groupData, R.layout.pf_listview_item, new String[] {KEY1, KEY2, KEY3,
                KEY4, KEY5}, new int[] {R.id.img1, R.id.tv1, R.id.tv2, R.id.tv3, R.id.imbtn1}, childData,
                R.layout.pf_listview_item, new String[] {KEY1, KEY2, KEY3, KEY4, KEY5}, new int[] {R.id.img1, R.id.tv1,
                        R.id.tv2, R.id.tv3, R.id.imbtn1});
        setListAdapter(mAdapter);
    }
    /**
     * 构造数据 ；
     * <功能详细描述>
     * @see [类、类#方法、类#成员]
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
    
}
