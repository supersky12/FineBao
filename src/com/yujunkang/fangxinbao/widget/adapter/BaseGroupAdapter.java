package com.yujunkang.fangxinbao.widget.adapter;



import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.model.Group;

import android.content.Context;
import android.widget.BaseAdapter;

/**
 * 
 * @date 2012-9-7
 * @author xieb
 * 
 */
public abstract class BaseGroupAdapter<T extends BaseModel> extends
		BaseAdapter {

	Group<T> group = null;

	public BaseGroupAdapter(Context context) {
	}

	@Override
	public int getCount() {
		return (group == null) ? 0 : group.size();
	}

	@Override
	public Object getItem(int position) {
		return group.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return (group == null) ? true : group.isEmpty();
	}

	/**
	 * Removes the element at the specified position in the list
	 */
	public void remove(int position) {
		if (group != null) {
			group.remove(position);
			notifyDataSetChanged();
		}

	}

	public void setGroup(Group<T> g) {
		group = g;
		this.notifyDataSetChanged();
	}
}
