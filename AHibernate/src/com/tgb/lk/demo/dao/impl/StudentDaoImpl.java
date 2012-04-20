package com.tgb.lk.demo.dao.impl;

import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
import com.tgb.lk.demo.model.Student;
import com.tgb.lk.demo.util.DBHelper;

import android.content.Context;

//如果您是J2EE高手一定希望支持接口吧,按下面的写法即可:
//写一个接口:public interface StudentDao extends BaseDao<Student> {}
//实现接口: public class StudentDaoImpl extends BaseDaoImpl<Student> implements StudentDao
public class StudentDaoImpl extends BaseDaoImpl<Student> {
	public StudentDaoImpl(Context context) {
		super(new DBHelper(context));
	}
}
