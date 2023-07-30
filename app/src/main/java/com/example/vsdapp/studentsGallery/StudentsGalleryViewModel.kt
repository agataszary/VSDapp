package com.example.vsdapp.studentsGallery

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.ComposeViewModel
import com.example.vsdapp.database.StorageRepository
import com.example.vsdapp.models.SceneDetails
import com.example.vsdapp.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentsGalleryViewModel(private val storageRepository: StorageRepository): ComposeViewModel() {

    val searchInput = mutableStateOf("")

    var scenesList = mutableStateOf<List<SceneDetails>>(listOf())
        private set

    private var availableScenes: MutableMap<String, SceneDetails> = mutableMapOf()

    private var userScenes: MutableMap<String, SceneDetails> = mutableMapOf()

    val shouldShowNoResultsDisclaimer = mutableStateOf(false)

    private lateinit var userModel: UserModel

    var userName = mutableStateOf("")
        private set
    var userSurname = mutableStateOf("")
        private set

    fun setInitialData(userModel: UserModel) {
        this.userModel = userModel
        userName.value = if (userModel.childName.isNullOrEmpty()) userModel.mainName else userModel.childName!!
        userSurname.value = if (userModel.childSurname.isNullOrEmpty()) userModel.mainSurname else userModel.childSurname!!
        loadData()
    }

    private fun loadData() {
        showProgress()
        viewModelScope.launch(Dispatchers.Main) {
            val tmpMap = mutableMapOf<String, SceneDetails>()
            withContext(Dispatchers.IO) { storageRepository.getScenesForUserId(userModel.userId) }.forEach {
                tmpMap[it.id] = it
            }
            availableScenes = tmpMap
            userScenes = tmpMap
            scenesList.value = tmpMap.toMap().values.toList()
        }.invokeOnCompletion { showContent() }
    }

    fun onSearchButtonClicked() {
        viewModelScope.launch(Dispatchers.Main) {
            val sceneList = userScenes.filter { it.value.title.contains(searchInput.value) }
            availableScenes = sceneList.toMutableMap()
            scenesList.value = availableScenes.values.toList()
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

    fun onAddBookmarkSceneClicked(sceneDetails: SceneDetails) {
        val isMarkedState = sceneDetails.markedByTherapist

        storageRepository.updateSceneBookmarkedField(sceneDetails.id, !isMarkedState)
            .addOnSuccessListener {
                userScenes[sceneDetails.id] = sceneDetails.copy(markedByTherapist = !isMarkedState)
                availableScenes[sceneDetails.id] = sceneDetails.copy(markedByTherapist = !isMarkedState)
                scenesList.value = availableScenes.values.toList()
            }
    }
}