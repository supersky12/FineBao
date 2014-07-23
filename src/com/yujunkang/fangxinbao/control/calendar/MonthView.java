package com.yujunkang.fangxinbao.control.calendar;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.AnimatorSet;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.anim.ExpandCollapseAnimation;
import com.yujunkang.fangxinbao.utility.LoggerTool;

public class MonthView extends LinearLayout {
	private static final String TAG = "MonthView";
	TextView title;
	CalendarGridView grid;
	private Listener listener;
	private ArrayList<CalendarCellView> CalendarCellViews = new ArrayList<CalendarCellView>();

	public static MonthView create(ViewGroup parent, LayoutInflater inflater,
			DateFormat weekdayNameFormat, Listener listener, Calendar today,
			int dividerColor, int dayBackgroundResId, int dayTextColorResId,
			int titleTextColor, int headerTextColor) {
		
		final MonthView view = (MonthView) inflater.inflate(R.layout.month,
				parent, false);
		view.setDividerColor(dividerColor);
		view.setDayTextColor(dayTextColorResId);
		view.setTitleTextColor(titleTextColor);
		view.setHeaderTextColor(headerTextColor);

		if (dayBackgroundResId != 0) {
			view.setDayBackground(dayBackgroundResId);
		}

		final int originalDayOfWeek = today.get(Calendar.DAY_OF_WEEK);

		int firstDayOfWeek = today.getFirstDayOfWeek();
		final CalendarRowView headerRow = (CalendarRowView) view.grid
				.getChildAt(0);
		
		for (int offset = 0; offset < 7; offset++) {
			
			today.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + offset);
			final TextView textView = (TextView) headerRow.getChildAt(offset);
			textView.setText(weekdayNameFormat.format(today.getTime()));
			
		}
		
		today.set(Calendar.DAY_OF_WEEK, originalDayOfWeek);
		view.listener = listener;
		
		return view;
	}

	public MonthView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public int getExactlyHeight()
	{
		return grid.getTotalHieght();
	}
	
	public int getRemainHeight()
	{
		return grid.getRemainHeight();
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		title = (TextView) findViewById(R.id.title);
		grid = (CalendarGridView) findViewById(R.id.calendar_grid);
	}

	public void executeExpandAnimation() {
		int childCount = grid.getChildCount();
		AnimatorSet animatorSet = new AnimatorSet();
		Animator[] animatorsArray = new Animator[childCount - 1];
		for (int index = 1; index < childCount; index++) {
			CalendarRowView childview = (CalendarRowView) grid.getChildAt(index);
			animatorsArray[index-1] = childview.getExpandAnimator();
		}
		animatorSet.playTogether(animatorsArray);
		animatorSet.start();
	}

	public void executeCollapseAnimation() {
		int childCount = grid.getChildCount();
//		AnimatorSet animatorSet = new AnimatorSet();
//		Animator[] animatorsArray = new Animator[childCount - 1];
//		for (int index = 1; index < childCount; index++) {
//			CalendarRowView childview = (CalendarRowView) grid.getChildAt(index);
//			animatorsArray[index-1] = childview.getCollapseAnimator();
//		}
//		animatorSet.playTogether(animatorsArray);
//		animatorSet.start();
		
		AnimatorSet animatorSet = new AnimatorSet();
		Animator[] animatorsArray = new Animator[1];
		
		CalendarRowView childview = (CalendarRowView) grid.getChildAt(1);
		animatorsArray[0] = childview.getCollapseAnimator();
		
		animatorSet.playTogether(animatorsArray);
		animatorSet.start();
		
//		CalendarRowView childview = (CalendarRowView) grid.getChildAt(1);
//		
//		ExpandCollapseAnimation.setHeightForWrapContent((CalendarRowView) grid.getChildAt(1),
//				childview.getHeight());
//		ExpandCollapseAnimation expandAni = new ExpandCollapseAnimation(
//				childview, 400);
//		
//		childview.startAnimation(expandAni);
	}
	
	


	public void init(MonthDescriptor month,
			List<List<MonthCellDescriptor>> cells, boolean displayOnly) {
		LoggerTool.d(TAG,String.format("Initializing MonthView (%d) for %s",
				System.identityHashCode(this), month));
		long start = System.currentTimeMillis();
		title.setText(month.getLabel());

		final int numRows = cells.size();
		grid.setNumRows(numRows);
		for (int i = 0; i < 6; i++) {
			CalendarRowView weekRow = (CalendarRowView) grid.getChildAt(i + 1);
			// weekRow.setListener(listener);
			if (i < numRows) {
				weekRow.setVisibility(VISIBLE);
				List<MonthCellDescriptor> week = cells.get(i);
				for (int c = 0; c < week.size(); c++) {
					MonthCellDescriptor cell = week.get(c);
					CalendarCellView cellView = (CalendarCellView) weekRow
							.getChildAt(c);
					cellView.setTemperature(cell.getValue());
					cellView.setEnabled(cell.isCurrentMonth());
					cellView.setClickable(!displayOnly);
					cellView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							for (CalendarCellView c : CalendarCellViews) {
								c.setSelected(false);
							}
							((CalendarCellView) v).setSelected(true);
							if (listener != null) {
								listener.handleClick((MonthCellDescriptor) v
										.getTag());
							}
						}
					});
					cellView.setSelectable(cell.isSelectable());
					cellView.setSelected(cell.isSelected());
					cellView.setCurrentMonth(cell.isCurrentMonth());
					cellView.setToday(cell.isToday());
					cellView.setHighlighted(cell.isHighlighted());
					cellView.setTag(cell);
					CalendarCellViews.add(cellView);
				}
			} else {
				weekRow.setVisibility(GONE);
			}
		}
		LoggerTool.d(TAG,String.format("MonthView.init took %d ms", System.currentTimeMillis()
				- start));
	}

	public void setDividerColor(int color) {
		grid.setDividerColor(color);
	}

	public void setDayBackground(int resId) {
		grid.setDayBackground(resId);
	}

	public void setDayTextColor(int resId) {
		grid.setDayTextColor(resId);
	}

	public void setTitleTextColor(int color) {
		title.setTextColor(color);
	}

	public void setHeaderTextColor(int color) {
		grid.setHeaderTextColor(color);
	}

	public interface Listener {
		void handleClick(MonthCellDescriptor cell);
	}
}
