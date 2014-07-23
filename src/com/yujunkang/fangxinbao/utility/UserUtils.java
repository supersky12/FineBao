package com.yujunkang.fangxinbao.utility;

import com.yujunkang.fangxinbao.R;

import android.content.Context;
import android.text.TextUtils;

/**
 * 
 * @date 2014-6-13
 * @author xieb
 * 
 */
public class UserUtils {
	public final static String Sex_Boy = "1";
	public final static String Sex_Girl = "0";

	public static String getSex(String type, Context context) {
		if (!TextUtils.isEmpty(type)) {
			if (type.equals(Sex_Boy)) {
				return context.getResources().getString(R.string.baby_sex_boy);
			} else if (type.equals(Sex_Girl)) {
				return context.getResources().getString(R.string.baby_sex_girl);
			}

		}
		return "";
	}
}
