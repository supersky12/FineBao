package com.yujunkang.fangxinbao.activity;

import java.util.ArrayList;
import java.util.List;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.control.TemperatureCureView;
import com.yujunkang.fangxinbao.model.TemperatureData;
import android.app.Activity;
import android.os.Bundle;

/**
 * 
 * @date 2014-5-23
 * @author xieb
 * 
 */
public class TestActivity extends Activity {
	private final String TAG = "TestActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity);
		TemperatureCureView tem = (TemperatureCureView) findViewById(R.id.temperature_curveView);

		List<TemperatureData> datas = new ArrayList<TemperatureData>();
		for (int index = 0; index < 5; index++) {
			TemperatureData data = new TemperatureData();
			if (index == 0) {
				data.setTemperature("29");
				data.setTime("18:00");
			} else if (index == 1) {
				data.setTemperature("28");
				data.setTime("19:00");
			} else if (index == 2) {
				data.setTemperature("27");
				data.setTime("20:00");
			} else if (index == 3) {
				data.setTemperature("26");
				data.setTime("21:00");
			} else if (index == 4) {
				data.setTemperature("27");
				data.setTime("22:00");
			}
			datas.add(data);
		}

		tem.initData(datas);
	}


	

}
