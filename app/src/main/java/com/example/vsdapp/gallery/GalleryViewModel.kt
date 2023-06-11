package com.example.vsdapp.gallery

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.BaseViewModel
import com.example.vsdapp.core.ComposeViewModel
import com.example.vsdapp.core.DeleteScene
import com.example.vsdapp.core.PreferencesDataStore
import com.example.vsdapp.database.AppDatabase
import com.example.vsdapp.database.Scene
import com.example.vsdapp.database.SceneDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel: ComposeViewModel() {

    private lateinit var sceneDao: SceneDao

    val searchInput = mutableStateOf("")

//    private val scenesListMutableFlow = MutableStateFlow<List<Scene>>(listOf())
//    val scenesListFlow: StateFlow<List<Scene>> = scenesListMutableFlow

    var scenesList = mutableStateOf<List<Scene>>(listOf())
        private set

    var openAlertDialog = mutableStateOf(false)
        private set

    var appMode = mutableStateOf(AppMode.NONE)
        private set

    private var sceneToDelete: Scene? = null

    fun setInitialData(sceneDao: SceneDao) {
        this.sceneDao = sceneDao
        loadData()
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.Main) {
//            withContext(Dispatchers.IO){ scenesListMutableFlow.value = sceneDao.getAll() }
            showProgress()
            withContext(Dispatchers.IO){ scenesList.value = sceneDao.getAll().toMutableList() }
            appMode.value = withContext(Dispatchers.IO) {dataStore.getPreference(PreferencesDataStore.APP_MODE_KEY)}
        }.invokeOnCompletion { showContent() }
    }

    fun onSearchButtonClicked() {
        viewModelScope.launch(Dispatchers.Main) {
            val sceneList = withContext(Dispatchers.IO){ sceneDao.getSceneByTitle(searchInput.value) }
            if (sceneList != null) {
                scenesList.value = sceneList
            } else {
                scenesList.value = listOf()
            }
        }
    }

    fun onSearchStringChanged(newSearch: String? = null) {
        viewModelScope.launch(Dispatchers.Main) {
            searchInput.value = newSearch ?: ""
            if (searchInput.value == "") {
                withContext(Dispatchers.IO){ scenesList.value = sceneDao.getAll() }
            }
        }
    }

    fun onDeleteSceneClicked(scene: Scene){
        openAlertDialog.value = true
        sceneToDelete = scene
    }

    private fun deleteScene(scene: Scene) {
        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                sceneDao.delete(scene)
//                scenesList.value.remove(scene)
                scenesList.value = scenesList.value.filter { it != scene }
            }
            sendEvent(DeleteScene(scene))
        }
    }

    fun onConfirmDeleteClicked(){
        openAlertDialog.value = false
        sceneToDelete?.let {
            deleteScene(it)
        }
    }

    fun changeAlertDialogState(state: Boolean){
        openAlertDialog.value = state
    }
}