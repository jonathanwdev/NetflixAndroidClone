package com.jonathanwdev.netflix

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jonathanwdev.netflix.models.Movie
import com.jonathanwdev.netflix.models.MovieDetail
import com.jonathanwdev.netflix.util.ImageTask
import com.jonathanwdev.netflix.util.MovieTask

class MovieActivity : AppCompatActivity(), MovieTask.CallBack {
    private lateinit var tvMovieTitle: TextView
    private lateinit var tvMovieDesc: TextView
    private lateinit var tvMovieCast: TextView
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var pbMovieLoader: ProgressBar
    private val movies = mutableListOf<Movie>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        val id =
            intent?.getIntExtra("id", 0) ?: throw  java.lang.IllegalStateException("Invalid ID")


        // common content
        tvMovieTitle = findViewById(R.id.tv_movie_title)
        tvMovieDesc = findViewById(R.id.tv_movie_desc)
        tvMovieCast = findViewById(R.id.tv_movie_cast)
        pbMovieLoader = findViewById(R.id.pb_movie_loading)
        val rvSimilar = findViewById<RecyclerView>(R.id.rv_movie_similar)

        // api loading
        MovieTask(this).execute("https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=5a972394-c55e-4c94-b7ed-b922be7a44fc")

        //recycler view
        movieAdapter = MovieAdapter(movies, R.layout.movie_item_similar)
        rvSimilar.layoutManager = GridLayoutManager(this, 3)
        rvSimilar.adapter = movieAdapter


        //toolbar
        val movieToolbar = findViewById<Toolbar>(R.id.movie_toolbar)
        setSupportActionBar(movieToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null



    }

    override fun onStartLoading() {
        pbMovieLoader.visibility = View.VISIBLE
    }

    override fun onSuccess(movieDetail: MovieDetail) {
        pbMovieLoader.visibility = View.GONE

        tvMovieTitle.text = movieDetail.movie.title
        tvMovieDesc.text = movieDetail.movie.desc
        tvMovieCast.text = getString(R.string.cast, movieDetail.movie.cast)

        movies.clear()
        movies.addAll(movieDetail.similars)
        movieAdapter.notifyDataSetChanged()

        ImageTask(object: ImageTask.CallBack {
            override fun onSuccess(bitmap: Bitmap) {
                val layerDrawable: LayerDrawable =
                    ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadows) as LayerDrawable
                val movieCover = BitmapDrawable(resources, bitmap)
                layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)
                val ivCover = findViewById<ImageView>(R.id.iv_movie)
                ivCover.setImageDrawable(layerDrawable)
            }
        }).execute(movieDetail.movie.coverUrl)

    }

    override fun onError(message: String) {
        pbMovieLoader.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}