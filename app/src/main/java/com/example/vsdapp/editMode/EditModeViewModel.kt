package com.example.vsdapp.editMode

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.*
import com.example.vsdapp.database.SceneDao
import com.example.vsdapp.database.StorageRepository
import com.example.vsdapp.models.GetIconsModel
import com.example.vsdapp.models.SceneDetails
import com.example.vsdapp.views.PictogramDetails
import com.example.vsdapp.views.PictogramView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

class EditModeViewModel(
    private val editRepository: EditModeRepository,
    private val storageRepository: StorageRepository
    ): DataBindingViewModel() {

    private lateinit var sceneDao: SceneDao
    private lateinit var mode: EditModeType
    private lateinit var scene: SceneDetails
    private var sceneToUpdateId = ""

    val searchInput = mutableStateOf("")
    val titleInput = mutableStateOf("")
    val shouldShowNoResultsDisclaimer = mutableStateOf(false)

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
    private lateinit var bitmapDetails: AspectRatioDetails
    private lateinit var editModeAreaDetails: AspectRatioDetails
    private lateinit var readModeAreaDetails: AspectRatioDetails

    private val iconsOnPicture = mutableMapOf<Int, PictogramDetails>()
    private val pictogramViewsMap = mutableMapOf<Int, PictogramView>()

    fun setInitialData(sceneDao: SceneDao, mode: EditModeType, sceneId: String?, imageLocation: String?, view: View, context: Context, contentResolver: ContentResolver) {
        showProgress()
        this.sceneDao = sceneDao
        this.mode = mode

        if (mode == EditModeType.UPDATE_MODE && sceneId != null && imageLocation != null) {
            setupUpdateMode(sceneId, imageLocation, view, context, contentResolver)
        } else {
            showContent()
        }

    }

    private fun setupUpdateMode(sceneId: String, imageLocation: String, view: View, context: Context, contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.Main) {

            sceneToUpdateId = sceneId

            scene = withContext(Dispatchers.IO) { storageRepository.getSceneDetails(sceneId) } ?: SceneDetails()

            val (imageTask, imageUri) = withContext(Dispatchers.IO) { storageRepository.getImage(imageLocation) }
            imageTask
                .addOnSuccessListener {
                    selectedPictureMutableData.value = imageUri
                    selectedPictureVisibilityMutableData.value = View.VISIBLE
                    searchButtonEnabledMutableFlow.value = true
                }
                .addOnFailureListener { println("Failureeeee ${it.message}") }
                .await()

            titleInput.value = scene.title
            showPictograms(view, context)

            val bitmap = uriToBitmap(imageUri, contentResolver)

            bitmapDetails = AspectRatioDetails(
                width = bitmap.width.toFloat(),
                height = bitmap.height.toFloat(),
                aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            )

            showContent()
            sendEvent(SetupTouchListenerAndGetARDetails)
        }
    }

    private fun showPictograms(view: View, context: Context) {
        for (pictogram in scene.pictograms) {
            val image = PictogramView(context)
            val params = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(pictogram.x, pictogram.y, 0, 0)
            image.layoutParams = params

            image.binding.imageAtPictogramView.layoutParams = LinearLayout.LayoutParams(pictogram.imageSize, pictogram.imageSize)

            Picasso.get().load(pictogram.imageUrl).resize(pictogram.imageSize, pictogram.imageSize).into(image.binding.imageAtPictogramView)

            image.setDetails(PictogramView.Data(id = imageId, imageUrl = pictogram.imageUrl))
            image.label.setText(pictogram.label)
            image.setDeleteButtonListener { deleteImage(it) }
            image.setLabelEditTextListener { id, label ->
                updateImageInfo(id, label)
            }

            (view as ViewGroup).addView(image)

            pictogramViewsMap[imageId] = image

            iconsOnPicture[imageId] = PictogramDetails(
                imageUrl = pictogram.imageUrl,
                x = pictogram.x,
                y = pictogram.y,
                label = image.label.text.toString(),
                xRead = pictogram.xRead,
                yRead = pictogram.yRead,
                imageSize = image.imageSize,
                viewWidth = image.viewWidth,
                viewHeight = image.viewHeight
            )

            imageId += 1
        }
    }

    private fun uriToBitmap(photoUri: Uri, contentResolver: ContentResolver): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, photoUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
        }

        return bitmap
    }

    fun onBackClicked() {
        sendEvent(CloseActivity)
    }

    fun onRightClicked() {
        sendEvent(CloseWithOkResult)
    }

    fun onSearchStringChanged(newSearch: String) {
        if (newSearch.isNotBlank() || (newSearch.isBlank() && searchInput.value != "")) searchInput.value = newSearch
    }

    fun onTitleStringChanged(newTitle: String? = null) {
        titleInput.value = newTitle ?: ""
    }

    fun onSearchButtonClicked() {
        iconClickedMutableFlow.value = ""
        viewModelScope.launch(Dispatchers.Main) {
            val listOfIcons = mutableListOf<String>()
            iconsFromSearch = withContext(Dispatchers.IO) { editRepository.getIcons(searchInput.value) }
            shouldShowNoResultsDisclaimer.value = iconsFromSearch.isEmpty()
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
            showProgress()
            for (id in pictogramViewsMap.keys) {
                iconsOnPicture[id]?.imageSize = pictogramViewsMap[id]?.imageSize ?: Constants.IMAGE_SIZE
                iconsOnPicture[id]?.viewWidth = pictogramViewsMap[id]?.viewWidth ?: RelativeLayout.LayoutParams.WRAP_CONTENT
                iconsOnPicture[id]?.viewHeight = pictogramViewsMap[id]?.viewHeight ?: RelativeLayout.LayoutParams.WRAP_CONTENT
            }
            if (mode == EditModeType.CREATE_MODE) {

                val imageLocation = titleInput.value.replace("\\s+".toRegex(), "_") + "_" + SimpleDateFormat("yyyyMMddHHmmss").format(Date()) + ".jpg"

                var sceneId = ""

                val imageUrl = withContext(Dispatchers.IO) { editRepository.saveBackgroundImage(imageLocation, selectedPictureMutableData.value!!) }

                val sceneDetails = SceneDetails(
                    title = titleInput.value,
                    imageLocation = imageLocation,
                    pictograms = iconsOnPicture.values.toList(),
                    imageUrl = imageUrl.toString()
                )

                val (task, docId) = withContext(Dispatchers.IO) { editRepository.saveSceneDetails(sceneDetails) }
                task
                    .addOnSuccessListener {
                        sceneId = docId
                        sendEvent(OpenReadMode(imageLocation, sceneId))
                    }
                    .addOnFailureListener { println("Failureeeee ${it.message}") }

            } else {
                val sceneToUpdate = SceneDetails(
                    title = titleInput.value,
                    imageLocation = scene.imageLocation,
                    pictograms = iconsOnPicture.values.toList(),
                    userId = scene.userId,
                    imageUrl = scene.imageUrl,
                    id = sceneToUpdateId
                )

                withContext(Dispatchers.IO) { editRepository.updateSceneDetails(sceneToUpdateId, sceneToUpdate) }
                    .addOnSuccessListener {
                        println("Updateeeed")
                        sendEvent(CloseWithOkResult)
                    }
                    .addOnFailureListener { println("Failureeeeee ${it.message}") }
                    .await()
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

    fun saveBitmap(bitmap: Bitmap, editAreaWidth: Float, editAreaHeight: Float, readAreaWidth: Float, readAreaHeight: Float) {
        selectedPictureBitmap = bitmap

        bitmapDetails = AspectRatioDetails(
            width = bitmap.width.toFloat(),
            height = bitmap.height.toFloat(),
            aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        )

        setAspectRatioDetails(editAreaWidth, editAreaHeight, readAreaWidth, readAreaHeight)
    }

    fun setAspectRatioDetails(editAreaWidth: Float, editAreaHeight: Float, readAreaWidth: Float, readAreaHeight: Float) {
        editModeAreaDetails = AspectRatioDetails(
            width = editAreaWidth,
            height = editAreaHeight,
            aspectRatio = editAreaWidth / editAreaHeight
        )

        readModeAreaDetails = AspectRatioDetails(
            width = readAreaWidth,
            height = readAreaHeight,
            aspectRatio = readAreaWidth / readAreaHeight
        )
    }

    private fun deleteImage(id: Int) {
        iconsOnPicture.remove(id)
    }

    fun updateImageInfo(id: Int, label: String? = null, x: Int? = null, y: Int? = null) {
        if (label != null) {
            iconsOnPicture[id]?.label = label
        }
        if (x != null && y != null && iconsOnPicture[id] != null) {
            iconsOnPicture[id]?.x = x
            iconsOnPicture[id]?.y = y
            iconsOnPicture[id] = calculateCoordinates(iconsOnPicture[id]!!)
        }
    }

    fun placeViewOnTouch(view: View, event: MotionEvent, context: Context) {
        if (event.action == MotionEvent.ACTION_DOWN && iconClickedMutableFlow.value != "") {
            val x = event.x.toInt() - Constants.IMAGE_SIZE/2
            val y = event.y.toInt() - Constants.IMAGE_SIZE/2

            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            val image = PictogramView(context)
            params.setMargins(x, y, 0, 0)
            image.layoutParams = params

            image.binding.imageAtPictogramView.layoutParams = LinearLayout.LayoutParams(Constants.IMAGE_SIZE, Constants.IMAGE_SIZE)

            val imageUrl = iconClickedMutableFlow.value
            Picasso.get().load(imageUrl).resize(Constants.IMAGE_SIZE, Constants.IMAGE_SIZE).into(image.binding.imageAtPictogramView)

            image.setDetails(PictogramView.Data(id = imageId, imageUrl = imageUrl))
            image.setDeleteButtonListener { deleteImage(it) }
            image.setLabelEditTextListener { id, label ->
                updateImageInfo(id, label)
            }

            (view as ViewGroup).addView(image)

            pictogramViewsMap[imageId] = image

            val pictogram = calculateCoordinates(PictogramDetails(
                imageUrl = imageUrl,
                x = x,
                y = y,
                label = image.label.text.toString(),
                imageSize = image.imageSize,
                viewWidth = image.viewWidth,
                viewHeight = image.viewHeight
            ))

            iconsOnPicture[imageId] = pictogram

            imageId += 1
        }
    }

    private fun calculateCoordinates(pictogramDetails: PictogramDetails): PictogramDetails {
        val pictogram = pictogramDetails.copy()
        if (bitmapDetails.aspectRatio > editModeAreaDetails.aspectRatio) {
            val photoW = editModeAreaDetails.width
            val photoH = photoW * bitmapDetails.height / bitmapDetails.width

            val yOffset = (editModeAreaDetails.height - photoH) / 2
            pictogram.yRead = pictogram.y - yOffset.toInt()

            if (bitmapDetails.aspectRatio < readModeAreaDetails.aspectRatio) {
                val largePhotoH = readModeAreaDetails.height
                val largePhotoW = largePhotoH * bitmapDetails.width / bitmapDetails.height

                val xOffset = (readModeAreaDetails.width - largePhotoW) / 2
                pictogram.xRead = pictogram.x + xOffset.toInt()

                val scale = largePhotoH / photoH

                pictogram.xRead = (pictogram.xRead!! * scale).toInt()
                pictogram.yRead = (pictogram.yRead!! * scale).toInt()
            }

        } else if (bitmapDetails.aspectRatio < editModeAreaDetails.aspectRatio) {
            val photoH = editModeAreaDetails.height
            val photoW = photoH * bitmapDetails.width / bitmapDetails.height

            val xOffset = Constants.SEARCH_COLUMN_WIDTH
            pictogram.xRead = pictogram.x + xOffset
            pictogram.yRead = pictogram.y
        }

        return pictogram
    }
}