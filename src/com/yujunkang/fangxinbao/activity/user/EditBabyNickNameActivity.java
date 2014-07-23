package com.yujunkang.fangxinbao.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.UiUtils;

/**
 * 
 * @date 2014-6-14
 * @author xieb
 * 
 */
public class EditBabyNickNameActivity extends ActivityWrapper {
	private static final String TAG = "EditBabyNickName";
	public static final String INTENT_EXTRA_NICKNAME = DataConstants.PACKAGE_NAME
			+ ".EditBabyNickName.INTENT_EXTRA_NICKNAME";

	private View btn_save;
	private EditText et_baby_nickname;
	private View ibtn_nickname_del;
	private String mNickName;
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
			btn_save.setEnabled(VerificationEmptyInput());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_baby_nickname_activity);
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

		if (extras.containsKey(INTENT_EXTRA_NICKNAME)) {
			mNickName = extras.getString(INTENT_EXTRA_NICKNAME);
		}

	}
	
	private void initControl()
	{
		btn_save = findViewById(R.id.btn_save);
		et_baby_nickname = (EditText) findViewById(R.id.et_baby_nickname);
		ibtn_nickname_del = (ImageButton) findViewById(R.id.ibtn_nickname_del);
		btn_save.setOnClickListener(this);
	}

	private void ensureUi() {
		/**
		 * 昵称
		 */
		
		ibtn_nickname_del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et_baby_nickname.setText(null);

			}
		});
		et_baby_nickname.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						et_baby_nickname.clearFocus();
					}
				}
				return false;
			}
		});
		et_baby_nickname.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && et_baby_nickname.length() > 0) {
					ibtn_nickname_del.setVisibility(View.VISIBLE);
				} else {
					ibtn_nickname_del.setVisibility(View.GONE);
				}
			}
		});

		et_baby_nickname.addTextChangedListener(new TextWatcher() {

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
				int len = et_baby_nickname.length();
				if (ibtn_nickname_del != null) {
					ibtn_nickname_del.setVisibility(len == 0 ? View.GONE
							: View.VISIBLE);
				}

			}
		});
		et_baby_nickname.setText(mNickName);

		et_baby_nickname.addTextChangedListener(onChange);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_save: {
			Intent intent = new Intent();
			intent.putExtra(INTENT_EXTRA_NICKNAME, et_baby_nickname.getText()
					.toString());
			setResult(Activity.RESULT_OK, intent);
			finish();
			break;
		}

		}

	}

	private boolean VerificationEmptyInput() {
		if (TextUtils.isEmpty(et_baby_nickname.getText().toString())) {
			return false;
		}

		return true;
	}

}
