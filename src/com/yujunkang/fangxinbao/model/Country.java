package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class Country extends BaseData implements Parcelable {
	/**
	 * 国家编号
	 */
	private String mId = "";
	/**
	 * 中文名称
	 */
	private String mName = "";
	/**
	 * 英文名称编号
	 */
	private String mEngName = "";
	/**
	 * 国家代码
	 */
	private String mCountryCode = "";

	/**
	 * 国家英文简称
	 */
	private String mCountrySimpleCode = "";

	/**
	 * 拼音首字母
	 */
	private String mFirstLetter = "";

	public String getFirstLetter() {
		return mFirstLetter;
	}

	public void setFirstLetter(String FirstLetter) {
		this.mFirstLetter = FirstLetter;
	}

	public String getId() {
		return mId;
	}

	public void setId(String Id) {
		this.mId = Id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String Name) {
		this.mName = Name;
	}

	public String getEngName() {
		return mEngName;
	}

	public void setEngName(String EngName) {
		this.mEngName = EngName;
	}

	public String getCountryCode() {
		return mCountryCode;
	}

	public void setCountryCode(String CountryCode) {
		this.mCountryCode = CountryCode;
	}

	public String getCountrySimpleCode() {
		return mCountrySimpleCode;
	}

	public void setCountrySimpleCode(String CountrySimpleCode) {
		this.mCountrySimpleCode = CountrySimpleCode;
	}

	public Country() {
	}

	private Country(Parcel in) {
		mId = ParcelUtils.readStringFromParcel(in);
		mName = ParcelUtils.readStringFromParcel(in);
		mEngName = ParcelUtils.readStringFromParcel(in);

		mCountryCode = ParcelUtils.readStringFromParcel(in);
		mCountrySimpleCode = ParcelUtils.readStringFromParcel(in);
		mFirstLetter = ParcelUtils.readStringFromParcel(in);
	}

	public static final Creator<Country> CREATOR = new Parcelable.Creator<Country>() {
		public Country createFromParcel(Parcel in) {
			return new Country(in);
		}

		@Override
		public Country[] newArray(int size) {
			return new Country[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, mId);
		ParcelUtils.writeStringToParcel(out, mName);
		ParcelUtils.writeStringToParcel(out, mEngName);
		ParcelUtils.writeStringToParcel(out, mCountryCode);
		ParcelUtils.writeStringToParcel(out, mCountrySimpleCode);
		ParcelUtils.writeStringToParcel(out, mFirstLetter);

	}

	@Override
	public int describeContents() {
		return 0;
	}

}
