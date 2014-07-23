package com.yujunkang.fangxinbao.control.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;



/**
 * 
 * @date 2014-6-4
 * @author xieb
 * 
 */
public class FormEditText extends EditText {
    public FormEditText(Context context) {
        super(context);
      
        throw new RuntimeException("Not supported");
    }

    public FormEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        editTextValidator = new DefaultEditTextValidator(this, attrs, context);
    }

    public FormEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        editTextValidator = new DefaultEditTextValidator(this, attrs, context);

    }

    /**
     * 添加一个验证器来此FormEditText
     *
     */
    public void addValidator(Validator theValidator) throws IllegalArgumentException {
        editTextValidator.addValidator(theValidator);
    }

    public EditTextValidator getEditTextValidator() {
        return editTextValidator;
    }

    public void setEditTextValidator(EditTextValidator editTextValidator) {
        this.editTextValidator = editTextValidator;
    }

  
    public boolean testValidity() {
        return editTextValidator.testValidity();
    }

    private EditTextValidator editTextValidator;


   
    private Drawable lastErrorIcon = null;

   
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (TextUtils.isEmpty(getText().toString())
                && keyCode == KeyEvent.KEYCODE_DEL)
            return true;
        else
            return super.onKeyPreIme(keyCode, event);
    }

 
    @Override
    public void setError(CharSequence error, Drawable icon) {
        super.setError(error, icon);
        lastErrorIcon = icon;
        if (error != null /* !isFocused() && */) {
            showErrorIconHax(icon);
        }
    }


  
    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        showErrorIconHax(lastErrorIcon);
    }

  
    private void showErrorIconHax(Drawable icon) {
        if (icon == null)
            return;

        // only for JB 4.2 and 4.2.1
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.JELLY_BEAN
                && android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.JELLY_BEAN_MR1)
            return;

        try {
            Class<?> textview = Class.forName("android.widget.TextView");
            Field tEditor = textview.getDeclaredField("mEditor");
            tEditor.setAccessible(true);
            Class<?> editor = Class.forName("android.widget.Editor");
            Method privateShowError = editor.getDeclaredMethod("setErrorIcon",
                    Drawable.class);
            privateShowError.setAccessible(true);
            privateShowError.invoke(tEditor.get(this), icon);
        } catch (Exception e) {
           
        }
    }


}
