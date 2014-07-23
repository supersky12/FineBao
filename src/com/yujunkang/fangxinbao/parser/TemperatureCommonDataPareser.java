package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.TemperatureCommonData;



/**
 * 
 * @date 2014-7-22
 * @author xieb
 * 
 */
public class TemperatureCommonDataPareser extends AbstractParser<TemperatureCommonData> {

	public TemperatureCommonDataPareser() {

		
	}

	@Override
	public TemperatureCommonData parse(JSONObject json) throws JSONException {
		TemperatureCommonData result = new TemperatureCommonData();

		if (json.has("en")) {
			result.setDesc_en(new TemperatureDataDescParser().parse(json.getJSONObject("en")));
		}
		if (json.has("zh")) {
			result.setDesc_zh(new TemperatureDataDescParser().parse(json.getJSONObject("zh")));
		}
		if (json.has("t")) {
			result.setRange(json.getString("t"));
		}

		return result;
	}

}


