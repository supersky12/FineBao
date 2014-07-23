package com.yujunkang.fangxinbao.widget.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.model.Group;

/**
 * 
 * @date 2013-6-28
 * @author xieb
 * 
 */
public abstract class BaseExpandableGroupAdapter<T extends BaseModel> extends
		BaseExpandableListAdapter {

	Group<Group<T>> group = null;

	public BaseExpandableGroupAdapter(Context context) {
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return (group == null) ? 0 : group.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return (group == null) ? null : group.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		if (group == null) {
			return null;
		}
		if (group.get(groupPosition) == null) {
			return null;
		}
		return group.get(groupPosition).get(childPosition);
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (group == null) {
			return 0;
		}
		return (group.get(groupPosition) == null) ? 0 : group
				.get(groupPosition).size();
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	public void setGroup(Group<Group<T>> g) {
		group = g;
		this.notifyDataSetChanged();
	}
}
