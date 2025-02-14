package com.dicoding.asclepius.data.retrofit

import com.dicoding.asclepius.data.response.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines?q=cancer&category=health&language=en")
    fun getNews(@Query("apiKey") apiKey: String): Call<NewsResponse>
}