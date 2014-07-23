package com.yujunkang.fangxinbao.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

import org.json.JSONObject;

import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.parser.Parser;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.SDK11;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentActivity;

/**
 * 
 * @date 2014-5-22
 * @author xieb
 * 
 */
public class FangXinBaoAsyncTask<TResult extends BaseModel> extends
		AsyncTaskWithLoadingDialog<Void, Void, TResult> {
	private String _pid;
	private Parser<TResult> _parser;

	private Context _context;

	/*
	 * 统一调用静态方法创建实例，避免一个实例调用多次
	 */
	public static <T extends BaseModel> FangXinBaoAsyncTask<T> createInstance(
			Activity context, String actionName, Parser<T> parser) {
		return new FangXinBaoAsyncTask<T>(context, actionName, parser, true);
	}

	public static <T extends BaseModel> FangXinBaoAsyncTask<T> createInstance(
			Activity context, String actionName, Parser<T> parser,
			boolean enableLoadingIndicator) {
		return new FangXinBaoAsyncTask<T>(context, actionName, parser,
				enableLoadingIndicator);
	}

	public static <T extends BaseModel> FangXinBaoAsyncTask<T> createInstance(
			Activity context, String actionName, Parser<T> parser,
			String loading) {
		return new FangXinBaoAsyncTask<T>(context, actionName, parser, true,
				loading);
	}

	public static <T extends BaseModel> FangXinBaoAsyncTask<T> createInstance(
			Activity context, String actionName, Parser<T> parser,
			boolean isCancelable, String loading) {
		return new FangXinBaoAsyncTask<T>(context, actionName, parser,
				isCancelable, loading);
	}

	private FangXinBaoAsyncTask(Activity context, String actionName,
			Parser<TResult> parser, boolean isCancelable, String loading) {
		super(context, loading, isCancelable);
		_pid = actionName;
		_context = context;
		_parser = parser;
	}

	private FangXinBaoAsyncTask(Activity context, String actionName,
			Parser<TResult> parser, boolean enableLoadingIndicator) {
		super(context, enableLoadingIndicator);

		_pid = actionName;
		_context = context;
		_parser = parser;
	}

	/*
	 * 设置参数
	 */
	public void putParameter(String key, String value) {
		if (_query == null) {
			_query = new HashMap<String, String>();
		}
		_query.put(key, value);
	}

	/*
	 * 一次性赋值
	 */
	public void putAllParameter(Map<String, String> query) {
		if (_query == null) {
			_query = new HashMap<String, String>();
		}
		_query.putAll(query);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		executePrepare();
	}

	@Override
	protected TResult doInBackground(Void... params) {
		// 开始执行请求
		if (_query == null) {
			_query = new HashMap<String, String>();

		}
		_query.put(DataConstants.HTTP_PARAM_NAME_PID, _pid);
		_result = FangXinBaoApplication.getApplication(_context)
				.getNetWorKManager().fetchModel(_query, _parser);

		/*
		 * 回调，用于在请求完成以后，对数据进行后台处理
		 */
		executeFinishedBackground(_result);

		return _result;
	}

	public TResult manualExcute(Void... params) {
		return doInBackground(params);
	}

	@Override
	protected void onPostExecute(TResult result) {
		super.onPostExecute(result);
		executeFinished();
	}

}
