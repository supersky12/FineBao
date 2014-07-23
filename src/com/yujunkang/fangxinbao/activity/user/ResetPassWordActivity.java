package com.yujunkang.fangxinbao.activity.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;


import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.activity.MainActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.app.NetWorKManager;
import com.yujunkang.fangxinbao.control.NetworkProgressButton;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.BaseDataParser;
import com.yujunkang.fangxinbao.parser.UserParser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedBackgroundListener;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnPrepareTaskListener;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;

/**
 * 
 * @date 2014-6-7
 * @author xieb
 * 
 */
public class ResetPassWordActivity extends ActivityWrapper {

	private NetworkProgressButton btn_reset_password;
	private EditText et_new_password;
	private Country mSelectCountry;
	private String mPhone;

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
			btn_reset_password.setEnabled(VerificationEmptyInput());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reset_password_activity);
		initControl();
		ensureUi();
	}

	protected void initData() {
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			UiUtils.showAlertDialog("数据错误。", getSelfContext());
			finish();
			return;
		}
		if (extras.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE)) {
			mPhone = extras
					.getString(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE);
		}

		if (extras.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY)) {
			mSelectCountry = (Country) extras
					.getParcelable(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY);
		}

	}

	private void initControl() {
		btn_reset_password = (NetworkProgressButton) findViewById(R.id.btn_reset_password);
		et_new_password = (EditText) findViewById(R.id.et_new_password);
	}

	private void lock()
	{
		UiUtils.disableView(et_new_password);
		
	}
	
	private void unlock()
	{
		
		UiUtils.enableView(et_new_password);
		
	}
	
	
	
	private void ensureUi() {

		et_new_password.addTextChangedListener(onChange);

		btn_reset_password.setOnClickListener(this);
	}

	private void startResetPasswordTask() {
		FangXinBaoAsyncTask<User> task = FangXinBaoAsyncTask
				.createInstance(getSelfContext(),
						UrlManager.URL_RESET_PASSWORD, new UserParser(),
						false);
		task.putParameter("phone", mPhone);
		task.putParameter("nationality", mSelectCountry.getCountryCode());
		task.putParameter("password", et_new_password.getText().toString());

		task.setOnPrepareTaskListener(new OnPrepareTaskListener() {

			@Override
			public void onPreExecute() {
				btn_reset_password.preNetworkExecute();
				lock();
			}
		});
		
		task.setOnFinishedListener(new OnFinishedListener<User>() {
			@Override
			public void onFininshed(User result) {
				btn_reset_password.finishNetworkExecute();
				unlock();
				if (result == null) {
					UiUtils.showAlertDialog(
							getString(R.string.error_reset_password_failed),
							getSelfContext());
				} else {
					if (result.code == 1) {
						UiUtils.showAlertDialog(
								getString(R.string.login_success),
								getSelfContext());
						// 保存用户信息
						Preferences.loginUser(getSelfContext(), mPhone,
								et_new_password.getText().toString(),
								result);
						sendRouteNotificationRoute(new String[] {
								FetchVerifyCodeActivity.class.getName(),
								VerifySMSCodeActivity.class.getName() ,
								LoginActivity.class.getName(),MainActivity.class.getName(),ModifyPasswordActivity.class.getName()},
								CommonAction.CLOSE_ALL_ACTIVITY, null);
						
						finish();
					} else {
						UiUtils.showAlertDialog(!TextUtils.isEmpty(result
								.getDesc()) ? result.getDesc()
								: getString(R.string.error_reset_password_failed),
								getSelfContext());
					}
				}
			}
		});
		task.safeExecute();
		putAsyncTask(task);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reset_password: {
			startResetPasswordTask();
			break;
		}

		}

	}

	private boolean VerificationEmptyInput() {
		if (TextUtils.isEmpty(et_new_password.getText().toString())) {
			return false;
		}

		return true;
	}

}
