package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.User;

/**
 * 
 * @date 2014-5-21
 * @author xieb
 * 
 */
public class UserParser extends AbstractParser<User> {

	public UserParser() {

		// TODO Auto-generated constructor stub
	}

	@Override
	public User parse(JSONObject json) throws JSONException {
		User user = new User();
		if (json.has("babies")) {
			user.setBaBies(new GroupParser(new BabyParser()).parse(json
					.getJSONArray("babies")));
		}
		if (json.has("phone")) {
			user.setPhone(json.getString("phone"));
		}
		if (json.has("id")) {
			user.setId(json.getString("id"));
		}

		if (json.has("email")) {
			user.setEmail(json.getString("email"));
		}
		if (json.has("nationality")) {
			Country country = new Country();
			country.setCountryCode(json.getString("nationality"));
			user.setCountry(country);
		}

		return user;
	}

	@Override
	public void onParserComplete(Context context, User result) {
		// TODO Auto-generated method stub
		super.onParserComplete(context, result);
		if (result.code == 1) {
			if (result.getCountry() != null
					&& !TextUtils.isEmpty(result.getCountry().getCountryCode())) {
				DBHelper mDatabaseHelper = DBHelper.getDBInstance(context);
				Country country = mDatabaseHelper.queryCountryByCode(result
						.getCountry().getCountryCode());
				result.setCountry(country);
			}
		}

	}

}
