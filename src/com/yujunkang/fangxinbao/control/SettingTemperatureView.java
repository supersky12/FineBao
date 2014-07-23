package com.yujunkang.fangxinbao.control;

import java.math.BigDecimal;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.TypeUtils;
import com.yujunkang.fangxinbao.utility.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @date 2014-6-18
 * @author xieb
 * 
 */
public class SettingTemperatureView extends LinearLayoutControlWrapView {
	private float increase = 0.1f;

	private View btn_add;
	private View btn_reduce;
	private TextView tv_temperature;

	private float temperatureValue;
	private int temperatureType;

	public SettingTemperatureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInflater.inflate(R.layout.settings_temperature_view, this);
		setGravity(Gravity.CENTER_VERTICAL);
		setOrientation(LinearLayout.HORIZONTAL);
		// 温度相关
		temperatureType = FangXinBaoApplication.getApplication(getContext())
				.getPrefs()
				.getInt(Preferences.PREFERENCE_NORMAL_TEMPERATURE, -1);
		ensureUI();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		final int totalWidth = MeasureSpec.getSize(widthMeasureSpec);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(totalWidth, (int) getContext().getResources()
				.getDimension(R.dimen.table_item_height));
	}

	public void ensureUI() {
		btn_add = findViewById(R.id.btn_add);
		btn_reduce = findViewById(R.id.btn_reduce);
		tv_temperature = (TextView) findViewById(R.id.tv_temperature);
		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (temperatureValue <= DataConstants.DEFAULT_MAX_TEMPERATURE) {
					temperatureValue = temperatureValue + increase;
					tv_temperature.setText(getTemperatureValue());
				}

			}
		});

		btn_reduce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (temperatureValue > DataConstants.DEFAULT_NORMAL_TEMPERATURE) {
					temperatureValue = temperatureValue - increase;
					tv_temperature.setText(getTemperatureValue());
				}
			}
		});
		temperatureValue = DataConstants.DEFAULT_NORMAL_TEMPERATURE;
		tv_temperature.setText(getTemperatureValue());

	}

	public String getTemperatureValue() {
		try {
			return TypeUtils.getTemperatureScaleValue(1, temperatureValue,
					FangXinBaoApplication.getApplication(getContext())
							.getLocale(), temperatureType, getContext());
		} catch (Exception ex) {

		}
		return "";
	}

	public float getValue() {
		return temperatureValue;
	}

	public void setTemperatureValue(float temperatureValue) {
		this.temperatureValue = temperatureValue;
		tv_temperature.setText(getTemperatureValue());
	}

}
