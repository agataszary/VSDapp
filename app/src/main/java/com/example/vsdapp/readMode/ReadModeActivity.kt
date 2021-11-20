package com.example.vsdapp.readMode

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.example.vsdapp.R
import com.example.vsdapp.compose.TopNavBar
import com.example.vsdapp.core.Constants
import com.example.vsdapp.database.AppDatabase
import com.example.vsdapp.database.Scene
import com.example.vsdapp.databinding.ActivityReadModeBinding
import com.example.vsdapp.editMode.EditModeActivity
import com.example.vsdapp.editMode.EditModeType
import java.util.*

class ReadModeActivity: AppCompatActivity(), TextToSpeech.OnInitListener {

    companion object {
        fun start(activity: Activity, sceneId: Int, imageLocation: String) {
            val intent = Intent(activity, ReadModeActivity::class.java)
                .putExtra(Constants.INTENT_SCENE, sceneId)
                .putExtra(Constants.IMAGE_LOCATION, imageLocation)
            activity.startActivity(intent)
        }
    }

    lateinit var viewModel: ReadModeViewModel
    private lateinit var binding: ActivityReadModeBinding
    lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        viewModel = ReadModeViewModel()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_read_mode)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val scene = intent.getIntExtra(Constants.INTENT_SCENE, 0)
        val imageLocation = intent.getStringExtra(Constants.IMAGE_LOCATION)!!
        val db = AppDatabase.getInstance(this)

        viewModel.initialData(
            sceneId = scene,
            db = db.sceneDao,
            photoUri = loadPhotoFromInternalStorage(imageLocation),
            view = binding.relativeLayoutAtReadMode,
            context = applicationContext,
            textToSpeech = tts
        )

        binding.readModeTopNavBar.setContent {
            TopNavBar(
                onBackClicked = { finish() },
                onRightClicked = { openEditModeScreen(scene, imageLocation) },
                leftText = stringResource(id = R.string.top_nav_bar_back_arrow_text),
                rightText = stringResource(id = R.string.top_nav_bar_forward_arrow_text_edit),
                searchFieldVisibility = false,
                onSaveButtonClicked = null,
                onTitleChanged = null,
                leftButtonVisibility = true,
                rightButtonVisibility = true
            )
        }

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("pl"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The language specified is not supported!")
            } else {
                Log.e("TTS", "Success")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    private  fun loadPhotoFromInternalStorage(filename: String): Uri? {
        val files = filesDir.listFiles { file ->
            file.canRead() && file.isFile && file.name == filename
        }
        return if (files != null && files.size == 1) {
            files[0].toUri()
        } else {
            null
        }
    }

    private fun openEditModeScreen(sceneId: Int, imageLocation: String) {
        EditModeActivity.start(this, EditModeType.UPDATE_MODE, sceneId, imageLocation)
    }

    public override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}