package model;

import java.io.Serializable;
import javax.persistence.Column;  
import javax.persistence.Entity;  
import javax.persistence.GeneratedValue;  
import javax.persistence.GenerationType;  
import javax.persistence.Id;  
import javax.persistence.Table; 

@Entity 
@Table(name="user")  
public class User implements Serializable{  
    /**  
     *   
     */ 
    private static final long serialVersionUID = 1L;  
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)  
    @Column(name = "ID")  
    private long id; 
    
    @Column(name="name")  
    private String name;  
    
    @Column(name="password")  
    private String password; 
    
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
