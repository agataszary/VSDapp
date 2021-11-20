package com.example.vsdapp.editMode

import com.example.vsdapp.core.BaseRepository
import com.example.vsdapp.models.GetIconsModel

class EditModeRepository: BaseRepository() {

    suspend fun getIcons(searchString: String): List<GetIconsModel> {
        return api.getIconsForSearchString(searchString)
    }
}