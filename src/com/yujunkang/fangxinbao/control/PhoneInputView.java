package com.yujunkang.fangxinbao.control;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * 
 * @date 2013-6-14
 * @author xieb
 * 
 */
public class PhoneInputView extends InputView {
	 private static final String TAG = "PhoneInputView";
	 
	 
	 
	public PhoneInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setSingleLine(true);
		String chsStr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		int charCount = chsStr.length();
		final char[] chs = new char[charCount];
		for (int i = 0; i < charCount; i++) {
			chs[i] = chsStr.charAt(i);
		}
		setKeyListener(new NumberKeyListener() {
			@Override
			protected char[] getAcceptedChars() {
				return chs;
			}

			@Override
			public int getInputType() {
				return InputType.TYPE_CLASS_NUMBER;
			}
		});

	}

	public String getPhoneNumber() {
		return getText().toString().trim().replace(" ", "");
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
