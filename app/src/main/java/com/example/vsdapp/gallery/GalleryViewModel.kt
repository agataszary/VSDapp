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

    private var userScenes: MutableMap<String, SceneDetails> = mutableMapOf()

    var openAlertDialog = mutableStateOf(false)
        private set

    var appMode = mutableStateOf(AppMode.NONE)
        private set

    var tabIndex = mutableStateOf(0)
        private set

    val shouldShowNoResultsDisclaimer = mutableStateOf(false)

    private var sceneToDelete: SceneDetails? = null
    private var availableScenes: MutableMap<String, SceneDetails> = mutableMapOf()

    fun loadData() {
        viewModelScope.launch(Dispatchers.Main) {
            showProgress()
            val tmpMap = mutableMapOf<String, SceneDetails>()
            withContext(Dispatchers.IO) { storageRepository.getAllScenesDetails() }
                .forEach {
                    tmpMap[it.id] = it
                }
            appMode.value = withContext(Dispatchers.IO) {dataStore.getPreference(PreferencesDataStore.APP_MODE_KEY)}
            availableScenes = tmpMap
            userScenes = tmpMap
            scenesList.value = tmpMap.values.toList()
        }.invokeOnCompletion { showContent() }
    }

    fun onSearchButtonClicked() {
        viewModelScope.launch(Dispatchers.Main) {
            val sceneList = userScenes.filter { it.value.title.contains(searchInput.value) }
            availableScenes = sceneList.toMutableMap()
            scenesList.value = sceneList.values.toList()
            shouldShowNoResultsDisclaimer.value = sceneList.isEmpty()
        }
    }

    fun onSearchStringChanged(newSearch: String) {
        if (newSearch.isNotBlank() || (newSearch.isBlank() && searchInput.value != "")){
            searchInput.value = newSearch
            if (searchInput.value == "") {
                availableScenes = userScenes
                scenesList.value = userScenes.values.toList()
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
                userScenes = userScenes.filter { it.key != scene.id}.toMutableMap()
                availableScenes = availableScenes.filter { it.key != scene.id }.toMutableMap()
                scenesList.value = availableScenes.values.toList()
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

    fun onFavouriteClicked(scene: SceneDetails) {
        val isFavourite = scene.favourite
        storageRepository.updateSceneFavouriteField(scene.id, !isFavourite)
            .addOnSuccessListener {
                userScenes[scene.id] = scene.copy(favourite = !isFavourite)
                availableScenes[scene.id] = scene.copy(favourite = !isFavourite)
                scenesList.value = availableScenes.values.toList()
            }
    }

    fun onTabClicked(index: Int) {
        tabIndex.value = index

        when(index) {
            0 -> scenesList.value = availableScenes.values.toList()
            1 -> scenesList.value = availableScenes.filterValues { it.favourite }.values.toList()
            2 -> scenesList.value = availableScenes.filterValues { it.markedByTherapist }.values.toList()
        }
    }
}