package com.example.vsdapp.gallery

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.AppMode
import com.example.vsdapp.core.ComposeViewModel
import com.example.vsdapp.core.PreferencesDataStore
import com.example.vsdapp.database.SceneDao
import com.example.vsdapp.database.StorageRepository
import com.example.vsdapp.models.SceneDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel(private val storageRepository: StorageRepository): ComposeViewModel() {

    val searchInput = mutableStateOf("")

    var scenesList = mutableStateOf<List<SceneDetails>>(listOf())
        private set

    private var userScenes: List<SceneDetails> = listOf()

    var openAlertDialog = mutableStateOf(false)
        private set

    var appMode = mutableStateOf(AppMode.NONE)
        private set

    val shouldShowNoResultsDisclaimer = mutableStateOf(false)

    private var sceneToDelete: SceneDetails? = null

    fun loadData() {
        viewModelScope.launch(Dispatchers.Main) {
            showProgress()
            scenesList.value = withContext(Dispatchers.IO) { storageRepository.getAllScenesDetails() }
            userScenes = scenesList.value
            appMode.value = withContext(Dispatchers.IO) {dataStore.getPreference(PreferencesDataStore.APP_MODE_KEY)}
        }.invokeOnCompletion { showContent() }
    }

    fun onSearchButtonClicked() {
        viewModelScope.launch(Dispatchers.Main) {
            val sceneList = userScenes.filter { it.title.contains(searchInput.value) }
            scenesList.value = sceneList
            shouldShowNoResultsDisclaimer.value = sceneList.isEmpty()
        }
    }

    fun onSearchStringChanged(newSearch: String) {
        if (newSearch.isNotBlank() || (newSearch.isBlank() && searchInput.value != "")){
            searchInput.value = newSearch
            if (searchInput.value == "") {
                scenesList.value = userScenes
                shouldShowNoResultsDisclaimer.value = false
            }
        }
    }

    fun onDeleteSceneClicked(scene: SceneDetails){
        openAlertDialog.value = true
        sceneToDelete = scene
    }

    private fun deleteScene(scene: SceneDetails) {
        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                storageRepository.deleteScene(scene.id, scene.imageLocation)
                userScenes = userScenes.filter { it.id != scene.id}
                scenesList.value = userScenes
            }
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