package com.yujunkang.fangxinbao.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.baidu.frontia.activity.share.FrontiaWeixinShareActivity;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;

/**
 * 
 * @date 2014-7-8
 * @author xieb
 * 
 */
public class WXEntryActivity extends FrontiaWeixinShareActivity {
	private static final String TAG = "WXEntryActivity";
	@Override
	protected boolean handleIntent() {
		if (super.handleIntent()) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
