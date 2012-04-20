package com.tgb.lk.ahibernate.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;
import com.tgb.lk.ahibernate.dao.BaseDao;
import com.tgb.lk.ahibernate.util.TableHelper;

/**
 * AHibernate��Ҫ <br/>
 * (һ)֧�ֹ���: 1.�Զ�����,֧���������Լ̳���:�ɸ���ע���Զ���ɽ���,���Ҷ��ڼ̳����е�ע���ֶ�Ҳ֧���Զ�����. 2.�Զ�֧����ɾ��
 * ,����֧�ֶ��󻯲���:��ɾ�������ݿ�������������Ԫ,�����ظ�д��Щ��ɾ�ĵĴ���,������Ӻ͸���֧��������hibernate�еĶ��󻯲���.
 * 3.��ѯ��ʽ���:֧��android����ṩ�ķ�ʽ,Ҳ֧��ԭ��sql��ʽ.
 * 4.��ѯ�������:���ڲ�ѯ������Զ���װΪʵ�����,������hibernate���.
 * 5.��ѯ������:��ѯ���֧�ֶ���,Ҳ֧�ֽ��ΪList<Map<String,String>>��ʽ,���������ʵ����Ŀ�к�ʵ��,��Ч�ʸ���Щ.
 * 6.��־����ϸ:��Ϊandroid������֧���Ȳ������,���б���ʱ�ɸ�����־����λ����,�������Լ�������Android�Ĵ���. <br/>
 * (��)����֮��: <br/>
 * 1.id��ʱֻ֧��int����,��֧��uuid,��sqlite�в�������uuid.
 * 2.����ÿ���������Լ������͹ر�����,��ʱ����֧����һ�����������������Ȼ��ͳһ�ύ����. <br/>
 * (��)���߼���:<br/>
 * ������JavaScript��Java��չ,����Ҳϣ��AHibernate��Hibernate֮����չ.
 * ϣ�������Ŀ�Ժ���Ϊ��Դ��������ҪһԱ,��ϣ�������Ŀ�ܸ�����Android�����ߴ�����.
 * ��ӭ�����ҵĲ���:http://blog.csdn.net/lk_blog,
 * �����������ܵ�ʹ�÷�����Դ��,ϣ�������Ƕ�ཻ������������,��ͬ�ƶ��й���Դ��ҵ�ķ�չ,AHibernate�ڴ�������������δ��!!!
 */
public class BaseDaoImpl<T> implements BaseDao<T> {
	private String TAG = "AHibernate";
	private SQLiteOpenHelper dbHelper;
	private String tableName;
	private String idColumn;
	private Class<T> clazz;
	private List<Field> allFields;

	public BaseDaoImpl(SQLiteOpenHelper dbHelper) {
		this.dbHelper = dbHelper;

		this.clazz = ((Class<T>) ((java.lang.reflect.ParameterizedType) super
				.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);

		if (this.clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) this.clazz.getAnnotation(Table.class);
			this.tableName = table.name();
		}

		// ���������ֶ�
		this.allFields = TableHelper.joinFields(this.clazz.getDeclaredFields(),
				this.clazz.getSuperclass().getDeclaredFields());

		// �ҵ�����
		for (Field field : this.allFields) {
			if (field.isAnnotationPresent(Id.class)) {
				Column column = (Column) field.getAnnotation(Column.class);
				this.idColumn = column.name();
				break;
			}
		}

		Log.d(TAG, "clazz:" + this.clazz + " tableName:" + this.tableName
				+ " idColumn:" + this.idColumn);
	}

	public SQLiteOpenHelper getDbHelper() {
		return dbHelper;
	}

	public T get(int id) {
		String selection = this.idColumn + " = ?";
		String[] selectionArgs = { Integer.toString(id) };
		Log.d(TAG, "[get]: select * from " + this.tableName + " where "
				+ this.idColumn + " = '" + id + "'");
		List<T> list = find(null, selection, selectionArgs, null, null, null,
				null);
		if ((list != null) && (list.size() > 0)) {
			return (T) list.get(0);
		}
		return null;
	}

	public List<T> rawQuery(String sql, String[] selectionArgs) {
		Log.d(TAG, "[rawQuery]: " + sql);

		List<T> list = new ArrayList<T>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.dbHelper.getReadableDatabase();
			cursor = db.rawQuery(sql, selectionArgs);

			getListFromCursor(list, cursor);
		} catch (Exception e) {
			Log.e(this.TAG, "[rawQuery] from DB Exception.");
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}

		return list;
	}

	public boolean isExist(String sql, String[] selectionArgs) {
		Log.d(TAG, "[isExist]: " + sql);

		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.dbHelper.getReadableDatabase();
			cursor = db.rawQuery(sql, selectionArgs);
			if (cursor.getCount() > 0) {
				return true;
			}
		} catch (Exception e) {
			Log.e(this.TAG, "[isExist] from DB Exception.");
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return false;
	}

	public List<T> find() {
		return find(null, null, null, null, null, null, null);
	}

	public List<T> find(String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		Log.d(TAG, "[find]");

		List<T> list = new ArrayList<T>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.dbHelper.getReadableDatabase();
			cursor = db.query(this.tableName, columns, selection,
					selectionArgs, groupBy, having, orderBy, limit);

			getListFromCursor(list, cursor);
		} catch (Exception e) {
			Log.e(this.TAG, "[find] from DB Exception");
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}

		return list;
	}

	private void getListFromCursor(List<T> list, Cursor cursor)
			throws IllegalAccessException, InstantiationException {
		while (cursor.moveToNext()) {
			T entity = this.clazz.newInstance();

			for (Field field : this.allFields) {
				Column column = null;
				if (field.isAnnotationPresent(Column.class)) {
					column = (Column) field.getAnnotation(Column.class);

					field.setAccessible(true);
					Class<?> fieldType = field.getType();

					int c = cursor.getColumnIndex(column.name());
					if (c < 0) {
						continue; // ���������ѭ���¸�����ֵ
					} else if ((Integer.TYPE == fieldType)
							|| (Integer.class == fieldType)) {
						field.set(entity, cursor.getInt(c));
					} else if (String.class == fieldType) {
						field.set(entity, cursor.getString(c));
					} else if ((Long.TYPE == fieldType)
							|| (Long.class == fieldType)) {
						field.set(entity, Long.valueOf(cursor.getLong(c)));
					} else if ((Float.TYPE == fieldType)
							|| (Float.class == fieldType)) {
						field.set(entity, Float.valueOf(cursor.getFloat(c)));
					} else if ((Short.TYPE == fieldType)
							|| (Short.class == fieldType)) {
						field.set(entity, Short.valueOf(cursor.getShort(c)));
					} else if ((Double.TYPE == fieldType)
							|| (Double.class == fieldType)) {
						field.set(entity, Double.valueOf(cursor.getDouble(c)));
					} else if (Blob.class == fieldType) {
						field.set(entity, cursor.getBlob(c));
					} else if (Character.TYPE == fieldType) {
						String fieldValue = cursor.getString(c);

						if ((fieldValue != null) && (fieldValue.length() > 0)) {
							field.set(entity, Character.valueOf(fieldValue
									.charAt(0)));
						}
					}
				}
			}

			list.add((T) entity);
		}
	}

	public long insert(T entity) {
		Log.d(TAG, "[insert]: inset into " + this.tableName + " "
				+ entity.toString());
		SQLiteDatabase db = null;
		try {
			db = this.dbHelper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			setContentValues(entity, cv, "create");
			long row = db.insert(this.tableName, null, cv);
			return row;
		} catch (Exception e) {
			Log.d(this.TAG, "[insert] into DB Exception.");
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}

		return 0L;
	}

	public void delete(int id) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		String where = this.idColumn + " = ?";
		String[] whereValue = { Integer.toString(id) };

		Log.d(TAG, "[delete]: delelte from " + this.tableName + " where "
				+ where.replace("?", String.valueOf(id)));

		db.delete(this.tableName, where, whereValue);
		db.close();
	}

	public void delete(Integer... ids) {
		if (ids.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < ids.length; i++) {
				sb.append('?').append(',');
			}
			sb.deleteCharAt(sb.length() - 1);
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			String sql = "delete from " + this.tableName + " where "
					+ this.idColumn + " in (" + sb + ")";

			Log.d(TAG, "[delete]: " + sql);

			db.execSQL(sql, (Object[]) ids);
			db.close();
		}
	}

	public void update(T entity) {
		SQLiteDatabase db = null;
		try {
			db = this.dbHelper.getWritableDatabase();
			ContentValues cv = new ContentValues();

			setContentValues(entity, cv, "update");

			String where = this.idColumn + " = ?";
			int id = Integer.parseInt(cv.get(this.idColumn).toString());
			cv.remove(this.idColumn);

			Log.d(TAG, "[update]: update " + this.tableName + " where "
					+ where.replace("?", String.valueOf(id)));

			String[] whereValue = { Integer.toString(id) };
			db.update(this.tableName, cv, where, whereValue);
		} catch (Exception e) {
			Log.d(this.TAG, "[update] DB Exception.");
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
		}
	}

	private void setContentValues(T entity, ContentValues cv, String type)
			throws IllegalAccessException {

		for (Field field : this.allFields) {
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}
			Column column = (Column) field.getAnnotation(Column.class);

			field.setAccessible(true);
			Object fieldValue = field.get(entity);
			if (fieldValue == null)
				continue;
			if (("create".equals(type))
					&& (field.isAnnotationPresent(Id.class))) {
				continue;
			}
			cv.put(column.name(), fieldValue.toString());
		}
	}

	/**
	 * ����ѯ�Ľ������Ϊ��ֵ��map.
	 * 
	 * @param sql
	 *            ��ѯsql
	 * @param selectionArgs
	 *            ����ֵ
	 * @return ���ص�Map�е�keyȫ����Сд��ʽ.
	 */
	public List<Map<String, String>> query2MapList(String sql,
			String[] selectionArgs) {
		Log.d(TAG, "[query2MapList]: " + sql);
		SQLiteDatabase db = null;
		Cursor cursor = null;
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		try {
			db = this.dbHelper.getReadableDatabase();
			cursor = db.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				for (String columnName : cursor.getColumnNames()) {
					map.put(columnName.toLowerCase(), cursor.getString(cursor
							.getColumnIndex(columnName)));
				}
				retList.add(map);
			}
		} catch (Exception e) {
			Log.e(TAG, "[query2MapList] from DB exception");
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}

		return retList;
	}

	/**
	 * ��װִ��sql����.
	 * 
	 * @param sql
	 * @param selectionArgs
	 */
	public void execSql(String sql, Object[] selectionArgs) {
		SQLiteDatabase db = null;
		Log.d(TAG, "[execSql]: " + sql);
		try {
			db = this.dbHelper.getWritableDatabase();
			if (selectionArgs == null) {
				db.execSQL(sql);
			} else {
				db.execSQL(sql, selectionArgs);
			}
		} catch (Exception e) {
			Log.e(TAG, "[execSql] DB exception.");
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
}