package com.yujunkang.fangxinbao.widget.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.Group;

/**
 * 
 * @date 2014-5-28
 * @author xieb
 * 
 */
public class CountryListAdapter extends BaseExpandableGroupAdapter<Country> {
	private Context mContext;

	public CountryListAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.select_country_list_item, null);
			holder = new ViewHolder();
			holder.tv_countryName = (TextView) convertView
					.findViewById(R.id.tv_country_name);
			holder.tv_countryEngName = (TextView) convertView
					.findViewById(R.id.tv_country_engname);
			holder.tv_countryCode = (TextView) convertView
					.findViewById(R.id.tv_country_code);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Country item = (Country) getChild(groupPosition, childPosition);
		String name = item.getName();
		if (!TextUtils.isEmpty(name)) {
			holder.tv_countryName.setVisibility(View.VISIBLE);
			holder.tv_countryName.setText(name);
		} else {
			holder.tv_countryName.setVisibility(View.GONE);
		}
		String engname = item.getName();
		if (!TextUtils.isEmpty(engname)) {
			holder.tv_countryEngName.setVisibility(View.VISIBLE);
			holder.tv_countryEngName.setText(engname);
		} else {
			holder.tv_countryEngName.setVisibility(View.GONE);
		}

		String countryCode = item.getCountryCode();
		if (!TextUtils.isEmpty(countryCode)) {
			holder.tv_countryCode.setVisibility(View.VISIBLE);
			holder.tv_countryCode.setText("+" + countryCode);
		} else {
			holder.tv_countryCode.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder groupHolder = null;
		if (convertView == null) {
			groupHolder = new GroupHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.select_country_group_item, null);
			groupHolder.tv_group_name = (TextView) convertView
					.findViewById(R.id.tv_group_name);

			convertView.setTag(groupHolder);
		} else {
			groupHolder = (GroupHolder) convertView.getTag();
		}

		Group<Country> item = (Group<Country>) getGroup(groupPosition);
		String type = item.getType();
		if (!TextUtils.isEmpty(type)) {
			groupHolder.tv_group_name.setText(type);
			groupHolder.tv_group_name.setVisibility(View.VISIBLE);
		} else {
			groupHolder.tv_group_name.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	class ViewHolder {
		TextView tv_countryName;
		TextView tv_countryEngName;
		TextView tv_countryCode;
	}

	class GroupHolder {
		TextView tv_group_name;

	}
}
