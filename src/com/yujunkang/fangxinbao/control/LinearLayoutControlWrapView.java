package com.yujunkang.fangxinbao.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;



/**
 * 
 * @date 2013-11-18
 * @author xieb
 * 
 */
public  abstract class LinearLayoutControlWrapView extends LinearLayout {
	
	
	protected LayoutInflater mInflater;
	private Context mContext;
	public LinearLayoutControlWrapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public LinearLayoutControlWrapView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	protected void init()
	{
		mInflater = LayoutInflater.from(mContext);
	}
	
	
	
}


