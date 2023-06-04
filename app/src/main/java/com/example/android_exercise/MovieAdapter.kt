package com.example.android_exercise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_exercise.data.api.Movie
import com.example.android_exercise.databinding.MovieItemBinding

class MovieAdapter(private val list: List<Movie>) : RecyclerView.Adapter<MovieViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}


class MovieViewHolder(private val binding: MovieItemBinding,
                      ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Movie) {
        item.apply {
            binding.title.text = title
        }

    }
}