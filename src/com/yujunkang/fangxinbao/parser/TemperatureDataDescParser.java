package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.TemperatureDataDesc;

public class TemperatureDataDescParser extends AbstractParser<TemperatureDataDesc> {

	@Override
	public TemperatureDataDesc parse(JSONObject json) throws JSONException {
		TemperatureDataDesc result = new TemperatureDataDesc();
		if (json.has("shortDesc")) {
			result.setShortDesc(json.getString("shortDesc"));
		}
		if (json.has("longDesc")) {
			result.setLongDesc(json.getString("longDesc"));
		}
		if (json.has("url")) {
			result.setUrl(json.getString("url"));
		}
		return result;
	}

}
