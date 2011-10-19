/*
 * 文 件 名:  TableViewActivity.java
 * 版    权:  New Element Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  yangzhao
 * 修改时间:  2011-5-6
 * 修改内容:  <修改内容>
 */

package com.szxys.mhub.ui.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.szxys.mhub.R;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * <一句话功能简述> ； <功能详细描述>
 * 
 * @author yangzhao
 * @version [版本号V01, 2011-5-6]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class TableViewActivity extends ListActivity implements OnGestureListener
{
    private static final String TAG = "TableViewActivity";
    
    private GestureDetector mGestureDetector;
    
    private int currentPage = 0;// 当前页数
    
    private int pageCount = 0;// 总页数
    
    private ListView mListView;// 操作ListView
    
    private LinearLayout mLoadLayout;// 生成表头Layout
    
    private TableViewAdapter mListViewAdapter;// TableView适配器
    
    private List<Map<String, Object>> mData0;// 加载的数据
    
    private int OP_Count = 10;// 每页显示的item数
    
    /*
     * 表头布局
     */
    private final LayoutParams mProgressBarLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
    
    private TextView tv5;// 表尾
    
    private float mDensity;// 显示的逻辑分辨率
    
    private int mWidth;// 表格宽度
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /*
         * "加载项"布局，此布局被添加到ListView的Footer中。
         */
        mLoadLayout = new LinearLayout(this);
        mLoadLayout.setMinimumHeight(60);
        mLoadLayout.setGravity(Gravity.CENTER);
        mLoadLayout.setOrientation(LinearLayout.HORIZONTAL);
        /*
         * 向"加载项"布局中添加一个TableHead
         */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;// 获取逻辑分辨率
        mWidth = metrics.widthPixels;// 获取屏幕宽度
        Log.v(TAG, "the density is:" + mDensity + ";and the w is:" + mWidth);
        TextView tv1 = new TextView(this);
        tv1.setText("ID");
        tv1.setGravity(Gravity.CENTER);
        tv1.setWidth(mWidth / 4);
        tv1.setBackgroundResource(R.drawable.pf_tableview_bg);
        TextView tv2 = new TextView(this);
        tv2.setText("Name");
        tv2.setBackgroundResource(R.drawable.pf_tableview_bg);
        tv2.setWidth(mWidth / 4);
        tv2.setGravity(Gravity.CENTER);
        TextView tv3 = new TextView(this);
        tv3.setText("Age");
        tv3.setBackgroundResource(R.drawable.pf_tableview_bg);
        tv3.setWidth(mWidth / 4);
        tv3.setGravity(Gravity.CENTER);
        TextView tv4 = new TextView(this);
        tv4.setText("Sex");
        tv4.setBackgroundResource(R.drawable.pf_tableview_bg);
        tv4.setWidth(mWidth / 4);
        tv4.setGravity(Gravity.CENTER);
        
        mLoadLayout.addView(tv1, mProgressBarLayoutParams);
        mLoadLayout.addView(tv2, mProgressBarLayoutParams);
        mLoadLayout.addView(tv3, mProgressBarLayoutParams);
        mLoadLayout.addView(tv4, mProgressBarLayoutParams);
        /*
         * 向加载布局添加tableFooter,并初始化数据
         */
        mListView = getListView();
        tv5 = new TextView(this);
        Map<String, Object>[] maps = testData();
        mData0 = GetMaps.getData(maps);
        if (mData0 != null && mData0.size() % OP_Count == 0)
        {
            currentPage = 1;
            pageCount = mData0.size() / OP_Count;
        }
        else if (mData0 != null)
        {
            currentPage = 1;
            pageCount = mData0.size() / OP_Count + 1;
        }
        String st = "当前页为：" + currentPage + "共：" + pageCount + "页";
        tv5.setText(st);
        tv5.setGravity(Gravity.CENTER);
        tv5.setBackgroundColor(Color.BLUE);
        mListView.addHeaderView(mLoadLayout);
        mListView.addFooterView(tv5);
        
        /*
         * 设置layout :mlistview 中需要显示的控件
         */
        String[] key1 = new String[] {"tv11", "tv12", "tv13", "tv14"};
        int[] ids1 = new int[] {R.id.tv11, R.id.tv12, R.id.tv13, R.id.tv14};
        /*
         * layout :mlistview的适配器 ，内容为mlistview 中所包含的控件
         */
        mListViewAdapter = new TableViewAdapter(this, mData0, R.layout.pf_table_item, key1, ids1);
        mListViewAdapter.setOP_Count(OP_Count);// 设置每页显示记录条数
        setListAdapter(mListViewAdapter);
        
        /*
         * 初始化手势
         */
        mGestureDetector = new GestureDetector(this);
        mListView.setOnTouchListener(new OnTouchListener()
        {
            
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1)
            {
                return mGestureDetector.onTouchEvent(arg1);
            }
        });
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        mListViewAdapter.setSelectedId(position);
        mListViewAdapter.notifyDataSetInvalidated();
        System.out.println(mListViewAdapter.getSelectedId());
        System.out.println(mListViewAdapter.getItemId(position));
        System.out.println(mListViewAdapter.getItem(position).toString());
    }
    
    @Override
    public void onContentChanged()
    {
        super.onContentChanged();
    }
    
    @Override
    public boolean onDown(MotionEvent e)
    {
        return false;
    }
    
    @Override
    public void onShowPress(MotionEvent e)
    {
        
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        return false;
    }
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        return false;
    }
    
    @Override
    public void onLongPress(MotionEvent e)
    {
        
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return mGestureDetector.onTouchEvent(event);
    }
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        if (velocityX > 0 && currentPage < pageCount)
        {// 翻页次数为pagecount
            currentPage++;
            mListViewAdapter.setPage(currentPage);
            String st = "当前页为：" + currentPage + "共：" + pageCount + "页";
            tv5.setText(st);
            mListViewAdapter.notifyDataSetInvalidated();
            mListView.setSelection(4);// 设置选择位置
            
        }
        else if (velocityX < 0 && currentPage > 1)
        {
            currentPage--;
            mListViewAdapter.setPage(currentPage);
            String st = "当前页为：" + currentPage + "共：" + pageCount + "页";
            tv5.setText(st);
            mListViewAdapter.notifyDataSetInvalidated();
            
        }
        return true;
    }
    
    // 支持mlistview layout的数据，子业务需扩展
    public HashMap<String, Object>[] testData()
    {
        
        String[] key1 = new String[] {"tv11", "tv12", "tv13", "tv14"};
        Object[] id1 = new Object[] {"yang1", "zhao1", "text", "1"};
        Object[] id2 = new Object[] {"yang2", "zhao2", "text", "2"};
        Object[] id3 = new Object[] {"yang3", "zhao3", "text", "3"};
        Object[] id4 = new Object[] {"yang4", "zhao4", "text", "4"};
        HashMap<String, Object> map1 = GetMaps.getMap(key1, id1);
        HashMap<String, Object> map2 = GetMaps.getMap(key1, id2);
        HashMap<String, Object> map3 = GetMaps.getMap(key1, id3);
        HashMap<String, Object> map4 = GetMaps.getMap(key1, id4);
        @SuppressWarnings("unchecked")
        HashMap<String, Object>[] maps = new HashMap[] {map1, map1, map1, map1, map1, map1, map1, map1, map1, map1,
                map1, map1, map2, map2, map2, map2, map2, map2, map2, map2, map2, map2, map2, map2, map3, map3, map3,
                map3, map3, map3, map3, map3, map3, map3, map3, map3, map4, map4, map4, map4, map4, map4, map4, map4,
                map4, map4, map4, map4,
        // map1,map1,map1,map1,map1,map1,map1,map1,map1,map1,map1,map1,
        // map2,map2,map2,map2,map2,map2,map2,map2,map2,map2,map2,map2,
        // map3,map3,map3,map3,map3,map3,map3,map3,map3,map3,map3,map3,
        // map4,map4,map4,map4,map4,map4,map4,map4,map4,map4,map4,map4,
        // map1,map1,map1,map1,map1,map1,map1,map1,map1,map1,map1,map1,
        // map2,map2,map2,map2,map2,map2,map2,map2,map2,map2,map2,map2,
        // map3,map3,map3,map3,map3,map3,map3,map3,map3,map3,map3,map3,
        // map4,map4,map4,map4,map4,map4,map4,map4,map4,map4,map4,map4,
        // map1,map1,map1,map1,map1,map1,map1,map1,map1,map1,map1,map1,
        // map2,map2,map2,map2,map2,map2,map2,map2,map2,map2,map2,map2
        };
        return maps;
    }
    
    // private RadioGroup page(Context context,int count){
    // RadioGroup rg = new RadioGroup(context);
    // for(int i = 0;i<count;i++)
    // rg.addView(new RadioButton(context), i);
    // return rg;
    // }
}
