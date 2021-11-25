package com.example.vsdapp.readMode

import android.content.Context
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.BaseViewModel
import com.example.vsdapp.core.Constants
import com.example.vsdapp.database.AppDatabase
import com.example.vsdapp.database.Scene
import com.example.vsdapp.database.SceneDao
import com.example.vsdapp.views.PictogramView
import com.example.vsdapp.views.ReadPictogramView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.pictogram_view.view.*
import kotlinx.android.synthetic.main.read_pictogram_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReadModeViewModel: BaseViewModel() {

    private val selectedPictureVisibilityMutableData = MutableLiveData(View.GONE)
    val selectedPictureVisibilityData: LiveData<Int> = selectedPictureVisibilityMutableData

    private val selectedPictureMutableData = MutableLiveData<Uri>()
    val selectedPictureData: LiveData<Uri> = selectedPictureMutableData

    private lateinit var scene: Scene
    private var sceneId = 0
    private lateinit var sceneDao: SceneDao
    private lateinit var tts: TextToSpeech

    fun initialData(sceneId: Int, db: SceneDao, photoUri: Uri?, view: View, context: Context, textToSpeech: TextToSpeech){
        this.sceneDao = db
        this.tts = textToSpeech
        this.sceneId = sceneId

        if (photoUri != null){
            selectedPictureMutableData.value = photoUri
            selectedPictureVisibilityMutableData.value = View.VISIBLE
        }

        loadData(view, context)
    }

    fun loadData(view: View, context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            scene = withContext(Dispatchers.IO) { sceneDao.getSceneById(sceneId) }

            (view as ViewGroup).children.forEach { v ->
                println("child ${v.javaClass.name}")
                if (v is ReadPictogramView) {
                    println("remove")
                    view.removeView(v)
                }
            }

            showPictograms(view, context)
        }
    }

    private fun showPictograms(view: View, context: Context) {
        for (pictogram in scene.pictograms) {
            val image = ReadPictogramView(context)
            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(pictogram.x, pictogram.y, 0, 0)
            image.layoutParams = params

            Picasso.get().load(pictogram.imageUrl).resize(Constants.IMAGE_SIZE, Constants.IMAGE_SIZE).into(image.imageAtReadPictogramView)

            image.setDetails(ReadPictogramView.Data(label = pictogram.label))
            image.setOnClickListener { readLabel(it, context) }

            (view as ViewGroup).addView(image)
        }
    }

    private fun readLabel(label: String, context: Context) {
        tts.speak(label, TextToSpeech.QUEUE_FLUSH, null,"")
        Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
    }

}