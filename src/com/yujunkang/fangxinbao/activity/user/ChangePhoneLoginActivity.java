package com.yujunkang.fangxinbao.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.control.NetworkProgressButton;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.model.UserAccount;
import com.yujunkang.fangxinbao.parser.UserParser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnPrepareTaskListener;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.VerifyCodeLanucherType;

/**
 * 
 * @date 2014-6-19
 * @author xieb
 * 
 */
public class ChangePhoneLoginActivity extends ActivityWrapper {
	private static final String TAG = "ChangePhoneLoginActivity";

	/**
	 * 控件
	 */
	private EditText et_password;
	private NetworkProgressButton btn_login;

	private User user;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_phone_verify_password_activity);
		init();
		initControl();
		ensureUi();
	}

	private void init() {
		user = Preferences.getUserCommonInfo(getSelfContext());
	}

	private void initControl() {
		btn_login = (NetworkProgressButton) findViewById(R.id.btn_login);
		et_password = (EditText) findViewById(R.id.et_password);
		btn_login.setOnClickListener(this);

	}

	private void lock()
	{
		UiUtils.disableView(et_password);
	}
	
	private void unlock()
	{
		UiUtils.enableView(et_password);
	}
	
	
	
	private void ensureUi() {
		et_password.addTextChangedListener(onChange);
	}

	private void startFetchVerifyCodeActivity() {
		Intent intent = new Intent(this, FetchVerifyCodeActivity.class);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE,
				VerifyCodeLanucherType.MODIFY_PHONE);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE,
				user.getPhone());
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY,
				user.getCountry());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login: {
			startLoginTask();
			break;
		}

		}

	}

	// 登录
	private void startLoginTask() {
		FangXinBaoAsyncTask<User> mTask = FangXinBaoAsyncTask
				.createInstance(getSelfContext(), UrlManager.URL_LOGIN,
						new UserParser(), false);
		UserAccount account = Preferences.getUserAccount(getSelfContext());

		if (account.getLoginType() == UserAccount.LOGINTYPE_PHONE) {
			mTask.putParameter("phone", user.getPhone());
			mTask.putParameter("nationality", user.getCountry()
					.getCountryCode());
		} else {
			mTask.putParameter("email", user.getEmail());
		}
		mTask.putParameter("password", et_password.getText().toString());
		mTask.setOnPrepareTaskListener(new OnPrepareTaskListener() {

			@Override
			public void onPreExecute() {
				btn_login.preNetworkExecute();
				lock();
			}
		});
		mTask.setOnFinishedListener(onLoginFinishListener);
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	/**
	 * 登录成功回调
	 */
	private OnFinishedListener<User> onLoginFinishListener = new OnFinishedListener<User>() {
		@Override
		public void onFininshed(User result) {
			btn_login.finishNetworkExecute();
			unlock();
			if (result == null) {
				UiUtils.showAlertDialog(getString(R.string.error_login_failed),
						getSelfContext());
			} else {
				if (result.code == 1) {
					startFetchVerifyCodeActivity();
					finish();
				} else {
					et_password.setText("");
					UiUtils.showAlertDialog(
							!TextUtils.isEmpty(result.getDesc()) ? result
									.getDesc()
									: getString(R.string.error_login_failed),
							getSelfContext());
				}
			}

		}
	};

	private boolean VerificationEmptyInput() {
		if (TextUtils.isEmpty(et_password.getText().toString())) {
			return false;
		}

		return true;
	}

}
