package com.fiuba.ubademy.main.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fiuba.ubademy.BuildConfig
import com.fiuba.ubademy.R

class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val applicationIdTextView = view.findViewById<TextView>(R.id.applicationIdLabel)
        applicationIdTextView.text = BuildConfig.APPLICATION_ID
        val buildTypeTextView = view.findViewById<TextView>(R.id.buildTypeLabel)
        buildTypeTextView.text = BuildConfig.BUILD_TYPE
        return view
    }
}