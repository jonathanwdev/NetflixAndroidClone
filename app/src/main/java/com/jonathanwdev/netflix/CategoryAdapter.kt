package com.jonathanwdev.netflix

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jonathanwdev.netflix.models.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(currentCategory: Category) {
            val txtTitle = itemView.findViewById<TextView>(R.id.txt_title)
            val rvCategory = itemView.findViewById<RecyclerView>(R.id.rv_category)
            txtTitle.text = currentCategory.name
            rvCategory.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            rvCategory.adapter = MovieAdapter(currentCategory.movies, R.layout.movie_item, onItemClickListener)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentCategory = categories[position]
        holder.bind(currentCategory)
    }

    override fun getItemCount() = categories.size
}
