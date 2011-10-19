/*
 * 文 件 名:  ListViewActivity.java
 * 版    权:  New Element Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  yangzhao
 * 修改时间:  2011-4-29
 * 修改内容:  <修改内容>
 */

package com.szxys.mhub.ui.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.szxys.mhub.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;

/**
 * ListViewActivity ； <功能详细描述>
 * 
 * @author yangzhao
 * @version [版本号V01, 2011-4-29]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ListViewActivity extends ListActivity implements OnScrollListener
{
    
    protected static final String TAG = "ListViewActivity";
    
    private ListView mListView;// 操作ListView
    
    private LinearLayout mLoadLayout;// 生成进度条的layout
    
    private List<? extends Map<String, ?>> mData0;// listItem对应布局所显示的内容
    
    private List<? extends Map<String, ?>> mData1;// listItem对应布局所显示的内容
    
    private List<? extends Map<String, ?>> mData2;// listItem对应布局所显示的内容
    
    private ListViewAdapter mListViewAdapter = null;
    
    private int mLastItem = 0;// 最后一个item位置，用来计数，到最后一个item动态加载
    
    private int mCount = 120;// ListViewItem动态加载item总条数
    
    private final int loadCount = 7;// 每次加载的Item条数
    
    private final Handler mHandler = new Handler();// 动态加载操作Handler
    
    /*
     * 进度条布局参数
     */
    private final LayoutParams mProgressBarLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    
    private final LayoutParams mTipContentLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    
    private boolean ismoved = true;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
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
         * 向"加载项"布局中添加一个圆型进度条。
         */
        ProgressBar mProgressBar = new ProgressBar(this);
        mProgressBar.setPadding(0, 0, 15, 0);
        mLoadLayout.addView(mProgressBar, mProgressBarLayoutParams);
        /*
         * 向"加载项"布局中添加提示信息。
         */
        TextView mTipContent = new TextView(this);
        mTipContent.setText("加载中...");
        mLoadLayout.addView(mTipContent, mTipContentLayoutParams);
        /*
         * 获取ListView组件，并将"加载项"布局添加到ListView组件的Footer中。
         */
        mListView = getListView();
        mListView.addFooterView(mLoadLayout);
        /*
         * 组ListView组件设置Adapter,并设置滑动监听事件。
         */
        Map<String, Object>[] maps = testData();
        Map<String, Object>[] maps1 = testData1();
        mData0 = GetMaps.getData(maps);
        mData1 = GetMaps.getData(maps1);
        mData2 = GetMaps.getData(maps1);
        mCount = mData0.size() + mData1.size() + mData2.size();
        /*
         * 设置layout :mlistview 中需要显示的控件
         */
        int[] ids = new int[] {R.id.img1, R.id.tv1, R.id.tv2, R.id.tv3, R.id.imbtn1};// imbtn1去掉，则不显示
        int[] ids1 = new int[] {R.id.tv11, R.id.tv12, R.id.tv13, R.id.tv14};// imbtn1去掉，则不显示
        String[] from1 = new String[] {"tv11", "tv12", "tv13", "tv14"};
        String[] from = new String[] {"img1", "tv1", "tv2", "tv3", "imbtn1"};
        /*
         * layout :mlistview的适配器 ，内容为mlistview 中所包含的控件
         */
        mListViewAdapter = new ListViewAdapter(this, mData0, mData1, mData2, new int[] {R.layout.pf_listview_item,
                R.layout.pf_table_item, R.layout.pf_table_item}, new String[][] {from, from1, from1}, new int[][] {ids,
                ids1, ids1});
        // mListViewAdapter = new ListViewAdpter(this, mData, R.layout.mlistview, new
        // String[]{"img1","tv1","tv2","tv3","imbtn1"},ids);
        setListAdapter(mListViewAdapter);
        mListView.setOnScrollListener(this);// 添加监听事件
    }
    
    @Override
    public void onScroll(AbsListView view, int mFirstVisibleItem, int mVisibleItemCount, int mTotalItemCount)
    {
        mLastItem = mFirstVisibleItem + mVisibleItemCount - 1;
        if (mListViewAdapter.getCount() > mCount - 1)
        {
            mListView.removeFooterView(mLoadLayout);// 删除加载FooterView,进度条
        }
    }
    
    @Override
    public void onScrollStateChanged(AbsListView view, int mScrollState)
    {
        /**
         * 当ListView滑动到最后一条记录时这时，我们会看到已经被添加到ListView的"加载项"布局， 这时应该加载剩余数据。
         */
        if (mLastItem == mListViewAdapter.getCount() && mScrollState == OnScrollListener.SCROLL_STATE_IDLE && ismoved)
        {
            if (mListViewAdapter.getCount() < mCount)
            {
                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mListViewAdapter.setLoadCount(loadCount);// 设置新加载的item数
                        mListViewAdapter.addListItem(// 增加新的数据
                        (ArrayList<HashMap<String, Object>>)mData0,
                                (ArrayList<HashMap<String, Object>>)mData1,
                                (ArrayList<HashMap<String, Object>>)mData2);
                        mListViewAdapter.notifyDataSetChanged();// 更新adapter
                        mListView.setSelection(mLastItem);
                    }
                }, 1);
                
            }
        }
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        System.out.println(position);
        if (position % 3 == 0)// 3代表3种布局，布局是成组形式布局的，3个一组
        {
            TextView tv1 = (TextView)v.findViewById(R.id.tv1);// 对应第一种布局中的ID
            tv1.setText("i am ok");
        }
        
        if (position % 3 == 1)
        {
            TextView tv = (TextView)v.findViewById(R.id.tv11);// 对应第二种布局中的ID
            tv.setText("zzzzzzzz");
        }
        if (position % 3 == 2)
        {
            TextView tv = (TextView)v.findViewById(R.id.tv13);// 对应第三种布局中的ID
            tv.setText("hhhhhhhh");
        }
    }
    
    // 测试数据
    public HashMap<String, Object>[] testData()
    {
        
        String[] key1 = new String[] {"img1", "tv1", "tv2", "tv3", "imbtn1"};
        String[] key2 = new String[] {"img1", "tv1", "tv3", "imbtn1"};
        String[] key3 = new String[] {"img1", "tv1", "tv2"};
        String[] key4 = new String[] {"img1", "tv1"};
        Object[] id1 = new Object[] {android.R.drawable.ic_dialog_alert, "G1", "test 1", "google 1", android.R.drawable.ic_dialog_map};
        Object[] id2 = new Object[] {android.R.drawable.ic_dialog_dialer, "G2", "google 2", android.R.drawable.ic_dialog_map};
        Object[] id3 = new Object[] {android.R.drawable.ic_dialog_email, "G3", "test 3"};
        Object[] id4 = new Object[] {android.R.drawable.ic_dialog_info, "G3"};
        HashMap<String, Object> map1 = GetMaps.getMap(key1, id1);
        HashMap<String, Object> map2 = GetMaps.getMap(key2, id2);
        HashMap<String, Object> map3 = GetMaps.getMap(key3, id3);
        HashMap<String, Object> map4 = GetMaps.getMap(key4, id4);
        @SuppressWarnings("unchecked")
        HashMap<String, Object>[] maps = new HashMap[] {map1, map2, map3, map4, map1, map2, map3, map4, map1, map2,
                map3, map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                map1, map2, map3, map4, map1, map2, map3, map4, map1, map2, map3, map4};
        return maps;
    }
    
    // 测试数据
    public HashMap<String, Object>[] testData1()
    {
        
        String[] key1 = new String[] {"tv11", "tv12", "tv13", "tv14"};
        String[] key2 = new String[] {"tv11", "tv12", "tv13"};
        String[] key3 = new String[] {"tv11", "tv12"};
        String[] key4 = new String[] {"tv11"};
        Object[] id1 = new Object[] {"yang1", "zhao1", "text", "1"};
        Object[] id2 = new Object[] {"yang2", "zhao2", "text"};
        Object[] id3 = new Object[] {"yang3", "zhao3"};
        Object[] id4 = new Object[] {"yang4"};
        HashMap<String, Object> map1 = GetMaps.getMap(key1, id1);
        HashMap<String, Object> map2 = GetMaps.getMap(key2, id2);
        HashMap<String, Object> map3 = GetMaps.getMap(key3, id3);
        HashMap<String, Object> map4 = GetMaps.getMap(key4, id4);
        @SuppressWarnings("unchecked")
        HashMap<String, Object>[] maps = new HashMap[] {map1, map2, map3, map4, map1, map2, map3, map4, map1, map2,
                map3, map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                // map1,map2,map3,map4,map1,map2,map3,map4,map1,map2,map3,map4,
                map1, map2, map3, map4, map1, map2, map3, map4, map1, map2, map3, map4};
        return maps;
    }
}
