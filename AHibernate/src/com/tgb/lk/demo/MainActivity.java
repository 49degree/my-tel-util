package com.tgb.lk.demo;

import java.util.List;
import java.util.Map;

import com.tgb.lk.demo.R;

import com.tgb.lk.demo.dao.impl.StudentDaoImpl;
import com.tgb.lk.demo.dao.impl.TeacherDaoImpl;
import com.tgb.lk.demo.model.Student;
import com.tgb.lk.demo.model.Teacher;

import android.app.Activity;
import android.os.Bundle;

/**
 * AHibernate概要 <br/>
 * (一)支持功能: 1.自动建表,支持属性来自继承类:可根据注解自动完成建表,并且对于继承类中的注解字段也支持自动建表. 2.自动支持增删改
 * ,增改支持对象化操作:增删改是数据库操作的最基本单元,不用重复写这些增删改的代码,并且添加和更新支持类似于hibernate中的对象化操作.
 * 3.查询方式灵活:支持android框架提供的方式,也支持原生sql方式.
 * 4.查询结果对象化:对于查询结果可自动包装为实体对象,类似于hibernate框架.
 * 5.查询结果灵活:查询结果支持对象化,也支持结果为List<Map<String,String>>形式,这个方法在实际项目中很实用,且效率更好些.
 * 6.日志较详细:因为android开发不支持热部署调试,运行报错时可根据日志来定位错误,这样可以减少运行Android的次数. <br/>
 * (二)不足之处: <br/>
 * 1.id暂时只支持int类型,不支持uuid,在sqlite中不建议用uuid.
 * 2.现在每个方法都自己开启和关闭事务,暂时还不支持在一个事务中做多个操作然后统一提交事务. <br/>
 * (三)作者寄语:<br/>
 * 昔日有JavaScript借Java发展,今日也希望AHibernate借Hibernate之名发展.
 * 希望这个项目以后会成为开源社区的重要一员,更希望这个项目能给所有Android开发者带便利.
 * 欢迎访问我的博客:http://blog.csdn.net/lk_blog,
 * 这里有这个框架的使用范例和源码,希望朋友们多多交流完善这个框架,共同推动中国开源事业的发展,AHibernate期待与您共创美好未来!!!
 */
public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// 熟悉用接口的朋友注意哦,这里也可以定义为接口哦,见StudentDaoImpl.java中的注释.
		TeacherDaoImpl teacherDao = new TeacherDaoImpl(MainActivity.this);
		StudentDaoImpl studentDao = new StudentDaoImpl(MainActivity.this);

		// 添加
		Teacher teacher = new Teacher();
		teacher.setName("米老师");
		teacher.setAge(50);
		teacher.setTitle("教授");
		Long teacherId = teacherDao.insert(teacher);

		Student student1 = new Student();
		student1.setName("lk");
		student1.setAge(26);
		student1.setClasses("五");
		student1.setTeacherId(teacherId.intValue());
		Long studentId1 = studentDao.insert(student1);

		Student student2 = new Student();
		student2.setName("cls");
		student2.setAge(26);
		student2.setClasses("五");
		student2.setTeacherId(teacherId.intValue());
		Long studentId2 = studentDao.insert(student2);

		Student student3 = new Student();
		student3.setName("lb");
		student3.setAge(27);
		student3.setClasses("五期");
		student3.setTeacherId(teacherId.intValue());
		Long studentId3 = studentDao.insert(student3);

		// 查询
		// 方式1:根据Id查询单个对象
		// 结果:student1Student [id=1, name=lk,age=26,teacherId=1, classes=五]
		Student student4 = studentDao.get(studentId1.intValue());
		System.out.println("student4" + student4);

		// 方式2:查询出表中的所有记录
		// 执行结果如下:
		// list1:Student [id=1, name=lk,age=26,teacherId=1, classes=五]
		// list1:Student [id=2, name=cls,age=26,teacherId=1, classes=五]
		// list1:Student [id=3, name=lb,age=27,teacherId=1, classes=五期]
		List<Student> list1 = studentDao.find();
		for (Student student : list1) {
			System.out.println("list1:" + student);
		}

		// 方式3:限制条件查询和查询结果
		// 执行结果:list2:Student [id=2, name=cls,age=0,teacherId=0, classes=null]
		List<Student> list2 = studentDao.find(new String[] { "id", "name" },
				" id = ? ", new String[] { studentId2.toString() }, null, null,
				null, null);
		for (Student student : list2) {
			System.out.println("list2:" + student);
		}

		// 方式4:使用sql查询出结果,此种方式是2,3,4中最灵活的.
		// 执行结果:
		// list3:Student [id=2, name=cls,age=26,teacherId=1, classes=五]
		// list3:Student [id=3, name=lb,age=27,teacherId=1, classes=五期]
		List<Student> list3 = studentDao.rawQuery(
				"select * from t_student where id in (?,?) ", new String[] {
						studentId2.toString(), studentId3.toString() });
		for (Student student : list3) {
			System.out.println("list3:" + student);
		}

		// 方式4进阶:如果想查询出米老师的学生,可以这样实现:
		// 执行结果:
		// list4:Student [id=1, name=lk,age=26,teacherId=1, classes=五]
		// list4:Student [id=2, name=cls,age=26,teacherId=1, classes=五]
		// list4:Student [id=3, name=lb,age=27,teacherId=1, classes=五期]
		List<Student> list4 = studentDao
				.rawQuery(
						"select s.* from t_student s join t_teacher t on s.teacher_id = t.id where t.name= ? ",
						new String[] { "米老师" });
		for (Student student : list4) {
			System.out.println("list4:" + student);
		}

		// 方式5:我只想知道姓名和年龄,查询得到List<Map<String,String>>形式.只查2个字会比查询所有字段并封装为对象效率高吧,尤其字段值很多时我们的手机更喜欢这种方式哦.
		// 结果:
		// listMap1: name:lk;age:26
		// listMap1: name:cls;age:26
		// listMap1: name:lb;age:27
		List<Map<String, String>> listMap1 = studentDao.query2MapList(
				"select name,Age from t_student ", null);
		for (Map<String, String> map : listMap1) {
			// 查询的List中的map以查询sql中的属性值的小写形式为key,注意是小写形式哦.
			System.out.println("listMap1: name:" + map.get("name") + ";age:"
					+ map.get("age"));
		}

		// 方式5进阶:我想知道前2名学生的姓名和班主任姓名,这种方式是不是超灵活啊,用其他的方式查询都没这种方式好用吧,哈哈.
		// 结果:
		// listMap2: student_name:lk;teacher_name:米老师
		// listMap2: student_name:cls;teacher_name:米老师
		List<Map<String, String>> listMap2 = studentDao
				.query2MapList(
						"select s.name sname,t.name tname from t_student s join t_teacher t on s.teacher_id = t.id limit ? ",
						new String[] { "2" });
		for (Map<String, String> map : listMap2) {
			System.out.println("listMap2: student_name:" + map.get("sname")
					+ ";teacher_name:" + map.get("tname"));
		}

		// 更新
		// 结果: Student [id=1, name=李坤,age=26,teacherId=1, classes=五期]
		student1 = studentDao.get(studentId1.intValue());
		student1.setName("李坤");
		student1.setClasses("五期");
		studentDao.update(student3);
		System.out.println(student1);

		// 删除:支持单个id删除,也支持多个id同时删除哦.
		studentDao.delete(studentId1.intValue());
		studentDao.delete(new Integer[] { studentId2.intValue(),
				studentId3.intValue() });

		// 支持执行sql语句哦.
		teacherDao.execSql("insert into t_teacher(name,age) values('米教授',50)",
				null);

	}
}