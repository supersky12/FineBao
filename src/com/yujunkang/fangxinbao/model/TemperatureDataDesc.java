package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;

public class TemperatureDataDesc extends BaseData implements Parcelable{
	private String shortDesc;
	private String longDesc;
	private String url;

	
	public String getShortDesc() {
		return shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public String getLongDesc() {
		return longDesc;
	}

	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public TemperatureDataDesc() {
		// TODO Auto-generated constructor stub
	}

	public TemperatureDataDesc(Parcel in) {
		shortDesc = ParcelUtils.readStringFromParcel(in);
		longDesc = ParcelUtils.readStringFromParcel(in);
		url = ParcelUtils.readStringFromParcel(in);
	}

	public static final Creator<TemperatureDataDesc> CREATOR = new Parcelable.Creator<TemperatureDataDesc>() {
		public TemperatureDataDesc createFromParcel(Parcel in) {
			return new TemperatureDataDesc(in);
		}

		@Override
		public TemperatureDataDesc[] newArray(int size) {
			return new TemperatureDataDesc[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, shortDesc);
		ParcelUtils.writeStringToParcel(out, longDesc);
		ParcelUtils.writeStringToParcel(out, url);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	
	
	

}
