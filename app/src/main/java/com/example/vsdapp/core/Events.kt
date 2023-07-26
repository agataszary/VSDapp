package com.example.vsdapp.core

import android.graphics.Bitmap
import com.example.vsdapp.database.Scene

object RequestOpenGallery
object SetupTouchListener
object SetupTouchListenerAndGetARDetails
object CloseActivity
class OpenReadMode(val fileLocation: String, val sceneId: String)
class DeleteScene(val scene: Scene)
object CloseWithOkResult
class ChangePictogramsVisibility(val visible: Boolean)
object OpenNavigationActivity
class ShowToast(val message: String)
object OpenRegisterActivity
object AskForPassword
object OpenSettings