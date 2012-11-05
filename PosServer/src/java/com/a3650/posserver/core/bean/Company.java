package com.a3650.posserver.core.bean;

import java.io.Serializable;

public class Company implements Serializable{  
    /**  
     *   
     */ 
    private static final long serialVersionUID = 1000000L;  
    
    private String companyId; 
    
    private String companyName;


	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}  
    
    
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	} 


}
