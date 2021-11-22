package com.fiuba.ubademy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.fiuba.ubademy.databinding.ActivityMainBinding
import com.fiuba.ubademy.databinding.NavHeaderMainBinding
import com.fiuba.ubademy.main.MainDrawerListener
import com.fiuba.ubademy.utils.hideKeyboard

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.mainDrawerLayout
        drawerLayout.addDrawerListener(MainDrawerListener())

        val navHeaderMainBinding = DataBindingUtil.inflate<NavHeaderMainBinding>(layoutInflater, R.layout.nav_header_main, binding.mainNavView, false)
        navHeaderMainBinding.mainDrawerViewModel = MainDrawerListener.MainDrawerViewModel
        navHeaderMainBinding.lifecycleOwner = this
        binding.mainNavView.addHeaderView(navHeaderMainBinding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.mainNavView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyboard()
        val navController = findNavController(R.id.mainNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}