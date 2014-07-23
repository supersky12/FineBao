package com.yujunkang.fangxinbao.activity.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.AnimatorListenerAdapter;
import com.actionbarsherlock.internal.nineoldandroids.animation.AnimatorSet;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.app.FangXinBaoSettings;
import com.yujunkang.fangxinbao.control.TemperatureCureView;
import com.yujunkang.fangxinbao.control.calendar.CalendarCardPager;
import com.yujunkang.fangxinbao.control.calendar.CalendarCardPager.OnDateSelectedListener;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.model.Tip;
import com.yujunkang.fangxinbao.parser.PerDayTemperatureDataParser;
import com.yujunkang.fangxinbao.parser.TipParser;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedBackgroundListener;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.CalendarUtil;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;

/**
 * 
 * @date 2014-6-16
 * @author xieb
 * 
 */
public class BabyHistoryRecordActivity extends ActivityWrapper {
	private static final String TAG = "BabyHistoryRecordActivity";
	public static final String INTENT_EXTRA_BABY = DataConstants.PACKAGE_NAME
			+ ".BabyHistoryRecordActivity.INTENT_EXTRA_BABY";
	private static final int REQUEST_ACTIVITY_CODE_EDIT_TIP = 1;
	/**
	 * 控件
	 */
	private CalendarCardPager calendar_view;
	private TextView tv_title;
	private TemperatureCureView temperature_curveView;
	private TextView btn_more;
	private TextView tv_week;
	private TextView tv_holiday;
	private TextView tv_birthday;
	private View ll_holiday;
	private View ll_edit_tip;
	private View ll_note;
	private View tv_add;
	private TextView tv_note;
	private View btn_edit_note;
	private View tv_empty_data;

	private boolean isExpand = true;
	private int remailHeight;
	private int calendarOriginalHeight;
	private Baby mBaby = null;
	private Date mSelectedDate = null;
	private Tip mCurrentTip = null;
	private Handler handler = new Handler();
	private StateHolder mStateHolder = new StateHolder();
	private ArrayList<TemperatureData> datas = new ArrayList<TemperatureData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baby_health_history_activity);
		init();
		initControl();
		ensureUi();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				calendarOriginalHeight = calendar_view.getHeight();
				remailHeight = calendar_view.getRemainHeight();
				LayoutParams params = (LayoutParams) temperature_curveView
						.getLayoutParams();
				params.height = calendarOriginalHeight - remailHeight;
				temperature_curveView.setLayoutParams(params);
				// temperature_curveView.initData(datas);
			}
		}, 1000);
		startGetDayTipTask(mSelectedDate);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		Intent intent = getIntent();
		if (intent.hasExtra(INTENT_EXTRA_BABY)) {
			mBaby = intent.getParcelableExtra(INTENT_EXTRA_BABY);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 编辑备忘录
		case R.id.btn_edit_note: {
			startEditTipActivity(2);
			break;
		}
		// 添加备忘录
		case R.id.ll_note: {
			startEditTipActivity(1);
			break;
		}

		case R.id.btn_more: {
			if (isExpand) {
				// calendar_view.getCurrentContentView().executeCollapseAnimation();
				calendarOriginalHeight = calendar_view.getHeight();
				remailHeight = calendar_view.getRemainHeight();
				final ViewGroup.LayoutParams lp = calendar_view
						.getLayoutParams();
				final int originalHeight = calendarOriginalHeight;
				ValueAnimator animator = ValueAnimator.ofInt(originalHeight,
						remailHeight);
				animator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(final Animator animator) {
						isExpand = false;
					}
				});
				animator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(
							final ValueAnimator valueAnimator) {
						lp.height = (Integer) valueAnimator.getAnimatedValue();
						calendar_view.setLayoutParams(lp);
					}
				});
				animator.setDuration(600);
				animator.start();
				btn_more.setText(getString(R.string.temperature_expand));
			} else {
				ValueAnimator heightAnimator = ValueAnimator.ofInt(
						remailHeight, calendarOriginalHeight);
				heightAnimator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(final ValueAnimator animation) {
						ViewGroup.LayoutParams layoutParams = calendar_view
								.getLayoutParams();
						layoutParams.height = (Integer) animation
								.getAnimatedValue();
						calendar_view.setLayoutParams(layoutParams);
					}
				});
				heightAnimator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						isExpand = true;

					}
				});
				heightAnimator.setDuration(600);
				heightAnimator.start();
				btn_more.setText(getString(R.string.temperature_collapse));
			}

			break;
		}
		}
	}

	/**
	 * 启动编辑备忘录窗体
	 * 
	 * @param type
	 *            1:添加 2:编辑
	 */
	private void startEditTipActivity(int type) {
		Intent intent = new Intent(getSelfContext(),
				EditHealthTipActivity.class);
		if (type == 1) {
			intent.putExtra(EditHealthTipActivity.INTENT_EXTRA_BABYID,
					mBaby.getId());
			intent.putExtra(EditHealthTipActivity.INTENT_EXTRA_DATE,
					mSelectedDate);

		} else if (type == 2) {
			intent.putExtra(EditHealthTipActivity.INTENT_EXTRA_TIP, mCurrentTip);
		}
		startActivityForResult(intent, REQUEST_ACTIVITY_CODE_EDIT_TIP);
	}

	public String getTemperatureValue(double temper) {
		try {

			BigDecimal decimal = new BigDecimal(temper);

			return String.valueOf(decimal.setScale(1, BigDecimal.ROUND_HALF_UP)
					.doubleValue());
		} catch (Exception ex) {

		}
		return "";
	}

	private void init() {
		// 初始化日历控件
		mSelectedDate = new Date();
	}

	private void initControl() {
		btn_more = (TextView) findViewById(R.id.btn_more);
		calendar_view = (CalendarCardPager) findViewById(R.id.calendar_view);
		tv_title = (TextView) findViewById(R.id.tv_title);

		btn_more.setOnClickListener(this);
		temperature_curveView = (TemperatureCureView) findViewById(R.id.temperature_curveView);
		// 节日
		ll_holiday = findViewById(R.id.ll_holiday);
		tv_week = (TextView) findViewById(R.id.tv_week);
		tv_holiday = (TextView) findViewById(R.id.tv_holiday);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);

		ll_holiday = findViewById(R.id.ll_holiday);
		tv_empty_data = findViewById(R.id.tv_empty_data);
		// 备忘录
		ll_note = findViewById(R.id.ll_note);
		tv_add = findViewById(R.id.tv_add);
		ll_edit_tip = findViewById(R.id.ll_edit_tip);
		tv_note = (TextView) findViewById(R.id.tv_note);
		btn_edit_note = findViewById(R.id.btn_edit_note);
	}

	private void ensureUi() {

		calendar_view.init(mSelectedDate);
		tv_title.setText(VeDate.DateToStr(new Date().getTime(),
				getString(R.string.month_name_format)));
		calendar_view.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, arg0);
				tv_title.setText(VeDate.DateToStr(cal.getTime().getTime(),
						getString(R.string.month_name_format)));

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		calendar_view.setDateSelectedListener(new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDateSelected(Date date) {
				mSelectedDate = date;
				palyAnimation();
				ensureHolidayUi();
				startGetDayTemperatureTask(mSelectedDate);
				startGetDayTipTask(mSelectedDate);
			}
		});
		if (FangXinBaoSettings.CODE_DEBUG) {
			for (int index = 0; index < 3; index++) {
				TemperatureData data = new TemperatureData();
				if (index == 0) {
					data.setTemperature("40.5");
					data.setTime("18:00");
				} else if (index == 1) {
					data.setTemperature("37.5");
					data.setTime("19:00");
				} else if (index == 2) {
					data.setTemperature("39");
					data.setTime("20:00");
				} else if (index == 3) {
					data.setTemperature("38.1");
					data.setTime("21:00");
				} else if (index == 4) {
					data.setTemperature("38.5");
					data.setTime("22:00");
				} else if (index == 5) {
					data.setTemperature("38.5");
					data.setTime("24:00");
				} else if (index == 6) {
					data.setTemperature("40.5");
					data.setTime("1:00");
				} else if (index == 7) {
					data.setTemperature("39.5");
					data.setTime("2:00");
				}
				datas.add(data);
			}
		}
		startGetDayTemperatureTask(mSelectedDate);
		ensureHolidayUi();
	}

	private void ensureHolidayUi() {
		if (mSelectedDate != null) {
			tv_week.setText(VeDate.getWeek(mSelectedDate));
			tv_holiday.setText(CalendarUtil.getNLInfo(
					VeDate.getYearOfDate(mSelectedDate),
					VeDate.getMonthOfDate(mSelectedDate),
					VeDate.getDayOfDate(mSelectedDate)));

			StringBuilder sb = new StringBuilder();
			sb.append(VeDate.getYearOfTwoDates(mSelectedDate,
					VeDate.strToDate(mBaby.getBorn())));
			sb.append("天");
			tv_birthday.setText(sb.toString());

		}
	}

	/**
	 * 备忘录
	 */
	private void ensureTipUi() {
		if (mCurrentTip != null && !TextUtils.isEmpty(mCurrentTip.getContent())) {
			ll_edit_tip.setVisibility(View.VISIBLE);
			tv_note.setText(mCurrentTip.getContent());
			tv_add.setVisibility(View.GONE);
			ll_note.setOnClickListener(null);
			ll_note.setClickable(false);
			btn_edit_note.setOnClickListener(this);
		} else {
			ll_edit_tip.setVisibility(View.GONE);
			tv_add.setVisibility(View.VISIBLE);
			ll_note.setOnClickListener(this);
			ll_note.setClickable(true);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_ACTIVITY_CODE_EDIT_TIP: {
				if (data != null) {
					if (data.hasExtra(EditHealthTipActivity.INTENT_EXTRA_TIP)) {
						mCurrentTip = (Tip) data
								.getParcelableExtra(EditHealthTipActivity.INTENT_EXTRA_TIP);
						ensureTipUi();
					}

				}
				break;
			}

			}

		}
	}

	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mStateHolder.cancelAlltasks();
	}

	private void startGetDayTipTask(Date queryDate) {
		FangXinBaoAsyncTask<Tip> mTask = FangXinBaoAsyncTask.createInstance(
				getSelfContext(), UrlManager.URL_GET_DAY_TIP, new TipParser(),
				getString(R.string.loading));
		Calendar instances = Calendar.getInstance();
		instances.setTime(queryDate);
		instances.set(Calendar.HOUR, 0);
		instances.set(Calendar.MINUTE, 0);
		instances.set(Calendar.SECOND, 0);

		mTask.putParameter("bbid", mBaby.getId());
		mTask.putParameter("date",
				String.valueOf(instances.getTimeInMillis() / 1000));
		mTask.setOnFinishedListener(new OnFinishedListener<Tip>() {
			@Override
			public void onFininshed(Tip result) {
				mCurrentTip = result;
				ensureTipUi();
			}
		});
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	private void startGetDayTemperatureTask(Date queryDate) {
		FangXinBaoAsyncTask<Group<TemperatureData>> mTask = FangXinBaoAsyncTask
				.createInstance(getSelfContext(),
						UrlManager.URL_FETCH_DAY_TEMPERATURE_DATA,
						new PerDayTemperatureDataParser(),
						getString(R.string.loading));
		Calendar instances = Calendar.getInstance();
		instances.setTime(queryDate);
		instances.set(Calendar.HOUR, 0);
		instances.set(Calendar.MINUTE, 0);
		instances.set(Calendar.SECOND, 0);
		mTask.putParameter("bbid", mBaby.getId());
		mTask.putParameter("date",
				String.valueOf(instances.getTimeInMillis() / 1000));
		mTask.setOnFinishedBackgroundListener(new OnFinishedBackgroundListener<Group<TemperatureData>>() {
			@Override
			public void onFinishedBackground(Group<TemperatureData> result) {

			}
		});
		mTask.setOnFinishedListener(new OnFinishedListener<Group<TemperatureData>>() {
			@Override
			public void onFininshed(Group<TemperatureData> result) {
				LoggerTool.d(TAG, "onFininshed");
				if (result != null && result.code == 1 && result.size() > 0) {
					temperature_curveView.initData(result);
					temperature_curveView.setVisibility(View.VISIBLE);
					tv_empty_data.setVisibility(View.GONE);
				} else {
					String testData = "{\"code\":1,\"desc\":\"676be146\",\"uid\":\"4059454217\",\"data\":[{\"c\":\"40.1\",\"d\":1409716300},{\"c\":\"37.5\",\"d\":1412716240}]}";
					Group<TemperatureData> testResult = new PerDayTemperatureDataParser()
							.consume(testData, getSelfContext());
					temperature_curveView.initData(testResult);
					temperature_curveView.setVisibility(View.VISIBLE);

				}
			}
		});
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	private void palyAnimation() {
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(ll_holiday, "translationX", 0,
				ll_holiday.getWidth()), ObjectAnimator.ofFloat(ll_holiday,
				"translationX", ll_holiday.getWidth(), 0));
		set.setStartDelay(300);
		set.setDuration(1000);
		set.start();
	}

	private class QueryTemperatureDataTask extends
			AsyncTaskWithLoadingDialog<Void, Void, Group<TemperatureData>> {

		public QueryTemperatureDataTask() {
			super(getSelfContext(), null, true, false);

		}

		@Override
		protected Group<TemperatureData> doInBackground(Void... params) {
			DBHelper db = DBHelper.getDBInstance(getSelfContext());
			return db.queryTemperatureDataByDate(mBaby.getId(),
					VeDate.dateToStr(mSelectedDate));
		}

		@Override
		protected void onPostExecute(Group<TemperatureData> result) {
			super.onPostExecute(result);
			mStateHolder.cancelQueryTemperatureDataTask();

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelQueryTemperatureDataTask();

		}
	}

	private class StateHolder {

		private QueryTemperatureDataTask mQueryTemperatureDataTask; // 得到验证码
		boolean isQueryTemperatureDataTaskRunning = false;

		public StateHolder() {

		}

		public void startQueryTemperatureDataTask() {
			if (!isQueryTemperatureDataTaskRunning) {
				isQueryTemperatureDataTaskRunning = true;
				mQueryTemperatureDataTask = new QueryTemperatureDataTask();
				mQueryTemperatureDataTask.safeExecute();
			}
		}

		public void cancelQueryTemperatureDataTask() {
			if (mQueryTemperatureDataTask != null) {
				mQueryTemperatureDataTask.cancel(true);
				mQueryTemperatureDataTask = null;
			}
			isQueryTemperatureDataTaskRunning = false;

		}

		public void cancelAlltasks() {

			cancelQueryTemperatureDataTask();
		}
	}
}
