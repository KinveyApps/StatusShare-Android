package com.kinvey.sample.statusshare.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager

object UiUtils {
    
    fun hideKeyboard(activity: Activity?) {
        if (activity?.currentFocus != null) {
            val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }
}

