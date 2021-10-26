package com.fiuba.ubademy.auth.createaccount

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.location.Location
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
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class CreateAccountFragment : Fragment() {

    private lateinit var viewModel: CreateAccountViewModel
    private lateinit var binding: FragmentCreateAccountBinding

    private lateinit var firstNameEditTextLayout: TextInputLayout
    private lateinit var lastNameEditTextLayout: TextInputLayout
    private lateinit var placeEditTextLayout: TextInputLayout
    private lateinit var emailEditTextLayout: TextInputLayout
    private lateinit var passwordEditTextLayout: TextInputLayout

    private var firstNameValid = false
    private var lastNameValid = false
    private var placeValid = false
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

        binding.submitCreateAccountFormButton.setOnClickListener { view ->
            lifecycleScope.launch {
                createAccount(view)
            }
        }

        binding.placeCreateAccountInput.setOnClickListener { view ->
            getPlace(view)
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
    }

    private fun setupValidators() {
        firstNameEditTextLayout = binding.root.findViewById(R.id.firstNameCreateAccountLayout)
        viewModel.firstName.observe(viewLifecycleOwner, { newValue ->
            firstNameValid = checkFirstName(newValue)
        })

        lastNameEditTextLayout = binding.root.findViewById(R.id.lastNameCreateAccountLayout)
        viewModel.lastName.observe(viewLifecycleOwner, { newValue ->
            lastNameValid = checkLastName(newValue)
        })

        placeEditTextLayout = binding.root.findViewById(R.id.placeCreateAccountLayout)
        viewModel.placeName.observe(viewLifecycleOwner, { newValue ->
            placeValid = checkPlace(newValue)
        })

        emailEditTextLayout = binding.root.findViewById(R.id.emailCreateAccountLayout)
        viewModel.email.observe(viewLifecycleOwner, { newValue ->
            emailValid = checkEmail(newValue)
        })

        passwordEditTextLayout = binding.root.findViewById(R.id.passwordCreateAccountLayout)
        viewModel.password.observe(viewLifecycleOwner, { newValue ->
            passwordValid = checkPassword(newValue)
        })
    }

    private fun checkFirstName(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return firstNameEditTextLayout.showError(getString(R.string.should_have_value))

        return firstNameEditTextLayout.hideError()
    }

    private fun checkLastName(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return lastNameEditTextLayout.showError(getString(R.string.should_have_value))

        return lastNameEditTextLayout.hideError()
    }

    private fun checkPlace(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return placeEditTextLayout.showError(getString(R.string.should_have_value))

        return placeEditTextLayout.hideError()
    }

    private fun checkEmail(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return emailEditTextLayout.showError(getString(R.string.should_have_value))

        if (!Patterns.EMAIL_ADDRESS.matcher(newValue).matches())
            return emailEditTextLayout.showError(getString(R.string.should_be_valid_email))

        return emailEditTextLayout.hideError()
    }

    private fun checkPassword(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return passwordEditTextLayout.showError(getString(R.string.should_have_value))

        val passwordMinimumLength = 6
        if (newValue.length < passwordMinimumLength) {
            return passwordEditTextLayout.showError(getString(R.string.should_be_valid_password, passwordMinimumLength))
        }

        return passwordEditTextLayout.hideError()
    }

    private fun checkForm() : Boolean {
        val firstNameOk = firstNameValid || checkFirstName(viewModel.firstName.value)
        val lastNameOk = lastNameValid || checkLastName(viewModel.lastName.value)
        val placeOk = placeValid || checkPlace(viewModel.placeName.value)
        val emailOk = emailValid || checkEmail(viewModel.email.value)
        val passwordOk = passwordValid || checkPassword(viewModel.password.value)
        return firstNameOk && lastNameOk && placeOk && emailOk && passwordOk
    }

    private suspend fun createAccount(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

        val busy = BusyFragment.show(this.parentFragmentManager)
        val createAccountStatus : CreateAccountStatus = viewModel.createAccount()
        busy.dismiss()

        when (createAccountStatus) {
            CreateAccountStatus.SUCCESS -> {
                view.findNavController().navigate(CreateAccountFragmentDirections.actionCreateAccountFragmentToAccountCreatedFragment())
            }
            CreateAccountStatus.EMAIL_ALREADY_USED -> Toast.makeText(context, R.string.email_already_used, Toast.LENGTH_LONG).show()
            CreateAccountStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }

    private fun getPlace(view: View) {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)

        if (ActivityCompat.checkSelfPermission(view.context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                if (location != null) {
                    val bounds = RectangularBounds.newInstance(
                        LatLng(location.latitude - 1, location.longitude - 1),
                        LatLng(location.latitude + 1, location.latitude + 1)
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