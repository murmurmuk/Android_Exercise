package com.example.android_exercise.data.api

import com.example.android_exercise.BuildConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
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


    @Headers(
        "Authorization: Bearer ${BuildConfig.TOKEN}",
        "accept: application/json")
    @GET("account/${BuildConfig.ACCOUNT_ID}/favorite/movies")
    suspend fun getUserFavorite(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int,
        @Query("sort_by") sort: String = "created_at.asc") : Response<MovieResponse>

    @Headers(
        "Authorization: Bearer ${BuildConfig.TOKEN}",
        "accept: application/json")
    @POST("account/${BuildConfig.ACCOUNT_ID}/favorite")
    suspend fun changeFavorite(
        @Body item: ChangeFavoritePayload) : Response<ChangeFavoriteResponse>
}

data class ChangeFavoriteResponse(val success: Boolean,
                                  val status_code: Int,
                                  val status_message: String?)
data class ChangeFavoritePayload(val media_type: String = "movie",
                                 val media_id: Int,
                                 val favorite: Boolean)

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