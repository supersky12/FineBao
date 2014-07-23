package com.yujunkang.fangxinbao.control.validator;

import android.widget.EditText;

/**
 * 
 * @date 2014-6-4
 * @author xieb
 * 
 */
public abstract class Validator {
	protected String errorMessage;

	public Validator(String _customErrorMessage) {
		errorMessage = _customErrorMessage;
	}

	public abstract boolean isValid(EditText et);

	public boolean hasErrorMessage() {
		return errorMessage != null;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
