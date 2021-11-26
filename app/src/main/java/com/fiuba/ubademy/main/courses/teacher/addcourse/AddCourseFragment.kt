package com.fiuba.ubademy.main.courses.teacher.addcourse

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentAddCourseBinding
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.hideError
import com.fiuba.ubademy.utils.hideKeyboard
import com.fiuba.ubademy.utils.showError
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

class AddCourseFragment : Fragment() {

    private lateinit var viewModel: AddCourseViewModel
    private lateinit var binding: FragmentAddCourseBinding

    private var titleValid = false
    private var descriptionValid = false
    private var selectedCourseTypeValid = false
    private var selectedImageUrisValid = false

    private var arl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (it.data!!.clipData != null) {
                val count: Int = it.data!!.clipData!!.itemCount

                val uris = (0 until count).map { i ->
                    it.data!!.clipData!!.getItemAt(i).uri
                }

                viewModel.selectedImageUris.postValue(uris)

                binding.selectImagesButton.text = getString(R.string.multimedia_selected, count)
            }
        }
    }

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

        binding.selectImagesButton.setOnClickListener {
            selectImages()
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

        viewModel.selectedImageUris.observe(viewLifecycleOwner, {
            selectedImageUrisValid = checkSelectedImageUris(it)
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

    private fun checkSelectedImageUris(newValue: List<Uri>?) : Boolean {
        if (newValue == null || newValue.isEmpty()) {
            Toast.makeText(context, R.string.should_select_multimedia, Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun checkForm() : Boolean {
        val titleOk = titleValid || checkTitle(viewModel.title.value)
        val descriptionOk = descriptionValid || checkDescription(viewModel.description.value)
        val selectedCourseTypeOk = selectedCourseTypeValid || checkSelectedCourseType(viewModel.selectedCourseType.value)
        val selectedImageUrisOk = selectedImageUrisValid || checkSelectedImageUris(viewModel.selectedImageUris.value)
        return titleOk && descriptionOk && selectedCourseTypeOk && selectedImageUrisOk
    }

    private fun selectImages() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        arl.launch(intent)
    }

    private suspend fun addCourse(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

        BusyFragment.show(this.parentFragmentManager)

        try {
            viewModel.selectedImageUris.value!!.forEach {
                val storageReference = FirebaseStorage.getInstance().getReference(it.lastPathSegment!! + ":" + UUID.randomUUID())
                storageReference.putFile(it).await()
                viewModel.selectedImageFirebasePaths.value = viewModel.selectedImageFirebasePaths.value?.plus(storageReference.path)
            }
        } catch (e: Exception) {
            Timber.e(e)
            Toast.makeText(context, R.string.unable_to_submit_multimedia, Toast.LENGTH_LONG).show()
            BusyFragment.hide()
            return
        }

        val (addCourseStatus, course) = viewModel.addCourse()
        BusyFragment.hide()

        when (addCourseStatus) {
            AddCourseStatus.SUCCESS -> {
                Toast.makeText(context, R.string.course_added, Toast.LENGTH_LONG).show()
                view.findNavController().navigate(AddCourseFragmentDirections.actionAddCourseFragmentToManageCourseFragment(course!!))
            }
            AddCourseStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }
}