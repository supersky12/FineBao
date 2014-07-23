package com.yujunkang.fangxinbao.activity;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.UiUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * @date 2014-6-17
 * @author xieb
 * 
 */
public class AboutActivity extends ActivityWrapper {
	private static final String TAG = "AboutActivity";
	private View btn_sina_weibo;
	private View btn_call_phone;
	private View btn_check_version;
	private View btn_send_feedback;
	private View btn_agreement;
	private TextView tv_version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		initControl();
		ensureUi();
	}

	private void initControl() {
		btn_sina_weibo = findViewById(R.id.btn_sina_weibo);
		btn_call_phone = findViewById(R.id.btn_call_phone);
		btn_check_version = findViewById(R.id.btn_check_version);
		btn_send_feedback = findViewById(R.id.btn_send_feedback);
		btn_agreement = findViewById(R.id.btn_agreement);
		tv_version = (TextView) findViewById(R.id.tv_version);

		btn_sina_weibo.setOnClickListener(this);
		btn_call_phone.setOnClickListener(this);
		btn_check_version.setOnClickListener(this);
		btn_agreement.setOnClickListener(this);
		btn_send_feedback.setOnClickListener(this);
	}

	private void ensureUi() {
		StringBuilder versionSb = new StringBuilder(
				getString(R.string.app_name));
		versionSb.append(" ");
		versionSb.append(DataConstants.CVER);
		tv_version.setText(versionSb.toString());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sina_weibo: {
			break;
		}
		case R.id.btn_call_phone: {
			break;
		}
		case R.id.btn_check_version: {
			UiUtils.showAlertDialog("目前是最新版本", getSelfContext());
			break;
		}
		case R.id.btn_send_feedback: {
			startFeedbackActivity();
			break;
		}
		case R.id.btn_agreement: {
			break;
		}
		}

	}

	
	private void startFeedbackActivity()
	{
		Intent intent = new Intent(getSelfContext(),FeedBackActivity.class);
		startActivity(intent);
		
	}
}
