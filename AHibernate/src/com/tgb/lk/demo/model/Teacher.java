package com.tgb.lk.demo.model;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Table;

//�Զ����ɵĽ������:
//crate table [t_teacher]: CREATE TABLE t_teacher (id INTEGER primary key autoincrement, title TEXT, name TEXT(20), age INTEGER )

@Table(name = "t_teacher")
public class Teacher extends Person {
	@Column(name = "title")
	private String title;// ְ��

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Teacher [" + super.toString() + ",title=" + title + "]";
	}
}
