package com.yujunkang.fangxinbao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.control.IndexScrollBar;
import com.yujunkang.fangxinbao.control.IndexScrollBar.ScrollListener;
import com.yujunkang.fangxinbao.control.PinnedHeaderExpandableListView;
import com.yujunkang.fangxinbao.control.PinnedHeaderExpandableListView.OnHeaderUpdateListener;
import com.yujunkang.fangxinbao.control.SearchBarView;
import com.yujunkang.fangxinbao.control.SearchBarView.OnEditTextOnFocusChangeListener;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.widget.adapter.CountryListAdapter;

/**
 * 
 * @date 2014-5-28
 * @author xieb
 * 
 */
public class SelectCountryActivity extends ActivityWrapper implements
		ExpandableListView.OnChildClickListener,
		ExpandableListView.OnGroupClickListener, OnHeaderUpdateListener {

	private static final String TAG = "SelectCountryActivity";
	public static final String INTENT_EXTRA_COUNTRY = DataConstants.PACKAGE_NAME
			+ ".SelectCountryActivity.INTENT_EXTRA_COUNTRY";
	/**
	 * 控件
	 */
	private IndexScrollBar iv_group_index;
	private TextView tv_title;
	private TextView tv_index_in_center;
	private PinnedHeaderExpandableListView expandableListView;
	private CountryListAdapter mCountryListAdapter;
	private ViewGroup mHeaderView;
	private SearchBarView lay_select_country_bar;
	/**
	 * 
	 */
	private DBHelper mDatabaseHelper = null;
	private Group<Group<Country>> citiesDataSource = new Group<Group<Country>>();

	// private Group<Group<Country>> searchResultCities = new
	// Group<Group<Country>>();
	private Handler mHandler = new Handler();
	private Runnable mRemoveWindow = new Runnable() {
		public void run() {
			tv_index_in_center.setVisibility(View.INVISIBLE);
		}
	};

	ScrollListener indexScroll = new ScrollListener() {
		@Override
		public void onScroll(String sectionStr) {
			if (mCountryListAdapter != null) {
				int groupCount = citiesDataSource.size();
				for (int position = 0; position < groupCount; position++)
					if (sectionStr.equals(citiesDataSource.get(position)
							.getType())) {
						expandableListView.setSelectedGroup(position);
						tv_index_in_center.setText(sectionStr);
						tv_index_in_center.setVisibility(View.VISIBLE);
						mHandler.removeCallbacks(mRemoveWindow);
						mHandler.postDelayed(mRemoveWindow, 2000);
						return;
					}
			}
		}

	};

	TextWatcher inputWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s != null && s.length() > 0) {
				mCountryListAdapter.setGroup(filterCountry(s.toString()));
			} else {
				mCountryListAdapter.setGroup(citiesDataSource);
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_country_activity);
		init();
		ensureUi();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void init() {
		mDatabaseHelper = DBHelper.getDBInstance(this);
		initCitiesDataSource();
	}

	private void ensureUi() {
		iv_group_index = (IndexScrollBar) findViewById(R.id.scroll_group_index);
		iv_group_index.setFocusable(true);
		iv_group_index.setOnScrollListener(indexScroll);
		expandableListView = (PinnedHeaderExpandableListView) findViewById(R.id.expandablelist);

		mCountryListAdapter = new CountryListAdapter(this);
		expandableListView.setAdapter(mCountryListAdapter);
		mCountryListAdapter.setGroup(citiesDataSource);
		// 展开所有group
		for (int i = 0, count = expandableListView.getCount(); i < count; i++) {
			expandableListView.expandGroup(i);
		}

		expandableListView.setOnHeaderUpdateListener(this);
		expandableListView.setOnChildClickListener(this);
		expandableListView.setOnGroupClickListener(this);
		lay_select_country_bar = (SearchBarView) findViewById(R.id.lay_select_country_bar);
		lay_select_country_bar.addTextWatcher(inputWatcher);
		lay_select_country_bar
				.setOnEditTextOnFocusChangeListener(new OnEditTextOnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							iv_group_index.setVisibility(View.GONE);
						} else {
							iv_group_index.setVisibility(View.VISIBLE);
						}

					}
				});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	/**
	 * 初始化城市数据
	 */
	@SuppressWarnings("unchecked")
	private void initCitiesDataSource() {
		String[] ss = new String[] { "A", "B", "C", "D", "E", "F", "G", "H",
				"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z" };
		int dataCount = ss.length;
		for (int index = 0; index < dataCount; index++) {
			Group<Country> data = mDatabaseHelper
					.queryCountryByFirstLetter(ss[index]);
			if (data != null && data.size() > 0) {
				citiesDataSource.add(data);
				data.setType(ss[index]);
			}
		}

	}

	private Group<Group<Country>> filterCountry(String inputCondition) {
		Group<Group<Country>> filterResult = new Group<Group<Country>>();
		for (Group<Country> item : citiesDataSource) {
			Group<Country> groupResult = null;
			for (Country country : item) {
				String simpleCode = country.getCountrySimpleCode();
				if (!TextUtils.isEmpty(simpleCode)) {
					if (simpleCode.toUpperCase().contains(
							inputCondition.toUpperCase())) {
						if (groupResult == null) {
							groupResult = new Group<Country>();
							groupResult.setType(item.getType());
							filterResult.add(groupResult);
						}
						groupResult.add(country);
					}
				}
			}
		}
		return filterResult;
	}

	@Override
	public void updatePinnedHeader(int firstVisibleGroupPos) {
		if (firstVisibleGroupPos >= 0) {
			Group firstVisibleGroup = (Group) mCountryListAdapter
					.getGroup(firstVisibleGroupPos);
			TextView textView = (TextView) getPinnedHeader().findViewById(
					R.id.tv_group_name);
			textView.setText(firstVisibleGroup.getType());
		}
	}

	@Override
	public View getPinnedHeader() {
		if (mHeaderView == null) {
			mHeaderView = (ViewGroup) getLayoutInflater().inflate(
					R.layout.select_country_group_item, null);
			mHeaderView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
		return mHeaderView;
	}

	@Override
	public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
			long arg3) {
		return true;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {

		Country data = (Country) parent.getExpandableListAdapter().getChild(
				groupPosition, childPosition);
		Intent intent = new Intent();
		intent.putExtra(INTENT_EXTRA_COUNTRY, data);
		setResult(Activity.RESULT_OK, intent);
		finish();
		return false;
	}

}
