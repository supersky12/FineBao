package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.BabyRecentTemperature;
import com.yujunkang.fangxinbao.model.TemperatureData;



/**
 * 
 * @date 2014-7-10
 * @author xieb
 * 
 */
public class TemperatureDataParser extends AbstractParser<TemperatureData> {

	public TemperatureDataParser() {

		
	}

	@Override
	public TemperatureData parse(JSONObject json) throws JSONException {
		TemperatureData result = new TemperatureData();

		if (json.has("c")) {
			result.setTemperature(json.getString("c"));
		}
		if (json.has("d")) {
			result.setTime(json.getString("d"));
		}
		

		return result;
	}

}


