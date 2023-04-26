package com.jonathanwdev.netflix

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jonathanwdev.netflix.models.Category
import com.jonathanwdev.netflix.models.Movie
import com.jonathanwdev.netflix.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.CallBack {
    private lateinit var pbLoader: ProgressBar
    private lateinit var adapter: CategoryAdapter
    private  val categories = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pbLoader = findViewById(R.id.pb_main_loading)


        val rvMain = findViewById<RecyclerView>(R.id.rv_main)
        adapter = CategoryAdapter(categories) {
            val intent = Intent(this@MainActivity, MovieActivity::class.java)
            intent.putExtra("id", it)
            startActivity(intent)
        }
        rvMain.layoutManager = LinearLayoutManager(this)
        rvMain.adapter = adapter

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=5a972394-c55e-4c94-b7ed-b922be7a44fc")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSuccess(categories: List<Category>) {
        this.categories.clear()
        this.categories.addAll(categories)
        this.adapter.notifyDataSetChanged()
        pbLoader.visibility = View.GONE
    }

    override fun onStartLoading() {
        pbLoader.visibility = View.VISIBLE
    }

    override fun onError(message: String) {
        pbLoader.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


}