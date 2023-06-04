package com.example.android_exercise.data.api

import com.example.android_exercise.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MovieApi {
    @Headers(
        "Authorization: Bearer ${BuildConfig.TOKEN}",
        "accept: application/json"
    )
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int
    ): Response<MovieResponse>
}

data class MovieResponse(val page: Int,
                         val results: List<Movie>,
                         val total_pages: Int)

data class Movie(
    val backdrop_path: String,
    val title: String,
    val id: Int,
    val poster_path: String,
    val overview: String
)