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
 * AHibernate��Ҫ <br/>
 * (һ)֧�ֹ���: 1.�Զ�����,֧���������Լ̳���:�ɸ���ע���Զ���ɽ���,���Ҷ��ڼ̳����е�ע���ֶ�Ҳ֧���Զ�����. 2.�Զ�֧����ɾ��
 * ,����֧�ֶ��󻯲���:��ɾ�������ݿ�������������Ԫ,�����ظ�д��Щ��ɾ�ĵĴ���,������Ӻ͸���֧��������hibernate�еĶ��󻯲���.
 * 3.��ѯ��ʽ���:֧��android����ṩ�ķ�ʽ,Ҳ֧��ԭ��sql��ʽ.
 * 4.��ѯ�������:���ڲ�ѯ������Զ���װΪʵ�����,������hibernate���.
 * 5.��ѯ������:��ѯ���֧�ֶ���,Ҳ֧�ֽ��ΪList<Map<String,String>>��ʽ,���������ʵ����Ŀ�к�ʵ��,��Ч�ʸ���Щ.
 * 6.��־����ϸ:��Ϊandroid������֧���Ȳ������,���б���ʱ�ɸ�����־����λ����,�������Լ�������Android�Ĵ���. <br/>
 * (��)����֮��: <br/>
 * 1.id��ʱֻ֧��int����,��֧��uuid,��sqlite�в�������uuid.
 * 2.����ÿ���������Լ������͹ر�����,��ʱ����֧����һ�����������������Ȼ��ͳһ�ύ����. <br/>
 * (��)���߼���:<br/>
 * ������JavaScript��Java��չ,����Ҳϣ��AHibernate��Hibernate֮����չ.
 * ϣ�������Ŀ�Ժ���Ϊ��Դ��������ҪһԱ,��ϣ�������Ŀ�ܸ�����Android�����ߴ�����.
 * ��ӭ�����ҵĲ���:http://blog.csdn.net/lk_blog,
 * �����������ܵ�ʹ�÷�����Դ��,ϣ�������Ƕ�ཻ������������,��ͬ�ƶ��й���Դ��ҵ�ķ�չ,AHibernate�ڴ�������������δ��!!!
 */
public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// ��Ϥ�ýӿڵ�����ע��Ŷ,����Ҳ���Զ���Ϊ�ӿ�Ŷ,��StudentDaoImpl.java�е�ע��.
		TeacherDaoImpl teacherDao = new TeacherDaoImpl(MainActivity.this);
		StudentDaoImpl studentDao = new StudentDaoImpl(MainActivity.this);

		// ���
		Teacher teacher = new Teacher();
		teacher.setName("����ʦ");
		teacher.setAge(50);
		teacher.setTitle("����");
		Long teacherId = teacherDao.insert(teacher);

		Student student1 = new Student();
		student1.setName("lk");
		student1.setAge(26);
		student1.setClasses("��");
		student1.setTeacherId(teacherId.intValue());
		Long studentId1 = studentDao.insert(student1);

		Student student2 = new Student();
		student2.setName("cls");
		student2.setAge(26);
		student2.setClasses("��");
		student2.setTeacherId(teacherId.intValue());
		Long studentId2 = studentDao.insert(student2);

		Student student3 = new Student();
		student3.setName("lb");
		student3.setAge(27);
		student3.setClasses("����");
		student3.setTeacherId(teacherId.intValue());
		Long studentId3 = studentDao.insert(student3);

		// ��ѯ
		// ��ʽ1:����Id��ѯ��������
		// ���:student1Student [id=1, name=lk,age=26,teacherId=1, classes=��]
		Student student4 = studentDao.get(studentId1.intValue());
		System.out.println("student4" + student4);

		// ��ʽ2:��ѯ�����е����м�¼
		// ִ�н������:
		// list1:Student [id=1, name=lk,age=26,teacherId=1, classes=��]
		// list1:Student [id=2, name=cls,age=26,teacherId=1, classes=��]
		// list1:Student [id=3, name=lb,age=27,teacherId=1, classes=����]
		List<Student> list1 = studentDao.find();
		for (Student student : list1) {
			System.out.println("list1:" + student);
		}

		// ��ʽ3:����������ѯ�Ͳ�ѯ���
		// ִ�н��:list2:Student [id=2, name=cls,age=0,teacherId=0, classes=null]
		List<Student> list2 = studentDao.find(new String[] { "id", "name" },
				" id = ? ", new String[] { studentId2.toString() }, null, null,
				null, null);
		for (Student student : list2) {
			System.out.println("list2:" + student);
		}

		// ��ʽ4:ʹ��sql��ѯ�����,���ַ�ʽ��2,3,4��������.
		// ִ�н��:
		// list3:Student [id=2, name=cls,age=26,teacherId=1, classes=��]
		// list3:Student [id=3, name=lb,age=27,teacherId=1, classes=����]
		List<Student> list3 = studentDao.rawQuery(
				"select * from t_student where id in (?,?) ", new String[] {
						studentId2.toString(), studentId3.toString() });
		for (Student student : list3) {
			System.out.println("list3:" + student);
		}

		// ��ʽ4����:������ѯ������ʦ��ѧ��,��������ʵ��:
		// ִ�н��:
		// list4:Student [id=1, name=lk,age=26,teacherId=1, classes=��]
		// list4:Student [id=2, name=cls,age=26,teacherId=1, classes=��]
		// list4:Student [id=3, name=lb,age=27,teacherId=1, classes=����]
		List<Student> list4 = studentDao
				.rawQuery(
						"select s.* from t_student s join t_teacher t on s.teacher_id = t.id where t.name= ? ",
						new String[] { "����ʦ" });
		for (Student student : list4) {
			System.out.println("list4:" + student);
		}

		// ��ʽ5:��ֻ��֪������������,��ѯ�õ�List<Map<String,String>>��ʽ.ֻ��2���ֻ�Ȳ�ѯ�����ֶβ���װΪ����Ч�ʸ߰�,�����ֶ�ֵ�ܶ�ʱ���ǵ��ֻ���ϲ�����ַ�ʽŶ.
		// ���:
		// listMap1: name:lk;age:26
		// listMap1: name:cls;age:26
		// listMap1: name:lb;age:27
		List<Map<String, String>> listMap1 = studentDao.query2MapList(
				"select name,Age from t_student ", null);
		for (Map<String, String> map : listMap1) {
			// ��ѯ��List�е�map�Բ�ѯsql�е�����ֵ��Сд��ʽΪkey,ע����Сд��ʽŶ.
			System.out.println("listMap1: name:" + map.get("name") + ";age:"
					+ map.get("age"));
		}

		// ��ʽ5����:����֪��ǰ2��ѧ���������Ͱ���������,���ַ�ʽ�ǲ��ǳ���,�������ķ�ʽ��ѯ��û���ַ�ʽ���ð�,����.
		// ���:
		// listMap2: student_name:lk;teacher_name:����ʦ
		// listMap2: student_name:cls;teacher_name:����ʦ
		List<Map<String, String>> listMap2 = studentDao
				.query2MapList(
						"select s.name sname,t.name tname from t_student s join t_teacher t on s.teacher_id = t.id limit ? ",
						new String[] { "2" });
		for (Map<String, String> map : listMap2) {
			System.out.println("listMap2: student_name:" + map.get("sname")
					+ ";teacher_name:" + map.get("tname"));
		}

		// ����
		// ���: Student [id=1, name=����,age=26,teacherId=1, classes=����]
		student1 = studentDao.get(studentId1.intValue());
		student1.setName("����");
		student1.setClasses("����");
		studentDao.update(student3);
		System.out.println(student1);

		// ɾ��:֧�ֵ���idɾ��,Ҳ֧�ֶ��idͬʱɾ��Ŷ.
		studentDao.delete(studentId1.intValue());
		studentDao.delete(new Integer[] { studentId2.intValue(),
				studentId3.intValue() });

		// ֧��ִ��sql���Ŷ.
		teacherDao.execSql("insert into t_teacher(name,age) values('�׽���',50)",
				null);

	}
}