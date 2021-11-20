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
import com.ortiz.touchview.TouchImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.pictogram_view.view.*
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

    private var imageId = 0
    private var selectedPictureBitmap: Bitmap? = null

    private val iconsOnPicture = mutableMapOf<Int, PictogramDetails>()

    fun setInitialData(sceneDao: SceneDao, filesLocation: File, mode: EditModeType, sceneId: Int?, imageLocation: String?) {
        this.sceneDao = sceneDao
        this.filesLocation = filesLocation
        this.mode = mode

        if (mode == EditModeType.UPDATE_MODE && sceneId != null && imageLocation != null) {
            setupUpdateMode()
        }
    }

    private fun setupUpdateMode() {

    }

    fun onBackClicked() {
        sendEvent(CloseActivity)
    }

    fun onRightClicked() {

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
            var counter = 0
            val locations = withContext(Dispatchers.IO) { sceneDao.getAll().map { it.imageLocation } }
            var imageLocation = titleInput.value
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
            selectedPictureMutableData.value
            withContext(Dispatchers.IO) {
                sceneDao.insert(scene)
            }
            sendEvent(SaveImageToInternalStorage(imageLocation, selectedPictureBitmap!!))
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

    private fun updateImageInfo(id: Int, label: String? = null, x: Int? = null, y: Int? = null) {
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
            image.setupMoveView{ id, newX, newY ->
                updateImageInfo(id = id, x = newX, y = newY)
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