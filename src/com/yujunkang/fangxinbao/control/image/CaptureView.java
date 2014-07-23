package com.yujunkang.fangxinbao.control.image;

import com.yujunkang.fangxinbao.utility.DataConstants;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 
 * @date 2014-6-2
 * @author xieb
 * 
 */
public class CaptureView extends View {
	private Paint outsideCapturePaint = new Paint(); // 捕获框体外部画笔
	private Paint lineCapturePaint = new Paint(); // 边框画笔
	
	private Rect viewRect; // 可视范围
	private Rect captureRect; // 框体范围
	
	private int scaleFlag = 0;//0:代表3：4,1:代表4:3
	private int viewWidth = 0,viewHeight = 0;//只考虑坚屏的情况下记录控件的高宽
	
	private final int leftEdge = 10;//左右两边保留的边距

	public CaptureView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

	public CaptureView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}

	public CaptureView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView();
		
	}
	
	public int getScaleFlag() {
		return scaleFlag;
	}

	public void setScaleFlag(int scaleFlag) {
		this.scaleFlag = scaleFlag;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		// 初始化可视范围及框体大小
		viewRect = new Rect(left, top, right, bottom);
		viewWidth = right - left;
		viewHeight = bottom - top;
		int captureY = (DataConstants.DEVICE_HEIGHT - DataConstants.DEVICE_WIDTH) / 2;
		captureRect = new Rect(leftEdge, captureY, DataConstants.DEVICE_WIDTH
				- leftEdge, captureY + DataConstants.DEVICE_WIDTH);
		//setScale(0);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		if(captureRect != null){
			canvas.save();
			Path path = new Path();
			path.addRect(new RectF(captureRect), Path.Direction.CW);// 顺时针闭合框体
			canvas.clipPath(path, Region.Op.DIFFERENCE);
			canvas.drawRect(viewRect, outsideCapturePaint); // 绘制框体外围区域
	
			canvas.drawPath(path, lineCapturePaint); // 绘制框体
			canvas.restore();
		}
	}
	
	private void initView(){
		lineCapturePaint.setStrokeWidth(4F); // 捕获框边框画笔大小
		lineCapturePaint.setStyle(Paint.Style.STROKE);// 画笔风格:空心
		lineCapturePaint.setAntiAlias(true); // 抗锯齿
		lineCapturePaint.setColor(Color.WHITE); // 画笔颜色
		setFullScreen(true);
	}
	
	private void setFullScreen(boolean full) {
		if (full) { // 全屏，则把外部框体颜色设为透明
			outsideCapturePaint.setARGB(100, 50, 50, 50);
		} else { // 只显示框体区域，框体外部为全黑
			outsideCapturePaint.setARGB(255, 0, 0, 0);
		}
	}
	
	public Rect getCaptureRect() {
		return captureRect;
	}

}
