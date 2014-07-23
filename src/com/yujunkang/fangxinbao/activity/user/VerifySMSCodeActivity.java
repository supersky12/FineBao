package com.yujunkang.fangxinbao.activity.user;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.activity.MainActivity;
import com.yujunkang.fangxinbao.activity.SettingActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.app.NetWorKManager;
import com.yujunkang.fangxinbao.control.NetworkProgressButton;
import com.yujunkang.fangxinbao.control.dialog.ISimpleDialogListener;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.model.UserAccount;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.utility.DataConstants.VerifyCodeLanucherType;
import com.yujunkang.fangxinbao.utility.SmsContent;
import com.yujunkang.fangxinbao.utility.UiUtils;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @date 2014-5-30
 * @author xieb
 * 
 */
public class VerifySMSCodeActivity extends ActivityWrapper implements
		ISimpleDialogListener {
	private static final String TAG = "VerifySMSCodeActivity";
	private static final int FETCH_SMSCODE_PERIODE = 10;
	private TextView tv_prompt;
	private EditText et_verifynumber;
	private TextView btn_getverifynumber;
	private NetworkProgressButton btn_confirm;
	private View btn_can_not_receive_sms;

	private String phone;
	private String verifyNumber;
	private Country mSelectCountry;
	private int countdown;
	private Handler handler = new Handler();
	private StateHolder mStateHolder = new StateHolder();
	private VerifyCodeLanucherType mLanucherType = VerifyCodeLanucherType.REGISTER;
	private SmsContent content;

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
			btn_confirm.setEnabled(VerificationEmptyInput());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verifycode_activity);
		initControl();
		ensureUI();
		countdown = FETCH_SMSCODE_PERIODE;
		handler.post(coundownUi);
	}

	protected void initData() {
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			UiUtils.showAlertDialog("数据错误。", getSelfContext());
			finish();
			return;
		}
		if (extras.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE)) {
			phone = extras
					.getString(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE);
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

	}

	private void initControl() {
		tv_prompt = (TextView) findViewById(R.id.tv_prompt);
		// 输入验证码
		et_verifynumber = (EditText) findViewById(R.id.et_verifynumber);
		// 重新获取验证码
		btn_getverifynumber = (TextView) findViewById(R.id.btn_getverifynumber);
		// 提交获取验证码
		btn_confirm = (NetworkProgressButton) findViewById(R.id.btn_confirm);
		btn_can_not_receive_sms = findViewById(R.id.btn_can_not_receive_sms);
		if (checkCallingOrSelfPermission(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
			content = new SmsContent(getSelfContext(), new Handler(),
					et_verifynumber);
			// 注册短信变化监听
			getContentResolver().registerContentObserver(
					Uri.parse("content://sms/"), true, content);
		}

	}

	private void lock() {
		UiUtils.disableView(et_verifynumber);

	}

	private void unlock() {
		UiUtils.enableView(et_verifynumber);

	}

	private void ensureUI() {

		tv_prompt.setText(getString(R.string.verifycode_prompt, phone));

		et_verifynumber.addTextChangedListener(onChange);

		btn_getverifynumber.setOnClickListener(this);

		btn_confirm.setOnClickListener(this);

		btn_can_not_receive_sms.setOnClickListener(this);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mStateHolder.cancelAlltasks();
		handler.removeCallbacks(coundownUi);
		if (checkCallingOrSelfPermission(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
			getContentResolver().unregisterContentObserver(content);
		}
	}

	/**
	 * 点击"获取验证码"后的倒计时60秒
	 */
	Runnable coundownUi = new Runnable() {
		@Override
		public void run() {
			if (countdown > 0) {
				handler.postDelayed(coundownUi, 1000);
				btn_getverifynumber.setClickable(false);
				btn_getverifynumber.setEnabled(false);
				btn_getverifynumber.setText(countdown + "秒重新获取");
				countdown--;
			} else {
				btn_getverifynumber.setText("获取验证码");
				btn_getverifynumber.setClickable(true);
				btn_getverifynumber
						.setOnClickListener(VerifySMSCodeActivity.this);
				btn_getverifynumber.setEnabled(true);
				if (btn_can_not_receive_sms.getVisibility() == View.GONE
						&& mLanucherType == VerifyCodeLanucherType.REGISTER) {
					btn_can_not_receive_sms.setVisibility(View.VISIBLE);
				}
			}
		}
	};

	private boolean VerificationEmptyInput() {

		if (TextUtils.isEmpty(et_verifynumber.getText().toString())) {

			return false;
		}

		return true;
	}

	private void startEditBabyInfoActivity() {
		Intent intent = new Intent(getSelfContext(), EditBabyInfoActivity.class);

		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE, phone);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY,
				mSelectCountry);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE,
				mLanucherType);
		startActivity(intent);
	}

	private void startEditEmainlctivity() {
		Intent intent = new Intent(getSelfContext(), EditEmailActivity.class);

		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY,
				mSelectCountry);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE,
				mLanucherType);
		startActivity(intent);
	}

	private void startResetPasswordActivity() {
		Intent intent = new Intent(getSelfContext(),
				ResetPassWordActivity.class);

		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE, phone);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY,
				mSelectCountry);
		startActivity(intent);
	}

	/**
	 * 获取验证码
	 */
	private class FetchVerifyCodeTask extends
			AsyncTaskWithLoadingDialog<Void, Void, BaseData> {
		public FetchVerifyCodeTask() {
			super(getSelfContext(), "", false, false);
		}

		@Override
		protected BaseData doInBackground(Void... arg0) {
			NetWorKManager httpApi = FangXinBaoApplication.getApplication(
					getSelfContext()).getNetWorKManager();
			BaseData result = httpApi.fetchVerifyCode(phone,
					mSelectCountry.getCountryCode(),
					String.valueOf(mLanucherType.ordinal()));
			return result;
		}

		@Override
		protected void onPostExecute(BaseData result) {
			super.onPostExecute(result);
			mStateHolder.cancelFetchVerifyCodeTask();
			if (result.getCode() == 1) {
				UiUtils.showAlertDialog(result.getDesc(), getSelfContext());
			} else {

				UiUtils.showAlertDialog(result.getDesc(), getSelfContext());
			}

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelFetchVerifyCodeTask();
		}
	}

	/**
	 * 验证码激活
	 */
	private class ConfirmVerifyCodeTask extends
			AsyncTaskWithLoadingDialog<Void, Void, BaseData> {
		public ConfirmVerifyCodeTask() {
			super(getSelfContext(), "", false, false);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			btn_confirm.preNetworkExecute();
			lock();
		}

		@Override
		protected BaseData doInBackground(Void... arg0) {
			NetWorKManager httpApi = FangXinBaoApplication.getApplication(
					getSelfContext()).getNetWorKManager();
			BaseData result = httpApi.confirmVerifyCode(verifyNumber, phone,
					mSelectCountry.getCountryCode());
			return result;
		}

		@Override
		protected void onPostExecute(BaseData result) {
			super.onPostExecute(result);
			mStateHolder.cancelConfirmVerifyCodeTask();
			btn_confirm.finishNetworkExecute();
			unlock();
			if (result == null) {
				UiUtils.showAlertDialog("提交验证码失败!", getSelfContext());
			} else {
				if (result.code == 1) {
					// 标题
					if (mLanucherType == VerifyCodeLanucherType.FORGET_PASSWORD) {
						startResetPasswordActivity();
					} else if (mLanucherType == VerifyCodeLanucherType.REGISTER) {
						startEditBabyInfoActivity();
					} else if (mLanucherType == VerifyCodeLanucherType.MODIFY_PHONE) {
						sendRouteNotificationRoute(
								new String[] { FetchVerifyCodeActivity.class
										.getName() },
								CommonAction.CLOSE_ALL_ACTIVITY, null);
						Bundle data = new Bundle();
						data.putString(
								FetchVerifyCodeActivity.INTENT_EXTRA_PHONE,
								phone);
						sendRouteNotificationRoute(
								new String[] { SettingActivity.class.getName() },
								CommonAction.ACTIVITY_MODIFY_PHONE_SUCCESS,
								data);
						// 更新用户信息
						UserAccount account = Preferences
								.getUserAccount(getSelfContext());
						User user = Preferences.getUserInfo(getSelfContext());
						user.setPhone(phone);
						user.setCountry(mSelectCountry);
						// 这里要判断登陆方式
						if (account.getLoginType() == UserAccount.LOGINTYPE_PHONE) {
							// 更新账号信息
							Preferences.loginUser(getSelfContext(), phone,
									null, user);
						} else {
							Preferences.storeUser(getSelfContext(), user);
						}

						finish();
					}

				} else {
					UiUtils.showAlertDialog(
							!TextUtils.isEmpty(result.getDesc()) ? result
									.getDesc() : "提交验证码失败!", getSelfContext());
				}
			}

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelConfirmVerifyCodeTask();
			unlock();
		}
	}

	private class StateHolder {

		private FetchVerifyCodeTask mFetchVerifyCodeTask; // 得到验证码
		boolean isFetchVerifyCodeTaskRunning = false;

		private ConfirmVerifyCodeTask mConfirmVerifyCodeTask;
		private boolean isConfirmVerifyCodeTaskRunning = false;

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
			cancelConfirmVerifyCodeTask();
		}

		public void startConfirmVerifyCodeTask() {
			if (isConfirmVerifyCodeTaskRunning == false) {
				isConfirmVerifyCodeTaskRunning = true;
				mConfirmVerifyCodeTask = new ConfirmVerifyCodeTask();
				mConfirmVerifyCodeTask.safeExecute();
			}
		}

		public void cancelConfirmVerifyCodeTask() {
			if (mConfirmVerifyCodeTask != null) {
				mConfirmVerifyCodeTask.cancel(true);
				mConfirmVerifyCodeTask = null;

			}
			isConfirmVerifyCodeTaskRunning = false;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_getverifynumber: {
			mStateHolder.startFetchVerifyCodeTask();
			countdown = 60;
			handler.post(coundownUi);
			break;
		}
		case R.id.btn_confirm: {
			verifyNumber = et_verifynumber.getText().toString();
			mStateHolder.startConfirmVerifyCodeTask();
			break;
		}
		case R.id.btn_can_not_receive_sms: {
			showOKOrCancelDialog(getString(R.string.dialog_register_by_email),
					getString(R.string.dialog_cancel_text),
					getString(R.string.register_email_prompt));
			break;
		}

		}
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		startEditEmainlctivity();

	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {

	}
}
