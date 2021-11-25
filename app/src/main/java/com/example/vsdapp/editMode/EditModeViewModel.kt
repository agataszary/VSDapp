package com.example.vsdapp.editMode

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.R
import com.example.vsdapp.core.*
import com.example.vsdapp.database.AppDatabase
import com.example.vsdapp.database.Scene
import com.example.vsdapp.database.SceneDao
import com.example.vsdapp.models.GetIconsModel
import com.example.vsdapp.views.PictogramDetails
import com.example.vsdapp.views.PictogramView
import com.example.vsdapp.views.ReadPictogramView
import com.ortiz.touchview.TouchImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.pictogram_view.view.*
import kotlinx.android.synthetic.main.read_pictogram_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File
import java.io.IOException

class EditModeViewModel(private val repository: EditModeRepository): BaseViewModel() {

    private lateinit var sceneDao: SceneDao
    private lateinit var filesLocation: File
    private lateinit var mode: EditModeType
    private lateinit var scene: Scene

    val searchInput = mutableStateOf("")
    val titleInput = mutableStateOf("")

    private var iconsFromSearch: List<GetIconsModel> = listOf()

    private val iconsUrlsMutableFlow = MutableStateFlow<List<String>>(listOf())
    val iconsUrl: StateFlow<List<String>> = iconsUrlsMutableFlow

    private val iconClickedMutableFlow = MutableStateFlow<String>("")
    val iconClicked: StateFlow<String> = iconClickedMutableFlow

    private val selectedPictureVisibilityMutableData = MutableLiveData(View.GONE)
    val selectedPictureVisibilityData: LiveData<Int> = selectedPictureVisibilityMutableData

    private val selectedPictureMutableData = MutableLiveData<Uri>()
    val selectedPictureData: LiveData<Uri> = selectedPictureMutableData

    private val searchButtonEnabledMutableFlow = MutableStateFlow(false)
    val searchButtonEnabledFlow: StateFlow<Boolean> = searchButtonEnabledMutableFlow

    private val rightButtonVisibilityMutableFlow = MutableStateFlow(false)
    val rightButtonVisibilityState: StateFlow<Boolean> = rightButtonVisibilityMutableFlow

    private var imageId = 0
    private var selectedPictureBitmap: Bitmap? = null

    private val iconsOnPicture = mutableMapOf<Int, PictogramDetails>()

    fun setInitialData(sceneDao: SceneDao, filesLocation: File, mode: EditModeType, sceneId: Int?, imageLocation: Uri?, view: View, context: Context) {
        this.sceneDao = sceneDao
        this.filesLocation = filesLocation
        this.mode = mode

        if (mode == EditModeType.UPDATE_MODE && sceneId != null && imageLocation != null) {
            setupUpdateMode(sceneId, imageLocation, view, context)
        }
    }

    private fun setupUpdateMode(sceneId: Int, imageLocation: Uri, view: View, context: Context) {
        viewModelScope.launch(Dispatchers.Main) {

            selectedPictureMutableData.value = imageLocation
            selectedPictureVisibilityMutableData.value = View.VISIBLE
            searchButtonEnabledMutableFlow.value = true

            scene = withContext(Dispatchers.IO) { sceneDao.getSceneById(sceneId) }

            titleInput.value = scene.imageName
            showPictograms(view, context)
            sendEvent(SetupTouchListener)
        }
    }

    private fun showPictograms(view: View, context: Context) {
        for (pictogram in scene.pictograms) {
            val image = PictogramView(context)
            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(pictogram.x, pictogram.y, 0, 0)
            image.layoutParams = params

            Picasso.get().load(pictogram.imageUrl).resize(Constants.IMAGE_SIZE, Constants.IMAGE_SIZE).into(image.imageAtPictogramView)

            image.setDetails(PictogramView.Data(id = imageId, imageUrl = pictogram.imageUrl))
            image.label.setText(pictogram.label)
            image.setDeleteButtonListener { deleteImage(it) }
            image.setLabelEditTextListener { id, label ->
                updateImageInfo(id, label)
            }

            (view as ViewGroup).addView(image)

            iconsOnPicture[imageId] = PictogramDetails(
                imageUrl = pictogram.imageUrl,
                x = pictogram.x,
                y = pictogram.y,
                label = image.label.text.toString()
            )

            imageId += 1
        }
    }

    fun onBackClicked() {
        sendEvent(CloseActivity)
    }

    fun onRightClicked() {
        sendEvent(CloseActivity)
    }

    fun onSearchStringChanged(newSearch: String? = null) {
        searchInput.value = newSearch ?: ""
    }

    fun onTitleStringChanged(newTitle: String? = null) {
        titleInput.value = newTitle ?: ""
    }

    fun onSearchButtonClicked() {
        iconClickedMutableFlow.value = ""
        viewModelScope.launch(Dispatchers.Main) {
            val listOfIcons = mutableListOf<String>()
            iconsFromSearch = withContext(Dispatchers.IO) { repository.getIcons(searchInput.value) }
            iconsFromSearch.forEach { icon ->
                listOfIcons.add(Constants.URL_PICTOGRAMS + icon._id)
            }
            iconsUrlsMutableFlow.value = listOfIcons
            sendEvent(SetupTouchListener)
        }
    }

    fun onIconClicked(icon: String) {
        iconClickedMutableFlow.value = icon
    }

    fun onChangeBackgroundPictureClicked() {
        sendEvent(RequestOpenGallery)
    }

    fun onSaveButtonClicked() {
        viewModelScope.launch(Dispatchers.Main) {
            if (mode == EditModeType.CREATE_MODE) {
                var counter = 0
                val locations = withContext(Dispatchers.IO) { sceneDao.getAll().map { it.imageLocation } }
                var imageLocation = titleInput.value.replace("\\s+".toRegex(), "_")

                while (locations.contains("$imageLocation.jpg")) {
                    imageLocation.dropLast(1)
                    imageLocation += counter.toString()
                    counter += 1
                }

                imageLocation += ".jpg"

                val scene = Scene(
                    imageName = titleInput.value,
                    imageLocation = imageLocation,
                    pictograms = iconsOnPicture.values.toList()
                )

                withContext(Dispatchers.IO) { sceneDao.insert(scene) }
                sendEvent(SaveImageToInternalStorage(imageLocation, selectedPictureBitmap!!))

                rightButtonVisibilityMutableFlow.value = true
            } else {
                val sceneToUpdate = Scene(
                    id = scene.id,
                    imageName = titleInput.value,
                    imageLocation = scene.imageLocation,
                    pictograms = iconsOnPicture.values.toList()
                )
                withContext(Dispatchers.IO) { sceneDao.update(sceneToUpdate) }
            }
        }
    }

    fun changeBackgroundPicture(picture: Uri?) {
        if (picture != null) {
            selectedPictureVisibilityMutableData.value = View.VISIBLE
            selectedPictureMutableData.value = picture
            searchButtonEnabledMutableFlow.value = true
        }
    }

    fun saveBitmap(bitmap: Bitmap) {
        selectedPictureBitmap = bitmap
    }

    private fun deleteImage(id: Int) {
        println("************ ${iconsOnPicture[id]}")
        iconsOnPicture.remove(id)
    }

    fun updateImageInfo(id: Int, label: String? = null, x: Int? = null, y: Int? = null) {
        if (label != null) {
            iconsOnPicture[id]?.label = label
        }
        if (x != null) {
            iconsOnPicture[id]?.x = x
        }
        if (y != null) {
            iconsOnPicture[id]?.y = y
        }
    }

    fun placeViewOnTouch(view: View, event: MotionEvent, context: Context) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x.toInt() - Constants.IMAGE_SIZE/2
            val y = event.y.toInt() - Constants.IMAGE_SIZE/2

            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            val image = PictogramView(context)
            params.setMargins(x, y, 0, 0)
            image.layoutParams = params

            val imageUrl = iconClickedMutableFlow.value
            Picasso.get().load(imageUrl).resize(Constants.IMAGE_SIZE, Constants.IMAGE_SIZE).into(image.imageAtPictogramView)

            image.setDetails(PictogramView.Data(id = imageId, imageUrl = imageUrl))
            image.setDeleteButtonListener { deleteImage(it) }
            image.setLabelEditTextListener { id, label ->
                updateImageInfo(id, label)
            }

            (view as ViewGroup).addView(image)

            iconsOnPicture[imageId] = PictogramDetails(
                imageUrl = imageUrl,
                x = x,
                y = y,
                label = image.label.text.toString()
            )

            imageId += 1
        }
    }
}