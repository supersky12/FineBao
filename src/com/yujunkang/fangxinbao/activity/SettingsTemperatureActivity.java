package com.yujunkang.fangxinbao.activity;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.control.SettingTemperatureView;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.SettingTemperatureLanucherType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * @date 2014-6-18
 * @author xieb
 * 
 */
public class SettingsTemperatureActivity extends ActivityWrapper {
	public static final String INTENT_EXTRA_LAUNCHERTYPE = DataConstants.PACKAGE_NAME
			+ ".SettingsTemperatureActivity.INTENT_EXTRA_LAUNCHERTYPE";
	public static final String INTENT_EXTRA_TEMPERATURE = DataConstants.PACKAGE_NAME
			+ ".SettingsTemperatureActivity.INTENT_EXTRA_TEMPERATURE";
	private TextView tv_setting_temperature;
	private TextView tv_title;
	private SettingTemperatureView v_settings_temperature;
	private View btn_confirm;
	private SettingTemperatureLanucherType launcherType;
	private float mTemperature = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_temperature_activity);
		initControl();
		ensureUi();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		Intent intent = getIntent();
		if (intent.hasExtra(INTENT_EXTRA_LAUNCHERTYPE)) {
			launcherType = (SettingTemperatureLanucherType) intent
					.getSerializableExtra(INTENT_EXTRA_LAUNCHERTYPE);
		}
		if (intent.hasExtra(INTENT_EXTRA_TEMPERATURE)) {
			mTemperature =  intent.getFloatExtra(INTENT_EXTRA_TEMPERATURE, -1);
				
		}
	
	}

	private void initControl() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_setting_temperature = (TextView) findViewById(R.id.tv_setting_temperature);
		v_settings_temperature = (SettingTemperatureView) findViewById(R.id.v_settings_temperature);
		btn_confirm = findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
	}

	private void ensureUi() {
		if (launcherType == SettingTemperatureLanucherType.Max) {
			tv_title.setText(getString(R.string.settings_max_temperature_title));
			tv_setting_temperature.setText(getString(R.string.settings_max_temperature_prompt));
		} else if (launcherType == SettingTemperatureLanucherType.Min) {
			tv_title.setText(getString(R.string.settings_min_temperature_title));
			tv_setting_temperature.setText(getString(R.string.settings_min_temperature_prompt));
		}
		
		if(mTemperature != -1)
		{
			v_settings_temperature.setTemperatureValue(mTemperature);
		}
		else
		{
			//v_settings_temperature.setTemperatureValue(mTemperature);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm: {
			Intent intent = new Intent();
			intent.putExtra(INTENT_EXTRA_TEMPERATURE, v_settings_temperature.getValue());
			setResult(Activity.RESULT_OK,intent);
			finish();
			break;
		}
		}

	}

}
