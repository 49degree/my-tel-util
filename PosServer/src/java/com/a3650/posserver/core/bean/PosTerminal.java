package com.a3650.posserver.core.bean;

import java.io.Serializable;

public class PosTerminal implements Serializable{  
    /**  
     *   
     */ 
    private static final long serialVersionUID = 1L;  
    private String posId; 
    private String companyId; 
    private String posName;
    private String rootKey;//分配的主密钥，使用|分割
    private int rootKeyLength;//主密钥长度



	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getPosName() {
		return posName;
	}

	public void setPosName(String posName) {
		this.posName = posName;
	}  
    
    
	public String getRootKey() {
		return rootKey;
	}

	public void setRootKey(String rootKey) {
		this.rootKey = rootKey;
	}

	public int getRootKeyLength() {
		return rootKeyLength;
	}

	public void setRootKeyLength(int rootKeyLength) {
		this.rootKeyLength = rootKeyLength;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	} 


}
