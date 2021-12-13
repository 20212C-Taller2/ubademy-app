package com.fiuba.ubademy.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

fun Fragment.getPlaceActivityResultLauncher(placeName: MutableLiveData<String?>, placeId: MutableLiveData<String?>) : ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            Activity.RESULT_OK -> {
                val place = Autocomplete.getPlaceFromIntent(it.data!!)
                placeName.value = place.address
                placeId.value = place.id
            }
            Activity.RESULT_CANCELED -> {
            }
            AutocompleteActivity.RESULT_ERROR -> {
            }
        }
    }
}

fun getPlace(view: View, getPlaceActivityResultLauncher: ActivityResultLauncher<Intent>) {
    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)

    if (ActivityCompat.checkSelfPermission(view.context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        LocationServices.getFusedLocationProviderClient(view.context).lastLocation.addOnSuccessListener {
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