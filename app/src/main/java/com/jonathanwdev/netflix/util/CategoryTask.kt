package com.jonathanwdev.netflix.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.jonathanwdev.netflix.models.Category
import com.jonathanwdev.netflix.models.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class CategoryTask(private val callBack: CallBack) {
    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()


    interface CallBack {
        fun onStartLoading()
        fun onSuccess(categories: List<Category>)
        fun onError(message: String)
    }

    fun execute(url:String)  {
        callBack.onStartLoading()
        executor.execute {
            var urlConnection: HttpURLConnection? = null
            var buffer: BufferedInputStream? = null
            var stream: InputStream? = null
            try {
                val requestUrl = URL(url)
                urlConnection =  requestUrl.openConnection() as HttpURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode = urlConnection.responseCode
                if(statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor")
                }

                stream = urlConnection.inputStream
                buffer = BufferedInputStream(stream)
                val jsonAsString = this.toString(buffer)
                val categories = this.toCategories(jsonAsString)
                handler.post {
                    callBack.onSuccess(categories)
                }
            }catch(e: IOException){
                val message = e.message ?: "Erro desconhecido"
                handler.post {
                    callBack.onError(message)
                }
            }finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }

        }
    }

    private fun toCategories(json: String): List<Category> {
        val categories = mutableListOf<Category>()
        val jsonRoot = JSONObject(json)
        val jsonCategories = jsonRoot.getJSONArray("category")
        for (i in 0 until jsonCategories.length()) {
            val jsonCategory = jsonCategories.getJSONObject(i);
            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()

            for(j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j);
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")
                movies.add(
                    Movie(
                        id,
                        coverUrl
                    )
                )
            }

            categories.add(Category(title, movies))

        }
        return categories
    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        val byteOutput = ByteArrayOutputStream()
        var read: Int
        while (true) {
            read = stream.read(bytes)
            if (read <= 0 ) break
            byteOutput.write(bytes, 0 , read)
        }

        return String(byteOutput.toByteArray())
    }
}