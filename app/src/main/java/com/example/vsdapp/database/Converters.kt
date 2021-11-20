package com.example.vsdapp.database

import androidx.room.TypeConverter
import com.example.vsdapp.views.PictogramDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun pictogramDetailsToString(images: List<PictogramDetails>): String {
        return gson.toJson(images)
    }

    @TypeConverter
    fun stringToPictogramDetails(jsonString: String): List<PictogramDetails> {
        val type = object: TypeToken<List<PictogramDetails>>() {}.type
        return gson.fromJson(jsonString, type)
    }
}