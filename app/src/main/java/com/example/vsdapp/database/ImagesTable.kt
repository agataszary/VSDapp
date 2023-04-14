package com.example.vsdapp.database

import android.os.Parcelable
import androidx.room.*
import com.example.vsdapp.views.PictogramDetails
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "scenes")
data class Scene(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var imageName: String,
    var imageLocation: String,
    @TypeConverters(Converters::class)
    var pictograms: List<PictogramDetails>
)