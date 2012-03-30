package com.custom.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class NotePad {
	   // Content Providers的URI   
	   public static final String AUTHORITY = "com.custom.view.provider.notepad";   
	 
	   private NotePad() {   
	 
	   }   
	   /**  
	    * 定义基本字段  
	    *   
	    * @author Aina_hk  
	    *   
	    */  
	   public static final class Notes implements BaseColumns {   
	       private Notes() {   
	 
	       }   
	       public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY+"/notes");   
	       // 新的MIME类型-多个   
	       public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.note";   
	       // 新的MIME类型-单个   
	       public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.note";   
	       // 默认排序   
	       public static final String DEFAULT_SORT_ORDER = "_ID ASC";   
	       // 字段   
	       public static final String TITLE = "name";   
	       public static final String NOTE = "value";   
//	       public static final String CREATEDDATE = "created";   
//	       public static final String MODIFIEDDATE = "modified";   
	   }   

}
