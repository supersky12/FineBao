package com.yujunkang.fangxinbao.control;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;



/**
 * 
 * @date 2014-5-30
 * @author xieb
 * 
 */
public abstract class InputView extends EditText {
	private View deleView;
	private boolean filled = false;
	private boolean noChange = false;
	private Drawable textDrawa;
	
	public InputView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
		
	}

	public InputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (noChange) {
					return;
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (noChange) {
					return;
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
				int len = editable.length();
				if (deleView != null) {
					deleView.setVisibility(len == 0 ? View.GONE : View.VISIBLE);
				}
				if (noChange) {
					return;
				}
				noChange = true;

				String str = editable.toString();
				str = str.replace(" ", "");
				len = str.length();
				if (len > getMaxLenth()) {
					str = str.substring(0, getMaxLenth());
					len = str.length();
				}
				SpannableString ss = new SpannableString(str);
				if (isSplit()) {
					int num = len / 4 + (len % 4 == 0 ? 0 : 1);
					StringBuilder strBd = new StringBuilder();
					for (int i = 0; i < num; i++) {
						if (i == num - 1) {
							strBd.append(str.substring(i * 4, len));
						} else {
							strBd.append(str.substring(i * 4, (i + 1) * 4));
							strBd.append(" ");
						}
					}
					len = strBd.length();
					ss = new SpannableString(strBd);
				}

				if (filled) {
					float textSize = getTextSize();
					textDrawa.setBounds(0, 0, (int) (textSize * 0.8f),
							(int) (textSize * 0.8f));
					for (int i = getStartHideIndex(); i < len
							&& i < getEndHideIndex(); i++) {
						char ch = ss.charAt(i);
						if (ch != ' ') {
							ss.setSpan(new ImageSpan(textDrawa,
									ImageSpan.ALIGN_BASELINE), i, i + 1,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
				}

				int index = getSelectionStart();
				index = index <= len ? index : len;
				index = index >= 0 ? index : 0;
				index = len;
				
				setText(ss);
				setSelection(index);
				//handler.postDelayed(setInputTypeRun, 500);
				noChange = false;
			}
		});
	}

	abstract public int getMaxLenth();

	abstract public int getStartHideIndex();

	abstract public int getEndHideIndex();

	abstract public boolean isSplit();

	public void setFilledText(CharSequence s, Drawable textDrawa) {
		this.textDrawa = textDrawa;
		if (s != null && s.length() == getMaxLenth()) {
			filled = true;
			this.setText(s);
			this.setEnabled(false);
		} else if (s != null && s.length() > 0) {
			filled = true;
			this.setText(s);
			this.setEnabled(true);
		}
	}

	
	
	
	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	public void setDeleView(View deleView) {
		this.deleView = deleView;
		String str = getText().toString().trim();
		deleView.setVisibility(str.length() > 0 ? View.VISIBLE : View.GONE);
		deleView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				filled = false;
				setText(null);
				setEnabled(true);
			}
		});
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			clearFocus();
		}
		return super.onKeyPreIme(keyCode, event);
	}


}


