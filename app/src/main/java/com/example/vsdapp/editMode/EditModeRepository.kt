package com.example.vsdapp.editMode

import com.example.vsdapp.core.BaseRepository
import com.example.vsdapp.models.GetIconsModel

class EditModeRepository: BaseRepository() {

    suspend fun getIcons(searchString: String): List<GetIconsModel> {
        val icons = try {
            api.getIconsForSearchString(searchString)
        } catch (e: Exception) {
            println("Empty response")
            listOf()
        }
        return icons
    }
}