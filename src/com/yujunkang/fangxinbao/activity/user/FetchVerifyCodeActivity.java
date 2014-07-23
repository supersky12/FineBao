package com.yujunkang.fangxinbao.activity.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yujunkang.fangxinbao.R;

import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.activity.SelectCountryActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.app.NetWorKManager;
import com.yujunkang.fangxinbao.control.NetworkProgressButton;
import com.yujunkang.fangxinbao.control.validator.FormEditText;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.utility.DataConstants.VerifyCodeLanucherType;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.UiUtils;

/**
 * 
 * @date 2014-5-27
 * @author xieb
 * 
 */
public class FetchVerifyCodeActivity extends ActivityWrapper {

	private static final String TAG = "FetchVerifyCodeActivity";
	public static final String INTENT_EXTRA_PHONE = DataConstants.PACKAGE_NAME
			+ ".FetchVerifyCodeActivity.INTENT_EXTRA_PHONE";

	public static final String INTENT_EXTRA_COUNTRY = DataConstants.PACKAGE_NAME
			+ ".FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY";

	public static final String INTENT_EXTRA_LANUCHER_TYPE = DataConstants.PACKAGE_NAME
			+ ".FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE";
	public static final String INTENT_EXTRA_EMAIL = DataConstants.PACKAGE_NAME
			+ ".FetchVerifyCodeActivity.INTENT_EXTRA_EMAIL";
	public static final String INTENT_EXTRA_L = DataConstants.PACKAGE_NAME
			+ ".FetchVerifyCodeActivity.INTENT_EXTRA_EMAIL";
	private static final int REQUEST_ACTIVITY_SELECT_COUNTRY = 1;

	/**
	 * 控件
	 */
	private View btn_select_country;
	private EditText et_country_code;
	private TextView tv_country;
	private NetworkProgressButton btn_fetch_verifycode;
	private FormEditText et_phone;
	private ImageButton phoneDelBtn;
	private TextView tv_fetch_verifycode_title;
	private View ll_phone;

	/**
	 * 
	 */
	private Country mSelectCountry;
	private DBHelper mDatabaseHelper = null;
	private boolean isEditModel = false;
	private VerifyCodeLanucherType mLanucherType = VerifyCodeLanucherType.REGISTER;
	private String mPhone;
	private StateHolder mStateHolder = new StateHolder();
	private Handler uiHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fetch_verifycode_activity);
		LoggerTool.d(TAG, "onCreate");
		init();
		initControl();
		ensureUi();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LoggerTool.d(TAG, "onResume");
	}

	private TextWatcher onChange = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			btn_fetch_verifycode.setEnabled(VerificationEmptyInput());
		}
	};

	private void init() {
		mDatabaseHelper = DBHelper.getDBInstance(this);
		// 如果没有值
		if (mSelectCountry == null
				|| TextUtils.isEmpty(mSelectCountry.getCountryCode())) {
			// 如果根据系统的语言设置找不到就用中国
			mSelectCountry = mDatabaseHelper
					.queryCountryBySimpleCode(mApplication.getLocale()
							.getCountry());
			if (mSelectCountry == null) {
				mSelectCountry = mDatabaseHelper.queryCountryByCode("86");
			}
		}
	}

	protected void initData() {
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			UiUtils.showAlertDialog("数据错误。", getSelfContext());
			finish();
			return;
		}

		if (extras
				.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE)) {
			mLanucherType = (VerifyCodeLanucherType) extras
					.getSerializable(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE);
		}
		if (extras.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY)) {
			mSelectCountry = (Country) extras
					.get(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY);
		}
		if (extras.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE)) {
			mPhone = extras
					.getString(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE);
		}

	}

	private void initControl() {
		tv_fetch_verifycode_title = (TextView) findViewById(R.id.tv_fetch_verifycode_title);
		btn_select_country = findViewById(R.id.btn_select_country);
		tv_country = (TextView) findViewById(R.id.tv_country);
		btn_fetch_verifycode = (NetworkProgressButton) findViewById(R.id.btn_fetch_verifycode);
		et_country_code = (EditText) findViewById(R.id.et_country_code);
		et_phone = (FormEditText) findViewById(R.id.et_phone);
		phoneDelBtn = (ImageButton) findViewById(R.id.ibtn_del);
		ll_phone = findViewById(R.id.ll_phone);
	}

	private void lock()
	{
		UiUtils.disableView(btn_select_country);
		UiUtils.disableView(ll_phone);
		et_phone.setEnabled(false);
	}
	
	private void unlock()
	{
		UiUtils.enableView(btn_select_country);
		UiUtils.enableView(ll_phone);
		et_phone.setEnabled(true);
	}
	
	
	private void ensureUi() {
		// 标题
		if (mLanucherType == VerifyCodeLanucherType.FORGET_PASSWORD) {
			tv_fetch_verifycode_title
					.setText(getString(R.string.fetch_verifycode_activity_reset_password));
		} else if (mLanucherType == VerifyCodeLanucherType.REGISTER) {
			tv_fetch_verifycode_title
					.setText(getString(R.string.fetch_verifycode_activity_register));
		} else if (mLanucherType == VerifyCodeLanucherType.MODIFY_PHONE) {
			tv_fetch_verifycode_title
					.setText(getString(R.string.fetch_verifycode_activity_modify_phone));
		}

		btn_select_country.setOnClickListener(this);

		tv_country.addTextChangedListener(onChange);
		ensureCountryCodeUi();

		btn_fetch_verifycode.setOnClickListener(this);
		ensurePhoneUi();
	}

	/**
	 * 初始化电话
	 */
	private void ensurePhoneUi() {
		phoneDelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et_phone.setText(null);

			}
		});
		et_phone.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						et_phone.clearFocus();
					}
				}
				return false;
			}
		});
		et_phone.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && et_phone.length() > 0) {
					phoneDelBtn.setVisibility(View.VISIBLE);
				} else {
					phoneDelBtn.setVisibility(View.GONE);
				}
			}
		});

		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				int len = et_phone.length();
				if (phoneDelBtn != null) {
					phoneDelBtn.setVisibility(len == 0 ? View.GONE
							: View.VISIBLE);
				}

			}
		});
		et_phone.addTextChangedListener(onChange);
		if (!TextUtils.isEmpty(mPhone)) {
			et_phone.setText(mPhone);
		}
		et_phone.requestFocus();
		// 弹出输入法
		uiHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(et_phone, 0);
			}
		}, 500);
	}

	/**
	 * 国籍选择
	 */
	private void ensureCountryCodeUi() {
		if (mSelectCountry != null) {
			et_country_code.setText(getString(R.string.country_code,
					mSelectCountry.getCountryCode()));
			tv_country.setText(mSelectCountry.getName());
		}

		et_country_code.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s == null) {
					// 这里要保存加号
					et_country_code
							.setText(getString(R.string.country_code, ""));
					Selection.setSelection(et_country_code.getEditableText(), 1);
				} else if (s.toString().length() == 0) {
					et_country_code
							.setText(getString(R.string.country_code, ""));
					Selection.setSelection(et_country_code.getEditableText(), 1);
				} else if (s.toString().length() > 1) {
					if (isEditModel) {
						String code = s.toString().substring(1);
						filterCountryByCode(code);
					}
				}
			}
		});

		et_country_code.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				isEditModel = hasFocus;

			}
		});
		et_country_code.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				isEditModel = true;
				return false;
			}
		});
		Selection.setSelection(et_country_code.getEditableText(),
				et_country_code.getEditableText().length());

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_select_country: {
			startSelectCountryActivity();
			break;
		}
		case R.id.btn_fetch_verifycode: {
			if (et_phone.testValidity()) {
				mStateHolder.startFetchVerifyCodeTask();
			}
			break;
		}
		}
	}

	/**
	 * 根据国家代码获取国家名称
	 * 
	 * @param code
	 */
	private void filterCountryByCode(String code) {
		Country result = mDatabaseHelper.queryCountryByCode(code);
		if (result != null) {
			tv_country.setText(result.getName());
			mSelectCountry = result;
		} else {
			tv_country.setText(getString(R.string.error_country_code));
			mSelectCountry = null;
		}
	}

	/**
	 * 选择国家
	 * 
	 */
	private void startSelectCountryActivity() {
		Intent intent = new Intent(getSelfContext(),
				SelectCountryActivity.class);
		startActivityForResult(intent, REQUEST_ACTIVITY_SELECT_COUNTRY);
	}

	// 启动提交
	private void startVerifyCodeActivity() {
		Intent intent = new Intent(getSelfContext(),
				VerifySMSCodeActivity.class);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE,
				mLanucherType);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE, et_phone
				.getText().toString().toString());
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY,
				mSelectCountry);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_ACTIVITY_SELECT_COUNTRY: {
				if (data != null) {
					if (data.hasExtra(SelectCountryActivity.INTENT_EXTRA_COUNTRY)) {
						mSelectCountry = (Country) data
								.getParcelableExtra(SelectCountryActivity.INTENT_EXTRA_COUNTRY);
						String name = mSelectCountry.getName();
						if (!TextUtils.isEmpty(name)) {
							tv_country.setText(name);
						}
						String countryCode = mSelectCountry.getCountryCode();
						if (!TextUtils.isEmpty(countryCode)) {
							isEditModel = false;
							et_country_code.setText(getString(
									R.string.country_code, countryCode));
							Selection.setSelection(
									et_country_code.getEditableText(),
									et_country_code.getEditableText().length());
						}

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

	private boolean VerificationEmptyInput() {

		if (TextUtils.isEmpty(et_phone.getText().toString())) {

			return false;
		}
		if (mSelectCountry == null) {

			return false;
		}

		return true;
	}

	/**
	 * 早期做的懒得改
	 * 
	 * @author supersky
	 * 
	 */
	private class FetchVerifyCodeTask extends
			AsyncTaskWithLoadingDialog<Void, Void, BaseData> {

		public FetchVerifyCodeTask() {
			super(getSelfContext(), "正在发送验证码...", false, false);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			btn_fetch_verifycode.preNetworkExecute();
			lock();
		}

		@Override
		protected BaseData doInBackground(Void... params) {
			NetWorKManager httpApi = FangXinBaoApplication.getApplication(
					getSelfContext()).getNetWorKManager();
			BaseData result = httpApi.fetchVerifyCode(et_phone.getText()
					.toString(), mSelectCountry.getCountryCode(), String
					.valueOf(mLanucherType.ordinal()));
			return result;
		}

		@Override
		protected void onPostExecute(BaseData result) {
			super.onPostExecute(result);
			mStateHolder.cancelFetchVerifyCodeTask();
			btn_fetch_verifycode.finishNetworkExecute();
			unlock();
			if (result == null) {
				UiUtils.showAlertDialog("发送验证码失败!", getSelfContext());
			} else {
				if (result.code == 1) {
					startVerifyCodeActivity();

				} else {
					UiUtils.showAlertDialog(
							!TextUtils.isEmpty(result.getDesc()) ? result
									.getDesc() : "发送验证码失败!", getSelfContext());
				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelFetchVerifyCodeTask();
			btn_fetch_verifycode.finishNetworkExecute();
			unlock();
		}
	}

	private class StateHolder {

		private FetchVerifyCodeTask mFetchVerifyCodeTask; // 得到验证码
		boolean isFetchVerifyCodeTaskRunning = false;

		public StateHolder() {

		}

		public void startFetchVerifyCodeTask() {
			if (!isFetchVerifyCodeTaskRunning) {
				isFetchVerifyCodeTaskRunning = true;
				mFetchVerifyCodeTask = new FetchVerifyCodeTask();
				mFetchVerifyCodeTask.safeExecute();
			}
		}

		public void cancelFetchVerifyCodeTask() {
			if (mFetchVerifyCodeTask != null) {
				mFetchVerifyCodeTask.cancel(true);
				mFetchVerifyCodeTask = null;
			}
			isFetchVerifyCodeTaskRunning = false;

		}

		public void cancelAlltasks() {

			cancelFetchVerifyCodeTask();
		}
	}
}
