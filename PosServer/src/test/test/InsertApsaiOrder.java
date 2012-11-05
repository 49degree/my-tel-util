package test;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.a3650.posserver.core.bean.ApsaiOrder;
import com.a3650.posserver.core.db.HibernateUtil;

public class InsertApsaiOrder extends TestCase{

	public void testInit(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		for(int i=2;i<10;i++){
			ApsaiOrder apsaiOrder = new ApsaiOrder();
			apsaiOrder.setApsaiId(i+"");
			apsaiOrder.setCustomName("测试保单"+i);
			apsaiOrder.setCustomAddr("测试保单地址"+i);
			apsaiOrder.setCustomLink("测试保单联系方式"+i);
			apsaiOrder.setApsaiAmount(123L+i);
			session.save(apsaiOrder);
		}

		
		tx.commit();
		
		session.close();
		HibernateUtil.shutdown();
	}
}
