package com.yujunkang.fangxinbao.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yujunkang.fangxinbao.R;





import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;



/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class IndexScrollBar extends View {
	private  List<String> sectionsData = Arrays.asList("A", "B",
			"C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
			"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
	private  ArrayList<String> sections = new ArrayList<String>();

	private ScrollListener mScrollListener;
	private Paint mPaint;
	private int viewHeight;
	private float cellHeight;
	private float textSize;
	private boolean isDynamicTextSize = true;
	

	public IndexScrollBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setColor(context.getResources().getColor(R.color.txt_fg_deep_normal));
		mPaint.setAntiAlias(true);
		mPaint.setTextAlign(Paint.Align.CENTER);
		
		sections.addAll(sectionsData);
		
	}

	public void setOnScrollListener(ScrollListener scroller) {
		mScrollListener = scroller;
	}

	public void addNewSection(int index, String section) {
		if (!sections.contains(section)) {
			sections.add(index, section);
			invalidate();
		}
	}
	
	public void setNewSectionList(ArrayList<String> sections, boolean isDynamicTextSize, float textSize){
		if (null != sections){
			this.sections = sections;
			this.isDynamicTextSize = isDynamicTextSize;
			if (!isDynamicTextSize) {
				this.textSize = textSize;
				mPaint.setTextSize(textSize);
			}
			this.setVisibility(View.VISIBLE);
			invalidate();
		}
	}
	
	public void resetSections() {
		if (null != sections) {
			sections.clear();
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int padding = 10;
		viewHeight = getMeasuredHeight() - 20;
		int viewWidth = this.getMeasuredWidth();
		int sectionCount = sections.size();
		
		if (0 == sectionCount) {
			this.setVisibility(View.GONE);
			return;
		} else {
			this.setVisibility(View.VISIBLE);
		}
		
		cellHeight = viewHeight / sectionCount;
		
		if (isDynamicTextSize) {		
			textSize = cellHeight - 5;
			mPaint.setTextSize(textSize);
			for (int i = 0; i < sectionCount; i++) {
				float y = cellHeight * i + cellHeight / 2 + textSize + 10;
				canvas.drawText(sections.get(i), viewWidth / 2, y, mPaint);
			}
		} else {
			// 非动态计算
			float startY = padding;
			float halfViewWith = viewWidth / 2;
			FontMetrics fm = mPaint.getFontMetrics();
			float below = (-fm.bottom - fm.top) / 2 + cellHeight / 2;
			for (int i = 0; i < sectionCount; i++) {
				canvas.drawText(sections.get(i), halfViewWith, startY + below, mPaint);
				startY += cellHeight;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			this.setBackgroundResource(R.drawable.scrollbarbackground);
			float touchY = event.getY();
			int position = (int) Math.floor(touchY / cellHeight);
			position = Math.min(position, sections.size() - 1);
			position = Math.max(position, 0);
			String text = sections.get(position);
			mScrollListener.onScroll(text);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			this.setBackgroundColor(Color.parseColor("#00000000"));
		}
		return true;
	}

	public interface ScrollListener {
		public void onScroll(String indexString);
	}

}


