package com.yujunkang.fangxinbao.control;



import com.yujunkang.fangxinbao.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class SearchBarView extends LinearLayoutControlWrapView {
	private EditText et_search_condition;

	private ImageButton btn_cancel;
	private OnEditTextOnFocusChangeListener mOnEditTextOnFocusChangeListener;
	
	
	
	public SearchBarView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	
	public void ensureUI() {
		et_search_condition = (EditText) findViewById(R.id.et_search_condition);
		btn_cancel = (ImageButton) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				et_search_condition.setText("");
				
			}
		});
		et_search_condition.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et_search_condition.setText(null);
			}
		});
		et_search_condition.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						et_search_condition.clearFocus();
					}
				}
				return false;
			}
		});
		et_search_condition
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus && et_search_condition.length() > 0) {
							btn_cancel.setVisibility(View.VISIBLE);
						} else {
							btn_cancel.setVisibility(View.GONE);
						}
						if(mOnEditTextOnFocusChangeListener!= null)
						{
							mOnEditTextOnFocusChangeListener.onFocusChange(v, hasFocus);
						}
					}
				});
		et_search_condition.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				int len = editable.length();
				if (btn_cancel != null) {
					btn_cancel.setVisibility(len == 0 ? View.GONE
							: View.VISIBLE);
				}

			}
		});
	}

	
	
	public OnEditTextOnFocusChangeListener getOnEditTextOnFocusChangeListener() {
		return mOnEditTextOnFocusChangeListener;
	}

	public void setOnEditTextOnFocusChangeListener(
			OnEditTextOnFocusChangeListener OnEditTextOnFocusChangeListener) {
		this.mOnEditTextOnFocusChangeListener = OnEditTextOnFocusChangeListener;
	}

	public void addTextWatcher(TextWatcher watcher) {
		et_search_condition.addTextChangedListener(watcher);
	}

	public interface OnEditTextOnFocusChangeListener
	{
		public void onFocusChange(View v, boolean hasFocus) ;
	}
	
	public void onFinishInflate() {
		super.onFinishInflate();
		mInflater.inflate(R.layout.input_searchbar_view, this);
		ensureUI();
	}
	
}
