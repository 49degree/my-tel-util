package test;

import org.hibernate.Session;
import org.hibernate.stat.Statistics;

import com.a3650.posserver.core.bean.Company;
import com.a3650.posserver.core.bean.PosTerminal;
import com.a3650.posserver.core.db.HibernateUtil;

public class TestHibernate2LevelCash {
	public static void main(String[] args){
		testUpdate();
	}
	
	
	public static void testTrans(){
		
	}
	static Session s=null;
	static Company user=null;
	static Statistics st = HibernateUtil.getSessionFactory().getStatistics(); 
	static String id  = "14";
	public static void testUpdate(){

		Class userClass = Company.class; 
		
		PosTerminal pos = null;
		Class posClass = PosTerminal.class;

		try{ 
		    s = HibernateUtil.getSessionFactory().getCurrentSession(); 
		    s.beginTransaction(); 
		    
		    printSecondLevelCache("get1",st);
		    user = (Company) s.get(userClass, id); //在一级缓存中取数据
		   //pos = (PosTerminal) s.get(posClass, "00010000"); //在一级缓存中取数据
		    
		    printSecondLevelCache("get1",st);
		    System.out.println("1从数据库中取数据:"+user.getCompanyName());
		    s.beginTransaction();
		    //前面查询出对象后在后面新建一相同主键的对象再去update会抛异常(org.hibernate.NonUniqueObjectException)，
		    //因为不能把2个相同主键的对象加入一级缓存中,可以s.clear()一下或者使用前面查询出的对象去更新
		    //update前如果session缓存中已经存在该对象，则会比较对象是否有变化，如果没有变化则不会执行update语句，
		    //如果session缓存中不存在该对象，则直接执行UPDATE语句
		    //s.clear();
		    //user = new Company();
		    //user.setCompanyId(id);
		   new Thread(){
			   public void run(){
				   s = HibernateUtil.getSessionFactory().getCurrentSession(); 
				    s.beginTransaction(); 
				   
				    user.setCompanyName("test_update11"+id);
				    s.update(user);//会加入一级缓存,cmmit前是不会做主键检查的
				    printSecondLevelCache("update1",st);
				    //s.flush();
				    printSecondLevelCache("update2",st);
				    s.getTransaction().commit();//只有在提交以后才会加入二级缓存
			   }
		   }.start();

		   try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    
		    s = HibernateUtil.getSessionFactory().getCurrentSession(); 
		    s.beginTransaction(); 
		    printSecondLevelCache("update3",st);
		    user = (Company) s.get(userClass, id); //在一级缓存中取数据 
		    printSecondLevelCache("update5",st);
		    System.out.println("1从一级缓存中取数据:"+user.getCompanyName());
            s.clear();//二级缓存中的对象不会清除，但是会导致session无法命中一级在二级缓存中的对象，下面的语句会从数据库查询
		    
		    
		    
//		    s.beginTransaction();
//		    Query q = s.createQuery("update Company c set c.companyName='update111' where c.companyId='1'");
//		    
//		    printSecondLevelCache("executeUpdate1",st);
//		    q.executeUpdate();//提交相应SQL语句到数据库服务器，修改数据库缓存中的数据，其他用户访问到的是原始数据,清空被修改对象类的所有对象的二级缓存
//		    printSecondLevelCache("executeUpdate1",st);
//		    s.getTransaction().commit();
//		    printSecondLevelCache("commit",st);
//		    s = HibernateUtil.getSessionFactory().openSession();
//		    user = (Company) s.get(userClass, id); //在一级缓存中取数据
//		    pos = (PosTerminal) s.get(posClass, "00010000"); //在一级缓存中取数据
//		    printSecondLevelCache("get",st);
//		    System.out.println("1查询到数据库缓存中取数据:"+pos.getPosName());
//		    System.out.println("1查询到数据库缓存中取数据:"+user.getCompanyName());
//            s.clear();//二级缓存中的对象不会清除，但是会导致session无法命中一级在二级缓存中的对象，下面的语句会从数据库查询

            s.clear();
            s.close();
    	    s = HibernateUtil.getSessionFactory().getCurrentSession(); 
		    s.beginTransaction(); //创建新对象后可以命中之前的session保存在二级缓存中的对象，所以下面的语句不会从数据库查询

		    user = (Company) s.get(userClass, id); 
		    printSecondLevelCache("get",st);
            System.out.println("2从数据库get:"+user.getCompanyName()); 
           
            
		    s.clear();//二级缓存中的对象不会清除，但是会导致session无法命中一级在二级缓存中的对象
		    s.close();
		    s = HibernateUtil.getSessionFactory().getCurrentSession(); 
		    s.beginTransaction(); //创建新对象后可以命中之前的session保存在二级缓存中的对象，所以下面的语句不会从数据库查询

		    user = (Company) s.load(userClass, id);  //load延迟加载，load方法抛异常是指在使用该对象的数据时，数据库中不存在该数据时抛异常，而不是在创建这个对象时
		    printSecondLevelCache("load",st);
            System.out.println("3在二级缓存中取数据:"+user.getCompanyName());
            
		} finally { 
		    if (s != null) 
		        s.close(); 
		}
	}
	
	public static void testSave(){
		Class userClass = Company.class; 
		Session s=null;
		Company user=null;
		Statistics st = HibernateUtil.getSessionFactory().getStatistics(); 
		String id  = "14";
		try{ 
		    s = HibernateUtil.getSessionFactory().openSession(); 
		    s.beginTransaction();
		    Company company = new Company();
		    company.setCompanyId(id);
		    company.setCompanyName("hibernate test"+id);
		    s.save(company);//会加入一级缓存,cmmit前是不会做主键检查的
		    printSecondLevelCache("save",st);
		    
		    s.getTransaction().commit();//只有在提交以后才会加入二级缓存
		    printSecondLevelCache("commit",st);
		    
		    user = (Company) s.get(userClass, id); //在一级缓存中取数据
		    printSecondLevelCache("get",st);
		    System.out.println("1在一级缓存中取数据:"+user.getCompanyName());
            s.clear();//二级缓存中的对象不会清除，但是会导致session无法命中一级在二级缓存中的对象，下面的语句会从数据库查询

		    user = (Company) s.get(userClass, id); 
		    printSecondLevelCache("get",st);
            System.out.println("2从数据库查询:"+user.getCompanyName()); 

		    s.clear();//二级缓存中的对象不会清除，但是会导致session无法命中一级在二级缓存中的对象
		    s = HibernateUtil.getSessionFactory().openSession(); //创建新对象后可以命中之前的session保存在二级缓存中的对象，所以下面的语句不会从数据库查询

		    user = (Company) s.get(userClass, id);  
		    printSecondLevelCache("get",st);
            System.out.println("3在二级缓存中取数据:"+user.getCompanyName());

		} finally { 
		    if (s != null) 
		        s.close(); 
		}
	}
	
	
	public static void testCash(){
		Session s=null;
		Company user=null;
		Class userClass = Company.class; 
		 Statistics st = HibernateUtil.getSessionFactory().getStatistics();
		try{ 
		    s = HibernateUtil.getSessionFactory().openSession(); 
		    user = (Company) s.get(userClass, "104110045110012"); 
		    printSecondLevelCache("get",st);
		    System.out.println("1从数据库查询:"+user.getCompanyName()); 
		    
		    s.clear();//二级缓存中的对象不会清除，但是会导致session无法命中一级在二级缓存中的对象,下面的语句会从数据库查询
		    user = (Company) s.get(userClass, "104110045110012");      
		    printSecondLevelCache("get",st);
            System.out.println("2从数据库查询:"+user.getCompanyName()); 
            s.clear();
		} finally { 
		    if (s != null) 
		        s.close(); 
			
		}

		try{ 
			
			s = HibernateUtil.getSessionFactory().openSession(); //创建新对象后可以命中之前的session保存在二级缓存中的对象，所以下面的语句不会从数据库查询
		    user = (Company) s.get(userClass, "104110045110012"); 
		    printSecondLevelCache("get",st);
		    System.out.println("2在二级缓存中取数据:"+user.getCompanyName()); 
		    
		    s.clear();//不会影响上一个session保存入二级缓存的数据，可以命中上一个session保存入二级缓存的数据，，所以下面的语句不会从数据库查询
		    user = (Company) s.get(userClass, "104110045110012");      
		    printSecondLevelCache("get",st);
		    System.out.println("3在二级缓存中取数据:"+user.getCompanyName());
		} finally { 
		    if (s != null) 
		        s.close(); 
		}
	}

	public  static void printSecondLevelCache(String tag,Statistics st){
		
		 System.out.println("tag:"+tag); 
        System.out.println("put:" + st.getSecondLevelCachePutCount()); 
        System.out.println("hit:" + st.getSecondLevelCacheHitCount()); 
        System.out.println("miss:" + st.getSecondLevelCacheMissCount()); 
        System.out.println("load:" + st.getEntityLoadCount()); 
	}
	
}
