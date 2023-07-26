package com.example.vsdapp.readMode

import android.content.Context
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.iterator
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.ChangePictogramsVisibility
import com.example.vsdapp.core.DataBindingViewModel
import com.example.vsdapp.core.PreferencesDataStore
import com.example.vsdapp.database.Scene
import com.example.vsdapp.database.SceneDao
import com.example.vsdapp.database.StorageRepository
import com.example.vsdapp.models.SceneDetails
import com.example.vsdapp.views.ReadPictogramView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Properties

class ReadModeViewModel(private val storageRepository: StorageRepository): DataBindingViewModel() {

    private val selectedPictureVisibilityMutableData = MutableLiveData(View.GONE)
    val selectedPictureVisibilityData: LiveData<Int> = selectedPictureVisibilityMutableData

    private val selectedPictureMutableData = MutableLiveData<Uri>()
    val selectedPictureData: LiveData<Uri> = selectedPictureMutableData

    var showAllCheckBoxChecked = mutableStateOf(false)
        private set
    var appMode = mutableStateOf(AppMode.NONE)

    private lateinit var scene: SceneDetails
    private var sceneId = ""
    private lateinit var sceneDao: SceneDao
    private lateinit var tts: TextToSpeech

    fun loadInitialData(sceneId: String, db: SceneDao, view: View, context: Context, textToSpeech: TextToSpeech, imageLocation: String){
        showProgress()

        this.sceneDao = db
        this.tts = textToSpeech
        this.sceneId = sceneId
        
        viewModelScope.launch(Dispatchers.Main) {
            scene = withContext(Dispatchers.IO) { storageRepository.getSceneDetails(sceneId) } ?: SceneDetails()
            appMode.value = withContext(Dispatchers.IO){ dataStore.getPreference(PreferencesDataStore.APP_MODE_KEY) }

            val (imageTask, imageUri) = withContext(Dispatchers.IO) { storageRepository.getImage(imageLocation) }
            imageTask
                .addOnSuccessListener {
                    selectedPictureMutableData.value = imageUri
                    selectedPictureVisibilityMutableData.value = View.VISIBLE
                }
                .addOnFailureListener { println("Failureeeee ${it.message}") }
                .await()

            showPictograms(view, context)
        }

    }

    private fun showPictograms(view: View, context: Context) {
        for (pictogram in scene.pictograms) {
            val image = ReadPictogramView(context)
            Picasso.get().load(pictogram.imageUrl).resize(pictogram.imageSize, pictogram.imageSize).into(image.binding.imageAtReadPictogramView)

            image.binding.imageAtReadPictogramView.layoutParams = LinearLayout.LayoutParams(pictogram.imageSize, pictogram.imageSize)
            val params = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            params.setMargins(pictogram.xRead ?: pictogram.x, pictogram.yRead ?: pictogram.y, 0, 0)
            image.layoutParams = params

            image.setDetails(ReadPictogramView.Data(label = pictogram.label))
            image.setOnClickListener {
                if (showAllCheckBoxChecked.value) onPictogramClickedInVisibleMode(it, context) else onPictogramClickedInHidingMode(view, it, context)
            }

            (view as ViewGroup).addView(image)
            image.hidePictogram()
        }
        showContent()
    }

    private fun onPictogramClickedInHidingMode(view: View, pictogram: ReadPictogramView, context: Context){
        for(v in (view as ViewGroup)){
            if (v is ReadPictogramView) v.hidePictogram()
        }
        pictogram.showPictogram()
        readLabel(pictogram.pictogramData.label, context)
    }

    private fun onPictogramClickedInVisibleMode(pictogram: ReadPictogramView, context: Context){
        readLabel(pictogram.pictogramData.label, context)
    }

    private fun readLabel(label: String, context: Context) {
        tts.speak(label, TextToSpeech.QUEUE_FLUSH, null,"")
        Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
    }

    fun onShowAllCheckedChanged(checked: Boolean){
        showAllCheckBoxChecked.value = checked
        changePictogramsVisibility()
    }

    private fun changePictogramsVisibility(){
        sendEvent(ChangePictogramsVisibility(showAllCheckBoxChecked.value))
    }
}