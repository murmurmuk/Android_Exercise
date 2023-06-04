package com.example.android_exercise.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_exercise.data.MovieRepository
import com.example.android_exercise.data.api.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val repository: MovieRepository): ViewModel() {
    val stateFlow = MutableStateFlow<Result<List<Movie>>>(Result.Loading())

    fun query() {
        stateFlow.value = Result.Loading()
        viewModelScope.launch {
            delay(3000)
            val response = repository.service.getPopularMovies(page = 1)
            stateFlow.value = Result.Success(response.body()!!.results)
        }
    }

    init {
        query()
    }

}

sealed class Result<out T> {
    data class Success<T>(val result: T) : Result<T>()
    data class Error<T>(val error: Throwable) : Result<T>()
    class Loading<T> : Result<T>()
}