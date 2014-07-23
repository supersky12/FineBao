package com.yujunkang.fangxinbao.widget.adapter;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.model.Sex;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @date 2014-6-1
 * @author xieb
 * 
 */
public class SexListAdapter extends BaseGroupAdapter<Sex> {
	private String mSelectedId;
	private Context mContext;

	public SexListAdapter(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.dialog_list_item, null);
			holder = new ViewHolder();
			holder.txtView = (TextView) convertView.findViewById(R.id.txtView);
			holder.markImageView = (ImageView) convertView
					.findViewById(R.id.iv_check);
			holder.iv_icon = (ImageView) convertView
					.findViewById(R.id.iconImageView);
			holder.v_line= convertView
					.findViewById(R.id.v_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Sex item = (Sex) getItem(position);

		boolean isSelected = false;
		if (!TextUtils.isEmpty(mSelectedId)) {
			if (mSelectedId.equals(item.getId())) {
				isSelected = true;
			}
		}
		holder.txtView.setText(item.getSexName());

		holder.iv_icon.setVisibility(View.GONE);
		if (isSelected) {
			holder.markImageView.setVisibility(View.VISIBLE);

		} else {
			holder.markImageView.setVisibility(View.GONE);

		}
		holder.v_line.setVisibility(View.GONE);
		return convertView;
	}

	public void setSelectedId(String id) {
		mSelectedId = id;
	}

	class ViewHolder {
		TextView txtView;
		ImageView markImageView;
		ImageView iv_icon;
		View v_line;
	}

}
