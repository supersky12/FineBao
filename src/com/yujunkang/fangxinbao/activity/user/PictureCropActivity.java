package com.yujunkang.fangxinbao.activity.user;

import java.io.File;
import java.io.FileNotFoundException;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.control.image.CaptureView;
import com.yujunkang.fangxinbao.control.image.ZoomImageView;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.utility.LoggerTool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * @date 2014-6-2
 * @author xieb
 * 
 */
public class PictureCropActivity extends ActivityWrapper {
	private static final String TAG = "PictureCropActivity";
	public static String CROPPER_FILE_NAME = "tempcrop.png";
	private View mZoomImgContainer;
	private ZoomImageView mZoomImg;
	private CaptureView mCapture;
	private View btn_cancel;
	
	private View btn_rotate;
	private View btn_done;

	private Bitmap mBitmap = null;
	private int mFlag = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.photo_croper_activity);

		initUI();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			displayScaleImg(false);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}

	public int computeSampleSize(BitmapFactory.Options options, int width,
			int height) {
		int inSampleSize = 1;

		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		float ratioX = (float) outWidth / (float) width;
		float ratioY = (float) outHeight / (float) height;
		float sample = ratioX > ratioY ? ratioX : ratioY;
		if (sample < 3) {
			inSampleSize = (int) sample;
		} else if (sample < 6.5) {
			inSampleSize = 4;
		} else if (sample < 8) {
			inSampleSize = 8;
		} else {
			inSampleSize = (int) sample;
		}

		return inSampleSize;
	}

	public void initData() {
		try {
			Uri uri = getIntent().getData();
			Display display = getWindowManager().getDefaultDisplay();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri), null, options);
			options.inSampleSize = computeSampleSize(options,
					display.getWidth(), display.getHeight());
			options.inJustDecodeBounds = false;
			mBitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri), null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
		}
	}

	private void initUI() {
		mZoomImgContainer = findViewById(R.id.zoom_img_container);
		mZoomImg = (ZoomImageView) findViewById(R.id.zoom_img);
		mCapture = (CaptureView) findViewById(R.id.capture);
		btn_cancel = findViewById(R.id.btn_cancel);
		
		btn_rotate = findViewById(R.id.btn_rotate);
		btn_done = findViewById(R.id.btn_done);

		setListener();
	}

	private void setListener() {
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	
		btn_rotate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBitmap != null) {
					try {
						Matrix matRotate = new Matrix();
						matRotate.postRotate(90);
						mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
								mBitmap.getWidth(), mBitmap.getHeight(),
								matRotate, false);
						displayScaleImg(true);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						System.gc();
					}
				}
			}
		});
		btn_done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new CropImgTask("正在截图...").safeExecute();
			}
		});
	}

	



	/**
	 * 判断图片是否为坚屏图片(高大于宽)否则为横屏图片(宽大于高)
	 * @param img
	 * @return
	 */
	private boolean imgIsVertical(Bitmap img){
		boolean result = false;
		if(img.getHeight() > img.getWidth()){
			result = true;
		}
		return result;
	}	
	
	/**
	 *	展示图片
	 * @param isRotate 如果为真则不按图片的长宽来重置截屏框大小
	 */
	private void displayScaleImg(boolean isRotate){
		if(mBitmap != null){
			if(isRotate){
				mZoomImg.setImageBitmap(mBitmap);
			}else{
				if(imgIsVertical(mBitmap)){
					//图片为坚屏图片
					mFlag = 0;
				}else{
					//图片为横屏图片
					mFlag = 1;
				}
				mZoomImg.setFlag(mFlag);
				mZoomImg.setClipRect(mCapture.getCaptureRect());
				mFlag = mFlag == 0?1:0;//取反
				mZoomImg.setImageBitmap(mBitmap);
			}
		}
	}		
	
	/**
	 * 截图 
	 * @return
	 */
	private Bitmap cropImage(){
		Bitmap cache = null;
		mZoomImgContainer.setDrawingCacheEnabled(true);
		mZoomImgContainer.buildDrawingCache();
		cache = Bitmap.createBitmap(mZoomImgContainer.getDrawingCache());
		mZoomImgContainer.destroyDrawingCache();
		mZoomImgContainer.setDrawingCacheEnabled(false);
		Rect cropRect = mCapture.getCaptureRect();
		int left = cropRect.left;
		int top = cropRect.top;
		int width = cropRect.width();
		int height = cropRect.height();
		
		if (width > mZoomImg.getImageWidth()*mZoomImg.getScale()) {
			left = (int) (left + (width - mZoomImg.getImageWidth()*mZoomImg.getScale())/2);
			width = (int) (mZoomImg.getImageWidth()*mZoomImg.getScale());
		}
		if (height > mZoomImg.getImageHeight()*mZoomImg.getScale()) {
			top = (int) (top + (height - mZoomImg.getImageHeight()*mZoomImg.getScale())/2);
			height = (int) (mZoomImg.getImageHeight()*mZoomImg.getScale());
		}
		
		Bitmap crop = Bitmap.createBitmap(cache, left, top, width, height);
		
		return crop;
	}	
	

	/**
	 * 截图异步任务
	 */
	private class CropImgTask extends
			AsyncTaskWithLoadingDialog<Void, Void, Void> {

		public CropImgTask(String title) {
			super(getSelfContext(), title);
		}

		@Override
		protected Void doInBackground(Void... params) {
			Bitmap bitmap = cropImage();
			if (bitmap != null) {
				try {
					if (mCapture.getScaleFlag() == 1) {
						Matrix matrix = new Matrix();
						matrix.postRotate(-90);
						bitmap = Bitmap.createBitmap(bitmap, 0, 0,
								bitmap.getWidth(), bitmap.getHeight(), matrix,
								true);
					}
					
					mFileCache.put(CROPPER_FILE_NAME, bitmap,
							CompressFormat.PNG, 100);
				} catch (Exception e) {
					LoggerTool.e(TAG, e.getMessage());
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			PictureCropActivity.this.setResult(RESULT_OK);
			
			finish();
			
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
