package com.example.vsdapp.database

import androidx.room.*

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes")
    fun getAll(): List<Scene>

    @Query("SELECT * FROM scenes WHERE imageName=:title")
    fun getSceneByTitle(title: String): List<Scene>?

    @Query("SELECT * FROM scenes WHERE id=:id")
    fun getSceneById(id: Long): Scene

    @Insert
    fun insert(scene: Scene): Long

    @Update
    fun update(scene: Scene)

    @Delete
    fun delete(scene: Scene)
}