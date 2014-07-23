package com.yujunkang.fangxinbao.control;

import com.yujunkang.fangxinbao.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 
 * @date 2014-6-17
 * @author xieb
 * 
 */
public abstract class SecurityTextView extends TextView {
	protected boolean filled = false;
	private boolean noChange = false;
	private Drawable textDrawa;

	public SecurityTextView(Context context) {
		this(context, null);

	}

	private void init() {
		setSingleLine(true);
		textDrawa = getContext().getResources().getDrawable(
				R.drawable.snow_char_a);
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
				StringBuilder strBd = new StringBuilder();
				if (isSplit()) {
					int num = len / 4 + (len % 4 == 0 ? 0 : 1);

					for (int i = 0; i < num; i++) {
						if (i == num - 1) {
							strBd.append(str.substring(i * 4, len));
						} else {
							strBd.append(str.substring(i * 4, (i + 1) * 4));
							strBd.append(" ");
						}
					}
				} else {
					strBd.append(str);
				}
				len = strBd.length();
				ss = new SpannableString(strBd);
				if (filled) {
					float textSize = getTextSize();
					textDrawa.setBounds(0, 0, (int) (textSize * 0.8f),
							(int) (textSize * 0.8f));
					for (int index = 0; index < len; index++) {
						char ch = ss.charAt(index);

						if (index >= getStartHideIndex()
								&& index < getEndHideIndex()) {
							if (ch != ' ') {
//								ss.setSpan("*", index,
//										index + 1,
//										Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								ss.setSpan(new ImageSpan(textDrawa,
										ImageSpan.ALIGN_BASELINE), index,
										index + 1,
										Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							}
						} else {
							if (ch == '*') {
								// ss.setSpan("*", start, end, flags);
								ss.setSpan("*", index,
										index + 1,
										Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

								// ss.setSpan(new ImageSpan(textDrawa,
								// ImageSpan.ALIGN_BASELINE), index,
								// index + 1,
								// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

							}
						}

					}
				}

				int index = getSelectionStart();
				index = index <= len ? index : len;
				index = index >= 0 ? index : 0;
				index = len;

				setText(ss);

				// handler.postDelayed(setInputTypeRun, 500);
				noChange = false;
			}
		});
	}

	public SecurityTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SecurityTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
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

}
