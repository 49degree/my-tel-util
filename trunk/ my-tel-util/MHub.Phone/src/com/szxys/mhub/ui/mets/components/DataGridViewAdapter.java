package com.szxys.mhub.ui.mets.components;

import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class DataGridViewAdapter extends BaseAdapter {
	private String[][] mColumnHeaders = null;//表头列表
	private ArrayList<ArrayList<View>> mDataList = null;//数据视图列表

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
	
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
}