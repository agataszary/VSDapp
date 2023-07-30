package com.example.vsdapp.students

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.vsdapp.core.ComposeViewModel
import com.example.vsdapp.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentsViewModel(private val studentsRepository: StudentsRepository): ComposeViewModel() {

    var tabIndex = mutableStateOf(0)
        private set

    var yourStudentsList = mutableStateOf(listOf<UserModel>())
        private set

    var searchedUsers = mutableStateOf(listOf<UserModel>())
        private set

    var searchTextValue = mutableStateOf("")
        private set

    private var savedUsersIds = listOf<String>()
    private var allAvailableUsers = listOf<UserModel>()
    private var unfilteredUsers = listOf<UserModel>()

    fun loadData() {
        viewModelScope.launch(Dispatchers.Main){
            showProgress()
            savedUsersIds = withContext(Dispatchers.IO){ studentsRepository.getSavedStudentsIds() }
            unfilteredUsers = withContext(Dispatchers.IO){ studentsRepository.getAllAvailableUsers() }.filter { !it.therapistAccount }
            allAvailableUsers = unfilteredUsers.filter { it.userId !in savedUsersIds }
            searchedUsers.value = allAvailableUsers
            yourStudentsList.value = unfilteredUsers.filter { it.userId in savedUsersIds }
            showContent()
        }

    }

    fun onTabClicked(tabIndex: Int) {
        this.tabIndex.value = tabIndex
    }

    fun onSearchStringChanged(value: String) {
        if (value.isNotBlank() || (value.isBlank() && searchTextValue.value != "")) {
            searchTextValue.value = value
            if (value == "") {
                searchedUsers.value = allAvailableUsers
            }
        }
    }

    fun onSearchButtonClicked() {
        searchedUsers.value = allAvailableUsers.filter {
            it.mainName.contains(searchTextValue.value) ||
                    it.mainSurname.contains(searchTextValue.value) ||
                    it.childName?.contains(searchTextValue.value) == true ||
                    it.childSurname?.contains(searchTextValue.value) == true ||
                    it.emailAddress.contains(searchTextValue.value)
        }
    }

    fun onAddIconClicked(userId: String) {
        val tmpList = savedUsersIds.toMutableList()
        tmpList.add(userId)
        val newSavedUsersList = tmpList.toList()
        studentsRepository.updateSavedUsers(newSavedUsersList)
            .addOnSuccessListener {
                savedUsersIds = newSavedUsersList
                searchedUsers.value = searchedUsers.value.filter { it.userId != userId }
                allAvailableUsers = allAvailableUsers.filter { it.userId != userId }
                yourStudentsList.value = unfilteredUsers.filter { it.userId in savedUsersIds }
            }
            .addOnFailureListener {
                println("Failureeeeeee ${it.message}")
            }
    }

    fun onDeleteUserClicked(userId: String) {
        val newSavedUsersList = yourStudentsList.value
            .filter { it.userId != userId }
            .map { it.userId }
        studentsRepository.updateSavedUsers(newSavedUsersList)
            .addOnSuccessListener {
                savedUsersIds = newSavedUsersList
                yourStudentsList.value = yourStudentsList.value.filter { it.userId != userId }
                allAvailableUsers = unfilteredUsers.filter { it.userId !in newSavedUsersList }
                searchedUsers.value = unfilteredUsers.filter { it.userId !in newSavedUsersList }
            }
            .addOnFailureListener {
                println("Failureeeeeee ${it.message}")
            }
    }

    fun onOpenUsersGalleryClicked(userId: String) {

    }

}