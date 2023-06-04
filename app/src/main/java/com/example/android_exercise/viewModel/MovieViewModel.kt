package com.example.android_exercise.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.android_exercise.data.MovieRepository
import com.example.android_exercise.data.db.entity.MovieEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val repository: MovieRepository): ViewModel() {
    val stateFlow = MutableStateFlow<GetResult<Flow<PagingData<MovieEntry>>>>(GetResult.Loading())

    fun query() {
        stateFlow.value = GetResult.Loading()
        viewModelScope.launch {
            Log.d("murmur", "query")
            repository.getPagingSourceWithFavorite()
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.d("murmur", "error $it")
                    stateFlow.value = GetResult.Error(it)
                }
                .collect {
                    if (it is GetResult.Success) {
                        stateFlow.value = GetResult.Success(it.result)
                    } else {
                        stateFlow.value = GetResult.Error(Throwable("has error"))
                    }
                }
        }
    }

    init {
        query()
    }

    fun updateFavorite(item: MovieEntry) = repository.updateFavorite(item)
        .flowOn(Dispatchers.IO)
        .catch {
            emit(GetResult.Error(it))
        }
}

sealed class GetResult<out T> {
    data class Success<T>(val result: T) : GetResult<T>()
    data class Error<T>(val error: Throwable) : GetResult<T>()
    class Loading<T> : GetResult<T>()
}