package com.fiuba.ubademy.auth.createaccount

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentCreateAccountBinding
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.hideError
import com.fiuba.ubademy.utils.hideKeyboard
import com.fiuba.ubademy.utils.showError
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.coroutines.launch
import java.lang.Exception

class CreateAccountFragment : Fragment() {

    private lateinit var viewModel: CreateAccountViewModel
    private lateinit var binding: FragmentCreateAccountBinding

    private var firstNameValid = false
    private var lastNameValid = false
    private var emailValid = false
    private var passwordValid = false

    private var getPlaceActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            RESULT_OK -> {
                val place = Autocomplete.getPlaceFromIntent(it.data!!)
                viewModel.placeName.value = place.address
                viewModel.placeId.value = place.id
            }
            RESULT_CANCELED -> {
            }
            AutocompleteActivity.RESULT_ERROR -> {
            }
        }
    }

    private var locationPermissionsActivityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var courseTypesLabels : Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CreateAccountViewModel::class.java)

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
            getPlace(it)
        }

        binding.createAccountViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupValidators()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

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

        BusyFragment.show(this.parentFragmentManager)
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

    private fun getPlace(view: View) {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)

        if (ActivityCompat.checkSelfPermission(view.context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    val bounds = RectangularBounds.newInstance(
                        LatLng(it.latitude - 1, it.longitude - 1),
                        LatLng(it.latitude + 1, it.latitude + 1)
                    )
                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setTypeFilter(TypeFilter.CITIES)
                        .setLocationBias(bounds)
                        .build(view.context)
                    getPlaceActivityResultLauncher.launch(intent)
                } else {
                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(view.context)
                    getPlaceActivityResultLauncher.launch(intent)
                }
            }
        else {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(view.context)
            getPlaceActivityResultLauncher.launch(intent)
        }
    }
}