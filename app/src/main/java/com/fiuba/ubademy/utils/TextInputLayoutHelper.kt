package com.fiuba.ubademy.utils

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.showError(text : String) : Boolean {
    isErrorEnabled = true
    error = text
    return false
}

fun TextInputLayout.hideError() : Boolean {
    isErrorEnabled = false
    error = ""
    return true
}