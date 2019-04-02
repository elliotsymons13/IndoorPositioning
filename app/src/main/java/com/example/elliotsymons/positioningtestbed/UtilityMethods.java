package com.example.elliotsymons.positioningtestbed;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class UtilityMethods {
    Context context;

    public UtilityMethods(Context context) {
        this.context = context;
    }

    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
