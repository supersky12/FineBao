package com.yujunkang.fangxinbao.control.validator;

import com.yujunkang.fangxinbao.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Default implementation of an {@link EditTextValidator}
 */
public class DefaultEditTextValidator implements EditTextValidator {

	public DefaultEditTextValidator(EditText editText, AttributeSet attrs,
			Context context) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.FormEditText);
		emptyAllowed = typedArray.getBoolean(
				R.styleable.FormEditText_emptyAllowed, false);
		editType = typedArray.getInt(R.styleable.FormEditText_editType,
				EditTextValidator.EDIT_NOCHECK);
		testErrorString = typedArray
				.getString(R.styleable.FormEditText_testErrorString);
		classType = typedArray.getString(R.styleable.FormEditText_classType);
		customRegexp = typedArray
				.getString(R.styleable.FormEditText_customRegexp);
		emptyErrorString = typedArray
				.getString(R.styleable.FormEditText_emptyErrorString);
		customFormat = typedArray
				.getString(R.styleable.FormEditText_customFormat);
		typedArray.recycle();

		setEditText(editText);
		resetValidators(context);

	}

	@Override
	public void addValidator(Validator theValidator)
			throws IllegalArgumentException {
		if (theValidator == null) {
			throw new IllegalArgumentException(
					"theValidator argument should not be null");
		}
		mValidator.enqueue(theValidator);
	}

	public String getClassType() {
		return classType;
	}

	public String getCustomRegexp() {
		return customRegexp;
	}

	public EditText getEditText() {
		return editText;
	}

	public String getTestErrorString() {
		return testErrorString;
	}

	public int getTestType() {
		return editType;
	}

	@Override
	public TextWatcher getTextWatcher() {
		if (tw == null) {
			tw = new TextWatcher() {

				public void afterTextChanged(Editable s) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					if (s != null && s.length() > 0
							&& editText.getError() != null) {
						editText.setError(null);
					}
				}
			};
		}
		return tw;
	}

	@Override
	public boolean isEmptyAllowed() {
		return emptyAllowed;
	}

	@Override
	public void resetValidators(Context context) {
		// its possible the context may have changed so re-get the
		// defaultEmptyErrorString
		defaultEmptyErrorString = context
				.getString(R.string.error_field_must_not_be_empty);
		setEmptyErrorString(emptyErrorString);

		mValidator = new AndValidator();
		Validator toAdd;

		switch (editType) {
		default:
		case EDIT_NOCHECK:
			toAdd = new DummyValidator();
			break;

		case EDIT_EMAIL:
			toAdd = new EmailValidator(
					TextUtils.isEmpty(testErrorString) ? context
							.getString(R.string.error_email_address_not_valid)
							: testErrorString);
			break;
		case EDIT_PHONE:
			toAdd = new PhoneValidator(
					TextUtils.isEmpty(testErrorString) ? context
							.getString(R.string.error_phone_not_valid)
							: testErrorString);
			break;

		}

		MultiValidator tmpValidator;
		if (!emptyAllowed) { 
			tmpValidator = new AndValidator();
			tmpValidator.enqueue(new EmptyValidator(emptyErrorStringActual));
			tmpValidator.enqueue(toAdd);
		} else {
			tmpValidator = new OrValidator(toAdd.getErrorMessage(),
					new NotValidator(null, new EmptyValidator(null)), toAdd);
		}

		addValidator(tmpValidator);
	}

	public void setEditText(EditText editText) {
		if (this.editText != null) {
			this.editText.removeTextChangedListener(getTextWatcher());
		}
		this.editText = editText;
		editText.addTextChangedListener(getTextWatcher());
	}

	public void setEmptyAllowed(boolean emptyAllowed, Context context) {
		this.emptyAllowed = emptyAllowed;
		resetValidators(context);
	}

	public void setEmptyErrorString(String emptyErrorString) {
		if (!TextUtils.isEmpty(emptyErrorString)) {
			emptyErrorStringActual = emptyErrorString;
		} else {
			emptyErrorStringActual = defaultEmptyErrorString;
		}
	}

	public void setTestErrorString(String testErrorString, Context context) {
		this.testErrorString = testErrorString;
		resetValidators(context);
	}

	public void setTestType(int testType, Context context) {
		this.editType = testType;
		resetValidators(context);
	}

	@Override
	public boolean testValidity() {
		return testValidity(true);
	}

	@Override
	public boolean testValidity(boolean showUIError) {
		boolean isValid = mValidator.isValid(editText);
		if (!isValid && showUIError) {
			showUIError();
		}
		return isValid;
	}

	@Override
	public void showUIError() {
		if (mValidator.hasErrorMessage()) {
			editText.setError(mValidator.getErrorMessage());
		}
	}

	private TextWatcher tw;

	private String defaultEmptyErrorString;

	/**
	 * The custom validators setted using
	 */
	protected MultiValidator mValidator;

	protected String testErrorString;

	protected boolean emptyAllowed;

	protected EditText editText;

	protected int editType;

	protected String classType;

	protected String customRegexp;

	protected String customFormat;

	protected String emptyErrorStringActual;

	protected String emptyErrorString;

}
