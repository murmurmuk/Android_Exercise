package com.example.android_exercise.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.android_exercise.data.api.MovieApi
import com.example.android_exercise.data.db.CacheDb
import com.example.android_exercise.data.db.entity.MovieEntry
import com.example.android_exercise.data.db.entity.PopularRemoteKey
import com.example.android_exercise.viewModel.GetResult
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class MovieRepository @Inject constructor(private val service: MovieApi,
                                          private val database: CacheDb) {
    @OptIn(ExperimentalPagingApi::class)
    fun getPagingSource() = flow {
        val movieDao = database.movieDao()
        val pager = Pager(config = PagingConfig(pageSize = 20), null,
            MovieRemoteMediator(database, service)) {
            movieDao.pagingSource()
        }
        emit(GetResult.Success(pager.flow))
    }

    fun updateFavorite(item: MovieEntry) = flow<GetResult<MovieEntry>> {
        val change = !item.isFavorite
        Log.d("murmur", "try change ${item.title}")
        val changeItem = MovieEntry(
            item.id,
            item.title,
            item.poster_path,
            item.overview,
            change,
            item.page
        )
        database.withTransaction {
            database.movieDao().update(changeItem)
        }
        emit(GetResult.Success(changeItem))
    }
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
                        MovieEntry(it.id, it.title, it.poster_path, it.overview, false, response.body()!!.page)
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