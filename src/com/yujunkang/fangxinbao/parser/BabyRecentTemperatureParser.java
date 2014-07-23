package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.BabyRecentTemperature;

/**
 * 
 * @date 2014-7-10
 * @author xieb
 * 
 */
public class BabyRecentTemperatureParser extends
		AbstractParser<BabyRecentTemperature> {

	public BabyRecentTemperatureParser() {

		// TODO Auto-generated constructor stub
	}

	@Override
	public BabyRecentTemperature parse(JSONObject json) throws JSONException {
		BabyRecentTemperature result = new BabyRecentTemperature();

		if (json.has("temperature")) {
			result.setRecentData(new GroupParser(new BabyParser()).parse(json
					.getJSONArray("temperature")));
		}

		return result;
	}

}
