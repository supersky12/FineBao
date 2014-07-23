package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * 
 * @date 2014-6-22
 * @author xieb
 * 
 */
public class TemperatureData extends BaseData implements Parcelable {
	private String time;
	private String temperature;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	private TemperatureData(Parcel in) {
		time = ParcelUtils.readStringFromParcel(in);
		temperature = ParcelUtils.readStringFromParcel(in);

	}

	public TemperatureData() {
		// TODO Auto-generated constructor stub
	}

	public static final Creator<TemperatureData> CREATOR = new Parcelable.Creator<TemperatureData>() {
		public TemperatureData createFromParcel(Parcel in) {
			return new TemperatureData(in);
		}

		@Override
		public TemperatureData[] newArray(int size) {
			return new TemperatureData[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, time);
		ParcelUtils.writeStringToParcel(out, temperature);
	}

	@Override
	public int describeContents() {
		return 0;
	}

}
