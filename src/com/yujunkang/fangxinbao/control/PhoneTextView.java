package com.yujunkang.fangxinbao.control;

import android.content.Context;
import android.util.AttributeSet;



/**
 * 
 * @date 2014-6-17
 * @author xieb
 * 
 */
public class PhoneTextView extends SecurityTextView {

	public PhoneTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMaxLenth() {
		// TODO Auto-generated method stub
		return 11;
	}

	@Override
	public int getStartHideIndex() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public int getEndHideIndex() {
		// TODO Auto-generated method stub
		return 7;
	}

	@Override
	public boolean isSplit() {
		// TODO Auto-generated method stub
		return false;
	}

}
