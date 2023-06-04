package com.example.android_exercise

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.android_exercise.data.db.entity.MovieEntry
import com.example.android_exercise.databinding.FragmentMovieDetailBinding
import com.example.android_exercise.viewModel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MovieDetailFragment : Fragment() {
    companion object {
        private const val IMAGE_PREFIX = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2"
    }
    private val args: MovieDetailFragmentArgs by navArgs()

    private val viewModel by activityViewModels<MovieViewModel>()

    private var _binding: FragmentMovieDetailBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("murmur", "open ${args.movieId}")
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getMovieInfo(args.movieId)
                    .collect {
                        setMovie(it)
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setMovie(movie: MovieEntry) {
        Log.d("murmur", "$movie")
        _binding?.apply {
            val link = if (movie.poster_path.isNullOrEmpty()) {
                null
            } else {
                IMAGE_PREFIX + movie.poster_path
            }
            Glide.with(coverImage)
                .load(Uri.parse(link))
                .into(coverImage)

            overviewText.text = movie.overview
        }
    }
}