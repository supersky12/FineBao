package com.yujunkang.fangxinbao.control;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.utility.FocusChangedUtils;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class FlatButton extends LinearLayoutControlWrapView {
	private ImageView btnImgLeft;
	private TextView btnTxt;
	private ImageView btnImgRight;

	private Drawable lefticon, righticon;
	private String middletxt;
	private int txtcolor = -1;
	private float txtsize = -1;

	public FlatButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FlatButton);
		try {
			lefticon = a.getDrawable(R.styleable.FlatButton_lefticon);
			middletxt = a.getString(R.styleable.FlatButton_middletxt);
			righticon = a.getDrawable(R.styleable.FlatButton_righticon);
			if (a.hasValue(R.styleable.FlatButton_txtcolor)) {
				txtcolor = a.getColor(R.styleable.FlatButton_txtcolor,
						color.white);
			}
			if (a.hasValue(R.styleable.FlatButton_txtsize)) {
				txtsize = a.getFloat(R.styleable.FlatButton_txtsize, 15f);
			}
		} catch (Exception ex) {
			//
		} finally {
			a.recycle();
		}
	}

	
	public void ensureUI() {
		btnImgLeft = (ImageView) this.findViewById(R.id.btnImgLeft);
		btnTxt = (TextView) this.findViewById(R.id.btnTxt);
		btnImgRight = (ImageView) this.findViewById(R.id.btnImgRight);
		FocusChangedUtils.setViewFocusChanged(this);

	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.btn_flat, this);
		initControls();
		initData();
	}

	public void setLeftIcon(int imgResourceID) {
		if (imgResourceID == -1) {
			btnImgLeft.setVisibility(View.GONE);
		} else {
			btnImgLeft.setVisibility(View.VISIBLE);
			btnImgLeft.setImageResource(imgResourceID);
		}
	}

	public void setRightIcon(int imgResourceID) {
		if (imgResourceID == -1) {
			btnImgRight.setVisibility(View.GONE);
		} else {
			btnImgRight.setVisibility(View.VISIBLE);
			btnImgRight.setImageResource(imgResourceID);
		}
	}

	public void setMiddleTxt(String txt) {
		if (!TextUtils.isEmpty(txt)) {
			btnTxt.setVisibility(View.VISIBLE);
			btnTxt.setText(txt);
		} else {
			btnTxt.setVisibility(View.GONE);
		}
	}

	private void initControls() {
		btnImgLeft = (ImageView) this.findViewById(R.id.btnImgLeft);
		btnTxt = (TextView) this.findViewById(R.id.btnTxt);
		btnImgRight = (ImageView) this.findViewById(R.id.btnImgRight);
		FocusChangedUtils.setViewFocusChanged(this);
	}

	private void initData() {
		if (lefticon != null) {
			btnImgLeft.setImageDrawable(lefticon);
			btnImgLeft.setVisibility(View.VISIBLE);
		} else {
			btnImgLeft.setVisibility(View.GONE);
		}
		if (txtcolor != -1) {
			btnTxt.setTextColor(txtcolor);
		} else {
			btnTxt.setTextColor(getResources().getColor(color.white));
		}
		if (txtsize != -1) {
			btnTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, txtsize);
		} else {
			//
		}
		if (!TextUtils.isEmpty(middletxt)) {
			btnTxt.setVisibility(View.VISIBLE);
			btnTxt.setText(middletxt);
		} else {
			btnTxt.setVisibility(View.GONE);
		}
		if (righticon != null) {
			btnImgRight.setImageDrawable(righticon);
			btnImgRight.setVisibility(View.VISIBLE);
		} else {
			btnImgRight.setVisibility(View.GONE);
		}
	}
}
