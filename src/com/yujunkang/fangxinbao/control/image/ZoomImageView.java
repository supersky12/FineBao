package com.yujunkang.fangxinbao.control.image;





import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;

/**
 * 
 * @date 2014-6-2
 * @author xieb
 * 
 */
public class ZoomImageView extends ImageView {
	private int imageWidth;// 图片的原始宽度
	private int imageHeight;// 图片的原始高度
	
	private float scaleRate;// 图片适应屏幕的缩放比例
	
	private Context myContext;
	
	private Bitmap image = null;
	private Matrix mSuppMatrix = new Matrix();
	private final Matrix mDisplayMatrix = new Matrix();
	private Matrix mBaseMatrix = new Matrix();
	private final float[] mMatrixValues = new float[9];
	private float mMaxZoom = 25.0f;// 最大缩放比例
	private float mMinZoom = 0.1f;// 最小缩放比例
	private int controlWidth = 0;
	private int controlHeight = 0;
	
	private float baseValue = 0;
	private float originalScale = 0;
	private float oldx = 0,oldy = 0,newx = 0,newy = 0;
	private static final int NONE = 0; //初始状态
	private static final int DRAG = 1; //拖动中 
	private static final int ZOOM = 2; //缩放中
	private int mode = NONE; 
	
	private GestureDetector gestureScanner;
	private Rect clipRect = null;//剪切控件传过来的剪切框矩形
	private int flag = 0;//0:代表3：4,1:代表4:3
	
	public void setFlag(int clipFlag){
		flag = clipFlag;
	}
	
	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public float getScaleRate() {
		return scaleRate;
	}

	public void setScaleRate(float scaleRate) {
		this.scaleRate = scaleRate;
	}
	
	public ZoomImageView(Context context) {
		// TODO Auto-generated constructor stub
		/*super(context);
		myContext = context;
		this.init();*/
		this(context,null,0);
	}
	
	public ZoomImageView(Context context,AttributeSet attrs){
		this(context,attrs,0);
	}
	
	public ZoomImageView(Context context,AttributeSet attrs, int defStyle){
		super(context,attrs,defStyle);
		myContext = context;
		gestureScanner = new GestureDetector(new MySimpleGesture());
		this.init();
	}
	

	private void init() {
		setScaleType(ImageView.ScaleType.MATRIX);
	}
	
	/**
	 * 计算图片要适应屏幕需要缩放的比例,如屏幕发生翻转需重新计算
	 */
	private void arithScaleRate(int flag) {
		float scaleWidth = 0,scaleHeight = 0;
		if(controlWidth == 0 || controlHeight == 0){
			return;
		}
		if((image.getWidth() > image.getHeight()) && flag == 0){
			//横屏图片进行坚屏截图
			/*scaleWidth = clipRect.width() / (float) imageWidth;
			scaleHeight = clipRect.height() / (float) imageHeight;
			scaleRate = Math.max(scaleWidth, scaleHeight);*/
			scaleRate = clipRect.height() / (float) imageHeight;
		}else{
			/*scaleWidth = controlWidth / (float) imageWidth;
			scaleHeight = controlHeight / (float) imageHeight;
			scaleRate = Math.min(scaleWidth, scaleHeight);*/
			scaleRate = clipRect.width() / (float)imageWidth;
		}	
	}
	
	/*@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		Log.d("pw", ""+bottom);
    }*/
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		controlWidth = w;
		controlHeight = h;
		System.out.println("w : " + w);
		System.out.println("h : " + h);
		// 计算适应屏幕的比例
		//arithScaleRate();
		//缩放到屏幕大小
		//zoomTo(scaleRate,controlWidth / 2f,controlHeight / 2f,true);
		//居中
		//layoutToCenter();
	}
	
	@Override
	public void setImageBitmap(Bitmap bitmap){
		super.setImageBitmap(bitmap);
		image = bitmap;
		//controlWidth = 0;
		//controlHeight = 0;
		//screenWidth = ((Activity)myContext).getWindowManager().getDefaultDisplay().getWidth();
		//screenHeight = ((Activity)myContext).getWindowManager().getDefaultDisplay().getHeight();
		this.imageHeight = image.getHeight();
		this.imageWidth = image.getWidth();
		if(controlWidth == 0 || controlHeight ==0){
			return;
		}
		// 计算适应屏幕的比例
		arithScaleRate(flag);
		//缩放到屏幕大小
		zoomTo(scaleRate,controlWidth / 2f,controlHeight / 2f,true);
		//zoomTo(scaleRate,clipRect.width() / 2f,clipRect.height() / 2f,true);
		//居中
		//layoutToCenter();
	}
	
	protected void zoomTo(float scale, float centerX, float centerY,boolean isCenter) {
		if(controlWidth == 0 || controlHeight == 0){
			return;
		}
		if (scale > mMaxZoom) {
			scale = mMaxZoom;
		} else if (scale < mMinZoom) {
			scale = mMinZoom;
		}

		float oldScale = getScale();
		float deltaScale = scale / oldScale;

		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		setImageMatrix(getImageViewMatrix());
		center(true, true,isCenter);
	}
	
	/**
	 * 
	 * @param horizontal
	 * @param vertical
	 * @param isFirstLoad 是否首次加载图片，如果是则要居中对齐，不是则不需要
	 */
	protected void center(boolean horizontal, boolean vertical,boolean isFirstLoad) {
		this.center(horizontal, vertical, 0.0f,isFirstLoad);
	}
	
	protected void center(boolean horizontal, boolean vertical,float durationMs,boolean isFirstLoad) {
		// if (mBitmapDisplayed.getBitmap() == null) {
		// return;
		// }
		if (image == null) {
			return;
		}

		Matrix m = getImageViewMatrix();

		RectF rect = new RectF(0, 0, image.getWidth(), image.getHeight());
//		RectF rect = new RectF(0, 0, imageWidth*getScale(), imageHeight*getScale());

		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;
			
        if(isFirstLoad){//第一次加载时居中
        	if (vertical) {
    			int viewHeight = getHeight();
    			//int viewHeight = 0;
    			if (height < viewHeight) {
    				deltaY = (viewHeight - height) / 2 - rect.top;
    			} else if (rect.top > 0) {
    				deltaY = -rect.top;
    			} else if (rect.bottom < viewHeight) {
    				deltaY = getHeight() - rect.bottom;
    			}
    		}

    		if (horizontal) {
    			int viewWidth = getWidth();
    			//int viewWidth = 0;
    			if (width < viewWidth) {
    				deltaX = (viewWidth - width) / 2 - rect.left;
    			} else if (rect.left > 0) {
    				deltaX = -rect.left;
    			} else if (rect.right < viewWidth) {
    				deltaX = viewWidth - rect.right;
    				Log.v("pw1", "bmp width:"+width);
    			}
//    			deltaX = (viewWidth - width) / 2 - rect.left;
    		}
        }else{//拖动释放时以顶或底部对齐
		if (vertical) {
			int viewHeight = getHeight();
			//int viewHeight = 0;
			if (height < viewHeight) {
				if(clipRect != null){
					if(rect.top > clipRect.top && rect.bottom > clipRect.bottom){
						if(rect.height() < clipRect.height()){
							//如果当前缩放图片高度小于裁剪框的高度则不进行顶部靠齐
							deltaY = (viewHeight - height) / 2 - rect.top;
						}else{
							deltaY = -rect.top+clipRect.top;
						}
					}else if(rect.top < clipRect.top && rect.bottom < clipRect.bottom){
						if(rect.height() < clipRect.height()){
							//如果当前缩放图片高度小于裁剪框的高度则不进行底部靠齐
							deltaY = (viewHeight - height) / 2 - rect.top;
						}else{
							deltaY = clipRect.bottom - rect.bottom;
						}
						//deltaY = (viewHeight - height) / 2 - rect.top;
					}else if(rect.top < clipRect.top && rect.bottom > clipRect.bottom){
						//正常情况下的居中对齐
						if(isFirstLoad){
							deltaY = (viewHeight - height) / 2 - rect.top;
						}
					}
				}else{
					//正常情况下的居中对齐
					deltaY = (viewHeight - height) / 2 - rect.top;
				}
			} else if (rect.top > 0) {
				if(clipRect != null){
					if(rect.top < clipRect.top){
						//deltaY = -rect.top;
					}else{
						/*if(isFirstLoad){
							deltaY = -rect.top;
						}else{
							deltaY = -rect.top+clipRect.top;
						}*/
						deltaY = -rect.top+clipRect.top;
					}
				}else{
					deltaY = -rect.top;
				}
			} else if (rect.bottom < viewHeight) {
				Log.v("pw","image bottom:"+rect.bottom+"viewHeight:"+viewHeight);
				if(clipRect != null){
					Log.v("pw", "clipRect:"+clipRect.bottom);
					if(rect.bottom < clipRect.bottom){
						deltaY = clipRect.bottom - rect.bottom;
						Log.v("pw","deltaY:"+deltaY);
					}else{
						//deltaY = getHeight() - rect.bottom;
					}
				}else{
					deltaY = getHeight() - rect.bottom;
				}
			}
		}
		
		if (horizontal) {
			int viewWidth = getWidth();
			//int viewWidth = 0;
			if (width < viewWidth) {
				deltaX = (viewWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < viewWidth) {
				deltaX = viewWidth - rect.right;
			}
		}
        }

		if(durationMs == 0.0f){
			postTranslate(deltaX, deltaY);
		}else{
			postTranslateDur(deltaX, deltaY,durationMs);
		}
		//setImageMatrix(getImageViewMatrix());
	}
	
	protected Handler mHandler = new Handler();
	
	float _dy=0.0f;
	float _dx=0.0f;
	protected void postTranslateDur(float dx,float dy,final float durationMs) {
		_dy=0.0f;
		_dx=0.0f;
		final float incrementPerMs_X = dx / durationMs;
		final float incrementPerMs_Y = dy / durationMs;
		final long startTime = System.currentTimeMillis();
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				
				postTranslate(incrementPerMs_X*currentMs-_dx, incrementPerMs_Y*currentMs-_dy);
				_dx=incrementPerMs_X*currentMs;
				_dy=incrementPerMs_Y*currentMs;

				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}
	
	/**
	 * 设置图片居中显示
	 */
	public void layoutToCenter()
	{
		if(controlWidth == 0 || controlHeight == 0){
			return;
		}
		//正在显示的图片实际宽高
		float width = imageWidth*getScale();
		float height = imageHeight*getScale();
		
		//空白区域宽高
		float fill_width = controlWidth - width;
		float fill_height = controlHeight - height;
		
		//需要移动的距离
		float tran_width = 0f;
		float tran_height = 0f;
		
		if(fill_width>0)
			tran_width = fill_width/2;
		if(fill_height>0)
			tran_height = fill_height/2;
		
		postTranslate(tran_width, tran_height);
		setImageMatrix(getImageViewMatrix());
	}
	
	public void postTranslate(float dx, float dy) {
		mSuppMatrix.postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}
	
	// Combine the base matrix and the supp matrix to make the final matrix.
	protected Matrix getImageViewMatrix() {
		// The final matrix is computed as the concatentation of the base matrix
		// and the supplementary matrix.
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}
	
	public float getScale() {
		return getScale(mSuppMatrix);
	}
	
	public float getScale(Matrix matrix) {
		return getValue(matrix, ((image.getWidth() > image.getHeight()) && flag == 0) ? Matrix.MSCALE_Y : Matrix.MSCALE_X);
	}
	
	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		//mMinZoom =(controlWidth/2f)/imageWidth;
		
		return mMatrixValues[whichValue];
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureScanner.onTouchEvent(event);
		switch(event.getAction() & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			baseValue = 0;
			originalScale = this.getScale();
			oldx = event.getX();
			oldy = event.getY();
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			break;
		case MotionEvent.ACTION_POINTER_UP: 
			mode = NONE; 
			break; 
		case MotionEvent.ACTION_MOVE:
			if(event.getPointerCount() == 1 && mode == DRAG){
				//正在显示的图片实际宽高
				float width = imageWidth*getScale();
				float height = imageHeight*getScale();
				
				//高宽小于控件高宽则不可拖动
				if(width < controlWidth && height < controlHeight){
					break;
				}
				//拖动
				newx = event.getX();
				newy = event.getY();
				if((newx-oldx) == 0.0f && (newy-oldy) == 0.0f){
					//Log.d("pw", "x:"+(newx-oldx)+" y:"+(newy-oldy));
				}else{
					this.postTranslate(newx-oldx, newy-oldy);
				}
				oldx = newx;
				oldy = newy;
			}else if(event.getPointerCount() == 2 && mode == ZOOM){
				//缩放
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				float value = FloatMath.sqrt(x * x + y * y);// 计算两点的距离
				if (baseValue == 0) {
					baseValue = value;
				} else {
					float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
					// scale the image
					zoomTo(originalScale * scale, x + event.getX(1), y + event.getY(1),false);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			//正在显示的图片实际宽高
			/*float width = imageWidth*getScale();
			float height = imageHeight*getScale();
			float v[] = new float[9];
			Matrix m = this.getImageMatrix();
			m.getValues(v);
			float left = v[Matrix.MTRANS_X];
			float top = v[Matrix.MTRANS_Y];
			float right = left + width;
			float bottom = top + height;
			Log.d("pw", "left:"+left+" right:"+right+"top:"+top+"bottom:"+bottom);*/
			//center(true,true,200f);
			if (getScale() < getScaleRate()) {
				zoomTo(getScaleRate(), controlWidth / 2, controlHeight / 2, 200f);
			}else{
				center(true,true,200f,false);
			}
			mode = NONE;
			//Log.d("pw", "up");
			break;
		default:
			//
			break;
		}
		//return super.onTouchEvent(event);
		return true;
		
	}
	
	/**
	 * 就算两点间的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	protected void zoomTo(final float scale, final float centerX, final float centerY, final float durationMs) {
		final float incrementPerMs = (scale - getScale()) / durationMs;
		final float oldScale = getScale();
		final long startTime = System.currentTimeMillis();

		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				float target = oldScale + (incrementPerMs * currentMs);
				zoomTo(target, centerX, centerY,true);
				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}
	
	private class MySimpleGesture extends SimpleOnGestureListener {
		// 按两下的第二下Touch down时触发
		public boolean onDoubleTap(MotionEvent e){
			if (getScale() > getScaleRate()) {
				zoomTo(getScaleRate(), controlWidth / 2, controlHeight / 2, 200f);
			} else {
				zoomTo(1.0f, controlWidth / 2, controlHeight / 2, 200f);
			}
			//return super.onDoubleTap(e);
			return true;
		}
	}
	
	public void setClipRect(Rect myRect){
		clipRect = myRect;
	}
}
