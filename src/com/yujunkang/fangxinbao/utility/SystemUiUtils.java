package com.yujunkang.fangxinbao.utility;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * 
 * @date 2014-6-1
 * @author xieb
 * 
 */
public class SystemUiUtils {
	public static Intent getPhotoAlbumActivity() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		intent.putExtra("return-data", true);
		return intent;

	}

	public static Intent getCaremaActivity(String outputPath) {

		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (!TextUtils.isEmpty(outputPath)) {
			Uri uri = Uri.fromFile(new File(outputPath));
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		}
		else
		{
			intent.addCategory(Intent.CATEGORY_DEFAULT);  
			intent.putExtra("return-data", true);
		}
		return intent;
	}

}
