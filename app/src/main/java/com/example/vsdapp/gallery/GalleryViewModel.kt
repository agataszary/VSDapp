package com.example.vsdapp.gallery

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.BaseViewModel
import com.example.vsdapp.core.DeleteScene
import com.example.vsdapp.database.AppDatabase
import com.example.vsdapp.database.Scene
import com.example.vsdapp.database.SceneDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel: BaseViewModel() {

    private lateinit var sceneDao: SceneDao

    val searchInput = mutableStateOf("")

    private val scenesListMutableFlow = MutableStateFlow<List<Scene>>(listOf())
    val scenesListFlow: StateFlow<List<Scene>> = scenesListMutableFlow

    fun setInitialData(sceneDao: SceneDao) {
        this.sceneDao = sceneDao
        loadData()
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){ scenesListMutableFlow.value = sceneDao.getAll() }
        }
    }

    fun onSearchButtonClicked() {
        viewModelScope.launch(Dispatchers.Main) {
            val sceneList = withContext(Dispatchers.IO){ sceneDao.getSceneByTitle(searchInput.value) }
            if (sceneList != null) {
                scenesListMutableFlow.value = sceneList
            } else {
                scenesListMutableFlow.value = listOf()
            }
        }
    }

    fun onSearchStringChanged(newSearch: String? = null) {
        viewModelScope.launch(Dispatchers.Main) {
            searchInput.value = newSearch ?: ""
            if (searchInput.value == "") {
                withContext(Dispatchers.IO){ scenesListMutableFlow.value = sceneDao.getAll() }
            }
        }
    }

    fun onDeleteSceneClicked(scene: Scene) {
        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                sceneDao.delete(scene)
                scenesListMutableFlow.value = sceneDao.getAll()
            }
            sendEvent(DeleteScene(scene))
        }
    }
}