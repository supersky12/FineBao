package com.yujunkang.fangxinbao.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.anim.ExpandCollapseAnimation;
import com.yujunkang.fangxinbao.control.validator.EmailValidator;
import com.yujunkang.fangxinbao.control.validator.FormEditText;
import com.yujunkang.fangxinbao.control.validator.OrValidator;
import com.yujunkang.fangxinbao.control.validator.PhoneValidator;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.Utils;
import com.yujunkang.fangxinbao.utility.DataConstants.EditEmailLanucherType;
import com.yujunkang.fangxinbao.utility.DataConstants.VerifyCodeLanucherType;

/**
 * 
 * @date 2014-7-22
 * @author xieb
 * 
 */
public class EditAccountActivity extends ActivityWrapper {

	private static final String TAG = "EditAccountActivity";
	public static final String INTENT_EXTRA_ACCOUNT = DataConstants.PACKAGE_NAME
			+ ".EditAccountActivity.INTENT_EXTRA_ACCOUNT";
	private FormEditText et_account;

	private View btn_done;

	private String account;
	private Country mSelectCountry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_account_activity);
		initControl();
		ensureUi();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		Intent intent = getIntent();
		if (intent.hasExtra(INTENT_EXTRA_ACCOUNT)) {
			account = intent.getStringExtra(INTENT_EXTRA_ACCOUNT);
		}
		if (intent.hasExtra(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY)) {
			mSelectCountry = intent
					.getParcelableExtra(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY);
		}

	}

	private void initControl() {
		et_account = (FormEditText) findViewById(R.id.et_account);
		btn_done = findViewById(R.id.btn_done);
		btn_done.setOnClickListener(this);
	}

	private void ensureUi() {
		et_account.addTextChangedListener(onChange);
		et_account.setText(account);
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
			btn_done.setEnabled(VerificationEmptyInput());
		}
	};

	private void startEditEmailActivity() {
		Intent intent = new Intent(getSelfContext(), EditEmailActivity.class);
		intent.putExtra(EditEmailActivity.INTENT_EXTRA_EMAIL_LANUCHER_TYPE,
				EditEmailLanucherType.FORGET_PASSWORD);
		intent.putExtra(EditEmailActivity.INTENT_EXTRA_EMAIL, et_account
				.getText().toString());
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_done: {
			String account = et_account.getText().toString();
			if (et_account.testValidity()) {
				// 如果是手机号就走手机号验证的流程
				if (Utils.isMobile(account)) {
					startFetchVerifyCodeActivity();
				} else if (Utils.isEmail(account)) {
					// 邮箱登录方式
					startEditEmailActivity();
				}
				finish();
			}
			break;
		}
		}

	}

	private boolean VerificationEmptyInput() {

		if (TextUtils.isEmpty(et_account.getText().toString())) {

			return false;
		}
		return true;
	}
}
