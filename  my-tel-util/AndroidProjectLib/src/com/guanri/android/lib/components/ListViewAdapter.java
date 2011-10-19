package com.guanri.android.lib.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.BaseAdapter;
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
 * ListView所需的适配器类 ； 用于listView.setadpter()
 */
public class ListViewAdapter extends BaseAdapter
{
    private static final boolean debug = false;
    
    private LayoutInflater mInflater;// 动态够构造Layout的Inflater
    
    private ArrayList<HashMap<String, Object>> listItems0;// 显示内容
    
    private ArrayList<HashMap<String, Object>> listItems1;// 显示内容
    
    private ArrayList<HashMap<String, Object>> listItems2;// 显示内容
    
    private ArrayList<HashMap<String, Object>> listItem;// 组合显示内容
    
    private int[] resource; // 资源layout ：ID
    
    private String[][] from;// 资源key值，对应资源ID
    
    private int[][] id;// 资源view ：ID
    
    private int selectedId = -1;// 选中的itemID
    
    private int[] counts; // 默认显示Item条数
    
    private int loadCount = 7;// 动态加载的Item条数
    
    public final static int LOADTYPE_FIXED = 0;// Item加载模式：定长模式
    
    public final static int LOADTYPE_ADD = 1;// Item加载模式：追加模式
    
    public final static int LIST0 = 0;// 初始化listindex，指listItems0
    
    public final static int LIST1 = 1;// 初始化listindex，指listItems1
    
    public final static int LIST2 = 2;// 初始化listindex，指listItems2
    
    private ViewHolder holder;// 显示内容构造类
    
    private int itemcount;// item总数
    
    private int groupcount;// 一组显示几个item
    
    private int[] listResource;
    
    private int[][] listId;
    
    private String[][] listFrom;
    
    /**
     * 构造listView的Item为一种layout
     */
    @SuppressWarnings("unchecked")
    public ListViewAdapter(Context context, List<? extends Map<String, ?>> listItems0, int resource, String[] from,
            int[] id)
    {
        this.mInflater = LayoutInflater.from(context);
        this.resource = new int[1];
        this.id = new int[1][];
        this.resource[0] = resource;
        this.id[0] = id;
        this.from = new String[1][];
        this.from[0] = from;
        this.listItems0 = (ArrayList<HashMap<String, Object>>)listItems0;
        this.counts = new int[1];
        counts[0] = listItems0.size();
        if (counts[0] < loadCount)
        {
            loadCount = counts[0];
        }
        itemcount = counts[0];
        groupcount = 1;
        getData();
    }
    
    /**
     * 构造listView的Item为两种layout
     */
    @SuppressWarnings("unchecked")
    public ListViewAdapter(Context context, List<? extends Map<String, ?>> listItems0,
            List<? extends Map<String, ?>> listItems1, int[] resource, String[][] from, int[][] id)
    {
        this.mInflater = LayoutInflater.from(context);
        this.resource = resource;
        this.id = id;
        this.from = from;
        this.listItems0 = (ArrayList<HashMap<String, Object>>)listItems0;
        this.listItems1 = (ArrayList<HashMap<String, Object>>)listItems1;
        this.counts = new int[2];
        counts[0] = listItems0.size();
        counts[1] = listItems1.size();
        
        if ((counts[0] + counts[1]) < loadCount)
        {
            loadCount = counts[0] + counts[1];
        }
        
        itemcount = counts[0] + counts[1];
        groupcount = 2;
        getData();
    }
    
    /**
     * 构造listView的Item为三种layout
     */
    @SuppressWarnings("unchecked")
    public ListViewAdapter(Context context, List<? extends Map<String, ?>> listItems0,
            List<? extends Map<String, ?>> listItems1, List<? extends Map<String, ?>> listItems2, int[] resource,
            String[][] from, int[][] id)
    {
        this.mInflater = LayoutInflater.from(context);
        this.resource = resource;
        this.id = id;
        this.from = from;
        this.listItems0 = (ArrayList<HashMap<String, Object>>)listItems0;
        this.listItems1 = (ArrayList<HashMap<String, Object>>)listItems1;
        this.listItems2 = (ArrayList<HashMap<String, Object>>)listItems2;
        this.counts = new int[3];
        counts[0] = listItems0.size();
        counts[1] = listItems1.size();
        counts[2] = listItems2.size();
        
        if (counts[0] + counts[1] + counts[2] < loadCount)
        {
            loadCount = counts[0] + counts[1] + counts[2];
        }
        itemcount = counts[0] + counts[1] + counts[2];
        groupcount = 3;
        getData();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = null;
        // if (convertView == null)
        {
            v = newLayoutView(position, null);
            v.setTag(listResource[position]);
        }
        
        // else
        // {
        // v=convertView;
        // }
        holder = new ViewHolder();
        initView(v, listItem.get(position), listFrom[position], listId[position]);
        return v;
    }
    
    @Override
    public int getCount()
    {
        return loadCount;
    }
    
    /**
     * @param position
     * @return
     */
    
    @Override
    public Object getItem(int position)
    {
        
        return listItem.get(position);
    }
    
    /**
     * @param position
     * @return
     */
    
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    /**
     * 
     * ListItem构造类 ； <功能详细描述>
     * 
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
    
    /**
     * 增加需要显示的listitem ； 增加3种布局文件一组的listitem，数据顺序与构造函数一致
     * 
     * @param maps0 ：ArrayList<HashMap<String, Object>>
     * @param maps1 ：ArrayList<HashMap<String, Object>>
     * @param maps2 ：ArrayList<HashMap<String, Object>>
     * @see [类、类#方法、类#成员]
     */
    public void addListItem(ArrayList<HashMap<String, Object>> maps0, ArrayList<HashMap<String, Object>> maps1,
            ArrayList<HashMap<String, Object>> maps2)
    {
        int length0 = 0;
        int length1 = 0;
        int length2 = 0;
        if (maps0 != null)
        {
            length0 = maps0.size();
        }
        if (maps1 != null)
        {
            length1 = maps1.size();
        }
        if (maps2 != null)
        {
            length2 = maps2.size();
        }
        if (length0 == length1 && length0 == length2 && maps0 != null)
        {
            int k0 = 0;
            int k1 = 0;
            int k2 = 0;
            int newlength = itemcount + length0 + length1 + length2;
            int[] ltres = new int[newlength];
            int[][] ltid = new int[newlength][];
            String[][] ltfrom = new String[newlength][];
            System.arraycopy(listResource, 0, ltres, 0, itemcount);
            System.arraycopy(listId, 0, ltid, 0, itemcount);
            System.arraycopy(listFrom, 0, ltfrom, 0, itemcount);
            for (int i = itemcount; i < newlength; i++)
            {
                if (i % 3 == 0)
                {
                    listItem.add(maps0.get(k0));
                    ltres[i] = resource[LIST0];
                    ltid[i] = id[LIST0];
                    ltfrom[i] = from[LIST0];
                    k0++;
                }
                else if (i % 3 == 1)
                {
                    listItem.add(maps1.get(k1));
                    ltres[i] = resource[LIST1];
                    ltid[i] = id[LIST1];
                    ltfrom[i] = from[LIST1];
                    k1++;
                }
                else
                {
                    listItem.add(maps2.get(k2));
                    ltres[i] = resource[LIST2];
                    ltid[i] = id[LIST2];
                    ltfrom[i] = from[LIST2];
                    k2++;
                }
            }
            listResource = ltres;
            listId = ltid;
            listFrom = ltfrom;
            itemcount = newlength;
        }
    }
    
    /**
     * 增加需要显示的listitem ； 增加2种布局文件一组的listitem，数据顺序与构造函数一致
     * 
     * @param maps0 ：ArrayList<HashMap<String, Object>>
     * @param maps1 ：ArrayList<HashMap<String, Object>>
     * @see [类、类#方法、类#成员]
     */
    public void addListItem(ArrayList<HashMap<String, Object>> maps0, ArrayList<HashMap<String, Object>> maps1)
    {
        int length0 = 0;
        int length1 = 0;
        if (maps0 != null)
        {
            length0 = maps0.size();
        }
        if (maps1 != null)
        {
            length1 = maps1.size();
        }
        if (length0 == length1 && maps0 != null)
        {
            int k0 = 0;
            int k1 = 0;
            int newlength = itemcount + length0 + length1;
            int[] ltres = new int[newlength];
            int[][] ltid = new int[newlength][];
            String[][] ltfrom = new String[newlength][];
            System.arraycopy(listResource, 0, ltres, 0, itemcount);
            System.arraycopy(listId, 0, ltid, 0, itemcount);
            System.arraycopy(listFrom, 0, ltfrom, 0, itemcount);
            for (int i = itemcount; i < newlength; i++)
            {
                if (i % 2 == 0)
                {
                    listItem.add(maps0.get(k0));
                    ltres[i] = resource[0];
                    ltid[i] = id[0];
                    ltfrom[i] = from[0];
                    k0++;
                }
                else
                {
                    listItem.add(maps1.get(k1));
                    ltres[i] = resource[1];
                    ltid[i] = id[1];
                    ltfrom[i] = from[1];
                    k1++;
                }
            }
            listResource = ltres;
            listId = ltid;
            listFrom = ltfrom;
            itemcount = newlength;
        }
        
    }
    
    /**
     * 增加需要显示的listitem ； 增加1种布局文件的listitem，数据顺序与构造函数一致
     * 
     * @param maps0 :ArrayList<HashMap<String, Object>>
     * @see [类、类#方法、类#成员]
     */
    public void addListItem(ArrayList<HashMap<String, Object>> maps0)
    {
        int length0 = 0;
        if (maps0 != null)
        {
            length0 = maps0.size();
            int newlength = itemcount + length0;
            int[] ltres = new int[newlength];
            int[][] ltid = new int[newlength][];
            String[][] ltfrom = new String[newlength][];
            System.arraycopy(listResource, 0, ltres, 0, itemcount);
            System.arraycopy(listId, 0, ltid, 0, itemcount);
            System.arraycopy(listFrom, 0, ltfrom, 0, itemcount);
            for (int i = itemcount; i < itemcount + length0; i++)
            {
                listItem.add(maps0.get(i - itemcount));
                ltres[i] = resource[0];
                ltid[i] = id[0];
                ltfrom[i] = from[0];
            }
            listResource = ltres;
            listId = ltid;
            listFrom = ltfrom;
            itemcount = newlength;
        }
        
    }
    
    public int getSelectedId()
    {
        return selectedId;
    }
    
    public void setSelectedId(int selectedId)
    {
        this.selectedId = selectedId;
    }
    
    /**
     * 取得当前已经加载的item数 ； <功能详细描述>
     * 
     * @return int
     * @see [类、类#方法、类#成员]
     */
    public int getLoadCount()
    {
        return loadCount;
    }
    
    /**
     * 修改listview显示的item条数 ； <功能详细描述>
     * 
     * @param n ：int 新加载的item组数
     * @see [类、类#方法、类#成员]
     */
    public void setLoadCount(int n)
    {
        System.out.println("itemcount:" + itemcount);
        if (loadCount + groupcount * n < itemcount)
        {
            this.loadCount += groupcount * n;
            System.out.println("loadCount:" + loadCount);
        }
        else
        {
            this.loadCount = itemcount;
            System.out.println("loadCount:" + loadCount);
        }
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
                if (debug)
                {
                    System.out.println(k);
                }
            }
            if (v instanceof TextView && k == 1)
            {
                if (debug)
                {
                    System.out.println("TextView:" + From[i]);
                }
                holder.tv1 = (TextView)v.findViewById(To[i]);
                holder.tv1.setText((String)data.get(From[i]));
            }
            else if (v instanceof TextView && k == 2)
            {
                if (debug)
                {
                    System.out.println("TextView:" + From[i]);
                }
                holder.tv2 = (TextView)v.findViewById(To[i]);
                holder.tv2.setText((String)data.get(From[i]));
            }
            else if (v instanceof TextView && k == 3)
            {
                if (debug)
                {
                    System.out.println("TextView:" + From[i]);
                }
                holder.tv3 = (TextView)v.findViewById(To[i]);
                holder.tv3.setText((String)data.get(From[i]));
            }
            else if (v instanceof TextView && k == 4)
            {
                if (debug)
                {
                    System.out.println("TextView:" + From[i]);
                }
                holder.tv4 = (TextView)v.findViewById(To[i]);
                holder.tv4.setText((String)data.get(From[i]));
            }
            else if (v instanceof ImageView)
            {
                if (debug)
                {
                    System.out.println("ImageView:" + From[i]);
                }
                holder.img1 = (ImageView)v.findViewById(To[i]);
                if (data.get(From[i]) != null)
                {
                    holder.img1.setBackgroundResource((Integer)data.get(From[i]));
                }
            }
            else if (v instanceof DigitalClock)
            {
                if (debug)
                {
                    System.out.println("DigitalClock:" + From[i]);
                }
                holder.dgtclk1 = (DigitalClock)v.findViewById(To[i]);
                if (data.get(From[i]) != null)
                {
                    holder.dgtclk1.setBackgroundResource((Integer)data.get(From[i]));
                }
                holder.dgtclk1.setFocusableInTouchMode(false);
                holder.dgtclk1.setFocusable(false);
            }
            if (v instanceof Button)
            {
                if (debug)
                {
                    System.out.println("Button:" + From[i]);
                }
                holder.btn1 = (Button)v.findViewById(To[i]);
                if (data.get(From[i]) != null)
                {
                    holder.btn1.setText((String)data.get(From[i]));
                }
                holder.btn1.setFocusableInTouchMode(false);
                holder.btn1.setFocusable(false);
            }
            if (v instanceof Spinner)
            {
                if (debug)
                {
                    System.out.println("Spinner:" + From[i]);
                }
                holder.spn1 = (Spinner)v.findViewById(To[i]);
                if (data.get(From[i]) != null)
                {
                    holder.spn1.setAdapter((SimpleAdapter)data.get(From[i]));
                }
                holder.spn1.setFocusableInTouchMode(false);
                holder.spn1.setFocusable(false);
            }
            if (v instanceof CheckBox)
            {
                if (debug)
                {
                    System.out.println("CheckBox:" + From[i]);
                }
                holder.cbx1 = (CheckBox)v.findViewById(To[i]);
                if (data.get(From[i]) != null)
                {
                    holder.cbx1.setText((String)data.get(From[i]));
                }
                holder.cbx1.setFocusableInTouchMode(false);
                holder.cbx1.setFocusable(false);
            }
            if (v instanceof ImageButton)
            {
                if (debug)
                {
                    System.out.println("ImageButton:" + From[i]);
                }
                holder.imbtn1 = (ImageButton)v.findViewById(To[i]);
                if (data.get(From[i]) != null)
                {
                    holder.imbtn1.setBackgroundResource((Integer)data.get(From[i]));
                }
                holder.imbtn1.setFocusableInTouchMode(false);
                holder.imbtn1.setFocusable(false);
            }
            if (v instanceof RadioButton)
            {
                if (debug)
                {
                    System.out.println("RadioButton:" + From[i]);
                }
                holder.rdbtn1 = (RadioButton)v.findViewById(To[i]);
                if (data.get(From[i]) != null)
                {
                    holder.rdbtn1.setBackgroundResource((Integer)data.get(From[i]));
                }
                holder.rdbtn1.setFocusableInTouchMode(false);
                holder.rdbtn1.setFocusable(false);
            }
            if (v instanceof AnalogClock)
            {
                if (debug)
                {
                    System.out.println("AnalogClock:" + From[i]);
                }
                holder.alclk1 = (AnalogClock)v.findViewById(To[i]);
                if (data.get(From[i]) != null)
                {
                    holder.alclk1.setBackgroundResource((Integer)data.get(From[i]));
                }
            }
            if (v instanceof ToggleButton)
            {
                if (debug)
                {
                    System.out.println("ToggleButton:" + From[i]);
                }
                
                holder.tgbtn1 = (ToggleButton)v.findViewById(To[i]);
                if (data.get(From[i]) != null)
                {
                    holder.tgbtn1.setChecked((Boolean)data.get(From[i]));
                }
                holder.tgbtn1.setFocusableInTouchMode(false);
                holder.tgbtn1.setFocusable(false);
            }
            
        }
        
    }
    
    /**
     * 构造item的layout ； <功能详细描述>
     * 
     * @param position
     * @param parent
     * @return
     * @see [类、类#方法、类#成员]
     */
    private View newLayoutView(int position, ViewGroup parent)
    {
        return mInflater.inflate(listResource[position], parent, false);
    }
    
    /**
     * 初始化显示数据 ； 组合三种布局数据
     * 
     * @see [类、类#方法、类#成员]
     */
    private void getData()
    {
        listItem = new ArrayList<HashMap<String, Object>>();
        listResource = new int[itemcount];
        listId = new int[itemcount][];
        listFrom = new String[itemcount][];
        if (groupcount == 1)
        {
            listItem = listItems0;
            for (int i = 0; i < itemcount; i++)
            {
                listResource[i] = resource[0];
                listId[i] = id[0];
                listFrom[i] = from[0];
            }
        }
        else if (groupcount == 2)
        {
            
            int k1 = 0;
            int k2 = 0;
            for (int i = 0; i < itemcount; i++)
            {
                if (i % 2 == 0)
                {
                    listItem.add(listItems0.get(k1));
                    listResource[i] = resource[0];
                    listId[i] = id[0];
                    listFrom[i] = from[0];
                    k1++;
                }
                else
                {
                    listItem.add(listItems1.get(k2));
                    listResource[i] = resource[1];
                    listId[i] = id[1];
                    listFrom[i] = from[1];
                    k2++;
                }
                
            }
        }
        else if (groupcount == 3)
        {
            int k0 = 0;
            int k1 = 0;
            int k2 = 0;
            for (int i = 0; i < itemcount; i++)
            {
                if (i % 3 == 0)
                {
                    listItem.add(listItems0.get(k0));
                    listResource[i] = resource[LIST0];
                    listId[i] = id[LIST0];
                    listFrom[i] = from[LIST0];
                    k0++;
                }
                else if (i % 3 == 1)
                {
                    listItem.add(listItems1.get(k1));
                    listResource[i] = resource[LIST1];
                    listId[i] = id[LIST1];
                    listFrom[i] = from[LIST1];
                    k1++;
                }
                else
                {
                    listItem.add(listItems2.get(k2));
                    listResource[i] = resource[LIST2];
                    listId[i] = id[LIST2];
                    listFrom[i] = from[LIST2];
                    k2++;
                }
            }
        }
    }
}
