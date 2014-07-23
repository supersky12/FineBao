package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;



/**
 * 
 * @date 2014-7-21
 * @author xieb
 * 
 */
public class PerDayTemperatureData extends BaseData implements Parcelable {

	private Group<TemperatureData> temperatureDatas ;

	
	public Group<TemperatureData> getTemperatureDatas() {
		return temperatureDatas;
	}

	public void setTemperatureDatas(Group<TemperatureData> temperatureDatas) {
		this.temperatureDatas = temperatureDatas;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	private PerDayTemperatureData(Parcel in) {
		
		temperatureDatas = new Group<TemperatureData>();
		int numDatas = in.readInt();
		for (int i = 0; i < numDatas; i++) {
			TemperatureData item = in.readParcelable(TemperatureData.class.getClassLoader());
			temperatureDatas.add(item);
		}

	}

	public static final Creator<PerDayTemperatureData> CREATOR = new Parcelable.Creator<PerDayTemperatureData>() {
		public PerDayTemperatureData createFromParcel(Parcel in) {
			return new PerDayTemperatureData(in);
		}

		@Override
		public PerDayTemperatureData[] newArray(int size) {
			return new PerDayTemperatureData[size];
		}
	};


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (temperatureDatas != null) {
			dest.writeInt(temperatureDatas.size());
			for (int i = 0; i < temperatureDatas.size(); i++) {
				dest.writeParcelable(temperatureDatas.get(i), flags);
			}
		} else {
			dest.writeInt(0);
		}

		
	}
	
	
}


