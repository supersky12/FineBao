package com.yujunkang.fangxinbao.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yujunkang.fangxinbao.execption.FangXinBaoCredentialsException;
import com.yujunkang.fangxinbao.execption.FangXinBaoException;
import com.yujunkang.fangxinbao.execption.FangXinBaoParseException;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.parser.Parser;

/**
 * 
 * @date 2014-6-2
 * @author xieb
 * 
 */
public class JsonUtils {
	private static final String TAG = "JsonUtils";
	/*
	 * 从JSON对象获取JSON数组
	 */
	public static JSONArray getJarrInJobj(JSONObject jobj, String key) {
		try {
			if (jobj.has(key)) {
				return jobj.getJSONArray(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getStrInJobj(String jsonStr, String key) {
		try {
			JSONObject json = new JSONObject(jsonStr);
			return getStrInJobj(json, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 从JSON对象获取对应key值
	 */
	public static String getStrInJobj(JSONObject json, String key) {
		try {
			if (json != null && json.has(key)) {
				return json.getString(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 从JSON对象获取Key对象
	 */
	public static Object getObjInJobj(JSONObject json, String key) {
		try {
			if (json != null && json.has(key)) {
				return json.get(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getJsonInJobj(String jsonStr, String key) {
		try {
			JSONObject json = new JSONObject(jsonStr);
			return getJsonInJobj(json, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getJsonFromString(String jsonStr) {
		try {
			JSONObject json = new JSONObject(jsonStr);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 从JSON对象获取Key对象
	 */
	public static JSONObject getJsonInJobj(JSONObject json, String key) {
		try {
			if (json != null && json.has(key)) {
				return json.getJSONObject(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getStrInJarr(JSONArray jarr, int index) {
		try {
			if (jarr != null && jarr.length() > index) {
				return jarr.getString(index);
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static JSONObject getJobjInJarr(JSONArray jarr, int index) {
		try {
			if (jarr != null && jarr.length() > index) {
				return jarr.getJSONObject(index);
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static Map<String, String> parseJSON2Map(String jsonStr) {
		try {

			Map<String, String> result = new HashMap<String, String>();
			JSONObject jsonObject = new JSONObject(jsonStr);
			Iterator<String> iterator = jsonObject.keys();
			String key = null;
			String value = null;
			while (iterator.hasNext()) {
				key = iterator.next();
				value = jsonObject.getString(key);
				result.put(key, value);
			}
			return result;
		} catch (Exception ex) {
			return null;
		}
	}

	public static Object jsonToMap(Object json) {
		if (json == JSONObject.NULL) {
			return null;
		}

		if (json instanceof JSONObject) {
			Map<String, Object> ret = new HashMap<String, Object>();
			JSONObject jObj = (JSONObject) json;
			try {
				Iterator<String> it = jObj.keys();
				while (it.hasNext()) {
					String key = it.next();
					Object value = jObj.get(key);
					value = jsonToMap(value);
					ret.put(key, value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ret;

		} else if (json instanceof JSONArray) {
			List<Object> ret = new ArrayList<Object>();
			JSONArray jArr = (JSONArray) json;
			try {
				for (int i = 0; i < jArr.length(); i++) {
					Object value = jArr.get(i);
					value = jsonToMap(value);
					ret.add(value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ret;
		}
		return json;
	}

	
}
