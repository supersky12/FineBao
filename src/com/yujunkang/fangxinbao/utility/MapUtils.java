package com.yujunkang.fangxinbao.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;



/**
 * 
 * @date 2013-8-13
 * @author xieb
 * 
 */
public class MapUtils {

	
	
	public static ArrayList<HashMap<String, String>> getArrayListInMap(Map source, String key) {
		try {
			if (source.containsKey(key)) {
				return (ArrayList<HashMap<String, String>>)source.get(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static String getStringInMap(Map source, String key) {
		try {
			if (source.containsKey(key)) {
				return (String) source.get(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object getObjectInMap(Map source, String key) {
		try {
			if (source.containsKey(key)) {
				return  source.get(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}



