package com.yujunkang.fangxinbao.activity;

import com.baidu.frontia.Frontia;
import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.FileUtils;
import com.yujunkang.fangxinbao.utility.LoggerTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PrelaunchActivity extends Activity {
	private StateHolder mStateHolder = new StateHolder();
	private FangXinBaoApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app = FangXinBaoApplication.getApplication(this);
		// 初始化百度api
		boolean isInit = Frontia.init(getApplicationContext(),
				DataConstants.BAIDU_PUSH_API_KEY);
		if (isInit) {
			LoggerTool.d("PrelaunchActivity", "Frontia init successful.");
		}
		// 更新数据
		app.requestUpdateCommonData();
		mStateHolder.startInitCommonDataTask();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mStateHolder.cancelAllTasks();
	}

	private class InitCommonDataTask extends
			AsyncTaskWithLoadingDialog<Void, Void, Void> {

		public InitCommonDataTask() {
			super(PrelaunchActivity.this, "", false);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			DBHelper db = DBHelper.getDBInstance(getApplication());
			Group<Country> dbData = db.queryAllCountries();
			//如果数据库没有数据，就把本地的数据导入数据库
			if (dbData == null || dbData.size() == 0) {
				FileUtils.initLocalCountry(PrelaunchActivity.this);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mStateHolder.cancelInitCommonDataTask();
			Intent intent = null;
			// 判断是否已经登录
			if (!app.isReady()) {
				intent = new Intent(PrelaunchActivity.this, MainActivity.class);
			} else {
				app.requestUpdateUser();
				intent = new Intent(PrelaunchActivity.this,
						UserMainActivity.class);
			}
			startActivity(intent);
			finish();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelInitCommonDataTask();
		}
	}

	private class StateHolder {
		private boolean mInitCommonDataTaskRunning = false;
		private InitCommonDataTask mInitCommonDataTask;

		public StateHolder() {

		}

		public void startInitCommonDataTask() {
			if (mInitCommonDataTaskRunning == false) {
				mInitCommonDataTaskRunning = true;
				mInitCommonDataTask = new InitCommonDataTask();
				mInitCommonDataTask.safeExecute();
			}
		}

		public void cancelInitCommonDataTask() {
			if (mInitCommonDataTask != null) {
				mInitCommonDataTask.cancel(true);
				mInitCommonDataTask = null;

			}
			mInitCommonDataTaskRunning = false;
		}

		public void cancelAllTasks() {
			cancelInitCommonDataTask();
		}
	}

}
