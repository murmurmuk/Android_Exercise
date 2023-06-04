package com.example.android_exercise.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.android_exercise.data.api.ChangeFavoritePayload
import com.example.android_exercise.data.api.MovieApi
import com.example.android_exercise.data.db.CacheDb
import com.example.android_exercise.data.db.entity.FavoriteMovie
import com.example.android_exercise.data.db.entity.MovieEntry
import com.example.android_exercise.data.db.entity.PopularRemoteKey
import com.example.android_exercise.viewModel.GetResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class MovieRepository @Inject constructor(private val service: MovieApi,
                                          private val database: CacheDb,
                                          private val dataStore: DataStore<Preferences>) {
    companion object {
        const val FAVORITE_JOB_TAG = "favorite_job_tag"
    }
    @OptIn(ExperimentalPagingApi::class)
    fun getPagingSource() = flow {
        val movieDao = database.movieDao()
        val pager = Pager(config = PagingConfig(pageSize = 20), null,
            MovieRemoteMediator(database, service)) {
            movieDao.pagingSource()
        }
        emit(GetResult.Success(pager.flow))
    }

    @OptIn(FlowPreview::class)
    fun getPagingSourceWithFavorite(): Flow<GetResult<Flow<PagingData<MovieEntry>>>> {
        val key = booleanPreferencesKey(FAVORITE_JOB_TAG)
        return dataStore.data.map {
            it[key] ?: false
        }.flatMapConcat {
            Log.d("murmur", "get $it")
            if (!it) {
                getAllFavorite().flatMapConcat {
                    getPagingSource()
                }
            } else {
                getPagingSource()
            }
        }
    }

    private fun getAllFavorite() = flow<GetResult<Boolean>> {
        var initPage = 1
        while (true) {
            val response = service.getUserFavorite(page = initPage).body()!!
            database.withTransaction {
                val list = response.results.map {
                    FavoriteMovie(it.id)
                }
                database.favoriteMovieDao().insertAll(list)
            }
            if (response.page >= response.total_pages) {
                break
            }
            initPage += 1
        }
        Log.d("murmur", "finish save favorite")
        val key = booleanPreferencesKey(FAVORITE_JOB_TAG)
        dataStore.edit {preference  ->
            preference[key] = true
        }
        emit(GetResult.Success(true))
    }

    fun updateFavorite(item: MovieEntry) = flow {
        val change = !item.isFavorite
        Log.d("murmur", "try change ${item.title}")
        val response = service.changeFavorite(
                ChangeFavoritePayload(
                    media_id = item.id,
                    favorite = change
                )
                )
        Log.d("murmur", "$response")

        if (response.isSuccessful) {
            Log.d("murmur", "change ${response.body()}")
            if (change) {
                database.favoriteMovieDao().insert(FavoriteMovie(item.id))
            } else {
                database.favoriteMovieDao().delete(FavoriteMovie(item.id))
            }
            val changeItem = MovieEntry(
                item.id,
                item.title,
                item.poster_path,
                item.overview,
                change,
                item.page
            )
            database.movieDao().update(changeItem)
            emit(GetResult.Success(changeItem))
        } else {
            Log.d("murmur", "change fail ${response.errorBody()}")
            emit(GetResult.Error(Throwable(response.errorBody().toString())))
        }
    }

    suspend fun getMovieInfo(id: Int) = database.movieDao().movieByQuery(id)

}

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val database: CacheDb,
    private val movieApi: MovieApi
) : RemoteMediator<Int, MovieEntry>() {
    override suspend fun load(
        loadType: LoadType,

        state: PagingState<Int, MovieEntry>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = database.withTransaction {
                        database.remoteKeyDao().remoteKey()
                    }

                    if (remoteKey.nextKey == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    remoteKey.nextKey
                }
            }
            Log.d("murmur", "load with $loadKey")
            val response = movieApi.getPopularMovies(page = loadKey)
            val movieResponse = response.body()
            if (movieResponse == null) {
                MediatorResult.Error(Throwable("null response"))
            } else {
                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.movieDao().clearAll()
                        database.remoteKeyDao().delete()
                    }
                    database.movieDao().insertAll(response.body()!!.results.map {
                        val flag = database.favoriteMovieDao().isFavorite(it.id)
                        MovieEntry(it.id, it.title, it.poster_path, it.overview, flag, response.body()!!.page)
                    })
                    val remoteKey = if (movieResponse.page == movieResponse.total_pages) {
                        PopularRemoteKey(0, null)
                    } else {
                        PopularRemoteKey(0, movieResponse.page + 1)
                    }
                    database.remoteKeyDao().insertOrReplace(remoteKey)
                }
                Log.d("murmur", "load finish ${movieResponse.page} ${movieResponse.total_pages}")
                MediatorResult.Success(endOfPaginationReached = movieResponse.page == movieResponse.total_pages)
            }
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

}