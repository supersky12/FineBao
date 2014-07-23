package com.yujunkang.fangxinbao.execption;



/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class FangXinBaoException extends Exception {
	 private static final long serialVersionUID = 1L;
	    
	    private String mExtra;

	    public FangXinBaoException(String message) {
	        super(message);
	    }

	    public FangXinBaoException(String message, String extra) {
	        super(message);
	        mExtra = extra;
	    }
	    
	    public String getExtra() {
	        return mExtra;
	    }
}


