package com.yujunkang.fangxinbao.control.validator;

import android.content.Context;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * 
 * @date 2014-6-4
 * @author xieb
 * 
 */
public interface EditTextValidator {

	public void addValidator(Validator theValidator)
			throws IllegalArgumentException;

	public TextWatcher getTextWatcher();

	public boolean isEmptyAllowed();

	public void resetValidators(Context context);

	public boolean testValidity();

	public boolean testValidity(boolean showUIError);

	public void showUIError();

	final int EDIT_EMAIL = 1;

	final int EDIT_PHONE = 2;
	final int EDIT_NOCHECK = 10;

}
