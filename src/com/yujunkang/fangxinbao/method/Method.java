package com.yujunkang.fangxinbao.method;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.baidu.frontia.api.FrontiaSocialShare;
import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaSocialShareListener;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yujunkang.fangxinbao.R;

import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.compare.CompareUrlParams;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.model.CommonData;
import com.yujunkang.fangxinbao.model.ShareData;
import com.yujunkang.fangxinbao.model.ShareItem;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.BitmapUtils;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DialogHelper;
import com.yujunkang.fangxinbao.utility.JniEncrypt;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.MD5;
import com.yujunkang.fangxinbao.utility.ShareUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class Method {
	static final String TAG = "Method";

	public static String getSource(Context context) {
		String source = "";
		// 读取assert中的渠道文件配置
		InputStream inputStream = null;
		InputStreamReader inputReader = null;
		BufferedReader bReader = null;
		try {
			inputStream = context.getAssets().open("channel.cfg");
			inputReader = new InputStreamReader(inputStream, "UTF-8");
			bReader = new BufferedReader(inputReader);
			/*
			 * FileWriter fw = new FileWriter(data); String line = ""; while
			 * ((line = bReader.readLine()) != null) { fw.write(line); }
			 */
			source = bReader.readLine().split("=")[1];
		} catch (Exception ex) {
			//
		} finally {
			//
			try {
				bReader.close();
				inputReader.close();
			} catch (IOException ex) {
				//
			}
		}

		return source;
	}

	/**
	 * 取得uid参数
	 */
	public static String getUid(Context context) {
		String uid = "";
		try {
			uid = Preferences.getString(context,
					Preferences.FILE_PREFERENCE_COMMON_SETTING,
					Preferences.PREFERENCE_COMMON_UID, "");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return uid;
	}

	/**
	 * 保存uid参数
	 */
	public static void storeUid(Context context, String uid) {

		try {
			Preferences.setString(context,
					Preferences.FILE_PREFERENCE_COMMON_SETTING,
					Preferences.PREFERENCE_COMMON_UID, uid);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}

	/**
	 * 取得dverc参数
	 */
	public static String getDverc(Context context) {
		String dverc = "";
		try {
			dverc = Preferences.getString(context,
					Preferences.FILE_PREFERENCE_COMMON_SETTING,
					Preferences.PREFERENCE_COMMON_DVERC, "0.0");
			// dverc = "0.0";
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return dverc;
	}

	/**
	 * 保存dverc参数(国家)
	 */
	public static void storeDverc(Context context, String version) {
		try {
			Preferences.setString(context,
					Preferences.FILE_PREFERENCE_COMMON_SETTING,
					Preferences.PREFERENCE_COMMON_DVERC, version);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}

	/**
	 * 取得温度参数
	 */
	public static String getDvert(Context context) {

		String dvert = "";
		try {
			dvert = Preferences.getString(context,
					Preferences.FILE_PREFERENCE_COMMON_SETTING,
					Preferences.PREFERENCE_COMMON_DVERT, "0.0");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return dvert;
	}

	/**
	 * 取得短信发送号码
	 */
	public static String getSmsAddress(Context context) {

		String smsAddress = "";
		try {
			smsAddress = Preferences.getString(context,
					Preferences.FILE_PREFERENCE_COMMON_SETTING,
					Preferences.PREFERENCE_SMS_ADDRESS,
					DataConstants.SMS_ADDRESS);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return smsAddress;
	}

	/**
	 * 保存短信发送号码
	 */
	public static void storeSmsAddress(Context context, String value) {
		try {
			Preferences.setString(context,
					Preferences.FILE_PREFERENCE_COMMON_SETTING,
					Preferences.PREFERENCE_SMS_ADDRESS, value);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}

	/**
	 * 保存dvert参数(温度)
	 */
	public static void storeDvert(Context context, String version) {
		try {
			Preferences.setString(context,
					Preferences.FILE_PREFERENCE_COMMON_SETTING,
					Preferences.PREFERENCE_COMMON_DVERT, version);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}

	/**
	 * 取得imei参数
	 */
	public static String getImei(Context context) {
		String imei = "";
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			if (TextUtils.isEmpty(deviceid) || deviceid.contains("unknown")) {
				// 取不到imei或imei不正确时，用androidId替代
				deviceid = "aid"
						+ Secure.getString(context.getContentResolver(),
								Secure.ANDROID_ID);
			}
			imei = java.net.URLEncoder.encode(deviceid + "", "UTF-8");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return imei;
	}

	/**
	 * 取得sid参数
	 */
	public static String getSid(Context context, List<NameValuePair> params) {

		try {
			Collections.sort(params, new CompareUrlParams());
			int dataCount = params.size();
			String[] keys = new String[dataCount];
			for (int index = 0; index < dataCount; index++) {
				keys[index] = params.get(index).getValue();
			}
			String csid = JniEncrypt.getInstance(context)
					.getEncryptString(keys);
			LoggerTool.d(TAG, csid);
			return csid;

		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return null;
	}

	private String getVersionName(Context context) {

		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo;
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {

		}
		return "";
	}

	/**
	 * 是中文还是英文
	 */
	public static String getIszh(Context context) {
		return "1";
	}

	public static String getTimeStamp(Context context) {
		long now = System.currentTimeMillis();
		FangXinBaoApplication app = FangXinBaoApplication
				.getApplication(context);
		long result = now + app.getTimeStampOffset();
		return String.valueOf(result);
	}

	/**
	 * 
	 * @param context
	 * @param version
	 * @param data
	 */
	public static void updateCommonData(Context context, CommonData data) {
		try {
			boolean success = false;
			// 保存国家数据
			if (data.getCountries() != null && data.getCountries().size() > 0) {
				DBHelper databaseHelper = DBHelper.getDBInstance(context);
				success = databaseHelper
						.batchInsertCountry(data.getCountries());
				if (success) {
					// 保存国家数据版本号
					storeDverc(context, data.getCountryVersion());
				}
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}

	}

	/**
	 * 保存温度基础数据
	 * 
	 * @param context
	 * @param version
	 * @param data
	 */
	public static void updateTemperatureCommonData(Context context,
			String data, String version) {
		try {
			// 保存温度数据
			if (!TextUtils.isEmpty(data)) {
				Preferences.setString(context,
						Preferences.FILE_PREFERENCE_COMMON_SETTING,
						Preferences.PREFERENCE_TEMPERATURE_COMMON_DATA, data);
				if (!TextUtils.isEmpty(version)) {
					storeDvert(context, version);
				}
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}

	}

	/**
	 * 分享公共对话框
	 * 
	 * @param context
	 * @param shareMenuList
	 * @param shareData
	 */
	public static void showShareDialog(final Context context,
			final ShareData shareData, final FrontiaSocialShare socialShare,
			final FrontiaSocialShareContent mImageContent,
			final FrontiaSocialShareListener shareListener) {

		final IWXAPI api = WXAPIFactory.createWXAPI(context,
				DataConstants.APP_ID, true);
		api.registerApp(DataConstants.APP_ID);

		final List<ShareItem> shareMenuList = ShareUtils.getShareObjList(
				context, api, shareData);
		if (shareMenuList.size() == 0) {
			return;
		}

		View gridMenuView = View
				.inflate(context, R.layout.share_gridview, null);
		GridView menuGrid = (GridView) gridMenuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getShareMenuAdapter(context, shareMenuList));
		final Dialog shareDialog = DialogHelper.popDialogGridMenuBottom(
				context, gridMenuView, null);
		menuGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ShareItem myObj = shareMenuList.get(position);
				if (myObj.shareType == ShareItem.SHARE_TYPE_SINA_WEIBO) {
					socialShare.share(mImageContent,
							MediaType.SINAWEIBO.toString(), shareListener, true);
				} else if (myObj.shareType == ShareItem.SHARE_TYPE_QQ) {
					socialShare.share(mImageContent,
							MediaType.QQFRIEND.toString(), shareListener, true);
				} else if (myObj.shareType == ShareItem.SHARE_TYPE_SMS) {
					socialShare.share(mImageContent, MediaType.SMS.toString(),
							shareListener, true);
				} else if (myObj.shareType == ShareItem.SHARE_TYPE_EMAIL) {
					socialShare.share(mImageContent,
							MediaType.EMAIL.toString(), shareListener, true);
				} else if (myObj.shareType == ShareItem.SHARE_TYPE_WEIXIN) {
					socialShare.share(mImageContent,
							MediaType.WEIXIN.toString(), shareListener, true);
					// 2种方式都可以用
					// final IWXAPI api = WXAPIFactory.createWXAPI(context,
					// DataConstants.WEIXIN_APP_ID, true);
					// api.registerApp(DataConstants.WEIXIN_APP_ID);
					// try {
					// if (api.isWXAppInstalled()) {
					// WXMediaMessage msg = null;
					// if (shareData.getWeixinApiType() == ShareData.WEBPAGE) {
					// WXWebpageObject webpage = new WXWebpageObject();
					// if (!TextUtils.isEmpty(shareData.getWeixinUrl())) {
					// webpage.webpageUrl = shareData
					// .getWeixinUrl();
					// } else {
					// webpage.webpageUrl = "www.baidu.com";
					// }
					// msg = new WXMediaMessage(webpage);
					// } else if (shareData.getWeixinApiType() ==
					// ShareData.IMAGE) {
					// WXImageObject image = new WXImageObject();
					// if (shareData.getWeixinBytes() != null) {
					// image.imageData = shareData
					// .getWeixinBytes();
					// } else if (shareData.getWeixinImg() != null) {
					// image.imageData = BitmapUtils
					// .bmpToByteArray(shareData
					// .getWeixinImg());
					// }
					//
					// if (!TextUtils.isEmpty(shareData.getWeixinUrl())) {
					// image.imageUrl = "www.baidu.com";
					// }
					//
					// msg = new WXMediaMessage(image);
					// }
					//
					// if (msg != null) {
					// if (!TextUtils.isEmpty(shareData.getWeixinMsg())) {
					// msg.description = shareData.getWeixinMsg();
					// }
					//
					// if (!TextUtils.isEmpty(shareData
					// .getWeixinTitle())) {
					// msg.title = shareData.getWeixinTitle();
					// }
					//
					// if (shareData.getWeixinBytes() != null) {
					// msg.thumbData = shareData.getWeixinBytes();
					// } else if (shareData.getWeixinImg() != null) {
					// Bitmap scaledImg = BitmapUtils
					// .scaleBmp(shareData.getWeixinImg());
					// msg.thumbData = BitmapUtils
					// .bmpToByteArray(scaledImg);
					// } else {
					// Drawable icon = context
					// .getResources()
					// .getDrawable(R.drawable.ic_launcher);
					// Bitmap scaledImg = BitmapUtils
					// .scaleBmp(BitmapUtils
					// .drawableToBitmap(icon));
					// msg.thumbData = BitmapUtils
					// .bmpToByteArray(scaledImg);
					// }
					// }
					// SendMessageToWX.Req req = new SendMessageToWX.Req();
					// req.transaction = "webpage"
					// + System.currentTimeMillis();
					// req.message = msg;
					// api.sendReq(req);
					// }
					// } catch (Exception e) {
					// LoggerTool.e("SearchDetail_Adapter", "", e);
					// }

				} else if (myObj.shareType == ShareItem.SHARE_TYPE_WEIXIN_FRIEND) {
					socialShare.share(mImageContent,
							MediaType.WEIXIN_FRIEND.toString(), shareListener,
							true);
				}
				shareDialog.dismiss();
			}
		});

	}

	public static SimpleAdapter getShareMenuAdapter(Context context,
			List<ShareItem> menuList) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (ShareItem item : menuList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", getShareImageResourceId(item.shareType));
			map.put("itemText", item.shareName);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(context, data,
				R.layout.share_grid_menu_item, new String[] { "itemImage",
						"itemText" }, new int[] { R.id.item_image,
						R.id.item_text });
		return simperAdapter;
	}

	/**
	 * 按分享菜单名返回对应资源图片id
	 * 
	 * @param shareName
	 * @return
	 */
	private static int getShareImageResourceId(int sharetype) {
		int resourceId = -1;
		switch (sharetype) {
		case ShareItem.SHARE_TYPE_QQ: {
			resourceId = R.drawable.icon_share_qq;
			break;
		}
		case ShareItem.SHARE_TYPE_SINA_WEIBO: {
			resourceId = R.drawable.icon_share_sina;
			break;
		}
		case ShareItem.SHARE_TYPE_SMS: {
			resourceId = R.drawable.icon_share_sms;
			break;
		}
		case ShareItem.SHARE_TYPE_WEIXIN: {
			resourceId = R.drawable.icon_share_weixin;
			break;
		}
		case ShareItem.SHARE_TYPE_WEIXIN_FRIEND: {
			resourceId = R.drawable.icon_share_weixin_friend;
			break;
		}
		}

		return resourceId;
	}

	/**
	 * 判断微信版本是否大于指定版本数，以此来判断是否能够使用朋友圈功能
	 * 
	 * @param weixinVer
	 * @return
	 */
	public static boolean friendIsEnable(int weixinVer) {
		boolean result = false;
		if (weixinVer > DataConstants.WEIXIN_FRIEND_VER) {
			result = true;
		}
		return result;
	}

}
