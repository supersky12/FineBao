package com.yujunkang.fangxinbao.utility;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;

/**
 * 
 * @date 2014-7-8
 * @author xieb
 * 
 */
public class BitmapUtils {
	
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable == null) {
			return null;
		}

		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);

		return bitmap;

	}
	
	
	
	public static byte[] bmpToByteArray(final Bitmap bmp) {
		if (bmp == null) {
			return null;
		}

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		// bmp.compress(CompressFormat.JPEG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 得到微信适配图片尺寸
	 * 
	 * @param myBitmap
	 * @param newImageSize
	 */
	public static void getBitmapScaleLenght(Bitmap myBitmap, int[] newImageSize) {
		if (myBitmap != null) {
			if (myBitmap.getWidth() > myBitmap.getHeight()) {
				// 宽大于高
				if (myBitmap.getWidth() > DataConstants.THUMB_SIZE) {
					newImageSize[0] = DataConstants.THUMB_SIZE;
					newImageSize[1] = Math.round((float) myBitmap.getHeight()
							/ (float) myBitmap.getWidth()
							* DataConstants.THUMB_SIZE);
				} else {
					newImageSize[0] = myBitmap.getWidth();
					newImageSize[1] = myBitmap.getHeight();
				}
			} else {
				// 宽小于高
				if (myBitmap.getHeight() > DataConstants.THUMB_SIZE) {
					newImageSize[0] = Math.round((float) myBitmap.getWidth()
							/ (float) myBitmap.getHeight()
							* DataConstants.THUMB_SIZE);
					newImageSize[1] = DataConstants.THUMB_SIZE;
				} else {
					newImageSize[0] = myBitmap.getWidth();
					newImageSize[1] = myBitmap.getHeight();
				}

			}
		}
	}

	/**
	 * 缩放图片尺寸，便于微信上传
	 * 
	 * @param src
	 * @return
	 */
	public static Bitmap scaleBmp(Bitmap src) {
		if (src == null) {
			return null;
		}

		int[] newImgSize = new int[] { 0, 0 };
		getBitmapScaleLenght(src, newImgSize);
		Bitmap thumbBmp = Bitmap.createScaledBitmap(src, newImgSize[0],
				newImgSize[1], true);

		return thumbBmp;
	}

}
