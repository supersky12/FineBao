package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;

public class Baby extends BaseData implements Parcelable {
	private String mId;
	private String mNickname;
	private String mPhoto;
	private String uid;

	private String mBorn;

	private String mSex;

	public Baby() {
	}

	private Baby(Parcel in) {
		mId = ParcelUtils.readStringFromParcel(in);
		mNickname = ParcelUtils.readStringFromParcel(in);
		mPhoto = ParcelUtils.readStringFromParcel(in);
		mBorn = ParcelUtils.readStringFromParcel(in);
		mSex = ParcelUtils.readStringFromParcel(in);
		uid = ParcelUtils.readStringFromParcel(in);
	}

	public static final Creator<Baby> CREATOR = new Parcelable.Creator<Baby>() {
		public Baby createFromParcel(Parcel in) {
			return new Baby(in);
		}

		@Override
		public Baby[] newArray(int size) {
			return new Baby[size];
		}
	};

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public String getNickname() {
		return mNickname;
	}

	public void setNickname(String Nickname) {
		this.mNickname = Nickname;
	}

	public String getBorn() {
		return mBorn;
	}

	public void setBorn(String Born) {
		this.mBorn = Born;
	}

	public String getSex() {
		return mSex;
	}

	public void setSex(String Sex) {
		this.mSex = Sex;
	}

	public String getPhoto() {
		return mPhoto;
	}

	public void setPhoto(String photo) {
		mPhoto = photo;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, mId);
		ParcelUtils.writeStringToParcel(out, mNickname);
		ParcelUtils.writeStringToParcel(out, mPhoto);
		ParcelUtils.writeStringToParcel(out, mBorn);
		ParcelUtils.writeStringToParcel(out, mSex);
		ParcelUtils.writeStringToParcel(out, uid);

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return mNickname;
	}

	
}
