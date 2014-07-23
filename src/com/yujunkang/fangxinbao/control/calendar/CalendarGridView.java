package com.yujunkang.fangxinbao.control.calendar;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.utility.LoggerTool;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * ViewGroup 绘制日历单元格. 所有子控件必须是 {@link CalendarRowView}s. 在第一行被认为是一个标头
 * 
 */
public class CalendarGridView extends ViewGroup {
	private static final String TAG = "CalendarGridView";
	private static final float FLOAT_FUDGE = 0.5f;

	private final Paint dividerPaint = new Paint();
	private int oldWidthMeasureSize;
	private int oldNumRows;
	private int mTotalHieght;
	private int mRemainHeight;

	public CalendarGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		dividerPaint
				.setColor(getResources().getColor(R.color.calendar_divider));
	}

	public void setDividerColor(int color) {
		dividerPaint.setColor(color);
	}

	public void setDayBackground(int resId) {
		for (int i = 1; i < getChildCount(); i++) {
			((CalendarRowView) getChildAt(i)).setCellBackground(resId);
		}
	}

	public void setDayTextColor(int resId) {
		for (int i = 1; i < getChildCount(); i++) {
			ColorStateList colors = getResources().getColorStateList(resId);
			((CalendarRowView) getChildAt(i)).setCellTextColor(colors);
		}
	}

	public void setHeaderTextColor(int color) {
		// CalendarRowView headRow = (CalendarRowView)getChildAt(0);
		// for (int i = 0; i < getChildCount(); i++) {
		// ((TextView) headRow.getChildAt(i)).setTextColor(color);
		// }
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if (getChildCount() == 0) {
			((CalendarRowView) child).setIsHeaderRow(true);
		}
		super.addView(child, index, params);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		final boolean retVal = super.drawChild(canvas, child, drawingTime);

		return retVal;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		long start = System.currentTimeMillis();
		LoggerTool.d(
				TAG,
				String.format("Grid.onMeasure w=%s h=%s",
						MeasureSpec.toString(widthMeasureSpec),
						MeasureSpec.toString(heightMeasureSpec)));
		int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
		if (oldWidthMeasureSize == widthMeasureSize) {
			LoggerTool.d(TAG, "SKIP Grid.onMeasure");
			setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
			return;
		}
		oldWidthMeasureSize = widthMeasureSize;
		int cellSize = widthMeasureSize / 7;

		widthMeasureSize = cellSize * 7 + 5;
		int totalHeight = 0;
		int headHeigth = 0;
		final int rowWidthSpec = makeMeasureSpec(widthMeasureSize, EXACTLY);
		final int rowHeightSpec = makeMeasureSpec(cellSize, EXACTLY);
		mRemainHeight = 0;
		for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
			final View child = getChildAt(c);
			if (child.getVisibility() == View.VISIBLE) {
				if (c == 0) {
					measureChild(child, rowWidthSpec,
							makeMeasureSpec(cellSize, AT_MOST));
				} else {
					measureChild(child, rowWidthSpec, rowHeightSpec);
				}
				totalHeight += child.getMeasuredHeight();
				// 只保留一行的高度
				if (c == 1) {
					mRemainHeight = totalHeight;
				}
			}
		}
		final int measuredWidth = widthMeasureSize;
		mTotalHieght = totalHeight;
		setMeasuredDimension(measuredWidth, totalHeight);
		LoggerTool.d(TAG, String.format("Grid.onMeasure w=%s h=%s",
				measuredWidth, totalHeight));
		LoggerTool.d(
				TAG,
				String.format("CalendarGridView.onMeasure %d ms",
						System.currentTimeMillis() - start));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		top = 0;
		for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
			final View child = getChildAt(c);
			final int rowHeight = child.getMeasuredHeight();
			child.layout(left, top, right, top + rowHeight);
			top += rowHeight;
		}
	}

	
	
	public int getRemainHeight() {
		return mRemainHeight;
	}

	public void setRemainHeight(int RemainHeight) {
		this.mRemainHeight = RemainHeight;
	}

	public int getTotalHieght() {
		return mTotalHieght;
	}

	public void setTotalHieght(int TotalHieght) {
		this.mTotalHieght = TotalHieght;
	}

	public void setNumRows(int numRows) {
		if (oldNumRows != numRows) {
			// If the number of rows changes, make sure we do a re-measure next
			// time around.
			oldWidthMeasureSize = 0;
		}
		oldNumRows = numRows;
	}
}
