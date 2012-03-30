package com.custom.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.custom.provider.NotePad.Notes;

public class NotePadProvider extends ContentProvider {   
  
    public static final String DATABASE_NAME = "test.db";   
    public static final String TABLE_NAME = "notes";   
    public static final int VERSION = 1;   
    public static final int NOTES = 1;   
    public static final int NOTE_ID = 2;   
    public static HashMap<String, String> hm = null;   
    public static UriMatcher mUriMatcher = null;   
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME   
            + " (" + Notes._ID + " INTEGER PRIMARY KEY," + Notes.TITLE   
            + " TEXT," + Notes.NOTE + " TEXT)";   
    private SQLiteDataHelper msdh = null;   
    static {   
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);   
        mUriMatcher.addURI(NotePad.AUTHORITY, "notes", NOTES);   
        mUriMatcher.addURI(NotePad.AUTHORITY, "notes/#", NOTE_ID);
        hm = new HashMap<String, String>();   
        hm.put(Notes._ID, Notes._ID);   
        hm.put(Notes.TITLE, Notes.TITLE);   
        hm.put(Notes.NOTE, Notes.NOTE);  
    }   
  
    private static class SQLiteDataHelper extends SQLiteOpenHelper {   
  
        public SQLiteDataHelper(Context context, String name,   
                CursorFactory factory, int version) {   
            super(context, name, factory, version);   
        }   
  
        @Override  
        public void onCreate(SQLiteDatabase db) {   
            db.execSQL(CREATE_TABLE);   
  
        }   
  
        @Override  
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {   
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);   
            this.onCreate(db);   
        }   
  
    }   
  
    /**  
     * 删除  
     */  
    @Override  
    public int delete(Uri uri, String selection, String[] selectionArgs) {   
        SQLiteDatabase db = msdh.getWritableDatabase();   
        int num = 0;   
        switch (mUriMatcher.match(uri)) {   
        case NOTES:   
            num = db.delete(TABLE_NAME, selection, selectionArgs);   
            break;   
        case NOTE_ID:   
            num = db.delete(TABLE_NAME, Notes._ID   
                    + " = "  
                    + uri.getPathSegments().get(1)   
                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection   
                            + ')' : ""), selectionArgs);   
            break;   
        default:   
            break;   
        }   
        this.getContext().getContentResolver().notifyChange(uri, null);   
        return num;   
    }   
  
    @Override  
    public String getType(Uri uri) {   
        String str = "";   
        switch (mUriMatcher.match(uri)) {   
        case NOTES:   
            str = Notes.CONTENT_TYPE;   
            break;   
        case NOTE_ID:   
            str = Notes.CONTENT_ITEM_TYPE;   
            break;   
        default:   
            throw new IllegalArgumentException("Unknown URI " + uri);   
        }   
        return str;   
    }   
  
    /**  
     * 插入  
     */  
    @Override  
    public Uri insert(Uri uri, ContentValues values) {   
        if (mUriMatcher.match(uri) != NOTES) {   
            throw new IllegalArgumentException("Unknown URI " + uri);   
        }   
        ContentValues cv = null;   
        if (values == null) {   
            cv = new ContentValues();   
        } else {   
            cv = new ContentValues(values);   
        }   
        long num = System.currentTimeMillis();   
  
        if (cv.containsKey(Notes.TITLE) == false) {   
            Resources r = Resources.getSystem();   
            cv.put(Notes.TITLE, r.getString(android.R.string.untitled));   
        }   
        if (cv.containsKey(Notes.NOTE) == false) {   
            cv.put(Notes.NOTE, "");   
        }   
        SQLiteDatabase db = msdh.getWritableDatabase();   
        long id = db.insertOrThrow(TABLE_NAME, Notes.NOTE, cv);   
        if (id > 0) {   
            Uri uri_new = ContentUris.withAppendedId(uri, id);   
            this.getContext().getContentResolver().notifyChange(uri_new, null);   
            return uri_new;   
        }   
        return null;   
    }   
  
    @Override  
    public boolean onCreate() {   
        msdh = new SQLiteDataHelper(this.getContext(), TABLE_NAME, null,   
                VERSION);   
        return true;   
    }   
  
    /**  
     * 查询  
     */  
    @Override  
    public Cursor query(Uri uri, String[] projection, String selection,   
            String[] selectionArgs, String sortOrder) {   
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();   
        switch (mUriMatcher.match(uri)) {   
        case NOTES:   
            qb.setTables(TABLE_NAME);   
            qb.setProjectionMap(hm);   
            break;   
        case NOTE_ID:   
            qb.setTables(TABLE_NAME);   
            qb.setProjectionMap(hm);   
            qb.appendWhere(Notes._ID + " = " + uri.getPathSegments().get(1));   
            break;   
        default:   
            throw new IllegalArgumentException("Unknown URI " + uri);   
        }   
        String orderBy = "";   
        if (TextUtils.isEmpty(sortOrder)) {   
            orderBy = Notes.DEFAULT_SORT_ORDER;   
        } else {   
            orderBy = sortOrder;   
        }   
        SQLiteDatabase db = msdh.getReadableDatabase();   
        Cursor cursor = qb.query(db, projection, selection, selectionArgs,   
                null, null, orderBy);   
        cursor.setNotificationUri(this.getContext().getContentResolver(), uri);   
        return cursor;   
    }   
  
    /**  
     * 更新  
     */  
    @Override  
    public int update(Uri uri, ContentValues values, String selection,   
            String[] selectionArgs) {   
        SQLiteDatabase db = msdh.getWritableDatabase();   
        int num = 0;   
        switch (mUriMatcher.match(uri)) {   
        case NOTES:   
            num = db.update(TABLE_NAME, values, selection, selectionArgs);   
            break;   
        case NOTE_ID:   
            num = db.update(TABLE_NAME, values, Notes._ID   
                    + " = "  
                    + uri.getPathSegments().get(1)   
                    + (!TextUtils.isEmpty(selection) ? " and (" + selection   
                            + ")" : ""), selectionArgs);   
        default:   
            break;   
        }   
        this.getContext().getContentResolver().notifyChange(uri, null);   
        return num;   
    }   

}
