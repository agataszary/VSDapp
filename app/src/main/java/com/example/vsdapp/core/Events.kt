package com.example.vsdapp.core

import android.graphics.Bitmap
import com.example.vsdapp.database.Scene

object RequestOpenGallery
object SetupTouchListener
object SetupTouchListenerAndGetARDetails
object CloseActivity
class SaveImageAndOpenReadMode(val fileLocation: String, val bitmap: Bitmap, val sceneId: Long)
class DeleteScene(val scene: Scene)
object CloseWithOkResult
class ChangePictogramsVisibility(val visible: Boolean)