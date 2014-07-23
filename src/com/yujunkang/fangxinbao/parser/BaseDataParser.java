package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.User;



/**
 * 
 * @date 2014-5-30
 * @author xieb
 * 
 */
public class BaseDataParser extends AbstractParser<BaseData> {

	public BaseDataParser() {
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseData parse(JSONObject json) throws JSONException {
		
		return new BaseData();
	}


}


