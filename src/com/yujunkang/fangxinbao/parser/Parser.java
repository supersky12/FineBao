package com.yujunkang.fangxinbao.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.model.Group;





/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public interface Parser <T extends BaseModel> {
    public abstract T parse(JSONObject json) throws JSONException;
    public abstract T consume(String content, Context context) throws JSONException;
    public Group parse(JSONArray array) throws JSONException;
   

}


