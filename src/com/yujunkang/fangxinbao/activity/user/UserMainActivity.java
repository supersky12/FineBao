package com.yujunkang.fangxinbao.activity.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.frontia.api.FrontiaSocialShareListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.update.UmengUpdateAgent;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.AboutActivity;
import com.yujunkang.fangxinbao.activity.SettingActivity;
import com.yujunkang.fangxinbao.activity.base.BaseSlidingActivity;
import com.yujunkang.fangxinbao.control.TemperatureCureView;
import com.yujunkang.fangxinbao.control.dialog.ISimpleDialogListener;
import com.yujunkang.fangxinbao.control.image.RoundedNetWorkImageView;
import com.yujunkang.fangxinbao.control.pulltorefresh.PullToRefreshBase;
import com.yujunkang.fangxinbao.control.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yujunkang.fangxinbao.control.pulltorefresh.PullToRefreshScrollView;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.BabyRecentTemperature;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.BabyRecentTemperatureParser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.utility.FocusChangedUtils;
import com.yujunkang.fangxinbao.utility.OnPageNotifyListener;
import com.yujunkang.fangxinbao.utility.ShareUtils;
import com.yujunkang.fangxinbao.utility.UiUtils;

/**
 * 
 * @date 2014-5-26
 * @author xieb
 * 
 */
public class UserMainActivity extends BaseSlidingActivity implements
		ISimpleDialogListener {
	private static final String TAG = "UserMainActivity";
	public static final String INTENT_EXTRA_USER = DataConstants.PACKAGE_NAME
			+ ".UserMainActivity.INTENT_EXTRA_USER";
	private static final int ACTIVITY_REQUEST_BABYINFO = 1;
	/**
	 * 控件
	 */
	private View btn_account;
	private View btn_health;
	private View btn_health_history;
	private View btn_health_info;
	private View btn_settings;
	private View btn_about;
	private View btn_menu;
	private View btn_refresh;
	private View btn_share;
	private TextView tv_baby_name;
	private TextView tv_temperature_desc;
	private View ll_head_content;
	private View rl_title;
	private PullToRefreshScrollView mPullRefreshScrollView;
	private ScrollView mScrollView;
	private TextView tv_refresh_record;
	private TextView tv_temperature_status;
	private View ll_connection_record;
	private View progress_connection;
	private TemperatureCureView temperature_curveView;
	private RoundedNetWorkImageView iv_babyPhoto;
	private TextView tv_temperature_continued;
	private TextView tv_temperature_max;
	private View lay_cure;
	private View lay_recent;

	private User mUserInfo;
	private boolean hasBaby = false;
	private int times = 0;
	private Baby mCurrentBaby;
	private StateHolder mStateHolder = new StateHolder();
	private Handler uiHandler = new Handler();

	private Runnable closeMenu = new Runnable() {

		@Override
		public void run() {
			getSlidingMenu().showContent(false);

		}
	};

	/**
	 * 授权回调
	 */
	private FrontiaSocialShareListener shareListener = new FrontiaSocialShareListener() {

		@Override
		public void onSuccess() {
			Log.d(TAG, "share success");
		}

		@Override
		public void onFailure(int errCode, String errMsg) {
			Log.d(TAG, String.format("share errCode : %s ,share errMsg : %s ",
					errCode, errMsg));
		}

		@Override
		public void onCancel() {
			Log.d(TAG, "cancel ");
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_main_activity);
		initSlidingMenu(R.layout.menu_settings_layout);
		init();
		initControl();
		ensureUi();
		UmengUpdateAgent.update(this);
		
	}

	@Override
	protected void initData() {
		super.initData();
		Intent data = getIntent();

		if (data.hasExtra(INTENT_EXTRA_USER)) {
			mUserInfo = (User) data.getParcelableExtra(INTENT_EXTRA_USER);
		} else {
			mUserInfo = Preferences.getUserInfo(getSelfContext());
		}

	}

	private void init() {

	}

	private void initControl() {
		btn_menu = findViewById(R.id.btn_menu);
		btn_refresh = findViewById(R.id.btn_refresh);
		btn_share = findViewById(R.id.btn_share);
		ll_head_content = findViewById(R.id.ll_head_content);
		rl_title = findViewById(R.id.rl_title);
		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		tv_temperature_desc = (TextView) findViewById(R.id.tv_temperature_desc);
		tv_temperature_status = (TextView) findViewById(R.id.tv_temperature_status);
		tv_refresh_record = (TextView) findViewById(R.id.tv_refresh_record);
		ll_connection_record = findViewById(R.id.ll_connection_record);
		progress_connection = findViewById(R.id.progress_connection);
		temperature_curveView = (TemperatureCureView) findViewById(R.id.temperature_curveView);
		// 持续发热时间
		tv_temperature_continued = (TextView) findViewById(R.id.tv_temperature_continued);
		// 最高温度
		tv_temperature_max = (TextView) findViewById(R.id.tv_temperature_max);

		lay_cure = findViewById(R.id.lay_cure);
		lay_recent = findViewById(R.id.lay_recent);
		initMenuControl();
	}

	/**
	 * 初始化菜单控件
	 */
	private void initMenuControl() {
		btn_account = findViewById(R.id.btn_account);
		iv_babyPhoto = (RoundedNetWorkImageView) findViewById(R.id.iv_baby_photo);
		tv_baby_name = (TextView) findViewById(R.id.tv_baby_name);
		// 宝宝健康
		btn_health = findViewById(R.id.btn_health);
		btn_health_history = findViewById(R.id.btn_health_history);
		btn_health_info = findViewById(R.id.btn_health_info);
		btn_settings = findViewById(R.id.btn_settings);
		btn_about = findViewById(R.id.btn_about);

	}

	private void ensureUi() {
		ensureTitleUi();
		ensureSlidingMenuUi(null);
		ensureMainUi();
		ensureTemperatureCureUi(null);
	}

	/**
	 * 初始化菜单控件
	 */
	private void ensureTitleUi() {
		FocusChangedUtils.setViewFocusChanged(btn_menu);
		FocusChangedUtils.setViewFocusChanged(btn_refresh);
		FocusChangedUtils.setViewFocusChanged(btn_share);
		FocusChangedUtils.expandTouchArea(btn_menu, 30, 10, 30, 10);
		FocusChangedUtils.expandTouchArea(btn_refresh);
		FocusChangedUtils.expandTouchArea(btn_share);
		btn_menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SlidingMenu settingsMenu = getSlidingMenu();
				if (settingsMenu.isMenuShowing()) {
					settingsMenu.showContent();
				} else {
					settingsMenu.showMenu();

				}

			}
		});

		btn_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ShareData share = null;
				// if (FangXinBaoSettings.CODE_DEBUG) {
				// share = new ShareData();
				// share.setWeixinTitle("放心宝");
				// share.setWeixinUrl("www.baidu.com");
				// share.setWeixinMsg("测试");
				// Bitmap imageData = BitmapFactory.decodeResource(
				// getResources(), R.drawable.ic_launcher);
				// share.setWeixinBytes(BitmapUtils.bmpToByteArray(imageData));
				// share.setWeixinApiType(ShareData.WEBPAGE);
				// }
				ShareUtils.showShareContent(getSelfContext(), "放心宝", "测试",
						null, null, shareListener);
			}
		});
		btn_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mStateHolder.startGetDataTask(true);
			}
		});

	}

	private void ensureMainUi() {

		// 下拉刷新
		mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						ll_connection_record.setVisibility(View.INVISIBLE);
						mStateHolder.startGetDataTask(false);
					}
				});

		mScrollView = mPullRefreshScrollView.getRefreshableView();
		mPullRefreshScrollView.getLoadingLayoutProxy().setPullLabel(
				getString(R.string.pull_to_refresh_device_pull_label));
		mPullRefreshScrollView.getLoadingLayoutProxy().setRefreshingLabel(
				getString(R.string.pull_to_refresh_device_refreshing_label));
		mPullRefreshScrollView.getLoadingLayoutProxy().setReleaseLabel(
				getString(R.string.pull_to_refresh_device_release_label));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		SlidingMenu settingsMenu = getSlidingMenu();
		if (keyCode == KeyEvent.KEYCODE_BACK && !settingsMenu.isMenuShowing()) {
			showOKOrCancelDialog(getString(R.string.exit_app),
					getString(R.string.dialog_cancel_text),
					getString(R.string.exit_app_prompt));
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 侧滑菜单
	 */
	private void ensureSlidingMenuUi(Baby baby) {
		if (baby != null) {
			hasBaby = true;
			mCurrentBaby = baby;
			iv_babyPhoto.loadImage(mCurrentBaby.getPhoto(), false, null);
			tv_baby_name.setText(mCurrentBaby.getNickname());
		} else {
			if (mUserInfo != null && mUserInfo.getBaBies() != null
					&& mUserInfo.getBaBies().size() > 0) {
				// 显示第一个宝宝的图像
				hasBaby = true;
				mCurrentBaby = mUserInfo.getBaBies().get(0);
				iv_babyPhoto.loadImage(mCurrentBaby.getPhoto(), false, null);
				tv_baby_name.setText(mCurrentBaby.getNickname());
			}

		}

		btn_account.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasBaby) {
					startBabyInfoActivity();
					uiHandler.postDelayed(closeMenu, 500);

				} else {
					UiUtils.showAlertDialog("", getSelfContext());
				}

			}
		});
		btn_health.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				uiHandler.postDelayed(closeMenu, 500);

			}
		});

		btn_health_history.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startBabyHistoryRecorderActivity();
				uiHandler.postDelayed(closeMenu, 500);

			}
		});
		btn_about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startAboutActivity();
				uiHandler.postDelayed(closeMenu, 500);
			}
		});
		btn_settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startSettingsActivity();
				uiHandler.postDelayed(closeMenu, 500);
			}
		});

	}

	private void ensureTemperatureCureUi(BabyRecentTemperature data) {
		if (data != null) {

			lay_recent.setVisibility(View.VISIBLE);
			Group<TemperatureData> temperatureDatas = data.getRecentData();
			if (temperatureDatas != null && temperatureDatas.size() > 0) {
				lay_cure.setVisibility(View.VISIBLE);
				temperature_curveView.initData(temperatureDatas);
			} else {
				lay_cure.setVisibility(View.GONE);
			}
			// 持续时间
			String duration = data.getDuration();
			if (!TextUtils.isEmpty(duration)) {
				tv_temperature_continued.setText(duration);
			} else {
				tv_temperature_continued.setText("--");
			}
			// 最高温度
			String maxTemperature = data.getMaxTemperature();
			if (!TextUtils.isEmpty(maxTemperature)) {
				tv_temperature_max.setText(maxTemperature);
			} else {
				tv_temperature_max.setText("--");
			}
		} else {
			lay_cure.setVisibility(View.GONE);
			lay_recent.setVisibility(View.GONE);
			 
			// 测试代码
			
//			Group<TemperatureData> testDatas = new Group<TemperatureData>();
//			Date now = new Date();
//			for (int index = 0; index < 6; index++) {
//				TemperatureData item = new TemperatureData();
//				String time = String.valueOf(now.getTime()/1000 + 10*60
//						* index);
//				if (index == 0) {
//					item.setTemperature("37.5");
//				} else if (index == 1) {
//					item.setTemperature("37.8");
//				} else if (index == 2) {
//					item.setTemperature("38.7");
//				} else if (index == 3) {
//					item.setTemperature("38.1");
//				} else if (index == 4) {
//					item.setTemperature("38.5");
//				} else if (index == 5) {
//					item.setTemperature("38.5");
//				}
//				item.setTime(time);
//				testDatas.add(item);
//			}
//			DBHelper db = DBHelper.getDBInstance(getSelfContext());
//			db.batchInsertTemperatureData(mUserInfo.getId(),
//					mCurrentBaby.getId(), testDatas);
			DBHelper db = DBHelper.getDBInstance(getSelfContext());
			Group<TemperatureData> result = db.queryTemperatureDataByDate(
					mCurrentBaby.getId(), "2014-07-15");
			if (result != null && result.size() > 0) {
				lay_cure.setVisibility(View.VISIBLE);
				temperature_curveView.initData(result);
			}
			
		}

	}

	/**
	 * 获取最新的温度数据
	 */
	private void fetchRecentData() {

		FangXinBaoAsyncTask<BabyRecentTemperature> mTask = FangXinBaoAsyncTask
				.createInstance(getSelfContext(),
						UrlManager.URL_FETCH_RECENT_TEMPERATURE_DATA,
						new BabyRecentTemperatureParser(),
						getString(R.string.loading));
		mTask.putParameter("bbid", mCurrentBaby.getId());
		mTask.setOnFinishedListener(new OnFinishedListener<BabyRecentTemperature>() {
			@Override
			public void onFininshed(BabyRecentTemperature result) {
				if (result == null) {

				} else {
					if (result.code == 1) {
						ensureTemperatureCureUi(result);
					} else {
						UiUtils.showAlertDialog(result.desc, getSelfContext());
					}
				}
			}
		});
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	private void startSettingsActivity() {
		Intent intent = new Intent(getSelfContext(), SettingActivity.class);
		startActivity(intent);
	}

	private void startAboutActivity() {
		Intent intent = new Intent(getSelfContext(), AboutActivity.class);
		startActivity(intent);
	}

	private void startBabyHistoryRecorderActivity() {
		Intent intent = new Intent(getSelfContext(),
				BabyHistoryRecordActivity.class);
		intent.putExtra(BabyHistoryRecordActivity.INTENT_EXTRA_BABY,
				mCurrentBaby);
		startActivity(intent);
	}

	private void startBabyInfoActivity() {
		Intent intent = new Intent(getSelfContext(), UserBabyInfoActivity.class);
		intent.putExtra(INTENT_EXTRA_USER, mUserInfo);
		intent.putExtra(UserBabyInfoActivity.INTENT_EXTRA_BABY, mCurrentBaby);
		startActivity(intent);
	}

	public OnPageNotifyListener generatePageNotifyListener() {
		return new OnPageNotifyListener() {

			@Override
			public void onNotify(int what, Bundle data) {
				switch (what) {
				case CommonAction.UPDATE_BABY_INFO: {
					// 更新宝宝信息
					if (data != null) {
						Baby baby = (Baby) data
								.getParcelable(UserBabyInfoActivity.INTENT_EXTRA_BABY);
						Group<Baby> babies = mUserInfo.getBaBies();
						for (Baby item : babies) {
							if (item.getId().equals(baby.getId())) {
								try {
									String itemPhoto = item.getPhoto();
									item.setBorn(baby.getBorn());
									item.setNickname(baby.getNickname());
									item.setPhoto(baby.getPhoto());
									item.setSex(baby.getSex());
									// 判断是否需要更新图片
									if (!itemPhoto.equals(baby.getPhoto())) {
										iv_babyPhoto.loadImage(baby.getPhoto(),
												false, null);
									}
									tv_baby_name.setText(baby.getNickname());
								} catch (Exception ex) {

								}
							}

						}
						mCurrentBaby = baby;
					}
					break;
				}
				case CommonAction.ACTIVITY_USERMAN_UPDATE_USER_INFO: {
					// 更新宝宝信息
					mUserInfo = Preferences.getUserInfo(getSelfContext());
					ensureSlidingMenuUi(null);
					break;
				}
				case CommonAction.ADD_BABY: {
					// 更新宝宝信息
					mUserInfo = Preferences.getUserInfo(getSelfContext());
					if (data != null) {
						Baby baby = (Baby) data
								.getParcelable(UserBabyInfoActivity.INTENT_EXTRA_BABY);
						ensureSlidingMenuUi(baby);
					}
					break;
				}
				case CommonAction.SWITCH_BABY: {
					// 更新宝宝信息
					if (data != null) {
						Baby baby = (Baby) data
								.getParcelable(UserBabyInfoActivity.INTENT_EXTRA_BABY);
						ensureSlidingMenuUi(baby);
					}
					break;
				}

				}

			}
		};
	}

	/**
	 * 刷新完毕调用
	 */
	private void onRefreshComplete() {
		switch (times % 3) {
		case 0: {
			ll_head_content
					.setBackgroundResource(R.drawable.temperature_normal_content_bg);
			rl_title.setBackgroundResource(R.drawable.temperature_normal_title_bg);
			tv_temperature_status.setText("体温正常");
			tv_temperature_desc
					.setText(getString(R.string.temperature_normal_desc));
			break;
		}
		case 1: {
			ll_head_content
					.setBackgroundResource(R.drawable.temperature_middle_content_bg);
			rl_title.setBackgroundResource(R.drawable.temperature_middle_title_bg);
			tv_temperature_status.setText("发烧中");
			tv_temperature_desc
					.setText(getString(R.string.temperature_middle_desc));
			break;
		}
		case 2: {
			ll_head_content
					.setBackgroundResource(R.drawable.temperature_high_content_bg);
			rl_title.setBackgroundResource(R.drawable.temperature_high_title_bg);
			tv_temperature_status.setText("极度高烧");
			tv_temperature_desc
					.setText(getString(R.string.temperature_max_desc));
			break;
		}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mStateHolder.cancelAlltasks();

	}

	/**
	 * 测试用
	 * 
	 * @author xieb
	 * 
	 */
	private class GetDataTask extends
			AsyncTaskWithLoadingDialog<Void, Void, String[]> {

		public GetDataTask(boolean dialogEnable) {
			super(UserMainActivity.this, dialogEnable);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(3000);
				times++;
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			mStateHolder.cancelGetDataTask();
			onRefreshComplete();
			mPullRefreshScrollView.onRefreshComplete();
			progress_connection.setVisibility(View.GONE);
			ll_connection_record.setVisibility(View.VISIBLE);
			StringBuilder sb = new StringBuilder();
			sb.append(VeDate.getUserDate("HH:mm"));
			sb.append(getString(R.string.checked));
			tv_refresh_record.setText(sb.toString());

		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			mStateHolder.cancelGetDataTask();
		}

	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		finish();

	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
		// TODO Auto-generated method stub

	}

	private class StateHolder {
		private GetDataTask mGetDataTask; // 得到验证码
		boolean isGetDataTaskRunning = false;

		public StateHolder() {

		}

		public void startGetDataTask(boolean enableDialog) {
			if (!isGetDataTaskRunning) {
				isGetDataTaskRunning = true;
				mGetDataTask = new GetDataTask(enableDialog);
				mGetDataTask.safeExecute();
			}
		}

		public void cancelGetDataTask() {
			if (mGetDataTask != null) {
				mGetDataTask.cancel(true);
				mGetDataTask = null;
			}
			isGetDataTaskRunning = false;

		}

		public void cancelAlltasks() {

			cancelGetDataTask();
		}
	}
}
