package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.BaseData;

/**
 * 
 * @date 2014-5-21
 * @author xieb
 * 
 */
public class BabyParser extends AbstractParser<Baby> {

	public BabyParser() {

		// TODO Auto-generated constructor stub
	}

	@Override
	public Baby parse(JSONObject json) throws JSONException {
		Baby baby = new Baby();

		if (json.has("nickname")) {
			baby.setNickname(json.getString("nickname"));
		}
		if (json.has("uid")) {
			baby.setUid(json.getString("uid"));
		}
		if (json.has("id")) {
			baby.setId(json.getString("id"));
		}
		if (json.has("sex")) {
			baby.setSex(json.getString("sex"));
		}
		if (json.has("born")) {
			baby.setBorn(json.getString("born"));
		}
		if (json.has("photo")) {
			baby.setPhoto(json.getString("photo"));
		}

		return baby;
	}

}
