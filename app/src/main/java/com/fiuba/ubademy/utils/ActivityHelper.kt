package com.fiuba.ubademy.utils

import android.app.Activity

fun Activity.hideKeyboard() {
    currentFocus?.hideKeyboard()
}