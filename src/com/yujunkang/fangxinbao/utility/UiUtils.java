package com.yujunkang.fangxinbao.utility;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;



/**
 * 
 * @date 2014-5-30
 * @author xieb
 * 
 */
public class UiUtils {
	/**
	 * 弹出提示信息
	 */
	public static void showAlertDialog(String info, Context context) {
		if (!TextUtils.isEmpty(info)) {
			Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void disableView(View view) {
		AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0.4f);
		alphaDown.setDuration(0);
		alphaDown.setFillAfter(true);
		view.startAnimation(alphaDown);
		view.setClickable(false);
		view.setEnabled(false);
	}
	
	public static void enableView(View view) {
		AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 1.0f);
		alphaDown.setDuration(0);
		alphaDown.setFillAfter(true);
		view.startAnimation(alphaDown);
		view.setClickable(true);
		view.setEnabled(true);
	}

}


