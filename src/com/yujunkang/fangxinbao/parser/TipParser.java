package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.Tip;



/**
 * 
 * @date 2014-7-17
 * @author xieb
 * 
 */
public class TipParser extends AbstractParser<Tip> {

	public TipParser() {

		// TODO Auto-generated constructor stub
	}

	@Override
	public Tip parse(JSONObject json) throws JSONException {
		Tip tip = new Tip();

		if (json.has("id")) {
			tip.setId(json.getString("id"));
		}
		if (json.has("comment")) {
			tip.setContent(json.getString("comment"));
		}
		return tip;
	}

}


