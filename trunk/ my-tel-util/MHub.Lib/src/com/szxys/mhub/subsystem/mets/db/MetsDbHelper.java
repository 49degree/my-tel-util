package com.szxys.mhub.subsystem.mets.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MetsDbHelper extends SQLiteOpenHelper{
	protected SQLiteDatabase mDb = null;
	private static final String DATABASE_NAME = "mets.db";
	private static int DATABASE_VERSION = 1;
	public MetsDbHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}
	public MetsDbHelper(Context context, String name,int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	public MetsDbHelper(Context context, int version) {
		this(context, DATABASE_NAME,version);
	}
	public MetsDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			for(int count=0;count<MetsData.SqliteObj.length;count++){
				db.execSQL(MetsData.SqliteObj[count]);	
				Log.d("DbHelper created ", "table["+count+"]");
			}
		} catch (Exception e) {
			Log.d("DbHelper Error:", "Failed to create table");
		}finally {
			if (mDb!=null && mDb.isOpen()) {
				mDb.close();
				Log.e("Sqlite", "DB closed.@onCreate");
			}
		}		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL("DROP TABLE IF EXISTS ??");
        onCreate(db);
	}
	
	public Cursor Select(String table, String[] columns, String selection,String[] selectionArgs, String groupBy, String having,String orderBy) {
		Cursor cursor=null;		
		try {
			this.close();
			mDb = this.getReadableDatabase();
			cursor = mDb.query(table, columns, selection, selectionArgs,groupBy, having, orderBy);
		} catch (Exception e) {
			Log.e("Sqlite", "occured error @Select cursor from "+table);
		}		
		return cursor;
	}
	public long Add(String table, ContentValues values) {
		long row =0;
		try {
			this.close();
			mDb = this.getWritableDatabase();		
			row = mDb.insert(table, null, values);
			Log.d("DbHelper", table+" INSERT "+row);
		} catch (Exception e) {
			Log.e("Sqlite", "occured error @insert into "+table);
		}finally {
			if (mDb!=null && mDb.isOpen()) {
				mDb.close();
				Log.e("Sqlite", "DB closed.@Add");
			}
		}		
		return row;
	}
	public long getTableCount(String table,String where,String[] whereArgs,String orderBy) {
		long count=0;
		String[] columns={"*"};
		Cursor cursor=null;
		try {
			cursor =Select(table, columns, where, whereArgs, null, null, orderBy);
			if (cursor!=null && cursor.getCount()>0) {
				count=cursor.getCount();
			}
		} catch (Exception e) {
			Log.e("Sqlite", "occured error @getTable count from "+table);
		}finally {
			if (cursor!=null && !cursor.isClosed()) {
				cursor.close();
			}
			if (mDb!=null && mDb.isOpen()) {
				mDb.close();
				Log.e("Sqlite", "DB closed.@getTableCount");
			}
		}		
		return count;
	}
	public int insertBySQL(String table,String sql) {
		int result=0;
		try {
			this.close();
			mDb=this.getWritableDatabase();
			mDb.execSQL(sql);
			result=1;
		} catch (Exception e) {
			// TODO: handle exception
			result=-1;
			Log.e("Sqlite Error ", "insertBySQL");
		}finally {
			if (mDb!=null && mDb.isOpen()) {
				mDb.close();
				Log.e("Sqlite", "DB closed.@insertBySQL");
			}
		}		
		return result;
	}
	public int Update(String table, ContentValues values,String where,String[] whereArgs) {
		int row=0;
		try {
			this.close();
			mDb = this.getWritableDatabase();
			row=mDb.update(table, values, where, whereArgs);
		} catch (Exception e) {
			Log.e("Sqlite", "occured error @Update from "+table);
		}finally {
			if (mDb!=null && mDb.isOpen()) {
				mDb.close();
				Log.e("Sqlite", "DB closed.@Update");
			}
		}		
		return row;
	}
	public int Delete(String table, String where, String[] whereArgs) {
		int row=0;
		try {
			this.close();
			mDb = this.getWritableDatabase();
			row=mDb.delete(table, where, whereArgs);
		} catch (Exception e) {
			Log.e("Sqlite", "occured error @delete from "+table);
		}finally {
			if (mDb!=null && mDb.isOpen()) {
				mDb.close();
				Log.e("Sqlite", "DB closed.@Delete");
			}
		}		
		return row;
	}
	@Override
    protected void finalize() {
		if (mDb!=null && mDb.isOpen()) {
			mDb.close();
			Log.e("Sqlite", "DB closed.@finalize");
		}
    }
	public void close() {
		if (mDb!=null && mDb.isOpen()) {
			mDb.close();
			//Log.e("Sqlite", "DB closed.");
		}
	}

}
