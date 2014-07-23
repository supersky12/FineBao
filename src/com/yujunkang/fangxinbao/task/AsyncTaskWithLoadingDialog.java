package com.yujunkang.fangxinbao.task;

import java.util.concurrent.RejectedExecutionException;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.control.dialog.ISimpleDialogCancelListener;
import com.yujunkang.fangxinbao.control.dialog.ProgressDialogFragment;
import com.yujunkang.fangxinbao.control.dialog.ProgressDialogFragment.ProgressDialogBuilder;
import com.yujunkang.fangxinbao.utility.SDK11;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

/**
 * 
 * @date 2014-5-22
 * @author xieb
 * 
 */
public class AsyncTaskWithLoadingDialog<Params, Progress, Result> extends
		AsyncTaskWrapper<Params, Progress, Result> implements
		ISimpleDialogCancelListener {
	private final static String TAG = "AsyncTaskWithLoadingDialog";
	private final static int WHAT_SET_LOADING_TITLE = 0x000001;
	private final static int REQUEST_PROGRESS = 1;
	private ProgressDialogBuilder builder;
	private boolean _enableWaitDesc;
	private Handler _uiHandler;
	private Activity _context;
	private ProgressDialogFragment dialogFragment;
	/*
	 * 加载文字
	 */
	protected String _waitDesc;

	public AsyncTaskWithLoadingDialog(Activity context) {
		this(context, context != null ? context.getString(R.string.loading)
				: "");
	}

	public AsyncTaskWithLoadingDialog(boolean dialogEnable) {
		this(null, null, true, dialogEnable);
	}

	public AsyncTaskWithLoadingDialog(Activity context, String title) {
		this(context, title, true, true);
	}

	public AsyncTaskWithLoadingDialog(Activity context, boolean dialogEnable) {
		this(context, context != null ? context.getString(R.string.loading)
				: "", true, dialogEnable);
	}

	public AsyncTaskWithLoadingDialog(Activity context, String title,
			boolean isCancelable) {
		this(context, title, isCancelable, true);
	}

	/**
	 * 带等待框的异步任务
	 * 
	 * @param 如果带等待框
	 *            ，此处Context必须为Activity
	 * @param 等待框内容
	 * @param 是否可以取消等待框
	 * @param 是否有等待框
	 */
	public AsyncTaskWithLoadingDialog(Activity context, String waitDesc,
			boolean isCancelable, boolean enableWaitDesc) {

		_enableWaitDesc = enableWaitDesc;
		_waitDesc = waitDesc;
		_context = context;

		// 如果在非UI线程上创建，直接return
		if (Looper.getMainLooper() != Looper.myLooper()) {
			return;
		}

		if (!_enableWaitDesc) {
			return;
		}

		_uiHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if (WHAT_SET_LOADING_TITLE == msg.what && builder != null) {
					builder.setMessage(_waitDesc);
				}
				return false;
			}
		});
		if (context != null && context instanceof FragmentActivity) {

			builder = ProgressDialogFragment.createBuilder(context,
					((FragmentActivity) context).getSupportFragmentManager());
			builder.setMessage(waitDesc);
			builder.setRequestCode(REQUEST_PROGRESS);
			builder.setCancelable(isCancelable);
			builder.setCancelableOnTouchOutside(false);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (_enableWaitDesc && builder != null && _context != null
				&& _context instanceof FragmentActivity) {
			Activity win = (Activity) _context;
			if (!win.isFinishing()) {
				dialogFragment = (ProgressDialogFragment) builder.show();
			}
		}

	}

	/*
	 * 安全执行，避免被cancel了再掉execute
	 */
	public void safeExecute(Params... params) {
		if (!isCancelled()) {
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					SDK11.executeOnThreadPool(this, params);
				} else {
					execute(params);
				}
			} catch (RejectedExecutionException e) {
				// This shouldn't happen, but might.
			}

		}
	}

	public void setWaitDesc(String title) {
		_waitDesc = title;

		if (_uiHandler != null) {
			_uiHandler.sendEmptyMessage(WHAT_SET_LOADING_TITLE);
		}
	}

	public void setEnableWaitIndicator(boolean enable) {
		_enableWaitDesc = enable;
	}

	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		if (_enableWaitDesc && builder != null && dialogFragment != null) {

			if (dialogFragment.isVisible() && _context != null
					&& _context instanceof FragmentActivity) {
				Activity win = (Activity) _context;
				if (!win.isFinishing()) {
					dialogFragment.dismiss();
				}
			}
		}
		cancel(true);

	}

	// 如果是调用的cacel函数 那么得关闭dialog
	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (_enableWaitDesc) {
			if (dialogFragment != null && dialogFragment.isVisible()) {
				dialogFragment.dismiss();
			}
		}
	}

	@Override
	public void onCancelled(int requestCode) {
		if (requestCode == REQUEST_PROGRESS) {
			if (!isCancelled()) {
				cancel(true);
			}
		}

	}

}
