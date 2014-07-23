package com.yujunkang.fangxinbao.utility;


import com.yujunkang.fangxinbao.app.FangXinBaoSettings;



/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class LoggerTool {
	public static int LEVEL = android.util.Log.VERBOSE;
	static {
		if (FangXinBaoSettings.DEBUG) {
			LEVEL = android.util.Log.VERBOSE;
		} else {
			LEVEL = android.util.Log.ERROR;
		}
	}
	
	
	public static void v(String msg) {
		v(getCallerName(),msg);
		
	}
	
	public static void d(String msg) {
		d(getCallerName(),msg);
		
	}
	public static void d(String format, Object... args) {
		d(getCallerName(), String.format(format, args));
	}

	public static void i(String msg) {
		i(getCallerName(),msg);
		
	}
	
	public static void e(String msg) {
		e(getCallerName(),msg);
		
	}
	
	public static void v(String tag, String msg) {
		if (LEVEL <= android.util.Log.VERBOSE) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (LEVEL <= android.util.Log.VERBOSE) {
			android.util.Log.v(tag, msg, tr);
		}
	}

	public static void d(String tag, String msg) {
		if (LEVEL <= android.util.Log.DEBUG) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (LEVEL <= android.util.Log.DEBUG) {
			android.util.Log.d(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (LEVEL <= android.util.Log.INFO) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (LEVEL <= android.util.Log.INFO) {
			android.util.Log.i(tag, msg, tr);
		}
	}

	public static void w(String tag, String msg) {
		if (LEVEL <= android.util.Log.WARN) {
			android.util.Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (LEVEL <= android.util.Log.WARN) {
			android.util.Log.w(tag, msg, tr);
		}
	}

	public static void w(String tag, Throwable tr) {
		if (LEVEL <= android.util.Log.WARN) {
			android.util.Log.w(tag, tr);
		}
	}

	public static void e(String tag, String msg) {
		if (LEVEL < android.util.Log.ERROR) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (LEVEL < android.util.Log.ERROR) {
			android.util.Log.e(tag, msg, tr);
		}
	}
	
	/*
	 * 获取调用者信息
	 */
	private static String getCallerName() {
		StackTraceElement stack[] = (new Throwable()).getStackTrace();
		int i = 0;
		while (i < stack.length) {
			StackTraceElement frame = stack[i];
			
			String className = frame.getClassName();
			String methodName = frame.getMethodName();
			
			if (!className.equals("com.flightmanager.utility.method.LoggerTool")) {
				return String.format("%s[%s]", className, methodName);
			}
			i++;
		}
		return "";
	}
}


