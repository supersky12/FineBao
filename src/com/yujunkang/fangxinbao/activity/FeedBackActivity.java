package com.yujunkang.fangxinbao.activity;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.parser.BaseDataParser;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.UiUtils;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * 
 * @date 2014-7-8
 * @author xieb
 * 
 */
public class FeedBackActivity extends ActivityWrapper {
	private EditText et_feedback;
	private EditText et_userContact;
	private View btn_submit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_activity);
		getViews();
	}

	private void getViews() {
		et_feedback = (EditText) findViewById(R.id.et_feedback);
		et_userContact = (EditText) findViewById(R.id.et_userContact);
		btn_submit = findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_submit: {
			submitFeedBack();
			break;
		}
		}
	}

	private void submitFeedBack() {
		String content = et_feedback.getText().toString();
		String contact = et_userContact.getText().toString();
		if (TextUtils.isEmpty(content)) {
			UiUtils.showAlertDialog(
					getString(R.string.error_empty_feedback_content),
					getSelfContext());
			return;
		}
		FangXinBaoAsyncTask<BaseData> mTask = FangXinBaoAsyncTask
				.createInstance(getSelfContext(), UrlManager.URL_FEEDBACK,
						new BaseDataParser(),
						getString(R.string.submit_feedback_loading));
		mTask.putParameter("text", content.trim());
		mTask.putParameter("contact", contact.trim());
		mTask.setOnFinishedListener(new OnFinishedListener<BaseData>() {
			@Override
			public void onFininshed(BaseData result) {

				if (result == null) {
					UiUtils.showAlertDialog(
							getString(R.string.submit_feedback_failed),
							getSelfContext());
				} else {
					if (result.code == 1) {
						UiUtils.showAlertDialog(
								getString(R.string.submit_feedback_successful),
								getSelfContext());
						finish();
					} else {
						UiUtils.showAlertDialog(!TextUtils.isEmpty(result
								.getDesc()) ? result.getDesc()
								: getString(R.string.submit_feedback_failed),
								getSelfContext());
					}
				}
			}
		});
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

}
