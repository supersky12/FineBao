package com.yujunkang.fangxinbao.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;



/**
 * 
 * @date 2014-6-22
 * @author xieb
 * 
 */
public class ExpandAnimation extends Animation{
	private View mAnimatedView;
	private int mStartHeight;
	private int orginalHeigth ;
	
	/**
	 * 
	 * @param view
	 * @param duration
	 */
	public ExpandAnimation(View view, int duration) {
		setDuration(duration);
		mAnimatedView = view;
		mStartHeight = mAnimatedView.getLayoutParams().height;
		
		
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		if (interpolatedTime < 1.0f) {
			mAnimatedView.getLayoutParams().height = mStartHeight
					+ (int) ((orginalHeigth-mStartHeight) * interpolatedTime);
		mAnimatedView.requestLayout();
		} else {
			mAnimatedView.getLayoutParams().height = orginalHeigth;
			mAnimatedView.requestLayout();
			//mAnimatedView.getLayoutParams().height = orginalHeigth;
		}

		
		
	}

	public  void setHeightForWrapContent(View view, int height) {
		orginalHeigth = height;
	}

	
}


