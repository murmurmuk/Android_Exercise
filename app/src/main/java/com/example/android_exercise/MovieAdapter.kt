package com.example.android_exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android_exercise.data.db.entity.MovieEntry
import com.example.android_exercise.databinding.MovieItemBinding

object MovieComparator : DiffUtil.ItemCallback<MovieEntry>() {
    override fun areItemsTheSame(oldItem: MovieEntry, newItem: MovieEntry): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MovieEntry, newItem: MovieEntry): Boolean {
        return oldItem == newItem
    }
}
class MovieAdapter(diffCallback: DiffUtil.ItemCallback<MovieEntry>,
                   private val helper: ClickHelper) :
    PagingDataAdapter<MovieEntry, MovieViewHolder>(diffCallback) {

    interface ClickHelper {
        fun clickFavorite(item: MovieEntry, position: Int)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding, helper)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = getItem(position)
        // Note that item may be null. ViewHolder must support binding a
        // null item as a placeholder.
        holder.bind(item)
    }
}

class MovieViewHolder(private val binding: MovieItemBinding,
                      private val clickHelper: MovieAdapter.ClickHelper)
    : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MovieEntry?) {
        item?.apply {
            binding.title.text = title
            binding.progress.visibility = View.GONE
            binding.favorite.visibility = View.VISIBLE
            if (isFavorite) {
                binding.favorite.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                binding.favorite.setImageResource(R.drawable.baseline_favorite_border_24)
            }
            binding.favorite.setOnClickListener {
                binding.favorite.visibility = View.GONE
                binding.progress.visibility = View.VISIBLE
                clickHelper.clickFavorite(item, bindingAdapterPosition)
            }
        }
    }
}