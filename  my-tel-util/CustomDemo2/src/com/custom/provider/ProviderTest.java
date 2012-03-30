package com.custom.provider;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.custom.provider.NotePad.Notes;
import com.custom.view.R;

public class ProviderTest extends Activity {
    ListView lv = null;   
    
    @Override  
    public void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.provider);   
        lv = (ListView) this.findViewById(R.id.ListView01);   
        ContentValues cv = new ContentValues();   
        cv.put(Notes.TITLE, "title1");   
        cv.put(Notes.NOTE, "note1");   
        this.getContentResolver().insert(Notes.CONTENT_URI, cv);   
        cv.clear();   
        cv.put(Notes.TITLE, "title2");   
        cv.put(Notes.NOTE, "note2");   
        this.getContentResolver().insert(Notes.CONTENT_URI, cv);   
        this.displayNote();   
    }   
  
    private void displayNote() {   
        String[] columns = new String[] { Notes._ID, Notes.TITLE, Notes.NOTE};   
        Cursor c = this.managedQuery(Notes.CONTENT_URI, columns, null, null,null);   
        this.startManagingCursor(c);   
        if (c != null) {   
            int cs = 0;
            if(c.isBeforeFirst()){   
                cs++;   
                this.setTitle("isBeforeFirst"+cs);   
            }   
            if(c.moveToFirst()){   
                cs++;   
                this.setTitle("moveToFirst"+cs);   
            }   
            if(c.isFirst()){   
                cs++;   
                this.setTitle("isFirst"+cs);   
            }   
            ListAdapter adapter = new SimpleCursorAdapter(this,   
                    android.R.layout.simple_list_item_2, c, new String[] {Notes._ID, Notes.TITLE },
                            new int[] {android.R.id.text1, android.R.id.text2 });   
            lv.setAdapter(adapter); 
  
            /*  
             * if (c.moveToFirst()) { this.setTitle(c.getCount()+""); String id =  
             * ""; String title = ""; do { id =  
             * c.getString(c.getColumnIndex(Notes._ID)); title =  
             * c.getString(c.getColumnIndex(Notes.TITLE)); Toast toast =  
             * Toast.makeText(this, c.getPosition()+"|ID:" + id + "|title:" +  
             * title, Toast.LENGTH_LONG); toast.show(); } while  
             * (c.moveToNext());  
             *  }  
             */  
        }   
    }
    
    /** Called when the activity is first created. */ 
//    @Override  
//    public void onCreate(Bundle savedInstanceState) {   
//        super.onCreate(savedInstanceState);   
//        setContentView(R.layout.main);   
//        String uri = "content://com.google.android.provider.notepad/xixi";   
//       String[] columns = new String[] { "_id", "title", "note" };   
//       try {   
//           /*添加数据  
//           ContentValues cv = new ContentValues();  
//           cv.put("title", "new title");  
//           cv.put("note", "new note");  
//           this.getContentResolver().insert(Uri.parse(uri), cv);  
//           */  
//           /*删除数据  
//           int num = this.getContentResolver().delete(Uri.parse(uri), "title='new title'", null);  
//           */  
//           /*修改  
//           ContentValues cv = new ContentValues();  
//           cv.put("title", "old title");  
//           cv.put("note", "old note");  
//           long num = this.getContentResolver().update(Uri.parse(uri), cv, "_id=3", null);  
//           this.setTitle("num="+num);  
//           */  
//           Cursor c = this  
//                   .managedQuery(Uri.parse(uri), null, null, null, null);   
//           // Cursor c = this.getContentResolver().query(Uri.parse(uri),   
//           // columns,   
//           // null, null, null);   
//           if (c == null) {   
//               this.setTitle("c=null");   
//           } else {   
//               this.startManagingCursor(c);   
//               ListView lv = (ListView) this.findViewById(R.id.ListView01);   
//               ListAdapter adapter = new SimpleCursorAdapter(this,   
//                       R.layout.simple, c, columns, new int[] { R.id.ID,   
//                               R.id.title, R.id.note });   
//               lv.setAdapter(adapter);   
//           }   
//       } catch (Exception ex) {   
//           ex.printStackTrace();   
//       }   
//   }   


}
