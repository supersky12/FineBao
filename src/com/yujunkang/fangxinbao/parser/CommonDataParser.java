package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.model.CommonData;
import com.yujunkang.fangxinbao.utility.TypeUtils;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class CommonDataParser extends AbstractParser<CommonData> {
	
	private String temperatureCommonDataJson = null;
	public CommonDataParser() {
		
		// TODO Auto-generated constructor stub
	}



	@Override
	public CommonData parse(JSONObject json) throws JSONException {
		CommonData result = new CommonData();
		if (json.has("country")) {
			result.setCountries(new GroupParser(new CountryParser()).parse(json
					.getJSONArray("country")));
		}
		if (json.has("dverc")) {
			result.setCountryVersion(json.getString("dverc"));
		}
		if (json.has("dvert")) {
			result.setTemperatureVersion(json.getString("dvert"));
		}
		if (json.has("timestamp")) {
			result.setTimestamp(TypeUtils.StringToLong(json
					.getString("timestamp")));
		}
		if (json.has("timestamp")) {
			result.setTimestamp(TypeUtils.StringToLong(json
					.getString("timestamp")));
		}
		
		if (json.has("temperature")) {
			temperatureCommonDataJson = json
					.getJSONArray("temperature").toString();
			result.setTemperatureCommonDatas(new GroupParser(new TemperatureCommonDataPareser()).parse(json
					.getJSONArray("temperature")));
		}
		return result;
	}

	
	
	@Override
	public void onParserComplete(Context context, CommonData result) {
		FangXinBaoApplication app = FangXinBaoApplication.getApplication(context);
		// 返回了时间戳
		if (result.getTimestamp() > 0) {
			app.setTimeStampOffset(result.getTimestamp()
					- System.currentTimeMillis());
		}
		if (result.getCountries()!= null&&result.getCountries().size()>0) {
			Method.updateCommonData(context, result);
		}
		if (!TextUtils.isEmpty(temperatureCommonDataJson)) {
			Method.updateTemperatureCommonData(context, temperatureCommonDataJson,result.getTemperatureVersion());
		}
		
	}



	



	

	

	

}
