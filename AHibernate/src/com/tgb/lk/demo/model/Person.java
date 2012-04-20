package com.tgb.lk.demo.model;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;

//此处没有加Table属性,它是其他类的基类,本类中用@Column注解的字段在子类中同样会被创建到表中.
public class Person {
	@Id
	@Column(name = "id")
	private int id; // 主键,int类型,数据库建表时此字段会设为自增长

	@Column(name = "name", length = 20)
	private String name; // 名字长度一般不会超过20个字符吧,length=20数据字段的长度是20

	@Column(name = "age", type = "INTEGER")
	private int age; // 年龄一般是数值,用type = "INTEGER"规范一下吧.

	// //假设您开始时没有此属性,程序开发中才想到此属性,去掉代码注释试试吧,数据库增删改查不用修改任何代码哦.
	// @Column(name = "sex")
	// private String sex;

	// 有些字段您可能不希望保存到数据库中,不用@Column注释就不会映射到数据库.
	private String noSaveFild;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getNoSaveFild() {
		return noSaveFild;
	}

	public void setNoSaveFild(String noSaveFild) {
		this.noSaveFild = noSaveFild;
	}

	@Override
	public String toString() {
		return "id=" + id + ", name=" + name + ",age=" + age;
	}
}
