package test;

import org.hibernate.Session;

import com.a3650.posserver.core.bean.Company;
import com.a3650.posserver.core.db.HibernateUtil;

/**
 * 10000次
 * c3p0耗时：3978
 * proxoo耗时：5289
 * 100000次
 * c3p0耗时：37241
 * proxoo耗时：49424
 * @author Administrator
 *
 */
public class TestQuerySpeed{

	public static void main(String[] args){
		HibernateUtil.getSessionFactory();
		long bg = System.currentTimeMillis();
		for(int i=0;i<100000;i++){
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			Company bean = (Company)session.get(Company.class, "104110045110012");
			//System.out.println(bean.getCompanyName());
			session.getTransaction().commit();
			
//			session = HibernateUtil.getSessionFactory().getCurrentSession();
//			bean.setCompanyName(bean.getCompanyName()+1);
//			session.beginTransaction();
//			session.saveOrUpdate(bean);
//			session.getTransaction().commit();
//			System.out.println(session.hashCode());
			
			
//			new Thread(){
//				public void run(){
//					try{
//
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//			}.start();
		}
		long end = System.currentTimeMillis();
		System.out.println("耗时："+(end-bg));



	}
}
