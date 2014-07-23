package com.yujunkang.fangxinbao.model;

import java.util.Map;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class BaseData implements BaseModel {
	public int code; // <code>0|1</code><!--请求结果：0、失败，1、成功-->
	public String desc; // <desc>sucess|其它描述内容</desc>
	

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	

}
