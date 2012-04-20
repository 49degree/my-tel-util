package com.tgb.lk.demo.dao.impl;

import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;
import com.tgb.lk.demo.model.Teacher;
import com.tgb.lk.demo.util.DBHelper;

import android.content.Context;

public class TeacherDaoImpl extends BaseDaoImpl<Teacher> {
	public TeacherDaoImpl(Context context) {
		super(new DBHelper(context));
	}
}
