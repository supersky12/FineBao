package com.yujunkang.fangxinbao.control.calendar;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.utility.LoggerTool;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Toast;

public class CalendarCardPager extends ViewPager {
	private static final String TAG = "CalendarCardPager";

	public enum SelectionMode {
		SINGLE,

	}

	SelectionMode selectionMode;
	private DateFormat monthNameFormat;
	private DateFormat weekdayNameFormat;
	private DateFormat monthKeyFormat;
	private DateFormat fullDateFormat;
	private Calendar minCal;
	private Calendar maxCal;

	private boolean displayOnly;
	Calendar today;
	private int dividerColor;
	private int dayBackgroundResId;
	private int dayTextColorResId;
	private int titleTextColor;
	private int headerTextColor;
	private Locale locale;
	private OnDateSelectedListener dateSelectedListener;
	private DateSelectableFilter dateConfiguredListener;
	private OnInvalidDateSelectedListener invalidDateListener = new DefaultOnInvalidDateSelectedListener();
	final List<MonthCellDescriptor> selectedCells = new ArrayList<MonthCellDescriptor>();
	final List<MonthCellDescriptor> highlightedCells = new ArrayList<MonthCellDescriptor>();
	private HashMap<String, Integer> selectCellsMap = new HashMap<String, Integer>();

	final MonthView.Listener listener = new CellClickedListener();
	final List<Calendar> selectedCals = new ArrayList<Calendar>();
	final List<Calendar> highlightedCals = new ArrayList<Calendar>();

	private CardPagerAdapter mCardPagerAdapter;

	public CalendarCardPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init(context);
	}

	public CalendarCardPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		Resources res = context.getResources();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CalendarCardPager);
		final int bg = a.getColor(
				R.styleable.CalendarCardPager_android_background,
				res.getColor(R.color.calendar_bg));
		dividerColor = a.getColor(R.styleable.CalendarCardPager_dividerColor,
				res.getColor(R.color.calendar_divider));
		dayBackgroundResId = a.getResourceId(
				R.styleable.CalendarCardPager_dayBackground,
				R.drawable.calendar_bg_selector);
		dayTextColorResId = a.getResourceId(
				R.styleable.CalendarCardPager_dayTextColor,
				R.color.calendar_text_selector);
		titleTextColor = a.getColor(
				R.styleable.CalendarCardPager_titleTextColor,
				R.color.calendar_text_title);
		headerTextColor = a.getColor(
				R.styleable.CalendarCardPager_headerTextColor, R.color.white);
		a.recycle();

		setBackgroundColor(bg);

		locale = Locale.getDefault();
		today = Calendar.getInstance(locale);
		minCal = Calendar.getInstance(locale);
		maxCal = Calendar.getInstance(locale);

		monthNameFormat = new SimpleDateFormat(
				context.getString(R.string.month_name_format), locale);
		weekdayNameFormat = new SimpleDateFormat(
				context.getString(R.string.day_name_format), locale);
		monthKeyFormat = new SimpleDateFormat(
				context.getString(R.string.month_key_format), locale);
		fullDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
		if (isInEditMode()) {

			init(new Date(), locale);
		}
	}

	public CalendarCardPager(Context context) {
		super(context);

	}

	public void init(Date minDate) {
		init(minDate, Locale.getDefault());

	}

	public OnDateSelectedListener getDateSelectedListener() {
		return dateSelectedListener;
	}

	public void setDateSelectedListener(OnDateSelectedListener dateListener) {
		this.dateSelectedListener = dateListener;
	}

	public void init(Date minDate, Locale locale) {

		if (locale == null) {
			throw new IllegalArgumentException("Locale is null.");
		}

		// 确保所有的日历实例使用相同的语言
		this.locale = locale;
		today = Calendar.getInstance(locale);

		monthNameFormat = new SimpleDateFormat(getContext().getString(
				R.string.month_name_format), locale);

		weekdayNameFormat = new SimpleDateFormat(getContext().getString(
				R.string.day_name_format), locale);
		fullDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

		this.selectionMode = SelectionMode.SINGLE;
		//清空所选择的日期
		selectedCals.clear();
		selectedCells.clear();
		highlightedCals.clear();
		highlightedCells.clear();
		minCal.setTime(minDate);

		setMidnight(minCal);
		setMidnight(maxCal);
		displayOnly = false;
		validateAndUpdate();

	}

	private void init(Context context) {
		// mCardPagerAdapter = new CardPagerAdapter(context);
		// setAdapter(mCardPagerAdapter);
	}

	public CardPagerAdapter getCardPagerAdapter() {
		return mCardPagerAdapter;
	}

	private class CellClickedListener implements MonthView.Listener {
		@Override
		public void handleClick(MonthCellDescriptor cell) {
			Date clickedDate = cell.getDate();

			if (!isDateSelectable(clickedDate)) {
				if (invalidDateListener != null) {
					invalidDateListener.onInvalidDateSelected(clickedDate);
				}
			} else {
				boolean wasSelected = doSelectDate(clickedDate, cell);

				if (dateSelectedListener != null) {
					if (wasSelected) {
						dateSelectedListener.onDateSelected(clickedDate);
					} else {
						dateSelectedListener.onDateUnselected(clickedDate);
					}
				}
			}
		}
	}

	List<List<MonthCellDescriptor>> getMonthCells(MonthDescriptor month,
			Calendar startCal) {
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(startCal.getTime());
		List<List<MonthCellDescriptor>> cells = new ArrayList<List<MonthCellDescriptor>>();
		cal.set(DAY_OF_MONTH, 1);
		int firstDayOfWeek = cal.get(DAY_OF_WEEK);
		int offset = cal.getFirstDayOfWeek() - firstDayOfWeek;
		if (offset > 0) {
			offset -= 7;
		}
		cal.add(Calendar.DATE, offset);
		long start = System.currentTimeMillis();
		for (int i = 1; i <= 42; i++) {
			List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
			cells.add(weekCells);
			for (int c = 0; c < 7; c++) {
				Date date = cal.getTime();
				boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();

				boolean isSelectable = isCurrentMonth && isDateSelectable(date);
				boolean isToday = sameDate(cal, today);
				// boolean isHighlighted = containsDate(highlightedCals, cal);
				boolean isHighlighted = false;
				int value = cal.get(DAY_OF_MONTH);
				boolean isSelected = false;
				Integer selectedValue = selectCellsMap.get(monthKeyFormat
						.format(date));

				if (selectedValue != null) {
					LoggerTool.d(
							TAG,
							"monthkey: " + monthKeyFormat.format(date)
									+ "value: "
									+ String.valueOf(selectedValue.intValue()));
					if (value == selectedValue.intValue()) {
						isSelected = isCurrentMonth;
					}
				}
				MonthCellDescriptor.RangeState rangeState = MonthCellDescriptor.RangeState.NONE;
				weekCells.add(new MonthCellDescriptor(date, isCurrentMonth,
						isSelectable, isSelected, isToday, isHighlighted,
						value, rangeState));
				cal.add(DATE, 1);
			}
		}
		LoggerTool.d(
				TAG,
				String.format("Building week %s ms", System.currentTimeMillis()
						- start));
		return cells;
	}

	private static boolean containsDate(List<Calendar> selectedCals,
			Calendar cal) {
		for (Calendar selectedCal : selectedCals) {
			if (sameDate(cal, selectedCal)) {
				return true;
			}
		}
		return false;
	}

	public int getRemainHeight()
	{
		return getCurrentContentView().getRemainHeight();
	}
	
	
	private static Calendar minDate(List<Calendar> selectedCals) {
		if (selectedCals == null || selectedCals.size() == 0) {
			return null;
		}
		Collections.sort(selectedCals);
		return selectedCals.get(0);
	}

	private static Calendar maxDate(List<Calendar> selectedCals) {
		if (selectedCals == null || selectedCals.size() == 0) {
			return null;
		}
		Collections.sort(selectedCals);
		return selectedCals.get(selectedCals.size() - 1);
	}

	private static boolean sameDate(Calendar cal, Calendar selectedDate) {
		return cal.get(MONTH) == selectedDate.get(MONTH)
				&& cal.get(YEAR) == selectedDate.get(YEAR)
				&& cal.get(DAY_OF_MONTH) == selectedDate.get(DAY_OF_MONTH);
	}

	/** Clears out the hours/minutes/seconds/millis of a Calendar. */
	static void setMidnight(Calendar cal) {
		cal.set(HOUR_OF_DAY, 0);
		cal.set(MINUTE, 0);
		cal.set(SECOND, 0);
		cal.set(MILLISECOND, 0);
	}

	private boolean doSelectDate(Date date, MonthCellDescriptor cell) {
		Calendar newlySelectedCal = Calendar.getInstance(locale);
		newlySelectedCal.setTime(date);

		setMidnight(newlySelectedCal);
		switch (selectionMode) {
		case SINGLE:
			clearOldSelections();
			break;
		default:
			throw new IllegalStateException("Unknown selectionMode "
					+ selectionMode);
		}

		if (date != null) {
			// Select a new cell.

			if (selectedCells.size() == 0 || !selectedCells.get(0).equals(cell)) {
				selectedCells.add(cell);
				cell.setSelected(true);
				putSelectedDateToMap(cell);
			}
		}
		validateAndUpdate();
		return date != null;
	}

	private void putSelectedDateToMap(MonthCellDescriptor cell) {

		selectCellsMap.put(monthKeyFormat.format(cell.getDate()),
				cell.getValue());
	}

	private void validateAndUpdate() {
		if (getAdapter() == null) {
			mCardPagerAdapter = new CardPagerAdapter(getContext());
			setAdapter(mCardPagerAdapter);
		}
		mCardPagerAdapter.notifyDataSetChanged();
	}

	/**
	 * 设置用于选择和不可选择的监听器
	 * <p>
	 * 重要提示: 此方法必须在 {@link #init(Date, Date)} 之前调用.
	 */
	public void setDateSelectableFilter(DateSelectableFilter listener) {
		dateConfiguredListener = listener;
	}

	/**
	 * 选择日期的回调接口.
	 * 
	 * 
	 * @see #setOnDateSelectedListener(OnDateSelectedListener)
	 */
	public interface OnDateSelectedListener {
		void onDateSelected(Date date);

		void onDateUnselected(Date date);
	}

	
	/**
	 * 
	 * @see #setOnInvalidDateSelectedListener(OnInvalidDateSelectedListener)
	 */
	public interface OnInvalidDateSelectedListener {
		void onInvalidDateSelected(Date date);
	}

	/**
	 * 
	 * @see #setDateSelectableFilter(DateSelectableFilter)
	 */
	public interface DateSelectableFilter {
		boolean isDateSelectable(Date date);
	}

	private class DefaultOnInvalidDateSelectedListener implements
			OnInvalidDateSelectedListener {
		@Override
		public void onInvalidDateSelected(Date date) {
			String errMessage = getResources().getString(R.string.invalid_date,
					fullDateFormat.format(minCal.getTime()),
					fullDateFormat.format(maxCal.getTime()));
			Toast.makeText(getContext(), errMessage, Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isDateSelectable(Date date) {
		return dateConfiguredListener == null
				|| dateConfiguredListener.isDateSelectable(date);
	}

	private void clearOldSelections() {
		for (MonthCellDescriptor selectedCell : selectedCells) {
			// De-select the currently-selected cell.
			selectedCell.setSelected(false);
		}
		selectedCells.clear();
		selectedCals.clear();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int childCount = getChildCount();
		if (childCount > 0) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			MonthView child = (MonthView) getChildAt(0);
			android.view.ViewGroup.LayoutParams params = getLayoutParams();
			if (params.height != LayoutParams.WRAP_CONTENT) {
				setMeasuredDimension(width, params.height);
				LoggerTool.e(TAG, " " + params.height);
			} else {
				setMeasuredDimension(width, child.getExactlyHeight());
				params.height = child.getExactlyHeight();
				LoggerTool.e(TAG, " " + child.getExactlyHeight());
			}

		}

	}

	public MonthView getCurrentContentView() {
		View child = getChildAt(getCurrentItem());
		return (MonthView) child;
	}

	class CardPagerAdapter extends PagerAdapter {

		private final LayoutInflater inflater;
		private MonthView currentView;

		public CardPagerAdapter(Context ctx) {

			inflater = LayoutInflater.from(ctx);
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(View collection, final int position) {

			MonthView monthView = null;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, position);

			monthView = MonthView.create(null, inflater, weekdayNameFormat,
					listener, today, dividerColor, dayBackgroundResId,
					dayTextColorResId, titleTextColor, headerTextColor);

			MonthDescriptor month = new MonthDescriptor(cal.get(MONTH),
					cal.get(YEAR), cal.getTime(), monthNameFormat.format(cal
							.getTime()));
			monthView.init(month, getMonthCells(month, cal), displayOnly);
			((ViewPager) collection).addView(monthView, 0);
			LoggerTool.d(TAG, monthKeyFormat.format(cal.getTime()));
			return monthView;

		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public int getCount() {

			return Integer.MAX_VALUE;
		}

	}

}
