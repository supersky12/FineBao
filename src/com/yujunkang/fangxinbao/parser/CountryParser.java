package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yujunkang.fangxinbao.model.CommonData;
import com.yujunkang.fangxinbao.model.Country;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class CountryParser extends AbstractParser<Country> {
	public CountryParser() {
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public Country parse(JSONObject json) throws JSONException {
		Country result = new Country();
		if (json.has("zh")) {
			result.setName(json.getString("zh"));
		}
		if (json.has("en")) {
			result.setEngName(json.getString("en"));
		}
		if (json.has("c")) {
			result.setCountryCode(json.getString("c"));
		}
		if (json.has("s")) {
			result.setCountrySimpleCode(json.getString("s"));
		}
		if (json.has("p")) {
			result.setFirstLetter(json.getString("p"));
		}
		
		return result;
	}

	

	

	

}
