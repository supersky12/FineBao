package com.yujunkang.fangxinbao.task;

import java.util.Map;

import android.os.AsyncTask;



/**
 * 
 * @date 2014-5-22
 * @author xieb
 * 
 */
public class AsyncTaskWrapper<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	protected Result _result = null;
	protected Map<String, String> _query;
	protected int _resultCode = 0;
	protected String _resultMsg = null;

	public Map<String, String> getTaskQuery() {
		return _query;
	}

	protected OnFinishedListener<Result> mOnFinishedListener;
	private OnFinishedWithTaskListener<Result> mOnFinishedWithTaskListener;
	private OnFinishedWithStatusAndTaskListener<Result> OnFinishedWithTaskAndTaskListener;
	private OnFinishedWithStatusListener<Result> mOnFinishedWithStatusListener;
	private OnFinishedBackgroundListener<Result> mOnFinishedBackgroundListener;
	private OnPrepareTaskListener mOnPrepareTaskListener;
	private OnCancleListener mOnCancleListener;
	private OnErrorListener mOnErrorListener;

	public OnErrorListener getmOnErrorListener() {
		return mOnErrorListener;
	}

	
	
	
	public OnPrepareTaskListener getOnPrepareTaskListener() {
		return mOnPrepareTaskListener;
	}




	public void setOnPrepareTaskListener(
			OnPrepareTaskListener OnPrepareTaskListener) {
		this.mOnPrepareTaskListener = OnPrepareTaskListener;
	}




	public void setmOnErrorListener(OnErrorListener mOnErrorListener) {
		this.mOnErrorListener = mOnErrorListener;
	}

	/*
	 * 设置异步完成事件
	 */
	public void setOnFinishedListener(OnFinishedListener<Result> l) {
		mOnFinishedListener = l;
	}

	/*
	 * 设置异步完成事件
	 */
	public void setOnFinishedListener(OnFinishedWithTaskListener<Result> l) {
		mOnFinishedWithTaskListener = l;
	}

	/*
	 * 设置异步完成事件
	 */
	public void setOnFinishedListener(
			OnFinishedWithStatusAndTaskListener<Result> l) {
		OnFinishedWithTaskAndTaskListener = l;
	}

	/*
	 * 设置异步完成事件
	 */
	public void setOnFinishedListener(OnFinishedWithStatusListener<Result> l) {
		mOnFinishedWithStatusListener = l;
	}

	/*
	 * 设置数据抓取完成事件
	 */
	public void setOnFinishedBackgroundListener(
			OnFinishedBackgroundListener<Result> l) {
		mOnFinishedBackgroundListener = l;
	}

	public OnCancleListener getOnCancleListener() {
		return mOnCancleListener;
	}

	public void setOnCancleListener(OnCancleListener mOnCancleListener) {
		this.mOnCancleListener = mOnCancleListener;
	}

	protected void executePrepare() {
		if(mOnPrepareTaskListener!=null)
		{
			mOnPrepareTaskListener.onPreExecute();
		}
	}
	
	/*
	 * 执行完成
	 */
	@SuppressWarnings("unchecked")
	protected void executeFinished() {
		if (mOnFinishedListener != null) {
			mOnFinishedListener.onFininshed(_result);
		}
		if (mOnFinishedWithTaskListener != null) {
			mOnFinishedWithTaskListener.onFininshed(_result,
					(AsyncTaskWrapper<Void, Void, Result>) this);
		}
		if (OnFinishedWithTaskAndTaskListener != null) {
			OnFinishedWithTaskAndTaskListener.onFininshed(_result, _resultCode,
					_resultMsg, (AsyncTaskWrapper<Void, Void, Result>) this);
		}
		if (mOnFinishedWithStatusListener != null) {
			mOnFinishedWithStatusListener.onFininshed(_result, _resultCode,
					_resultMsg);
		}
	}

	protected void executeFinishedBackground(Result result) {
		if (mOnFinishedBackgroundListener != null) {
			mOnFinishedBackgroundListener.onFinishedBackground(result);
		}
	}

	/**
	 * 请求结束回调
	 */
	public interface OnFinishedListener<TResult> {
		void onFininshed(TResult result);
	}

	/**
	 * 请求执行前
	 */
	public interface OnPrepareTaskListener {
		void onPreExecute();
	}
	
	/**
	 * 请求结束回调，带请求参数，便于识别
	 */
	public interface OnFinishedWithTaskListener<TResult> {
		void onFininshed(TResult result,
				AsyncTaskWrapper<Void, Void, TResult> task);
	}

	/**
	 * 请求结束回调，带请求参数，便于识别
	 */
	public interface OnFinishedWithStatusListener<TResult> {
		void onFininshed(TResult result, int statusCode, String statusMsg);
	}

	/**
	 * 请求结束回调，带请求参数，便于识别
	 */
	public interface OnFinishedWithStatusAndTaskListener<TResult> {
		void onFininshed(TResult result, int statusCode, String statusMsg,
				AsyncTaskWrapper<Void, Void, TResult> task);
	}

	/*
	 * 用于完成数据请求以后，后台的数据处理
	 */
	public interface OnFinishedBackgroundListener<TResult> {
		void onFinishedBackground(TResult result);
	}

	/*
	 * 在被取消后调用
	 */
	public interface OnCancleListener {
		void onCancleEvent();
	}

	/*
	 * 在网络请求失败后或则服务器返回失败后调用 需要手动调用
	 */
	public interface OnErrorListener {
		void onErrorEvent();
	}

	@Override
	protected Result doInBackground(Params... params) {
		return null;
	}

	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		_result = result;
		
	}

	protected void setTaskResult(int resultCode, String resultMsg) {
		_resultCode = resultCode;
		_resultMsg = resultMsg;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (null != mOnCancleListener) {
			mOnCancleListener.onCancleEvent();
		}
	}

}
