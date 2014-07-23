package com.yujunkang.fangxinbao.parser;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yujunkang.fangxinbao.execption.FangXinBaoCredentialsException;
import com.yujunkang.fangxinbao.execption.FangXinBaoException;
import com.yujunkang.fangxinbao.execption.FangXinBaoParseException;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.utility.JsonUtils;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.TypeUtils;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public abstract class AbstractParser<T extends BaseModel> implements Parser<T> {
	private static final String TAG = "AbstractParser";

	protected Context mContext;

	public AbstractParser() {
		super();

	}

	public T consume(String content, Context context) {
		mContext = context;
		LoggerTool.d(TAG, "http response: " + content);
		try {
			T result = null;
			JSONObject json = new JSONObject(content);
			if (json.has("data")) {
				Object dataJson = json.get("data");
				if (dataJson instanceof JSONArray) {
					result = (T) parse((JSONArray) dataJson);
				} else if (dataJson instanceof JSONObject) {
					result = (T) parse((JSONObject) dataJson);
				} else {
					result = (T) parse((JSONObject) json);
				}
			} else {
				result = (T) parse((JSONObject) json);
			}
			parserInner(json, result);
			return result;

		} catch (Exception ex) {
			LoggerTool.e(TAG, ex.getMessage());
		}
		return null;
	}

	public void onParserComplete(Context context, T result) {
		// TODO Auto-generated method stub

	}

	public void parserInner(JSONObject json, T result) {
		if (json.has("code")) {
			result.setCode(TypeUtils.StringToInt(JsonUtils.getStrInJobj(json,
					"code")));
		}
		if (json.has("desc")) {
			result.setDesc(JsonUtils.getStrInJobj(json, "desc"));

		}
		if (json.has("uid")) {
			Method.storeUid(mContext, JsonUtils.getStrInJobj(json, "uid"));
		}
		onParserComplete(mContext, (T) result);
	}

	public abstract T parse(JSONObject json) throws JSONException;

	/**
	 * 解析json数组
	 */
	public Group parse(JSONArray array) throws JSONException {
		throw new JSONException("Unexpected JSONArray parse type encountered.");
	}
}