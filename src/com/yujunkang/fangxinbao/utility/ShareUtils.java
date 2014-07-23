package com.yujunkang.fangxinbao.utility;

import java.util.ArrayList;
import java.util.List;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.api.FrontiaSocialShare;
import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaSocialShareListener;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.model.ShareData;
import com.yujunkang.fangxinbao.model.ShareItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 
 * @date 2014-6-14
 * @author xieb
 * 
 */
public class ShareUtils {
	public static List<ShareItem> getShareObjList(Context context, IWXAPI api,
			ShareData shareData) {
		ShareItem item;
		List<ShareItem> shareMenuList = new ArrayList<ShareItem>();

		// 有短信分享菜单
		item = new ShareItem();
		item.shareType = ShareItem.SHARE_TYPE_SMS;
		item.shareName = context.getString(R.string.share_send_sms);
		item.isEnable = true;
		shareMenuList.add(item);

		// 有邮件分享菜单
		// item = new ShareItem();
		// item.shareType = ShareItem.SHARE_TYPE_EMAIL;
		// item.shareName = context.getString(R.string.share_send_email);
		// item.isEnable = true;
		// shareMenuList.add(item);

		// 有分享到新浪微博菜单
		item = new ShareItem();
		item.shareType = ShareItem.SHARE_TYPE_SINA_WEIBO;
		item.shareName = context.getString(R.string.share_sina_weibo);

		item.isEnable = true;

		shareMenuList.add(item);

		// 有分享到微信的菜单
		item = new ShareItem();
		item.shareType = ShareItem.SHARE_TYPE_WEIXIN;
		item.shareName = context.getString(R.string.share_weixin);

		item.isEnable = true;

		shareMenuList.add(item);

		// 有分享到微信的朋友圈菜单
		item = new ShareItem();
		item.shareType = ShareItem.SHARE_TYPE_WEIXIN_FRIEND;
		item.shareName = context.getString(R.string.share_weixin_friendcircle);

		item.isEnable = false;

		shareMenuList.add(item);

		item = new ShareItem();
		item.shareType = ShareItem.SHARE_TYPE_QQ;
		item.shareName = context.getString(R.string.share_qq);
		item.isEnable = true;
		shareMenuList.add(item);

		return shareMenuList;
	}

	public static void showShareContent(Context context, String title,
			String content, String url, Bitmap image,
			FrontiaSocialShareListener shareListener) {

		FrontiaSocialShareContent ImageContent = new FrontiaSocialShareContent();
		FrontiaSocialShare mSocialShare = Frontia.getSocialShare();
		mSocialShare.setContext(context);
		mSocialShare.setClientId(MediaType.SINAWEIBO.toString(),
				DataConstants.SINA_APP_ID);
		mSocialShare.setClientId(MediaType.QZONE.toString(),
				DataConstants.QQ_APP_ID);
		mSocialShare.setClientId(MediaType.QQFRIEND.toString(),
				DataConstants.QQ_APP_ID);
		String appName = context.getString(R.string.app_name);
		mSocialShare.setClientName(MediaType.QQFRIEND.toString(), appName);
		mSocialShare.setClientName(MediaType.QZONE.toString(), appName);
		mSocialShare.setClientName(MediaType.WEIXIN.toString(), appName);
		mSocialShare.setClientId(MediaType.WEIXIN.toString(),
				DataConstants.WEIXIN_APP_ID);
		mSocialShare.setClientId(MediaType.WEIXIN_FRIEND.toString(),
				DataConstants.WEIXIN_APP_ID);
		mSocialShare.setClientName(MediaType.WEIXIN_FRIEND.toString(), appName);
		ImageContent.setTitle(title);
		ImageContent.setContent(content);
		ImageContent.setLinkUrl(DataConstants.APP_WEB_PROTAL);
		if (image == null) {
			Drawable icon = context.getResources().getDrawable(
					R.drawable.ic_launcher);
			image = BitmapUtils.scaleBmp(BitmapUtils.drawableToBitmap(icon));
		}
		ImageContent.setImageData(image);
		Method.showShareDialog(context, null, mSocialShare, ImageContent,
				shareListener);
	}
}
