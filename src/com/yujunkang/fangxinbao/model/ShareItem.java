package com.yujunkang.fangxinbao.model;



/**
 * 
 * @date 2014-6-14
 * @author xieb
 * 
 */
public class ShareItem implements BaseModel {
	public final static int SHARE_TYPE_SMS= 1;
	public final static int SHARE_TYPE_EMAIL= 2;
	public final static int SHARE_TYPE_SINA_WEIBO= 3;
	public final static int SHARE_TYPE_WEIXIN= 4;
	public final static int SHARE_TYPE_WEIXIN_FRIEND= 5;
	public final static int SHARE_TYPE_COPY= 6;
	public final static int SHARE_TYPE_QQ= 7;
	public int shareType;
	public String shareName = "";//分享菜单名称
	public boolean isEnable = false;//当前
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDesc(String desc) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCode(int code) {
		// TODO Auto-generated method stub

	}

}


