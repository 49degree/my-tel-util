package init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.a3650.posserver.core.db.HibernateUtil;

public class Init{
	
	public static void main(String[] args) throws Exception{
		String[] params = args[0].split(";");
		Logger.getLogger(Init.class).error("SQL_FILE:"+params.length);
		if(params.length<2)
			throw new Exception("参数错误");
		if(!"".equals(params[0].trim()))
			HibernateUtil.setDatabaseConfig(params[0]);
		//服务器配置文件
		for(int i=1;i<params.length;i++){
			Logger.getLogger(Init.class).error("SQL_FILE:"+params[i]);
			initDatas(params[i]);
		}

	}
	
	
	public static void initDatas(String initDatas){
		
		Session session = HibernateUtil.getSessionFactory().openSession();//.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try{
			File file = new File(System.getProperty("user.dir")+File.separator+"bin"+File.separator+ initDatas);
			BufferedReader reader= new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
			
			String line = reader.readLine();
			while(line!=null){
				Logger.getLogger(Init.class).error("SQL:"+line); 
				if(line.indexOf("#")!=0&&!"".equals(line.trim())){
					session.connection().createStatement().executeUpdate(line);
				}
				line = reader.readLine();
			}
			tx.commit();
			
			
		}catch(Exception e){
			e.printStackTrace();
			tx.rollback();
		}
		HibernateUtil.shutdown();
	}
	
}
