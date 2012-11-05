package test;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.a3650.posserver.core.bean.ApsaiOrder;
import com.a3650.posserver.core.bean.Company;
import com.a3650.posserver.core.bean.PosTerminal;
import com.a3650.posserver.core.db.HibernateUtil;

public class Init extends TestCase{

	public void testInit(){
		try{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			Transaction tx = session.beginTransaction();
			Company user = new Company();
			user.setCompanyId("104110045110012");
			user.setCompanyName("太平洋保险公司集团公司_快钱");
			session.save(user);
			

			
			PosTerminal ter = new PosTerminal();
			ter.setCompanyId(user.getCompanyId());
			ter.setPosId("20100601");
			ter.setPosName("太平洋保险公司保单销售");
			ter.setRootKey("DBED28F6415162BD");
			ter.setRootKeyLength(8);
			session.save(ter);
			
			ter = new PosTerminal();
			ter.setCompanyId(user.getCompanyId());
			ter.setPosId("20100602");
			ter.setPosName("太平洋保险公司保单销售2");
			ter.setRootKey("DBED28F6415162BD");
			ter.setRootKeyLength(8);
			session.save(ter);
			
			
			user = new Company();
			user.setCompanyId("309610182200003");
			user.setCompanyName("太平洋保险公司集团公司_通联");
			session.save(user);
			
			ter = new PosTerminal();
			ter.setCompanyId(user.getCompanyId());
			ter.setPosId("00030000");
			ter.setPosName("太平洋保险公司保单销售3");
			ter.setRootKey("D364FB15B07032B592FE0B8608B6C76EBF6885EFCB0426230DA2733846AB68492C76AEE6DAB3735404BF801AE5E583C78A0D922A769B4301B00D01C25E20DF134F4A0B3710B5DAE9DF2F32A7AB8334ECDFC19BE9BC34BA43D69B57AB1FEA25C2A19DFBDCE675F1DA38806ECEE6DC7F7A621FE5D08A9289312AC71040B0A2A4C8237073CDE60EDFAD6BBC6DEA376832E08607CB01BCDF3EC16491E901B9C81A19");
			ter.setRootKeyLength(16);
			session.save(ter);
			
			user = new Company();
			user.setCompanyId("309610156910001");
			user.setCompanyName("太平洋保险公司集团公司_通联");
			session.save(user);
			ter = new PosTerminal();
			ter.setCompanyId(user.getCompanyId());
			ter.setPosId("00010000");
			ter.setPosName("太平洋保险公司保单销售4");
			ter.setRootKey("D364FB15B07032B592FE0B8608B6C76EBF6885EFCB0426230DA2733846AB68492C76AEE6DAB3735404BF801AE5E583C78A0D922A769B4301B00D01C25E20DF134F4A0B3710B5DAE9DF2F32A7AB8334ECDFC19BE9BC34BA43D69B57AB1FEA25C2A19DFBDCE675F1DA38806ECEE6DC7F7A621FE5D08A9289312AC71040B0A2A4C8237073CDE60EDFAD6BBC6DEA376832E08607CB01BCDF3EC16491E901B9C81A19");
			ter.setRootKeyLength(16);
			session.save(ter);
			
			ApsaiOrder apsaiOrder = new ApsaiOrder();
			apsaiOrder.setApsaiId("1");
			apsaiOrder.setCustomName("测试保单");
			apsaiOrder.setCustomAddr("测试保单地址");
			apsaiOrder.setCustomLink("测试保单联系方式123");
			apsaiOrder.setApsaiAmount(100023L);
			session.save(apsaiOrder);
			
			tx.commit();
			
			//session.close();
			HibernateUtil.shutdown();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
}
