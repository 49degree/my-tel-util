package com.yang.android.tel.ui;


import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class DataGridViewAdapter extends BaseAdapter {
	private String[][] mColumnHeaders = null;//��ͷ�б�
	private ArrayList<ArrayList<View>> mDataList = null;//������ͼ�б�

	/**
	 * ��ȡ��ͷ�б�
	 * @return ArrayList<ColumnHead>
	 */
	public String[][] getMColumnHeaders() {
		return mColumnHeaders;
	}

	/**
	 * ���ñ�ͷ�б�
	 * @param mColumnHeads
	 */
	public void setMColumnHeaders(String[][] mColumnHeaders) {
		this.mColumnHeaders = mColumnHeaders;
	}
	
	/**
	 * ��ȡ������
	 * @return ArrayList<ArrayList<View>>
	 */
	public ArrayList<ArrayList<View>> getMData() {
		return mDataList;
	}

	/**
	 * ���ñ�����
	 * @param mData
	 */
	public void setMData(ArrayList<ArrayList<View>> mDataList) {
		this.mDataList = mDataList;
	}
	
	

	
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList.size();
	}

	
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDataList.get(position);
	}

	
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
}