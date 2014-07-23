package com.yujunkang.fangxinbao.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class TemperatureCureView extends View {
	private final static String TAG = "TemperatureCureView";
	private final static float TEMPERATURE_NORMAL = 37.5f;

	private final static int PADDING_LEFT = 30;
	private final static int PADDING_RIGHT = 20;
	private final static int PADDING_BOTTOM = 10;
	private final static int PADDING_TOP = 15;

	private final static int PADDING_SMALL = 5;
	private final static int PADDING_MIDDLE = 10;
	private final static int PADDING_LARGE = 15;

	private static int PADDING_LEFT_PX = 0;
	private static int PADDING_RIGHT_PX = 0;
	private static int PADDING_TOP_PX = 0;
	private static int PADDING_SMALL_PX = 0;
	private static int PADDING_MIDDLE_PX = 0;
	private static int PADDING_LARGE_PX = 0;

	private static ArrayList<TemperatureData> constantsTemperature = new ArrayList<TemperatureData>();

	private int _dataCount = 5;

	private int _circle;
	// 图标的圆
	private int _circleBitmap;
	// 图标的圆
	private int _circleNormalBitmap;
	// 图标的圆
	private int _circleHighBitmap;

	private int _totalWidth;

	private Context mContext;
	// 线
	private Paint _linePaint = new Paint();

	// 图表上的温度
	private Paint _temperaturePaint = new Paint();
	private float _temperaturePaintHeight;
	// 时间段
	private Paint _timePaint = new Paint();
	private float _timePaintHeight;

	private BitmapFactory.Options _iconOption = new BitmapFactory.Options();

	// Y坐标的温度
	private Paint _temperatureYPaint = new Paint();
	private float _temperatureYPaintHeight;
	private float _temperatureYTextStartPointY;
	private Paint _horizontaLlinePaint = new Paint();
	private Paint _bitmapPaint = new Paint();
	private float _temperatureYTextWidth;
	// 数据字体大小 如 时间

	private float _lineWidth;
	private int _lineColor;

	// 文字的行间距

	private float _paddingBottomTemperature;

	private float _paddingTopTemperature;

	private float _paddingTopTime;
	private float _paddingBottomTime;

	private float _totalHeight;
	private float _proportion;
	/**
	 * 每个点的x轴间距
	 * 
	 */
	private float _viewportSize;

	/**
	 * 每个刻度的垂直距离
	 */
	private float _VPerSpacing;

	private float _startPointX;
	private float _endPointX;
	private float _startPointY;
	private float _startYCurePointX;
	private float _pathStartX;
	private float _pathStartY;
	private float _offsetY;
	private float _offsetX;
	private float _endPointY;
	private float _rectWidth;
	private float _rectHeight;

	private List<TemperatureData> mDataList = null;

	private float _chartHeight;

	// 坐标
	private float _timeTextPointY;

	private float _temperatureTextStartPointY;

	private float _minTemperatureFloat;

	private float _maxTemperatureFloat;

	private int _gradientColorOnEnd;
	private int _gradientColorOnMiddle;
	private int _gradientColorOnStart;

	private float lastTouchEventX;
	private float graphwidth;
	private boolean scrollingStarted;
	private float _viewportStart = 0;

	public TemperatureCureView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TemperatureCureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TemperatureView);
		try {
			_circle = a.getResourceId(
					R.styleable.TemperatureView_connectionImage,
					R.drawable.icon_temperature_low);

			_lineWidth = a.getFloat(
					R.styleable.TemperatureView_lineStrokeWidth, 2);
			_lineColor = a.getColor(R.styleable.TemperatureView_lineColor,
					Color.parseColor("#2ea7e0"));
			int timePaintTextSize = Utils.dip2px(mContext,
					a.getFloat(R.styleable.TemperatureView_timeTextSize, 14f));
			int timeTextColor = a.getColor(
					R.styleable.TemperatureView_timeTextColor, Color.BLACK);
			generateTextPaint(_timePaint, timePaintTextSize, timeTextColor);
			int temperaturePaintTextSize = Utils.dip2px(mContext, a.getFloat(
					R.styleable.TemperatureView_temperatureTextSize, 16f));
			int temperatureTextColor = a.getColor(
					R.styleable.TemperatureView_temperatureTextColor,
					Color.BLACK);
			generateTextPaint(_temperaturePaint, temperaturePaintTextSize,
					temperatureTextColor);
			int temperatureYPaintTextSize = Utils.dip2px(mContext, a.getFloat(
					R.styleable.TemperatureView_temperatureYTextSize, 16f));
			int temperatureYTextColor = a.getColor(
					R.styleable.TemperatureView_temperatureYTextColor,
					Color.BLACK);
			generateTextPaint(_temperatureYPaint, temperatureYPaintTextSize,
					temperatureYTextColor);
			float horizontaLlineWidth = a.getFloat(
					R.styleable.TemperatureView_horizontalLineStrokeWidth, 2);
			_horizontaLlinePaint.setColor(temperatureYTextColor);
			_horizontaLlinePaint.setAntiAlias(true);
			_horizontaLlinePaint.setStrokeWidth(horizontaLlineWidth);
			_proportion = a.getFloat(R.styleable.TemperatureView_proportion,
					1.0f);
			_gradientColorOnStart = a.getColor(
					R.styleable.TemperatureView_gradientColorOnStart,
					Color.GREEN);
			_gradientColorOnMiddle = a.getColor(
					R.styleable.TemperatureView_gradientColorOnMiddle,
					Color.GREEN);
			_gradientColorOnEnd = a
					.getColor(R.styleable.TemperatureView_gradientColorOnEnd,
							Color.WHITE);
			_totalHeight = a.getFloat(R.styleable.TemperatureView_totalHeight,
					480);
			_viewportSize = a.getFloat(R.styleable.TemperatureView_hSpacing,
					150);
		} finally {
			a.recycle();
		}

		//init();
	}

	private void generateTextPaint(Paint paint, int textSize, int textColor) {
		paint.setTextSize(textSize);
		paint.setColor(textColor);
		paint.setAntiAlias(true);
	}

	private void init() {
		constantsTemperature.clear();
		// 初始化固定的温度
		TemperatureData temp = new TemperatureData();
		temp.setTemperature("38.5");
		_temperatureYTextWidth = _temperatureYPaint.measureText(temp
				.getTemperature());
		constantsTemperature.add(temp);
		temp = new TemperatureData();
		temp.setTemperature("35.3");
		constantsTemperature.add(temp);
		temp = new TemperatureData();
		temp.setTemperature("37.5");
		constantsTemperature.add(temp);
		// 初始化间距参数
		PADDING_LEFT_PX = Utils.dip2px(mContext, PADDING_LEFT);
		PADDING_RIGHT_PX = Utils.dip2px(mContext, PADDING_RIGHT);

		PADDING_TOP_PX = Utils.dip2px(mContext, PADDING_TOP);
		PADDING_SMALL_PX = Utils.dip2px(mContext, PADDING_SMALL);
		PADDING_MIDDLE_PX = Utils.dip2px(mContext, PADDING_MIDDLE);
		PADDING_LARGE_PX = Utils.dip2px(mContext, PADDING_LARGE);

		_paddingBottomTemperature = PADDING_MIDDLE_PX;
		_paddingTopTemperature = PADDING_MIDDLE_PX;
		_paddingTopTime = PADDING_SMALL_PX * 6;
		_paddingBottomTime = PADDING_SMALL_PX * 6;
		_offsetY = PADDING_SMALL_PX;
		_bitmapPaint.setAntiAlias(true);
		_bitmapPaint.setStyle(Style.FILL);
		if (_circle != -1) {

			_circleBitmap = _circle;

			_circleNormalBitmap = R.drawable.icon_temperature_normal;

			_circleHighBitmap = R.drawable.icon_temperature_high;

		}

		// 线
		_linePaint.setColor(_lineColor);
		_linePaint.setAntiAlias(true);
		_linePaint.setStrokeWidth(_lineWidth);

		// 温度文字的高度

		_temperaturePaintHeight = Utils
				.computePaintTextHeight(_temperaturePaint);

		// 时间

		_timePaintHeight = Utils.computePaintTextHeight(_timePaint);

		_temperatureYPaintHeight = Utils
				.computePaintTextHeight(_temperatureYPaint);

		calculateTimeTextPointY();

		_startYCurePointX = PADDING_LEFT_PX - _temperatureYTextWidth / 2f;
		_startPointX = PADDING_LEFT_PX + _temperatureYTextWidth;

		_startPointY = _totalHeight - _timePaintHeight - _paddingBottomTime
				- _paddingTopTime;
		_temperatureTextStartPointY = _startPointY - _paddingBottomTemperature
				- _temperaturePaintHeight
				- _temperaturePaint.getFontMetrics().top;
		_temperatureYTextStartPointY = _startPointY - _temperatureYPaintHeight
				/ 2 - _temperatureYPaint.getFontMetrics().top;
		_chartHeight = _totalHeight - PADDING_TOP_PX - _temperaturePaintHeight
				- _paddingBottomTemperature - _paddingTopTime
				- _timePaintHeight - _paddingBottomTime;

		_pathStartY = _timeTextPointY - _timePaintHeight / 2f
				- _timePaint.getFontMetrics().top;
		_pathStartX = _startPointX;
		_iconOption.inSampleSize = 3;
	}

	public void setProportion(float value) {
		init();
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		LoggerTool.d(TAG, "onMeasure");
		final int _totalWidth = MeasureSpec.getSize(widthMeasureSpec);
		LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
		if (params.height != LayoutParams.WRAP_CONTENT) {
			LoggerTool.d(TAG, " " + params.height);
			_totalHeight = params.height;
			init();
		}
		setMeasuredDimension(_totalWidth, (int) _totalHeight);

	}

	private void calculateTimeTextPointY() {
		_timeTextPointY = _totalHeight - _timePaintHeight - _paddingBottomTime
				- _timePaint.getFontMetrics().top;

	}

	private float calculateTextPointX(int index, float textWidth) {
		return this._startPointX + _viewportSize * index - textWidth / 2f;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		LoggerTool.d(TAG, "onDraw");
		if (mDataList != null && mDataList.size() > 0) {
			drawRegionPath(canvas);
			for (TemperatureData itemY : constantsTemperature) {
				float YTextPointY = getLineYTextPointY(itemY);
				String temperatureY = itemY.getTemperature();
				canvas.drawText(temperatureY, _startYCurePointX, YTextPointY,
						_temperatureYPaint);
				canvas.drawLine(_startYCurePointX + _temperatureYTextWidth,
						getLinePointY(itemY), 1000, getLinePointY(itemY),
						_horizontaLlinePaint);
			}
			drawTemperature(canvas);
		}
	}

	// 画体温曲线
	private void drawRegionPath(Canvas canvas) {
		if (mDataList != null && mDataList.size() > 0) {
			canvas.save();

			Rect clientRect = new Rect((int) (0 - _viewportStart),
					-(int) (_rectHeight + _offsetY),
					(int) (_rectWidth - _viewportStart), 0);

			int colors[] = new int[3];
			float positions[] = new float[3];

			// 第1个点
			colors[0] = _gradientColorOnStart;
			positions[0] = 0;

			// 第2个点
			colors[1] = _gradientColorOnMiddle;
			positions[1] = 0.5f;

			// 第3个点
			colors[2] = _gradientColorOnEnd;
			positions[2] = 1;
			LinearGradient lg = new LinearGradient(0, -_rectHeight - _offsetY,
					0, 0, colors, positions, TileMode.MIRROR);

			Paint paint = new Paint();
			paint.setShader(lg);
			// 这里用相对坐标简单点,把坐标y,时间的字baselineAligned=bottom
			canvas.translate(_pathStartX, _pathStartY);
			Path path = new Path(); // Path对象
			// 起始点
			path.moveTo(0 - _viewportStart, 0);
			for (int index = 0; index < _dataCount; index++) {
				TemperatureData item = mDataList.get(index);
				path.lineTo(_viewportSize * index - _viewportStart,
						-getOffsetY(item) - _paddingTopTime - _timePaintHeight
								- 1);
			}
			path.lineTo(_viewportSize * (_dataCount - 1) - _viewportStart, 0);
			path.lineTo(0 - _viewportStart, 0);
			path.close();
			canvas.clipPath(path, Region.Op.INTERSECT);
			canvas.drawRect(clientRect, paint);
			canvas.restore();
		}
	}

	// 画体温曲线
	private void drawTemperature(Canvas canvas) {

		float startX = 0;
		float startY = 0;
		canvas.save();
		canvas.translate(_startPointX, 0);
		for (int index = 0; index < _dataCount; index++) {
			startX = _viewportSize * index - _viewportStart;
			TemperatureData item = mDataList.get(index);
			if (index < (_dataCount - 1)) {
				// 画线
				TemperatureData nextItem = mDataList.get(index + 1);
				float endLineX = startX + _viewportSize;
				canvas.drawLine(startX, getLinePointY(item), endLineX,
						getLinePointY(nextItem), _linePaint);
			}
			// 画圆
			startY = getLinePointY(item);
			drawCircle(canvas, item, startX, startY);
			// // 画温度
			if (isShowTemperature(item)) {
				String temperature = item.getTemperature();
				float temperatureTextWidth = _temperaturePaint
						.measureText(temperature);
				float temperatureY = getLineTextPointY(item);
				canvas.drawText(temperature,
						startX - temperatureTextWidth / 2f, temperatureY,
						_temperaturePaint);
			}
		}
		canvas.restore();
		for (int index = 0; index < _dataCount; index++) {
			TemperatureData item = mDataList.get(index);
			// 画时间文字
			String timeString = item.getTime();
			if (!TextUtils.isEmpty(timeString)) {
				float timeTextWidth = _timePaint.measureText(timeString);
				canvas.drawText(timeString,
						calculateTextPointX(index, timeTextWidth)
								- _viewportStart, _timeTextPointY, _timePaint);
			}
		}
	}

	/**
	 * @param event
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean handled = false;
		if (!handled) {
			if ((event.getAction() & MotionEvent.ACTION_DOWN) == MotionEvent.ACTION_DOWN
					&& (event.getAction() & MotionEvent.ACTION_MOVE) == 0) {
				scrollingStarted = true;
				handled = true;
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			if ((event.getAction() & MotionEvent.ACTION_UP) == MotionEvent.ACTION_UP) {
				scrollingStarted = false;
				lastTouchEventX = 0;
				handled = true;
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			if ((event.getAction() & MotionEvent.ACTION_MOVE) == MotionEvent.ACTION_MOVE) {
				if (scrollingStarted) {
					if (lastTouchEventX != 0) {
						onMoveGesture(event.getX() - lastTouchEventX);
					}
					lastTouchEventX = event.getX();
					handled = true;
				}
			}
			if (handled)
				invalidate();
		} else {
			// currently scaling
			scrollingStarted = false;
			lastTouchEventX = 0;
		}
		return handled;
	}

	private void onMoveGesture(float f) {
		if (_viewportSize != 0
				&& _rectWidth >= DataConstants.DEVICE_WIDTH - _startPointX) {
			_viewportStart = _viewportStart - f;
			if (_viewportStart < 0) {
				_viewportStart = 0;
			} else if (_viewportStart + _viewportSize * 2 > _rectWidth) {
				_viewportStart = _rectWidth - _viewportSize * 2;
			}
			LoggerTool.d(TAG, "viewportStart :" + _viewportStart);
		}
		invalidate();
	}

	/**
	 * 画圆
	 * 
	 * @param data
	 * @return
	 */
	private void drawCircle(Canvas canvas, TemperatureData data, float x,
			float y) {
		try {
			int circle = getBitmapResIdByTemperature(data);
			Bitmap circleBitmap = BitmapFactory.decodeResource(getResources(),
					circle);
			int height = circleBitmap.getHeight();
			int width = circleBitmap.getWidth();
			int circleBitmapOffsetY = height / 2;
			int circleBitmapOffsetX = width / 2;
			canvas.drawBitmap(circleBitmap, x - circleBitmapOffsetX, y
					- circleBitmapOffsetY, _bitmapPaint);
		} catch (Exception ex) {

		}
	}

	private boolean isShowTemperature(TemperatureData data) {
		float temperature = getTemperature(data.getTemperature());
		if (temperature >= TEMPERATURE_NORMAL) {
			return true;
		}
		return false;
	}

	/**
	 * 根据不同温度展示不同的圆
	 * 
	 * @param data
	 * @return
	 */
	private int getBitmapResIdByTemperature(TemperatureData data) {
		float temperature = getTemperature(data.getTemperature());
		if (temperature < TEMPERATURE_NORMAL) {
			return _circleBitmap;
		} else if (temperature == TEMPERATURE_NORMAL) {
			return _circleNormalBitmap;
		} else if (temperature > TEMPERATURE_NORMAL) {
			return _circleHighBitmap;
		}
		return _circleBitmap;
	}

	/**
	 * 线上点的坐标=起始坐标+(温度-最小温度)*每1度温度的间距
	 * 
	 * @param data
	 * @return
	 */
	private float getLinePointY(TemperatureData data) {
		return this._startPointY - getOffsetY(data);
	}

	private float getOffsetY(TemperatureData data) {
		return (getTemperature(data.getTemperature()) - _minTemperatureFloat)
				* _VPerSpacing;
	}

	/**
	 * 线上点的坐标=起始坐标+(温度-最小温度)*每1度温度的间距
	 * 
	 * @param data
	 * @return
	 */
	private float getLineTextPointY(TemperatureData data) {
		return this._temperatureTextStartPointY
				- (getTemperature(data.getTemperature()) - _minTemperatureFloat)
				* _VPerSpacing;
	}

	/**
	 * 线上点的坐标=起始坐标+(温度-最小温度)*每1度温度的间距
	 * 
	 * @param data
	 * @return
	 */
	public float getLineYTextPointY(TemperatureData data) {
		return this._temperatureYTextStartPointY
				- (getTemperature(data.getTemperature()) - _minTemperatureFloat)
				* _VPerSpacing;
	}

	public void initData(List<TemperatureData> datas) {
		if (datas != null && datas.size() > 0) {
			_dataCount = datas.size();
			mDataList = datas;
			resolveData();
			invalidate();
		} else {
			setVisibility(View.GONE);
		}
	}

	private float getTemperature(String Temperature) {
		try

		{
			return Float.parseFloat(Temperature.substring(0,
					Temperature.length()));
		} catch (Exception ex) {
			return _minTemperatureFloat;
		}
	}

	/**
	 * 得到最高温度和最低温度，好计算每个刻度的间距
	 */
	private void resolveData() {
		ArrayList<TemperatureData> tempData = new ArrayList<TemperatureData>();
		tempData.addAll(mDataList);
		tempData.addAll(constantsTemperature);

		Collections.sort(tempData, new TemperatureComparator());
		try {
			String maxTemperature = tempData.get(tempData.size() - 1)
					.getTemperature();
			String minTemperature = tempData.get(0).getTemperature();
			_maxTemperatureFloat = Float.parseFloat(maxTemperature.substring(0,
					maxTemperature.length()));

			_minTemperatureFloat = Float.parseFloat(minTemperature.substring(0,
					minTemperature.length()));
			if (_maxTemperatureFloat == _minTemperatureFloat) {
				_VPerSpacing = 0;
			} else {
				_VPerSpacing = _chartHeight
						/ (_maxTemperatureFloat - _minTemperatureFloat);
			}
			_rectWidth = _viewportSize * (_dataCount - 1);
			_rectHeight = _totalHeight;
		} catch (Exception ex) {
			Log.e("TemperatureView", ex.getMessage());
		}

	}

	public class TemperatureComparator implements Comparator<TemperatureData> {
		@Override
		public int compare(TemperatureData data1, TemperatureData data2) {
			try {
				String temperature1 = data1.getTemperature();
				String temperature2 = data2.getTemperature();
				float d1 = Float.parseFloat(temperature1);
				float d2 = Float.parseFloat(temperature2);
				if (d1 >= d2) {
					return 1;
				} else if (d1 == d2) {
					return 0;
				} else {
					return -1;
				}
			} catch (Exception ex) {
				return 0;
			}
		}

	}

	class Point {
		float offsetX;
		float offsetY;
	}
}
