package com.yujunkang.fangxinbao.utility;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.Sex;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class DataConstants {
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String APP_ID = "3333";
	public static final float DEFAULT_MAX_TEMPERATURE = 42f;
	public static final float DEFAULT_NORMAL_TEMPERATURE = 35.5f;
	public static final float DEFAULT_MIDDLE_TEMPERATURE = 40f;
	public static final String DEFAULT_COUNTRY_CODE = "CN";

	public static final String HTTP_PARAM_PID = "pid";
	public static final String HTTP_PARAM_UID = "uid";
	public static final String HTTP_PARAM_CVER = "cver";
	public static final String HTTP_PARAM_DVERC = "dverc";
	public static final String HTTP_PARAM_DVERT = "dvert";
	public static final String HTTP_PARAM_IVER = "iver";
	public static final String HTTP_PARAM_SOURCE = "source";
	public static final String HTTP_PARAM_PLATFORM = "platform";

	public static final String HTTP_PARAM_NAME_IMEI = "imei";

	public static final String HTTP_PARAM_NAME_SID = "sid";
	public static final String HTTP_PARAM_NAME_PID = "pid";
	public static final String HTTP_PARAM_WIDTH = "w";
	public static final String HTTP_PARAM_HEIGHT = "h";

	public static final String HTTP_PARAM_USERID = "userid";
	public static final String HTTP_PARAM_TIMESTAMP = "timestamp";
	public static final String HTTP_PARAM_ISZH = "iszh";
	public static final String PACKAGE_NAME = "com.yujunkang.fangxinbao";

	public static final String FILE_CACHE_PATH = "/fangxinbao-BitmapCache";
	public static final String SMS_ADDRESS = "106550101872218386";
	public static final String BAIDU_PUSH_API_KEY = "eV1bke0TdC9WSIVa0HHi9kcf";
	public static final String SINA_APP_ID = "3028249165";
	public static final String QQ_APP_ID = "101136751";
	public static final String WEIXIN_APP_ID = "wxc2449fddb9ce631b";
	public static final int WEIXIN_FRIEND_VER = 553779201;//0x21020001,微信的版本必须大于指定值时才有朋友圈功能
	public static final String APP_WEB_PROTAL = "http://developer.baidu.com/";
	
	public static String CVER = "1.0";
	
	
	 public static final int THUMB_SIZE = /*150*/100;//微信上传图片尺寸限制

	public static int DEVICE_WIDTH;
	public static int DEVICE_HEIGHT;
	public static float scaledDensity;

	public static int densityDpi = -1;

	public static class CommonAction {
		// 关闭所有进程
		public static final int CLOSE_ALL_ACTIVITY = 1;
		// 获取宝贝信息
		public static final int UPDATE_BABY_INFO = 2;
		// 获取用户信息
		public static final int ACTIVITY_USERMAN_UPDATE_USER_INFO = 3;
		// 修改手机号信息
		public static final int ACTIVITY_MODIFY_PHONE_SUCCESS = 4;

		// 绑定邮箱
		public static final int ACTIVITY_MODIFY_EMAIL_SUCCESS = 5;
		// 添加宝宝
		public static final int ADD_BABY = 6;
		// 添加宝宝
				public static final int SWITCH_BABY = 7;
	}

	public static Group<Sex> SexList = new Group<Sex>();

	public static enum VerifyCodeLanucherType {
		MODIFY_PHONE, FORGET_PASSWORD, REGISTER
	};

	public static enum SettingTemperatureLanucherType {
		Max, Min
	};
	
	
	
	public static enum EditEmailLanucherType {
		FORGET_PASSWORD, REGISTER, BINDING
	};

	public static enum TemperatureType {
		Celsius,Fahrenheit
	};
	
	
	public static String FETCH_SMSCODE_TYPE_MODIFY_PHONE = "0";
	public static String FETCH_SMSCODE_TYPE_FORGET_PASSWORD = "1";
	public static String FETCH_SMSCODE_TYPE_REGISTER = "2";

	public static void init(Context context) {
		// 获取屏幕尺寸
		if (densityDpi < 0) {
			DisplayMetrics metrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(metrics);
			DataConstants.DEVICE_WIDTH = metrics.widthPixels;
			DataConstants.DEVICE_HEIGHT = metrics.heightPixels;
			DataConstants.scaledDensity = metrics.scaledDensity;
			DataConstants.densityDpi = metrics.densityDpi;
		}
		SexList.clear();
		SexList.add(new Sex(context.getResources().getString(
				R.string.baby_sex_boy), UserUtils.Sex_Boy));
		SexList.add(new Sex(context.getResources().getString(
				R.string.baby_sex_girl), UserUtils.Sex_Girl));

	}
}
