package com.tgb.lk.demo.dao.impl;

import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
import com.tgb.lk.demo.model.Student;
import com.tgb.lk.demo.util.DBHelper;

import android.content.Context;

//�������J2EE����һ��ϣ��֧�ֽӿڰ�,�������д������:
//дһ���ӿ�:public interface StudentDao extends BaseDao<Student> {}
//ʵ�ֽӿ�: public class StudentDaoImpl extends BaseDaoImpl<Student> implements StudentDao
public class StudentDaoImpl extends BaseDaoImpl<Student> {
	public StudentDaoImpl(Context context) {
		super(new DBHelper(context));
	}
}
