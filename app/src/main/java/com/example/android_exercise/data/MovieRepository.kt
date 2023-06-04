package com.example.android_exercise.data

import com.example.android_exercise.data.api.MovieApi
import javax.inject.Inject


class MovieRepository @Inject constructor(val service: MovieApi) {
}