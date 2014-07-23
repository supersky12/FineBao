package com.yujunkang.fangxinbao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

/**
 * 
 * @date 2014-7-10
 * @author xieb
 * 
 */
public class BabyRecentTemperature extends BaseData implements Parcelable {
	private Group<TemperatureData> recentData;
	private String maxTemperature;
	private String duration;

	public Group<TemperatureData> getRecentData() {
		return recentData;
	}

	public void setRecentData(Group<TemperatureData> recentData) {
		this.recentData = recentData;
	}

	public String getMaxTemperature() {
		return maxTemperature;
	}

	public void setMaxTemperature(String maxTemperature) {
		this.maxTemperature = maxTemperature;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	private BabyRecentTemperature(Parcel in) {
		maxTemperature = ParcelUtils.readStringFromParcel(in);
		duration = ParcelUtils.readStringFromParcel(in);

		recentData = new Group<TemperatureData>();
		int numData = in.readInt();
		for (int i = 0; i < numData; i++) {
			TemperatureData data = in.readParcelable(TemperatureData.class
					.getClassLoader());
			recentData.add(data);
		}

	}

	public BabyRecentTemperature() {
		// TODO Auto-generated constructor stub
	}

	public static final Creator<BabyRecentTemperature> CREATOR = new Parcelable.Creator<BabyRecentTemperature>() {
		public BabyRecentTemperature createFromParcel(Parcel in) {
			return new BabyRecentTemperature(in);
		}

		@Override
		public BabyRecentTemperature[] newArray(int size) {
			return new BabyRecentTemperature[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, maxTemperature);
		ParcelUtils.writeStringToParcel(out, duration);
		if (recentData != null) {
			out.writeInt(recentData.size());
			for (int i = 0; i < recentData.size(); i++) {
				out.writeParcelable(recentData.get(i), flags);
			}
		} else {
			out.writeInt(0);
		}

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
