package com.yujunkang.fangxinbao.activity.user;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.app.NetWorKManager;
import com.yujunkang.fangxinbao.cache.CacheableBitmapDrawable;
import com.yujunkang.fangxinbao.control.image.RoundedNetWorkImageView;
import com.yujunkang.fangxinbao.control.image.RoundedNetWorkImageView.OnImageLoadedListener;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.Sex;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.BabyParser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.SystemUiUtils;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.UserUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.widget.adapter.SexListAdapter;

/**
 * 
 * @date 2014-6-10
 * @author xieb
 * 
 */
public class UserBabyInfoActivity extends ActivityWrapper {
	private static final String TAG = "UserBabyInfoActivity";

	public static final String INTENT_EXTRA_BABY = DataConstants.PACKAGE_NAME
			+ ".UserBabyInfoActivity.INTENT_EXTRA_BABY";
	private static final int DATA_PICKER_ID = 1;
	private static final int ACTIVITY_REQUEST_CODE_GALLERY = 2;
	private static final int ACTIVITY_REQUEST_CODE_CAREMA = 3;

	private static final int ACTIVITY_REQUEST_CROP_IMAGE = 4;
	private static final int ACTIVITY_REQUEST_EDIT_BABY_NICKNAME = 5;
	private static final int ACTIVITY_REQUEST_CODE_EDIT_BABYINFO = 6;
	/**
	 * 控件
	 */
	private View btn_change_photo;
	private View btn_choose_other;
	private View btn_choose_born;
	private TextView tv_baby_born;
	private RoundedNetWorkImageView iv_baby_photo;
	private View btn_change_nickname;
	private TextView tv_baby_nickname;
	private View btn_choose_sex;
	private TextView tv_baby_sex;
	private Dialog listViewDialog;
	private ListView lv_sex;
	private SexListAdapter mSexListAdapter;
	private MenuAdapter mMenuAdapter;
	private ListView lv_menu;
	private TextView tv_baby_nickname_title;
	private Dialog mBabyManagerDialog;

	private StateHolder mStateHolder = new StateHolder();
	private Sex mSelectedSex;
	private String[] menuList = new String[2];
	private int pickPhotoMenuType = 0;
	private User mUserInfo;
	private Baby currentBaby;
	private String cameraFileName = null;

	private OnItemClickListener pickUpPhotoListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (listViewDialog != null) {
				listViewDialog.dismiss();
			}
			pickPhotoMenuType = position - lv_menu.getHeaderViewsCount();
			// 拍照
			if (pickPhotoMenuType == 0) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 没有插SD卡则无法拍照
					UiUtils.showAlertDialog(
							getString(R.string.external_storage_unmounted),
							getSelfContext());
				} else {

					cameraFileName = mFileCache
							.getDiskCacheFilePath(PictureCropActivity.CROPPER_FILE_NAME);
					try {

						startActivityForResult(
								SystemUiUtils.getCaremaActivity(cameraFileName),
								ACTIVITY_REQUEST_CODE_CAREMA);
					} catch (Exception e) {
						LoggerTool.e(TAG, e.getMessage());
						UiUtils.showAlertDialog(
								getString(R.string.save_imagefile_path_error),
								getSelfContext());
					}
				}
			}
			// 从相册获取
			else if (pickPhotoMenuType == 1) {
				Intent intent = new Intent(getSelfContext(),
						PhotoAlbumActivity.class);
				startActivityForResult(intent, ACTIVITY_REQUEST_CODE_GALLERY);
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_baby_info_activity);
		init();
		initControl();
		ensureUi();
	}

	@Override
	protected void initData() {
		super.initData();
		Intent data = getIntent();

		if (data.hasExtra(UserMainActivity.INTENT_EXTRA_USER)) {
			mUserInfo = data
					.getParcelableExtra(UserMainActivity.INTENT_EXTRA_USER);
		}
		if (data.hasExtra(INTENT_EXTRA_BABY)) {
			currentBaby = data.getParcelableExtra(INTENT_EXTRA_BABY);
		}
	}

	private void init() {
		mSelectedSex = DataConstants.SexList.get(0);
		menuList = getResources().getStringArray(R.array.choosephotos);
	}

	private void initControl() {
		tv_baby_nickname_title = (TextView) findViewById(R.id.tv_baby_nickname_title);
		btn_change_photo = findViewById(R.id.btn_change_photo);
		btn_choose_other = findViewById(R.id.btn_choose_other);
		btn_choose_born = findViewById(R.id.btn_choose_born);
		tv_baby_born = (TextView) findViewById(R.id.tv_baby_born);
		iv_baby_photo = (RoundedNetWorkImageView) findViewById(R.id.iv_baby_photo);
		btn_change_nickname = findViewById(R.id.btn_change_nickname);
		tv_baby_nickname = (TextView) findViewById(R.id.tv_baby_nickname);
		btn_choose_sex = findViewById(R.id.btn_choose_sex);
		tv_baby_sex = (TextView) findViewById(R.id.tv_baby_sex);

		btn_change_photo.setOnClickListener(this);
		btn_choose_other.setOnClickListener(this);
		btn_choose_born.setOnClickListener(this);
		btn_choose_sex.setOnClickListener(this);
		btn_change_nickname.setOnClickListener(this);
	}

	private void resetParams() {
		mSelectedSex = DataConstants.SexList.get(0);
	}

	private void ensureUi() {
		String nickname = currentBaby.getNickname();
		if (!TextUtils.isEmpty(nickname)) {
			tv_baby_nickname.setText(nickname);
			tv_baby_nickname_title.setText(nickname);
		}
		ensureBabySexUi();
		tv_baby_born.setText(currentBaby.getBorn());
		lv_menu = new ListView(this);
		lv_menu.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		mMenuAdapter = new MenuAdapter();
		lv_menu.setAdapter(mMenuAdapter);
		lv_menu.setOnItemClickListener(pickUpPhotoListener);
		iv_baby_photo.loadImage(currentBaby.getPhoto(), true,
				new OnImageLoadedListener() {
					@Override
					public void onImageLoaded(CacheableBitmapDrawable result) {
						btn_change_photo.setClickable(true);

					}
				});
	}

	/**
	 * 性别
	 */
	private void ensureBabySexUi() {

		tv_baby_sex.setText(UserUtils.getSex(currentBaby.getSex(),
				getSelfContext()));

		lv_sex = new ListView(getContext());
		lv_sex.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		View headerView = LayoutInflater.from(getContext()).inflate(
				R.layout.filter_list_header, null);
		TextView title = (TextView) headerView
				.findViewById(R.id.tv_filterTitle);
		title.setText(getString(R.string.choose_sex));
		lv_sex.addHeaderView(headerView, null, false);
		lv_sex.setDividerHeight(0);
		lv_sex.setHeaderDividersEnabled(true);
		mSexListAdapter = new SexListAdapter(getApplicationContext());

		mSexListAdapter.setSelectedId(currentBaby.getSex());
		lv_sex.setAdapter(mSexListAdapter);
		lv_sex.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (listViewDialog != null) {
					listViewDialog.dismiss();

					HashMap<String, String> query = new HashMap<String, String>();
					final Sex sex = (Sex) parent.getItemAtPosition(position);
					query.put("sex", sex.getId());
					// 修改性别
					modifyBabyInfo(query,
							getString(R.string.modify_baby_sex_loading),
							new OnFinishedListener<Baby>() {
								@Override
								public void onFininshed(Baby result) {
									if (result.getCode() == 1) {
										currentBaby = result;
										mSelectedSex = sex;
										mSexListAdapter
												.setSelectedId(mSelectedSex
														.getId());
										tv_baby_sex.setText(UserUtils.getSex(
												mSelectedSex.getId(),
												getSelfContext()));
										UiUtils.showAlertDialog(
												getString(R.string.modify_baby_sex_success),
												getSelfContext());
									} else {
										UiUtils.showAlertDialog(
												result.getDesc(),
												getSelfContext());
									}
								}
							});
				}
			}
		});
		mSexListAdapter.setGroup(DataConstants.SexList);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATA_PICKER_ID:
			return new DatePickerDialog(this, onDateSetListener,
					VeDate.getYearOfDate(currentBaby.getBorn()),
					VeDate.getMonthOfDate(currentBaby.getBorn()) - 1,
					VeDate.getDayOfDate(currentBaby.getBorn()));
		}
		return super.onCreateDialog(id);
	}

	/**
	 * 修改宝宝生日
	 */
	DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			if (year != VeDate.getYearOfDate(currentBaby.getBorn())
					|| month != (VeDate.getMonthOfDate(currentBaby.getBorn()) - 1)
					|| day != VeDate.getDayOfDate(currentBaby.getBorn())) {
				LoggerTool.d(TAG, year + "-" + month + "-" + day);
				final Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.DAY_OF_MONTH, day);
				String born = VeDate.DateToStr(cal.getTime().getTime(),
						"yyyy-MM-dd");
				HashMap<String, String> query = new HashMap<String, String>();
				query.put("born", born);
				// 修改出生日期
				modifyBabyInfo(query,
						getString(R.string.modify_baby_born_loading),
						new OnFinishedListener<Baby>() {
							@Override
							public void onFininshed(Baby result) {
								if (result.getCode() == 1) {
									currentBaby = result;
									tv_baby_born.setText(VeDate
											.DateToStr(cal.getTime().getTime(),
													"yyyy年MM月dd日"));
									refreshUserMainData();
									UiUtils.showAlertDialog(
											getString(R.string.modify_baby_born_success),
											getSelfContext());
								} else {
									UiUtils.showAlertDialog(result.getDesc(),
											getSelfContext());
								}
							}
						});
			}

		}

	};

	/**
	 * 更新主界面的宝宝信息
	 */
	private void refreshUserMainData() {
		Bundle data = new Bundle();
		data.putParcelable(UserBabyInfoActivity.INTENT_EXTRA_BABY, currentBaby);
		sendRouteNotificationRoute(
				new String[] { UserMainActivity.class.getName() },
				CommonAction.UPDATE_BABY_INFO, data);
	}

	private void fetchBabyInfo(OnFinishedListener<Baby> listener) {
		FangXinBaoAsyncTask<Baby> mTask = FangXinBaoAsyncTask.createInstance(
				getSelfContext(), UrlManager.URL_FETCH_BABY_INFO,
				new BabyParser(), false);
		mTask.putParameter("bbid", currentBaby.getId());
		mTask.setOnFinishedListener(listener);
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	/**
	 * 修改宝宝资料
	 * 
	 * @param query
	 * @param loading
	 * @param listener
	 */
	private void modifyBabyInfo(HashMap<String, String> query, String loading,
			OnFinishedListener<Baby> listener) {
		FangXinBaoAsyncTask<Baby> mTask = FangXinBaoAsyncTask.createInstance(
				getSelfContext(), UrlManager.URL_MODIFY_BABY_INFO,
				new BabyParser(), loading);

		mTask.putAllParameter(query);
		mTask.putParameter("bbid", currentBaby.getId());

		mTask.setOnFinishedListener(listener);
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	private void showBabyManagerMenu() {
		ArrayList<View> menuList = new ArrayList<View>();
		if (mUserInfo.getBaBies() != null) {
			for (Baby item : mUserInfo.getBaBies()) {
				String babyId = item.getId();
				if (!TextUtils.isEmpty(babyId)
						&& !babyId.equals(currentBaby.getId())) {
					TextView btn_baby = (TextView) LayoutInflater.from(
							getSelfContext()).inflate(
							R.layout.dialog_bottom_button_view, null);
					btn_baby.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Baby baby = (Baby) v.getTag();
							currentBaby = baby;
							Bundle data = new Bundle();
							data.putParcelable(INTENT_EXTRA_BABY, currentBaby);
							sendRouteNotificationRoute(
									new String[] { UserMainActivity.class
											.getName() },
									CommonAction.SWITCH_BABY, data);
							resetParams();
							ensureUi();
							mBabyManagerDialog.dismiss();
						}
					});
					btn_baby.setText(item.getNickname());
					btn_baby.setTag(item);
					menuList.add(btn_baby);
				}
			}
		}

		TextView btn_add = (TextView) LayoutInflater.from(getSelfContext())
				.inflate(R.layout.dialog_bottom_button_view, null);
		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startEditBabyInfoActivity();
				mBabyManagerDialog.dismiss();
			}
		});
		btn_add.setText(getString(R.string.add_baby));

		menuList.add(btn_add);

		TextView btn_cancel = (TextView) LayoutInflater.from(getSelfContext())
				.inflate(R.layout.dialog_bottom_button_view, null);
		btn_cancel.setText(getString(R.string.dialog_cancel_text));
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBabyManagerDialog != null
						&& mBabyManagerDialog.isShowing()) {
					mBabyManagerDialog.dismiss();
				}

			}
		});
		menuList.add(btn_cancel);
		mBabyManagerDialog = mDialog.popButtonListDialogMenu(menuList);
	}

	private void startEditBabyInfoActivity() {
		Intent intent = new Intent(getSelfContext(), EditBabyInfoActivity.class);
		startActivityForResult(intent, ACTIVITY_REQUEST_CODE_EDIT_BABYINFO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ACTIVITY_REQUEST_CODE_GALLERY://
			{
				mStateHolder.startUpLoadBabyPhotoTask();
				break;
			}
			case ACTIVITY_REQUEST_CODE_CAREMA:// 从相机
			{
				File myFile = new File(cameraFileName);
				if (myFile.exists()) {
					Intent intent = new Intent(getSelfContext(),
							PictureCropActivity.class);
					intent.setDataAndNormalize(Uri.fromFile(myFile));
					startActivityForResult(intent, ACTIVITY_REQUEST_CROP_IMAGE);
				} else {
					UiUtils.showAlertDialog(
							getString(R.string.read_imagefile_path_error),
							getSelfContext());
				}
				break;
			}
			case ACTIVITY_REQUEST_CROP_IMAGE://
			{
				mStateHolder.startUpLoadBabyPhotoTask();
				break;
			}
			case ACTIVITY_REQUEST_CODE_EDIT_BABYINFO://
			{
				if (data.hasExtra(INTENT_EXTRA_BABY)) {
					mUserInfo = Preferences.getUserInfo(getSelfContext());
					currentBaby = (Baby) data
							.getParcelableExtra(INTENT_EXTRA_BABY);
					resetParams();
					ensureUi();
					sendRouteNotificationRoute(
							new String[] { UserMainActivity.class.getName() },
							CommonAction.ADD_BABY, data.getExtras());
				}
				break;
			}

			case ACTIVITY_REQUEST_EDIT_BABY_NICKNAME: {
				if (data.hasExtra(EditBabyNickNameActivity.INTENT_EXTRA_NICKNAME)) {
					String nickname = data
							.getStringExtra(EditBabyNickNameActivity.INTENT_EXTRA_NICKNAME);
					HashMap<String, String> query = new HashMap<String, String>();
					query.put("nickname", nickname);
					// 修改昵称
					modifyBabyInfo(query,
							getString(R.string.modify_baby_nickname_loading),
							new OnFinishedListener<Baby>() {
								@Override
								public void onFininshed(Baby result) {
									if (result.getCode() == 1) {
										currentBaby = result;
										tv_baby_nickname.setText(currentBaby
												.getNickname());
										refreshUserMainData();
										UiUtils.showAlertDialog(
												getString(R.string.modify_baby_nickname_success),
												getSelfContext());
									} else {
										UiUtils.showAlertDialog(
												result.getDesc(),
												getSelfContext());
									}
								}
							});
				}
				break;
			}

			default:
				//
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mStateHolder.cancelAllTasks();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_choose_sex: {
			if (listViewDialog != null) {
				listViewDialog.dismiss();
			}
			listViewDialog = mDialog.showFromBottomDialog(lv_sex);

			break;
		}
		case R.id.btn_choose_born: {
			showDialog(DATA_PICKER_ID);
			break;
		}
		case R.id.btn_change_photo: {
			if (listViewDialog != null) {
				listViewDialog.dismiss();
			}
			listViewDialog = mDialog.showFromBottomDialog(lv_menu);
			break;
		}
		case R.id.btn_change_nickname: {
			Intent intent = new Intent(getSelfContext(),
					EditBabyNickNameActivity.class);
			intent.putExtra(EditBabyNickNameActivity.INTENT_EXTRA_NICKNAME,
					currentBaby.getNickname());
			startActivityForResult(intent, ACTIVITY_REQUEST_EDIT_BABY_NICKNAME);
			break;
		}
		case R.id.btn_choose_other: {
			showBabyManagerMenu();
			break;
		}

		}
	}

	private class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return menuList.length;
		}

		@Override
		public Object getItem(int position) {
			return menuList[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getSelfContext()).inflate(
						R.layout.dialog_list_item, null);
				holder = new ViewHolder();
				holder.v_line = convertView.findViewById(R.id.v_line);
				holder.v_line.setVisibility(View.GONE);
				holder.txtView = (TextView) convertView
						.findViewById(R.id.txtView);
				holder.markImageView = (ImageView) convertView
						.findViewById(R.id.iv_check);
				holder.markImageView.setVisibility(View.GONE);
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iconImageView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txtView.setText(menuList[position]);
			return convertView;
		}

		class ViewHolder {
			TextView txtView;
			ImageView markImageView;
			ImageView iv_icon;
			View v_line;
		}

	}

	/**
	 * 修改宝宝图片
	 * 
	 * @author xieb
	 * 
	 */
	private class UpLoadBabyPhotoTask extends
			AsyncTaskWithLoadingDialog<Void, Void, Baby> {

		public UpLoadBabyPhotoTask() {
			super(getSelfContext(),
					getString(R.string.modify_baby_photo_loading), false, true);

		}

		@Override
		protected Baby doInBackground(Void... params) {

			NetWorKManager network = mApplication.getNetWorKManager();
			return network
					.modifyBabyPhoto(
							mFileCache
									.getDiskCacheFilePath(PictureCropActivity.CROPPER_FILE_NAME),
							currentBaby.getId());

		}

		@Override
		protected void onPostExecute(Baby result) {
			super.onPostExecute(result);
			mStateHolder.cancelUpLoadBabyPhotoTask();
			if (result.getCode() == 1) {
				currentBaby = result;
				refreshUserMainData();
				iv_baby_photo.loadImage(currentBaby.getPhoto(), true, null);
			} else {
				UiUtils.showAlertDialog(result.getDesc(), getSelfContext());
			}

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelUpLoadBabyPhotoTask();
		}

	}

	/**
	 * 
	 * @author xieb
	 * 
	 */
	private class FetchImageTask extends
			AsyncTaskWithLoadingDialog<Void, Void, Void> {

		public FetchImageTask() {
			super(getSelfContext(), "", false, false);

		}

		@Override
		protected Void doInBackground(Void... params) {
			if (mFileCache.contains(currentBaby.getPhoto())) {

			} else {
				NetWorKManager network = mApplication.getNetWorKManager();
				mFileCache.put(currentBaby.getPhoto(),
						network.fetchStream(currentBaby.getPhoto()));

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mStateHolder.cancelFetchImageTask();
			iv_baby_photo.setImageDrawable(mFileCache.get(currentBaby
					.getPhoto()));
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelFetchImageTask();
		}

	}

	private class StateHolder {
		private FetchImageTask mFetchImageTask;
		private boolean mFetchImageTaskRunning = false;
		private UpLoadBabyPhotoTask mUpLoadBabyPhotoTask;
		private boolean mUpLoadBabyPhotoTaskRunning = false;

		public void startUpLoadBabyPhotoTask() {
			if (mUpLoadBabyPhotoTaskRunning == false) {
				mUpLoadBabyPhotoTaskRunning = true;
				mUpLoadBabyPhotoTask = new UpLoadBabyPhotoTask();
				mUpLoadBabyPhotoTask.safeExecute();
			}
		}

		public void cancelUpLoadBabyPhotoTask() {
			if (mUpLoadBabyPhotoTask != null) {
				mUpLoadBabyPhotoTask.cancel(true);
				mUpLoadBabyPhotoTask = null;
			}

			mUpLoadBabyPhotoTaskRunning = false;
		}

		public void startFetchImageTask() {
			if (mFetchImageTaskRunning == false) {
				mFetchImageTaskRunning = true;
				mFetchImageTask = new FetchImageTask();
				mFetchImageTask.safeExecute();
			}
		}

		public void cancelFetchImageTask() {
			if (mFetchImageTask != null) {
				mFetchImageTask.cancel(true);
				mFetchImageTask = null;
			}

			mFetchImageTaskRunning = false;
		}

		public void cancelAllTasks() {
			cancelFetchImageTask();
			cancelUpLoadBabyPhotoTask();
		}
	}

}
