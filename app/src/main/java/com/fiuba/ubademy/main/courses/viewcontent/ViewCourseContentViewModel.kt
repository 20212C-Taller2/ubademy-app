package com.fiuba.ubademy.main.courses.viewcontent

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ViewCourseContentViewModel(application: Application) : AndroidViewModel(application) {

    var mediaPaths = MutableLiveData<List<String>>()
    var currentMediaPathIndex = MutableLiveData<Int>()

    var mediaUrisByPaths = MutableLiveData<HashMap<String, Uri>>()
    var currentMediaUri = MutableLiveData<Uri>()

    init {
        currentMediaPathIndex.value = 0
        mediaUrisByPaths.value = hashMapOf()
    }

    public suspend fun showNextMedia() {
        if (currentMediaPathIndex.value!! < mediaPaths.value!!.size - 1) {
            currentMediaPathIndex.value = currentMediaPathIndex.value?.plus(1)
            val currentMediaPath = mediaPaths.value!![currentMediaPathIndex.value!!]
            if (mediaUrisByPaths.value!!.containsKey(currentMediaPath))
                currentMediaUri.value = mediaUrisByPaths.value!![currentMediaPath]
            else {
                val mediaUri = FirebaseStorage.getInstance().getReference(currentMediaPath).downloadUrl.await()
                mediaUrisByPaths.value!![currentMediaPath] = mediaUri
                currentMediaUri.value = mediaUri
            }
        }
    }

    public fun showPreviousMedia() {
        if (currentMediaPathIndex.value!! != 0) {
            currentMediaPathIndex.value = currentMediaPathIndex.value?.minus(1)
            val currentMediaPath = mediaPaths.value!![currentMediaPathIndex.value!!]
            currentMediaUri.value = mediaUrisByPaths.value!![currentMediaPath]
        }
    }

    public suspend fun showFirstMedia() {
        val currentMediaPath = mediaPaths.value!![0]
        val mediaUri = FirebaseStorage.getInstance().getReference(currentMediaPath).downloadUrl.await()
        mediaUrisByPaths.value!![currentMediaPath] = mediaUri
        currentMediaUri.value = mediaUri
    }
}