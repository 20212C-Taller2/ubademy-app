package com.fiuba.ubademy.main

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.fiuba.ubademy.utils.hideKeyboard

class MainDrawerListener : DrawerLayout.DrawerListener {
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        drawerView.requestFocus()
        drawerView.hideKeyboard()
    }
    override fun onDrawerOpened(drawerView: View) { }
    override fun onDrawerClosed(drawerView: View) { }
    override fun onDrawerStateChanged(newState: Int) { }
}