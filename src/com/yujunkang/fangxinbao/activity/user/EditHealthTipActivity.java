package com.yujunkang.fangxinbao.activity.user;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.Tip;
import com.yujunkang.fangxinbao.parser.BaseDataParser;
import com.yujunkang.fangxinbao.parser.TipParser;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.UiUtils;

/**
 * 
 * @date 2014-7-17
 * @author xieb
 * 
 */
public class EditHealthTipActivity extends ActivityWrapper {
	private static final String TAG = "EditHealthTipActivity";
	public static final String INTENT_EXTRA_TIP = DataConstants.PACKAGE_NAME
			+ ".EditHealthTipActivity.INTENT_EXTRA_TIP";
	public static final String INTENT_EXTRA_BABYID = DataConstants.PACKAGE_NAME
			+ ".EditHealthTipActivity.INTENT_EXTRA_BABYID";
	public static final String INTENT_EXTRA_DATE = DataConstants.PACKAGE_NAME
			+ ".EditHealthTipActivity.INTENT_EXTRA_DATE";
	private View btn_save;
	private EditText et_health_tip;
	private TextView tv_edit_tip_title;

	private Tip mTip;
	private boolean isAdd = true;
	private String babyId;
	private Date mTipDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_baby_health_tip_activity);
		initControl();
		ensureUi();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		Intent intent = getIntent();
		if (intent.hasExtra(INTENT_EXTRA_TIP)) {
			isAdd = false;
			mTip = (Tip) intent.getParcelableExtra(INTENT_EXTRA_TIP);
		}
		if (intent.hasExtra(INTENT_EXTRA_BABYID)) {
			babyId = intent.getStringExtra(INTENT_EXTRA_BABYID);
		}
		if (intent.hasExtra(INTENT_EXTRA_DATE)) {
			mTipDate =(Date) intent.getSerializableExtra(INTENT_EXTRA_DATE);
		}
		
	}

	private void initControl() {
		btn_save = findViewById(R.id.btn_save);
		et_health_tip = (EditText) findViewById(R.id.et_health_tip);
		btn_save.setOnClickListener(this);
		tv_edit_tip_title = (TextView) findViewById(R.id.tv_edit_tip_title);
	}

	private void ensureUi() {
		if (isAdd) {
			tv_edit_tip_title.setText(getString(R.string.add_health_tip));
		} else {
			tv_edit_tip_title.setText(getString(R.string.edit_health_tip));
			et_health_tip.setText(mTip.getContent());
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// 编辑备忘录
		case R.id.btn_save: {
			String tip = et_health_tip.getText().toString();
			if(!TextUtils.isEmpty(tip))
			{
				if (isAdd) {
					startAddTipTask();
				} else {
					startEditTipTask();
				}
			}
			break;
		}
		}

	}
	
	
	private void OnUploadTipCompleted(Tip tip)
	{
		Intent intent = new Intent();
		intent.putExtra(INTENT_EXTRA_TIP, tip);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
	
	private void startEditTipTask()
	{
		FangXinBaoAsyncTask<BaseData> mTask = FangXinBaoAsyncTask.createInstance(
				getSelfContext(), UrlManager.URL_EDIT_DAY_TIP, new BaseDataParser(),
				getString(R.string.upload_tip_loading));
		
		mTask.putParameter("cid", mTip.getId());
		mTask.putParameter("comment",
				et_health_tip.getText().toString());
		mTask.setOnFinishedListener(new OnFinishedListener<BaseData>() {
			@Override
			public void onFininshed(BaseData result) {
				if(result != null&&result.code==1)
				{
					mTip.setContent(et_health_tip.getText().toString());
					UiUtils.showAlertDialog(getString(R.string.upload_tip_success), getSelfContext());
					OnUploadTipCompleted(mTip);
				}
				else
				{
					UiUtils.showAlertDialog(getString(R.string.upload_tip_failed), getSelfContext());
				}
			}
		});
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	private void startAddTipTask()
	{
		FangXinBaoAsyncTask<Tip> mTask = FangXinBaoAsyncTask.createInstance(
				getSelfContext(), UrlManager.URL_ADD_DAY_TIP, new TipParser(),
				false);
		Calendar instances = Calendar.getInstance();
		instances.setTime(mTipDate);
		
		instances.set(Calendar.HOUR, 0);
		instances.set(Calendar.MINUTE, 0);
		instances.set(Calendar.SECOND, 0);

		mTask.putParameter("bbid", babyId);
		mTask.putParameter("date",
				String.valueOf(instances.getTime().getTime()/1000));
		mTask.putParameter("comment",
				et_health_tip.getText().toString());
		mTask.setOnFinishedListener(new OnFinishedListener<Tip>() {
			@Override
			public void onFininshed(Tip result) {
				if(result != null&&result.code==1)
				{
					result.setContent(et_health_tip.getText().toString());
					UiUtils.showAlertDialog(getString(R.string.upload_tip_success), getSelfContext());
					OnUploadTipCompleted(result);
				}
				else
				{
					UiUtils.showAlertDialog(getString(R.string.upload_tip_failed), getSelfContext());
				}
			}
		});
		mTask.safeExecute();
		putAsyncTask(mTask);
	}
}
