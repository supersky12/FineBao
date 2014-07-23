package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class User extends BaseData implements Parcelable {

	private String mId="";
	private String mPhone="";
	private String mEmail="";

	private Group<Baby> babies;

	private Country country;
	

	private String temperature="";

	public User() {
	}

	private User(Parcel in) {
		mId = ParcelUtils.readStringFromParcel(in);
		mPhone = ParcelUtils.readStringFromParcel(in);
		mEmail = ParcelUtils.readStringFromParcel(in);

		if (in.readInt() == 1) {
			country = in.readParcelable(Country.class.getClassLoader());
		}
		temperature = ParcelUtils.readStringFromParcel(in);
		babies = new Group<Baby>();
		int numBabies = in.readInt();
		for (int i = 0; i < numBabies; i++) {
			Baby item = in.readParcelable(Baby.class.getClassLoader());
			babies.add(item);
		}

	}

	public static final Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	public Group<Baby> getBaBies() {
		return babies;
	}

	public void setBaBies(Group<Baby> baby) {
		babies = baby;
	}

	

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String email) {
		mEmail = email;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public void setPhone(String phone) {
		mPhone = phone;
	}

	
	public String getPhone() {
		return mPhone;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, mId);
		ParcelUtils.writeStringToParcel(out, mPhone);
		ParcelUtils.writeStringToParcel(out, mEmail);
		if (country != null) {
			out.writeInt(1);
			out.writeParcelable(country, flags);
		} else {
			out.writeInt(0);
		}

		ParcelUtils.writeStringToParcel(out, temperature);

		if (babies != null) {
			out.writeInt(babies.size());
			for (int i = 0; i < babies.size(); i++) {
				out.writeParcelable(babies.get(i), flags);
			}
		} else {
			out.writeInt(0);
		}

	}

	@Override
	public int describeContents() {
		return 0;
	}

}
