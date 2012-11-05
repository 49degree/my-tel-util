package test;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.bean.ApsaiOrderPayInfo;
import com.a3650.posserver.core.bean.Company;
import com.a3650.posserver.core.dao.CglibImpl;
import com.a3650.posserver.core.service.impl.ApsaiOrderPayInfoBuss;
import com.a3650.posserver.core.service.impl.CompanyBuss;

public class TestBaseDaoProxy {
	static Logger logger = Logger.getLogger(CglibImpl.class);

	public static void main(String[] args) {
		testBaseBussProxy();

	}
	
	public static void testBaseDaoProxy(){
//		BaseDaoCglibProxy<ApsaiOrderPayInfo,Long,ApsaiOrderPayInfoDao> handler = 
//			new BaseDaoCglibProxy<ApsaiOrderPayInfo,Long,ApsaiOrderPayInfoDao>(new ApsaiOrderPayInfoDao());
//		ApsaiOrderPayInfoDao proxy = handler.getProxy();
//		ApsaiOrderPayInfo o = proxy.get(123483L);
//		logger.info("aaa:" + o.getPayTime());
//		
//		BaseDaoImplProxy<Company,String,CompanyDao> handler2 = 
//			new BaseDaoImplProxy<Company,String,CompanyDao>(new CompanyDao());
//		CompanyDao proxy2 = handler2.getProxy();
//		Company company = proxy2.get("104110045110012");;
//		logger.info("aaa:" + company.getCompanyName());
	}
	
	public static void testBaseBussProxy(){
		ApsaiOrderPayInfo o = new ApsaiOrderPayInfoBuss().getBaseDao().get(123483L);
		logger.info("aaa:" + o.getPayTime());

		Company company = new CompanyBuss().getBaseDao().get("104110045110012");
		logger.info("aaa:" + company.getCompanyName());
		company.setCompanyName("太平洋保险公司集团公司_块钱");
		new CompanyBuss().getBaseDao().update(company);
		company = new CompanyBuss().getBaseDao().get("104110045110012");
		logger.info("aaa:" + company.getCompanyName());
		
	}
	
}
