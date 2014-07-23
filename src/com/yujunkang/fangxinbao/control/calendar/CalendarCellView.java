package com.yujunkang.fangxinbao.control.calendar;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.app.FangXinBaoSettings;
import com.yujunkang.fangxinbao.utility.DataConstants;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarCellView extends LinearLayout {
	private final static String TAG = "CalendarCellView";
	private static final int[] STATE_SELECTABLE = { R.attr.state_selectable };
	private static final int[] STATE_CURRENT_MONTH = { R.attr.state_current_month };
	private static final int[] STATE_TODAY = { R.attr.state_today };
	private static final int[] STATE_HIGHLIGHTED = { R.attr.state_highlighted };
	private CalendarDateView tv_date;
	private TextView tv_temperature;
	private ImageView iv_birthday;

	private boolean isSelectable = false;
	private boolean isCurrentMonth = false;
	private boolean isToday = false;
	private boolean isHighlighted = false;

	@SuppressWarnings("UnusedDeclaration")
	public CalendarCellView(Context context, AttributeSet attrs) {
		super(context, attrs);
	
		LayoutInflater.from(context).inflate(R.layout.cell_item, this);
		setGravity(Gravity.CENTER_HORIZONTAL);
		setOrientation(LinearLayout.VERTICAL);
		initControl();
	}

	private void initControl() {
		tv_date = (CalendarDateView) findViewById(R.id.tv_date);
		tv_temperature = (TextView) findViewById(R.id.tv_temperature);
		iv_birthday = (ImageView)findViewById(R.id.iv_birthday);
	}

	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
		tv_date.setSelectable(isSelectable);
		refreshDrawableState();
	}

	
	
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		CalendarRowView parentView = (CalendarRowView)getParent();
		parentView.setIsSelected(selected);
		
	}

	public void setCurrentMonth(boolean isCurrentMonth) {
		this.isCurrentMonth = isCurrentMonth;
		tv_date.setCurrentMonth(isCurrentMonth);
		refreshDrawableState();
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
		tv_date.setToday(isToday);
		refreshDrawableState();
	}

	public void setTemperature(int value) {
		float temperature = 0;
		if (FangXinBaoSettings.CODE_DEBUG) {
			if (value % 3 == 0) {
				temperature = 37.0f;
			} else if (value % 3 == 1) {
				temperature = 38.0f;
			} else if (value % 3 == 2) {
				temperature = 41.0f;
			}
//			if(value==15)
//			{
//				iv_birthday.setVisibility(View.VISIBLE);
//				tv_date.setVisibility(View.GONE);
//			}
		}
	
		
		tv_date.setText(String.valueOf(value));
		

		// 发烧
		if (temperature > DataConstants.DEFAULT_NORMAL_TEMPERATURE
				&& temperature <= DataConstants.DEFAULT_MIDDLE_TEMPERATURE) {
			tv_temperature
					.setBackgroundResource(R.drawable.caleddar_temperature_middle_bg);
		} else if (temperature <= DataConstants.DEFAULT_NORMAL_TEMPERATURE) {
			tv_temperature
					.setBackgroundResource(R.drawable.caleddar_temperature_normal_bg);
		}
		// 极度发烧
		else if (temperature > DataConstants.DEFAULT_MIDDLE_TEMPERATURE) {
			tv_temperature
					.setBackgroundResource(R.drawable.caleddar_temperature_high_bg);
		}
		tv_temperature.setText(String.valueOf(temperature));
	}

	public void setHighlighted(boolean highlighted) {
		isHighlighted = highlighted;
		tv_date.setHighlighted(isSelectable);
		refreshDrawableState();
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 5);

		if (isSelectable) {
			mergeDrawableStates(drawableState, STATE_SELECTABLE);
		}

		if (isCurrentMonth) {
			mergeDrawableStates(drawableState, STATE_CURRENT_MONTH);
		}

		if (isToday) {
			tv_date.setText(getContext().getString(R.string.today));
			mergeDrawableStates(drawableState, STATE_TODAY);
		}

		if (isHighlighted) {
			mergeDrawableStates(drawableState, STATE_HIGHLIGHTED);
		}

		return drawableState;
	}

}
