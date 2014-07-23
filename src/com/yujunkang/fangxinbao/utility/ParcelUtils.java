package com.yujunkang.fangxinbao.utility;

import android.os.Parcel;



/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class ParcelUtils {
	  public static void writeStringToParcel(Parcel out, String str) {
	        if (str != null) {
	            out.writeInt(1);
	            out.writeString(str);
	        } else {
	            out.writeInt(0);
	        }
	    }
	    
	    public static String readStringFromParcel(Parcel in) {
	        int flag = in.readInt();
	        if (flag == 1) {
	            return in.readString();
	        } else {
	            return null;
	        }
	    }
}


