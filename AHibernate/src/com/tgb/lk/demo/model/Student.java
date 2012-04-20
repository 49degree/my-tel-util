package com.tgb.lk.demo.model;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Table;

//自动生成的建表语句:
//CREATE TABLE t_student (id INTEGER primary key autoincrement, classes TEXT, teacher_id INTEGER, name TEXT(20), age INTEGER )

@Table(name = "t_student")
public class Student extends Person {

	@Column(name = "teacher_id")
	private int teacherId;// 班主任id

	@Column(name = "classes")
	private String classes;// 班级

	public int getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	@Override
	public String toString() {
		return "Student [" + super.toString() + ",teacherId=" + teacherId
				+ ", classes=" + classes + "]";
	}

}
