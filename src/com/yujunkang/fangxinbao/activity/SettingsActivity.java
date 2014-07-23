package com.yujunkang.fangxinbao.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.user.ChangePhoneLoginActivity;
import com.yujunkang.fangxinbao.activity.user.EditBabyNickNameActivity;
import com.yujunkang.fangxinbao.activity.user.EditEmailActivity;
import com.yujunkang.fangxinbao.activity.user.FetchVerifyCodeActivity;
import com.yujunkang.fangxinbao.activity.user.ModifyPasswordActivity;
import com.yujunkang.fangxinbao.activity.user.PictureCropActivity;
import com.yujunkang.fangxinbao.activity.user.UserBabyInfoActivity;
import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.control.PhoneTextView;
import com.yujunkang.fangxinbao.control.TableItemView;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.model.UserAccount;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.TemperatureType;
import com.yujunkang.fangxinbao.utility.DialogHelper;
import com.yujunkang.fangxinbao.utility.OnPageNotifyListener;
import com.yujunkang.fangxinbao.utility.TypeUtils;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.utility.DataConstants.EditEmailLanucherType;
import com.yujunkang.fangxinbao.utility.DataConstants.SettingTemperatureLanucherType;
import com.yujunkang.fangxinbao.utility.Utils;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 
 * @date 2014-6-18
 * @author xieb
 * 
 */
public class SettingsActivity extends ActivityWrapper {

	private static final int REQUEST_ACTIVITY_CODE_SETTING_TEMPERATURE = 1;
	private static final int REQUEST_ACTIVITY_CODE_BINDING_EMAIL = 2;
	private TableItemView btn_temperature_switch;
	private TableItemView btn_max_temperature;
	private TableItemView btn_min_temperature;
	private View btn_modify_phone;
	private View btn_modify_password;
	private PhoneTextView tv_phone;
	private TextView tv_password;
	private TableItemView btn_binding_sina;
	private TableItemView btn_email;
	private View btn_modify_language;
	private View btn_logout;
	private View icon_logout;
	private Dialog mTemperatureSwitchDialog;
	

	private User mUserInfo;
	private SettingTemperatureLanucherType settingTemperatureType;
	private int temperatureType;
	private float maxTemperature;
	private float minTemperature;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		init();
		initControl();
		ensureUi();
	}

	private void init() {
		mUserInfo = Preferences.getUserCommonInfo(getSelfContext());
		// 温度相关
		temperatureType = mApplication.getPrefs().getInt(
				Preferences.PREFERENCE_NORMAL_TEMPERATURE, -1);
		maxTemperature = mApplication.getPrefs().getFloat(
				Preferences.PREFERENCE_MAX_TEMPERATURE, -1);
		minTemperature = mApplication.getPrefs().getFloat(
				Preferences.PREFERENCE_MIN_TEMPERATURE, -1);
	}

	private void initControl() {
		btn_temperature_switch = (TableItemView) findViewById(R.id.btn_temperature_switch);
		btn_max_temperature = (TableItemView) findViewById(R.id.btn_max_temperature);
		btn_min_temperature = (TableItemView) findViewById(R.id.btn_min_temperature);

		btn_modify_phone = findViewById(R.id.btn_modify_phone);
		tv_phone = (PhoneTextView) findViewById(R.id.tv_phone);
		btn_modify_password = findViewById(R.id.btn_modify_password);
		tv_password = (TextView) findViewById(R.id.tv_password);

		btn_binding_sina = (TableItemView) findViewById(R.id.btn_binding_sina);
		btn_email = (TableItemView) findViewById(R.id.btn_email);
		btn_modify_language = findViewById(R.id.btn_modify_language);
		btn_logout = findViewById(R.id.btn_logout);

		btn_temperature_switch.setOnClickListener(this);
		btn_max_temperature.setOnClickListener(this);
		btn_min_temperature.setOnClickListener(this);
		btn_modify_phone.setOnClickListener(this);
		btn_modify_password.setOnClickListener(this);
		btn_binding_sina.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
	}

	private void ensureUi() {
		// 设置手机号
		String phone = mUserInfo.getPhone();
		if (!TextUtils.isEmpty(phone)) {
			tv_phone.setFilled(true);
			tv_phone.setText(phone);
		} else {
			tv_phone.setText("");
		}
		// 设置密码
		String password = Preferences.getPassword(getSelfContext());
		if (!TextUtils.isEmpty(phone)) {
			tv_password.setText(password);
		}
		// 设置邮箱
		String email = mUserInfo.getEmail();
		if (TextUtils.isEmpty(email)) {
			btn_email.setClickable(true);
			btn_email.setOnClickListener(this);
		} else {
			btn_email.setClickable(false);
			btn_email.setShowIcon(false);
			btn_email.setRightText(email);
		}
		ensureTemperatureUi();
	}

	private void ensureTemperatureUi() {
		// 温度相关
		if (maxTemperature > 0) {

			btn_max_temperature.setRightText(
					TypeUtils.getTemperatureScaleValue(1, maxTemperature,mApplication.getLocale(),temperatureType,getSelfContext()));
		}
		if (minTemperature > 0) {

			btn_min_temperature.setRightText(TypeUtils.getTemperatureScaleValue(1, minTemperature,mApplication.getLocale(),temperatureType,getSelfContext()));
		}

	}

	private void startSettingsTemperatureActivity(
			SettingTemperatureLanucherType type,float value) {
		settingTemperatureType = type;
		Intent intent = new Intent(getSelfContext(),
				SettingsTemperatureActivity.class);
		intent.putExtra(SettingsTemperatureActivity.INTENT_EXTRA_LAUNCHERTYPE,
				type);
		intent.putExtra(SettingsTemperatureActivity.INTENT_EXTRA_TEMPERATURE,
				value);
		startActivityForResult(intent,
				REQUEST_ACTIVITY_CODE_SETTING_TEMPERATURE);
	}

	private void startModifyPhoneActivity() {

		Intent intent = new Intent(getSelfContext(),
				ChangePhoneLoginActivity.class);
		startActivity(intent);
	}

	private void startModifPasswordActivity() {

		Intent intent = new Intent(getSelfContext(),
				ModifyPasswordActivity.class);
		startActivity(intent);
	}

	private void startBindEmailActivity() {

		Intent intent = new Intent(getSelfContext(), EditEmailActivity.class);
		intent.putExtra(EditEmailActivity.INTENT_EXTRA_EMAIL_LANUCHER_TYPE,
				EditEmailLanucherType.BINDING);
		startActivity(intent);
	}

	private void showTemperatureSwitchMenu() {
		ArrayList<View> menuList = new ArrayList<View>();
		

		TextView btn_DegreeCelsius  = (TextView) LayoutInflater.from(getSelfContext())
				.inflate(R.layout.dialog_bottom_button_view, null);
		btn_DegreeCelsius.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				temperatureType = TemperatureType.Celsius.ordinal();
				ensureTemperatureUi();
				//保存设置
				Editor editor = mApplication.getPrefs().edit();
				editor.putInt(Preferences.PREFERENCE_NORMAL_TEMPERATURE, temperatureType);
				editor.commit();
				mTemperatureSwitchDialog.dismiss();
			}
		});
		btn_DegreeCelsius.setText(getString(R.string.button_degreecelsius));

		menuList.add(btn_DegreeCelsius);
		
		TextView btn_DegreesFahrenheit  = (TextView) LayoutInflater.from(getSelfContext())
				.inflate(R.layout.dialog_bottom_button_view, null);
		btn_DegreesFahrenheit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				temperatureType = TemperatureType.Fahrenheit.ordinal();
				ensureTemperatureUi();
				//保存设置
				Editor editor = mApplication.getPrefs().edit();
				editor.putInt(Preferences.PREFERENCE_NORMAL_TEMPERATURE, temperatureType);
				editor.commit();
				mTemperatureSwitchDialog.dismiss();
			}
		});
		btn_DegreesFahrenheit.setText(getString(R.string.button_degreefahrenheit));

		menuList.add(btn_DegreesFahrenheit);
		
		
		TextView btn_cancel = (TextView) LayoutInflater.from(getSelfContext())
				.inflate(R.layout.dialog_bottom_button_view, null);
		btn_cancel.setText(getString(R.string.dialog_cancel_text));
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTemperatureSwitchDialog != null
						&& mTemperatureSwitchDialog.isShowing()) {
					mTemperatureSwitchDialog.dismiss();
				}

			}
		});
		menuList.add(btn_cancel);
		mTemperatureSwitchDialog = mDialog.popButtonListDialogMenu(menuList);
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_ACTIVITY_CODE_SETTING_TEMPERATURE://
				if (data != null) {
					float temperatureValue = data
							.getFloatExtra(
									SettingsTemperatureActivity.INTENT_EXTRA_TEMPERATURE,
									-1);
					Editor editor = mApplication.getPrefs().edit();
					if (settingTemperatureType == SettingTemperatureLanucherType.Max) {
						maxTemperature = temperatureValue;
						btn_max_temperature.setRightText(
								TypeUtils.getTemperatureScaleValue(1, temperatureValue,mApplication.getLocale(), temperatureType,getSelfContext()));
						editor.putFloat(Preferences.PREFERENCE_MAX_TEMPERATURE,
								temperatureValue);
						editor.commit();
					} else if (settingTemperatureType == SettingTemperatureLanucherType.Min) {
						minTemperature = temperatureValue;
						btn_min_temperature.setRightText(
								TypeUtils.getTemperatureScaleValue(1, temperatureValue,mApplication.getLocale(), temperatureType,getSelfContext()));
						editor.putFloat(Preferences.PREFERENCE_MIN_TEMPERATURE,
								temperatureValue);
						editor.commit();
					}
				}
				break;
			default:
				//
				break;
			}
		}
	}

	@Override
	public OnPageNotifyListener generatePageNotifyListener() {
		return new OnPageNotifyListener() {
			@Override
			public void onNotify(int what, Bundle data) {
				switch (what) {
				// 修改手机号成功
				case CommonAction.ACTIVITY_MODIFY_PHONE_SUCCESS: {
					DialogHelper.showSuccessAlertDialogInCenter(
							getString(R.string.modify_phone_success_tip),
							getSelfContext(), true, -1);
					if (data != null
							&& data.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE)) {
						String phone = data
								.getString(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE);
						tv_phone.setText(phone);
						DialogHelper.showSuccessAlertDialogInCenter(
								getString(R.string.modify_phone_success_tip),
								getSelfContext(), true, -1);
					}

					break;
				}

				}

			}
		};
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_temperature_switch: {
			showTemperatureSwitchMenu();
			break;
		}
		case R.id.btn_max_temperature: {
			startSettingsTemperatureActivity(SettingTemperatureLanucherType.Max,maxTemperature);
			break;
		}
		case R.id.btn_min_temperature: {
			startSettingsTemperatureActivity(SettingTemperatureLanucherType.Min,minTemperature);
			break;
		}
		case R.id.btn_modify_phone: {
			startModifyPhoneActivity();
			break;
		}
		case R.id.btn_modify_password: {
			startModifPasswordActivity();
			break;
		}
		case R.id.btn_binding_sina: {
			break;
		}
		case R.id.btn_email: {
			startBindEmailActivity();
			break;
		}
		case R.id.btn_modify_language: {
			break;
		}
		case R.id.btn_logout:
			sendNotificationBroad(DataConstants.CommonAction.CLOSE_ALL_ACTIVITY);
			Preferences.logoutUser(getSelfContext());
			Intent intent = new Intent(getSelfContext(), MainActivity.class);
			startActivity(intent);
			break;
		}
	}

}
