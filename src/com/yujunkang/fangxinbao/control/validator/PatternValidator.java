package com.yujunkang.fangxinbao.control.validator;

import java.util.regex.Pattern;

import android.widget.EditText;



/**
 * 
 * @date 2014-6-4
 * @author xieb
 * 
 */
public class PatternValidator extends Validator{
	private Pattern pattern;
	public PatternValidator(String _customErrorMessage, Pattern _pattern) {
		super(_customErrorMessage);
		if (_pattern == null) throw new IllegalArgumentException("_pattern must not be null");
		pattern = _pattern;
	}

	public boolean isValid(EditText et) {
		return pattern.matcher(et.getText()).matches();
	}

}


