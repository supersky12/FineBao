package com.yujunkang.fangxinbao.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaApplication;
import com.umeng.analytics.MobclickAgent;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.user.UserBabyInfoActivity;
import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.cache.BitmapLruCache;
import com.yujunkang.fangxinbao.execption.CrashHandler;
import com.yujunkang.fangxinbao.http.FangXinBaoHttpApi;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.CommonData;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.BaseDataParser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.AlbumUtils;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.IconUtils;
import com.yujunkang.fangxinbao.utility.JavaLoggingHandler;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class FangXinBaoApplication extends FrontiaApplication {
	private static final String TAG = "FangXinBaoApplication";
	private static final boolean DEBUG = FangXinBaoSettings.DEBUG;
	static {
		Logger.getLogger("com.yujunkang.fangxinbao").addHandler(
				new JavaLoggingHandler());
		Logger.getLogger("ccom.yujunkang.fangxinbao").setLevel(Level.ALL);
	}
	public static final String INTENT_ACTION_LOGGED_IN = DataConstants.PACKAGE_NAME
			+ ".intent.action.LOGGED_IN";
	public static final String INTENT_ACTION_LOGGED_OUT = DataConstants.PACKAGE_NAME
			+ ".intent.action.LOGGED_OUT";

	private TaskHandler mTaskHandler;
	private HandlerThread mTaskThread;
	public static boolean IS_FOREGROUND = false;
	private SharedPreferences mPrefs;
	private BitmapLruCache mCache;
	private NetWorKManager mNetWorKManager;
	private File cacheLocation;
	private long timeStampOffset = 0;
	private Locale mLocale = null;
	private StateHolder mStateHolder = new StateHolder();

	/*
	 * 存放所有activity的通讯处理器
	 */
	private static Map<String, Handler> mActivityHandlerPool = new HashMap<String, Handler>();
	/**
	 * 异步消息列表
	 * */
	private static List<AysncMessage> mAsynMessagePeddingList = new LinkedList<AysncMessage>();

	@Override
	public void onCreate() {
		super.onCreate();
		AlbumUtils.initialize(this);
		DataConstants.init(this);
		if(FangXinBaoSettings.DEBUG)
		{
			MobclickAgent.setDebugMode(true);
		}
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mLocale = Locale.getDefault();

		
		float screenDensity = getApplicationContext().getResources()
				.getDisplayMetrics().density;
		IconUtils.get().setRequestHighDensityIcons(screenDensity > 1.0f);
		
		mTaskThread = new HandlerThread(TAG + "-AsyncThread");
		mTaskThread.start();
		mTaskHandler = new TaskHandler(mTaskThread.getLooper());

		//图片缓存
		loadResourceManagers();

		// 初始化全局统一异常处理
		// CrashHandler.getInstance(this).init();

		
		loadFangXinBaoApi();
	}

	public boolean isReady() {
		User user = Preferences.getUserCommonInfo(this);
		if (user == null) {
			return false;
		}
		if (TextUtils.isEmpty(user.getId())) {
			return false;
		}
		return true;
	}

	public Locale getLocale() {
		return mLocale;
	}

	public void setLocale(Locale Locale) {
		this.mLocale = Locale;
	}

	public void requestStartService() {
		mTaskHandler.sendMessage( //
				mTaskHandler.obtainMessage(TaskHandler.MESSAGE_START_SERVICE));
	}

	public void requestUpdateUser() {
		mTaskHandler.sendEmptyMessage(TaskHandler.MESSAGE_UPDATE_USER);
	}

	public void requestUpdateCommonData() {
		mTaskHandler.sendEmptyMessage(TaskHandler.MESSAGE_START_FETCH_DATA);
	}

	public BitmapLruCache getBitmapCache() {
		return mCache;
	}

	public long getTimeStampOffset() {
		return timeStampOffset;
	}

	public void setTimeStampOffset(long timeStampOffset) {
		this.timeStampOffset = timeStampOffset;
	}

	public NetWorKManager getNetWorKManager() {
		return mNetWorKManager;
	}

	public void setNetWorKManager(NetWorKManager NetWorKManager) {
		this.mNetWorKManager = NetWorKManager;
	}

	private void loadFangXinBaoApi() {
		// Try logging in and setting up oauth, then user
		// credentials.
		String domain = getResources().getString(R.string.mDomain);
		mNetWorKManager = new NetWorKManager(FangXinBaoHttpApi.createHttpApi(
				domain, this, false));

		LoggerTool.d(TAG, "loadCredentials()");
		String phoneNumber = mPrefs.getString(Preferences.PREFERENCE_LOGIN,
				null);
		String password = mPrefs.getString(Preferences.PREFERENCE_PASSWORD,
				null);
		mNetWorKManager.setCredentials(phoneNumber, password);
		
		
	}

	private void loadResourceManagers() {
		// We probably don't have SD card access if we get an
		// IllegalStateException. If it did, lets
		// at least have some sort of disk cache so that things don't npe when
		// trying to access the
		// resource managers.
		try {

			if (DEBUG)
				Log.d(TAG, "Attempting to load RemoteResourceManager(cache)");
			// If we have external storage use it for the disk cache. Otherwise
			// we use
			// the cache dir
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				cacheLocation = new File(
						Environment.getExternalStorageDirectory()
								+ DataConstants.FILE_CACHE_PATH);
			} else {
				cacheLocation = new File(getFilesDir()
						+ DataConstants.FILE_CACHE_PATH);
			}
			cacheLocation.mkdirs();

			BitmapLruCache.Builder builder = new BitmapLruCache.Builder(this);
			builder.setMemoryCacheEnabled(true)
					.setMemoryCacheMaxSizeUsingHeapSize();
			builder.setDiskCacheEnabled(true).setDiskCacheLocation(
					cacheLocation);
			mCache = builder.build();
		} catch (IllegalStateException e) {

			LoggerTool.d(TAG,
					"Falling back to NullDiskCache for RemoteResourceManager");
		}
	}

	private enum PeddingMsgType {
		NEW_ASYN_MESSAGE, NEW_HANDLER, REMOVE_HANDLER;
	}

	public static FangXinBaoApplication getApplication(Context context) {
		return (FangXinBaoApplication) context.getApplicationContext();
	}

	public File getCacheLocation() {
		return cacheLocation;
	}

	public void setCacheLocation(File cacheLocation) {
		this.cacheLocation = cacheLocation;
	}

	public SharedPreferences getPrefs() {
		return mPrefs;
	}

	public void setPrefs(SharedPreferences Prefs) {
		this.mPrefs = Prefs;
	}

	/**
	 * 发送异步消息
	 * 
	 * expireTime 几秒以后失效
	 * */
	public static synchronized void sendAsynMessage(Message msg, String flag,
			int expireTime) {
		checkPeddingMessage(PeddingMsgType.NEW_ASYN_MESSAGE, "");
		Handler handler = mActivityHandlerPool.get(flag);
		if (null != handler) {
			handler.sendMessage(msg);
		} else {
			AysncMessage asm = new AysncMessage(msg, flag, expireTime);
			mAsynMessagePeddingList.add(asm);
		}
	}

	/*
	 * 每个Activity对应一个Handler，初始化时候入池
	 */
	public static synchronized void pushHandlerPool(String flag, Handler handler) {
		if (mActivityHandlerPool == null) {
			mActivityHandlerPool = new HashMap<String, Handler>();
		}
		mActivityHandlerPool.put(flag, handler);

		// 当有handler加入的时候应该检查它的msg数据
		checkPeddingMessage(PeddingMsgType.NEW_HANDLER, flag);
	}

	/*
	 * Activity销毁的时候，移除
	 */
	public static synchronized void popHandlerPool(String flag) {
		if (mActivityHandlerPool != null
				&& mActivityHandlerPool.containsKey(flag)) {
			mActivityHandlerPool.remove(flag);
		}

		checkPeddingMessage(PeddingMsgType.REMOVE_HANDLER, flag);
	}

	/**
	 * 检查过期的message
	 * 
	 * NEW_ASYN_MESSAGE newMessage NEW_HANDLER new Handler REMOVE_HANDLER remove
	 * Handler
	 * */
	private static void checkPeddingMessage(PeddingMsgType state, String flag) {

		if (mAsynMessagePeddingList.size() > 0) {
			long currrentClock = SystemClock.elapsedRealtime();
			ArrayList<AysncMessage> removed = new ArrayList<AysncMessage>();
			for (AysncMessage msg : mAsynMessagePeddingList) {
				if (msg.expireTime < currrentClock) {
					removed.add(msg);
					continue;
				}

				switch (state) {
				case NEW_ASYN_MESSAGE:
					break;
				case NEW_HANDLER:
					if (flag.equals(msg.flag)) {
						removed.add(msg);
						Handler handler = mActivityHandlerPool.get(flag);
						if (null != handler) {
							handler.sendMessage(msg.msg);
						}
					}
					break;
				case REMOVE_HANDLER:
					if (flag.equals(msg.flag)) {
						removed.add(msg);
					}
					break;
				}
			}

			if (removed.size() > 0) {
				mAsynMessagePeddingList.removeAll(removed);
			}

		}
	}

	/*
	 * 通知所有Handler处理
	 */
	public static void notifyHandlerPool(int what, Bundle data) {
		if (mActivityHandlerPool == null) {
			Log.d(TAG, "没有Handler可以接受消息, what:" + what);
			return;
		}
		for (Handler handler : mActivityHandlerPool.values()) {
			Message msg = Message.obtain();
			msg.what = what;
			msg.setData(data);
			handler.sendMessageDelayed(msg, 0);
		}
	}

	public static void notifyHandlers(String[] activityNames, int what,
			Bundle data) {
		for (String flag : activityNames) {
			Message msg = Message.obtain();
			msg.what = what;
			msg.setData(data);
			if (mActivityHandlerPool != null
					&& mActivityHandlerPool.containsKey(flag)) {
				mActivityHandlerPool.get(flag).sendMessageDelayed(msg, 0);
			}
		}
	}

	/**
	 * Set up resource managers on the application depending on SD card state.
	 * 
	 * @author xieb
	 */
	private class MediaCardStateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (DEBUG)
				Log.d(TAG, "Media state changed, reloading resource managers:"
						+ intent.getAction());
			if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
				loadResourceManagers();
			} else if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
				loadResourceManagers();
			}
		}

		public void register() {
			// Register our media card broadcast receiver so we can
			// enable/disable the cache as
			// appropriate.
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
			intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
			// intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
			// intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
			// intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
			// intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
			// intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
			// intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
			// intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
			intentFilter.addDataScheme("file");
			registerReceiver(this, intentFilter);
		}
	}

	

	private static class AysncMessage {
		Message msg;
		String flag;
		long expireTime;

		AysncMessage(Message msg, String flag, long expireTime) {
			this.msg = msg;
			this.flag = flag;
			this.expireTime = SystemClock.elapsedRealtime() + expireTime;
		}
	}

	private class TaskHandler extends Handler {

		private static final int MESSAGE_UPDATE_USER = 1;
		private static final int MESSAGE_START_SERVICE = 2;
		private static final int MESSAGE_START_FETCH_DATA = 3;

		public TaskHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (DEBUG)
				Log.d(TAG, "handleMessage: " + msg.what);

			switch (msg.what) {
			case MESSAGE_UPDATE_USER:
				try {
					// Update user info
					Log.d(TAG, "Updating user.");

					mStateHolder.startFetchUserInfoTask();
				} catch (Exception e) {
					if (DEBUG)
						Log.d(TAG, "FangxinbaoException", e);
				}
				return;

			case MESSAGE_START_SERVICE:

				return;
			case MESSAGE_START_FETCH_DATA:

				mStateHolder.startFetchCommonDataTask();

				return;

			}

		}
	}

	/**
	 * 获取用户信息
	 * 
	 * @author supersky
	 * 
	 */
	private class FetchUserInfoTask extends
			AsyncTaskWithLoadingDialog<Void, Void, User> {

		public FetchUserInfoTask() {
			super(null, false);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected User doInBackground(Void... params) {

			return mNetWorKManager.user();
		}

		@Override
		protected void onPostExecute(User result) {
			super.onPostExecute(result);
			mStateHolder.cancelFetchUserInfoTask();
			if (result == null) {

			} else {
				if (result.code == 1) {
					LoggerTool.d(TAG, "setting user info.");
					Preferences.storeUser(FangXinBaoApplication.this, result);
					FangXinBaoApplication.notifyHandlers(
							new String[] { UserMainActivity.class.getName() },
							CommonAction.ACTIVITY_USERMAN_UPDATE_USER_INFO,
							null);
				} else {

				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelFetchUserInfoTask();
		}
	}

	/**
	 * 获取基础数据
	 * 
	 * @author supersky
	 * 
	 */
	private class FetchCommonDataTask extends
			AsyncTaskWithLoadingDialog<Void, Void, CommonData> {

		public FetchCommonDataTask() {
			super(null, false);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected CommonData doInBackground(Void... params) {
			return mNetWorKManager.fetchCommonData();
		}

		@Override
		protected void onPostExecute(CommonData result) {
			super.onPostExecute(result);
			mStateHolder.cancelFetchCommonDataTask();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelFetchCommonDataTask();
		}
	}

	private class StateHolder {

		private FetchCommonDataTask mFetchCommonDataTask; // 得到基础数据
		boolean isFetchCommonDataTaskRunning = false;
		private FetchUserInfoTask mFetchUserInfoTask; // 得到用户信息
		boolean isFetchUserInfoTaskRunning = false;

		public StateHolder() {

		}

		public void startFetchCommonDataTask() {
			if (!isFetchCommonDataTaskRunning) {
				isFetchCommonDataTaskRunning = true;
				mFetchCommonDataTask = new FetchCommonDataTask();
				mFetchCommonDataTask.safeExecute();
			}
		}

		public void cancelFetchCommonDataTask() {
			if (mFetchCommonDataTask != null) {
				mFetchCommonDataTask.cancel(true);
				mFetchCommonDataTask = null;
			}
			isFetchCommonDataTaskRunning = false;

		}

		public void startFetchUserInfoTask() {
			if (!isFetchUserInfoTaskRunning) {
				isFetchUserInfoTaskRunning = true;
				mFetchUserInfoTask = new FetchUserInfoTask();
				mFetchUserInfoTask.safeExecute();
			}
		}

		public void cancelFetchUserInfoTask() {
			if (mFetchUserInfoTask != null) {
				mFetchUserInfoTask.cancel(true);
				mFetchUserInfoTask = null;
			}
			isFetchUserInfoTaskRunning = false;

		}

		public void cancelAlltasks() {

			cancelFetchCommonDataTask();
			cancelFetchUserInfoTask();
		}
	}
}
