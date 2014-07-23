package com.yujunkang.fangxinbao.control.calendar;

import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.AnimatorListenerAdapter;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.anim.ExpandCollapseAnimation;
import com.yujunkang.fangxinbao.utility.LoggerTool;


import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * 日历的行 {@link CalendarGridView}.
 */
public class CalendarRowView extends ViewGroup {
	private static final String TAG = "CalendarRowView";
	private boolean isHeaderRow;
	private MonthView.Listener listener;
	private int cellSize;
	private boolean isSelected = false;

	public CalendarRowView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		// child.setOnClickListener(this);
		super.addView(child, index, params);
	}

	public void setIsSelected(boolean selected) {
		isSelected = selected;
	}

	public boolean getIsSelected() {
		return isSelected;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		long start = System.currentTimeMillis();
		final int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
		LoggerTool.d("CalendarRowView", String.format(
				"Row.onMeasure w=%s h=%s", totalWidth,
				MeasureSpec.toString(heightMeasureSpec)));
		cellSize = totalWidth / 7;

		int cellWidthSpec = makeMeasureSpec(cellSize, EXACTLY);
		int cellHeightSpec = makeMeasureSpec(cellSize, EXACTLY);
		cellHeightSpec = isHeaderRow ? makeMeasureSpec(cellSize, AT_MOST)
				: cellHeightSpec;
		int rowHeight = 0;
		for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
			final View child = getChildAt(c);
			child.measure(cellWidthSpec, cellHeightSpec);
			// 该行高度是最高的单元格的高度.
			if (child.getMeasuredHeight() > rowHeight) {
				rowHeight = child.getMeasuredHeight();
			}
		}
		final int widthWithPadding = totalWidth + getPaddingLeft()
				+ getPaddingRight();
		final int heightWithPadding = rowHeight + getPaddingTop()
				+ getPaddingBottom();
		setMeasuredDimension(widthWithPadding, heightWithPadding);
		LoggerTool.d(
				TAG,
				String.format("Row.onMeasure %d ms", System.currentTimeMillis()
						- start));

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		long start = System.currentTimeMillis();
		int cellHeight = bottom - top;
		for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
			final View child = getChildAt(c);
			child.layout(c * cellSize, 0, (c + 1) * cellSize, cellHeight);
		}
		LoggerTool.d(
				TAG,
				String.format("Row.onLayout %d ms", System.currentTimeMillis()
						- start));
	}

	public void setIsHeaderRow(boolean isHeaderRow) {
		this.isHeaderRow = isHeaderRow;
	}

	// @Override
	// public void onClick(View v) {
	// // Header rows don't have a click listener
	// if (listener != null) {
	// listener.handleClick((MonthCellDescriptor) v.getTag());
	// }
	// }

	public void setListener(MonthView.Listener listener) {
		this.listener = listener;
	}

	public void setCellBackground(int resId) {
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).setBackgroundResource(resId);
		}
	}

	public void setCellTextColor(ColorStateList colors) {
		for (int index = 0; index < getChildCount(); index++) {
			LinearLayout child = (LinearLayout) getChildAt(index);
			CalendarDateView tv_date = (CalendarDateView) child
					.findViewById(R.id.tv_date);
			tv_date.setTextColor(colors);
		}
	}

	public Animator getExpandAnimator() {

		ValueAnimator heightAnimator = ValueAnimator.ofInt(1, getHeight());
		heightAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				ViewGroup.LayoutParams layoutParams = getLayoutParams();
				layoutParams.height = (Integer) animation.getAnimatedValue();
				setLayoutParams(layoutParams);
			}
		});
		return heightAnimator;
	}

	public Animator getCollapseAnimator() {

		final ViewGroup.LayoutParams lp = getLayoutParams();
		final int originalHeight = getHeight();

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0);
		animator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd(final Animator animator) {
				lp.height = 0;
				setLayoutParams(lp);
			}
		});

		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(final ValueAnimator valueAnimator) {
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				setLayoutParams(lp);
			}
		});

		return animator;
	}

}
