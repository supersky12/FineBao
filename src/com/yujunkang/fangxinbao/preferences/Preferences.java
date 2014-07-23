package com.yujunkang.fangxinbao.preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.http.FangXinBaoHttpApi;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.UserAccount;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class Preferences {
	private static final String TAG = "Preferences";
	public static final String FILE_PREFERENCE_COMMON_SETTING = "file_setting_common";
	public static final String PREFERENCE_IS_FIRST_RUN = "is_first_run";
	/**
	 * 用户
	 */
	public static final String PREFERENCE_LOGIN = "user_login";
	public static final String PREFERENCE_PASSWORD = "user_password";

	public static final String PREFERENCE_ID = "id";
	public static final String PREFERENCE_USER_EMAIL = "user_email";
	public static final String PREFERENCE_USER_PHONE = "user_phone";
	public static final String PREFERENCE_USER_NATIONALITY = "user_nationality";
	public static final String PREFERENCE_USER_NATIONALITYCODE = "user_nationalitycode";
	public static final String PREFERENCE_USER_NATIONALITY_ENGNAME = "user_nationality_engname";
	public static final String PREFERENCE_USER_NATIONALITY_SIMPLE_CODE = "user_nationality_simplecode";
	public static final String PREFERENCE_USER_BABY = "user_baby";
	public static final String PREFERENCE_USER_TEMPERATURE = "user_temperature";

	/**
	 * http公共参数
	 */
	public static final String PREFERENCE_COMMON_UID = "common_uid";
	public static final String PREFERENCE_COMMON_DVERC = "common_dverc";
	public static final String PREFERENCE_COMMON_DVERT = "common_dvert";
	public static final String PREFERENCE_LOCALE = "preference_locale";
	public static final String PREFERENCE_SMS_ADDRESS = "preference_sms_address";
	public static final String PREFERENCE_TEMPERATURE_COMMON_DATA = "preference_temperature_data";
	

	/**
	 * 设置参数
	 */
	public static final String PREFERENCE_MAX_TEMPERATURE = "preference_max_temperature";
	public static final String PREFERENCE_MIN_TEMPERATURE = "preference_min_temperature";
	public static final String PREFERENCE_NORMAL_TEMPERATURE = "preference_normal_temperature";

	/*
	 * 设置指定文件和字段的字符串
	 */
	public static void setString(Context context, String filename,
			String field, String value) {
		if (TextUtils.isEmpty(value)) {
			return;
		}
		Editor edit = getPreferencesEditor(filename, context);
		edit.putString(field, value).commit();
	}

	/*
	 * 获取指定文件和字段的字符串
	 */
	public static String getString(Context context, String filename,
			String field) {
		return getString(context, filename, field, "");
	}

	/*
	 * 获取指定文件和字段的字符串，可以设置默认值
	 */
	public static String getString(Context context, String filename,
			String field, String defaultValue) {
		return getPreferences(filename, context).getString(field, defaultValue);
	}

	public static SharedPreferences getPreferences(String filename,
			Context context) {
		return !TextUtils.isEmpty(filename) ? context.getSharedPreferences(
				filename, Context.MODE_PRIVATE) : PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	public static Editor getPreferencesEditor(String filename, Context context) {
		return getPreferences(filename, context).edit();

	}

	public static boolean logoutUser(Context context) {
		Editor editor = FangXinBaoApplication.getApplication(context)
				.getPrefs().edit();
		editor.clear();
		editor.commit();
		FangXinBaoApplication.getApplication(context).getNetWorKManager()
				.setCredentials(null, null);
		return true;
	}

	public static User getUserInfo(Context context) {
		String userId = getString(context, null, PREFERENCE_ID);
		if (!TextUtils.isEmpty(userId)) {
			User user = new User();
			user.setId(userId);
			Country country = new Country();
			country.setName(getString(context, null,
					PREFERENCE_USER_NATIONALITY));
			country.setCountryCode(getString(context, null,
					PREFERENCE_USER_NATIONALITYCODE));
			country.setCountrySimpleCode(getString(context, null,
					PREFERENCE_USER_NATIONALITY_SIMPLE_CODE));
			country.setEngName(getString(context, null,
					PREFERENCE_USER_NATIONALITY_ENGNAME));
			user.setCountry(country);
			user.setPhone(getString(context, null, PREFERENCE_USER_PHONE));
			user.setEmail(getString(context, null, PREFERENCE_USER_EMAIL));
			String baby = getString(context, null, PREFERENCE_USER_BABY);
			if (!TextUtils.isEmpty(baby)) {
				Gson gson = new Gson();
				Group<Baby> babyGroup = gson.fromJson(baby,
						new TypeToken<Group<Baby>>() {
						}.getType());
				user.setBaBies(babyGroup);
				LoggerTool.d(TAG,
						"baby count: " + String.valueOf(babyGroup.size()));
			} else {
				user.setBaBies(new Group<Baby>());
			}
			return user;
		}
		return null;
	}

	public static User getUserCommonInfo(Context context) {
		String userId = getString(context, null, PREFERENCE_ID);
		if (!TextUtils.isEmpty(userId)) {
			User user = new User();
			user.setId(userId);
			Country country = new Country();
			country.setName(getString(context, null,
					PREFERENCE_USER_NATIONALITY));
			country.setCountryCode(getString(context, null,
					PREFERENCE_USER_NATIONALITYCODE));
			country.setCountrySimpleCode(getString(context, null,
					PREFERENCE_USER_NATIONALITY_SIMPLE_CODE));
			country.setEngName(getString(context, null,
					PREFERENCE_USER_NATIONALITY_ENGNAME));
			user.setCountry(country);
			user.setPhone(getString(context, null, PREFERENCE_USER_PHONE));
			user.setEmail(getString(context, null, PREFERENCE_USER_EMAIL));
			return user;
		}
		return null;
	}

	/**
	 * 保存用户名和账号
	 * 
	 * @param context
	 * @param login
	 * @param password
	 * @param countryCode
	 * @return
	 */
	public static boolean storeLoginAndPassword(Context context, String login,
			String password, String countryCode) {
		Editor editor = FangXinBaoApplication.getApplication(context)
				.getPrefs().edit();
		if (Utils.isMobile(login)) {
			StringBuilder sb = new StringBuilder(countryCode);
			sb.append(",");
			sb.append(login);
			login = sb.toString();
		}
		if (!TextUtils.isEmpty(login)) {
			editor.putString(Preferences.PREFERENCE_LOGIN, login);
		}

		if (!TextUtils.isEmpty(password)) {
			editor.putString(Preferences.PREFERENCE_PASSWORD, password);
		}
		if (!editor.commit()) {
			LoggerTool.d(TAG, "storeLoginAndPassword commit failed");
			return false;
		}
		FangXinBaoApplication.getApplication(context).getNetWorKManager()
				.setCredentials(login, password);
		return true;
	}

	/**
	 * 获取登录账号信息
	 * 
	 * @param context
	 * @return
	 */
	public static UserAccount getUserAccount(Context context) {
		UserAccount account = null;
		String login = getString(context, null, Preferences.PREFERENCE_LOGIN);
		if (!TextUtils.isEmpty(login)) {
			account = new UserAccount();
			account.setAccount(login);
			if (Utils.isEmail(login)) {
				account.setLoginType(UserAccount.LOGINTYPE_EMAIL);
			} else {
				account.setLoginType(UserAccount.LOGINTYPE_PHONE);
			}
			return account;

		}
		return null;
	}

	public static String getPassword(Context context) {

		return getString(context, null, PREFERENCE_PASSWORD);
	}

	/**
	 * 保存用户信息
	 * 
	 * @param context
	 * @param user
	 */
	@SuppressLint("CommitPrefEdits")
	public static void storeUser(Context context, User user) {
		if (user != null && user.getId() != null) {
			Editor editor = FangXinBaoApplication.getApplication(context)
					.getPrefs().edit();
			editor.putString(PREFERENCE_ID, user.getId());
			if (!TextUtils.isEmpty(user.getEmail())) {
				editor.putString(PREFERENCE_USER_EMAIL, user.getEmail());
			}
			if (!TextUtils.isEmpty(user.getPhone())) {
				editor.putString(PREFERENCE_USER_PHONE, user.getPhone());
			}
			Country country = user.getCountry();
			if (country != null) {
				if (!TextUtils.isEmpty(country.getName())) {
					editor.putString(PREFERENCE_USER_NATIONALITY, user
							.getCountry().getName());
				}
				if (!TextUtils.isEmpty(country.getCountryCode())) {
					editor.putString(PREFERENCE_USER_NATIONALITYCODE, user
							.getCountry().getCountryCode());
				}
				if (!TextUtils.isEmpty(country.getCountrySimpleCode())) {
					editor.putString(PREFERENCE_USER_NATIONALITY_SIMPLE_CODE,
							user.getCountry().getCountrySimpleCode());
				}
				if (!TextUtils.isEmpty(country.getEngName())) {
					editor.putString(PREFERENCE_USER_NATIONALITY_ENGNAME, user
							.getCountry().getEngName());
				}
			}
			if (!TextUtils.isEmpty(user.getTemperature())) {
				editor.putString(PREFERENCE_USER_TEMPERATURE,
						user.getTemperature());
			}
			if (user.getBaBies() != null && user.getBaBies().size() > 0) {
				Gson gson = new Gson();
				editor.putString(PREFERENCE_USER_BABY,
						gson.toJson(user.getBaBies()));
			}
			editor.commit();
			LoggerTool.d(TAG, "Setting user info");
		} else {
			LoggerTool.d(Preferences.TAG, "Unable to lookup user.");
		}
	}

	public static void loginUser(Context context, String login,
			String password, User user) {
		if (Preferences
				.storeLoginAndPassword(context, login, password, user
						.getCountry() != null ? user.getCountry()
						.getCountryCode() : "")) {
			Preferences.storeUser(context, user);
		}
	}

}
