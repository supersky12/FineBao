package com.yujunkang.fangxinbao.utility;





/**
 * 
 * @date 2014-6-2
 * @author xieb
 * 
 */
public class IconUtils {
	private static IconUtils mInstance;
    private boolean mRequestHighDensityIcons;
    
    private IconUtils() {
        mRequestHighDensityIcons = false;
    }
    
    public static IconUtils get() {
        if (mInstance == null) {
            mInstance = new IconUtils();
        }
        return mInstance;
    }

    public boolean getRequestHighDensityIcons() {
        return mRequestHighDensityIcons;
    }
    
    public void setRequestHighDensityIcons(boolean requestHighDensityIcons) {
        mRequestHighDensityIcons = requestHighDensityIcons;
    }
}


