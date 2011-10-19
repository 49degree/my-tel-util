
 /*
 * 文 件 名:  GetMaps.java
 * 版    权:  New Element Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  yangzhao
 * 修改时间:  2011-4-28
 * 修改内容:  <修改内容>
 */
 
package com.szxys.mhub.ui.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


 /**
 * MListViewAdapter所需map转换类 ；
 * <功能详细描述>
 * 
 * @author  yangzhao
 * @version  [版本号V01, 2011-4-28]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */

public class GetMaps
{
    /**
     * 把键值对转换成HashMap ；
     * <功能详细描述>
     * @param key :String[] 布局文件中的id名
     * @param id ：Object[] 需要显示的资源
     * @return HashMap<String, Object>
     * @see [类、类#方法、类#成员]
     */
    public static HashMap<String, Object> getMap(String[] key ,Object[] id)
    {
        
        HashMap<String, Object> map = new HashMap<String, Object>();
        int length = id.length;
        if(length == key.length)
        for(int i =0;i<length;i++){
          map.put(key[i], id[i]);
        }

        
        return map;
    }
    /**
     * 把maps添加到list并返回list ；
     * <功能详细描述>
     * @param map :Map<String, Object>[] item显示数据map
     * @return List<Map<String, Object>>
     * @see [类、类#方法、类#成员]
     */
    public static List<Map<String, Object>> getData(Map<String, Object>[] map)
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int length = map.length;
        for(int i =0;i<length;i++){
            list.add(map[i]);
        }
        return list;
    }
}
