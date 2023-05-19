package com.example.vsdapp.readMode

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.core.view.iterator
import androidx.databinding.DataBindingUtil
import com.example.vsdapp.R
import com.example.vsdapp.compose.TopNavBar
import com.example.vsdapp.core.ChangePictogramsVisibility
import com.example.vsdapp.core.Constants
import com.example.vsdapp.core.runEventsCollector
import com.example.vsdapp.database.AppDatabase
import com.example.vsdapp.databinding.ActivityReadModeBinding
import com.example.vsdapp.editMode.EditModeActivity
import com.example.vsdapp.editMode.EditModeType
import com.example.vsdapp.views.ReadPictogramView
import java.util.*

class ReadModeActivity: AppCompatActivity(), TextToSpeech.OnInitListener {

    companion object {
        fun start(activity: Activity, sceneId: Long, imageLocation: String) {
            val intent = Intent(activity, ReadModeActivity::class.java)
                .putExtra(Constants.INTENT_SCENE, sceneId)
                .putExtra(Constants.IMAGE_LOCATION, imageLocation)
            activity.startActivity(intent)
        }
    }

    lateinit var viewModel: ReadModeViewModel
    private lateinit var binding: ActivityReadModeBinding
    lateinit var tts: TextToSpeech
    lateinit var imageLocation: String
    var scene = 0L

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            finish()
            start(this, scene, imageLocation)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this, "com.google.android.tts")

        viewModel = ReadModeViewModel()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_read_mode)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        scene = intent.getLongExtra(Constants.INTENT_SCENE, 0)
        imageLocation = intent.getStringExtra(Constants.IMAGE_LOCATION)!!
        val db = AppDatabase.getInstance(this)

        viewModel.initialData(
            sceneId = scene,
            db = db.sceneDao,
            photoUri = loadPhotoFromInternalStorage(imageLocation),
            view = binding.relativeLayoutAtReadMode,
            context = this,
            textToSpeech = tts
        )

        binding.readModeTopNavBar.setContent {
            TopNavBar(
                onBackClicked = { finish() },
                leftText = stringResource(id = R.string.top_nav_bar_back_arrow_text),
                searchFieldVisibility = false,
                onSaveButtonClicked = null,
                onTitleChanged = null,
                rightButtonVisibility = true,
                dropdownMenuContent = { TopBarDropdownMenuItems() }
            )
        }

        setupEventsObserver()
    }

    @Composable
    private fun TopBarDropdownMenuItems(){
        DropdownMenuItem(onClick = { viewModel.onShowAllCheckedChanged(!viewModel.showAllCheckBoxChecked.value) }) {
            Text(stringResource(R.string.show_all_icons))
            Checkbox(
                checked = viewModel.showAllCheckBoxChecked.value,
                onCheckedChange = { viewModel.onShowAllCheckedChanged(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = colorResource(R.color.light_purple)
                )
            )
        }
        DropdownMenuItem(onClick = { openEditModeScreen(scene, imageLocation) } ) {
            Text(stringResource(R.string.edit))
        }
    }

    private fun setupEventsObserver() {
       runEventsCollector(viewModel){ event ->
            when (val payload = event.getContent()) {
                is ChangePictogramsVisibility -> changePictogramsVisibility(payload.visible)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("PL"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The language specified is not supported!")
            } else {
                Log.e("TTS", "Success")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
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

    private fun openEditModeScreen(sceneId: Long, imageLocation: String) {
        val intent = Intent(this, EditModeActivity::class.java)
            .putExtra(Constants.EDIT_MODE_TYPE, EditModeType.UPDATE_MODE)
            .putExtra(Constants.INTENT_SCENE, sceneId)
            .putExtra(Constants.IMAGE_LOCATION, imageLocation)

        startForResult.launch(intent)
    }

    private fun changePictogramsVisibility(visible: Boolean) {
        for (view in (binding.relativeLayoutAtReadMode)) {
            when {
                view is ReadPictogramView && visible -> view.showPictogram()
                view is ReadPictogramView && !visible -> view.hidePictogram()
            }
        }
    }

    public override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}