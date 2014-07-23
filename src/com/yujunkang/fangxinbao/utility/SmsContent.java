package com.yujunkang.fangxinbao.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yujunkang.fangxinbao.method.Method;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Telephony.Sms.Inbox;
import android.widget.EditText;

/**
 * 
 * @date 2014-7-3
 * @author xieb
 * 
 */
public class SmsContent extends ContentObserver {
	private static final String TAG = "SmsContent";
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	private Activity mActivity = null;
	private String smsContent = "";
	private EditText mVerifyText = null;

	public SmsContent(Activity activity, Handler handler, EditText verifyText) {
		super(handler);
		mActivity = activity;
		mVerifyText = verifyText;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		LoggerTool.d(TAG, "onChange");
		Cursor cursor = null;// 光标
		// 读取收件箱中指定号码的短信

		cursor = mActivity.managedQuery(Uri.parse(SMS_URI_INBOX), new String[] {
				Inbox._ID, "address", "read", "body" },
				"address=? and read=?", new String[] { Method.getSmsAddress(mActivity.getApplicationContext()), "0" },
				"_id desc");
		if (cursor != null && cursor.getCount() > 0) {  
	        ContentValues values = new ContentValues();  
	        values.put("read", "1"); // 修改短信为已读模式  
	        cursor.moveToNext();  
	        int smsbodyColumn = cursor.getColumnIndex("body");  
	        String smsBody = cursor.getString(smsbodyColumn);  
	        mVerifyText.setText(getDynamicPassword(smsBody));
	  
	    }  
		 // 在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上， 会发生崩溃  
	    if (Build.VERSION.SDK_INT < 14) {  
	        cursor.close();  
	    }  

	}
	
	/**
	 * 从字符串中截取连续6位数字组合 ([0-9]{" + 6 + "})截取六位数字 进行前后断言不能出现数字 用于从短信中获取动态密码
	 * 
	 * @param str
	 *            短信内容
	 * @return 截取得到的6位动态密码
	 */
	public static String getDynamicPassword(String str) {
		// 6是验证码的位数一般为六位
		Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{"
				+ 6 + "})(?![0-9])");
		Matcher m = continuousNumberPattern.matcher(str);
		String dynamicPassword = "";
		while (m.find()) {
			dynamicPassword = m.group();
			LoggerTool.d(TAG, dynamicPassword);
		}
		return dynamicPassword;
	}

}
