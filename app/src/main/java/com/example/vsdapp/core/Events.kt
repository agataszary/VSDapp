package com.example.vsdapp.core

import android.graphics.Bitmap
import com.example.vsdapp.database.Scene

object RequestOpenGallery
object SetupTouchListener
object CloseActivity
class SaveImageToInternalStorage(val fileLocation: String, val bitmap: Bitmap)
class DeleteScene(val scene: Scene)