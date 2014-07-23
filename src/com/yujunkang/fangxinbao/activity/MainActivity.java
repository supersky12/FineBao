package com.yujunkang.fangxinbao.activity;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.user.FetchVerifyCodeActivity;
import com.yujunkang.fangxinbao.activity.user.LoginActivity;
import com.yujunkang.fangxinbao.utility.DataConstants.VerifyCodeLanucherType;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class MainActivity extends ActivityWrapper {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		ensureUi();
	}

	private void ensureUi() {
		View btn_register = findViewById(R.id.btn_register);
		btn_register.setOnClickListener(this);
		View btn_login = findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_register: {
			startRegisterActivity();
			//finish();
			break;
		}
		case R.id.btn_login: {
			startLoginActivity();
			//finish();
			break;
		}
		}
	}
	
	private void startLoginActivity()
	{
		Intent intent = new Intent(this,LoginActivity.class);
		startActivity(intent);
	}
	
	private void startRegisterActivity()
	{
		Intent intent = new Intent(this,FetchVerifyCodeActivity.class);
		intent.putExtra(FetchVerifyCodeActivity.INTENT_EXTRA_LANUCHER_TYPE, VerifyCodeLanucherType.REGISTER);
		startActivity(intent);
	}
	

}
