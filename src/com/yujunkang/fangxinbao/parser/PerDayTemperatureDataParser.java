package com.yujunkang.fangxinbao.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.TemperatureData;



/**
 * 
 * @date 2014-7-21
 * @author xieb
 * 
 */
public class PerDayTemperatureDataParser extends AbstractParser<Group<TemperatureData>> {

	public PerDayTemperatureDataParser() {

		
	}



	@Override
	public Group<TemperatureData> parse(JSONArray array) throws JSONException {
		return new GroupParser(new TemperatureDataParser()).parse(array);
	}



	@Override
	public Group<TemperatureData> parse(JSONObject json) throws JSONException {
		// TODO Auto-generated method stub
		return new Group<TemperatureData>();
	}

	
	
}


