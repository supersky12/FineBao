/**
 * Copyright 2010 xieb
 */
package com.yujunkang.fangxinbao.utility;



import android.content.Context;

/**
 * @author xieb (xieb@huoli.com)
 * @date 2014-6-2
 */
public class JniEncrypt {
	private Context mContext;
	private static JniEncrypt _instance;

	public native String getEncryptString(String[] params);

	public JniEncrypt(Context context) {
		mContext = context;
	}

	public static synchronized JniEncrypt getInstance(Context ctx) {
		if (_instance == null) {
			_instance = new JniEncrypt(ctx);
		}
		return _instance;
	}
	
	static {
		System.loadLibrary("JniEncrypt");
		}

}
