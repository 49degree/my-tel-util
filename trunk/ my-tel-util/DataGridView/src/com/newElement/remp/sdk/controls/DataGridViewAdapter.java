package com.newElement.remp.sdk.controls;

import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class DataGridViewAdapter extends BaseAdapter {
	private String[][] mColumnHeaders = null;//��ͷ�б�
	private ArrayList<ArrayList<View>> mDataList = null;//������ͼ�б�
	
	private int mRowsPerPage = 20;//ÿҳ��¼��
	private int mTotalRows = 0;//�ܼ�¼��
	private int mTotalPages = 1;//��ҳ��
	private int mCurPage = 1;//��ǰҳ��
	
	private Object mDataObject = null;//�������ò�ѯ���ݵĶ���
	private String mDataMethodName = null;//�������ò�ѯ���ݵķ�������
	
	private boolean mDataRemot = false;//��ҳ�Ƿ�ȡԶ������
	
	
	/**
	 * ��ȡ��ҳ�Ƿ�ȡԶ������
	 * @return
	 */
	public boolean getMDataRemot() {
		return mDataRemot;
	}

	/**
	 * ���� ��ҳ�Ƿ�ȡԶ������
	 * @param mDataRemot
	 */
	public void setMDataRemot(boolean mDataRemot) {
		this.mDataRemot = mDataRemot;
	}

	/**
	 * �����ܼ�¼�� ��������ҳ��
	 * @param mTotalRows �ܼ�¼��
	 */
	public void setPageInfo(int mTotalRows){
		setPageInfo(mRowsPerPage,mTotalRows);
	}
	
	/**
	 * ����ҳ�뼰��¼��
	 * @param mRowsPerPage ÿҳ��¼��
	 * @param mTotalRows �ܼ�¼��
	 */
	public void setPageInfo(int mRowsPerPage,int mTotalRows){
		this.mRowsPerPage = mRowsPerPage;
		this.mTotalRows = mTotalRows;
		//������ҳ��
		if(mRowsPerPage==0||mTotalRows==0){
			this.mTotalPages = 1;
		}else{
			this.mTotalPages = (int)Math.ceil(
					(double)mTotalRows/(double)mRowsPerPage);
		}
	}
	
	/**
	 * ��ȡÿҳ��¼��
	 * @return
	 */
	public int getMRowsPerPage() {
		return mRowsPerPage;
	}

	/**
	 * ��ȡ��ҳ��
	 * @return
	 */
	public int getMTotalPages() {
		return mTotalPages;
	}
	/**
	 * ��ȡ�ܼ�¼��
	 * @return
	 */
	public int getMTotalRows() {
		return mTotalRows;
	}
	/**
	 * ��ȡ��ǰҳ��
	 * @return
	 */
	public int getMCurPage() {
		return mCurPage;
	}
	/**
	 * ��ȡ��ǰҳ��
	 * @return
	 */
	public void setMCurPage(int mCurPage) {
		this.mCurPage=mCurPage;
	}
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
	
	/**
	 * ����Զ�̲�ѯ���ݵĶ���ͷ���
	 * @param mDataObject
	 * @param mDataMethodName
	 */
	public void setRemotDataOM(Object mDataObject,String mDataMethodName){
		this.mDataObject = mDataObject;
		this.mDataMethodName = mDataMethodName; 
	}
	
	/**
	 * ��ѯ��ǰҳ��Զ������
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