package com.yujunkang.fangxinbao.model;



/**
 * 
 * @date 2014-6-1
 * @author xieb
 * 
 */
public class Sex implements BaseModel {
	public Sex(String sexName, String Id) {
		super();
		this.sexName = sexName;
		this.mId = Id;
	}

	private String sexName;
	private String mId;
	
	
	
	
	public String getSexName() {
		return sexName;
	}

	public void setSexName(String sexName) {
		this.sexName = sexName;
	}

	public String getId() {
		return mId;
	}

	public void setId(String Id) {
		this.mId = Id;
	}

	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDesc(String desc) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCode(int code) {
		// TODO Auto-generated method stub

	}

}


