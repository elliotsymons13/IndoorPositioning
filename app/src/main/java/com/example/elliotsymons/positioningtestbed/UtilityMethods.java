package com.example.elliotsymons.positioningtestbed;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Any commonly used utility methods, centralised in one class to reduce code redundancy.
 */
class UtilityMethods {
    Context context;

    UtilityMethods(Context context) {
        this.context = context;
    }

    void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
