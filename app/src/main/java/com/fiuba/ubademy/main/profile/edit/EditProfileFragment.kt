package com.fiuba.ubademy.main.profile.edit

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentEditProfileBinding
import com.fiuba.ubademy.utils.*
import kotlinx.coroutines.launch
import timber.log.Timber

class EditProfileFragment : Fragment() {

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var sharedPreferencesData: SharedPreferencesData

    private var firstNameValid = false
    private var lastNameValid = false
    private var emailValid = false

    private lateinit var getPlaceActivityResultLauncher : ActivityResultLauncher<Intent>

    private var locationPermissionsActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    private lateinit var courseTypesLabels : Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)

        getPlaceActivityResultLauncher = getPlaceActivityResultLauncher(viewModel.placeName, viewModel.placeId)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_profile,
            container,
            false
        )

        binding.editProfileButton.setOnClickListener {
            editProfile(it)
        }

        binding.submitEditProfileFormButton.setOnClickListener {
            lifecycleScope.launch {
                saveEditProfile(it)
            }
        }

        binding.placeEditProfileInput.setOnClickListener {
            getPlace(it, getPlaceActivityResultLauncher)
        }

        binding.editProfileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        sharedPreferencesData = requireContext().getSharedPreferencesData()

        setupValidators()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            try {
                if (viewModel.courseTypes.value?.any() != true)
                    viewModel.getCourseTypes()

                courseTypesLabels = viewModel.courseTypes.value!!.map {
                    item -> getString(resources.getIdentifier(item, "string", requireActivity().packageName))
                }.toTypedArray()

                viewModel.selectedCourseTypesText.postValue(
                    courseTypesLabels
                        .filterIndexed { index, _ -> viewModel.selectedCourseTypes.value!![index] }
                        .joinToString(separator = ", ") { item -> item })

                binding.interestsEditProfileInput.setOnClickListener {
                    openInterestsDialog()
                }
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            }
        }

        if (sharedPreferencesData.loggedInWithGoogle) {
            binding.firstNameEditProfileLayout.visibility = View.GONE
            binding.lastNameEditProfileLayout.visibility = View.GONE
            binding.displayNameEditProfileLayout.visibility = View.VISIBLE
            binding.placeEditProfileLayout.visibility = View.VISIBLE
            binding.emailEditProfileLayout.visibility = View.VISIBLE
            binding.interestsEditProfileLayout.visibility = View.VISIBLE
        } else {
            binding.firstNameEditProfileLayout.visibility = View.VISIBLE
            binding.lastNameEditProfileLayout.visibility = View.VISIBLE
            binding.displayNameEditProfileLayout.visibility = View.GONE
            binding.placeEditProfileLayout.visibility = View.VISIBLE
            binding.emailEditProfileLayout.visibility = View.VISIBLE
            binding.interestsEditProfileLayout.visibility = View.VISIBLE
        }

        if (viewModel.editInProgress.value!!)
            enableForm()
        else
            disableForm()
    }

    private fun editProfile(view: View) {
        enableForm()
        if (ActivityCompat.checkSelfPermission(view.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            locationPermissionsActivityResultLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private fun enableForm() {
        viewModel.editInProgress.value = true
        binding.editProfileButton.visibility = View.GONE
        binding.submitEditProfileFormButton.visibility = View.VISIBLE

        if (sharedPreferencesData.loggedInWithGoogle) {
            binding.firstNameEditProfileLayout.isEnabled = false
            binding.lastNameEditProfileLayout.isEnabled = false
            binding.displayNameEditProfileLayout.isEnabled = false
            binding.placeEditProfileLayout.isEnabled = true
            binding.emailEditProfileLayout.isEnabled = false
            binding.interestsEditProfileLayout.isEnabled = true
        } else {
            binding.firstNameEditProfileLayout.isEnabled = true
            binding.lastNameEditProfileLayout.isEnabled = true
            binding.displayNameEditProfileLayout.isEnabled = false
            binding.placeEditProfileLayout.isEnabled = true
            binding.emailEditProfileLayout.isEnabled = true
            binding.interestsEditProfileLayout.isEnabled = true
        }
    }

    private fun disableForm() {
        viewModel.editInProgress.value = false
        binding.editProfileButton.visibility = View.VISIBLE
        binding.submitEditProfileFormButton.visibility = View.GONE

        binding.firstNameEditProfileLayout.isEnabled = false
        binding.lastNameEditProfileLayout.isEnabled = false
        binding.displayNameEditProfileLayout.isEnabled = false
        binding.placeEditProfileLayout.isEnabled = false
        binding.emailEditProfileLayout.isEnabled = false
        binding.interestsEditProfileLayout.isEnabled = false
    }

    private fun setupValidators() {
        if (!sharedPreferencesData.loggedInWithGoogle) {
            viewModel.firstName.observe(viewLifecycleOwner, {
                firstNameValid = checkFirstName(it)
            })

            viewModel.lastName.observe(viewLifecycleOwner, {
                lastNameValid = checkLastName(it)
            })

            viewModel.email.observe(viewLifecycleOwner, {
                emailValid = checkEmail(it)
            })
        }
    }

    private fun checkFirstName(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.firstNameEditProfileLayout.showError(getString(R.string.should_have_value))

        return binding.firstNameEditProfileLayout.hideError()
    }

    private fun checkLastName(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.lastNameEditProfileLayout.showError(getString(R.string.should_have_value))

        return binding.lastNameEditProfileLayout.hideError()
    }

    private fun checkEmail(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.emailEditProfileLayout.showError(getString(R.string.should_have_value))

        if (!Patterns.EMAIL_ADDRESS.matcher(newValue).matches())
            return binding.emailEditProfileLayout.showError(getString(R.string.should_be_valid_email))

        return binding.emailEditProfileLayout.hideError()
    }

    private fun checkForm() : Boolean {
        if (sharedPreferencesData.loggedInWithGoogle)
            return true

        val firstNameOk = firstNameValid || checkFirstName(viewModel.firstName.value)
        val lastNameOk = lastNameValid || checkLastName(viewModel.lastName.value)
        val emailOk = emailValid || checkEmail(viewModel.email.value)
        return firstNameOk && lastNameOk && emailOk
    }

    private fun openInterestsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setTitle(getString(R.string.select_interests))

        builder.setCancelable(true)

        builder.setMultiChoiceItems(courseTypesLabels, viewModel.selectedCourseTypes.value) { _, _, _ ->
            viewModel.selectedCourseTypesText.postValue(
                courseTypesLabels
                    .filterIndexed { index, _ -> viewModel.selectedCourseTypes.value!![index] }
                    .joinToString(separator = ", ") { item -> item })
        }

        builder.setPositiveButton(R.string.ok) { _, _ ->
        }

        builder.show()
    }

    private suspend fun saveEditProfile(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

        if (sharedPreferencesData.loggedInWithGoogle && viewModel.placeId.value.isNullOrBlank()) {
            disableForm()
            return
        }

        BusyFragment.show(parentFragmentManager)
        val editProfileStatus : EditProfileStatus = viewModel.editProfile()
        BusyFragment.hide()

        when (editProfileStatus) {
            EditProfileStatus.SUCCESS -> {
                Toast.makeText(context, R.string.changes_have_been_saved, Toast.LENGTH_LONG).show()
                disableForm()
            }
            EditProfileStatus.EMAIL_ALREADY_USED -> Toast.makeText(context, R.string.email_already_used, Toast.LENGTH_LONG).show()
            EditProfileStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }
}