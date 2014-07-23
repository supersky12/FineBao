package com.yujunkang.fangxinbao.activity.user;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.activity.MainActivity;
import com.yujunkang.fangxinbao.control.NetworkProgressButton;
import com.yujunkang.fangxinbao.control.dialog.ISimpleDialogListener;
import com.yujunkang.fangxinbao.control.validator.FormEditText;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.BaseDataParser;
import com.yujunkang.fangxinbao.parser.UserParser;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnPrepareTaskListener;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.utility.DataConstants.EditEmailLanucherType;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.VerifyCodeLanucherType;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

/**
 * 
 * @date 2014-6-4
 * @author xieb
 * 
 */
public class EditEmailActivity extends ActivityWrapper implements
		ISimpleDialogListener {
	private static final String TAG = "EditEmailActivity";
	public static final String INTENT_EXTRA_EMAIL_LANUCHER_TYPE = DataConstants.PACKAGE_NAME
			+ ".EditEmailActivity.INTENT_EXTRA_EMAIL_LANUCHER_TYPE";
	public static final String INTENT_EXTRA_EMAIL = DataConstants.PACKAGE_NAME
			+ ".EditEmailActivity.INTENT_EXTRA_EMAIL";
	public static final int Request_Binding_Email = 1;
	/**
	 * 控件
	 */
	private FormEditText et_email;
	private NetworkProgressButton btn_register_email;

	private Country mSelectCountry;
	private VerifyCodeLanucherType mLanucherType;
	private EditEmailLanucherType mEditEmailLanucherType = EditEmailLanucherType.REGISTER;
	private String mEmail;
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
			btn_register_email.setEnabled(VerificationEmptyInput());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_email_activity);
		initControl();
		ensureUi();
	}

	protected void initData() {
		super.initData();
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			UiUtils.showAlertDialog("数据错误。", getSelfContext());
			finish();
			return;
		}

		if (extras.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY)) {
			mSelectCountry = (Country) extras
					.getParcelable(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY);
		}
		if (extras
				.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE)) {
			mLanucherType = (VerifyCodeLanucherType) extras
					.getSerializable(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE);
		}
		if (extras.containsKey(INTENT_EXTRA_EMAIL_LANUCHER_TYPE)) {
			mEditEmailLanucherType = (EditEmailLanucherType) extras
					.getSerializable(INTENT_EXTRA_EMAIL_LANUCHER_TYPE);
		}
		if (extras.containsKey(INTENT_EXTRA_EMAIL)) {
			mEmail = extras.getString(INTENT_EXTRA_EMAIL);
		}

	}

	private void initControl() {
		btn_register_email = (NetworkProgressButton) findViewById(R.id.btn_register_email);
		et_email = (FormEditText) findViewById(R.id.et_email);
	}

	private void ensureUi() {

		et_email.addTextChangedListener(onChange);
		if (!TextUtils.isEmpty(mEmail)) {
			et_email.setText(mEmail);
		}
		btn_register_email.setOnClickListener(this);
		if (mEditEmailLanucherType == EditEmailLanucherType.FORGET_PASSWORD) {
			btn_register_email.setButtonText(getString(R.string.email_confirm));
			btn_register_email
					.setLoadingPrompt(getString(R.string.email_confirm_loading));
		} else if (mEditEmailLanucherType == EditEmailLanucherType.BINDING) {
			btn_register_email.setButtonText(getString(R.string.email_confirm));
			btn_register_email
					.setLoadingPrompt(getString(R.string.email_binding_confirm_loading));
		}
	}

	private void startEditBabyInfoActivity() {
		Intent intent = new Intent(getSelfContext(), EditBabyInfoActivity.class);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_EMAIL, et_email
				.getEditableText().toString());
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY,
				mSelectCountry);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE,
				mLanucherType);

		startActivity(intent);
	}

	
	
	// 重置密码
	private void resetPasswordByEmail() {
		FangXinBaoAsyncTask<BaseData> mTask = FangXinBaoAsyncTask
				.createInstance(getSelfContext(),
						UrlManager.URL_RESET_PASSWORD, new BaseDataParser(),
						getString(R.string.reset_password_send_email_loading));
		String email = et_email.getText().toString();
		mTask.putParameter("email", email);
		mTask.setOnPrepareTaskListener(new OnPrepareTaskListener() {

			@Override
			public void onPreExecute() {
				btn_register_email.preNetworkExecute();

			}
		});
		mTask.setOnFinishedListener(resetPasswordByEmailListener);

		mTask.safeExecute();
		putAsyncTask(mTask);
	}
	
	/**
	 * 绑定邮箱
	 */
	private void bindingEmail() {
		FangXinBaoAsyncTask<User> mTask = FangXinBaoAsyncTask
				.createInstance(getSelfContext(),
						UrlManager.URL_MODIFY_EMAIL, new UserParser(),
						getString(R.string.loading));
		
		String email = et_email.getText().toString();
		mTask.putParameter("email", email);
		mTask.setOnPrepareTaskListener(new OnPrepareTaskListener() {

			@Override
			public void onPreExecute() {
				btn_register_email.preNetworkExecute();

			}
		});
		mTask.setOnFinishedListener(modifyEmailFinishListener);

		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	private OnFinishedListener<User> modifyEmailFinishListener = new OnFinishedListener<User>() {

		@Override
		public void onFininshed(User result) {
			btn_register_email.finishNetworkExecute();
			if (result == null) {
				UiUtils.showAlertDialog(
						getString(R.string.request_failed),
						getSelfContext());
			} else {
				if (result.code == 1) {
					UiUtils.showAlertDialog(
							getString(R.string.binding_email_success),
							getSelfContext());
					finish();
				} else {
					UiUtils.showAlertDialog(
							!TextUtils.isEmpty(result.getDesc()) ? result
									.getDesc()
									: getString(R.string.request_failed),
							getSelfContext());
				}
			}
		}

	};

	private OnFinishedListener<BaseData> resetPasswordByEmailListener = new OnFinishedListener<BaseData>() {

		@Override
		public void onFininshed(BaseData result) {
			btn_register_email.finishNetworkExecute();
			if (result == null) {
				UiUtils.showAlertDialog(
						getString(R.string.error_send_email_failed),
						getSelfContext());
			} else {
				if (result.code == 1) {
					UiUtils.showAlertDialog(
							getString(R.string.send_email_success),
							getSelfContext());
					finish();
				} else {
					UiUtils.showAlertDialog(
							!TextUtils.isEmpty(result.getDesc()) ? result
									.getDesc()
									: getString(R.string.error_send_email_failed),
							getSelfContext());
				}
			}
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_register_email: {
			if (et_email.testValidity()) {
				if (mEditEmailLanucherType == EditEmailLanucherType.REGISTER) {
					showOKOrCancelDialog(
							getString(R.string.dialog_register_confirm_email),
							getString(R.string.dialog_cancel_text),
							getString(R.string.submit_email_prompt));
				} else if (mEditEmailLanucherType == EditEmailLanucherType.FORGET_PASSWORD) {
					resetPasswordByEmail();
				} else if (mEditEmailLanucherType == EditEmailLanucherType.BINDING) {
					showOKOrCancelDialog(
							getString(R.string.dialog_binding_email_confirm),
							getString(R.string.dialog_cancel_text),
							getString(R.string.submit_email_prompt),
							Request_Binding_Email);
				} 
			}
			break;
		}

		}

	}

	private boolean VerificationEmptyInput() {
		if (TextUtils.isEmpty(et_email.getText().toString())) {
			return false;
		}

		return true;
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		if (requestCode == Request_Binding_Email) {
			//UiUtils.showAlertDialog("暂时还没此功能！",getSelfContext());
			bindingEmail();
		} else {
			startEditBabyInfoActivity();
		}

	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {

	}

}
