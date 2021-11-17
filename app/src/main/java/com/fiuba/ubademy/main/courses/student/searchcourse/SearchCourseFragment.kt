package com.fiuba.ubademy.main.courses.student.searchcourse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentSearchCourseBinding
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class SearchCourseFragment : Fragment() {

    private lateinit var viewModel: SearchCourseViewModel
    private lateinit var binding: FragmentSearchCourseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(SearchCourseViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_course,
            container,
            false
        )

        binding.searchCourseViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            try {
                if (viewModel.courseTypes.value?.any() != true)
                    viewModel.getCourseTypes()

                val courseTypesLabels = arrayOf(getString(R.string.all_m)) + viewModel.courseTypes.value!!.drop(1).map {
                    item -> getString(resources.getIdentifier(item, "string", requireActivity().packageName))
                }.toTypedArray()

                val adapter = ArrayAdapter(view.context, R.layout.support_simple_spinner_dropdown_item, courseTypesLabels)
                binding.courseTypeSearchCourseInput.setAdapter(adapter)
                binding.courseTypeSearchCourseInput.setOnItemClickListener { _, _, position, _ ->
                    viewModel.selectedCourseType.postValue(viewModel.courseTypes.value!![position])
                }
                binding.courseTypeSearchCourseInput.threshold = 100
                binding.courseTypeSearchCourseInput.setText(getString(R.string.all_m), false)
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            }
        }
    }
}