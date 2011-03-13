package com.newElement.remp.sdk.controls;

import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class DataGridViewAdapter extends BaseAdapter {
	private String[][] mColumnHeaders = null;//表头列表
	private ArrayList<ArrayList<View>> mDataList = null;//数据视图列表
	
	private int mRowsPerPage = 20;//每页记录数
	private int mTotalRows = 0;//总记录数
	private int mTotalPages = 1;//总页数
	private int mCurPage = 1;//当前页码
	
	private Object mDataObject = null;//用于配置查询数据的对象
	private String mDataMethodName = null;//用于配置查询数据的方法名称
	
	private boolean mDataRemot = false;//翻页是否取远程数据
	
	
	/**
	 * 获取翻页是否取远程数据
	 * @return
	 */
	public boolean getMDataRemot() {
		return mDataRemot;
	}

	/**
	 * 设置 翻页是否取远程数据
	 * @param mDataRemot
	 */
	public void setMDataRemot(boolean mDataRemot) {
		this.mDataRemot = mDataRemot;
	}

	/**
	 * 设置总记录数 并计算总页数
	 * @param mTotalRows 总记录数
	 */
	public void setPageInfo(int mTotalRows){
		setPageInfo(mRowsPerPage,mTotalRows);
	}
	
	/**
	 * 设置页码及记录数
	 * @param mRowsPerPage 每页记录数
	 * @param mTotalRows 总记录数
	 */
	public void setPageInfo(int mRowsPerPage,int mTotalRows){
		this.mRowsPerPage = mRowsPerPage;
		this.mTotalRows = mTotalRows;
		//计算总页数
		if(mRowsPerPage==0||mTotalRows==0){
			this.mTotalPages = 1;
		}else{
			this.mTotalPages = (int)Math.ceil(
					(double)mTotalRows/(double)mRowsPerPage);
		}
	}
	
	/**
	 * 获取每页记录数
	 * @return
	 */
	public int getMRowsPerPage() {
		return mRowsPerPage;
	}

	/**
	 * 获取总页数
	 * @return
	 */
	public int getMTotalPages() {
		return mTotalPages;
	}
	/**
	 * 获取总记录数
	 * @return
	 */
	public int getMTotalRows() {
		return mTotalRows;
	}
	/**
	 * 获取当前页码
	 * @return
	 */
	public int getMCurPage() {
		return mCurPage;
	}
	/**
	 * 获取当前页码
	 * @return
	 */
	public void setMCurPage(int mCurPage) {
		this.mCurPage=mCurPage;
	}
	/**
	 * 获取表头列表
	 * @return ArrayList<ColumnHead>
	 */
	public String[][] getMColumnHeaders() {
		return mColumnHeaders;
	}

	/**
	 * 配置表头列表
	 * @param mColumnHeads
	 */
	public void setMColumnHeaders(String[][] mColumnHeaders) {
		this.mColumnHeaders = mColumnHeaders;
	}
	
	/**
	 * 获取表数据
	 * @return ArrayList<ArrayList<View>>
	 */
	public ArrayList<ArrayList<View>> getMData() {
		return mDataList;
	}

	/**
	 * 配置表数据
	 * @param mData
	 */
	public void setMData(ArrayList<ArrayList<View>> mDataList) {
		this.mDataList = mDataList;
	}
	
	/**
	 * 配置远程查询数据的对象和方法
	 * @param mDataObject
	 * @param mDataMethodName
	 */
	public void setRemotDataOM(Object mDataObject,String mDataMethodName){
		this.mDataObject = mDataObject;
		this.mDataMethodName = mDataMethodName; 
	}
	
	/**
	 * 查询当前页的远程数据
	 */
	public void initCurPageData(int startRow,int endRow){
		if(mDataRemot){
			try{
				Method method = mDataObject.getClass().getMethod(mDataMethodName, new Class[]{Integer.class,Integer.class});
				this.mDataList = (ArrayList<ArrayList<View>>)method.invoke(mDataObject, 
						new Object[]{Integer.parseInt(String.valueOf(startRow)),Integer.parseInt(String.valueOf(endRow))});
			}catch(NoSuchMethodException e){
				e.printStackTrace();
			}catch(InvocationTargetException ie){
				ie.printStackTrace();
			}catch(IllegalAccessException ile){
				ile.printStackTrace();
			}
			
		}
	}
	

	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
}