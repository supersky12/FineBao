package com.yujunkang.fangxinbao.anim;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 
 * @date 2014-6-22
 * @author xieb
 * 
 */
public class CollapseAnimation extends Animation {
	private View mAnimatedView;
	private int mStartHeight;
	private int mStartVisibility;
	private int orginalHeigth;

	/**
	 * 
	 * @param view
	 * @param duration
	 */
	public CollapseAnimation(View view, int duration) {
		setDuration(duration);
		mAnimatedView = view;
		mStartHeight = mAnimatedView.getLayoutParams().height;

	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		if (interpolatedTime < 1.0f) {
			mStartHeight = mStartHeight
					- (int) ((mStartHeight - 180) * interpolatedTime);
			int tempHeight = mStartHeight;
			mAnimatedView.getLayoutParams().height = tempHeight;
			mAnimatedView.requestLayout();
		} else {
			mAnimatedView.getLayoutParams().height = orginalHeigth;
			mAnimatedView.requestLayout();
		}
	}

	public void setHeightForWrapContent(View view, int height) {
		orginalHeigth = height;
	}

	/**
	 * 
	 * @param activity
	 * @param view
	 */
	public static void setHeightForWrapContent(Activity activity, View view) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int screenWidth = metrics.widthPixels;

		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED);
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(screenWidth,
				MeasureSpec.EXACTLY);

		view.measure(widthMeasureSpec, heightMeasureSpec);
		int height = view.getMeasuredHeight();
		view.getLayoutParams().height = height;
	}

}
