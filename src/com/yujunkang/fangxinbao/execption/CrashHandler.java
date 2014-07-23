package com.yujunkang.fangxinbao.execption;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.app.FangXinBaoSettings;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;





/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";
	public static final boolean DEBUG = FangXinBaoSettings.DEBUG;
	private static CrashHandler _instance;
	private Context _ctx;
	private static final ExecutorService mExecutor = Executors.newFixedThreadPool(2);
	private StringBuffer _log;
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";
	
	private CrashHandler(Context ctx) {
		_ctx = ctx;
	}

	public static CrashHandler getInstance(Context ctx) {
		if (_instance == null)
			_instance = new CrashHandler(ctx);
		return _instance;
	}
	
	public void init() {
		Thread.setDefaultUncaughtExceptionHandler(this);
		Thread.currentThread().setUncaughtExceptionHandler(this);
	}
	
	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		
		// 处理异常：上传异常
		handleException(ex);
		
		// 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Log.e(TAG, "Error : ", e);
		}
		
		// 杀掉进程
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}

	/*
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 */
	private void handleException(Throwable ex) {
		if (ex == null) {
			return;
		}
		
		_log = new StringBuffer();
		
		// 收集设备信息
		collectCrashDeviceInfo(_ctx);
		
		//收集出现问题的界面
		collectCrashActivity();
		
		// 保存错误报告文件
		collectCrashInfo(ex);
		
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				doForeground();
				Looper.loop();
			}
		}.start();
	}
	
	private void collectCrashActivity() {
		try {
			_log.append(String.format("==%s==\n%s\n", "onCreateActivity", 
					((ActivityWrapper.onCreateActivity == null) ? "null" : ActivityWrapper.onCreateActivity.getClass().toString())));
			_log.append(String.format("==%s==\n%s\n", "onResumeActivity", 
					((ActivityWrapper.onResumeActivity == null) ? "null" : ActivityWrapper.onResumeActivity.getClass().toString())));
		} catch (Throwable t) {
			
		}
	}
	/*
	 * 避免
	 */
	private void doForeground() {
		Toast.makeText(_ctx, "抱歉，出错了!", Toast.LENGTH_LONG).show();
		
		// 发送错误消息
		sendCrashReportsToServer(_ctx);
	}

	/*
	 * 收集程序崩溃的设备信息
	 */
	public void collectCrashDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			
			if (pi != null) {
				_log.append(String.format("==%s==\n%s\n", VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName));
				_log.append(String.format("==%s==\n%s\n", VERSION_CODE, String.valueOf(pi.versionCode)));
			}
			
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
		}
		
		//将设备的标示存储
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				_log.append(String.format("==%s==\n%s\n", field.getName(), field.get(null) == null ? "" : field.get(null).toString()));
				
				if (DEBUG) {
					Log.d(TAG, field.getName() + " : " + field.get(null));
				}
			} catch (Exception e) {
				Log.e(TAG, "Error while collect crash info", e);
			}
		}
	}
	
	/*
	 * 收集堆栈
	 */
	private void collectCrashInfo(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		String result = info.toString();
		printWriter.close();
		_log.append(String.format("==%s==\n%s\n", STACK_TRACE, result));
	}
	
	/*
	 * 把错误报告发送给服务器,包含新产生的和以前没发送的.
	 */
	private void sendCrashReportsToServer(Context ctx) {
		
		if (_log != null && !TextUtils.isEmpty(_log.toString())) {
			//Logger.eGTGJ(_log.toString());
			
			logToServer(_log.toString());
		}
	}

	/**
	 * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
	 */
	public void sendPreviousReportsToServer() {
		sendCrashReportsToServer(_ctx);
	}
	
	/*
	 * 将log发送到服务器
	 */
	public void logToServer(String log) {
		if (!TextUtils.isEmpty(log)) {
			
			
		}
	}
}


