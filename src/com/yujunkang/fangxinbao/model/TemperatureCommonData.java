package com.yujunkang.fangxinbao.model;


import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 
 * @date 2014-7-22
 * @author xieb
 * 
 */
public class TemperatureCommonData extends BaseData implements Parcelable {

	private String range;
	private TemperatureDataDesc desc_zh;
	private TemperatureDataDesc desc_en;

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}



	public TemperatureDataDesc getDesc_zh() {
		return desc_zh;
	}

	public void setDesc_zh(TemperatureDataDesc desc_zh) {
		this.desc_zh = desc_zh;
	}

	public TemperatureDataDesc getDesc_en() {
		return desc_en;
	}

	public void setDesc_en(TemperatureDataDesc desc_en) {
		this.desc_en = desc_en;
	}

	public boolean inRange(float value) {
		if (!TextUtils.isEmpty(range)) {
			try {
				int startIndex = range.indexOf("[");
				int endIndex = range.indexOf("]");
				String rangeValue = range.substring(startIndex + 1, endIndex);
				// 温度筛选格式[0,36]，[36,38],[38,0],
				String[] rangeArray = rangeValue.split(",");
				if (rangeArray != null && rangeArray.length == 2) {
					int filterminprice = Integer.valueOf(rangeArray[0]);
					int filtermaxprice = Integer.valueOf(rangeArray[1]);
					if (filterminprice == 0) {
						if (value <= filtermaxprice) {
							return true;
						}
					} else if (filtermaxprice == 0) {
						if (value >= filterminprice) {
							return true;
						}
					} else {
						if (value >= filterminprice && value <= filtermaxprice) {
							return true;
						}
					}
				}
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}

	private TemperatureCommonData(Parcel in) {
		range = ParcelUtils.readStringFromParcel(in);
		if (in.readInt() == 1) {
			desc_zh = in.readParcelable(TemperatureDataDesc.class.getClassLoader());
		}
		if (in.readInt() == 1) {
			desc_en = in.readParcelable(TemperatureDataDesc.class.getClassLoader());
		}
	}

	public TemperatureCommonData() {
		// TODO Auto-generated constructor stub
	}

	public static final Creator<TemperatureCommonData> CREATOR = new Parcelable.Creator<TemperatureCommonData>() {
		public TemperatureCommonData createFromParcel(Parcel in) {
			return new TemperatureCommonData(in);
		}
		@Override
		public TemperatureCommonData[] newArray(int size) {
			return new TemperatureCommonData[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, range);
		if (desc_zh != null) {
			out.writeInt(1);
			out.writeParcelable(desc_zh, flags);
		} else {
			out.writeInt(0);
		}
		if (desc_en != null) {
			out.writeInt(1);
			out.writeParcelable(desc_en, flags);
		} else {
			out.writeInt(0);
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
