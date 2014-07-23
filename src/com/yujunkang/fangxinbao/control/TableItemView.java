package com.yujunkang.fangxinbao.control;

import com.yujunkang.fangxinbao.R;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @date 2014-6-17
 * @author xieb
 * 
 */
public class TableItemView extends LinearLayoutControlWrapView {
	private TextView tv_left;
	private TextView tv_right;
	private ImageView iv_arrow;

	public TableItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInflater.inflate(R.layout.table_item, this);
		ensureUI();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TableItemView);
		try {
			if (a.hasValue(R.styleable.TableItemView_leftLabel)) {
				String leftLabel = a
						.getString(R.styleable.TableItemView_leftLabel);
				tv_left.setText(leftLabel);
			} else {
				tv_left.setText("");
			}
			if (a.hasValue(R.styleable.TableItemView_rightLabel)) {
				String rightLabel = a
						.getString(R.styleable.TableItemView_rightLabel);
				tv_right.setText(rightLabel);
			} else {
				tv_right.setText("");
			}

			if (a.hasValue(R.styleable.TableItemView_leftTextColor)) {
				int txtcolor = a.getColor(
						R.styleable.TableItemView_leftTextColor, color.black);
				tv_left.setTextColor(txtcolor);
			}
			if (a.hasValue(R.styleable.TableItemView_rightTextColor)) {
				tv_right.setTextColor(a.getColor(
						R.styleable.TableItemView_rightTextColor, color.black));
			}
		} catch (Exception ex) {
			//
		} finally {
			a.recycle();
		}
	}

	public void ensureUI() {
		tv_left = (TextView) findViewById(R.id.tv_left);
		tv_right = (TextView) findViewById(R.id.tv_right);
		iv_arrow= (ImageView) findViewById(R.id.iv_arrow);
	}
	
	public void setRightText(String text)
	{
		tv_right.setText(text);
	}

	public void setShowIcon(boolean show)
	{
		iv_arrow.setVisibility(show?View.VISIBLE:View.INVISIBLE);
	}
}
