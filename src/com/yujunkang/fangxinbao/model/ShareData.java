package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @date 2014-6-14
 * @author xieb
 * 
 */
public class ShareData extends BaseData implements Parcelable {
	public static final int WEBPAGE = 0;
	public static final int IMAGE = 1;
	public static final int TEXT = 2;
	public static final int FILE = 3;
	public static final int MUSIC = 4;
	public static final int VIDEO = 5;

	public String shareIcon = "";

	// 微信相关数据
	private String weixinTitle = "";
	private String weixinMsg = "";
	private String weixinUrl = "";
	private String weixinIcon = "";
	private Bitmap weixinImg = null;
	private byte[] weixinBytes = null;
	private int weixinApiType;

	// 朋友圈相关数据
	private String friendcircleTitle = "";
	private String friendcircleMsg = "";
	private String friendcircleUrl = "";
	private String friendcircleIcon = "";
	private Bitmap friendcircleImg = null;
	private byte[] friendcircleBytes = null;
	private int friendcircleApiType;

	// 微博相关数据
	private String weiboText = "";
	private Bitmap weiboImg = null;
	private byte[] weiboBytes = null;

	// 短信相关数据
	private String smsText = "";

	// 邮件相关数据
	private String mailSubject = "";
	private String mailContent = "";
	private String mailFilePath = null;
	private Bitmap mailImg = null;

	public String getShareIcon() {
		return shareIcon;
	}

	public void setShareIcon(String shareIcon) {
		this.shareIcon = shareIcon;
	}

	public String getWeixinTitle() {
		return weixinTitle;
	}

	public void setWeixinTitle(String weixinTitle) {
		this.weixinTitle = weixinTitle;
	}

	public String getWeixinMsg() {
		return weixinMsg;
	}

	public void setWeixinMsg(String weixinMsg) {
		this.weixinMsg = weixinMsg;
	}

	public String getWeixinUrl() {
		return weixinUrl;
	}

	public void setWeixinUrl(String weixinUrl) {
		this.weixinUrl = weixinUrl;
	}

	public String getWeixinIcon() {
		return weixinIcon;
	}

	public void setWeixinIcon(String weixinIcon) {
		this.weixinIcon = weixinIcon;
	}

	public Bitmap getWeixinImg() {
		return weixinImg;
	}

	public void setWeixinImg(Bitmap weixinImg) {
		this.weixinImg = weixinImg;
	}

	public byte[] getWeixinBytes() {
		return weixinBytes;
	}

	public void setWeixinBytes(byte[] weixinBytes) {
		this.weixinBytes = weixinBytes;
	}

	public int getWeixinApiType() {
		return weixinApiType;
	}

	public void setWeixinApiType(int weixinApiType) {
		this.weixinApiType = weixinApiType;
	}

	public String getFriendcircleTitle() {
		return friendcircleTitle;
	}

	public void setFriendcircleTitle(String friendcircleTitle) {
		this.friendcircleTitle = friendcircleTitle;
	}

	public String getFriendcircleMsg() {
		return friendcircleMsg;
	}

	public void setFriendcircleMsg(String friendcircleMsg) {
		this.friendcircleMsg = friendcircleMsg;
	}

	public String getFriendcircleUrl() {
		return friendcircleUrl;
	}

	public void setFriendcircleUrl(String friendcircleUrl) {
		this.friendcircleUrl = friendcircleUrl;
	}

	public String getFriendcircleIcon() {
		return friendcircleIcon;
	}

	public void setFriendcircleIcon(String friendcircleIcon) {
		this.friendcircleIcon = friendcircleIcon;
	}

	public Bitmap getFriendcircleImg() {
		return friendcircleImg;
	}

	public void setFriendcircleImg(Bitmap friendcircleImg) {
		this.friendcircleImg = friendcircleImg;
	}

	public byte[] getFriendcircleBytes() {
		return friendcircleBytes;
	}

	public void setFriendcircleBytes(byte[] friendcircleBytes) {
		this.friendcircleBytes = friendcircleBytes;
	}

	public int getFriendcircleApiType() {
		return friendcircleApiType;
	}

	public void setFriendcircleApiType(int friendcircleApiType) {
		this.friendcircleApiType = friendcircleApiType;
	}

	public String getWeiboText() {
		return weiboText;
	}

	public void setWeiboText(String weiboText) {
		this.weiboText = weiboText;
	}

	public Bitmap getWeiboImg() {
		return weiboImg;
	}

	public void setWeiboImg(Bitmap weiboImg) {
		this.weiboImg = weiboImg;
	}

	public byte[] getWeiboBytes() {
		return weiboBytes;
	}

	public void setWeiboBytes(byte[] weiboBytes) {
		this.weiboBytes = weiboBytes;
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public String getMailFilePath() {
		return mailFilePath;
	}

	public void setMailFilePath(String mailFilePath) {
		this.mailFilePath = mailFilePath;
	}

	public Bitmap getMailImg() {
		return mailImg;
	}

	public void setMailImg(Bitmap mailImg) {
		this.mailImg = mailImg;
	}

	private ShareData(Parcel in) {

	}

	public ShareData() {
		// TODO Auto-generated constructor stub
	}

	public static final Creator<ShareData> CREATOR = new Parcelable.Creator<ShareData>() {
		public ShareData createFromParcel(Parcel in) {
			return new ShareData(in);
		}

		@Override
		public ShareData[] newArray(int size) {
			return new ShareData[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
