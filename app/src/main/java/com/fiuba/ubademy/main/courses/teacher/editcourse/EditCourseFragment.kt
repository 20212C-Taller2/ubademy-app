package com.fiuba.ubademy.main.courses.teacher.editcourse

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentEditCourseBinding
import com.fiuba.ubademy.utils.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

class EditCourseFragment : Fragment() {

    private lateinit var viewModel: EditCourseViewModel
    private lateinit var binding: FragmentEditCourseBinding

    private var titleValid = false
    private var descriptionValid = false

    private lateinit var getPlaceActivityResultLauncher: ActivityResultLauncher<Intent>

    private var locationPermissionsActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    private var getMultimediaActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (it.data!!.clipData != null) {
                val count: Int = it.data!!.clipData!!.itemCount

                val uris = (0 until count).map { i ->
                    it.data!!.clipData!!.getItemAt(i).uri
                }

                viewModel.selectedImageUris.postValue(uris)

                binding.selectImagesButton.text = getString(R.string.multimedia_selected, count)
            } else {
                viewModel.selectedImageUris.postValue(listOf(it.data!!.data!!))
                binding.selectImagesButton.text = getString(R.string.multimedia_selected, 1)
            }
            viewModel.selectedImageFirebasePaths.value = listOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(EditCourseViewModel::class.java)

        getPlaceActivityResultLauncher = getPlaceActivityResultLauncher(viewModel.placeName, viewModel.placeId)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_course,
            container,
            false
        )

        val course = EditCourseFragmentArgs.fromBundle(requireArguments()).selectedCourse

        viewModel.id.value = course.id
        viewModel.title.value = course.title
        viewModel.description.value = course.description
        viewModel.selectedCourseType.value = course.type
        viewModel.selectedSubscription.value = getString(resources.getIdentifier(course.subscription, "string", requireActivity().packageName))
        viewModel.placeId.value = course.location
        viewModel.selectedImageFirebasePaths.value = course.media
        binding.selectImagesButton.text = getString(R.string.multimedia_selected, course.media.count())

        binding.submitEditCourseFormButton.setOnClickListener {
            lifecycleScope.launch {
                editCourse(it)
            }
        }

        binding.placeEditCourseInput.setOnClickListener {
            getPlace(it, getPlaceActivityResultLauncher)
        }

        binding.selectImagesButton.setOnClickListener {
            selectImages()
        }

        binding.editCourseViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupValidators()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BusyFragment.show(parentFragmentManager)
        lifecycleScope.launch {
            try {
                if (viewModel.courseTypes.value?.any() != true)
                    viewModel.getCourseTypes()

                val courseTypesLabels = viewModel.courseTypes.value!!.map { item ->
                    getString(resources.getIdentifier(item, "string", requireActivity().packageName))
                }.toTypedArray()

                val adapter = ArrayAdapter(view.context, R.layout.support_simple_spinner_dropdown_item, courseTypesLabels)
                binding.courseTypeEditCourseInput.setAdapter(adapter)
                binding.courseTypeEditCourseInput.setOnItemClickListener { _, _, position, _ ->
                    viewModel.selectedCourseType.postValue(viewModel.courseTypes.value!![position])
                }
                binding.courseTypeEditCourseInput.threshold = 100
                binding.courseTypeEditCourseInput.setText(getString(resources.getIdentifier(viewModel.selectedCourseType.value, "string", requireActivity().packageName)), false)
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            }

            viewModel.placeName.value =
                if (viewModel.placeId.value != null)
                    viewModel.getPlaceName(viewModel.placeId.value!!)
                else
                    ""

            BusyFragment.hide()
        }

        if (ActivityCompat.checkSelfPermission(view.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            locationPermissionsActivityResultLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private fun setupValidators() {
        viewModel.title.observe(viewLifecycleOwner, {
            titleValid = checkTitle(it)
        })

        viewModel.description.observe(viewLifecycleOwner, {
            descriptionValid = checkDescription(it)
        })
    }

    private fun checkTitle(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.titleEditCourseLayout.showError(getString(R.string.should_have_value))

        return binding.titleEditCourseLayout.hideError()
    }

    private fun checkDescription(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.descriptionEditCourseLayout.showError(getString(R.string.should_have_value))

        return binding.descriptionEditCourseLayout.hideError()
    }

    private fun checkForm() : Boolean {
        val titleOk = titleValid || checkTitle(viewModel.title.value)
        val descriptionOk = descriptionValid || checkDescription(viewModel.description.value)
        return titleOk && descriptionOk
    }

    private fun selectImages() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        getMultimediaActivityResultLauncher.launch(intent)
    }

    private suspend fun editCourse(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

        BusyFragment.show(parentFragmentManager)

        if (viewModel.selectedImageFirebasePaths.value!!.isEmpty()) {
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
        }

        val (editCourseStatus, course) = viewModel.editCourse()
        BusyFragment.hide()

        when (editCourseStatus) {
            EditCourseStatus.SUCCESS -> {
                Toast.makeText(context, R.string.changes_have_been_saved, Toast.LENGTH_LONG).show()
                view.findNavController().navigate(EditCourseFragmentDirections.actionEditCourseFragmentToManageCourseFragment(course!!))
            }
            EditCourseStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }
}