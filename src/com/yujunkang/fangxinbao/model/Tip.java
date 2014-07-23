package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @date 2014-7-17
 * @author xieb
 * 
 */
public class Tip extends BaseData implements Parcelable {
	private String mId;
	private String mContent;

	public Tip() {
	}

	private Tip(Parcel in) {
		mId = ParcelUtils.readStringFromParcel(in);
		mContent = ParcelUtils.readStringFromParcel(in);

	}

	public static final Creator<Tip> CREATOR = new Parcelable.Creator<Tip>() {
		public Tip createFromParcel(Parcel in) {
			return new Tip(in);
		}

		@Override
		public Tip[] newArray(int size) {
			return new Tip[size];
		}
	};

	public String getContent() {
		return mContent;
	}

	public void setContent(String Content) {
		this.mContent = Content;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, mId);
		ParcelUtils.writeStringToParcel(out, mContent);

	}

	@Override
	public int describeContents() {
		return 0;
	}

}
