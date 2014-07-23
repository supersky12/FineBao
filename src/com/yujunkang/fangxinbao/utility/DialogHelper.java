package com.yujunkang.fangxinbao.utility;

import java.util.List;


import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @date 2014-5-31
 * @author xieb
 * 
 */
public class DialogHelper {
	private Context context;
	private Handler handler;
	private LayoutInflater inflater;
	private FangXinBaoApplication mApp;

	public DialogHelper(Context context) {
		this.context = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mApp = FangXinBaoApplication.getApplication(context);
	}

	public DialogHelper(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private Dialog mFromBottomDialog;

	public Dialog showFromBottomDialog(final View contentView,int backgroundResId) {
		if (context instanceof Activity) {
			View dialogRootView = inflater.inflate(R.layout.dialog_container,
					null);
			final LinearLayout lay_content = (LinearLayout) dialogRootView
					.findViewById(R.id.lay_content);

			mFromBottomDialog = new Dialog(context, R.style.NoBorderDialog);
			mFromBottomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mFromBottomDialog.setCanceledOnTouchOutside(true);
			ViewParent parentView = contentView.getParent();
			if (parentView != null) {
				mFromBottomDialog.show();
				return mFromBottomDialog;
			}
			lay_content.addView(contentView);
			mFromBottomDialog.setContentView(dialogRootView);
			mFromBottomDialog.getWindow().setGravity(Gravity.BOTTOM);
			mFromBottomDialog.getWindow().getAttributes().windowAnimations = R.style.TranslateFromBottomAnimation;
			mFromBottomDialog.getWindow().getAttributes().width = LayoutParams.FILL_PARENT;
			if (contentView instanceof ListView) {
				((ListView) contentView).setDividerHeight(0);
				((ListView) contentView).setCacheColorHint(context
						.getResources().getColor(R.color.transparent));
				((ListView) contentView).setSelector(R.drawable.button07_bg);
			}
			mFromBottomDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					lay_content.removeView(contentView);
				}

			});
			mFromBottomDialog.getWindow().setBackgroundDrawable(
					context.getResources().getDrawable(backgroundResId));
			Display display = ((Activity) context).getWindowManager()
					.getDefaultDisplay();
			int widthMeasureSpec = MeasureSpec.makeMeasureSpec(
					display.getWidth(), MeasureSpec.AT_MOST);
			int heightMeasureSpec = MeasureSpec.makeMeasureSpec(
					display.getHeight(), MeasureSpec.AT_MOST);
			contentView.measure(widthMeasureSpec, heightMeasureSpec);

			if (contentView.getMeasuredHeight() > (display.getHeight() / 2)) {
				mFromBottomDialog.getWindow().getAttributes().height = display
						.getHeight() / 2;
			} else {
				mFromBottomDialog.getWindow().getAttributes().height = LayoutParams.WRAP_CONTENT;
			}

			mFromBottomDialog.show();
		}

		return mFromBottomDialog;
	}

	public Dialog showListViewFromBottomDialog(final View contentView,int backgroundResId) {
		
		return showFromBottomDialog(contentView,backgroundResId);
	}

	public Dialog showFromBottomDialog(final View contentView) {
		
		return showFromBottomDialog(contentView,R.drawable.button07_bg);
	}
	
	/**
	 * 在屏幕底部弹出菜单对话框
	 * 
	 */
	public Dialog popButtonListDialogMenu(List<View> btns) {
		LinearLayout contentView = new LinearLayout(context);
		LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		contentParams.setMargins(0, Utils.dip2px(context, 25), 0, 0);
		contentView.setLayoutParams(contentParams);
		contentView.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < btns.size(); i++) {
			View btn = btns.get(i);
			LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			btnParams.setMargins(Utils.dip2px(context, 20), 0, Utils.dip2px(context, 20), Utils.dip2px(context, 10));
			btn.setLayoutParams(btnParams);
			contentView.addView(btn);
		}
		return showFromBottomDialog(contentView,R.drawable.dialog_bottom_bg);
	}

	
	
	private Dialog wheelDialog;

	/**
	 * 从下方弹出对话框
	 * 
	 * @param myView
	 * @return
	 */
	public Dialog showWheelDialog(View myView) {
		if (context instanceof Activity) {
			if (wheelDialog == null) {
				wheelDialog = new Dialog(context, R.style.NoBorderDialog);
				wheelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				wheelDialog.setCanceledOnTouchOutside(true);

				View dialogRootView = inflater.inflate(
						R.layout.dialog_container, null);
				LinearLayout lay_content = (LinearLayout) dialogRootView
						.findViewById(R.id.lay_content);
				lay_content.addView(myView);

				wheelDialog.setContentView(dialogRootView);

				wheelDialog.getWindow().setGravity(Gravity.BOTTOM);
				wheelDialog.getWindow().getAttributes().windowAnimations = R.style.TranslateFromBottomAnimation;
				wheelDialog.getWindow().getAttributes().width = LayoutParams.FILL_PARENT;
			}

			wheelDialog.show();
		}
		return wheelDialog;
	}

	public void setDialogContext(Context ctx) {
		this.context = ctx;
	}

	/**
	 * 用于分享功能中弹出GridView风格菜单
	 * 
	 * @param context
	 * @param gridView
	 * @param titleView
	 * @return
	 */
	public static Dialog popDialogGridMenuBottom(Context context,
			View gridView, View titleView) {
		final Dialog dialog = new Dialog(context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_gridmenu_bottom, null);
		LinearLayout lay_title = (LinearLayout) layout
				.findViewById(R.id.lay_title);
		if (titleView == null) {
			lay_title.setVisibility(View.GONE);
		} else {
			lay_title.setVisibility(View.VISIBLE);
			lay_title.addView(titleView);
		}

		LinearLayout lay_btns = (LinearLayout) layout
				.findViewById(R.id.lay_btns);
		if (gridView != null) {
			lay_btns.addView(gridView);
		}

		dialog.setContentView(layout);
		dialog.setCanceledOnTouchOutside(true);

		dialog.show();

		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);

		return dialog;
	}

	/**
	 * 在屏幕中间显示弹出提示信息
	 */
	public static void showSuccessAlertDialogInCenter(String info,
			Context context, boolean showIcon, int iconResid) {
		if (TextUtils.isEmpty(info)) {
			return;
		}

		LinearLayout contentView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.dialog_success_view, null);
		TextView tv_info = (TextView) contentView.findViewById(R.id.tv_info);
		ImageView iv_icon = (ImageView) contentView.findViewById(R.id.iv_icon);

		if (iconResid != -1) {
			iv_icon.setImageResource(iconResid);
		}
		if (showIcon) {
			iv_icon.setVisibility(View.VISIBLE);
		} else {
			iv_icon.setVisibility(View.GONE);
		}

		tv_info.setText(info);
		if (context instanceof Activity) {
			Activity activity = (Activity) context;
			final int defaultMessageTextColor = context.getResources()
					.getColor(R.color.sdl_message_text_dark);
			final TypedArray a = activity.getTheme().obtainStyledAttributes(
					null, R.styleable.DialogStyle, R.attr.sdlDialogStyle, 0);
			int MessageTextColor = a.getColor(
					R.styleable.DialogStyle_messageTextColor,
					defaultMessageTextColor);
			tv_info.setTextColor(MessageTextColor);
			Drawable dialogBackground = a
					.getDrawable(R.styleable.DialogStyle_dialogBackground);
			a.recycle();
			contentView.setBackground(dialogBackground);
		}

		Toast toastView = new Toast(context);

		toastView.setView(contentView);
		toastView.setDuration(Toast.LENGTH_LONG);
		toastView.setGravity(Gravity.CENTER, 0, 0);
		toastView.show();
	}

}
