package com.fiuba.ubademy.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.fiuba.ubademy.R

class BusyFragment: DialogFragment() {
    companion object {
        private const val FRAGMENT_TAG = "Busy"

        fun show(supportFragmentManager: FragmentManager) : BusyFragment {
            val dialog = BusyFragment()
            // prevent dismiss by user click
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, FRAGMENT_TAG)
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) : View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.attributes?.dimAmount = 0.3f
        return requireActivity().layoutInflater.inflate(R.layout.fragment_busy, container)
    }
}