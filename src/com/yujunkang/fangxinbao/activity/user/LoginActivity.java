package com.yujunkang.fangxinbao.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.activity.MainActivity;
import com.yujunkang.fangxinbao.activity.SelectCountryActivity;
import com.yujunkang.fangxinbao.anim.ExpandCollapseAnimation;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.app.NetWorKManager;
import com.yujunkang.fangxinbao.control.NetworkProgressButton;
import com.yujunkang.fangxinbao.control.validator.EmailValidator;
import com.yujunkang.fangxinbao.control.validator.FormEditText;
import com.yujunkang.fangxinbao.control.validator.OrValidator;
import com.yujunkang.fangxinbao.control.validator.PhoneValidator;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.BaseDataParser;
import com.yujunkang.fangxinbao.parser.UserParser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnPrepareTaskListener;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.utility.AlbumUtils;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.EditEmailLanucherType;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.utility.DataConstants.VerifyCodeLanucherType;
import com.yujunkang.fangxinbao.utility.Utils;

public class LoginActivity extends ActivityWrapper {

	private static final String TAG = "LoginActivity";
	private static final int REQUEST_ACTIVITY_SELECT_COUNTRY = 1;
	/**
	 * 控件
	 */
	private View btn_select_country;
	private EditText et_country_code;
	private TextView tv_country;
	private EditText et_password;

	private NetworkProgressButton btn_login;
	private FormEditText et_account;

	private TextView btn_change_country;
	private View ll_select_country;
	private View btn_forget_password;
	/**
	 * 
	 */
	private Country mSelectCountry;
	private DBHelper mDatabaseHelper = null;
	private boolean isEditModel = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		init();
		initControl();
		ensureUi();
	}

	private TextWatcher onChange = new TextWatcher() {

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
			// TODO Auto-generated method stub
			btn_login.setEnabled(VerificationEmptyInput());
		}
	};

	private void init() {
		mDatabaseHelper = DBHelper.getDBInstance(this);
		// 如果根据系统的语言设置找不到就用中国
		mSelectCountry = mDatabaseHelper.queryCountryBySimpleCode(mApplication
				.getLocale().getCountry());
		if (mSelectCountry == null) {
			mSelectCountry = mDatabaseHelper.queryCountryByCode("86");
		}

	}

	private void initControl() {
		// 选择国家
		ll_select_country = findViewById(R.id.ll_select_country);
		btn_select_country = findViewById(R.id.btn_select_country);
		tv_country = (TextView) findViewById(R.id.tv_country);
		// 登录按钮
		btn_login = (NetworkProgressButton) findViewById(R.id.btn_login);
		// 登录密码
		et_password = (EditText) findViewById(R.id.et_password);
		// 忘记密码
		btn_forget_password = findViewById(R.id.btn_forget_password);

		et_country_code = (EditText) findViewById(R.id.et_country_code);
		et_account = (FormEditText) findViewById(R.id.et_account);
		btn_change_country = (TextView) findViewById(R.id.btn_change_country);
	}

	private void lock()
	{
		UiUtils.disableView(ll_select_country);
		UiUtils.disableView(et_account);
		UiUtils.disableView(et_password);
	}
	
	private void unlock()
	{
		UiUtils.enableView(ll_select_country);
		UiUtils.enableView(et_account);
		UiUtils.enableView(et_password);
		
	}
	
	
	
	private void ensureUi() {
		try {
			//Preferences.getUserInfo(this);
			btn_select_country.setOnClickListener(this);

			tv_country.addTextChangedListener(onChange);
			ensureCountryCodeUi();

			btn_login.setOnClickListener(this);
			// 登录名
			ensureAccountUi();
			ensureChangeCountryUi();

			et_password.addTextChangedListener(onChange);

			btn_forget_password.setOnClickListener(this);
		} catch (Exception ex) {
			LoggerTool.e(TAG, ex.getMessage());
		}
	}

	private void ensureChangeCountryUi() {

		btn_change_country.setOnClickListener(this);
	}

	
	
	
	private void ensureAccountUi() {

		et_account.addTextChangedListener(onChange);
		OrValidator validator = new OrValidator(
				getString(R.string.error_field_account_not_valid),
				new EmailValidator(""), new PhoneValidator(""));
		et_account.addValidator(validator);
		et_account.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				int length = et_account.getEditableText().length();
				if (!hasFocus && length > 0) {
					et_account.testValidity();
				}

			}
		});
	}

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

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s == null) {
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
		Selection.setSelection(et_country_code.getEditableText(), 1);
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

	}

	// 登录
	private void startLoginTask() {
		FangXinBaoAsyncTask<User> mTask = FangXinBaoAsyncTask
				.createInstance(getSelfContext(), UrlManager.URL_LOGIN,
						new UserParser(), false);
		String account = et_account.getText().toString();

		// 判断是手机还是邮箱登录
		if (Utils.isMobile(account)) {
			mTask.putParameter("phone", account);
			mTask.putParameter("nationality", mSelectCountry.getCountryCode());
		} else {
			mTask.putParameter("email", account);
		}
		mTask.putParameter("password", et_password.getText().toString());
		mTask.setOnPrepareTaskListener(new OnPrepareTaskListener() {

			@Override
			public void onPreExecute() {
				btn_login.preNetworkExecute();
				lock();
			}
		});
		mTask.setOnFinishedListener(new OnFinishedListener<User>() {
			@Override
			public void onFininshed(User result) {
				btn_login.finishNetworkExecute();
				unlock();
				if (result == null) {
					UiUtils.showAlertDialog(
							getString(R.string.error_login_failed),
							getSelfContext());
				} else {
					if (result.code == 1) {
						UiUtils.showAlertDialog(
								getString(R.string.login_success),
								getSelfContext());
						String account = et_account.getText().toString();
						// 保存用户信息
						Preferences.loginUser(getSelfContext(), account,
								et_password.getText().toString(),
								result);
						startUserMainActivity();
						sendRouteNotificationRoute(new String[] {
								MainActivity.class.getName()},
								CommonAction.CLOSE_ALL_ACTIVITY, null);
						finish();
					} else {
						UiUtils.showAlertDialog(!TextUtils.isEmpty(result
								.getDesc()) ? result.getDesc()
								: getString(R.string.error_login_failed),
								getSelfContext());
					}
				}
			}
		});
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	private void startSelectCountryActivity() {
		Intent intent = new Intent(getSelfContext(),
				SelectCountryActivity.class);
		startActivityForResult(intent, REQUEST_ACTIVITY_SELECT_COUNTRY);
	}
	
	private void startEditEmailActivity() {
		Intent intent = new Intent(getSelfContext(),
				EditEmailActivity.class);
		intent.putExtra(EditEmailActivity.INTENT_EXTRA_EMAIL_LANUCHER_TYPE, EditEmailLanucherType.FORGET_PASSWORD);
		intent.putExtra(EditEmailActivity.INTENT_EXTRA_EMAIL, et_account
				.getText().toString());
		startActivity(intent);
	}
	
	
	private void startUserMainActivity()
	{
		Intent intent = new Intent(getSelfContext(),UserMainActivity.class);
		startActivity(intent);
	}
	
	
	
	private void startFetchVerifyCodeActivity() {
		Intent intent = new Intent(this, FetchVerifyCodeActivity.class);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE,
				VerifyCodeLanucherType.FORGET_PASSWORD);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE, et_account
				.getText().toString());
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY,
				mSelectCountry);
		startActivity(intent);
	}

	
	private void startEditAccountActivity()
	{
		Intent intent = new Intent(this, EditAccountActivity.class);
		intent.putExtra(EditAccountActivity.INTENT_EXTRA_ACCOUNT, et_account
				.getText().toString());
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY,
				mSelectCountry);
		startActivity(intent);
		
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_select_country: {
			startSelectCountryActivity();
			break;
		}
		case R.id.btn_login: {
			if (et_account.testValidity()) {
				startLoginTask();
			}
			break;
		}

		case R.id.btn_change_country: {
			// 控制选择国家
			ExpandCollapseAnimation.setHeightForWrapContent(ll_select_country,
					getResources().getDimensionPixelSize(R.dimen.btn_height_l));
			ExpandCollapseAnimation expandAni = new ExpandCollapseAnimation(
					ll_select_country, 400);
			ll_select_country.startAnimation(expandAni);
			break;
		}

		case R.id.btn_forget_password: {
			String account = et_account.getText().toString();

			
				// 如果是手机号就走手机号验证的流程
				if (Utils.isMobile(account)) {
					startFetchVerifyCodeActivity();
				} else if(Utils.isEmail(account)){
					// 邮箱登录方式
					startEditEmailActivity();
				}
				else
				{
					startEditAccountActivity();
				}
			break;
		}
		}
	}

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

	}

	private boolean VerificationEmptyInput() {

		if (TextUtils.isEmpty(et_account.getText().toString())) {

			return false;
		}
		if (mSelectCountry == null) {

			return false;
		}

		if (TextUtils.isEmpty(et_password.getText().toString())) {

			return false;
		}
		return true;
	}

}
