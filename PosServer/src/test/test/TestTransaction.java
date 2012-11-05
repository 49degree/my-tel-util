package test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import org.hibernate.Session;

import com.a3650.posserver.core.bean.Company;
import com.a3650.posserver.core.bean.PosTerminal;
import com.a3650.posserver.core.db.HibernateUtil;


public class TestTransaction {
	Class userClass = Company.class; 
	Class posClass = PosTerminal.class;
	Company user=null;
	PosTerminal pos = null;
	
	public static void main(String[] args){
		testConnPool();
	}
	
	public static void testConnPool(){ 
		try {
			Hashtable<String,String> env = new Hashtable<String,String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
			env.put(Context.PROVIDER_URL, "t3://localhost:7001"); //
			// 调EJB
			InitialContext ctx = new InitialContext(env);
//			Object obj = ctx.lookup("JNDI_POS_SERVER_DATASOURCE");
//		    Context envContext = (Context) new InitialContext();
		    DataSource ds = (DataSource) ctx.lookup("JNDI_POS_SERVER_DATASOURCE"); //查找配置
		    Connection conn = ds.getConnection();
		    Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select companyName from company");
		    while (rs.next()) {
		      System.out.println(rs.getString("companyName") + "<br>");
		    }

		} catch (Exception e) {
			e.printStackTrace();
		}
	


	} 


	
	
	public static void saveJDBC(){
		UserTransaction tx = null;
		try{
			tx = (UserTransaction)new InitialContext().lookup("javax.transaction.UserTransaction");
			tx.begin();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		Session s = HibernateUtil.getSessionFactory().openSession(); 
		//s.beginTransaction();

		
		
		Company user = new Company();
		user.setCompanyId("14");
		user.setCompanyName("testsave");
		PosTerminal pos = new PosTerminal();
		pos.setCompanyId(user.getCompanyId());
		pos.setPosId("1");
		pos.setPosName("test save");
		
		s.save(user);
		s.save(pos);
		
		s.flush();
		s.close();
		try{
			tx.commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
