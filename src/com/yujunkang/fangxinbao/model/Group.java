package com.yujunkang.fangxinbao.model;

import java.util.ArrayList;
import java.util.Collection;





/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class Group <T extends BaseModel> extends ArrayList<T> implements BaseModel {

    private static final long serialVersionUID = 1L;

    private String mType;
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
    public Group() {
        super();
    }
    
    public Group(Collection<T> collection) {
        super(collection);
    }

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }

	
	

}


