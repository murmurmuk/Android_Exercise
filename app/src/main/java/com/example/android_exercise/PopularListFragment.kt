package com.example.android_exercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.android_exercise.databinding.FragmentPopularListBinding
import com.example.android_exercise.viewModel.MovieViewModel
import com.example.android_exercise.viewModel.Result
import kotlinx.coroutines.launch

class PopularListFragment : Fragment() {

    private val viewModel by activityViewModels<MovieViewModel>()
    private lateinit var binding: FragmentPopularListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPopularListBinding.inflate(inflater, container, false)
        binding.swipe.setOnRefreshListener {
            viewModel.query()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect {
                    binding.swipe.isRefreshing = false
                    when(it) {
                        is Result.Error -> {
                            binding.errorText.isVisible = true
                            binding.progress.isVisible = false
                        }
                        is Result.Loading -> {
                            binding.errorText.isVisible = false
                            binding.progress.isVisible = true
                        }
                        is Result.Success -> {
                            binding.errorText.isVisible = false
                            binding.progress.isVisible = false
                            binding.listView.adapter = MovieAdapter(it.result)
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PopularListFragment()
    }
}