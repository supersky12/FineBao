package com.yujunkang.fangxinbao.activity.user;

import java.io.File;
import java.util.Calendar;
import com.yujunkang.fangxinbao.R;

import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.activity.MainActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.app.NetWorKManager;
import com.yujunkang.fangxinbao.control.NetworkProgressButton;
import com.yujunkang.fangxinbao.control.NetworkedCacheableImageView;
import com.yujunkang.fangxinbao.control.image.RoundedNetWorkImageView;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.Sex;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.BabyParser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnPrepareTaskListener;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.SystemUiUtils;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.Utils;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.utility.DataConstants.VerifyCodeLanucherType;
import com.yujunkang.fangxinbao.widget.adapter.SexListAdapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @date 2014-5-31
 * @author xieb
 * 
 */
public class EditBabyInfoActivity extends ActivityWrapper {

	private static final String TAG = "EditBabyInfoActivity";
	public static final String INTENT_EXTRA_IS_ADD_BABY = DataConstants.PACKAGE_NAME
			+ ".EditBabyInfoActivity.INTENT_EXTRA_IS_ADD_BABY";

	private static final int DATA_PICKER_ID = 1;
	private static final int ACTIVITY_REQUEST_CODE_GALLERY = 2;
	private static final int ACTIVITY_REQUEST_CODE_CAREMA = 3;
	private static final int REQUEST_ACTIVITY_CROP_IMAGE = 4;
	private String cameraFileName = null;
	/**
	 * 控件
	 */

	private NetworkProgressButton btn_register_done;
	private View btn_change_photo;
	private RoundedNetWorkImageView iv_baby_photo;
	private EditText et_baby_nickname;
	private ImageButton ibtn_nickname_del;
	private Dialog listViewDialog;
	private EditText et_password;
	private TextView tv_sex;
	private TextView tv_born;
	private ListView lv_sex;
	private SexListAdapter mSexListAdapter;
	private MenuAdapter mMenuAdapter;
	private ListView lv_menu;
	private View ll_password;
	private View btn_choose_born;
	private View btn_choose_sex;
	private TextView tv_title;
	private View ll_common_info;
	
	
	
	
	private String mSex;
	private String phone;
	private String mEmail;
	private Country mSelectCountry;
	private Sex mSelectedSex;
	private StateHolder mStateHolder = new StateHolder();

	private String[] menuList = new String[2];
	private int pickPhotoMenuType = 0;
	private String mBirthday = null;
	private boolean isPickupPhoto = false;
	private boolean isAddBaby = false;

	private TextWatcher onChange = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			btn_register_done.setEnabled(VerificationEmptyInput());
		}
	};

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
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_baby_info_activity);
		init();
		initControl();
		ensureUi();
	}

	protected void initData() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			isAddBaby = false;
			if (extras.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE)) {
				phone = extras
						.getString(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE);
			}

			if (extras
					.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY)) {
				mSelectCountry = (Country) extras
						.getParcelable(FetchVerifyCodeActivity.INTENT_EXTRA_COUNTRY);
			}

			if (extras.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_EMAIL)) {
				mEmail = extras
						.getString(FetchVerifyCodeActivity.INTENT_EXTRA_EMAIL);
			}
		} else {
			isAddBaby = true;
		}
	}

	private void init() {
		mSelectedSex = DataConstants.SexList.get(0);

		menuList = getResources().getStringArray(R.array.choosephotos);
	}

	private void initControl() {
		// 标题
		tv_title = (TextView) findViewById(R.id.ContentTopText);
		// 头像
		btn_change_photo = findViewById(R.id.btn_change_photo);
		iv_baby_photo = (RoundedNetWorkImageView) findViewById(R.id.iv_baby_photo);
		// 昵称
		et_baby_nickname = (EditText) findViewById(R.id.et_baby_nickname);
		ibtn_nickname_del = (ImageButton) findViewById(R.id.ibtn_nickname_del);
		btn_choose_born = findViewById(R.id.btn_choose_born);

		// 密码
		ll_password = findViewById(R.id.ll_password);
		et_password = (EditText) findViewById(R.id.et_password);
		btn_register_done = (NetworkProgressButton) findViewById(R.id.btn_register_done);
		btn_choose_sex = findViewById(R.id.btn_choose_sex);
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		ll_common_info=findViewById(R.id.ll_common_info);
		
		
		btn_choose_sex.setOnClickListener(this);

		btn_register_done.setOnClickListener(this);
		btn_choose_born.setOnClickListener(this);
		btn_change_photo.setOnClickListener(this);
	}

	private void lock()
	{
		UiUtils.disableView(ll_common_info);
		UiUtils.disableView(btn_change_photo);
		UiUtils.disableView(et_baby_nickname);
		UiUtils.disableView(btn_choose_born);
		UiUtils.disableView(ll_password);
		UiUtils.disableView(et_password);
		UiUtils.disableView(btn_choose_sex);
	}
	
	private void unlock()
	{
		UiUtils.enableView(ll_common_info);
		UiUtils.enableView(btn_change_photo);
		UiUtils.enableView(et_baby_nickname);
		UiUtils.enableView(btn_choose_born);
		UiUtils.enableView(ll_password);
		UiUtils.enableView(et_password);
		UiUtils.enableView(btn_choose_sex);
	}
	
	
	private void ensureUi() {
		if(isAddBaby)
		{
			tv_title.setText(R.string.add_baby_activity_title);
		}
		
		ensureBabyPhotoUi();
		ensureNickNameUi();
		ensureBabySexUi();
		ensureBabyBornUi();
		if (!isAddBaby) {
			ll_password.setVisibility(View.VISIBLE);
			et_password.addTextChangedListener(onChange);
		} else {
			ll_password.setVisibility(View.GONE);
		}
	}

	private void ensureBabyPhotoUi() {

		lv_menu = new ListView(this);
		lv_menu.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		mMenuAdapter = new MenuAdapter();
		lv_menu.setAdapter(mMenuAdapter);
		lv_menu.setOnItemClickListener(pickUpPhotoListener);
	}

	/**
	 * 昵称
	 */
	private void ensureNickNameUi() {

		ibtn_nickname_del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et_baby_nickname.setText(null);

			}
		});
		et_baby_nickname.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						et_baby_nickname.clearFocus();
					}
				}
				return false;
			}
		});
		et_baby_nickname.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && et_baby_nickname.length() > 0) {
					ibtn_nickname_del.setVisibility(View.VISIBLE);
				} else {
					ibtn_nickname_del.setVisibility(View.GONE);
				}
			}
		});

		et_baby_nickname.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				int len = et_baby_nickname.length();
				if (ibtn_nickname_del != null) {
					ibtn_nickname_del.setVisibility(len == 0 ? View.GONE
							: View.VISIBLE);
				}

			}
		});
		et_baby_nickname.addTextChangedListener(onChange);
	}

	/**
	 * 性别
	 */
	private void ensureBabySexUi() {

		tv_sex.setText(mSelectedSex.getSexName());
		tv_sex.addTextChangedListener(onChange);
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

		mSexListAdapter.setSelectedId("1");
		lv_sex.setAdapter(mSexListAdapter);
		lv_sex.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (listViewDialog != null) {
					listViewDialog.dismiss();
				}
				mSelectedSex = (Sex) parent.getItemAtPosition(position);
				tv_sex.setText(mSelectedSex.getSexName());
				mSexListAdapter.setSelectedId(mSelectedSex.getId());
			}
		});
		mSexListAdapter.setGroup(DataConstants.SexList);

	}

	/**
	 * 生日
	 */
	private void ensureBabyBornUi() {

		tv_born = (TextView) findViewById(R.id.tv_born);
		tv_born.addTextChangedListener(onChange);
	}

	private boolean VerificationEmptyInput() {

		if (!isAddBaby && TextUtils.isEmpty(et_password.getText().toString())) {

			return false;
		}
		if (TextUtils.isEmpty(et_baby_nickname.getText().toString())) {

			return false;
		}
		if (TextUtils.isEmpty(tv_born.getText())) {

			return false;
		}
		if (TextUtils.isEmpty(tv_sex.getText())) {

			return false;
		}
		
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ACTIVITY_REQUEST_CODE_GALLERY:// 从图库
				iv_baby_photo.loadImage(PictureCropActivity.CROPPER_FILE_NAME,
						false, null);
				isPickupPhoto = true;
				btn_register_done.setEnabled(VerificationEmptyInput());
				break;
			case ACTIVITY_REQUEST_CODE_CAREMA:// 从相机
				File myFile = new File(cameraFileName);
				if (myFile.exists()) {
					Intent intent = new Intent(getSelfContext(),
							PictureCropActivity.class);
					intent.setDataAndNormalize(Uri.fromFile(myFile));
					startActivityForResult(intent, REQUEST_ACTIVITY_CROP_IMAGE);
				} else {
					UiUtils.showAlertDialog(
							getString(R.string.read_imagefile_path_error),
							getSelfContext());
				}
				break;
			case REQUEST_ACTIVITY_CROP_IMAGE:// 从图库
				iv_baby_photo.loadImage(PictureCropActivity.CROPPER_FILE_NAME,
						false, null);
				isPickupPhoto = true;
				btn_register_done.setEnabled(VerificationEmptyInput());
				break;
			default:
				//
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mStateHolder.cancelAlltasks();

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_choose_sex: {
			if (listViewDialog != null) {
				listViewDialog.dismiss();
			}
			listViewDialog = mDialog.showFromBottomDialog(lv_sex,
					R.drawable.button07_bg);

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
		case R.id.btn_register_done: {
			if (!isAddBaby) {
				mStateHolder.startRegisterUserTask();
			} else {
				mStateHolder.startAddBabyTask();
			}
			break;
		}

		}

	}

	DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {

			LoggerTool.d(TAG, year + "-" + month + "-" + day);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, day);
			tv_born.setText(VeDate.DateToStr(cal.getTime().getTime(),
					"yyyy年MM月dd日"));
			mBirthday = VeDate.DateToStr(cal.getTime().getTime(), "yyyy-MM-dd");
		}

	};

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

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DATA_PICKER_ID:
			return new DatePickerDialog(this, onDateSetListener, 2011, 3, 22);
		}
		return super.onCreateDialog(id);
	}

	private class RegisterUserTask extends
			AsyncTaskWithLoadingDialog<Void, Void, User> {

		public RegisterUserTask() {
			super(getSelfContext(), "正在注册...", false, false);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			btn_register_done.preNetworkExecute();
			lock();
		}

		@Override
		protected User doInBackground(Void... params) {
			NetWorKManager httpApi = mApplication.getNetWorKManager();
			String filePath = null;
			if(isPickupPhoto)
			{
				filePath = mFileCache
						.getDiskCacheFilePath(PictureCropActivity.CROPPER_FILE_NAME);
			}
			User result = httpApi
					.register(
							phone,
							mEmail,
							et_password.getText().toString(),
							mSelectCountry.getCountryCode(),
							et_baby_nickname.getText().toString(),
							mBirthday,
							filePath,
							mSex);
			return result;
		}

		@Override
		protected void onPostExecute(User result) {
			super.onPostExecute(result);
			mStateHolder.cancelRegisterUserTask();
			btn_register_done.finishNetworkExecute();
			unlock();
			if (result == null) {
				UiUtils.showAlertDialog(getString(R.string.register_failed),
						getSelfContext());
			} else {
				if (result.code == 1) {
					String account = !TextUtils.isEmpty(phone) ? phone : mEmail;
					// 保存用户信息
					Preferences.loginUser(getSelfContext(), account,
							et_password.getText().toString(), result);
					UiUtils.showAlertDialog(
							getString(R.string.register_success),
							getSelfContext());
					sendRouteNotificationRoute(new String[] {
							FetchVerifyCodeActivity.class.getName(),
							VerifySMSCodeActivity.class.getName(),
							EditEmailActivity.class.getName() },
							CommonAction.CLOSE_ALL_ACTIVITY, null);
					startUserMainActivity();
					finish();
				} else {
					UiUtils.showAlertDialog(
							!TextUtils.isEmpty(result.getDesc()) ? result
									.getDesc()
									: getString(R.string.register_failed),
							getSelfContext());
				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelRegisterUserTask();
			btn_register_done.finishNetworkExecute();
			unlock();
		}
	}

	private class AddBabyTask extends
			AsyncTaskWithLoadingDialog<Void, Void, Baby> {

		public AddBabyTask() {
			super(getSelfContext(), getString(R.string.add_baby_loading), true,
					true);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Baby doInBackground(Void... params) {
			NetWorKManager httpApi = mApplication.getNetWorKManager();
			Baby result = httpApi
					.addBabyInfo(
							et_baby_nickname.getText().toString(),
							mBirthday,
							mSex,
							mFileCache
									.getDiskCacheFilePath(PictureCropActivity.CROPPER_FILE_NAME));
			return result;
		}

		@Override
		protected void onPostExecute(Baby result) {
			super.onPostExecute(result);
			mStateHolder.cancelAddBabyTask();
			
			if (result == null) {
				UiUtils.showAlertDialog(getString(R.string.add_baby_failed),
						getSelfContext());
			} else {
				if (result.code == 1) {
					// 保存用户信息
					UiUtils.showAlertDialog(
							getString(R.string.add_baby_successful),
							getSelfContext());
					User temUser = Preferences.getUserInfo(getSelfContext());
					temUser.getBaBies().add(result);
					Preferences.storeUser(getSelfContext(), temUser);
					Intent intent = new Intent();
					intent.putExtra(UserBabyInfoActivity.INTENT_EXTRA_BABY,
							result);
					setResult(Activity.RESULT_OK, intent);
					finish();
				} else {
					UiUtils.showAlertDialog(
							!TextUtils.isEmpty(result.getDesc()) ? result
									.getDesc()
									: getString(R.string.add_baby_failed),
							getSelfContext());
				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelAddBabyTask();
			
		}
	}

	private void startUserMainActivity() {
		Intent intent = new Intent(getSelfContext(), UserMainActivity.class);

		startActivity(intent);
	}

	private class StateHolder {

		private RegisterUserTask mRegisterUserTask; // 得到验证码
		boolean isRegisterUserTaskRunning = false;
		private AddBabyTask mAddBabyTask; // 得到验证码
		boolean isAddBabyTaskRunning = false;

		public StateHolder() {

		}

		public void startRegisterUserTask() {
			if (!isRegisterUserTaskRunning) {
				isRegisterUserTaskRunning = true;
				mRegisterUserTask = new RegisterUserTask();
				mRegisterUserTask.safeExecute();
			}
		}

		public void cancelRegisterUserTask() {
			if (mRegisterUserTask != null) {
				mRegisterUserTask.cancel(true);
				mRegisterUserTask = null;
			}
			isRegisterUserTaskRunning = false;

		}

		public void startAddBabyTask() {
			if (!isAddBabyTaskRunning) {
				isAddBabyTaskRunning = true;
				mAddBabyTask = new AddBabyTask();
				mAddBabyTask.safeExecute();
			}
		}

		public void cancelAddBabyTask() {
			if (mAddBabyTask != null) {
				mAddBabyTask.cancel(true);
				mAddBabyTask = null;
			}
			isAddBabyTaskRunning = false;

		}

		public void cancelAlltasks() {

			cancelRegisterUserTask();
			cancelAddBabyTask();
		}
	}
}
