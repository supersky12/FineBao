package com.yujunkang.fangxinbao.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.umeng.analytics.MobclickAgent;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.cache.BitmapLruCache;
import com.yujunkang.fangxinbao.control.dialog.SimpleDialogFragment;
import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.utility.DialogHelper;
import com.yujunkang.fangxinbao.utility.FocusChangedUtils;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.Notifiable;
import com.yujunkang.fangxinbao.utility.OnPageNotifyListener;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public abstract class ActivityWrapper extends FragmentActivity implements
		Notifiable, View.OnClickListener {
	private static final String TAG = "ActivityWrapper";
	
	private Handler mTagBackgroundHandler = null;
	private Runnable mTagBackgroundRunnable = null;
	protected FangXinBaoApplication mApplication = null;
	private Handler mNotifyHandler = null;
	public static Activity onCreateActivity;
	public static Activity onResumeActivity;
	protected DialogHelper mDialog;
	protected BitmapLruCache mFileCache;
	protected List<FangXinBaoAsyncTask<? extends BaseModel>> mAsyncTasks = new ArrayList<FangXinBaoAsyncTask<? extends BaseModel>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
		initData();

	}

	protected void initData() {

	}

	private void initialize() {
		mDialog = new DialogHelper(this);

		onCreateActivity = this;
		mApplication = (FangXinBaoApplication) getApplication();
		mFileCache = mApplication.getBitmapCache();
		// 设置语言为启动时的语言
		if (!Locale.getDefault().getCountry()
				.equals(mApplication.getLocale().getCountry())) {
			Locale locale = mApplication.getLocale();
			Locale.setDefault(locale);
			Configuration config = getBaseContext().getResources()
					.getConfiguration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LoggerTool.d(TAG, "onConfigurationChanged");

	}

	/*
	 * 生成页面通知监听，如果页面创建了监听器，页面的通知接收器讲放入池中
	 */
	@Override
	public OnPageNotifyListener generatePageNotifyListener() {
		return null;
	}

	/*
	 * 获取Application的Context
	 */
	public Context getContext() {
		return getApplication();
	}

	/*
	 * 获取Activity的Context
	 */
	public FragmentActivity getSelfContext() {
		return this;
	}

	/*
	 * 发送广播通知
	 */
	public void sendNotificationBroad(int what, Bundle data) {
		FangXinBaoApplication.notifyHandlerPool(what, data);
	}

	/*
	 * 发送广播通知
	 */
	public void sendNotificationBroad(int what) {
		sendNotificationBroad(what, null);
	}

	/*
	 * 发现路由通知
	 */
	public void sendRouteNotificationRoute(String[] activityNames, int what,
			Bundle data) {
		FangXinBaoApplication.notifyHandlers(activityNames, what, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		onResumeActivity = this;
		/*
		 * 注册通知
		 */
		final OnPageNotifyListener notifyListener = generatePageNotifyListener();

		if (mNotifyHandler == null) {

			mNotifyHandler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					switch (msg.what) {
					case CommonAction.CLOSE_ALL_ACTIVITY:
						finish();
						break;
					}
					if (null != notifyListener) {
						notifyListener.onNotify(msg.what, msg.getData());
					}
					return false;
				}
			});

			FangXinBaoApplication.pushHandlerPool(this.getClass().getName(),
					mNotifyHandler);
		}

		View btn_back = findViewById(R.id.btn_back);
		if (btn_back != null) {
			FocusChangedUtils.setViewFocusChanged(btn_back);
			FocusChangedUtils.expandTouchArea(btn_back, 20, 20, 20, 20);
			btn_back.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		if (FangXinBaoApplication.IS_FOREGROUND == false) {

			LoggerTool.d(TAG, "FOREGROUND");
		}

	}

	protected void putAsyncTask(
			FangXinBaoAsyncTask<? extends BaseModel> asyncTask) {
		mAsyncTasks.add(asyncTask);
	}

	protected void clearAsyncTask() {
		Iterator<FangXinBaoAsyncTask<? extends BaseModel>> iterator = mAsyncTasks
				.iterator();
		while (iterator.hasNext()) {
			FangXinBaoAsyncTask<? extends BaseModel> asyncTask = iterator
					.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
				asyncTask = null;
			}
		}
		mAsyncTasks.clear();
	}

	public void showOKOrCancelDialog(String ok, String cancel, String message,
			int requestCode) {
		SimpleDialogFragment
				.createBuilder(getSelfContext(), getSupportFragmentManager())
				.setMessage(message).setPositiveButtonText(ok)
				.setNegativeButtonText(cancel).setRequestCode(requestCode)
				.show();

	}

	public void showConfirmDialog(String cancel, String message) {
		SimpleDialogFragment
				.createBuilder(getSelfContext(), getSupportFragmentManager())
				.setMessage(message).setNegativeButtonText(cancel).show();

	}

	public void showOKOrCancelDialog(String ok, String cancel, String message) {
		showOKOrCancelDialog(ok, cancel, message, -1);

	}

	@Override
	protected void onPause() {
		onResumeActivity = null;
		if (mTagBackgroundHandler == null || mTagBackgroundRunnable == null) {
			/*
			 * 设置程序后台标识
			 */
			mTagBackgroundHandler = new Handler();
			mTagBackgroundRunnable = new Runnable() {
				@Override
				public void run() {
					final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					List<RunningAppProcessInfo> Applist = activityManager
							.getRunningAppProcesses();
					Iterator<RunningAppProcessInfo> l = Applist.iterator();
					String packegName = getPackageName();
					while (l.hasNext()) {
						RunningAppProcessInfo AppInfo = (RunningAppProcessInfo) l
								.next();
						if (AppInfo.processName.equals(packegName)) {
							if (AppInfo.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

								FangXinBaoApplication.IS_FOREGROUND = false;// 保存应用程序当前的状态

								LoggerTool.d(TAG, "BACKGROUND");
							}
							break;
						}
					}
				}
			};
		}
		mTagBackgroundHandler.removeCallbacks(mTagBackgroundRunnable);
		mTagBackgroundHandler.postDelayed(mTagBackgroundRunnable, 1000);
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		clearAsyncTask();
		super.onDestroy();
		FangXinBaoApplication.popHandlerPool(this.getClass().getName());
	}

}
