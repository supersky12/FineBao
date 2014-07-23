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
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.model.UserAccount;
import com.yujunkang.fangxinbao.parser.BaseDataParser;
import com.yujunkang.fangxinbao.parser.UserParser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnPrepareTaskListener;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.Utils;
import com.yujunkang.fangxinbao.utility.DataConstants.EditEmailLanucherType;
import com.yujunkang.fangxinbao.utility.DataConstants.VerifyCodeLanucherType;

/**
 * 
 * @date 2014-6-20
 * @author xieb
 * 
 */
public class ModifyPasswordActivity extends ActivityWrapper {
	private static final String TAG = "ModifyPasswordActivity";

	/**
	 * 控件
	 */
	private EditText et_old_password;
	private View btn_forget_password;
	private EditText et_new_password;

	private NetworkProgressButton btn_modify_password;

	private User user;
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
			
			btn_modify_password.setEnabled(VerificationEmptyInput());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_password_activity);
		init();
		initControl();
		ensureUi();
	}

	private void init() {
		user = Preferences.getUserCommonInfo(getSelfContext());
	}

	private void initControl() {
		btn_modify_password = (NetworkProgressButton) findViewById(R.id.btn_modify_password);
		et_old_password = (EditText) findViewById(R.id.et_old_password);
		et_new_password = (EditText) findViewById(R.id.et_new_password);
		btn_forget_password = findViewById(R.id.btn_forget_password);
		btn_modify_password.setOnClickListener(this);
		btn_forget_password.setOnClickListener(this);
	}

	private void lock()
	{
		UiUtils.disableView(et_old_password);
		UiUtils.disableView(et_new_password);
	}
	
	private void unlock()
	{
		
		UiUtils.enableView(et_old_password);
		UiUtils.enableView(et_new_password);
	}
	
	
	
	
	private void ensureUi() {
		et_old_password.addTextChangedListener(onChange);
		et_new_password.addTextChangedListener(onChange);
	}

	private void startFetchVerifyCodeActivity() {
		Intent intent = new Intent(this, FetchVerifyCodeActivity.class);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE,
				VerifyCodeLanucherType.FORGET_PASSWORD);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE,
				user.getPhone());
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY,
				user.getCountry());
		startActivity(intent);
	}

	/**
	 * 启动
	 */
	private void startEditEmailActivity() {
		Intent intent = new Intent(getSelfContext(), EditEmailActivity.class);
		intent.putExtra(EditEmailActivity.INTENT_EXTRA_EMAIL_LANUCHER_TYPE,
				EditEmailLanucherType.FORGET_PASSWORD);
		intent.putExtra(EditEmailActivity.INTENT_EXTRA_EMAIL, user.getEmail());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_modify_password: {
			startModifyPasswordTask();
			break;
		}
		case R.id.btn_forget_password: {
			UserAccount account = Preferences.getUserAccount(getSelfContext());
			if (account.getLoginType() == UserAccount.LOGINTYPE_PHONE) {
				startFetchVerifyCodeActivity();
			} else {
				startEditEmailActivity();
			}
			break;
		}
		}

	}

	// 登录
	private void startModifyPasswordTask() {
		FangXinBaoAsyncTask<BaseData> mTask = FangXinBaoAsyncTask
				.createInstance(getSelfContext(),
						UrlManager.URL_MODIFY_PASSWORD, new BaseDataParser(),
						false);
		mTask.putParameter("oldpassword", et_old_password.getText().toString());
		mTask.putParameter("newpassword", et_new_password.getText().toString());
		mTask.setOnPrepareTaskListener(new OnPrepareTaskListener() {

			@Override
			public void onPreExecute() {
				btn_modify_password.preNetworkExecute();
				lock();

			}
		});
		mTask.setOnFinishedListener(onModifyFinishListener);
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	/**
	 * 登录成功回调
	 */
	private OnFinishedListener<BaseData> onModifyFinishListener = new OnFinishedListener<BaseData>() {
		@Override
		public void onFininshed(BaseData result) {
			btn_modify_password.finishNetworkExecute();
			unlock();
			if (result == null) {
				UiUtils.showAlertDialog(
						getString(R.string.modify_password_failed),
						getSelfContext());
			} else {
				if (result.code == 1) {
					UiUtils.showAlertDialog(
							getString(R.string.modify_password_success),
							getSelfContext());
					finish();
				} else {
					et_old_password.setText("");
					et_new_password.setText("");
					UiUtils.showAlertDialog(
							!TextUtils.isEmpty(result.getDesc()) ? result
									.getDesc()
									: getString(R.string.modify_password_failed),
							getSelfContext());
				}
			}

		}
	};

	private boolean VerificationEmptyInput() {
		if (TextUtils.isEmpty(et_old_password.getText().toString())) {
			return false;
		}
		if (TextUtils.isEmpty(et_new_password.getText().toString())) {
			return false;
		}
		return true;
	}

}
