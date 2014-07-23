package com.yujunkang.fangxinbao.control;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.Utils;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @date 2014-5-21
 * @author xieb
 * 
 */
public class NetworkProgressButton extends LinearLayoutControlWrapView {

	private TextView tv_label;
	private ProgressBar progress;
	private String _loadingPrompt;
	private String _buttonText;

	public NetworkProgressButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.HORIZONTAL);
		mInflater.inflate(
				R.layout.button_network_proressbar, this);
		setGravity(Gravity.CENTER);
		setPadding(0, Utils.dip2px(getContext(), 8), 0, Utils.dip2px(getContext(), 8));
		ensureUI();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.NetworkProgressButton);
		try {
			String buttontext = a
					.getString(R.styleable.NetworkProgressButton_buttonText);
			if (!TextUtils.isEmpty(buttontext)) {
				_buttonText = buttontext;
				tv_label.setText(buttontext);
			}
			if (a.hasValue(R.styleable.NetworkProgressButton_textSize)) {
				float txtsize = a.getFloat(
						R.styleable.NetworkProgressButton_textSize, 15f);
				tv_label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, txtsize);
			}
			if (a.hasValue(R.styleable.NetworkProgressButton_loadingPrompt)) {
				_loadingPrompt = a
						.getString(R.styleable.NetworkProgressButton_loadingPrompt);
			}

			setEnabled(false);
		} catch (Exception ex) {
			//
		} finally {
			a.recycle();
		}
	}

	
	public void ensureUI() {
		progress = (ProgressBar) findViewById(R.id.progress);
		tv_label = (TextView) findViewById(R.id.tv_label);
	}

	public void preNetworkExecute() {
		progress.setVisibility(View.VISIBLE);
		tv_label.setText(_loadingPrompt);
		setEnabled(false);
	}

	public void finishNetworkExecute() {
		progress.setVisibility(View.GONE);
		tv_label.setText(_buttonText);
		setEnabled(true);
	}

	public String getLoadingPrompt() {
		return _loadingPrompt;
	}

	public void setLoadingPrompt(String loadingPrompt) {
		this._loadingPrompt = loadingPrompt;
	}

	public String getButtonText() {
		return _buttonText;
	}

	public void setButtonText(String buttonText) {
		this._buttonText = buttonText;
	}

	
}
