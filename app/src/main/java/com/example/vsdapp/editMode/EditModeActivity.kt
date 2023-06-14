package com.example.vsdapp.editMode

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.vsdapp.R
import com.example.vsdapp.compose.SearchIconsColumn
import com.example.vsdapp.compose.TopNavBar
import com.example.vsdapp.core.*
import com.example.vsdapp.database.AppDatabase
import com.example.vsdapp.databinding.ActivityEditModeBinding
import com.example.vsdapp.readMode.ReadModeActivity
import com.example.vsdapp.views.PictogramView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*

class EditModeActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity, mode: EditModeType, sceneId: Int? = null, imageLocation: String? = null){
            val intent = Intent(activity, EditModeActivity::class.java)
                .putExtra(Constants.EDIT_MODE_TYPE, mode)
                .putExtra(Constants.INTENT_SCENE, sceneId)
                .putExtra(Constants.IMAGE_LOCATION, imageLocation)
            activity.startActivity(intent)
        }
    }

    private val viewModel by viewModel<EditModeViewModel>()
    private lateinit var binding: ActivityEditModeBinding
    lateinit var db: AppDatabase

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            viewModel.changeBackgroundPicture(intent?.data)
            val bitmap = uriToBitmap(intent?.data!!)
            viewModel.saveBitmap(
                bitmap = bitmap,
                editAreaWidth = binding.relativeLayoutAtEditMode.width.toFloat(),
                editAreaHeight = binding.relativeLayoutAtEditMode.height.toFloat(),
                readAreaWidth = binding.horizontalLinearLayoutAtEditMode.width.toFloat(),
                readAreaHeight = binding.horizontalLinearLayoutAtEditMode.height.toFloat(),
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_mode)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        db = AppDatabase.getInstance(this)


        val mode = intent.getSerializableExtra(Constants.EDIT_MODE_TYPE) as EditModeType
        val sceneId = intent.getLongExtra(Constants.INTENT_SCENE, 0L)
        val imageLocation = intent.getStringExtra(Constants.IMAGE_LOCATION)
        val imageUri = if(imageLocation != null) loadPhotoFromInternalStorage(imageLocation) else null

        viewModel.setInitialData(
            sceneDao = db.sceneDao,
            filesLocation = filesDir,
            mode = mode,
            sceneId = sceneId,
            imageLocation =  imageUri,
            view = binding.relativeLayoutAtEditMode,
            context = this,
            bitmap = if (mode == EditModeType.UPDATE_MODE && imageUri != null) uriToBitmap(imageUri) else null
        )

        binding.composeTopNavBar.setContent {
            if (mode == EditModeType.CREATE_MODE) {

                TopNavBar(
                    onBackClicked = { viewModel.onBackClicked() },
                    title = viewModel.titleInput.value,
                    onTitleChanged = { viewModel.onTitleStringChanged(it) },
                    onSaveButtonClicked = { viewModel.onSaveButtonClicked() },
                    leftText = stringResource(id = R.string.top_nav_bar_back_arrow_text),
                    searchFieldVisibility = true,
                    rightButtonVisibility = false
                )
            } else {
                TopNavBar(
                    onBackClicked = { viewModel.onRightClicked() },
                    title = viewModel.titleInput.value,
                    onTitleChanged = { viewModel.onTitleStringChanged(it) },
                    onSaveButtonClicked = { viewModel.onSaveButtonClicked() },
                    leftText = stringResource(id = R.string.top_nav_bar_forward_arrow_text_read),
                    searchFieldVisibility = true,
                    rightButtonVisibility = false
                )
            }
        }

        binding.searchIconsColumn.setContent {
            val iconsList: List<String> by viewModel.iconsUrl.collectAsState(listOf())
            val iconClicked: String by viewModel.iconClicked.collectAsState("")
            val searchButtonEnabled: Boolean by viewModel.searchButtonEnabledFlow.collectAsState(false)

            SearchIconsColumn(
                textFieldValue = viewModel.searchInput.value,
                onSearchButtonClicked = { viewModel.onSearchButtonClicked() },
                iconsList = iconsList,
                iconClicked = iconClicked,
                searchButtonEnabled = searchButtonEnabled,
                onChangeBackgroundPictureClicked = { viewModel.onChangeBackgroundPictureClicked() },
                onSearchStringChanged = { viewModel.onSearchStringChanged(it) },
                onIconClicked = { viewModel.onIconClicked(it) },
                choosePictureButtonVisibility = mode == EditModeType.CREATE_MODE,
                shouldShowNoResultsDisclaimer = viewModel.shouldShowNoResultsDisclaimer.value
            )
        }

        setupEventsObserver()
    }

    private fun setupEventsObserver() {
       runEventsCollector(viewModel) { event ->
            when (val payload = event.getContent()) {
                is RequestOpenGallery -> getPictureFromGallery()
                is SetupTouchListener -> setupTouchListener()
                is SetupTouchListenerAndGetARDetails -> setupUpdateMode()
                is CloseActivity -> {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
                is CloseWithOkResult -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                is SaveImageAndOpenReadMode -> {
                    saveImageToInternalStorage(payload.fileLocation, payload.bitmap)
                    openReadMode(payload.sceneId, payload.fileLocation)
                }
            }
        }
    }

    private fun setupUpdateMode() {
        setupTouchListener()
        viewModel.setAspectRatioDetails(
            editAreaWidth = binding.relativeLayoutAtEditMode.width.toFloat(),
            editAreaHeight = binding.relativeLayoutAtEditMode.height.toFloat(),
            readAreaWidth = binding.horizontalLinearLayoutAtEditMode.width.toFloat(),
            readAreaHeight = binding.horizontalLinearLayoutAtEditMode.height.toFloat()
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {
        binding.relativeLayoutAtEditMode.setOnTouchListener { view, event ->
            viewModel.placeViewOnTouch(view, event, this)
            true
        }

        binding.relativeLayoutAtEditMode.setOnDragListener { v, event ->
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    val x = event.x.toInt()
                    val y = event.y.toInt()

                    params.setMargins(x, y, 0, 0)

                    true
                }
                DragEvent.ACTION_DROP -> {
                    val item: ClipData.Item = event.clipData.getItemAt(0)

                    val view = event.localState as PictogramView

                    val x = event.x.toInt() - view.width/2
                    val y = event.y.toInt() - view.height/2

                    params.setMargins(x, y, 0, 0)

                    view.layoutParams = params

                    viewModel.updateImageInfo(id = view.pictogramData.id, x = x, y = y)

                    view.visibility = View.VISIBLE
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    true
                }
                else -> {
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                    false
                }
            }
            true
        }
    }

    private fun getPictureFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startForResult.launch(intent)
    }

    private fun saveImageToInternalStorage(filename: String, bitmap: Bitmap) {
        try {
            openFileOutput(filename, MODE_PRIVATE).use { stream ->
                if(!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                    throw IOException("Couldn't save bitmap")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun openReadMode(sceneId: Long, imageLocation: String){
        ReadModeActivity.start(this, sceneId, imageLocation)
        finish()
    }

    private fun loadPhotoFromInternalStorage(filename: String): Uri? {
        val files = filesDir.listFiles { file ->
            file.canRead() && file.isFile && file.name == filename
        }
        return if (files != null && files.size == 1) {
            files[0].toUri()
        } else {
            null
        }
    }

    private fun uriToBitmap(photoUri: Uri): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, photoUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
        }

        return bitmap
    }
}