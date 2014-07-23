package com.yujunkang.fangxinbao.activity;

import java.util.Locale;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;

/**
 * 
 * @date 2014-6-19
 * @author xieb
 * 
 */
public class SettingLanguageActivity extends ActivityWrapper {
	private ListView mListView;
	private LanguagesAdapter mAdapter;

	private String[] languages = null;
	private String selectLanguage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_language_activity);
		init();
		initControl();
		ensureUi();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private void init() {

		languages = getResources().getStringArray(R.array.languages);
		selectLanguage = mApplication.getLocale().getLanguage();
	}

	private void initControl() {
		mListView = (ListView) findViewById(R.id.lv_language);
		mAdapter = new LanguagesAdapter();
		mListView.setAdapter(mAdapter);
	}

	private void ensureUi() {
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String item = languages[position];
				String[] data = item.split(",");
				Locale locale = mApplication.getLocale();
				String language = locale.getLanguage();
				if (!TextUtils.isEmpty(language) && !language.contains(data[1])) {
					Locale selectedLanguage = new Locale(data[1]);
					
					selectLanguage = data[1];
					mApplication.setLocale(selectedLanguage);
					Locale.setDefault(selectedLanguage);
					Configuration config = getBaseContext().getResources()
							.getConfiguration();
					config.locale = selectedLanguage;
					getResources()
							.updateConfiguration(
									config,
									getBaseContext().getResources()
											.getDisplayMetrics());
					mAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	class LanguagesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return languages.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return languages[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getSelfContext()).inflate(
						R.layout.languages_list_item, null);
				holder = new ViewHolder();
				holder.tv_language = (TextView) convertView
						.findViewById(R.id.tv_language);
				holder.iv_choose = (ImageView) convertView
						.findViewById(R.id.iv_choose);
				holder.iv_language = (ImageView) convertView
						.findViewById(R.id.iv_language);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String item = languages[position];
			String[] data = item.split(",");
			holder.tv_language.setText(data[0]);

			if (selectLanguage.contains(data[1])) {
				holder.iv_choose.setVisibility(View.VISIBLE);
			} else {
				holder.iv_choose.setVisibility(View.INVISIBLE);
			}
			holder.iv_language.setImageResource(getIconResid(data[1]));
			return convertView;
		}

		private int getIconResid(String language) {
			String sourceOnName = getPackageName() + ":drawable/language_"
					+ language;
			int iconSource = getResources().getIdentifier(sourceOnName, null,
					null);
			return iconSource;
		}

		class ViewHolder {
			TextView tv_language;
			ImageView iv_choose;
			ImageView iv_language;

		}

	}

}
