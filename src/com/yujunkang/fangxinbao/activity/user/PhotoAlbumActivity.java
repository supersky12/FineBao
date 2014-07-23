package com.yujunkang.fangxinbao.activity.user;

import java.io.FileNotFoundException;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.cache.CacheableBitmapDrawable;
import com.yujunkang.fangxinbao.control.NetworkedCacheableImageView;
import com.yujunkang.fangxinbao.control.NetworkedCacheableImageView.OnImageLoadedListener;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.FileUtils;
import com.yujunkang.fangxinbao.utility.ImageResizer;
import com.yujunkang.fangxinbao.utility.LoggerTool;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * 
 * @date 2014-6-2
 * @author xieb
 * 
 */
public class PhotoAlbumActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {
	private static final String TAG = "PhotoAlbumActivity";
	private static final int REQUEST_ACTIVITY_CROP_IMAGE = 1;
	private GridView mGridView;
	private SimpleCursorAdapter simpleCursorAdapter = null;

	private static final String[] STORE_IMAGES = {
			MediaStore.Images.Media.DISPLAY_NAME,
			MediaStore.Images.Media.LATITUDE,
			MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media._ID };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_album_activity);

		mGridView = (GridView) findViewById(R.id.grid_photo);
		simpleCursorAdapter = new SimpleCursorAdapter(this,
				R.layout.photo_album_item, null, STORE_IMAGES,
				new int[] { R.id.iv_photo }, 0);

		simpleCursorAdapter.setViewBinder(new ImageLocationBinder());

		mGridView.setAdapter(simpleCursorAdapter);
		// 注意此处是getSupportLoaderManager()，而不是getLoaderManager()方法。
		getSupportLoaderManager().initLoader(0, null, this);
		// 单击显示图片
		mGridView.setOnItemClickListener(new PhotoItemImageOnClickListener());
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		// 为了查看信息，需要用到CursorLoader。
		CursorLoader cursorLoader = new CursorLoader(this,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES,
				null, null, null);
		return cursorLoader;
	}

	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Cursor cursor=	simpleCursorAdapter.getCursor();
		if(cursor != null&& !cursor.isClosed())
		{
			cursor.close();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		simpleCursorAdapter.swapCursor(null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// 使用swapCursor()方法，以使旧的游标不被关闭．
		simpleCursorAdapter.swapCursor(cursor);
	}

	// 将图片的位置绑定到视图
	private class ImageLocationBinder implements ViewBinder {

		@Override
		public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
			LoggerTool.d(TAG, "setViewValue");

			// 此处arg2代表的是STORE_IMAGES数组的索引
			if (arg2 == 0) {
				long id = arg1.getLong(3);
				Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
						.buildUpon().appendPath(Long.toString(id)).build();

				try {
					final NetworkedCacheableImageView imageView = (NetworkedCacheableImageView) arg0;

					String filePath = FileUtils
							.UriToPath(uri, PhotoAlbumActivity.this);
					imageView.getLayoutParams().height = DataConstants.DEVICE_WIDTH / 3;
					imageView
							.loadImage(filePath, ImageResizer
									.computeSampleSize(filePath,
											DataConstants.DEVICE_WIDTH / 3,
											DataConstants.DEVICE_WIDTH / 3),
									new OnImageLoadedListener() {
										
										@Override
										public void onImageLoaded(CacheableBitmapDrawable result) {
											
											//imageView.getLayoutParams().height = DataConstants.DEVICE_WIDTH / 3;
										}

										@Override
										public void onImageSet(
												CacheableBitmapDrawable result) {
											//imageView.getLayoutParams().height = DataConstants.DEVICE_WIDTH / 3;
											
										}
									}, true);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return true;
			}
			return false;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_ACTIVITY_CROP_IMAGE: {
				setResult(RESULT_OK);
				finish();
				break;
			}

			}

		}
	}

	// 单击项显示图片事件监听器
	private class PhotoItemImageOnClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			LoggerTool.i("info", id + "+++++++id");
			Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
					.appendPath(Long.toString(id)).build();

			try {
				Intent intent = new Intent(PhotoAlbumActivity.this,
						PictureCropActivity.class);
				intent.setDataAndNormalize(uri);
				startActivityForResult(intent, REQUEST_ACTIVITY_CROP_IMAGE);
			} catch (Exception e) {
				LoggerTool.e(TAG, e.getMessage());
			}
		}
	}

}
