package com.jonathanwdev.netflix

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.jonathanwdev.netflix.models.Movie
import com.jonathanwdev.netflix.util.ImageTask

class MovieAdapter(
    private val movies: List<Movie>,
    @LayoutRes private val layoutId: Int,
    private  val onItemClickListener: ((Int) -> Unit)? = null
    ) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(currentMovie: Movie) {
            val imageCover = itemView.findViewById<ImageView>(R.id.img_cover)
            ImageTask(object: ImageTask.CallBack {
                override fun onSuccess(bitmap: Bitmap) {
                    imageCover.setImageBitmap(bitmap)
                }
            }).execute(currentMovie.coverUrl)
            imageCover.setOnClickListener {
                onItemClickListener?.invoke(currentMovie.id)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = movies[position]
        holder.bind(currentMovie)
    }

    override fun getItemCount() = movies.size
}
