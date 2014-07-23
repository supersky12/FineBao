package com.yujunkang.fangxinbao.parser;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.model.Group;

/**
 * 
 * @date 2014-5-21
 * @author xieb
 * 
 */
public class GroupParser extends AbstractParser<Group<? extends BaseModel>> {

	private Parser<? extends BaseModel> mSubParser;

	public GroupParser(Parser<? extends BaseModel> subParser) {
		
		mSubParser = subParser;
	}

	
	


	/**
	 * When we encounter a JSONObject in a GroupParser, we expect one attribute
	 * named 'type', and then another JSONArray attribute.
	 */
	public Group<BaseModel> parse(JSONObject json) throws JSONException {

		Group<BaseModel> group = new Group<BaseModel>();

		Iterator<String> it = (Iterator<String>) json.keys();
		while (it.hasNext()) {
			String key = it.next();
			if (key.equals("type")) {
				group.setType(json.getString(key));
			} else {
				Object obj = json.get(key);
				if (obj instanceof JSONArray) {
					parse(group, (JSONArray) obj);
				} else {
					throw new JSONException("Could not parse data.");
				}
			}
		}

		return group;
	}

	/**
	 * Here we are getting a straight JSONArray and do not expect the 'type'
	 * attribute.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Group parse(JSONArray array) throws JSONException {

		Group<BaseModel> group = new Group<BaseModel>();
		parse(group, array);
		return group;
	}

	private void parse(Group group, JSONArray array) throws JSONException {
		for (int i = 0, m = array.length(); i < m; i++) {
			Object element = array.get(i);
			BaseModel item = null;
			if (element instanceof JSONArray) {
				item = mSubParser.parse((JSONArray) element);
			} else {
				item = mSubParser.parse((JSONObject) element);
			}

			group.add(item);
		}
	}

	

}
