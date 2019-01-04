package br.com.diogo.example.models;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;

public class User implements Serializable, Principal{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8679913943607840307L;
	private long id;
	private String email;
	private String password;
	private String gcmRegId;
	private Date lastLogin;
	private Date lastGCMRegister;
	private String role;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGcmRegId() {
		return gcmRegId;
	}
	public void setGcmRegId(String gcmRegId) {
		this.gcmRegId = gcmRegId;
	}
	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	public Date getLastGCMRegister() {
		return lastGCMRegister;
	}
	public void setLastGCMRegister(Date lastGCMRegister) {
		this.lastGCMRegister = lastGCMRegister;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@Override
	public String getName() {
		return this.email;
	}
}
