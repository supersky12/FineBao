package com.yujunkang.fangxinbao.model;

import java.io.Serializable;



/**
 * 
 * @date 2014-6-19
 * @author xieb
 * 
 */
public class UserAccount implements Serializable {
	public static final int LOGINTYPE_PHONE = 1;
	public static final int LOGINTYPE_EMAIL = 2;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6421754169547276186L;
	private int loginType;
	private String account;
	private String password;
	public int getLoginType() {
		return loginType;
	}
	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	
}


