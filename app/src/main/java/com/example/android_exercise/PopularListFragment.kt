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
import androidx.paging.PagingData
import com.example.android_exercise.data.db.entity.MovieEntry
import com.example.android_exercise.databinding.FragmentPopularListBinding
import com.example.android_exercise.viewModel.GetResult
import com.example.android_exercise.viewModel.MovieViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PopularListFragment : Fragment(), MovieAdapter.ClickHelper {

    private val viewModel by activityViewModels<MovieViewModel>()
    private lateinit var binding: FragmentPopularListBinding
    private lateinit var adapter: MovieAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPopularListBinding.inflate(inflater, container, false)
        binding.swipe.setOnRefreshListener {
            viewModel.query()
        }
        adapter = MovieAdapter(MovieComparator, this)
        binding.listView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect {
                    binding.swipe.isRefreshing = false
                    when(it) {
                        is GetResult.Error -> {
                            binding.errorText.isVisible = true
                            binding.progress.isVisible = false
                        }
                        is GetResult.Loading -> {
                            binding.errorText.isVisible = false
                            binding.progress.isVisible = true
                        }
                        is GetResult.Success -> {
                            binding.errorText.isVisible = false
                            binding.progress.isVisible = false
                            it.result.collectLatest {
                                    value: PagingData<MovieEntry> ->
                                adapter.submitData(value)
                            }
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

    override fun click(item: MovieEntry, position: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateFavorite(item)
                    .collect {
                        if (it is GetResult.Error) {
                            adapter.notifyItemChanged(position)
                        }
                    }
            }
        }
    }
}