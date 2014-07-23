package com.yujunkang.fangxinbao.control.validator;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * 
 * @date 2014-6-4
 * @author xieb
 * 
 */
public class ValidatingEditTextPreference extends EditTextPreference {
	public ValidatingEditTextPreference(Context context) {
		super(context);
		// FIXME how should this constructor be handled
		throw new RuntimeException("Not supported");
	}

	public ValidatingEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		editTextValidator = new DefaultEditTextValidator(getEditText(), attrs,
				context);
	}

	public ValidatingEditTextPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		editTextValidator = new DefaultEditTextValidator(getEditText(), attrs,
				context);
	}

	public EditTextValidator getEditTextValidator() {
		return editTextValidator;
	}

	public void setEditTextValidator(EditTextValidator editTextValidator) {
		this.editTextValidator = editTextValidator;
	}

	@Override
	protected void showDialog(Bundle state) {
		super.showDialog(state);

		if (super.getDialog() instanceof AlertDialog) {
			final AlertDialog theDialog = (AlertDialog) super.getDialog();

			int padding = Integer.MIN_VALUE;
			try {
				padding = ((LinearLayout) getEditText().getParent())
						.getPaddingBottom();
			} catch (Exception e) {

			}

			final int originalBottomPadding = padding;

			Button b = theDialog.getButton(AlertDialog.BUTTON_POSITIVE);

			ValidatingOnClickListener l = new ValidatingOnClickListener(
					originalBottomPadding, theDialog);
			b.setOnClickListener(l);

			getEditText().setOnEditorActionListener(l);
		}
	}

	private final class ValidatingOnClickListener implements
			View.OnClickListener, OnEditorActionListener {
		private ValidatingOnClickListener(int originalBottomPadding,
				AlertDialog theDialog) {
			this.originalBottomPadding = originalBottomPadding;
			this.theDialog = theDialog;
		}

		public void onClick(View view) {
			performValidation();
		}

		public void performValidation() {
			getEditText().setError(null);
			if (editTextValidator.testValidity()) {

				theDialog.dismiss();
				ValidatingEditTextPreference.this.onClick(theDialog,
						AlertDialog.BUTTON_POSITIVE);

				if (originalBottomPadding != Integer.MIN_VALUE) {
					LinearLayout parentLayout = (LinearLayout) getEditText()
							.getParent();

					if (originalBottomPadding == parentLayout
							.getPaddingBottom()) {
						parentLayout.setPadding(parentLayout.getPaddingLeft(),
								parentLayout.getPaddingTop(),
								parentLayout.getPaddingRight(),
								originalBottomPadding);
					}
				}
			} else {

				if (originalBottomPadding != Integer.MIN_VALUE) {
					LinearLayout parentLayout = (LinearLayout) getEditText()
							.getParent();

					if (originalBottomPadding == parentLayout
							.getPaddingBottom()) {
						parentLayout
								.setPadding(
										parentLayout.getPaddingLeft(),
										parentLayout.getPaddingTop(),
										parentLayout.getPaddingRight(),
										(int) (parentLayout.getPaddingBottom() + getEditText()
												.getHeight() * 1.05));
					}
				}

			}
		}

		private final int originalBottomPadding;

		private final AlertDialog theDialog;

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			performValidation();
			return true;
		}

	}

	private EditTextValidator editTextValidator;

}
