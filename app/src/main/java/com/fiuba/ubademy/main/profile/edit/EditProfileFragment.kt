package com.fiuba.ubademy.main.profile.edit

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.location.Location
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentEditProfileBinding
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

class EditProfileFragment : Fragment() {

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var binding: FragmentEditProfileBinding

    private var firstNameValid = false
    private var lastNameValid = false
    private var placeValid = false
    private var emailValid = false

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
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)

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
            getPlace(it)
        }

        binding.editProfileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupValidators()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

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
        binding.firstNameEditProfileLayout.isEnabled = true
        binding.lastNameEditProfileLayout.isEnabled = true
        binding.placeEditProfileLayout.isEnabled = true
        binding.emailEditProfileLayout.isEnabled = true
    }

    private fun disableForm() {
        viewModel.editInProgress.value = false
        binding.editProfileButton.visibility = View.VISIBLE
        binding.submitEditProfileFormButton.visibility = View.GONE
        binding.firstNameEditProfileLayout.isEnabled = false
        binding.lastNameEditProfileLayout.isEnabled = false
        binding.placeEditProfileLayout.isEnabled = false
        binding.emailEditProfileLayout.isEnabled = false
    }

    private fun setupValidators() {
        viewModel.firstName.observe(viewLifecycleOwner, {
            firstNameValid = checkFirstName(it)
        })

        viewModel.lastName.observe(viewLifecycleOwner, {
            lastNameValid = checkLastName(it)
        })

        viewModel.placeName.observe(viewLifecycleOwner, {
            placeValid = checkPlace(it)
        })

        viewModel.email.observe(viewLifecycleOwner, {
            emailValid = checkEmail(it)
        })
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

    private fun checkPlace(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.placeEditProfileLayout.showError(getString(R.string.should_have_value))

        return binding.placeEditProfileLayout.hideError()
    }

    private fun checkEmail(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.emailEditProfileLayout.showError(getString(R.string.should_have_value))

        if (!Patterns.EMAIL_ADDRESS.matcher(newValue).matches())
            return binding.emailEditProfileLayout.showError(getString(R.string.should_be_valid_email))

        return binding.emailEditProfileLayout.hideError()
    }

    private fun checkForm() : Boolean {
        val firstNameOk = firstNameValid || checkFirstName(viewModel.firstName.value)
        val lastNameOk = lastNameValid || checkLastName(viewModel.lastName.value)
        val placeOk = placeValid || checkPlace(viewModel.placeName.value)
        val emailOk = emailValid || checkEmail(viewModel.email.value)
        return firstNameOk && lastNameOk && placeOk && emailOk
    }

    private suspend fun saveEditProfile(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

        BusyFragment.show(this.parentFragmentManager)
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