package com.example.vsdapp.core

import com.example.vsdapp.models.GetIconsModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    companion object {
        fun createApi(): Api {
            val client = OkHttpClient.Builder().build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.arasaac.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(Api::class.java)
        }
    }

    @GET("pictograms/pl/search/{search_string}")
    suspend fun getIconsForSearchString(@Path("search_string") searchString: String): List<GetIconsModel>
}