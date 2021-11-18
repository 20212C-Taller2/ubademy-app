package com.fiuba.ubademy.main.courses.teacher.addcourse

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentAddCourseBinding
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.hideError
import com.fiuba.ubademy.utils.hideKeyboard
import com.fiuba.ubademy.utils.showError
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class AddCourseFragment : Fragment() {

    private lateinit var viewModel: AddCourseViewModel
    private lateinit var binding: FragmentAddCourseBinding

    private var titleValid = false
    private var descriptionValid = false
    private var selectedCourseTypeValid = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AddCourseViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_course,
            container,
            false
        )

        binding.submitAddCourseFormButton.setOnClickListener {
            lifecycleScope.launch {
                addCourse(it)
            }
        }

        binding.addCourseViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupValidators()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            try {
                if (viewModel.courseTypes.value?.any() != true)
                    viewModel.getCourseTypes()

                val courseTypesLabels = viewModel.courseTypes.value!!.map {
                    item -> getString(resources.getIdentifier(item, "string", requireActivity().packageName))
                }.toTypedArray()

                val adapter = ArrayAdapter(view.context, R.layout.support_simple_spinner_dropdown_item, courseTypesLabels)
                binding.courseTypeAddCourseInput.setAdapter(adapter)
                binding.courseTypeAddCourseInput.setOnItemClickListener { _, _, position, _ ->
                    viewModel.selectedCourseType.postValue(viewModel.courseTypes.value!![position])
                }
                binding.courseTypeAddCourseInput.threshold = 100
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupValidators() {
        viewModel.title.observe(viewLifecycleOwner, {
            titleValid = checkTitle(it)
        })

        viewModel.description.observe(viewLifecycleOwner, {
            descriptionValid = checkDescription(it)
        })

        viewModel.selectedCourseType.observe(viewLifecycleOwner, {
            selectedCourseTypeValid = checkSelectedCourseType(it)
        })
    }

    private fun checkTitle(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.titleAddCourseLayout.showError(getString(R.string.should_have_value))

        return binding.titleAddCourseLayout.hideError()
    }

    private fun checkDescription(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.descriptionAddCourseLayout.showError(getString(R.string.should_have_value))

        return binding.descriptionAddCourseLayout.hideError()
    }

    private fun checkSelectedCourseType(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.courseTypeAddCourseLayout.showError(getString(R.string.should_have_value))

        return binding.courseTypeAddCourseLayout.hideError()
    }

    private fun checkForm() : Boolean {
        val titleOk = titleValid || checkTitle(viewModel.title.value)
        val descriptionOk = descriptionValid || checkDescription(viewModel.description.value)
        val selectedCourseTypeOk = selectedCourseTypeValid || checkSelectedCourseType(viewModel.selectedCourseType.value)
        return titleOk && descriptionOk && selectedCourseTypeOk
    }

    private suspend fun addCourse(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

        BusyFragment.show(this.parentFragmentManager)
        val addCourseStatus : AddCourseStatus = viewModel.addCourse()
        BusyFragment.hide()

        when (addCourseStatus) {
            AddCourseStatus.SUCCESS -> {
                Toast.makeText(context, R.string.course_added, Toast.LENGTH_LONG).show()
                view.findNavController().navigate(AddCourseFragmentDirections.actionAddCourseFragmentToTeacherCoursesFragment())
            }
            AddCourseStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }
}