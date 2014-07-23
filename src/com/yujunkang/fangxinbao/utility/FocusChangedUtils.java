package com.yujunkang.fangxinbao.utility;

import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;

/**
 * 
 * 
 * @date 2014-6-2
 * @author xieb
 * 
 */
public class FocusChangedUtils {
	public final static float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0,
			1, 0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };
	public final static float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0,
			0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

	public final static OnFocusChangeListener buttonOnFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!v.isEnabled()) {
				return;
			}
			if (hasFocus) {
				if (v.getBackground() != null) {
					v.getBackground().setColorFilter(
							new ColorMatrixColorFilter(BT_SELECTED));
					v.setBackgroundDrawable(v.getBackground());
				} else {
					setClickStyle(true, v);
				}
			} else {
				if (v.getBackground() != null) {
					v.getBackground().setColorFilter(
							new ColorMatrixColorFilter(BT_NOT_SELECTED));
					v.setBackgroundDrawable(v.getBackground());
				} else {
					setClickStyle(false, v);
				}

			}
		}
	};

	public final static OnTouchListener buttonOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (!v.isEnabled()) {
				return false;
			}
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (v.getBackground() != null) {
					v.getBackground().setColorFilter(
							new ColorMatrixColorFilter(BT_SELECTED));
					v.setBackgroundDrawable(v.getBackground());
				} else {
					setClickStyle(true, v);
				}
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

			} else {
				if (v.getBackground() != null) {
					v.getBackground().setColorFilter(
							new ColorMatrixColorFilter(BT_NOT_SELECTED));
					v.setBackgroundDrawable(v.getBackground());
				} else {
					setClickStyle(false, v);
				}
			}
			return false;
		}
	};

	private static void setClickStyle(final boolean isClick, final View v) {
		Handler handler  = v.getHandler();
		v.clearAnimation();
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				if (isClick) {
					// ����Ч��
					disableListItemView(v);
				} else {
					// ����Ч��
					enableView(v);
					v.clearAnimation();
				}
			}
		}, 100);
	}

	private static void enableView(View view) {
		AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 1.0f);
		alphaDown.setDuration(0);
		alphaDown.setFillAfter(true);
		view.startAnimation(alphaDown);
	}

	private static void disableListItemView(View view) {
		AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0.4f);
		alphaDown.setDuration(0);
		alphaDown.setFillAfter(true);
		view.startAnimation(alphaDown);
	}

	/**
	 * ����ͼƬ�ؼ���ȡ����ı�״̬
	 * 
	 * @param view
	 */
	public final static void setViewFocusChanged(View inView) {
		inView.setOnTouchListener(buttonOnTouchListener);
		inView.setOnFocusChangeListener(buttonOnFocusChangeListener);
	}

	/**
	 * ȡ��ͼƬ�ؼ���ȡ����ı�״̬
	 * 
	 * @param view
	 */
	public final static void CancelViewFocusChanged(View inView) {
		inView.setOnTouchListener(null);
		inView.setOnFocusChangeListener(null);
	}

	/**
	 * ����ؼ��ɵ�����
	 * 
	 * @param view
	 */
	public final static void expandTouchArea(final View view) {
		final View parent = (View) view.getParent();
		parent.post(new Runnable() {

			public void run() {
				final Rect r = new Rect();
				view.getHitRect(r);
				r.top -= 15;
				r.bottom += 15;
				r.left -= 15;
				r.right += 15;
				parent.setTouchDelegate(new TouchDelegate(r, view));
			}
		});
	}

	public final static void expandTouchArea(final View view, final int left,
			final int top, final int right, final int bottom) {
		final View parent = (View) view.getParent();
		parent.post(new Runnable() {

			public void run() {
				final Rect r = new Rect();
				view.getHitRect(r);
				r.top -= top;
				r.bottom += bottom;
				r.left -= left;
				r.right += right;
				parent.setTouchDelegate(new TouchDelegate(r, view));
			}
		});
	}
}
