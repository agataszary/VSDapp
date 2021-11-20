package com.example.vsdapp

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.vsdapp.database.AppDatabase
import com.example.vsdapp.database.Scene
import com.example.vsdapp.database.SceneDao
import com.example.vsdapp.views.PictogramDetails
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var sceneDao: SceneDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sceneDao = db.sceneDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGet() {
        val pictogram = PictogramDetails(imageUrl = "url", x = 1, y = 1, label = "label")
        val scene = Scene(imageLocation = "location", imageName = "scene1", pictograms = listOf(pictogram))
        sceneDao.insert(scene)
        val getScene = sceneDao.getAll()
        assertEquals(getScene[0].imageName, "scene1")
    }
}
