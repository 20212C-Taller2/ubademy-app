package com.fiuba.ubademy.auth.createaccount

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
import androidx.navigation.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentCreateAccountBinding
import com.fiuba.ubademy.utils.*
import kotlinx.coroutines.launch
import timber.log.Timber

class CreateAccountFragment : Fragment() {

    private lateinit var viewModel: CreateAccountViewModel
    private lateinit var binding: FragmentCreateAccountBinding

    private var firstNameValid = false
    private var lastNameValid = false
    private var emailValid = false
    private var passwordValid = false

    private lateinit var getPlaceActivityResultLauncher: ActivityResultLauncher<Intent>

    private var locationPermissionsActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    private lateinit var courseTypesLabels : Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CreateAccountViewModel::class.java)

        getPlaceActivityResultLauncher = getPlaceActivityResultLauncher(viewModel.placeName, viewModel.placeId)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_account,
            container,
            false
        )

        binding.submitCreateAccountFormButton.setOnClickListener {
            lifecycleScope.launch {
                createAccount(it)
            }
        }

        binding.placeCreateAccountInput.setOnClickListener {
            getPlace(it, getPlaceActivityResultLauncher)
        }

        binding.createAccountViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupValidators()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ActivityCompat.checkSelfPermission(view.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            locationPermissionsActivityResultLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))

        lifecycleScope.launch {
            try {
                if (viewModel.courseTypes.value?.any() != true)
                    viewModel.getCourseTypes()

                courseTypesLabels = viewModel.courseTypes.value!!.map {
                    item -> getString(resources.getIdentifier(item, "string", requireActivity().packageName))
                }.toTypedArray()

                binding.interestsCreateAccountInput.setOnClickListener {
                    openInterestsDialog()
                }
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupValidators() {
        viewModel.firstName.observe(viewLifecycleOwner, {
            firstNameValid = checkFirstName(it)
        })

        viewModel.lastName.observe(viewLifecycleOwner, {
            lastNameValid = checkLastName(it)
        })

        viewModel.email.observe(viewLifecycleOwner, {
            emailValid = checkEmail(it)
        })

        viewModel.password.observe(viewLifecycleOwner, {
            passwordValid = checkPassword(it)
        })
    }

    private fun checkFirstName(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.firstNameCreateAccountLayout.showError(getString(R.string.should_have_value))

        return binding.firstNameCreateAccountLayout.hideError()
    }

    private fun checkLastName(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.lastNameCreateAccountLayout.showError(getString(R.string.should_have_value))

        return binding.lastNameCreateAccountLayout.hideError()
    }

    private fun checkEmail(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.emailCreateAccountLayout.showError(getString(R.string.should_have_value))

        if (!Patterns.EMAIL_ADDRESS.matcher(newValue).matches())
            return binding.emailCreateAccountLayout.showError(getString(R.string.should_be_valid_email))

        return binding.emailCreateAccountLayout.hideError()
    }

    private fun checkPassword(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.passwordCreateAccountLayout.showError(getString(R.string.should_have_value))

        val passwordMinimumLength = 6
        if (newValue.length < passwordMinimumLength) {
            return binding.passwordCreateAccountLayout.showError(getString(R.string.should_be_valid_password, passwordMinimumLength))
        }

        return binding.passwordCreateAccountLayout.hideError()
    }

    private fun checkForm() : Boolean {
        val firstNameOk = firstNameValid || checkFirstName(viewModel.firstName.value)
        val lastNameOk = lastNameValid || checkLastName(viewModel.lastName.value)
        val emailOk = emailValid || checkEmail(viewModel.email.value)
        val passwordOk = passwordValid || checkPassword(viewModel.password.value)
        return firstNameOk && lastNameOk && emailOk && passwordOk
    }

    private suspend fun createAccount(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

        BusyFragment.show(parentFragmentManager)
        val createAccountStatus : CreateAccountStatus = viewModel.createAccount()
        BusyFragment.hide()

        when (createAccountStatus) {
            CreateAccountStatus.SUCCESS -> {
                view.findNavController().navigate(CreateAccountFragmentDirections.actionCreateAccountFragmentToAccountCreatedFragment())
            }
            CreateAccountStatus.EMAIL_ALREADY_USED -> Toast.makeText(context, R.string.email_already_used, Toast.LENGTH_LONG).show()
            CreateAccountStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }

    private fun openInterestsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setTitle(getString(R.string.select_interests))

        builder.setCancelable(true)

        builder.setMultiChoiceItems(courseTypesLabels, viewModel.selectedCourseTypes.value) { _, _, _ ->
            val selectedCourseTypes = courseTypesLabels.filterIndexed { index, _ -> viewModel.selectedCourseTypes.value!![index] }
            viewModel.selectedCourseTypesText.postValue(selectedCourseTypes.joinToString(separator = ", ") { item -> item })
        }

        builder.setPositiveButton(R.string.ok) { _, _ ->
        }

        builder.show()
    }
}